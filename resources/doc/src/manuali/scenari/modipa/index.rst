.. _scenari_modi:

=====================
Profilo 'ModI'
=====================

Nelle sezioni successive verranno mostrati degli scenari di esempio di API Rest e API SOAP erogate o fruite con profilo “ModI" in accordo alla normativa prevista dal Modello di Interoperabilità.

I scenari descritti si differenziano rispetto ai pattern di sicurezza associati alle API erogate o fruite:

- nella sezione :ref:`scenari_modi_idauth` le API sono configurate tramite il pattern :ref:`modipa_idar01`;

- nella sezione :ref:`scenari_modi_integrity` viene utilizzato il pattern :ref:`modipa_idar03`;

- nella sezione :ref:`scenari_modi_idauth_pdnd` le API sono configurate tramite il pattern :ref:`modipa_pdnd`;

- nella sezione :ref:`scenari_modi_integrity_pdnd` viene utilizzato il pattern :ref:`modipa_pdnd_integrity`.

.. note:: 

    Per una consultazione mirata alle informazioni di interesse per lo scenario si consiglia di impostare nel menù in alto a destra il profilo 'ModI' e la selezione del soggetto 'Ente' come mostrato nella figura :numref:`profilo_apigateway_selezione`.

    .. figure:: ../_figure_scenari/SelezioneProfiloModI.png
     :scale: 80%
     :align: center
     :name: profilo_modi_selezione

     Selezione del profilo 'ModI'

.. toctree::
    :maxdepth: 2

    idauth/index
    integrity/index
    idauth_pdnd/index
    integrity_pdnd/index
