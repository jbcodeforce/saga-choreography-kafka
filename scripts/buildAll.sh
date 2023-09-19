#!/bin/bash

SCRIPT_DIR=$(pwd)

if [[ $# -eq 1 ]]
then
  TAG=$1
else
  TAG=latest
fi

echo "@@@@ Build Order image"
cd $SCRIPT_DIR/order-mg-ms/
./scripts/buildAll.sh $tag

echo "\n\n@@@@ Build Reefer image"
cd $SCRIPT_DIR/reefer-mg-ms/
./scripts/buildAll.sh $tag

echo "\n\n@@@@ Build Vessel image"
cd $SCRIPT_DIR/vessel-mg-ms/
./scripts/buildAll.sh $tag

cd $SCRIPT_DIR