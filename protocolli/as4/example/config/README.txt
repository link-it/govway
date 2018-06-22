=====================
1) AS Config
   Copiare il file as4_local.properties nella directory di configurazione della PdD.
   La directory di configurazione DEVE essere impostata attraverso la variabile 'GOVWAY_HOME' 
   tramite il seguente comando:
	JAVA_OPTS="$JAVA_OPTS -DGOVWAY_HOME=/etc/govway/" 

   Configurare come la PdD accede al servizio di backend dove spedire messaggi AS4 con ruolo Sender tramite la seguente configurazione presente nel file 'as4_local.properties':
   - org.openspcoop2.protocol.as4.domibusGateway.config.default.url: url del servizio (es. http://127.0.0.1:8080/domibus/services/backend)
   - org.openspcoop2.protocol.as4.domibusGateway.config.default.https.enabled (true/false): indicazione se utilizzare una configurazione ssl custom
   - Se si abilita l'https si possono fornire i seguenti valori (con esempi):
	org.openspcoop2.protocol.as4.domibusGateway.config.default.https.property.trustStoreLocation=/etc/govway/keys/soggetto1.jks
	org.openspcoop2.protocol.as4.domibusGateway.config.default.https.property.trustStorePassword=openspcoopjks
	org.openspcoop2.protocol.as4.domibusGateway.config.default.https.property.keyStoreLocation=/etc/govway/keys/soggetto1.jks
	org.openspcoop2.protocol.as4.domibusGateway.config.default.https.property.keyStorePassword=openspcoopjks
	org.openspcoop2.protocol.as4.domibusGateway.config.default.https.property.keyPassword=openspcoop
	org.openspcoop2.protocol.as4.domibusGateway.config.default.https.property.hostnameVerifier=false

=====================
2) Configurazione Security Policies

2.1) Se l'installazione della PdD e quella di Domibus risiedono sulla stessa macchina,
     indicare nella proprietà 'org.openspcoop2.protocol.as4.securityPolicies.folder' del file 'as4_local.properties'
     la directory dove risiedono le security policies di domibus, tipicamente in 'DOMIBUS_DIR/conf/domibus/policies'.

2.2) Se l'installazione della PdD risiede su una macchina differente da quella di domibus,
     copiare le policy, che si trovano nella directory 'securityPolicies', 
     all'interno della directory indicata nella proprietà 'org.openspcoop2.protocol.as4.securityPolicies.folder' del file 'as4_local.properties'.
     Default value:  /etc/govway/as4/securityPolicies
    
2.3) Verificare che la policy 'eDeliveryPolicy' esista tra le policy di domibus.
     Se non esiste crearla copiando il file che si trova nella directory 'securityPolicies/eDeliveryPolicy.xml'
     in 'DOMIBUS_DIR/conf/domibus/policies'.
     La policy viene utilizzata dalle configurazioni di test

2.4) Copiare la policy 'eDeliveryPolicy_noSignature' tra le policy di domibus.
     Copiare il file che si trova nella directory 'securityPolicies/eDeliveryPolicy_noSignature.xml'
     in 'DOMIBUS_DIR/conf/domibus/policies'.
     La policy viene utilizzata dalle configurazioni di test dei casi REST.
     Questo poichè Domibus non sembra supportare la firma di attachment differenti da xml.
     Se si prova ad utilizzare la policy di default 'eDeliveryPolicy', domibus va in errore durante la firma
     di un attach non xml (es. json) fornendo l'errore seguente:
	Caused by: org.apache.wss4j.common.ext.WSSecurityException: Since it seems that nobody reads our installation notes, we must do it in the exception messages. Hope you read them. You did NOT use the endorsed mechanism from JDK 1.4 properly; look at <http://xml.apache.org/security/Java/installation.html> how to solve this problem. Original message was ""
        at org.apache.wss4j.dom.message.WSSecSignature.computeSignature(WSSecSignature.java:593)

=====================
3) Payload Profiles

3.1) I Payload profile di default sono definiti nel prodotto all'interno dell'archivio del protocollo as4,
     e definiscono quali sono i contenuti che devono viaggiare in AS4.
     Si possono trovare in protocolli/as4/src in:
     - org.openspcoop2.protocol.as4.pmode.pmode-payloadProfileDefault.ftl
     - org.openspcoop2.protocol.as4.pmode.pmode-payloadDefault.ftl
     Se si desidera ridefinire questi default si deve creare dei nuovi file ed indicarli nelle proprietà
     'org.openspcoop2.protocol.as4.payloadProfiles.defaultPayloads' e
     'org.openspcoop2.protocol.as4.payloadProfiles.defaultPayloadProfiles' nel file 'as4_local.properties'.

