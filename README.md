Amazon Wishlist Scraper API
====
A PHP5 based Amazon Wishlist API with supporting examples and code around it.

By William Narmontas - https://vynar.com/
==
Requirements:
* Tidy: apt-get install php5-tidy
* XSL: apt-get install php5-xsl

Getting started
Just copy this repository into a directory that you like.
You can use this application either as an API or as the end-user application.
We'll assume you've put everything in https://vynar.com/2013/amazon-wishlist-api/

A demo is available here:
https://vynar.com/2013/amazon-wishlist-api/api.php

World-facing:
Request wishlist XML data:
* https://vynar.com/2013/amazon-wishlist-api/api.php?data=data
* Sampled: https://vynar.com/2013/amazon-wishlist-api/samples/C.slim.xml

Request the RSS feed:
* https://vynar.com/2013/amazon-wishlist-api/api.php?feed=rss

Request the Atom feed:
* https://vynar.com/2013/amazon-wishlist-api/api.php?feed=atom

Development:

First, comment the following line in api.php:
$protect = false;
Generate XML samples:
* https://vynar.com/2013/amazon-wishlist-api/api.php?samples=samples

Force re-newal of SQLite database:
* https://vynar.com/2013/amazon-wishlist-api/api.php?renew=renew

Request semantic XML data:
* Sampled: https://vynar.com/2013/amazon-wishlist-api/api.php?full=full
* Sampled: https://vynar.com/2013/amazon-wishlist-api/samples/A.combined.xml

Request raw XML data:
* Sampled: https://vynar.com/2013/amazon-wishlist-api/samples/A.combined.xml
* Also available in the SQLite database.

How it works:
1. Fetch the necessary data from the Amazon site. Cache it. Combine it.
2. Turn the combined document into a semantic document.
3. Extract the semantic data.
4. Process it into XML and then HTML/RSS/Atom if required.

Technical bits:
Caching: there are three classes that fetch data for us. The main one uses SQLite and the other one uses PHP file/serialization.
Adding semantic data: this is done using XSLT 1.0. There is a PHP function that is being used inside the semanticiser.
Extracting semantic data: this is done using XSLT 1.0.
Transforming into HTML/RSS/Atom: again all done using XSL transformations.
