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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.PortaDominio;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PortaDominio 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaDominioModel extends AbstractModel<PortaDominio> {

	public PortaDominioModel(){
	
		super();
	
		this.SUPER_USER = new Field("super-user",java.lang.String.class,"porta-dominio",PortaDominio.class);
		this.NOME = new Field("nome",java.lang.String.class,"porta-dominio",PortaDominio.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"porta-dominio",PortaDominio.class);
		this.IMPLEMENTAZIONE = new Field("implementazione",java.lang.String.class,"porta-dominio",PortaDominio.class);
		this.SUBJECT = new Field("subject",java.lang.String.class,"porta-dominio",PortaDominio.class);
		this.CLIENT_AUTH = new Field("client-auth",java.lang.String.class,"porta-dominio",PortaDominio.class);
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"porta-dominio",PortaDominio.class);
	
	}
	
	public PortaDominioModel(IField father){
	
		super(father);
	
		this.SUPER_USER = new ComplexField(father,"super-user",java.lang.String.class,"porta-dominio",PortaDominio.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"porta-dominio",PortaDominio.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"porta-dominio",PortaDominio.class);
		this.IMPLEMENTAZIONE = new ComplexField(father,"implementazione",java.lang.String.class,"porta-dominio",PortaDominio.class);
		this.SUBJECT = new ComplexField(father,"subject",java.lang.String.class,"porta-dominio",PortaDominio.class);
		this.CLIENT_AUTH = new ComplexField(father,"client-auth",java.lang.String.class,"porta-dominio",PortaDominio.class);
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"porta-dominio",PortaDominio.class);
	
	}
	
	

	public IField SUPER_USER = null;
	 
	public IField NOME = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField IMPLEMENTAZIONE = null;
	 
	public IField SUBJECT = null;
	 
	public IField CLIENT_AUTH = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 

	@Override
	public Class<PortaDominio> getModeledClass(){
		return PortaDominio.class;
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