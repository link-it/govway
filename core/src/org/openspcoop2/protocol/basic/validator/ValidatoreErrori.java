/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.basic.validator;

import jakarta.xml.soap.SOAPFault;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.basic.BasicStateComponentFactory;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.MessaggiFaultErroreCooperazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazioneErrori;

/**
 * ValidatoreErrori
 * 
 * @author Nardi Lorenzo (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */

public class ValidatoreErrori extends BasicStateComponentFactory implements org.openspcoop2.protocol.sdk.validator.IValidatoreErrori {

	private ITraduttore costanti;
	
	public ValidatoreErrori(IProtocolFactory<?> protocolFactory,IState state) throws ProtocolException{
		super(protocolFactory, state);
		this.costanti = protocolFactory.createTraduttore();
	}
	


	@Override
	public boolean isBustaErrore(Busta busta,OpenSPCoop2Message msg,ProprietaValidazioneErrori proprietaValidazioneErrori){
		
		boolean eccezioneProcessamento = false;
		try{
			if(msg!=null && ServiceBinding.SOAP.equals(msg.getServiceBinding())){
				OpenSPCoop2SoapMessage soapMessage = msg.castAsSoap();
				if(soapMessage.hasSOAPFault()){
					SOAPFault soapFault = soapMessage.getSOAPBody().getFault();
					String faultS = soapFault.getFaultString();
					if(faultS!=null)
						faultS = faultS.trim();
					
					eccezioneProcessamento = (this.costanti.toString( MessaggiFaultErroreCooperazione.FAULT_STRING_PROCESSAMENTO).equals(faultS))  || (this.costanti.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE).equals(faultS));
					
				}
			}
		}catch(Exception e){
			this.log.error("Errore durante l'analisi per comprendere se un msg e' una busta Errore: "+e.getMessage(),e);
		}
				
		if(eccezioneProcessamento){
			return true;
		}
		else{
			IProtocolVersionManager pManager = null;
			try{
				pManager = this.protocolFactory.createProtocolVersionManager(proprietaValidazioneErrori.getVersioneProtocollo());
			}catch(Exception e){
				this.log.error("Errore durante la createProtocolManager: "+e.getMessage(),e);
			}
			
			if( 
					(pManager!=null && pManager.isIgnoraEccezioniLivelloNonGrave())
					||  
					proprietaValidazioneErrori.isIgnoraEccezioniNonGravi()
				){
				if(  busta.containsEccezioniGravi() ){
					return true;
				}else{
					return false;
				}
			}else{
				if(  busta.sizeListaEccezioni() > 0 ){
					return true;
				}else{
					return false;
				}
			}
		}
	}
	
	@Override
	public boolean isBustaErroreProcessamento(Busta busta,OpenSPCoop2Message msg,ProprietaValidazioneErrori proprietaValidazioneErrori){
		
		boolean eccezioneProcessamento = false;
		try{
			if(msg!=null && ServiceBinding.SOAP.equals(msg.getServiceBinding())){
				OpenSPCoop2SoapMessage soapMessage = msg.castAsSoap();
				if(soapMessage.hasSOAPFault()){
					SOAPFault soapFault = soapMessage.getSOAPBody().getFault();
					String faultS = soapFault.getFaultString();
					if(faultS!=null)
						faultS = faultS.trim();
					
					if( (this.costanti.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_PROCESSAMENTO).equals(faultS))  ){
						eccezioneProcessamento = true;
					}
				}
			}
		}catch(Exception e){
			this.log.error("Errore durante l'analisi per comprendere se un msg e' una busta Errore: "+e.getMessage(),e);
		}
		
		return eccezioneProcessamento;
	}
	
	@Override
	public boolean isBustaErroreIntestazione(Busta busta,OpenSPCoop2Message msg,ProprietaValidazioneErrori proprietaValidazioneErrori){
		
		boolean eccezioneProcessamento = false;
		try{
			if(msg!=null && ServiceBinding.SOAP.equals(msg.getServiceBinding())){
				OpenSPCoop2SoapMessage soapMessage = msg.castAsSoap();
				if(soapMessage.hasSOAPFault()){
					SOAPFault soapFault = soapMessage.getSOAPBody().getFault();
					String faultS = soapFault.getFaultString();
					if(faultS!=null)
						faultS = faultS.trim();
					
					if( (this.costanti.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE).equals(faultS))  ){
						return true;
					}
					
					if( (this.costanti.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_PROCESSAMENTO).equals(faultS))  ){
						eccezioneProcessamento = true;
					}
				}
			}
		}catch(Exception e){
			this.log.error("Errore durante l'analisi per comprendere se un msg e' una busta Errore: "+e.getMessage(),e);
		}
				
		if(eccezioneProcessamento){
			return false;
		}
		else{
			IProtocolVersionManager pManager = null;
			try{
				pManager = this.protocolFactory.createProtocolVersionManager(proprietaValidazioneErrori.getVersioneProtocollo());
			}catch(Exception e){
				this.log.error("Errore durante la createProtocolManager: "+e.getMessage(),e);
			}
			
			if( 
					(pManager!=null && pManager.isIgnoraEccezioniLivelloNonGrave()) 
					|| 
					proprietaValidazioneErrori.isIgnoraEccezioniNonGravi()
				){
				if(  busta.containsEccezioniGravi() ){
					return true;
				}else{
					return false;
				}
			}else{
				if(  busta.sizeListaEccezioni() > 0 ){
					return true;
				}else{
					return false;
				}
			}
		}
	}
}
