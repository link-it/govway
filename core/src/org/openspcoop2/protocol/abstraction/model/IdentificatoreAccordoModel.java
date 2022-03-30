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
package org.openspcoop2.protocol.abstraction.model;

import org.openspcoop2.protocol.abstraction.IdentificatoreAccordo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model IdentificatoreAccordo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IdentificatoreAccordoModel extends AbstractModel<IdentificatoreAccordo> {

	public IdentificatoreAccordoModel(){
	
		super();
	
		this.SOGGETTO = new org.openspcoop2.protocol.abstraction.model.SoggettoModel(new Field("soggetto",org.openspcoop2.protocol.abstraction.Soggetto.class,"IdentificatoreAccordo",IdentificatoreAccordo.class));
		this.NOME = new Field("nome",java.lang.String.class,"IdentificatoreAccordo",IdentificatoreAccordo.class);
		this.VERSIONE = new Field("versione",java.lang.Integer.class,"IdentificatoreAccordo",IdentificatoreAccordo.class);
	
	}
	
	public IdentificatoreAccordoModel(IField father){
	
		super(father);
	
		this.SOGGETTO = new org.openspcoop2.protocol.abstraction.model.SoggettoModel(new ComplexField(father,"soggetto",org.openspcoop2.protocol.abstraction.Soggetto.class,"IdentificatoreAccordo",IdentificatoreAccordo.class));
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"IdentificatoreAccordo",IdentificatoreAccordo.class);
		this.VERSIONE = new ComplexField(father,"versione",java.lang.Integer.class,"IdentificatoreAccordo",IdentificatoreAccordo.class);
	
	}
	
	

	public org.openspcoop2.protocol.abstraction.model.SoggettoModel SOGGETTO = null;
	 
	public IField NOME = null;
	 
	public IField VERSIONE = null;
	 

	@Override
	public Class<IdentificatoreAccordo> getModeledClass(){
		return IdentificatoreAccordo.class;
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