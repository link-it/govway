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
package eu.domibus.configuration.model;

import eu.domibus.configuration.BusinessProcesses;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model BusinessProcesses 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BusinessProcessesModel extends AbstractModel<BusinessProcesses> {

	public BusinessProcessesModel(){
	
		super();
	
		this.ROLES = new eu.domibus.configuration.model.RolesModel(new Field("roles",eu.domibus.configuration.Roles.class,"businessProcesses",BusinessProcesses.class));
		this.PARTIES = new eu.domibus.configuration.model.PartiesModel(new Field("parties",eu.domibus.configuration.Parties.class,"businessProcesses",BusinessProcesses.class));
		this.MEPS = new eu.domibus.configuration.model.MepsModel(new Field("meps",eu.domibus.configuration.Meps.class,"businessProcesses",BusinessProcesses.class));
		this.PROPERTIES = new eu.domibus.configuration.model.PropertiesModel(new Field("properties",eu.domibus.configuration.Properties.class,"businessProcesses",BusinessProcesses.class));
		this.PAYLOAD_PROFILES = new eu.domibus.configuration.model.PayloadProfilesModel(new Field("payloadProfiles",eu.domibus.configuration.PayloadProfiles.class,"businessProcesses",BusinessProcesses.class));
		this.SECURITIES = new eu.domibus.configuration.model.SecuritiesModel(new Field("securities",eu.domibus.configuration.Securities.class,"businessProcesses",BusinessProcesses.class));
		this.ERROR_HANDLINGS = new eu.domibus.configuration.model.ErrorHandlingsModel(new Field("errorHandlings",eu.domibus.configuration.ErrorHandlings.class,"businessProcesses",BusinessProcesses.class));
		this.AGREEMENTS = new eu.domibus.configuration.model.AgreementsModel(new Field("agreements",eu.domibus.configuration.Agreements.class,"businessProcesses",BusinessProcesses.class));
		this.SERVICES = new eu.domibus.configuration.model.ServicesModel(new Field("services",eu.domibus.configuration.Services.class,"businessProcesses",BusinessProcesses.class));
		this.ACTIONS = new eu.domibus.configuration.model.ActionsModel(new Field("actions",eu.domibus.configuration.Actions.class,"businessProcesses",BusinessProcesses.class));
		this.AS_4 = new eu.domibus.configuration.model.As4Model(new Field("as4",eu.domibus.configuration.As4.class,"businessProcesses",BusinessProcesses.class));
		this.LEG_CONFIGURATIONS = new eu.domibus.configuration.model.LegConfigurationsModel(new Field("legConfigurations",eu.domibus.configuration.LegConfigurations.class,"businessProcesses",BusinessProcesses.class));
		this.PROCESS = new eu.domibus.configuration.model.ProcessModel(new Field("process",eu.domibus.configuration.Process.class,"businessProcesses",BusinessProcesses.class));
	
	}
	
	public BusinessProcessesModel(IField father){
	
		super(father);
	
		this.ROLES = new eu.domibus.configuration.model.RolesModel(new ComplexField(father,"roles",eu.domibus.configuration.Roles.class,"businessProcesses",BusinessProcesses.class));
		this.PARTIES = new eu.domibus.configuration.model.PartiesModel(new ComplexField(father,"parties",eu.domibus.configuration.Parties.class,"businessProcesses",BusinessProcesses.class));
		this.MEPS = new eu.domibus.configuration.model.MepsModel(new ComplexField(father,"meps",eu.domibus.configuration.Meps.class,"businessProcesses",BusinessProcesses.class));
		this.PROPERTIES = new eu.domibus.configuration.model.PropertiesModel(new ComplexField(father,"properties",eu.domibus.configuration.Properties.class,"businessProcesses",BusinessProcesses.class));
		this.PAYLOAD_PROFILES = new eu.domibus.configuration.model.PayloadProfilesModel(new ComplexField(father,"payloadProfiles",eu.domibus.configuration.PayloadProfiles.class,"businessProcesses",BusinessProcesses.class));
		this.SECURITIES = new eu.domibus.configuration.model.SecuritiesModel(new ComplexField(father,"securities",eu.domibus.configuration.Securities.class,"businessProcesses",BusinessProcesses.class));
		this.ERROR_HANDLINGS = new eu.domibus.configuration.model.ErrorHandlingsModel(new ComplexField(father,"errorHandlings",eu.domibus.configuration.ErrorHandlings.class,"businessProcesses",BusinessProcesses.class));
		this.AGREEMENTS = new eu.domibus.configuration.model.AgreementsModel(new ComplexField(father,"agreements",eu.domibus.configuration.Agreements.class,"businessProcesses",BusinessProcesses.class));
		this.SERVICES = new eu.domibus.configuration.model.ServicesModel(new ComplexField(father,"services",eu.domibus.configuration.Services.class,"businessProcesses",BusinessProcesses.class));
		this.ACTIONS = new eu.domibus.configuration.model.ActionsModel(new ComplexField(father,"actions",eu.domibus.configuration.Actions.class,"businessProcesses",BusinessProcesses.class));
		this.AS_4 = new eu.domibus.configuration.model.As4Model(new ComplexField(father,"as4",eu.domibus.configuration.As4.class,"businessProcesses",BusinessProcesses.class));
		this.LEG_CONFIGURATIONS = new eu.domibus.configuration.model.LegConfigurationsModel(new ComplexField(father,"legConfigurations",eu.domibus.configuration.LegConfigurations.class,"businessProcesses",BusinessProcesses.class));
		this.PROCESS = new eu.domibus.configuration.model.ProcessModel(new ComplexField(father,"process",eu.domibus.configuration.Process.class,"businessProcesses",BusinessProcesses.class));
	
	}
	
	

	public eu.domibus.configuration.model.RolesModel ROLES = null;
	 
	public eu.domibus.configuration.model.PartiesModel PARTIES = null;
	 
	public eu.domibus.configuration.model.MepsModel MEPS = null;
	 
	public eu.domibus.configuration.model.PropertiesModel PROPERTIES = null;
	 
	public eu.domibus.configuration.model.PayloadProfilesModel PAYLOAD_PROFILES = null;
	 
	public eu.domibus.configuration.model.SecuritiesModel SECURITIES = null;
	 
	public eu.domibus.configuration.model.ErrorHandlingsModel ERROR_HANDLINGS = null;
	 
	public eu.domibus.configuration.model.AgreementsModel AGREEMENTS = null;
	 
	public eu.domibus.configuration.model.ServicesModel SERVICES = null;
	 
	public eu.domibus.configuration.model.ActionsModel ACTIONS = null;
	 
	public eu.domibus.configuration.model.As4Model AS_4 = null;
	 
	public eu.domibus.configuration.model.LegConfigurationsModel LEG_CONFIGURATIONS = null;
	 
	public eu.domibus.configuration.model.ProcessModel PROCESS = null;
	 

	@Override
	public Class<BusinessProcesses> getModeledClass(){
		return BusinessProcesses.class;
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