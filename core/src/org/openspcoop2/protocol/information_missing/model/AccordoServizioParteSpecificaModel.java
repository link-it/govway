/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.information_missing.model;

import org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AccordoServizioParteSpecifica 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoServizioParteSpecificaModel extends AbstractModel<AccordoServizioParteSpecifica> {

	public AccordoServizioParteSpecificaModel(){
	
		super();
	
		this.CONDITIONS = new org.openspcoop2.protocol.information_missing.model.ConditionsTypeModel(new Field("conditions",org.openspcoop2.protocol.information_missing.ConditionsType.class,"AccordoServizioParteSpecifica",AccordoServizioParteSpecifica.class));
		this.REPLACE_MATCH = new org.openspcoop2.protocol.information_missing.model.ReplaceMatchTypeModel(new Field("replace-match",org.openspcoop2.protocol.information_missing.ReplaceMatchType.class,"AccordoServizioParteSpecifica",AccordoServizioParteSpecifica.class));
		this.HEADER = new org.openspcoop2.protocol.information_missing.model.DescriptionModel(new Field("header",org.openspcoop2.protocol.information_missing.Description.class,"AccordoServizioParteSpecifica",AccordoServizioParteSpecifica.class));
		this.FOOTER = new org.openspcoop2.protocol.information_missing.model.DescriptionModel(new Field("footer",org.openspcoop2.protocol.information_missing.Description.class,"AccordoServizioParteSpecifica",AccordoServizioParteSpecifica.class));
		this.DEFAULT = new org.openspcoop2.protocol.information_missing.model.DefaultModel(new Field("default",org.openspcoop2.protocol.information_missing.Default.class,"AccordoServizioParteSpecifica",AccordoServizioParteSpecifica.class));
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"AccordoServizioParteSpecifica",AccordoServizioParteSpecifica.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"AccordoServizioParteSpecifica",AccordoServizioParteSpecifica.class);
		this.STATO = new Field("stato",java.lang.String.class,"AccordoServizioParteSpecifica",AccordoServizioParteSpecifica.class);
		this.PROTOCOLLO = new Field("protocollo",java.lang.String.class,"AccordoServizioParteSpecifica",AccordoServizioParteSpecifica.class);
	
	}
	
	public AccordoServizioParteSpecificaModel(IField father){
	
		super(father);
	
		this.CONDITIONS = new org.openspcoop2.protocol.information_missing.model.ConditionsTypeModel(new ComplexField(father,"conditions",org.openspcoop2.protocol.information_missing.ConditionsType.class,"AccordoServizioParteSpecifica",AccordoServizioParteSpecifica.class));
		this.REPLACE_MATCH = new org.openspcoop2.protocol.information_missing.model.ReplaceMatchTypeModel(new ComplexField(father,"replace-match",org.openspcoop2.protocol.information_missing.ReplaceMatchType.class,"AccordoServizioParteSpecifica",AccordoServizioParteSpecifica.class));
		this.HEADER = new org.openspcoop2.protocol.information_missing.model.DescriptionModel(new ComplexField(father,"header",org.openspcoop2.protocol.information_missing.Description.class,"AccordoServizioParteSpecifica",AccordoServizioParteSpecifica.class));
		this.FOOTER = new org.openspcoop2.protocol.information_missing.model.DescriptionModel(new ComplexField(father,"footer",org.openspcoop2.protocol.information_missing.Description.class,"AccordoServizioParteSpecifica",AccordoServizioParteSpecifica.class));
		this.DEFAULT = new org.openspcoop2.protocol.information_missing.model.DefaultModel(new ComplexField(father,"default",org.openspcoop2.protocol.information_missing.Default.class,"AccordoServizioParteSpecifica",AccordoServizioParteSpecifica.class));
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"AccordoServizioParteSpecifica",AccordoServizioParteSpecifica.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"AccordoServizioParteSpecifica",AccordoServizioParteSpecifica.class);
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"AccordoServizioParteSpecifica",AccordoServizioParteSpecifica.class);
		this.PROTOCOLLO = new ComplexField(father,"protocollo",java.lang.String.class,"AccordoServizioParteSpecifica",AccordoServizioParteSpecifica.class);
	
	}
	
	

	public org.openspcoop2.protocol.information_missing.model.ConditionsTypeModel CONDITIONS = null;
	 
	public org.openspcoop2.protocol.information_missing.model.ReplaceMatchTypeModel REPLACE_MATCH = null;
	 
	public org.openspcoop2.protocol.information_missing.model.DescriptionModel HEADER = null;
	 
	public org.openspcoop2.protocol.information_missing.model.DescriptionModel FOOTER = null;
	 
	public org.openspcoop2.protocol.information_missing.model.DefaultModel DEFAULT = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField TIPO = null;
	 
	public IField STATO = null;
	 
	public IField PROTOCOLLO = null;
	 

	@Override
	public Class<AccordoServizioParteSpecifica> getModeledClass(){
		return AccordoServizioParteSpecifica.class;
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