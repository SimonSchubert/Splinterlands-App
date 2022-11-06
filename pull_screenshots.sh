#!/bin/bash
adb root
adb pull /data/data/com.example.splinterlandstest/files/ .
echo "Move files from device to project"
mv files/*.png .
echo "Move files to art folder"
rm files/*
rmdir files