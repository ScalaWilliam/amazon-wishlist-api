<?php

namespace Awl;
require_once "tidier.inc.php";
require_once "transform.inc.php";
require_once "fetcher.inc.php";


/*
 * Use a bit of dependency injection
 * And make use of PHP's DOM functionality
 * Also use Tidy to clean up any nasty HTML.
 */


class Amazon_Wishlist_Fetcher {
    protected $__fetcher;
    protected $__tidier;
    static $namespace = "https://vynar.com/2013/amazon-wishlist";
    public function __construct(Fetcher $fetcher = null, Tidier $tidier = null) {
        $this->__fetcher = $fetcher ? $fetcher : new URLFetcher;
        $this->__tidier = $tidier ? $tidier : new Tidier();
    }
    public static function createWL($name, $parent = null, \DOMDocument $document = null, $text = null) {
        if ( !$document ) {
            if ( $parent ) {
                $document = $parent->ownerDocument;
            } else {
                throw new Exception("No document specified");
            }
        }
        $element = $document->createElementNS(static::$namespace, 'wl:'.$name);
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
    public static function semanticise(\DOMDocument $dom) {
        $xpath = new \DOMXPath($dom);


        $ns = static::$namespace;

        $aboutMe = $xpath->query('//*[@id="profile-description-visitor-Field"]/text()')->item(0);
        if ( $aboutMe ) {
            if ( $aboutMe->nodeValue != 'Nothing entered' && trim($aboutMe->nodeValue) != '' ) {
                $aboutMeNode = $dom->createElementNS($ns, 'wi:about');
                $aboutMe->parentNode->replaceChild($aboutMeNode, $aboutMe);
                $aboutMeNode->appendChild($aboutMe);
            }
        }
        $birthday = $xpath->query('//*[@id="profile-birthday-Field"]/text()')->item(0);
        if ( $birthday ) {
            if ( $birthday->nodeValue != 'None entered' && trim($birthday->nodeValue) != '') {
                $birthdayNode = $dom->createElementNS($ns, 'wi:birthday');
                $birthday->parentNode->replaceChild($birthdayNode, $birthday);
                $birthdayNode->appendChild($birthday);
            }
        }
        $address = $xpath->query('//*[@id="profile-address-Field"]/text()')->item(0);
        if ( $address ) {
            if ( $address->nodeValue != 'None entered' && trim($address->nodeValue) != '') {
                $addressNode = $dom->createElementNS($ns, 'wi:address');
                $address->parentNode->replaceChild($addressNode, $address);
                $addressNode->appendChild($address);
            }
        }
        $name = $xpath->query('//*[@id="profile-name-Field"]/text()')->item(0);
        if ( $name ) {
            if ( $name->nodeValue != 'None entered' && trim($name->nodeValue) != '') {
                $nameNode = $dom->createElementNS($ns, 'wi:name');
                $name->parentNode->replaceChild($nameNode, $name);
                $nameNode->appendChild($name);
            }
        }
        $title = $xpath->query('//*[local-name()="H1" and @class="visitor"]/text()')->item(0);
        if ( $title ) {
            if ( $title->nodeValue != 'None entered') {
                $titleNode = $dom->createElementNS($ns, 'wi:title');
                $title->parentNode->replaceChild($titleNode, $title);
                $titleNode->appendChild($title);
            }
        }

        $items = $xpath->query('//tbody[@class="itemWrapper"]');
        $result = array();
        foreach($items as $item) {
            $result[] = static::processItemX($item, $xpath);
        }
        return $result;
    }
    public static function processItemX(\DOMElement $item, \DOMXPath $xpath) {

        $dom = $item->ownerDocument;


        $ns = static::$namespace;
        $itemNode = $dom->createElementNS($ns, 'wi:item');
        $item->parentNode->replaceChild($itemNode, $item);
        $itemNode->appendChild($item);


        $hrefAttribute = $xpath->query("(.//*[@class='small productTitle']/*/*/@href)",     $item)->item(0);
        if ( $hrefAttribute ) {
            $link = $hrefAttribute->nodeValue;
            $linkNode = $item->ownerDocument->createElementNS($ns, 'wi:link');
            $linkTextNode = $item->ownerDocument->createTextNode($link);
            $item->appendChild($linkNode)->appendChild($linkTextNode);
        }

        $titleText     = $xpath->query("(.//*[@class='small productTitle']/*/*/text())[1]", $item)->item(0);
        if ( $titleText ) {
            $titleNode = $item->ownerDocument->createElementNS($ns, 'wi:title');
            $titleText->parentNode->replaceChild($titleNode, $titleText);
            $titleNode->appendChild($titleText);
        }
        $imageElement  = $xpath->query("(.//*[@class='productImage']/*/*[local-name()='img'])[1]", $item)->item(0);
        if ( $imageElement ) {
            $destImage = $item->ownerDocument->createElementNS($ns, 'wi:image');
            $imageElement->parentNode->insertBefore($destImage, $imageElement->nextSibling);
            foreach(array('width', 'height', 'alt', 'src') as $attName) {
                $attValue = $imageElement->getAttribute($attName);
                if ( $attValue ) {
                    $destImage->setAttributeNS($ns, 'wi:'.$attName, $attValue);
                }
            }
        }

        $xpath->registerNamespace('x', 'http://www.w3.org/1999/xhtml');
        $addedWhen = $xpath->query('.//td[contains(@class, "lineItemOwnerInfoJS")]//nobr', $item)->item(0);
        if ( $addedWhen && preg_match('/^Added (.*)$/i', $addedWhen->textContent, $m) != 0 ) {
            $addedWhen->childNodes->item(0)->nodeValue = 'Added ';
            $addedWhenWI = $dom->createElementNS($ns, 'wi:added');
            $addedWhen->appendChild($addedWhenWI);
            $date = $m[1];
            $time = strtotime($date);
            $addedWhenWI->appendChild($dom->createTextNode($date));
            $addedWhenWI->setAttributeNS($ns, 'wi:atom', date(DATE_ATOM, $time));
            $addedWhenWI->setAttributeNS($ns, 'wi:rss', date(DATE_RSS, $time));
            $addedWhenWI->setAttributeNS($ns, 'wi:rfc', date(DATE_RFC822, $time));
        }

        $priorityText  = $xpath->query("(.//*[@class='priorityValueText']/text())[1]", $item)->item(0);
        $priorityNode = $item->ownerDocument->createElementNS($ns, 'wi:priority');
        if ( $priorityText ) {
            $priorityText->parentNode->replaceChild($priorityNode, $priorityText);
            $priorityNode->appendChild($priorityText);
        } else {
            $item->appendChild($priorityNode);
        }

        $priority = $priorityText ? $priorityText->nodeValue : null;

        $priorityLevel = isset(static::$priorities[$priority]) ? static::$priorities[$priority] : 3;
        $priorityNode->setAttributeNS($ns, 'wi:level', (string)$priorityLevel);


        $priceText     = $xpath->query("(.//*[@class='wlPriceBold']/*/text())[1]",          $item)->item(0);

        if ( $priceText ) {
            $priceNode = $item->ownerDocument->createElementNS($ns, 'wi:price');
            $priceText->parentNode->replaceChild($priceNode, $priceText);
            $priceNode->appendChild($priceText);
        }
        $nameAttribute = $xpath->query("@name", $item)->item(0);
        if ( $hrefAttribute && preg_match('/dp\/([A-Z0-9]+)\//', $hrefAttribute->nodeValue, $m) )
            static::createWL('id', $item, null, $m[1]);
        if ( $nameAttribute )
            static::createWL('name', $item, null, $nameAttribute->nodeValue);

        return $item;
    }
    static $priorities = array('lowest' => '1', 'low'=>'2', 'medium'=>'3', 'high'=>'4', 'highest'=>'5');
    public function getDOM($url) {
        $html = $this->__fetcher->fetchURL($url);
        if ( !$html ) return;
        $xml = $this->__tidier->TidyXML($html);
        if ( !$xml ) return;
        $dom = clone $this->__tidier->XMLToDOM($xml);
        return $dom;
    }

    public function PickUpInterestingBits(\DOMDocument $dom) {
        $result = transform($dom, 'extract-useful.xsl');
        return $result;
    }

    public function FetchWishlistPages($id, Fetcher $fetcher = null, Tidier $tidier = null) {
        if ( !ctype_alnum($id) )
            throw new \Exception("Specified ID is invalid.");

        $startURL = 'http://www.amazon.co.uk/wishlist/'.$id.'/ref=cm_wl_prev_ret?_encoding=UTF8&reveal=';
        $url = $startURL;
        $pagesIndex = array();

        while($url !== null) {
            $origUrl = $url;
            list($dom, $url) = $this->processPage($url);
            $pagesIndex[$origUrl] = $dom;
        }
        return static::composePagesIndex($pagesIndex, $id);
    }
    public function processPage($url) {
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
        return array($dom, $url);
    }
    public function composePagesIndex($pagesIndex, $id) {
        $rootDocument = new \DOMDocument("1.0","UTF-8");
        $ns = static::$namespace;
        $rootDocument->loadXML('<wishlist xmlns="'.$ns.'"></wishlist>');
        $idElement = $rootDocument->createElementNS($ns, 'wl:id', $id);
        $rootDocument->documentElement->appendChild($idElement);
        foreach($pagesIndex as $url => $dom) {

            $page = $rootDocument->documentElement->appendChild($rootDocument->createElementNS($ns, 'wi:page'));
            $page->appendChild($rootDocument->createElementNS($ns, 'wi:url'))->appendChild($rootDocument->createTextNode($url));
            $html = $rootDocument->importNode($dom->documentElement, true);
            $page->appendChild($html);
        }

        static::semanticise($rootDocument);

        return $rootDocument;
    }
}
