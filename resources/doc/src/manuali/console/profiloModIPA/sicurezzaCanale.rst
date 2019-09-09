.. _modipa_sicurezzaCanale:

Sicurezza Canale
----------------

I profili di sicurezza a livello del canale riguardano le modalità di trasporto dei messaggi tra il dominio fruitore e quello erogatore. La specifica tenica del Modello di Interoperabilità prevede, per questa tipologia, i seguenti due profili:

- [IDAC01] Direct Trust Transport-Level Security: comunicazione basata sul canale SSL dopo aver effettuato il trust del certificato X509 fornito dal dominio erogatore.
- [IDAC02] Direct Trust mutual Transport-Level Security: comunicazione basata sul canale SSL dopo aver effettuato il trust dei certificati X509, del fruitore e dell’erogatore, nella modalità di mutua autenticazione.

Il concetto di ente/dominio, previsto dalle specifiche del Modello di Interoperabilità, viene riportato su quello di Soggetto nell'ambito delle entità di configurazione di GovWay.

Vediamo nelle sezioni seguenti come si possono effettuare le configurazioni per i profili di sicurezza canale.

.. toctree::
        :maxdepth: 2

        canale/idac01
        canale/idac02