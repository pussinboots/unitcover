#!/bin/sh
scripts/bliss scripts/stop_play.sh
sbt -Dconfig.file=conf/application-e2e.conf start & 
wget -O/dev/null --retry-connrefused --tries=40 http://localhost:9000/products.html 
./node_modules/.bin/karma start karma.conf-e2e.js --single-run && scripts/stop_play.sh