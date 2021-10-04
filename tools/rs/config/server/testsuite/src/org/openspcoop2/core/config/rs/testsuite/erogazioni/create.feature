Feature: Creazione Erogazioni

Background:

* call read('classpath:crud_commons.feature')

* def api_petstore = read('api_petstore.json')
* eval randomize(api_petstore, ["nome"])
* eval api_petstore.referente = soggettoDefault

* def api_spcoop = read('api_spcoop.json')
* eval randomize(api_spcoop, ["nome"])
* eval api_spcoop.referente = soggettoDefault

* def erogazione_petstore = read('erogazione_petstore.json')
* eval erogazione_petstore.api_nome = api_petstore.nome
* eval erogazione_petstore.api_versione = api_petstore.versione

* def erogazione_petstore_auth = read('classpath:bodies/erogazione-petstore-autenticazione-autorizzazione.json')
* eval erogazione_petstore_auth.api_nome = api_petstore.nome
* eval erogazione_petstore_auth.api_versione = api_petstore.versione

* def petstore_key = erogazione_petstore.api_nome + '/' + erogazione_petstore.api_versione
* def api_petstore_path = 'api/' + petstore_key

* def erogazione_spcoop = read('erogazione_spcoop.json')
* eval erogazione_spcoop.api_nome = api_spcoop.nome
* eval erogazione_spcoop.api_versione = api_spcoop.versione
* eval erogazione_spcoop.api_referente = api_spcoop.referente

* def spcoop_key = erogazione_spcoop.api_soap_servizio + '/' + erogazione_spcoop.api_versione
* def api_spcoop_path = 'api/' + api_spcoop.nome + '/' + api_spcoop.versione

* def erogatore = read('soggetto_erogatore.json')
* eval randomize (erogatore, ["nome", "credenziali.username"])

* def erogazione_petstore_connettore_https_jks = read('erogazione_petstore_connettore_jks.json')
* eval erogazione_petstore_connettore_https_jks.api_nome = api_petstore.nome
* eval erogazione_petstore_connettore_https_jks.api_versione = api_petstore.versione

* def erogazione_petstore_connettore_https_pkcs11 = read('erogazione_petstore_connettore_pkcs11.json')
* eval erogazione_petstore_connettore_https_pkcs11.api_nome = api_petstore.nome
* eval erogazione_petstore_connettore_https_pkcs11.api_versione = api_petstore.versione

@CreateErogatoreEsterno400
Scenario: Erogazioni Creazione Fallita Erogatore Esterno
    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create ({ resourcePath: 'soggetti', body: erogatore })
    * def query_params = ( { soggetto: erogatore.nome })
    
    * call create_400 ({ resourcePath: 'erogazioni', body: erogazione_petstore })
    
    * call delete ({ resourcePath: 'soggetti/' + erogatore.nome })
    * def query_params = ( { })
    * call delete ({ resourcePath: api_petstore_path })

@CreatePetstore204
Scenario: Erogazioni Creazione Petstore 204

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create_201 ({ resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key })
    * call delete ({ resourcePath: api_petstore_path })

@CreatePetstore204_connettore_ClientJKS_ServerJKS
Scenario: Erogazioni Creazione Petstore 204 (truststore JKS, keystore JKS)

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create_201 ({ resourcePath: 'erogazioni', body: erogazione_petstore_connettore_https_jks,  key: petstore_key })
    * call delete ({ resourcePath: api_petstore_path })
    
@CreatePetstore204_connettore_ClientPKCS11_ServerPKCS11
Scenario: Erogazioni Creazione Petstore 204 (truststore PKCS11, keystore PKCS11)

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create_201 ({ resourcePath: 'erogazioni', body: erogazione_petstore_connettore_https_pkcs11,  key: petstore_key })
    * call delete ({ resourcePath: api_petstore_path })

