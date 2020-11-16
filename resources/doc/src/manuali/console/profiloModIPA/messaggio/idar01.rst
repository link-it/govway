.. _modipa_idar01:

[ID_AUTH_SOAP_01 / ID_AUTH_REST_01] Direct Trust con certificato X.509
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. note::
    La sigla che identifica il profilo di sicurezza messaggio varia a seconda se l'API sia di tipo REST, per cui la sigla corrisponde a *ID_AUTH_REST_01*, o SOAP dove viene utilizzata la sigla *ID_AUTH_SOAP_01*.

L'adozione di questo profilo consente, alla ricezione di un messaggio, di validare il certificato fornito dall'applicativo mittente, la porzione di messaggio firmata, la validità temporale nonché la corrispondenza del destinatario della comunicazione.

Nel processo di configurazione, per i servizi con questo profilo, la registrazione delle API prevede che nella sezione "ModIPA", elemento "Profilo Sicurezza Messaggio", venga selezionato il profilo "ID_AUTH_REST_01" per API REST o "ID_AUTH_SOAP_01" per API SOAP come indicato in :numref:`api_messaggio1_fig` e :numref:`api_messaggio1_soap_fig`.


  .. figure:: ../../_figure_console/modipa_api_messaggio1.png
    :scale: 50%
    :name: api_messaggio1_fig

    Profilo di sicurezza messaggio "ID_AUTH_REST_01" per l'API

  .. figure:: ../../_figure_console/modipa_api_messaggio1_soap.png
    :scale: 50%
    :name: api_messaggio1_soap_fig

    Profilo di sicurezza messaggio "ID_AUTH_SOAP_01" per l'API

Le voci 'Header HTTP del Token' (presente solamente su API di tipo REST) e 'Applicabilità' consentono di personalizzare l'header HTTP utilizzato e di indicare se il profilo di sicurezza verrà attuato sia sulla richiesta che sulla risposta. Maggiori informazioni vengono fornite nella sezione ':ref:`modipa_sicurezza_avanzate`'.

Nel contesto della configurazione della specifica operation/risorsa è presente anche la sezione "Profilo Sicurezza Messaggio" che consente di intervenire sul profilo di sicurezza messaggio in modo puntuale. È quindi possibile lasciare l'impostazione del profilo al valore già stabilito a livello della API, oppure decidere di ridefinirlo andando a fornire una configurazione specifica per la singola operation/risorsa come indicato in :numref:`api_messaggio1_fig`.


  .. figure:: ../../_figure_console/modipa_api_messaggio_risorsa.png
    :scale: 50%
    :name: modipa_api_messaggio_risorsa

    Profilo di sicurezza messaggio ridefinito per una risorsa dell'API

Il processo prosegue con alcune differenze in base al tipo di servizio che si vuole configurare.

.. toctree::
   :maxdepth: 2

    Passi per la configurazione di una fruizione <idar01_fruizione>
    Passi per la configurazione di una erogazione <idar01_erogazione>



























