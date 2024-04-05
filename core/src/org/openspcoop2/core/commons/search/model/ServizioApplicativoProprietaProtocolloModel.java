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
package org.openspcoop2.core.commons.search.model;

import org.openspcoop2.core.commons.search.ServizioApplicativoProprietaProtocollo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ServizioApplicativoProprietaProtocollo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServizioApplicativoProprietaProtocolloModel extends AbstractModel<ServizioApplicativoProprietaProtocollo> {

	public ServizioApplicativoProprietaProtocolloModel(){
	
		super();
	
		this.NAME = new Field("name",java.lang.String.class,"servizio-applicativo-proprieta-protocollo",ServizioApplicativoProprietaProtocollo.class);
		this.VALUE_STRING = new Field("value_string",java.lang.String.class,"servizio-applicativo-proprieta-protocollo",ServizioApplicativoProprietaProtocollo.class);
		this.VALUE_NUMBER = new Field("value_number",long.class,"servizio-applicativo-proprieta-protocollo",ServizioApplicativoProprietaProtocollo.class);
		this.VALUE_BOOLEAN = new Field("value_boolean",int.class,"servizio-applicativo-proprieta-protocollo",ServizioApplicativoProprietaProtocollo.class);
	
	}
	
	public ServizioApplicativoProprietaProtocolloModel(IField father){
	
		super(father);
	
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"servizio-applicativo-proprieta-protocollo",ServizioApplicativoProprietaProtocollo.class);
		this.VALUE_STRING = new ComplexField(father,"value_string",java.lang.String.class,"servizio-applicativo-proprieta-protocollo",ServizioApplicativoProprietaProtocollo.class);
		this.VALUE_NUMBER = new ComplexField(father,"value_number",long.class,"servizio-applicativo-proprieta-protocollo",ServizioApplicativoProprietaProtocollo.class);
		this.VALUE_BOOLEAN = new ComplexField(father,"value_boolean",int.class,"servizio-applicativo-proprieta-protocollo",ServizioApplicativoProprietaProtocollo.class);
	
	}
	
	

	public IField NAME = null;
	 
	public IField VALUE_STRING = null;
	 
	public IField VALUE_NUMBER = null;
	 
	public IField VALUE_BOOLEAN = null;
	 

	@Override
	public Class<ServizioApplicativoProprietaProtocollo> getModeledClass(){
		return ServizioApplicativoProprietaProtocollo.class;
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