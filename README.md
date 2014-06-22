unitcover
=======
[![Build Status](https://travis-ci.org/pussinboots/unitcover.svg?branch=master)](https://travis-ci.org/pussinboots/unitcover)
[![Coverage Status](https://img.shields.io/coveralls/pussinboots/unitcover.svg)](https://coveralls.io/r/pussinboots/unitcover?branch=master)
[![Dependencies](https://david-dm.org/pussinboots/unitcover.png)](https://david-dm.org/pussinboots/unitcover)
[![Heroku](http://heroku-badge.heroku.com/?app=unitcover)](https://unitcover.herokuapp.com/products.html#/builds/pussinboots/unitcover/builds)
[![Unit Tests](https://unitcover.herokuapp.com/api/pussinboots/unitcover/badge?ts=10)](https://unitcover.herokuapp.com/#/builds/pussinboots/unitcover/builds)


Since one or two weeks i search for a service like [coveralls](https://coveralls.io) but only for test results and i didn't found something for free and
SonarQube seems to complex for me. So i decided to build one similar to coveralls and that is my motivation. It is in aeryl stage now and implemented in one day so there still a lot of work to do but can be used.

Feel free to fork this repo and hosted it self the heroku instance running is a free one and very limited one web dyno and 10 database connection so please contact me before you want to upload something in the moment. 

At the top of the project you see different badges and the badge on the right side is the badge from this unitcover project. Thanks to [shields.io](http://shields.io/) they make the creation of image badges so easy like calculate 1 + 1.

##Supported unit reports
To increase the supported unit reports from different language send me or upload it into the test/resources folder.

* karma unit report
* sbt unit report

##Done
* max build history is ten
* updated to play 2.3.0
* migrate from slick version 1 to 2
* buildnumber generation is missing always 1 (done)
* grouping of test suites to one build (need build number generation) (done)
* support badge images (done) like [![Unit Tests](https://unitcover.herokuapp.com/api/pussinboots/unitcover/badge?ts=10)](https://unitcover.herokuapp.com/#/builds/pussinboots/unitcover/builds)
* link test reports with travis build (done)

##TODO
* authorization is complete missing
* nice and usable design (never done)
* sbt plugin to upload the unit reports (maybe the solution with curl seems very simple)

Transfer the todos to trello is still open

##Features
Todo and features are mantained with trello now [Trello](https://trello.com/b/tPkEhbaY/unitcover)


##Usage

There is no build integration yet but with the following script you could upload your sbt and karma junit reports
```bash
#!/bin/bash
owner=pussinboots
project=unitcover
endpoint=unitcover.herokuapp.com
FILES=./target/test-reports/*
trigger="trigger=Travis"
travisBuildId=""
 if [[ -n ${TRAVIS} ]]
    then
        echo "travis build detected"
        travisBuildId="&travisBuildId=$TRAVIS_BUILD_ID"
    else
        trigger="trigger=Local"
    fi
#create new build and fetch the build number
buildnumber=$(curl -s -X POST http://$endpoint/api/$owner/$project/builds?$trigger$travisBuildId | sed -E 's/.*"buildNumber":([0-9]*).*/\1/')
echo $buildnumber
echo "http://$endpoint/api/$owner/$project/$buildnumber"
for f in $FILES
do
  echo "Processing $f file..."
  #upload play junit reports
  curl -H "Content-Type:application/xml" -X POST -d @$f http://$endpoint/api/$owner/$project/$buildnumber
done
#upload karma unit report
curl -H "Content-Type:application/xml" -X POST -d @test-results.xml http://$endpoint/api/$owner/$project/$buildnumber

curl -X POST http://$endpoint/api/$owner/$project/builds/$buildnumber/end
```
##Build

```
git clone https://github.com/pussinboots/unitcover.git
cd unitcover
npm install
sbt -Dconfig.file=conf/application-e2e.conf run
```
Than the [server](http://localhost:9000/#/builds/pussinboots/banbapp/builds) should be accessible with some dummy data. It use 
an h2 in memory database.

###Requirements
* play 2.3.0
* nodejs
