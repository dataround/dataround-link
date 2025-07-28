#!/usr/bin/env bash

pid=`ps aux |grep "dataround-link" |grep -v grep |awk '{print $2}'`
kill $pid
echo "dataround-link(pid $pid) shutdown now"