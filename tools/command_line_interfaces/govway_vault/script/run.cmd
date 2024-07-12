@echo off
REM #!\bin\sh

REM # The default case runs the installer in an X session if it can
REM # falling back to text if the Graphics environment is not available
REM # This can be changed by uncommenting the if statement and the default
REM # can be changed by replacing "text" with "swing" below
REM # or passing the required option on the command line

if  "%JAVA_HOME%" == "" goto nojava


set VAULT_CLASSPATH=lib
set VAULT_CONFIG=properties
set VAULT_JDBC=jdbc

set COMMAND="%JAVA_HOME%\bin\java"

%COMMAND% -cp "%VAULT_CLASSPATH%/*";"%VAULT_CONFIG%";"%VAULT_JDBC%/*" org.openspcoop2.pdd.config.vault.cli.VaultTools TIPO_OPERAZIONE %*


goto end

:nojava
echo "Il Batch richiede Java Runtime Environment (JRE) 11 (https://jdk.java.net/archive/)"
echo Se la jvm e' gia installata provare a settare la variabile JAVA_HOME
goto end


:end
