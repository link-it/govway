/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.model;

import it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.FileSdIType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model FileSdIType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FileSdITypeModel extends AbstractModel<FileSdIType> {

	public FileSdITypeModel(){
	
		super();
	
		this.IDENTIFICATIVO_SD_I = new Field("IdentificativoSdI",java.lang.Integer.class,"fileSdI_Type",FileSdIType.class);
		this.NOME_FILE = new Field("NomeFile",java.lang.String.class,"fileSdI_Type",FileSdIType.class);
		this.FILE = new Field("File",byte[].class,"fileSdI_Type",FileSdIType.class);
	
	}
	
	public FileSdITypeModel(IField father){
	
		super(father);
	
		this.IDENTIFICATIVO_SD_I = new ComplexField(father,"IdentificativoSdI",java.lang.Integer.class,"fileSdI_Type",FileSdIType.class);
		this.NOME_FILE = new ComplexField(father,"NomeFile",java.lang.String.class,"fileSdI_Type",FileSdIType.class);
		this.FILE = new ComplexField(father,"File",byte[].class,"fileSdI_Type",FileSdIType.class);
	
	}
	
	

	public IField IDENTIFICATIVO_SD_I = null;
	 
	public IField NOME_FILE = null;
	 
	public IField FILE = null;
	 

	@Override
	public Class<FileSdIType> getModeledClass(){
		return FileSdIType.class;
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