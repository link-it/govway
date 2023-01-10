/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package it.gov.spcoop.sica.manifest.model;

import it.gov.spcoop.sica.manifest.ElencoPartecipanti;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ElencoPartecipanti 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ElencoPartecipantiModel extends AbstractModel<ElencoPartecipanti> {

	public ElencoPartecipantiModel(){
	
		super();
	
		this.PARTECIPANTE = new Field("partecipante",java.net.URI.class,"ElencoPartecipanti",ElencoPartecipanti.class);
	
	}
	
	public ElencoPartecipantiModel(IField father){
	
		super(father);
	
		this.PARTECIPANTE = new ComplexField(father,"partecipante",java.net.URI.class,"ElencoPartecipanti",ElencoPartecipanti.class);
	
	}
	
	

	public IField PARTECIPANTE = null;
	 

	@Override
	public Class<ElencoPartecipanti> getModeledClass(){
		return ElencoPartecipanti.class;
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