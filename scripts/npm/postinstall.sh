#!/bin/sh
./node_modules/bower/bin/bower install 
./node_modules/coffee-script/bin/coffee -c -o public/js/bankapp/coffee public/js/bankapp/coffee
./node_modules/jade/bin/jade.js -P public/*.jade --out public
./node_modules/jade/bin/jade.js -P public/partials/bankapp/*.jade --out public/partials/bankapp/