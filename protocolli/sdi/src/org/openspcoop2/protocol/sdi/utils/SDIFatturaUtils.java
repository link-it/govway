/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.sdi.utils;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdi.config.SDIProperties;
import org.openspcoop2.protocol.sdi.constants.SDICostanti;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRicezioneFatture;
import org.openspcoop2.protocol.sdi.validator.SDIValidazioneUtils;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.utils.xml.AbstractValidatoreXSD;

/**
 * SDIFatturaUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDIFatturaUtils {

	public static void validazioneFattura(byte[] fattura, SDIProperties sdiProperties, List<Eccezione> eccezioniValidazione, 
			SDIValidazioneUtils validazioneUtils, IProtocolFactory<?> protocolFactory,
			Busta busta,OpenSPCoop2Message msg,boolean addMsgInContextIfEnabled,
			boolean validaDatiTrasmissione, boolean forceDisableValidazioneXSD) throws Exception{
			
		boolean forceEccezioneLivelloInfo = false;
		if(validaDatiTrasmissione) {
			if(sdiProperties.isEnableAccessoFattura() == false) {
				return;
			}
			else if(sdiProperties.isEnableAccessoFatturaWarningMode()) {
				forceEccezioneLivelloInfo = true;
			}
		}
		
		// Valori letti nel file Metadati o come parametro nel caso di fattura da inviare
		String versioneFattura = busta.getProperty(SDICostanti.SDI_BUSTA_EXT_VERSIONE_FATTURA_PA);
				
		String codiceDestinatario = busta.getProperty(SDICostanti.SDI_BUSTA_EXT_CODICE_DESTINATARIO);
		
		// validazione XSD file Fattura
		if(forceDisableValidazioneXSD==false && sdiProperties.isEnableValidazioneXsdFattura()){
			try{
				AbstractValidatoreXSD validatore = null;
				if(SDICostanti.SDI_VERSIONE_FATTURA_PA_10.equals(versioneFattura)){
					validatore = it.gov.fatturapa.sdi.fatturapa.v1_0.utils.XSDValidatorWithSignature.getOpenSPCoop2MessageXSDValidator(protocolFactory.getLogger());
				}
				else if(SDICostanti.SDI_VERSIONE_FATTURA_PA_11.equals(versioneFattura)){
					validatore = it.gov.fatturapa.sdi.fatturapa.v1_1.utils.XSDValidatorWithSignature.getOpenSPCoop2MessageXSDValidator(protocolFactory.getLogger());
				}
				else if(SDICostanti.SDI_VERSIONE_FATTURA_SEMPLIFICATA_10.equals(versioneFattura)){
					validatore = it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.utils.XSDValidatorWithSignature.getOpenSPCoop2MessageXSDValidator(protocolFactory.getLogger());
				}
				else{
					validatore = it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.utils.XSDValidatorWithSignature.getOpenSPCoop2MessageXSDValidator(protocolFactory.getLogger());
				}
				validatore.valida(new ByteArrayInputStream(fattura));
			}catch(Exception e){
				String msgErrore = "Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_FILE+"] contiene un file Fattura("+versioneFattura+") non valido rispetto allo schema XSD: "+e.getMessage();
				eccezioniValidazione.add(
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,msgErrore,e,forceEccezioneLivelloInfo));
				return;	 // esco anche in caso di forceEccezioneLivelloInfo poiche' la fattura non e' ben formata e non ha senso andare avanti
			}
		}
		
		// Lettura fattura
		String attributoVersione = null;
		String trasmittenteIdPaese = null;
		String trasmittenteIdCodice = null;
		String trasmissioneProgressivoInvio = null;
		String trasmissioneFormatoTrasmissione = null;
		String trasmissioneCodiceDestinatario = null;
		String trasmissionePECDestinatario = null; // solo per versione 1.2
		String cedentePrestatoreIdPaese = null;
		String cedentePrestatoreIdCodice = null;
		String cedentePrestatoreDenominazione = null;
		String cedentePrestatoreNome = null;
		String cedentePrestatoreCognome = null;
		String cedentePrestatoreCodiceFiscale = null;
		String cessionarioCommittenteIdPaese = null;
		String cessionarioCommittenteIdCodice = null;
		String cessionarioCommittenteDenominazione = null;
		String cessionarioCommittenteNome = null;
		String cessionarioCommittenteCognome = null;
		String cessionarioCommittenteCodiceFiscale = null;
		String terzoIdPaese = null;
		String terzoIdCodice = null;
		String terzoDenominazione = null;
		String terzoNome = null;
		String terzoCognome = null;
		String terzoCodiceFiscale = null;
		String soggettoEmittente = null;
		try{
			// Fattura v1.0
			if(SDICostanti.SDI_VERSIONE_FATTURA_PA_10.equals(versioneFattura)){
				it.gov.fatturapa.sdi.fatturapa.v1_0.utils.serializer.JaxbDeserializer deserializer =
						new it.gov.fatturapa.sdi.fatturapa.v1_0.utils.serializer.JaxbDeserializer();
				
				it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType fatturaObject = deserializer.readFatturaElettronicaType(fattura);
				if(addMsgInContextIfEnabled && sdiProperties.isSaveFatturaInContext()){
					msg.addContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_FATTURA, fatturaObject);
				}
				it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaHeaderType fatturaHeaderObject = fatturaObject.getFatturaElettronicaHeader();
				
				// Attributo Versione
				attributoVersione = fatturaObject.getVersione();
				
				// Fattura.DatiTrasmissione.IdTrasmittente
				trasmittenteIdPaese = fatturaHeaderObject.getDatiTrasmissione().getIdTrasmittente().getIdPaese();
				trasmittenteIdCodice = fatturaHeaderObject.getDatiTrasmissione().getIdTrasmittente().getIdCodice();
				
				// Fattura.DatiTrasmissione.ProgressivoInvio
				trasmissioneProgressivoInvio = fatturaHeaderObject.getDatiTrasmissione().getProgressivoInvio();
				
				// Fattura.DatiTrasmissione.FormatoTrasmissione
				trasmissioneFormatoTrasmissione = fatturaHeaderObject.getDatiTrasmissione().getFormatoTrasmissione().name();
				
				// Fattura.DatiTrasmissione.CodiceDestinatario
				trasmissioneCodiceDestinatario = fatturaHeaderObject.getDatiTrasmissione().getCodiceDestinatario();
				
				if(fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici()!=null){
					if(fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getIdFiscaleIVA()!=null){
						// Fattura.CedentePrestatore.DatiAnagrafici.IdFiscaleIVA
						cedentePrestatoreIdPaese = fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getIdFiscaleIVA().getIdPaese();
						cedentePrestatoreIdCodice = fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getIdFiscaleIVA().getIdCodice();
					}
					if(fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getCodiceFiscale()!=null){
						cedentePrestatoreCodiceFiscale = fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getCodiceFiscale();
					}
					if(fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getAnagrafica()!=null){
						// Fattura.CedentePrestatore.DatiAnagrafici.Anagrafica.[Denominazione,Nome/Cognome] sono in choice
						cedentePrestatoreDenominazione = fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getDenominazione();
						cedentePrestatoreNome = fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getNome();
						cedentePrestatoreCognome = fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getCognome();
					}
				}
				
				if(fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici()!=null){
					if(fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getIdFiscaleIVA()!=null){
						// Fattura.CessionarioCommittente.DatiAnagrafici.IdFiscaleIVA
						cessionarioCommittenteIdPaese = fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getIdFiscaleIVA().getIdPaese();
						cessionarioCommittenteIdCodice = fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getIdFiscaleIVA().getIdCodice();
					}
					if(fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getCodiceFiscale()!=null){
						cessionarioCommittenteCodiceFiscale = fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getCodiceFiscale();
					}
					if(fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getAnagrafica()!=null){
						// Fattura.CessionarioCommittente.DatiAnagrafici.Anagrafica.[Denominazione,Nome/Cognome] sono in choice
						cessionarioCommittenteDenominazione = fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getAnagrafica().getDenominazione();
						cessionarioCommittenteNome = fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getAnagrafica().getNome();
						cessionarioCommittenteCognome = fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getAnagrafica().getCognome();
					}
				}
				if(fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente()!=null && 
						fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici()!=null){
					if(fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getIdFiscaleIVA()!=null){
						// Fattura.TerzoIntermediarioOSoggettoEmittente.DatiAnagrafici.IdFiscaleIVA
						terzoIdPaese = fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getIdFiscaleIVA().getIdPaese();
						terzoIdCodice = fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getIdFiscaleIVA().getIdCodice();
					}
					if(fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getCodiceFiscale()!=null){
						terzoCodiceFiscale = fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getCodiceFiscale();
					}
					if(fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getAnagrafica()!=null){
						// Fattura.TerzoIntermediarioOSoggettoEmittente.DatiAnagrafici.Anagrafica.[Denominazione,Nome/Cognome] sono in choice
						terzoDenominazione = fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getAnagrafica().getDenominazione();
						terzoNome = fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getAnagrafica().getNome();
						terzoCognome = fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getAnagrafica().getCognome();
					}
				}
				if(fatturaHeaderObject.getSoggettoEmittente()!=null){
					// Fattura.SoggettoEmittente
					soggettoEmittente = fatturaHeaderObject.getSoggettoEmittente().name();
				}
				
			}
			
			// Fattura v1.1
			else if(SDICostanti.SDI_VERSIONE_FATTURA_PA_11.equals(versioneFattura)){
				it.gov.fatturapa.sdi.fatturapa.v1_1.utils.serializer.JaxbDeserializer deserializer =
						new it.gov.fatturapa.sdi.fatturapa.v1_1.utils.serializer.JaxbDeserializer();
				it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType fatturaObject = deserializer.readFatturaElettronicaType(fattura);
				if(addMsgInContextIfEnabled && sdiProperties.isSaveFatturaInContext()){
					msg.addContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_FATTURA, fatturaObject);
				}
				it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaHeaderType fatturaHeaderObject = fatturaObject.getFatturaElettronicaHeader();
				
				// Attributo Versione
				attributoVersione = fatturaObject.getVersione();
				
				// Fattura.DatiTrasmissione.IdTrasmittente
				trasmittenteIdPaese = fatturaHeaderObject.getDatiTrasmissione().getIdTrasmittente().getIdPaese();
				trasmittenteIdCodice = fatturaHeaderObject.getDatiTrasmissione().getIdTrasmittente().getIdCodice();
				
				// Fattura.DatiTrasmissione.ProgressivoInvio
				trasmissioneProgressivoInvio = fatturaHeaderObject.getDatiTrasmissione().getProgressivoInvio();
				
				// Fattura.DatiTrasmissione.FormatoTrasmissione
				trasmissioneFormatoTrasmissione = fatturaHeaderObject.getDatiTrasmissione().getFormatoTrasmissione().name();
				
				// Fattura.DatiTrasmissione.CodiceDestinatario
				trasmissioneCodiceDestinatario = fatturaHeaderObject.getDatiTrasmissione().getCodiceDestinatario();
				
				if(fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici()!=null){
					if(fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getIdFiscaleIVA()!=null){
						// Fattura.CedentePrestatore.DatiAnagrafici.IdFiscaleIVA
						cedentePrestatoreIdPaese = fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getIdFiscaleIVA().getIdPaese();
						cedentePrestatoreIdCodice = fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getIdFiscaleIVA().getIdCodice();
					}
					if(fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getCodiceFiscale()!=null){
						cedentePrestatoreCodiceFiscale = fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getCodiceFiscale();
					}
					if(fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getAnagrafica()!=null){
						// Fattura.CedentePrestatore.DatiAnagrafici.Anagrafica.[Denominazione,Nome/Cognome] sono in choice
						cedentePrestatoreDenominazione = fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getDenominazione();
						cedentePrestatoreNome = fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getNome();
						cedentePrestatoreCognome = fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getCognome();
					}
				}
					
				if(fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici()!=null){
					if(fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getIdFiscaleIVA()!=null){
						// Fattura.CessionarioCommittente.DatiAnagrafici.IdFiscaleIVA
						cessionarioCommittenteIdPaese = fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getIdFiscaleIVA().getIdPaese();
						cessionarioCommittenteIdCodice = fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getIdFiscaleIVA().getIdCodice();
					}
					if(fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getCodiceFiscale()!=null){
						cessionarioCommittenteCodiceFiscale = fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getCodiceFiscale();
					}
					if(fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getAnagrafica()!=null){
						// Fattura.CessionarioCommittente.DatiAnagrafici.Anagrafica.[Denominazione,Nome/Cognome] sono in choice
						cessionarioCommittenteDenominazione = fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getAnagrafica().getDenominazione();
						cessionarioCommittenteNome = fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getAnagrafica().getNome();
						cessionarioCommittenteCognome = fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getAnagrafica().getCognome();
					}
				}
					
				if(fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente()!=null && 
						fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici()!=null){
					if(fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getIdFiscaleIVA()!=null){
						// Fattura.TerzoIntermediarioOSoggettoEmittente.DatiAnagrafici.IdFiscaleIVA
						terzoIdPaese = fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getIdFiscaleIVA().getIdPaese();
						terzoIdCodice = fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getIdFiscaleIVA().getIdCodice();
					}
					if(fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getCodiceFiscale()!=null){
						terzoCodiceFiscale = fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getCodiceFiscale();
					}
					if(fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getAnagrafica()!=null){
						// Fattura.TerzoIntermediarioOSoggettoEmittente.DatiAnagrafici.Anagrafica.[Denominazione,Nome/Cognome] sono in choice
						terzoDenominazione = fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getAnagrafica().getDenominazione();
						terzoNome = fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getAnagrafica().getNome();
						terzoCognome = fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getAnagrafica().getCognome();
					}
				}
				if(fatturaHeaderObject.getSoggettoEmittente()!=null){
					// Fattura.SoggettoEmittente
					soggettoEmittente = fatturaHeaderObject.getSoggettoEmittente().name();
				}
				
			}
			
			// FatturaSemplificata v1.0
			else if(SDICostanti.SDI_VERSIONE_FATTURA_SEMPLIFICATA_10.equals(versioneFattura)){
				it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.utils.serializer.JaxbDeserializer deserializer =
						new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.utils.serializer.JaxbDeserializer();
				it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType fatturaObject = deserializer.readFatturaElettronicaType(fattura);
				if(addMsgInContextIfEnabled && sdiProperties.isSaveFatturaInContext()){
					msg.addContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_FATTURA, fatturaObject);
				}
				it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType fatturaHeaderObject = fatturaObject.getFatturaElettronicaHeader();
				
				// Attributo Versione
				if(fatturaObject.getVersione()!=null) {
					attributoVersione = fatturaObject.getVersione().name();
				}
				
				// Fattura.DatiTrasmissione.IdTrasmittente
				trasmittenteIdPaese = fatturaHeaderObject.getDatiTrasmissione().getIdTrasmittente().getIdPaese();
				trasmittenteIdCodice = fatturaHeaderObject.getDatiTrasmissione().getIdTrasmittente().getIdCodice();
				
				// Fattura.DatiTrasmissione.ProgressivoInvio
				trasmissioneProgressivoInvio = fatturaHeaderObject.getDatiTrasmissione().getProgressivoInvio();
				
				// Fattura.DatiTrasmissione.FormatoTrasmissione
				trasmissioneFormatoTrasmissione = fatturaHeaderObject.getDatiTrasmissione().getFormatoTrasmissione().name();
				
				// Fattura.DatiTrasmissione.CodiceDestinatario
				trasmissioneCodiceDestinatario = fatturaHeaderObject.getDatiTrasmissione().getCodiceDestinatario();
				
				if(fatturaHeaderObject.getCedentePrestatore()!=null){
					if(fatturaHeaderObject.getCedentePrestatore().getIdFiscaleIVA()!=null){
						// Fattura.CedentePrestatore.IdFiscaleIVA
						cedentePrestatoreIdPaese = fatturaHeaderObject.getCedentePrestatore().getIdFiscaleIVA().getIdPaese();
						cedentePrestatoreIdCodice = fatturaHeaderObject.getCedentePrestatore().getIdFiscaleIVA().getIdCodice();
					}
					if(fatturaHeaderObject.getCedentePrestatore().getCodiceFiscale()!=null){
						cedentePrestatoreCodiceFiscale = fatturaHeaderObject.getCedentePrestatore().getCodiceFiscale();
					}
					// Fattura.CedentePrestatore.[Denominazione,Nome/Cognome] sono in choice
					if(fatturaHeaderObject.getCedentePrestatore().getDenominazione()!=null){
						cedentePrestatoreDenominazione = fatturaHeaderObject.getCedentePrestatore().getDenominazione();
					}
					if(fatturaHeaderObject.getCedentePrestatore().getNome()!=null){
						cedentePrestatoreNome = fatturaHeaderObject.getCedentePrestatore().getNome();
					}
					if(fatturaHeaderObject.getCedentePrestatore().getCognome()!=null){
						cedentePrestatoreCognome = fatturaHeaderObject.getCedentePrestatore().getCognome();
					}
				}
					
				if(fatturaHeaderObject.getCessionarioCommittente()!=null){
					if(fatturaHeaderObject.getCessionarioCommittente().getIdentificativiFiscali()!=null) {
						if(fatturaHeaderObject.getCessionarioCommittente().getIdentificativiFiscali().getIdFiscaleIVA()!=null){
							// Fattura.CessionarioCommittente.DatiAnagrafici.IdFiscaleIVA
							cessionarioCommittenteIdPaese = fatturaHeaderObject.getCessionarioCommittente().getIdentificativiFiscali().getIdFiscaleIVA().getIdPaese();
							cessionarioCommittenteIdCodice = fatturaHeaderObject.getCessionarioCommittente().getIdentificativiFiscali().getIdFiscaleIVA().getIdCodice();
						}
						if(fatturaHeaderObject.getCessionarioCommittente().getIdentificativiFiscali().getCodiceFiscale()!=null){
							cessionarioCommittenteCodiceFiscale = fatturaHeaderObject.getCessionarioCommittente().getIdentificativiFiscali().getCodiceFiscale();
						}
					}
					if(fatturaHeaderObject.getCessionarioCommittente().getAltriDatiIdentificativi()!=null) {
						// Fattura.CessionarioCommittente.AltriDatiIdentificativi.[Denominazione,Nome/Cognome] sono in choice
						if(fatturaHeaderObject.getCessionarioCommittente().getAltriDatiIdentificativi().getDenominazione()!=null){
							cessionarioCommittenteDenominazione = fatturaHeaderObject.getCessionarioCommittente().getAltriDatiIdentificativi().getDenominazione();
						}
						if(fatturaHeaderObject.getCessionarioCommittente().getAltriDatiIdentificativi().getNome()!=null){
							cessionarioCommittenteNome = fatturaHeaderObject.getCessionarioCommittente().getAltriDatiIdentificativi().getNome();
						}
						if(fatturaHeaderObject.getCessionarioCommittente().getAltriDatiIdentificativi().getCognome()!=null){
							cessionarioCommittenteCognome = fatturaHeaderObject.getCessionarioCommittente().getAltriDatiIdentificativi().getCognome();
						}
					}
				}
				
				if(fatturaHeaderObject.getSoggettoEmittente()!=null){
					// Fattura.SoggettoEmittente
					soggettoEmittente = fatturaHeaderObject.getSoggettoEmittente().name();
				}
				
			}
			
			// Fattura v1.2
			else{
				
				it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.utils.serializer.JaxbDeserializer deserializer =
						new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.utils.serializer.JaxbDeserializer();
				it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.FatturaElettronicaType fatturaObject = deserializer.readFatturaElettronicaType(fattura);
				if(addMsgInContextIfEnabled && sdiProperties.isSaveFatturaInContext()){
					msg.addContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_FATTURA, fatturaObject);
				}
				it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.FatturaElettronicaHeaderType fatturaHeaderObject = fatturaObject.getFatturaElettronicaHeader();
				
				// Attributo Versione
				if(fatturaObject.getVersione()!=null){
					attributoVersione = fatturaObject.getVersione().getValue();
				}
				
				// Fattura.DatiTrasmissione.IdTrasmittente
				trasmittenteIdPaese = fatturaHeaderObject.getDatiTrasmissione().getIdTrasmittente().getIdPaese();
				trasmittenteIdCodice = fatturaHeaderObject.getDatiTrasmissione().getIdTrasmittente().getIdCodice();
				
				// Fattura.DatiTrasmissione.ProgressivoInvio
				trasmissioneProgressivoInvio = fatturaHeaderObject.getDatiTrasmissione().getProgressivoInvio();
				
				// Fattura.DatiTrasmissione.FormatoTrasmissione
				trasmissioneFormatoTrasmissione = fatturaHeaderObject.getDatiTrasmissione().getFormatoTrasmissione().name();
				
				// Fattura.DatiTrasmissione.CodiceDestinatario
				trasmissioneCodiceDestinatario = fatturaHeaderObject.getDatiTrasmissione().getCodiceDestinatario();
				
				// Fattura.DatiTrasmissione.PECDestinatario
				trasmissionePECDestinatario = fatturaHeaderObject.getDatiTrasmissione().getPecDestinatario();
				
				if(fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici()!=null){
					if(fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getIdFiscaleIVA()!=null){
						// Fattura.CedentePrestatore.DatiAnagrafici.IdFiscaleIVA
						cedentePrestatoreIdPaese = fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getIdFiscaleIVA().getIdPaese();
						cedentePrestatoreIdCodice = fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getIdFiscaleIVA().getIdCodice();
					}
					if(fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getCodiceFiscale()!=null){
						cedentePrestatoreCodiceFiscale = fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getCodiceFiscale();
					}
					if(fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getAnagrafica()!=null){
						// Fattura.CedentePrestatore.DatiAnagrafici.Anagrafica.[Denominazione,Nome/Cognome] sono in choice
						cedentePrestatoreDenominazione = fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getDenominazione();
						cedentePrestatoreNome = fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getNome();
						cedentePrestatoreCognome = fatturaHeaderObject.getCedentePrestatore().getDatiAnagrafici().getAnagrafica().getCognome();
					}
				}
					
				if(fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici()!=null){
					if(fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getIdFiscaleIVA()!=null){
						// Fattura.CessionarioCommittente.DatiAnagrafici.IdFiscaleIVA
						cessionarioCommittenteIdPaese = fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getIdFiscaleIVA().getIdPaese();
						cessionarioCommittenteIdCodice = fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getIdFiscaleIVA().getIdCodice();
					}
					if(fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getCodiceFiscale()!=null){
						cessionarioCommittenteCodiceFiscale = fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getCodiceFiscale();
					}
					if(fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getAnagrafica()!=null){
						// Fattura.CessionarioCommittente.DatiAnagrafici.Anagrafica.[Denominazione,Nome/Cognome] sono in choice
						cessionarioCommittenteDenominazione = fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getAnagrafica().getDenominazione();
						cessionarioCommittenteNome = fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getAnagrafica().getNome();
						cessionarioCommittenteCognome = fatturaHeaderObject.getCessionarioCommittente().getDatiAnagrafici().getAnagrafica().getCognome();
					}
				}
					
				if(fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente()!=null && 
						fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici()!=null){
					if(fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getIdFiscaleIVA()!=null){
						// Fattura.TerzoIntermediarioOSoggettoEmittente.DatiAnagrafici.IdFiscaleIVA
						terzoIdPaese = fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getIdFiscaleIVA().getIdPaese();
						terzoIdCodice = fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getIdFiscaleIVA().getIdCodice();
					}
					if(fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getCodiceFiscale()!=null){
						terzoCodiceFiscale = fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getCodiceFiscale();
					}
					if(fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getAnagrafica()!=null){
						// Fattura.TerzoIntermediarioOSoggettoEmittente.DatiAnagrafici.Anagrafica.[Denominazione,Nome/Cognome] sono in choice
						terzoDenominazione = fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getAnagrafica().getDenominazione();
						terzoNome = fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getAnagrafica().getNome();
						terzoCognome = fatturaHeaderObject.getTerzoIntermediarioOSoggettoEmittente().getDatiAnagrafici().getAnagrafica().getCognome();
					}
				}
				if(fatturaHeaderObject.getSoggettoEmittente()!=null){
					// Fattura.SoggettoEmittente
					soggettoEmittente = fatturaHeaderObject.getSoggettoEmittente().name();
				}
			}
			
		}catch(Exception e){
			String msgErrore = "Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_FILE+"] contiene un file Fattura("+versioneFattura+") non valido: "+e.getMessage();
			eccezioniValidazione.add(
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,msgErrore,e,forceEccezioneLivelloInfo));
			return;	 // esco anche in caso di forceEccezioneLivelloInfo poiche' la fattura non e' ben formata e non ha senso andare avanti
		}
		
		// Attributo Versione
		String attributoAtteso = null;
		boolean attributoVersioneNonCorretto = false;
		if(SDICostanti.SDI_VERSIONE_FATTURA_PA_10.equals(versioneFattura)){
			if(!SDICostanti.SDI_ATTRIBUTE_VERSION_FATTURA_PA_10.equals(attributoVersione)){
				attributoAtteso = SDICostanti.SDI_ATTRIBUTE_VERSION_FATTURA_PA_10;
				attributoVersioneNonCorretto = true;
			}
		}
		else if(SDICostanti.SDI_VERSIONE_FATTURA_PA_11.equals(versioneFattura)){
			if(!SDICostanti.SDI_ATTRIBUTE_VERSION_FATTURA_PA_11.equals(attributoVersione)){
				attributoAtteso = SDICostanti.SDI_ATTRIBUTE_VERSION_FATTURA_PA_11;
				attributoVersioneNonCorretto = true;
			}
		}
		else if(SDICostanti.SDI_VERSIONE_FATTURA_SEMPLIFICATA_10.equals(versioneFattura)){
			if(!SDICostanti.SDI_ATTRIBUTE_VERSION_FATTURA_SEMPLIFICATA_10.equals(attributoVersione)){
				attributoAtteso = SDICostanti.SDI_ATTRIBUTE_VERSION_FATTURA_SEMPLIFICATA_10;
				attributoVersioneNonCorretto = true;
			}
		}
		else if(SDICostanti.SDI_VERSIONE_FATTURA_PA_12.equals(versioneFattura)){
			if(!SDICostanti.SDI_ATTRIBUTE_VERSION_FATTURA_PA_12.equals(attributoVersione)){
				attributoAtteso = SDICostanti.SDI_ATTRIBUTE_VERSION_FATTURA_PA_12;
				attributoVersioneNonCorretto = true;
			}
		}
		else if(SDICostanti.SDI_VERSIONE_FATTURA_PR_12.equals(versioneFattura)){
			if(!SDICostanti.SDI_ATTRIBUTE_VERSION_FATTURA_PR_12.equals(attributoVersione)){
				attributoAtteso = SDICostanti.SDI_ATTRIBUTE_VERSION_FATTURA_PR_12;
				attributoVersioneNonCorretto = true;
			}
		}
		if(attributoVersioneNonCorretto){
			eccezioniValidazione.add(
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Attributo versione presente ["+attributoVersione+
							"] differente da quello atteso ["+attributoAtteso+"] per la versione della fattura ["+versioneFattura+"]",
							!sdiProperties.isEnableValidazioneCampiInterniFattura()));
			if(sdiProperties.isEnableValidazioneCampiInterniFattura()){
				return;	
			}
		}
		
		// Fattura.DatiTrasmissione.IdTrasmittente (la validazione del nome file e' stata effettuata nella validazione sintattica)
		if(validaDatiTrasmissione){
			String nomeFileSdi = busta.getProperty(SDICostanti.SDI_BUSTA_EXT_NOME_FILE);
			String [] nomeFileSdiSplit = nomeFileSdi.split("_");
			String idPaese = nomeFileSdiSplit[0].substring(0, 2);
			String idCodice = nomeFileSdiSplit[0].substring(2);
			if(idPaese.equals(trasmittenteIdPaese)==false){
				eccezioniValidazione.add(
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"NomeFile presente nel messaggio SdI ["+nomeFileSdi+
								"] possiede un idPaese["+idPaese+"] differente da quello presente nella fattura ["+trasmittenteIdPaese+"]",
								!sdiProperties.isEnableValidazioneCampiInterniFattura()));
				if(sdiProperties.isEnableValidazioneCampiInterniFattura()){
					return;	
				}
			}
			if(idCodice.equals(trasmittenteIdCodice)==false){
				eccezioniValidazione.add(
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"NomeFile presente nel messaggio SdI ["+nomeFileSdi+
								"] possiede un idCodice["+idCodice+"] differente da quello presente nella fattura ["+trasmittenteIdCodice+"]",
								!sdiProperties.isEnableValidazioneCampiInterniFattura()));
				if(sdiProperties.isEnableValidazioneCampiInterniFattura()){
					return;	
				}
			}
		}
		busta.addProperty(SDICostanti.SDI_BUSTA_EXT_TRASMITTENTE_ID_PAESE, trasmittenteIdPaese);
		busta.addProperty(SDICostanti.SDI_BUSTA_EXT_TRASMITTENTE_ID_CODICE, trasmittenteIdCodice);
		
		// Fattura.DatiTrasmissione.progressivoInvio (la validazione del nome file e' stata effettuata nella validazione sintattica)
// NOTA: Si tratta di due progressivi differenti.
//		if(sdiProperties.isEnableValidazioneCampiInterniFattura()){
//			String nomeFileSdi = busta.getProperty(SDICostanti.SDI_BUSTA_EXT_NOME_FILE);
//			String [] nomeFileSdiSplit = nomeFileSdi.split("_");
//			String progressivoInvio = nomeFileSdiSplit[1].split("\\.")[0];
//			if(progressivoInvio.equals(trasmissioneProgressivoInvio)==false){
//				eccezioniValidazione.add(
//						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
//								"NomeFile presente nel messaggio SdI ["+nomeFileSdi+
//								"] possiede un progressivoInvio["+progressivoInvio+"] differente da quello presente nella fattura ["+trasmissioneProgressivoInvio+"]"));
//				return;	
//			}
//		}
		busta.addProperty(SDICostanti.SDI_BUSTA_EXT_TRASMISSIONE_PROGRESSIVO_INVIO, trasmissioneProgressivoInvio);
		
		// Fattura.DatiTrasmissione.FormatoTrasmissione
		if(validaDatiTrasmissione){
			if(versioneFattura.equals(trasmissioneFormatoTrasmissione)==false){
				eccezioniValidazione.add(
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Formato di trasmissione indicato nel file Metadati ["+versioneFattura+
								"] differente da quello presente nella fattura ["+trasmissioneFormatoTrasmissione+"]",
								!sdiProperties.isEnableValidazioneCampiInterniFattura()));
				if(sdiProperties.isEnableValidazioneCampiInterniFattura()){
					return;	
				}
			}
		}
		
		// Fattura.DatiTrasmissione.CodiceDestinatario
		if(validaDatiTrasmissione){
			if(codiceDestinatario.equals(trasmissioneCodiceDestinatario)==false){
				eccezioniValidazione.add(
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Codice Destinatario indicato nel file Metadati ["+codiceDestinatario+
								"] differente da quello presente nella fattura ["+trasmissioneCodiceDestinatario+"]",
								!sdiProperties.isEnableValidazioneCampiInterniFattura()));
				if(sdiProperties.isEnableValidazioneCampiInterniFattura()){
					return;	
				}
			}
		}
		
		// Fattura.DatiTrasmissione.PECDestinatario
		busta.addProperty(SDICostanti.SDI_BUSTA_EXT_TRASMISSIONE_PEC_DESTINATARIO, trasmissionePECDestinatario);
		
		// Fattura.CedentePrestatore.DatiAnagrafici.IdFiscaleIVA
		if(cedentePrestatoreIdPaese!=null){
			busta.addProperty(SDICostanti.SDI_BUSTA_EXT_CEDENTE_PRESTATORE_ID_PAESE, cedentePrestatoreIdPaese);
		}
		if(cedentePrestatoreIdCodice!=null){
			busta.addProperty(SDICostanti.SDI_BUSTA_EXT_CEDENTE_PRESTATORE_ID_CODICE, cedentePrestatoreIdCodice);
		}
		
		// Fattura.CedentePrestatore.DatiAnagrafici.CodiceFiscale
		if(cedentePrestatoreCodiceFiscale!=null){
			busta.addProperty(SDICostanti.SDI_BUSTA_EXT_CEDENTE_PRESTATORE_CODICE_FISCALE, cedentePrestatoreCodiceFiscale);
		}
		
		// Fattura.CedentePrestatore.DatiAnagrafici.Anagrafica.[Denominazione,Nome/Cognome] sono in choice
		if(cedentePrestatoreDenominazione!=null){
			busta.addProperty(SDICostanti.SDI_BUSTA_EXT_CEDENTE_PRESTATORE_DENOMINAZIONE, cedentePrestatoreDenominazione);
		}
		if(cedentePrestatoreNome!=null){
			busta.addProperty(SDICostanti.SDI_BUSTA_EXT_CEDENTE_PRESTATORE_NOME, cedentePrestatoreNome);
		}
		if(cedentePrestatoreCognome!=null){
			busta.addProperty(SDICostanti.SDI_BUSTA_EXT_CEDENTE_PRESTATORE_COGNOME, cedentePrestatoreCognome);
		}
		
		// Fattura.CessionarioCommittente.DatiAnagrafici.IdFiscaleIVA
		if(cessionarioCommittenteIdPaese!=null){
			busta.addProperty(SDICostanti.SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_ID_PAESE, cessionarioCommittenteIdPaese);
		}
		if(cessionarioCommittenteIdCodice!=null){
			busta.addProperty(SDICostanti.SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_ID_CODICE, cessionarioCommittenteIdCodice);
		}
		
		// Fattura.CessionarioCommittente.DatiAnagrafici.CodiceFiscale
		if(cessionarioCommittenteCodiceFiscale!=null){
			busta.addProperty(SDICostanti.SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_CODICE_FISCALE, cessionarioCommittenteCodiceFiscale);
		}
		
		// Fattura.CessionarioCommittente.DatiAnagrafici.Anagrafica.[Denominazione,Nome/Cognome] sono in choice
		if(cessionarioCommittenteDenominazione!=null){
			busta.addProperty(SDICostanti.SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_DENOMINAZIONE, cessionarioCommittenteDenominazione);
		}
		if(cessionarioCommittenteNome!=null){
			busta.addProperty(SDICostanti.SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_NOME, cessionarioCommittenteNome);
		}
		if(cessionarioCommittenteCognome!=null){
			busta.addProperty(SDICostanti.SDI_BUSTA_EXT_CESSIONARIO_COMMITTENTE_COGNOME, cessionarioCommittenteCognome);
		}
		
		// Fattura.TerzoIntermediarioOSoggettoEmittente.DatiAnagrafici.IdFiscaleIVA
		if(terzoIdPaese!=null){
			busta.addProperty(SDICostanti.SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_ID_PAESE, terzoIdPaese);
		}
		if(terzoIdCodice!=null){
			busta.addProperty(SDICostanti.SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_ID_CODICE, terzoIdCodice);
		}
		
		// Fattura.TerzoIntermediarioOSoggettoEmittente.DatiAnagrafici.CodiceFiscale
		if(terzoCodiceFiscale!=null){
			busta.addProperty(SDICostanti.SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_CODICE_FISCALE, terzoCodiceFiscale);
		}
		
		// Fattura.TerzoIntermediarioOSoggettoEmittente.DatiAnagrafici.Anagrafica.[Denominazione,Nome/Cognome] sono in choice
		if(terzoDenominazione!=null){
			busta.addProperty(SDICostanti.SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_DENOMINAZIONE, terzoDenominazione);
		}
		if(terzoNome!=null){
			busta.addProperty(SDICostanti.SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_NOME, terzoNome);
		}
		if(terzoCognome!=null){
			busta.addProperty(SDICostanti.SDI_BUSTA_EXT_TERZO_INTERMEDIARIO_O_SOGGETTO_EMITTENTE_COGNOME, terzoCognome);
		}
		
		// Fattura.SoggettoEmittente
		if(soggettoEmittente!=null){
			busta.addProperty(SDICostanti.SDI_BUSTA_EXT_SOGGETTO_EMITTENTE, soggettoEmittente);
		}

	}
	
}
