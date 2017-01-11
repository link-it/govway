/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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

import org.openspcoop2.protocol.manifest.IntegrationErrorCollection;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model IntegrationErrorCollection 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrationErrorCollectionModel extends AbstractModel<IntegrationErrorCollection> {

	public IntegrationErrorCollectionModel(){
	
		super();
	
		this.AUTHENTICATION = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new Field("authentication",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.AUTHORIZATION = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new Field("authorization",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.NOT_FOUND = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new Field("notFound",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.BAD_REQUEST = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new Field("badRequest",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.INTERNAL_ERROR = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new Field("internalError",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.DEFAULT = new org.openspcoop2.protocol.manifest.model.DefaultIntegrationErrorModel(new Field("default",org.openspcoop2.protocol.manifest.DefaultIntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
	
	}
	
	public IntegrationErrorCollectionModel(IField father){
	
		super(father);
	
		this.AUTHENTICATION = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new ComplexField(father,"authentication",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.AUTHORIZATION = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new ComplexField(father,"authorization",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.NOT_FOUND = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new ComplexField(father,"notFound",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.BAD_REQUEST = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new ComplexField(father,"badRequest",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.INTERNAL_ERROR = new org.openspcoop2.protocol.manifest.model.IntegrationErrorModel(new ComplexField(father,"internalError",org.openspcoop2.protocol.manifest.IntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
		this.DEFAULT = new org.openspcoop2.protocol.manifest.model.DefaultIntegrationErrorModel(new ComplexField(father,"default",org.openspcoop2.protocol.manifest.DefaultIntegrationError.class,"IntegrationErrorCollection",IntegrationErrorCollection.class));
	
	}
	
	

	public org.openspcoop2.protocol.manifest.model.IntegrationErrorModel AUTHENTICATION = null;
	 
	public org.openspcoop2.protocol.manifest.model.IntegrationErrorModel AUTHORIZATION = null;
	 
	public org.openspcoop2.protocol.manifest.model.IntegrationErrorModel NOT_FOUND = null;
	 
	public org.openspcoop2.protocol.manifest.model.IntegrationErrorModel BAD_REQUEST = null;
	 
	public org.openspcoop2.protocol.manifest.model.IntegrationErrorModel INTERNAL_ERROR = null;
	 
	public org.openspcoop2.protocol.manifest.model.DefaultIntegrationErrorModel DEFAULT = null;
	 

	@Override
	public Class<IntegrationErrorCollection> getModeledClass(){
		return IntegrationErrorCollection.class;
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