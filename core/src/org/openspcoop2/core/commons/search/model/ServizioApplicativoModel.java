/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.openspcoop2.core.commons.search.ServizioApplicativo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ServizioApplicativo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServizioApplicativoModel extends AbstractModel<ServizioApplicativo> {

	public ServizioApplicativoModel(){
	
		super();
	
		this.NOME = new Field("nome",java.lang.String.class,"servizio-applicativo",ServizioApplicativo.class);
		this.TIPOLOGIA_FRUIZIONE = new Field("tipologia_fruizione",java.lang.String.class,"servizio-applicativo",ServizioApplicativo.class);
		this.TIPOLOGIA_EROGAZIONE = new Field("tipologia_erogazione",java.lang.String.class,"servizio-applicativo",ServizioApplicativo.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"servizio-applicativo",ServizioApplicativo.class);
		this.AS_CLIENT = new Field("as_client",java.lang.Integer.class,"servizio-applicativo",ServizioApplicativo.class);
		this.ID_SOGGETTO = new org.openspcoop2.core.commons.search.model.IdSoggettoModel(new Field("id-soggetto",org.openspcoop2.core.commons.search.IdSoggetto.class,"servizio-applicativo",ServizioApplicativo.class));
	
	}
	
	public ServizioApplicativoModel(IField father){
	
		super(father);
	
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"servizio-applicativo",ServizioApplicativo.class);
		this.TIPOLOGIA_FRUIZIONE = new ComplexField(father,"tipologia_fruizione",java.lang.String.class,"servizio-applicativo",ServizioApplicativo.class);
		this.TIPOLOGIA_EROGAZIONE = new ComplexField(father,"tipologia_erogazione",java.lang.String.class,"servizio-applicativo",ServizioApplicativo.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"servizio-applicativo",ServizioApplicativo.class);
		this.AS_CLIENT = new ComplexField(father,"as_client",java.lang.Integer.class,"servizio-applicativo",ServizioApplicativo.class);
		this.ID_SOGGETTO = new org.openspcoop2.core.commons.search.model.IdSoggettoModel(new ComplexField(father,"id-soggetto",org.openspcoop2.core.commons.search.IdSoggetto.class,"servizio-applicativo",ServizioApplicativo.class));
	
	}
	
	

	public IField NOME = null;
	 
	public IField TIPOLOGIA_FRUIZIONE = null;
	 
	public IField TIPOLOGIA_EROGAZIONE = null;
	 
	public IField TIPO = null;
	 
	public IField AS_CLIENT = null;
	 
	public org.openspcoop2.core.commons.search.model.IdSoggettoModel ID_SOGGETTO = null;
	 

	@Override
	public Class<ServizioApplicativo> getModeledClass(){
		return ServizioApplicativo.class;
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