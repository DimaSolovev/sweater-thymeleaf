#!/usr/bin/env bash

mvn clean package

echo 'Copy files...'

scp -i ~/.ssh/id_rsa \
    target/sweater-0.0.1-SNAPSHOT.jar \
    soloveid@127.0.0.1:/home/soloveid/

echo 'Restart server...'

ssh -tt -i ~/.ssh/id_rsa soloveid@127.0.0.1 << EOF
pgrep java | xargs kill -9172.23.0.1
nohup java -jar sweater-0.0.1-SNAPSHOT.jar > log.txt &
EOF

echo 'Bye'