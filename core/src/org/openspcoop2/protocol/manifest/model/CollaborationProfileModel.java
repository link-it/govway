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

import org.openspcoop2.protocol.manifest.CollaborationProfile;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model CollaborationProfile 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CollaborationProfileModel extends AbstractModel<CollaborationProfile> {

	public CollaborationProfileModel(){
	
		super();
	
		this.ONEWAY = new Field("oneway",boolean.class,"CollaborationProfile",CollaborationProfile.class);
		this.INPUT_OUTPUT = new Field("inputOutput",boolean.class,"CollaborationProfile",CollaborationProfile.class);
		this.ASYNC_INPUT_OUTPUT = new Field("asyncInputOutput",boolean.class,"CollaborationProfile",CollaborationProfile.class);
		this.POLLED_INPUT_OUTPUT = new Field("polledInputOutput",boolean.class,"CollaborationProfile",CollaborationProfile.class);
	
	}
	
	public CollaborationProfileModel(IField father){
	
		super(father);
	
		this.ONEWAY = new ComplexField(father,"oneway",boolean.class,"CollaborationProfile",CollaborationProfile.class);
		this.INPUT_OUTPUT = new ComplexField(father,"inputOutput",boolean.class,"CollaborationProfile",CollaborationProfile.class);
		this.ASYNC_INPUT_OUTPUT = new ComplexField(father,"asyncInputOutput",boolean.class,"CollaborationProfile",CollaborationProfile.class);
		this.POLLED_INPUT_OUTPUT = new ComplexField(father,"polledInputOutput",boolean.class,"CollaborationProfile",CollaborationProfile.class);
	
	}
	
	

	public IField ONEWAY = null;
	 
	public IField INPUT_OUTPUT = null;
	 
	public IField ASYNC_INPUT_OUTPUT = null;
	 
	public IField POLLED_INPUT_OUTPUT = null;
	 

	@Override
	public Class<CollaborationProfile> getModeledClass(){
		return CollaborationProfile.class;
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