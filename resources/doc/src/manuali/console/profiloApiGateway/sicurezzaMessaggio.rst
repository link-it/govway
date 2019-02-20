.. _sicurezzaLivelloMessaggio:

Sicurezza a livello del messaggio
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Tramite il collegamento *Sicurezza Messaggio*, presente nella sezione di
configurazione della specifica erogazione/fruizione, è possibile
impostare criteri di elaborazione dei messaggi in transito, attuati dal
gateway, al fine di gestire i meccanismi di sicurezza previsti a livello
del messaggio.

Il form presenta inizialmente lo *Stato* disabilitato. Per abilitare la
sicurezza, impostare il valore dello stato su abilitato e confermare con
il pulsante *Invia*. Appariranno gli elementi *Richiesta* e *Risposta*,
come nella figura seguente.

   .. figure:: ../_figure_console/SicurezzaMessaggio.png
    :scale: 100%
    :align: center
    :name: sicurezza

    Abilitazione Sicurezza Messaggio

Il form consente di selezionare uno schema di sicurezza, tra quelli
disponibili, da applicare al messaggio di richiesta ed a quello di
risposta. Gli schemi di sicurezza applicabili cambiano in base alla
tipologia del messaggio sul quale si applica.

Per la gestione della sicurezza sul messaggio di richiesta, nel caso di
una erogazione, il gateway agisce con il ruolo *Receiver* che comporta
la seguente casistica:

-  *Nel caso del protocollo SOAP*:

   -  *WSSec Signature*, in ricezione si attende un messaggio firmato;
      l'azione è quella di verificare la firma presente

   -  *WSSec Decrypt*, il messaggio ricevuto verrà decifrato

   -  *WSSec SAML Token*, si attende un messaggio contenente una
      asserzione SAML; viene effettuata la verifica dell'asserzione
      presente.

   -  *WSSec Username Token*, viene effettuata la validazione del token
      di autenticazione

   -  *WSSec Timestamp*, se è prevista una scadenza all'interno del
      timestamp presente nel messaggio, se ne verificherà la validità

-  *Nel caso del protocollo REST*

   -  *JWT Decrypt*: il messaggio JSON ricevuto viene decifrato.

   -  *JWT Verifier Signature*: al messaggio JSON ricevuto viene
      verificata la firma.

   -  *XML Decrypt*: il messaggio XML ricevuto viene decifrato.

   -  *XML Verifier Signature*: al messaggio XML ricevuto viene
      verificata la firma.

Per la gestione della sicurezza sul messaggio di risposta, nel caso di
una erogazione, il gateway agisce con il ruolo *Sender* che comporta la
seguente casistica:

-  *Nel caso del protocollo SOAP*:

   -  *WSSec Signature*, il messaggio verrà firmato

   -  *WSSec Encrypt*, il messaggio verrà cifrato

   -  *WSSec SAML Token*, sul messaggio verrà inserita una asserzione
      SAML

   -  *WSSec Username Token*, il messaggio verrà arrichito di un token
      di autenticazione

   -  *WSSec Timestamp*, il messaggio verrà arrichito di una
      informazione temporale (tipicamente utilizzato insieme alla firma
      del messaggio)

-  *Nel caso del protocollo REST*:

   -  *JWT Encrypt*: il messaggio JSON di risposta viene cifrato prima
      dell'invio.

   -  *JWT Signature*: il messaggio JSON di risposta viene firmato prima
      dell'invio.

   -  *XML Encrypt*: il messaggio XML di risposta viene cifrato prima
      dell'invio.

   -  *XML Signature*: il messaggio XML di risposta viene firmato prima
      dell'invio.

.. note::
    Si tenga presente che, nel caso di una fruizione, il ruolo del
    gateway si inverte diventando *Sender* nel caso della richiesta e
    *Receiver* nel caso della risposta. Gli schemi di sicurezza
    disponibili, nel caso della fruizione, rimangono quelli già
    descritti per Sender e Receiver.
