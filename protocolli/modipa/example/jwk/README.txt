Per produrre un jwk compatibile per agire da truststore per i test seguire i seguenti comandi:

rm -rf jwk_private_keys jwk_public_keys

mkdir -p jwk_private_keys

./generaPrivateKeyJWK.sh ../../../../core/dist ../../../../lib ../keys/xca/ExampleServer.key ../keys/xca/ExampleServer.crt jwk_private_keys/ExampleServer.jwk KID-ExampleServer

./generaPrivateKeyJWK.sh ../../../../core/dist ../../../../lib ../keys/xca/ExampleExternalClient2.key ../keys/xca/ExampleExternalClient2.crt jwk_private_keys/ExampleExternalClient2.jwk KID-ExampleExternalClient2
./generaPrivateKeyJWK.sh ../../../../core/dist ../../../../lib ../keys/xca/ExampleExternalClient4.key ../keys/xca/ExampleExternalClient4.crt jwk_private_keys/ExampleExternalClient4.jwk KID-ExampleExternalClient4

mkdir -p jwk_public_keys

./generaJWK.sh ../../../../core/dist ../../../../lib ../keys/xca/ExampleServer.crt jwk_public_keys/KID-ExampleServer.jwk KID-ExampleServer
./generaJWK.sh ../../../../core/dist ../../../../lib ../keys/xca/ExampleClient1.crt jwk_public_keys/KID-ApplicativoBlockingIDA01.jwk KID-ApplicativoBlockingIDA01
./generaJWK.sh ../../../../core/dist ../../../../lib ../keys/xca/ExampleClient3.crt jwk_public_keys/KID-ApplicativoBlockingIDA01ExampleClient3.jwk KID-ApplicativoBlockingIDA01ExampleClient3
./generaJWK.sh ../../../../core/dist ../../../../lib trasparente_keys/testJWKrsapubkey.pem jwk_public_keys/KID-ApplicativoBlockingJWK.jwk KID-ApplicativoBlockingJWK
./generaJWK.sh ../../../../core/dist ../../../../lib trasparente_keys/testJWKrsapubkey.pem jwk_public_keys/de606068-01cb-49a5-824d-fb171b5d5ae4.jwk de606068-01cb-49a5-824d-fb171b5d5ae4
./generaJWK.sh ../../../../core/dist ../../../../lib trasparente_keys/keyPair-test.rsa.publicKey.pem jwk_public_keys/KID-ApplicativoBlockingKeyPair.jwk KID-ApplicativoBlockingKeyPair
./generaJWK.sh ../../../../core/dist ../../../../lib ../keys/xca/ExampleExternalClient1.crt jwk_public_keys/KID-ExampleExternalClient1.jwk KID-ExampleExternalClient1
./generaJWK.sh ../../../../core/dist ../../../../lib ../keys/xca/ExampleExternalClient2.crt jwk_public_keys/KID-ExampleExternalClient2.jwk KID-ExampleExternalClient2
./generaJWK.sh ../../../../core/dist ../../../../lib ../keys/xca/ExampleExternalClient3.crt jwk_public_keys/KID-ExampleExternalClient3.jwk KID-ExampleExternalClient3
./generaJWK.sh ../../../../core/dist ../../../../lib ../keys/xca/ExampleExternalClient4.crt jwk_public_keys/KID-ExampleExternalClient4.jwk KID-ExampleExternalClient4

./generaTrustStoreJWK.sh jwk_public_keys/ truststore_certificati.jwk

cp -r jwk_private_keys /etc/govway/keys/
cp -r jwk_public_keys /etc/govway/keys/
cp truststore_certificati.jwk /etc/govway/keys




