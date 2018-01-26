/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.CredenzialiSoggetto;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model CredenzialiSoggetto 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CredenzialiSoggettoModel extends AbstractModel<CredenzialiSoggetto> {

	public CredenzialiSoggettoModel(){
	
		super();
	
		this.TIPO = new Field("tipo",java.lang.String.class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.USER = new Field("user",java.lang.String.class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.PASSWORD = new Field("password",java.lang.String.class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.SUBJECT = new Field("subject",java.lang.String.class,"credenziali-soggetto",CredenzialiSoggetto.class);
	
	}
	
	public CredenzialiSoggettoModel(IField father){
	
		super(father);
	
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.USER = new ComplexField(father,"user",java.lang.String.class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.PASSWORD = new ComplexField(father,"password",java.lang.String.class,"credenziali-soggetto",CredenzialiSoggetto.class);
		this.SUBJECT = new ComplexField(father,"subject",java.lang.String.class,"credenziali-soggetto",CredenzialiSoggetto.class);
	
	}
	
	

	public IField TIPO = null;
	 
	public IField USER = null;
	 
	public IField PASSWORD = null;
	 
	public IField SUBJECT = null;
	 

	@Override
	public Class<CredenzialiSoggetto> getModeledClass(){
		return CredenzialiSoggetto.class;
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