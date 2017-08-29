#!/bin/bash

tmux split-window -h "~/emma/emma-manager/etc/start-redis.sh"
tmux split-window -h "java -jar ~/emma/emma-manager/target/emma-manager-0.0.1-SNAPSHOT.jar"

tmux select-pane -t 0
tmux select-layout even-vertical > /dev/null

while ! nc localhost 50042; do
	sleep 1
	continue
done
