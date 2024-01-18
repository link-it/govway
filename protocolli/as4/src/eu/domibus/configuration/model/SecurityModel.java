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
package eu.domibus.configuration.model;

import eu.domibus.configuration.Security;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Security 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SecurityModel extends AbstractModel<Security> {

	public SecurityModel(){
	
		super();
	
		this.NAME = new Field("name",java.lang.String.class,"security",Security.class);
		this.POLICY = new Field("policy",java.lang.String.class,"security",Security.class);
		this.SIGNATURE_METHOD = new Field("signatureMethod",java.lang.String.class,"security",Security.class);
	
	}
	
	public SecurityModel(IField father){
	
		super(father);
	
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"security",Security.class);
		this.POLICY = new ComplexField(father,"policy",java.lang.String.class,"security",Security.class);
		this.SIGNATURE_METHOD = new ComplexField(father,"signatureMethod",java.lang.String.class,"security",Security.class);
	
	}
	
	

	public IField NAME = null;
	 
	public IField POLICY = null;
	 
	public IField SIGNATURE_METHOD = null;
	 

	@Override
	public Class<Security> getModeledClass(){
		return Security.class;
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