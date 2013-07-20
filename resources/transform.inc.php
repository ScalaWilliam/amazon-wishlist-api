<?php
namespace Awl;

function transform(\DOMDocument $dom, $xslFile = 'great-style.xsl') {

    if ( !file_exists($xslFile)&& defined("_RESOURCES") )
        $xslFile = _RESOURCES.DIRECTORY_SEPARATOR.$xslFile;

    $xsl = new \DOMDocument;
    $xsl->load($xslFile);

    $proc = new \XSLTProcessor;
    $proc->registerPHPFunctions(array('\Awl\aidate', 'function_exists', 'md5', '\Awl\localsrc'));
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
function localsrc($val) {
    if ( !is_string($val) ) return;
    if ( !preg_match('/^https?:\/\//i', $val) ) return;
    if ( !_USE_LOCAL_IMAGES ) return;
    return 'samples/images/'.md5($val).'.dat';
}
function download_images(\DOMDocument $slim) {
    $ns = "https://vynar.com/2013/amazon-wishlist";
    $xpath = new \DOMXPath($slim);
    $xpath->registerNamespace($ns, 'wi');
    $images = $xpath->query('//wi:image[@wi:localsrc]');
    foreach($images as $image) {
        $from = $image->getAttributeNS($ns, 'src');

        if ( !preg_match('/^https?:\/\//i', $from) )
            continue;

        $to = $image->getAttributeNS($ns, 'localsrc');

        if ( file_exists($to) ) continue;
        if ( !$to ) continue;
        file_put_contents($to, file_get_contents($from));
    }
}
?>