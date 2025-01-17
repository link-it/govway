.. _valoriDinamici:

Valori dinamici
***************

Le regole di trasformazione possono avvalersi di un contesto di risorse, con valori aggiornati dinamicamente dal gateway, cui attingere per le trasformazioni da attuare. Tali risorse sono utilizzabili quando si procede con la definizione di una regola di trasformazione. Elenchiamo le risorse disponibili:

-   *header:NAME* : valore dell'header http, corrispondente all'identificativo NAME, della richiesta.
-   *query:NAME* : valore di un parametro della url di invocazione, corrispondente all'identificativo NAME.
-   *form:NAME* : valore di un parametro della form, corrispondente all'identificativo NAME.
-   *urlRegExp:EXPR* : applicazione di un’espressione regolare, rappresentata dal valore EXPR, alla url di invocazione (l'espressione deve avere un match con l'intera url).
-   *xPath:EXPR* : applicazione di un'espressione XPath, rappresentata dal valore EXPR, alla richiesta xml (o soap).
-   *jsonPath:EXPR* : applicazione di un'espressione jsonPath, rappresentata dal valore EXPR, alla richiesta json.
-   *transaction:id* : l'identificativo UUID della transazione corrente.
-   *date:FORMAT* : la data di elaborazione del messaggio; il formato fornito deve essere conforme a quanto richiesto dalla classe java 'java.text.SimpleDateFormat' (es. ${date:yyyyMMdd_HHmmssSSS})
-   *busta:FIELD* : accesso alle informazioni proprie del profilo di interoperabilità utilizzato; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe 'org.openspcoop2.protocol.sdk.Busta' (ad es. per il mittente usare *busta.mittente*)
-   *property:NAME*: accesso alle proprietà contenute nella traccia (ad esempio l'identificativo SDI); Il valore 'NAME' indica il nome della proprietà da utilizzare.
-   *tokenInfo:FIELD* : accesso ai claim di un token precedentemente validato; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe 'org.openspcoop2.pdd.core.token.InformazioniToken' (es. per ottenere il valore del claim 'sub' usare ${tokenInfo:sub})
-   *tokenClient:FIELD* : identità dell'applicativo client identificato tramite il clientId presente nel token; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe 'org.openspcoop2.core.id.IDServizioApplicativo' (es. per ottenere il nome dell'applicativo usare ${tokenClient:nome})
-   *aa:FIELD* : consente di accedere agli attributi recuperati tramite Attribute Authority; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe 'org.openspcoop2.pdd.core.token.attribute_authority.InformazioniAttributi' (es. per ottenere il valore dell'attributo 'attr1' usare ${aa:attributes[attr1]}, se configurata solamente 1 A.A., altrimenti usare ${aa:attributes[nomeAttributeAuthority][attr1]} )
-   *transportContext:FIELD* : accesso ai dati della richiesta http; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe 'org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext' (es. per il principal usare ${transportContext:credential.principal})
-   *securityToken:FIELD* : permette di accedere alle informazioni relative ai certificati ed ai security token presenti nella richiesta; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe 'org.openspcoop2.protocol.sdk.SecurityToken' (es. per accedere al CN del certificato presente nel token ModI 'Authorization' usare ${securityToken:authorization.certificate.subject.info(CN)})
-   *integration:FIELD* : permette di accedere ai claim di un token di integrazione; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe 'org.openspcoop2.pdd.core.dynamic.InformazioniIntegrazione' (es. per ottenere il valore del claim 'claimCustom' usare ${integration:info[claimCustom]}). Maggiori informazioni sulla funzionalità sono disponibili nella sezione ':ref:`integrazioneTokenJson`'.
-   *config:NAME* : accesso alle proprietà configurate per l'API; il valore 'NAME' indica la proprietà desiderata
-   *clientApplicationConfig:NAME* : accesso alle proprietà configurate nell'applicativo fruitore; il valore 'NAME' indica la proprietà desiderata
-   *clientOrganizationConfig:NAME* : accesso alle proprietà configurate nel soggetto fruitore; il valore 'NAME' indica la proprietà desiderata
-   *providerOrganizationConfig:NAME* : accesso alle proprietà configurate nel soggetto erogatore; il valore 'NAME' indica la proprietà desiderata
-   *tokenClientApplicationConfig:NAME* : permette di accedere alla proprietà, configurata nell'applicativo client identificato tramite il clientId presente nel token, con nome 'NAME'
-   *tokenClientOrganizationConfig:NAME* : permette di accedere alla proprietà, configurata nel soggetto proprietario dell'applicativo client identificato tramite il clientId presente nel token, con nome 'NAME'
-   *dynamicConfig:FIELD* : permette di accedere alle proprietà degli attori coinvolti nella richiesta (api, applicativi, soggetti); il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe 'org.openspcoop2.pdd.core.dynamic.DynamicConfig'; maggiori informazioni sulla funzionalità sono disponibili nella sezione ':ref:`avanzate_dynamic_config`'.
-   *request:FIELD* : permette di accedere al contenuto della richiesta; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe 'org.openspcoop2.pdd.core.dynamic.ContentReader' (es. per ottenere il digest dell'attachment usare ${request:part.attachmentByIndex(0).contentBase64Digest(SHA-256)})
-   *system:NAME* : valore associato alla proprietà di sistema, indicata nella configurazione generale, con nome 'NAME'
-   *env:NAME* : valore associato alla variabile di sistema con nome 'NAME'
-   *java:NAME* : valore associato alla variabile java con nome 'NAME'
-   *envj:NAME* : valore associato alla variabile di sistema o java con nome 'NAME'; la variabile viene cercata prima come variabile di sistema e, se non presente, come variabile della jvm

Per le risposte sono inoltre disponibili anche le seguenti risorse:

-   headerResponse.NAME: valore dell'header http, corrispondente all'identificativo NAME, della risposta.
-   xPathResponse.EXPR: applicazione di un'espressione XPath, rappresentata dal valore EXPR, alla risposta xml (o soap).
-   jsonPathResponse.EXPR: applicazione di un'espressione jsonPath, rappresentata dal valore EXPR, alla risposta json.
-   dateResponse.FORMAT: la data di elaborazione della risposta; il formato fornito deve essere conforme a quanto richiesto dalla classe java 'java.text.SimpleDateFormat' (es. ${date:yyyyMMdd_HHmmssSSS})
-   *integrationResponse:FIELD* : permette di accedere ai claim di un token di integrazione; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe 'org.openspcoop2.pdd.core.dynamic.InformazioniIntegrazione' (es. per ottenere il valore del claim 'claimCustom' usare ${integrationResponse:info[claimCustom]}). Maggiori informazioni sulla funzionalità sono disponibili nella sezione ':ref:`integrazioneTokenJson`'.
-   *response:FIELD* : permette di accedere al contenuto della risposta; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe 'org.openspcoop2.pdd.core.dynamic.ContentReader' (es. per ottenere il digest dell'attachment usare ${response:part.attachmentByIndex(0).contentBase64Digest(SHA-256)})

L'utilizzo dei suddetti elementi, come placeholder all'interno di template, comporta l'automatica sostituzione con il valore attuale a runtime da parte del gateway.

La sintassi per accedere le proprietà dinamiche sopraelencate è differente in base allo specifico contesto di utilizzo. Se si tratta di un testo interpretato direttamente da GovWay le proprietà saranno direttamente accessibili utilizzando il seguente formato:

- ${header:NAME} o ${headerResponse:NAME}
- ${query:NAME}
- ${form:NAME}
- ${xPath:EXPR} o ${xPathResponse:EXPR}
- ${jsonPath:EXPR} o ${jsonPathResponse:EXPR}
- ${urlRegExp:EXPR}
- ${transaction:id}
- ${date:FORMAT} o ${dateResponse:FORMAT}
- ${busta:FIELD}
- ${property:NAME}
- ${tokenInfo:FIELD}
- ${tokenClient:FIELD}
- ${aa:FIELD}
- ${transportContext:FIELD}
- ${securityToken:FIELD}
- ${integration:FIELD} o ${integrationResponse:FIELD}
- ${config:NAME}
- ${clientApplicationConfig:NAME}
- ${clientOrganizationConfig:NAME}
- ${providerOrganizationConfig:NAME}
- ${tokenClientApplicationConfig:NAME}
- ${tokenClientOrganizationConfig:NAME}
- ${dynamicConfig:FIELD}
- ${request:FIELD} o ${response:FIELD}
- ${system:NAME}
- ${env:NAME}
- ${java:NAME}
- ${envj:NAME}

Nei casi in cui il testo della trasformazione è interpretato da framework esterni (quali Freemarker o Velocity) le proprietà vengono rese disponibili da Govway inizializzando una mappa contenente i valori come oggetti. In questo caso le chiavi della mappa sono le seguenti (tra parentesi sono indicati i tipi di dato corrispondenti):

- header o headerResponse (java.util.Map<String, String>); in caso di molteplici header con stesso nome è disponibile la variabile headerValues o headerResponseValues (java.util.Map<String, List<String>>)
- query (java.util.Map<String, String>); in caso di molteplici parametri con stesso nome è disponibile la variabile queryValues (java.util.Map<String, List<String>>)
- form (java.util.Map<String, String>); in caso di molteplici parametri con stesso nome è disponibile la variabile formValues (java.util.Map<String, List<String>>)
- xPath o xPathResponse (org.openspcoop2.pdd.core.dynamic.PatternExtractor)
- jsonPath o jsonPathResponse (org.openspcoop2.pdd.core.dynamic.PatternExtractor)
- urlRegExp (org.openspcoop2.pdd.core.dynamic.URLRegExpExtractor)
- transactionId (java.lang.String)
- date (java.util.Date)
- busta (org.openspcoop2.protocol.sdk.Busta)
- property (java.util.Map<String, String>)
- tokenInfo (org.openspcoop2.pdd.core.token.InformazioniToken)
- tokenClient (org.openspcoop2.core.id.IDServizioApplicativo)
- aa (org.openspcoop2.pdd.core.token.attribute_authority.InformazioniAttributi)
- transportContext (org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext)
- securityToken (org.openspcoop2.protocol.sdk.SecurityToken)
- integration o integrationResponse (org.openspcoop2.pdd.core.dynamic.InformazioniIntegrazione)
- config (java.util.Map<String, String>)
- clientApplicationConfig (java.util.Map<String, String>)
- clientOrganizationConfig (java.util.Map<String, String>)
- providerOrganizationConfig (java.util.Map<String, String>)
- tokenClientApplicationConfig (java.util.Map<String, String>)
- tokenClientOrganizationConfig (java.util.Map<String, String>)
- dynamicConfig (org.openspcoop2.pdd.core.dynamic.DynamicConfig)
- request o response (org.openspcoop2.pdd.core.dynamic.ContentExtractor)
- system (org.openspcoop2.pdd.core.dynamic.PropertiesReader)
- env (org.openspcoop2.pdd.core.dynamic.PropertiesReader)
- java (org.openspcoop2.pdd.core.dynamic.PropertiesReader)
- envj (org.openspcoop2.pdd.core.dynamic.PropertiesReader)

Nel caso di utilizzo di template 'Freemarker' o 'Velocity' sono disponibili i seguenti ulteriori oggetti:
 
- class; permette di definire classi. L'utilizzo varia a seconda del tipo di template engine:

  - velocity: class.forName("my.package.name")
  - freemarker: class["my.package.name"] 

- new; permette di istanziare una classe. L'utilizzo varia a seconda del tipo di template engine:

  - velocity: new.instance("my.package.name","Parametro1","ParametroN") 
  - freemarker: new("my.package.name","Parametro1","ParametroN")

- attachments (org.openspcoop2.pdd.core.dynamic.AttachmentsReader); consente di ottenere gli allegati registrati sull'API
- context (java.util.Map<String, Object>); permette di accedere al contesto della richiesta.
- errorHandler (org.openspcoop2.pdd.core.dynamic.ErrorHandler); permette di generare risposte personalizzate che segnalano l'impossibilità di proseguire la trasformazione.

Nel caso di utilizzo di template 'ZIP', 'TGZ' o 'TAR' sono disponibili le seguenti le proprietà dinamiche, interpretate direttamente da GovWay, utilizzabili per accedere a parti della richiesta o della risposta:

- ${content} : payload http del messaggio
- ${soapEnvelope} : soap envelope del messaggio
- ${soapBody} : contenuto del soap body
- ${attachment[index]} : attachment presente in un messaggio multipart alla posizione indicata dall'intero 'index'
- ${attachmentId[id]} : attachment presente in un messaggio multipart che possiede il Content-ID indicato












