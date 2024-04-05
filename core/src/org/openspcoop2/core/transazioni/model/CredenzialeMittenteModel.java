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
package org.openspcoop2.core.transazioni.model;

import org.openspcoop2.core.transazioni.CredenzialeMittente;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model CredenzialeMittente 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CredenzialeMittenteModel extends AbstractModel<CredenzialeMittente> {

	public CredenzialeMittenteModel(){
	
		super();
	
		this.TIPO = new Field("tipo",java.lang.String.class,"credenziale-mittente",CredenzialeMittente.class);
		this.CREDENZIALE = new Field("credenziale",java.lang.String.class,"credenziale-mittente",CredenzialeMittente.class);
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"credenziale-mittente",CredenzialeMittente.class);
		this.REF_CREDENZIALE = new Field("ref-credenziale",java.lang.Long.class,"credenziale-mittente",CredenzialeMittente.class);
	
	}
	
	public CredenzialeMittenteModel(IField father){
	
		super(father);
	
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"credenziale-mittente",CredenzialeMittente.class);
		this.CREDENZIALE = new ComplexField(father,"credenziale",java.lang.String.class,"credenziale-mittente",CredenzialeMittente.class);
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"credenziale-mittente",CredenzialeMittente.class);
		this.REF_CREDENZIALE = new ComplexField(father,"ref-credenziale",java.lang.Long.class,"credenziale-mittente",CredenzialeMittente.class);
	
	}
	
	

	public IField TIPO = null;
	 
	public IField CREDENZIALE = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 
	public IField REF_CREDENZIALE = null;
	 

	@Override
	public Class<CredenzialeMittente> getModeledClass(){
		return CredenzialeMittente.class;
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