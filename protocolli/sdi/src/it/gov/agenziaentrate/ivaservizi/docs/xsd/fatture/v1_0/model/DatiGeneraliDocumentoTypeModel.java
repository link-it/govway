/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiGeneraliDocumentoType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiGeneraliDocumentoTypeModel extends AbstractModel<DatiGeneraliDocumentoType> {

	public DatiGeneraliDocumentoTypeModel(){
	
		super();
	
		this.TIPO_DOCUMENTO = new Field("TipoDocumento",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.DIVISA = new Field("Divisa",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.DATA = new Field("Data",java.util.Date.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.NUMERO = new Field("Numero",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.BOLLO_VIRTUALE = new Field("BolloVirtuale",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
	
	}
	
	public DatiGeneraliDocumentoTypeModel(IField father){
	
		super(father);
	
		this.TIPO_DOCUMENTO = new ComplexField(father,"TipoDocumento",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.DIVISA = new ComplexField(father,"Divisa",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.DATA = new ComplexField(father,"Data",java.util.Date.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.NUMERO = new ComplexField(father,"Numero",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
		this.BOLLO_VIRTUALE = new ComplexField(father,"BolloVirtuale",java.lang.String.class,"DatiGeneraliDocumentoType",DatiGeneraliDocumentoType.class);
	
	}
	
	

	public IField TIPO_DOCUMENTO = null;
	 
	public IField DIVISA = null;
	 
	public IField DATA = null;
	 
	public IField NUMERO = null;
	 
	public IField BOLLO_VIRTUALE = null;
	 

	@Override
	public Class<DatiGeneraliDocumentoType> getModeledClass(){
		return DatiGeneraliDocumentoType.class;
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