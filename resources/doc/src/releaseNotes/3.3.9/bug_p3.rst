.. _3.3.9.3_bug:

Bug Fix 3.3.9.p3
----------------

Sono stati risolti i seguenti bug:

- con interfacce OpenAPI complesse di grandi dimensioni la validazione dei contenuti utilizzando la libreria "swagger-request-validator" impiegava diversi secondi ad inizializzare lo schema, anche per richieste successive alla prima dove le informazioni vengono salvate in cache;

- sono state risolte le seguenti vulnerabilità relative ai jar di terza parte:

	- CVE-2021-37533: aggiornata libreria 'commons-net' alla versione 3.9.0;
	- CVE-2022-40150: aggiornata libreria 'jettison' alla versione 1.5.2.

Per la console di gestione sono stati risolti i seguenti bug:

- utilizzando il database SQLServer l'accesso alla pagina di configurazione di una erogazione o fruizione produceva il seguente errore SQL: "ERROR ... org.openspcoop2.core.mapping.DBMappingUtils._mappingErogazionePortaApplicativaList: Ambiguous column name 'descrizione'";

- la selezione della modalità 'interfaccia avanzata', tramite la voce presente nel menù in alto a destra, presentava le seguenti problematiche:

	- la modalità selezionata veniva erroneamente visualizzata anche accedendo al profilo dell'utenza, invece  dei criteri configurati in maniera persistente per quell'utente;
	  
	- anche se veniva selezionata la modalità 'interfaccia avanzata', la selezione di connettori differenti da http (jms, file, ...) non era disponibile se il profilo persistente associato all'utente era definito come 'interfaccia standard';

- la modifica del keystore di un applicativo ModI, nella sezione 'Sicurezza Messaggio', provocava un errore e nel file di log era presente la segnalazione: "Parametro [confSSLCredWizStep] Duplicato.";

- effettuando un export contenente la configurazione di un soggetto
  diverso dal soggetto di default dichiarato nell'installer, la
  successiva importazione dell'archivio in un'installazione in cui
  tale soggetto fosse stato definito come soggetto di default provocava
  una inconsistenza delle configurazioni, segnalata dalla console al
  momento del login.
