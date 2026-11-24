.. _sicurezzaLivelloMessaggioWSSecuritySAML:

WS-Security SAML Token
~~~~~~~~~~~~~~~~~~~~~~

Lo schema gestisce un'asserzione SAML (Security Assertion Markup Language) inserita nell'header WS-Security. L'asserzione è emessa da un soggetto (*Issuer*) e attesta l'identità di un soggetto (*Subject*), eventualmente accompagnata da informazioni sull'autenticazione effettuata (*Authentication Statement*) e da attributi (*Attribute Statement*).

Il legame tra l'asserzione e il messaggio che la trasporta è determinato dal metodo di conferma del soggetto (*Subject Confirmation Method*):

- *Sender Vouches*: il mittente garantisce per il soggetto; il mittente firma il messaggio, includendo nella firma sia il Body sia l'asserzione;
- *Holder of Key*: il soggetto dimostra di possedere la chiave indicata nell'asserzione, firmando il messaggio con tale chiave;
- *Bearer*: chiunque presenti l'asserzione è considerato il soggetto; l'asserzione deve essere firmata dall'emittente per garantirne l'integrità.

Per i campi presenti nella sezione *Informazioni Generali* si rimanda alla sezione :ref:`sicurezzaLivelloMessaggioWSSecurityComuni`.

Ricezione (Receiver)
""""""""""""""""""""

Viene verificata l'asserzione SAML presente nell'header WS-Security (:numref:`wssReceiverSAMLFig`).

   .. figure:: ../../../_figure_console/SicurezzaMessaggioWSSReceiverSAML.png
    :scale: 70%
    :align: center
    :name: wssReceiverSAMLFig

    Ricezione: WS-Security SAML Token con asserzione firmata

**SAML Assertion**

- *Versione* (default: 2.0): versione SAML dell'asserzione attesa (2.0, 1.1 o 1.0);
- *Enveloped SAML Signature*: l'asserzione deve essere firmata dall'emittente; la firma viene verificata con il truststore indicato nella sezione *Signature*;
- *Subject Confirmation* (default: abilitato): vengono verificati i requisiti del metodo di conferma indicato nel campo *Tipo*:

  - *Sender Vouches* (default): deve essere presente una firma del messaggio che copre sia il Body sia l'asserzione SAML;
  - *Holder of Key*: deve essere presente una firma di una parte del messaggio effettuata con la chiave indicata nel Subject dell'asserzione;

- *External Signature* e *Timestamp*: visibili con *Enveloped SAML Signature* abilitato e *Subject Confirmation* disabilitato, richiedono rispettivamente la presenza di una firma del messaggio e di un elemento Timestamp, oltre all'asserzione firmata.

**Signature**

La sezione compare se è abilitata la verifica della firma dell'asserzione o del metodo di conferma.

- *TrustStore*: truststore utilizzato per la verifica delle firme, comprensivo delle eventuali verifiche di revoca tramite OCSP o CRL (vedi :ref:`sicurezzaLivelloMessaggioKeystore`).

Spedizione (Sender)
"""""""""""""""""""

Viene generata un'asserzione SAML e inserita nell'header WS-Security.

**SAML Assertion**

   .. figure:: ../../../_figure_console/SicurezzaMessaggioWSSSenderSAMLAssertion.png
    :scale: 70%
    :align: center
    :name: wssSenderSAMLAssertionFig

    Spedizione: sezioni Informazioni Generali e SAML Assertion


- *Versione* (default: 2.0): versione SAML dell'asserzione generata (2.0, 1.1 o 1.0); alcuni campi descritti di seguito sono disponibili solo per SAML 2.0;
- *Authentication Statement*: aggiunge all'asserzione le informazioni sull'autenticazione del soggetto, configurate nella sezione *SAML Authentication Statement*;
- *Attribute Statement*: aggiunge all'asserzione degli attributi del soggetto, configurati nella sezione *SAML Attribute Statement*;
- *Enveloped SAML Signature*: l'asserzione viene firmata, con la chiave indicata nella sezione *Signature*; è richiesta per i metodi di conferma *Holder of Key* e *Bearer*.

*Issuer*

- *Value*: identificativo dell'emittente dell'asserzione;
- *Name Qualifier* (SAML 2.0): dominio di sicurezza in cui è definito l'identificativo;
- *Format Personalizzato* e *Format* (SAML 2.0): formato dell'identificativo, selezionato tra i formati NameID previsti da SAML (es. 'unspecified', 'emailAddress', 'X509SubjectName', 'entity') oppure indicato liberamente.

*Conditions*

I campi sono visibili, insieme alla sezione *SAML Subject*, nella :numref:`wssSenderSAMLSubjectFig`.

- *NotBefore (minutes)* (default: 0) e *NotOnOrAfter (minutes)* (default: 60): inizio e fine del periodo di validità dell'asserzione, espressi come differenza in minuti rispetto all'istante di creazione;
- *AudienceURI*: destinatario a cui è rivolta l'asserzione.

**SAML Subject**

   .. figure:: ../../../_figure_console/SicurezzaMessaggioWSSSenderSAMLSubject.png
    :scale: 70%
    :align: center
    :name: wssSenderSAMLSubjectFig

    Spedizione: Conditions e SAML Subject con metodo di conferma Bearer


- *Value*: identificativo del soggetto;
- *Name Qualifier*: dominio di sicurezza in cui è definito l'identificativo;
- *Custom Format* e *Format*: formato dell'identificativo, selezionato tra i formati NameID previsti dalla versione SAML (default: 'unspecified') oppure indicato liberamente.

*Confirmation Method*

