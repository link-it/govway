/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import eu.domibus.configuration.ErrorHandlings;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ErrorHandlings 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ErrorHandlingsModel extends AbstractModel<ErrorHandlings> {

	public ErrorHandlingsModel(){
	
		super();
	
		this.ERROR_HANDLING = new eu.domibus.configuration.model.ErrorHandlingModel(new Field("errorHandling",eu.domibus.configuration.ErrorHandling.class,"errorHandlings",ErrorHandlings.class));
	
	}
	
	public ErrorHandlingsModel(IField father){
	
		super(father);
	
		this.ERROR_HANDLING = new eu.domibus.configuration.model.ErrorHandlingModel(new ComplexField(father,"errorHandling",eu.domibus.configuration.ErrorHandling.class,"errorHandlings",ErrorHandlings.class));
	
	}
	
	

	public eu.domibus.configuration.model.ErrorHandlingModel ERROR_HANDLING = null;
	 

	@Override
	public Class<ErrorHandlings> getModeledClass(){
		return ErrorHandlings.class;
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