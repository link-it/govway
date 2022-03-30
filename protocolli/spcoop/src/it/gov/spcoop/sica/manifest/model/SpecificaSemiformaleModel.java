/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package it.gov.spcoop.sica.manifest.model;

import it.gov.spcoop.sica.manifest.SpecificaSemiformale;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model SpecificaSemiformale 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SpecificaSemiformaleModel extends AbstractModel<SpecificaSemiformale> {

	public SpecificaSemiformaleModel(){
	
		super();
	
		this.DOCUMENTO_SEMIFORMALE = new it.gov.spcoop.sica.manifest.model.DocumentoSemiformaleModel(new Field("documentoSemiformale",it.gov.spcoop.sica.manifest.DocumentoSemiformale.class,"SpecificaSemiformale",SpecificaSemiformale.class));
	
	}
	
	public SpecificaSemiformaleModel(IField father){
	
		super(father);
	
		this.DOCUMENTO_SEMIFORMALE = new it.gov.spcoop.sica.manifest.model.DocumentoSemiformaleModel(new ComplexField(father,"documentoSemiformale",it.gov.spcoop.sica.manifest.DocumentoSemiformale.class,"SpecificaSemiformale",SpecificaSemiformale.class));
	
	}
	
	

	public it.gov.spcoop.sica.manifest.model.DocumentoSemiformaleModel DOCUMENTO_SEMIFORMALE = null;
	 

	@Override
	public Class<SpecificaSemiformale> getModeledClass(){
		return SpecificaSemiformale.class;
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