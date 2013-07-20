<?php
namespace Awl;

function transform(\DOMDocument $dom, $xslFile = 'xhtml.xsl') {

    if ( !file_exists($xslFile)&& defined("_RESOURCES") )
        $xslFile = _RESOURCES.DIRECTORY_SEPARATOR.$xslFile;

    $xsl = new \DOMDocument;
    $xsl->load($xslFile);

    $proc = new \XSLTProcessor;
    $proc->importStylesheet($xsl);
    return $proc->transformToDoc($dom);
}
?>