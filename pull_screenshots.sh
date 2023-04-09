#!/bin/bash
adb root
adb pull /data/data/com.splintergod.app/files/ .
echo "Resize image"
for file in files/*.png; do convert "$file" -resize 240 "$file"; done
echo "Move files from device to project"
mv files/*.png .
echo "Move files to art folder"
rm files/*
rmdir files