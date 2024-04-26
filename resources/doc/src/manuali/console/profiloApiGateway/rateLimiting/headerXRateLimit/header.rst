.. _headerGWRateLimitingHeader:

Header HTTP utilizzati nelle Policy di Rate Limiting
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

- **Retry After** (:numref:`headerGwRateLimitingRetryAfter`)

  .. table:: Header HTTP 'Retry After'
     :widths: 35 65
     :name: headerGwRateLimitingRetryAfter

     ===============================  ================================================================================
     Nome Header HTTP                 Descrizione
     ===============================  ================================================================================
     Retry-After                      Numero di secondi dopo i quali il client può riprovare ad utilizzare il servizio                                                                   
     ===============================  ================================================================================

- **Numero Richieste** (:numref:`headerGwRateLimitingMetricaNumeroRichieste`)

  .. table:: Header HTTP relativi alla metrica 'Numero Richieste'
     :widths: 35 65
     :name: headerGwRateLimitingMetricaNumeroRichieste

     ===============================  ===============================================================================
     Nome Header HTTP                 Descrizione
     ===============================  ===============================================================================
     X-RateLimit-Limit                Numero massimo di richieste effettuabili nell'intervallo temporale configurato                                                                       
     X-RateLimit-Remaining            Numero di richieste ancora effettuabili nella finestra temporale in corso
     X-RateLimit-Reset                Numero di secondi mancante alla prossima finestra temporale
     ===============================  ===============================================================================

- **Numero Richieste Simultanee** (:numref:`headerGwRateLimitingMetricaNumeroRichiesteSimultanee`)

  .. table:: Header HTTP relativi alla metrica 'Numero Richieste Simultanee'
     :widths: 35 65
     :name: headerGwRateLimitingMetricaNumeroRichiesteSimultanee

     ============================================  ===============================================================================
     Nome Header HTTP                              Descrizione
     ============================================  ===============================================================================
     GovWay-RateLimit-ConcurrentRequest-Limit      Numero massimo di richieste simultanee effettuabili
     GovWay-RateLimit-ConcurrentRequest-Remaining  Numero di richieste ancora effettuabili
     ============================================  ===============================================================================

- **Occupazione Banda** (:numref:`headerGwRateLimitingMetricaOccupazioneBanda`)

  .. table:: Header HTTP relativi alla metrica 'Occupazione Banda'
     :widths: 35 65
     :name: headerGwRateLimitingMetricaOccupazioneBanda

     ============================================  ===============================================================================
     Nome Header HTTP                              Descrizione
     ============================================  ===============================================================================
     GovWay-RateLimit-BandwithQuota-Limit          Numero totale massimo di KB consentiti nell'intervallo temporale configurato 
     GovWay-RateLimit-BandwithQuota-Remaining      Banda ancora occupabile (in KB) nella finestra temporale in corso
     GovWay-RateLimit-BandwithQuota-Reset          Numero di secondi mancante alla prossima finestra temporale
     ============================================  ===============================================================================

- **Tempo Medio Risposta** (:numref:`headerGwRateLimitingMetricaTempoMedioRisposta`)

  .. table:: Header HTTP relativi alla metrica 'Tempo Medio Risposta'
     :widths: 35 65
     :name: headerGwRateLimitingMetricaTempoMedioRisposta

     ============================================  ===============================================================================
     Nome Header HTTP                              Descrizione
     ============================================  ===============================================================================
     GovWay-RateLimit-AvgTimeResponse-Limit        Tempo medio di risposta atteso
     GovWay-RateLimit-AvgTimeResponse-Reset        Numero di secondi mancante alla prossima finestra temporale
     ============================================  ===============================================================================

- **Tempo Complessivo Risposta** (:numref:`headerGwRateLimitingMetricaTempoComplessivoRisposta`)

  .. table:: Header HTTP relativi alla metrica 'Tempo Complessivo Risposta'
     :widths: 35 65
     :name: headerGwRateLimitingMetricaTempoComplessivoRisposta

     ============================================  ==================================================================================
     Nome Header HTTP                              Descrizione
     ============================================  ==================================================================================
     GovWay-RateLimit-TimeResponseQuota-Limit      Numero totale massimo di secondi consentiti nell'intervallo temporale configurato 
     GovWay-RateLimit-TimeResponseQuota-Remaining  Tempo di risposta (in secondi) ancora occupabile nella finestra temporale in corso
     GovWay-RateLimit-TimeResponseQuota-Reset      Numero di secondi mancante alla prossima finestra temporale
     ============================================  ==================================================================================

- **Numero Richieste Completate con Successo, Fallite o con Fault Applicativi** (:numref:`headerGwRateLimitingMetricaNumeroRichiesteEsito`)

  .. table:: Header HTTP relativi alla metrica 'Numero Richieste Completate con Successo, Fallite o con Fault Applicativi'
     :widths: 50 50 50 50 50
     :name: headerGwRateLimitingMetricaNumeroRichiesteEsito

     =============================================  ========================================  =======================================  =================================================  ==============================================================================
     Header HTTP metrica 'Completate con Successo'  Header HTTP metrica 'Fallite'             Header HTTP metrica 'Fault Applicativi'  Header HTTP metrica 'Fallite e Fault Applicativi'  Descrizione
     =============================================  ========================================  =======================================  =================================================  ==============================================================================
     GovWay-RateLimit-RequestSuccessful-Limit       GovWay-RateLimit-RequestFailed-Limit      GovWay-RateLimit-Fault-Limit             GovWay-RateLimit-RequestFailedOrFault-Limit        Numero di richieste consentite nell'intervallo temporale configurato 
     GovWay-RateLimit-RequestSuccessful-Remaining   GovWay-RateLimit-RequestFailed-Remaining  GovWay-RateLimit-Fault-Remaining         GovWay-RateLimit-RequestFailedOrFault-Remaining    Numero di richieste ancora effettuabili nella finestra temporale in corso
     GovWay-RateLimit-RequestSuccessful-Reset       GovWay-RateLimit-RequestFailed-Reset      GovWay-RateLimit-Fault-Reset             GovWay-RateLimit-RequestFailedOrFault-Reset        Numero di secondi mancante alla prossima finestra temporale
     =============================================  ========================================  =======================================  =================================================  ==============================================================================



