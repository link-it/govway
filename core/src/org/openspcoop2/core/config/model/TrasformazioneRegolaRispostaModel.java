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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TrasformazioneRegolaRisposta 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TrasformazioneRegolaRispostaModel extends AbstractModel<TrasformazioneRegolaRisposta> {

	public TrasformazioneRegolaRispostaModel(){
	
		super();
	
		this.CONVERSIONE_TEMPLATE = new Field("conversione-template",byte[].class,"trasformazione-regola-risposta",TrasformazioneRegolaRisposta.class);
		this.APPLICABILITA = new org.openspcoop2.core.config.model.TrasformazioneRegolaApplicabilitaRispostaModel(new Field("applicabilita",org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRisposta.class,"trasformazione-regola-risposta",TrasformazioneRegolaRisposta.class));
		this.HEADER = new org.openspcoop2.core.config.model.TrasformazioneRegolaParametroModel(new Field("header",org.openspcoop2.core.config.TrasformazioneRegolaParametro.class,"trasformazione-regola-risposta",TrasformazioneRegolaRisposta.class));
		this.TRASFORMAZIONE_SOAP = new org.openspcoop2.core.config.model.TrasformazioneSoapRispostaModel(new Field("trasformazione-soap",org.openspcoop2.core.config.TrasformazioneSoapRisposta.class,"trasformazione-regola-risposta",TrasformazioneRegolaRisposta.class));
		this.NOME = new Field("nome",java.lang.String.class,"trasformazione-regola-risposta",TrasformazioneRegolaRisposta.class);
		this.POSIZIONE = new Field("posizione",int.class,"trasformazione-regola-risposta",TrasformazioneRegolaRisposta.class);
		this.CONVERSIONE = new Field("conversione",boolean.class,"trasformazione-regola-risposta",TrasformazioneRegolaRisposta.class);
		this.CONVERSIONE_TIPO = new Field("conversione-tipo",java.lang.String.class,"trasformazione-regola-risposta",TrasformazioneRegolaRisposta.class);
		this.CONTENT_TYPE = new Field("content-type",java.lang.String.class,"trasformazione-regola-risposta",TrasformazioneRegolaRisposta.class);
		this.RETURN_CODE = new Field("return-code",java.lang.String.class,"trasformazione-regola-risposta",TrasformazioneRegolaRisposta.class);
	
	}
	
	public TrasformazioneRegolaRispostaModel(IField father){
	
		super(father);
	
		this.CONVERSIONE_TEMPLATE = new ComplexField(father,"conversione-template",byte[].class,"trasformazione-regola-risposta",TrasformazioneRegolaRisposta.class);
		this.APPLICABILITA = new org.openspcoop2.core.config.model.TrasformazioneRegolaApplicabilitaRispostaModel(new ComplexField(father,"applicabilita",org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRisposta.class,"trasformazione-regola-risposta",TrasformazioneRegolaRisposta.class));
		this.HEADER = new org.openspcoop2.core.config.model.TrasformazioneRegolaParametroModel(new ComplexField(father,"header",org.openspcoop2.core.config.TrasformazioneRegolaParametro.class,"trasformazione-regola-risposta",TrasformazioneRegolaRisposta.class));
		this.TRASFORMAZIONE_SOAP = new org.openspcoop2.core.config.model.TrasformazioneSoapRispostaModel(new ComplexField(father,"trasformazione-soap",org.openspcoop2.core.config.TrasformazioneSoapRisposta.class,"trasformazione-regola-risposta",TrasformazioneRegolaRisposta.class));
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"trasformazione-regola-risposta",TrasformazioneRegolaRisposta.class);
		this.POSIZIONE = new ComplexField(father,"posizione",int.class,"trasformazione-regola-risposta",TrasformazioneRegolaRisposta.class);
		this.CONVERSIONE = new ComplexField(father,"conversione",boolean.class,"trasformazione-regola-risposta",TrasformazioneRegolaRisposta.class);
		this.CONVERSIONE_TIPO = new ComplexField(father,"conversione-tipo",java.lang.String.class,"trasformazione-regola-risposta",TrasformazioneRegolaRisposta.class);
		this.CONTENT_TYPE = new ComplexField(father,"content-type",java.lang.String.class,"trasformazione-regola-risposta",TrasformazioneRegolaRisposta.class);
		this.RETURN_CODE = new ComplexField(father,"return-code",java.lang.String.class,"trasformazione-regola-risposta",TrasformazioneRegolaRisposta.class);
	
	}
	
	

	public IField CONVERSIONE_TEMPLATE = null;
	 
	public org.openspcoop2.core.config.model.TrasformazioneRegolaApplicabilitaRispostaModel APPLICABILITA = null;
	 
	public org.openspcoop2.core.config.model.TrasformazioneRegolaParametroModel HEADER = null;
	 
	public org.openspcoop2.core.config.model.TrasformazioneSoapRispostaModel TRASFORMAZIONE_SOAP = null;
	 
	public IField NOME = null;
	 
	public IField POSIZIONE = null;
	 
	public IField CONVERSIONE = null;
	 
	public IField CONVERSIONE_TIPO = null;
	 
	public IField CONTENT_TYPE = null;
	 
	public IField RETURN_CODE = null;
	 

	@Override
	public Class<TrasformazioneRegolaRisposta> getModeledClass(){
		return TrasformazioneRegolaRisposta.class;
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