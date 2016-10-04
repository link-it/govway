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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.OpenspcoopSorgenteDati;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model OpenspcoopSorgenteDati 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenspcoopSorgenteDatiModel extends AbstractModel<OpenspcoopSorgenteDati> {

	public OpenspcoopSorgenteDatiModel(){
	
		super();
	
		this.PROPERTY = new org.openspcoop2.core.config.model.PropertyModel(new Field("property",org.openspcoop2.core.config.Property.class,"openspcoop-sorgente-dati",OpenspcoopSorgenteDati.class));
		this.NOME = new Field("nome",java.lang.String.class,"openspcoop-sorgente-dati",OpenspcoopSorgenteDati.class);
		this.NOME_JNDI = new Field("nome-jndi",java.lang.String.class,"openspcoop-sorgente-dati",OpenspcoopSorgenteDati.class);
		this.TIPO_DATABASE = new Field("tipo-database",java.lang.String.class,"openspcoop-sorgente-dati",OpenspcoopSorgenteDati.class);
	
	}
	
	public OpenspcoopSorgenteDatiModel(IField father){
	
		super(father);
	
		this.PROPERTY = new org.openspcoop2.core.config.model.PropertyModel(new ComplexField(father,"property",org.openspcoop2.core.config.Property.class,"openspcoop-sorgente-dati",OpenspcoopSorgenteDati.class));
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"openspcoop-sorgente-dati",OpenspcoopSorgenteDati.class);
		this.NOME_JNDI = new ComplexField(father,"nome-jndi",java.lang.String.class,"openspcoop-sorgente-dati",OpenspcoopSorgenteDati.class);
		this.TIPO_DATABASE = new ComplexField(father,"tipo-database",java.lang.String.class,"openspcoop-sorgente-dati",OpenspcoopSorgenteDati.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.PropertyModel PROPERTY = null;
	 
	public IField NOME = null;
	 
	public IField NOME_JNDI = null;
	 
	public IField TIPO_DATABASE = null;
	 

	@Override
	public Class<OpenspcoopSorgenteDati> getModeledClass(){
		return OpenspcoopSorgenteDati.class;
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