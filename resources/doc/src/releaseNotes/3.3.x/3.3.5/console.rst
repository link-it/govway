Miglioramenti alla Console di Gestione
-------------------------------------------------------

Sono stati apportati i seguenti miglioramenti alla console di gestione:

- nei filtri di ricerca delle Erogazioni, Fruizioni e Applicativi è stata aggiunta una sezione dedicata ai dati sui connettori tramite la quale è possibile filtrare per tipo di connettore, endpoint, token policy e keystore (solo in https);
	
- nei filtri di ricerca delle Erogazioni, Fruizioni, Applicativi e Soggetti  è stata aggiunta una sezione dedicata alle proprietà che consente di filtrare selezionando una proprietà tra quelle configurate e/o indicandone un valore;

- il criterio di ricerca, nelle Erogazioni e Fruizioni, che consente di filtrare per nome API e soggetto erogatore è stato disaccoppiato in modo da poter effettuare una ricerca composta su entrambi i criteri;

- nei connettori multipli di una erogazione è adesso possibile effettuare ricerche usando come criterio il nome, un filtro e i dati del connettore;

- tra i criteri di ricerca, negli Applicativi e nei Soggetti, è adesso possibile indicare oltre al tipo di credenziale (https/http-basic/principal) anche il valore stesso della credenziale (es. CN del certificato X.509);

- la maschera di gestione del controllo degli accessi di una Erogazione o Fruizione è stata ottimizzata in modo da espandere solamente le sezioni per cui è stata abilitata una funzionalità;

- aggiunto al widget che consente di caricare un file sulla console la possibilità di rimuovere tale scelta;

- tra le informazioni visualizzate per il certificato associato ad un soggetto o ad un applicativo è adesso presente anche il serial number in formato Hex;

- nella maschera di dettaglio di una erogazione o fruizione, in presenza di un connettore di lunghezza superiore ai 150 caratteri viene visualizzata una informazione troncata contenente il suffisso ' ...'; lo stesso accorgimento è stato adottato nelle liste degli header HTTP e dei parametri della URL configurabili nelle trasformazioni.
	
