.. _profiloFatturaPA_attiva_ricezioneNotifiche:

Ricezione delle Notifiche dallo Sdi
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Allo Sdi dovrà essere comunicata la seguente url che utilizzerà per
inoltrare le notifiche:

::

    https://<host-govway</govway/sdi/in/<SoggettoSDI>/TrasmissioneFatture/v1

Le notifiche ricevute verranno consegnate secondo le modalità
specificate durante l'esecuzione del Govlet. In fase di consegna
verranno generati gli header descritti nella :numref:`headerRicezioneNotificheTab`
