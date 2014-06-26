#!/bin/sh
rm -rv public/js/bankapp/coverage
./node_modules/istanbul/lib/cli.js instrument public/js/bankapp/ -o public/js/bankapp/coverage
wget -O/dev/null --retry-connrefused --tries=4 http://localhost:9000/ 
./node_modules/.bin/karma start karma.conf-e2e.js --single-run
