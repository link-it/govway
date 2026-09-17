.. _avanzate_generazione_claims:

Aggiunta di Claims nei Token
-----------------------------

In diverse funzionalità (:ref:`tokenNegoziazionePolicy` - :ref:`tokenNegoziazionePolicy_jwt`, :ref:`aa` - :ref:`aaRichiesta`, :ref:`modipa_sicurezza_avanzate_claims` ) è stata documentata la possibilità di aggiungere claim personalizzati nel payload JWT prodotto da GovWay indicandoli per riga nel formato 'nome=valore'. 

Tutti i valori definiti possono contenere parti dinamiche che verranno risolte a runtime dal Gateway (per maggiori dettagli :ref:`valoriDinamici`).

Le coppie di valori indicate consentono di aggiungere ulteriori claim nel payload JWT come tipo semplice stringa. Ad esempio definendo un valore come 'claimTest=valoreClaim' verrà effettuata la seguente aggiunta al payload JWT:

   ::

      {
         ...
         "claimTest": "valoreClaim"
      }

Un valore definito tramite una parte dinamica, comporta un errore a runtime, se tale risoluzione non è possibile. Ad esempio  definendo un valore come 'claimTest=${header:X-Example}', se poi l'header http 'X-Example' non esiste nella richiesta la transazione abortisce con errore. Per aggiungere il claim solamente se la risoluzione dinamica del valore viene effettuata con successo è possibile usare la forma opzionale '?{..}'. Ad esempio  definendo un valore come 'claimTest=?{header:X-Example}', se poi l'header http 'X-Example' non esiste nella richiesta, l'unico effetto è quello che non sarà aggiunto al JWT Payload il claim 'claimTest'.

La forma opzionale elimina il claim solamente se il valore risulta interamente non risolvibile. Se il placeholder opzionale costituisce una parte del valore, il claim viene comunque aggiunto con la restante parte: definendo un valore come 'claimTest=prefisso-?{header:X-Example}', in assenza dell'header http 'X-Example' il claim 'claimTest' verrà valorizzato con 'prefisso-'.

Fornendo un valore che inizia e termina con le parentesi graffe si definisce un oggetto json. Ad esempio definendo un valore come 'claimTest={"prova":"valoreProva", "prova2":"${header:X-Example}"}' verrà effettuata la seguente aggiunta al payload JWT:

   ::

      {
         ...
         "claimTest": {
           "prova":"valoreProva", 
           "prova2":"<ValorePrelevatoHeaderHTTPIndicato>"
         }
      }

Se il valore inizia e termina con le parentesi quadre si definisce invece un array json. Ad esempio definendo un valore come 'claimTest=["valoreProva", "valoreProva2", "${header:X-Example}"]' verrà effettuata la seguente aggiunta al payload JWT:

   ::

      {
         ...
         "claimTest": ["valoreProva", "valoreProva2", "zValorePrelevatoHeaderHTTPIndicato>"]
      }

Per definire tipi primitivi json (boolean,int,long,float,double) è necessario attuare un cast nella forma 'cast(<valore> as <tipoPrimitivo>)'. Ad esempio definendo dei valori come 'claimTest=cast(true as boolean)' e 'claimTest2=cast(${header:X-Example} as long)' verrà effettuata la seguente aggiunta al payload JWT (si suppone presente nella richiesta un header http 'X-Example' valorizzato con '678'):

   ::

      {
         ...
         "claimTest": true,
         "claimTest2": 678
      }

Per convertire una lista json di tipi primitivi in lista di stringhe è possibile attuare un cast nella forma 'cast(<valore> as string array)'. Ad esempio definendo dei valori come 'claimTest=cast([1,2,3] as string array)' verrà effettuata la seguente aggiunta al payload JWT:

   ::

      {
         ...
         "claimTest": ["1", "2", "3"]
      }

La forma opzionale '?{..}' è utilizzabile anche negli elenchi 'nome=valore' con cui vengono definiti i parametri e gli header HTTP di una richiesta prodotta dal Gateway: la sezione 'Dati Richiesta' di una policy di negoziazione del token (:ref:`tokenNegoziazionePolicy`) e la richiesta di attributi verso una Attribute Authority (:ref:`aaRichiesta`). Anche in tali elenchi, se la risoluzione dinamica del valore non è possibile, l'unico effetto è che il parametro o l'header HTTP non viene inserito nella richiesta, anzichè essere inserito con un valore vuoto. Le restanti modalità descritte in questa sezione riguardano invece solamente la generazione di claims in un payload JWT: il valore di un parametro o di un header HTTP viene sempre utilizzato come semplice testo.
