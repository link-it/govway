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
package org.openspcoop2.protocol.abstraction.model;

import org.openspcoop2.protocol.abstraction.DatiServizio;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiServizio 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiServizioModel extends AbstractModel<DatiServizio> {

	public DatiServizioModel(){
	
		super();
	
		this.ENDPOINT = new Field("endpoint",java.lang.String.class,"DatiServizio",DatiServizio.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"DatiServizio",DatiServizio.class);
		this.NOME = new Field("nome",java.lang.String.class,"DatiServizio",DatiServizio.class);
		this.TIPOLOGIA_SERVIZIO = new Field("tipologia-servizio",java.lang.String.class,"DatiServizio",DatiServizio.class);
		this.FRUITORI = new org.openspcoop2.protocol.abstraction.model.FruitoriModel(new Field("fruitori",org.openspcoop2.protocol.abstraction.Fruitori.class,"DatiServizio",DatiServizio.class));
	
	}
	
	public DatiServizioModel(IField father){
	
		super(father);
	
		this.ENDPOINT = new ComplexField(father,"endpoint",java.lang.String.class,"DatiServizio",DatiServizio.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"DatiServizio",DatiServizio.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"DatiServizio",DatiServizio.class);
		this.TIPOLOGIA_SERVIZIO = new ComplexField(father,"tipologia-servizio",java.lang.String.class,"DatiServizio",DatiServizio.class);
		this.FRUITORI = new org.openspcoop2.protocol.abstraction.model.FruitoriModel(new ComplexField(father,"fruitori",org.openspcoop2.protocol.abstraction.Fruitori.class,"DatiServizio",DatiServizio.class));
	
	}
	
	

	public IField ENDPOINT = null;
	 
	public IField TIPO = null;
	 
	public IField NOME = null;
	 
	public IField TIPOLOGIA_SERVIZIO = null;
	 
	public org.openspcoop2.protocol.abstraction.model.FruitoriModel FRUITORI = null;
	 

	@Override
	public Class<DatiServizio> getModeledClass(){
		return DatiServizio.class;
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