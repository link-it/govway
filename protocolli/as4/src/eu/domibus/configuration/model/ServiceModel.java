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

import eu.domibus.configuration.Service;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Service 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServiceModel extends AbstractModel<Service> {

	public ServiceModel(){
	
		super();
	
		this.NAME = new Field("name",java.lang.String.class,"service",Service.class);
		this.VALUE = new Field("value",java.lang.String.class,"service",Service.class);
		this.TYPE = new Field("type",java.lang.String.class,"service",Service.class);
	
	}
	
	public ServiceModel(IField father){
	
		super(father);
	
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"service",Service.class);
		this.VALUE = new ComplexField(father,"value",java.lang.String.class,"service",Service.class);
		this.TYPE = new ComplexField(father,"type",java.lang.String.class,"service",Service.class);
	
	}
	
	

	public IField NAME = null;
	 
	public IField VALUE = null;
	 
	public IField TYPE = null;
	 

	@Override
	public Class<Service> getModeledClass(){
		return Service.class;
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