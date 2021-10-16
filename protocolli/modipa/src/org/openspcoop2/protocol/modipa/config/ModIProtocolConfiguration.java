/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.modipa.config;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.basic.config.BasicConfiguration;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;

/**
 * Classe che implementa, in base al protocollo ModI, l'interfaccia {@link org.openspcoop2.protocol.sdk.config.IProtocolConfiguration} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIProtocolConfiguration extends BasicConfiguration {

	private ModIProperties properties;
	
	public ModIProtocolConfiguration(IProtocolFactory<?> factory) throws ProtocolException {
		super(factory);
		this.properties = ModIProperties.getInstance();
	}
	
	@Override
	public boolean isIntegrationInfoRequired(TipoPdD tipoPdD, ServiceBinding serviceBinding, FunzionalitaProtocollo funzionalitaProtocollo) throws ProtocolException{
		
		if (FunzionalitaProtocollo.RIFERIMENTO_ID_RICHIESTA.equals(funzionalitaProtocollo)){
			if(TipoPdD.DELEGATA.equals(tipoPdD)) {
				return this.properties.isRiferimentoIDRichiesta_PD_Required();
			}
			else {
				return this.properties.isRiferimentoIDRichiesta_PA_Required();
			}
		}
		else {
			return super.isIntegrationInfoRequired(tipoPdD, serviceBinding, funzionalitaProtocollo);
		}
		
	}
	
	@Override
	public boolean isAbilitataGenerazioneTracce() {
		if(this.properties.isGenerazioneTracce()) {
			return true;
		}
		return super.isAbilitataGenerazioneTracce();
	}
	
	@Override
	public boolean isAbilitatoSalvataggioHeaderProtocolloTracce() {
		return this.properties.isGenerazioneTracce_registraToken();
	}
	
	@Override
	public boolean isSupportato(ServiceBinding serviceBinding, FunzionalitaProtocollo funzionalitaProtocollo)
			throws ProtocolException {
		if(funzionalitaProtocollo==null || serviceBinding==null){
			throw new ProtocolException("Params not defined");
		}
		if(FunzionalitaProtocollo.FILTRO_DUPLICATI.equals(funzionalitaProtocollo)) {
			return true;
		}
		else {
			return super.isSupportato(serviceBinding, funzionalitaProtocollo);
		}
	}
	
	@Override
	public boolean isDataPresenteInIdentificativoMessaggio() {
		return !this.properties.generateIDasUUID();
	}
	
}
