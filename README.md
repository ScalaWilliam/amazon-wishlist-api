Amazon Wishlist Scraper API
====
A PHP5 based Amazon Wishlist API with supporting examples and code around it.

Developed By William Narmontas - https://vynar.com/

**A demo is available here:**

* https://vynar.com/2013/amazon-wishlist/

Getting started
---


### Requirements (Debuntu Linux):

~~~bash
apt-get install php5-tidy php5-xsl # XSL and Tidy
~~~

### Installation
Just copy this repository into a directory that you like.
You can use this application either as an API or as the end-user application.
We'll assume you've put everything in:

 * https://vynar.com/2013/amazon-wishlist/

So that it can be accessed via:

 * https://vynar.com/2013/amazon-wishlist/api.php

### World-facing: ###
Request wishlist XML data:

 * https://vynar.com/2013/amazon-wishlist/api.php?data=data
 * Sampled: https://vynar.com/2013/amazon-wishlist/samples/C.slim.xml

Request the RSS feed:

 * https://vynar.com/2013/amazon-wishlist/api.php?feed=rss

Request the Atom feed:

 * https://vynar.com/2013/amazon-wishlist/api.php?feed=atom

Use cases
___

This application can be used:

 * As a back-end API for collecting information about your Amazon Wishlist.
 * As a front-end site. It works with tablets (iPad, Android) and desktop browsers (IE10, Chrome, Opera).
 * As a plug-in for your personal site.


Development
___


Everything that you need to know is located in 'api.php' Once you've got the API running in your hosting environment, comment the following line in 'api.php':

~~~php
$protect = false;
~~~

so that it looks like this:

~~~php
// $protect = false;
~~~


This will make sure that you can access the full features.


Then visit the following URLs:

__Generate XML samples:__

 * https://vynar.com/2013/amazon-wishlist/api.php?samples=samples

__Force re-newal of SQLite database__:

 * https://vynar.com/2013/amazon-wishlist/api.php?renew=renew

__Request semantic XML data:__

 * https://vynar.com/2013/amazon-wishlist/api.php?full=full
 * Sampled: https://vynar.com/2013/amazon-wishlist/samples/A.combined.xml

__Request raw XML data:__

 * Sampled: https://vynar.com/2013/amazon-wishlist/samples/A.combined.xml
 * Also available in the SQLite database.

How it works:
----

 1. Fetch the necessary data from the Amazon site. Cache it. Combine it.
 2. Turn the combined document into a semantic document.
 3. Extract the semantic data.
 4. Process it into XML and then HTML/RSS/Atom if required.

### Technical bits ###

**Caching:** There are three classes that fetch data for us. The main one uses SQLite and the other one uses PHP file/serialization.

**Adding semantic data:** This is done using XSLT 1.0. There is a PHP function that is being used inside the semanticiser.

**Extracting semantic data:** This is done using XSLT 1.0.

**Transforming into HTML/RSS/Atom:** Again all done using XSL transformations.


Licensing
---
(2013 William Narmontas https://vynar.com/)
The GitHub code is licensed under the MIT license.
Attribution is very much apprciated.
