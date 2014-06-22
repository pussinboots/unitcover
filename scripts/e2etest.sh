#!/bin/sh
scripts/bliss scripts/stop_play.sh
sbt stage
mkdir -p logs
target/universal/stage/bin/unitcover -Dconfig.resource=application-e2e.conf &>logs/application.log 
wget -O/dev/null --retry-connrefused --tries=15 http://localhost:9000/products.html 
./node_modules/.bin/karma start karma.conf-e2e.js --single-run && scripts/stop_play.sh
