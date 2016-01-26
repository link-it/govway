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

import it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaPrincipaleType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model FatturaPrincipaleType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FatturaPrincipaleTypeModel extends AbstractModel<FatturaPrincipaleType> {

	public FatturaPrincipaleTypeModel(){
	
		super();
	
		this.NUMERO_FATTURA_PRINCIPALE = new Field("NumeroFatturaPrincipale",java.lang.String.class,"FatturaPrincipaleType",FatturaPrincipaleType.class);
		this.DATA_FATTURA_PRINCIPALE = new Field("DataFatturaPrincipale",java.util.Date.class,"FatturaPrincipaleType",FatturaPrincipaleType.class);
	
	}
	
	public FatturaPrincipaleTypeModel(IField father){
	
		super(father);
	
		this.NUMERO_FATTURA_PRINCIPALE = new ComplexField(father,"NumeroFatturaPrincipale",java.lang.String.class,"FatturaPrincipaleType",FatturaPrincipaleType.class);
		this.DATA_FATTURA_PRINCIPALE = new ComplexField(father,"DataFatturaPrincipale",java.util.Date.class,"FatturaPrincipaleType",FatturaPrincipaleType.class);
	
	}
	
	

	public IField NUMERO_FATTURA_PRINCIPALE = null;
	 
	public IField DATA_FATTURA_PRINCIPALE = null;
	 

	@Override
	public Class<FatturaPrincipaleType> getModeledClass(){
		return FatturaPrincipaleType.class;
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