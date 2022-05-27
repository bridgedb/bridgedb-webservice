#!/bin/sh

# change to directory of this script
cd $(dirname $0)

JAR=org.bridgedb.server/target/bridgedbServer.jar
java -Xmx4096m -Xms4096m -DstrictDataSourceChecking=false -jar ${JAR} "$@"
