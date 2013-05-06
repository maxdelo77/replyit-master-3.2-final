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


# Startup script for jBilling under Cruise Control. This script sets the
# server port on the running jBilling instance so that multiple jBilling 
# projects can be tested under the same Cruise Control build loop.
#
# see also 'cc-build.properties'


# load properties if file exists, otherwise use port 8080
if [ -f cc-build.properties ]; then
    . cc-build.properties
else
    server_port=8080
fi

# grails runtime options
export GRAILS_OPTS="-server -Xmx1024M -Xms256M -XX:MaxPermSize=256m"

# start jbilling and record process id
nohup $GRAILS_HOME/bin/grails -Ddisable.auto.recompile=true -Dserver.port=${server_port} run-app &
echo $!> jbilling.pid

echo "Started jBilling on port ${server_port}."

exit 0;
