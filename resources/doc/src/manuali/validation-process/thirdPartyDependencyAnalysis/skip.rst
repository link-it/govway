.. _releaseProcessGovWay_thirdPartyDynamicAnalysis_skip:

OWASP Dependency-Check Falsi Positivi
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


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

Nell'utilizzo del plugin vengono aggiunte le configurazioni che permettono di registrare dei falsi positivi rispetto al progetto, individuati nella :ref:`vulnerabilityManagement`.
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
                                <suppressionFile>${owasp.falsePositives.dir}/xercesImpl.xml</suppressionFile>
                                <suppressionFile>${owasp.falsePositives.dir}/CVE-2022-45688.xml</suppressionFile>
				...
                            </suppressionFiles>
                        </configuration>
                        <goals>
                            <goal>aggregate</goal>
                        </goals>
                    </execution>
                </executions>
	</plugin>


Ogni falso positivo registrato viene descritto nel registro dei falsi positivi disponibile in :ref:`vulnerabilityManagement_skip_registry`.
