/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.core.plugins.model;

import org.openspcoop2.core.plugins.ConfigurazioneServizio;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConfigurazioneServizio 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneServizioModel extends AbstractModel<ConfigurazioneServizio> {

	public ConfigurazioneServizioModel(){
	
		super();
	
		this.ACCORDO = new Field("accordo",java.lang.String.class,"configurazione-servizio",ConfigurazioneServizio.class);
		this.TIPO_SOGGETTO_REFERENTE = new Field("tipo-soggetto-referente",java.lang.String.class,"configurazione-servizio",ConfigurazioneServizio.class);
		this.NOME_SOGGETTO_REFERENTE = new Field("nome-soggetto-referente",java.lang.String.class,"configurazione-servizio",ConfigurazioneServizio.class);
		this.VERSIONE = new Field("versione",java.lang.Integer.class,"configurazione-servizio",ConfigurazioneServizio.class);
		this.SERVIZIO = new Field("servizio",java.lang.String.class,"configurazione-servizio",ConfigurazioneServizio.class);
		this.CONFIGURAZIONE_SERVIZIO_AZIONE = new org.openspcoop2.core.plugins.model.IdConfigurazioneServizioAzioneModel(new Field("configurazione-servizio-azione",org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione.class,"configurazione-servizio",ConfigurazioneServizio.class));
	
	}
	
	public ConfigurazioneServizioModel(IField father){
	
		super(father);
	
		this.ACCORDO = new ComplexField(father,"accordo",java.lang.String.class,"configurazione-servizio",ConfigurazioneServizio.class);
		this.TIPO_SOGGETTO_REFERENTE = new ComplexField(father,"tipo-soggetto-referente",java.lang.String.class,"configurazione-servizio",ConfigurazioneServizio.class);
		this.NOME_SOGGETTO_REFERENTE = new ComplexField(father,"nome-soggetto-referente",java.lang.String.class,"configurazione-servizio",ConfigurazioneServizio.class);
		this.VERSIONE = new ComplexField(father,"versione",java.lang.Integer.class,"configurazione-servizio",ConfigurazioneServizio.class);
		this.SERVIZIO = new ComplexField(father,"servizio",java.lang.String.class,"configurazione-servizio",ConfigurazioneServizio.class);
		this.CONFIGURAZIONE_SERVIZIO_AZIONE = new org.openspcoop2.core.plugins.model.IdConfigurazioneServizioAzioneModel(new ComplexField(father,"configurazione-servizio-azione",org.openspcoop2.core.plugins.IdConfigurazioneServizioAzione.class,"configurazione-servizio",ConfigurazioneServizio.class));
	
	}
	
	

	public IField ACCORDO = null;
	 
	public IField TIPO_SOGGETTO_REFERENTE = null;
	 
	public IField NOME_SOGGETTO_REFERENTE = null;
	 
	public IField VERSIONE = null;
	 
	public IField SERVIZIO = null;
	 
	public org.openspcoop2.core.plugins.model.IdConfigurazioneServizioAzioneModel CONFIGURAZIONE_SERVIZIO_AZIONE = null;
	 

	@Override
	public Class<ConfigurazioneServizio> getModeledClass(){
		return ConfigurazioneServizio.class;
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