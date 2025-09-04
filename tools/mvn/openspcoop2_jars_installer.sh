# Installer dei JAR openspcoop nel repository Link.it

MVN_DEPLOY_HOSTNAME="$1"
if [ -z "$1" ]
then
	echo "Hostname non indicato"
	exit 1;
fi


IS_SNAPSHOT="true"
if [ ! -z "$2" ]
then
	IS_SNAPSHOT="$2"
fi

OPENSPCOOP_VERSION="3.4"
#utilizzare per le versioni di svn >= 1.8
#OPENSPCOOP_VERSION="$(svn info | grep 'URL: svn' | cut -d '/' -f 7)"

DEPLOY_MAVEN=true
BUILD_PUBLIC_MAVEN=true
DEPLOY_PUBLIC_MAVEN=true
BUILD_DEPLOY_FULL_UTILS_PUBLIC_MAVEN=false

# ATTENZIONE!!!! Se si ottiene:
# Failed to execute goal org.apache.maven.plugins:maven-deploy-plugin:2.7:deploy-file (default-cli) on project standalone-pom: Failed to deploy artifacts/metadata: Cannot access scpexe://poli-dev.link.it/opt/local/maven/thirdparty-releases with type default using the available connector factories: BasicRepositoryConnectorFactory: Cannot access scpexe://poli-dev.link.it/opt/local/maven/thirdparty-releases using the registered transporter factories: WagonTransporterFactory: java.util.NoSuchElementException
# cd $M2_HOME/lib/ext
# wget https://repo1.maven.org/maven2/org/apache/maven/wagon/wagon-ssh-common/3.3.4/wagon-ssh-common-3.3.4.jar
# wget https://repo1.maven.org/maven2/org/apache/maven/wagon/wagon-ssh-external/3.3.4/wagon-ssh-external-3.3.4.jar

# Check maven version
# Per il deploy serve maven, versione 2, altrimenti non sono riuscito a farlo funzionare
MVN_VERSION=$(mvn -v | grep "Apache Maven" | cut -d ' ' -f 3)
MVN_MAJOR_VERSION=$(echo "${MVN_VERSION}" | cut -d '.' -f 1)
if [ ! "${MVN_MAJOR_VERSION}" == "3" ]
then
	echo "Versione di Maven non supporta, utilizzare la versione 3 per il deploy! (versione '${MVN_MAJOR_VERSION}' trovata: ${MVN_VERSION})"
        echo "NOTA: in caso di errore durante l'upload 'Cannot access scpexe.....WagonTransporterFactory....' aggiungere in $M2_HOME/lib/ext i jar wagon-ssh-common-3.3.4.jar e wagon-ssh-external-3.3.4.jar"
	exit 1
fi

PREFIX_URL="scpexe://${MVN_DEPLOY_HOSTNAME}/opt/local/maven"
SNAPSHOT_SUFFIX=""
if [ "$IS_SNAPSHOT" == "true" ]
then
	SNAPSHOT_SUFFIX="-SNAPSHOT"
	URL_REPOSITORY="${PREFIX_URL}/snapshots"
else
	URL_REPOSITORY="${PREFIX_URL}/public"
fi



#Ricavo la minor
VERSIONE_DISTRIB=$(cat build.number  | grep "build\.number" | cut -d '=' -f 2)

MVN_VERSION="$OPENSPCOOP_VERSION.$VERSIONE_DISTRIB$SNAPSHOT_SUFFIX"





echo "Installazione JAR Openspcoop2 nel repository Link.it in corso..."

cp pom.xml ../generic_project/
cp scripts/repo/pom.xml.template ../generic_project/

pushd ../generic_project

echo "Generazione dei JAR in corso..."

#ant clean build;

UTILS_JAR="openspcoop2_utils"
GENERIC_PROJECT_JAR="openspcoop2_generic-project"

OPENSPCOOP_BUILD_VERSION="$(ls dist/${UTILS_JAR}_*.jar | cut -d '_' -f 3 | cut -d '.' -f 1)"


echo "OPENSPCOOP_VERSION [" $OPENSPCOOP_VERSION "]"

echo "OPENSPCOOP_BUILD_VERSION ["  $OPENSPCOOP_BUILD_VERSION "]"

echo "IS_SNAPSHOT [" $IS_SNAPSHOT "]"

echo "MVN_VERSION [" $MVN_VERSION "]"

echo "Generazione dei JAR completa."




# ----- UTILS COMPLESSIVE

