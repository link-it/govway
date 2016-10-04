/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.pdd.core.autenticazione;

import java.util.Properties;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;

/**
 * Esempio che definisce un gestore delle credenziali
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreCredenzialiTest extends AbstractCore implements IGestoreCredenziali {

	public static final String TEST_CREDENZIALI_BASIC_USERNAME = "X-OpenSPCoop-TestCredenziali-BasicUsername";
	public static final String TEST_CREDENZIALI_BASIC_PASSWORD = "X-OpenSPCoop-TestCredenziali-BasicPassword";
	public static final String TEST_CREDENZIALI_SSL_SUBJECT = "X-OpenSPCoop-TestCredenziali-SSLSubject";
	public static final String TEST_CREDENZIALI_SIMULAZIONE_ERRORE = "X-OpenSPCoop-TestCredenziali-SimulazioneErrore";
	public static final String TEST_CREDENZIALI_SIMULAZIONE_ERRORE_CONFIGURAZIONE = "X-OpenSPCoop-TestCredenziali-SimulazioneErroreConfigurazione";
	
	private String identita = null;
	
	@Override
	public Credenziali elaborazioneCredenziali(InfoConnettoreIngresso infoConnettoreIngresso, 
			OpenSPCoop2Message messaggio) throws GestoreCredenzialiException,GestoreCredenzialiConfigurationException{
		
		Credenziali c = new Credenziali();
		
		if(existsHeader(infoConnettoreIngresso.getUrlProtocolContext().getParametersTrasporto(), GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_USERNAME)){
			String username = getProperty(infoConnettoreIngresso.getUrlProtocolContext().getParametersTrasporto(),GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_USERNAME);	
			String password = getProperty(infoConnettoreIngresso.getUrlProtocolContext().getParametersTrasporto(),GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_PASSWORD);
			if(username==null || "".equals(username)){
				throw new GestoreCredenzialiConfigurationException("Username value non fornito");
			}
			if(password==null || "".equals(password)){
				throw new GestoreCredenzialiConfigurationException("Password value non fornito");
			}
			c.setUsername(username);
			c.setPassword(password);
		}
		else if(existsHeader(infoConnettoreIngresso.getUrlProtocolContext().getParametersTrasporto(), GestoreCredenzialiTest.TEST_CREDENZIALI_SSL_SUBJECT)){
			String subject = getProperty(infoConnettoreIngresso.getUrlProtocolContext().getParametersTrasporto(),GestoreCredenzialiTest.TEST_CREDENZIALI_SSL_SUBJECT);
			if(subject==null || "".equals(subject)){
				throw new GestoreCredenzialiConfigurationException("Subject value non fornito");
			}
			c.setSubject(subject);
		}
		else if(existsHeader(infoConnettoreIngresso.getUrlProtocolContext().getParametersTrasporto(), GestoreCredenzialiTest.TEST_CREDENZIALI_SIMULAZIONE_ERRORE)){
			throw new GestoreCredenzialiException("Eccezione generale richiesta dalla testsuite");
		}
		else if(existsHeader(infoConnettoreIngresso.getUrlProtocolContext().getParametersTrasporto(), GestoreCredenzialiTest.TEST_CREDENZIALI_SIMULAZIONE_ERRORE_CONFIGURAZIONE)){
			throw new GestoreCredenzialiConfigurationException("Eccezione, di configurazione, richiesta dalla testsuite");
		}
		else{
			// ritorno credenziali originali
			return infoConnettoreIngresso.getCredenziali();
		}
		
		if(infoConnettoreIngresso.getCredenziali().getSubject()!=null || infoConnettoreIngresso.getCredenziali().getUsername()!=null)
			this.identita = "GestoreCredenziali di test "+infoConnettoreIngresso.getCredenziali().toString();
		else
			this.identita = "GestoreCredenziali di test anonimo";
		
		return c;
		
	}
	
	@Override
	public String getIdentitaGestoreCredenziali(){
		return this.identita;
	}
	
	private boolean existsHeader(Properties properties, String name){
		if(properties!=null){
			return properties.containsKey(name) ||
				properties.containsKey(name.toLowerCase()) ||
				properties.containsKey(name.toUpperCase());
		}else{
			return false;
		}
	}
	
	private String getProperty(Properties properties, String name){
		if(properties!=null){
			String tmp = properties.getProperty(name);
			if(tmp==null){
				tmp = properties.getProperty(name.toLowerCase());
			}
			if(tmp==null){
				tmp = properties.getProperty(name.toUpperCase());
			}
			return tmp;
		}else{
			return null;
		}
		
	}
}
