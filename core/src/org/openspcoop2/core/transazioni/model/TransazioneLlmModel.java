/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.core.transazioni.model;

import org.openspcoop2.core.transazioni.TransazioneLlm;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TransazioneLlm 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransazioneLlmModel extends AbstractModel<TransazioneLlm> {

	public TransazioneLlmModel(){
	
		super();
	
		this.ID_TRANSAZIONE = new Field("id-transazione",java.lang.String.class,"transazione-llm",TransazioneLlm.class);
		this.DATA_INGRESSO_RICHIESTA = new Field("data-ingresso-richiesta",java.util.Date.class,"transazione-llm",TransazioneLlm.class);
		this.LLM_PROVIDER = new Field("llm-provider",java.lang.String.class,"transazione-llm",TransazioneLlm.class);
		this.LLM_MODEL = new Field("llm-model",java.lang.String.class,"transazione-llm",TransazioneLlm.class);
		this.LLM_PROVIDER_BINDING = new Field("llm-provider-binding",java.lang.String.class,"transazione-llm",TransazioneLlm.class);
		this.TOKEN_INPUT = new Field("token-input",java.lang.Long.class,"transazione-llm",TransazioneLlm.class);
		this.TOKEN_OUTPUT = new Field("token-output",java.lang.Long.class,"transazione-llm",TransazioneLlm.class);
		this.COST_ESTIMATED = new Field("cost-estimated",double.class,"transazione-llm",TransazioneLlm.class);
	
	}
	
	public TransazioneLlmModel(IField father){
	
		super(father);
	
		this.ID_TRANSAZIONE = new ComplexField(father,"id-transazione",java.lang.String.class,"transazione-llm",TransazioneLlm.class);
		this.DATA_INGRESSO_RICHIESTA = new ComplexField(father,"data-ingresso-richiesta",java.util.Date.class,"transazione-llm",TransazioneLlm.class);
		this.LLM_PROVIDER = new ComplexField(father,"llm-provider",java.lang.String.class,"transazione-llm",TransazioneLlm.class);
		this.LLM_MODEL = new ComplexField(father,"llm-model",java.lang.String.class,"transazione-llm",TransazioneLlm.class);
		this.LLM_PROVIDER_BINDING = new ComplexField(father,"llm-provider-binding",java.lang.String.class,"transazione-llm",TransazioneLlm.class);
		this.TOKEN_INPUT = new ComplexField(father,"token-input",java.lang.Long.class,"transazione-llm",TransazioneLlm.class);
		this.TOKEN_OUTPUT = new ComplexField(father,"token-output",java.lang.Long.class,"transazione-llm",TransazioneLlm.class);
		this.COST_ESTIMATED = new ComplexField(father,"cost-estimated",double.class,"transazione-llm",TransazioneLlm.class);
	
	}
	
	

	public IField ID_TRANSAZIONE = null;
	 
	public IField DATA_INGRESSO_RICHIESTA = null;
	 
	public IField LLM_PROVIDER = null;
	 
	public IField LLM_MODEL = null;
	 
	public IField LLM_PROVIDER_BINDING = null;
	 
	public IField TOKEN_INPUT = null;
	 
	public IField TOKEN_OUTPUT = null;
	 
	public IField COST_ESTIMATED = null;
	 

	@Override
	public Class<TransazioneLlm> getModeledClass(){
		return TransazioneLlm.class;
	}
	
	@Override
	public String toString(){
		if(this.getModeledClass()!=null){
			return this.getModeledClass().getName();
		}else{
			return "N.D.";
		}
	}

}