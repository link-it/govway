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
package org.openspcoop2.monitor.engine.condition;

import java.util.List;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.exception.ExpressionNotImplementedException;
import org.openspcoop2.generic_project.exception.NotImplementedException;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.expression.IExpression;
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

	public final static String ALL_LABEL  = "[Qualsiasi]";
	public final static String ALL_ERROR_LABEL  = "Fallite";
	public final static String ALL_OK_LABEL  = "Completate con successo";
	public final static String ALL_FAULT_APPLICATIVO_LABEL  = "Fault Applicativo";
	public final static String ALL_PERSONALIZZATO_LABEL  = "Personalizzato";
	
	public final static String ALL_VALUE_AS_STRING = "-";
	
	public final static Integer ALL_VALUE = -1;
	public final static Integer ALL_ERROR_VALUE = -2;
	public final static Integer ALL_OK_VALUE = -3;
	public final static Integer ALL_FAULT_APPLICATIVO_VALUE = -4;
	public final static Integer ALL_PERSONALIZZATO_VALUE = -5;
	
	private Logger logger;
	private EsitiProperties esitiProperties;
	public EsitoUtils(Logger logger) throws ProtocolException{
		this.logger = logger;
		this.esitiProperties = EsitiProperties.getInstance(this.logger);
	}
	
	public void setExpression(IExpression expr,Integer esitoGruppo, Integer esitoDettaglio, Integer[] esitoDettaglioPersonalizzato, String contesto, 
			IField fieldEsito, IField fieldContesto, IExpression newExpression) throws ProtocolException,ExpressionException, ExpressionNotImplementedException, ServiceException, NotImplementedException{
			
		this.setExpression(expr, esitoGruppo, esitoDettaglio, esitoDettaglioPersonalizzato, fieldEsito, newExpression);
		
		this.setExpressionContesto(expr, fieldContesto, contesto);

	}
	
	public void setExpression(IExpression expr,Integer esitoGruppo, Integer esitoDettaglio, Integer[] esitoDettaglioPersonalizzato,
			IField fieldEsito, IExpression newExpression) throws ProtocolException,ExpressionException, ExpressionNotImplementedException, ServiceException, NotImplementedException{
			
		boolean senzaFiltro = (EsitoUtils.ALL_VALUE == esitoGruppo) && (EsitoUtils.ALL_VALUE == esitoDettaglio);
		boolean soloOk = (EsitoUtils.ALL_OK_VALUE == esitoGruppo) && (EsitoUtils.ALL_VALUE == esitoDettaglio);
		boolean faultApplicativo = (EsitoUtils.ALL_FAULT_APPLICATIVO_VALUE == esitoGruppo);
		boolean soloErrori = (EsitoUtils.ALL_ERROR_VALUE == esitoGruppo) && (EsitoUtils.ALL_VALUE == esitoDettaglio);
		boolean personalizzato = (EsitoUtils.ALL_PERSONALIZZATO_VALUE == esitoGruppo);

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
			else if(soloErrori){
				// Troppi valori dentro gli IN
//				List<Integer> esitiKo = esitiProperties.getEsitiCodeKo();
//				expr.and().in(fieldEsito, esitiKo);
				
				IExpression exprOk = newExpression;
				List<Integer> esitiOk = this.esitiProperties.getEsitiCodeOk(); // li prendo tutti anche il fault, poichè faccio il not
				exprOk.and().in(fieldEsito, esitiOk);
				expr.and().not(exprOk);
			}
			else{
				if(esitoDettaglio == ALL_FAULT_APPLICATIVO_VALUE){
					// si tratta del fault, devo trasformarlo nel codice ufficiale
					// Questo caso avviene quando si seleziona qualsiasi esito, e poi come dettaglio il fault
					int codeFaultApplicativo = this.esitiProperties.convertNameToCode(EsitoTransazioneName.ERRORE_APPLICATIVO.name());
					expr.and().equals(fieldEsito, codeFaultApplicativo);
				}
				else{
					expr.and().equals(fieldEsito, esitoDettaglio);
				}
			}
			
		}
		
	}
	
	public void setExpressionContesto(IExpression expr,IField fieldContesto,String contesto) throws ExpressionNotImplementedException, ExpressionException{
		if(EsitoUtils.ALL_VALUE_AS_STRING != contesto){
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
		
		try{
			return this.esitiProperties.convertLabelToCode(label);
		}catch(Exception e){
			this.logger.error("Conversione non riuscita: "+e.getMessage(),e);
			return null;
		}
	}
	
	public String getEsitoLabelFromValue(Object value){
		if(value instanceof Integer){
			
			if(ALL_VALUE == ((Integer)value)){
				return ALL_LABEL;
			}
			else if(ALL_ERROR_VALUE == ((Integer)value)){
				return ALL_ERROR_LABEL;
			}
			else if(ALL_OK_VALUE == ((Integer)value)){
				return ALL_OK_LABEL;
			}
			else if(ALL_FAULT_APPLICATIVO_VALUE == ((Integer)value)){
				return ALL_FAULT_APPLICATIVO_LABEL;
			}
			else if(ALL_PERSONALIZZATO_VALUE == ((Integer)value)){
				return ALL_PERSONALIZZATO_LABEL;
			}
			
			try{
				return this.esitiProperties.getEsitoLabel((Integer)value);
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
				return this.esitiProperties.getEsitoTransactionContextLabel((String)value);
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
