#!/bin/sh

PORT=8088
JAR_NAME=backend-1-0.0.1-SNAPSHOT.jar


echo " ??   [$PORT] �� ��Ʈ�� ����ϴ� ���ø����̼��� ã���ϴ�...\n"

PID=$(lsof -i :$PORT -t)

if [ -z $PID ]; then
	echo " ??   �������� ���ø����̼��� ��� ��ٷ� �����մϴ�.\n"

else
	echo " ?   �������� ���ø����̼��� �־ �̸� �����մϴ�. [PID = $PID]\n"
	kill -15 $PID
fi

echo " ??   ���ø����̼��� �����ϴµ� �ʿ��� ȯ�溯���� �����մϴ�.\n"

export FIRST_MALLANG_ENV="oieho"
export SECOND_MALLANG_ENV=1234
export SPRING_PROFILES_ACTIVE="dev"
export RDS_DRIVER="org.mariadb.jdbc.Driver"
export RDS_URL="jdbc:mariadb://svc.sel3.cloudtype.app:31795/oieho"
export RDS_USERNAME="root"
export RDS_PASSWORD="RooT3%3"

echo " ??   ���ø����̼� �����մϴ�~ ??\n"

nohup java -jar $JAR_NAME 1>> build.log 2>> build_error.log &