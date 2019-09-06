Creati con i comandi:

RSA
- keytool -genkey -alias rsa256 -keyalg RSA -sigalg SHA256withRSA -keysize 2048 -validity 3650 -keystore keystore_rsa256.jks

ECDSA
- keytool -genkeypair -alias ec256 -keyalg EC -keysize 256 -sigalg SHA256withECDSA -validity 3650 -storetype JKS -keystore keystore_ec256.jks

Conversione in p12
- keytool -importkeystore -srckeystore keystore_rsa256.jks -destkeystore keystore_rsa256.p12 -deststoretype pkcs12

Password sia del keystore che delle chiavi: 123456
