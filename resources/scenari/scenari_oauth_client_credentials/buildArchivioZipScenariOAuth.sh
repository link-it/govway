if [ ! -d scenari ]
then
	echo "Directory scenari non esistente"
	exit 2
fi
sudo su -c "rm scenari/data/govway/log/* -rf"
if [ -e scenari_oauth_client_credentials.zip ] 
then
	mv scenari_oauth_client_credentials.zip scenari_oauth_client_credentials.zip.old
fi
sudo su -c "zip -r scenari_oauth_client_credentials.zip scenari"
