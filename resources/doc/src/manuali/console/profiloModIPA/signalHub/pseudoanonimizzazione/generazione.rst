.. _modipa_signalhub_pseudoanonimizzazione_generazione:

Generazione delle informazioni crittografiche
-----------------------------------------------

Le informazioni crittografiche sono generate da GovWay all'atto della prima richiesta verso l'endpoint di pseudoanonimizzazione (descritto nelle sezioni :ref:`modipa_signalhub_configurazione_erogazione_rest` e :ref:`modipa_signalhub_configurazione_erogazione_soap`) e restano valide per tutto il periodo di rotazione configurato sull'erogazione. Sono composte da due elementi.

**Seed (seme)**

È generato come N byte casuali tramite generatore crittograficamente sicuro (``java.security.SecureRandom``), con N configurabile sull'erogazione (``16``, ``32`` o ``64`` byte) in funzione della tipologia di dato pseudoanonimizzato, secondo le raccomandazioni PDND (rispettivamente ≥16, ≥32, ≥64 caratteri). I byte casuali vengono successivamente codificati in Base64 e **la stringa Base64 risultante costituisce essa stessa il valore del seme** restituito dall'endpoint. Tale stringa va utilizzata dal consumatore così com'è, senza decodifica preventiva: i suoi caratteri concorrono come tali all'input della funzione di hash.

Le lunghezze attese della stringa Base64 esposta sono:

.. list-table::
   :widths: 30 30 40
   :header-rows: 1

   * - Dimensione configurata
     - Byte raw
     - Lunghezza stringa Base64 esposta
   * - 16
     - 16 byte
     - 24 caratteri
   * - 32
     - 32 byte
     - 44 caratteri
   * - 64
     - 64 byte
     - 88 caratteri

Tutte le configurazioni soddisfano la soglia minima PDND (rispettivamente ≥16, ≥32, ≥64 caratteri della stringa esposta) per la categoria di dato corrispondente.

**objectId**

È il risultato dell'applicazione della funzione di hash configurata sull'identificativo originale concatenato al seed, con encoding Base64 dell'output binario del digest. La stringa Base64 ottenuta costituisce il valore pubblicato sul Signal Hub come ``objectId``. Anche in questo caso il valore va trattato come dato opaco: il consumatore non deve decodificarlo per recuperare l'identificativo originale (operazione tra l'altro infattibile, trattandosi di una funzione di hash), ma deve confrontarlo per uguaglianza con il candidato ricalcolato.

**Encoding Base64 dei valori esposti**

Sia il ``seed`` sia l'``objectId`` sono esposti come stringhe Base64. La codifica è applicata ai byte binari prodotti rispettivamente dal generatore casuale (per il seed) e dalla funzione di hash (per l'``objectId``) per consentirne il trasporto all'interno di campi testuali (JSON, XML) senza alterarne il contenuto. Si tratta di una scelta puramente di rappresentazione, che non altera l'entropia né l'informazione del valore originale. Il consumatore deve trattare entrambi i valori come stringhe opache, senza decodifica preventiva.

**Algoritmo di hash**

L'algoritmo applicato è quello selezionato in fase di configurazione dell'erogazione, esposto sull'endpoint di pseudoanonimizzazione tramite il campo ``cryptoHashFunction``. I valori supportati sono allineati alla lista raccomandata dal manuale PDND e descritti nella sezione :doc:`../panoramica`.

**Composizione dell'input**

L'input passato alla funzione di hash è la concatenazione dell'identificativo originale e del seed, nell'ordine ``${message}${salt}`` (ovvero ``objectId_originale || seed``). La concatenazione avviene tra le due rappresentazioni testuali, i cui byte UTF-8 costituiscono l'input binario passato al digest. L'ordine di concatenazione è personalizzabile tramite la proprietà ``org.openspcoop2.protocol.modipa.signalHub.hash.composition`` documentata nella sezione :ref:`modipa_signalhub_properties`.
