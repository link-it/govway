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

import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;

import org.apache.log4j.Logger;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.tracciamento.Allegati;
import org.openspcoop2.core.tracciamento.CodiceEccezione;
import org.openspcoop2.core.tracciamento.ContestoCodificaEccezione;
import org.openspcoop2.core.tracciamento.Data;
import org.openspcoop2.core.tracciamento.Dominio;
import org.openspcoop2.core.tracciamento.DominioSoggetto;
import org.openspcoop2.core.tracciamento.Eccezioni;
import org.openspcoop2.core.tracciamento.ProfiloCollaborazione;
import org.openspcoop2.core.tracciamento.ProfiloTrasmissione;
import org.openspcoop2.core.tracciamento.Protocollo;
import org.openspcoop2.core.tracciamento.ProtocolloProprieta;
import org.openspcoop2.core.tracciamento.RilevanzaEccezione;
import org.openspcoop2.core.tracciamento.Riscontri;
import org.openspcoop2.core.tracciamento.Servizio;
import org.openspcoop2.core.tracciamento.Soggetto;
import org.openspcoop2.core.tracciamento.SoggettoIdentificativo;
import org.openspcoop2.core.tracciamento.TipoData;
import org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione;
import org.openspcoop2.core.tracciamento.Trasmissioni;
import org.openspcoop2.core.tracciamento.constants.Costanti;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.protocol.basic.Utilities;
import org.openspcoop2.protocol.sdk.Allegato;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.Riscontro;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.EsitoElaborazioneMessaggioTracciatura;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.LivelloRilevanza;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.constants.TipoTraccia;
import org.openspcoop2.protocol.sdk.tracciamento.EsitoElaborazioneMessaggioTracciato;
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
	
	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.factory;
	}
	
	public XMLTracciaBuilder(IProtocolFactory factory){
		this.log = factory.getLogger();
		this.factory = factory;
		this.fac = OpenSPCoop2MessageFactory.getMessageFactory();
		this.xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
	}

	@Override
	public SOAPElement toElement(Traccia tracciaObject)
			throws ProtocolException {
		try{			
			org.openspcoop2.core.tracciamento.Traccia tracciaBase = new org.openspcoop2.core.tracciamento.Traccia();
			
			// tipoTraccia
			if(tracciaObject.getTipoMessaggio()!=null){
				if(TipoTraccia.RICHIESTA.equals(tracciaObject.getTipoMessaggio())){
					tracciaBase.setTipo(Costanti.TIPO_TRACCIA_RICHIESTA);
				}
				else{
					tracciaBase.setTipo(Costanti.TIPO_TRACCIA_RISPOSTA);
				}
			}
			
			// *** Dati Porta di Comunicazione che ha emesso la traccia ***
			if(tracciaObject.getIdSoggetto()!=null){
				Dominio dominio = new Dominio();
				dominio.setIdentificativoPorta(tracciaObject.getIdSoggetto().getCodicePorta());
				DominioSoggetto dominioSoggetto = new DominioSoggetto();
				dominioSoggetto.setBase(tracciaObject.getIdSoggetto().getNome());
				dominioSoggetto.setTipo(tracciaObject.getIdSoggetto().getTipo());
				dominio.setSoggetto(dominioSoggetto);
				if(tracciaObject.getTipoPdD()!=null){
					if(TipoPdD.DELEGATA.equals(tracciaObject.getTipoPdD())){
						dominio.setFunzione(Costanti.TIPO_PDD_PORTA_DELEGATA);
					}
					else if(TipoPdD.APPLICATIVA.equals(tracciaObject.getTipoPdD())){
						dominio.setFunzione(Costanti.TIPO_PDD_PORTA_APPLICATIVA);
					}
					else if(TipoPdD.INTEGRATION_MANAGER.equals(tracciaObject.getTipoPdD())){
						dominio.setFunzione(Costanti.TIPO_PDD_INTEGRATION_MANAGER);
					}
					else if(TipoPdD.ROUTER.equals(tracciaObject.getTipoPdD())){
						dominio.setFunzione(Costanti.TIPO_PDD_ROUTER);
					}
				}
				tracciaBase.setDominio(dominio);
			}
			
			// ** Traccia **
			tracciaBase.setOraRegistrazione(tracciaObject.getGdo());
			if(tracciaObject.getEsitoElaborazioneMessaggioTracciato()!=null){
				EsitoElaborazioneMessaggioTracciato esito = tracciaObject.getEsitoElaborazioneMessaggioTracciato();
				TracciaEsitoElaborazione esitoBase = new TracciaEsitoElaborazione();
				esitoBase.setDettaglio(esito.getDettaglio());
				if(EsitoElaborazioneMessaggioTracciatura.INVIATO.equals(esito.getEsito())){
					esitoBase.setTipo(Costanti.TIPO_ESITO_ELABORAZIONE_INVIATO);
				}
				else if(EsitoElaborazioneMessaggioTracciatura.RICEVUTO.equals(esito.getEsito())){
					esitoBase.setTipo(Costanti.TIPO_ESITO_ELABORAZIONE_RICEVUTO);
				}
				else if(EsitoElaborazioneMessaggioTracciatura.ERRORE.equals(esito.getEsito())){
					esitoBase.setTipo(Costanti.TIPO_ESITO_ELABORAZIONE_ERRORE);
				}
				tracciaBase.setEsitoElaborazione(esitoBase);
			}
			tracciaBase.setIdentificativoCorrelazioneRichiesta(tracciaObject.getCorrelazioneApplicativa());
			tracciaBase.setIdentificativoCorrelazioneRisposta(tracciaObject.getCorrelazioneApplicativaRisposta());
			tracciaBase.setLocation(tracciaObject.getLocation());
			
			// ** Dati Busta **
			
			Busta busta = tracciaObject.getBusta();
			if(busta!=null){
				org.openspcoop2.core.tracciamento.Busta bustaBase = new org.openspcoop2.core.tracciamento.Busta();
				
				ITraduttore traduttore = this.getProtocolFactory().createTraduttore();
				
				// mittente
				if(busta.getTipoMittente()!=null && busta.getMittente()!=null){
					Soggetto mittenteBase = new Soggetto();	
					SoggettoIdentificativo identificativo = new SoggettoIdentificativo();
					identificativo.setTipo(busta.getTipoMittente());
					identificativo.setBase(busta.getMittente());
					mittenteBase.setIdentificativo(identificativo);
					mittenteBase.setIdentificativoPorta(busta.getIdentificativoPortaMittente());
					mittenteBase.setIndirizzo(busta.getIndirizzoMittente());
					bustaBase.setMittente(mittenteBase);
				}
				
				// destinatario
				if(busta.getTipoDestinatario()!=null && busta.getDestinatario()!=null){
					Soggetto destinatarioBase = new Soggetto();	
					SoggettoIdentificativo identificativo = new SoggettoIdentificativo();
					identificativo.setTipo(busta.getTipoDestinatario());
					identificativo.setBase(busta.getDestinatario());
					destinatarioBase.setIdentificativo(identificativo);
					destinatarioBase.setIdentificativoPorta(busta.getIdentificativoPortaDestinatario());
					destinatarioBase.setIndirizzo(busta.getIndirizzoDestinatario());
					bustaBase.setDestinatario(destinatarioBase);
				}
				
				// profilo-collaborazione
				if(busta.getProfiloDiCollaborazione()!=null){
					ProfiloCollaborazione profiloBase = new ProfiloCollaborazione();
					if(ProfiloDiCollaborazione.ONEWAY.equals(busta.getProfiloDiCollaborazione())){
						profiloBase.setTipo(Costanti.TIPO_PROFILO_COLLABORAZIONE_ONEWAY);
					}
					else if(ProfiloDiCollaborazione.SINCRONO.equals(busta.getProfiloDiCollaborazione())){
						profiloBase.setTipo(Costanti.TIPO_PROFILO_COLLABORAZIONE_SINCRONO);
					}
					else if(ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(busta.getProfiloDiCollaborazione())){
						profiloBase.setTipo(Costanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO);		
					}
					else if(ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(busta.getProfiloDiCollaborazione())){
						profiloBase.setTipo(Costanti.TIPO_PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO);
					}
					else{
						profiloBase.setTipo(Costanti.TIPO_PROFILO_COLLABORAZIONE_SCONOSCIUTO);
					}
					if(busta.getProfiloDiCollaborazioneValue()!=null)
						profiloBase.setBase(busta.getProfiloDiCollaborazioneValue());
					else{
						profiloBase.setBase(traduttore.toString(busta.getProfiloDiCollaborazione()));
					}
					bustaBase.setProfiloCollaborazione(profiloBase);
				}
				
				// servizio
				if(busta.getServizio()!=null || busta.getTipoServizio()!=null ){
					Servizio servizioBase = new Servizio();
					if(busta.getServizio()!=null){
						servizioBase.setBase(busta.getServizio());
					}
					else{
						servizioBase.setBase("");
					}
					servizioBase.setTipo(busta.getTipoServizio());
					servizioBase.setVersione(new BigInteger(busta.getVersioneServizio()+""));
					bustaBase.setServizio(servizioBase);
				}
				
				// azione
				bustaBase.setAzione(busta.getAzione());
				
				// servizio-correlato
				if(busta.getServizioCorrelato()!=null || busta.getTipoServizioCorrelato()!=null ){
					Servizio servizioBase = new Servizio();
					if(busta.getServizioCorrelato()!=null){
						servizioBase.setBase(busta.getServizioCorrelato());
					}
					else{
						servizioBase.setBase("");
					}
					servizioBase.setTipo(busta.getTipoServizioCorrelato());
					bustaBase.setServizioCorrelato(servizioBase);
				}
				
				// collaborazione
				bustaBase.setCollaborazione(busta.getCollaborazione());
				
				// identificativo
				bustaBase.setIdentificativo(busta.getID());
				
				// riferimento-messaggio
				bustaBase.setRiferimentoMessaggio(busta.getRiferimentoMessaggio());
				
				// ora registrazione
				if(busta.getOraRegistrazione()!=null || busta.getTipoOraRegistrazione()!=null){
					Data dataBase = new Data();
					dataBase.setDateTime(busta.getOraRegistrazione());
					if(busta.getTipoOraRegistrazione()!=null){
						TipoData tipoData = new TipoData();
						if(TipoOraRegistrazione.LOCALE.equals(busta.getTipoOraRegistrazione())){
							tipoData.setTipo(Costanti.TIPO_TEMPO_LOCALE);
						}
						else if(TipoOraRegistrazione.SINCRONIZZATO.equals(busta.getTipoOraRegistrazione())){
							tipoData.setTipo(Costanti.TIPO_TEMPO_SINCRONIZZATO);
						}
						else{
							tipoData.setTipo(Costanti.TIPO_TEMPO_SCONOSCIUTO);
						}
						if(busta.getTipoOraRegistrazioneValue()!=null){
							tipoData.setBase(busta.getTipoOraRegistrazioneValue());
						}
						else {
							tipoData.setBase(traduttore.toString(busta.getTipoOraRegistrazione()));
						}
						dataBase.setSorgente(tipoData);
					}
					bustaBase.setOraRegistrazione(dataBase);
				}
				
				// scadenza
				bustaBase.setScadenza(busta.getScadenza());
				
				// profilo-trasmissione
				ProfiloTrasmissione profiloTrasmissioneBase = new ProfiloTrasmissione();
				profiloTrasmissioneBase.setConfermaRicezione(busta.isConfermaRicezione());
				if(busta.getInoltro()!=null){
					org.openspcoop2.core.tracciamento.Inoltro inoltroBase = new org.openspcoop2.core.tracciamento.Inoltro();
					if(Inoltro.CON_DUPLICATI.equals(busta.getInoltro())){
						inoltroBase.setTipo(Costanti.TIPO_INOLTRO_CON_DUPLICATI);
					}
					else if(Inoltro.SENZA_DUPLICATI.equals(busta.getInoltro())){
						inoltroBase.setTipo(Costanti.TIPO_INOLTRO_SENZA_DUPLICATI);
					}
					else{
						inoltroBase.setTipo(Costanti.TIPO_INOLTRO_SCONOSCIUTO);
					}
					if(busta.getInoltroValue()!=null){
						inoltroBase.setBase(busta.getInoltroValue());
					}
					else{
						inoltroBase.setBase(traduttore.toString(busta.getInoltro()));
					}
					profiloTrasmissioneBase.setInoltro(inoltroBase);
				}
				if(busta.getSequenza()>0){
					profiloTrasmissioneBase.setSequenza(new BigInteger(busta.getSequenza()+""));
				}
				bustaBase.setProfiloTrasmissione(profiloTrasmissioneBase);
				
				// servizio applicativo
				bustaBase.setServizioApplicativoFruitore(busta.getServizioApplicativoFruitore());
				bustaBase.setServizioApplicativoErogatore(busta.getServizioApplicativoErogatore());
				
				// digest
				bustaBase.setDigest(busta.getDigest());
				
				// trasmissioni
				Trasmissioni trLista = null;
				for (int i = 0; i < busta.sizeListaTrasmissioni(); i++) {
					Trasmissione tr = busta.getTrasmissione(i);
					if(tr!=null){
						if(trLista==null){
							trLista = new Trasmissioni();
						}
						org.openspcoop2.core.tracciamento.Trasmissione trBase = new org.openspcoop2.core.tracciamento.Trasmissione();
						
						if(tr.getTipoOrigine()!=null && tr.getOrigine()!=null){
							Soggetto origineBase = new Soggetto();	
							SoggettoIdentificativo identificativoOrigine = new SoggettoIdentificativo();
							identificativoOrigine.setTipo(tr.getTipoOrigine());
							identificativoOrigine.setBase(tr.getOrigine());
							origineBase.setIdentificativo(identificativoOrigine);
							origineBase.setIdentificativoPorta(tr.getIdentificativoPortaOrigine());
							origineBase.setIndirizzo(tr.getIndirizzoOrigine());
							trBase.setOrigine(origineBase);
						}
						
						if(tr.getTipoDestinazione()!=null && tr.getDestinazione()!=null){
							Soggetto destinazioneBase = new Soggetto();	
							SoggettoIdentificativo identificativoDestinazione = new SoggettoIdentificativo();
							identificativoDestinazione.setTipo(tr.getTipoDestinazione());
							identificativoDestinazione.setBase(tr.getDestinazione());
							destinazioneBase.setIdentificativo(identificativoDestinazione);
							destinazioneBase.setIdentificativoPorta(tr.getIdentificativoPortaDestinazione());
							destinazioneBase.setIndirizzo(tr.getIndirizzoDestinazione());
							trBase.setDestinazione(destinazioneBase);
						}
						
						if(tr.getOraRegistrazione()!=null || tr.getTempo()!=null){
							Data dataBase = new Data();
							dataBase.setDateTime(tr.getOraRegistrazione());
							if(tr.getTempo()!=null){
								TipoData tipoData = new TipoData();
								if(TipoOraRegistrazione.LOCALE.equals(tr.getTempo())){
									tipoData.setTipo(Costanti.TIPO_TEMPO_LOCALE);
								}
								else if(TipoOraRegistrazione.SINCRONIZZATO.equals(tr.getTempo())){
									tipoData.setTipo(Costanti.TIPO_TEMPO_SINCRONIZZATO);
								}
								else{
									tipoData.setTipo(Costanti.TIPO_TEMPO_SCONOSCIUTO);
								}
								tipoData.setBase(tr.getTempoValue(this.factory));
								dataBase.setSorgente(tipoData);
							}
							trBase.setOraRegistrazione(dataBase);
						}
						
						trLista.addTrasmissione(trBase);
					}
				}
				bustaBase.setTrasmissioni(trLista);
				
				// riscontri
				Riscontri riscontriLista = null;
				for (int i = 0; i < busta.sizeListaRiscontri(); i++) {
					Riscontro r = busta.getRiscontro(i);
					if(r!=null){
						if(riscontriLista==null){
							riscontriLista = new Riscontri();
						}
						org.openspcoop2.core.tracciamento.Riscontro rBase = new org.openspcoop2.core.tracciamento.Riscontro();
						
						rBase.setIdentificativo(r.getID());
						
						if(r.getOraRegistrazione()!=null || r.getTipoOraRegistrazione()!=null){
							Data dataBase = new Data();
							dataBase.setDateTime(r.getOraRegistrazione());
							if(r.getTipoOraRegistrazione()!=null){
								TipoData tipoData = new TipoData();
								if(TipoOraRegistrazione.LOCALE.equals(r.getTipoOraRegistrazione())){
									tipoData.setTipo(Costanti.TIPO_TEMPO_LOCALE);
								}
								else if(TipoOraRegistrazione.SINCRONIZZATO.equals(r.getTipoOraRegistrazione())){
									tipoData.setTipo(Costanti.TIPO_TEMPO_SINCRONIZZATO);
								}
								else{
									tipoData.setTipo(Costanti.TIPO_TEMPO_SCONOSCIUTO);
								}
								tipoData.setBase(r.getTipoOraRegistrazioneValue(this.factory));
								dataBase.setSorgente(tipoData);
							}
							rBase.setOraRegistrazione(dataBase);
						}
						
						riscontriLista.addRiscontro(rBase);
					}
				}
				bustaBase.setRiscontri(riscontriLista);
				
				// eccezioni
				Eccezioni eccezioniLista = null;
				for (int i = 0; i < busta.sizeListaEccezioni(); i++) {
					Eccezione e = busta.getEccezione(i);
					if(e!=null){
						if(eccezioniLista==null){
							eccezioniLista = new Eccezioni();
						}
						org.openspcoop2.core.tracciamento.Eccezione eBase = new org.openspcoop2.core.tracciamento.Eccezione();
						
						if(e.getCodiceEccezione()!=null){
							CodiceEccezione ceBase = new CodiceEccezione();
							ceBase.setBase(e.getCodiceEccezioneValue(this.factory));
							ceBase.setTipo(new BigInteger(e.getCodiceEccezione().getCodice()+""));
							if(e.getSubCodiceEccezione()!=null){
								ceBase.setSottotipo(new BigInteger(e.getSubCodiceEccezione().getSubCodice()+""));
							}
							eBase.setCodice(ceBase);
						}
						
						if(e.getContestoCodifica()!=null){
							ContestoCodificaEccezione ceBase = new ContestoCodificaEccezione();
							ceBase.setBase(e.getContestoCodificaValue(this.factory));
							if(org.openspcoop2.protocol.sdk.constants.ContestoCodificaEccezione.INTESTAZIONE.equals(e.getContestoCodifica())){
								ceBase.setTipo(Costanti.TIPO_CODIFICA_ECCEZIONE_VALIDAZIONE);
							}
							else if(org.openspcoop2.protocol.sdk.constants.ContestoCodificaEccezione.PROCESSAMENTO.equals(e.getContestoCodifica())){
								ceBase.setTipo(Costanti.TIPO_CODIFICA_ECCEZIONE_PROCESSAMENTO);
							}
							else{
								ceBase.setTipo(Costanti.TIPO_CODIFICA_ECCEZIONE_SCONOSCIUTO);
							}
							eBase.setContestoCodifica(ceBase);
						}
						
						eBase.setPosizione(e.getDescrizione(this.factory));
						
						if(e.getRilevanza()!=null){
							RilevanzaEccezione rBase = new RilevanzaEccezione();
							rBase.setBase(e.getRilevanzaValue(this.factory));
							if(LivelloRilevanza.DEBUG.equals(e.getRilevanza())){
								rBase.setTipo(Costanti.TIPO_RILEVANZA_ECCEZIONE_DEBUG);
							}
							else if(LivelloRilevanza.INFO.equals(e.getRilevanza())){
								rBase.setTipo(Costanti.TIPO_RILEVANZA_ECCEZIONE_INFO);
							}
							else if(LivelloRilevanza.WARN.equals(e.getRilevanza())){
								rBase.setTipo(Costanti.TIPO_RILEVANZA_ECCEZIONE_WARN);
							}
							else if(LivelloRilevanza.ERROR.equals(e.getRilevanza())){
								rBase.setTipo(Costanti.TIPO_RILEVANZA_ECCEZIONE_ERROR);
							}
							else if(LivelloRilevanza.FATAL.equals(e.getRilevanza())){
								rBase.setTipo(Costanti.TIPO_RILEVANZA_ECCEZIONE_FATAL);
							}
							else{
								rBase.setTipo(Costanti.TIPO_RILEVANZA_ECCEZIONE_SCONOSCIUTO);
							}
							eBase.setRilevanza(rBase);
						}
						
						eccezioniLista.addEccezione(eBase);
					}
				}
				bustaBase.setEccezioni(eccezioniLista);
						
				// Protocol Info
				if(busta.getProtocollo()!=null){
					Protocollo protocollo = new Protocollo();
					protocollo.setIdentificativo(busta.getProtocollo());
					Hashtable<String, String> properties = busta.getProperties();
					if(properties!=null){
						Enumeration<String> keys = properties.keys();
						while(keys.hasMoreElements()){
							String key = keys.nextElement();
							String value = properties.get(key);
							ProtocolloProprieta pp = new ProtocolloProprieta();
							pp.setNome(key);
							pp.setValore(value);
							protocollo.addProprieta(pp);
						}
					}
					bustaBase.setProtocollo(protocollo);
				}
				
				tracciaBase.setBusta(bustaBase);
			}
			
			// xml
			if(tracciaObject.getBustaAsString()!=null)
				tracciaBase.setBustaXml(tracciaObject.getBustaAsString());
			else if(tracciaObject.getBustaAsByteArray()!=null)
				tracciaBase.setBustaXml(new String(tracciaObject.getBustaAsByteArray()));
			else if(tracciaObject.getBustaAsElement()!=null){
				OpenSPCoop2Message msg = OpenSPCoop2MessageFactory.getMessageFactory().createEmptySOAPMessage(SOAPVersion.SOAP11);
				tracciaBase.setBustaXml(msg.getAsString(tracciaObject.getBustaAsElement(), false));
			}
			
			// DatiAllegati
			Allegati allegatiLista = null;
			for (int i = 0; i < tracciaObject.sizeListaAllegati(); i++) {
				Allegato a = tracciaObject.getAllegato(i);
				if(a!=null){
					if(allegatiLista==null){
						allegatiLista = new Allegati();
					}
					org.openspcoop2.core.tracciamento.Allegato aBase = new org.openspcoop2.core.tracciamento.Allegato();
					
					aBase.setContentId(a.getContentId());
					aBase.setContentLocation(a.getContentLocation());
					aBase.setContentType(a.getContentType());
					aBase.setDigest(a.getDigest());
				}
			}
			tracciaBase.setAllegati(allegatiLista);
			
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

}