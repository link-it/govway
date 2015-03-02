/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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



package org.openspcoop2.protocol.basic.tracciamento;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;

import org.apache.log4j.Logger;
import org.openspcoop2.core.tracciamento.Eccezione;
import org.openspcoop2.core.tracciamento.Riscontro;
import org.openspcoop2.core.tracciamento.Trasmissione;
import org.openspcoop2.core.tracciamento.constants.TipoCodificaEccezione;
import org.openspcoop2.core.tracciamento.constants.TipoRilevanzaEccezione;
import org.openspcoop2.core.tracciamento.constants.TipoTempo;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.protocol.basic.Utilities;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.SubCodiceErrore;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.w3c.dom.Element;

/**
 * XMLTracciaBuilder
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XMLTracciaBuilder implements org.openspcoop2.protocol.sdk.tracciamento.IXMLTracciaBuilder {
	
	protected Logger log;
	protected IProtocolFactory factory;
	protected OpenSPCoop2MessageFactory fac = null;
	protected AbstractXMLUtils xmlUtils;
	protected ITraduttore protocolTraduttore = null;
	
	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.factory;
	}
	
	public XMLTracciaBuilder(IProtocolFactory factory) throws ProtocolException{
		this.log = factory.getLogger();
		this.factory = factory;
		this.fac = OpenSPCoop2MessageFactory.getMessageFactory();
		this.protocolTraduttore = this.factory.createTraduttore();
		this.xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
	}


	
	@Override
	public SOAPElement toElement(Traccia tracciaObject)
			throws ProtocolException {
		try{			
			org.openspcoop2.core.tracciamento.Traccia tracciaBase = tracciaObject.getTraccia();
			
			// xml
			if(tracciaBase.getBustaXml()==null){
				if(tracciaObject.getBustaAsByteArray()!=null)
					tracciaBase.setBustaXml(new String(tracciaObject.getBustaAsByteArray()));
				else if(tracciaObject.getBustaAsElement()!=null){
					OpenSPCoop2Message msg = OpenSPCoop2MessageFactory.getMessageFactory().createEmptySOAPMessage(SOAPVersion.SOAP11);
					tracciaBase.setBustaXml(msg.getAsString(tracciaObject.getBustaAsElement(), false));
				}
			}
			
			// Traduzioni da factory

			if(tracciaBase!=null){
				if(tracciaBase.getBusta()!=null){
					if(tracciaBase.getBusta().getOraRegistrazione()!=null){
						if(tracciaBase.getBusta().getOraRegistrazione().getSorgente()!=null){
							if(tracciaBase.getBusta().getOraRegistrazione().getSorgente().getBase()==null && 
									tracciaBase.getBusta().getOraRegistrazione().getSorgente().getTipo()!=null){
								tracciaBase.getBusta().getOraRegistrazione().getSorgente().setBase(this.getBaseValueTipoTempo(tracciaBase.getBusta().getOraRegistrazione().getSorgente().getTipo()));
							}
						}
					}
					if(tracciaBase.getBusta().getTrasmissioni()!=null && tracciaBase.getBusta().getTrasmissioni().sizeTrasmissioneList()>0){
						for (Trasmissione trasmissione : tracciaBase.getBusta().getTrasmissioni().getTrasmissioneList()) {
							if(trasmissione.getOraRegistrazione()!=null){
								if(trasmissione.getOraRegistrazione().getSorgente()!=null){
									if(trasmissione.getOraRegistrazione().getSorgente().getBase()==null &&
											trasmissione.getOraRegistrazione().getSorgente().getTipo()!=null){
										trasmissione.getOraRegistrazione().getSorgente().setBase(this.getBaseValueTipoTempo(trasmissione.getOraRegistrazione().getSorgente().getTipo()));
									}
								}
							}
						}
					}
					if(tracciaBase.getBusta().getRiscontri()!=null && tracciaBase.getBusta().getRiscontri().sizeRiscontroList()>0){
						for (Riscontro riscontro : tracciaBase.getBusta().getRiscontri().getRiscontroList()) {
							if(riscontro.getOraRegistrazione()!=null){
								if(riscontro.getOraRegistrazione().getSorgente()!=null){
									if(riscontro.getOraRegistrazione().getSorgente().getBase()==null &&
											riscontro.getOraRegistrazione().getSorgente().getTipo()!=null){
										riscontro.getOraRegistrazione().getSorgente().setBase(this.getBaseValueTipoTempo(riscontro.getOraRegistrazione().getSorgente().getTipo()));
									}
								}
							}
						}
					}
					if(tracciaBase.getBusta().getEccezioni()!=null && tracciaBase.getBusta().getEccezioni().sizeEccezioneList()>0){
						for (Eccezione eccezione : tracciaBase.getBusta().getEccezioni().getEccezioneList()) {
							if(eccezione.getCodice()!=null){
								if(eccezione.getCodice().getBase()==null && eccezione.getCodice().getTipo()!=null){
									eccezione.getCodice().setBase(this.getBaseValueCodiceEccezione(eccezione.getCodice().getTipo(), eccezione.getCodice().getSottotipo()));
								}
							}
							if(eccezione.getContestoCodifica()!=null){
								if(eccezione.getContestoCodifica().getBase()==null && 
										eccezione.getContestoCodifica().getTipo()!=null){
									eccezione.getContestoCodifica().setBase(this.getBaseValueContestoCodifica(eccezione.getContestoCodifica().getTipo()));
								}
							}
							if(eccezione.getRilevanza()!=null){
								if(eccezione.getRilevanza().getBase()==null &&
										eccezione.getRilevanza().getTipo()!=null){
									eccezione.getRilevanza().setBase(this.getBaseValueRilevanzaEccezione(eccezione.getRilevanza().getTipo()));
								}
							}
						}
					}
				}
			}
							
			// serializzazione
			byte[]xmlTraccia = org.openspcoop2.core.tracciamento.utils.XMLUtils.generateTraccia(tracciaBase);
			Element elementTraccia = this.xmlUtils.newElement(xmlTraccia);
			
			SOAPFactory sf = SoapUtils.getSoapFactory(SOAPVersion.SOAP11);
			SOAPElement traccia =  sf.createElement(elementTraccia);
					
			return  traccia;

		} catch(Exception e) {
			this.log.error("XMLBuilder.buildElement_Tracciamento error: "+e.getMessage(),e);
			throw new ProtocolException("XMLBuilder.buildElement_Tracciamento error: "+e.getMessage(),e);
		}
	}

	@Override
	public String toString(Traccia traccia) throws ProtocolException {
		SOAPElement tracciamento = this.toElement(traccia);	
		return Utilities.toString(this.log, tracciamento);
	}

	@Override
	public byte[] toByteArray(Traccia traccia) throws ProtocolException {
		SOAPElement tracciamento = this.toElement(traccia);	
		return Utilities.toByteArray(this.log, tracciamento);
	}

	
	
	// UTILITIES 
	
	private String getBaseValueTipoTempo(TipoTempo tipoTempo){
		switch (tipoTempo) {
		case LOCALE:
			return this.protocolTraduttore.toString(TipoOraRegistrazione.LOCALE);
		case SCONOSCIUTO:
			return this.protocolTraduttore.toString(TipoOraRegistrazione.UNKNOWN);
		case SINCRONIZZATO:
			return this.protocolTraduttore.toString(TipoOraRegistrazione.SINCRONIZZATO);
		}
		return null;
	}
	
	private String getBaseValueCodiceEccezione(Integer codice, Integer subCodice){
		CodiceErroreCooperazione errore = CodiceErroreCooperazione.toCodiceErroreCooperazione(codice);
		if(subCodice==null){
			return this.protocolTraduttore.toString(errore);
		}
		else{
			SubCodiceErrore sub = new SubCodiceErrore();
			sub.setSubCodice(subCodice);
			return this.protocolTraduttore.toString(errore,sub);
		}
	}
	
	private String getBaseValueContestoCodifica(TipoCodificaEccezione codifica){
		switch (codifica) {
		case ECCEZIONE_PROCESSAMENTO:
			return this.protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.ContestoCodificaEccezione.PROCESSAMENTO);
		case ECCEZIONE_VALIDAZIONE_PROTOCOLLO:
			return this.protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.ContestoCodificaEccezione.INTESTAZIONE);
		case SCONOSCIUTO:
			return null;
		}
		return null;
	}
	
	private String getBaseValueRilevanzaEccezione(TipoRilevanzaEccezione codifica){
		switch (codifica) {
		case DEBUG:
			return this.protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.LivelloRilevanza.DEBUG);
		case ERROR:
			return this.protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.LivelloRilevanza.ERROR);
		case FATAL:
			return this.protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.LivelloRilevanza.FATAL);
		case INFO:
			return this.protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.LivelloRilevanza.INFO);
		case WARN:
			return this.protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.LivelloRilevanza.WARN);
		case SCONOSCIUTO:
			return this.protocolTraduttore.toString(org.openspcoop2.protocol.sdk.constants.LivelloRilevanza.UNKNOWN);
		}
		return null;
	}
}