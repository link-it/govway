/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import it.gov.fatturapa.sdi.fatturapa.v1_0.DatiBolloType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiBolloType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiBolloTypeModel extends AbstractModel<DatiBolloType> {

	public DatiBolloTypeModel(){
	
		super();
	
		this.NUMERO_BOLLO = new Field("NumeroBollo",java.lang.String.class,"DatiBolloType",DatiBolloType.class);
		this.IMPORTO_BOLLO = new Field("ImportoBollo",java.math.BigDecimal.class,"DatiBolloType",DatiBolloType.class);
	
	}
	
	public DatiBolloTypeModel(IField father){
	
		super(father);
	
		this.NUMERO_BOLLO = new ComplexField(father,"NumeroBollo",java.lang.String.class,"DatiBolloType",DatiBolloType.class);
		this.IMPORTO_BOLLO = new ComplexField(father,"ImportoBollo",java.math.BigDecimal.class,"DatiBolloType",DatiBolloType.class);
	
	}
	
	

	public IField NUMERO_BOLLO = null;
	 
	public IField IMPORTO_BOLLO = null;
	 

	@Override
	public Class<DatiBolloType> getModeledClass(){
		return DatiBolloType.class;
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