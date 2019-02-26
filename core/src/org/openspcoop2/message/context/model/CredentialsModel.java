/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.message.context.model;

import org.openspcoop2.message.context.Credentials;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Credentials 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CredentialsModel extends AbstractModel<Credentials> {

	public CredentialsModel(){
	
		super();
	
		this.PRINCIPAL = new Field("principal",java.lang.String.class,"credentials",Credentials.class);
		this.SUBJECT = new Field("subject",java.lang.String.class,"credentials",Credentials.class);
		this.USERNAME = new Field("username",java.lang.String.class,"credentials",Credentials.class);
		this.PASSWORD = new Field("password",java.lang.String.class,"credentials",Credentials.class);
	
	}
	
	public CredentialsModel(IField father){
	
		super(father);
	
		this.PRINCIPAL = new ComplexField(father,"principal",java.lang.String.class,"credentials",Credentials.class);
		this.SUBJECT = new ComplexField(father,"subject",java.lang.String.class,"credentials",Credentials.class);
		this.USERNAME = new ComplexField(father,"username",java.lang.String.class,"credentials",Credentials.class);
		this.PASSWORD = new ComplexField(father,"password",java.lang.String.class,"credentials",Credentials.class);
	
	}
	
	

	public IField PRINCIPAL = null;
	 
	public IField SUBJECT = null;
	 
	public IField USERNAME = null;
	 
	public IField PASSWORD = null;
	 

	@Override
	public Class<Credentials> getModeledClass(){
		return Credentials.class;
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