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

$modes = array('default', 'datafile', 'sqlite');

$mode = 'sqlite';
if ( !in_array($mode, $modes, true) )
    $mode = 'default';


if ( $mode == 'default' ) {
    $wish = new \Awl\Amazon_Wishlist_Fetcher;
}
if ( $mode == 'sqlite' ) {
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

echo $result->saveXML();

?>