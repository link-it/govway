/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.trasparente.builder;

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
import org.openspcoop2.protocol.trasparente.config.TrasparenteProperties;

/**
 * Classe che implementa, in base al protocollo Trasparente, l'interfaccia {@link org.openspcoop2.protocol.sdk.builder.IBustaBuilder} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TrasparenteBustaBuilder extends BustaBuilder<BasicEmptyRawContent> {


	private TrasparenteProperties trasparenteProperties;
	public TrasparenteBustaBuilder(IProtocolFactory<?> factory,IState state) throws ProtocolException {
		super(factory,state);
		this.trasparenteProperties = TrasparenteProperties.getInstance(factory.getLogger());
	}

	@Override
	public ProtocolMessage imbustamento(OpenSPCoop2Message msg, Busta busta,
			RuoloMessaggio ruoloMessaggio,
			ProprietaManifestAttachments proprietaManifestAttachments)
			throws ProtocolException {
		
		ProtocolMessage protocolMessage = super.imbustamento(msg, busta, ruoloMessaggio, proprietaManifestAttachments);
				
		if(RuoloMessaggio.RISPOSTA.equals(ruoloMessaggio) && busta.sizeListaEccezioni()>0 ){
			// le eccezioni vengono tornate anche per gli errori di processamento poiche' in TrasparenteProtocolVersionManager
			// e' stato cablato il metodo isGenerazioneListaEccezioniErroreProcessamento al valore 'true'
			
			// Per quanto riguarda la generazione dei codici SOAPFault personalizzati e/o la generazione dell'elemento errore-applicativo
			// la scelta e' delegata a due proprieta' nel file di proprieta'.
			//
			// Infine la scelta della presenza o meno dell'elemento OpenSPCoop2Details lo stesso viene pilotata dalle proprieta' presenti nel file di proprieta'
		
			boolean ignoraEccezioniNonGravi = this.protocolFactory.createProtocolManager().isIgnoraEccezioniNonGravi();
			if(ignoraEccezioniNonGravi){
				if(busta.containsEccezioniGravi() ){
					this.enrichFault(msg, busta, ignoraEccezioniNonGravi,
							this.trasparenteProperties.isPortaApplicativaBustaErrore_personalizzaElementiFault(),
							this.trasparenteProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo());
				}	
			}
			else{
				this.enrichFault(msg, busta, ignoraEccezioniNonGravi,
						this.trasparenteProperties.isPortaApplicativaBustaErrore_personalizzaElementiFault(),
						this.trasparenteProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo());
			}

		}
			
		return protocolMessage;
	}
	
	
	@Override
	public String newID(IDSoggetto idSoggetto, String idTransazione, RuoloMessaggio ruoloMessaggio) throws ProtocolException {
		return super.newID( idSoggetto, idTransazione, ruoloMessaggio, this.trasparenteProperties.generateIDasUUID());
	}
	
	
	@Override
	public Date extractDateFromID(String id) throws ProtocolException {
		return extractDateFromID(id, this.trasparenteProperties.generateIDasUUID());
		
	}
	
}
