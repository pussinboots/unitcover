#!/bin/sh
PID_FILE=target/universal/stage/RUNNING_PID
if [ -f "$PID_FILE" ] 
then
	echo "pid file $PID_FILE found stop running play server"
	cat $PID_FILE | xargs kill
	rm -f $PID_FILE
else
	echo "no pid file $PID_FILE found"
fi