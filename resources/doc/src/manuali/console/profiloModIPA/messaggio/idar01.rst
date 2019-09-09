.. _modipa_idar01:

[IDAS01 / IDAR01] Direct Trust con certificato X.509
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. note::
    La sigla che identifica il profilo di sicurezza messaggio varia a seconda se l'API sia di tipo REST, per cui la sigla corrisponde a *IDAR01*, o SOAP dove viene utilizzata la sigla *IDAS01*.

L'adozione di questo profilo consente, alla ricezione di un messaggio, di validare il certificato fornito dall'applicativo mittente, la porzione di messaggio firmata, la validità temporale nonché la corrispondenza del destinatario della comunicazione.

Nel processo di configurazione, per i servizi con questo profilo, la registrazione delle API prevede che nella sezione "ModIPA", elemento "Profilo Sicurezza Messaggio", venga selezionato il profilo "IDAR01" (o IDAS01 per SOAP) come indicato in :numref:`api_messaggio1_fig`.


  .. figure:: ../../_figure_console/modipa_api_messaggio1.png
    :scale: 50%
    :name: api_messaggio1_fig

    Profilo di sicurezza messaggio IDAR01 per l'API

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



























