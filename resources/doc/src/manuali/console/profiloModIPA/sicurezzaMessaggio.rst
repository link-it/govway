.. _modipa_sicurezzaMessaggio:

Sicurezza Messaggio
-------------------

Il pattern di sicurezza sul messaggio definisce le modalità di comunicazione dei messaggi tra componenti interne  ai domini delle entità coinvolte. Tali pattern sono distinti per il caso SOAP e per quello REST:

- *[ID_AUTH_SOAP_01 o ID_AUTH_REST_01] Direct Trust con certificato X.509 su SOAP o REST*: Tramite la validazione del certificato X509, inserito dall'applicazione mittente nel token di sicurezza, l'applicativo destinatario verifica la corrispondenza delle identità e la validità del messaggio, prima di procedere con il processamento del messaggio.

- *[ID_AUTH_SOAP_02 o ID_AUTH_REST_02]  Direct  Trust  con  certificato  X.509  su  SOAP o REST  con  unicità  del messaggio/token*: estensione del pattern precedente con l'aggiunta di un meccanismo di filtro che impedisce il processamento di un messaggio duplicato.

- *[INTEGRITY_SOAP_01 o INTEGRITY_REST_01] Integrità del payload del messaggio SOAP o REST*: pattern che estende i precedenti aggiungendo la gestione della firma del payload come verifica di integrità del messaggio ricevuto.

Le applicazioni di un dominio interno o esterno, descritte negli scenari del Modello di Interoperabilità, vengono rappresentate in GovWay tramite la registrazione di Applicativi come entità di configurazione. In accordo al modello di GovWay, ciascun applicativo è associato al soggetto di riferimento che, nell'ottica ModI, rappresenta il dominio di appartenenza.

Per quanto concerne le fruizioni, le richieste che provengono dagli applicativi interni del dominio e sono dirette verso altre amministrazioni vengono arricchite del token di sicurezza 'ModI' associato all'operazione invocata. Gli applicativi vengono identificati attraverso una delle modalità di autenticazione previste da GovWay (vedi sez. :ref:`apiGwAutenticazione`) ed una volta identificato viene utilizzato il certificato X509 associatogli in fase di registrazione da utilizzare per effettuare la firma del token di sicurezza 'ModI' (:numref:`FruizioneModIPA`).

   .. figure:: ../_figure_console/FruizioneModIPA.jpg
    :scale: 70%
    :align: center
    :name: FruizioneModIPA

    Fruizione con Profilo di Interoperabilità 'ModI'

Nelle erogazioni invece, le richieste provengono da amministrazioni esterne al dominio e sono dirette ad applicativi interni. Prima di procedere con l'inoltro della richiesta verso il backend interno, GovWay valida il token di sicurezza ricevuto rispetto al pattern associato all'operazione invocata: verifica firma, validazione temporale, filtro duplicati, verifica integrità del messaggio ... (:numref:`ErogazioneModIPA`)

   .. figure:: ../_figure_console/ErogazioneModIPA.jpg
    :scale: 70%
    :align: center
    :name: ErogazioneModIPA

    Erogazione con Profilo di Interoperabilità 'ModI'


Vediamo nelle sezioni seguenti come si possono effettuare le configurazioni relative ai pattern di sicurezza messaggio.

.. toctree::
        :maxdepth: 2

	messaggio/passiPreliminari
        messaggio/idar01
        messaggio/idar02
        messaggio/idar03
        messaggio/pdnd
        messaggio/pdndIntegrity
	messaggio/requestDigest
	messaggio/informazioniUtente
	messaggio/avanzata/index
