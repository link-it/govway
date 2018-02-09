/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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

package org.openspcoop2.protocol.utils;

import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;

/**
 * PorteNamingUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 13575 $, $Date: 2018-01-26 12:33:08 +0100 (Fri, 26 Jan 2018) $
 */
public class PorteNamingUtils {

	@SuppressWarnings("unused")
	private IProtocolFactory<?> protocolFactory;
	private IProtocolConfiguration protocolConfiguration;
	
	public PorteNamingUtils(IProtocolFactory<?> protocolFactory) throws ProtocolException {
		this.protocolFactory = protocolFactory;
		this.protocolConfiguration = protocolFactory.createProtocolConfiguration();
	}
	
	
	public String normalizePD(String nome, ServiceBinding serviceBinding) throws ProtocolException {
		// Convenzione PD: FRUITORE/EROGATORE/SERVIZIO/...
		// Ogni oggetto viene separato dal tipo "_"
		 int [] posizioneSoggetto = new int[2];
		 posizioneSoggetto[0] = 0;
		 posizioneSoggetto[1] = 1;
		 int [] posizioneServizio = new int[1];
		 posizioneSoggetto[0] = 2;
		 return _normalize(nome, serviceBinding, posizioneSoggetto, posizioneServizio);
	}
	public String normalizePA(String nome, ServiceBinding serviceBinding) throws ProtocolException {
		// Convenzione PA: EROGATORE/SERVIZIO/...
		// Ogni oggetto viene separato dal tipo "_"
		 int [] posizioneSoggetto = new int[1];
		 posizioneSoggetto[0] = 0;
		 int [] posizioneServizio = new int[1];
		 posizioneSoggetto[0] = 1;
		 return _normalize(nome, serviceBinding, posizioneSoggetto, posizioneServizio);
	}
	private String _normalize(String nome, ServiceBinding serviceBinding, int [] posizioneSoggetto, int [] posizioneServizio) throws ProtocolException {
		if(nome.contains("/")) {
			
			String tipoSoggettoDefault = this.protocolConfiguration.getTipoSoggettoDefault();
			String _tipoSoggettoDefault = tipoSoggettoDefault +"_";
			int _lengthTipoSoggettoDefault = _tipoSoggettoDefault.length();
			
			String tipoServizioDefault = this.protocolConfiguration.getTipoServizioDefault(serviceBinding);
			String _tipoServizioDefault = tipoServizioDefault +"_";
			int _lengthTipoServizioDefault = _tipoServizioDefault.length();
			
			String [] tmp = nome.split("/");
			if(tmp.length>=3) {
				StringBuffer bf = new StringBuffer();
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
							if(s.startsWith(tipoSoggettoDefault) && s.length()>_lengthTipoSoggettoDefault) {
								bf.append(s.substring(_lengthTipoSoggettoDefault));
							}
							else {
								bf.append(s);
							}
						}
						else {
							if(s.startsWith(tipoServizioDefault) && s.length()>_lengthTipoServizioDefault) {
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
			}
			
		}
		return nome;
	}
	
	
	
	public String enrichPD(String nome, ServiceBinding serviceBinding) throws ProtocolException {
		// Convenzione PD: FRUITORE/EROGATORE/SERVIZIO/...
		// Ogni oggetto viene separato dal tipo "_"
		 int [] posizioneSoggetto = new int[2];
		 posizioneSoggetto[0] = 0;
		 posizioneSoggetto[1] = 1;
		 int [] posizioneServizio = new int[1];
		 posizioneSoggetto[0] = 2;
		 return _enrich(nome, serviceBinding, posizioneSoggetto, posizioneServizio);
	}
	public String enrichPA(String nome, ServiceBinding serviceBinding) throws ProtocolException {
		// Convenzione PA: EROGATORE/SERVIZIO/...
		// Ogni oggetto viene separato dal tipo "_"
		 int [] posizioneSoggetto = new int[1];
		 posizioneSoggetto[0] = 0;
		 int [] posizioneServizio = new int[1];
		 posizioneSoggetto[0] = 1;
		 return _enrich(nome, serviceBinding, posizioneSoggetto, posizioneServizio);
	}
	private String _enrich(String nome, ServiceBinding serviceBinding, int [] posizioneSoggetto, int [] posizioneServizio) throws ProtocolException {
		if(nome.contains("/")) {
			
			String tipoSoggettoDefault = this.protocolConfiguration.getTipoSoggettoDefault();
			String _tipoSoggettoDefault = tipoSoggettoDefault +"_";
			
			String tipoServizioDefault = this.protocolConfiguration.getTipoServizioDefault(serviceBinding);
			String _tipoServizioDefault = tipoServizioDefault +"_";
			
			String [] tmp = nome.split("/");
			if(tmp.length>=3) {
				StringBuffer bf = new StringBuffer();
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
							bf.append(s);
						}
						else {
							if(s.contains("_")==false) {
								bf.append(_tipoServizioDefault);	
							}
							bf.append(s);
						}
					}
					else {
						bf.append(s);
					}
				}
			}
			
		}
		return nome;
	}
	
}