- *Method* (default: Sender Vouches): metodo di conferma del soggetto; per SAML 2.0 sono disponibili *Bearer*, *Holder of Key* e *Sender Vouches*, per SAML 1.x anche *Artifact* e *Identity*; con *Custom Method* il metodo viene indicato liberamente;
- *Timestamp* ed *External Signature* (metodo *Bearer* con *Enveloped SAML Signature* abilitato): aggiungono al messaggio rispettivamente un elemento Timestamp e una firma, configurata nella sezione *External Signature*.

Con i metodi *Sender Vouches* e *Holder of Key* il messaggio viene sempre firmato (Body e asserzione), utilizzando i parametri della sezione *Signature*; per *Holder of Key* la chiave del keystore indicato è anche quella referenziata nel Subject dell'asserzione.

*Confirmation Data*

- *Date* (default: *Default*): con *Default* il periodo di validità della conferma inizia all'istante di creazione e termina dopo 60 minuti; con *Minutes from Creation Time* inizio e fine sono indicati nei campi *NotBefore (minutes)* e *NotOnOrAfter (minutes)* come differenza in minuti rispetto all'istante di creazione;
- *Address*, *InResponseTo* e *Recipient* (SAML 2.0): indirizzo dell'entità che presenta l'asserzione, identificativo del messaggio a cui l'asserzione risponde e destinatario a cui l'asserzione può essere presentata.

**SAML Authentication Statement**

La sezione compare se è abilitato *Authentication Statement* (:numref:`wssSenderSAMLAuthnFig`).

   .. figure:: ../../../_figure_console/SicurezzaMessaggioWSSSenderSAMLAuthn.png
    :scale: 70%
    :align: center
    :name: wssSenderSAMLAuthnFig

    Spedizione: SAML Authentication Statement

- *Date* (default: *Default*): istante dell'autenticazione (*NotBefore*) e scadenza della sessione di autenticazione (*NotOnOrAfter*):

  - *Default*: autenticazione all'istante di creazione e scadenza dopo 60 minuti;
  - *Minutes from Creation Time*: valori espressi come differenza in minuti rispetto all'istante di creazione;
  - *Configurazione Manuale*: date indicate esplicitamente, ciascuna con il formato con cui è espressa (es. 'yyyy-MM-dd HH:mm:ss');

- *Custom Method* e *Method* (default: 'unspecified'): metodo con cui il soggetto si è autenticato (AuthnContextClassRef), selezionato tra quelli previsti dalla versione SAML (es. Password, PasswordProtectedTransport, X509, Smartcard) oppure indicato liberamente;
- *Declaration Reference (URI)* (SAML 2.0): riferimento alla dichiarazione del contesto di autenticazione;
- *Subject Locality*: indirizzo IP (*IP Address*) e nome DNS (*DNS Address*) del sistema da cui il soggetto si è autenticato.

**SAML Attribute Statement**

La sezione compare se è abilitato *Attribute Statement* (:numref:`wssSenderSAMLAttributeFig`).

   .. figure:: ../../../_figure_console/SicurezzaMessaggioWSSSenderSAMLAttribute.png
    :scale: 70%
    :align: center
    :name: wssSenderSAMLAttributeFig

    Spedizione: SAML Attribute Statement con due attributi

- *Numero di Attributi* (default: 1, al massimo 10): numero di attributi da inserire; per ciascun attributo compare una sottosezione *Attribute N.x* con i seguenti campi:

  - *Name Qualified* e *Name Simple*: nome dell'attributo in forma qualificata e semplice; per SAML 1.x sono entrambi obbligatori, per SAML 2.0 ne è richiesto almeno uno;
  - *Value(s)*: valore dell'attributo; più valori vengono separati dal carattere indicato nel campo *Separator* (default: ',');
  - *Custom Format* e *Format* (SAML 2.0): formato del nome dell'attributo ('unspecified', 'uri' o 'basic'), oppure indicato liberamente.

**Signature**

La sezione compare se è abilitata la firma dell'asserzione o se il metodo di conferma è *Sender Vouches* o *Holder of Key* (:numref:`wssSenderSAMLSignatureFig`, che riporta anche la sezione *Timestamp*).

   .. figure:: ../../../_figure_console/SicurezzaMessaggioWSSSenderSAMLSignature.png
    :scale: 70%
    :align: center
    :name: wssSenderSAMLSignatureFig

    Spedizione: sezioni Signature e Timestamp

- *Signature Algorithm*, *Digest Algorithm* e *Canonicalization Algorithm*: algoritmi utilizzati per la firma, con gli stessi default dello schema :ref:`sicurezzaLivelloMessaggioWSSecuritySignature`;
- *Key Identifier - Enveloped SAML Signature*: con *Send Key* (default: abilitato) il certificato di firma viene incluso nella firma dell'asserzione;
- *Key Identifier - External Signature* (metodi *Sender Vouches* e *Holder of Key*): modalità con cui viene referenziato il certificato nella firma del messaggio, con le opzioni descritte per lo schema :ref:`sicurezzaLivelloMessaggioWSSecuritySignature`;
- *KeyStore*: keystore contenente la chiave privata di firma (vedi :ref:`sicurezzaLivelloMessaggioKeystore`).

**Timestamp**

La sezione compare se è abilitata l'azione *Timestamp* (metodo *Bearer*); il campo *Time to Live (seconds)* è descritto nella sezione :ref:`sicurezzaLivelloMessaggioWSSecurityTimestamp`.

**External Signature**

La sezione compare se è abilitata *External Signature* (metodo *Bearer*).

- *Elementi da firmare*: elementi del messaggio da firmare, come per lo schema :ref:`sicurezzaLivelloMessaggioWSSecuritySignature`;
- *Key Identifier*: modalità con cui viene referenziato il certificato di firma.

La firma utilizza gli algoritmi e il keystore indicati nella sezione *Signature*.
