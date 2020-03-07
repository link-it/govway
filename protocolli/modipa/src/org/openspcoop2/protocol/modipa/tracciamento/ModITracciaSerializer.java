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

import org.openspcoop2.protocol.basic.tracciamento.TracciaSerializer;
import org.openspcoop2.protocol.modipa.constants.ModIConsoleCostanti;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.tracciamento.TracciaExtInfoDefinition;

/**
 * ModITracciaSerializer
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModITracciaSerializer extends TracciaSerializer {

	public ModITracciaSerializer(IProtocolFactory<?> factory) throws ProtocolException {
		super(factory);
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
}
