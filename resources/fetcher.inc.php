<?php

namespace Awl;
interface Fetcher {
    public function fetchURL($url);
}

class URLFetcher implements Fetcher {
    public function fetchURL($url) {
        return file_get_contents_utf8($url);
    }
}

class SQLiteFetcher extends URLFetcher {
    protected $__database;
    protected $__table;
    protected $__selectURL;
    protected $__insertURL;
    protected $__timeout;
    protected static function selectURLSQL($table) {
        return  'SELECT content FROM `'.$table.'`'." WHERE url = :url AND datetime > strftime('%s', 'now') - :timeout ORDER BY datetime DESC LIMIT 1";
    }
    protected static function insertURLSQL($table) {
        return  'INSERT INTO `'.$table.'` (url, content, datetime) VALUES (:url, :content, strftime(\'%s\',\'now\'));';
    }
    public function __construct(\PDO $database, $table, $timeout = 0) {
        $database->setAttribute(\PDO::ATTR_ERRMODE, \PDO::ERRMODE_EXCEPTION);
        $this->__database = $database;
        $this->__table = $table;

        $this->__timeout = $timeout;

        $tableExists = $database->query("SELECT name FROM sqlite_master WHERE type='table' AND name='{$table}'");
        if ( !$tableExists->fetch() ) {
            $database->query("CREATE TABLE '{$table}' (url text, datetime integer, content text)");
        }

        $selectURLSQL = static::selectURLSQL($table);
        $insertURLSQL = static::insertURLSQL($table);
        $this->__selectURL = $this->__database->prepare($selectURLSQL);
        $this->__insertURL = $this->__database->prepare($insertURLSQL);
        if ( !$this->__selectURL || !$this->__insertURL )
            throw new \Exception("Something failed down.");


    }
    public function fetchURL($url) {
        $params = array('url'=>$url, 'timeout'=>$this->__timeout);
        $q = $this->__selectURL;
        $q->execute($params);
        $row = $q->fetch();
        if ( $row ) {
            return $row[0];
        }
        $content = parent::fetchURL($url);

        // if it fails - fair enough - but we have to shove it in anyway!

        $i = $this->__insertURL;
        $params = array('url'=>$url, 'content' => $content);
        $i->execute($params);
        return $content;

    }
}
class SerialisedFetcher implements Fetcher {

    protected $__filename;

    public function __construct($filename) {
        $this->__filename = $filename;
    }

    public function read() {
        $data = deserialise_from_file($this->__filename);
        return $data ? $data : array();
    }

    public function save($uncached) {
        return serialize_into_file($this->__filename, $uncached);
    }

    public function fetchURL($url) {
        $read = $this->read();
        if ( isset($read[$url]))
            return $read[$url];
        $data = file_get_contents_utf8($url);
        if ( !$data )
            return;
        $read[$url] = $data;
        $this->save($read);
        return $data;
    }
}

function file_put_contents_utf8($fn, $data) {
    return file_put_contents($fn,  $data, FILE_TEXT);
}

// shamelessly borrowed from php.net/file_get_contents comments. Or was it stack overflow? Not sure!
function file_get_contents_utf8($fn) {
    //return file_get_contents($fn, FILE_TEXT);
    $content = file_get_contents($fn);
    return mb_convert_encoding($content, 'UTF-8',
        mb_detect_encoding($content, 'UTF-8, ISO-8859-1', true));
}


function deserialise_from_file($filename) {
    if (!file_exists($filename))
        return;
    $contents = file_get_contents_utf8($filename);
    if ( !$contents )
        return;
    return unserialize($contents);
}

function serialize_into_file($filename, $data) {
    $bytes = serialize($data);
    return file_put_contents_utf8($filename, $bytes);
}



class Amazon_MainImage_Fetcher {
    protected $__fetcher;
    protected $__tidier;

    public function __construct(Fetcher $fetcher = null, Tidier $tidier = null) {
        $this->__fetcher = $fetcher ? $fetcher : new URLFetcher;
        $this->__tidier = $tidier ? $tidier : new Tidier();
    }

    public function getDOM($url) {
        $tidier = clone $this->__tidier;
        $html = $this->__fetcher->fetchURL($url);
        if ( !$html ) return;
        $xml = $tidier->TidyXML($html);
        if ( !$xml ) return;
        $dom = clone $tidier->XMLToDOM($xml);
        return $dom;
    }

    public function FetchMainImage($id) {


        if ( !ctype_alnum($id) )
            throw new \Exception("Specified ID is invalid.");


        $url = 'http://www.amazon.co.uk/dp/'.$id;

        $dom = $this->getDOM($url);
        if ( !$dom ) return;
        $xpath = new \DOMXPath($dom);
        $img = $xpath->query('//*[@id="main-image"]')->item(0);
        if ( !$img ) return;
        $src = $img->getAttribute('src');
        if ( !$src ) return;
        return $src;
    }
}
class Amazon_MainImage_Addon {

    protected $__fetcher;
    protected $__tidier;

    static $namespace = "https://vynar.com/2013/amazon-wishlist";

    public function __construct(Fetcher $fetcher = null, Tidier $tidier = null) {
        $this->__fetcher = $fetcher ? $fetcher : new URLFetcher;
        $this->__tidier = $tidier ? $tidier : new Tidier();
    }

    public static function attachImage($item, \DOMDocument $dom, \DOMXPath $xpath, Amazon_MainImage_Fetcher $amma) {
        $id = $xpath->query("*[local-name() = 'id' and namespace-uri() = '".static::$namespace."']", $item)->item(0);
        if ( !$id ) return;
        $imageURL = $amma->FetchMainImage($id->nodeValue);
        if ( !$imageURL ) return;
        $mainImageElement = $dom->createElementNS(static::$namespace, 'wl:main-image');
        $mainImageElement->appendChild($dom->createTextNode($imageURL));
        $item->appendChild($mainImageElement);
        return $mainImageElement;
    }
    public function AddOn(\DOMDocument $dom) {
        $xpath = new \DOMXPath($dom);
        $items = $xpath->query("//*[local-name() = 'item' and namespace-uri() = '".static::$namespace."']");
        $mainimager = new Amazon_MainImage_Fetcher($this->__fetcher, $this->__tidier);
        foreach($items as $item) {
            $this->attachImage($item, $dom, $xpath, $mainimager);
        }
        return $dom;
    }

}



?>