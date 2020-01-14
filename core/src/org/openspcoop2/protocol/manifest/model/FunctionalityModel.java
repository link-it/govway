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
package org.openspcoop2.protocol.manifest.model;

import org.openspcoop2.protocol.manifest.Functionality;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Functionality 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FunctionalityModel extends AbstractModel<Functionality> {

	public FunctionalityModel(){
	
		super();
	
		this.DUPLICATE_FILTER = new Field("duplicateFilter",boolean.class,"Functionality",Functionality.class);
		this.ACKNOWLEDGEMENT = new Field("acknowledgement",boolean.class,"Functionality",Functionality.class);
		this.CONVERSATION_IDENTIFIER = new Field("conversationIdentifier",boolean.class,"Functionality",Functionality.class);
		this.REFERENCE_TO_REQUEST_IDENTIFIER = new Field("referenceToRequestIdentifier",boolean.class,"Functionality",Functionality.class);
		this.DELIVERY_ORDER = new Field("deliveryOrder",boolean.class,"Functionality",Functionality.class);
		this.EXPIRATION = new Field("expiration",boolean.class,"Functionality",Functionality.class);
		this.MANIFEST_ATTACHMENTS = new Field("manifestAttachments",boolean.class,"Functionality",Functionality.class);
	
	}
	
	public FunctionalityModel(IField father){
	
		super(father);
	
		this.DUPLICATE_FILTER = new ComplexField(father,"duplicateFilter",boolean.class,"Functionality",Functionality.class);
		this.ACKNOWLEDGEMENT = new ComplexField(father,"acknowledgement",boolean.class,"Functionality",Functionality.class);
		this.CONVERSATION_IDENTIFIER = new ComplexField(father,"conversationIdentifier",boolean.class,"Functionality",Functionality.class);
		this.REFERENCE_TO_REQUEST_IDENTIFIER = new ComplexField(father,"referenceToRequestIdentifier",boolean.class,"Functionality",Functionality.class);
		this.DELIVERY_ORDER = new ComplexField(father,"deliveryOrder",boolean.class,"Functionality",Functionality.class);
		this.EXPIRATION = new ComplexField(father,"expiration",boolean.class,"Functionality",Functionality.class);
		this.MANIFEST_ATTACHMENTS = new ComplexField(father,"manifestAttachments",boolean.class,"Functionality",Functionality.class);
	
	}
	
	

	public IField DUPLICATE_FILTER = null;
	 
	public IField ACKNOWLEDGEMENT = null;
	 
	public IField CONVERSATION_IDENTIFIER = null;
	 
	public IField REFERENCE_TO_REQUEST_IDENTIFIER = null;
	 
	public IField DELIVERY_ORDER = null;
	 
	public IField EXPIRATION = null;
	 
	public IField MANIFEST_ATTACHMENTS = null;
	 

	@Override
	public Class<Functionality> getModeledClass(){
		return Functionality.class;
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