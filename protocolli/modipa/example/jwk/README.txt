Per produrre un jwk compatibile per agire da truststore per i test seguire i seguenti comandi:

rm -rf jwk_private_keys jwk_public_keys

mkdir -p jwk_private_keys

./generaPrivateKeyJWK.sh ../../../../core/dist ../../../../lib ../keys/xca/ExampleServer.key ../keys/xca/ExampleServer.crt jwk_private_keys/ExampleServer.jwk KID-ExampleServer

mkdir -p jwk_public_keys

./generaJWK.sh ../../../../core/dist ../../../../lib ../keys/xca/ExampleServer.crt jwk_public_keys/KID-ExampleServer.jwk KID-ExampleServer
./generaJWK.sh ../../../../core/dist ../../../../lib ../keys/xca/ExampleClient1.crt jwk_public_keys/KID-ApplicativoBlockingIDA01.jwk KID-ApplicativoBlockingIDA01
./generaJWK.sh ../../../../core/dist ../../../../lib trasparente_keys/testJWKrsapubkey.pem jwk_public_keys/KID-ApplicativoBlockingJWK.jwk KID-ApplicativoBlockingJWK
./generaJWK.sh ../../../../core/dist ../../../../lib trasparente_keys/keyPair-test.rsa.publicKey.pem jwk_public_keys/KID-ApplicativoBlockingKeyPair.jwk KID-ApplicativoBlockingKeyPair

./generaTrustStoreJWK.sh jwk_public_keys/ truststore_certificati.jwk

cp -r jwk_private_keys /etc/govway/keys/
cp -r jwk_public_keys /etc/govway/keys/
cp truststore_certificati.jwk /etc/govway/keys




