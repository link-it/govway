/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package eu.domibus.configuration.model;

import eu.domibus.configuration.Securities;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Securities 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SecuritiesModel extends AbstractModel<Securities> {

	public SecuritiesModel(){
	
		super();
	
		this.SECURITY = new eu.domibus.configuration.model.SecurityModel(new Field("security",eu.domibus.configuration.Security.class,"securities",Securities.class));
	
	}
	
	public SecuritiesModel(IField father){
	
		super(father);
	
		this.SECURITY = new eu.domibus.configuration.model.SecurityModel(new ComplexField(father,"security",eu.domibus.configuration.Security.class,"securities",Securities.class));
	
	}
	
	

	public eu.domibus.configuration.model.SecurityModel SECURITY = null;
	 

	@Override
	public Class<Securities> getModeledClass(){
		return Securities.class;
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