Feature: Feature di mock per i test non bloccante push

Background:
    
    * def getHeader = 
    """
    function(name) {
        headerArray = (karate.get("requestHeaders['" + name + "']") ||
               karate.get("requestHeaders['" + name.toLowerCase() + "']"))   
        if (headerArray == null)
        	return null;
        return headerArray[0];
    }
    """
    
    # UTILITY per gestire il 'db' del mock backend
    * def errors = ""
    * def traces = {}
    * def disabled = {}
    * def push = function(obj, elem) { obj[obj.size + ""] = elem; obj.size++; }
    * def filter = function(obj, state) { v = { size: 0 }; for (i = 0; i < obj.size; i++) { if (obj[i + ""].state == state) { push(v, obj[i + ""]) }}; return v; }
    * def get = function(obj, id) { for (i = 0; i < obj.size; i++) { if ((obj[i + ""].tracingId + "") == (id + "")) { return obj[i + ""] }}; return null; }
    * def set = function(obj, id, val) { for (i = 0; i < obj.size; i++) { if ((obj[i + ""].tracingId + "") == (id + "")) { obj[i + ""] = val; }}; }
    * def slice = function(obj, i, j) { v  = { size: 0 }; for(;i<obj.size && i < j;i++){ push(v, obj[i + ""]); }; return v; }
    * def clear = function(pdd) { traces[pdd] = {size: 0}; disabled[pdd] = { tracings: false, submit: false, recover: false, replace: false, errors: false, status: false }; }
    * def array = function(obj) { arr = []; for(i=0;i<obj.size;i++){ arr.push(obj[i + ""]); }; return arr; }
	* def current_date = function() { return new Date().toISOString().split('T')[0]; }
	* def response_error = function(code) { return {"type": "string", "status": 500, "title": "Service Unavailable", "correlationId": "53af4f2d-0c87-41ef-a645-b726a821852b", "detail": "Request took too long to complete.","errors": [{"code": code, "detail": "No details available"}]}; }
	* def utils = Java.type('org.openspcoop2.core.protocolli.modipa.testsuite.TestUtils')

	* clear("DemoSoggettoErogatore")
	* clear("DemoSoggettoErogatore2")
	
# ----------------------------------------------
# - SCENARI CHE SIMULANO IL BACKEND DELLA PDND -
# ----------------------------------------------

Scenario: pathMatches('/tracings') && methodIs('GET')
    * def filtered = {}
    * def soggetto = getHeader('pdd')
    * if (paramExists('states')) filtered = filter(traces[soggetto], paramValue('states'))
    * if (!paramExists('states')) filtered = traces[soggetto]
    * def filtered_size = filtered.size
    * def page = array(slice(filtered, paramValue('offset'), 1 * paramValue('offset') + 1 * paramValue('limit')))
    
    * eval
 	"""
 		if (disabled[soggetto].tracings) {
    		res = "disabilitato"
    		status = 500
    		karate.fail("Fallimento controllato")
    	} else {
    		res = ({ totalCount: filtered_size, results: page })
    		status = 200
    	}
    """
    
    * def response = res
    * def responseStatus = status

Scenario: pathMatches('/tracings/{id}/errors') && methodIs('GET')
    * def soggetto = getHeader('pdd')
    * def entry = get(traces[soggetto], pathParams.id)
    
    * eval
 	"""
 		if (disabled[soggetto].errors) {
    		res = "disabilitato"
    		status = 500
    		karate.fail("Fallimento controllato")
    	} else if(entry.state == "ERROR") {
    		pdnd_error = { size: 0 }
    		push(pdnd_error, { "purposeId": "string", "errorCode": "string", "message": "string", "rowNumber": 0 })
    		error_data = array(slice(pdnd_error, paramValue('offset'), 1 * paramValue('offset') + 1 * paramValue('limit')))
    		res = ({ results: error_data, totalCount: pdnd_error.size })
    		status = 500
    	} else {
    		res = ({ results: [], totalCount: 0 })
    		status = 200
    	}
    	
    	karate.log(res, status)
    """
 
    * def response = res
    * def responseStatus = status


