Bug Fix
-------

Sono stati risolti i seguenti bug:

- una richiesta POST senza contenuto e senza ContentType veniva erroneamente riconosciuta come invalida dal filtro CORS e di conseguenza non veniva generato l'header http 'Access-Control-Allow-Origin' nella risposta;

- corretto errore di parser che si presentava con alcune SOAPEnvelope: 

	"Invalid content (</SOAP-ENV:Envelope/>): The markup in the document preceding the root element must be well-formed.".

Per la console di monitoraggio sono stati risolti i seguenti bug:

- utilizzando il database SQLServer la ricerca base nello storico delle transazioni produceva il seguente errore SQL: 

	"ERROR <20-10-2022 14:03:03.969> org.openspcoop2.core.commons.search.dao.jdbc.JDBCAccordoServizioParteSpecificaServiceSearch.findAll(348): Ambiguous column name 'tipo_soggetto'."

- era erroneamente possibile cancellare testo nel contenuto di un messaggio visualizzato nel dettaglio di una transazione;

- sono stati corrette le seguenti anomalie relative alla funzionalità di export CSV delle configurazioni:

	- l'export produceva risultati non deterministici e/o incompleti;

	- l'username associato ad una erogazione tramite servizio IntegrationManager/MessageBox non veniva riportato nel report.
