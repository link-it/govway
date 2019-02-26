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

OPENSPCOOP_VERSION="3.1"
#utilizzare per le versioni di svn >= 1.8
#OPENSPCOOP_VERSION="$(svn info | grep 'URL: svn' | cut -d '/' -f 7)"

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

cp pom.xml ../web_generic_project/

pushd ../web_generic_project

echo "Generazione dei JAR in corso..."

#ant clean build;

UTILS_JAR="openspcoop2_utils"
GENERIC_PROJECT_JAR="openspcoop2_generic-project"
WEB_GENERIC_PROJECT_JAR="openspcoop2_web-generic-project"

OPENSPCOOP_BUILD_VERSION="$(ls dist/${UTILS_JAR}_*.jar | cut -d '_' -f 3 | cut -d '.' -f 1)"


echo "OPENSPCOOP_VERSION [" $OPENSPCOOP_VERSION "]"

echo "OPENSPCOOP_BUILD_VERSION ["  $OPENSPCOOP_BUILD_VERSION "]"

echo "IS_SNAPSHOT [" $IS_SNAPSHOT "]"

echo "MVN_VERSION [" $MVN_VERSION "]"

echo "Generazione dei JAR completa."








echo "Deploy del JAR $UTILS_JAR in corso..."

echo "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"
        xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">
        <artifactId>$UTILS_JAR</artifactId><groupId>org.openspcoop2</groupId><version>$MVN_VERSION</version><modelVersion>4.0.0</modelVersion>
        <name>Openspcoop2 Utils</name></project>" > "dist/${UTILS_JAR}_${MVN_VERSION}.pom"

mvn deploy:deploy-file -DrepositoryId=link-repository -DpomFile=dist/$UTILS_JAR"_"$MVN_VERSION.pom -Dfile=dist/$UTILS_JAR"_"$OPENSPCOOP_BUILD_VERSION.jar -Durl=$URL_REPOSITORY

rm -f "dist/${UTILS_JAR}_${MVN_VERSION}.pom"

echo "Deploy del JAR $UTILS_JAR completato."

LIST_PACKAGE_UTILS="beans cache checksum crypt csv datasource date dch digest id io jaxb jaxrs jdbc jmx json logger mail mime openapi properties regexp resources rest security semaphore serialization service sonde sql threads transport wadl wsdl xacml xml2json xml"

for packageName in ${LIST_PACKAGE_UTILS}
do
	echo "Deploy del JAR $UTILS_JAR (${packageName}) in corso..."

	echo "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"
		xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">
		<artifactId>$UTILS_JAR-${packageName}</artifactId><groupId>org.openspcoop2.utils</groupId><version>$MVN_VERSION</version><modelVersion>4.0.0</modelVersion>
		<name>Openspcoop2 Utils - ${packageName}</name></project>" > "dist/utils/${UTILS_JAR}-${packageName}_${MVN_VERSION}.pom"

	mvn deploy:deploy-file -DrepositoryId=link-repository -DpomFile=dist/utils/$UTILS_JAR"-"${packageName}"_"$MVN_VERSION.pom -Dfile=dist/utils/$UTILS_JAR"-"${packageName}"_"$OPENSPCOOP_BUILD_VERSION.jar -Durl=$URL_REPOSITORY

	rm -f "dist/utils/${UTILS_JAR}-${packageName}_${MVN_VERSION}.pom"

	echo "Deploy del JAR $UTILS_JAR (${packageName}) completato."	
done

echo "Deploy del JAR $UTILS_JAR (base) in corso..."

echo "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"
        xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">
        <artifactId>$UTILS_JAR-core</artifactId><groupId>org.openspcoop2.utils</groupId><version>$MVN_VERSION</version><modelVersion>4.0.0</modelVersion>
        <name>Openspcoop2 Utils - Base</name></project>" > "dist/utils/${UTILS_JAR}-base_${MVN_VERSION}.pom"

mvn deploy:deploy-file -DrepositoryId=link-repository -DpomFile=dist/utils/$UTILS_JAR"-base_"$MVN_VERSION.pom -Dfile=dist/utils/$UTILS_JAR"_"$OPENSPCOOP_BUILD_VERSION.jar -Durl=$URL_REPOSITORY

rm -f "dist/utils/${UTILS_JAR}-base_${MVN_VERSION}.pom"

echo "Deploy del JAR $UTILS_JAR (base) completato."




echo "Deploy del JAR $GENERIC_PROJECT_JAR in corso..."

echo "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"
        xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">
        <artifactId>$GENERIC_PROJECT_JAR</artifactId><groupId>org.openspcoop2</groupId><version>$MVN_VERSION</version><modelVersion>4.0.0</modelVersion>
        <name>Openspcoop2 Generic Project</name></project>" > "dist/${GENERIC_PROJECT_JAR}_${MVN_VERSION}.pom"

