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


#TODO Sistemare i campi predefiniti quando si checka il campo autorizzazione in erogazione.

@CreatePetstore204
Scenario: Creazione Fruizioni Petstore 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * call create_201 ({ resourcePath: 'fruizioni', body: fruizione_petstore,  key: petstore_key })
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

    * eval fruizione_spcoop.autorizzazione.configurazione.soggetto = soggetto_autenticato.nome
    * eval fruizione_spcoop.autorizzazione.configurazione.ruolo = ruolo_autenticato.nome

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

