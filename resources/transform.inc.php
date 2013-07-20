<?php
namespace Awl;

function transform(\DOMDocument $dom, $xslFile = 'great-style.xsl') {

    if ( !file_exists($xslFile)&& defined("_RESOURCES") )
        $xslFile = _RESOURCES.DIRECTORY_SEPARATOR.$xslFile;

    $xsl = new \DOMDocument;
    $xsl->load($xslFile);

    $proc = new \XSLTProcessor;
    $proc->registerPHPFunctions(array('\Awl\aidate', 'function_exists'));
    $proc->importStylesheet($xsl);
    $res = $proc->transformToXML($dom);
    $newDOM = new \DOMDocument;
    $newDOM->loadXML($res);
    return $newDOM;
}
function aidate($val) {
    if ( !(is_array($val) && count($val) == 1 && $val[0] instanceof \DOMElement) ) return;
    $dom = new \DOMDocument;

    $addedWhen = $dom->importNode($val[0]->cloneNode(true), true);
    $ns = 'https://vynar.com/2013/amazon-wishlist';
    $dom->appendChild($addedWhen);
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
        $addedWhenWI->setAttributeNS($ns, 'wi:unix', $time);
    }
    return $dom;
}
?>