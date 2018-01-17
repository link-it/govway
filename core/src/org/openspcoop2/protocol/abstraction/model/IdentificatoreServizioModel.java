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
package org.openspcoop2.protocol.abstraction.model;

import org.openspcoop2.protocol.abstraction.IdentificatoreServizio;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model IdentificatoreServizio 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IdentificatoreServizioModel extends AbstractModel<IdentificatoreServizio> {

	public IdentificatoreServizioModel(){
	
		super();
	
		this.SOGGETTO = new org.openspcoop2.protocol.abstraction.model.SoggettoModel(new Field("soggetto",org.openspcoop2.protocol.abstraction.Soggetto.class,"IdentificatoreServizio",IdentificatoreServizio.class));
		this.NOME = new Field("nome",java.lang.String.class,"IdentificatoreServizio",IdentificatoreServizio.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"IdentificatoreServizio",IdentificatoreServizio.class);
	
	}
	
	public IdentificatoreServizioModel(IField father){
	
		super(father);
	
		this.SOGGETTO = new org.openspcoop2.protocol.abstraction.model.SoggettoModel(new ComplexField(father,"soggetto",org.openspcoop2.protocol.abstraction.Soggetto.class,"IdentificatoreServizio",IdentificatoreServizio.class));
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"IdentificatoreServizio",IdentificatoreServizio.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"IdentificatoreServizio",IdentificatoreServizio.class);
	
	}
	
	

	public org.openspcoop2.protocol.abstraction.model.SoggettoModel SOGGETTO = null;
	 
	public IField NOME = null;
	 
	public IField TIPO = null;
	 

	@Override
	public Class<IdentificatoreServizio> getModeledClass(){
		return IdentificatoreServizio.class;
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