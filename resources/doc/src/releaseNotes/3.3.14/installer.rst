Miglioramenti all'Installer
---------------------------

Sono stati apportati i seguenti miglioramenti all'installer binario:

- aggiunti script di svecchiamento delle tracce per tipo di database sqlserver;

- eliminata la generazione dell'archivio 'govwaySec' prodotto per default tra gli archivi generati per l'application server WildFly; l'archivio è comunque generabile abilitando l'opzione specifica disponibile in modalità avanzata;

- gli artefatti prodotti dall'installer in caso di scelta del profilo di interoperabiltà 'eDelivery' presentavano i seguenti errori:

	- nell'archivio govway.ear mancava il jar 'openspcoop2_as4-protocol_ecodexBackendStub_cxf.jar';
	- il datasource per wildfly 'domibus-ds.xml' non conteneva il nome jndi 'org.govway.datasource.domibus' atteso.
