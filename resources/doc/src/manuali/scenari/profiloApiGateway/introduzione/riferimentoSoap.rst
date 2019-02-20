.. _quickProfiloAPIGW_EsempioSOAP:

Servzio SOAP di Riferimento per gli Esempi
------------------------------------------

Per i servizi di tipologia **SOAP**, il servizio di esempio utilizzato
per mostrare le funzionalità dell'API Gateway è *Credit Card
Verification* (web site:
http://wiki.cdyne.com/index.php/Credit_Card_Verification ) disponibile
all'indirizzo http://ws.cdyne.com/creditcardverify/luhnchecker.asmx.
L'interfaccia WSDL del servizio è scaricabile in
https://ws.cdyne.com/creditcardverify/luhnchecker.asmx?wsdl. Per
simulare una richiesta il cui fine è validare un numero di carta di
credito è utilizzabile il seguente comando, che genera una richiesta
SOAP 1.1:

::

    curl -v -X POST "http://ws.cdyne.com/creditcardverify/luhnchecker.asmx" \
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

L'esito della verifica viene ritornato con un codice http 200 e una
risposta contenente i dettagli della carta:

::

    HTTP/1.1 200 OK
    Cache-Control: no-cache
    Pragma: no-cache
    Content-Type: text/xml; charset=utf-8
    Expires: -1
    Server: Microsoft-IIS/8.5
    X-AspNet-Version: 4.0.30319
    X-Powered-By: ASP.NET
    Date: Thu, 15 Nov 2018 11:50:12 GMT
    Content-Length: 393
    Connection: keep-alive


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

Per simulare la medesima richiesta utilizzando SOAP 1.2 è possibile
usare il comando:

::

    curl -v -X POST "http://ws.cdyne.com/creditcardverify/luhnchecker.asmx" \
    -H 'Content-Type: application/soap+xml; charset=utf-8' \
    -d '<soap12:Envelope xmlns:soap12="http://www.w3.org/2003/05/soap-envelope">
        <soap12:Header/>
        <soap12:Body>
            <CheckCC xmlns="http://ws.cdyne.com/">
                <CardNumber>4111111111111111</CardNumber>
            </CheckCC>
        </soap12:Body>
    </soap12:Envelope>'

L'esito della verifica viene ritornato con un codice http 200 e una
risposta contenente i dettagli della carta:

::

    HTTP/1.1 200 OK
    Cache-Control: no-cache
    Pragma: no-cache
    Content-Type: application/soap+xml; charset=utf-8
    Expires: -1
    Server: Microsoft-IIS/8.5
    X-AspNet-Version: 4.0.30319
    X-Powered-By: ASP.NET
    Date: Thu, 15 Nov 2018 11:50:12 GMT
    Content-Length: 393
    Connection: keep-alive


    <soap12:Envelope xmlns:soap12="http://www.w3.org/2003/05/soap-envelope">
       <soap12:Body>
            <CheckCCResponse xmlns="http://ws.cdyne.com/">
                <CheckCCResult>
                    <CardType>VISA</CardType>
                    <CardValid>true</CardValid>
                </CheckCCResult>
            </CheckCCResponse>
       </soap12:Body>
    </soap12:Envelope>
