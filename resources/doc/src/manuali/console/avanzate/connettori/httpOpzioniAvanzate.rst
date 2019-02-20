.. _avanzate_connettori_httpOpzioniAvanzate:

Configurazione Http Avanzata
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Richiede accesso alla govwayConsole in modalità *avanzata*

Tramite questa sezione è possibile indicare sia quale modalità di
comunicazione (streaming o meno) deve essere utilizzata, sia se deve
avvenire una eventuale gestione dei redirect http.

   .. figure:: ../../_figure_console/OpzioniAvanzateHttp.jpg
    :scale: 100%
    :align: center
    :name: OpzioniAvanzateHttpFig

    Configurazione http avanzata

Facendo riferimento alla maschera raffigurata in :numref:`OpzioniAvanzateHttpFig` andiamo a descrivere
il significato dei parametri:

-  *Data Transfer Mode* tramite questa configurazione è possibile
   indicare se la comunicazione deve avvenire in modalità
   transfer-encoding-chunked (streaming) o con content length fisso.

   -  **Modalità Data Transfer** (default, content-length,
      transfer-encoding-chunked): indica il tipo di trasferimento dati;
      scegliendo la voce default verrà utilizzato il comportamento
      configurato a livello globale nel file govway.properties tramite
      le opzioni:

      -  org.openspcoop2.pdd.connettori.inoltroBuste.httpTransferLength

      -  org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.httpTransferLength

   -  **Chunk Length (Bytes)** (presente solamente se la modalità è
      transfer-encoding-chunked): indica la dimensione in bytes di ogni
      singolo http chunk.

-  *Redirect* tramite questa configurazione è possibile indicare se un
   eventuale redirect ritornato dal server contattato deve essere
   seguito o meno.

   -  **Gestione Redirect** (default, abilitato, disabilitato): consente
      di personalizzare il comportamento sul singolo connettore;
      scegliendo la voce default verrà utilizzato il comportamento
      configurato a livello globale nel file govway.properties tramite
      le opzioni:

      -  org.openspcoop2.pdd.connettori.inoltroBuste.followRedirects

      -  org.openspcoop2.pdd.connettori.consegnaContenutiApplicativi.followRedirects

   -  **Massimo Numero di Redirect** (presente solamente se la gestione
      redirect è abilitata): indica il massimo numero di redirect
      seguiti.
