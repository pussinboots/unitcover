#!/bin/bash
FILES=./target/test-reports/*
#upload play junit reports
for f in $FILES
do
  echo "Processing $f file..."
  curl -H "Content-Type:application/xml" -X POST -d @$f http://unitcover.herokuapp.com/api/pussinboots/unitcover
done

curl -H "Content-Type:application/xml" -X POST -d @test-result.xml http://unitcover.herokuapp.com/api/unitcover/bankapp
