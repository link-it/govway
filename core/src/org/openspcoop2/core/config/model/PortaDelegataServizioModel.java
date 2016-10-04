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

import org.openspcoop2.core.config.PortaDelegataServizio;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PortaDelegataServizio 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaDelegataServizioModel extends AbstractModel<PortaDelegataServizio> {

	public PortaDelegataServizioModel(){
	
		super();
	
		this.IDENTIFICAZIONE = new Field("identificazione",java.lang.String.class,"porta-delegata-servizio",PortaDelegataServizio.class);
		this.PATTERN = new Field("pattern",java.lang.String.class,"porta-delegata-servizio",PortaDelegataServizio.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"porta-delegata-servizio",PortaDelegataServizio.class);
		this.NOME = new Field("nome",java.lang.String.class,"porta-delegata-servizio",PortaDelegataServizio.class);
	
	}
	
	public PortaDelegataServizioModel(IField father){
	
		super(father);
	
		this.IDENTIFICAZIONE = new ComplexField(father,"identificazione",java.lang.String.class,"porta-delegata-servizio",PortaDelegataServizio.class);
		this.PATTERN = new ComplexField(father,"pattern",java.lang.String.class,"porta-delegata-servizio",PortaDelegataServizio.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"porta-delegata-servizio",PortaDelegataServizio.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"porta-delegata-servizio",PortaDelegataServizio.class);
	
	}
	
	

	public IField IDENTIFICAZIONE = null;
	 
	public IField PATTERN = null;
	 
	public IField TIPO = null;
	 
	public IField NOME = null;
	 

	@Override
	public Class<PortaDelegataServizio> getModeledClass(){
		return PortaDelegataServizio.class;
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