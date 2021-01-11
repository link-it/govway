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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.Gruppo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Gruppo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GruppoModel extends AbstractModel<Gruppo> {

	public GruppoModel(){
	
		super();
	
		this.NOME = new Field("nome",java.lang.String.class,"gruppo",Gruppo.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"gruppo",Gruppo.class);
		this.SERVICE_BINDING = new Field("service-binding",java.lang.String.class,"gruppo",Gruppo.class);
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"gruppo",Gruppo.class);
		this.SUPER_USER = new Field("super-user",java.lang.String.class,"gruppo",Gruppo.class);
	
	}
	
	public GruppoModel(IField father){
	
		super(father);
	
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"gruppo",Gruppo.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"gruppo",Gruppo.class);
		this.SERVICE_BINDING = new ComplexField(father,"service-binding",java.lang.String.class,"gruppo",Gruppo.class);
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"gruppo",Gruppo.class);
		this.SUPER_USER = new ComplexField(father,"super-user",java.lang.String.class,"gruppo",Gruppo.class);
	
	}
	
	

	public IField NOME = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField SERVICE_BINDING = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 
	public IField SUPER_USER = null;
	 

	@Override
	public Class<Gruppo> getModeledClass(){
		return Gruppo.class;
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