<?php
namespace Awl;
function require_switch($key) {
    if (isset($_SYSTEM['argv']) && in_array($key, $_SYSTEM['argv'], true)) return true;
    if (isset($_GET[$key]) && $_GET[$key] === $key) return true;
    return false;
}

function require_switches() {
    return array_map('\Awl\require_switch', func_get_args());
}
?>