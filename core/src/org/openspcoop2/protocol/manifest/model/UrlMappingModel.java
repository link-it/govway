/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.manifest.model;

import org.openspcoop2.protocol.manifest.UrlMapping;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model UrlMapping 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UrlMappingModel extends AbstractModel<UrlMapping> {

	public UrlMappingModel(){
	
		super();
	
		this.FILE = new Field("file",java.lang.String.class,"urlMapping",UrlMapping.class);
		this.TYPE = new Field("type",java.lang.String.class,"urlMapping",UrlMapping.class);
	
	}
	
	public UrlMappingModel(IField father){
	
		super(father);
	
		this.FILE = new ComplexField(father,"file",java.lang.String.class,"urlMapping",UrlMapping.class);
		this.TYPE = new ComplexField(father,"type",java.lang.String.class,"urlMapping",UrlMapping.class);
	
	}
	
	

	public IField FILE = null;
	 
	public IField TYPE = null;
	 

	@Override
	public Class<UrlMapping> getModeledClass(){
		return UrlMapping.class;
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