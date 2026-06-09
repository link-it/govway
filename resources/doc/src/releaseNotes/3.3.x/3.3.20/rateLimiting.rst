Miglioramenti alla funzionalità di RateLimiting
------------------------------------------------------------

Aggiunto il supporto per connessioni Redis in modalità single-endpoint (es. Redis Enterprise con HA interna) e per connessioni TLS, con configurazione del truststore e verifica dell'hostname.

Per il profilo di interoperabilità 'ModI' sono stati aggiunti plugin di rate limiting che consentono l'estrazione di dati dal SecurityToken ModI (client_id, sub, subject e CN del certificato x5c di authorization e integrity) utilizzabili come criteri di filtro e raggruppamento nelle policy di controllo del traffico.
