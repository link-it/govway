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
package org.openspcoop2.web.lib.audit.log.model;

import org.openspcoop2.web.lib.audit.log.Binary;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Binary 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BinaryModel extends AbstractModel<Binary> {

	public BinaryModel(){
	
		super();
	
		this.BINARY_ID = new Field("binary-id",java.lang.String.class,"binary",Binary.class);
		this.CHECKSUM = new Field("checksum",long.class,"binary",Binary.class);
		this.ID_OPERATION = new Field("id-operation",java.lang.Long.class,"binary",Binary.class);
	
	}
	
	public BinaryModel(IField father){
	
		super(father);
	
		this.BINARY_ID = new ComplexField(father,"binary-id",java.lang.String.class,"binary",Binary.class);
		this.CHECKSUM = new ComplexField(father,"checksum",long.class,"binary",Binary.class);
		this.ID_OPERATION = new ComplexField(father,"id-operation",java.lang.Long.class,"binary",Binary.class);
	
	}
	
	

	public IField BINARY_ID = null;
	 
	public IField CHECKSUM = null;
	 
	public IField ID_OPERATION = null;
	 

	@Override
	public Class<Binary> getModeledClass(){
		return Binary.class;
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