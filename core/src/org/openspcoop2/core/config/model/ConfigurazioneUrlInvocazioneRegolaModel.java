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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConfigurazioneUrlInvocazioneRegola 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneUrlInvocazioneRegolaModel extends AbstractModel<ConfigurazioneUrlInvocazioneRegola> {

	public ConfigurazioneUrlInvocazioneRegolaModel(){
	
		super();
	
		this.SOGGETTO = new org.openspcoop2.core.config.model.IdSoggettoModel(new Field("soggetto",org.openspcoop2.core.config.IdSoggetto.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class));
		this.NOME = new Field("nome",java.lang.String.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class);
		this.POSIZIONE = new Field("posizione",int.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class);
		this.STATO = new Field("stato",java.lang.String.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class);
		this.REGEXPR = new Field("regexpr",boolean.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class);
		this.REGOLA = new Field("regola",java.lang.String.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class);
		this.CONTESTO_ESTERNO = new Field("contesto-esterno",java.lang.String.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class);
		this.BASE_URL = new Field("base-url",java.lang.String.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class);
		this.PROTOCOLLO = new Field("protocollo",java.lang.String.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class);
		this.RUOLO = new Field("ruolo",java.lang.String.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class);
		this.SERVICE_BINDING = new Field("service-binding",java.lang.String.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class);
	
	}
	
	public ConfigurazioneUrlInvocazioneRegolaModel(IField father){
	
		super(father);
	
		this.SOGGETTO = new org.openspcoop2.core.config.model.IdSoggettoModel(new ComplexField(father,"soggetto",org.openspcoop2.core.config.IdSoggetto.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class));
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class);
		this.POSIZIONE = new ComplexField(father,"posizione",int.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class);
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class);
		this.REGEXPR = new ComplexField(father,"regexpr",boolean.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class);
		this.REGOLA = new ComplexField(father,"regola",java.lang.String.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class);
		this.CONTESTO_ESTERNO = new ComplexField(father,"contesto-esterno",java.lang.String.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class);
		this.BASE_URL = new ComplexField(father,"base-url",java.lang.String.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class);
		this.PROTOCOLLO = new ComplexField(father,"protocollo",java.lang.String.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class);
		this.RUOLO = new ComplexField(father,"ruolo",java.lang.String.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class);
		this.SERVICE_BINDING = new ComplexField(father,"service-binding",java.lang.String.class,"configurazione-url-invocazione-regola",ConfigurazioneUrlInvocazioneRegola.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.IdSoggettoModel SOGGETTO = null;
	 
	public IField NOME = null;
	 
	public IField POSIZIONE = null;
	 
	public IField STATO = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField REGEXPR = null;
	 
	public IField REGOLA = null;
	 
	public IField CONTESTO_ESTERNO = null;
	 
	public IField BASE_URL = null;
	 
	public IField PROTOCOLLO = null;
	 
	public IField RUOLO = null;
	 
	public IField SERVICE_BINDING = null;
	 

	@Override
	public Class<ConfigurazioneUrlInvocazioneRegola> getModeledClass(){
		return ConfigurazioneUrlInvocazioneRegola.class;
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