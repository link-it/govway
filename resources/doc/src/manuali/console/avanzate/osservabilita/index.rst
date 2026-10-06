.. _configAvanzataOsservabilita:

Metriche di Osservabilità
~~~~~~~~~~~~~~~~~~~~~~~~~

GovWay è in grado di esporre metriche operative (numero di richieste, latenze, dimensioni dei
messaggi, stato interno del gateway, risorse JVM, ecc.) verso i più diffusi sistemi di
monitoraggio infrastrutturale, adottando il modello dati e le convenzioni di
`Micrometer <https://micrometer.io>`_.

Le metriche sono utili per il monitoraggio *operativo* del gateway (dashboard real-time,
alerting, capacity planning) e sono complementari alla *Console di Monitoraggio*, che è invece
orientata all'analisi funzionale delle singole transazioni.

Sono supportate due modalità di raccolta, attivabili contemporaneamente:

- **pull (Prometheus)**: GovWay espone le metriche sull'endpoint HTTP ``/govway/metrics`` nel
  formato testuale Prometheus; è il sistema di monitoraggio a interrogare periodicamente il
  gateway (*scrape*);

- **push (OTLP)**: GovWay invia periodicamente le metriche a un collector compatibile
  `OpenTelemetry <https://opentelemetry.io>`_ (protocollo OTLP su HTTP/protobuf).

Per default le metriche sono disabilitate; l'abilitazione dei collettori è descritta nella sezione
:ref:`osservabilitaInstall` della Guida di Installazione.

.. toctree::
        :maxdepth: 2

	endpoint
	configurazione
	dettaglioServizio
	metriche
	istogrammi
	integrazione
