#!/usr/bin/env bash

./gradlew stage
java -DPROD_MODE=$PROD_MODE \
    -cp ./build/deps/*:./build/libs/* \
    agoodfriendalwayspayshisdebts.Main