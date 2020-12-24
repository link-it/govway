/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.pdd.core.controllo_traffico.policy.driver;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.driver.CostantiServizioControlloTraffico;
import org.openspcoop2.core.controllo_traffico.driver.IPolicyGroupByActiveThreads;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**     
 * PolicyGroupByActiveThreadsWS
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PolicyGroupByActiveThreadsWS implements IPolicyGroupByActiveThreads {

	private String uriService;
	private String activeId;
	private Logger log;
	
	public PolicyGroupByActiveThreadsWS(String serviceUrl,String activeId,Logger log){
		this.uriService = serviceUrl;
		this.activeId = activeId;
		this.log = log;
	}
	
	@Override
	public DatiCollezionati registerStartRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy)
			throws PolicyException {
		try{
			Map<String, String> p = new HashMap<String, String>();
			p.put(CostantiServizioControlloTraffico.PARAMETER_ACTIVE_ID, this.activeId);
			p.put(CostantiServizioControlloTraffico.PARAMETER_GROUP_BY_ID, IDUnivocoGroupByPolicy.serialize(datiGroupBy));
			String url = TransportUtils.buildLocationWithURLBasedParameter(p, this.uriService+CostantiServizioControlloTraffico.OPERAZIONE_REGISTER_START_REQUEST);
			
			this.log.debug("[PolicyGroupByActiveThreadsWS.registerStartRequest] invoke ("+url+") ...");
			HttpResponse response = HttpUtilities.getHTTPResponse(url);
			this.log.debug("[PolicyGroupByActiveThreadsWS.registerStartRequest] invoked with code ["+response.getResultHTTPOperation()+"]");
			
			if(response.getResultHTTPOperation()!=200){
				throw new Exception("[httpCode:"+response.getResultHTTPOperation()+"] "+new String(response.getContent()));
			}
			
			return DatiCollezionati.deserialize(new String(response.getContent()));
						
		}catch(Exception e){
			this.log.error("registerStartRequest error: "+e.getMessage(),e); 
			throw new PolicyException(e.getMessage(),e);
		}
	}

	@Override
	public DatiCollezionati updateDatiStartRequestApplicabile(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy)
			throws PolicyException, PolicyNotFoundException {
		try{
			Map<String, String> p = new HashMap<String, String>();
			p.put(CostantiServizioControlloTraffico.PARAMETER_ACTIVE_ID, this.activeId);
			p.put(CostantiServizioControlloTraffico.PARAMETER_GROUP_BY_ID, IDUnivocoGroupByPolicy.serialize(datiGroupBy));
			String url = TransportUtils.buildLocationWithURLBasedParameter(p, this.uriService+CostantiServizioControlloTraffico.OPERAZIONE_UPDATE_START_REQUEST);
			
			this.log.debug("[PolicyGroupByActiveThreadsWS.updateDatiStartRequestApplicabile] invoke ("+url+") ...");
			HttpResponse response = HttpUtilities.getHTTPResponse(url);
			this.log.debug("[PolicyGroupByActiveThreadsWS.updateDatiStartRequestApplicabile] invoked with code ["+response.getResultHTTPOperation()+"]");
			
			if(response.getResultHTTPOperation()!=200){
				throw new Exception("[httpCode:"+response.getResultHTTPOperation()+"] "+new String(response.getContent()));
			}
			
			return DatiCollezionati.deserialize(new String(response.getContent()));
						
		}catch(Exception e){
			this.log.error("updateDatiStartRequestApplicabile error: "+e.getMessage(),e); 
			throw new PolicyException(e.getMessage(),e);
		}
	}

	@Override
	public void registerStopRequest(Logger log, String idTransazione, IDUnivocoGroupByPolicy datiGroupBy, MisurazioniTransazione dati,
			boolean isApplicabile, boolean isViolata) throws PolicyException, PolicyNotFoundException {
		try{
			Map<String, String> p = new HashMap<String, String>();
			p.put(CostantiServizioControlloTraffico.PARAMETER_ACTIVE_ID, this.activeId);
			p.put(CostantiServizioControlloTraffico.PARAMETER_GROUP_BY_ID, IDUnivocoGroupByPolicy.serialize(datiGroupBy));
			p.put(CostantiServizioControlloTraffico.PARAMETER_MISURAZIONI_TRANSAZIONE, MisurazioniTransazione.serialize(dati));
			p.put(CostantiServizioControlloTraffico.PARAMETER_APPLICABILE, isApplicabile+"");
			p.put(CostantiServizioControlloTraffico.PARAMETER_VIOLATA, isViolata+"");
			String url = TransportUtils.buildLocationWithURLBasedParameter(p, this.uriService+CostantiServizioControlloTraffico.OPERAZIONE_REGISTER_STOP_REQUEST);
			
			this.log.debug("[PolicyGroupByActiveThreadsWS.registerStopRequest] invoke ("+url+") ...");
			HttpResponse response = HttpUtilities.getHTTPResponse(url);
			this.log.debug("[PolicyGroupByActiveThreadsWS.registerStopRequest] invoked with code ["+response.getResultHTTPOperation()+"]");
			
			if(response.getResultHTTPOperation()!=200){
				throw new Exception("[httpCode:"+response.getResultHTTPOperation()+"] "+new String(response.getContent()));
			}
						
		}catch(Exception e){
			this.log.error("registerStopRequest error: "+e.getMessage(),e); 
			throw new PolicyException(e.getMessage(),e);
		}
	}

}
