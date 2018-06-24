/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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



package org.openspcoop2.protocol.spcoop.validator;

import javax.xml.soap.SOAPFault;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.protocol.basic.BasicStateComponentFactory;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.IValidatoreErrori;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazioneErrori;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;

/**
 * Classe che implementa, in base al protocollo SPCoop, l'interfaccia {@link org.openspcoop2.protocol.sdk.validator.IValidatoreErrori} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopValidatoreErrori extends BasicStateComponentFactory implements IValidatoreErrori {

	/**
	 * Costruttore.
	 *
	 * @param protocolFactory ProtocolFactory
	 * @throws ProtocolException 
	 * 
	 */
	public SPCoopValidatoreErrori(IProtocolFactory<?> protocolFactory,IState state) throws ProtocolException{
		super(protocolFactory,state);
	}
	


	@Override
	public boolean isBustaErrore(Busta busta,OpenSPCoop2Message msgParam,ProprietaValidazioneErrori proprietaValidazioneErrori) {
		
		boolean eccezioneProcessamento = false;
		IProtocolVersionManager protocolManager = null;
		try{
			OpenSPCoop2SoapMessage msg = msgParam.castAsSoap();
			
			if(msg!=null && msg.getSOAPBody()!=null && msg.getSOAPBody().hasFault()){
				SOAPFault soapFault = msg.getSOAPBody().getFault();
				String faultS = soapFault.getFaultString();
				if(faultS!=null)
					faultS = faultS.trim();
				
				if( (SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP).equals(faultS)  ){
					eccezioneProcessamento = true;
				}
			}
		}catch(Exception e){
			if(this.log!=null){
				this.log.error("Errore durante l'analisi per comprendere se un msg e' una busta SPCoop Errore: "+e.getMessage(),e);
			}
		}
		try{
			protocolManager = this.protocolFactory.createProtocolVersionManager(proprietaValidazioneErrori.getVersioneProtocollo());
		}catch(Exception e){
			if(this.log!=null){
				this.log.error("Errore durante la createProtocolVersionManager: "+e.getMessage(),e);
			}
		}
		
		if(eccezioneProcessamento){
			return true;
		}
		else{
			if( protocolManager.isIgnoraEccezioniLivelloNonGrave() ||  proprietaValidazioneErrori.isIgnoraEccezioniNonGravi()){
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
	public boolean isBustaErroreProcessamento(Busta busta,OpenSPCoop2Message msgParam,ProprietaValidazioneErrori proprietaValidazioneErrori){
		
		boolean eccezioneProcessamento = false;
		try{
			OpenSPCoop2SoapMessage msg = msgParam.castAsSoap();
			
			if(msg!=null && msg.getSOAPBody()!=null && msg.getSOAPBody().hasFault()){
				SOAPFault soapFault = msg.getSOAPBody().getFault();
				String faultS = soapFault.getFaultString();
				if(faultS!=null)
					faultS = faultS.trim();
				
				if( (SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP).equals(faultS)  ){
					eccezioneProcessamento = true;
				}
			}
		}catch(Exception e){
			if(this.log!=null){
				this.log.error("Errore durante l'analisi per comprendere se un msg e' una busta SPCoop Errore: "+e.getMessage(),e);
			}
		}
		
		return eccezioneProcessamento;
	}
	
	@Override
	public boolean isBustaErroreIntestazione(Busta busta,OpenSPCoop2Message msgParam,ProprietaValidazioneErrori proprietaValidazioneErrori){
		
		boolean eccezioneProcessamento = false;
		IProtocolVersionManager protocolManager = null;
		try{
			OpenSPCoop2SoapMessage msg = msgParam.castAsSoap();
			
			if(msg!=null && msg.getSOAPBody()!=null && msg.getSOAPBody().hasFault()){
				SOAPFault soapFault = msg.getSOAPBody().getFault();
				String faultS = soapFault.getFaultString();
				if(faultS!=null)
					faultS = faultS.trim();
				
				if( (SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP).equals(faultS)  ){
					eccezioneProcessamento = true;
				}
			}
		}catch(Exception e){
			if(this.log!=null){
				this.log.error("Errore durante l'analisi per comprendere se un msg e' una busta SPCoop Errore: "+e.getMessage(),e);
			}
		}
		
		try{
			protocolManager = this.protocolFactory.createProtocolVersionManager(proprietaValidazioneErrori.getVersioneProtocollo());
		}catch(Exception e){
			if(this.log!=null){
				this.log.error("Errore durante la createProtocolVersionManager: "+e.getMessage(),e);
			}
		}
		
		if(eccezioneProcessamento){
			return false;
		}
		else{
			if( protocolManager.isIgnoraEccezioniLivelloNonGrave() ||  proprietaValidazioneErrori.isIgnoraEccezioniNonGravi()){
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
