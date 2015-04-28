#!/bin/bash

set +e

echo y | android update sdk --no-ui --filter extra-android-m2repository > /dev/null
echo y | android update sdk --no-ui --filter extra-google-m2repository > /dev/null
echo y | android update sdk --no-ui --filter android-18 > /dev/null
echo y | android update sdk --no-ui --filter sys-img-armeabi-v7a-android-18 --all > /dev/null
