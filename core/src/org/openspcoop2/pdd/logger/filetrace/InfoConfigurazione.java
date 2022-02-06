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
package org.openspcoop2.pdd.logger.filetrace;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.core.handlers.transazioni.PostOutResponseHandler_TransazioneUtilities;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Context;

/**     
 * InfoConfigurazione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InfoConfigurazione implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected static final String KEYS_SEPARATOR = ",";
	protected static final String PROPERTY_SEPARATOR = " ";
	protected static final String VALUE_SEPARATOR = "=";
	
	private Map<String, String> context = new HashMap<String, String>();
	
	private Map<String, String> apiImplProperties = new HashMap<String, String>();
	
	private Map<String, String> soggettoFruitoreProperties = new HashMap<String, String>();
	private Map<String, String> soggettoErogatoreProperties = new HashMap<String, String>();
	
	private Map<String, String> applicativoFruitoreProperties = new HashMap<String, String>();
	
	private String nomeConnettoriMultipli = null;
	
	public InfoConfigurazione(Transazione transazioneDTO, Context contextGateway) {
		
		if(contextGateway!=null && !contextGateway.isEmpty()) {
			for (String key : contextGateway.keys()) {
				Object o = contextGateway.getObject(key);
				if(o!=null && o instanceof String) {
					String s = (String) o;
					this.context.put(key, s);
				}
			}
		}
		
		
		ConfigurazionePdDManager configurazionePdDManager = ConfigurazionePdDManager.getInstance();
		RegistroServiziManager registroServiziManager = RegistroServiziManager.getInstance();
					
		IDSoggetto idSoggettoFruitore = null;
		IDSoggetto idSoggettoErogatore = null;
		
		if(contextGateway!=null) {
			
			this.nomeConnettoriMultipli = PostOutResponseHandler_TransazioneUtilities.getConnettoriMultipli(contextGateway);
			
			if(contextGateway.containsKey(Costanti.REQUEST_INFO)) {
				try {
					RequestInfo requestInfo = (RequestInfo) contextGateway.getObject(Costanti.REQUEST_INFO);					
					if(requestInfo.getProtocolContext()!=null) {
						String nomePorta = requestInfo.getProtocolContext().getInterfaceName();
						if(PddRuolo.DELEGATA.equals(transazioneDTO.getPddRuolo())) {
							IDPortaDelegata idPD = new IDPortaDelegata();
							idPD.setNome(nomePorta);
							PortaDelegata pd = configurazionePdDManager.getPortaDelegata_SafeMethod(idPD);
							if(pd!=null) {
								
								idSoggettoFruitore = new IDSoggetto(pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario());
								idSoggettoErogatore = new IDSoggetto(pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome());
								
								if(pd.sizeProprietaList()>0) {
									for (Proprieta prop : pd.getProprietaList()) {
										if(prop.getNome()!=null && prop.getValore()!=null) {
											this.apiImplProperties.put(prop.getNome(), prop.getValore());
										}
									}
								}
							}
						}
						else {
							IDPortaApplicativa idPA = new IDPortaApplicativa();
							idPA.setNome(nomePorta);
							PortaApplicativa pa = configurazionePdDManager.getPortaApplicativa_SafeMethod(idPA);
							if(pa!=null) {
								
								idSoggettoErogatore = new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario());
								
								if(pa.sizeProprietaList()>0) {
									for (Proprieta prop : pa.getProprietaList()) {
										if(prop.getNome()!=null && prop.getValore()!=null) {
											this.apiImplProperties.put(prop.getNome(), prop.getValore());
										}
									}
								}
							}
						}
					}
				}catch(Throwable t) {}
			}
		}
		
		if(idSoggettoFruitore==null && transazioneDTO.getTipoSoggettoFruitore()!=null && transazioneDTO.getNomeSoggettoFruitore()!=null) {
			idSoggettoFruitore = new IDSoggetto(transazioneDTO.getTipoSoggettoFruitore(), transazioneDTO.getNomeSoggettoFruitore());
		}
		if(idSoggettoFruitore!=null) {
			try {
				Soggetto soggetto = registroServiziManager.getSoggetto(idSoggettoFruitore, null);
				if(soggetto.sizeProprietaList()>0) {
					for (org.openspcoop2.core.registry.Proprieta prop : soggetto.getProprietaList()) {
						if(prop.getNome()!=null && prop.getValore()!=null) {
							this.soggettoFruitoreProperties.put(prop.getNome(), prop.getValore());
						}
					}
				}
			}catch(Throwable t) {}
			
			if(transazioneDTO.getServizioApplicativoFruitore()!=null) {
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setNome(transazioneDTO.getServizioApplicativoFruitore());
				idSA.setIdSoggettoProprietario(idSoggettoFruitore);
				try {
					ServizioApplicativo sa = configurazionePdDManager.getServizioApplicativo(idSA);
					if(sa.sizeProprietaList()>0) {
						for (Proprieta prop : sa.getProprietaList()) {
							if(prop.getNome()!=null && prop.getValore()!=null) {
								this.applicativoFruitoreProperties.put(prop.getNome(), prop.getValore());
							}
						}
					}
				}catch(Throwable t) {}
			}
		}
		
		if(idSoggettoErogatore==null && transazioneDTO.getTipoSoggettoErogatore()!=null && transazioneDTO.getNomeSoggettoErogatore()!=null) {
			idSoggettoErogatore = new IDSoggetto(transazioneDTO.getTipoSoggettoErogatore(), transazioneDTO.getNomeSoggettoErogatore());
		}
		if(idSoggettoErogatore!=null) {
			try {
				Soggetto soggetto = registroServiziManager.getSoggetto(idSoggettoErogatore, null);
				if(soggetto.sizeProprietaList()>0) {
					for (org.openspcoop2.core.registry.Proprieta prop : soggetto.getProprietaList()) {
						if(prop.getNome()!=null && prop.getValore()!=null) {
							this.soggettoErogatoreProperties.put(prop.getNome(), prop.getValore());
						}
					}
				}
			}catch(Throwable t) {}
		}
		
	}
	
	public String getContextProperty(String nome) {
		return _getProperty(this.context, nome);
	}
	public Map<String, String> getContextProperties(String nome) {
		return this.context;
	}
	public List<String> getContextPropertiesKeys(){
		return _getPropertiesKeys(this.context);
	}
	public String getContextPropertiesKeysAsString(String separator){
		return _getPropertiesKeysAsString(this.context, separator);
	}
	public String getContextPropertiesAsString(String propertySeparator, String valueSeparator){
		return _getPropertiesAsString(this.context, propertySeparator, valueSeparator);
	}
	
	public String getApiImplProperty(String nome) {
		return _getProperty(this.apiImplProperties, nome);
	}
	public Map<String, String> getApiImplProperties(String nome) {
		return this.apiImplProperties;
	}
	public List<String> getApiImplPropertiesKeys(){
		return _getPropertiesKeys(this.apiImplProperties);
	}
	public String getApiImplPropertiesKeysAsString(String separator){
		return _getPropertiesKeysAsString(this.apiImplProperties, separator);
	}
	public String getApiImplPropertiesAsString(String propertySeparator, String valueSeparator){
		return _getPropertiesAsString(this.apiImplProperties, propertySeparator, valueSeparator);
	}
	
	public String getSenderProperty(String nome) {
		return _getProperty(this.soggettoFruitoreProperties, nome);
	}
	public Map<String, String> getSenderProperties(String nome) {
		return this.soggettoFruitoreProperties;
	}
	public List<String> getSenderPropertiesKeys(){
		return _getPropertiesKeys(this.soggettoFruitoreProperties);
	}
	public String getSenderPropertiesKeysAsString(String separator){
		return _getPropertiesKeysAsString(this.soggettoFruitoreProperties, separator);
	}
	public String getSenderPropertiesAsString(String propertySeparator, String valueSeparator){
		return _getPropertiesAsString(this.soggettoFruitoreProperties, propertySeparator, valueSeparator);
	}
	
	public String getProviderProperty(String nome) {
		return _getProperty(this.soggettoErogatoreProperties, nome);
	}
	public Map<String, String> getProviderProperties(String nome) {
		return this.soggettoErogatoreProperties;
	}
	public List<String> getProviderPropertiesKeys(){
		return _getPropertiesKeys(this.soggettoErogatoreProperties);
	}
	public String getProviderPropertiesKeysAsString(String separator){
		return _getPropertiesKeysAsString(this.soggettoErogatoreProperties, separator);
	}
	public String getProviderPropertiesAsString(String propertySeparator, String valueSeparator){
		return _getPropertiesAsString(this.soggettoErogatoreProperties, propertySeparator, valueSeparator);
	}
	
	public String getApplicationProperty(String nome) {
		return _getProperty(this.applicativoFruitoreProperties, nome);
	}
	public Map<String, String> getApplicationProperties(String nome) {
		return this.applicativoFruitoreProperties;
	}
	public List<String> getApplicationPropertiesKeys(){
		return _getPropertiesKeys(this.applicativoFruitoreProperties);
	}
	public String getApplicationPropertiesKeysAsString(String separator){
		return _getPropertiesKeysAsString(this.applicativoFruitoreProperties, separator);
	}
	public String getApplicationPropertiesAsString(String propertySeparator, String valueSeparator){
		return _getPropertiesAsString(this.applicativoFruitoreProperties, propertySeparator, valueSeparator);
	}
	
	
	public String getOutConnectorName() {
		return this.nomeConnettoriMultipli;
	}
	
	
	private String _getProperty(Map<String, String> p, String nome) {
		if(p!=null) {
			return p.get(nome);
		}
		return null;
	}
	private List<String> _getPropertiesKeys(Map<String, String> p){
		if(p!=null && !p.isEmpty()) {
			List<String> l = new ArrayList<String>();
			l.addAll(p.keySet());
			return l;
		}
		return null;
	}
	private String _getPropertiesKeysAsString(Map<String, String> p, String separator){
		if(p!=null && !p.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (String key : p.keySet()) {
				if(sb.length()>0) {
					sb.append(separator);
				}
				sb.append(key);
			}
			return sb.toString();
		}
		return null;
	}
	private String _getPropertiesAsString(Map<String, String> p, String propertySeparator, String valueSeparator ){
		if(p!=null && !p.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (String key : p.keySet()) {
				if(sb.length()>0) {
					sb.append(propertySeparator);
				}
				sb.append(key);
				sb.append(valueSeparator);
				sb.append(p.get(key));
			}
			return sb.toString();
		}
		return null;
	}
	
}
