#!/bin/bash
FILES=./target/test-reports/*
#upload play junit reports
for f in $FILES
do
  echo "Processing $f file..."
  curl -H "Content-Type:application/xml" -X POST --data-binary @$f http://unitcover.heroku.com/api/pussinboots/bankapp
done

curl -H "Content-Type:application/xml" -X POST --data-binary @test-result.xml http://unitcover.heroku.com/api/pussinboots/bankapp