@echo off
REM #!\bin\sh

REM # The default case runs the installer in an X session if it can
REM # falling back to text if the Graphics environment is not available
REM # This can be changed by uncommenting the if statement and the default
REM # can be changed by replacing "text" with "swing" below
REM # or passing the required option on the command line

if  "%JAVA_HOME%" == "" goto nojava

set INTERFACE=default
set COMMAND="%JAVA_HOME%\bin\java"
if "%1" == "text" goto settext
if "%1" == "swing" goto setswing
goto install

:settext
set COMMAND="%JAVA_HOME%\bin\java"
set INTERFACE=text
goto install

:setswing
set COMMAND="%JAVA_HOME%\bin\javaw"
set INTERFACE=swing
goto install

:install

set ROOT_OPENSPCOOP=..\..
set OUTPUTDIR=..\..
set LIBRARIES=%ROOT_OPENSPCOOP%\lib
set ANTINSTALLER_LIBRARIES=%LIBRARIES%\antinstaller

REM # Installer from command line classpath
set CLASSPATH=%LIBRARIES%\shared\xercesImpl-2.11.0.jar
set CLASSPATH=%LIBRARIES%\shared\xml-apis-2.11.0.jar
set CLASSPATH=%CLASSPATH%;%ANTINSTALLER_LIBRARIES%\xml-apis_antinstaller0.8b.jar
set CLASSPATH=%CLASSPATH%;%ANTINSTALLER_LIBRARIES%\ant-installer-0.8b.jar
set CLASSPATH=%CLASSPATH%;%ANTINSTALLER_LIBRARIES%\ai-icons-eclipse_antinstaller0.8b.jar

REM # JGoodies Look And Feel
set CLASSPATH=%CLASSPATH%;%ANTINSTALLER_LIBRARIES%\jgoodies-edited-1_2_2.jar

REM # minimal ANT classpath requirements
set CLASSPATH=%CLASSPATH%;%ANTINSTALLER_LIBRARIES%\ant-1.7.0.jar
set CLASSPATH=%CLASSPATH%;%ANTINSTALLER_LIBRARIES%\ant-launcher-1.7.0.jar
set CLASSPATH=%CLASSPATH%;%ROOT_OPENSPCOOP%\ant\setup\deploy\resources

REM # minimal regular expression env
set CLASSPATH=%CLASSPATH%;%ANTINSTALLER_LIBRARIES%\ant-apache-regexp-1.7.0.jar
set CLASSPATH=%CLASSPATH%;%ANTINSTALLER_LIBRARIES%\jakarta-regexp-1.5.jar

REM # unix tasks
set CLASSPATH=%CLASSPATH%;%ANTINSTALLER_LIBRARIES%\ant-nodeps-1.7.0.jar

%COMMAND% -classpath %CLASSPATH%  org.tp23.antinstaller.runtime.ExecInstall %INTERFACE% %ROOT_OPENSPCOOP%\ant\setup

goto end

:nojava
echo L'installazione richiede l'installazione di java.
echo Se la jvm e' gia installata provare a settare la variabile JAVA_HOME
goto end


:end
