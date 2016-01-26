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

import org.openspcoop2.core.config.AccessoRegistroRegistro;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AccessoRegistroRegistro 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccessoRegistroRegistroModel extends AbstractModel<AccessoRegistroRegistro> {

	public AccessoRegistroRegistroModel(){
	
		super();
	
		this.TIPO_DATABASE = new Field("tipo-database",java.lang.String.class,"accesso-registro-registro",AccessoRegistroRegistro.class);
		this.NOME = new Field("nome",java.lang.String.class,"accesso-registro-registro",AccessoRegistroRegistro.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"accesso-registro-registro",AccessoRegistroRegistro.class);
		this.LOCATION = new Field("location",java.lang.String.class,"accesso-registro-registro",AccessoRegistroRegistro.class);
		this.USER = new Field("user",java.lang.String.class,"accesso-registro-registro",AccessoRegistroRegistro.class);
		this.PASSWORD = new Field("password",java.lang.String.class,"accesso-registro-registro",AccessoRegistroRegistro.class);
	
	}
	
	public AccessoRegistroRegistroModel(IField father){
	
		super(father);
	
		this.TIPO_DATABASE = new ComplexField(father,"tipo-database",java.lang.String.class,"accesso-registro-registro",AccessoRegistroRegistro.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"accesso-registro-registro",AccessoRegistroRegistro.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"accesso-registro-registro",AccessoRegistroRegistro.class);
		this.LOCATION = new ComplexField(father,"location",java.lang.String.class,"accesso-registro-registro",AccessoRegistroRegistro.class);
		this.USER = new ComplexField(father,"user",java.lang.String.class,"accesso-registro-registro",AccessoRegistroRegistro.class);
		this.PASSWORD = new ComplexField(father,"password",java.lang.String.class,"accesso-registro-registro",AccessoRegistroRegistro.class);
	
	}
	
	

	public IField TIPO_DATABASE = null;
	 
	public IField NOME = null;
	 
	public IField TIPO = null;
	 
	public IField LOCATION = null;
	 
	public IField USER = null;
	 
	public IField PASSWORD = null;
	 

	@Override
	public Class<AccessoRegistroRegistro> getModeledClass(){
		return AccessoRegistroRegistro.class;
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