<?php

namespace Awl;
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


?>