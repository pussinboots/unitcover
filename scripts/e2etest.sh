#!/bin/sh
scripts/bliss scripts/stop_play.sh
rm -rv public/js/bankapp/coverage
./scripts/npm/postinstall.sh
./node_modules/istanbul/lib/cli.js instrument public/js/bankapp/ -o public/js/bankapp/coverage
sbt stage
mkdir -p logs
target/universal/stage/bin/unitcover -Dconfig.resource=application-e2e.conf &>logs/application.log 
wget -O/dev/null --retry-connrefused --tries=8 http://localhost:9000/ 
scripts/karma.sh e2e 
scripts/stop_play.sh
#scripts/karma.sh e2elive
