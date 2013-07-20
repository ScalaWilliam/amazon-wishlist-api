<?php
error_reporting(E_ALL);
define('_AWL_DEFAULT_ID', '1FY1N9FN7CLX8');
define('_RESOURCES', 'resources');
set_include_path(get_include_path() . PATH_SEPARATOR . _RESOURCES);

if ( !class_exists('tidy', true)) die("<code>sudo apt-get install php5-tidy</code>");
if ( !class_exists('XSLTProcessor', true)) die("<code>sudo apt-get install php5-xsl</code>");

require_once "resources/awl.inc.php";
require_once "resources/util.inc.php";
\Awl\encoding();



function protect_awl() {
    $_GET['full'] = null;
    $_GET['id'] = null;
    $_GET['renew'] = null;
    $_GET['samples'] = null;
    $_GET['images'] = null;
}

$protect = true;
/* comment the following line to access everything in the API */
$protect = false;

// if running via CLI, we don't really care.
if ( isset($_SYSTEM['argv'][0]) )
    $protect = false;

if ( $protect )
    protect_awl();

$sqlite = 'sqlite:samples/awl.sqlite';

// Ubuntu: sudo apt-get install php5-tidy
// TODO: write some tests ;)
// Somehow I don't think I'll manage to get there though!


$fetchID = isset($_GET['id']) && is_string($_GET['id']) ? $_GET['id'] : _AWL_DEFAULT_ID;
$feed = isset($_GET['feed']) ? (in_array($_GET['feed'], array('atom', 'rss'), true) ? $_GET['feed'] : null) : null;

list($fetchFullQ, $renew, $data, $createSamples, $downloadImages) = \Awl\require_switches('full', 'renew', 'data', 'samples', 'images');

if ( !defined('_USE_LOCAL_IMAGES'))
    define('_USE_LOCAL_IMAGES', $downloadImages);

$modes = array('default', 'datafile', 'sqlite');
$mode = 'sqlite';
if ( !in_array($mode, $modes, true) )
    $mode = 'default';


if ( $mode == 'default' ) {
    $wish = new \Awl\Amazon_Wishlist_Fetcher;
}
if ( $mode == 'sqlite' ) {
    // caching here is just for a demo.
    // I'd say, stick with the 'default' mode and perform caching at the highest level possible
    // we don't want to have cache running in a dozen places, and having to figure out where things went wrong
    // Load up the results directly (use this as a straightforward api) -> cache/save them then...
    // but for development purposes - stick to sqlite, it's neat.
    $timeout = $renew ? 0 : 300600;
    $fetcher = new \Awl\SQLiteFetcher(new PDO($sqlite), 'bang', $timeout);
}
if ( $mode == 'datafile' ) {
    $fetcher = new \Awl\SerialisedFetcher("cacher.dat");
}
if ( $mode != 'default' ) {
    $wish = new \Awl\Amazon_Wishlist_Fetcher($fetcher);
}

//header("Content-type: text/plain; charset=utf-8");

$combined = $wish->FetchWishlistPages($fetchID);
if ( $createSamples ) \Awl\file_put_contents_utf8('samples/A.combined.xml', $combined->saveXML());
$semantic = $wish->semanticise($combined);
if ( $createSamples ) \Awl\file_put_contents_utf8('samples/B.semantic.xml', $semantic->saveXML());
$slim = $wish->PickUpInterestingBits($semantic);
if ( $createSamples ) \Awl\file_put_contents_utf8('samples/C.slim.xml', $slim->saveXML());


if ( _USE_LOCAL_IMAGES )
    \Awl\download_images($slim);

$xml = $xhtml = $atom = $rss = null;



if ( $feed === 'rss') {
    header("Content-type: application/rss+xml; charset=utf-8");
    $rss = \Awl\transform($slim, 'rss.xsl');
    echo $rss->saveXML();
} elseif ( $feed === 'atom' ) {
    header("Content-type: application/atom+xml; charset=utf-8");
    $atom = \Awl\transform($slim, 'atom.xsl');
    echo $atom->saveXML();
} elseif ($data) {
    header("Content-type: text/xml; charset=utf-8");
    echo $slim->saveXML();
} elseif ( $fetchFullQ ) {
    header("Content-type: text/xml; charset=utf-8");
    echo $semantic->saveXML();
} else {
    header("Content-type: application/xhtml+xml; charset=utf-8");
    if ( !defined('_AWL_XHTML') )
        define('_AWL_XHTML', 'xhtml.xsl');
    $xhtml = \Awl\transform($slim, _AWL_XHTML);
    echo $xhtml->saveXML();
}



?>