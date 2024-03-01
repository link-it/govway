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
package org.openspcoop2.monitor.engine.condition;

import java.util.List;

import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
import org.openspcoop2.pdd.logger.transazioni.TransazioneUtilities;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.slf4j.Logger;

/**
 * EsitoUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EsitoUtils {

	public static final String ALL_LABEL  = "[Qualsiasi]";
	public static final String ALL_ERROR_LABEL  = "Fallite";
	public static final String ALL_OK_LABEL  = "Completate con successo";
	public static final String ALL_FAULT_APPLICATIVO_LABEL  = "Fault Applicativo";
	public static final String ALL_PERSONALIZZATO_LABEL  = "Personalizzato";
	public static final String ALL_ERROR_FAULT_APPLICATIVO_LABEL  = "Fallite - Fault Applicativo";
	public static final String ALL_ERROR_CONSEGNA_LABEL  = "Errori di Consegna";
	public static final String ALL_ERROR_RICHIESTE_SCARTATE_LABEL  = "Richieste Scartate";
	
	public static final String ALL_VALUE_AS_STRING = "-";
	
	public static final Integer ALL_VALUE = -1;
	public static final Integer ALL_ERROR_VALUE = -2;
	public static final Integer ALL_OK_VALUE = -3;
	public static final Integer ALL_FAULT_APPLICATIVO_VALUE = -4;
	public static final Integer ALL_PERSONALIZZATO_VALUE = -5;
	public static final Integer ALL_ERROR_FAULT_APPLICATIVO_VALUE = -6;
	public static final Integer ALL_ERROR_CONSEGNA_VALUE = -7;
	public static final Integer ALL_ERROR_RICHIESTE_SCARTATE_VALUE = -8;
	
	public static final boolean DEFAULT_VALUE_ESCLUDI_RICHIESTE_SCARTATE = true;
	
	public static final String LABEL_ESITO_CONSEGNA_MULTIPLA_SENZA_STATI = "Consegna Multipla";
	
	private Logger logger;
	private EsitiProperties esitiProperties;
	public EsitoUtils(Logger logger,String protocollo) throws ProtocolException{
		this.logger = logger;
		this.esitiProperties = EsitiProperties.getInstanceFromProtocolName(this.logger,protocollo);
	}
	
	public void setExpression(IExpression expr,Integer esitoGruppo, Integer esitoDettaglio, Integer[] esitoDettaglioPersonalizzato, String contesto , boolean escludiRichiesteScartate, 
			IField fieldEsito, IField fieldContesto, IExpression newExpression) throws ProtocolException,ExpressionException, ExpressionNotImplementedException, ServiceException, NotImplementedException{
			
		this.setExpression(expr, esitoGruppo, esitoDettaglio, esitoDettaglioPersonalizzato, escludiRichiesteScartate, fieldEsito, newExpression);
		
		this.setExpressionContesto(expr, fieldContesto, contesto);

	}
	
	public void setExpression(IExpression expr,Integer esitoGruppo, Integer esitoDettaglio, Integer[] esitoDettaglioPersonalizzato, boolean escludiRichiesteScartate,
			IField fieldEsito, IExpression newExpression) throws ProtocolException,ExpressionException, ExpressionNotImplementedException, ServiceException, NotImplementedException{
			
		boolean senzaFiltro = 
				(esitoGruppo!=null && EsitoUtils.ALL_VALUE.intValue() == esitoGruppo.intValue()) 
				&& 
				(esitoDettaglio!=null && EsitoUtils.ALL_VALUE.intValue() == esitoDettaglio.intValue());
		boolean soloOk = 
				(esitoGruppo!=null && EsitoUtils.ALL_OK_VALUE.intValue() == esitoGruppo.intValue()) 
				&& 
				(esitoDettaglio!=null && EsitoUtils.ALL_VALUE.intValue() == esitoDettaglio.intValue());
		boolean faultApplicativo = 
				(esitoGruppo!=null && EsitoUtils.ALL_FAULT_APPLICATIVO_VALUE.intValue() == esitoGruppo.intValue());
		boolean soloErrori = 
				(esitoGruppo!=null && EsitoUtils.ALL_ERROR_VALUE.intValue() == esitoGruppo.intValue()) 
				&& 
				(esitoDettaglio!=null && EsitoUtils.ALL_VALUE.intValue() == esitoDettaglio.intValue());
		boolean personalizzato = 
				(esitoGruppo!=null && EsitoUtils.ALL_PERSONALIZZATO_VALUE.intValue() == esitoGruppo.intValue());
		boolean soloErroriPiuFaultApplicativi = 
				(esitoGruppo!=null && EsitoUtils.ALL_ERROR_FAULT_APPLICATIVO_VALUE.intValue() == esitoGruppo.intValue()) 
				&& 
				(esitoDettaglio!=null && EsitoUtils.ALL_VALUE.intValue() == esitoDettaglio.intValue());
		boolean soloErroriConsegna = 
				(esitoGruppo!=null && EsitoUtils.ALL_ERROR_CONSEGNA_VALUE.intValue() == esitoGruppo.intValue()) 
				&& 
				(esitoDettaglio!=null && EsitoUtils.ALL_VALUE.intValue() == esitoDettaglio.intValue());
		boolean soloRichiesteScartate = 
				(esitoGruppo!=null && EsitoUtils.ALL_ERROR_RICHIESTE_SCARTATE_VALUE.intValue() == esitoGruppo.intValue()) 
				&& 
				(esitoDettaglio!=null && EsitoUtils.ALL_VALUE.intValue() == esitoDettaglio.intValue());
		
		if(senzaFiltro && escludiRichiesteScartate) {
			senzaFiltro = false;
		}
		
		if(!senzaFiltro){
			
			if(personalizzato){
				if(esitoDettaglioPersonalizzato==null || esitoDettaglioPersonalizzato.length<=0){
					throw new ServiceException("Esito Personalizzato richiede la selezione di almeno un dettaglio");
				}
				expr.and().in(fieldEsito, (Object[]) esitoDettaglioPersonalizzato);
			}
			else if(soloOk){
				List<Integer> esitiOk = this.esitiProperties.getEsitiCodeOk_senzaFaultApplicativo();
				expr.and().in(fieldEsito, esitiOk);
			}
			else if(faultApplicativo){
				int codeFaultApplicativo = this.esitiProperties.convertNameToCode(EsitoTransazioneName.ERRORE_APPLICATIVO.name());
				expr.and().equals(fieldEsito, codeFaultApplicativo);
			}
			else if(soloErrori || soloErroriPiuFaultApplicativi){
				// Troppi valori dentro gli IN
//				List<Integer> esitiKo = esitiProperties.getEsitiCodeKo();
//				expr.and().in(fieldEsito, esitiKo);
				
				IExpression exprOk = newExpression;
				List<Integer> esitiOk = null;
				if(soloErrori) {
					esitiOk = this.esitiProperties.getEsitiCodeOk(); // li prendo tutti anche il fault, poichè faccio il not
				}
				else {
					esitiOk = this.esitiProperties.getEsitiCodeOk_senzaFaultApplicativo();
				}
				exprOk.and().in(fieldEsito, esitiOk);
				expr.and().not(exprOk);
				
				if(escludiRichiesteScartate) {
					List<Integer> esitiRichiesteMalformate = this.esitiProperties.getEsitiCodeRichiestaScartate();
					IExpression exprRichiesteMalformate = newExpression;
					exprRichiesteMalformate.and().in(fieldEsito, esitiRichiesteMalformate);
					expr.and().not(exprRichiesteMalformate);
				}
			}
			else if(soloErroriConsegna) {
				List<Integer> esitiErroriConsegna = this.esitiProperties.getEsitiCodeErroriConsegna();
				expr.and().in(fieldEsito, esitiErroriConsegna);
			}
			else if(soloRichiesteScartate) {
				List<Integer> esitiRichiesteMalformate = this.esitiProperties.getEsitiCodeRichiestaScartate();
				expr.and().in(fieldEsito, esitiRichiesteMalformate);
			}
			else{
				if(esitoDettaglio!=null && (esitoDettaglio.intValue() == ALL_FAULT_APPLICATIVO_VALUE.intValue())){
					// si tratta del fault, devo trasformarlo nel codice ufficiale
					// Questo caso avviene quando si seleziona qualsiasi esito, e poi come dettaglio il fault
					int codeFaultApplicativo = this.esitiProperties.convertNameToCode(EsitoTransazioneName.ERRORE_APPLICATIVO.name());
					expr.and().equals(fieldEsito, codeFaultApplicativo);
				}
				else if(esitoDettaglio!=null && esitoDettaglio>=0){
					expr.and().equals(fieldEsito, esitoDettaglio);
				}
				else if(escludiRichiesteScartate) {
					List<Integer> esitiRichiesteMalformate = this.esitiProperties.getEsitiCodeRichiestaScartate();
					IExpression exprRichiesteMalformate = newExpression;
					exprRichiesteMalformate.and().in(fieldEsito, esitiRichiesteMalformate);
					expr.and().not(exprRichiesteMalformate);
				}
			}
			
		}
		
	}
	
	public void setExpressionContesto(IExpression expr,IField fieldContesto,String contesto) throws ExpressionNotImplementedException, ExpressionException{
		if(!EsitoUtils.ALL_VALUE_AS_STRING.equals(contesto)){
			expr.and().equals(fieldContesto, contesto);
		}
	}
	
	public Integer getEsitoValueFromLabel(String label){
		if(ALL_LABEL.equals(label)){
			return ALL_VALUE;
		}
		else if(ALL_ERROR_LABEL.equals(label)){
			return ALL_ERROR_VALUE;
		}
		else if(ALL_OK_LABEL.equals(label)){
			return ALL_OK_VALUE;
		}
		else if(ALL_FAULT_APPLICATIVO_LABEL.equals(label)){
			return ALL_FAULT_APPLICATIVO_VALUE;
		}
		else if(ALL_PERSONALIZZATO_LABEL.equals(label)){
			return ALL_PERSONALIZZATO_VALUE;
		}
		else if(ALL_ERROR_FAULT_APPLICATIVO_LABEL.equals(label)){
			return ALL_ERROR_FAULT_APPLICATIVO_VALUE;
		}
		else if(ALL_ERROR_CONSEGNA_LABEL.equals(label)){
			return ALL_ERROR_CONSEGNA_VALUE;
		}
		else if(ALL_ERROR_RICHIESTE_SCARTATE_LABEL.equals(label)){
			return ALL_ERROR_RICHIESTE_SCARTATE_VALUE;
		}
		else if(LABEL_ESITO_CONSEGNA_MULTIPLA_SENZA_STATI.equals(label)) {
			try{
				return this.esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA);
			}catch(Exception e){
				this.logger.error("Conversione non riuscita: "+e.getMessage(),e);
				return null;
			}
		}
		
		try{
			return this.esitiProperties.convertLabelToCode(label);
		}catch(Exception e){
			this.logger.error("Conversione non riuscita: "+e.getMessage(),e);
			return null;
		}
	}
	
	public String getEsitoLabelFromValue(Object value, boolean consegnaMultiplaSenzaVariStati){
		if(value!=null && value instanceof Integer){
			
			if(ALL_VALUE.intValue() == ((Integer)value).intValue()){
				return ALL_LABEL;
			}
			else if(ALL_ERROR_VALUE.intValue() == ((Integer)value).intValue()){
				return ALL_ERROR_LABEL;
			}
			else if(ALL_OK_VALUE.intValue() == ((Integer)value).intValue()){
				return ALL_OK_LABEL;
			}
			else if(ALL_FAULT_APPLICATIVO_VALUE.intValue() == ((Integer)value).intValue()){
				return ALL_FAULT_APPLICATIVO_LABEL;
			}
			else if(ALL_PERSONALIZZATO_VALUE.intValue() == ((Integer)value).intValue()){
				return ALL_PERSONALIZZATO_LABEL;
			}
			else if(ALL_ERROR_FAULT_APPLICATIVO_VALUE.intValue() == ((Integer)value).intValue()){
				return ALL_ERROR_FAULT_APPLICATIVO_LABEL;
			}
			else if(ALL_ERROR_CONSEGNA_VALUE.intValue() == ((Integer)value).intValue()){
				return ALL_ERROR_CONSEGNA_LABEL;
			}
			else if(ALL_ERROR_RICHIESTE_SCARTATE_VALUE.intValue() == ((Integer)value).intValue()){
				return ALL_ERROR_RICHIESTE_SCARTATE_LABEL;
			}
			
			
			try{
				int valueInt = (Integer)value;
				if(consegnaMultiplaSenzaVariStati) {
					int consegnaMultipla = this.esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA);
					if(consegnaMultipla == valueInt) {
						return LABEL_ESITO_CONSEGNA_MULTIPLA_SENZA_STATI;
					}
				}
				return this.esitiProperties.getEsitoLabel(valueInt);
			}catch(Exception e){
				this.logger.error("Conversione non riuscita: "+e.getMessage(),e);
				return "Conversione non riuscita";
			}
		}
		else{
			this.logger.error("Conversione non riuscita: tipo ["+(value!=null ? value.getClass().getName() : "value is null")+"] non supportato");
			return "Conversione non riuscita";
		}
	}
	
	public String getEsitoContestoValueFromLabel(String label){
		
		if(ALL_LABEL.equals(label)){
			return ALL_VALUE_AS_STRING;
		}
		
		try{
			return this.esitiProperties.convertLabelToContextTypeCode(label);
		}catch(Exception e){
			this.logger.error("Conversione non riuscita: "+e.getMessage(),e);
			return null;
		}
	}
	
	public String getEsitoContestoLabelFromValue(Object value){
		if(value instanceof String){
			
			if(ALL_VALUE_AS_STRING.equals(value)){
				return ALL_LABEL;
			}
			
			try{
				boolean moreContext = this.esitiProperties.getEsitiTransactionContextCode().size()>1;
				
				String code = (String)value;
				
				String label = null;
				if(TransazioneUtilities.isFaseRequestIn(code)) {
					label = (moreContext ? this.esitiProperties.getEsitoTransactionContextLabel(TransazioneUtilities.getRawEsitoContext(code))+" - " : "" ) + CostantiLabel.LABEL_CONFIGURAZIONE_AVANZATA_REQ_IN;
				}
				else if(TransazioneUtilities.isFaseRequestOut(code)) {
					label = (moreContext ? this.esitiProperties.getEsitoTransactionContextLabel(TransazioneUtilities.getRawEsitoContext(code))+" - " : "" ) + CostantiLabel.LABEL_CONFIGURAZIONE_AVANZATA_REQ_OUT;
				}
				else if(TransazioneUtilities.isFaseResponseOut(code)) {
					label = (moreContext ? this.esitiProperties.getEsitoTransactionContextLabel(TransazioneUtilities.getRawEsitoContext(code))+" - " : "" ) + CostantiLabel.LABEL_CONFIGURAZIONE_AVANZATA_RES_OUT;
				}
				else {
					label = this.esitiProperties.getEsitoTransactionContextLabel(code);
				}
				
				return label;
			}catch(Exception e){
				this.logger.error("Conversione non riuscita: "+e.getMessage(),e);
				return "Conversione non riuscita";
			}
		}
		else{
			this.logger.error("Conversione non riuscita: tipo ["+value.getClass().getName()+"] non supportato");
			return "Conversione non riuscita";
		}
	}
}
