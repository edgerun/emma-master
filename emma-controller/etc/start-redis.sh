#!/bin/bash

redis-server --protected-mode no &
sleep 1
redis-cli config set notify-keyspace-events KEA

wait
