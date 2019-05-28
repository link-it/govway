.. _valoriDinamici:

Valori dinamici
***************

Le regole di trasformazione possono avvalersi di un contesto di risorse, con valori aggiornati dinamicamente dal gateway, cui attingere per le trasformazioni da attuare. Tali risorse sono utilizzabili quando si procede con la definizione di una regola di trasformazione. Elenchiamo le risorse disponibili:

-   header.NAME: valore dell'header http, corrispondente all'identificativo NAME, della richiesta e/o della risposta.
-   query.NAME: valore di un parametro della url di invocazione, corrispondente all'identificativo NAME.
-   urlRegExp.EXPR: applicazione di un’espressione regolare, rappresentata dal valore EXPR, alla url di invocazione.
-   xPath.EXPR: applicazione di un'espressione XPath, rappresentata dal valore EXPR, al messaggio xml o in alternativa un'espressione JsonPath se si tratta di un messaggio Json.
-   transaction.id: l'identificativo UUID della transazione corrente.
-   date.FORMAT: la data di elaborazione del messaggio; il formato fornito deve essere conforme a quanto richiesto dalla classe java 'java.text.SimpleDateFormat' (es. ${date:yyyyMMdd_HHmmssSSS})
-   busta.FIELD: accesso alle informazioni proprie del profilo di interoperabilità utilizzato; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe 'org.openspcoop2.protocol.sdk.Busta' (ad es. per il mittente usare *busta.mittente*)
-   property.NAME: accesso alle proprietà contenute nella traccia (ad esempio l'identificativo SDI); Il valore 'NAME' indica il nome della proprietà da utilizzare.

L'utilizzo dei suddetti elementi, come placeholder all'interno di template, comporta l'automatica sostituzione con il valore attuale a runtime da parte del gateway.

La sintassi per accedere le proprietà dinamiche sopraelencate è differente in base allo specifico contesto di utilizzo. Se si tratta di un testo interpretato direttamente da GovWay le proprietà saranno direttamente accessibili utilizzando il seguente formato:

- ${header:NAME}
- ${query:NAME}
- ${xPath:EXPR}
- ${jsonPath:EXPR}
- ${urlRegExp:EXPR}
- ${transaction:id}
- ${date:FORMAT}
- ${busta:FIELD}
- ${property:NAME}

Nei casi in cui il testo della trasformazione è interpretato da framework esterni (ad esempio Freemarker) le proprietà vengono rese disponibili da Govway inizializzando una mappa contenente i valori come oggetti. In questo caso le chiavi della mappa sono le seguenti (tra parentesi sono indicati i tipi di dato corrispondenti):

- header (java.util.Properties)
- query (java.util.Properties)
- xPath (org.openspcoop2.pdd.core.dynamic.PatternExtractor)
- jsonPath (org.openspcoop2.pdd.core.dynamic.PatternExtractor)
- urlRegExp (org.openspcoop2.pdd.core.dynamic.URLRegExpExtractor)
- transactionId (java.lang.String)
- date (java.util.Date)
- busta (org.openspcoop2.protocol.sdk.Busta)
- property (java.util.Properties)












