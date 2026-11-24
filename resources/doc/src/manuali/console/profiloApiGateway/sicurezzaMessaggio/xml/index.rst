.. _sicurezzaLivelloMessaggioXML:

XML Security (API REST XML)
^^^^^^^^^^^^^^^^^^^^^^^^^^^

Gli schemi XML Security sono disponibili per le API REST con messaggi XML e consentono di firmare (XML Signature) o cifrare (XML Encryption) l'intero documento XML del messaggio. Come per gli altri schemi, si distingue tra il ruolo *Receiver* (verifica o decifratura del messaggio ricevuto) e *Sender* (firma o cifratura del messaggio inviato).

Nelle maschere di ricezione è presente il campo *Ripulisci Messaggio dalle Informazioni Sicurezza* (default: abilitato): dopo la verifica gli elementi di sicurezza vengono eliminati dal messaggio prima del suo inoltro.

I campi relativi a keystore e truststore sono descritti nella sezione :ref:`sicurezzaLivelloMessaggioKeystore`.

.. toctree::
        :maxdepth: 2

        signature
        encrypt

È inoltre disponibile lo schema *Configurazione Manuale*, descritto nella sezione :ref:`sicurezzaLivelloMessaggioManuale`.
