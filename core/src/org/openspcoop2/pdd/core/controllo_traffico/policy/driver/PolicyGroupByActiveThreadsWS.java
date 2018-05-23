package org.openspcoop2.pdd.core.controllo_traffico.policy.driver;

import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

import org.openspcoop2.core.controllo_congestione.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_congestione.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_congestione.beans.MisurazioniTransazione;
import org.openspcoop2.core.controllo_congestione.driver.CostantiServizioControlloTraffico;
import org.openspcoop2.core.controllo_congestione.driver.IPolicyGroupByActiveThreads;
import org.openspcoop2.core.controllo_congestione.driver.PolicyException;
import org.openspcoop2.core.controllo_congestione.driver.PolicyNotFoundException;

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
