.. _modipa_sicurezza_avanzate_pdndConfAvanzata_api_raccoltaInfoClient:

Raccolta informazioni sul ClientId mittente
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

La raccolta di maggiori informazioni relative all'identificativo client presente nel payload dei token di sicurezza JWT viene effettuata, invocando le risorse *GET /clients/{clientId}* e *GET /organizations/{organizationId}*, se viene abilitata la proprietà '*org.openspcoop2.pdd.gestorePDND.clientInfo.enabled*' presente nel file "/etc/govway/govway_local.properties" come descritto nell'elenco puntato '*Erogazione: maggiori informazioni sul mittente*' della sezione :ref:`modipa_passiPreliminari_api_pdnd`. Per attivare la raccolta delle informazioni sul client, oltre all'abilitazione della proprietà è necessario che la token policy sia associata ad un repository su cui è stata attivata la verifica degli eventi descritta precedentemente.
