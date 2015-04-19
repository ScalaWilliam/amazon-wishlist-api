Scala version for Amazon Wishlist API.

Demo:
http://demo.scalawilliam.com/amazon-wishlist-api/

Stuff:
* sbt 0.13.8
* sbt-native-packager 1.0.0
* Scala 2.11.6
* akka-http-experimental 1.0-M5
* spray-client 1.3.3
* AngularJS
* JSoup

Run this application:

```
sbt clean test run
```

Build an XZ tarball:
```
sbt clean test universal:packageXzTarball
```

Publish it:
```
sbt clean test publish
```

Extract it:
```
tar Jxvf abc.txz
```

Run it:
```
./bin/amazon-wishlist-api
./bin/amazon-wishlist-api -Dhttp.host=0.0.0.0 -Dhttp.port=4414
```

You can set the root context with:
```
-Dhttp.context=
-Dhttp.context=/hello/john
```
These will resolve to, ie, API paths, "/get" and "/hello/john/get" respectively.

Default address: http://localhost:7119/

![Example JSON response](json.png)
![Example AngularJS UI](screenshot.png)
![Source Wishlist example](amazon.png)

This trawler does deal with large Wishlists, ie composed of multiple pages.
