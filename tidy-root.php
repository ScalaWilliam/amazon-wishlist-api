<?php
error_reporting(E_ALL);
require_once "tidy.php";
\awl\encoding();

// Ubuntu: sudo apt-get install php5-tidy
// TODO: write some tests ;)
//                         Somehow I don't think I'll manage to get there though!
$fetchFullQ = isset($_GET['full']) && $_GET['full'] === 'full';
$fetchID = isset($_GET['id']) && is_string($_GET['id']) ? $_GET['id'] : '1FY1N9FN7CLX8';


header("Content-type: text/xml; charset=utf-8");

//$wish = new \Awl\Amazon_Wishlist_Fetcher;
//$fetcher = new \Awl\SerialisedFetcher("cacher.dat");
$fetcher = new \Awl\SQLiteFetcher(new PDO('sqlite:banaga.sqlite'), 'bang', 3600);
$wish = new \Awl\Amazon_Wishlist_Fetcher($fetcher);
$result = $wish->FetchWishlistPages($fetchID);
if ( !$fetchFullQ )
    $result = $wish->PickUpInterestingBits($result);

echo $result->saveXML();

?>