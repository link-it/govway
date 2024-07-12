.. _govwaySecretsMap:

GovWay Secrets
---------------

All'interno del file *<directory-lavoro>/govway.secrets.properties* è possibile razionalizzare una serie di variabili Java, in maniera simile a :ref:`govwayConfigMap`, con la differenza che i valori saranno forniti cifrati e GovWay si occuperà di decifrarli prima del loro caricamento nel sistema.

Le variabili saranno riferibili in qualsiasi file di proprietà di GovWay presente nella *<directory-lavoro>* tramite la sintassi '${nomeVar}' o all'interno delle varie configurazioni di GovWay, come descritto, ad esempio, nella sezione :ref:`valoriDinamici`, tramite la sintassi 'java:NAME' o 'envj:NAME'.

La cifratura dei valori può essere attuata utilizzando il tool 'govway-vault-cli', disponibile all'interno della directory *dist/tools/* prodotta dall'installer (:ref:`inst_installer`), e descritto nella sezione :ref:`byokInstallToolVaultCli` .

La sintassi da utilizzare all'interno del file *<directory-lavoro>/govway.secrets.properties* viene descritta nella sezione :ref:`govwaySecretsMapConfig`

.. toctree::
        :maxdepth: 2

	config
