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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.Documento;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Documento 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DocumentoModel extends AbstractModel<Documento> {

	public DocumentoModel(){
	
		super();
	
		this.BYTE_CONTENUTO = new Field("byte-contenuto",byte[].class,"documento",Documento.class);
		this.RUOLO = new Field("ruolo",java.lang.String.class,"documento",Documento.class);
		this.TIPO_PROPRIETARIO_DOCUMENTO = new Field("tipo-proprietario-documento",java.lang.String.class,"documento",Documento.class);
		this.ID_PROPRIETARIO_DOCUMENTO = new Field("id-proprietario-documento",java.lang.Long.class,"documento",Documento.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"documento",Documento.class);
		this.FILE = new Field("file",java.lang.String.class,"documento",Documento.class);
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"documento",Documento.class);
	
	}
	
	public DocumentoModel(IField father){
	
		super(father);
	
		this.BYTE_CONTENUTO = new ComplexField(father,"byte-contenuto",byte[].class,"documento",Documento.class);
		this.RUOLO = new ComplexField(father,"ruolo",java.lang.String.class,"documento",Documento.class);
		this.TIPO_PROPRIETARIO_DOCUMENTO = new ComplexField(father,"tipo-proprietario-documento",java.lang.String.class,"documento",Documento.class);
		this.ID_PROPRIETARIO_DOCUMENTO = new ComplexField(father,"id-proprietario-documento",java.lang.Long.class,"documento",Documento.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"documento",Documento.class);
		this.FILE = new ComplexField(father,"file",java.lang.String.class,"documento",Documento.class);
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"documento",Documento.class);
	
	}
	
	

	public IField BYTE_CONTENUTO = null;
	 
	public IField RUOLO = null;
	 
	public IField TIPO_PROPRIETARIO_DOCUMENTO = null;
	 
	public IField ID_PROPRIETARIO_DOCUMENTO = null;
	 
	public IField TIPO = null;
	 
	public IField FILE = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 

	@Override
	public Class<Documento> getModeledClass(){
		return Documento.class;
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