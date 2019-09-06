/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import org.openspcoop2.core.config.ConfigurazioneProtocollo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConfigurazioneProtocollo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneProtocolloModel extends AbstractModel<ConfigurazioneProtocollo> {

	public ConfigurazioneProtocolloModel(){
	
		super();
	
		this.NOME = new Field("nome",java.lang.String.class,"configurazione-protocollo",ConfigurazioneProtocollo.class);
		this.URL_INVOCAZIONE_SERVIZIO_PD = new Field("urlInvocazioneServizioPD",java.lang.String.class,"configurazione-protocollo",ConfigurazioneProtocollo.class);
		this.URL_INVOCAZIONE_SERVIZIO_PA = new Field("urlInvocazioneServizioPA",java.lang.String.class,"configurazione-protocollo",ConfigurazioneProtocollo.class);
		this.URL_INVOCAZIONE_SERVIZIO_REST_PD = new Field("urlInvocazioneServizioRestPD",java.lang.String.class,"configurazione-protocollo",ConfigurazioneProtocollo.class);
		this.URL_INVOCAZIONE_SERVIZIO_REST_PA = new Field("urlInvocazioneServizioRestPA",java.lang.String.class,"configurazione-protocollo",ConfigurazioneProtocollo.class);
		this.URL_INVOCAZIONE_SERVIZIO_SOAP_PD = new Field("urlInvocazioneServizioSoapPD",java.lang.String.class,"configurazione-protocollo",ConfigurazioneProtocollo.class);
		this.URL_INVOCAZIONE_SERVIZIO_SOAP_PA = new Field("urlInvocazioneServizioSoapPA",java.lang.String.class,"configurazione-protocollo",ConfigurazioneProtocollo.class);
	
	}
	
	public ConfigurazioneProtocolloModel(IField father){
	
		super(father);
	
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"configurazione-protocollo",ConfigurazioneProtocollo.class);
		this.URL_INVOCAZIONE_SERVIZIO_PD = new ComplexField(father,"urlInvocazioneServizioPD",java.lang.String.class,"configurazione-protocollo",ConfigurazioneProtocollo.class);
		this.URL_INVOCAZIONE_SERVIZIO_PA = new ComplexField(father,"urlInvocazioneServizioPA",java.lang.String.class,"configurazione-protocollo",ConfigurazioneProtocollo.class);
		this.URL_INVOCAZIONE_SERVIZIO_REST_PD = new ComplexField(father,"urlInvocazioneServizioRestPD",java.lang.String.class,"configurazione-protocollo",ConfigurazioneProtocollo.class);
		this.URL_INVOCAZIONE_SERVIZIO_REST_PA = new ComplexField(father,"urlInvocazioneServizioRestPA",java.lang.String.class,"configurazione-protocollo",ConfigurazioneProtocollo.class);
		this.URL_INVOCAZIONE_SERVIZIO_SOAP_PD = new ComplexField(father,"urlInvocazioneServizioSoapPD",java.lang.String.class,"configurazione-protocollo",ConfigurazioneProtocollo.class);
		this.URL_INVOCAZIONE_SERVIZIO_SOAP_PA = new ComplexField(father,"urlInvocazioneServizioSoapPA",java.lang.String.class,"configurazione-protocollo",ConfigurazioneProtocollo.class);
	
	}
	
	

	public IField NOME = null;
	 
	public IField URL_INVOCAZIONE_SERVIZIO_PD = null;
	 
	public IField URL_INVOCAZIONE_SERVIZIO_PA = null;
	 
	public IField URL_INVOCAZIONE_SERVIZIO_REST_PD = null;
	 
	public IField URL_INVOCAZIONE_SERVIZIO_REST_PA = null;
	 
	public IField URL_INVOCAZIONE_SERVIZIO_SOAP_PD = null;
	 
	public IField URL_INVOCAZIONE_SERVIZIO_SOAP_PA = null;
	 

	@Override
	public Class<ConfigurazioneProtocollo> getModeledClass(){
		return ConfigurazioneProtocollo.class;
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