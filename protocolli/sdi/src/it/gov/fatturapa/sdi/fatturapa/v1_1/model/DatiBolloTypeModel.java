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

import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiBolloType;

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
		this.IMPORTO_BOLLO = new Field("ImportoBollo",java.lang.Double.class,"DatiBolloType",DatiBolloType.class);
	
	}
	
	public DatiBolloTypeModel(IField father){
	
		super(father);
	
		this.BOLLO_VIRTUALE = new ComplexField(father,"BolloVirtuale",java.lang.String.class,"DatiBolloType",DatiBolloType.class);
		this.IMPORTO_BOLLO = new ComplexField(father,"ImportoBollo",java.lang.Double.class,"DatiBolloType",DatiBolloType.class);
	
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