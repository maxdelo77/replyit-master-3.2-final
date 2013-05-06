rem
rem  JBILLING CONFIDENTIAL
rem  _____________________
rem
rem  [2003] - [2012] Enterprise jBilling Software Ltd.
rem  All Rights Reserved.
rem
rem  NOTICE:  All information contained herein is, and remains
rem  the property of Enterprise jBilling Software.
rem  The intellectual and technical concepts contained
rem  herein are proprietary to Enterprise jBilling Software
rem  and are protected by trade secret or copyright law.
rem  Dissemination of this information or reproduction of this material
rem  is strictly forbidden.

set JAVA_OPTS=%JAVA_OPTS% -Xms256m -Xmx512m -XX:PermSize=512m -XX:MaxPermSize=512m
set GRAILS_OPTS=-server -Xmx1024M -Xms256M -XX:PermSize=512m -XX:MaxPermSize=512m

grails -Ddisable.auto.recompile=true run-app
