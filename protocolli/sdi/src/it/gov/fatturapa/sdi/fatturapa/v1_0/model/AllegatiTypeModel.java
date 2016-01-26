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
package it.gov.fatturapa.sdi.fatturapa.v1_0.model;

import it.gov.fatturapa.sdi.fatturapa.v1_0.AllegatiType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AllegatiType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllegatiTypeModel extends AbstractModel<AllegatiType> {

	public AllegatiTypeModel(){
	
		super();
	
		this.NOME_ATTACHMENT = new Field("NomeAttachment",java.lang.String.class,"AllegatiType",AllegatiType.class);
		this.ALGORITMO_COMPRESSIONE = new Field("AlgoritmoCompressione",java.lang.String.class,"AllegatiType",AllegatiType.class);
		this.FORMATO_ATTACHMENT = new Field("FormatoAttachment",java.lang.String.class,"AllegatiType",AllegatiType.class);
		this.DESCRIZIONE_ATTACHMENT = new Field("DescrizioneAttachment",java.lang.String.class,"AllegatiType",AllegatiType.class);
		this.ATTACHMENT = new Field("Attachment",byte[].class,"AllegatiType",AllegatiType.class);
	
	}
	
	public AllegatiTypeModel(IField father){
	
		super(father);
	
		this.NOME_ATTACHMENT = new ComplexField(father,"NomeAttachment",java.lang.String.class,"AllegatiType",AllegatiType.class);
		this.ALGORITMO_COMPRESSIONE = new ComplexField(father,"AlgoritmoCompressione",java.lang.String.class,"AllegatiType",AllegatiType.class);
		this.FORMATO_ATTACHMENT = new ComplexField(father,"FormatoAttachment",java.lang.String.class,"AllegatiType",AllegatiType.class);
		this.DESCRIZIONE_ATTACHMENT = new ComplexField(father,"DescrizioneAttachment",java.lang.String.class,"AllegatiType",AllegatiType.class);
		this.ATTACHMENT = new ComplexField(father,"Attachment",byte[].class,"AllegatiType",AllegatiType.class);
	
	}
	
	

	public IField NOME_ATTACHMENT = null;
	 
	public IField ALGORITMO_COMPRESSIONE = null;
	 
	public IField FORMATO_ATTACHMENT = null;
	 
	public IField DESCRIZIONE_ATTACHMENT = null;
	 
	public IField ATTACHMENT = null;
	 

	@Override
	public Class<AllegatiType> getModeledClass(){
		return AllegatiType.class;
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