@parallel=false
Feature: Ricerca Transazioni tramite purposeId

Background: 
  * def generate_tracing = (['bash', '-c', 'cd ' + batchPath + '/generatoreStatistiche &&  ./generaReportPDND.sh'])
  * def pubblish_tracing = (['bash', '-c', 'cd ' + batchPath + '/generatoreStatistiche &&  ./pubblicaReportPDND.sh'])
  * def clear_pdnd_tracing = read('classpath:org/openspcoop2/core/monitor/rs/testsuite/reportistica/clear_pdnd_tracing.js')

  * configure afterFeature = function(){ karate.call('classpath:prepare_modi_tests.feature@cleanup'); }

  * def ricerca_url = monitorUrl + '/reportistica/tracing-pdnd'
  * call read('classpath:crud_commons.feature')

  * def setup = callonce read('classpath:prepare_modi_tests.feature@prepare')
  
  * configure headers = ({ "Authorization": govwayMonitorCred }) 

  * clear_pdnd_tracing(govwayDbConfig, 5)
  
  # chiamo il batch di generazione
  * karate.exec(generate_tracing)
  
  * def DbUtils = Java.type('org.openspcoop2.core.monitor.rs.testsuite.DbUtils')
  * def String = Java.type('java.lang.String')

  * def db = new DbUtils(govwayDbConfig)

  * def pdd = db.readValue("SELECT identificativo_porta FROM soggetti WHERE nome_soggetto='rs-monitor-DemoSoggettoErogatore' AND tipo_soggetto = 'modipa'")
  
  * def tracing_id = 'd376c74a-bc3e-4c9d-8bb1-6d63e8a78da4'
  * def dettagli_errori = '{ "error": "strano" }'
  * def csv_data = "csv;di;esempio"
  * def day0 = db.parseTimestamp(db.formatTimestamp(db.addTimestamp(db.now(), -1), 'yyyy-MM-dd'), 'yyyy-MM-dd')
  * eval db.update("UPDATE statistiche_pdnd_tracing SET tracing_id = ?, error_details = ?, csv = ? WHERE data_tracciamento = ? AND pdd_codice = ?", tracing_id, dettagli_errori, csv_data.bytes, day0, pdd);

  * def day1 = db.parseTimestamp(db.formatTimestamp(db.addTimestamp(db.now(), -2), 'yyyy-MM-dd'), 'yyyy-MM-dd')
  * eval db.update("UPDATE statistiche_pdnd_tracing SET stato = ? WHERE data_tracciamento = ? AND pdd_codice = ?", 'FAILED', day1, pdd);

  * def day2 = db.parseTimestamp(db.formatTimestamp(db.addTimestamp(db.now(), -3), 'yyyy-MM-dd'), 'yyyy-MM-dd')
  * eval db.update("UPDATE statistiche_pdnd_tracing SET stato_pdnd = ? WHERE data_tracciamento = ? AND pdd_codice = ?", 'ERROR', day2, pdd);

  * def day3 = db.parseTimestamp(db.formatTimestamp(db.addTimestamp(db.now(), -4), 'yyyy-MM-dd'), 'yyyy-MM-dd')
  * eval db.update("UPDATE statistiche_pdnd_tracing SET tentativi_pubblicazione = ? WHERE data_tracciamento = ? AND pdd_codice = ?", 27, day3, pdd);

@search_all
Scenario: Ricerca usando come filtro il soggetto

  * def data_inizio = db.formatTimestamp(db.addTimestamp(db.now(), -20), 'yyyy-MM-dd')
  * def data_fine = db.formatTimestamp(db.now(), 'yyyy-MM-dd')
  
  Given url ricerca_url
  And param soggetto = 'rs-monitor-DemoSoggettoErogatore'
  And param data_inizio = data_inizio
  And param data_fine = data_fine
  When method get 
  Then status 200
  * match (response.items.length) == 5
  
@filter_tracing_id
Scenario: Ricerca usando come filtro il tracing id

  * def data_inizio = db.formatTimestamp(db.addTimestamp(db.now(), -20), 'yyyy-MM-dd')
  * def data_fine = db.formatTimestamp(db.now(), 'yyyy-MM-dd')
  
  Given url ricerca_url
  And param soggetto = 'rs-monitor-DemoSoggettoErogatore'
  And param data_inizio = data_inizio
  And param data_fine = data_fine
  And param tracing_id = tracing_id
  When method get 
  Then status 200
  Then match (response.items.length) == 1
  Then match (response.items[0].tracing_id) == tracing_id
  Then match (response.items[0].data_tracciamento) == db.formatTimestamp(day0, 'yyyy-MM-dd')

@filter_stato
Scenario: Ricerca usando come filtro lo stato di pubblicazione

  * def data_inizio = db.formatTimestamp(db.addTimestamp(db.now(), -20), 'yyyy-MM-dd')
  * def data_fine = db.formatTimestamp(db.now(), 'yyyy-MM-dd')
  
  Given url ricerca_url
  And param soggetto = 'rs-monitor-DemoSoggettoErogatore'
  And param data_inizio = data_inizio
  And param data_fine = data_fine
  And param stato = 'Fallita'
  When method get 
  Then status 200
  Then match (response.items.length) == 1
  Then match (response.items[0].stato) == 'Fallita'
  Then match (response.items[0].data_tracciamento) == db.formatTimestamp(day1, 'yyyy-MM-dd')

