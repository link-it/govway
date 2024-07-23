.. _configGenerale_urlInvocazione_urlInterna:

Contesto di una API erogata o fruita
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

L'url di invocazione di una API su GovWay, al netto di eventuali ridefinizioni definite tramite :ref:`configGenerale_urlInvocazione_proxyPass`, segue una convenzione di naming che varia tra fruizione ed erogazione e in base al profilo di interoperabilità.

Nel seguito di questa documentazione verrà indica con *<prefix-erogazione>* e *<prefix-fruizione>* rispettivamente la base url configurata per le erogazioni e per le fruizioni, come descritto nella sezione :ref:`configGenerale_urlInvocazione`.

**erogazioni**

<prefix-erogazione>/\ **<profilo>[/in]**/\ <soggettoDominioInterno>/<nomeErogazione>/v<versioneErogazione>

- <profilo> assume i seguenti differenti valori in funzione del profilo di interoperabilità a cui l'API appartiene:

	- *api*: per il profilo 'API Gateway';
	- *soap*: per API di tipo SOAP su profilo 'ModI';
	- *rest*: per API di tipo REST su profilo 'ModI';
	- *spcoop*: per il profilo 'SPCoop';
	- *sdi*: per il profilo 'Fatturazione Elettronica';
	- *as4*: per il profilo 'eDelivery';

- <in> indica una erogazione di API; il contesto */in* può essere omesso.
	
**fruizioni**
	
<prefix-fruizione>/\ **<profilo>/out**/\ <soggettoDominioInterno>/<soggettoErogatore>/<nomeFruizione>/v<versioneFruizione>

- <profilo> assume uno dei valori descritti per l'erogazione;

- <out> indica una fruizione di API; per il profilo di interoperabilità 'Fatturazione Elettronica' deve essere usato 'out/xml2soap' al posto di 'out'.


.. note::
      Nell'URL di invocazione può essere omesso anche il profilo, sia per un'erogazione che per una fruizione; in tal caso, l'API verrà ricercata all'interno del profilo 'API Gateway'.