mvn deploy:deploy-file -DrepositoryId=link-repository -DpomFile=dist/$GENERIC_PROJECT_JAR"_"$MVN_VERSION.pom -Dfile=dist/$GENERIC_PROJECT_JAR"_"$OPENSPCOOP_BUILD_VERSION.jar -Durl=$URL_REPOSITORY

rm -f "dist/${GENERIC_PROJECT_JAR}_${MVN_VERSION}.pom"

echo "Deploy del JAR $GENERIC_PROJECT_JAR completato."




echo "Deploy del JAR $WEB_GENERIC_PROJECT_JAR in corso..."

echo "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"
        xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">
        <artifactId>$WEB_GENERIC_PROJECT_JAR</artifactId>
        <groupId>org.openspcoop2</groupId>
        <version>$MVN_VERSION</version>
        <modelVersion>4.0.0</modelVersion>
        <name>Openspcoop2 Web Generic Project</name>
        <dependencies>
                <dependency>
                        <groupId>org.openspcoop2</groupId>
                        <artifactId>$GENERIC_PROJECT_JAR</artifactId>
                        <version>$MVN_VERSION</version>
                </dependency>
                <dependency>
                        <groupId>org.openspcoop2</groupId>
                        <artifactId>$UTILS_JAR</artifactId>
                        <version>$MVN_VERSION</version>
                </dependency>
        </dependencies>
</project>" > "dist/${WEB_GENERIC_PROJECT_JAR}_${MVN_VERSION}.pom"

mvn deploy:deploy-file -DrepositoryId=link-repository -DpomFile=dist/$WEB_GENERIC_PROJECT_JAR"_"$MVN_VERSION.pom -Dfile=dist/$WEB_GENERIC_PROJECT_JAR"_"$OPENSPCOOP_BUILD_VERSION.jar -Durl=$URL_REPOSITORY

rm -f "dist/${WEB_GENERIC_PROJECT_JAR}_${MVN_VERSION}.pom"

echo "Deploy del JAR $WEB_GENERIC_PROJECT_JAR completato."


echo "Installazione JAR Openspcoop2 Utils, GenericProject, WebGenericProject nel repository Link.it completata."


popd
echo "Position: [${PWD}]"


echo "Installazione JAR Openspcoop2 WebGenericProjectImpl nel repository Link.it in corso..."

cp pom.xml ../web_generic_project/impl

pushd ../web_generic_project/impl

echo "Generazione dei JAR in corso..."

WEB_GENERIC_PROJECT_IMPL_JSF1_JAR="openspcoop2_web-generic-project-impl-jsf1"

echo "Deploy del JAR $WEB_GENERIC_PROJECT_IMPL_JSF1_JAR in corso..."

echo "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"
        xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">
        <artifactId>$WEB_GENERIC_PROJECT_IMPL_JSF1_JAR</artifactId>
        <groupId>org.openspcoop2</groupId>
        <version>$MVN_VERSION</version>
        <modelVersion>4.0.0</modelVersion>
        <name>Openspcoop2 Web Generic Project</name>
        <dependencies>
                <dependency>
                        <groupId>org.openspcoop2</groupId>
                        <artifactId>$WEB_GENERIC_PROJECT_JAR</artifactId>
                        <version>$MVN_VERSION</version>
                </dependency>
        </dependencies>
</project>" > "jsf1/dist/${WEB_GENERIC_PROJECT_IMPL_JSF1_JAR}_${MVN_VERSION}.pom"

mvn deploy:deploy-file -DrepositoryId=link-repository -DpomFile=jsf1/dist/$WEB_GENERIC_PROJECT_IMPL_JSF1_JAR"_"$MVN_VERSION.pom -Dfile=jsf1/dist/$WEB_GENERIC_PROJECT_IMPL_JSF1_JAR"_"$OPENSPCOOP_BUILD_VERSION.jar -Durl=$URL_REPOSITORY

echo "Deploy del JAR $WEB_GENERIC_PROJECT_IMPL_JSF1_JAR completato."

rm -f "jsf1/dist/${WEB_GENERIC_PROJECT_IMPL_JSF1_JAR}_${MVN_VERSION}.pom"

popd 

echo "Installazione JAR Openspcoop2 WebGenericProjectImpl nel repository Link.it completata."

rm -f ../web_generic_project/impl/pom.xml
rm -f ../web_generic_project/pom.xml





if [ "$IS_SNAPSHOT" == "false" ]
then
	ant -buildfile scripts/buildNumber.xml -DbuildNumberFile=build.number
fi

