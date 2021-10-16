SOGGETTO_DEFAULT=$1
if [ -z "$SOGGETTO_DEFAULT" ]
then
	echo "Indicare soggetto di default"
	exit
fi

bash updateCertificatoClient2Interno  $SOGGETTO_DEFAULT

bash updateCredenzialiClient1Esterno  $SOGGETTO_DEFAULT

bash updateCredenzialiClient3Esterno  $SOGGETTO_DEFAULT

