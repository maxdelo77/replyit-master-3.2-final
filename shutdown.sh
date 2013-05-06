#!/bin/bash
#
# JBILLING CONFIDENTIAL
# _____________________
#
# [2003] - [2012] Enterprise jBilling Software Ltd.
# All Rights Reserved.
#
# NOTICE:  All information contained herein is, and remains
# the property of Enterprise jBilling Software.
# The intellectual and technical concepts contained
# herein are proprietary to Enterprise jBilling Software
# and are protected by trade secret or copyright law.
# Dissemination of this information or reproduction of this material
# is strictly forbidden.


# Shutdown script for jBilling under Cruise Control. This script kills the
# jBilling instance identified by the process ID from the 'jbilling.pid' file
# created by the startup script.


PID_FILE=jbilling.pid

# get the PID output from the startup script
if [ -f $PID_FILE ]; then
    JBILLING_PID=`cat $PID_FILE`
    echo "Shutting down jBilling PID $JBILLING_PID"
else 
	echo "$PID_FILE not found, jBilling is not running."
	exit 0;
fi

# kill the process if it's running
if [ -n "$JBILLING_PID" ] && ps -p ${JBILLING_PID} > /dev/null ; then
    kill -9 ${JBILLING_PID}
else
    echo "jBilling is not running."
fi
                
# remove the pid file
if [ -f $PID_FILE ]; then
    rm $PID_FILE
fi

exit 0;