@filter_stato_pdnd
Scenario: Ricerca usando come filtro lo stato pdnd

  * def data_inizio = db.formatTimestamp(db.addTimestamp(db.now(), -20), 'yyyy-MM-dd')
  * def data_fine = db.formatTimestamp(db.now(), 'yyyy-MM-dd')
  
  Given url ricerca_url
  And param soggetto = 'rs-monitor-DemoSoggettoErogatore'
  And param data_inizio = data_inizio
  And param data_fine = data_fine
  And param stato_pdnd = 'Errore'
  When method get 
  Then status 200
  Then match (response.items.length) == 1
  Then match (response.items[0].stato_pdnd) == 'Errore'
  Then match (response.items[0].data_tracciamento) == db.formatTimestamp(day2, 'yyyy-MM-dd')

@filter_tentativi_pubblicazione
Scenario: Ricerca usando come filtro i tentativi di pubblicazione

  * def data_inizio = db.formatTimestamp(db.addTimestamp(db.now(), -20), 'yyyy-MM-dd')
  * def data_fine = db.formatTimestamp(db.now(), 'yyyy-MM-dd')
  
  Given url ricerca_url
  And param soggetto = 'rs-monitor-DemoSoggettoErogatore'
  And param data_inizio = data_inizio
  And param data_fine = data_fine
  And param tentativi_pubblicazione = 27
  When method get 
  Then status 200
  Then match (response.items.length) == 1
  Then match (response.items[0].tentativi_pubblicazione) == 27
  Then match (response.items[0].data_tracciamento) == db.formatTimestamp(day3, 'yyyy-MM-dd')
  
@export
Scenario: Controlla la corretta esportazione del CSV prodotto dal batch di generazione

  * def data_inizio = db.formatTimestamp(db.addTimestamp(db.now(), -20), 'yyyy-MM-dd')
  * def data_fine = db.formatTimestamp(db.now(), 'yyyy-MM-dd')
  
  Given url ricerca_url
  And param soggetto = 'rs-monitor-DemoSoggettoErogatore'
  And param data_inizio = data_inizio
  And param data_fine = data_fine
  And param tracing_id = tracing_id
  When method get 
  Then status 200
  Then match (response.items.length) == 1
  Then match (response.items[0].tracing_id) == tracing_id
  Then match (response.items[0].data_tracciamento) == db.formatTimestamp(day0, 'yyyy-MM-dd')
  
  * def id = response.items[0].id
  
  Given url ricerca_url + '/' + id + '/esporta'
  When method get 
  Then status 200
  Then match response == csv_data
  
@details
Scenario: controlla la corretta ricezione della struttura dettagliata del tracciato PDND

  * def data_inizio = db.formatTimestamp(db.addTimestamp(db.now(), -20), 'yyyy-MM-dd')
  * def data_fine = db.formatTimestamp(db.now(), 'yyyy-MM-dd')
  
  Given url ricerca_url
  And param soggetto = 'rs-monitor-DemoSoggettoErogatore'
  And param data_inizio = data_inizio
  And param data_fine = data_fine
  And param tracing_id = tracing_id
  When method get 
  Then status 200
  Then match (response.items.length) == 1
  Then match (response.items[0].tracing_id) == tracing_id
  Then match (response.items[0].data_tracciamento) == db.formatTimestamp(day0, 'yyyy-MM-dd')
  
  * def id = response.items[0].id
  
  Given url ricerca_url + '/' + id + '/dettagli'
  When method get 
  Then status 200
  Then match (response.dettagli_errore) == dettagli_errori
  
@force-publish
Scenario: controlla che l'operazione di force publish fallisca se non sono nello stato corretto

  * def data_inizio = db.formatTimestamp(db.addTimestamp(db.now(), -20), 'yyyy-MM-dd')
  * def data_fine = db.formatTimestamp(db.now(), 'yyyy-MM-dd')
  
  Given url ricerca_url
  And param soggetto = 'rs-monitor-DemoSoggettoErogatore'
  And param data_inizio = data_inizio
  And param data_fine = data_fine
  And param tracing_id = tracing_id
  When method get
  Then status 200
  Then match (response.items.length) == 1
  Then match (response.items[0].tracing_id) == tracing_id
  Then match (response.items[0].data_tracciamento) == db.formatTimestamp(day0, 'yyyy-MM-dd')
  
  * def id = response.items[0].id
  
  Given url ricerca_url + '/' + id + '/force-publish'
  And request { force_publish: true }
  When method put 
  Then status 500
  Then match (response.detail) == 'Il tracciato non risulta essere in uno stato in cui può essere abilitata la pubblicazione forzata: non ha ancora superato il numero massimo di tentativi o non è nello stato FAILED'
  

  
