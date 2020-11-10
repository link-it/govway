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

package org.openspcoop2.protocol.modipa.tracciamento;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.tracciamento.Proprieta;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.basic.tracciamento.TracciaSerializer;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.utils.ModIPropertiesUtils;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.tracciamento.TracciaExtInfo;
import org.openspcoop2.protocol.sdk.tracciamento.TracciaExtInfoDefinition;

/**
 * ModITracciaSerializer
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModITracciaSerializer extends TracciaSerializer {

	private ModIProperties modiProperties = null;
	
	public ModITracciaSerializer(IProtocolFactory<?> factory) throws ProtocolException {
		super(factory);
		this.modiProperties = ModIProperties.getInstance();
	}

	@Override
	public List<TracciaExtInfoDefinition> getExtInfoDefinition(){
		List<TracciaExtInfoDefinition> list = new ArrayList<TracciaExtInfoDefinition>();
		
		TracciaExtInfoDefinition interazioneAsincrona = new TracciaExtInfoDefinition();
		interazioneAsincrona.setPrefixId(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE_ASINCRONA_PREFIX);
		interazioneAsincrona.setLabel(ModIConsoleCostanti.MODIPA_API_TRACCIA_EXT_INFO_PROFILO_INTERAZIONE_NON_BLOCCANTE_LABEL);
		list.add(interazioneAsincrona);
		
		TracciaExtInfoDefinition sicurezzaMessaggio = new TracciaExtInfoDefinition();
		sicurezzaMessaggio.setPrefixId(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_PREFIX);
		sicurezzaMessaggio.setLabel(ModIConsoleCostanti.MODIPA_API_TRACCIA_EXT_INFO_PROFILO_SICUREZZA_MESSAGGIO_LABEL);
		list.add(sicurezzaMessaggio);
		
		TracciaExtInfoDefinition sicurezzaMessaggioSignedHeaders = new TracciaExtInfoDefinition();
		sicurezzaMessaggioSignedHeaders.setPrefixId(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO_SIGNED_HEADER_PREFIX);
		sicurezzaMessaggioSignedHeaders.setLabel(ModIConsoleCostanti.MODIPA_API_TRACCIA_EXT_INFO_PROFILO_SICUREZZA_MESSAGGIO_SIGNED_HEADERS_LABEL);
		list.add(sicurezzaMessaggioSignedHeaders);
		
		return list;
	}
	@Override
	public List<TracciaExtInfo> extractExtInfo(Busta busta, ServiceBinding tipoApi){
		
		boolean terminologiaBozza = this.modiProperties.isModIVersioneBozza();
		
		List<TracciaExtInfo> list = super.extractExtInfo(busta, tipoApi);
		if(list!=null && !list.isEmpty()) {
			for (TracciaExtInfo tracciaExtInfo : list) {
				List<Proprieta> proprieta = tracciaExtInfo.getProprieta();
				if(proprieta!=null && !proprieta.isEmpty()) {
					for (Proprieta p : proprieta) {
						//System.out.println("["+p.getNome()+"]=["+p.getValore()+"]");
						if(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_INTERAZIONE.equals(p.getNome())) {
							if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_CRUD.equalsIgnoreCase(p.getValore())) {
								p.setValore(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_LABEL_CRUD);
							}
							else if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_BLOCCANTE.equalsIgnoreCase(p.getValore())) {
								p.setValore(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_LABEL_BLOCCANTE);
							}
							else if(ModICostanti.MODIPA_PROFILO_INTERAZIONE_VALUE_NON_BLOCCANTE.equalsIgnoreCase(p.getValore())) {
								p.setValore(ModIConsoleCostanti.MODIPA_PROFILO_INTERAZIONE_LABEL_NON_BLOCCANTE);
							}
						}
						else if(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_CANALE.equals(p.getNome())) {
							if(ModICostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC01.equalsIgnoreCase(p.getValore())) {
								p.setValore(terminologiaBozza ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC01_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC01_NEW);
							}
							else if(ModICostanti.MODIPA_PROFILO_SICUREZZA_CANALE_VALUE_IDAC02.equalsIgnoreCase(p.getValore())) {
								p.setValore(terminologiaBozza ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC02_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_CANALE_LABEL_IDAC02_NEW);
							}
						}
						else if(ModICostanti.MODIPA_BUSTA_EXT_PROFILO_SICUREZZA_MESSAGGIO.equals(p.getNome())) {
							if(tipoApi!=null && ServiceBinding.REST.equals(tipoApi)) {
								if(ModIPropertiesUtils.convertProfiloSicurezzaToSDKValue(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01,true).equalsIgnoreCase(p.getValore())) {
									p.setValore(terminologiaBozza ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_REST_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_REST_NEW);
								}
								else if(ModIPropertiesUtils.convertProfiloSicurezzaToSDKValue(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02,true).equalsIgnoreCase(p.getValore())) {
									p.setValore(terminologiaBozza ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_REST_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_REST_NEW);
								}
								else if(ModIPropertiesUtils.convertProfiloSicurezzaToSDKValue(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301,true).equalsIgnoreCase(p.getValore())) {
									p.setValore(terminologiaBozza ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_REST_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_REST_NEW);
								}
								else if(ModIPropertiesUtils.convertProfiloSicurezzaToSDKValue(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302,true).equalsIgnoreCase(p.getValore())) {
									p.setValore(terminologiaBozza ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_REST_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_REST_NEW);
								}
							}
							else if(tipoApi!=null && ServiceBinding.SOAP.equals(tipoApi)) {
								if(ModIPropertiesUtils.convertProfiloSicurezzaToSDKValue(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM01,false).equalsIgnoreCase(p.getValore())) {
									p.setValore(terminologiaBozza ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_SOAP_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM01_SOAP_NEW);
								}
								else if(ModIPropertiesUtils.convertProfiloSicurezzaToSDKValue(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM02,false).equalsIgnoreCase(p.getValore())) {
									p.setValore(terminologiaBozza ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_SOAP_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM02_SOAP_NEW);
								}
								else if(ModIPropertiesUtils.convertProfiloSicurezzaToSDKValue(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0301,false).equalsIgnoreCase(p.getValore())) {
									p.setValore(terminologiaBozza ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_SOAP_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0301_SOAP_NEW);
								}
								else if(ModIPropertiesUtils.convertProfiloSicurezzaToSDKValue(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_IDAM0302,false).equalsIgnoreCase(p.getValore())) {
									p.setValore(terminologiaBozza ? ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_SOAP_OLD : ModIConsoleCostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_LABEL_IDAM0302_SOAP_NEW);
								}
							}
						}
					}
				}
			}
		}
		return list;
	}
}
