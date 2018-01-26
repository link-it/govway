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


package org.openspcoop2.protocol.as4.config;

import org.slf4j.Logger;

import java.util.Enumeration;
import java.util.Properties;

import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.protocol.basic.config.BasicVersionManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;

/**
 * Classe che implementa, in base al protocollo AS4, l'interfaccia {@link org.openspcoop2.protocol.sdk.config.IProtocolVersionManager} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AS4ProtocolVersionManager extends BasicVersionManager {
	
	protected AS4Properties as4Properties = null;
	protected Logger logger = null;
	protected String versione;
	public AS4ProtocolVersionManager(IProtocolFactory<?> protocolFactory,String versione) throws ProtocolException{
		super(protocolFactory);
		this.versione = versione;
		this.logger = this.getProtocolFactory().getLogger();
		this.as4Properties = AS4Properties.getInstance(this.logger);
	}
	
	
	@Override
	public Boolean isAggiungiDetailErroreApplicativo_FaultApplicativo() {
		return this.as4Properties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo();
	}

	@Override
	public Boolean isAggiungiDetailErroreApplicativo_FaultPdD() {
		return this.as4Properties.isAggiungiDetailErroreApplicativo_SoapFaultPdD();
	}
	
	@Override
	public org.openspcoop2.core.registry.Connettore getStaticRoute(IDSoggetto idSoggettoMittente, IDServizio idServizio,
			IRegistryReader registryReader) throws ProtocolException{
		try {
			boolean registry = this.as4Properties.isDomibusGatewayRegistry();
			if(registry) {
				
				String tipo = this.protocolFactory.createProtocolConfiguration().getTipoSoggettoDefault();
				
				IDSoggetto idSoggettoGateway = null;
				String nome = this.as4Properties.getDomibusGatewayRegistrySoggettoCustom(idSoggettoMittente.getNome());
				if(nome!=null && !"".equals(nome)) {
					idSoggettoGateway = new IDSoggetto(tipo, nome);
				}
				else {
					idSoggettoGateway = new IDSoggetto(tipo, this.as4Properties.getDomibusGatewayRegistrySoggettoDefault());
				}
				
				Soggetto s = null;
				try {
					s = registryReader.getSoggetto(idSoggettoGateway);
					if(s==null) {
						throw new RegistryNotFound();
					}
				}catch(RegistryNotFound notFound) {
					throw new Exception("Soggetto Gateway ["+idSoggettoGateway+"], indicato nella configurazione, non risulta esistere nel registro",notFound);
				}
				if(s.getConnettore()==null || TipiConnettore.DISABILITATO.getNome().equals(s.getConnettore().getTipo())) {
					throw new Exception("Soggetto Gateway ["+idSoggettoGateway+"], indicato nella configurazione, non contiene la definizione di un connettore");
				}
				return s.getConnettore();
			}
			else {
				String url = this.as4Properties.getDomibusGatewayConfigCustomUrl(idSoggettoMittente.getNome());
				if(url==null || "".equals(url)) {
					url = this.as4Properties.getDomibusGatewayConfigDefaultUrl();
				}
								
				String tipoConnettore = TipiConnettore.HTTP.getNome();
				Properties pConnettore = null;
				Boolean https = this.as4Properties.isDomibusGatewayConfigCustomHttpsEnabled(idSoggettoMittente.getNome());
				if(https==null) {
					https = this.as4Properties.isDomibusGatewayConfigDefaultHttpsEnabled();
				}
				if(https) {
					tipoConnettore = TipiConnettore.HTTPS.getNome();
					pConnettore = this.as4Properties.getDomibusGatewayConfigCustomHttpsProperties(idSoggettoMittente.getNome());
					if(pConnettore==null || pConnettore.size()<=0) {
						pConnettore = this.as4Properties.getDomibusGatewayConfigDefaultHttpsProperties();
					}
				}
				
				
				org.openspcoop2.core.registry.Connettore c = new org.openspcoop2.core.registry.Connettore();
				c.setNome("DomibusGateway");
				c.setTipo(tipoConnettore);
				Property pUrl = new Property();
				pUrl.setNome(CostantiConnettori.CONNETTORE_LOCATION);
				pUrl.setValore(url);
				c.getPropertyList().add(pUrl);
				if(pConnettore!=null && pConnettore.size()>0) {
					Enumeration<?> names = pConnettore.keys();
					while (names.hasMoreElements()) {
						Object object = (Object) names.nextElement();
						if(object instanceof String) {
							String key = (String) object;
							String value = pConnettore.getProperty(key);
							Property p = new Property();
							p.setNome(key);
							p.setValore(value);
							c.getPropertyList().add(p);
						}
					}
				}
				return c;
			}
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	

	
}
