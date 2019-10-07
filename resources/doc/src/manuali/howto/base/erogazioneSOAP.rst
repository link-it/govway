.. _erogazioneSOAP:

Erogazione API SOAP
-------------------

In questa sezione vengono descritti i passi di configurazione
necessari a registrare una API SOAP implementata da un applicativo
interno al proprio dominio di gestione. 
Nello scenario si
suppone che il servizio *Credit Card Verification*, disponibile on line all'indirizzo http://ws.cdyne.com/creditcardverify/luhnchecker.asmx , sia erogato all'interno del dominio di gestione.

L'API, per questo esempio, viene registrata in modo che sia accessibile
in forma anonima da qualunque client invocando l'url esposta da GovWay.
Una rappresentazione di questo scenario è mostrata nella :numref:`quick_erogazioneSOAP_fig`. Prima
di procedere con la configurazione effettuare il download
dell'interfaccia WSDL disponibile in
https://ws.cdyne.com/creditcardverify/luhnchecker.asmx?wsdl.

.. figure:: ../_figure_howto/erogazioneSOAPBase.png
    :scale: 100%
    :align: center
    :name: quick_erogazioneSOAP_fig

    Erogazione di una API SOAP tramite GovWay

Per registrare l'API su Govway, utilizzando la console *govwayConsole*,
procedere come segue:

1. **Registrazione API**.

   Accedere alla sezione *'API'* e selezionare il pulsante *'Aggiungi'*.
   Fornire i seguenti dati:

   -  *Tipo*: selezionare la tipologia *'SOAP'*.

   -  *Nome*: indicare il nome dell'API che si sta registrando, ad
      esempio *'CreditCardVerification'*.

   -  *Descrizione*: opzionalmente è possibile fornire una descrizione
      generica dell'API.

   -  *Versione*: indicare la versione dell'API che si sta registrando;
      nell'esempio utilizziamo la versione *1*.

   -  *WSDL*: caricare l'interfaccia WSDL scaricata dall'indirizzo
      https://ws.cdyne.com/creditcardverify/luhnchecker.asmx?wsdl.

   .. figure:: ../_figure_howto/erogazioneSOAPBaseRegistrazioneAPI.png
       :scale: 100%
       :align: center
       :name: quick_registrazioneAPISOAP_fig

       Registrazione di una API SOAP

   Effettuato il salvataggio, l'API sarà consultabile all'interno dell'elenco delle API registrate. Accedendo al dettaglio si potranno visionare i servizi che tale API dispone che corrispondono ai *port type* presenti nell'interfaccia wsdl caricata. Come si può vedere dalla :numref:`quick_serviziAPISOAP_fig` l'interfaccia *Credit Card Verification* possiede tre differenti servizi che corrispondo a differenti modalità di utilizzo. Nel seguito di questo esempio verrà utilizzato esclusivamente il servizio *LuhnCheckerSoap*.

   .. figure:: ../_figure_howto/erogazioneSOAPBaseConsultazioneServiziAPI.png
       :scale: 100%
       :align: center
       :name: quick_serviziAPISOAP_fig

       Servizi di una API SOAP

2. **Registrazione Erogazione**

   Accedere alla sezione *'Erogazioni'* e selezionare il pulsante
   *'Aggiungi'*. Fornire i seguenti dati:

   -  *Nome*: selezionare l'API precedentemente registrata
      *'CreditCardVerification v1'*.

   -  *Servizio*: selezionare uno dei servizi (port type) definiti
      nell'API precedentemente registrata *'LuhnCheckerSoap'*.

   -  *Controllo degli Accessi - Accesso API*: per esporre l'API in modo che sia
      invocabile da qualunque client in forma anonima selezionare lo
      stato *'pubblico'*.

   -  *Connettore - Endpoint*: indicare l'endpoint dove viene erogata
      l'API nel dominio interno. Per il nostro esempio utilizzare la
      url:

      -  *http://ws.cdyne.com/creditcardverify/luhnchecker.asmx*

   .. figure:: ../_figure_howto/erogazioneSOAPBaseRegistrazioneErogazione.png
       :scale: 100%
       :align: center
       :name: quick_erogazioneAPISOAP_fig

       Registrazione di una erogazione di API SOAP

   Effettuato il salvataggio, l'API erogata sarà consultabile all'interno dell'elenco delle erogazioni. Accedendo al dettaglio si potrà conoscere l'\ *url di invocazione* che deve essere comunicata ai client che desiderano invocare l'API.

   .. figure:: ../_figure_howto/erogazioneSOAPBaseConsultazioneErogazione.png
       :scale: 100%
       :align: center
       :name: quick_urlErogazioneAPISOAP_fig

       URL di Invocazione dell'API SOAP erogata

3. **Invocazione API tramite GovWay**

   Al termine di questi passi di configurazione il servizio SOAP sarà
   raggiungibile dai client utilizzando l'url di invocazione:

   -  http://host:port/govway/*<soggetto-dominio-interno>*/LuhnCheckerSoap/v1

       **Soggetto Interno al Dominio**

       In questo esempio si suppone che il nome del soggetto fornito
       durante la fase di installazione di GovWay sia *Ente*.

   ::

       curl -v -X POST "http://127.0.0.1:8080/govway/Ente/LuhnCheckerSoap/v1" \
       -H 'Content-Type: text/xml;charset=UTF-8' \
       -H 'SOAPAction: "http://ws.cdyne.com/CheckCC"' \
       -d '<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
           <soapenv:Header/>
           <soapenv:Body>
               <CheckCC xmlns="http://ws.cdyne.com/">
                   <CardNumber>4111111111111111</CardNumber>
               </CheckCC>
           </soapenv:Body>
       </soapenv:Envelope>'

   L'esito della verifica viene ritornato con un codice http 200 e una risposta contenente i dettagli della carta:

   ::

       HTTP/1.1 200 OK
       Connection: keep-alive
       Server: GovWay
       GovWay-Message-ID: b62dc163-e788-4dc2-9cee-40c77b0a7a29
       GovWay-Transaction-ID: fc155be0-c1ac-4e2e-93f7-d69a30258069
       Transfer-Encoding: chunked
       Content-Type: text/xml;charset=utf-8
       Date: Thu, 15 Nov 2018 13:34:22 GMT

       <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
          <soap:Body>
               <CheckCCResponse xmlns="http://ws.cdyne.com/">
                   <CheckCCResult>
                       <CardType>VISA</CardType>
                       <CardValid>true</CardValid>
                   </CheckCCResult>
               </CheckCCResponse>
          </soap:Body>
       </soap:Envelope>

   Per simulare la medesima richiesta utilizzando un messaggio SOAP 1.2 è possibile usare la stessa url di invocazione:

   ::

       curl -v -X POST "http://127.0.0.1:8080/govway/Ente/LuhnCheckerSoap/v1" \
       -H 'Content-Type: application/soap+xml; charset=utf-8' \
       -d '<soap12:Envelope xmlns:soap12="http://www.w3.org/2003/05/soap-envelope">
           <soap12:Header/>
           <soap12:Body>
               <CheckCC xmlns="http://ws.cdyne.com/">
                   <CardNumber>4111111111111111</CardNumber>
               </CheckCC>
           </soap12:Body>
       </soap12:Envelope>'

4. **Consultazione Tracce**

   Attraverso la console *govwayMonitor* è possibile consultare lo
   storico delle transazioni che sono transitate nel gateway e
   recuperare i dettagli di una singola invocazione cosi come già
   descritto nella sezione :ref:`erogazioneREST`.
