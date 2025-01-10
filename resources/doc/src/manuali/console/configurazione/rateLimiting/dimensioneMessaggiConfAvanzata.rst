.. _configurazioneRateLimiting_dimensioneMessaggiConfAvanzata:

Configurazione Avanzata della metrica 'Dimensione Massima Messaggi'
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Nella sezione :ref:`rateLimiting_attivazioneNuovaPolicy` è possibile attivare una policy basata sulla metrica 'Dimensione Massima Messaggi' che consente di limitare la dimensione massima accettata di una richiesta e di una risposta.

La verifica della dimensione del messaggio avviene attraverso due modalità:

- header HTTP 'Content-Length': se presente, il valore indicato nell’header viene utilizzato per verificare che non superi la dimensione massima consentita;
- payload HTTP: il payload viene conteggiato man mano che viene ricevuto dallo stream. La ricezione si blocca e genera un errore quando il conteggio supera la dimensione massima consentita. Questo metodo consente di applicare la policy anche nei casi in cui l’header Content-Length non è presente, come nella modalità di trasmissione Transfer-Encoding: Chunked.

È possibile modificare i controlli applicati alla metrica 'Dimensione Massima Messaggi' registrando le seguenti :ref:`configProprieta`, applicabili sia all’erogazione che alla fruizione. I valori associabili alle proprietà sono 'true' o 'false'.

- *rateLimiting.useHttpContentLength*: consente di abilitare o disabilitare il controllo basato sul valore presente nell'header HTTP 'Content-Length'.
- *rateLimiting.useHttpContentLength.acceptZeroValue* (default: true): se disabilitato (false), un valore pari a zero nell'header HTTP 'Content-Length' non verrà accettato. 
