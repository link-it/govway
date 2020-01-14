/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package it.gov.fatturapa.sdi.fatturapa.v1_0.model;

import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiDDTType;

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
		this.RIFERIMENTO_NUMERO_LINEA = new Field("RiferimentoNumeroLinea",java.math.BigInteger.class,"DatiDDTType",DatiDDTType.class);
	
	}
	
	public DatiDDTTypeModel(IField father){
	
		super(father);
	
		this.NUMERO_DDT = new ComplexField(father,"NumeroDDT",java.lang.String.class,"DatiDDTType",DatiDDTType.class);
		this.DATA_DDT = new ComplexField(father,"DataDDT",java.util.Date.class,"DatiDDTType",DatiDDTType.class);
		this.RIFERIMENTO_NUMERO_LINEA = new ComplexField(father,"RiferimentoNumeroLinea",java.math.BigInteger.class,"DatiDDTType",DatiDDTType.class);
	
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