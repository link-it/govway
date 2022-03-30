/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

package org.openspcoop2.protocol.utils;

import java.util.List;

import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;

/**
 * PorteNamingUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteNamingUtils {

	private IProtocolFactory<?> protocolFactory;
	private IProtocolConfiguration protocolConfiguration;
	private ServiceBinding serviceBindingDefault = null;
	
	public PorteNamingUtils(IProtocolFactory<?> protocolFactory) throws ProtocolException {
		this.protocolFactory = protocolFactory;
		this.protocolConfiguration = protocolFactory.createProtocolConfiguration();
		
		if(this.protocolFactory.getManifest().getBinding().getRest()!=null &&
				this.protocolFactory.getManifest().getBinding().getSoap()!=null) {
			// deve essere specificato un service binding di default
			switch(this.protocolFactory.getManifest().getBinding().getDefault()) {
			case REST:
				this.serviceBindingDefault = ServiceBinding.REST;
				break;
			case SOAP:
				this.serviceBindingDefault = ServiceBinding.SOAP;
				break;
			}
		}
		else if(this.protocolFactory.getManifest().getBinding().getRest()!=null) {
			this.serviceBindingDefault = ServiceBinding.REST;
		}
		else {
			this.serviceBindingDefault = ServiceBinding.SOAP;
		}
	}
	
	
	public String normalizePD(String nome) throws ProtocolException {
		// Convenzione PD: FRUITORE/EROGATORE/SERVIZIO/...
		// Ogni oggetto viene separato dal tipo "_"
		 int [] posizioneSoggetto = new int[2];
		 posizioneSoggetto[0] = 0;
		 posizioneSoggetto[1] = 1;
		 int [] posizioneServizio = new int[1];
		 posizioneServizio[0] = 2;
		 return _normalize(nome, posizioneSoggetto, posizioneServizio);
	}
	public String normalizePA(String nome) throws ProtocolException {
		// Convenzione PA: EROGATORE/SERVIZIO/...
		// Ogni oggetto viene separato dal tipo "_"
		 int [] posizioneSoggetto = new int[1];
		 posizioneSoggetto[0] = 0;
		 int [] posizioneServizio = new int[1];
		 posizioneServizio[0] = 1;
		 return _normalize(nome, posizioneSoggetto, posizioneServizio);
	}
	private String _normalize(String nome, int [] posizioneSoggetto, int [] posizioneServizio) throws ProtocolException {
		if(nome.contains("/")) {
			
			String tipoSoggettoDefault = this.protocolConfiguration.getTipoSoggettoDefault();
			String _tipoSoggettoDefault = tipoSoggettoDefault +"_";
			int _lengthTipoSoggettoDefault = _tipoSoggettoDefault.length();
			
			String tipoServizioDefault = this.protocolConfiguration.getTipoServizioDefault(this.serviceBindingDefault);
			String _tipoServizioDefault = tipoServizioDefault +"_";
			int _lengthTipoServizioDefault = _tipoServizioDefault.length();
			
			String [] tmp = nome.split("/");
			if(tmp.length>=3) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < tmp.length; i++) {
					if(i>0) {
						bf.append("/");
					}
					
					boolean soggetto = false;
					for (int j = 0; j < posizioneSoggetto.length; j++) {
						if(i==posizioneSoggetto[j]) {
							soggetto = true;
							break;
						}
					}
					
					boolean servizio = false;
					for (int j = 0; j < posizioneServizio.length; j++) {
						if(i==posizioneServizio[j]) {
							servizio = true;
							break;
						}
					}
					
					String s = tmp[i].trim();
					if(soggetto || servizio) {
						if(soggetto) {
							if(s.startsWith(_tipoSoggettoDefault) && s.length()>_lengthTipoSoggettoDefault) {
								bf.append(s.substring(_lengthTipoSoggettoDefault));
							}
							else {
								bf.append(s);
							}
						}
						else {
							if(s.startsWith(_tipoServizioDefault) && s.length()>_lengthTipoServizioDefault) {
								bf.append(s.substring(_lengthTipoServizioDefault));
							}
							else {
								bf.append(s);
							}
						}
					}
					else {
						bf.append(s);
					}
				}
				return bf.toString();
			}
			
		}
		return nome;
	}
	
	
	
	public String enrichPD(String nome) throws ProtocolException {
		// Convenzione PD: FRUITORE/EROGATORE/SERVIZIO/...
		// Ogni oggetto viene separato dal tipo "_"
		 int [] posizioneSoggetto = new int[2];
		 posizioneSoggetto[0] = 0;
		 posizioneSoggetto[1] = 1;
		 int [] posizioneServizio = new int[1];
		 posizioneServizio[0] = 2;
		 return _enrich(nome, posizioneSoggetto, posizioneServizio);
	}
	public String enrichPA(String nome) throws ProtocolException {
		// Convenzione PA: EROGATORE/SERVIZIO/...
		// Ogni oggetto viene separato dal tipo "_"
		 int [] posizioneSoggetto = new int[1];
		 posizioneSoggetto[0] = 0;
		 int [] posizioneServizio = new int[1];
		 posizioneServizio[0] = 1;
		 return _enrich(nome, posizioneSoggetto, posizioneServizio);
	}
	private String _enrich(String nome, int [] posizioneSoggetto, int [] posizioneServizio) throws ProtocolException {
		if(nome.contains("/")) {
			
			String tipoSoggettoDefault = this.protocolConfiguration.getTipoSoggettoDefault();
			String _tipoSoggettoDefault = tipoSoggettoDefault +"_";
			List<String> tipiSoggetto = this.protocolConfiguration.getTipiSoggetti();
			
			String tipoServizioDefault = this.protocolConfiguration.getTipoServizioDefault(this.serviceBindingDefault);
			String _tipoServizioDefault = tipoServizioDefault +"_";
			List<String> tipiServizi = this.protocolConfiguration.getTipiServizi(this.serviceBindingDefault);
			
			String [] tmp = nome.split("/");
			if(tmp.length>=3) {
				StringBuilder bf = new StringBuilder();
				for (int i = 0; i < tmp.length; i++) {
					if(i>0) {
						bf.append("/");
					}
					
					boolean soggetto = false;
					for (int j = 0; j < posizioneSoggetto.length; j++) {
						if(i==posizioneSoggetto[j]) {
							soggetto = true;
							break;
						}
					}
					
					boolean servizio = false;
					for (int j = 0; j < posizioneServizio.length; j++) {
						if(i==posizioneServizio[j]) {
							servizio = true;
							break;
						}
					}
					
					String s = tmp[i].trim();
					if(soggetto || servizio) {
						if(soggetto) {
							if(s.contains("_")==false) {
								bf.append(_tipoSoggettoDefault);	
							}
							else {
								boolean found = false;
								for (String tipoSoggetto : tipiSoggetto) {
									if(s.startsWith(tipoSoggetto+"_")) {
										found = true;
										break;
									}
								}
								if(!found) {
									bf.append(_tipoSoggettoDefault);	
								}
							}
							bf.append(s);
						}
						else {
							if(s.contains("_")==false) {
								bf.append(_tipoServizioDefault);	
							}
							else {
								boolean found = false;
								for (String tipoServizio : tipiServizi) {
									if(s.startsWith(tipoServizio+"_")) {
										found = true;
										break;
									}
								}
								if(!found) {
									bf.append(_tipoServizioDefault);	
								}
							}
							bf.append(s);
						}
					}
					else {
						bf.append(s);
					}
				}
				return bf.toString();
			}
			
		}
		return nome;
	}
	
}
