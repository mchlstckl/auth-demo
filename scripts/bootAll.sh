#!/usr/bin/env bash

cd ..

./gradlew :authorization-service:bootRun &
./gradlew :resource-service:bootRun &
./gradlew :relying-party:bootRun &

read -p "Press enter key to stop..."

pgrep -f relying-party | xargs kill -9
pgrep -f resource-service | xargs kill -9
pgrep -f authorization-service | xargs kill -9