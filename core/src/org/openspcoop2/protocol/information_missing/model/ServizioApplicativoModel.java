/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.openspcoop2.protocol.information_missing.ServizioApplicativo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ServizioApplicativo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServizioApplicativoModel extends AbstractModel<ServizioApplicativo> {

	public ServizioApplicativoModel(){
	
		super();
	
		this.CONDITIONS = new org.openspcoop2.protocol.information_missing.model.ConditionsTypeModel(new Field("conditions",org.openspcoop2.protocol.information_missing.ConditionsType.class,"ServizioApplicativo",ServizioApplicativo.class));
		this.REPLACE_MATCH = new org.openspcoop2.protocol.information_missing.model.ReplaceMatchTypeModel(new Field("replace-match",org.openspcoop2.protocol.information_missing.ReplaceMatchType.class,"ServizioApplicativo",ServizioApplicativo.class));
		this.HEADER = new org.openspcoop2.protocol.information_missing.model.DescriptionModel(new Field("header",org.openspcoop2.protocol.information_missing.Description.class,"ServizioApplicativo",ServizioApplicativo.class));
		this.FOOTER = new org.openspcoop2.protocol.information_missing.model.DescriptionModel(new Field("footer",org.openspcoop2.protocol.information_missing.Description.class,"ServizioApplicativo",ServizioApplicativo.class));
		this.DEFAULT = new org.openspcoop2.protocol.information_missing.model.DefaultModel(new Field("default",org.openspcoop2.protocol.information_missing.Default.class,"ServizioApplicativo",ServizioApplicativo.class));
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"ServizioApplicativo",ServizioApplicativo.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"ServizioApplicativo",ServizioApplicativo.class);
		this.PROTOCOLLO = new Field("protocollo",java.lang.String.class,"ServizioApplicativo",ServizioApplicativo.class);
	
	}
	
	public ServizioApplicativoModel(IField father){
	
		super(father);
	
		this.CONDITIONS = new org.openspcoop2.protocol.information_missing.model.ConditionsTypeModel(new ComplexField(father,"conditions",org.openspcoop2.protocol.information_missing.ConditionsType.class,"ServizioApplicativo",ServizioApplicativo.class));
		this.REPLACE_MATCH = new org.openspcoop2.protocol.information_missing.model.ReplaceMatchTypeModel(new ComplexField(father,"replace-match",org.openspcoop2.protocol.information_missing.ReplaceMatchType.class,"ServizioApplicativo",ServizioApplicativo.class));
		this.HEADER = new org.openspcoop2.protocol.information_missing.model.DescriptionModel(new ComplexField(father,"header",org.openspcoop2.protocol.information_missing.Description.class,"ServizioApplicativo",ServizioApplicativo.class));
		this.FOOTER = new org.openspcoop2.protocol.information_missing.model.DescriptionModel(new ComplexField(father,"footer",org.openspcoop2.protocol.information_missing.Description.class,"ServizioApplicativo",ServizioApplicativo.class));
		this.DEFAULT = new org.openspcoop2.protocol.information_missing.model.DefaultModel(new ComplexField(father,"default",org.openspcoop2.protocol.information_missing.Default.class,"ServizioApplicativo",ServizioApplicativo.class));
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"ServizioApplicativo",ServizioApplicativo.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"ServizioApplicativo",ServizioApplicativo.class);
		this.PROTOCOLLO = new ComplexField(father,"protocollo",java.lang.String.class,"ServizioApplicativo",ServizioApplicativo.class);
	
	}
	
	

	public org.openspcoop2.protocol.information_missing.model.ConditionsTypeModel CONDITIONS = null;
	 
	public org.openspcoop2.protocol.information_missing.model.ReplaceMatchTypeModel REPLACE_MATCH = null;
	 
	public org.openspcoop2.protocol.information_missing.model.DescriptionModel HEADER = null;
	 
	public org.openspcoop2.protocol.information_missing.model.DescriptionModel FOOTER = null;
	 
	public org.openspcoop2.protocol.information_missing.model.DefaultModel DEFAULT = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField TIPO = null;
	 
	public IField PROTOCOLLO = null;
	 

	@Override
	public Class<ServizioApplicativo> getModeledClass(){
		return ServizioApplicativo.class;
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