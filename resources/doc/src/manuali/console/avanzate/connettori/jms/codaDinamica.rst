.. _avanzate_connettori_jms_codaDinamica:

Nome della coda dinamico
************************

Il nome della queue/topic può essere definito, anche solo in parte, tramite i valori dinamici descritti nella sezione :ref:`valoriDinamici`, consentendo di pubblicare su code differenti in funzione della richiesta.

Se l'informazione è disponibile negli header http o nei parametri della url, non è necessaria alcuna ulteriore configurazione. Ad esempio indicando come nome:

   ::

      queue/ordini-${header:GovWay-Coda}

una richiesta con header 'GovWay-Coda: urgenti' viene pubblicata sulla coda 'queue/ordini-urgenti'.

Se invece l'informazione è presente nel contenuto della richiesta, deve essere estratta tramite una :ref:`trasformazioniRichiesta` con tipo di conversione 'Alimentazione Contesto (Freemarker Template)', che non modifica il messaggio ma consente di salvare nel contesto della richiesta un valore da riutilizzare successivamente. Ad esempio, per una richiesta json che contiene il campo 'coda':

   ::

      <#assign coda = jsonPath.read("$.coda")!/>
      <#assign tmpPutContext = context?api.put("jmsCoda", coda)!/>

o, per una richiesta SOAP che contiene l'elemento 'coda':

   ::

      <#assign coda = xPath.read("//*[local-name()='coda']/text()")!/>
      <#assign tmpPutContext = context?api.put("jmsCoda", coda)!/>

il nome della coda può quindi essere definito come:

   ::

      queue/ordini-${context:jmsCoda}

La queue/topic risolta deve essere registrata nel contesto JNDI. Se il broker richiede la definizione del binding tramite le proprietà del contesto JNDI (es. ActiveMQ), anche la proprietà può essere definita in modo dinamico:

   ::

      context-queue.queue/ordini-${context:jmsCoda} = ordini-${context:jmsCoda}

.. note::
   Il nome della coda viene determinato a partire da informazioni fornite dal client: si consiglia di definire nel nome una parte fissa (es. 'queue/ordini-') e di prevedere sul broker le sole code ammesse, in modo che il client non possa indirizzare risorse JMS diverse da quelle previste.
