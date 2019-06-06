#!/usr/bin/env bash
../apache-zookeeper-3.5.5-bin/bin/zkServer.sh stop ../apache-zookeeper-3.5.5-bin/conf/zoo1.cfg
../apache-zookeeper-3.5.5-bin/bin/zkServer.sh stop ../apache-zookeeper-3.5.5-bin/conf/zoo2.cfg
../apache-zookeeper-3.5.5-bin/bin/zkServer.sh stop ../apache-zookeeper-3.5.5-bin/conf/zoo3.cfg