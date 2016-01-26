/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

package org.openspcoop2.protocol.trasparente.builder;

import java.util.Date;

import javax.xml.soap.SOAPElement;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.basic.builder.BustaBuilder;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.trasparente.config.TrasparenteProperties;

/**
 * Classe che implementa, in base al protocollo Trasparente, l'interfaccia {@link org.openspcoop2.protocol.sdk.builder.IBustaBuilder} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TrasparenteBustaBuilder extends BustaBuilder {

	private TrasparenteProperties trasparenteProperties;
	public TrasparenteBustaBuilder(IProtocolFactory factory) throws ProtocolException {
		super(factory);
		this.trasparenteProperties = TrasparenteProperties.getInstance(factory.getLogger());
	}

	@Override
	public SOAPElement imbustamento(IState state, OpenSPCoop2Message msg, Busta busta,
			boolean isRichiesta,
			ProprietaManifestAttachments proprietaManifestAttachments)
			throws ProtocolException {

		// Aggiunto in richiesta tramite urlMapping, ed in risposta tramite validazioneRisposta
//		// aggiunto lista trasmissione
//		if(busta.sizeListaTrasmissioni()<=0){
//			Trasmissione trasmissione = new Trasmissione();
//			
//			trasmissione.setTipoOrigine(busta.getTipoMittente());
//			trasmissione.setOrigine(busta.getMittente());
//			trasmissione.setIdentificativoPortaOrigine(busta.getIdentificativoPortaMittente());
//			trasmissione.setIndirizzoOrigine(busta.getIndirizzoMittente());
//			
//			trasmissione.setTipoDestinazione(busta.getTipoDestinatario());
//			trasmissione.setDestinazione(busta.getDestinatario());
//			trasmissione.setIdentificativoPortaDestinazione(busta.getIdentificativoPortaDestinatario());
//			trasmissione.setIndirizzoDestinazione(busta.getIndirizzoDestinatario());
//			
//			trasmissione.setOraRegistrazione(busta.getOraRegistrazione());
//			trasmissione.setTempo(busta.getTipoOraRegistrazione(), busta.getTipoOraRegistrazioneValue());
//			
//			busta.addTrasmissione(trasmissione);
//		}
		
		super.imbustamento(state, msg, busta, isRichiesta, proprietaManifestAttachments);
				
		if(!isRichiesta && busta.sizeListaEccezioni()>0 ){
		
			boolean ignoraEccezioniNonGravi = this.protocolFactory.createProtocolManager().isIgnoraEccezioniNonGravi();
			if(ignoraEccezioniNonGravi){
				if(busta.containsEccezioniGravi() ){
					this.addEccezioniInFault(msg, busta, ignoraEccezioniNonGravi);
				}	
			}
			else{
				this.addEccezioniInFault(msg, busta, ignoraEccezioniNonGravi);
			}

		}
		
		return null;
	}
	
	
	@Override
	public String newID(IState state, IDSoggetto idSoggetto, String idTransazione, Boolean isRichiesta) throws ProtocolException {
		return newID(state, idSoggetto, idTransazione, isRichiesta, this.trasparenteProperties.generateIDasUUID());
	}
	
	
	@Override
	public Date extractDateFromID(String id) throws ProtocolException {
		return extractDateFromID(id, this.trasparenteProperties.generateIDasUUID());
		
	}
	
}
