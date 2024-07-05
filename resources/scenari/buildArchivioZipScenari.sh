if [ ! -d scenari ]
then
	echo "Directory scenari non esistente"
	exit 2
fi
rm scenari/data/govway/log/* -rf
rm scenari/plugin/DigestMapper/target/* -rf
if [ -e scenari.zip ] 
then
	mv scenari.zip scenari.zip.old
fi
sudo su -c "zip -r scenari.zip scenari"
