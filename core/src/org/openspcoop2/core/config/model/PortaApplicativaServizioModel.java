/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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

import org.openspcoop2.core.config.PortaApplicativaServizio;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PortaApplicativaServizio 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaApplicativaServizioModel extends AbstractModel<PortaApplicativaServizio> {

	public PortaApplicativaServizioModel(){
	
		super();
	
		this.TIPO = new Field("tipo",java.lang.String.class,"porta-applicativa-servizio",PortaApplicativaServizio.class);
		this.NOME = new Field("nome",java.lang.String.class,"porta-applicativa-servizio",PortaApplicativaServizio.class);
		this.VERSIONE = new Field("versione",java.lang.Integer.class,"porta-applicativa-servizio",PortaApplicativaServizio.class);
	
	}
	
	public PortaApplicativaServizioModel(IField father){
	
		super(father);
	
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"porta-applicativa-servizio",PortaApplicativaServizio.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"porta-applicativa-servizio",PortaApplicativaServizio.class);
		this.VERSIONE = new ComplexField(father,"versione",java.lang.Integer.class,"porta-applicativa-servizio",PortaApplicativaServizio.class);
	
	}
	
	

	public IField TIPO = null;
	 
	public IField NOME = null;
	 
	public IField VERSIONE = null;
	 

	@Override
	public Class<PortaApplicativaServizio> getModeledClass(){
		return PortaApplicativaServizio.class;
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