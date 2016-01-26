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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.ValidazioneBuste;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ValidazioneBuste 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ValidazioneBusteModel extends AbstractModel<ValidazioneBuste> {

	public ValidazioneBusteModel(){
	
		super();
	
		this.STATO = new Field("stato",java.lang.String.class,"validazione-buste",ValidazioneBuste.class);
		this.CONTROLLO = new Field("controllo",java.lang.String.class,"validazione-buste",ValidazioneBuste.class);
		this.PROFILO_COLLABORAZIONE = new Field("profiloCollaborazione",java.lang.String.class,"validazione-buste",ValidazioneBuste.class);
		this.MANIFEST_ATTACHMENTS = new Field("manifestAttachments",java.lang.String.class,"validazione-buste",ValidazioneBuste.class);
	
	}
	
	public ValidazioneBusteModel(IField father){
	
		super(father);
	
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"validazione-buste",ValidazioneBuste.class);
		this.CONTROLLO = new ComplexField(father,"controllo",java.lang.String.class,"validazione-buste",ValidazioneBuste.class);
		this.PROFILO_COLLABORAZIONE = new ComplexField(father,"profiloCollaborazione",java.lang.String.class,"validazione-buste",ValidazioneBuste.class);
		this.MANIFEST_ATTACHMENTS = new ComplexField(father,"manifestAttachments",java.lang.String.class,"validazione-buste",ValidazioneBuste.class);
	
	}
	
	

	public IField STATO = null;
	 
	public IField CONTROLLO = null;
	 
	public IField PROFILO_COLLABORAZIONE = null;
	 
	public IField MANIFEST_ATTACHMENTS = null;
	 

	@Override
	public Class<ValidazioneBuste> getModeledClass(){
		return ValidazioneBuste.class;
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