/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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




package org.openspcoop2.pdd.core.connettori;

import java.util.Map;

import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.services.DirectVMProtocolInfo;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.messages.DirectVMConnectorInMessage;
import org.openspcoop2.pdd.services.connector.messages.DirectVMConnectorOutMessage;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.IDService;
import org.openspcoop2.utils.date.DateManager;

/**
 * Classe utilizzata per effettuare consegne di messaggi Soap, attraverso
 * l'invocazione di un server http. 
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractConnettoreDirectVM extends ConnettoreBase {

    @Override
	public String getProtocollo() {
    	return "VM";
    }
	
	
	/* ********  METODI  ******** */

	@Override
	protected boolean initializePreSend(ResponseCachingConfigurazione responseCachingConfig, ConnettoreMsg request) {
		
		if(this.initialize(request, true, responseCachingConfig)==false){
			return false;
		}
		
		return true;
		
	}
	
	@Override
	protected boolean send(ConnettoreMsg request) {
	
		// protocol
		IProtocolFactory<?> pFactory = null;
		try{
			pFactory = this.buildProtocolFactoryForForwardMessage(this.properties);
		}catch(Exception e){
			this.errore = this.readExceptionMessageFromException(e);
			return false;
		}
				
		// credenziali
		if(this.credenziali==null){
			String user = this.properties.get(CostantiConnettori.CONNETTORE_USERNAME);
			String password = this.properties.get(CostantiConnettori.CONNETTORE_PASSWORD);
			if(user!=null || password!=null){
				this.credenziali = new InvocazioneCredenziali();
				this.credenziali.setUser(user);
				this.credenziali.setPassword(password);
			}
		}
		
		// Il Validate legge ulteriori proprieta' che poi vengono usate dal buildLocation
		if(this.validate(request)){	
					
			// location
			try{
				if(this.debug)
					this.logger.debug("Creazione URL...");
				this.buildLocation(this.properties,true);
			}catch(Exception e){
				this.errore = this.readExceptionMessageFromException(e);
				return false;
			}
			
			return sendByVM(pFactory);
		}
		else{
			return false;
		}

	}

	private IProtocolFactory<?> buildProtocolFactoryForForwardMessage(Map<String, String> properties) throws Exception{
		// protocol
		IProtocolFactory<?> pFactory = this.getProtocolFactory();
		String protocol = properties.get(CostantiConnettori.CONNETTORE_DIRECT_VM_PROTOCOL);
		if(protocol!=null){
			try{
				pFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocol);
			}catch(Exception e){
				throw new Exception("Proprieta' '"+CostantiConnettori.CONNETTORE_DIRECT_VM_PROTOCOL+"' fornita contiene un tipo di protocollo ["+protocol+"] non valido: "+e.getMessage());
			}
		}
		return pFactory;
	}
	
	public void buildLocation(Map<String, String> properties, boolean setFormBasedParameter) throws Exception{
		
		// protocol
		IProtocolFactory<?> pFactory = buildProtocolFactoryForForwardMessage(properties);
		
		// context
		String webContext = "openspcoop2";
		String context = properties.get(CostantiConnettori.CONNETTORE_DIRECT_VM_CONTEXT);
		if(context!=null){
			webContext = context;
		}
		
		this.location = "/"+webContext+"/"+pFactory.getProtocol()+"/"+this.getFunction();
		String suffix = this.getFunctionParameters();
		if(suffix!=null && !"".equals(suffix)){
			this.location = this.location + "/"+suffix;
		}
		
		if(setFormBasedParameter){
			// Il connettore non possiede possibilita' di consegnare su di una url reale
			//this.location = ConnettoreUtils.buildLocationWithURLBasedParameter(this.requestMsg, this.propertiesUrlBased, this.location);
		}
	}
	

	/**
	 * Si occupa di effettuare la consegna SOAP (invocazione di un WebService).
	 * Si aspetta di ricevere una risposta non sbustata.
	 *
	 * @return true in caso di consegna con successo, false altrimenti
	 * 
	 */
	private boolean sendByVM(IProtocolFactory<?> pFactory){
		
		String oldIdTransazione = null;
		try{
			
			DirectVMProtocolInfo directVMProtocolInfo = new DirectVMProtocolInfo();
			if(this.idMessaggio!=null){
				directVMProtocolInfo.setIdMessaggioRichiesta(this.idMessaggio);
			}
			if(this.getPddContext()!=null){
				Object o = this.getPddContext().getObject(Costanti.ID_TRANSAZIONE);
				directVMProtocolInfo.setIdTransazione(o!=null ? (String)o : null);
			}
			
			PdDContext newPddContext = null;
			String pddContextPreserve = this.properties.get(CostantiConnettori.CONNETTORE_DIRECT_VM_PDD_CONTEXT_PRESERVE);
			if(pddContextPreserve!=null){
				if("true".equalsIgnoreCase(pddContextPreserve.trim())){
					newPddContext = this.getPddContext();
					if(newPddContext.containsKey(Costanti.ID_TRANSAZIONE)){
						oldIdTransazione = (String) newPddContext.removeObject(Costanti.ID_TRANSAZIONE);
					}
				}
			}
			
			Credenziali credenziali = null;
			if(this.credenziali!=null &&
					this.credenziali.getUser()!=null){
				credenziali = new Credenziali();
				credenziali.setUsername(this.credenziali.getUser());
				credenziali.setPassword(this.credenziali.getPassword());
			}
			
			DirectVMConnectorInMessage inMessage = new DirectVMConnectorInMessage(this.requestMsg, 
					this.getIdModuloAsIDService(),
					this.getIdModulo(), 
					this.propertiesTrasporto,
					this.propertiesUrlBased,
					pFactory,
					this.getFunction(), 
					this.location, credenziali,
					this.getFunctionParameters(),
					this.requestInfo.getBindingConfig(),
					directVMProtocolInfo,
					newPddContext);
			OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
			inMessage.setThresholdContext(newPddContext, op2Properties.getDumpBinario_inMemoryThreshold(), op2Properties.getDumpBinario_repository());
			
			DirectVMConnectorOutMessage outMessage = new DirectVMConnectorOutMessage();
			
			this.process(inMessage, outMessage);
			
			this.responseMsg = outMessage.getMessage();
			this.contentLength = outMessage.getContentLength();
			this.propertiesTrasportoRisposta = outMessage.getHeaders();
			this.codice = outMessage.getResponseStatus();
			this.dataAccettazioneRisposta = DateManager.getDate();
			
			
			try{
				if(oldIdTransazione!=null){
					this.getPddContext().addObject(Costanti.ID_TRANSAZIONE, oldIdTransazione);
					oldIdTransazione = null;
				}
			}catch(Exception e){
				this.logger.error("Errore avvenuto durante il ripristino del cluster id: "+e.getMessage(),e);
			}
			try{
				this.getPddContext().removeObject(Costanti.REQUEST_INFO);
				this.getPddContext().addObject(Costanti.REQUEST_INFO,this.requestInfo);
			}catch(Exception e){
				this.logger.error("Errore avvenuto durante il ripristino delle informazioni sulla richiesta: "+e.getMessage(),e);
			}
			
			
			
			
			/* ------------  PostOutRequestHandler ------------- */
			this.postOutRequest();
			
			
			
			// Imposto informazioni di attraversamento VM
			if(outMessage.getDirectVMProtocolInfo()!=null){
				outMessage.getDirectVMProtocolInfo().setInfo(this.getPddContext());
			}
			
			
			
			
//			/* ------------  PreInResponseHandler ------------- */
			// Con connettore Direct non e' utilizzabile
//			this.preInResponse();
//			
//			// Lettura risposta parametri NotifierInputStream per la risposta
//			org.openspcoop.utils.io.notifier.NotifierInputStreamParams notifierInputStreamParams = null;
//			if(this.preInResponseContext!=null){
//				notifierInputStreamParams = this.preInResponseContext.getNotifierInputStreamParams();
//			}
			
			
			return true;
		}  catch(Exception e){ 
			this.eccezioneProcessamento = e;
			this.logger.error("Errore avvenuto durante la consegna SOAP: "+this.readExceptionMessageFromException(e),e);
			this.errore = "Errore avvenuto durante la consegna SOAP: "+this.readExceptionMessageFromException(e);
			return false;
		}	
		finally{
			try{
				if(oldIdTransazione!=null){
					this.getPddContext().addObject(Costanti.ID_TRANSAZIONE, oldIdTransazione);
				}
			}catch(Exception e){
				this.logger.error("Errore avvenuto durante il ripristino del cluster id: "+e.getMessage(),e);
			}
		}
    }
    
	public abstract boolean validate(ConnettoreMsg request);
	public abstract String getFunctionParameters();
	@Override
	public abstract String getIdModulo();
	public abstract IDService getIdModuloAsIDService();
	public abstract String getFunction();
	public abstract void process(DirectVMConnectorInMessage inMessage,DirectVMConnectorOutMessage outMessage) throws ConnectorException;
	
	
	protected String normalizeFunctionParamters(String value){
		if(value==null){
			return null;
		}
		if(value.contains("?")){
			return value.substring(0,value.indexOf("?"));
		}
		else{
			return value;
		}
	}
}





