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

- commons-discovery.xml: :ref:`releaseProcessGovWay_thirdPartyDynamicAnalysis_skip_registry_CVE-2022-0869`

- snakeyaml.xml: :ref:`releaseProcessGovWay_thirdPartyDynamicAnalysis_skip_registry_CVE-2022-38752`

- spring-web.xml: :ref:`releaseProcessGovWay_thirdPartyDynamicAnalysis_skip_registry_CVE-2016-1000027`

- spring-security-crypto.xml: :ref:`releaseProcessGovWay_thirdPartyDynamicAnalysis_skip_registry_CVE-2020-5408`

- xercesImpl.xml: :ref:`releaseProcessGovWay_thirdPartyDynamicAnalysis_skip_registry_CVE-2017-10355`

- console-back-office.xml: esclude i jar inclusi nel file `mvn/dependencies/faces/pom.xml <https://github.com/link-it/govway/blob/master/mvn/dependencies/faces/pom.xml>`_ poichè utilizzati dalle console di gestione e monitoraggio adibite a funzioni di backoffice che non devono essere esposte al pubblico.

  .. note::

     È in corso una attività di revisione dei jar utilizzati dalle console al fine di superare tutte le vulnerabilità note.


.. toctree::
        :maxdepth: 2
        
	registry/index
