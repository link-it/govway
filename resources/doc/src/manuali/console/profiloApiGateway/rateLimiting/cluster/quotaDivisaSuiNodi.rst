.. _headerGWRateLimitingCluster_quotaDivisaSuiNodi:

Sincronizzazione Locale con quota divisa sui nodi
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Il gestore delle policy di rate limiting descritto in questa sezione rappresenta una prima soluzione all'attuazione di un controllo di rate limiting su più nodi.

**Requisito**: la presenza di un bilanciamento del carico round robin dove le richieste devono essere distribuite in ordine tra i nodi.

Il bilanciamento ordinato tra nodi consente di attuare correttamente la policy di rate limiting semplicemente suddividendo la quota configurata per il numero di nodi attivi. 

Il gestore attivabile impostando la sincronizzazione '*Locale - quota divisa sui nodi*' attiva una registrazione automatica del nodo al suo avvio e una cancellazione durante lo shutdown in modo da consentire ad ogni nodo di conoscere l'effettivo numero di nodi attivi per poter suddividere la quota configurata correttamente (:numref:`configurazioneSincronizzazioneRateLimitingLocaleQuotaDivisa`).

.. figure:: ../../../_figure_console/ConfigurazioneSincronizzazioneRateLimitingLocaleQuotaDivisa.png
    :scale: 100%
    :align: center
    :name: configurazioneSincronizzazioneRateLimitingLocaleQuotaDivisa

    Sincronizzazione Locale con quota divisa sui nodi del cluster 

*Vantaggi*

L'attuazione del controllo di rate limiting possiede le medesime performance della sincronizzazione *Locale* attiva per default sul prodotto.

*Svantaggi*

1. Dipendenza dal Load Balancer

  La soluzione è banalmente attuabile solamente se sui nodi risiede un'unica API e il conteggio avviene esclusivamente per numero di richieste indipendentemente dal client richiedente o dall'operazione invocata. 

  La soluzione inizia a presentare difetti non appena vi sono più API, poichè il bilanciamento da configurare sul bilanciatore dovrà essere basato sul contesto dell'API invocata. La soluzione è comunque attuabile se il numero di API complessivo è limitato o comunque viene scelto di applicarla su un numero ristretto, sempre conteggiando esclusivamente per numero di richieste indipendentemente dal client chiamante.

  Inizia invece a diventare di difficile attuazione quando il conteggio deve tener conto del client richiedente (es. 10 richieste al minuto per ogni client) e quindi anche il bilanciamento dovrebbe farlo.

2. Header "\*-Remaining"

  La modalità prevede di suddividere la quota per il numero di nodi e ciò rende difficile calcolare il valore esatto del numero di richieste ancora effettuabili nella finestra temporale in corso. Nel resto di questa sezione verranno fornite indicazioni sulle tecniche di approssimazione dei valori ritornati negli header "\*-Remaining".

3. Altre metriche
  
  Questa tipologia è applicabile solamente con metriche che prevedono di contare il numero di richieste in una finestra temporale. Non è quindi applicabile per le metriche: "Numero Richieste Simultanee", "Occupazione Banda", "Tempo Medio Risposta" e "Tempo Complessivo Risposta".

*Header "\*-Remaining"*

Il valore del numero di richieste ancora effettuabili nella finestra temporale in corso verrà calcolato tramite la tecnica di approssimazione che consiste nel moltiplicare la quota rimasta su un nodo per il numero di nodi attivi.

Di seguito viene mostrato un flusso di richieste di esempio, supponendo di avere un cluster formato da 2 nodi, e una policy di rate limiting impostata con una metrica che prevede 11 richieste al minuto.

   ::

      Quota configurata: 11
      Nodi attivi: 2
      Quota effettiva = 11/2 = 5
      
      Invocazione 1 (nodo1): X-RateLimit-Remaining (4*2 = 8)
      Invocazione 2 (nodo2): X-RateLimit-Remaining (4*2 = 8)
      Invocazione 3 (nodo1): X-RateLimit-Remaining (3*2 = 6)
      Invocazione 4 (nodo2): X-RateLimit-Remaining (3*2 = 6)
      Invocazione 5 (nodo1): X-RateLimit-Remaining (2*2 = 4)
      Invocazione 6 (nodo2): X-RateLimit-Remaining (2*2 = 4)
      Invocazione 7 (nodo1): X-RateLimit-Remaining (1*2 = 2)
      Invocazione 8 (nodo2): X-RateLimit-Remaining (1*2 = 2)
      Invocazione 9 (nodo1): X-RateLimit-Remaining (0*2 = ['remaining.zeroValue' ? 0 : 1]) 
      Invocazione 10(nodo2): X-RateLimit-Remaining (0*2 = ['remaining.zeroValue' ? 0 : 1])
      Invocazione 11(nodo1): 409
      Invocazione 12(nodo2): 409

Vi sono alcuni aspetti dell'approssimazione che sono configurabili agendo sul file *<directory-lavoro>/govway_local.properties*

- Quota effettiva: come mostrato nell'esempio la quota configurata viene suddivisa sui nodi attivi. L'arrotondamento del valore ottenuto può essere configurato per difetto o per eccesso. Con un arrotondamento per difetto, se la divisione non consente di avere un numero maggiore di 0 viene associato il valore 1 ad ogni nodo. Per default è attivo un arrotondamento per difetto.

   ::

      # Quota effettiva
      org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.LOCAL_DIVIDED_BY_NODES.limit.roundingDown=true

- Header "\*-Limit": nell'header è possibile indicare la quota configurata o la quota effettiva (ottenuta dal calcolo per difetto o eccesso) moltipicata per il numero di nodi. Nell'esempio sopra riportato nel primo caso verrebbe ritornato il valore 11, mentre nel secondo 10. Per default viene ritornata la quota configurata.

   ::

      # Quota normalizzata
      org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.LOCAL_DIVIDED_BY_NODES.limit.normalizedQuota=false

- Remaning 0 o 1: è possibile configurare quale valore ritornare nel caso sia rimasta solamente 1 invocazione a disposizione (per default viene ritornato il valore 1):

	- se si torna 1, si permette di rispettare il contratto poichè ad esempio nel caso di 2 nodi, con quota N, alla N-1 esima invocazione il client si vede ricevere un remaining=0 e potrebbe pensare di non poter fare la N-esima invocazione che invece da contratto è disponibile. Lo svantaggio di questa soluzione è che il client riceverà un errore 409 senza mai aver ricevuto remaining=0.

	- se si torna 0, rispetto alla precedente soluzione il client può smettere di effettuare invocazioni non appena riceve un remaining=0 evitando quindi di ricevere un 409. Lo svantaggio di questa soluzione è che il client se rispetta l'header effettuerà N-1 invocazioni rispetto alle N previste da contratto.

   ::

      # Remaining zeroValue
      org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.LOCAL_DIVIDED_BY_NODES.remaining.zeroValue=false
