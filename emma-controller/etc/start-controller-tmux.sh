#!/bin/bash

tmux split-window -h "~/emma/emma-controller/etc/start-redis.sh"
tmux split-window -h "java -jar ~/emma/emma-controller/target/emma-controller-0.0.1-SNAPSHOT.jar"

tmux select-pane -t 0
tmux select-layout even-vertical > /dev/null

while ! nc localhost 50042; do
	sleep 1
	continue
done
