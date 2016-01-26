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
package it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.model;

import it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.FileSdIConMetadatiType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model FileSdIConMetadatiType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FileSdIConMetadatiTypeModel extends AbstractModel<FileSdIConMetadatiType> {

	public FileSdIConMetadatiTypeModel(){
	
		super();
	
		this.IDENTIFICATIVO_SD_I = new Field("IdentificativoSdI",java.lang.Integer.class,"fileSdIConMetadati_Type",FileSdIConMetadatiType.class);
		this.NOME_FILE = new Field("NomeFile",java.lang.String.class,"fileSdIConMetadati_Type",FileSdIConMetadatiType.class);
		this.FILE = new Field("File",byte[].class,"fileSdIConMetadati_Type",FileSdIConMetadatiType.class);
		this.NOME_FILE_METADATI = new Field("NomeFileMetadati",java.lang.String.class,"fileSdIConMetadati_Type",FileSdIConMetadatiType.class);
		this.METADATI = new Field("Metadati",byte[].class,"fileSdIConMetadati_Type",FileSdIConMetadatiType.class);
	
	}
	
	public FileSdIConMetadatiTypeModel(IField father){
	
		super(father);
	
		this.IDENTIFICATIVO_SD_I = new ComplexField(father,"IdentificativoSdI",java.lang.Integer.class,"fileSdIConMetadati_Type",FileSdIConMetadatiType.class);
		this.NOME_FILE = new ComplexField(father,"NomeFile",java.lang.String.class,"fileSdIConMetadati_Type",FileSdIConMetadatiType.class);
		this.FILE = new ComplexField(father,"File",byte[].class,"fileSdIConMetadati_Type",FileSdIConMetadatiType.class);
		this.NOME_FILE_METADATI = new ComplexField(father,"NomeFileMetadati",java.lang.String.class,"fileSdIConMetadati_Type",FileSdIConMetadatiType.class);
		this.METADATI = new ComplexField(father,"Metadati",byte[].class,"fileSdIConMetadati_Type",FileSdIConMetadatiType.class);
	
	}
	
	

	public IField IDENTIFICATIVO_SD_I = null;
	 
	public IField NOME_FILE = null;
	 
	public IField FILE = null;
	 
	public IField NOME_FILE_METADATI = null;
	 
	public IField METADATI = null;
	 

	@Override
	public Class<FileSdIConMetadatiType> getModeledClass(){
		return FileSdIConMetadatiType.class;
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