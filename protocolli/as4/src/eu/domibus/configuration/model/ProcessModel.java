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
package eu.domibus.configuration.model;

import eu.domibus.configuration.Process;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Process 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProcessModel extends AbstractModel<Process> {

	public ProcessModel(){
	
		super();
	
		this.INITIATOR_PARTIES = new eu.domibus.configuration.model.InitiatorPartiesModel(new Field("initiatorParties",eu.domibus.configuration.InitiatorParties.class,"process",Process.class));
		this.RESPONDER_PARTIES = new eu.domibus.configuration.model.ResponderPartiesModel(new Field("responderParties",eu.domibus.configuration.ResponderParties.class,"process",Process.class));
		this.LEGS = new eu.domibus.configuration.model.LegsModel(new Field("legs",eu.domibus.configuration.Legs.class,"process",Process.class));
		this.NAME = new Field("name",java.lang.String.class,"process",Process.class);
		this.RESPONDER_ROLE = new Field("responderRole",java.lang.String.class,"process",Process.class);
		this.AGREEMENT = new Field("agreement",java.lang.String.class,"process",Process.class);
		this.BINDING = new Field("binding",java.lang.String.class,"process",Process.class);
		this.MEP = new Field("mep",java.lang.String.class,"process",Process.class);
		this.INITIATOR_ROLE = new Field("initiatorRole",java.lang.String.class,"process",Process.class);
	
	}
	
	public ProcessModel(IField father){
	
		super(father);
	
		this.INITIATOR_PARTIES = new eu.domibus.configuration.model.InitiatorPartiesModel(new ComplexField(father,"initiatorParties",eu.domibus.configuration.InitiatorParties.class,"process",Process.class));
		this.RESPONDER_PARTIES = new eu.domibus.configuration.model.ResponderPartiesModel(new ComplexField(father,"responderParties",eu.domibus.configuration.ResponderParties.class,"process",Process.class));
		this.LEGS = new eu.domibus.configuration.model.LegsModel(new ComplexField(father,"legs",eu.domibus.configuration.Legs.class,"process",Process.class));
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"process",Process.class);
		this.RESPONDER_ROLE = new ComplexField(father,"responderRole",java.lang.String.class,"process",Process.class);
		this.AGREEMENT = new ComplexField(father,"agreement",java.lang.String.class,"process",Process.class);
		this.BINDING = new ComplexField(father,"binding",java.lang.String.class,"process",Process.class);
		this.MEP = new ComplexField(father,"mep",java.lang.String.class,"process",Process.class);
		this.INITIATOR_ROLE = new ComplexField(father,"initiatorRole",java.lang.String.class,"process",Process.class);
	
	}
	
	

	public eu.domibus.configuration.model.InitiatorPartiesModel INITIATOR_PARTIES = null;
	 
	public eu.domibus.configuration.model.ResponderPartiesModel RESPONDER_PARTIES = null;
	 
	public eu.domibus.configuration.model.LegsModel LEGS = null;
	 
	public IField NAME = null;
	 
	public IField RESPONDER_ROLE = null;
	 
	public IField AGREEMENT = null;
	 
	public IField BINDING = null;
	 
	public IField MEP = null;
	 
	public IField INITIATOR_ROLE = null;
	 

	@Override
	public Class<Process> getModeledClass(){
		return Process.class;
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