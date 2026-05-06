.. _modipa_signalhub_pseudoanonimizzazione_verifica:

Verifica lato consumatore
---------------------------

Per ricostruire e verificare l'``objectId`` di un segnale a partire da un identificativo candidato, si riporta uno pseudocodice in linguaggio Java che illustra, passo per passo, le operazioni da eseguire.

.. code-block:: java
   :caption: Pseudocodice di verifica dell'objectId

   // === DATI IN INGRESSO ===
   String algorithm      = response.cryptoHashFunction();   // es. "SHA-256"
   String seed           = response.seed();                 // "Xzzh3pQqLp+VK7gG9NwR6w=="
                                                            // !! da trattare come dato opaco, NON va decodificato !!
   String originalId     = "RSSMRA80A01H501Z";              // identificativo candidato in chiaro
   String signalObjectId = signal.objectId();               // "LAa/aPrxlEvOKacO1KFhEJDVj+UqHXg30Ksb7kE1+z8="
                                                            // !! da trattare come dato opaco, NON va decodificato !!


   // === STEP 1 — Composizione dell'input dell'hash ===
   //
   // Si concatenano l'identificativo originale e il seed COME STRINGHE,
   // poi si convertono in byte UTF-8 in un'unica passata.
   //
   // IMPORTANTE: il seed NON viene decodificato Base64.
   // Si usano i byte ASCII della stringa così come sono.

   byte[] input = (originalId + seed).getBytes(StandardCharsets.UTF_8);


   // === STEP 2 — Calcolo del digest ===

   MessageDigest md = MessageDigest.getInstance(algorithm);
   byte[] digest = md.digest(input);


   // === STEP 3 — Encoding Base64 dell'output e confronto ===

   String candidate = Base64.getEncoder().encodeToString(digest);
   boolean match    = candidate.equals(signalObjectId);

   // Se match == true, l'identificativo originale fornito è effettivamente
   // il soggetto a cui il segnale fa riferimento.

Lo stesso codice in forma compatta:

.. code-block:: java
   :caption: Forma compatta production-ready

   public static boolean matches(String signalObjectId,
                                 String candidateOriginalId,
                                 String seed,
                                 String algorithm) throws NoSuchAlgorithmException {
       byte[] input  = (candidateOriginalId + seed).getBytes(StandardCharsets.UTF_8);
       byte[] digest = MessageDigest.getInstance(algorithm).digest(input);
       return Base64.getEncoder().encodeToString(digest).equals(signalObjectId);
   }


**Errore tipico da evitare**

L'errore più comune consiste nel decodificare il ``seed`` Base64 prima di passarlo alla funzione di hash. Si tratta di un'operazione **sbagliata**: il seed esposto da GovWay è una stringa testuale e i suoi caratteri Base64 vanno utilizzati così come sono, in quanto già parte integrante del materiale crittografico contrattualizzato con il consumatore.

.. code-block:: java
   :caption: Esempio di codice ERRATO da non utilizzare

   // ❌ SBAGLIATO: decodificare il seed prima di passarlo all'hash
   byte[] seedBytes = Base64.getDecoder().decode(seed);          // <-- NON FARLO
   byte[] input     = concat(originalId.getBytes(UTF_8), seedBytes);
   byte[] digest    = MessageDigest.getInstance("SHA-256").digest(input);
   String candidate = Base64.getEncoder().encodeToString(digest);

Codice di questo tipo produrrebbe un ``candidate`` differente dall'``objectId`` pubblicato nel segnale, facendo fallire la verifica anche per identificativi corretti. La regola mnemonica è semplice: ``seed`` e ``objectId`` si trattano sempre come stringhe opache; l'unica chiamata a ``Base64.getEncoder()`` avviene sull'output binario dell'hash e l'unico confronto è una ``equals`` fra stringhe.
