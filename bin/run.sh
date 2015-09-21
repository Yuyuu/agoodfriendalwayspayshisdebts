#!/usr/bin/env bash

./gradlew stage
foreman start -e dev.env,dev_secret.env
