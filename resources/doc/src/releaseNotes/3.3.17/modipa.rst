Miglioramenti al Profilo di Interoperabilità 'ModI'
------------------------------------------------------

In ottemperanza a quanto indicato nella segnalazione 'https://github.com/AgID/specifiche-tecniche-DPR-160-2010/issues/198', è stata introdotta la possibilità di generare un token di integrità anche per richieste e/o risposte prive di payload, calcolando in questo caso il Digest su un body vuoto ("").

È stata inoltre migliorata la gestione della cache per i token di audit e per i token di autenticazione generati localmente dal fruitore. Un token viene adesso rigenerato prima della scadenza effettiva per evitare che il suo utilizzo prossimo alla scadenza risulti scaduto una volta ricevuto dall'erogatore.
