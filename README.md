unitcover
=======
[![Build Status](https://travis-ci.org/pussinboots/unitcover.svg?branch=master)](https://travis-ci.org/pussinboots/unitcover)
[![Coverage Status](https://img.shields.io/coveralls/pussinboots/unitcover.svg)](https://coveralls.io/r/pussinboots/unitcover?branch=master)
[![Dependencies](https://david-dm.org/pussinboots/unitcover.png)](https://david-dm.org/pussinboots/unitcover)
[![Heroku](http://heroku-badge.heroku.com/?app=unitcover)](https://unitcover.herokuapp.com/products.html#/builds/pussinboots/unitcover)
[Unit Tests](https://unitcover.herokuapp.com/products.html#/builds/pussinboots/unitcover)


Since  one or two weeks i search for a service like [coveralls]() but only for test results and i didn't found something for free and
SonarQube seems to complex for me. So i decided to build one similar to coveralls and that is my motivation. It is in aeryl stage now and implemented in one day so there still a lot of work to do but can be used.

Feel free to fork this repo and hosted it self the heroku instance running is a free one and very limited one web dyno and 10 database connection so please contact me before you want to upload something in the moment.

##TODO
* authorization is complete missing
* nice and usable design
* buildnumber generation is missing always 1
* sbt plugin to upload the unit reports (maybe the solution with curl seems very simple)
* grouping of test suites to one build (need build number generation)
* support badge images
* link test reports with travis build

##Uasage

There is no build integration yet but with the following script you could upload your sbt and karma junit reports
```bash
#!/bin/bash
owner=pussinboots
project=unitcover
FILES=./target/test-reports/*
#upload play junit reports
for f in $FILES
do
  echo "Processing $f file..."
  curl -H "Content-Type:application/xml" -X POST -d @$f http://unitcover.herokuapp.com/api/$owner/$project
done

curl -H "Content-Type:application/xml" -X POST -d @test-result.xml http://unitcover.herokuapp.com/api/$owner/$project

```
