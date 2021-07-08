Feature: Aggiornamento Applicativi Server

Background:

    * call read('classpath:crud_commons.feature')

@Update204
Scenario Outline: Applicativi Server Aggiornamento 204 OK

    * def applicativo = read('applicativo.json') 
    * eval randomize(applicativo, ["nome"]);

    * def applicativo_key = applicativo.nome
    * def applicativo_update = read('<nome>')
    * eval applicativo_update.nome = applicativo.nome
    
    * call create ( { resourcePath: 'applicativi-server', body: applicativo,  key: applicativo_key } )
    * call put ( { resourcePath: 'applicativi-server/'+applicativo_key, body: applicativo_update } )
		* call get ( { resourcePath: 'applicativi-server', key: applicativo_key } )
    * match response == applicativo_update
    * call delete ({ resourcePath: 'applicativi-server/' + applicativo_key } )

Examples:
|nome|
|applicativo.json|
|applicativo_proprieta.json|


