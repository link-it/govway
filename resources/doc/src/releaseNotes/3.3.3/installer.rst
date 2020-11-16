Miglioramenti all'Installer
---------------------------

Sono stati apportati i seguenti miglioramenti all'installer binario:

- È stato aggiunto il supporto per la nuova versione dell'application server 'WildFly' 21.x.

- Le patch SQL che modificano il tipo di una colonna da VARCHAR a CLOB, tramite il comando ALTER, sono state riviste al fine di utilizzare una versione più efficente per i tipi di database che lo consentono (https://github.com/link-it/govway/issues/58).

- Modificato tipo della colonna 'value' della tabella 'tracce_ext_protocol_info' al fine di poter creare un indice su tale colonna. L'indice consente di migliorare le performance come descritto nell'issue https://github.com/link-it/govway/issues/60.

- Lo script SQL generato per MySQL, possedeva un vincolo 'unique' non instanziabile su mysql: "ERROR 1071 (42000): Specified key was too long; max key length is 3072" (https://github.com/link-it/govway/issues/66).

- L'installer genera adesso un archivio govway.ear contenente nel file application.xml i 'resource-ref' necessari all'A.S. per effettuare un shutdown corretto dei datasource.
