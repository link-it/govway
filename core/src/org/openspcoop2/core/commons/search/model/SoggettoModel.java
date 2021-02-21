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
package org.openspcoop2.core.commons.search.model;

import org.openspcoop2.core.commons.search.Soggetto;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Soggetto 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoggettoModel extends AbstractModel<Soggetto> {

	public SoggettoModel(){
	
		super();
	
		this.NOME_SOGGETTO = new Field("nome-soggetto",java.lang.String.class,"soggetto",Soggetto.class);
		this.TIPO_SOGGETTO = new Field("tipo-soggetto",java.lang.String.class,"soggetto",Soggetto.class);
		this.SERVER = new Field("server",java.lang.String.class,"soggetto",Soggetto.class);
		this.IDENTIFICATIVO_PORTA = new Field("identificativo-porta",java.lang.String.class,"soggetto",Soggetto.class);
		this.SOGGETTO_RUOLO = new org.openspcoop2.core.commons.search.model.SoggettoRuoloModel(new Field("soggetto-ruolo",org.openspcoop2.core.commons.search.SoggettoRuolo.class,"soggetto",Soggetto.class));
	
	}
	
	public SoggettoModel(IField father){
	
		super(father);
	
		this.NOME_SOGGETTO = new ComplexField(father,"nome-soggetto",java.lang.String.class,"soggetto",Soggetto.class);
		this.TIPO_SOGGETTO = new ComplexField(father,"tipo-soggetto",java.lang.String.class,"soggetto",Soggetto.class);
		this.SERVER = new ComplexField(father,"server",java.lang.String.class,"soggetto",Soggetto.class);
		this.IDENTIFICATIVO_PORTA = new ComplexField(father,"identificativo-porta",java.lang.String.class,"soggetto",Soggetto.class);
		this.SOGGETTO_RUOLO = new org.openspcoop2.core.commons.search.model.SoggettoRuoloModel(new ComplexField(father,"soggetto-ruolo",org.openspcoop2.core.commons.search.SoggettoRuolo.class,"soggetto",Soggetto.class));
	
	}
	
	

	public IField NOME_SOGGETTO = null;
	 
	public IField TIPO_SOGGETTO = null;
	 
	public IField SERVER = null;
	 
	public IField IDENTIFICATIVO_PORTA = null;
	 
	public org.openspcoop2.core.commons.search.model.SoggettoRuoloModel SOGGETTO_RUOLO = null;
	 

	@Override
	public Class<Soggetto> getModeledClass(){
		return Soggetto.class;
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