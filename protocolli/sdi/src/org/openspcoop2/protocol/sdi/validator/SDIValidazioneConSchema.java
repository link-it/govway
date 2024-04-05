/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.sdi.validator;

import java.util.List;

import jakarta.xml.soap.SOAPElement;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.soap.mtom.MtomXomReference;
import org.openspcoop2.protocol.basic.validator.ValidazioneConSchema;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRiceviFile;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRiceviNotifica;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRicezioneFatture;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioTrasmissioneFatture;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ContestoCodificaEccezione;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.utils.xml.AbstractValidatoreXSD;

/**
 * Classe che implementa, in base al protocollo SdI, l'interfaccia {@link org.openspcoop2.protocol.sdk.validator.IValidazioneConSchema}

 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDIValidazioneConSchema extends ValidazioneConSchema {

	/** Errori di validazione riscontrati sulla busta */
	private java.util.List<Eccezione> erroriValidazione;
	/** Errori di processamento riscontrati sulla busta */
	private java.util.List<Eccezione> erroriProcessamento;

	public SDIValidazioneConSchema(IProtocolFactory<?> factory, IState state) throws ProtocolException {
		super(factory, state);
	}



	@Override
	public boolean initialize(OpenSPCoop2MessageFactory messageFactory) {
		return true;
	}

	@Override
	public List<Eccezione> getEccezioniValidazione() {
		return this.erroriValidazione;
	}

	@Override
	public List<Eccezione> getEccezioniProcessamento() {
		return this.erroriProcessamento;
	}

	@Override
	public void valida(OpenSPCoop2Message messageParam,
			boolean isErroreProcessamento,
			boolean isErroreIntestazione,
			boolean isMessaggioConAttachments, boolean validazioneManifestAttachments) throws ProtocolException {

		this.erroriValidazione = new java.util.ArrayList<Eccezione>();
		this.erroriProcessamento = new java.util.ArrayList<Eccezione>();

		List<MtomXomReference> references = null;
		
		try{
			OpenSPCoop2SoapMessage message = messageParam.castAsSoap();
			
			references = message.mtomFastUnpackagingForXSDConformance();
			
			SOAPElement child = SoapUtils.getNotEmptyFirstChildSOAPElement(message.getSOAPBody());
			String namespace = child.getNamespaceURI();

			if(SDICostantiServizioRiceviFile.SDI_SERVIZIO_RICEVI_FILE_NAMESPACE.equals(namespace) ||
					SDICostantiServizioTrasmissioneFatture.TRASMISSIONE_SERVIZIO_TRASMISSIONE_FATTURE_NAMESPACE.equals(namespace)){
				AbstractValidatoreXSD validator = null;
				if(SDICostantiServizioRiceviFile.RICEVI_FILE_RICHIESTA_ROOT_ELEMENT.equals(child.getLocalName())){
					validator = it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.utils.SdIXSDValidator.getXSDValidatorTrasmissione_1_0(org.openspcoop2.message.xml.MessageXMLUtils.class,this.log);
				}else{
					validator = it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.utils.SdIXSDValidator.getXSDValidatorTrasmissione_1_1(org.openspcoop2.message.xml.MessageXMLUtils.class,this.log);
				}
				try{
					validator.valida(child);
				}catch(Exception e){
					this.log.error("Validazione con schema xsd fallita",e);
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(e.getMessage());
					this.erroriValidazione.add(ecc);
				}
			}
			else if(SDICostantiServizioRiceviNotifica.SDI_SERVIZIO_RICEVI_NOTIFICA_NAMESPACE.equals(namespace) ||
					SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE_NAMESPACE.equals(namespace)){
				AbstractValidatoreXSD validator = it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.utils.SdIXSDValidator.getXSDValidator(org.openspcoop2.message.xml.MessageXMLUtils.class,this.log);
				try{
					validator.valida(child);
				}catch(Exception e){
					this.log.error("Validazione con schema xsd fallita",e);
					Eccezione ecc = new Eccezione();
					ecc.setContestoCodifica(ContestoCodificaEccezione.INTESTAZIONE);
					ecc.setCodiceEccezione(CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO);
					ecc.setRilevanza(LivelloRilevanza.ERROR);
					ecc.setDescrizione(e.getMessage());
					this.erroriValidazione.add(ecc);
				}
			}
		}catch(Exception e){
			this.log.error("Validazione con schema xsd non riuscita non riuscita",e);
			Eccezione ecc = new Eccezione();
			ecc.setContestoCodifica(ContestoCodificaEccezione.PROCESSAMENTO);
			ecc.setCodiceEccezione(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
			ecc.setRilevanza(LivelloRilevanza.ERROR);
			ecc.setDescrizione("Validazione con schema xsd non riuscita: errore di processamento");
			this.erroriProcessamento.add(ecc);
		}
		finally{
			try{
				OpenSPCoop2SoapMessage message = messageParam.castAsSoap();
				message.mtomRestoreAfterXSDConformance(references);
			}catch(Exception e){
				this.log.error("Validazione con schema xsd non riuscita non riuscita",e);
				Eccezione ecc = new Eccezione();
				ecc.setContestoCodifica(ContestoCodificaEccezione.PROCESSAMENTO);
				ecc.setCodiceEccezione(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
				ecc.setRilevanza(LivelloRilevanza.ERROR);
				ecc.setDescrizione("Validazione con schema xsd non riuscita: errore di processamento");
				this.erroriProcessamento.add(ecc);
			}
		}

		return;
	}

}
