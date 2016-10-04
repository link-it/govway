/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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



package org.openspcoop2.protocol.trasparente.testsuite.core;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;


/**
 * Test sui profili di collaborazione implementati nella Porta di Dominio
 * utilizzabili con messaggi Soap normali o con attachments
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CooperazioneTrasparenteBase {

	public static CooperazioneBaseInformazioni getCooperazioneBaseInformazioni(){
		CooperazioneBaseInformazioni info = new CooperazioneBaseInformazioni();
		
		info.setServizio_versioneDefault(CostantiTestSuite.PROXY_VERSIONE_SERVIZIO_DEFAULT);
		
		info.setProfiloCollaborazione_protocollo_oneway(CostantiTestSuite.PROXY_PROFILO_COLLABORAZIONE_ONEWAY);
		info.setProfiloCollaborazione_protocollo_sincrono(CostantiTestSuite.PROXY_PROFILO_COLLABORAZIONE_SINCRONO);
		
		return info;
	}
	
	public static CooperazioneBaseInformazioni getCooperazioneBaseInformazioni(IDSoggetto mittente, IDSoggetto destinatario,
			boolean confermaRicezione, String inoltro, Inoltro inoltroSdk){
		CooperazioneBaseInformazioni info = getCooperazioneBaseInformazioni();
		
		info.setMittente(mittente);
		info.setDestinatario(destinatario);
		info.setConfermaRicezione(confermaRicezione);
		info.setInoltro(inoltro);
		info.setInoltroSdk(inoltroSdk);
		
		return info;
	}
	
}
