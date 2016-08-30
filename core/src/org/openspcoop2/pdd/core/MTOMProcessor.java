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
package org.openspcoop2.pdd.core;

import org.slf4j.Logger;
import org.openspcoop2.core.config.constants.MTOMProcessorType;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.MTOMProcessorConfig;
import org.openspcoop2.pdd.config.MessageSecurityConfig;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.constants.TipoTraccia;

/**
 * MTOMProcessor
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MTOMProcessor {

	private MTOMProcessorConfig config;
	private MessageSecurityConfig secConfig;
	private TipoPdD tipoPdD;
	private MsgDiagnostico msgDiag;
	private Logger log;
	private PdDContext pddContext;
	
	public MTOMProcessor(MTOMProcessorConfig config, MessageSecurityConfig secConfig, TipoPdD tipoPdD, 
			MsgDiagnostico msgDiag, Logger log, PdDContext pddContext){
		this.config = config;
		this.secConfig = secConfig;
		this.tipoPdD = tipoPdD;
		this.msgDiag = msgDiag;
		this.log = log;
		this.pddContext = pddContext;
	}
	
	
	public MTOMProcessorType getMTOMProcessorType(){
		if(this.config!=null)
			return this.config.getMtomProcessorType();
		else
			return null;
	}
	
	public void mtomBeforeSecurity(OpenSPCoop2Message msg,TipoTraccia tipo) throws Exception{
		
		boolean emitDiagDisabled = false;
		
		if(this.isEngineEnabled()){
			
			if(this.isMTOMBeforeSecurity(tipo)){
				
				this.setProcessorTypeIntoDiagnostic(tipo);
				
				this.emitDiagnostic(tipo, 
						"mtom.processamentoRichiestaInCorso",
						"mtom.processamentoRispostaInCorso");
				
				try{
					
					this.mtomApply(msg);
					
					this.emitDiagnostic(tipo, 
							"mtom.processamentoRichiestaEffettuato",
							"mtom.processamentoRispostaEffettuato");
					
				}catch(Exception e){
					
					this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
					this.log.error("[MTOM BeforeSecurity "+tipo.getTipo()+"] "+e.getMessage(),e);
					
					this.emitDiagnostic(tipo, 
							"mtom.processamentoRichiestaInErrore",
							"mtom.processamentoRispostaInErrore");
					
					throw e;
				}
				
			}
			else{
				emitDiagDisabled = true;
			}
			
		}
		else{
			emitDiagDisabled = true;
		}
		
		if(emitDiagDisabled){
			this.emitDiagnostic(tipo, 
					"mtom.beforeSecurity.processamentoRichiestaDisabilitato", 
					"mtom.beforeSecurity.processamentoRispostaDisabilitato");			
		}
		
	}
	
	public void mtomAfterSecurity(OpenSPCoop2Message msg,TipoTraccia tipo) throws Exception{
		
		boolean emitDiagDisabled = false;
		
		if(this.isEngineEnabled()){
			
			if(this.isMTOMBeforeSecurity(tipo)==false){
				
				this.setProcessorTypeIntoDiagnostic(tipo);
				
				this.emitDiagnostic(tipo, 
						"mtom.processamentoRichiestaInCorso",
						"mtom.processamentoRispostaInCorso");
				
				try{
					
					this.mtomApply(msg);
					
					this.emitDiagnostic(tipo, 
							"mtom.processamentoRichiestaEffettuato",
							"mtom.processamentoRispostaEffettuato");
					
				}catch(Exception e){
					
					this.msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, e.getMessage());
					this.log.error("[MTOM AfterSecurity "+tipo.getTipo()+"] "+e.getMessage(),e);
					
					this.emitDiagnostic(tipo, 
							"mtom.processamentoRichiestaInErrore",
							"mtom.processamentoRispostaInErrore");
					
					throw e;
				}
				
			}
			else{
				emitDiagDisabled = true;
			}
			
		}
		else{
			emitDiagDisabled = true;
		}
		
		if(emitDiagDisabled){
			this.emitDiagnostic(tipo, 
					"mtom.afterSecurity.processamentoRichiestaDisabilitato", 
					"mtom.afterSecurity.processamentoRispostaDisabilitato");			
		}
		
	}
	
	
	/* **** UTILITIES INTERNE ***** */
	
	private void setProcessorTypeIntoDiagnostic(TipoTraccia tipo){
		switch (tipo) {
		case RICHIESTA:
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_PROCESSAMENTO_MTOM_RICHIESTA, this.config.getMtomProcessorType().getValue());
			this.pddContext.addObject(CostantiPdD.KEY_TIPO_PROCESSAMENTO_MTOM_RICHIESTA, this.config.getMtomProcessorType().getValue());
			break;
		case RISPOSTA:
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_PROCESSAMENTO_MTOM_RISPOSTA, this.config.getMtomProcessorType().getValue());
			this.pddContext.addObject(CostantiPdD.KEY_TIPO_PROCESSAMENTO_MTOM_RISPOSTA, this.config.getMtomProcessorType().getValue());
			break;
		}	
	}
	
	private void emitDiagnostic(TipoTraccia tipo, String idDiagnosticRichiesta, String idDiagnosticRisposta){
		
		// Il set del prefisso viene fatto poichè il processor viene usato anche in moduli (es. LocalForward) dove non è correttamente impostato
		
		String originalPrefix = this.msgDiag.getPrefixMsgPersonalizzati();
		try{
			switch (this.tipoPdD) {
			case DELEGATA:
				this.msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE);
				break;
			case APPLICATIVA:
				this.msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE);
				break;
			default:
				return; // nessun diagnostico
			}
			
			switch (tipo) {
			case RICHIESTA:
				this.msgDiag.logPersonalizzato(idDiagnosticRichiesta);
				break;
			case RISPOSTA:
				this.msgDiag.logPersonalizzato(idDiagnosticRisposta);
				break;
			}	
		}finally{
			this.msgDiag.setPrefixMsgPersonalizzati(originalPrefix);
		}
	}
	
	private void mtomApply(OpenSPCoop2Message msg) throws Exception{
		switch (this.config.getMtomProcessorType()) {
		case PACKAGING:
			
			if(this.config.getInfo()!=null && this.config.getInfo().size()>0){
				msg.mtomPackaging(this.config.getInfo());
			}
			
			break;
			
		case UNPACKAGING:
			
			msg.mtomUnpackaging();
			
			break;
			
		case VERIFY:
			
			if(this.config.getInfo()!=null && this.config.getInfo().size()>0){
				msg.mtomVerify(this.config.getInfo());
			}
			
			break;

		default:
			break;
		}
	}
	
	private boolean isMTOMBeforeSecurity(TipoTraccia tipoTraccia) throws Exception{
		
		MTOMProcessorType processorType = null;
		if(this.config!=null && this.config.getMtomProcessorType()!=null){
			processorType = this.config.getMtomProcessorType();
		}
		else{
			processorType = MTOMProcessorType.DISABLE;
		}
		
		// NOTA: per default la sicurezza viene sempre applicato prima del processo di packaging 
		//		e dopo il processo di unpackaging trattando di fatto l'MTOM come un mero trasporto.
		Boolean applyToMtom = null;
		if(this.secConfig!=null && this.secConfig.getApplyToMtom()!=null){
			applyToMtom = this.secConfig.getApplyToMtom();
		}
		
		
		switch (this.tipoPdD) {
		
		case DELEGATA:
		
			switch (tipoTraccia) {
			
			case RICHIESTA:
				
				switch (processorType) {
				
				case DISABLE:
					// caso che non puo' avvenire grazie al metodo isEngineEnabled
					throw new Exception("Caso non previsto ["+processorType+"] Delegata.richiesta.disabile");
					
				case PACKAGING:
					if(applyToMtom==null){
						return false; // (role:sender) primo cifro/firmo e poi applico packaging, in pratica mtom e' un mero trasporto (come se venisse chiamato sul connettore)
					}
					else if(applyToMtom){
						return true; // (role:sender) prima applico il packaging in modo da applicare la sicurezza sul messaggio mtom
					}else{
						return false; // (role:sender) prima cifro/firmo e poi applico packaging, in pratica mtom e' un mero trasporto
					}
					
				case UNPACKAGING:
					if(applyToMtom==null){
						return false; // (role:sender) primo cifro/firmo e poi applico packaging, in pratica mtom e' un mero trasporto (come se venisse chiamato sul connettore)
					}
					else if(applyToMtom){
						return false; // (role:sender) primo cifro/firmo e poi effettuo unpacking. Scenario senza senso !!!!
					}else{
						return true; // (role:sender) prima applico unpackaging poi cifro e firmo, in pratica mtom e' un mero trasporto (Sembra poco indicato al contesto della PdD)
					}
					
				case VERIFY: 
					return true; // (role:sender) prima verifico le references e poi applico la sicurezza (in modo da leggere eventuali elementi che saranno poi cifrati)

				default:
					throw new Exception("Caso non previsto Delegata.richiesta.["+processorType+"]");
				}
				
			case RISPOSTA:
				
				switch (processorType) {
				
				case DISABLE:
					// caso che non puo' avvenire grazie al metodo isEngineEnabled
					throw new Exception("Caso non previsto ["+processorType+"] Delegata.risposta.disabile");
					
				case PACKAGING:
					if(applyToMtom==null){
						return true; // (role:receiver) prima applico il packaging e solo dopo verifico firma cifratura, in pratica mtom e' un mero trasporto (come se venisse chiamato sul connettore)
					}
					else if(applyToMtom){
						return true; // (role:receiver) prima applico il packaging e solo dopo verifico firma e cifratura
					}else{
						return false;  // (role:receiver) prima verifico firma cifratura, poi applico il packaging
					}
					
				case UNPACKAGING:
					if(applyToMtom==null){
						return true; // (role:receiver) primo applico l'unpackaging e solo dopo verifico firma cifratura, in pratica mtom e' un mero trasporto (come se venisse chiamato sul connettore)
					}
					else if(applyToMtom){
						return false; // (role:receiver) prima verifico firma cifratura, poi applico il l'unpackaging
					}else{
						return true; // (role:receiver) prima applico l'unpackaging e solo dopo verifico firma e cifratura
					}
					
				case VERIFY:
					return true; // (role:receiver) prima applico la sicurezza e solo dopo verifico (in modo da leggere eventuali elementi cifrati)

				default:
					throw new Exception("Caso non previsto Delegata.risposta.["+processorType+"]");
				}
					
			default:
				throw new Exception("Tipo non gestito ["+tipoTraccia+"] in Delegata.risposta");
			}
			
			
		case APPLICATIVA:
			
			switch (tipoTraccia) {
			
			case RICHIESTA:
				
				switch (processorType) {
				
				case DISABLE:
					// caso che non puo' avvenire grazie al metodo isEngineEnabled
					throw new Exception("Caso non previsto ["+processorType+"] Applicativa.richiesta.disabile");
					
				case PACKAGING:
					if(applyToMtom==null){
						return true; // (role:receiver) prima applico il packaging e solo dopo verifico firma cifratura, in pratica mtom e' un mero trasporto (come se venisse chiamato sul connettore)
					}
					else if(applyToMtom){
						return true; // (role:receiver) prima applico il packaging e solo dopo verifico firma e cifratura
					}else{
						return false;  // (role:receiver) prima verifico firma cifratura, poi applico il packaging
					}
					
				case UNPACKAGING:
					if(applyToMtom==null){
						return true; // (role:receiver) primo applico l'unpackaging e solo dopo verifico firma cifratura, in pratica mtom e' un mero trasporto (come se venisse chiamato sul connettore)
					}
					else if(applyToMtom){
						return false; // (role:receiver) prima verifico firma cifratura, poi applico il l'unpackaging
					}else{
						return true; // (role:receiver) prima applico l'unpackaging e solo dopo verifico firma e cifratura
					}
					
				case VERIFY:
					return true; // (role:receiver) prima applico la sicurezza e solo dopo verifico (in modo da leggere eventuali elementi cifrati)

				default:
					throw new Exception("Caso non previsto Applicativa.richiesta.["+processorType+"]");
				}
				
			case RISPOSTA:
				
				switch (processorType) {
				
				case DISABLE:
					// caso che non puo' avvenire grazie al metodo isEngineEnabled
					throw new Exception("Caso non previsto ["+processorType+"] Applicativa.risposta.disabile");
					
				case PACKAGING:
					if(applyToMtom==null){
						return false; // (role:sender) primo cifro/firmo e poi applico packaging, in pratica mtom e' un mero trasporto (come se venisse chiamato sul connettore)
					}
					else if(applyToMtom){
						return true; // (role:sender) prima applico il packaging in modo da applicare la sicurezza sul messaggio mtom
					}else{
						return false; // (role:sender) prima cifro/firmo e poi applico packaging, in pratica mtom e' un mero trasporto
					}
					
				case UNPACKAGING:
					if(applyToMtom==null){
						return false; // (role:sender) primo cifro/firmo e poi applico packaging, in pratica mtom e' un mero trasporto (come se venisse chiamato sul connettore)
					}
					else if(applyToMtom){
						return false; // (role:sender) primo cifro/firmo e poi effettuo unpacking. Scenario senza senso !!!!
					}else{
						return true; // (role:sender) prima applico unpackaging poi cifro e firmo, in pratica mtom e' un mero trasporto (Sembra poco indicato al contesto della PdD)
					}
					
				case VERIFY: 
					return true; // (role:sender) prima verifico le references e poi applico la sicurezza (in modo da leggere eventuali elementi che saranno poi cifrati)

				default:
					throw new Exception("Caso non previsto Applicativa.risposta.["+processorType+"]");
				}
					
			default:
				throw new Exception("Tipo non gestito ["+tipoTraccia+"] in Applicativa.risposta");
			}
			
		case INTEGRATION_MANAGER:
		case ROUTER:
		default:
				throw new Exception("RuoloPdD["+this.tipoPdD+"] non gestito");
			
		}
		
		
	}
	
	private boolean isEngineEnabled(){
		if(!TipoPdD.DELEGATA.equals(this.tipoPdD) && !TipoPdD.APPLICATIVA.equals(this.tipoPdD)){
			return false;
		}
		if(this.config!=null && this.config.getMtomProcessorType()!=null && !MTOMProcessorType.DISABLE.equals(this.config.getMtomProcessorType())){
			return true;
		}
		return false;
	}
}
