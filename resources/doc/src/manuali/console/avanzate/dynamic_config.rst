.. _avanzate_dynamic_config:

Accesso alle proprietà delle entità del Registro
-------------------------------------------------

In diverse funzionalità (:ref:`trasformazioni` - :ref:`valoriDinamici`, :ref:`apiGwAutorizzazioneContenuti`, :ref:`tokenClaims` ... ) è stata documentata la possibilità di accedere alle proprietà registrate nelle entità presenti sul Registro; vengono di seguito riportate le keyword utilizzabili (il valore 'NAME' indica la proprietà desiderata):

-   *config:NAME* : proprietà configurata per l'API erogata o fruitore;
-   *clientApplicationConfig:NAME* : proprietà configurata nell'applicativo fruitore; 
-   *clientOrganizationConfig:NAME* : proprietà configurata nel soggetto fruitore;
-   *providerOrganizationConfig:NAME* : proprietà configurata nel soggetto erogatore;
-   *tokenClientApplicationConfig:NAME* : proprietà configurata nell'applicativo client identificato tramite il clientId presente nel token;
-   *tokenClientOrganizationConfig:NAME* : proprietà configurata nel soggetto proprietario dell'applicativo client identificato tramite il clientId presente nel token.

Oltre agli accessi diretti alle proprietà di una singola entità, GovWay fornisce un'ulteriore modalità di accesso definita tramite la keyword 'dynamicConfig:FIELD' che consente di accedere alle proprietà delle entità coinvolte nella richiesta (api, applicativi, soggetti) verificando la presenza di una proprietà desiderata definita in funzione prima dell'entità più specifica e poi via via tramite quella più generica; il valore 'FIELD' fornito deve rappresentare un field valido all'interno della classe 'org.openspcoop2.pdd.core.dynamic.DynamicConfig'. 

Di seguito viene fornita una descrizione dei principali metodi forniti e l'ordine di ricerca della proprietà, indicata come parametro 'pName', dalla più specifica alla più generica; non appena viene individuata una proprietà viene utilizzato il suo valore e l'algoritmo di ricerca termina.

- *dynamicConfig:apiSearchByClientApplication(pName)*: effettua una ricerca tra le proprietà di un'erogazione o fruizione di API (config) attraverso i seguenti criteri:

	- <clientOrganizationName>.<clientApplicationName>.<pName>
	- <clientApplicationName>.<pName>
	- <clientOrganizationName>.<pName>
	- <pName>

- *dynamicConfig:clientApplicationSearch(pName)*: effettua una ricerca tra le proprietà di un'applicativo fruitore (clientApplicationConfig) attraverso i seguenti criteri:

	- <nomeErogatore>.<nomeApiImpl>.v<versioneApiImpl>.<pName>
	- <nomeApiImpl>.v<nomeApiImpl>.<pName>
	- <nomeErogatore>.<pName>
	- <pName>

- *dynamicConfig:clientOrganizationSearch(pName)*: effettua una ricerca tra le proprietà di un soggetto fruitore (clientOrganizationConfig) attraverso i seguenti criteri:

	- <nomeErogatore>.<nomeApiImpl>.v<versioneApiImpl>.<pName>
	- <nomeApiImpl>.v<nomeApiImpl>.<pName>
	- <nomeErogatore>.<pName>
	- <pName>

- *dynamicConfig:apiSearchByTokenClientApplication(pName)*: effettua una ricerca tra le proprietà di un'erogazione o fruizione di API (config) attraverso i seguenti criteri:

	- <tokenClientOrganizationName>.<tokenClientApplicationName>.<pName>
	- <tokenClientApplicationName>.<pName>
	- <tokenClientOrganizationName>.<pName>
	- <pName>

- *dynamicConfig:tokenClientApplicationSearch(pName)*: effettua una ricerca tra le proprietà di un'applicativo fruitore identificato tramite il clientId presente nel token (tokenClientApplicationConfig) attraverso i seguenti criteri:

	- <nomeErogatore>.<nomeApiImpl>.v<versioneApiImpl>.<pName>
	- <nomeApiImpl>.v<nomeApiImpl>.<pName>
	- <nomeErogatore>.<pName>
	- <pName>

- *dynamicConfig:tokenClientOrganizationSearch(pName)*: effettua una ricerca tra le proprietà di un soggetto proprietario dell'applicativo client identificato tramite il clientId presente nel token (tokenClientOrganizationConfig) attraverso i seguenti criteri:

	- <nomeErogatore>.<nomeApiImpl>.v<versioneApiImpl>.<pName>
	- <nomeApiImpl>.v<nomeApiImpl>.<pName>
	- <nomeErogatore>.<pName>
	- <pName>

- *dynamicConfig:providerSearch(pName)*: effettua una ricerca tra le proprietà di un soggetto erogatore (providerOrganizationConfig) attraverso i seguenti criteri:

	- <nomeApiImpl>.v<nomeApiImpl>.<pName>
	- <pName>


