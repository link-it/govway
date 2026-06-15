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
package org.openspcoop2.core.statistiche.model;

import org.openspcoop2.core.statistiche.StatisticaGiornalieraLlm;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model StatisticaGiornalieraLlm 
 *
 * @author Poli Andrea (poli@link.it)
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticaGiornalieraLlmModel extends AbstractModel<StatisticaGiornalieraLlm> {

	public StatisticaGiornalieraLlmModel(){
	
		super();
	
		this.DATA = new Field("data",java.util.Date.class,"statistica-giornaliera-llm",StatisticaGiornalieraLlm.class);
		this.LLM_PROVIDER = new Field("llm-provider",java.lang.String.class,"statistica-giornaliera-llm",StatisticaGiornalieraLlm.class);
		this.LLM_MODEL = new Field("llm-model",java.lang.String.class,"statistica-giornaliera-llm",StatisticaGiornalieraLlm.class);
		this.LLM_PROVIDER_BINDING = new Field("llm-provider-binding",java.lang.String.class,"statistica-giornaliera-llm",StatisticaGiornalieraLlm.class);
		this.TOKEN_INPUT = new Field("token-input",java.lang.Long.class,"statistica-giornaliera-llm",StatisticaGiornalieraLlm.class);
		this.TOKEN_OUTPUT = new Field("token-output",java.lang.Long.class,"statistica-giornaliera-llm",StatisticaGiornalieraLlm.class);
		this.COST_ESTIMATED = new Field("cost-estimated",double.class,"statistica-giornaliera-llm",StatisticaGiornalieraLlm.class);
	
	}
	
	public StatisticaGiornalieraLlmModel(IField father){
	
		super(father);
	
		this.DATA = new ComplexField(father,"data",java.util.Date.class,"statistica-giornaliera-llm",StatisticaGiornalieraLlm.class);
		this.LLM_PROVIDER = new ComplexField(father,"llm-provider",java.lang.String.class,"statistica-giornaliera-llm",StatisticaGiornalieraLlm.class);
		this.LLM_MODEL = new ComplexField(father,"llm-model",java.lang.String.class,"statistica-giornaliera-llm",StatisticaGiornalieraLlm.class);
		this.LLM_PROVIDER_BINDING = new ComplexField(father,"llm-provider-binding",java.lang.String.class,"statistica-giornaliera-llm",StatisticaGiornalieraLlm.class);
		this.TOKEN_INPUT = new ComplexField(father,"token-input",java.lang.Long.class,"statistica-giornaliera-llm",StatisticaGiornalieraLlm.class);
		this.TOKEN_OUTPUT = new ComplexField(father,"token-output",java.lang.Long.class,"statistica-giornaliera-llm",StatisticaGiornalieraLlm.class);
		this.COST_ESTIMATED = new ComplexField(father,"cost-estimated",double.class,"statistica-giornaliera-llm",StatisticaGiornalieraLlm.class);
	
	}
	
	

	public IField DATA = null;
	 
	public IField LLM_PROVIDER = null;
	 
	public IField LLM_MODEL = null;
	 
	public IField LLM_PROVIDER_BINDING = null;
	 
	public IField TOKEN_INPUT = null;
	 
	public IField TOKEN_OUTPUT = null;
	 
	public IField COST_ESTIMATED = null;
	 

	@Override
	public Class<StatisticaGiornalieraLlm> getModeledClass(){
		return StatisticaGiornalieraLlm.class;
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