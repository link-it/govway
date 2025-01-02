/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.CodiceArticoloType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model CodiceArticoloType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CodiceArticoloTypeModel extends AbstractModel<CodiceArticoloType> {

	public CodiceArticoloTypeModel(){
	
		super();
	
		this.CODICE_TIPO = new Field("CodiceTipo",java.lang.String.class,"CodiceArticoloType",CodiceArticoloType.class);
		this.CODICE_VALORE = new Field("CodiceValore",java.lang.String.class,"CodiceArticoloType",CodiceArticoloType.class);
	
	}
	
	public CodiceArticoloTypeModel(IField father){
	
		super(father);
	
		this.CODICE_TIPO = new ComplexField(father,"CodiceTipo",java.lang.String.class,"CodiceArticoloType",CodiceArticoloType.class);
		this.CODICE_VALORE = new ComplexField(father,"CodiceValore",java.lang.String.class,"CodiceArticoloType",CodiceArticoloType.class);
	
	}
	
	

	public IField CODICE_TIPO = null;
	 
	public IField CODICE_VALORE = null;
	 

	@Override
	public Class<CodiceArticoloType> getModeledClass(){
		return CodiceArticoloType.class;
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