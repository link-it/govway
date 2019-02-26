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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.model;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.FileMetadatiType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model FileMetadatiType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FileMetadatiTypeModel extends AbstractModel<FileMetadatiType> {

	public FileMetadatiTypeModel(){
	
		super();
	
		this.IDENTIFICATIVO_SD_I = new Field("IdentificativoSdI",java.lang.String.class,"FileMetadati_Type",FileMetadatiType.class);
		this.NOME_FILE = new Field("NomeFile",java.lang.String.class,"FileMetadati_Type",FileMetadatiType.class);
		this.HASH = new Field("Hash",java.lang.String.class,"FileMetadati_Type",FileMetadatiType.class);
		this.CODICE_DESTINATARIO = new Field("CodiceDestinatario",java.lang.String.class,"FileMetadati_Type",FileMetadatiType.class);
		this.FORMATO = new Field("Formato",java.lang.String.class,"FileMetadati_Type",FileMetadatiType.class);
		this.TENTATIVI_INVIO = new Field("TentativiInvio",java.lang.Integer.class,"FileMetadati_Type",FileMetadatiType.class);
		this.MESSAGE_ID = new Field("MessageId",java.lang.String.class,"FileMetadati_Type",FileMetadatiType.class);
		this.NOTE = new Field("Note",java.lang.String.class,"FileMetadati_Type",FileMetadatiType.class);
		this.VERSIONE = new Field("versione",java.lang.String.class,"FileMetadati_Type",FileMetadatiType.class);
	
	}
	
	public FileMetadatiTypeModel(IField father){
	
		super(father);
	
		this.IDENTIFICATIVO_SD_I = new ComplexField(father,"IdentificativoSdI",java.lang.String.class,"FileMetadati_Type",FileMetadatiType.class);
		this.NOME_FILE = new ComplexField(father,"NomeFile",java.lang.String.class,"FileMetadati_Type",FileMetadatiType.class);
		this.HASH = new ComplexField(father,"Hash",java.lang.String.class,"FileMetadati_Type",FileMetadatiType.class);
		this.CODICE_DESTINATARIO = new ComplexField(father,"CodiceDestinatario",java.lang.String.class,"FileMetadati_Type",FileMetadatiType.class);
		this.FORMATO = new ComplexField(father,"Formato",java.lang.String.class,"FileMetadati_Type",FileMetadatiType.class);
		this.TENTATIVI_INVIO = new ComplexField(father,"TentativiInvio",java.lang.Integer.class,"FileMetadati_Type",FileMetadatiType.class);
		this.MESSAGE_ID = new ComplexField(father,"MessageId",java.lang.String.class,"FileMetadati_Type",FileMetadatiType.class);
		this.NOTE = new ComplexField(father,"Note",java.lang.String.class,"FileMetadati_Type",FileMetadatiType.class);
		this.VERSIONE = new ComplexField(father,"versione",java.lang.String.class,"FileMetadati_Type",FileMetadatiType.class);
	
	}
	
	

	public IField IDENTIFICATIVO_SD_I = null;
	 
	public IField NOME_FILE = null;
	 
	public IField HASH = null;
	 
	public IField CODICE_DESTINATARIO = null;
	 
	public IField FORMATO = null;
	 
	public IField TENTATIVI_INVIO = null;
	 
	public IField MESSAGE_ID = null;
	 
	public IField NOTE = null;
	 
	public IField VERSIONE = null;
	 

	@Override
	public Class<FileMetadatiType> getModeledClass(){
		return FileMetadatiType.class;
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