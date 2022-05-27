#!/bin/sh

# change to directory of this script
cd $(dirname $0)
BRIDGEDBVERSION=3.0.14-SNAPSHOT
DERBYVERSION=10.15.2.0

CLASSPATH=org.bridgedb.server/target/bridgedbServer-${BRIDGEDBVERSION}-jar-with-dependencies.jar:derbyclient-${DERBYVERSION}.jar:derby-${DERBYVERSION}.jar

java -Xmx4096m -Xms4096m -cp ${CLASSPATH} -DstrictDataSourceChecking=false org.bridgedb.server.Server "$@"
