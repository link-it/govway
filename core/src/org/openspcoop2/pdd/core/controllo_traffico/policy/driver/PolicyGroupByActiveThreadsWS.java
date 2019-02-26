/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_traffico.driver.CostantiServizioControlloTraffico;
import org.openspcoop2.core.controllo_traffico.driver.IPolicyGroupByActiveThreads;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;

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
	public DatiCollezionati registerStartRequest(Logger log, IDUnivocoGroupByPolicy datiGroupBy)
			throws PolicyException {
		try{
			Properties p = new Properties();
			p.setProperty(CostantiServizioControlloTraffico.PARAMETER_ACTIVE_ID, this.activeId);
			p.setProperty(CostantiServizioControlloTraffico.PARAMETER_GROUP_BY_ID, IDUnivocoGroupByPolicy.serialize(datiGroupBy));
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
	public DatiCollezionati updateDatiStartRequestApplicabile(Logger log, IDUnivocoGroupByPolicy datiGroupBy)
			throws PolicyException, PolicyNotFoundException {
		try{
			Properties p = new Properties();
			p.setProperty(CostantiServizioControlloTraffico.PARAMETER_ACTIVE_ID, this.activeId);
			p.setProperty(CostantiServizioControlloTraffico.PARAMETER_GROUP_BY_ID, IDUnivocoGroupByPolicy.serialize(datiGroupBy));
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
	public void registerStopRequest(Logger log, IDUnivocoGroupByPolicy datiGroupBy, MisurazioniTransazione dati,
			boolean isApplicabile) throws PolicyException, PolicyNotFoundException {
		try{
			Properties p = new Properties();
			p.setProperty(CostantiServizioControlloTraffico.PARAMETER_ACTIVE_ID, this.activeId);
			p.setProperty(CostantiServizioControlloTraffico.PARAMETER_GROUP_BY_ID, IDUnivocoGroupByPolicy.serialize(datiGroupBy));
			p.setProperty(CostantiServizioControlloTraffico.PARAMETER_MISURAZIONI_TRANSAZIONE, MisurazioniTransazione.serialize(dati));
			p.setProperty(CostantiServizioControlloTraffico.PARAMETER_APPLICABILE, isApplicabile+"");
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
