@echo off
set OH_HOME=%~dps0
REM if java is not in the system path set JAVA_HOME variable
set JAVA_HOME=%OH_HOME%jvm\bin

for %%i in (java.exe) do set JAVA=%%~$PATH:i

IF NOT DEFINED JAVA (
	@echo Java not found
	REM EXIT /B
)

set OH_BIN=%OH_HOME%oh\bin
set OH_LIB=%OH_HOME%oh\lib
set OH_BUNDLE=%OH_HOME%oh\bundle
set OH_RPT=%OH_HOME%oh\rpt

set CLASSPATH=%OH_BIN%

SETLOCAL ENABLEDELAYEDEXPANSION

FOR %%A IN (%OH_LIB%\*.jar) DO (
	set CLASSPATH=!CLASSPATH!;%%A
)

set CLASSPATH=%CLASSPATH%;%OH_BUNDLE%
set CLASSPATH=%CLASSPATH%;%OH_RPT%
set CLASSPATH=%CLASSPATH%;%OH_BIN%;%OH_BIN%\OH.jar


cd /d %OH_HOME%\oh\

REM %JAVA% -showversion -Djava.library.path=%OH_LIB%\native\Windows -classpath %CLASSPATH% org.isf.menu.gui.Menu
@echo on
%JAVA_HOME%\java -Xms128m -Xmx512m -showversion -Djava.library.path=%OH_LIB%\native\Windows -classpath %CLASSPATH% org.isf.menu.gui.Menu
exit

