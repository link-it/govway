/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.ConfigurazioneControlloTraffico;
import org.openspcoop2.core.controllo_traffico.beans.DatiTransazione;
import org.openspcoop2.core.controllo_traffico.beans.UniqueIdentifierUtilities;
import org.openspcoop2.core.controllo_traffico.driver.CostantiServizioControlloTraffico;
import org.openspcoop2.core.controllo_traffico.driver.IGestorePolicyAttive;
import org.openspcoop2.core.controllo_traffico.driver.IPolicyGroupByActiveThreads;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyShutdownException;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**     
 * GestorePolicyAttiveWS
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestorePolicyAttiveWS implements IGestorePolicyAttive {

	private static final String IMPL_DESCR = "Implementazione WS IGestorePolicyAttive";
	public static String getImplDescr(){
		return IMPL_DESCR;
	}
	
	private Logger log;
	private PolicyGroupByActiveThreadsType type;
	private String uriService;
	@Override
	public void initialize(Logger log, boolean isStartupGovWay, PolicyGroupByActiveThreadsType type, Object ... params) throws PolicyException{
		this.log = log;
		this.type = type;
		if(this.type==null) {
			this.type = PolicyGroupByActiveThreadsType.LOCAL;
		}
		if(params.length<=0){
			throw new PolicyException("URI Service not found");
		}
		if(params[0] == null || !(params[0] instanceof String)){
			throw new PolicyException("URI Service not found ("+params[0]+")");
		}
		this.uriService = ((String) params[0]).trim();
		if(this.uriService.endsWith("/")==false){
			this.uriService = this.uriService + "/";
		}
	}
	
	@Override
	public PolicyGroupByActiveThreadsType getType() {
		return this.type;
	}
	
	@Override
	public IPolicyGroupByActiveThreads getActiveThreadsPolicy(ActivePolicy activePolicy, DatiTransazione datiTransazione, Object state)
			throws PolicyShutdownException,PolicyException {
		
		try{
			String activeId = UniqueIdentifierUtilities.getUniqueId(activePolicy.getInstanceConfiguration());
			Map<String, List<String>> p = new HashMap<>();
			TransportUtils.setHeader(p,CostantiServizioControlloTraffico.PARAMETER_ACTIVE_ID, activeId);
			String url = TransportUtils.buildUrlWithParameters(p, this.uriService+CostantiServizioControlloTraffico.OPERAZIONE_REGISTER_POLICY, this.log);
			
			this.log.debug("[GestorePolicyAttiveWS.getActiveThreadsPolicy(ActivePolicy)] invoke ("+url+") ...");
			HttpResponse response = HttpUtilities.getHTTPResponse(url);
			this.log.debug("[GestorePolicyAttiveWS.getActiveThreadsPolicy(ActivePolicy)] invoked with code ["+response.getResultHTTPOperation()+"]");
			
			if(response.getResultHTTPOperation()!=200){
				throw new Exception("[httpCode:"+response.getResultHTTPOperation()+"] "+new String(response.getContent()));
			}
			
			PolicyGroupByActiveThreadsWS group = new PolicyGroupByActiveThreadsWS(this.uriService, activeId, this.log);
			return group;
			
		}catch(Exception e){
			this.log.error("getActiveThreadsPolicy(ActivePolicy) error: "+e.getMessage(),e); 
			throw new PolicyException(e.getMessage(),e);
		}
		
	}
	
	@Override
	public IPolicyGroupByActiveThreads getActiveThreadsPolicy(String activeId)
			throws PolicyShutdownException, PolicyException, PolicyNotFoundException {
		try{
			Map<String, List<String>> p = new HashMap<>();
			TransportUtils.setHeader(p,CostantiServizioControlloTraffico.PARAMETER_ACTIVE_ID, activeId);
			String url = TransportUtils.buildUrlWithParameters(p, this.uriService+CostantiServizioControlloTraffico.OPERAZIONE_GET_POLICY, this.log);
			
			this.log.debug("[GestorePolicyAttiveWS.getActiveThreadsPolicy] invoke ("+url+") ...");
			HttpResponse response = HttpUtilities.getHTTPResponse(url);
			this.log.debug("[GestorePolicyAttiveWS.getActiveThreadsPolicy] invoked with code ["+response.getResultHTTPOperation()+"]");
			
			if(response.getResultHTTPOperation()!=200){
				throw new Exception("[httpCode:"+response.getResultHTTPOperation()+"] "+new String(response.getContent()));
			}
			
			PolicyGroupByActiveThreadsWS group = new PolicyGroupByActiveThreadsWS(this.uriService, activeId, this.log);
			return group;
			
		}catch(Exception e){
			this.log.error("getActiveThreadsPolicy(id) error: "+e.getMessage(),e); 
			throw new PolicyException(e.getMessage(),e);
		}
	}
	

	@Override
	public long sizeActivePolicyThreads(boolean sum) throws PolicyShutdownException,PolicyException {
		try{
			Map<String, List<String>> p = new HashMap<>();
			TransportUtils.setHeader(p,CostantiServizioControlloTraffico.PARAMETER_SUM, sum+"");
			String url = TransportUtils.buildUrlWithParameters(p, this.uriService+CostantiServizioControlloTraffico.OPERAZIONE_SIZE_ACTIVE_THREADS_POLICY, this.log);
			
			this.log.debug("[GestorePolicyAttiveWS.sizeActivePolicyThreads] invoke ("+url+") ...");
			HttpResponse response = HttpUtilities.getHTTPResponse(url);
			this.log.debug("[GestorePolicyAttiveWS.sizeActivePolicyThreads] invoked with code ["+response.getResultHTTPOperation()+"]");
			
			if(response.getResultHTTPOperation()!=200){
				throw new Exception("[httpCode:"+response.getResultHTTPOperation()+"] "+new String(response.getContent()));
			}
			
			return Long.parseLong(new String(response.getContent()));
			
		}catch(Exception e){
			this.log.error("sizeActivePolicyThreads error: "+e.getMessage(),e); 
			throw new PolicyException(e.getMessage(),e);
		}
	}
	
	@Override
	public String printKeysPolicy(String separator) throws PolicyShutdownException, PolicyException {
		try{
			Map<String, List<String>> p = new HashMap<>();
			TransportUtils.setHeader(p,CostantiServizioControlloTraffico.PARAMETER_SEPARATOR, separator);
			String url = TransportUtils.buildUrlWithParameters(p, this.uriService+CostantiServizioControlloTraffico.OPERAZIONE_PRINT_KEYS_POLICY, this.log);
			
			this.log.debug("[GestorePolicyAttiveWS.printKeysPolicy] invoke ("+url+") ...");
			HttpResponse response = HttpUtilities.getHTTPResponse(url);
			this.log.debug("[GestorePolicyAttiveWS.printKeysPolicy] invoked with code ["+response.getResultHTTPOperation()+"]");
			
			if(response.getResultHTTPOperation()!=200){
				throw new Exception("[httpCode:"+response.getResultHTTPOperation()+"] "+new String(response.getContent()));
			}
			
			return new String(response.getContent());
			
		}catch(Exception e){
			this.log.error("printKeysPolicy error: "+e.getMessage(),e); 
			throw new PolicyException(e.getMessage(),e);
		}
	}
	
	@Override
	public String printInfoPolicy(String id, String separatorGroups)
			throws PolicyShutdownException, PolicyException, PolicyNotFoundException {
		try{
			Map<String, List<String>> p = new HashMap<>();
			TransportUtils.setHeader(p,CostantiServizioControlloTraffico.PARAMETER_ACTIVE_ID, id);
			TransportUtils.setHeader(p,CostantiServizioControlloTraffico.PARAMETER_SEPARATOR, separatorGroups);
			String url = TransportUtils.buildUrlWithParameters(p, this.uriService+CostantiServizioControlloTraffico.OPERAZIONE_PRINT_INFO_POLICY, this.log);
			
			this.log.debug("[GestorePolicyAttiveWS.printInfoPolicy] invoke ("+url+") ...");
			HttpResponse response = HttpUtilities.getHTTPResponse(url);
			this.log.debug("[GestorePolicyAttiveWS.printInfoPolicy] invoked with code ["+response.getResultHTTPOperation()+"]");
			
			if(response.getResultHTTPOperation()!=200){
				throw new Exception("[httpCode:"+response.getResultHTTPOperation()+"] "+new String(response.getContent()));
			}
			
			return new String(response.getContent());
			
		}catch(Exception e){
			this.log.error("printInfoPolicy error: "+e.getMessage(),e); 
			throw new PolicyException(e.getMessage(),e);
		}
	}
	@Override
	public void removeActiveThreadsPolicy(String idActivePolicy) throws PolicyShutdownException, PolicyException {
		try{
			Map<String, List<String>> p = new HashMap<>();
			TransportUtils.setHeader(p,CostantiServizioControlloTraffico.PARAMETER_ACTIVE_ID, idActivePolicy);
			String url = TransportUtils.buildUrlWithParameters(p, this.uriService+CostantiServizioControlloTraffico.OPERAZIONE_REMOVE_ACTIVE_THREADS_POLICY, this.log);
			
			this.log.debug("[GestorePolicyAttiveWS.removeActiveThreadsPolicy] invoke ("+url+") ...");
			HttpResponse response = HttpUtilities.getHTTPResponse(url);
			this.log.debug("[GestorePolicyAttiveWS.removeActiveThreadsPolicy] invoked with code ["+response.getResultHTTPOperation()+"]");
			
			if(response.getResultHTTPOperation()!=200){
				throw new Exception("[httpCode:"+response.getResultHTTPOperation()+"] "+new String(response.getContent()));
			}
			
		}catch(Exception e){
			this.log.error("removeActiveThreadsPolicy error: "+e.getMessage(),e); 
			throw new PolicyException(e.getMessage(),e);
		}
	}
	
	@Override
	public void removeActiveThreadsPolicyUnsafe(String idActivePolicy) throws PolicyShutdownException,PolicyException{
		try{
			Map<String, List<String>> p = new HashMap<>();
			TransportUtils.setHeader(p,CostantiServizioControlloTraffico.PARAMETER_ACTIVE_ID, idActivePolicy);
			String url = TransportUtils.buildUrlWithParameters(p, this.uriService+CostantiServizioControlloTraffico.OPERAZIONE_REMOVE_ACTIVE_THREADS_POLICY_UNSAFE, this.log);
			
			this.log.debug("[GestorePolicyAttiveWS.removeActiveThreadsPolicyUnsafe] invoke ("+url+") ...");
			HttpResponse response = HttpUtilities.getHTTPResponse(url);
			this.log.debug("[GestorePolicyAttiveWS.removeActiveThreadsPolicyUnsafe] invoked with code ["+response.getResultHTTPOperation()+"]");
			
			if(response.getResultHTTPOperation()!=200){
				throw new Exception("[httpCode:"+response.getResultHTTPOperation()+"] "+new String(response.getContent()));
			}
			
		}catch(Exception e){
			this.log.error("removeActiveThreadsPolicyUnsafe error: "+e.getMessage(),e); 
			throw new PolicyException(e.getMessage(),e);
		}
	}
	
	@Override
	public void removeAllActiveThreadsPolicy() throws PolicyShutdownException, PolicyException {
		try{
			Map<String, List<String>> p = new HashMap<>();
			String url = TransportUtils.buildUrlWithParameters(p, this.uriService+CostantiServizioControlloTraffico.OPERAZIONE_REMOVE_ALL_ACTIVE_THREADS_POLICY, this.log);
			
			this.log.debug("[GestorePolicyAttiveWS.removeAllActiveThreadsPolicy] invoke ("+url+") ...");
			HttpResponse response = HttpUtilities.getHTTPResponse(url);
			this.log.debug("[GestorePolicyAttiveWS.removeAllActiveThreadsPolicy] invoked with code ["+response.getResultHTTPOperation()+"]");
			
			if(response.getResultHTTPOperation()!=200){
				throw new Exception("[httpCode:"+response.getResultHTTPOperation()+"] "+new String(response.getContent()));
			}
			
		}catch(Exception e){
			this.log.error("removeAllActiveThreadsPolicy error: "+e.getMessage(),e); 
			throw new PolicyException(e.getMessage(),e);
		}
	}
	@Override
	public void resetCountersActiveThreadsPolicy(String idActivePolicy)
			throws PolicyShutdownException, PolicyException {
		try{
			Map<String, List<String>> p = new HashMap<>();
			TransportUtils.setHeader(p,CostantiServizioControlloTraffico.PARAMETER_ACTIVE_ID, idActivePolicy);
			String url = TransportUtils.buildUrlWithParameters(p, this.uriService+CostantiServizioControlloTraffico.OPERAZIONE_RESET_COUNTERS_ACTIVE_THREADS_POLICY, this.log);
			
			this.log.debug("[GestorePolicyAttiveWS.resetCountersActiveThreadsPolicy] invoke ("+url+") ...");
			HttpResponse response = HttpUtilities.getHTTPResponse(url);
			this.log.debug("[GestorePolicyAttiveWS.resetCountersActiveThreadsPolicy] invoked with code ["+response.getResultHTTPOperation()+"]");
			
			if(response.getResultHTTPOperation()!=200){
				throw new Exception("[httpCode:"+response.getResultHTTPOperation()+"] "+new String(response.getContent()));
			}
			
		}catch(Exception e){
			this.log.error("resetCountersActiveThreadsPolicy error: "+e.getMessage(),e); 
			throw new PolicyException(e.getMessage(),e);
		}
	}
	@Override
	public void resetCountersAllActiveThreadsPolicy() throws PolicyShutdownException, PolicyException {
		try{
			Map<String, List<String>> p = new HashMap<>();
			String url = TransportUtils.buildUrlWithParameters(p, this.uriService+CostantiServizioControlloTraffico.OPERAZIONE_RESET_COUNTERS_ALL_ACTIVE_THREADS_POLICY, this.log);
			
			this.log.debug("[GestorePolicyAttiveWS.resetCountersAllActiveThreadsPolicy] invoke ("+url+") ...");
			HttpResponse response = HttpUtilities.getHTTPResponse(url);
			this.log.debug("[GestorePolicyAttiveWS.resetCountersAllActiveThreadsPolicy] invoked with code ["+response.getResultHTTPOperation()+"]");
			
			if(response.getResultHTTPOperation()!=200){
				throw new Exception("[httpCode:"+response.getResultHTTPOperation()+"] "+new String(response.getContent()));
			}
			
		}catch(Exception e){
			this.log.error("resetCountersAllActiveThreadsPolicy error: "+e.getMessage(),e); 
			throw new PolicyException(e.getMessage(),e);
		}
	}
	@Override
	public void serialize(OutputStream out) throws PolicyException {
		// nop;
	}
	@Override
	public void initialize(InputStream in, ConfigurazioneControlloTraffico configurazioneControlloTraffico)
			throws PolicyException {
		// nop;
	}
	@Override
	public void cleanOldActiveThreadsPolicy() throws PolicyException{
		// nop;
	}

	
}

