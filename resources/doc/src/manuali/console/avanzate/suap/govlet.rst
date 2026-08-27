.. _configAvanzataSsuGovlet:

GovLet per la configurazione delle erogazioni
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Per facilitare la configurazione delle erogazioni previste dagli e-service del SSU sono rese disponibili delle GovLet, ossia archivi di configurazione che, una volta importati tramite la sezione *Configurazione > Importa* della govwayConsole (:ref:`importa`), richiedono all'operatore i soli dati specifici dell'installazione e creano automaticamente API, erogazioni, applicativi e policy necessari.

Le GovLet sono organizzate per attore: ogni archivio configura tutti gli e-service erogati da un dato attore verso gli altri. Per ciascun e-service vengono create due erogazioni:

- l'erogazione **esposta all'esterno**, che riceve la richiesta dal chiamante e firma la risposta con il token *Agid-JWT-Signature* (pattern di sicurezza *INTEGRITY_REST_02*);
- l'erogazione **interna**, con suffisso *-IN*, che valida il voucher PDND e il token *Agid-JWT-Signature* della richiesta, effettua la validazione dei contenuti rispetto all'interfaccia OpenAPI e inoltra la richiesta all'applicativo di backend.

.. note::
    La configurazione a cascata è ciò che consente di firmare anche le risposte di errore. Per ovvi motivi di affidabilità della piattaforma, GovWay non produce token di integrity per messaggi scartati, come ad esempio i messaggi privi di token PDND. Il risultato atteso per i servizi del SUAP può essere comunque ottenuto con un'ulteriore erogazione in cascata, ma è una soluzione sconsigliata per un'installazione in produzione, obbligando GovWay a produrre token firmati per richieste non valide che potrebbero arrivare da chiunque.

Ogni archivio contiene inoltre la token policy di validazione del voucher PDND e la registrazione del plugin di adeguamento del formato di errori descritto nella sezione :ref:`configAvanzataSua`, già attivato su tutte le erogazioni interne.

GovLet SUAP
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Le GovLet SUAP sono disponibili in due versioni, ciascuna allineata a una diversa versione approvata delle `specifiche tecniche DPR 160/2010 <https://github.com/AgID/specifiche-tecniche-DPR-160-2010>`_. La versione della specifica determina anche la versione delle API e delle erogazioni create dalla GovLet.

.. list-table::
   :header-rows: 1
   :widths: 20 25 55

   * - Versione
     - Specifica di riferimento
     - Archivi
   * - v1
     - `approved01 <https://github.com/AgID/specifiche-tecniche-DPR-160-2010/tree/approved01/openAPI>`_
     - `protocolli/modipa/example/config/SUAP/v1 <https://github.com/link-it/govway/tree/3.4.x/protocolli/modipa/example/config/SUAP/v1>`_
   * - v2
     - `approved02 <https://github.com/AgID/specifiche-tecniche-DPR-160-2010/tree/approved02/openAPI>`_
     - `protocolli/modipa/example/config/SUAP/v2 <https://github.com/link-it/govway/tree/3.4.x/protocolli/modipa/example/config/SUAP/v2>`_

Gli e-service configurati da ciascun archivio sono i seguenti:

.. list-table::
   :header-rows: 1
   :widths: 30 35 35

   * - Archivio
     - e-service (v1)
     - e-service (v2)
   * - GovWay_SUAP-BO-to-Test
     - BO-to-ET, BO-to-FO, BO-to-RI
     - BO-to-ET, BO-to-FO, BO-to-RI
   * - GovWay_SUAP-ET-to-Test
     - ET-to-BO
     - ET-to-BO, ET-to-ET
   * - GovWay_SUAP-FO-to-Test
     - FO-to-BO, FO-to-CU
     - FO-to-BO, FO-to-CU
   * - GovWay_SUAP-RI-to-Test
     - RI-to-BO
     - RI-to-BO

Rispetto alla v1, la v2 recepisce le nuove operation introdotte da *approved02* sugli e-service già presenti e aggiunge l'e-service **ET-to-ET**, che gestisce il coinvolgimento ricorsivo tra Enti Terzi (endoprocedimenti).

.. note::
    Le interfacce OpenAPI incluse negli archivi contengono alcune correzioni rispetto a quelle pubblicate da AgID, necessarie affinché la validazione dei contenuti non scarti richieste conformi ai flussi descritti dalle specifiche.

GovLet SUE
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Le GovLet SUE hanno la stessa struttura e lo stesso contesto di utilizzo di quelle SUAP e si riferiscono agli e-service descritti nell'`Allegato Tecnico SUE <https://github.com/AgID/SUE-allegato-tecnico>`_, le cui interfacce OpenAPI sono pubblicate nella cartella `OpenAPI <https://github.com/AgID/SUE-allegato-tecnico/tree/main/OpenAPI>`_ del medesimo repository. Gli archivi sono disponibili in `protocolli/modipa/example/config/SUE <https://github.com/link-it/govway/tree/3.4.x/protocolli/modipa/example/config/SUE>`_.

.. list-table::
   :header-rows: 1
   :widths: 40 60

   * - Archivio
     - e-service
   * - GovWay_SUE-BO-to-Test
     - SUE-BO-to-ET, SUE-BO-to-FO, SUE-BO-to-RI
   * - GovWay_SUE-ET-to-Test
     - SUE-ET-to-BO
   * - GovWay_SUE-FO-to-Test
     - SUE-FO-to-BO
   * - GovWay_SUE-RI-to-Test
     - SUE-RI-to-BO

Dati richiesti durante l'importazione
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Tutte le GovLet, sia SUAP che SUE, richiedono i medesimi dati:

- **Soggetto**: selezionare il soggetto erogatore dei servizi, appartenente al profilo di interoperabilità "ModI".

- **URL di invocazione**: indicare la URL di invocazione esterna e quella interna. La URL interna viene utilizzata per l'invocazione a cascata tra l'erogazione esposta all'esterno e quella interna: si suggerisce di indirizzarla su un listener http dedicato, in ascolto su una porta di servizio distinta da quella utilizzata per le normali invocazioni provenienti dall'esterno.

- **Richiesta – Token Authorization**: indicare il path sul file system della chiave, in formato PEM, utilizzata per la validazione del token di autorizzazione. La chiave viene rilasciata dal kit di certificazione BBTS.

- **Richiesta – Token Agid-JWT-Signature**: indicare il path sul file system della chiave, in formato JWK, utilizzata per la validazione del token di integrità. Questa chiave è rilasciata dal kit di certificazione BBTS ed è disponibile anche su PDND.

- **Risposta – Token Agid-JWT-Signature**: indicare i path sul file system della chiave pubblica e della chiave privata utilizzate per la firma dei token di integrità in risposta. Oltre ai path, specificare anche il valore del KID, ottenuto registrando la chiave pubblica come chiave server sulla PDND. Come ClientId indicare infine l'identificativo dell'eService o dell'Ente erogatore.

- **Connettore**: indicare l'endpoint dell'applicativo di backend per ciascun e-service configurato dall'archivio.

.. note::
    Nella cartella `resources <https://github.com/link-it/govway/tree/3.4.x/protocolli/modipa/example/config/SUAP/resources>`_ sono fornite chiavi generate al momento della redazione della documentazione: verificare che siano aggiornate prima dell'utilizzo effettivo.
