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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiBolloType;

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
	
		this.BOLLO_VIRTUALE = new Field("BolloVirtuale",java.lang.String.class,"DatiBolloType",DatiBolloType.class);
		this.IMPORTO_BOLLO = new Field("ImportoBollo",java.math.BigDecimal.class,"DatiBolloType",DatiBolloType.class);
	
	}
	
	public DatiBolloTypeModel(IField father){
	
		super(father);
	
		this.BOLLO_VIRTUALE = new ComplexField(father,"BolloVirtuale",java.lang.String.class,"DatiBolloType",DatiBolloType.class);
		this.IMPORTO_BOLLO = new ComplexField(father,"ImportoBollo",java.math.BigDecimal.class,"DatiBolloType",DatiBolloType.class);
	
	}
	
	

	public IField BOLLO_VIRTUALE = null;
	 
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