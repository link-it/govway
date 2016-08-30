/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import javax.xml.soap.SOAPFault;

import org.slf4j.Logger;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.MessaggiFaultErroreCooperazione;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazioneErrori;

/**
 * ValidatoreErrori
 * 
 * @author Nardi Lorenzo (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */

public class ValidatoreErrori implements org.openspcoop2.protocol.sdk.validator.IValidatoreErrori {

	/** Logger utilizzato per debug. */
	protected IProtocolFactory protocolFactory;
	private ITraduttore costanti;
	protected Logger log = null;

	public ValidatoreErrori(IProtocolFactory protocolFactory) throws ProtocolException{
		this.protocolFactory = protocolFactory;
		this.costanti = protocolFactory.createTraduttore();
		this.log = protocolFactory.getLogger();
	}
	

	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}

	@Override
	public boolean isBustaErrore(Busta busta,OpenSPCoop2Message msg,ProprietaValidazioneErrori proprietaValidazioneErrori){
		
		boolean eccezioneProcessamento = false;
		try{
			if(msg!=null && msg.getSOAPBody()!=null && msg.getSOAPBody().hasFault()){
				SOAPFault soapFault = msg.getSOAPBody().getFault();
				String faultS = soapFault.getFaultString();
				if(faultS!=null)
					faultS = faultS.trim();
				
				return (this.costanti.toString( MessaggiFaultErroreCooperazione.FAULT_STRING_PROCESSAMENTO).equals(faultS))  || (this.costanti.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE).equals(faultS));
				
			}
		}catch(Exception e){
			this.log.error("Errore durante l'analisi per comprendere se un msg e' una busta Errore: "+e.getMessage(),e);
		}
		
		IProtocolVersionManager pManager = null;
		try{
			pManager = this.protocolFactory.createProtocolVersionManager(proprietaValidazioneErrori.getVersioneProtocollo());
		}catch(Exception e){
			this.log.error("Errore durante la createProtocolManager: "+e.getMessage(),e);
		}
		
		if(eccezioneProcessamento){
			return true;
		}
		else{
			if( pManager.isIgnoraEccezioniLivelloNonGrave() ||  proprietaValidazioneErrori.isIgnoraEccezioniNonGravi()){
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
			if(msg!=null && msg.getSOAPBody()!=null && msg.getSOAPBody().hasFault()){
				SOAPFault soapFault = msg.getSOAPBody().getFault();
				String faultS = soapFault.getFaultString();
				if(faultS!=null)
					faultS = faultS.trim();
				
				if( (this.costanti.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_PROCESSAMENTO).equals(faultS))  ){
					eccezioneProcessamento = true;
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
			if(msg!=null && msg.getSOAPBody()!=null && msg.getSOAPBody().hasFault()){
				SOAPFault soapFault = msg.getSOAPBody().getFault();
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
		}catch(Exception e){
			this.log.error("Errore durante l'analisi per comprendere se un msg e' una busta Errore: "+e.getMessage(),e);
		}
		
		IProtocolVersionManager pManager = null;
		try{
			pManager = this.protocolFactory.createProtocolVersionManager(proprietaValidazioneErrori.getVersioneProtocollo());
		}catch(Exception e){
			this.log.error("Errore durante la createProtocolManager: "+e.getMessage(),e);
		}
		
		if(eccezioneProcessamento){
			return false;
		}
		else{
			if( pManager.isIgnoraEccezioniLivelloNonGrave() ||  proprietaValidazioneErrori.isIgnoraEccezioniNonGravi()){
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
