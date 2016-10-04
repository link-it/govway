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

package org.openspcoop2.protocol.spcoop.archive;

import it.gov.spcoop.sica.dao.driver.XMLUtils;

import org.slf4j.Logger;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoCooperazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioComposto;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteComune;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteSpecifica;
import org.openspcoop2.protocol.sdk.archive.ArchiveIdCorrelazione;
import org.openspcoop2.protocol.sdk.archive.IRegistryReader;
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

	private IProtocolFactory protocolFactory = null;
	private Logger logger = null;
	public SPCoopArchiveImport(IProtocolFactory protocolFactory){
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
			String soggettoReferente = aspcSICA.getManifesto().getParteComune().getPubblicatore();
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
				aspcOpenSPCoop2 = SICAtoOpenSPCoopUtilities.accordoServizioParteComune_sicaToOpenspcoop(aspcSICA, sicaContext, this.logger);
				
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
			String soggettoReferente = asCompostoSICA.getManifesto().getPubblicatore();
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
				asCompostoOpenSPCoop2 = SICAtoOpenSPCoopUtilities.accordoServizioComposto_sicaToOpenspcoop(asCompostoSICA, sicaContext, this.logger);
				
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
			String soggettoErogatore = aspsSICA.getManifesto().getParteSpecifica().getErogatore();
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
				AccordoServizioParteSpecifica aspsOpenSPCoop2 = SICAtoOpenSPCoopUtilities.accordoServizioParteSpecifica_sicaToOpenspcoop(aspsSICA, sicaContext, this.logger);
				
				Archive archiveObject = new Archive();
				
				ArchiveIdCorrelazione idCorrelazione = new ArchiveIdCorrelazione("CnipaPackageASPS");
				
				ArchiveAccordoServizioParteSpecifica archiveASPS = new ArchiveAccordoServizioParteSpecifica(aspsOpenSPCoop2,idCorrelazione,true);
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
			String soggettoCoordinatore = acSICA.getManifesto().getCoordinatore();
			if(soggettoCoordinatore!=null){
				String codiceIPA = SICAtoOpenSPCoopUtilities.removeURI_IDSoggettoSica(soggettoCoordinatore);
				if(registryReader.existsSoggettoByCodiceIPA(codiceIPA)==false){
					throw new ProtocolException("Il soggetto coordinatore con Codice IPA ["+codiceIPA+"] non esiste.");
				}
			}
			
			// soggetti partecipanti
			if(acSICA.getManifesto()!=null && acSICA.getManifesto().getElencoPartecipanti()!=null){
				for(int i=0;i<acSICA.getManifesto().getElencoPartecipanti().sizePartecipanteList();i++){
					String partecipante = acSICA.getManifesto().getElencoPartecipanti().getPartecipante(i);
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
				acOpenSPCoop2 = SICAtoOpenSPCoopUtilities.accordoCooperazione_sicaToOpenspcoop(acSICA, sicaContext, this.logger);
				
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
