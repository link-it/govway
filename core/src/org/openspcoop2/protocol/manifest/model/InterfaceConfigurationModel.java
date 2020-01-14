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

import org.openspcoop2.protocol.manifest.InterfaceConfiguration;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model InterfaceConfiguration 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InterfaceConfigurationModel extends AbstractModel<InterfaceConfiguration> {

	public InterfaceConfigurationModel(){
	
		super();
	
		this.TYPE = new Field("type",java.lang.String.class,"InterfaceConfiguration",InterfaceConfiguration.class);
		this.SCHEMA = new Field("schema",boolean.class,"InterfaceConfiguration",InterfaceConfiguration.class);
		this.CONVERSATIONS = new Field("conversations",boolean.class,"InterfaceConfiguration",InterfaceConfiguration.class);
		this.IMPLEMENTATION = new Field("implementation",boolean.class,"InterfaceConfiguration",InterfaceConfiguration.class);
	
	}
	
	public InterfaceConfigurationModel(IField father){
	
		super(father);
	
		this.TYPE = new ComplexField(father,"type",java.lang.String.class,"InterfaceConfiguration",InterfaceConfiguration.class);
		this.SCHEMA = new ComplexField(father,"schema",boolean.class,"InterfaceConfiguration",InterfaceConfiguration.class);
		this.CONVERSATIONS = new ComplexField(father,"conversations",boolean.class,"InterfaceConfiguration",InterfaceConfiguration.class);
		this.IMPLEMENTATION = new ComplexField(father,"implementation",boolean.class,"InterfaceConfiguration",InterfaceConfiguration.class);
	
	}
	
	

	public IField TYPE = null;
	 
	public IField SCHEMA = null;
	 
	public IField CONVERSATIONS = null;
	 
	public IField IMPLEMENTATION = null;
	 

	@Override
	public Class<InterfaceConfiguration> getModeledClass(){
		return InterfaceConfiguration.class;
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