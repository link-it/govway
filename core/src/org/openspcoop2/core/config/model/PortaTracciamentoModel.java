/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.PortaTracciamento;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PortaTracciamento 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaTracciamentoModel extends AbstractModel<PortaTracciamento> {

	public PortaTracciamentoModel(){
	
		super();
	
		this.SEVERITA = new Field("severita",java.lang.String.class,"porta-tracciamento",PortaTracciamento.class);
		this.ESITI = new Field("esiti",java.lang.String.class,"porta-tracciamento",PortaTracciamento.class);
	
	}
	
	public PortaTracciamentoModel(IField father){
	
		super(father);
	
		this.SEVERITA = new ComplexField(father,"severita",java.lang.String.class,"porta-tracciamento",PortaTracciamento.class);
		this.ESITI = new ComplexField(father,"esiti",java.lang.String.class,"porta-tracciamento",PortaTracciamento.class);
	
	}
	
	

	public IField SEVERITA = null;
	 
	public IField ESITI = null;
	 

	@Override
	public Class<PortaTracciamento> getModeledClass(){
		return PortaTracciamento.class;
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