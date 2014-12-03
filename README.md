Scala version for Amazon Wishlist API.

Demo:
http://demo.scalawilliam.com/amazon-wishlist-api/

Previously PHP, now SBT+Scala+Spray+AngularJS+JSoup. Cool stuff, I like it myself.

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

Default address: http://localhost:7119/