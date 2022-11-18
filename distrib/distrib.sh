#!/bin/bash

[ -f log4bash-core.sh ] && source log4bash-core.sh
PropertyConfigurator

#####################################
# Costanti #########################
#####################################
TRUNK_VERSION=3.3
TAG_PDD_VERSION_PRODUCT="3"
TAG_PDD_VERSION_MAJOR="3"
TAG_PDD_MAJOR_VERSION="${TAG_PDD_VERSION_PRODUCT}.${TAG_PDD_VERSION_MAJOR}"
TAG_PDD_MINOR_VERSION=9
TAG_PDD_PATCHLEVEL=p1
ramo=tags # tags / branches
GIT_URL=https://github.com/link-it/govway.git

#####################################
# Variabili per la forzatura #######
#####################################
UPDATE_DOC=true
UPDATE_LIB=true
REBUILD=true
READ_GIT_INFO=true

#####################################
# Variabili per lo skip ############
#####################################
SKIP_CHECKS=
SKIP_SRC_VERSION=

#####################################
# Input ############
#####################################
# Parametri per l'automazione tramite Jenkins. È possibile specificare il ramo
# e il path del clone locale da github
if [ $# -eq 0 ] ; then
    WORK_DIR="."
fi
if [ $# -eq 1 ] ; then
    ramo="$1"
fi
if [ $# -eq 2 ] ; then
    ramo="$1"
    WORK_DIR="$2"
fi
if [ $# -eq 3 ] ; then
    ramo="$1"
    WORK_DIR="$2"
    SKIP_CHECKS="$3"
fi
if [ $# -eq 4 ] ; then
    ramo="$1"
    WORK_DIR="$2"
    SKIP_CHECKS="$3"
    SKIP_SRC_VERSION="$4"
fi
if [ $# -eq 5 ] ; then
    ramo="$1"
    WORK_DIR="$2"
    SKIP_CHECKS="$3"
    SKIP_SRC_VERSION="$4"
    UPDATE_LIB="$5"
fi
if [ $# -eq 6 ] ; then
    ramo="$1"
    WORK_DIR="$2"
    SKIP_CHECKS="$3"
    SKIP_SRC_VERSION="$4"
    UPDATE_LIB="$5"
    LOG_DIR="$6"
fi
if [ $# -eq 7 ] ; then
    ramo="$1"
    WORK_DIR="$2"
    SKIP_CHECKS="$3"
    SKIP_SRC_VERSION="$4"
    UPDATE_LIB="$5"
    LOG_DIR="$6"
    UPDATE_DOC="$7"
fi
if [ $# -eq 8 ] ; then
    ramo="$1"
    WORK_DIR="$2"
    SKIP_CHECKS="$3"
    SKIP_SRC_VERSION="$4"
    UPDATE_LIB="$5"
    LOG_DIR="$6"
    UPDATE_DOC="$7"
    READ_GIT_INFO="$8"
fi

#####################################
# Variabili a Runtime ##############
#####################################
WORK_DIR=$(readlink -f $WORK_DIR)
if [ -z "${LOG_DIR}" ]
then
	LOG_DIR="$WORK_DIR/log"
else
	LOG_DIR=$(readlink -f $LOG_DIR)
fi
LOG_FILE="${LOG_DIR}/build_openspcoop2_distribs.log"

infoPrintln "---------------------------------------"
infoPrintln "WORK_DIR: ${WORK_DIR}"
infoPrintln "LOG_DIR: ${LOG_DIR}"
infoPrintln "TIPO: ${ramo}"
infoPrintln "BUILD-SETUP: ${REBUILD}"
infoPrintln "BUILD-DOC: ${UPDATE_DOC}"
infoPrintln "BUILD-LIB: ${UPDATE_LIB}"
infoPrintln "READ_GIT_INFO: ${READ_GIT_INFO}"

#####################################
# Versionamento dei sorgenti #######
#####################################
if [ -z "${TAG_PDD_MAJOR_VERSION}" ] 
then
	errorPrintln "Indicare la Major version di openspcoop nella variabile TAG_PDD_MAJOR_VERSION. (Es: 2.1) "
	exit 1
fi
GOVWAY_FULL_VERSION=${TAG_PDD_MAJOR_VERSION}
if [ -n "${TAG_PDD_MINOR_VERSION}" ]
then
	[ ${TAG_PDD_MINOR_VERSION:0:1} == "." ] && TAG_PDD_MINOR_VERSION=${TAG_PDD_MINOR_VERSION:1}
	GOVWAY_FULL_VERSION="${GOVWAY_FULL_VERSION}.${TAG_PDD_MINOR_VERSION}"
fi
if [ -n "${TAG_PDD_PATCHLEVEL}" ]
then
   #       [ ${TAG_PDD_PATCHLEVEL=:0:1} == "." ] && TAG_PDD_PATCHLEVEL=${TAG_PDD_PATCHLEVEL:1} # tenere il separatore nella variabile (puo anche non esserci)
       GOVWAY_FULL_VERSION="${GOVWAY_FULL_VERSION}.${TAG_PDD_PATCHLEVEL}"
fi
if [ $ramo == "tags" ]
then
	TAG_FULL_VERSION="${GOVWAY_FULL_VERSION}"

elif [ $ramo == "branches" ]
then
	# I sorgenti in sviluppo che stanno sul ramo "branch" hanno una struttura di directory 
	TAG_FULL_VERSION="${GOVWAY_FULL_VERSION}.build-master"
	if [ "${READ_GIT_INFO}" == "true" ]
	then
		GIT_VERSION=$(git log --pretty=oneline --abbrev-commit  | head -1 | cut -d ' ' -f 1)
		if [ ! -z "${GIT_VERSION}" ]
		then
			TAG_FULL_VERSION="${GOVWAY_FULL_VERSION}.build-${GIT_VERSION}"
		fi	
	fi
fi
infoPrintln "VERSION: ${TAG_FULL_VERSION}"
infoPrintln "---------------------------------------"


#####################################
# Riferimenti ai sorgenti ##########
#####################################
WORKING_COPY=${PWD}/../
DOC_WORKING_COPY="${WORKING_COPY}/resources/doc"
CHECKS_WORKING_COPY="${WORKING_COPY}/distrib/check"
OPENSPCOOP_SRC_FILE="govway-src-${TAG_FULL_VERSION}"
OPENSPCOOP_PDD_FILE="govway-installer-${TAG_FULL_VERSION}"
OPENSPCOOP_EXTERNAL_LIB_FILE="govway-external-lib-${TAG_FULL_VERSION}"

#1. Verifica prerequisiti (JAVA e ANT)
if [ $# -eq 0 ] ; then
    . prerequisiti
fi
mkdir -p ${LOG_DIR}
> ${LOG_FILE}

#2. Librerie 3Parti
if [ ! -d "${WORKING_COPY}/lib/ant" -o "$UPDATE_LIB" == "true" ] 
then
        infoPrintln "Checkout delle librerie ..."
	pushd ${WORKING_COPY} >> ${LOG_FILE} >> ${LOG_FILE} 2>&1
	mvn initialize
	popd >> ${LOG_FILE} 2>&1
	infoPrintln "Checkout delle librerie completato"
	infoPrintln "Eliminazione librerie deprecate (applicationServer) ..."
	rm -rf ${WORKING_COPY}/lib/applicationServer/wildfly8/
	rm -rf ${WORKING_COPY}/lib/applicationServer/wildfly9/
	rm -rf ${WORKING_COPY}/lib/applicationServer/wildfly10/
	rm -rf ${WORKING_COPY}/lib/applicationServer/wildfly11/
	rm -rf ${WORKING_COPY}/lib/applicationServer/wildfly12/
	rm -rf ${WORKING_COPY}/lib/applicationServer/wildfly13/
	rm -rf ${WORKING_COPY}/lib/applicationServer/wildfly14/
	rm -rf ${WORKING_COPY}/lib/applicationServer/wildfly15/
	rm -rf ${WORKING_COPY}/lib/applicationServer/wildfly16/
	rm -rf ${WORKING_COPY}/lib/applicationServer/wildfly17/
	infoPrintln "Eliminazione librerie deprecate (applicationServer) completato"
fi

#3 controlla i sorgenti (gpl/copyright/javadoc/stringBuffer)
if [ $# -eq 0 ] ; then
    if [ "$SKIP_CHECKS" == "true" ]
    then
	    warningPrintln "Verifiche (GPL/JavaDoc/StringBuffer) sui sorgenti non eseguite su richiesta utente."
    else
	    infoPrintln "Verifiche (GPL/JavaDoc/StringBuffer) sui sorgenti ..."
	    pushd ${WORKING_COPY} >> ${LOG_FILE} 2>&1
	    ERROROUTPUT=
	    for check in GPLCheck JavaDocCheck StringBufferCheck HashtableCheck VectorCheck NonShortCircuitLogicCheck #XSDCheck
	    do
		    [ $check == "XSDCheck" ] && PARAM=core/src/schemi/
		    [ $check == "JavaDocCheck" ] && PARAM=true
		    [ $check == "GPLCheck" ] && PARAM=
		    [ $check == "StringBufferCheck" ] && PARAM=
		    [ $check == "HashtableCheck" ] && PARAM=
		    [ $check == "VectorCheck" ] && PARAM=
		    [ $check == "NonShortCircuitLogicCheck" ] && PARAM=
		    debugPrintln "Esecuzione check $check con parametri [${WORKING_COPY} , $PARAM]"
		    OUTPUTCHECK=$(java -cp ${CHECKS_WORKING_COPY} $check ${WORKING_COPY} $PARAM 2>&1)
		    if [ $? -ne 0 ] 
		    then
			    ERROROUTPUT=true
			    echo "${OUTPUTCHECK}" > ${LOG_DIR}/esecuzione_$check.log
			    errorPrintln "Esecuzione del check $check in errore."
			    infoPrintln "Verificare l'output dell'esecuzione sul file ${LOG_DIR}/esecuzione_$check.log"
		    fi
	    done
	    popd >> ${LOG_FILE} 2>&1
	    infoPrintln "Verifiche (GPL/JavaDoc/StringBuffer) sui sorgenti terminata correttamente"
    fi
    [ -n "${ERROROUTPUT}" ] && exit 2
fi


######## Produzione distribuzione sorgente
if [ ! "${SKIP_SRC_VERSION}" == "true" ]
then
	infoPrintln "Generazione distribuzione sorgente ..."
	cd ${WORKING_COPY}
	sed -i -e "s#<module>git</module>#<!-- <module>git</module> -->#g" ${WORKING_COPY}/mvn/dependencies/pom.xml
	mv ${WORKING_COPY}/lib/git/openspcoop2_git-task-1.0.jar ${WORKING_COPY}/lib/git/openspcoop2_git-task-1.0.jar.rename
	mv ${WORKING_COPY}/lib/git/org.eclipse.jgit-5.0.1.201806211838-r.jar ${WORKING_COPY}/lib/git/org.eclipse.jgit-5.0.1.201806211838-r.jar.rename
	tar -h -c -z -f /tmp/${OPENSPCOOP_SRC_FILE}.tgz --xform="s@^@${OPENSPCOOP_SRC_FILE}/@" --exclude-vcs --exclude lib/svn * 
	mv ${WORKING_COPY}/lib/git/openspcoop2_git-task-1.0.jar.rename ${WORKING_COPY}/lib/git/openspcoop2_git-task-1.0.jar
	mv ${WORKING_COPY}/lib/git/org.eclipse.jgit-5.0.1.201806211838-r.jar.rename ${WORKING_COPY}/lib/git/org.eclipse.jgit-5.0.1.201806211838-r.jar
	mv /tmp/${OPENSPCOOP_SRC_FILE}.tgz ${WORK_DIR}/
	infoPrintln "Generazione distribuzione sorgente completata. Archivio generato: ${WORK_DIR}/${OPENSPCOOP_SRC_FILE}.tgz"
else
	warningPrintln "Generazione distribuzione sorgente non eseguita su richiesta utente."
fi

####### Produzione versione binaria

cd ${WORK_DIR}/

if [ -z "${ANT_OPTS}" ]
then
	export ANT_OPTS="-Xmx1024m -XX:MaxMetaspaceSize=1024m"
fi
echo "ANT_OPTS: ${ANT_OPTS}"

infoPrintln "Comincio produzione distribuzione binaria"
cd ${WORKING_COPY}/ant/setup
if [ ! -d deploy/sw/ -o "$REBUILD" == "true" ]
then

	if [ "${READ_GIT_INFO}" == "false" ]
	then
		debugPrintln "Disabilitazione jar per accesso informazioni git ..."
		mv ${WORKING_COPY}/lib/git/openspcoop2_git-task-1.0.jar ${WORKING_COPY}/lib/git/openspcoop2_git-task-1.0.jar.rename
		mv ${WORKING_COPY}/lib/git/org.eclipse.jgit-5.0.1.201806211838-r.jar ${WORKING_COPY}/lib/git/org.eclipse.jgit-5.0.1.201806211838-r.jar.rename
	fi

	infoPrintln "Generazione dei pacchetti software in corso (!!NOTA: questa operazione richiede parecchi minuti) ..."
	debugPrintln "E' possibile seguire il processo di generazione dei pacchetti tramite il file di log ${LOG_FILE}"
	#if [ $# -eq 0 ] ; then
	bash prepare-build.sh >> ${LOG_FILE} 2>&1
	#else
		#bash prepare-build.sh
	#fi    
	OPENSPCOOP2_PROTOCOL_JARS_COMPILATI=$(ls -1  deploy/sw/openspcoop2_*-protocol*.jar 2>/dev/null| grep -v "openspcoop2_as4-protocol_ecodexBackendStub_cxf.jar" | wc -l )
	OPENSPCOOP2_PROTOCOL_JARS_ATTESI=$(ls -1 ${WORKING_COPY}/protocolli 2> /dev/null |wc -l)
	if [ ${OPENSPCOOP2_PROTOCOL_JARS_COMPILATI:-0} -lt  ${OPENSPCOOP2_PROTOCOL_JARS_ATTESI} ]
	then
                errorPrintln "Generazione dei pacchetti software fallita."
                infoPrintln "Verificare l'output dell'esecuzione sul file ${LOG_FILE}"
		exit 1
	fi
        if [ ${OPENSPCOOP2_PROTOCOL_JARS_COMPILATI:-0} -gt  ${OPENSPCOOP2_PROTOCOL_JARS_ATTESI} ]
        then
                warningPrintln "Generazione dei pacchetti software ha generato un output differente da quello atteso. Verificare ${WORKING_COPY}/ant/setup/deploy/sw/"
		exit 2
        fi
	infoPrintln "Generazione dei pacchetti software terminata correttamente"

	if [ "${READ_GIT_INFO}" == "false" ]
	then
		debugPrintln "Ripristino jar per accesso informazioni git ..."
		mv ${WORKING_COPY}/lib/git/openspcoop2_git-task-1.0.jar.rename ${WORKING_COPY}/lib/git/openspcoop2_git-task-1.0.jar
		mv ${WORKING_COPY}/lib/git/org.eclipse.jgit-5.0.1.201806211838-r.jar.rename ${WORKING_COPY}/lib/git/org.eclipse.jgit-5.0.1.201806211838-r.jar
	fi
fi

mkdir -p ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}

/bin/cp -f ${WORKING_COPY}/COPYING ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}

infoPrintln "Generazione della documentazione (Questa operazione richiede qualche minuto) ..."

mkdir -p ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/doc

# ChangeLog
/bin/cp -f ${WORKING_COPY}/ChangeLog ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/doc
/bin/cp -f ${WORKING_COPY}/ChangeLog ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}

# License
/bin/cp -f ${WORKING_COPY}/LICENSE ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/doc
/bin/cp -f ${WORKING_COPY}/COPYING ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/doc
/bin/cp -rf ${WORKING_COPY}/third-party-licenses ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/doc

pushd ${WORKING_COPY}/resources/doc/ >> ${LOG_FILE} 2>&1
if [ ! -f pdf/GovWay-ManualeUtente.pdf -o "$UPDATE_DOC" == "true" ]
then
	debugPrintln "  Generazione Manuali ..."
	pushd src/manuali/ >> ${LOG_FILE} 2>&1
	make clean pdf >> ${LOG_FILE} 2>&1

	/bin/cp -f installazione/_build/pdf/*.pdf ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/doc/GovWay-ManualeInstallazione-${TAG_FULL_VERSION}.pdf
	if [ ! -e ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/doc/GovWay-ManualeInstallazione-${TAG_FULL_VERSION}.pdf ]
	then
		errorPrintln "Generazione della documentazione fallita: GovWay-ManualeInstallazione-${TAG_FULL_VERSION}.pdf non generato"
		exit 2
	fi

	/bin/cp -f console/_build/pdf/*.pdf ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/doc/GovWay-ManualeUtente-${TAG_FULL_VERSION}.pdf
	if [ ! -e ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/doc/GovWay-ManualeUtente-${TAG_FULL_VERSION}.pdf ]
	then
		errorPrintln "Generazione della documentazione fallita: GovWay-ManualeUtente-${TAG_FULL_VERSION}.pdf non generato"
		exit 2
	fi

	/bin/cp -f monitoraggio/_build/pdf/*.pdf ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/doc/GovWay-ManualeMonitoraggio-${TAG_FULL_VERSION}.pdf
	if [ ! -e ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/doc/GovWay-ManualeMonitoraggio-${TAG_FULL_VERSION}.pdf ]
	then
		errorPrintln "Generazione della documentazione fallita: GovWay-ManualeMonitoraggio-${TAG_FULL_VERSION}.pdf non generato"
		exit 2
	fi

	popd >> ${LOG_FILE} 2>&1
	debugPrintln "  Generazione Manuali completata con successo"
else
	/bin/cp -f pdf/GovWay-ManualeUtente.pdf ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/doc/GovWay-ManualeUtente-${TAG_FULL_VERSION}.pdf	
	/bin/cp -f pdf/GovWay-ManualeInstallazione.pdf ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/doc/GovWay-ManualeInstallazione-${TAG_FULL_VERSION}.pdf	
	/bin/cp -f pdf/GovWay-ManualeMonitoraggio.pdf ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/doc/GovWay-ManualeMonitoraggio-${TAG_FULL_VERSION}.pdf	
fi
popd >> ${LOG_FILE} 2>&1

pushd ${WORKING_COPY}/resources/doc/ >> ${LOG_FILE} 2>&1
if [ ! -f pdf/GovWay-ReleaseNotes.pdf -o "$UPDATE_DOC" == "true" ]
then
	debugPrintln "  Generazione Release Notes ..."
	pushd src/releaseNotes/ >> ${LOG_FILE} 2>&1
	make clean pdf >> ${LOG_FILE} 2>&1

	/bin/cp -f _build/pdf/*.pdf ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/doc/GovWay-ReleaseNotes-${TAG_FULL_VERSION}.pdf
	if [ ! -e ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/doc/GovWay-ReleaseNotes-${TAG_FULL_VERSION}.pdf ]
	then
		errorPrintln "Generazione della documentazione fallita: GovWay-ReleaseNotes-${TAG_FULL_VERSION}.pdf non generato"
		exit 2
	fi

	popd >> ${LOG_FILE} 2>&1
	debugPrintln "  Generazione Release Notes completata con successo"
else
	/bin/cp -f pdf/GovWay-ReleaseNotes.pdf ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/doc/GovWay-ReleaseNotes-${TAG_FULL_VERSION}.pdf
fi
popd >> ${LOG_FILE} 2>&1

/bin/cp -f ${WORKING_COPY}/distrib/README.txt ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/doc/
/bin/cp -f ${WORKING_COPY}/distrib/README.txt ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/
sed -i -e "s#@VERSIONE@#${TAG_FULL_VERSION}#g" ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/doc/README.txt
sed -i -e "s#@VERSIONE@#${TAG_FULL_VERSION}#g" ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/README.txt
DATA_README=$(date +"%d/%m/%y")
sed -i -e "s#@DATA@#${DATA_README}#g" ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/doc/README.txt
sed -i -e "s#@DATA@#${DATA_README}#g" ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/README.txt
infoPrintln "Generazione della documentazione terminata correttamente"


infoPrintln "Generazione pacchetto installer ..."
/bin/cp -f ${WORKING_COPY}/ant/setup/install.cmd ${WORKING_COPY}/ant/setup/install.sh ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/
touch ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/arch.dodeploy
sed -r -i -e 's#(.*)ROOT_OPENSPCOOP=.*#\1ROOT_OPENSPCOOP=.#g' \
 -e 's#/ant/setup#/installer/setup#g' -e 's#\\ant\\setup#\\installer\\setup#g'  \
 -e 's#\$\{ROOT_OPENSPCOOP\}/lib#\$\{ROOT_OPENSPCOOP\}/installer/lib#g'  -e 's#%ROOT_OPENSPCOOP%\\lib#%ROOT_OPENSPCOOP%\\installer\\lib#g' \
 ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/install.*

mkdir -p ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/installer/lib/

/bin/cp -rf ${WORKING_COPY}/lib/ant/ ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/installer/lib/

/bin/cp -rf ${WORKING_COPY}/lib/antinstaller/ ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/installer/lib/

mkdir -p ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/installer/lib/shared
/bin/cp -rf ${WORKING_COPY}/lib/shared/xercesImpl-2.12.2.jar ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/installer/lib/shared
/bin/cp -rf ${WORKING_COPY}/lib/shared/xml-apis-1.4.01.jar ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/installer/lib/shared

mkdir -p ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/installer/commons 
/bin/cp -f ${WORKING_COPY}/ant/commons/utils.xml ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/installer/commons

/bin/cp -rf ${WORKING_COPY}/ant/setup ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/installer/

/bin/rm -f ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/installer/setup/prepare*

/bin/rm -f ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/installer/setup/install.{cmd,sh}

/bin/rm -rf ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/installer/setup/ant/prepare

/bin/rm -rf ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/installer/setup/dist

sed -r -i -e 's# *<property +name="lib" +(value|location)=".*" */>#<property name="lib" \1="../lib" />#' \
 -e 's# *<property +name="dist" +(value|location)=".*" */>#<property name="dist" \1="../../dist" />#' \
 ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}/installer/setup/local_env.xml


cd ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}
tar -h -c -z -f ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}.tgz --xform="s@^@${OPENSPCOOP_PDD_FILE}/@" --exclude-vcs *
cd ${WORK_DIR}/ 
echo "/bin/rm -rf ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}"

infoPrintln "Generazione pacchetto installer terminata correttamente."

infoPrintln "Generazione distribuzione binaria terminata correttamente. Archivio generato: ${WORK_DIR}/${OPENSPCOOP_PDD_FILE}.tgz"

exit 0
