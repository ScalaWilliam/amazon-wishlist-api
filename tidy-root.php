<?php
error_reporting(E_ALL);
require_once "tidy.php";
\awl\encoding();

// Ubuntu: sudo apt-get install php5-tidy
// TODO: write some tests ;)
//                         Somehow I don't think I'll manage to get there though!
$fetchFullQ = isset($_GET['full']) && $_GET['full'] === 'full';
$fetchID = isset($_GET['id']) && is_string($_GET['id']) ? $_GET['id'] : '1FY1N9FN7CLX8';
$renew = isset($_GET['renew']) && $_GET['renew'] === 'renew';
$ashtml = isset($_GET['html']) && $_GET['html'] === 'html';

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
    $fetcher = new \Awl\SQLiteFetcher(new PDO('sqlite:banaga.sqlite'), 'bang', $timeout);
}
if ( $mode == 'datafile' ) {
    $fetcher = new \Awl\SerialisedFetcher("cacher.dat");
}
if ( $mode != 'default' ) {
    $wish = new \Awl\Amazon_Wishlist_Fetcher($fetcher);
}

$result = $wish->FetchWishlistPages($fetchID);

if ( !$fetchFullQ )
    $result = $wish->PickUpInterestingBits($result);

header("Content-type: text/xml; charset=utf-8");


if ( !$ashtml ) {
    $xml = $result->saveXML();
    echo $xml;
} else {
    $xhtml = \Awl\pretty($result);
    echo $xhtml;
}

?>