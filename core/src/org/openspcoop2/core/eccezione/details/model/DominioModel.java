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
package org.openspcoop2.core.eccezione.details.model;

import org.openspcoop2.core.eccezione.details.Dominio;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Dominio 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DominioModel extends AbstractModel<Dominio> {

	public DominioModel(){
	
		super();
	
		this.ID = new Field("id",java.lang.String.class,"dominio",Dominio.class);
		this.ORGANIZATION = new org.openspcoop2.core.eccezione.details.model.DominioSoggettoModel(new Field("organization",org.openspcoop2.core.eccezione.details.DominioSoggetto.class,"dominio",Dominio.class));
		this.ROLE = new Field("role",java.lang.String.class,"dominio",Dominio.class);
		this.MODULE = new Field("module",java.lang.String.class,"dominio",Dominio.class);
	
	}
	
	public DominioModel(IField father){
	
		super(father);
	
		this.ID = new ComplexField(father,"id",java.lang.String.class,"dominio",Dominio.class);
		this.ORGANIZATION = new org.openspcoop2.core.eccezione.details.model.DominioSoggettoModel(new ComplexField(father,"organization",org.openspcoop2.core.eccezione.details.DominioSoggetto.class,"dominio",Dominio.class));
		this.ROLE = new ComplexField(father,"role",java.lang.String.class,"dominio",Dominio.class);
		this.MODULE = new ComplexField(father,"module",java.lang.String.class,"dominio",Dominio.class);
	
	}
	
	

	public IField ID = null;
	 
	public org.openspcoop2.core.eccezione.details.model.DominioSoggettoModel ORGANIZATION = null;
	 
	public IField ROLE = null;
	 
	public IField MODULE = null;
	 

	@Override
	public Class<Dominio> getModeledClass(){
		return Dominio.class;
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