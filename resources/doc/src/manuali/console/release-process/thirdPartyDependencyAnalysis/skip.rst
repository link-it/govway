.. _releaseProcessGovWay_thirdPartyDynamicAnalysis_skip:

Falsi Positivi
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


**Librerie utilizzate solo a scopo di test**

Le librerie terza parte utilizzate solamente nelle testsuite interne del prodotto, che non insistono negli archivi binari rilasciati, non vengono considerate tra i check di vulnerabilità 'owasp'.
Il motivo risiede nel fatto che alcune batterie di test richiedono versioni storiche di librerie, alcune delle quali possiedono anche vulnerabilità.
Per evitare la verifica, le librerie utilizzate solamente dalle testsuite e non dai componenti runtime sono marcate con lo scope 'test' come mostrato nell'esempio seguente:

::

	<dependency>
                <groupId>org.apache.axis</groupId>
                <artifactId>axis</artifactId>
                <version>1.4</version>
                <exclusions>
                        <exclusion>
                                <groupId>*</groupId>
                                <artifactId>*</artifactId>
                        </exclusion>
                </exclusions>
                <scope>test</scope>
        </dependency>


**Falsi Positivi**

Nell'utilizzo del plugin vengono aggiunte le configurazioni che permettono di registrare dei falsi positivi rispetto al progetto.
Di seguito il frammento del file `mvn/dependencies/pom.xml <https://github.com/link-it/govway/blob/master/mvn/dependencies/pom.xml>`_ che evidenza come venga utilizzato il plugin owasp configurato con i suppressionFiles:

::

	<plugin>
                <groupId>org.owasp</groupId>
                <artifactId>dependency-check-maven</artifactId>
                <version>7.3.2</version>
                <executions>
                    <execution>
                        <id>check owasp</id>
                        <phase>verify</phase>
                        <configuration>
                            <autoUpdate>true</autoUpdate>
                            <failBuildOnAnyVulnerability>false</failBuildOnAnyVulnerability>
                            <outputDirectory>../../dependency-check-result</outputDirectory>
                            <format>ALL</format>
                            <suppressionFiles>
                                <suppressionFile>${owasp.falsePositives.dir}/swagger-codegen-linkit.xml</suppressionFile>
				<suppressionFile>${owasp.falsePositives.dir}/console-back-office.xml</suppressionFile>
				...
                            </suppressionFiles>
                        </configuration>
                        <goals>
                            <goal>aggregate</goal>
                        </goals>
                    </execution>
                </executions>
	</plugin>


Esaminando nel dettaglio i file che definiscono i falsi positivi:

- swagger-codegen-linkit.xml: esclude i jar inclusi nel file `mvn/dependencies/swagger-codegen/pom.xml <https://github.com/link-it/govway/blob/master/mvn/dependencies/swagger-codegen/pom.xml>`_ poichè vengono utilizzati solamente durante lo sviluppo per generare alcune classi e non a runtime dal Gateway.
- commons-discovery.xml: considera un falso positivo la vulnerabilità 'CVE-2022-0869' segnalata poichè:
	
	- Non risultano vulnerabilità note: `https://ossindex.sonatype.org/component/pkg:maven/commons-discovery/commons-discovery@0.5 <https://ossindex.sonatype.org/component/pkg:maven/commons-discovery/commons-discovery@0.5>`_;
	- Viene segnalato come un falso positivo anche nell'`issue 4644 <https://github.com/jeremylong/DependencyCheck/issues/4644>`_ del plugin OWASP Dependency-Check.

- snakeyaml.xml: le vulnerabilità 'CVE-2022-38752' e 'CVE-2022-41854' non sono sfruttabili su GovWay per effettuare attacchi poichè la libreria viene utilizzata solamente per la gestione delle interfacce yaml caricate sulla console dagli amministratori e non viene utilizzata per input forniti dinamicamente nelle richieste gestite dai componenti di runtime. Tanto premesso non appena esisterà una nuova versione della libreria che consente di superare le vulnerabilità note verrà effettuato un aggiornamento.

