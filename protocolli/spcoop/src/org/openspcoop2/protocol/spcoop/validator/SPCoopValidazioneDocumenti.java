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
package org.openspcoop2.protocol.spcoop.validator;

import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.protocol.basic.validator.ValidazioneDocumenti;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.validator.ValidazioneResult;

/**
 * SPCoopValidazioneDocumenti
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopValidazioneDocumenti extends ValidazioneDocumenti  {

	public SPCoopValidazioneDocumenti(IProtocolFactory factory) {
		super(factory);
	}

	
	@Override
	public ValidazioneResult validaSpecificaConversazione(
			AccordoServizioParteComune accordoServizioParteComune) {
		
		
		String objectInEsame = null;
		ValidazioneResult result = new ValidazioneResult();
		result.setEsito(false);
		
		
		// WSBL Concettuale
		try{
			objectInEsame = "[InterfacciaWSBL Concettuale] ";

			byte[]wsblConcettuale = null;
			if(accordoServizioParteComune.getByteSpecificaConversazioneConcettuale()!=null){
				wsblConcettuale = accordoServizioParteComune.getByteSpecificaConversazioneConcettuale();
			}
			else if(accordoServizioParteComune.getSpecificaConversazioneConcettuale()!=null){
				wsblConcettuale = this.readDocumento(accordoServizioParteComune.getSpecificaConversazioneConcettuale());		
			}
			if(wsblConcettuale!=null){
				// Verifico che sia un documento xml valido
				this.xmlUtils.newDocument(wsblConcettuale);

				if(it.gov.spcoop.sica.wsbl.driver.XMLUtils.isMessageBehavior(wsblConcettuale)){
					throw new Exception("La specifica di conversazione concettuale non e' un documento WSBL ConceptualBehavior, ma erroneamente un documento WSBL MessageBehavior");
				}
				it.gov.spcoop.sica.wsbl.driver.XMLUtils.getConceptualBehavior(this.log,wsblConcettuale);	
			}	
		}catch(Exception e){
			result.setMessaggioErrore(objectInEsame+" Documento non valido: "+e.getMessage());
			result.setException(e);
			return result;
		}
		
		
		
		// WSBL Erogatore
		try{
			objectInEsame = "[InterfacciaWSBL Erogatore] ";

			byte[]wsblErogatore = null;
			if(accordoServizioParteComune.getByteSpecificaConversazioneErogatore()!=null){
				wsblErogatore = accordoServizioParteComune.getByteSpecificaConversazioneErogatore();
			}
			else if(accordoServizioParteComune.getSpecificaConversazioneErogatore()!=null){
				wsblErogatore = this.readDocumento(accordoServizioParteComune.getSpecificaConversazioneErogatore());		
			}
			if(wsblErogatore!=null){
				// Verifico che sia un documento xml valido
				this.xmlUtils.newDocument(wsblErogatore);

				if(it.gov.spcoop.sica.wsbl.driver.XMLUtils.isConceptualBehavior(wsblErogatore)){
					throw new Exception("La specifica di conversazione logica erogatore non e' un documento WSBL MessageBehavior, ma erroneamente un documento WSBL ConceptualBehavior");
				}
				it.gov.spcoop.sica.wsbl.driver.XMLUtils.getMessageBehavior(this.log,wsblErogatore);
			}	
		}catch(Exception e){
			result.setMessaggioErrore(objectInEsame+" Documento non valido: "+e.getMessage());
			result.setException(e);
			return result;
		}
		
		
		
		
		// WSBL Fruitore
		try{
			objectInEsame = "[InterfacciaWSBL Fruitore] ";

			byte[]wsblFruitore = null;
			if(accordoServizioParteComune.getByteSpecificaConversazioneFruitore()!=null){
				wsblFruitore = accordoServizioParteComune.getByteSpecificaConversazioneFruitore();
			}
			else if(accordoServizioParteComune.getSpecificaConversazioneFruitore()!=null){
				wsblFruitore = this.readDocumento(accordoServizioParteComune.getSpecificaConversazioneFruitore());		
			}
			if(wsblFruitore!=null){
				// Verifico che sia un documento xml valido
				this.xmlUtils.newDocument(wsblFruitore);

				if(it.gov.spcoop.sica.wsbl.driver.XMLUtils.isConceptualBehavior(wsblFruitore)){
					throw new Exception("La specifica di conversazione logica fruitore non e' un documento WSBL MessageBehavior, ma erroneamente un documento WSBL ConceptualBehavior");
				}						
				it.gov.spcoop.sica.wsbl.driver.XMLUtils.getMessageBehavior(this.log,wsblFruitore);
			}	
		}catch(Exception e){
			result.setMessaggioErrore(objectInEsame+" Documento non valido: "+e.getMessage());
			result.setException(e);
			return result;
		}
	
		
		
		
		// result
		result.setEsito(true);
		return result;
	}
}
