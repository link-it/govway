/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.spcoop.archive;

import it.gov.spcoop.sica.dao.driver.XMLUtils;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoCooperazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioComposto;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteComune;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteSpecifica;
import org.openspcoop2.protocol.sdk.archive.ArchiveFruitore;
import org.openspcoop2.protocol.sdk.archive.ArchiveIdCorrelazione;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaApplicativa;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaDelegata;
import org.openspcoop2.protocol.sdk.archive.ArchiveServizioApplicativo;
import org.openspcoop2.protocol.sdk.config.Implementation;
import org.openspcoop2.protocol.sdk.config.Subscription;
import org.openspcoop2.protocol.sdk.registry.FiltroRicercaSoggetti;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.protocol.spcoop.sica.SICAtoOpenSPCoopContext;
import org.openspcoop2.protocol.spcoop.sica.SICAtoOpenSPCoopUtilities;

/**
 * SPCoopArchiveImport 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopArchiveImport {

	private IProtocolFactory<?> protocolFactory = null;
	private Logger logger = null;
	public SPCoopArchiveImport(IProtocolFactory<?> protocolFactory){
		this.protocolFactory = protocolFactory;
		this.logger = this.protocolFactory.getLogger();
	}
	
	public Archive buildAccordoServizioParteComune(byte[]archive,
			IRegistryReader registryReader,boolean validationDocuments) throws ProtocolException, SPCoopConvertToPackageCNIPAException{
		
		try{
		
			SICAtoOpenSPCoopContext sicaContext = new SICAtoOpenSPCoopContext();
			
			
			// *** Trasformazione in package CNIPA ***
			XMLUtils daoUtilities = new XMLUtils(sicaContext,this.logger);
			it.gov.spcoop.sica.dao.AccordoServizioParteComune aspcSICA = null;
			try{
				aspcSICA = daoUtilities.getAccordoServizioParteComune(archive);
			}catch(Exception e){
				throw new SPCoopConvertToPackageCNIPAException("Conversione dell'archivio in package CNIPA non riuscita: "+e.getMessage(),e);
			}
			
								
			// *** Verifica informazioni presenti nel package ***
			if(aspcSICA.getManifesto().getNome()==null){
				throw new ProtocolException("Riscontrato un archivio che possiede un manifesto senza nome");
			}
			
			// soggetto referente
			String soggettoReferente = null;
			if(aspcSICA.getManifesto().getParteComune().getPubblicatore()!=null) {
				soggettoReferente = aspcSICA.getManifesto().getParteComune().getPubblicatore().toString();
			}
			if(soggettoReferente!=null){
				String codiceIPA = SICAtoOpenSPCoopUtilities.removeURI_IDSoggettoSica(soggettoReferente);
				if(registryReader.existsSoggettoByCodiceIPA(codiceIPA)==false){
					throw new ProtocolException("Il soggetto referente con Codice IPA ["+codiceIPA+"] non esiste.");
				}
			}
			
			
			// *** Verifica documenti wsdl/wsbl presenti all'interno del package ***
			if(validationDocuments){
				SPCoopArchiveImportUtils.validazioneContenuti(aspcSICA);
			}
			
						
			// *** impostazione mapping soggetto con codice IPA ***
			SPCoopArchiveImportUtils.setIDSoggettoFromCodiceIPA(aspcSICA, sicaContext, registryReader);
			
			
			// *** Trasformazione in oggetto OpenSPCoop2 ***
			AccordoServizioParteComune aspcOpenSPCoop2 = null;
			try{
				aspcOpenSPCoop2 = SICAtoOpenSPCoopUtilities.accordoServizioParteComune_sicaToOpenspcoop(registryReader, aspcSICA, sicaContext, this.logger);
				
				Archive archiveObject = new Archive();
				
				ArchiveIdCorrelazione idCorrelazione = new ArchiveIdCorrelazione("CnipaPackageASPC");
				
				ArchiveAccordoServizioParteComune archiveASPC = new ArchiveAccordoServizioParteComune(aspcOpenSPCoop2,idCorrelazione,true);
				archiveObject.getAccordiServizioParteComune().add(archiveASPC);

				return archiveObject;
				
			}catch(Exception e){
				throw new ProtocolException("Conversione dell'archivio, da formato CNIPA a formato OpenSPCoop2, non riuscita: "+e.getMessage(),e);
			}
			
		}catch(SPCoopConvertToPackageCNIPAException convert){
			throw convert;
		}catch(Exception e){
			throw new ProtocolException("Conversione archivio non riuscita: "+e.getMessage(),e);
		}
		
	} 
	
	public Archive buildAccordoServizioComposto(byte[]archive,
			IRegistryReader registryReader, boolean validationDocuments) throws ProtocolException, SPCoopConvertToPackageCNIPAException{
		
		try{
		
			SICAtoOpenSPCoopContext sicaContext = new SICAtoOpenSPCoopContext();
			
			
			// *** Trasformazione in package CNIPA ***
			XMLUtils daoUtilities = new XMLUtils(sicaContext,this.logger);
			it.gov.spcoop.sica.dao.AccordoServizioComposto asCompostoSICA = null;
			try{
				asCompostoSICA = daoUtilities.getAccordoServizioComposto(archive);
			}catch(Exception e){
				throw new SPCoopConvertToPackageCNIPAException("Conversione dell'archivio in package CNIPA non riuscita: "+e.getMessage(),e);
			}
			
			
			// *** Verifica informazioni presenti nel package ***
			if(asCompostoSICA.getManifesto().getNome()==null){
				throw new ProtocolException("Riscontrato un archivio che possiede un manifesto senza nome");
			}			
			
			// soggetto referente
			String soggettoReferente = null;
			if(asCompostoSICA.getManifesto().getPubblicatore()!=null) {
				soggettoReferente = asCompostoSICA.getManifesto().getPubblicatore().toString();
			}
			if(soggettoReferente!=null){
				String codiceIPA = SICAtoOpenSPCoopUtilities.removeURI_IDSoggettoSica(soggettoReferente);
				if(registryReader.existsSoggettoByCodiceIPA(codiceIPA)==false){
					throw new ProtocolException("Il soggetto referente con Codice IPA ["+codiceIPA+"] non esiste.");
				}
			}
			
			
			// *** Verifica documenti wsdl/wsbl presenti all'interno del package ***
			if(validationDocuments){
				SPCoopArchiveImportUtils.validazioneContenuti(asCompostoSICA);
			}
			
			
			// *** impostazione mapping soggetto con codice IPA ***
			SPCoopArchiveImportUtils.setIDSoggettoFromCodiceIPA(asCompostoSICA, sicaContext, registryReader);
			SPCoopArchiveImportUtils.setIDServizioFromURI_APS(asCompostoSICA, sicaContext, registryReader);
			
						
			// *** Trasformazione in oggetto OpenSPCoop2 ***
			AccordoServizioParteComune asCompostoOpenSPCoop2 = null;
			try{
				asCompostoOpenSPCoop2 = SICAtoOpenSPCoopUtilities.accordoServizioComposto_sicaToOpenspcoop(registryReader, asCompostoSICA, sicaContext, this.logger);
				
				Archive archiveObject = new Archive();
				
				ArchiveIdCorrelazione idCorrelazione = new ArchiveIdCorrelazione("CnipaPackageASC");
				
				ArchiveAccordoServizioComposto archiveASC = new ArchiveAccordoServizioComposto(asCompostoOpenSPCoop2,idCorrelazione,true);
				archiveObject.getAccordiServizioComposto().add(archiveASC);
				
				return archiveObject;
				
			}catch(Exception e){
				throw new ProtocolException("Conversione dell'archivio, da formato CNIPA a formato OpenSPCoop2, non riuscita: "+e.getMessage(),e);
			}
			
		}catch(SPCoopConvertToPackageCNIPAException convert){
			throw convert;
		}catch(Exception e){
			throw new ProtocolException("Conversione archivio non riuscita: "+e.getMessage(),e);
		}
		
	} 
	
	
	public Archive buildAccordoServizioParteSpecifica(byte[]archive,
			IRegistryReader registryReader, boolean validationDocuments) throws ProtocolException, SPCoopConvertToPackageCNIPAException{
		
		try{
			
			SICAtoOpenSPCoopContext sicaContext = new SICAtoOpenSPCoopContext();
			
			
			// *** Trasformazione in package CNIPA ***
			XMLUtils daoUtilities = new XMLUtils(sicaContext,this.logger);
			it.gov.spcoop.sica.dao.AccordoServizioParteSpecifica aspsSICA = null;
			try{
				aspsSICA = daoUtilities.getAccordoServizioParteSpecifica(archive);
			}catch(Exception e){
				throw new SPCoopConvertToPackageCNIPAException("Conversione dell'archivio in package CNIPA non riuscita: "+e.getMessage(),e);
			}
			
			
			// *** Verifica informazioni presenti nel package ***
			if(aspsSICA.getManifesto().getNome()==null){
				throw new ProtocolException("Riscontrato un archivio che possiede un manifesto senza nome");
			}
			
			// soggetto erogatore
			String soggettoErogatore = null;
			if(aspsSICA.getManifesto().getParteSpecifica().getErogatore()!=null) {
				soggettoErogatore = aspsSICA.getManifesto().getParteSpecifica().getErogatore().toString();
			}
			if(soggettoErogatore!=null){
				String codiceIPA = SICAtoOpenSPCoopUtilities.removeURI_IDSoggettoSica(soggettoErogatore);
				if(registryReader.existsSoggettoByCodiceIPA(codiceIPA)==false){
					throw new ProtocolException("Il soggetto referente con Codice IPA ["+codiceIPA+"] non esiste.");
				}
			}
				
			
			// *** Verifica documenti presenti all'interno del package ***
			if(validationDocuments){
				SPCoopArchiveImportUtils.validazioneContenuti(aspsSICA);
			}
			
						
			// *** impostazione mapping soggetto con codice IPA ***
			SPCoopArchiveImportUtils.setIDSoggettoFromCodiceIPA(aspsSICA, sicaContext, registryReader);
			
			
			// *** Trasformazione in oggetto OpenSPCoop2 ***
			try{
				AccordoServizioParteSpecifica aspsOpenSPCoop2 = SICAtoOpenSPCoopUtilities.accordoServizioParteSpecifica_sicaToOpenspcoop(registryReader, aspsSICA, sicaContext, this.logger);
				
				Archive archiveObject = new Archive();
				
				ArchiveIdCorrelazione idCorrelazione = new ArchiveIdCorrelazione("CnipaPackageASPS");
				
				ArchiveAccordoServizioParteSpecifica archiveASPS = new ArchiveAccordoServizioParteSpecifica(aspsOpenSPCoop2,idCorrelazione, true);
				archiveObject.getAccordiServizioParteSpecifica().add(archiveASPS);
								
				return archiveObject;
				
			}catch(Exception e){
				throw new ProtocolException("Conversione dell'archivio, da formato CNIPA a formato OpenSPCoop2, non riuscita: "+e.getMessage(),e);
			}

				
		}catch(SPCoopConvertToPackageCNIPAException convert){
			throw convert;
		}catch(Exception e){
			throw new ProtocolException("Conversione archivio non riuscita: "+e.getMessage(),e);
		}
		
	} 
	
	public void finalizeAccordoServizioParteSpecifica(Archive archiveObject,
			IRegistryReader registryReader, boolean validationDocuments) throws ProtocolException, SPCoopConvertToPackageCNIPAException{
		
		try{
			AccordoServizioParteSpecifica aspsOpenSPCoop2 = null;
			ArchiveIdCorrelazione idCorrelazione = null;
			if(archiveObject.getAccordiServizioParteSpecifica()!=null && archiveObject.getAccordiServizioParteSpecifica().size()>0) {
				aspsOpenSPCoop2 = archiveObject.getAccordiServizioParteSpecifica().get(0).getAccordoServizioParteSpecifica();
				idCorrelazione = archiveObject.getAccordiServizioParteSpecifica().get(0).getIdCorrelazione();
			}
			else {
				return;
			}
			
			
			boolean informazioniComplete = true;
			if(aspsOpenSPCoop2.getTipoSoggettoErogatore()==null || aspsOpenSPCoop2.getNomeSoggettoErogatore()==null) {
				informazioniComplete = false;
			}
			if(aspsOpenSPCoop2.getVersione()==null) {
				informazioniComplete = false;
			}
			if(aspsOpenSPCoop2.getAccordoServizioParteComune()==null) {
				informazioniComplete = false;
			}
			
			ArchiveAccordoServizioParteSpecifica archiveASPS = new ArchiveAccordoServizioParteSpecifica(aspsOpenSPCoop2,idCorrelazione,!informazioniComplete);
			
			if(informazioniComplete) {
			
				// Devo creare una erogazione o fruizione
				IDServizio idServizio = archiveASPS.getIdAccordoServizioParteSpecifica();
				IDSoggetto idSoggettoErogatore = archiveASPS.getIdSoggettoErogatore();
				Soggetto soggetto = registryReader.getSoggetto(idSoggettoErogatore);
				String portaDominio = soggetto.getPortaDominio();
				boolean operativo = false;
				List<String> pddOperative = null;
				try {
					pddOperative = registryReader.findIdPorteDominio(true);
				}catch(RegistryNotFound notFound) {}
				if(portaDominio!=null) {
					if(pddOperative!=null && !pddOperative.isEmpty()) {
						for (String pdd : pddOperative) {
							if(portaDominio.equals(pdd)) {
								operativo = true;
								break;
							}
						}
					}
				}
				
				if(operativo) {
					
					// creo Porta Applicativa
					
					Implementation implementationDefault = this.protocolFactory.createProtocolIntegrationConfiguration().
							createDefaultImplementation(ServiceBinding.SOAP, idServizio);
					
					PortaApplicativa portaApplicativa = implementationDefault.getPortaApplicativa();
					portaApplicativa.setIdSoggetto(soggetto.getId());
					portaApplicativa.setStato(StatoFunzionalita.DISABILITATO);
					// Per non creare un buco di sicurezza per default, abilito l'autorizzazione dei soggetti
					portaApplicativa.setAutenticazione(TipoAutenticazione.DISABILITATO.getValue());
					portaApplicativa.setAutorizzazione(TipoAutorizzazione.AUTHENTICATED.getValue());
					
					// Viene creata in automatico
					MappingErogazionePortaApplicativa mappingErogazione = implementationDefault.getMapping();
					
					ServizioApplicativo sa = new ServizioApplicativo();
					sa.setNome(portaApplicativa.getNome());
					sa.setTipologiaFruizione(TipologiaFruizione.DISABILITATO.getValue());
					sa.setTipologiaErogazione(TipologiaErogazione.TRASPARENTE.getValue());
					sa.setIdSoggetto(soggetto.getId());
					sa.setTipoSoggettoProprietario(portaApplicativa.getTipoSoggettoProprietario());
					sa.setNomeSoggettoProprietario(portaApplicativa.getNomeSoggettoProprietario());
						
					RispostaAsincrona rispostaAsinc = new RispostaAsincrona();
					rispostaAsinc.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
					rispostaAsinc.setGetMessage(CostantiConfigurazione.DISABILITATO);
					sa.setRispostaAsincrona(rispostaAsinc);
						
					InvocazioneServizio invServizio = new InvocazioneServizio();
					invServizio.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
					invServizio.setGetMessage(CostantiConfigurazione.DISABILITATO);
					Connettore connettore = new Connettore();
					connettore.setTipo(TipiConnettore.HTTP.getNome());
					org.openspcoop2.core.config.Property prop = new org.openspcoop2.core.config.Property();
					prop.setNome(CostantiDB.CONNETTORE_HTTP_LOCATION);
					prop.setValore("http://undefined");
					connettore.addProperty(prop);
					invServizio.setConnettore(connettore);
					sa.setInvocazioneServizio(invServizio);
								
					PortaApplicativaServizioApplicativo paSA = new PortaApplicativaServizioApplicativo();
					paSA.setNome(sa.getNome());
					portaApplicativa.addServizioApplicativo(paSA);
			
					if(archiveASPS.getMappingPorteApplicativeAssociate()==null) {
						archiveASPS.setMappingPorteApplicativeAssociate(new ArrayList<>());
					}
					archiveASPS.getMappingPorteApplicativeAssociate().add(mappingErogazione);
					
					ArchiveServizioApplicativo archiveSA = new ArchiveServizioApplicativo(sa, idCorrelazione, false);
					archiveObject.getServiziApplicativi().add(archiveSA);	
					
					ArchivePortaApplicativa archivePA = new ArchivePortaApplicativa(portaApplicativa, idCorrelazione,false);
					archiveObject.getPorteApplicative().add(archivePA);
				}
				else {
					
					// creo Porta Delegata
					
					IDSoggetto idFruitore = null;
					if(pddOperative==null) {
						throw new Exception("Non esistono pdd operative");
					}
					for (String pddOperativa : pddOperative) {
						FiltroRicercaSoggetti filtro = new FiltroRicercaSoggetti();
						filtro.setTipo("spc");
						filtro.setNomePdd(pddOperativa);
						List<IDSoggetto> idSoggetti = null;
						try {
							idSoggetti = registryReader.findIdSoggetti(filtro);
						}catch(RegistryNotFound notFound) {}
						if(idSoggetti!=null && !idSoggetti.isEmpty()) {
							idFruitore = idSoggetti.get(0);
						}
					}
					if(idFruitore==null) {
						throw new Exception("Non esistone un soggetto interno al dominio");
					}
					Soggetto soggettoFruitore = registryReader.getSoggetto(idFruitore);
															
					Subscription subscriptionDefault = this.protocolFactory.createProtocolIntegrationConfiguration().
							createDefaultSubscription(ServiceBinding.SOAP, idFruitore, idServizio);
					
					PortaDelegata portaDelegata = subscriptionDefault.getPortaDelegata();
					portaDelegata.setIdSoggetto(soggettoFruitore.getId());
					portaDelegata.setStato(StatoFunzionalita.DISABILITATO);
					// Per non creare un buco di sicurezza per default, abilito l'autenticazione degli applicativi
					portaDelegata.setAutenticazione(TipoAutenticazione.SSL.getValue());
					
					@SuppressWarnings("unused")
					// Viene creata in automatico
					MappingFruizionePortaDelegata mappingFruizione = subscriptionDefault.getMapping();

					Fruitore fruitore = new Fruitore();
					fruitore.setTipo(idFruitore.getTipo());
					fruitore.setNome(idFruitore.getNome());
					org.openspcoop2.core.registry.Connettore connettore = new org.openspcoop2.core.registry.Connettore();
					connettore.setTipo(TipiConnettore.HTTP.getNome());
					org.openspcoop2.core.registry.Property prop = new org.openspcoop2.core.registry.Property();
					prop.setNome(CostantiDB.CONNETTORE_HTTP_LOCATION);
					prop.setValore("http://undefined");
					connettore.addProperty(prop);
					fruitore.setStatoPackage(StatiAccordo.operativo.name());
					fruitore.setConnettore(connettore);
										
					ArchivePortaDelegata archivePD = new ArchivePortaDelegata(portaDelegata, idCorrelazione, false);
					archiveObject.getPorteDelegate().add(archivePD);
					
					ArchiveFruitore archiveFruitore = new ArchiveFruitore(idServizio, fruitore, idCorrelazione, false);
					archiveObject.getAccordiFruitori().add(archiveFruitore);
					
					if(archiveFruitore.getMappingPorteDelegateAssociate()==null) {
						archiveFruitore.setMappingPorteDelegateAssociate(new ArrayList<>());
					}
					archiveFruitore.getMappingPorteDelegateAssociate().add(mappingFruizione);
				}
			}
				
		}catch(SPCoopConvertToPackageCNIPAException convert){
			throw convert;
		}catch(Exception e){
			throw new ProtocolException("Conversione archivio non riuscita: "+e.getMessage(),e);
		}
		
	} 
	
	public Archive buildAccordoCooperazione(byte[]archive,
			IRegistryReader registryReader) throws ProtocolException, SPCoopConvertToPackageCNIPAException{
		
		try{
		
			SICAtoOpenSPCoopContext sicaContext = new SICAtoOpenSPCoopContext();
			
			
			// *** Trasformazione in package CNIPA ***
			XMLUtils daoUtilities = new XMLUtils(sicaContext,this.logger);
			it.gov.spcoop.sica.dao.AccordoCooperazione acSICA = null;
			try{
				acSICA = daoUtilities.getAccordoCooperazione(archive);
			}catch(Exception e){
				throw new SPCoopConvertToPackageCNIPAException("Conversione dell'archivio in package CNIPA non riuscita: "+e.getMessage(),e);
			}
			
			
			// *** Verifica informazioni presenti nel package ***
			if(acSICA.getManifesto().getNome()==null){
				throw new ProtocolException("Riscontrato un archivio che possiede un manifesto senza nome");
			}
			
			// soggetto coordinatore
			String soggettoCoordinatore = null;
			if(acSICA.getManifesto().getCoordinatore()!=null) {
				soggettoCoordinatore = acSICA.getManifesto().getCoordinatore().toString();
			}
			if(soggettoCoordinatore!=null){
				String codiceIPA = SICAtoOpenSPCoopUtilities.removeURI_IDSoggettoSica(soggettoCoordinatore);
				if(registryReader.existsSoggettoByCodiceIPA(codiceIPA)==false){
					throw new ProtocolException("Il soggetto coordinatore con Codice IPA ["+codiceIPA+"] non esiste.");
				}
			}
			
			// soggetti partecipanti
			if(acSICA.getManifesto()!=null && acSICA.getManifesto().getElencoPartecipanti()!=null){
				for(int i=0;i<acSICA.getManifesto().getElencoPartecipanti().sizePartecipanteList();i++){
					String partecipante = null;
					if(acSICA.getManifesto().getElencoPartecipanti().getPartecipante(i)!=null) {
						partecipante = acSICA.getManifesto().getElencoPartecipanti().getPartecipante(i).toString();
					}
					String codiceIPA = SICAtoOpenSPCoopUtilities.removeURI_IDSoggettoSica(partecipante);
					if(registryReader.existsSoggettoByCodiceIPA(codiceIPA)==false){
						throw new ProtocolException("Il soggetto partecipante con Codice IPA ["+codiceIPA+"] non esiste.");
					}
				}
			}
								
						
			// *** impostazione mapping soggetto con codice IPA ***
			SPCoopArchiveImportUtils.setIDSoggettoFromCodiceIPA(acSICA, sicaContext, registryReader);
			
			
			// *** Trasformazione in oggetto OpenSPCoop2 ***
			AccordoCooperazione acOpenSPCoop2 = null;
			try{
				acOpenSPCoop2 = SICAtoOpenSPCoopUtilities.accordoCooperazione_sicaToOpenspcoop(registryReader, acSICA, sicaContext, this.logger);
				
				Archive archiveObject = new Archive();
				
				ArchiveIdCorrelazione idCorrelazione = new ArchiveIdCorrelazione("CnipaPackageAC");
				
				ArchiveAccordoCooperazione archiveAC = new ArchiveAccordoCooperazione(acOpenSPCoop2,idCorrelazione,true);
				archiveObject.getAccordiCooperazione().add(archiveAC);
				
				return archiveObject;
				
			}catch(Exception e){
				throw new ProtocolException("Conversione dell'archivio, da formato CNIPA a formato OpenSPCoop2, non riuscita: "+e.getMessage(),e);
			}
			
		}catch(SPCoopConvertToPackageCNIPAException convert){
			throw convert;
		}catch(Exception e){
			throw new ProtocolException("Conversione archivio non riuscita: "+e.getMessage(),e);
		}
		
	} 
	
}
