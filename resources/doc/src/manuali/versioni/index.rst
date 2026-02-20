========================================
Versioni di GovWay
========================================

Diverse versioni di GovWay sono disponibili per differenti stack tecnologici.
Di seguito è riportata la corrispondenza tra le versioni di GovWay e le relative versioni minime
dell'Application Server, di Java e delle principali librerie di terze parti utilizzate.

.. warning:: **Avviso End of Life — GovWay 3.3.x**

   A partire dal **31 dicembre 2026** la linea **3.3.x** raggiungerà lo stato di End of Life (EOL).
   Dopo tale data non verranno rilasciati aggiornamenti di sicurezza, patch correttive né nuove funzionalità.
   Si raccomanda di pianificare la migrazione alla versione **3.4.x** il prima possibile.


.. rubric:: Versioni attualmente supportate

.. list-table::
   :header-rows: 1
   :widths: 15 15 8 12 30 12 10

   * - Versione GovWay
     - Stato
     - Java
     - Apache Tomcat
     - WildFly
     - Ultima Release
     - EOL
   * - ``3.4.x``
     - **Supportata**
     - 21+
     - 11
     - 35 – 38 (JBoss EAP 8.1), 27 – 28 (JBoss EAP 8.0)
     - ``3.4.2``
     - —
   * - ``3.3.x``
     - **EOL imminente**
     - 11+
     - 9
     - 26, 22 – 25 (JBoss EAP 7.4), 18 – 21 (JBoss EAP 7.3)
     - ``3.3.19``
     - **31/12/2026**


.. rubric:: Versioni non più supportate

Le seguenti versioni hanno raggiunto il termine del ciclo di vita. Si raccomanda l'aggiornamento ad una versione supportata.

.. list-table::
   :header-rows: 1
   :widths: 15 8 8 12 35 12 10

   * - Versione GovWay
     - Stato
     - Java
     - Apache Tomcat
     - WildFly
     - Ultima Release
     - EOL
   * - ``3.2.x``
     - **EOL**
     - 11+
     - 7 / 8 / 9
     - 18 (JBoss EAP 7.3), 14 – 17 (JBoss EAP 7.2), 11 (JBoss EAP 7.1), 10 (JBoss EAP 7.0)
     - ``3.2.2.p2``
     - 31/12/2020

Le versioni indicate come "EOL" non ricevono più aggiornamenti di alcun tipo, incluse patch di sicurezza.


.. rubric:: Librerie di terze parti

La tabella seguente riporta le versioni delle principali librerie incluse in ciascuna linea di rilascio.
Le versioni si riferiscono all'ultima release pubblicata per ogni linea.

.. list-table::
   :header-rows: 1
   :widths: 28 24 24 24

   * - Libreria
     - 3.4.x
     - 3.3.x
     - 3.2.x
   * - **Apache CXF**
     - ``4.1.3``
     - ``3.6.8``
     - ``3.2.6``
   * - **HTTP Client**
     - Apache HttpClient ``5.5``, HttpCore ``5.3.4``
     - HttpURLConnection JDK built-in, opz. Apache HttpClient ``4.5.13``
     - HttpURLConnection JDK built-in
   * - **Java EE / Jakarta EE**
     - ``jakarta.*``
     - ``javax.*``
     - ``javax.*``
   * - **SAAJ** (SOAP with Attachments)
     - soap-api ``3.0.2``, saaj-impl ``3.0.4``
     - soap-api ``1.4.0``, saaj-impl ``1.5.3``
     - JDK built-in, saaj-impl ``1.3.28``
   * - **Jackson** (JSON / YAML)
     - ``2.19.1``
     - ``2.18.3``
     - ``2.9.10``
   * - **Swagger / OpenAPI**
     - core ``2.2.33``, parser ``2.1.29``
     - core ``2.2.4``, parser ``2.1.6``
     - core ``2.0.3``, parser ``2.0.2``
   * - **Logging**
     - Log4j ``2.25.3``
     - Log4j ``2.25.3``
     - Log4j ``2.11.1``
   * - **Spring Framework**
     - ``6.2.11``
     - ``5.3.39``
     - ``5.1.6``
   * - **Spring Security**
     - ``6.5.5``
     - ``5.8.16``
     - ``5.1.5``
   * - **Bouncy Castle**
     - ``1.81``
     - ``1.81``
     - ``1.60``
   * - **Apache Santuario** (XML Security)
     - ``4.0.4``
     - ``2.3.4``
     - ``2.1.1``
   * - **Apache WSS4J**
     - ``4.0.0``
     - ``2.4.1``
     - ``2.2.2``
   * - **OpenSAML**
     - ``5.1.4``
     - ``3.4.6``
     - ``3.3.1``
   * - **Nimbus JOSE+JWT**
     - ``10.3.1``
     - ``10.3.1``
     - —

.. note::

   Le versioni delle librerie sono aggiornate con continuità tramite patch di sicurezza.
   Per il dettaglio completo delle dipendenze e delle relative versioni, fare riferimento ai file
   ``mvn/dependencies/*/pom.xml`` presenti nel `repository GitHub <https://github.com/link-it/govway>`_
   del progetto, selezionando il branch corrispondente alla versione in uso:
   ``3.4.x`` o ``master`` (3.3.x).


.. rubric:: Note sulle dipendenze esterne

**Java** — Java 11 ha raggiunto la fine del supporto pubblico da parte di Oracle.
La versione LTS raccomandata è **Java 21**, supportata dalla linea GovWay 3.4.x.

**Apache Tomcat** — Apache Tomcat 9 (utilizzato dalla linea 3.3.x): il termine del supporto per Tomcat 9.0.x è previsto
`non prima del 31 marzo 2027 <https://tomcat.apache.org/whichversion.html>`_.
Si raccomanda comunque di pianificare la migrazione a **Tomcat 11**,
supportato dalla linea GovWay 3.4.x.

**Spring Framework** — La linea Spring Framework 5.3.x (utilizzata da GovWay 3.3.x e precedenti) ha raggiunto
la fine del supporto open source il **31 agosto 2024**: non vengono più rilasciate patch di sicurezza
né correzioni pubbliche. Solo opzioni di supporto commerciale restano disponibili.
La linea GovWay 3.4.x adotta **Spring Framework 6.2.x**, attualmente in supporto attivo.
Per maggiori dettagli, consultare la pagina ufficiale
`Spring Framework Versions <https://github.com/spring-projects/spring-framework/wiki/Spring-Framework-Versions>`_.

**Migrazione Javax → Jakarta** — La linea 3.4.x adotta il namespace **Jakarta EE** (``jakarta.*``) in sostituzione
del precedente ``javax.*``. Questo passaggio è necessario per la compatibilità con i moderni
Application Server (Tomcat 10+, WildFly 27+) e le nuove versioni delle librerie (CXF 4.x, Spring 6.x, ecc.).

**Connettore HTTP** — A partire dalla versione 3.4.x, il connettore HTTP di default utilizza
**Apache HTTP Core 5** con supporto nativo HTTP/2, NIO e virtual thread.
Le versioni precedenti (≤ 3.3.x) si appoggiano a ``java.net.HttpURLConnection``.

**Browser** — A partire dalla versione 3.4.x non viene più supportato IE11.
