#!/bin/bash

WORKSPACE_DIR=$(pwd)
RELEASE_DIR=$(mktemp -d)

VERSION=$(cat ${WORKSPACE_DIR}/build.gradle | grep "version '" | awk '{print $2}' | tr -d \')

mkdir ${RELEASE_DIR}/WorkLog
cp ${WORKSPACE_DIR}/build/libs/WorkLog-*-all.jar ${RELEASE_DIR}/WorkLog/WorkLog.jar
cp ${WORKSPACE_DIR}/ReleaseFiles/* ${RELEASE_DIR}

cd ${RELEASE_DIR}
zip -r ${WORKSPACE_DIR}/build/WorkLog-${VERSION}-${BUILD_NUMBER}.zip .

cd ${WORKSPACE_DIR}



