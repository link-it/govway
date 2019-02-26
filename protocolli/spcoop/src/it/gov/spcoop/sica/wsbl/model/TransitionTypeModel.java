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
package it.gov.spcoop.sica.wsbl.model;

import it.gov.spcoop.sica.wsbl.TransitionType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TransitionType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransitionTypeModel extends AbstractModel<TransitionType> {

	public TransitionTypeModel(){
	
		super();
	
		this.ACTIVATION_MODE = new Field("activationMode",java.lang.String.class,"transitionType",TransitionType.class);
		this.COMPLETION_MODE = new it.gov.spcoop.sica.wsbl.model.CompletionModeTypeModel(new Field("completionMode",it.gov.spcoop.sica.wsbl.CompletionModeType.class,"transitionType",TransitionType.class));
		this.GUARD = new it.gov.spcoop.sica.wsbl.model.GuardTypeModel(new Field("guard",it.gov.spcoop.sica.wsbl.GuardType.class,"transitionType",TransitionType.class));
		this.EVENTS = new it.gov.spcoop.sica.wsbl.model.EventListTypeModel(new Field("events",it.gov.spcoop.sica.wsbl.EventListType.class,"transitionType",TransitionType.class));
		this.TEMPORAL_CONDITION = new it.gov.spcoop.sica.wsbl.model.TemporalConditionTypeModel(new Field("temporalCondition",it.gov.spcoop.sica.wsbl.TemporalConditionType.class,"transitionType",TransitionType.class));
		this.NAME = new Field("name",java.lang.String.class,"transitionType",TransitionType.class);
		this.SOURCE = new Field("source",java.lang.String.class,"transitionType",TransitionType.class);
		this.TARGET = new Field("target",java.lang.String.class,"transitionType",TransitionType.class);
	
	}
	
	public TransitionTypeModel(IField father){
	
		super(father);
	
		this.ACTIVATION_MODE = new ComplexField(father,"activationMode",java.lang.String.class,"transitionType",TransitionType.class);
		this.COMPLETION_MODE = new it.gov.spcoop.sica.wsbl.model.CompletionModeTypeModel(new ComplexField(father,"completionMode",it.gov.spcoop.sica.wsbl.CompletionModeType.class,"transitionType",TransitionType.class));
		this.GUARD = new it.gov.spcoop.sica.wsbl.model.GuardTypeModel(new ComplexField(father,"guard",it.gov.spcoop.sica.wsbl.GuardType.class,"transitionType",TransitionType.class));
		this.EVENTS = new it.gov.spcoop.sica.wsbl.model.EventListTypeModel(new ComplexField(father,"events",it.gov.spcoop.sica.wsbl.EventListType.class,"transitionType",TransitionType.class));
		this.TEMPORAL_CONDITION = new it.gov.spcoop.sica.wsbl.model.TemporalConditionTypeModel(new ComplexField(father,"temporalCondition",it.gov.spcoop.sica.wsbl.TemporalConditionType.class,"transitionType",TransitionType.class));
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"transitionType",TransitionType.class);
		this.SOURCE = new ComplexField(father,"source",java.lang.String.class,"transitionType",TransitionType.class);
		this.TARGET = new ComplexField(father,"target",java.lang.String.class,"transitionType",TransitionType.class);
	
	}
	
	

	public IField ACTIVATION_MODE = null;
	 
	public it.gov.spcoop.sica.wsbl.model.CompletionModeTypeModel COMPLETION_MODE = null;
	 
	public it.gov.spcoop.sica.wsbl.model.GuardTypeModel GUARD = null;
	 
	public it.gov.spcoop.sica.wsbl.model.EventListTypeModel EVENTS = null;
	 
	public it.gov.spcoop.sica.wsbl.model.TemporalConditionTypeModel TEMPORAL_CONDITION = null;
	 
	public IField NAME = null;
	 
	public IField SOURCE = null;
	 
	public IField TARGET = null;
	 

	@Override
	public Class<TransitionType> getModeledClass(){
		return TransitionType.class;
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