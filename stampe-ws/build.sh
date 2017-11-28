#!/bin/sh
GROUP="it.govpay"
NAME="stampe-ws"
VERSION="latest"
ID="$GROUP/$NAME:$VERSION"
echo building $ID
docker rmi $ID
docker build -t $ID .
