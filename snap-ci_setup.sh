#!/bin/bash

set +e

download-android
echo y | android update sdk --no-ui --filter android-18 > /dev/null
echo y | android update sdk --no-ui --filter sys-img-armeabi-v7a-android-18 --all > /dev/null

echo no | android create avd --force -n test -t android-18 --abi armeabi-v7a
emulator -avd test -no-skin -no-audio -no-window &


# Originally written by Ralf Kistner <ralf@embarkmobile.com>, but placed in the public domain

bootanim=""
failcounter=0
until [[ "$bootanim" =~ "stopped" ]]; do
   bootanim=`adb -e shell getprop init.svc.bootanim 2>&1`
   echo "$bootanim"
   if [[ "$bootanim" =~ "not found" ]]; then
      let "failcounter += 1"
      if [[ $failcounter -gt 42 ]]; then
        echo "Failed to start emulator"
        exit 1
      fi
   fi
   sleep 1
done
echo "Emulator available"

adb shell input keyevent 82 &