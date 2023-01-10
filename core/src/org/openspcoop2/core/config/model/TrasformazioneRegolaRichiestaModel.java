/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.TrasformazioneRegolaRichiesta;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TrasformazioneRegolaRichiesta 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TrasformazioneRegolaRichiestaModel extends AbstractModel<TrasformazioneRegolaRichiesta> {

	public TrasformazioneRegolaRichiestaModel(){
	
		super();
	
		this.CONVERSIONE_TEMPLATE = new Field("conversione-template",byte[].class,"trasformazione-regola-richiesta",TrasformazioneRegolaRichiesta.class);
		this.HEADER = new org.openspcoop2.core.config.model.TrasformazioneRegolaParametroModel(new Field("header",org.openspcoop2.core.config.TrasformazioneRegolaParametro.class,"trasformazione-regola-richiesta",TrasformazioneRegolaRichiesta.class));
		this.PARAMETRO_URL = new org.openspcoop2.core.config.model.TrasformazioneRegolaParametroModel(new Field("parametro-url",org.openspcoop2.core.config.TrasformazioneRegolaParametro.class,"trasformazione-regola-richiesta",TrasformazioneRegolaRichiesta.class));
		this.TRASFORMAZIONE_REST = new org.openspcoop2.core.config.model.TrasformazioneRestModel(new Field("trasformazione-rest",org.openspcoop2.core.config.TrasformazioneRest.class,"trasformazione-regola-richiesta",TrasformazioneRegolaRichiesta.class));
		this.TRASFORMAZIONE_SOAP = new org.openspcoop2.core.config.model.TrasformazioneSoapModel(new Field("trasformazione-soap",org.openspcoop2.core.config.TrasformazioneSoap.class,"trasformazione-regola-richiesta",TrasformazioneRegolaRichiesta.class));
		this.CONVERSIONE = new Field("conversione",boolean.class,"trasformazione-regola-richiesta",TrasformazioneRegolaRichiesta.class);
		this.CONVERSIONE_TIPO = new Field("conversione-tipo",java.lang.String.class,"trasformazione-regola-richiesta",TrasformazioneRegolaRichiesta.class);
		this.CONTENT_TYPE = new Field("content-type",java.lang.String.class,"trasformazione-regola-richiesta",TrasformazioneRegolaRichiesta.class);
	
	}
	
	public TrasformazioneRegolaRichiestaModel(IField father){
	
		super(father);
	
		this.CONVERSIONE_TEMPLATE = new ComplexField(father,"conversione-template",byte[].class,"trasformazione-regola-richiesta",TrasformazioneRegolaRichiesta.class);
		this.HEADER = new org.openspcoop2.core.config.model.TrasformazioneRegolaParametroModel(new ComplexField(father,"header",org.openspcoop2.core.config.TrasformazioneRegolaParametro.class,"trasformazione-regola-richiesta",TrasformazioneRegolaRichiesta.class));
		this.PARAMETRO_URL = new org.openspcoop2.core.config.model.TrasformazioneRegolaParametroModel(new ComplexField(father,"parametro-url",org.openspcoop2.core.config.TrasformazioneRegolaParametro.class,"trasformazione-regola-richiesta",TrasformazioneRegolaRichiesta.class));
		this.TRASFORMAZIONE_REST = new org.openspcoop2.core.config.model.TrasformazioneRestModel(new ComplexField(father,"trasformazione-rest",org.openspcoop2.core.config.TrasformazioneRest.class,"trasformazione-regola-richiesta",TrasformazioneRegolaRichiesta.class));
		this.TRASFORMAZIONE_SOAP = new org.openspcoop2.core.config.model.TrasformazioneSoapModel(new ComplexField(father,"trasformazione-soap",org.openspcoop2.core.config.TrasformazioneSoap.class,"trasformazione-regola-richiesta",TrasformazioneRegolaRichiesta.class));
		this.CONVERSIONE = new ComplexField(father,"conversione",boolean.class,"trasformazione-regola-richiesta",TrasformazioneRegolaRichiesta.class);
		this.CONVERSIONE_TIPO = new ComplexField(father,"conversione-tipo",java.lang.String.class,"trasformazione-regola-richiesta",TrasformazioneRegolaRichiesta.class);
		this.CONTENT_TYPE = new ComplexField(father,"content-type",java.lang.String.class,"trasformazione-regola-richiesta",TrasformazioneRegolaRichiesta.class);
	
	}
	
	

	public IField CONVERSIONE_TEMPLATE = null;
	 
	public org.openspcoop2.core.config.model.TrasformazioneRegolaParametroModel HEADER = null;
	 
	public org.openspcoop2.core.config.model.TrasformazioneRegolaParametroModel PARAMETRO_URL = null;
	 
	public org.openspcoop2.core.config.model.TrasformazioneRestModel TRASFORMAZIONE_REST = null;
	 
	public org.openspcoop2.core.config.model.TrasformazioneSoapModel TRASFORMAZIONE_SOAP = null;
	 
	public IField CONVERSIONE = null;
	 
	public IField CONVERSIONE_TIPO = null;
	 
	public IField CONTENT_TYPE = null;
	 

	@Override
	public Class<TrasformazioneRegolaRichiesta> getModeledClass(){
		return TrasformazioneRegolaRichiesta.class;
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