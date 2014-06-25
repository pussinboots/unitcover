#!/bin/sh
wget -O/dev/null --retry-connrefused --tries=2 http://localhost:9000/ 
./node_modules/istanbul/lib/cli.js instrument public/js/bankapp/ -o public/js/bankapp/coverage
./node_modules/.bin/karma start karma.conf-e2e.js --single-run && scripts/stop_play.sh
