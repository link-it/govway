/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import org.openspcoop2.core.config.TrasformazioneSoapRisposta;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TrasformazioneSoapRisposta 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TrasformazioneSoapRispostaModel extends AbstractModel<TrasformazioneSoapRisposta> {

	public TrasformazioneSoapRispostaModel(){
	
		super();
	
		this.ENVELOPE_BODY_CONVERSIONE_TEMPLATE = new Field("envelope-body-conversione-template",byte[].class,"trasformazione-soap-risposta",TrasformazioneSoapRisposta.class);
		this.ENVELOPE = new Field("envelope",boolean.class,"trasformazione-soap-risposta",TrasformazioneSoapRisposta.class);
		this.ENVELOPE_AS_ATTACHMENT = new Field("envelope-as-attachment",boolean.class,"trasformazione-soap-risposta",TrasformazioneSoapRisposta.class);
		this.ENVELOPE_BODY_CONVERSIONE_TIPO = new Field("envelope-body-conversione-tipo",java.lang.String.class,"trasformazione-soap-risposta",TrasformazioneSoapRisposta.class);
	
	}
	
	public TrasformazioneSoapRispostaModel(IField father){
	
		super(father);
	
		this.ENVELOPE_BODY_CONVERSIONE_TEMPLATE = new ComplexField(father,"envelope-body-conversione-template",byte[].class,"trasformazione-soap-risposta",TrasformazioneSoapRisposta.class);
		this.ENVELOPE = new ComplexField(father,"envelope",boolean.class,"trasformazione-soap-risposta",TrasformazioneSoapRisposta.class);
		this.ENVELOPE_AS_ATTACHMENT = new ComplexField(father,"envelope-as-attachment",boolean.class,"trasformazione-soap-risposta",TrasformazioneSoapRisposta.class);
		this.ENVELOPE_BODY_CONVERSIONE_TIPO = new ComplexField(father,"envelope-body-conversione-tipo",java.lang.String.class,"trasformazione-soap-risposta",TrasformazioneSoapRisposta.class);
	
	}
	
	

	public IField ENVELOPE_BODY_CONVERSIONE_TEMPLATE = null;
	 
	public IField ENVELOPE = null;
	 
	public IField ENVELOPE_AS_ATTACHMENT = null;
	 
	public IField ENVELOPE_BODY_CONVERSIONE_TIPO = null;
	 

	@Override
	public Class<TrasformazioneSoapRisposta> getModeledClass(){
		return TrasformazioneSoapRisposta.class;
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