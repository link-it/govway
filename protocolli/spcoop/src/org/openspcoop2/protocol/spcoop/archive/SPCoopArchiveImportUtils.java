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

package org.openspcoop2.protocol.spcoop.archive;

import it.gov.spcoop.sica.dao.AccordoServizioComposto;
import it.gov.spcoop.sica.dao.AccordoServizioParteComune;
import it.gov.spcoop.sica.dao.AccordoServizioParteSpecifica;
import it.gov.spcoop.sica.manifest.ElencoPartecipanti;
import it.gov.spcoop.sica.manifest.ElencoServiziComponenti;
import it.gov.spcoop.sica.manifest.ElencoServiziComposti;
import it.gov.spcoop.sica.manifest.driver.TipiDocumentoConversazione;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.IRegistryReader;
import org.openspcoop2.protocol.sdk.archive.RegistryNotFound;
import org.openspcoop2.protocol.spcoop.sica.SICAtoOpenSPCoopContext;
import org.openspcoop2.protocol.spcoop.sica.SICAtoOpenSPCoopUtilities;

/**
 * SPCoopArchiveImportUtils 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopArchiveImportUtils {


	/* ------------ SetCodiceIPA -------------------------- */
	
	public static void setIDSoggettoFromCodiceIPA(AccordoServizioParteComune aspc,SICAtoOpenSPCoopContext contextSICA,IRegistryReader registryReader) throws Exception{
		
		// Imposto CodiceIPA memorizzato per Referente
		if(aspc.getManifesto()!=null && aspc.getManifesto().getParteComune()!=null &&
				aspc.getManifesto().getParteComune().getPubblicatore()!=null){
			String codiceIPAReferente = SICAtoOpenSPCoopUtilities.removeURI_IDSoggettoSica(aspc.getManifesto().getParteComune().getPubblicatore());
			IDSoggetto soggettoReferente =  null;
			try{
				soggettoReferente = registryReader.getIdSoggettoByCodiceIPA(codiceIPAReferente);
				contextSICA.addMappingSoggettoSPCoopToCodiceIPA(soggettoReferente, codiceIPAReferente);
			}catch(RegistryNotFound dNotF){
				throw new ProtocolException("Il soggetto referente con Codice IPA ["+codiceIPAReferente+"] non esiste");
			}
		}
	}
	
	public static void setIDSoggettoFromCodiceIPA(AccordoServizioComposto asc,SICAtoOpenSPCoopContext contextSICA,IRegistryReader registryReader) throws Exception{
		
		// Imposto CodiceIPA memorizzato per Referente
		if(asc.getManifesto()!=null &&
				asc.getManifesto().getPubblicatore()!=null){
			String codiceIPAReferente = SICAtoOpenSPCoopUtilities.removeURI_IDSoggettoSica(asc.getManifesto().getPubblicatore());
			IDSoggetto soggettoReferente =  null;
			try{
				soggettoReferente = registryReader.getIdSoggettoByCodiceIPA(codiceIPAReferente);
				contextSICA.addMappingSoggettoSPCoopToCodiceIPA(soggettoReferente, codiceIPAReferente);
			}catch(RegistryNotFound dNotF){
				throw new ProtocolException("Il soggetto referente con Codice IPA ["+codiceIPAReferente+"] non esiste");
			}
		}
		// Imposto CodiceIPA memorizzato per erogatori servizi componenti
		if(asc.getManifesto()!=null && asc.getManifesto().getServiziComponenti()!=null ){
			ElencoServiziComponenti sComponenti = asc.getManifesto().getServiziComponenti();
			for(int i=0; i<sComponenti.sizeServizioComponenteList(); i++){
				String servComponente = sComponenti.getServizioComponente(i);
				String codiceIPASoggettoErogatoreServizioComponente = SICAtoOpenSPCoopUtilities.readDNSoggettoFromUriAccordo(servComponente);
				IDSoggetto soggettoErogatoreServizioComponente = null;
				try{
					soggettoErogatoreServizioComponente = registryReader.getIdSoggettoByCodiceIPA(codiceIPASoggettoErogatoreServizioComponente);
					contextSICA.addMappingSoggettoSPCoopToCodiceIPA(soggettoErogatoreServizioComponente, codiceIPASoggettoErogatoreServizioComponente);
				}catch(RegistryNotFound dNotF){
					throw new ProtocolException("Il soggetto erogatore con Codice IPA ["+codiceIPASoggettoErogatoreServizioComponente+"] non esiste (servizio componente: "+servComponente+")");
				}
			}
		}
	}
	
	public static void setIDServizioFromURI_APS(AccordoServizioComposto asc,SICAtoOpenSPCoopContext contextSICA,IRegistryReader registryReader) throws Exception{
		
		// Imposto ServizioSPCoop memorizzato per servizi componenti
		if(asc.getManifesto()!=null && asc.getManifesto().getServiziComponenti()!=null ){
			ElencoServiziComponenti sComponenti = asc.getManifesto().getServiziComponenti();
			for(int i=0; i<sComponenti.sizeServizioComponenteList(); i++){
				String servComponente = sComponenti.getServizioComponente(i);
				IDAccordo idAccordoServizioParteSpecifica = null;
				try{
					idAccordoServizioParteSpecifica = SICAtoOpenSPCoopUtilities.idAccordoServizioParteSpecifica_sicaToOpenspcoop(servComponente, contextSICA);
					IDServizio idServizio = registryReader.convertToIDServizio(idAccordoServizioParteSpecifica);
					contextSICA.addMappingServizioToUriAPS(idServizio, idAccordoServizioParteSpecifica);
				}catch(RegistryNotFound dNotF){
					if(idAccordoServizioParteSpecifica!=null){
						throw new ProtocolException("L'accordo di servizio parte specifica ["+IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordoServizioParteSpecifica)
								+"] non esiste (servizio componente: "+servComponente+").");
					}
					else{
						throw new ProtocolException("L'accordo di servizio parte specifica relativo al servizio componente ("+servComponente+") non esiste");
					}
				}
			}
		}
	}
	
	public static void setIDSoggettoFromCodiceIPA(it.gov.spcoop.sica.dao.AccordoCooperazione ac,SICAtoOpenSPCoopContext contextSICA,IRegistryReader registryReader) throws Exception{
		 // Imposto CodiceIPA memorizzato per Coordinatore
		 if(ac.getManifesto()!=null && ac.getManifesto().getCoordinatore()!=null){
			 String codiceIPACoordinatore = SICAtoOpenSPCoopUtilities.removeURI_IDSoggettoSica(ac.getManifesto().getCoordinatore());
			 IDSoggetto soggettoCoordinatore =  null;
			 try{
				 soggettoCoordinatore = registryReader.getIdSoggettoByCodiceIPA(codiceIPACoordinatore);
				 contextSICA.addMappingSoggettoSPCoopToCodiceIPA(soggettoCoordinatore, codiceIPACoordinatore);
			 }catch(RegistryNotFound dNotF){
				 throw new ProtocolException("Il soggetto coordinatore con Codice IPA ["+codiceIPACoordinatore+"] non esiste");
			 }
		 }
		 // Imposto CodiceIPA per Partecipanti
		 if(ac.getManifesto()!=null && ac.getManifesto().getElencoPartecipanti()!=null ){
			 ElencoPartecipanti elencoPartecipanti = ac.getManifesto().getElencoPartecipanti();
			 for(int i=0; i<elencoPartecipanti.sizePartecipanteList(); i++){
				 String codiceIPASoggettoPartecipante = SICAtoOpenSPCoopUtilities.removeURI_IDSoggettoSica(elencoPartecipanti.getPartecipante(i));
				 IDSoggetto soggettoPartecipante =  null;
				 try{
					 soggettoPartecipante = registryReader.getIdSoggettoByCodiceIPA(codiceIPASoggettoPartecipante);
					 contextSICA.addMappingSoggettoSPCoopToCodiceIPA(soggettoPartecipante, codiceIPASoggettoPartecipante);
				 }catch(RegistryNotFound dNotF){
					 throw new ProtocolException("Il soggetto partecipante con Codice IPA ["+codiceIPASoggettoPartecipante+"] non esiste");
				 }
			 }
		 }
		 // Imposto CodiceIPA per erogatori servizi composti
		 if(ac.getManifesto()!=null && ac.getManifesto().getServiziComposti()!=null ){
			 ElencoServiziComposti sComposti = ac.getManifesto().getServiziComposti();
			 for(int i=0; i<sComposti.sizeServizioCompostoList(); i++){
				 String uriServizioComposto = sComposti.getServizioComposto(i);
				 String codiceIPASoggettoErogatoreServizioComposto = SICAtoOpenSPCoopUtilities.readDNSoggettoFromUriAccordo(uriServizioComposto);
				 IDSoggetto soggettoErogatoreServizioComposto = null;
				 try{
					 soggettoErogatoreServizioComposto = registryReader.getIdSoggettoByCodiceIPA(codiceIPASoggettoErogatoreServizioComposto);
					 contextSICA.addMappingSoggettoSPCoopToCodiceIPA(soggettoErogatoreServizioComposto, codiceIPASoggettoErogatoreServizioComposto);
				 }catch(RegistryNotFound dNotF){
					 throw new ProtocolException("Il soggetto erogatore con Codice IPA ["+codiceIPASoggettoErogatoreServizioComposto+"] non esiste (servizio composto: "+uriServizioComposto+")");
				 }
			 }
		 }
	}
	
	public static void setIDSoggettoFromCodiceIPA(AccordoServizioParteSpecifica asps,SICAtoOpenSPCoopContext contextSICA,IRegistryReader registryReader) throws Exception{
		// Imposto CodiceIPA memorizzato per Erogatore
		if(asps.getManifesto()!=null && asps.getManifesto().getParteSpecifica()!=null &&
				asps.getManifesto().getParteSpecifica().getErogatore()!=null){
			 String codiceIPAErogatore = SICAtoOpenSPCoopUtilities.removeURI_IDSoggettoSica(asps.getManifesto().getParteSpecifica().getErogatore());
			 IDSoggetto soggettoErogatore =  null;
			 try{
				 soggettoErogatore = registryReader.getIdSoggettoByCodiceIPA(codiceIPAErogatore);
				 contextSICA.addMappingSoggettoSPCoopToCodiceIPA(soggettoErogatore, codiceIPAErogatore);
			 }catch(RegistryNotFound dNotF){
				 throw new ProtocolException("Il soggetto erogatore con Codice IPA ["+codiceIPAErogatore+"] non esiste");
			 }
		}
		// Imposto CodiceIPA memorizzato per referente Accordo di Servizio Parte Comune
		if(asps.getManifesto()!=null && asps.getManifesto().getParteSpecifica()!=null &&
				asps.getManifesto().getParteSpecifica().getRiferimentoParteComune()!=null){
			 String uriRiferimentoParteComune = asps.getManifesto().getParteSpecifica().getRiferimentoParteComune();
			 String codiceIPASoggettoReferenteParteComune = SICAtoOpenSPCoopUtilities.readDNSoggettoFromUriAccordo(uriRiferimentoParteComune);
			 IDSoggetto soggettoReferenteParteComune = null;
			 try{
				 soggettoReferenteParteComune = registryReader.getIdSoggettoByCodiceIPA(codiceIPASoggettoReferenteParteComune);
				 contextSICA.addMappingSoggettoSPCoopToCodiceIPA(soggettoReferenteParteComune, codiceIPASoggettoReferenteParteComune);
			 }catch(RegistryNotFound dNotF){
				 throw new ProtocolException("Il soggetto referente con Codice IPA ["+codiceIPASoggettoReferenteParteComune+"] non esiste (parte comune: "+uriRiferimentoParteComune+")");
			 }
		}

	}
	
	
	
	
	
	/* ------------ Validazione -------------------------- */
	
	public static void validazioneContenuti(AccordoServizioParteComune aspc) throws Exception{
		
		if(aspc.getInterfacciaConcettuale()==null){
			throw new Exception("WSDL Concettuale non presente");
		}
		if(aspc.getInterfacciaLogicaLatoErogatore()==null){
			throw new Exception("WSDL Logico Erogatore non presente");
		}
		
		byte[] wsdlConcettuale = aspc.getInterfacciaConcettuale().getContenuto();
		if(wsdlConcettuale==null){
			throw new Exception("WSDL Concettuale non presente nell'archivio");
		}
		if(SICAtoOpenSPCoopUtilities.isWsdlEmpty(wsdlConcettuale)){
			wsdlConcettuale=null;
		}
		aspc.getInterfacciaConcettuale().setContenuto(wsdlConcettuale);
					
		byte[] wsdlLogicoErogatore = aspc.getInterfacciaLogicaLatoErogatore().getContenuto();
		if(wsdlLogicoErogatore==null){
			throw new Exception("WSDL Logico Erogatore non presente nell'archivio");
		}
		if(SICAtoOpenSPCoopUtilities.isWsdlEmpty(wsdlLogicoErogatore)){
			wsdlLogicoErogatore=null;
		}
		aspc.getInterfacciaLogicaLatoErogatore().setContenuto(wsdlLogicoErogatore);
		
		byte[] wsdlLogicoFruitore = null;
		if(aspc.getInterfacciaLogicaLatoFruitore()!=null){
			wsdlLogicoFruitore = aspc.getInterfacciaLogicaLatoFruitore().getContenuto();
			if(wsdlLogicoFruitore==null){
				throw new Exception("WSDL Logico Fruitore non presente nell'archivio");
			}
			if(SICAtoOpenSPCoopUtilities.isWsdlEmpty(wsdlLogicoFruitore)){
				wsdlLogicoFruitore=null;
			}
		}
		if(aspc.getInterfacciaLogicaLatoFruitore()!=null)
			aspc.getInterfacciaLogicaLatoFruitore().setContenuto(wsdlLogicoFruitore);
		
		byte[] wsblConcettuale = null;
		if(aspc.getConversazioneConcettuale()!=null && 
				TipiDocumentoConversazione.WSBL.toString().equals(aspc.getConversazioneConcettuale().getTipo())
				){
			wsblConcettuale = aspc.getConversazioneConcettuale().getContenuto();	
			if(wsblConcettuale==null){
				throw new Exception("WSBL Concettuale non presente nell'archivio");
			}
		}
		if(aspc.getConversazioneConcettuale()!=null)
			aspc.getConversazioneConcettuale().setContenuto(wsblConcettuale);
		
		byte[] wsblLogicoErogatore = null;
		if(aspc.getConversazioneLogicaErogatore()!=null && 
				TipiDocumentoConversazione.WSBL.toString().equals(aspc.getConversazioneLogicaErogatore().getTipo())
				){
			wsblLogicoErogatore = aspc.getConversazioneLogicaErogatore().getContenuto();		
			if(wsblLogicoErogatore==null){
				throw new Exception("WSBL Logico Erogatore non presente nell'archivio");
			}
		}
		if(aspc.getConversazioneLogicaErogatore()!=null)
			aspc.getConversazioneLogicaErogatore().setContenuto(wsblLogicoErogatore);
		
		byte[] wsblLogicoFruitore = null;
		if(aspc.getConversazioneLogicaFruitore()!=null && 
				TipiDocumentoConversazione.WSBL.toString().equals(aspc.getConversazioneLogicaFruitore().getTipo())
				){
			wsblLogicoFruitore = aspc.getConversazioneLogicaFruitore().getContenuto();	
			if(wsblLogicoFruitore==null){
				throw new Exception("WSBL Logico Fruitore non presente nell'archivio");
			}
		}
		if(aspc.getConversazioneLogicaFruitore()!=null)
			aspc.getConversazioneLogicaFruitore().setContenuto(wsblLogicoFruitore);

		// il resto della validazione viene effettuata di base dal prodotto
		
	}
	
	public static void validazioneContenuti(AccordoServizioComposto asc) throws Exception{
		
		if(asc.getInterfacciaConcettuale()==null){
			throw new Exception("WSDL Concettuale non presente");
		}
		if(asc.getInterfacciaLogicaLatoErogatore()==null){
			throw new Exception("WSDL Logico Erogatore non presente");
		}
		
		byte[] wsdlConcettuale = asc.getInterfacciaConcettuale().getContenuto();
		if(wsdlConcettuale==null){
			throw new Exception("WSDL Concettuale non presente nell'archivio");
		}
		if(SICAtoOpenSPCoopUtilities.isWsdlEmpty(wsdlConcettuale)){
			wsdlConcettuale=null;
		}
		asc.getInterfacciaConcettuale().setContenuto(wsdlConcettuale);
					
		byte[] wsdlLogicoErogatore = asc.getInterfacciaLogicaLatoErogatore().getContenuto();
		if(wsdlLogicoErogatore==null){
			throw new Exception("WSDL Logico Erogatore non presente nell'archivio");
		}
		if(SICAtoOpenSPCoopUtilities.isWsdlEmpty(wsdlLogicoErogatore)){
			wsdlLogicoErogatore=null;
		}
		asc.getInterfacciaLogicaLatoErogatore().setContenuto(wsdlLogicoErogatore);
		
		byte[] wsdlLogicoFruitore = null;
		if(asc.getInterfacciaLogicaLatoFruitore()!=null){
			wsdlLogicoFruitore = asc.getInterfacciaLogicaLatoFruitore().getContenuto();
			if(wsdlLogicoFruitore==null){
				throw new Exception("WSDL Logico Fruitore non presente nell'archivio");
			}
			if(SICAtoOpenSPCoopUtilities.isWsdlEmpty(wsdlLogicoFruitore)){
				wsdlLogicoFruitore=null;
			}
		}
		if(asc.getInterfacciaLogicaLatoFruitore()!=null)
			asc.getInterfacciaLogicaLatoFruitore().setContenuto(wsdlLogicoFruitore);
		
		byte[] wsblConcettuale = null;
		if(asc.getConversazioneConcettuale()!=null && 
				TipiDocumentoConversazione.WSBL.toString().equals(asc.getConversazioneConcettuale().getTipo())
				){
			wsblConcettuale = asc.getConversazioneConcettuale().getContenuto();	
			if(wsblConcettuale==null){
				throw new Exception("WSBL Concettuale non presente nell'archivio");
			}
		}
		if(asc.getConversazioneConcettuale()!=null)
			asc.getConversazioneConcettuale().setContenuto(wsblConcettuale);
		
		byte[] wsblLogicoErogatore = null;
		if(asc.getConversazioneLogicaErogatore()!=null && 
				TipiDocumentoConversazione.WSBL.toString().equals(asc.getConversazioneLogicaErogatore().getTipo())
				){
			wsblLogicoErogatore = asc.getConversazioneLogicaErogatore().getContenuto();		
			if(wsblLogicoErogatore==null){
				throw new Exception("WSBL Logico Erogatore non presente nell'archivio");
			}
		}
		if(asc.getConversazioneLogicaErogatore()!=null)
			asc.getConversazioneLogicaErogatore().setContenuto(wsblLogicoErogatore);
		
		byte[] wsblLogicoFruitore = null;
		if(asc.getConversazioneLogicaFruitore()!=null && 
				TipiDocumentoConversazione.WSBL.toString().equals(asc.getConversazioneLogicaFruitore().getTipo())
				){
			wsblLogicoFruitore = asc.getConversazioneLogicaFruitore().getContenuto();	
			if(wsblLogicoFruitore==null){
				throw new Exception("WSBL Logico Fruitore non presente nell'archivio");
			}
		}
		if(asc.getConversazioneLogicaFruitore()!=null)
			asc.getConversazioneLogicaFruitore().setContenuto(wsblLogicoFruitore);
		
		// il resto della validazione viene effettuata di base dal prodotto
	}
	
	public static void validazioneContenuti(AccordoServizioParteSpecifica aps) throws Exception{
		
		if(aps.getPortiAccessoErogatore()==null && aps.getPortiAccessoFruitore()==null){
			throw new Exception("Nessun WSDL Implementativo presente");
		}
						
		//implementativo erogatore
		if(aps.getPortiAccessoErogatore()!=null){
			byte[]  wsdlImplementativo = aps.getPortiAccessoErogatore().getContenuto();
			if(wsdlImplementativo==null){
				throw new Exception("WSDL Implementativo Erogatore non presente nell'archivio");
			}
			if(SICAtoOpenSPCoopUtilities.isWsdlEmpty(wsdlImplementativo)){
				wsdlImplementativo=null;
			}
			aps.getPortiAccessoErogatore().setContenuto(wsdlImplementativo);
		}
		
		//implementativo fruitore
		if(aps.getPortiAccessoFruitore()!=null){
			byte[]  wsdlImplementativo = aps.getPortiAccessoFruitore().getContenuto();
			if(wsdlImplementativo==null){
				throw new Exception("WSDL Implementativo Fruitore non presente nell'archivio");
			}
			if(SICAtoOpenSPCoopUtilities.isWsdlEmpty(wsdlImplementativo)){
				wsdlImplementativo=null;
			}
			aps.getPortiAccessoFruitore().setContenuto(wsdlImplementativo);
		}

	}
	
	
	
	
	
	
	
	
	
	/* -------------------- InfoServices -------------------------------- */
	
	// Metodi oramai gestiti direttamente dall'import
	
	@Deprecated
	public static boolean isInfoEGovPresenti(AccordoServizioParteComune aspc){
		boolean infoEgovPresenti = false;
		if(aspc!=null){
			it.gov.spcoop.sica.dao.Documento docSICA = null;
			for(int j=0; j<aspc.sizeAllegati(); j++){
				docSICA = aspc.getAllegato(j);
				if(it.gov.spcoop.sica.wscp.driver.XMLUtils.isProfiloCollaborazioneEGOV(docSICA.getContenuto())){
					infoEgovPresenti = true;
					break;
				}
				else if(it.cnipa.collprofiles.driver.XMLUtils.isProfiloCollaborazioneEGOV(docSICA.getContenuto())){
					infoEgovPresenti = true;
					break;
				}
			}
			if(infoEgovPresenti==false){
				for(int j=0; j<aspc.sizeSpecificheSemiformali(); j++){
					docSICA = aspc.getSpecificaSemiformale(j);
					if(it.gov.spcoop.sica.wscp.driver.XMLUtils.isProfiloCollaborazioneEGOV(docSICA.getContenuto())){
						infoEgovPresenti = true;
						break;
					}
					else if(it.cnipa.collprofiles.driver.XMLUtils.isProfiloCollaborazioneEGOV(docSICA.getContenuto())){
						infoEgovPresenti = true;
						break;
					}
				}
			}
		}
		return infoEgovPresenti;
	}
	
	@Deprecated
	public static boolean isInfoEGovPresenti(AccordoServizioComposto asc){
		boolean infoEgovPresenti = false;
		if(asc!=null){
			it.gov.spcoop.sica.dao.Documento docSICA = null;
			for(int j=0; j<asc.sizeAllegati(); j++){
				docSICA = asc.getAllegato(j);
				if(it.gov.spcoop.sica.wscp.driver.XMLUtils.isProfiloCollaborazioneEGOV(docSICA.getContenuto())){
					infoEgovPresenti = true;
					break;
				}
				else if(it.cnipa.collprofiles.driver.XMLUtils.isProfiloCollaborazioneEGOV(docSICA.getContenuto())){
					infoEgovPresenti = true;
					break;
				}
			}
			if(infoEgovPresenti==false){
				for(int j=0; j<asc.sizeSpecificheSemiformali(); j++){
					docSICA = asc.getSpecificaSemiformale(j);
					if(it.gov.spcoop.sica.wscp.driver.XMLUtils.isProfiloCollaborazioneEGOV(docSICA.getContenuto())){
						infoEgovPresenti = true;
						break;
					}
					else if(it.cnipa.collprofiles.driver.XMLUtils.isProfiloCollaborazioneEGOV(docSICA.getContenuto())){
						infoEgovPresenti = true;
						break;
					}
				}
			}
		}
		return infoEgovPresenti;
	}
	
}
