@echo off
REM #!\bin\sh

REM # The default case runs the installer in an X session if it can
REM # falling back to text if the Graphics environment is not available
REM # This can be changed by uncommenting the if statement and the default
REM # can be changed by replacing "text" with "swing" below
REM # or passing the required option on the command line

if  "%JAVA_HOME%" == "" goto nojava


set TOOL_CLASSPATH=lib
set TOOL_CONFIG=properties
set TOOL_JDBC=jdbc

set COMMAND="%JAVA_HOME%\bin\java"

%COMMAND% -cp "%TOOL_CLASSPATH%/*";"%TOOL_CONFIG%";"%TOOL_JDBC%/*" org.openspcoop2.pdd.template_scan.cli.TemplateScan %*


goto end

:nojava
echo "Il tool richiede Java Runtime Environment (JRE) 21"
echo Se la jvm e' gia installata provare a settare la variabile JAVA_HOME
goto end


:end
