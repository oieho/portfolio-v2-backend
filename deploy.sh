#!/bin/sh

PORT=8088
JAR_NAME=backend-1-0.0.1-SNAPSHOT.jar


echo " ??   [$PORT] 번 포트를 사용하는 애플리케이션을 찾습니다...\n"

PID=$(lsof -i :$PORT -t)

if [ -z $PID ]; then
	echo " ??   실행중인 애플리케이션이 없어서 곧바로 실행합니다.\n"

else
	echo " ?   실행중인 애플리케이션이 있어서 이를 종료합니다. [PID = $PID]\n"
	kill -15 $PID
fi

echo " ??   애플리케이션을 실행하는데 필요한 환경변수를 세팅합니다.\n"

export FIRST_MALLANG_ENV="oieho"
export SECOND_MALLANG_ENV=1234
export SPRING_PROFILES_ACTIVE="dev"
export RDS_DRIVER="org.mariadb.jdbc.Driver"
export RDS_URL="jdbc:mariadb://svc.sel3.cloudtype.app:31795/oieho"
export RDS_USERNAME="root"
export RDS_PASSWORD="RooT3%3"

echo " ??   애플리케이션 실행합니다~ ??\n"

nohup java -jar $JAR_NAME 1>> build.log 2>> build_error.log &