- spring-web.xml: la vulnerabilità 'CVE-2016-1000027' viene descritta come "Pivotal Spring Framework through 5.3.16 suffers from a potential remote code execution ...". La versione della libreria utilizzata in GovWay è superiore alla '5.3.16' quindi la segnalazione è considerabile un falso positivo. Dalle discussioni degli issues `4849 <https://github.com/jeremylong/DependencyCheck/issues/4849>`_ e `4558 <https://github.com/jeremylong/DependencyCheck/issues/4558>`_ del plugin OWASP Dependency-Check si comprende la motivazione che risiede nel fatto che fino ad un rilascio della versione 6.x, spring ha solamente deprecato l'utilizzo degli oggetti vulnerabili. Nel progetto GovWay comunque la classe oggetto della vulnerabilità (remoting-httpinvoker) non viene utilizzata.

- spring-security-crypto.xml: la vulnerabilità 'CVE-2020-5408' viene descritta come: "Spring Security versions 5.3.x prior to 5.3.2, ... use a fixed null initialization vector with CBC Mode in the implementation of the queryable text encryptor. ....".  La versione utilizzata in GovWay è superiore alla '5.3.2' quindi la segnalazione è considerabile un falso positivo. Dalle discussioni degli issues `287 <https://github.com/OSSIndex/vulns/issues/287>`_ e `284 <https://github.com/OSSIndex/vulns/issues/284>`_ del repository 'OSSIndex' si comprende la motivazione che risiede nel fatto che fino ad un rilascio della versione 6.x, spring-security ha solamente deprecato l'utilizzo degli oggetti vulnerabili. Nel progetto GovWay comunque il metodo oggetto della vulnerabilità ( Encryptors#queryableText(CharSequence, CharSequence) ) non viene utilizzato.

- xercesImpl.xml: la vulnerabilità 'CVE-2017-10355' è oggetto di discussione e aperture di segnalazioni, poichè non presente nel database nvd.nist.gov ma invece rilevata da Sonatype OSSIndex come si evince dalle discussioni degli issues `4614 <https://github.com/jeremylong/DependencyCheck/issues/4614>`_ e `316 <https://github.com/OSSIndex/vulns/issues/316>`_: "the intelligence that this CVE (still) applies to version 2.12.2 comes from the security analysts of Sonatype OSSINDEX, not from the NVD datastreams". In particolare la vulnerabilità `sonatype-2017-0348 <https://ossindex.sonatype.org/vulnerability/sonatype-2017-0348>`_ non ha poi una evidenza nel blog esistente (il link https://blogs.securiteam.com/index.php/archives/3271 non esiste). Il contenuto del blog, quando era esistente, viene riportato nell'`issue 4614 <https://github.com/jeremylong/DependencyCheck/issues/4614>`_, dove sembrava che la problematica rilevata fosse sul metodo XMLEntityManager.setupCurrentEntity() che non dispone di un meccanismo di timeout, metodo non utilizzato su GovWay. Nella discussione si fa inoltre riferimento alla vulnerabilità descritta in `SNYK-JAVA-XERCES-31497 <https://security.snyk.io/vuln/SNYK-JAVA-XERCES-31497>`_ che consentiva di attuare attacchi DOS. Nel progetto GovWay è comunque corretto considerarlo un falso positivo poichè la libreria viene utilizzata per espressioni xpath configurate solamente sulla console dagli amministratori e non fornite in input dinamicamente nelle richieste gestite dai componenti runtime. Inoltre su GovWay è disabilitato l'accesso a risorse esterne (DTDs.enabled=false).

- console-back-office.xml: esclude i jar inclusi nel file `mvn/dependencies/faces/pom.xml <https://github.com/link-it/govway/blob/master/mvn/dependencies/faces/pom.xml>`_ poichè utilizzati dalle console di gestione e monitoraggio adibite a funzioni di backoffice che non devono essere esposte al pubblico.

  .. note::

     È in corso una attività di revisione dei jar utilizzati dalle console al fine di superare tutte le vulnerabilità note.
