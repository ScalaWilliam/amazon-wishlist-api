<?php
error_reporting(E_ALL);
require_once "tidy.php";
\awl\encoding();

header("Content-type: text/plain; charset=utf-8");
$wish = new \Awl\Amazon_Wishlist_Fetcher(new \Awl\SerialisedFetcher("cacher.dat"));
$result = $wish->FetchWishlistPages('1FY1N9FN7CLX8');
echo $result->saveXML();

?>