unitcover
=======
[![Build Status](https://travis-ci.org/pussinboots/unitcover.svg?branch=master)](https://travis-ci.org/pussinboots/unitcover)
[![Coverage Status](https://img.shields.io/coveralls/pussinboots/unitcover.svg)](https://coveralls.io/r/pussinboots/unitcover?branch=master)
[![Dependencies](https://david-dm.org/pussinboots/unitcover.png)](https://david-dm.org/pussinboots/unitcover)
[![Heroku](https://heroku-badge.herokuapp.com/?app=unitcover)](https://unitcover.herokuapp.com/)
[![Unit Tests](https://unitcover.herokuapp.com/api/pussinboots/unitcover/badge?ts=10)](https://unitcover.herokuapp.com/#/builds/pussinboots/unitcover/builds)
[![Online IDE](https://d2g7ua7d94r3fl.cloudfront.net/assets/images/da4d66c4.codio_logo.png)](https://codio.com/pussinboots/unitcover)

[![Unit Tests](http://unitcover.herokuapp.com/api/pussinboots/unitcover/testsuites/badge)](https://unitcover.herokuapp.com/#/builds/pussinboots/unitcover/builds)

Since one or two weeks i search for a service like [coveralls](https://coveralls.io) but only for test results and i didn't found something for free and
SonarQube seems to complex for me. So i decided to build one similar to coveralls and that is my motivation. It is in aeryl stage now and implemented in one day so there still a lot of work to do but can be used.

Feel free to fork this repo and hosted it self the heroku instance running is a free one and very limited one web dyno and 10 database connection so please contact me before you want to upload something in the moment. 

At the top of the project you see different badges and the badge on the right side is the badge from this unitcover project. Thanks to [shields.io](http://shields.io/) they make the creation of image badges so easy like calculate 1 + 1.

**It runs on the [heroku](https://www.heroku.com/) plattform but with the lowest free scalability setup. So it could take some minutes to restart after it was suspended.**
##Supported unit reports
To increase the supported unit reports from different languages you can send me or upload the unit test result xml file into the test/resources folder. 

* karma unit report
```xml
<?xml version="1.0"?>
<testsuites>
  <testsuite name="Opera 12.16.0 (Linux)" package="" timestamp="2014-06-18T18:38:51" id="0" hostname="vagrant-VirtualBox" tests="3" errors="0" failures="0" time="10.997">
    <properties>
      <property name="browser.fullName" value="Opera/9.80 (X11; Linux x86_64) Presto/2.12.388 Version/12.16"/>
    </properties>
    <testcase name="ten test suites are display" time="9.178" classname="Opera 12.16.0 (Linux).UnitCover build with eleven test suites"/>
    <testcase name="the latest ten test suites should be display started with the eleven test suite" time="1.103" classname="Opera 12.16.0 (Linux).UnitCover build with eleven test suites"/>
    <testcase name="should redirect products.html to products.html#/builds" time="0.716" classname="Opera 12.16.0 (Linux).UnitCover"/>
    <system-out><![CDATA[
]]></system-out>
    <system-err/>
  </testsuite>
  <testsuite name="Firefox 30.0.0 (Ubuntu)" package="" timestamp="2014-06-18T18:38:51" id="0" hostname="vagrant-VirtualBox" tests="3" errors="0" failures="0" time="10.684">
    <properties>
      <property name="browser.fullName" value="Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:30.0) Gecko/20100101 Firefox/30.0"/>
    </properties>
    <testcase name="ten test suites are display" time="9.071" classname="Firefox 30.0.0 (Ubuntu).UnitCover build with eleven test suites"/>
    <testcase name="the latest ten test suites should be display started with the eleven test suite" time="1.149" classname="Firefox 30.0.0 (Ubuntu).UnitCover build with eleven test suites"/>
    <testcase name="should redirect products.html to products.html#/builds" time="0.464" classname="Firefox 30.0.0 (Ubuntu).UnitCover"/>
    <system-out><![CDATA[
]]></system-out>
    <system-err/>
  </testsuite>
</testsuites>
```
* sbt unit report
```xml
<testsuite hostname="vagrant-VirtualBox" name="integration.ApplicationSpec" tests="7" errors="0" failures="0" skipped="0" time="1.975">
	<testcase name="application changed setup will work should::disable db ssl" classname="integration.ApplicationSpec" time="0.066">
		</testcase><testcase name="application changed setup will work should::enable DB logging" classname="integration.ApplicationSpec" time="0.039">
		</testcase><testcase name="application setup should should::configured with custom keystore is enabled" classname="integration.ApplicationSpec" time="0.046">
		</testcase><testcase name="application setup should should::check reditect to products.html work" classname="integration.ApplicationSpec" time="1.469">
		</testcase><testcase name="application setup should should::configured to redirect all http request to https on heroku" classname="integration.ApplicationSpec" time="0.222">
		</testcase><testcase name="application setup should should::configured with custom truststore is enabled" classname="integration.ApplicationSpec" time="0.046">
		</testcase><testcase name="application setup should should::configured with DB logging deactivate" classname="integration.ApplicationSpec" time="0.078">
	</testcase>
	<system-out></system-out>
	<system-err></system-err>
</testsuite>
```
* mocha test report with [xunit-file](https://github.com/peerigon/xunit-file)
```xml
<testsuite name="Mocha Tests" tests="13" failures="0" errors="0" skipped="0" timestamp="Tue, 08 Jul 2014 12:52:49 GMT" time="119.755">
<testcase classname="api discover" name="should return the json discover response" time="0.022"/>
<testcase classname="api build" name="should return 200 for /html/pussinboots/book" time="22.718"/>
<testcase classname="api build" name="should return 200 for /pdf/pussinboots/book" time="10.208"/>
<testcase classname="api build" name="should return 200 for /epub/pussinboots/book" time="2.904"/>
<testcase classname="api build" name="should return 200 for /mobi/pussinboots/book" time="25.602"/>
<testcase classname="api console" name="should return 200 for /html/pussinboots/book" time="15.293"/>
<testcase classname="api console" name="should return 200 for /pdf/pussinboots/book" time="8.577"/>
<testcase classname="api console" name="should return 200 for /epub/pussinboots/book" time="2.821"/>
<testcase classname="api console" name="should return 200 for /mobi/pussinboots/book" time="31.266"/>
<testcase classname="api content" name="should return an 404 for /html/pussinboots/book" time="0.026"/>
<testcase classname="api content" name="should return an 404 for /pdf/pussinboots/book" time="0.014"/>
<testcase classname="api content" name="should return an 404 for /epub/pussinboots/book" time="0.016"/>
<testcase classname="api content" name="should return an 404 for /mobi/pussinboots/book" time="0.012"/>
</testsuite>
```

##Done
* max build history is ten
* updated to play 2.3.0
* migrate from slick version 1 to 2
* buildnumber generation is missing always 1 (done)
* grouping of test suites to one build (need build number generation) (done)
* support badge images (done) like [![Unit Tests](https://unitcover.herokuapp.com/api/pussinboots/unitcover/badge?ts=10)](https://unitcover.herokuapp.com/#/builds/pussinboots/unitcover/builds)
* link test reports with travis build (done)
* badge for all testsuites (done) [![Unit Tests](http://unitcover.herokuapp.com/api/pussinboots/unitcover/testsuites/badge)](https://unitcover.herokuapp.com/#/builds/pussinboots/unitcover/builds)

##TODO
* authorization is complete missing
* nice and usable design (never done)
* sbt plugin to upload the unit reports (maybe the solution with curl seems very simple)

Transfer the todos to trello is still open

##Features
Todo and features are mantained with trello now [Trello](https://trello.com/b/tPkEhbaY/unitcover)


##Usage

###CI Integration

Example for [Travis Ci](https://travis-ci.org/)

Put that in the .travis.yml file and adapt the git owner, repository, trf and lrf parameter. 
```yaml
after_failure:
- wget -O - https://unitcover.herokuapp.com/scripts/upload.sh | bash /dev/stdin <owner> <repository> <strf> <lrf>
after_success:
- wget -O - https://unitcover.herokuapp.com/scripts/upload.sh | bash /dev/stdin <owner> <repository> <strf> <lrf>
```

Parameter | Description | Example Value 
--- | --- | ---
owner | git owner used for identification, has not to be a valid github owner only a string for identification | pussinboots
repository | git repository used for identification, has not to be a valid github repository only a string for identification vould also be used as project name| unitcover
strf | sbt unit test result folder or an empty string " | ./target/test-reports
lrf | list of report files (is optional than put an empty string "") | xunit.xml

Example for Scale Tests with SBT
```yaml
after_failure:
- wget -O - https://unitcover.herokuapp.com/scripts/upload.sh | bash /dev/stdin pussinboots unitcover ./target/test-reports/ ""
after_success:
wget -O - https://unitcover.herokuapp.com/scripts/upload.sh | bash /dev/stdin pussinboots unitcover ./target/test-reports/ ""
```

Example Integratgion
* [Mocha Tests](https://github.com/pussinboots/heroku-softcover/blob/master/.travis.yml )
* [SBT And Karma Tests](https://github.com/pussinboots/unitcover/blob/master/.travis.yml)

###Manual
Simple setup by download the provided upload script from unitcover and specify the 3 needed parameters.
* github owner
* github project
* target test folder

```bash
wget -O - https://unitcover.herokuapp.com/scripts/upload.sh | bash /dev/stdin pussinboots unitcover ./target/test-reports/
```

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
```

###Requirements
* play 2.3.0
* nodejs

###Local Server
```
sbt -Dconfig.file=conf/application-e2e.conf run
```
Than the [server](http://localhost:9000/#/builds/) should be accessible with some dummy data. It use an h2 in memory database.

###
