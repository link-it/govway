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



package org.openspcoop2.core.mapping;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IdentificativiErogazione;

/**
 * ImplementationUtils
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ImplementationUtils {

	public static IDPortaApplicativa setCommonParameter(PortaApplicativa portaApplicativa,
			IDServizio idServizio, boolean setDatiServizio, boolean portaClonata) {
	
		portaApplicativa.setTipoSoggettoProprietario(idServizio.getSoggettoErogatore().getTipo());
		portaApplicativa.setNomeSoggettoProprietario(idServizio.getSoggettoErogatore().getNome());
		
		if(setDatiServizio) {
			PortaApplicativaServizio pdServizio = new PortaApplicativaServizio();
			pdServizio.setTipo(idServizio.getTipo());
			pdServizio.setNome(idServizio.getNome());
			pdServizio.setVersione(idServizio.getVersione());
			portaApplicativa.setServizio(pdServizio);
		}
		
		portaApplicativa.setStato(StatoFunzionalita.ABILITATO);
		
		if(!portaClonata) {
			portaApplicativa.setRicevutaAsincronaAsimmetrica(StatoFunzionalita.ABILITATO);
			portaApplicativa.setRicevutaAsincronaSimmetrica(StatoFunzionalita.ABILITATO);
			
			portaApplicativa.setAllegaBody(StatoFunzionalita.DISABILITATO);
			portaApplicativa.setScartaBody(StatoFunzionalita.DISABILITATO);
			
			portaApplicativa.setStatoMessageSecurity(StatoFunzionalita.DISABILITATO.toString());
		}
		
		IDPortaApplicativa idPortaApplicativa = new IDPortaApplicativa();
		idPortaApplicativa.setNome(portaApplicativa.getNome());
		IdentificativiErogazione identificativiErogazione = new IdentificativiErogazione();
		identificativiErogazione.setIdServizio(idServizio);
		idPortaApplicativa.setIdentificativiErogazione(identificativiErogazione);
		
		return idPortaApplicativa;
	}
	
	public static void setAzioneDelegate(PortaApplicativa portaApplicativa, String nomePortaDelegante, String ... azione) {
		PortaApplicativaAzione pda = new PortaApplicativaAzione();
		pda.setIdentificazione(PortaApplicativaAzioneIdentificazione.DELEGATED_BY); 
		pda.setNomePortaDelegante(nomePortaDelegante);
		for (int i = 0; i < azione.length; i++) {
			pda.addAzioneDelegata(azione[i]); 
		}
		portaApplicativa.setAzione(pda);
		
		portaApplicativa.setRicercaPortaAzioneDelegata(StatoFunzionalita.DISABILITATO);
	}
	
	public static MappingErogazionePortaApplicativa createMappingDefault(IDServizio idServizio,IDPortaApplicativa idPortaApplicativa) {
		MappingErogazionePortaApplicativa mappingErogazione = _creteMapping(idServizio, idPortaApplicativa);
		mappingErogazione.setDefault(true);
		mappingErogazione.setNome(getDefaultMappingName());
		return mappingErogazione;
	}
	public static MappingErogazionePortaApplicativa createMapping(IDServizio idServizio,IDPortaApplicativa idPortaApplicativa, String ruleName) {
		MappingErogazionePortaApplicativa mappingErogazione = _creteMapping(idServizio, idPortaApplicativa);
		mappingErogazione.setDefault(false);
		mappingErogazione.setNome(ruleName);
		return mappingErogazione;
	}
	private static MappingErogazionePortaApplicativa _creteMapping(IDServizio idServizio,IDPortaApplicativa idPortaApplicativa) {
		MappingErogazionePortaApplicativa mappingErogazione = new MappingErogazionePortaApplicativa();
		mappingErogazione.setIdServizio(idServizio);
		mappingErogazione.setIdPortaApplicativa(idPortaApplicativa);
		return mappingErogazione;
	}
	
	public static boolean isPortaApplicativaUtilizzabileComeDefault(PortaApplicativa pa) {
		if( (pa.getAzione()==null) || 
				( 
						!PortaApplicativaAzioneIdentificazione.DELEGATED_BY.equals(pa.getAzione().getIdentificazione()) 
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
		return Costanti.MAPPING_EROGAZIONE_PA_NOME_DEFAULT;
	}
}





