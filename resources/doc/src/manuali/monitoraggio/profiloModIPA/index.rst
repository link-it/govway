.. _monitor_profiloModIPA:

======================
Profilo 'ModI'
======================

Il profilo 'ModI' consente in maniera del tutto trasparente alle
applicazioni interne al dominio, la conformità delle API (sia in
fruzione che in erogazione) alle nuove *Linee Guida AGID di
Interoperabilità*
(https://www.agid.gov.it/it/infrastrutture/sistema-pubblico-connettivita/il-nuovo-modello-interoperabilita).

La struttura complessiva del processo di monitoraggio si mantiene analoga a quanto già descritto per il profilo API Gateway. Le differenze, rispetto al profilo API Gateway, presentate in questa sezione, riguardano informazioni aggiuntive presenti nelle tracce di Richiesta o Risposta di una transazione.

All'interno di una traccia vengono fornite le informazioni riguardanti i pattern adottati dall'API:

- *Pattern di Interazione*: definisce la modalità con cui fruitore ed erogatore di un servizio interagiscono. Nella traccia viene indicato il tipo 'bloccante' o 'non bloccante'. Nel caso di pattern non bloccante viene riportato il modello scelto (PUSH/PULL) e il ruolo della transazione all'interno del modello (Richiesta, Risposta, Richiesta Stato). Nel caso si tratti di transazioni relative a risposte (o richieste stato) viene indicato anche l'API o la risorsa correlata. Infine vengono riportati i valori degli header (soap o rest) richiesti da un pattern non bloccante (X-ReplyTo, X-Correlation-ID, Location). Nella figura :numref:`ModIPA-DettaglioTraccia` viene riportato un esempio di traccia relativa ad un messaggio di risposta relativo ad un pattern non bloccante con modello 'PUSH'.

   .. figure:: ../_figure_monitoraggio/ModIPA-DettaglioTraccia.png
    :scale: 100%
    :align: center
    :name: ModIPA-DettaglioTraccia

    ModI: informazioni aggiuntive sulla Traccia riguardanti il Pattern di Interazione.

- *Sicurezza Canale*: definisce la sicurezza inerente il canale di comunicazione tra i domini fruitore ed erogatore. Nella traccia viene riportato il tipo 'ID_AUTH_CHANNEL_01' o 'ID_AUTH_CHANNEL_02' utilizzato dall'API.

- *Sicurezza Messaggio*: definisce la sicurezza adottata a livello messaggio. Nella traccia viene indicato il pattern utilizzato e tutti i dettagli relativi agli elementi di sicurezza quali il certificato X509 utilizzato, l'audience, le date di validità del token di sicurezza etc. Nella figura :numref:`ModIPA-DettaglioTracciaSicurezza` viene riportato un esempio di traccia relativa ad un'API REST che implementa il pattern di sicurezza 'INTEGRITY_REST_01'.

   .. figure:: ../_figure_monitoraggio/ModIPA-DettaglioTracciaSicurezza.png
    :scale: 100%
    :align: center
    :name: ModIPA-DettaglioTracciaSicurezza

    ModI: informazioni aggiuntive sulla Traccia riguardanti il Pattern di Sicurezza.
