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



package org.openspcoop2.pdd.core.autenticazione;

import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.utils.transport.http.HttpConstants;


/**
 * AutenticazioneUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AutenticazioneUtils {

	public static void finalizeProcessHeaderAuthorization(OpenSPCoop2Message message, boolean clean) throws AutenticazioneException {
		try {
			if(message.getTransportRequestContext()!=null) {
				if(message.getTransportRequestContext().getParametersTrasporto()!=null) {
					String headerValue = message.getTransportRequestContext().getParameterTrasporto(HttpConstants.AUTHORIZATION);
					if(headerValue!=null) {
						message.getTransportRequestContext().removeParameterTrasporto(HttpConstants.AUTHORIZATION);
						if(!clean) {
							message.forceTransportHeader(HttpConstants.AUTHORIZATION, headerValue); // serve soprattutto per soap
						}
					}
	    		}
			}
		}catch(Throwable t) {
			throw new AutenticazioneException("Clean Header Authorization failed: "+t.getMessage(),t);
		}
    }
	
	public static void finalizeProcessPrincipal(OpenSPCoop2Message message, TipoAutenticazionePrincipal tipoAutenticazionePrincipal, String nome, boolean clean) throws AutenticazioneException {
		try {
			switch (tipoAutenticazionePrincipal) {
			case CONTAINER:
			case INDIRIZZO_IP:
			case INDIRIZZO_IP_X_FORWARDED_FOR:
			case URL:
			case TOKEN:
				break;
			case HEADER:
				if(nome!=null && message.getTransportRequestContext()!=null) {
					if(message.getTransportRequestContext().getParametersTrasporto()!=null) {
						String headerValue = message.getTransportRequestContext().getParameterTrasporto(nome);
						if(headerValue!=null) {
							message.getTransportRequestContext().removeParameterTrasporto(nome);
							if(!clean) {
								message.forceTransportHeader(nome, headerValue); // serve soprattutto per soap
							}
						}
		    		}
				}
				break;
			case FORM:
				if(message.getTransportRequestContext()!=null) {
					if(message.getTransportRequestContext().getParametersFormBased()!=null) {
						String propertyValue = message.getTransportRequestContext().getParameterFormBased(nome);
						if(propertyValue!=null) {
							message.getTransportRequestContext().removeParameterFormBased(nome);
							if(!clean) {
								message.forceUrlProperty(nome, propertyValue); // serve soprattutto per soap
							}
						}
		    		}
				}
				break;
			}
		}catch(Throwable t) {
			throw new AutenticazioneException("Clean Principal failed: "+t.getMessage(),t);
		}
    }
	
	public static void finalizeProcessApiKey(OpenSPCoop2Message message, 
			boolean header, boolean cookie, boolean queryParameter, 
			String nomeHeader, String nomeCookie, String nomeQueryParameter,
			boolean clean) throws AutenticazioneException {
		
		if(header) {
			if(nomeHeader!=null && message.getTransportRequestContext()!=null) {
				if(message.getTransportRequestContext().getParametersTrasporto()!=null) {
					String headerValue = message.getTransportRequestContext().getParameterTrasporto(nomeHeader);
					if(headerValue!=null) {
						message.getTransportRequestContext().removeParameterTrasporto(nomeHeader);
						if(!clean) {
							message.forceTransportHeader(nomeHeader, headerValue); // serve soprattutto per soap
						}
					}
	    		}
			}
		}
		if(cookie && clean) { // su soap deve essere effettuato il forward solamente se consentito dalle regole
			if(nomeCookie!=null && message.getTransportRequestContext()!=null) {
				if(message.getTransportRequestContext().getParametersTrasporto()!=null) {
					String headerValue = message.getTransportRequestContext().getParameterTrasporto(HttpConstants.COOKIE);
					if(headerValue!=null) {
						message.getTransportRequestContext().removeParameterTrasporto(HttpConstants.COOKIE);
						String [] tmp = headerValue.split(HttpConstants.COOKIE_SEPARATOR);
						StringBuilder sb = new StringBuilder();
						if(tmp!=null && tmp.length>0) {
							for (int i = 0; i < tmp.length; i++) {
								String cNameValue = tmp[i];
								String [] c = cNameValue.split(HttpConstants.COOKIE_NAME_VALUE_SEPARATOR);
								if(c!=null && c.length>0) {
									String name = c[0];
									if(!nomeCookie.equalsIgnoreCase(name)) {
										if(sb.length()>0) {
											sb.append(HttpConstants.COOKIE_SEPARATOR);
										}
										sb.append(cNameValue);
									}
								}
							}
						}
						if(sb.length()>0) {
							message.getTransportRequestContext().getParametersTrasporto().put(HttpConstants.COOKIE, sb.toString());
						}
					}
	    		}
			}
		}
		if(queryParameter) {
			if(message.getTransportRequestContext()!=null) {
				if(message.getTransportRequestContext().getParametersFormBased()!=null) {
					String propertyValue = message.getTransportRequestContext().getParameterFormBased(nomeQueryParameter);
					if(propertyValue!=null) {
						message.getTransportRequestContext().removeParameterFormBased(nomeQueryParameter);
						if(!clean) {
							message.forceUrlProperty(nomeQueryParameter, propertyValue); // serve soprattutto per soap
						}
					}
	    		}
			}
		}
		
	}
}