@CreatePetstoreAuth204
Scenario: Erogazioni Creazione Petstore con autenticazione e autorizzazione

    * call create ({ resourcePath: 'api', body: api_petstore })

    # Creo il Soggetto con autenticazione https e il ruolo per l'autorizzazione
    * def soggetto_autenticato = read('classpath:bodies/soggetto-esterno-https.json')
    * eval randomize (soggetto_autenticato, ["nome", "credenziali.certificato.subject", "credenziali.certificato.issuer"])
    * call create ( { resourcePath: 'soggetti', body: soggetto_autenticato })

    * def ruolo_autenticato = read('classpath:bodies/ruolo.json');
    * eval randomize ( ruolo_autenticato, ["nome"])    
    * call create ( { resourcePath: 'ruoli', body: ruolo_autenticato })

    * eval erogazione_petstore_auth.autorizzazione.soggetto = soggetto_autenticato.nome
    * eval erogazione_petstore_auth.autorizzazione.ruolo = ruolo_autenticato.nome

    * call create_201 ({ resourcePath: 'erogazioni', body: erogazione_petstore_auth,  key: petstore_key })

    * call delete ( { resourcePath: 'ruoli/' + ruolo_autenticato.nome })
    * call delete ( { resourcePath: 'soggetti/' + soggetto_autenticato.nome })
    * call delete ({ resourcePath: api_petstore_path })


@CreatePetstore409
Scenario: Erogazioni Creazione Petstore 409

    * call create ({ resourcePath: 'api', body: api_petstore })
    * call create_409 ({ resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key })
    * call delete ({ resourcePath: api_petstore_path })


@CreateNoApi400
Scenario: Erogazioni Creazione Errore 400 Nessuna api registrata
    
    * call create_400 ({ resourcePath: 'erogazioni', body: erogazione_petstore,  key: petstore_key })

@CreateSPCoop204
Scenario: Erogazioni Creazione SPCoop 204

    * def query_params = ( { profilo: "SPCoop", soggetto: soggettoDefault , tipo_servizio: "ldap"})
    * call create ({ resourcePath: 'api', body: api_spcoop })

    # Creo il Soggetto con autenticazione https e il ruolo per l'autorizzazione
    * def soggetto_autenticato = read('classpath:bodies/soggetto-esterno-https.json')
    * eval randomize (soggetto_autenticato, ["nome", "credenziali.certificato.subject", "credenziali.certificato.issuer"])
    * call create ( { resourcePath: 'soggetti', body: soggetto_autenticato })

    * def ruolo_autenticato = read('classpath:bodies/ruolo.json');
    * eval randomize ( ruolo_autenticato, ["nome"])    
    * call create ( { resourcePath: 'ruoli', body: ruolo_autenticato })

    * eval erogazione_spcoop.autorizzazione.soggetto = soggetto_autenticato.nome
    * eval erogazione_spcoop.autorizzazione.ruolo = ruolo_autenticato.nome

    * call create_201 ({ resourcePath: 'erogazioni', body: erogazione_spcoop,  key: spcoop_key })

    * call delete ( { resourcePath: 'ruoli/' + ruolo_autenticato.nome })
    * call delete ( { resourcePath: 'soggetti/' + soggetto_autenticato.nome })
    * call delete ({ resourcePath: api_spcoop_path })

@CreateSPCoop409
Scenario: Erogazioni Creazione SPCoop 409

    * def query_params = ( { profilo: "SPCoop", soggetto: soggettoDefault , tipo_servizio: "ldap"})
    * call create ({ resourcePath: 'api', body: api_spcoop })
    # Creo il Soggetto con autenticazione https e il ruolo per l'autorizzazione
    * def soggetto_autenticato = read('classpath:bodies/soggetto-esterno-https.json')
    * eval randomize (soggetto_autenticato, ["nome", "credenziali.certificato.subject", "credenziali.certificato.issuer"])
    * call create ( { resourcePath: 'soggetti', body: soggetto_autenticato })

    * def ruolo_autenticato = read('classpath:bodies/ruolo.json');
    * eval randomize ( ruolo_autenticato, ["nome"])    
    * call create ( { resourcePath: 'ruoli', body: ruolo_autenticato })

    * eval erogazione_spcoop.autorizzazione.soggetto = soggetto_autenticato.nome
    * eval erogazione_spcoop.autorizzazione.ruolo = ruolo_autenticato.nome

    * call create_409 ({ resourcePath: 'erogazioni', body: erogazione_spcoop,  key: spcoop_key })

    * call delete ( { resourcePath: 'ruoli/' + ruolo_autenticato.nome })
    * call delete ( { resourcePath: 'soggetti/' + soggetto_autenticato.nome })
    * call delete ({ resourcePath: api_spcoop_path })


