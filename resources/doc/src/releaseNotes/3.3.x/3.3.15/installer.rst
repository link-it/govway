Miglioramenti all'Installer
---------------------------

Sono stati apportati i seguenti miglioramenti all'installer binario:

- aggiunti i seguenti tools command line presenti nella directory *dist/tools* prodotta dall'installer:

   - *govway-vault-cli* consente:

        - la cifratura o decifratura di informazioni o chiavi tramite una master key;
        
        - l'aggiornamento di una base dati esistente, consentendo di cifrare le informazioni confidenziali precedentemente salvate in chiaro o di aggiornarle attraverso l'utilizzo di una differente master key.

   - *govway-config-loader* dispone delle medesime funzionalità presenti nella sezione 'Importa' della console di gestione, che consentono di importare o eliminare le configurazioni memorizzate in un archivio ottenuto con la funzionalità 'Esporta'.

- i file relativi ai profili di interoperabilità (es. modipa_local.properties) presenti nella configurazione esterna (es. /etc/govway) non venivano configurati correttamente dall'installer se erano presenti archivi patch al suo interno;
	
- gli archivi 'patch' relativi ai profili di interoperabilità (es. openspcoop2_modipa-protocol-<version>.jar) non venivano configurati nel file di proprietà interno (es. modipa.properties) per contenere la proprietà relativa alla directory di configurazione esterna indicata nell'installer (es. 'org.openspcoop2.protocol.modipa.confDirectory=/etc/govway').
