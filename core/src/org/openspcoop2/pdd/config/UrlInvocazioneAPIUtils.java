/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.config;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.RuoloContesto;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.pdd.core.autorizzazione.canali.CanaliUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.Utilities;

/**
 * UrlInvocazioneAPIUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UrlInvocazioneAPIUtils {

	private UrlInvocazioneAPIUtils() {}
	
	public static UrlInvocazioneAPI getUrlInvocazioneObject(IProtocolFactory<?> protocolFactory, IState state, RequestInfo requestInfo,
			boolean rest, AccordoServizioParteComune aspc, RuoloContesto ruoloContesto, String nomePorta, IDSoggetto proprietarioPorta) throws ProtocolException {
		try {
			List<String> tags = new ArrayList<>();
			if(aspc!=null && aspc.getGruppi()!=null && aspc.getGruppi().sizeGruppoList()>0) {
				for (int i = 0; i < aspc.getGruppi().sizeGruppoList(); i++) {
					tags.add(aspc.getGruppi().getGruppo(i).getNome());
				}
			}
			
			IConfigIntegrationReader configReader = protocolFactory.getCachedConfigIntegrationReader(state, requestInfo);
			CanaliConfigurazione canaliConfigurazione = configReader.getCanaliConfigurazione();
			String canaleApi = null;
			if(aspc!=null) {
				canaleApi = aspc.getCanale();
			}
			String canalePorta = null;
			if(nomePorta!=null) {
				if(RuoloContesto.PORTA_APPLICATIVA.equals(ruoloContesto)) {
					try {
						IDPortaApplicativa idPA = new IDPortaApplicativa();
						idPA.setNome(nomePorta);
						PortaApplicativa pa = configReader.getPortaApplicativa(idPA);
						canalePorta = pa.getNome();
					}catch(Exception t) {
						// ignore
					}
				}
				else {
					try {
						IDPortaDelegata idPD = new IDPortaDelegata();
						idPD.setNome(nomePorta);
						PortaDelegata pd = configReader.getPortaDelegata(idPD);
						canalePorta = pd.getNome();
					}catch(Exception t) {
						// ignore
					}
				}
			}
			String canale = CanaliUtils.getCanale(canaliConfigurazione, canaleApi, canalePorta);
			
			return ConfigurazionePdDManager.getInstance().getConfigurazioneUrlInvocazione(protocolFactory, 
					ruoloContesto,
					rest ? org.openspcoop2.message.constants.ServiceBinding.REST : org.openspcoop2.message.constants.ServiceBinding.SOAP,
					nomePorta,
					proprietarioPorta,
					tags, canale, 
					requestInfo);		 
			
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	public static String getUrlInvocazione(IProtocolFactory<?> protocolFactory, IState state, RequestInfo requestInfo,
			boolean rest, AccordoServizioParteComune aspc, RuoloContesto ruoloContesto, String nomePorta, IDSoggetto proprietarioPorta) throws ProtocolException {
		UrlInvocazioneAPI urlInvocazioneApi = getUrlInvocazioneObject(protocolFactory, state, requestInfo,
				rest, aspc, ruoloContesto, nomePorta, proprietarioPorta);
		String prefixGatewayUrl = urlInvocazioneApi.getBaseUrl();
		String contesto = urlInvocazioneApi.getContext();
		return Utilities.buildUrl(prefixGatewayUrl, contesto);
	}
	
}
