.. _scenari_ambiente_prerequisiti:

Prerequisiti
------------

Per l'avvio dell'ambiente di esecuzione degli scenari è necessario disporre del seguente software di base:

- dotarsi di una installazione `Docker <https://www.docker.com>`_ che gestirà l'intero contesto di esecuzione degli scenari;

- dotarsi dell'applicativo `Postman <https://www.getpostman.com>`_ utilizzato come client per l'invio delle richieste a Govway.

L'ambiente di esecuzione è composto da:

- `ambiente docker-compose <https://raw.githubusercontent.com/link-it/govway/master/resources/scenari/scenari.zip>`_ preinizializzato con gli scenari descritti in queso manuale;

- `progetto Postman <https://raw.githubusercontent.com/link-it/govway/master/resources/scenari/scenari-postman.json>`_ preconfigurato per verificare gli scenari:

  - invocazione pubblica o OAuth su profilo 'API Gateway';

  - profilo 'ModI' su API REST;

  - profilo 'ModI' su API SOAP.

Gli scenari configurati sull'ambiente docker devono poter accedere ai seguenti servizi su internet:

- Petstore: https://petstore.swagger.io/

- Credit Card Verification: https://ws.cdyne.com/creditcardverify/luhnchecker.asmx

