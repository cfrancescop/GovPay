#!/bin/sh
GROUP="it.govpay"
NAME="stampe-ws"
ID="$GROUP/$NAME"
echo building $ID
docker rmi $ID
docker build -t $ID .
