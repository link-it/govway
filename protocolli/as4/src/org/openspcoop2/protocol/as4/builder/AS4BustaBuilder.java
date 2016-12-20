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

package org.openspcoop2.protocol.as4.builder;

import java.util.Date;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.basic.BasicEmptyRawContent;
import org.openspcoop2.protocol.basic.builder.BustaBuilder;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.as4.config.AS4Properties;

/**
 * Classe che implementa, in base al protocollo AS4, l'interfaccia {@link org.openspcoop2.protocol.sdk.builder.IBustaBuilder} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12499 $, $Date: 2016-12-16 12:25:57 +0100 (Fri, 16 Dec 2016) $
 */
public class AS4BustaBuilder extends BustaBuilder<BasicEmptyRawContent> {


	private AS4Properties as4Properties;
	public AS4BustaBuilder(IProtocolFactory<?> factory) throws ProtocolException {
		super(factory);
		this.as4Properties = AS4Properties.getInstance(factory.getLogger());
	}

	@Override
	public ProtocolMessage imbustamento(IState state, OpenSPCoop2Message msg, Busta busta,
			RuoloMessaggio ruoloMessaggio,
			ProprietaManifestAttachments proprietaManifestAttachments)
			throws ProtocolException {
		
		ProtocolMessage protocolMessage = super.imbustamento(state, msg, busta, ruoloMessaggio, proprietaManifestAttachments);
				
		if(RuoloMessaggio.RISPOSTA.equals(ruoloMessaggio) && busta.sizeListaEccezioni()>0 ){
		
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
			
		return protocolMessage;
	}
	
	
	@Override
	public String newID(IState state, IDSoggetto idSoggetto, String idTransazione, RuoloMessaggio ruoloMessaggio) throws ProtocolException {
		return super.newID(state, idSoggetto, idTransazione, ruoloMessaggio, this.as4Properties.generateIDasUUID());
	}
	
	
	@Override
	public Date extractDateFromID(String id) throws ProtocolException {
		return extractDateFromID(id, this.as4Properties.generateIDasUUID());
		
	}
	
}
