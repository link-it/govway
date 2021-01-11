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
package org.openspcoop2.protocol.manifest.model;

import org.openspcoop2.protocol.manifest.IntegrationConfigurationElementName;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model IntegrationConfigurationElementName 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrationConfigurationElementNameModel extends AbstractModel<IntegrationConfigurationElementName> {

	public IntegrationConfigurationElementNameModel(){
	
		super();
	
		this.PREFIX = new Field("prefix",java.lang.String.class,"IntegrationConfigurationElementName",IntegrationConfigurationElementName.class);
		this.ACTOR = new Field("actor",java.lang.String.class,"IntegrationConfigurationElementName",IntegrationConfigurationElementName.class);
		this.SUFFIX = new Field("suffix",java.lang.String.class,"IntegrationConfigurationElementName",IntegrationConfigurationElementName.class);
	
	}
	
	public IntegrationConfigurationElementNameModel(IField father){
	
		super(father);
	
		this.PREFIX = new ComplexField(father,"prefix",java.lang.String.class,"IntegrationConfigurationElementName",IntegrationConfigurationElementName.class);
		this.ACTOR = new ComplexField(father,"actor",java.lang.String.class,"IntegrationConfigurationElementName",IntegrationConfigurationElementName.class);
		this.SUFFIX = new ComplexField(father,"suffix",java.lang.String.class,"IntegrationConfigurationElementName",IntegrationConfigurationElementName.class);
	
	}
	
	

	public IField PREFIX = null;
	 
	public IField ACTOR = null;
	 
	public IField SUFFIX = null;
	 

	@Override
	public Class<IntegrationConfigurationElementName> getModeledClass(){
		return IntegrationConfigurationElementName.class;
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