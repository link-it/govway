Nuovo Profilo di Interoperabilità ModI PA
----------------------------------------------

Il profilo ModI PA consente di produrre le configurazioni dei servizi in conformità a quanto previsto dalla normativa tecnica presente nel documento *Linee Guida Modello Interoperabilità* (https://docs.italia.it/italia/piano-triennale-ict/lg-modellointeroperabilita-docs/it/bozza/) rilasciato da AGID nel 2018.

Tutti i nuovi concetti, introdotti dalla normativa tecnica, sono stati recepiti e gestiti nelle maschere di configurazione della govwayConsole, adottando il profilo ModI PA. I messaggi in ingresso o uscita dal Gateway vengono validati e arricchiti con le informazioni necessarie a rispettare i criteri di interoperabilità di ModI PA.

Il Modello di Interoperabilità mantiene sostanzialmente invariato il concetto di *dominio* di un'amministrazione rispetto a quanto prevedeva il precedente modello SPCoop. Resta quindi fondamentale individuare il perimetro d'azione delle interfacce dei servizi rispetto al sistema informativo dell'ente e i propri interlocutori. Il concetto di dominio si sposa perfettamente con i modelli di configurazione di GovWay dove è possibile attivare:

- *erogazioni di API*: richieste che provengono da amministrazioni esterne al dominio e sono dirette ad applicativi interni.
- *fruizioni di API*: richieste che provengono dagli applicativi interni del dominio e sono dirette verso altre amministrazioni.

Nel processo di configurazione, tramite la govwayConsole, sono inoltre tenuti in considerazione tutti gli aspetti previsti dalle Linee Guida:

- *Profili di Interazione*: definiscono la modalità con cui fruitore ed erogatore di un servizio interagiscono. Sono previsti i seguenti due profili:

    + *Bloccante*: il fruitore invia la richiesta è resta bloccato in attesa di ricevere la risposta, completa dei dati attesi, dall'erogatore
    + *Non Bloccante*: il fruitore non resta bloccato dopo aver inviato la richiesta, se non per ricevere una notifica di presa in carico. Per ottenere la risposta sarà necessario effettuare una distinta interazione, prevista nello scenario del servizio.

- *Sicurezza Canale*: gestione della sicurezza inerente il canale di comunicazione tra i domini fruitore ed erogatore. Vengono supportati i seguenti due profili:

    + *[IDAC01] Direct Trust Transport-Level Security*: comunicazione basata sul canale SSL dopo aver effettuato il trust del certificato X509 fornito dal dominio erogatore.
    + *[IDAC02] Direct Trust mutual Transport-Level Security*: comunicazione basata sul canale SSL dopo aver effettuato il trust dei certificati X509, del fruitore e dell'erogatore, nella modalità di mutua autenticazione.

- *Sicurezza Messaggio*: gestione della sicurezza attuata a livello di messaggio, inerente lo scambio di informazioni tra le applicazioni agli estremi del flusso di comunicazione. I profili di sicurezza previsti si distinguono per il caso SOAP e per quello REST:

    + *[IDAS01 o IDAR01] Direct Trust con certificato X.509 su SOAP o REST*: Tramite la validazione del certificato X509, inserito dall'applicazione mittente all'interno del token di sicurezza della richiesta, l'applicativo destinatario verifica la corrispondenza delle identità e la validità del messaggio, prima di procedere con la produzione della risposta.
    + *[IDAS02 o IDAR02]  Direct  Trust  con  certificato  X.509  su  SOAP o REST  con  unicità  del token/messaggio*: estensione del profilo precedente con l'aggiunta di un meccanismo di filtro che impedisce il processamento di un messaggio di richiesta duplicato.
    + *[IDAS03 o IDAR03] Integrità del payload del messaggio SOAP o REST*: profilo che estende i profili precedenti aggiungendo la gestione della firma del payload come verifica di integrità del messaggio ricevuto.


