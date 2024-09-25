.. _3.3.15.1_bug:

Bug Fix 3.3.15.p1
------------------

Sono state risolte le seguenti vulnerabilità relative ai jar di terza parte:

- CVE-2024-38808, CVE-2024-38809: aggiornata libreria 'org.springframework:\*' alla versione 5.3.39;

- CVE-2024-45801 aggiornata libreria 'org.webjars:swagger-ui' alla versione 4.19.1.

Sono stati risolti i seguenti bug:

- (https://github.com/link-it/govway/issues/170) abilitando la validazione degli header in una Token Policy di Validazione e inserendo i valori attesi solo per i claim 'typ' e 'alg', si otteneva uno dei seguenti errori inattesi: 

	- "JWT header validation failed; null"
	- "JWT header validation failed; Expected claim 'cty' not found"

- (https://github.com/link-it/govway/issues/171) utilizzando il profilo 'Fatturazione Elettronica' su Tomcat, durante la validazione di una richiesta di notifica di decorrenza termini, nei log diagnostici si manifestava la seguente anomalia:
	
	- "Eccezione INFO con codice [GOVWAY-5] - EccezioneValidazioneProtocollo: Traccia di una precedente fattura inviata, con identificativo SDI [xxx], non rilevata: Errore durante la ricerca del datasource..."

Inoltre, sono state aggiunte utility per trattare gli attributi mustUnderstand e actor attraverso gli elementi request e response accessibili tramite trasformazioni.

Per la console di gestione sono stati risolti i seguenti bug:

- quando si navigava in liste interne a una singola erogazione o fruizione (ad esempio, gli applicativi autorizzati nel controllo degli accessi), il passaggio alla pagina successiva veniva erroneamente mantenuto anche quando si accedeva a una lista di un'altra erogazione o fruizione comportando una visualizzazione scorretta: nella seconda pagina potevano non esserci dati e non era più possibile tornare indietro alla pagina precedente;

- la maschera di caricamento di un certificato in un applicativo o in un soggetto è stata rivista per rendere più chiaro cosa comporta disabilitare la verifica del certificato.

Per la console di monitoraggio sono stati risolti i seguenti bug:

- nella distribuzione statistica a 3 dimensioni, personalizzata per esito, non venivano incluse le transazioni gestite con successo;

- esaminando i dettagli del messaggio di risposta presenti in una transazione fallita per indisponibilità del backend, veniva erroneamente indicata una dimensione di una ipotetica risposta ricevuta, che non può esistere a causa del fallimento della connessione al backend.

Infine è stata corretta una anomalia presente all'interno del tool command line 'govway-vault-cli' che ne impediva il funzionamento su database oracle.
