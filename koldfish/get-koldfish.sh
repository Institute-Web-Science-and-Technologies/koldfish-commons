#! /usr/bin/env sh
mvn=mvn
mvn_parameters=-q
dependency_get=org.apache.maven.plugins:maven-dependency-plugin:2.1:get
koldfish_groupId=de.unikoblenz.west.koldfish
koldfish_artifactId=koldfish
koldfish_version=0.0.1
repo_uri=https://maven.west.uni-koblenz.de:8443/repository/koral

${mvn} ${mvn_parameters} ${dependency_get} -Dartifact=${koldfish_groupId}:${koldfish_artifactId}:${koldfish_version}:pom  -DrepoUrl=${repo_uri}
