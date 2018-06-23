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
package org.openspcoop2.monitor.engine.config.statistiche.model;

import org.openspcoop2.monitor.engine.config.statistiche.IdConfigurazioneServizio;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model IdConfigurazioneServizio 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IdConfigurazioneServizioModel extends AbstractModel<IdConfigurazioneServizio> {

	public IdConfigurazioneServizioModel(){
	
		super();
	
		this.ACCORDO = new Field("accordo",java.lang.String.class,"id-configurazione-servizio",IdConfigurazioneServizio.class);
		this.TIPO_SOGGETTO_REFERENTE = new Field("tipo-soggetto-referente",java.lang.String.class,"id-configurazione-servizio",IdConfigurazioneServizio.class);
		this.NOME_SOGGETTO_REFERENTE = new Field("nome-soggetto-referente",java.lang.String.class,"id-configurazione-servizio",IdConfigurazioneServizio.class);
		this.VERSIONE = new Field("versione",java.lang.Integer.class,"id-configurazione-servizio",IdConfigurazioneServizio.class);
		this.SERVIZIO = new Field("servizio",java.lang.String.class,"id-configurazione-servizio",IdConfigurazioneServizio.class);
	
	}
	
	public IdConfigurazioneServizioModel(IField father){
	
		super(father);
	
		this.ACCORDO = new ComplexField(father,"accordo",java.lang.String.class,"id-configurazione-servizio",IdConfigurazioneServizio.class);
		this.TIPO_SOGGETTO_REFERENTE = new ComplexField(father,"tipo-soggetto-referente",java.lang.String.class,"id-configurazione-servizio",IdConfigurazioneServizio.class);
		this.NOME_SOGGETTO_REFERENTE = new ComplexField(father,"nome-soggetto-referente",java.lang.String.class,"id-configurazione-servizio",IdConfigurazioneServizio.class);
		this.VERSIONE = new ComplexField(father,"versione",java.lang.Integer.class,"id-configurazione-servizio",IdConfigurazioneServizio.class);
		this.SERVIZIO = new ComplexField(father,"servizio",java.lang.String.class,"id-configurazione-servizio",IdConfigurazioneServizio.class);
	
	}
	
	

	public IField ACCORDO = null;
	 
	public IField TIPO_SOGGETTO_REFERENTE = null;
	 
	public IField NOME_SOGGETTO_REFERENTE = null;
	 
	public IField VERSIONE = null;
	 
	public IField SERVIZIO = null;
	 

	@Override
	public Class<IdConfigurazioneServizio> getModeledClass(){
		return IdConfigurazioneServizio.class;
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