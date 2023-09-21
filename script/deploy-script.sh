#!/bin/sh

# 이 스크립트의 현재 디렉토리를 스크립트가 있는 디렉토리로 변경합니다.
cd "$(dirname "$0")"

PORT=8088
JAR_NAME=/전체/경로/backend-1-0.0.1-SNAPSHOT.jar

echo " ??   [$PORT] 번 포트를 사용하는 애플리케이션을 찾습니다...\n"

PID=$(lsof -i :$PORT -t)

if [ -z $PID ]; then
    echo " ??   실행중인 애플리케이션이 없어서 곧바로 실행합니다.\n"
else
    echo " ?   실행중인 애플리케이션이 있어서 이를 종료합니다. [PID = $PID]\n"
    kill -15 $PID
fi

echo " ??   애플리케이션을 실행하는데 필요한 환경변수를 세팅합니다.\n"

echo " ??   애플리케이션 실행합니다~ ??\n"

nohup java -jar $JAR_NAME 1>> build.log 2>> build_error.log &
