<?php

namespace Awl;

/*
 * Use a bit of dependency injection
 * And make use of PHP's DOM functionality
 * Also use Tidy to clean up any nasty HTML.
 */

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

    public function __construct(Fetcher $fetcher = null, Tidier $tidier = null) {
        $this->__fetcher = $fetcher ? $fetcher : new URLFetcher;
        $this->__tidier = $tidier ? $tidier : new Tidier();
    }

    public static function attachImage($item, \DOMDocument $dom, \DOMXPath $xpath, Amazon_MainImage_Fetcher $amma) {
        $id = $xpath->query("*[local-name() = 'id' and namespace-uri() = 'urn:vynar:wishlist']", $item)->item(0);
        if ( !$id ) return;
        $imageURL = $amma->FetchMainImage($id->nodeValue);
        if ( !$imageURL ) return;
        $mainImageElement = $dom->createElementNS('urn:vynar:wishlist', 'wl:main-image');
        $mainImageElement->appendChild($dom->createTextNode($imageURL));
        $item->appendChild($mainImageElement);
        return $mainImageElement;
    }
    public function AddOn(\DOMDocument $dom) {
        $xpath = new \DOMXPath($dom);
        $items = $xpath->query("//*[local-name() = 'item' and namespace-uri() = 'urn:vynar:wishlist']");
        $mainimager = new Amazon_MainImage_Fetcher($this->__fetcher, $this->__tidier);
        foreach($items as $item) {
            $this->attachImage($item, $dom, $xpath, $mainimager);
        }
        return $dom;
    }

}
class Amazon_Wishlist_Fetcher {
    protected $__fetcher;
    protected $__tidier;

    public function __construct(Fetcher $fetcher = null, Tidier $tidier = null) {
        $this->__fetcher = $fetcher ? $fetcher : new URLFetcher;
        $this->__tidier = $tidier ? $tidier : new Tidier();
    }

    public static function getNextPageLink(\DOMDocument $dom) {
        $xpath = new \DOMXPath($dom);
        $query = "(//span[@class='pagSide'])[position() = last()]/a";

        $results = $xpath->query($query);
        if ( $results && $results->length )
            return $results->item(0)->getAttribute('href');
    }

    public static function getPageItems(\DOMDocument $dom) {
        $xpath = new \DOMXPath($dom);
        $items = $xpath->query('//tbody[@class="itemWrapper"]');
        $result = array();
        $itemsa = static::createWL('items', null, $dom);
        foreach($items as $item) {
            $nitty = static::processPageItem($item, $xpath);
            $itemsa->appendChild($nitty);
        }
        return $itemsa;
    }

    public static function createWL($name, $parent = null, \DOMDocument $document = null, $text = null) {
        if ( !$document ) {
            if ( $parent ) {
                $document = $parent->ownerDocument;
            } else {
                throw new Exception("No document specified");
            }
        }
        $element = $document->createElementNS('urn:vynar:wishlist', 'wl:'.$name);
        if ( $text )
            $element->appendChild($document->createTextNode($text));
        if ( $parent )
            return $parent->appendChild($element);
        return $element;
    }

    public static function createText($text, $parent, DOMDocument $document = null) {
        if ( !$document ) {
            if ( $parent ) {
                $document = $parent->ownerDocument;
            } else {
                throw new Exception("No document specified");
            }
        }
        $element = $document->createTextNode($text);
        if ( $parent )
            return $parent->appendChild($element);
        return $element;
    }
    static $priorities = array('lowest' => '1', 'low'=>'2', 'medium'=>'3', 'high'=>'4', 'highest'=>'5');
    public static function processPageItem(\DOMElement $item, \DOMXPath $xpath) {

        $dom = $item->ownerDocument;
        $bit = static::createWL('item', null, $item->ownerDocument);

        $hrefAttribute = $xpath->query("(.//*[@class='small productTitle']/*/*/@href)",     $item)->item(0);
        $titleText     = $xpath->query("(.//*[@class='small productTitle']/*/*/text())[1]", $item)->item(0);
        $imageElement  = $xpath->query("(.//*[@class='productImage']/*/*[local-name()='img'])[1]", $item)->item(0);
        $priorityText  = $xpath->query("(.//*[@class='priorityValueText']/text())[1]",      $item)->item(0);
        $priceText     = $xpath->query("(.//*[@class='wlPriceBold']/*/text())[1]",          $item)->item(0);
        $nameAttribute = $xpath->query("@name", $item)->item(0);
        if ( $hrefAttribute && preg_match('/dp\/([A-Z0-9]+)\//', $hrefAttribute->nodeValue, $m) )
            static::createWL('id', $bit, null, $m[1]);
        if ( $nameAttribute )
            static::createWL('name', $bit, null, $nameAttribute->nodeValue);
        if ( $hrefAttribute )
            static::createWL('link', $bit, null, $hrefAttribute->nodeValue);
        if ( $titleText )
            static::createWL('title', $bit, null, $titleText->nodeValue);
        if ( $priceText )
            static::createWL('price', $bit, null, $priceText->nodeValue);
        $destImage = null;
        if ( $imageElement ) {
            $destImage = static::createWL('image', $bit);
            foreach(array('width', 'height', 'alt', 'src') as $attName) {
                $attValue =  $imageElement->getAttribute($attName);
                if ( $attValue )
                    static::createWL($attName, $destImage, null, $attValue);
            }

        }
        $priority = $priorityText ? $priorityText->nodeValue : null;
        $priorityLevel = isset(static::$priorities[$priority]) ? static::$priorities[$priority] : 3;
        static::createWL('priority', $bit, null, $priority)->setAttribute('level', $priorityLevel);

        return $bit;
    }

