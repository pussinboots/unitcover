#!/bin/sh
scripts/bliss scripts/stop_play.sh
./node_modules/istanbul/lib/cli.js instrument public/js/bankapp/ -o public/js/bankapp/coverage
./node_modules/coffee-script/bin/coffee -c -o public/js/bankapp/coffee public/js/bankapp/coffee
sbt stage
mkdir -p logs
target/universal/stage/bin/unitcover -Dconfig.resource=application-e2e.conf &>logs/application.log 
wget -O/dev/null --retry-connrefused --tries=8 http://localhost:9000/ 
./node_modules/.bin/karma start karma.conf-e2e.js --single-run && scripts/stop_play.sh
