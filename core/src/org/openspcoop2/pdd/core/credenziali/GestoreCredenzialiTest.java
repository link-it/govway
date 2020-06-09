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

package org.openspcoop2.pdd.core.credenziali;

import java.util.Map;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.utils.transport.TransportUtils;

/**
 * Esempio che definisce un gestore delle credenziali
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreCredenzialiTest extends AbstractCore implements IGestoreCredenziali {

	public static final String TEST_CREDENZIALI_BASIC_USERNAME = "GovWay-TestCredenziali-BasicUsername";
	public static final String TEST_CREDENZIALI_BASIC_PASSWORD = "GovWay-TestCredenziali-BasicPassword";
	public static final String TEST_CREDENZIALI_SSL_SUBJECT = "GovWay-TestCredenziali-SSLSubject";
	public static final String TEST_CREDENZIALI_SIMULAZIONE_ERRORE = "GovWay-TestCredenziali-SimulazioneErrore";
	public static final String TEST_CREDENZIALI_SIMULAZIONE_ERRORE_CONFIGURAZIONE = "GovWay-TestCredenziali-SimulazioneErroreConfigurazione";
	public static final String TEST_CREDENZIALI_SIMULAZIONE_ERRORE_FORWARD = "GovWay-TestCredenziali-SimulazioneErroreForward";
	
	private String identita = null;
	
	@Override
	public Credenziali elaborazioneCredenziali(InfoConnettoreIngresso infoConnettoreIngresso, 
			OpenSPCoop2Message messaggio) throws GestoreCredenzialiException,GestoreCredenzialiConfigurationException{
		
		Credenziali c = new Credenziali();
		
		if(existsHeader(infoConnettoreIngresso.getUrlProtocolContext().getParametersTrasporto(), GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_USERNAME)){
			String username = getProperty(infoConnettoreIngresso.getUrlProtocolContext().getParametersTrasporto(),GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_USERNAME);	
			String password = getProperty(infoConnettoreIngresso.getUrlProtocolContext().getParametersTrasporto(),GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_PASSWORD);
			if(username==null || "".equals(username)){
				throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_CREDENTIALS_NOT_FOUND, "Username value non fornito");
			}
			if(password==null || "".equals(password)){
				throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_CREDENTIALS_NOT_FOUND, "Password value non fornito");
			}
			c.setUsername(username);
			c.setPassword(password);
		}
		else if(existsHeader(infoConnettoreIngresso.getUrlProtocolContext().getParametersTrasporto(), GestoreCredenzialiTest.TEST_CREDENZIALI_SSL_SUBJECT)){
			String subject = getProperty(infoConnettoreIngresso.getUrlProtocolContext().getParametersTrasporto(),GestoreCredenzialiTest.TEST_CREDENZIALI_SSL_SUBJECT);
			if(subject==null || "".equals(subject)){
				throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_CREDENTIALS_NOT_FOUND, "Subject value non fornito");
			}
			c.setSubject(subject);
		}
		else if(existsHeader(infoConnettoreIngresso.getUrlProtocolContext().getParametersTrasporto(), GestoreCredenzialiTest.TEST_CREDENZIALI_SIMULAZIONE_ERRORE)){
			throw new GestoreCredenzialiException("Eccezione generale richiesta dalla testsuite");
		}
		else if(existsHeader(infoConnettoreIngresso.getUrlProtocolContext().getParametersTrasporto(), GestoreCredenzialiTest.TEST_CREDENZIALI_SIMULAZIONE_ERRORE_CONFIGURAZIONE)){
			throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_INVALID_CREDENTIALS, "Eccezione, di configurazione, richiesta dalla testsuite");
		}
		else if(existsHeader(infoConnettoreIngresso.getUrlProtocolContext().getParametersTrasporto(), GestoreCredenzialiTest.TEST_CREDENZIALI_SIMULAZIONE_ERRORE_FORWARD)){
			throw new GestoreCredenzialiConfigurationException(IntegrationFunctionError.PROXY_AUTHENTICATION_FORWARDED_CREDENTIALS_NOT_FOUND, "Eccezione, di configurazione, richiesta dalla testsuite");
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
	
	private boolean existsHeader(Map<String, String> properties, String name){
		if(properties!=null){
			return TransportUtils.hasKey(properties, name);
		}else{
			return false;
		}
	}
	
	private String getProperty(Map<String, String> properties, String name){
		if(properties!=null){
			return TransportUtils.get(properties, name);
		}else{
			return null;
		}
		
	}
}
