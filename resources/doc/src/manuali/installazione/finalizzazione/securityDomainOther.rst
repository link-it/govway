.. _securityDomainOther:

ApplicationSecurityDomain 'other' su WildFly 25 o superiore
-----------------------------------------------------------

A partire dalla versione 25 di wildfly, nella configurazione di default è abilitato un application-security-domain 'other' che rende obbligatoria la presenza di credenziali valide per invocare applicazioni web in generale e quindi anche i contesti 'govway'. 

Questo comporta che qualsiasi invocazione effettuata verso GovWay provoca un errore inatteso:

   ::

       curl -u test:123456 -v -k http://127.0.0.1:8080/govway/APITest/v1

       HTTP/1.1 401 Unauthorized
       WWW-Authenticate: Basic realm="127.0.0.1:8080"
       Content-Type: text/html;charset=UTF-8
       Content-Length: 71
       Date: Thu, 14 Oct 2021 10:03:51 GMT
       
       <html><head><title>Error</title></head><body>Unauthorized</body></html>

Poichè la gestione delle autorizzazioni deve invece avvenire su GovWay (tramite il Controllo degli Accessi), si deve procedere a disabilitare l'application security domain commentandone la definizione all'interno della configurazione 'undertow':

   ::

       <subsystem xmlns="urn:jboss:domain:undertow:x.0" ...>
	...
	    <application-security-domains>
               <!-- <application-security-domain name="other" security-domain="ApplicationDomain"/> -->
            </application-security-domains>
       </subsystem>
