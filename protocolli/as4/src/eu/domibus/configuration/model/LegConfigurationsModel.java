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

import eu.domibus.configuration.LegConfigurations;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model LegConfigurations 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LegConfigurationsModel extends AbstractModel<LegConfigurations> {

	public LegConfigurationsModel(){
	
		super();
	
		this.LEG_CONFIGURATION = new eu.domibus.configuration.model.LegConfigurationModel(new Field("legConfiguration",eu.domibus.configuration.LegConfiguration.class,"legConfigurations",LegConfigurations.class));
	
	}
	
	public LegConfigurationsModel(IField father){
	
		super(father);
	
		this.LEG_CONFIGURATION = new eu.domibus.configuration.model.LegConfigurationModel(new ComplexField(father,"legConfiguration",eu.domibus.configuration.LegConfiguration.class,"legConfigurations",LegConfigurations.class));
	
	}
	
	

	public eu.domibus.configuration.model.LegConfigurationModel LEG_CONFIGURATION = null;
	 

	@Override
	public Class<LegConfigurations> getModeledClass(){
		return LegConfigurations.class;
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