3.2) Alcuni payload profile, utili per i test, vengono ridefiniti all'interno dei files presenti nella directory 'payloadProfiles'.
     Copiare la directory in /etc/govway/as4 poichè tali files vengono indirizzati dalle configurazioni definite dal file registroServizi.xml


=====================
4) Properties

4.1) Le proprietà di default sono definite nel prodotto all'interno dell'archivio del protocollo as4,
     e definiscono quali sono le proprietà che devono viaggiare in AS4 (tutte opzionali).
     Si possono trovare in protocolli/as4/src in:
     - org.openspcoop2.protocol.as4.pmode.pmode-propertyDefault.ftl
     - org.openspcoop2.protocol.as4.pmode.pmode-propertySetDefault.ftl
     Se si desidera ridefinire questi default si deve creare dei nuovi file ed indicarli nelle proprietà
     'org.openspcoop2.protocol.as4.properties.defaultProperty' e
     'org.openspcoop2.protocol.as4.properties.defaultPropertySet' nel file 'as4_local.properties'.

4.2) Alcuni propertySet, utili per i test, vengono ridefiniti all'interno dei files presenti nella directory 'properties'.
     Copiare la directory in /etc/govway/as4 poichè tali files vengono indirizzati dalle configurazioni definite dal file registroServizi.xml


=====================
5) JMS Configuration

5.1) Accedere alla console di Domibus ('http://red:8180/domibus/') e verificare tramite la sezione 'Message Filter' di avere il plugin 'Jms' come prima voce
     e che lo stato 'Persisted' sia abilitato.

5.2) Configurare l'accesso al broker JMS di Domibus da parte della PdD tramite il file as4_local.properties come segue.
     - org.openspcoop2.protocol.as4.domibusJms.jndi.*: configurazione jndi per accedere al broker
     - org.openspcoop2.protocol.as4.domibusJms.connectionFactory: connecton factory per accedere al broker
     - org.openspcoop2.protocol.as4.domibusJms.username e org.openspcoop2.protocol.as4.domibusJms.password: credenziali di accesso al broker
     - org.openspcoop2.protocol.as4.domibusJms.queue.receiver: nome JNDI della coda JMS per i messaggi ricevuti quando si assume il ruolo 'Receiver'
     - org.openspcoop2.protocol.as4.domibusJms.queue.sender: nome JNDI della coda JMS per gli ack ricevuti quando si assume il ruolo 'Sender'
     - org.openspcoop2.protocol.as4.domibusJms.queue.sender.provider.url: URLBroker JMS specifico per gli ack ricevuti. E' utile nei test di loopback dove si utilizza una PdD e due Domibus.
     - org.openspcoop2.protocol.as4.domibusJms.debug: (true/false) indicazione se vengono prodotti i log di debug della ricezione dei msg. Attenzione: vengono scritti i contenuti ricevuti nella directory temporanea
     - org.openspcoop2.protocol.as4.domibusJms.threadsPool.size: (default:5) numero di threads in ascolto sulla coda jms
     - org.openspcoop2.protocol.as4.domibusJms.thread.checkIntervalMs: (default: 1000) Intervallo di check, in millisecondi (quando non sono presenti messaggi sulla coda)


=====================
6) Caricamento Configurazioni sulla PdD

6.1) Caricare il file 'registroServizi.xml' tramite la console govwayLoader

6.2) Accedere alla govwayConsole ed impostare il dominio 'interno' per i soggetti che si desidera gestire sui domibus a propria disposizione.
     NOTA: Se si desidera utilizzare entrambi i soggetti, utilizzare la modalità multi-tenant dei soggetti
     NOTA2: Fino al termine del caricamento delle configurazioni (voce successiva) la sezione 'Erogazioni' della govwayConsole non funziona

6.3) Caricare le configurazioni dei soggetti resi 'interni' al punto precedente tramite la console govwayLoader.
     Le configurazioni sono rispettivamente 'config_red.xml' e 'config_blue.xml'  per i soggetto Red e Blue.


=====================
7) Caricamento configurazione PMode

7.1) Effetture l'export della configurazione in pmode tramite l'elenco dei soggetti, cliccando su export sul soggetto desiderato ed 
     utilizzando la tipologia archivio 'Domibus pmode (single-xml-configuration)'.

7.2) Caricare la configurazione PMODE del soggetto tramite la console di domibus all'indirizzo 'http://red:8180/domibus/' tramite la sezione pmode.



=====================
8) Test

Creare la directory '/var/tmp/as4' utilizzata per salvare i messaggi di test ricevuti sulla porta applicativa.
E' la configurazione definita nel file config_blue.xml

Utilizzare il client 'example/pdd/client/Benchmark' tramite la configurazione che si trova in Benchmark_config/Client.properties
che permette di verificare tutti i test definiti nei files caricati precedentemente
