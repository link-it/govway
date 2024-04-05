.. _modipa_sicurezza_avanzate_fruizione_keystore_scenari:

Keystore di firma in una fruizione di API
------------------------------------------------------------

Il materiale crittografico utilizzato per una fruizione di API può essere associato a differenti oggetti del registro in funzione del contesto applicativo di utilizzo:

- Materiale associato ad un **applicativo client**: scenario di default descritto nella sezione :ref:`modipa_idar01_fruizione` che prevede l'associazione del keystore di firma all'applicativo mittente. In questo scenario si assume che il materiale crittografico identifica l'applicativo il quale lo riutilizzerà su ogni API che necessita di fruire.

- Materiale associato ad una **fruizione**: scenario descritto nella sezione :ref:`modipa_sicurezza_avanzate_fruizione_keystore` che prevede l'associazione del keystore di firma alla fruizione di API. In questo scenario si assume che esista un materiale crittografico dedicato ad ogni fruizione e non riutilizzato come nello scenario precedente. Questo scenario consente agli applicativi che indirizzano la fruizione di uscire verso il dominio esterno tramite un'unica identità rappresentata dal certificato presente nel keystore definito nella fruizione stessa. 

- Materiale associato ad una **token policy**: scenario descritto nella sezione :ref:`modipa_sicurezza_avanzate_fruizione_token_policy_keystore` che prevede la definizione di un keystore di firma all'interno di una token Policy. In questo scenario si assume l'esistenza di un unico materiale crittografico che verrà utilizzato per uscire verso il dominio esterno per qualsiasi fruizione di API da qualsiasi applicativo.

.. toctree::
        :maxdepth: 2

	fruizione_keystore
	fruizione_token_policy_keystore
