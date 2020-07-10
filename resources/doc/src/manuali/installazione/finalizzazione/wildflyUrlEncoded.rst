.. _wfUrlEncoded:

Richieste 'application/x-www-form-urlencoded' su WildFly
--------------------------------------------------------

Per poter gestire correttamente richieste con Content-Type 'application/x-www-form-urlencoded' su application server 'WildFly', è richiesto di abilitare l'attributo 'allow-non-standard-wrappers' nell'elemento 'servlet-container' della configurazione di WildFly (es. in standalone/configuration/standalone.xml). 

   ::

       <servlet-container name="default" allow-non-standard-wrappers="true">
               ...
       </servlet-container>

In assenza della configurazione sopra indicata, una richiesta 'form-urlencoded' provoca un errore inatteso. 

L'esempio seguente riporta l'errore che si ottiene non abilitando l'attributo 'allow-non-standard-wrappers':

   ::

       curl -v -d "param1=value1&param2=value2" -X POST http://127.0.0.1:8080/govway/EnteTest/api-config/v1

       HTTP/1.1 500 Internal Server Error
       Connection: keep-alive
       Content-Type: text/html;charset=UTF-8
       Content-Length: 11543
       Date: Tue, 07 Jul 2020 16:13:25 GMT
        
       <html><head><title>ERROR</title><style>
       body {
           font-family: "Lucida Grande", "Lucida Sans Unicode", "Trebuchet MS", Helvetica, Arial, Verdana, sans-serif;
           margin: 5px
           ...
       </head><body><div class="header"><div class="error-div"></div><div class="error-text-div">Error processing request</div>
       </div><div class="label">Context Path:</div><div class="value">/govway</div><br/><div class="label">Servlet Path:</div>
       <div class="value"></div><br/><div class="label">Path Info:</div><div class="value">/EnteTest/api-config/v1</div><br/>
       <div class="label">Query String:</div><div class="value">null</div><br/><div class="label">Stack Trace:</div>
       <div class="value"></div><br/><pre>java.lang.IllegalArgumentException: UT010023: Request org.openspcoop2.pdd.services.connector.FormUrlEncodedHttpServletRequest@76dd2491 was not original or a wrapper
	at io.undertow.servlet@2.1.0.Final//io.undertow.servlet.handlers.FilterHandler$FilterChainImpl.doFilter(FilterHandler.java:116)
	at deployment.govway.ear//org.openspcoop2.pdd.services.connector.FormUrlEncodedFilter.doFilter(FormUrlEncodedFilter.java:75)
	at io.undertow.servlet@2.1.0.Final//io.undertow.servlet.core.ManagedFilter.doFilter(ManagedFilter.java:61)
	at io.undertow.servlet@2.1.0.Final//io.undertow.servlet.handlers.FilterHandler$FilterChainImpl.doFilter(FilterHandler.java:131)
	at io.undertow.servlet@2.1.0.Final//io.undertow.servlet.handlers.FilterHandler.handleRequest(FilterHandler.java:84)
	at io.undertow.servlet@2.1.0.Final//io.undertow.servlet.handlers.security.ServletSecurityRoleHandler.handleRequest(ServletSecurityRoleHandler.java:62)
	at io.undertow.servlet@2.1.0.Final//io.undertow.servlet.handlers.ServletChain$1.handleRequest(ServletChain.java:68)
	at io.undertow.servlet@2.1.0.Final//io.undertow.servlet.handlers.ServletDispatchingHandler.handleRequest(ServletDispatchingHandler.java:36)
	...
	at org.jboss.threads@2.3.3.Final//org.jboss.threads.EnhancedQueueExecutor$ThreadBody.doRunTask(EnhancedQueueExecutor.java:1486)
	at org.jboss.threads@2.3.3.Final//org.jboss.threads.EnhancedQueueExecutor$ThreadBody.run(EnhancedQueueExecutor.java:1377)
	at java.base/java.lang.Thread.run(Thread.java:834)
       </pre></body></html>


