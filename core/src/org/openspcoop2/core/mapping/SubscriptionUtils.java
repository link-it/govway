/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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



package org.openspcoop2.core.mapping;

import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.PortaDelegataServizio;
import org.openspcoop2.core.config.PortaDelegataSoggettoErogatore;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.id.IdentificativiFruizione;

/**
 * SubscriptionUtils
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class SubscriptionUtils {

	public static IDPortaDelegata setCommonParameter(PortaDelegata portaDelegata,
			IDSoggetto idFruitore, IDServizio idServizio, boolean setDatiServizio, boolean portaClonata) {
		
		portaDelegata.setTipoSoggettoProprietario(idFruitore.getTipo());
		portaDelegata.setNomeSoggettoProprietario(idFruitore.getNome());
		
		if(setDatiServizio) {
			PortaDelegataSoggettoErogatore pdSogg = new PortaDelegataSoggettoErogatore();
			pdSogg.setTipo(idServizio.getSoggettoErogatore().getTipo());
			pdSogg.setNome(idServizio.getSoggettoErogatore().getNome());
			portaDelegata.setSoggettoErogatore(pdSogg);
	
			PortaDelegataServizio pdServizio = new PortaDelegataServizio();
			pdServizio.setTipo(idServizio.getTipo());
			pdServizio.setNome(idServizio.getNome());
			pdServizio.setVersione(idServizio.getVersione());
			portaDelegata.setServizio(pdServizio);
		}
		
		portaDelegata.setStato(StatoFunzionalita.ABILITATO);
		
		if(!portaClonata) {
			portaDelegata.setRicevutaAsincronaAsimmetrica(StatoFunzionalita.ABILITATO);
			portaDelegata.setRicevutaAsincronaSimmetrica(StatoFunzionalita.ABILITATO);
			
			portaDelegata.setAllegaBody(StatoFunzionalita.DISABILITATO);
			portaDelegata.setScartaBody(StatoFunzionalita.DISABILITATO);
			
			portaDelegata.setStatoMessageSecurity(StatoFunzionalita.DISABILITATO.toString());
		}
		
		IDPortaDelegata idPortaDelegata = new IDPortaDelegata();
		idPortaDelegata.setNome(portaDelegata.getNome());
		IdentificativiFruizione identificativiFruizione = new IdentificativiFruizione();
		identificativiFruizione.setIdServizio(idServizio);
		identificativiFruizione.setSoggettoFruitore(idFruitore);
		idPortaDelegata.setIdentificativiFruizione(identificativiFruizione);
		
		return idPortaDelegata;
	}
	
	public static void setAzioneDelegate(PortaDelegata portaDelegata, String nomePortaDelegante, String ... azione) {
		PortaDelegataAzione pda = new PortaDelegataAzione();
		pda.setIdentificazione(PortaDelegataAzioneIdentificazione.DELEGATED_BY); 
		pda.setNomePortaDelegante(nomePortaDelegante);
		for (int i = 0; i < azione.length; i++) {
			pda.addAzioneDelegata(azione[i]); 
		}
		portaDelegata.setAzione(pda);
		
		portaDelegata.setRicercaPortaAzioneDelegata(StatoFunzionalita.DISABILITATO);
	}
	
	public static MappingFruizionePortaDelegata createMappingDefault(IDSoggetto idFruitore, IDServizio idServizio,IDPortaDelegata idPortaDelegata) {
		MappingFruizionePortaDelegata mappingFruizione = _creteMapping(idFruitore, idServizio, idPortaDelegata);
		mappingFruizione.setDefault(true);
		mappingFruizione.setNome(getDefaultMappingName());
		mappingFruizione.setDescrizione(getDefaultMappingDescription());
		return mappingFruizione;
	}
	public static MappingFruizionePortaDelegata createMapping(IDSoggetto idFruitore, IDServizio idServizio,IDPortaDelegata idPortaDelegata, String ruleName, String description) {
		MappingFruizionePortaDelegata mappingFruizione = _creteMapping(idFruitore, idServizio, idPortaDelegata);
		mappingFruizione.setDefault(false);
		mappingFruizione.setNome(ruleName);
		mappingFruizione.setDescrizione(description);
		return mappingFruizione;
	}
	private static MappingFruizionePortaDelegata _creteMapping(IDSoggetto idFruitore, IDServizio idServizio,IDPortaDelegata idPortaDelegata) {
		MappingFruizionePortaDelegata mappingFruizione = new MappingFruizionePortaDelegata();
		mappingFruizione.setIdFruitore(idFruitore);
		mappingFruizione.setIdServizio(idServizio);
		mappingFruizione.setIdPortaDelegata(idPortaDelegata);
		return mappingFruizione;
	}
	
	public static boolean isPortaDelegataUtilizzabileComeDefault(PortaDelegata pa) {
		if( (pa.getAzione()==null) || 
				( 
						!PortaDelegataAzioneIdentificazione.DELEGATED_BY.equals(pa.getAzione().getIdentificazione()) 
						&&
						(pa.getAzione().getNome()==null || "".equals(pa.getAzione().getNome()))
						&&
						(pa.getRicercaPortaAzioneDelegata()==null || StatoFunzionalita.ABILITATO.equals(pa.getRicercaPortaAzioneDelegata()))
				)
			){
			return true;
		}
		return false;
	}
	public static String getDefaultMappingName() {
		return Costanti.MAPPING_FRUIZIONE_PD_NOME_DEFAULT;
	}
	public static String getDefaultMappingDescription() {
		return Costanti.MAPPING_FRUIZIONE_PD_DESCRIZIONE_DEFAULT;
	}
	
}





