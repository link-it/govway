/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

import eu.domibus.configuration.Configuration;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Configuration 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurationModel extends AbstractModel<Configuration> {

	public ConfigurationModel(){
	
		super();
	
		this.MPCS = new eu.domibus.configuration.model.MpcsModel(new Field("mpcs",eu.domibus.configuration.Mpcs.class,"configuration",Configuration.class));
		this.BUSINESS_PROCESSES = new eu.domibus.configuration.model.BusinessProcessesModel(new Field("businessProcesses",eu.domibus.configuration.BusinessProcesses.class,"configuration",Configuration.class));
		this.PARTY = new Field("party",java.lang.String.class,"configuration",Configuration.class);
	
	}
	
	public ConfigurationModel(IField father){
	
		super(father);
	
		this.MPCS = new eu.domibus.configuration.model.MpcsModel(new ComplexField(father,"mpcs",eu.domibus.configuration.Mpcs.class,"configuration",Configuration.class));
		this.BUSINESS_PROCESSES = new eu.domibus.configuration.model.BusinessProcessesModel(new ComplexField(father,"businessProcesses",eu.domibus.configuration.BusinessProcesses.class,"configuration",Configuration.class));
		this.PARTY = new ComplexField(father,"party",java.lang.String.class,"configuration",Configuration.class);
	
	}
	
	

	public eu.domibus.configuration.model.MpcsModel MPCS = null;
	 
	public eu.domibus.configuration.model.BusinessProcessesModel BUSINESS_PROCESSES = null;
	 
	public IField PARTY = null;
	 

	@Override
	public Class<Configuration> getModeledClass(){
		return Configuration.class;
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