    public function getDOM($url) {
        $html = $this->__fetcher->fetchURL($url);
        if ( !$html ) return;
        $xml = $this->__tidier->TidyXML($html);
        if ( !$xml ) return;
        $dom = clone $this->__tidier->XMLToDOM($xml);
        return $dom;
    }

    public function PickUpInterestingBits(\DOMDocument $dom) {
        $xpath = new \DOMXPath($dom);
        $items = $xpath->query("//*[local-name() = 'items' and namespace-uri() = 'urn:vynar:wishlist']")->item(0);
        $bam = new \DOMDocument("1.0", "UTF-8");
        $bam->appendChild($bam->importNode($items, true));
        return $bam;
    }

    public function FetchWishlistPages($id, Fetcher $fetcher = null, Tidier $tidier = null) {
        if ( !ctype_alnum($id) )
            throw new \Exception("Specified ID is invalid.");

        $startURL = 'http://www.amazon.co.uk/wishlist/'.$id.'/ref=cm_wl_prev_ret?_encoding=UTF8&reveal=';
        $url = $startURL;
        $pagesIndex = array();

        while($url !== null) {
            $dom = $this->getDOM($url);
            if ( !$dom )
                throw new Exception("Failed to load DOM for: '{$url}'");
            $pagesIndex[$url] = $dom;
            $url = $this->getNextPageLink($dom);
            $dom->documentElement->setAttribute('xmlns', 'http://www.w3.org/1999/xhtml');
            if ( $url && substr($url, 0, 1) == '/')
                $url = 'http://www.amazon.co.uk'.$url;
            if ( isset($pagesIndex[$url]))
                $url = null;
        }

        $rootDocument = new \DOMDocument("1.0","UTF-8");
        $rootDocument->loadXML('<index xmlns="urn:vynar:pageindex"></index>');
        foreach($pagesIndex as $url => $dom) {
            $html = $rootDocument->importNode($dom->documentElement, true);
            $rootDocument->documentElement->appendChild($html);
            $html->setAttributeNS('urn:vynar:pageindex', 'pi:url', $url);
        }
        $items = static::getPageItems($rootDocument);
        $rootDocument->documentElement->appendChild($items);
        return $rootDocument;
    }
}


function encoding() {
    mb_internal_encoding("UTF-8");
    mb_http_output("UTF-8");
}

class Tidier {
	protected $__tidy;
	protected $__dom;
	public function __construct(\tidy $tidy = null, \DOMDocument $dom = null) {
		$this->__tidy = $tidy ? $tidy : new \tidy;
		$this->__dom = $dom ? $dom : new \DOMDocument("1.0", "UTF-8");
	}
	public function TidyXML($html, \tidy $tidy = null, \DOMDocument $dom = null) {
		$config = array (
			'indent' => false,
			'input-xml'  => false,
			'output-xml' => true,
			'numeric-entities' => true,
			'wrap'=>false
		);
		$tidy = $this->__tidy;
		$tidy->parseString($html, $config, 'utf8');
		$tidy->cleanRepair();
		$tidied = (string)$tidy;
		return $tidied;
	}
	public function XMLToDOM($xml, \DOMDocument $dom = null) {
		if ($this->__dom->loadXML($xml))
			return $this->__dom;
	}

}


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

?>