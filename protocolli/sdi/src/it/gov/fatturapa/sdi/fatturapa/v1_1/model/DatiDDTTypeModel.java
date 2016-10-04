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
package it.gov.fatturapa.sdi.fatturapa.v1_1.model;

import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiDDTType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiDDTType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiDDTTypeModel extends AbstractModel<DatiDDTType> {

	public DatiDDTTypeModel(){
	
		super();
	
		this.NUMERO_DDT = new Field("NumeroDDT",java.lang.String.class,"DatiDDTType",DatiDDTType.class);
		this.DATA_DDT = new Field("DataDDT",java.util.Date.class,"DatiDDTType",DatiDDTType.class);
		this.RIFERIMENTO_NUMERO_LINEA = new Field("RiferimentoNumeroLinea",java.lang.Integer.class,"DatiDDTType",DatiDDTType.class);
	
	}
	
	public DatiDDTTypeModel(IField father){
	
		super(father);
	
		this.NUMERO_DDT = new ComplexField(father,"NumeroDDT",java.lang.String.class,"DatiDDTType",DatiDDTType.class);
		this.DATA_DDT = new ComplexField(father,"DataDDT",java.util.Date.class,"DatiDDTType",DatiDDTType.class);
		this.RIFERIMENTO_NUMERO_LINEA = new ComplexField(father,"RiferimentoNumeroLinea",java.lang.Integer.class,"DatiDDTType",DatiDDTType.class);
	
	}
	
	

	public IField NUMERO_DDT = null;
	 
	public IField DATA_DDT = null;
	 
	public IField RIFERIMENTO_NUMERO_LINEA = null;
	 

	@Override
	public Class<DatiDDTType> getModeledClass(){
		return DatiDDTType.class;
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