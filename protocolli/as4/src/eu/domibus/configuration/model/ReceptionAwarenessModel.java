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

import eu.domibus.configuration.ReceptionAwareness;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ReceptionAwareness 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ReceptionAwarenessModel extends AbstractModel<ReceptionAwareness> {

	public ReceptionAwarenessModel(){
	
		super();
	
		this.NAME = new Field("name",java.lang.String.class,"receptionAwareness",ReceptionAwareness.class);
		this.RETRY = new Field("retry",java.lang.String.class,"receptionAwareness",ReceptionAwareness.class);
		this.DUPLICATE_DETECTION = new Field("duplicateDetection",java.lang.String.class,"receptionAwareness",ReceptionAwareness.class);
	
	}
	
	public ReceptionAwarenessModel(IField father){
	
		super(father);
	
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"receptionAwareness",ReceptionAwareness.class);
		this.RETRY = new ComplexField(father,"retry",java.lang.String.class,"receptionAwareness",ReceptionAwareness.class);
		this.DUPLICATE_DETECTION = new ComplexField(father,"duplicateDetection",java.lang.String.class,"receptionAwareness",ReceptionAwareness.class);
	
	}
	
	

	public IField NAME = null;
	 
	public IField RETRY = null;
	 
	public IField DUPLICATE_DETECTION = null;
	 

	@Override
	public Class<ReceptionAwareness> getModeledClass(){
		return ReceptionAwareness.class;
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