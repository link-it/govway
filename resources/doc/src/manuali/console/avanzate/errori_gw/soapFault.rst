.. _soapFault:

SOAP Fault
~~~~~~~~~~

Per le API di tipologia SOAP, sia in erogazione che in fruizione, quando
il Gateway non può completare con successo la gestione della transazione
in corso genera un SOAPFault contenente un actor (o role in SOAP 1.2)
valorizzato con *http://govway.org/integration*.

Di seguito viene riportato un esempio di tale oggetto:

::

    <?xml version="1.0" encoding="UTF-8"?>
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
        <SOAP-ENV:Body>
            <SOAP-ENV:Fault>
                <faultcode xmlns:integration="http://govway.org/integration/fault">integration:GOVWAY-403</faultcode>
                <faultstring>Identificazione dinamica dell'azione associata alla porta delegata fallita</faultstring>
                <faultactor>http://govway.org/integration</faultactor>
                <detail>
                    <problem xmlns="urn:ietf:rfc:7807">
                        <type>https://httpstatuses.com/500</type>
                        <title>Internal Server Error</title>
                        <status>500</status>
                        <detail>Identificazione dinamica dell'azione associata alla porta delegata fallita</detail>
                        <govway_status>integration:GOVWAY-403</govway_status>
                    </problem>
                </detail>
            </SOAP-ENV:Fault>
        </SOAP-ENV:Body>
    </SOAP-ENV:Envelope>

L'elemento *fault string* contiene informazioni di dettaglio sull'errore
avvenuto, errore codificato dal codice presente nell'elemento *fault
code*. (Per ulteriori dettagli sul codice consultare la sezione :ref:`codiciErrore`).

Il SOAP Fault contiene all'interno dell'elemento *detail* un oggetto
*Problem Details* simile a quanto descritto nella sezione :ref:`rfc7807` utile a
comprendere il codice di errore ed il dettaglio attraverso una modalità
alternativa alla lettura degli elementi *fault string* e *fault code*
(che varia a seconda della versione SOAP utilizzata).
