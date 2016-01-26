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
package it.gov.fatturapa.sdi.fatturapa.v1_1.model;

import it.gov.fatturapa.sdi.fatturapa.v1_1.AltriDatiGestionaliType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AltriDatiGestionaliType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AltriDatiGestionaliTypeModel extends AbstractModel<AltriDatiGestionaliType> {

	public AltriDatiGestionaliTypeModel(){
	
		super();
	
		this.TIPO_DATO = new Field("TipoDato",java.lang.String.class,"AltriDatiGestionaliType",AltriDatiGestionaliType.class);
		this.RIFERIMENTO_TESTO = new Field("RiferimentoTesto",java.lang.String.class,"AltriDatiGestionaliType",AltriDatiGestionaliType.class);
		this.RIFERIMENTO_NUMERO = new Field("RiferimentoNumero",java.lang.Double.class,"AltriDatiGestionaliType",AltriDatiGestionaliType.class);
		this.RIFERIMENTO_DATA = new Field("RiferimentoData",java.util.Date.class,"AltriDatiGestionaliType",AltriDatiGestionaliType.class);
	
	}
	
	public AltriDatiGestionaliTypeModel(IField father){
	
		super(father);
	
		this.TIPO_DATO = new ComplexField(father,"TipoDato",java.lang.String.class,"AltriDatiGestionaliType",AltriDatiGestionaliType.class);
		this.RIFERIMENTO_TESTO = new ComplexField(father,"RiferimentoTesto",java.lang.String.class,"AltriDatiGestionaliType",AltriDatiGestionaliType.class);
		this.RIFERIMENTO_NUMERO = new ComplexField(father,"RiferimentoNumero",java.lang.Double.class,"AltriDatiGestionaliType",AltriDatiGestionaliType.class);
		this.RIFERIMENTO_DATA = new ComplexField(father,"RiferimentoData",java.util.Date.class,"AltriDatiGestionaliType",AltriDatiGestionaliType.class);
	
	}
	
	

	public IField TIPO_DATO = null;
	 
	public IField RIFERIMENTO_TESTO = null;
	 
	public IField RIFERIMENTO_NUMERO = null;
	 
	public IField RIFERIMENTO_DATA = null;
	 

	@Override
	public Class<AltriDatiGestionaliType> getModeledClass(){
		return AltriDatiGestionaliType.class;
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