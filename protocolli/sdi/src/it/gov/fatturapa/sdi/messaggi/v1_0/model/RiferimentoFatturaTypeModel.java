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
package it.gov.fatturapa.sdi.messaggi.v1_0.model;

import it.gov.fatturapa.sdi.messaggi.v1_0.RiferimentoFatturaType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RiferimentoFatturaType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RiferimentoFatturaTypeModel extends AbstractModel<RiferimentoFatturaType> {

	public RiferimentoFatturaTypeModel(){
	
		super();
	
		this.NUMERO_FATTURA = new Field("NumeroFattura",java.lang.String.class,"RiferimentoFattura_Type",RiferimentoFatturaType.class);
		this.ANNO_FATTURA = new Field("AnnoFattura",java.lang.Integer.class,"RiferimentoFattura_Type",RiferimentoFatturaType.class);
		this.POSIZIONE_FATTURA = new Field("PosizioneFattura",java.lang.Integer.class,"RiferimentoFattura_Type",RiferimentoFatturaType.class);
	
	}
	
	public RiferimentoFatturaTypeModel(IField father){
	
		super(father);
	
		this.NUMERO_FATTURA = new ComplexField(father,"NumeroFattura",java.lang.String.class,"RiferimentoFattura_Type",RiferimentoFatturaType.class);
		this.ANNO_FATTURA = new ComplexField(father,"AnnoFattura",java.lang.Integer.class,"RiferimentoFattura_Type",RiferimentoFatturaType.class);
		this.POSIZIONE_FATTURA = new ComplexField(father,"PosizioneFattura",java.lang.Integer.class,"RiferimentoFattura_Type",RiferimentoFatturaType.class);
	
	}
	
	

	public IField NUMERO_FATTURA = null;
	 
	public IField ANNO_FATTURA = null;
	 
	public IField POSIZIONE_FATTURA = null;
	 

	@Override
	public Class<RiferimentoFatturaType> getModeledClass(){
		return RiferimentoFatturaType.class;
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