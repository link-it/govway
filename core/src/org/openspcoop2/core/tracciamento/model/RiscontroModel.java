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
package org.openspcoop2.core.tracciamento.model;

import org.openspcoop2.core.tracciamento.Riscontro;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Riscontro 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RiscontroModel extends AbstractModel<Riscontro> {

	public RiscontroModel(){
	
		super();
	
		this.IDENTIFICATIVO = new Field("identificativo",java.lang.String.class,"riscontro",Riscontro.class);
		this.ORA_REGISTRAZIONE = new org.openspcoop2.core.tracciamento.model.DataModel(new Field("ora-registrazione",org.openspcoop2.core.tracciamento.Data.class,"riscontro",Riscontro.class));
		this.RICEVUTA = new Field("ricevuta",java.lang.String.class,"riscontro",Riscontro.class);
	
	}
	
	public RiscontroModel(IField father){
	
		super(father);
	
		this.IDENTIFICATIVO = new ComplexField(father,"identificativo",java.lang.String.class,"riscontro",Riscontro.class);
		this.ORA_REGISTRAZIONE = new org.openspcoop2.core.tracciamento.model.DataModel(new ComplexField(father,"ora-registrazione",org.openspcoop2.core.tracciamento.Data.class,"riscontro",Riscontro.class));
		this.RICEVUTA = new ComplexField(father,"ricevuta",java.lang.String.class,"riscontro",Riscontro.class);
	
	}
	
	

	public IField IDENTIFICATIVO = null;
	 
	public org.openspcoop2.core.tracciamento.model.DataModel ORA_REGISTRAZIONE = null;
	 
	public IField RICEVUTA = null;
	 

	@Override
	public Class<Riscontro> getModeledClass(){
		return Riscontro.class;
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