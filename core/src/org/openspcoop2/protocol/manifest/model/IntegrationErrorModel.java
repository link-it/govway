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
package org.openspcoop2.protocol.manifest.model;

import org.openspcoop2.protocol.manifest.IntegrationError;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model IntegrationError 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrationErrorModel extends AbstractModel<IntegrationError> {

	public IntegrationErrorModel(){
	
		super();
	
		this.ERROR_CODE = new org.openspcoop2.protocol.manifest.model.IntegrationErrorCodeModel(new Field("errorCode",org.openspcoop2.protocol.manifest.IntegrationErrorCode.class,"IntegrationError",IntegrationError.class));
		this.MESSAGE_TYPE = new Field("messageType",java.lang.String.class,"IntegrationError",IntegrationError.class);
		this.RETRY = new Field("retry",boolean.class,"IntegrationError",IntegrationError.class);
		this.ERROR_MESSAGE = new Field("errorMessage",java.lang.String.class,"IntegrationError",IntegrationError.class);
	
	}
	
	public IntegrationErrorModel(IField father){
	
		super(father);
	
		this.ERROR_CODE = new org.openspcoop2.protocol.manifest.model.IntegrationErrorCodeModel(new ComplexField(father,"errorCode",org.openspcoop2.protocol.manifest.IntegrationErrorCode.class,"IntegrationError",IntegrationError.class));
		this.MESSAGE_TYPE = new ComplexField(father,"messageType",java.lang.String.class,"IntegrationError",IntegrationError.class);
		this.RETRY = new ComplexField(father,"retry",boolean.class,"IntegrationError",IntegrationError.class);
		this.ERROR_MESSAGE = new ComplexField(father,"errorMessage",java.lang.String.class,"IntegrationError",IntegrationError.class);
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.IntegrationErrorCodeModel ERROR_CODE = null;
	 
	public IField MESSAGE_TYPE = null;
	 
	public IField RETRY = null;
	 
	public IField ERROR_MESSAGE = null;
	 

	@Override
	public Class<IntegrationError> getModeledClass(){
		return IntegrationError.class;
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