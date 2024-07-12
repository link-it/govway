Feature: Creazione Fruizioni

Background:

* call read('classpath:crud_commons.feature')

* def api_petstore = read('api_petstore.json')
* eval randomize(api_petstore, ["nome"])
* eval api_petstore.referente = soggettoDefault
* def api_petstore_path = 'api/' + api_petstore.nome + '/' + api_petstore.versione

* def api_spcoop = read('api_spcoop.json')
* eval randomize(api_spcoop, ["nome"])
* eval api_spcoop.referente = soggettoDefault
* def api_spcoop_path = 'api/' + api_spcoop.nome + '/' + api_spcoop.versione

* def erogatore = read('soggetto_erogatore.json')
* eval randomize (erogatore, ["nome", "credenziali.username"])

* def fruizione_petstore = read('fruizione_petstore.json')
* eval fruizione_petstore.api_nome = api_petstore.nome
* eval fruizione_petstore.api_versione = api_petstore.versione
* eval fruizione_petstore.erogatore = erogatore.nome
* eval fruizione_petstore.api_referente = api_petstore.referente

* def petstore_key = fruizione_petstore.erogatore + '/' + fruizione_petstore.api_nome + '/' + fruizione_petstore.api_versione

* def fruizione_spcoop = read('fruizione_spcoop.json')
* eval fruizione_spcoop.api_nome = api_spcoop.nome
* eval fruizione_spcoop.api_versione = api_spcoop.versione
* eval fruizione_spcoop.erogatore = erogatore.nome
* eval fruizione_spcoop.api_referente = api_spcoop.referente
* def spcoop_key = fruizione_spcoop.erogatore + '/' + fruizione_spcoop.api_soap_servizio + '/' + fruizione_spcoop.api_versione

* def fruizione_petstore_connettore_https_jks = read('fruizione_petstore_connettore_jks.json')
* eval fruizione_petstore_connettore_https_jks.api_nome = api_petstore.nome
* eval fruizione_petstore_connettore_https_jks.api_versione = api_petstore.versione
* eval fruizione_petstore_connettore_https_jks.erogatore = erogatore.nome
* eval fruizione_petstore_connettore_https_jks.api_referente = api_petstore.referente

* def fruizione_petstore_connettore_https_pkcs11 = read('fruizione_petstore_connettore_pkcs11.json')
* eval fruizione_petstore_connettore_https_pkcs11.api_nome = api_petstore.nome
* eval fruizione_petstore_connettore_https_pkcs11.api_versione = api_petstore.versione
* eval fruizione_petstore_connettore_https_pkcs11.erogatore = erogatore.nome
* eval fruizione_petstore_connettore_https_pkcs11.api_referente = api_petstore.referente

* def fruizione_petstore_connettore_apikey = read('fruizione_petstore_connettore_apikey.json')
* eval fruizione_petstore_connettore_apikey.api_nome = api_petstore.nome
* eval fruizione_petstore_connettore_apikey.api_versione = api_petstore.versione
* eval fruizione_petstore_connettore_apikey.erogatore = erogatore.nome
* eval fruizione_petstore_connettore_apikey.api_referente = api_petstore.referente

#TODO Sistemare i campi predefiniti quando si checka il campo autorizzazione in erogazione.

@CreatePetstore204
Scenario: Creazione Fruizioni Petstore 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create_201 ({ resourcePath: 'fruizioni', body: fruizione_petstore,  key: petstore_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })


@CreatePetstore204_connettore_ClientJKS_ServerJKS
Scenario: Creazione Fruizioni Petstore 204 (truststore JKS, keystore JKS)

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create_201 ({ resourcePath: 'fruizioni', body: fruizione_petstore_connettore_https_jks,  key: petstore_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

@CreatePetstore204_connettore_ClientPKCS11_ServerPKCS11
Scenario: Creazione Fruizioni Petstore 204 (truststore PKCS11, keystore PKCS11)

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create_201 ({ resourcePath: 'fruizioni', body: fruizione_petstore_connettore_https_pkcs11,  key: petstore_key })
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

@CreatePetstore204_connettoreDebug
Scenario Outline: Erogazioni Creazione Petstore 204 connettore debug

		* eval fruizione_petstore.connettore.debug = <debug>
    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create ({ resourcePath: 'fruizioni', body: fruizione_petstore,  key: petstore_key })
		* call get ( { resourcePath: 'fruizioni', key: petstore_key + '/connettore'} )
		* match response.connettore.debug == <debug>
    * call delete ({ resourcePath: 'fruizioni/' + petstore_key})
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

Examples:
|debug|
|true|
|false|

@CreatePetstore204_connettore_ApiKey
Scenario: Creazione Fruizioni Petstore 204 (api key e app id)

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    
    # full
    * call create_201 ({ resourcePath: 'fruizioni', body: fruizione_petstore_connettore_apikey,  key: petstore_key })
    
    # senza header
    * remove fruizione_petstore_connettore_apikey.connettore.autenticazione_apikey.api_key_header
    * remove fruizione_petstore_connettore_apikey.connettore.autenticazione_apikey.app_id_header
    * call create_201 ({ resourcePath: 'fruizioni', body: fruizione_petstore_connettore_apikey,  key: petstore_key })
    
    # senza app id
    * remove fruizione_petstore_connettore_apikey.connettore.autenticazione_apikey.app_id
    * call create_201 ({ resourcePath: 'fruizioni', body: fruizione_petstore_connettore_apikey,  key: petstore_key })
    
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_petstore_path })

@CreateSPCoop204
Scenario: Creazione Fruizioni SPCoop 204

    * def query_params = ( { profilo: "SPCoop", soggetto: soggettoDefault , tipo_servizio: "ldap"})
    * call create ({ resourcePath: 'api', body: api_spcoop })
    * call create ({ resourcePath: 'soggetti', body: erogatore })

    # Creo il Soggetto con autenticazione https e il ruolo per l'autorizzazione
    * def soggetto_autenticato = read('classpath:bodies/soggetto-esterno-https.json')
    * eval randomize (soggetto_autenticato, ["nome", "credenziali.certificato.subject", "credenziali.certificato.issuer"])
    * call create ( { resourcePath: 'soggetti', body: soggetto_autenticato })

    * def ruolo_autenticato = read('classpath:bodies/ruolo.json');
    * eval randomize ( ruolo_autenticato, ["nome"])    
    * call create ( { resourcePath: 'ruoli', body: ruolo_autenticato })

    * eval fruizione_spcoop.autorizzazione.soggetto = soggetto_autenticato.nome
    * eval fruizione_spcoop.autorizzazione.ruolo = ruolo_autenticato.nome

    * call create_201 ({ resourcePath: 'fruizioni', body: fruizione_spcoop,  key: spcoop_key })

    * call delete ( { resourcePath: 'ruoli/' + ruolo_autenticato.nome })
    * call delete ( { resourcePath: 'soggetti/' + soggetto_autenticato.nome })

    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * call delete ({ resourcePath: api_spcoop_path })


@Create409
Scenario: Creazione Fruizioni 409

* call create ({ resourcePath: 'api', body: api_petstore })
* call create ({ resourcePath: 'soggetti', body: erogatore })
* call create_409 ({ resourcePath: 'fruizioni', body: fruizione_petstore,  key: petstore_key })
* call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
* call delete ({ resourcePath: api_petstore_path })

