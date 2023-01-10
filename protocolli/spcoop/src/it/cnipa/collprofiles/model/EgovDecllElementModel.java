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
package it.cnipa.collprofiles.model;

import it.cnipa.collprofiles.EgovDecllElement;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model EgovDecllElement 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EgovDecllElementModel extends AbstractModel<EgovDecllElement> {

	public EgovDecllElementModel(){
	
		super();
	
		this.E_GOV_VERSION = new Field("e-govVersion",java.lang.String.class,"egovDecllElement",EgovDecllElement.class);
		this.RIF_DEFINIZIONE_INTERFACCIA = new Field("rifDefinizioneInterfaccia",java.net.URI.class,"egovDecllElement",EgovDecllElement.class);
		this.OPERATION_LIST = new it.cnipa.collprofiles.model.OperationListTypeModel(new Field("operationList",it.cnipa.collprofiles.OperationListType.class,"egovDecllElement",EgovDecllElement.class));
	
	}
	
	public EgovDecllElementModel(IField father){
	
		super(father);
	
		this.E_GOV_VERSION = new ComplexField(father,"e-govVersion",java.lang.String.class,"egovDecllElement",EgovDecllElement.class);
		this.RIF_DEFINIZIONE_INTERFACCIA = new ComplexField(father,"rifDefinizioneInterfaccia",java.net.URI.class,"egovDecllElement",EgovDecllElement.class);
		this.OPERATION_LIST = new it.cnipa.collprofiles.model.OperationListTypeModel(new ComplexField(father,"operationList",it.cnipa.collprofiles.OperationListType.class,"egovDecllElement",EgovDecllElement.class));
	
	}
	
	

	public IField E_GOV_VERSION = null;
	 
	public IField RIF_DEFINIZIONE_INTERFACCIA = null;
	 
	public it.cnipa.collprofiles.model.OperationListTypeModel OPERATION_LIST = null;
	 

	@Override
	public Class<EgovDecllElement> getModeledClass(){
		return EgovDecllElement.class;
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