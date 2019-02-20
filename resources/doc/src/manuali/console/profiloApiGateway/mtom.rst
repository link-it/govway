.. _console_mtom:

MTOM
~~~~

Nei casi in cui il mittente e il destinatario si scambiano messaggi con
allegati (nell'ambito del protocollo SOAP), utilizzando il protocollo
MTOM, GovWay è in grado di gestire tali comunicazioni in modalità
trasparente e quindi senza alcun intervento.

In altre situazioni è possibile sfruttare le funzionalità di GovWay per
beneficiare delle ottimizzazioni del protocollo MTOM quando uno dei due
interlocutori non è in grado di supportare tale protocollo, oppure per
effettuare verifiche di congruità dei messaggi in transito basati su
MTOM.

Nel caso di una erogazione, per il messaggio di richiesta, le opzioni
disponibili sono:

-  *disable*. Non viene svolta alcuna azione.

-  *unpackaging*. In questo scenario il client fruitore invia dati
   binari nel formato MTOM ma l'erogatore non supporta tale formato. Il
   gateway effettua la trasformazione del messaggio inserendo i dati
   binari in modalità *Base64 encoded* prima che venga inviato al
   destinatario. Sulla risposta sarà effettuato il processo inverso.

-  *verify*. Sia il fruitore che l'erogatore utilizzano MTOM ma si
   vogliono validare i messaggi. Il gateway effettua, tramite opportuni
   pattern xpath forniti, la validazione dei messaggi al fine di
   verificare la conformità del formato del messaggio rispetto a quanto
   atteso dall'erogatore.

Sempre nel caso di una erogazione, per il messaggio di risposta, le
opzioni disponibili sono:

-  *disable*. Non viene svolta alcuna azione.

-  *packaging*. In questo scenario il client fruitore invia dati binari
   nella modalità Base64 encoded ma l'erogatore richiede il formato
   MTOM. Il gateway effettua la trasformazione del messaggio secondo il
   protocollo MTOM prima che venga inviato al destinatario. Sulla
   risposta sarà effettuato il processo inverso.

-  *verify*. Analogo a quanto descritto per il messaggio di richiesta.

.. note::
    Nel caso si utilizzi la validazione dei contenuti, basata su
    xsd o wsdl, è possibile che la struttura MTOM non sia stata prevista
    negli schemi e quindi faccia fallire l'esito della stessa. In questo
    caso, quando si attiva la validazione è necessario abilitare l'opzione
    *Accetta MTOM/XOP-Message* affinché il processo di validazione tenga
    conto del formato MTOM.

.. note::
    Nel caso di una fruizione, le opzioni di configurazione disponibili
    per la richiesta diventano quelle per la risposta e viceversa.
