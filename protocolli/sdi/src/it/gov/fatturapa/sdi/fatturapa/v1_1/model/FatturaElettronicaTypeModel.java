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
package it.gov.fatturapa.sdi.fatturapa.v1_1.model;

import it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model FatturaElettronicaType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FatturaElettronicaTypeModel extends AbstractModel<FatturaElettronicaType> {

	public FatturaElettronicaTypeModel(){
	
		super();
	
		this.FATTURA_ELETTRONICA_HEADER = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.FatturaElettronicaHeaderTypeModel(new Field("FatturaElettronicaHeader",it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaHeaderType.class,"FatturaElettronicaType",FatturaElettronicaType.class));
		this.FATTURA_ELETTRONICA_BODY = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.FatturaElettronicaBodyTypeModel(new Field("FatturaElettronicaBody",it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaBodyType.class,"FatturaElettronicaType",FatturaElettronicaType.class));
		this.VERSIONE = new Field("versione",java.lang.String.class,"FatturaElettronicaType",FatturaElettronicaType.class);
	
	}
	
	public FatturaElettronicaTypeModel(IField father){
	
		super(father);
	
		this.FATTURA_ELETTRONICA_HEADER = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.FatturaElettronicaHeaderTypeModel(new ComplexField(father,"FatturaElettronicaHeader",it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaHeaderType.class,"FatturaElettronicaType",FatturaElettronicaType.class));
		this.FATTURA_ELETTRONICA_BODY = new it.gov.fatturapa.sdi.fatturapa.v1_1.model.FatturaElettronicaBodyTypeModel(new ComplexField(father,"FatturaElettronicaBody",it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaBodyType.class,"FatturaElettronicaType",FatturaElettronicaType.class));
		this.VERSIONE = new ComplexField(father,"versione",java.lang.String.class,"FatturaElettronicaType",FatturaElettronicaType.class);
	
	}
	
	

	public it.gov.fatturapa.sdi.fatturapa.v1_1.model.FatturaElettronicaHeaderTypeModel FATTURA_ELETTRONICA_HEADER = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_1.model.FatturaElettronicaBodyTypeModel FATTURA_ELETTRONICA_BODY = null;
	 
	public IField VERSIONE = null;
	 

	@Override
	public Class<FatturaElettronicaType> getModeledClass(){
		return FatturaElettronicaType.class;
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