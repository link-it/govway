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

import org.openspcoop2.core.config.IntegrationManager;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model IntegrationManager 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrationManagerModel extends AbstractModel<IntegrationManager> {

	public IntegrationManagerModel(){
	
		super();
	
		this.AUTENTICAZIONE = new Field("autenticazione",java.lang.String.class,"integration-manager",IntegrationManager.class);
	
	}
	
	public IntegrationManagerModel(IField father){
	
		super(father);
	
		this.AUTENTICAZIONE = new ComplexField(father,"autenticazione",java.lang.String.class,"integration-manager",IntegrationManager.class);
	
	}
	
	

	public IField AUTENTICAZIONE = null;
	 

	@Override
	public Class<IntegrationManager> getModeledClass(){
		return IntegrationManager.class;
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