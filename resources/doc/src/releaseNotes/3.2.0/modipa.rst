Nuovo Profilo di Interoperabilità ModI PA
----------------------------------------------

La 3.2 è la prima versione di GovWay a supportare completamente il
profilo ModIPA, assicurando in maniera del tutto trasparente alle
applicazioni interne al dominio, la conformità delle API (sia in
fruzione che in erogazione) alle nuove *Linee Guida AGID di
Interoperabilità*
(https://docs.italia.it/italia/piano-triennale-ict/lg-modellointeroperabilita-docs/it/bozza/).

Il Modello di Interoperabilità di ModIPA mantiene sostanzialmente
invariato il concetto di *dominio* di un'amministrazione rispetto a
quanto prevedeva il precedente modello SPCoop, rendendo quindi le
modalità di configurazione dei profili previsti da ModiPA del tutto
analoghe a quelle già adottate per SPCoop.

Tramite la govwayConsole è quindi possibile gestire tutti gli aspetti
previsti dalle Linee Guida.

- *Profili di Interazione*: definiscono la modalità con cui interagiscono fruitore ed erogatore di una API. Sono supportati i due profili previsti in ModIPA:

  + *Bloccante*: il fruitore invia la richiesta e resta bloccato in
      attesa di ricevere la risposta dall'erogatore;

  + *Non Bloccante*: il fruitore non resta in attesa dopo aver inviato
    la richiesta, se non per ricevere una notifica di presa in
    carico. Per ottenere la risposta sarà poi necessario effettuare una
    distinta interazione, esplicitamente prevista dallo scenario del servizio.

- *Sicurezza Canale*: gestione della sicurezza inerente il canale di comunicazione tra i domini fruitore ed erogatore. Sono supportati i due profili previsti in ModIPA:

    + *[IDAC01] Direct Trust Transport-Level Security*: comunicazione basata sul canale SSL con trust del certificato X509 fornito dal dominio erogatore.
    + *[IDAC02] Direct Trust mutual Transport-Level Security*: comunicazione basata sul canale SSL con mutua autenticazione, tramite trust dei certificati X509 del fruitore e dell'erogatore.

- *Sicurezza Messaggio*: gestione della sicurezza a livello di messaggio, inerente lo scambio di informazioni tra le applicazioni agli estremi del flusso di comunicazione. I profili di sicurezza previsti si distinguono per il caso SOAP e per quello REST:

    + *[IDAS01 o IDAR01] Direct Trust con certificato X.509 su SOAP o REST*: Tramite la validazione del certificato X509, inserito dall'applicazione mittente all'interno del token di sicurezza della richiesta, l'applicativo destinatario verifica la corrispondenza delle identità e la validità del messaggio, prima di procedere con l'invio della risposta.
    + *[IDAS02 o IDAR02]  Direct  Trust  con  certificato  X.509  su  SOAP o REST  con  unicità  del token/messaggio*: estensione del profilo precedente con l'aggiunta di un meccanismo di filtro che impedisce il processamento di un messaggio di richiesta duplicato.
    + *[IDAS03 o IDAR03] Integrità del payload del messaggio SOAP o REST*: profilo che estende i profili precedenti aggiungendo la gestione della firma del payload come verifica di integrità del messaggio ricevuto.


