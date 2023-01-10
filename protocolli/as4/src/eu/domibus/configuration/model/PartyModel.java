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
package eu.domibus.configuration.model;

import eu.domibus.configuration.Party;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Party 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PartyModel extends AbstractModel<Party> {

	public PartyModel(){
	
		super();
	
		this.IDENTIFIER = new eu.domibus.configuration.model.IdentifierModel(new Field("identifier",eu.domibus.configuration.Identifier.class,"party",Party.class));
		this.NAME = new Field("name",java.lang.String.class,"party",Party.class);
		this.USER_NAME = new Field("userName",java.lang.String.class,"party",Party.class);
		this.PASSWORD = new Field("password",java.lang.String.class,"party",Party.class);
		this.ENDPOINT = new Field("endpoint",java.net.URI.class,"party",Party.class);
		this.ALLOW_CHUNKING = new Field("allowChunking",java.lang.String.class,"party",Party.class);
	
	}
	
	public PartyModel(IField father){
	
		super(father);
	
		this.IDENTIFIER = new eu.domibus.configuration.model.IdentifierModel(new ComplexField(father,"identifier",eu.domibus.configuration.Identifier.class,"party",Party.class));
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"party",Party.class);
		this.USER_NAME = new ComplexField(father,"userName",java.lang.String.class,"party",Party.class);
		this.PASSWORD = new ComplexField(father,"password",java.lang.String.class,"party",Party.class);
		this.ENDPOINT = new ComplexField(father,"endpoint",java.net.URI.class,"party",Party.class);
		this.ALLOW_CHUNKING = new ComplexField(father,"allowChunking",java.lang.String.class,"party",Party.class);
	
	}
	
	

	public eu.domibus.configuration.model.IdentifierModel IDENTIFIER = null;
	 
	public IField NAME = null;
	 
	public IField USER_NAME = null;
	 
	public IField PASSWORD = null;
	 
	public IField ENDPOINT = null;
	 
	public IField ALLOW_CHUNKING = null;
	 

	@Override
	public Class<Party> getModeledClass(){
		return Party.class;
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