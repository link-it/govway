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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.OpenspcoopAppender;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model OpenspcoopAppender 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenspcoopAppenderModel extends AbstractModel<OpenspcoopAppender> {

	public OpenspcoopAppenderModel(){
	
		super();
	
		this.PROPERTY = new org.openspcoop2.core.config.model.PropertyModel(new Field("property",org.openspcoop2.core.config.Property.class,"openspcoop-appender",OpenspcoopAppender.class));
		this.TIPO = new Field("tipo",java.lang.String.class,"openspcoop-appender",OpenspcoopAppender.class);
	
	}
	
	public OpenspcoopAppenderModel(IField father){
	
		super(father);
	
		this.PROPERTY = new org.openspcoop2.core.config.model.PropertyModel(new ComplexField(father,"property",org.openspcoop2.core.config.Property.class,"openspcoop-appender",OpenspcoopAppender.class));
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"openspcoop-appender",OpenspcoopAppender.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.PropertyModel PROPERTY = null;
	 
	public IField TIPO = null;
	 

	@Override
	public Class<OpenspcoopAppender> getModeledClass(){
		return OpenspcoopAppender.class;
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