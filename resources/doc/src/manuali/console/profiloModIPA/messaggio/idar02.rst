.. _modipa_idar02:

[IDAS02 / IDAR02] Direct Trust con certificato X.509 con unicità del token/messaggio
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. note::
    La sigla che identifica il profilo di sicurezza messaggio varia a seconda se l'API sia di tipo REST, per cui la sigla corrisponde a *IDAR02*, o SOAP dove viene utilizzata la sigla *IDAS02*.

Questo profilo di sicurezza presenta le medesime caratteristiche di :ref:`modipa_idar01`, con l'unica differenza di prevedere un meccanismo di filtro che impedisce la ricezione di messaggi duplicati da parte di ciascun ricevente.

L'attivazione di questo profilo avviene a livello della relativa API, nella sezione "ModIPA", elemento "Profilo Sicurezza Messaggio", selezionando il profilo "IDAR02" (o IDAS02 per SOAP) come indicato in :numref:`api_messaggio2_fig`.

  .. figure:: ../../_figure_console/modipa_api_messaggio2.png
    :scale: 50%
    :align: center
    :name: api_messaggio2_fig

    Profilo di sicurezza messaggio IDAR02 per l'API

Per le configurazioni successive procedere come già descritto in precedenza per il profilo :ref:`modipa_idar01`.