if [ "${DEPLOY_MAVEN}" == "true" ]
then
	echo "Deploy del JAR $UTILS_JAR in corso..."

	echo "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"
        xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">
        <artifactId>$UTILS_JAR</artifactId><groupId>org.openspcoop2</groupId><version>$MVN_VERSION</version><modelVersion>4.0.0</modelVersion>
        <name>Openspcoop2 Utils</name></project>" > "dist/${UTILS_JAR}_${MVN_VERSION}.pom"
        
	mvn deploy:deploy-file -DrepositoryId=link-repository -DpomFile=dist/$UTILS_JAR"_"$MVN_VERSION.pom -Dfile=dist/$UTILS_JAR"_"$OPENSPCOOP_BUILD_VERSION.jar -Durl=$URL_REPOSITORY
	
	rm -f "dist/${UTILS_JAR}_${MVN_VERSION}.pom"
	
	echo "Deploy del JAR $UTILS_JAR completato."
fi

if [ "${BUILD_PUBLIC_MAVEN}" == "true" -a "${BUILD_DEPLOY_FULL_UTILS_PUBLIC_MAVEN}" == "true" -a ! "$IS_SNAPSHOT" == "true" ]
then
	echo "Preparazione struttura del JAR $UTILS_JAR per mvn central in corso..."
	MAVEN_CENTRAL_REPO=../mvn/mvn_central_${MVN_VERSION}/${UTILS_JAR}-${MVN_VERSION}
	mkdir -p ${MAVEN_CENTRAL_REPO}
	cp pom.xml.template ${MAVEN_CENTRAL_REPO}/pom.xml
	sed -i "s/ARTIFACT-ID/${UTILS_JAR}/g" ${MAVEN_CENTRAL_REPO}/pom.xml
	sed -i "s/VERSIONE-LIBRERIA/${MVN_VERSION}/g" ${MAVEN_CENTRAL_REPO}/pom.xml
	mkdir ${MAVEN_CENTRAL_REPO}/src
	cp -r ../utils/src/* ${MAVEN_CENTRAL_REPO}/src
	mkdir ${MAVEN_CENTRAL_REPO}/javadoc
	cp -r doc/api/utils/* ${MAVEN_CENTRAL_REPO}/javadoc
	unzip -q dist/$UTILS_JAR"_"$OPENSPCOOP_BUILD_VERSION.jar -d ${MAVEN_CENTRAL_REPO}/classes
	echo "Preparazione struttura del JAR $UTILS_JAR per mvn central completato."
fi

if [ "${DEPLOY_PUBLIC_MAVEN}" == "true" -a "${BUILD_DEPLOY_FULL_UTILS_PUBLIC_MAVEN}" == "true" -a ! "$IS_SNAPSHOT" == "true" ]
then
	echo "Deploy del JAR $UTILS_JAR su mvn central in corso..."
	pushd ${MAVEN_CENTRAL_REPO} > /dev/null
	mvn clean deploy
	popd > /dev/null
	echo "Deploy del JAR $UTILS_JAR su mvn central completato"
fi



# ----- UTILS PARZIALI

LIST_PACKAGE_UTILS="beans cache certificate checksum crypt csv datasource date dch digest id io jaxb jaxrs jdbc jmx json logger mail mime openapi pdf properties random regexp resources rest security semaphore serialization service sonde sql threads transport wsdl xacml xml2json xml"

for packageName in ${LIST_PACKAGE_UTILS}
do

	if [ "${DEPLOY_MAVEN}" == "true" ]
	then
		echo "Deploy del JAR $UTILS_JAR (${packageName}) in corso..."

		echo "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"
		xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">
		<artifactId>$UTILS_JAR-${packageName}</artifactId><groupId>org.openspcoop2.utils</groupId><version>$MVN_VERSION</version><modelVersion>4.0.0</modelVersion>
		<name>Openspcoop2 Utils - ${packageName}</name></project>" > "dist/utils/${UTILS_JAR}-${packageName}_${MVN_VERSION}.pom"

		mvn deploy:deploy-file -DrepositoryId=link-repository -DpomFile=dist/utils/$UTILS_JAR"-"${packageName}"_"$MVN_VERSION.pom -Dfile=dist/utils/$UTILS_JAR"-"${packageName}"_"$OPENSPCOOP_BUILD_VERSION.jar -Durl=$URL_REPOSITORY
		rm -f "dist/utils/${UTILS_JAR}-${packageName}_${MVN_VERSION}.pom"

		echo "Deploy del JAR $UTILS_JAR (${packageName}) completato."	
	fi
	
	if [ "${BUILD_PUBLIC_MAVEN}" == "true" -a ! "$IS_SNAPSHOT" == "true" ]
	then
		echo "Preparazione struttura del JAR $UTILS_JAR (${packageName}) per mvn central in corso..."
		MAVEN_CENTRAL_REPO=../mvn/mvn_central_${MVN_VERSION}/${UTILS_JAR}-${packageName}-${MVN_VERSION}
		mkdir -p ${MAVEN_CENTRAL_REPO}
		cp pom.xml.template ${MAVEN_CENTRAL_REPO}/pom.xml
		sed -i "s/ARTIFACT-ID/${UTILS_JAR}-${packageName}/g" ${MAVEN_CENTRAL_REPO}/pom.xml
		sed -i "s/VERSIONE-LIBRERIA/${MVN_VERSION}/g" ${MAVEN_CENTRAL_REPO}/pom.xml
		mkdir -p ${MAVEN_CENTRAL_REPO}/src/org/openspcoop2/utils/
		cp -r ../utils/src/org/openspcoop2/utils/${packageName} ${MAVEN_CENTRAL_REPO}/src//org/openspcoop2/utils/
		mkdir ${MAVEN_CENTRAL_REPO}/javadoc
		cp -r doc/api/utils/* ${MAVEN_CENTRAL_REPO}/javadoc
		pushd ${MAVEN_CENTRAL_REPO}/javadoc/org/openspcoop2/utils/ > /dev/null
		# Cancella tutti i file normali
		find . -mindepth 1 -maxdepth 1 -type f -exec rm -f {} +
		# Cancella tutte le directory tranne quella da mantenere
		find . -mindepth 1 -maxdepth 1 -type d ! -name "${packageName}" -exec rm -rf {} +
		popd > /dev/null
		unzip -q dist/utils/$UTILS_JAR"-"${packageName}"_"$OPENSPCOOP_BUILD_VERSION.jar -d ${MAVEN_CENTRAL_REPO}/classes
		echo "Preparazione struttura del JAR $UTILS_JAR (${packageName}) per mvn central completato."
	fi
	
	if [ "${DEPLOY_PUBLIC_MAVEN}" == "true" -a ! "$IS_SNAPSHOT" == "true" ]
	then
		echo "Deploy del JAR $UTILS_JAR (${packageName}) su mvn central in corso..."
		pushd ${MAVEN_CENTRAL_REPO} > /dev/null
		mvn clean deploy
		popd > /dev/null
		echo "Deploy del JAR $UTILS_JAR (${packageName}) su mvn central completato"
	fi
done

# -- base

if [ "${DEPLOY_MAVEN}" == "true" ]
then
	echo "Deploy del JAR $UTILS_JAR (base) in corso..."

	echo "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"
        xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">
        <artifactId>$UTILS_JAR-base</artifactId><groupId>org.openspcoop2.utils</groupId><version>$MVN_VERSION</version><modelVersion>4.0.0</modelVersion>
        <name>Openspcoop2 Utils - Base</name></project>" > "dist/utils/${UTILS_JAR}-base_${MVN_VERSION}.pom"

	mvn deploy:deploy-file -DrepositoryId=link-repository -DpomFile=dist/utils/$UTILS_JAR"-base_"$MVN_VERSION.pom -Dfile=dist/utils/$UTILS_JAR"_"$OPENSPCOOP_BUILD_VERSION.jar -Durl=$URL_REPOSITORY

	rm -f "dist/utils/${UTILS_JAR}-base_${MVN_VERSION}.pom"

	echo "Deploy del JAR $UTILS_JAR (base) completato."
fi

if [ "${BUILD_PUBLIC_MAVEN}" == "true" -a ! "$IS_SNAPSHOT" == "true" ]
then
	echo "Preparazione struttura del JAR $UTILS_JAR (base) per mvn central in corso..."
	MAVEN_CENTRAL_REPO=../mvn/mvn_central_${MVN_VERSION}/${UTILS_JAR}-base-${MVN_VERSION}
	mkdir -p ${MAVEN_CENTRAL_REPO}
	cp pom.xml.template ${MAVEN_CENTRAL_REPO}/pom.xml
	sed -i "s/ARTIFACT-ID/${UTILS_JAR}-base/g" ${MAVEN_CENTRAL_REPO}/pom.xml
	sed -i "s/VERSIONE-LIBRERIA/${MVN_VERSION}/g" ${MAVEN_CENTRAL_REPO}/pom.xml
	mkdir -p ${MAVEN_CENTRAL_REPO}/src/org/openspcoop2/utils/
	cp ../utils/src/org/openspcoop2/utils/*.java ${MAVEN_CENTRAL_REPO}/src//org/openspcoop2/utils/
	mkdir ${MAVEN_CENTRAL_REPO}/javadoc
	cp -r doc/api/utils/* ${MAVEN_CENTRAL_REPO}/javadoc
	pushd ${MAVEN_CENTRAL_REPO}/javadoc/org/openspcoop2/utils/ > /dev/null
	# Cancella tutte le directory
	find . -mindepth 1 -maxdepth 1 -type d -exec rm -rf {} +
	popd > /dev/null
	unzip -q dist/utils/$UTILS_JAR"_"$OPENSPCOOP_BUILD_VERSION.jar -d ${MAVEN_CENTRAL_REPO}/classes
	echo "Preparazione struttura del JAR $UTILS_JAR (base per mvn central completato."
fi

if [ "${DEPLOY_PUBLIC_MAVEN}" == "true" -a ! "$IS_SNAPSHOT" == "true" ]
then
	echo "Deploy del JAR $UTILS_JAR (base) su mvn central in corso..."
	pushd ${MAVEN_CENTRAL_REPO} > /dev/null
	mvn clean deploy
	popd > /dev/null
	echo "Deploy del JAR $UTILS_JAR (base) su mvn central completato"
fi




# ----- GENERIC PROJECT

if [ "${DEPLOY_MAVEN}" == "true" ]
then

	echo "Deploy del JAR $GENERIC_PROJECT_JAR in corso..."

	echo "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"
        xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">
        <artifactId>$GENERIC_PROJECT_JAR</artifactId><groupId>org.openspcoop2</groupId><version>$MVN_VERSION</version><modelVersion>4.0.0</modelVersion>
        <name>Openspcoop2 Generic Project</name></project>" > "dist/${GENERIC_PROJECT_JAR}_${MVN_VERSION}.pom"


	mvn deploy:deploy-file -DrepositoryId=link-repository -DpomFile=dist/$GENERIC_PROJECT_JAR"_"$MVN_VERSION.pom -Dfile=dist/$GENERIC_PROJECT_JAR"_"$OPENSPCOOP_BUILD_VERSION.jar -Durl=$URL_REPOSITORY

	rm -f "dist/${GENERIC_PROJECT_JAR}_${MVN_VERSION}.pom"

	echo "Deploy del JAR $GENERIC_PROJECT_JAR completato."
fi

if [ "${BUILD_PUBLIC_MAVEN}" == "true" -a ! "$IS_SNAPSHOT" == "true" ]
then
	echo "Preparazione struttura del JAR $GENERIC_PROJECT_JAR per mvn central in corso..."
	MAVEN_CENTRAL_REPO=../mvn/mvn_central_${MVN_VERSION}/${GENERIC_PROJECT_JAR}-${MVN_VERSION}
	mkdir -p ${MAVEN_CENTRAL_REPO}
	cp pom.xml.template ${MAVEN_CENTRAL_REPO}/pom.xml
	sed -i "s/ARTIFACT-ID/${GENERIC_PROJECT_JAR}/g" ${MAVEN_CENTRAL_REPO}/pom.xml
	sed -i "s/VERSIONE-LIBRERIA/${MVN_VERSION}/g" ${MAVEN_CENTRAL_REPO}/pom.xml
	mkdir ${MAVEN_CENTRAL_REPO}/src
	cp -r ../generic_project/src/* ${MAVEN_CENTRAL_REPO}/src
	mkdir ${MAVEN_CENTRAL_REPO}/javadoc
	cp -r doc/api/generic-project/* ${MAVEN_CENTRAL_REPO}/javadoc
	unzip -q dist/$GENERIC_PROJECT_JAR"_"$OPENSPCOOP_BUILD_VERSION.jar -d ${MAVEN_CENTRAL_REPO}/classes
	echo "Preparazione struttura del JAR $GENERIC_PROJECT_JAR per mvn central completato."
fi

if [ "${DEPLOY_PUBLIC_MAVEN}" == "true" -a ! "$IS_SNAPSHOT" == "true" ]
then
	echo "Deploy del JAR $GENERIC_PROJECT_JAR su mvn central in corso..."
	pushd ${MAVEN_CENTRAL_REPO} > /dev/null
	mvn clean deploy
	popd > /dev/null
	echo "Deploy del JAR $GENERIC_PROJECT_JAR su mvn central completato"
fi




echo "Installazione JAR Openspcoop2 Utils, GenericProject nel repository Link.it completata."


popd
echo "Position: [${PWD}]"



rm -f ../generic_project/pom.xml





if [ "$IS_SNAPSHOT" == "false" ]
then
	ant -buildfile scripts/buildNumber.xml -DbuildNumberFile=build.number
fi

