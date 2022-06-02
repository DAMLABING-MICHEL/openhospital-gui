#!/bin/bash

MYSQL_DIR="mysql-5.0.51a-linux-i686"
JAVA_DIR="jdk1.6.0_04"
OH_DIR="oh-1.7.0"

where_i_am=$(pwd)
cd $(dirname $0)
POH_PATH=$(pwd)
cd $where_i_am

# Find a free port to run MySQL starting from the default port.
#mysql_port=3306
#while [ $(netstat -tna | grep -e '^tcp' | awk '{ print $4 }' | grep ":$mysql_port") ]; do
#	mysql_port=$(expr $mysql_port + 1)
#done
#
#POH_PATH_ESCAPED=$(echo $POH_PATH | sed -e 's/\//\\\//g')
#sed -e "s/OH_PATH_SUBSTITUTE/$POH_PATH_ESCAPED/g" -e "s/MYSQL_PORT/$mysql_port/" $POH_PATH/etc/mysql/my.ori > $POH_PATH/etc/mysql/my.cnf
#sed -e "s/MYSQL_PORT/$mysql_port/" $POH_PATH/$OH_DIR/rsc/database.properties.ori > $POH_PATH/$OH_DIR/rsc/database.properties
#sed -e "s/MYSQL_PORT/$mysql_port/" $POH_PATH/$OH_DIR/rsc/log4j.properties.ori > $POH_PATH/$OH_DIR/rsc/log4j.properties
#
#echo "Starting MySQL... "
#cd $POH_PATH/$MYSQL_DIR/
#./bin/mysqld_safe --defaults-file=$POH_PATH/etc/mysql/my.cnf 2>&1 > /dev/null &

echo "Starting Open Hospital... "
######## OPENHOSPITAL Configuration:

# add the libraries to the OPENHOSPITAL_CLASSPATH.
# EXEDIR is the directory where this executable is.
cd $OH_DIR
EXEDIR=${0%/*}

DIRLIBS=${EXEDIR}/bin/*.jar
for i in ${DIRLIBS}
do
  if [ -z "$OPENHOSPITAL_CLASSPATH" ] ; then
    OPENHOSPITAL_CLASSPATH=$i
  else
    OPENHOSPITAL_CLASSPATH="$i":$OPENHOSPITAL_CLASSPATH
  fi
done


DIRLIBS=${EXEDIR}/lib/*.jar
for i in ${DIRLIBS}
do
  if [ -z "$OPENHOSPITAL_CLASSPATH" ] ; then
    OPENHOSPITAL_CLASSPATH=$i
  else
    OPENHOSPITAL_CLASSPATH="$i":$OPENHOSPITAL_CLASSPATH
  fi
done

OPENHOSPITAL_CLASSPATH="bundle/":$OPENHOSPITAL_CLASSPATH
OPENHOSPITAL_CLASSPATH="rpt/":$OPENHOSPITAL_CLASSPATH

ARCH=$(uname -m)
case $ARCH in
	x86_64|amd64|AMD64)
		NATIVE_LIB_PATH=$POH_PATH/$OH_DIR/lib/native/Linux/amd64
		;;
	i[3456789]86|x86|i86pc)
		NATIVE_LIB_PATH=$POH_PATH/$OH_DIR/lib/native/Linux/i386
		;;
	*)
		echo "Unknown architecture $(uname -m)"
		;;
esac
cd $POH_PATH/$OH_DIR/
#$POH_PATH/jdk1.6.0_04/bin/java -showversion -Djava.library.path=${NATIVE_LIB_PATH} -classpath ${OPENHOSPITAL_CLASSPATH} org.isf.menu.gui.Menu
java -Xms128m -Xmx512m -showversion -Djava.library.path=${NATIVE_LIB_PATH} -classpath ${OPENHOSPITAL_CLASSPATH} org.isf.menu.gui.Menu

#echo "Shutting down MySQL... "
#cd $POH_PATH/$MYSQL_DIR/
#./bin/mysqladmin --host=127.0.0.1 --port=$mysql_port --user=root shutdown 2>&1 > /dev/null
#rm -f $POH_PATH/etc/mysql/my.cnf
#rm -f $POH_PATH/$OH_DIR/rsc/database.properties
#rm -f $POH_PATH/$OH_DIR/rsc/log4j.properties

exit 0
