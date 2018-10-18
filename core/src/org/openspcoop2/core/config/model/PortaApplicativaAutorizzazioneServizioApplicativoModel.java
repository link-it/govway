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

import org.openspcoop2.core.config.PortaApplicativaAutorizzazioneServizioApplicativo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PortaApplicativaAutorizzazioneServizioApplicativo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaApplicativaAutorizzazioneServizioApplicativoModel extends AbstractModel<PortaApplicativaAutorizzazioneServizioApplicativo> {

	public PortaApplicativaAutorizzazioneServizioApplicativoModel(){
	
		super();
	
		this.TIPO_SOGGETTO_PROPRIETARIO = new Field("tipo-soggetto-proprietario",java.lang.String.class,"porta-applicativa-autorizzazione-servizio-applicativo",PortaApplicativaAutorizzazioneServizioApplicativo.class);
		this.NOME_SOGGETTO_PROPRIETARIO = new Field("nome-soggetto-proprietario",java.lang.String.class,"porta-applicativa-autorizzazione-servizio-applicativo",PortaApplicativaAutorizzazioneServizioApplicativo.class);
		this.NOME = new Field("nome",java.lang.String.class,"porta-applicativa-autorizzazione-servizio-applicativo",PortaApplicativaAutorizzazioneServizioApplicativo.class);
	
	}
	
	public PortaApplicativaAutorizzazioneServizioApplicativoModel(IField father){
	
		super(father);
	
		this.TIPO_SOGGETTO_PROPRIETARIO = new ComplexField(father,"tipo-soggetto-proprietario",java.lang.String.class,"porta-applicativa-autorizzazione-servizio-applicativo",PortaApplicativaAutorizzazioneServizioApplicativo.class);
		this.NOME_SOGGETTO_PROPRIETARIO = new ComplexField(father,"nome-soggetto-proprietario",java.lang.String.class,"porta-applicativa-autorizzazione-servizio-applicativo",PortaApplicativaAutorizzazioneServizioApplicativo.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"porta-applicativa-autorizzazione-servizio-applicativo",PortaApplicativaAutorizzazioneServizioApplicativo.class);
	
	}
	
	

	public IField TIPO_SOGGETTO_PROPRIETARIO = null;
	 
	public IField NOME_SOGGETTO_PROPRIETARIO = null;
	 
	public IField NOME = null;
	 

	@Override
	public Class<PortaApplicativaAutorizzazioneServizioApplicativo> getModeledClass(){
		return PortaApplicativaAutorizzazioneServizioApplicativo.class;
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