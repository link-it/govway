AS_VERSION="$1"
DB_VERSION="$2"

if [ -z "${AS_VERSION}" -o -z "${DB_VERSION}" ]
then
	source prepare-build.properties
fi

echo "Application Server supportati: ${AS_VERSION}"
echo ""
echo "Database supportati: ${DB_VERSION}"
echo ""

ROOT=../../
CORE=${ROOT}/core
WEB_LIB=${ROOT}/tools/web_interfaces/lib
PDD_CONSOLE=${ROOT}/tools/web_interfaces/control_station
PDD_MONITOR=${ROOT}/tools/web_interfaces/monitor
LOADER_CONSOLE=${ROOT}/tools/web_interfaces/loader
TRASPARENTE_PROTOCOL=${ROOT}/protocolli/trasparente
SPCOOP_PROTOCOL=${ROOT}/protocolli/spcoop
AS4_PROTOCOL=${ROOT}/protocolli/as4
SDI_PROTOCOL=${ROOT}/protocolli/sdi
SPCOOP_BACKWARD_COMPATIBILITY=${ROOT}/protocolli/spcoop/tools/backward_compatibility
RS_CONFIG=${ROOT}/tools/rs/config/server
RS_MONITOR=${ROOT}/tools/rs/monitor/server

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

# Web PddMonitor
if [ ! -f ${PDD_MONITOR}/local_env.xml  ]
then
	cp ${PDD_MONITOR}/local_env.xml.template ${PDD_MONITOR}/local_env.xml
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

# RS Api Config
if [ ! -f ${RS_CONFIG}/local_env.xml  ]
then
        cp ${RS_CONFIG}/local_env.xml.template ${RS_CONFIG}/local_env.xml
fi

# RS Api Monitor
if [ ! -f ${RS_MONITOR}/local_env.xml  ]
then
        cp ${RS_MONITOR}/local_env.xml.template ${RS_MONITOR}/local_env.xml
fi


ant -buildfile prepare-build.xml -Dapplication_server_version=${AS_VERSION} -Ddatabase_version=${DB_VERSION}

