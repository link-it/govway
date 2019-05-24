@ignore
Feature:

Scenario: Cleanup

* configure headers = ({ "Authorization": govwayConfAuth })
* call setup.delete_lock(setup.db)

* call delete ({ resourcePath: 'soggetti/'+setup.soggetto_http.nome})
* call delete ({ resourcePath: 'soggetti/'+setup.soggetto_certificato.nome})
* call delete ({ resourcePath: 'applicativi/'+setup.applicativo.nome})
* call delete ({ resourcePath: 'applicativi/'+setup.applicativo_principal.nome})
* call delete ({ resourcePath: setup.erogazione_petstore_path })
* call delete ({ resourcePath: setup.fruizione_petstore_path })
* call delete ({ resourcePath: 'soggetti/' + setup.erogatore.nome })
* call delete ({ resourcePath: setup.api_petstore_path })
