.. _quickControlloAccessiAutenticazione:

Autenticazione
--------------

GovWay può essere configurata per autenticare i mittenti che invocano
una erogazione o fruizione di API attraverso una delle seguenti
modalità:

-  *https*: l'invocazione del client deve essere avvenuta su canale ssl
   e deve aver inviato un proprio certificato client validato dal
   front-end https. La terminazione ssl può essere gestita direttamente
   sull'application server (es. wildfly, tomcat) o può essere gestita da
   un frontend web (es. apache) il quale deve però inoltrare le
   informazioni ssl all'application server (es. via mod\_jk). Un esempio
   viene descritto nella sezione :ref:`authHTTPS`.

-  *http-basic*: il client deve inoltrare a GovWay delle credenziali di
   tipo *BASIC* (vedi specifica `RFC
   7617 <https://tools.ietf.org/html/rfc7617>`__). L'username e la
   password fornita deve corrispondere ad un applicativo o ad un
   soggetto registrato. 
   .. Un esempio viene descritto nella sezione XXX authBasic.

-  *principal*: questa configurazione richiede che l'autenticazione sia
   delegata al container via jaas in modo da permettere a GovWay di
   accedere al principal tramite la api
   *HttpServletRequest.getUserPrincipal()*. 
   .. Un esempio viene descritto nella sezione XXX authPrincipal .

.. toctree::
        :maxdepth: 2

	https/index
.. basic TODO
.. principal TODO
