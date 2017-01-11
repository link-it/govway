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
package backend.ecodex.org._1_1.model;

import backend.ecodex.org._1_1.FaultDetail;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model FaultDetail 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FaultDetailModel extends AbstractModel<FaultDetail> {

	public FaultDetailModel(){
	
		super();
	
		this.CODE = new Field("code",java.lang.String.class,"FaultDetail",FaultDetail.class);
		this.MESSAGE = new Field("message",java.lang.String.class,"FaultDetail",FaultDetail.class);
	
	}
	
	public FaultDetailModel(IField father){
	
		super(father);
	
		this.CODE = new ComplexField(father,"code",java.lang.String.class,"FaultDetail",FaultDetail.class);
		this.MESSAGE = new ComplexField(father,"message",java.lang.String.class,"FaultDetail",FaultDetail.class);
	
	}
	
	

	public IField CODE = null;
	 
	public IField MESSAGE = null;
	 

	@Override
	public Class<FaultDetail> getModeledClass(){
		return FaultDetail.class;
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