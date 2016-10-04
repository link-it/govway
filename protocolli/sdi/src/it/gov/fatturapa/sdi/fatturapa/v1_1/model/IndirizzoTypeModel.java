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

import it.gov.fatturapa.sdi.fatturapa.v1_1.IndirizzoType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model IndirizzoType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IndirizzoTypeModel extends AbstractModel<IndirizzoType> {

	public IndirizzoTypeModel(){
	
		super();
	
		this.INDIRIZZO = new Field("Indirizzo",java.lang.String.class,"IndirizzoType",IndirizzoType.class);
		this.NUMERO_CIVICO = new Field("NumeroCivico",java.lang.String.class,"IndirizzoType",IndirizzoType.class);
		this.CAP = new Field("CAP",java.lang.String.class,"IndirizzoType",IndirizzoType.class);
		this.COMUNE = new Field("Comune",java.lang.String.class,"IndirizzoType",IndirizzoType.class);
		this.PROVINCIA = new Field("Provincia",java.lang.String.class,"IndirizzoType",IndirizzoType.class);
		this.NAZIONE = new Field("Nazione",java.lang.String.class,"IndirizzoType",IndirizzoType.class);
	
	}
	
	public IndirizzoTypeModel(IField father){
	
		super(father);
	
		this.INDIRIZZO = new ComplexField(father,"Indirizzo",java.lang.String.class,"IndirizzoType",IndirizzoType.class);
		this.NUMERO_CIVICO = new ComplexField(father,"NumeroCivico",java.lang.String.class,"IndirizzoType",IndirizzoType.class);
		this.CAP = new ComplexField(father,"CAP",java.lang.String.class,"IndirizzoType",IndirizzoType.class);
		this.COMUNE = new ComplexField(father,"Comune",java.lang.String.class,"IndirizzoType",IndirizzoType.class);
		this.PROVINCIA = new ComplexField(father,"Provincia",java.lang.String.class,"IndirizzoType",IndirizzoType.class);
		this.NAZIONE = new ComplexField(father,"Nazione",java.lang.String.class,"IndirizzoType",IndirizzoType.class);
	
	}
	
	

	public IField INDIRIZZO = null;
	 
	public IField NUMERO_CIVICO = null;
	 
	public IField CAP = null;
	 
	public IField COMUNE = null;
	 
	public IField PROVINCIA = null;
	 
	public IField NAZIONE = null;
	 

	@Override
	public Class<IndirizzoType> getModeledClass(){
		return IndirizzoType.class;
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