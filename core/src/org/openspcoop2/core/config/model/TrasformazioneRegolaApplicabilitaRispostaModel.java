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

import org.openspcoop2.core.config.TrasformazioneRegolaApplicabilitaRisposta;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TrasformazioneRegolaApplicabilitaRisposta 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TrasformazioneRegolaApplicabilitaRispostaModel extends AbstractModel<TrasformazioneRegolaApplicabilitaRisposta> {

	public TrasformazioneRegolaApplicabilitaRispostaModel(){
	
		super();
	
		this.RETURN_CODE_MIN = new Field("return-code-min",java.lang.Integer.class,"trasformazione-regola-applicabilita-risposta",TrasformazioneRegolaApplicabilitaRisposta.class);
		this.RETURN_CODE_MAX = new Field("return-code-max",java.lang.Integer.class,"trasformazione-regola-applicabilita-risposta",TrasformazioneRegolaApplicabilitaRisposta.class);
		this.CONTENT_TYPE = new Field("content-type",java.lang.String.class,"trasformazione-regola-applicabilita-risposta",TrasformazioneRegolaApplicabilitaRisposta.class);
		this.PATTERN = new Field("pattern",java.lang.String.class,"trasformazione-regola-applicabilita-risposta",TrasformazioneRegolaApplicabilitaRisposta.class);
	
	}
	
	public TrasformazioneRegolaApplicabilitaRispostaModel(IField father){
	
		super(father);
	
		this.RETURN_CODE_MIN = new ComplexField(father,"return-code-min",java.lang.Integer.class,"trasformazione-regola-applicabilita-risposta",TrasformazioneRegolaApplicabilitaRisposta.class);
		this.RETURN_CODE_MAX = new ComplexField(father,"return-code-max",java.lang.Integer.class,"trasformazione-regola-applicabilita-risposta",TrasformazioneRegolaApplicabilitaRisposta.class);
		this.CONTENT_TYPE = new ComplexField(father,"content-type",java.lang.String.class,"trasformazione-regola-applicabilita-risposta",TrasformazioneRegolaApplicabilitaRisposta.class);
		this.PATTERN = new ComplexField(father,"pattern",java.lang.String.class,"trasformazione-regola-applicabilita-risposta",TrasformazioneRegolaApplicabilitaRisposta.class);
	
	}
	
	

	public IField RETURN_CODE_MIN = null;
	 
	public IField RETURN_CODE_MAX = null;
	 
	public IField CONTENT_TYPE = null;
	 
	public IField PATTERN = null;
	 

	@Override
	public Class<TrasformazioneRegolaApplicabilitaRisposta> getModeledClass(){
		return TrasformazioneRegolaApplicabilitaRisposta.class;
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