Scenario: pathMatches('/tracings/{id}/recover') && methodIs('POST')
    * def soggetto = getHeader('pdd')
	* def contentType = getHeader('Content-Type')
	* def trace = utils.getPart(request, contentType, 0)
    * def entry = get(traces[soggetto], pathParams.id)
    
    
    * eval
   	"""
   		if (disabled[soggetto].recover) {
   			status = 500
   			res = response_error('disabled') 
   			karate.fail("Fallimento controllato")
   		} else if (entry == null) {
   			status = 500
   			res = response_error('not present') 
   		} else {
   			entry.state = 'PENDING'
   			entry.content = trace
   			set(traces[soggetto], pathParams.id, entry)
   			
   			res = ({ tracingId: pathParams.id, errors: false})
   			status = 200
   		}
   	"""
   	
    * def response = res
    * def responseStatus = status
     
Scenario: pathMatches('/tracings/{id}/replace') && methodIs('POST')
    * def soggetto = getHeader('pdd')
	* def contentType = getHeader('Content-Type')
	* def trace = utils.getPart(request, contentType, 0)	
    * def entry = get(traces[soggetto], pathParams.id)
    
    * eval
   	"""
   		if (disabled[soggetto].replace) {
   			status = 500
   			res = response_error('disabled') 
   			karate.fail("Fallimento controllato")
   		} else if (entry == null) {
   			status = 500
   			res = response_error('Boh') 
   		} else {
   			entry.state = 'PENDING'
   			entry.content = trace
   			set(traces[soggetto], pathParams.id, entry)
   			
   			res = ({ tracingId: pathParams.id, errors: false})
   			status = 200
   		}
   	"""
   	
    * def response = res
    * def responseStatus = status

Scenario: pathMatches('/tracings/submit') && methodIs('POST')
    * def soggetto = getHeader('pdd')
	* def contentType = getHeader('Content-Type')
	* def now = utils.getPart(request, contentType, 1)
	* def trace = utils.getPart(request, contentType, 0)
	
	* def tracingId = traces[getHeader('pdd')].size
 	
 	* eval
 	"""
 		if (disabled[soggetto].submit) {
 			res = "disabilitato"
    		status = 500
    		karate.fail("Fallimento controllato")
    	} else {
    		push(traces[soggetto], { tracingId: tracingId + "", state: 'PENDING', date: now, content: trace })
    		res = ({ tracingId: tracingId + "", errors: false})
    		status = 200
    	}
    	karate.log(res, status, disabled[soggetto], traces)
    """
    
    * def response = res
    * def responseStatus = status


Scenario: pathMatches('/status') && methodIs('GET')
    * def soggetto = getHeader('pdd')
	* eval
 	"""
 		if (disabled[soggetto].status) {
    		res = "disabilitato"
    		status = 500
    		karate.fail("Fallimento controllato")
    	} else {
    		res = "{}"
    		status = 200
    	}
    """
    
    * def response = res
    * def responseStatus = status
    
# -------------------------------------------------
# - SCENARI CHE CONTROLLANO IL DB DELLA PDND MOCK -
# -------------------------------------------------

Scenario: pathMatches('/control/push')
 	* push(traces[paramValue('pdd')], { tracingId: paramValue('id') + "", state: paramValue('state'), date: paramValue('date'), content: paramValue('content') })
 	* def response = ''
 	* def responseStatus = 200

Scenario: pathMatches('/control/{id}/update')
 	* def entry = get(traces[paramValue('pdd')], pathParams.id)
 	* entry.state = paramValue('state')
 	* set(traces[paramValue('pdd')], pathParams.id, entry)
	* def response = 'ok'
 	* def responseStatus = 200
 	
Scenario: pathMatches('/control/{id}/get')
 	* def entry = get(traces[paramValue('pdd')], pathParams.id)
 	* def response = (entry['content'])
 	* def responseStatus = 200

Scenario: pathMatches('/control/state')
 	* def response = errors
 	* def responseStatus = 200
 	* if(errors != "") responseStatus = 503

Scenario: pathMatches('/control/clear')
	* clear("DemoSoggettoErogatore")
	* clear("DemoSoggettoErogatore2")
	* def response = 'ok'
	* def responseStatus = 200
 	
Scenario: pathMatches('/control/{operation}/disable')
 	* disabled[paramValue('pdd')][pathParams.operation] = true
	* def response = 'ok'
 	* def responseStatus = 200

Scenario: pathMatches('/control/{operation}/enable')
 	* disabled[paramValue('pdd')][pathParams.operation] = false
	* def response = 'ok'
 	* def responseStatus = 200

Scenario:
	* def errors = 'Scenario non riconosciuto'
