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
package it.gov.spcoop.sica.wsbl.model;

import it.gov.spcoop.sica.wsbl.CompletionModeType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model CompletionModeType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CompletionModeTypeModel extends AbstractModel<CompletionModeType> {

	public CompletionModeTypeModel(){
	
		super();
	
		this.MODE = new Field("mode",java.lang.String.class,"completionModeType",CompletionModeType.class);
		this.COMPENSATE_MESSAGE = new it.gov.spcoop.sica.wsbl.model.CompletionModeTypeCompensateMessageModel(new Field("compensateMessage",it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage.class,"completionModeType",CompletionModeType.class));
	
	}
	
	public CompletionModeTypeModel(IField father){
	
		super(father);
	
		this.MODE = new ComplexField(father,"mode",java.lang.String.class,"completionModeType",CompletionModeType.class);
		this.COMPENSATE_MESSAGE = new it.gov.spcoop.sica.wsbl.model.CompletionModeTypeCompensateMessageModel(new ComplexField(father,"compensateMessage",it.gov.spcoop.sica.wsbl.CompletionModeTypeCompensateMessage.class,"completionModeType",CompletionModeType.class));
	
	}
	
	

	public IField MODE = null;
	 
	public it.gov.spcoop.sica.wsbl.model.CompletionModeTypeCompensateMessageModel COMPENSATE_MESSAGE = null;
	 

	@Override
	public Class<CompletionModeType> getModeledClass(){
		return CompletionModeType.class;
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