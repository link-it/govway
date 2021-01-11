/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.information_missing.model;

import org.openspcoop2.protocol.information_missing.Proprieta;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Proprieta 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProprietaModel extends AbstractModel<Proprieta> {

	public ProprietaModel(){
	
		super();
	
		this.HEADER = new org.openspcoop2.protocol.information_missing.model.DescriptionModel(new Field("header",org.openspcoop2.protocol.information_missing.Description.class,"Proprieta",Proprieta.class));
		this.FOOTER = new org.openspcoop2.protocol.information_missing.model.DescriptionModel(new Field("footer",org.openspcoop2.protocol.information_missing.Description.class,"Proprieta",Proprieta.class));
		this.PLACEHOLDER = new Field("placeholder",java.lang.String.class,"Proprieta",Proprieta.class);
		this.NOME = new Field("nome",java.lang.String.class,"Proprieta",Proprieta.class);
		this.DEFAULT = new Field("default",java.lang.String.class,"Proprieta",Proprieta.class);
		this.USE_IN_DELETE = new Field("use-in-delete",boolean.class,"Proprieta",Proprieta.class);
	
	}
	
	public ProprietaModel(IField father){
	
		super(father);
	
		this.HEADER = new org.openspcoop2.protocol.information_missing.model.DescriptionModel(new ComplexField(father,"header",org.openspcoop2.protocol.information_missing.Description.class,"Proprieta",Proprieta.class));
		this.FOOTER = new org.openspcoop2.protocol.information_missing.model.DescriptionModel(new ComplexField(father,"footer",org.openspcoop2.protocol.information_missing.Description.class,"Proprieta",Proprieta.class));
		this.PLACEHOLDER = new ComplexField(father,"placeholder",java.lang.String.class,"Proprieta",Proprieta.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"Proprieta",Proprieta.class);
		this.DEFAULT = new ComplexField(father,"default",java.lang.String.class,"Proprieta",Proprieta.class);
		this.USE_IN_DELETE = new ComplexField(father,"use-in-delete",boolean.class,"Proprieta",Proprieta.class);
	
	}
	
	

	public org.openspcoop2.protocol.information_missing.model.DescriptionModel HEADER = null;
	 
	public org.openspcoop2.protocol.information_missing.model.DescriptionModel FOOTER = null;
	 
	public IField PLACEHOLDER = null;
	 
	public IField NOME = null;
	 
	public IField DEFAULT = null;
	 
	public IField USE_IN_DELETE = null;
	 

	@Override
	public Class<Proprieta> getModeledClass(){
		return Proprieta.class;
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