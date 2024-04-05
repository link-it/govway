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

import it.gov.fatturapa.sdi.fatturapa.v1_0.CessionarioCommittenteType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model CessionarioCommittenteType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CessionarioCommittenteTypeModel extends AbstractModel<CessionarioCommittenteType> {

	public CessionarioCommittenteTypeModel(){
	
		super();
	
		this.DATI_ANAGRAFICI = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiAnagraficiCessionarioTypeModel(new Field("DatiAnagrafici",it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCessionarioType.class,"CessionarioCommittenteType",CessionarioCommittenteType.class));
		this.SEDE = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.IndirizzoTypeModel(new Field("Sede",it.gov.fatturapa.sdi.fatturapa.v1_0.IndirizzoType.class,"CessionarioCommittenteType",CessionarioCommittenteType.class));
	
	}
	
	public CessionarioCommittenteTypeModel(IField father){
	
		super(father);
	
		this.DATI_ANAGRAFICI = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiAnagraficiCessionarioTypeModel(new ComplexField(father,"DatiAnagrafici",it.gov.fatturapa.sdi.fatturapa.v1_0.DatiAnagraficiCessionarioType.class,"CessionarioCommittenteType",CessionarioCommittenteType.class));
		this.SEDE = new it.gov.fatturapa.sdi.fatturapa.v1_0.model.IndirizzoTypeModel(new ComplexField(father,"Sede",it.gov.fatturapa.sdi.fatturapa.v1_0.IndirizzoType.class,"CessionarioCommittenteType",CessionarioCommittenteType.class));
	
	}
	
	

	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.DatiAnagraficiCessionarioTypeModel DATI_ANAGRAFICI = null;
	 
	public it.gov.fatturapa.sdi.fatturapa.v1_0.model.IndirizzoTypeModel SEDE = null;
	 

	@Override
	public Class<CessionarioCommittenteType> getModeledClass(){
		return CessionarioCommittenteType.class;
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