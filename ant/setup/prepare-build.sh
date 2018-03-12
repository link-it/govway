#AS_VERSION=jboss4,jboss5,jboss6,jboss7,wildfly8,wildfly9,wildfly10,tomcat6
# Gli application server jboss4, jboss5 e jboss6 sono stati deprecati dalla versione 2.3
# L'application server jboss7 è stato deprecato dalla versione 3.0 per motivi di jdk minimo (v8) non supportato da jboss7
# L'application server tomcat6, wildfly8 e wildfly9 sono stati deprecati dalla versione 3.0 per motivi di test non più verificati su tali versioni
AS_VERSION=wildfly10,wildfly11,tomcat7,tomcat8,tomcat9
# Per tomcat qualsiasi versione venga generata è uguale (a meno del datasource).
# L'importante che anche il build.xml del setup associ poi lo stesso valore per qualsiasi scelta di tomcat venga fatta

ROOT=../../
CORE=${ROOT}/core
WEB_LIB=${ROOT}/tools/web_interfaces/lib
PDD_CONSOLE=${ROOT}/tools/web_interfaces/control_station
LOADER_CONSOLE=${ROOT}/tools/web_interfaces/loader
TRASPARENTE_PROTOCOL=${ROOT}/protocolli/trasparente
SPCOOP_PROTOCOL=${ROOT}/protocolli/spcoop
AS4_PROTOCOL=${ROOT}/protocolli/as4
SDI_PROTOCOL=${ROOT}/protocolli/sdi
SPCOOP_BACKWARD_COMPATIBILITY=${ROOT}/protocolli/spcoop/tools/backward_compatibility

# Core
if [ ! -f ${CORE}/local_env.xml  ]
then
	cp ${CORE}/local_env.xml.template ${CORE}/local_env.xml
	perl -pi -e "s#jboss/wildfly/tomcat#jboss7#g" ${CORE}/local_env.xml;
fi

# WebLib-MVC
if [ ! -f ${WEB_LIB}/mvc/local_env.xml  ]
then
        cp ${WEB_LIB}/mvc/local_env.xml.template ${WEB_LIB}/mvc/local_env.xml
fi

# WebLib-QUEUE
if [ ! -f ${WEB_LIB}/queue/local_env.xml  ]
then
        cp ${WEB_LIB}/queue/local_env.xml.template ${WEB_LIB}/queue/local_env.xml
fi

# WebLib-USERS
if [ ! -f ${WEB_LIB}/users/local_env.xml  ]
then
        cp ${WEB_LIB}/users/local_env.xml.template ${WEB_LIB}/users/local_env.xml
fi

# WebLib-AUDIT
if [ ! -f ${WEB_LIB}/audit/local_env.xml  ]
then
        cp ${WEB_LIB}/audit/local_env.xml.template ${WEB_LIB}/audit/local_env.xml
fi

# Web PddConsole
if [ ! -f ${PDD_CONSOLE}/local_env.xml  ]
then
        cp ${PDD_CONSOLE}/local_env.xml.template ${PDD_CONSOLE}/local_env.xml
fi

# Web LoaderConsole
if [ ! -f ${LOADER_CONSOLE}/local_env.xml  ]
then
        cp ${LOADER_CONSOLE}/local_env.xml.template ${LOADER_CONSOLE}/local_env.xml
fi

# Protocollo Trasparente
if [ ! -f ${TRASPARENTE_PROTOCOL}/local_env.xml  ]
then
        cp ${TRASPARENTE_PROTOCOL}/local_env.xml.template ${TRASPARENTE_PROTOCOL}/local_env.xml
fi

# Protocollo SPCoop
if [ ! -f ${SPCOOP_PROTOCOL}/local_env.xml  ]
then
        cp ${SPCOOP_PROTOCOL}/local_env.xml.template ${SPCOOP_PROTOCOL}/local_env.xml
fi

# Protocollo AS4
if [ ! -f ${AS4_PROTOCOL}/local_env.xml  ]
then
        cp ${AS4_PROTOCOL}/local_env.xml.template ${AS4_PROTOCOL}/local_env.xml
fi

# Protocollo SDI
if [ ! -f ${SDI_PROTOCOL}/local_env.xml  ]
then
        cp ${SDI_PROTOCOL}/local_env.xml.template ${SDI_PROTOCOL}/local_env.xml
fi

# Modulo di BackwardCompatibility SPCoop
if [ ! -f ${SPCOOP_BACKWARD_COMPATIBILITY}/local_env.xml  ]
then
        cp ${SPCOOP_BACKWARD_COMPATIBILITY}/local_env.xml.template ${SPCOOP_BACKWARD_COMPATIBILITY}/local_env.xml
fi


ant -buildfile prepare-build.xml -Dapplication_server_version=${AS_VERSION}

