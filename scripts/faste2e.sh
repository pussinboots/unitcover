#!/bin/sh
./node_modules/istanbul/lib/cli.js instrument public/js/bankapp/ -o public/js/bankapp/coverage
wget -O/dev/null --retry-connrefused --tries=2 http://localhost:9000/ 
./node_modules/.bin/karma start karma.conf-dev.js
