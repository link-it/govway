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

package org.openspcoop2.pdd.core.integrazione;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.slf4j.Logger;

/**
 * Classe contenenti utilities per le integrazioni gestite tramite autenticazione
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UtilitiesAutenticazione {

	private OpenSPCoop2Message msg;
	@SuppressWarnings("unused")
	private Context context;
	@SuppressWarnings("unused")
	private Busta busta;
	@SuppressWarnings("unused")
	private Logger log;
	
	private List<String> headers = null;
	private Map<String, String> hdrValues = null;
	
	public UtilitiesAutenticazione(HeaderIntegrazione integrazione,
			OutRequestPDMessage inRequestPDMessage, Context context, Logger log) throws HeaderIntegrazioneException {
		try {
			init(inRequestPDMessage.getMessage(), context, inRequestPDMessage.getBustaRichiesta(), log);
			
			List<Proprieta> proprieta = null;
			if(inRequestPDMessage.getPortaDelegata()!=null && inRequestPDMessage.getPortaDelegata().getProprietaList()!=null) {
				proprieta = inRequestPDMessage.getPortaDelegata().getProprietaList();
			}
			init(proprieta, true);
			
		}catch(Exception e) {
			throw new HeaderIntegrazioneException(e.getMessage(),e);
		}
	}
	
	public UtilitiesAutenticazione(HeaderIntegrazione integrazione,
			OutRequestPAMessage inRequestPAMessage, Context context, Logger log) throws HeaderIntegrazioneException {
		try {
			init(inRequestPAMessage.getMessage(), context, inRequestPAMessage.getBustaRichiesta(), log);
			
			List<Proprieta> proprieta = null;
			if(inRequestPAMessage.getPortaApplicativa()!=null && inRequestPAMessage.getPortaApplicativa().getProprietaList()!=null) {
				proprieta = inRequestPAMessage.getPortaApplicativa().getProprietaList();
			}
			init(proprieta, false);
			
		}catch(Exception e) {
			throw new HeaderIntegrazioneException(e.getMessage(),e);
		}
	}
	
	private void init(OpenSPCoop2Message msg, Context context, Busta busta, Logger log){
		this.msg = msg;
		this.context = context;
		this.busta = busta;
		this.log = log;
	}
	private void init(List<Proprieta> proprieta, boolean portaDelegata) throws HeaderIntegrazioneException {
		try {
			OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();

			if(portaDelegata) {
				this.headers = properties.getIntegrazioneAutenticazionePortaDelegataRequestHeaders();
				this.hdrValues = properties.getIntegrazioneAutenticazionePortaDelegataRequestHeadersMap();
			}
			else {
				this.headers = properties.getIntegrazioneAutenticazionePortaApplicativaRequestHeaders();
				this.hdrValues = properties.getIntegrazioneAutenticazionePortaApplicativaRequestHeadersMap();
			}
			
			if(proprieta!=null && !proprieta.isEmpty()) {
				
				String headersPropertyName = properties.getIntegrazioneAutenticazionePropertyHeaders();
				String headerPrefixPropertyName = properties.getIntegrazioneAutenticazionePropertyHeaderPrefix();
				
				for (Proprieta p : proprieta) {
					if(headersPropertyName.equalsIgnoreCase(p.getNome())) {
						this.headers = convert(p.getValore(), headersPropertyName);
					}
				}
				if(this.headers!=null && !this.headers.isEmpty()) {
					for (String hdr : this.headers) {
						String pName = headerPrefixPropertyName+hdr;
						for (Proprieta p : proprieta) {
							if(pName.equalsIgnoreCase(p.getNome())) {
								String valore = p.getValore();
								this.hdrValues.put(hdr, valore); // aggiorno valore se già definito
							}
						}
					}
				}
			}

			if(this.headers==null || this.headers.isEmpty()) {
				throw new HeaderIntegrazioneException("Nessun header di autenticazione configurato");
			}
			if(this.hdrValues==null || this.hdrValues.isEmpty()) {
				throw new HeaderIntegrazioneException("Nessun valore definito per gli header di autenticazione configurati");
			}
			for (String hdr : this.headers) {
				String v = this.hdrValues.get(hdr);
				if(v==null) {
					throw new HeaderIntegrazioneException("Nessun valore definito per l'header di autenticazione '"+hdr+"' configurato");
				}
			}

		}catch(Exception e) {
			throw new HeaderIntegrazioneException(e.getMessage(),e);
		}
	}

	public static List<String> convert(String headers, String pName) throws HeaderIntegrazioneException {
		try {
			List<String> l = new ArrayList<>();
			if(headers!=null) {
				headers = headers.trim();
				String [] split = headers.split(",");
				if(split!=null){
					for (int i = 0; i < split.length; i++) {
						String v = split[i];
						if(v!=null) {
							v = v.trim();
						}
						if(!"".equals(v)) {
							l.add(v);
						}
					}
				}
			}
			if(l.isEmpty()) {
				throw new HeaderIntegrazioneException("Trovata proprietà '"+pName+"' che non contiene alcun header");
			}
			return l;
		}catch(Exception e) {
			throw new HeaderIntegrazioneException(e.getMessage(),e);
		}
	}

		
	public void process() throws HeaderIntegrazioneException {
		try {
			if(this.msg==null) {
				return;
			}
			
			for (String hdr : this.headers) {
				String v = this.hdrValues.get(hdr);
				this.msg.forceTransportHeader(hdr, v);
			}
			
		}catch(Exception e) {
			throw new HeaderIntegrazioneException(e.getMessage(),e);
		}
	}
}
