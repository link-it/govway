.. _releaseProcessGovWay_thirdPartyDynamicAnalysis_sbom:

Generazione SBOM (Software Bill of Materials)
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Oltre alla verifica delle vulnerabilità, il processo di build produce un **SBOM** (Software Bill of Materials): un inventario formale e machine-readable di tutte le dipendenze del progetto, con i relativi identificativi univoci (PURL), hash crittografici, licenze e relazioni di dipendenza transitive.

**Formati prodotti**

Il progetto genera l'SBOM in due formati standard:

- `CycloneDX <https://cyclonedx.org/>`_: standard OWASP, ottimizzato per security e vulnerability tracking;
- `SPDX <https://spdx.dev/>`_: standard Linux Foundation (ISO/IEC 5962:2021), ottimizzato per compliance legale e licenze.

Entrambi i formati vengono generati ad ogni build in formati diversi (JSON, XML, RDF) tramite i plugin Maven `cyclonedx-maven-plugin <https://github.com/CycloneDX/cyclonedx-maven-plugin>`_ e `spdx-maven-plugin <https://github.com/spdx/spdx-maven-plugin>`_.

**Utilizzo**

La generazione dell'SBOM è integrata nel ciclo di build Maven e si attiva automaticamente nella fase ``package``:

::

    mvn package

In alternativa è possibile attivarla esplicitamente nella fase ``verify``, saltando compilazione e packaging:

::

    mvn verify -Dsbom=verify -Dcompile=none -Dtestsuite=none -Dpackage=none -Dowasp=none -Dosv=none

**Configurazione**

La generazione è controllata tramite le seguenti proprietà Maven nel `root pom.xml <https://github.com/link-it/govway/blob/3.4.x/pom.xml>`_:

- ``sbom``: fase master (default: ``package``, usare ``none`` per disabilitare tutti i formati);
- ``sbom.output.dir``: directory di output (default: ``${project.basedir}/sbom``);
- ``sbom.includeTestScope``: include le dipendenze con scope test (default: ``false``);
- ``sbom.cyclonedx``: fase per CycloneDX (eredita da ``sbom``, ``none`` per disabilitare solo CycloneDX);
- ``sbom.cyclonedx.format``: formato CycloneDX (``json``, ``xml``, ``all``, default: ``all``);
- ``sbom.spdx``: fase per SPDX (eredita da ``sbom``, ``none`` per disabilitare solo SPDX);
- ``sbom.spdx.json``: attivazione formato JSON SPDX (eredita da ``sbom.spdx``);
- ``sbom.spdx.rdfxml``: attivazione formato RDF/XML SPDX (eredita da ``sbom.spdx``).

**Artefatti prodotti**

Al termine del build, nella directory ``sbom/`` sono disponibili:

::

    sbom/
    ├── cyclonedx/
    │   ├── bom.cdx.json      (CycloneDX 1.6 - formato JSON)
    │   └── bom.cdx.xml       (CycloneDX 1.6 - formato XML)
    └── spdx/
        ├── bom.spdx.json     (SPDX 2.3 - formato JSON)
        └── bom.spdx.rdf.xml  (SPDX 2.3 - formato RDF/XML)

**Esempio di comandi**

Per generare l'SBOM senza eseguire compilazione, testsuite, packaging e verifiche:

::

    mvn verify -Dsbom=verify -Dcompile=none -Dtestsuite=none -Dpackage=none -Dowasp=none -Dosv=none

Per generare solo CycloneDX:

::

    mvn verify -Dsbom=verify -Dsbom.spdx=none -Dcompile=none -Dtestsuite=none -Dpackage=none -Dowasp=none -Dosv=none

Per generare solo SPDX:

::

    mvn verify -Dsbom=verify -Dsbom.cyclonedx=none -Dcompile=none -Dtestsuite=none -Dpackage=none -Dowasp=none -Dosv=none

Per disabilitare del tutto la generazione SBOM durante un ``mvn package``:

::

    mvn package -Dsbom=none

Per includere anche le dipendenze di test nell'SBOM:

::

    mvn package -Dsbom.includeTestScope=true
