/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.model;

import it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIBaseType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model FileSdIBaseType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FileSdIBaseTypeModel extends AbstractModel<FileSdIBaseType> {

	public FileSdIBaseTypeModel(){
	
		super();
	
		this.NOME_FILE = new Field("NomeFile",java.lang.String.class,"fileSdIBase_Type",FileSdIBaseType.class);
		this.FILE = new Field("File",byte[].class,"fileSdIBase_Type",FileSdIBaseType.class);
	
	}
	
	public FileSdIBaseTypeModel(IField father){
	
		super(father);
	
		this.NOME_FILE = new ComplexField(father,"NomeFile",java.lang.String.class,"fileSdIBase_Type",FileSdIBaseType.class);
		this.FILE = new ComplexField(father,"File",byte[].class,"fileSdIBase_Type",FileSdIBaseType.class);
	
	}
	
	

	public IField NOME_FILE = null;
	 
	public IField FILE = null;
	 

	@Override
	public Class<FileSdIBaseType> getModeledClass(){
		return FileSdIBaseType.class;
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