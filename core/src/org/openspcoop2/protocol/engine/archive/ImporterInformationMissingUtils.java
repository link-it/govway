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

package org.openspcoop2.protocol.engine.archive;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.xml.namespace.QName;

import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.protocol.information_missing.Openspcoop2;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoCooperazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioComposto;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteComune;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteSpecifica;
import org.openspcoop2.protocol.sdk.archive.ArchiveFruitore;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaApplicativa;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaDelegata;
import org.openspcoop2.protocol.sdk.archive.ArchiveServizioApplicativo;
import org.openspcoop2.protocol.sdk.archive.ArchiveSoggetto;
import org.openspcoop2.protocol.sdk.archive.IRegistryReader;
import org.openspcoop2.protocol.sdk.validator.IValidazioneDocumenti;
import org.openspcoop2.protocol.sdk.validator.ValidazioneResult;
import org.openspcoop2.utils.wsdl.DefinitionWrapper;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.w3c.dom.Document;

/**
 * ImporterInformationMissingUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ImporterInformationMissingUtils {

	private IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = null;
	private IDAccordoFactory idAccordoFactory = null;
	private ImportInformationMissingCollection importInformationMissingCollection = null;
	private boolean validateDocuments = false;
	private IProtocolFactory protocolFactory;
	private IValidazioneDocumenti validatoreDocumenti;
	private AbstractXMLUtils xmlUtils;
	private IRegistryReader registryReader;
	private Archive archive;
	
	public ImporterInformationMissingUtils(ImportInformationMissingCollection importInformationMissingCollection,IRegistryReader registryReader,
			boolean validateDocuments,IProtocolFactory protocolFactory, String userLogin, Archive archive) throws Exception{
		this.idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
		this.idAccordoFactory = IDAccordoFactory.getInstance();
		this.importInformationMissingCollection = importInformationMissingCollection;
		this.validateDocuments = validateDocuments;
		this.protocolFactory = protocolFactory;
		this.validatoreDocumenti = this.protocolFactory.createValidazioneDocumenti();
		this.xmlUtils = XMLUtils.getInstance();
		this.registryReader = registryReader;
		this.archive = archive;
	}
	
	
	
	public void validateAndFillInformationMissing(Openspcoop2 archiveInformationMissing) throws Exception{
		
		ImportInformationMissingException infoException = null;	
		String objectId = null;
		String objectIdDescription = null;
		ImportInformationMissing importInformationMissing = null;
		boolean throwException = false;
		try{
		
			// -------- soggetto --------------
			
			if(!throwException && archiveInformationMissing.sizeSoggettoList()>0){
				for (int i = 0; i < archiveInformationMissing.sizeSoggettoList(); i++) {
					org.openspcoop2.protocol.information_missing.Soggetto soggettoMissingInfo = 
							archiveInformationMissing.getSoggetto(i);
			
					// *** object id ***
					
					importInformationMissing = null;
					objectId = "[[InformationMissingSoggetto-"+i+"]]"; 
					objectIdDescription = soggettoMissingInfo.getDescrizione();	
					if(this.importInformationMissingCollection!=null){
						importInformationMissing = this.importInformationMissingCollection.get(objectId);
					}
					
					// *** campi da verificare ***
					boolean updateInfo = false;
					switch (soggettoMissingInfo.getTipo()) {
					case RIFERIMENTO:
						if(importInformationMissing!=null && importInformationMissing.getSoggetto()!=null){
							if(!this.registryReader.existsSoggetto(importInformationMissing.getSoggetto())){
								// verifico che non esista nell'archivio che sto importanto
								boolean found = false;
								if(this.archive.getSoggetti()!=null && this.archive.getSoggetti().size()>0){
									for (int j = 0; j < this.archive.getSoggetti().size(); j++) {
										ArchiveSoggetto archiveSoggetto = this.archive.getSoggetti().get(j);
										if(archiveSoggetto.getIdSoggetto().equals(importInformationMissing.getSoggetto())){
											found = true;
											break;
										}
									}
								}
								if(!found){
									throw new ProtocolException("Il Soggetto "+importInformationMissing.getSoggetto().toString()+" non esiste (indicato in ImportInformationMissing parameter??)");
								}
							}
							updateInfo = true;
						}else{
							infoException = new ImportInformationMissingException(objectId,objectIdDescription);
							infoException.setMissingInfoSoggetto(true);
							infoException.setMissingInfoSoggetto_protocollo(soggettoMissingInfo.getProtocollo());
							if(soggettoMissingInfo.getTipoPdd()!=null)
								infoException.setMissingInfoSoggetto_tipoPdD(soggettoMissingInfo.getTipoPdd().getValue());
							throwException = true;
						}	
						break;
					case CONNETTORE:
						if(importInformationMissing!=null && importInformationMissing.getInvocazioneServizio()!=null){
							updateInfo = true;
						}
						else{
							infoException = new ImportInformationMissingException(objectId,objectIdDescription);
							infoException.setMissingInfoInvocazioneServizio(true);
							infoException.setMissingInfoSoggetto_protocollo(soggettoMissingInfo.getProtocollo());
							if(soggettoMissingInfo.getTipoPdd()!=null)
								infoException.setMissingInfoSoggetto_tipoPdD(soggettoMissingInfo.getTipoPdd().getValue());	
							throwException = true;
						}
						break;
					}
					
					if(!throwException && updateInfo){
						// Se non sono state lanciate eccezioni a questo punto posso usare le informazioni salvate di information missing per riempire
						// le informazioni mancanti negli altri archivi, altrimenti poi i metodi sottostanti lanceranno le relative informazioni
						// di information missing
						IDSoggetto importInformationMissing_soggetto = null;
						Connettore importInformationMissing_connettore = null;
						if(importInformationMissing!=null){
							importInformationMissing_soggetto = importInformationMissing.getSoggetto();
							importInformationMissing_connettore = importInformationMissing.getConnettore();
						}
						ImporterInformationMissingSetter.setInformationMissingSoggetto(this.archive, soggettoMissingInfo, 
								importInformationMissing_soggetto, importInformationMissing_connettore);
					}
					else{
						break;
					}
					
				}
			}
			
			// -------- input --------------
			
			if(!throwException && archiveInformationMissing.sizeInputList()>0){
				for (int i = 0; i < archiveInformationMissing.sizeInputList(); i++) {
					org.openspcoop2.protocol.information_missing.Input inputMissingInfo = 
							archiveInformationMissing.getInput(i);
			
					// *** object id ***
					
					importInformationMissing = null;
					objectId = "[[InformationMissingInput-"+i+"]]"; 
					objectIdDescription = inputMissingInfo.getDescrizione();	
					if(this.importInformationMissingCollection!=null){
						importInformationMissing = this.importInformationMissingCollection.get(objectId);
					}
					
					// *** campi da verificare ***
					if(importInformationMissing==null || importInformationMissing.getInputPlaceholder()==null){
						infoException = new ImportInformationMissingException(objectId,objectIdDescription);
						infoException.setMissingInfoInput(true);
						infoException.setMissingInfoInputObject(inputMissingInfo);
						throwException = true;
					}
						
					if(throwException) {
						break;
					}
					
				}
			}
			
			// -------- servizioApplicativo --------------
					
			if(!throwException && archiveInformationMissing.sizeServizioApplicativoList()>0){
				for (int i = 0; i < archiveInformationMissing.sizeServizioApplicativoList(); i++) {
					org.openspcoop2.protocol.information_missing.ServizioApplicativo saMissingInfo = 
							archiveInformationMissing.getServizioApplicativo(i);
					
					// *** object id ***
					
					importInformationMissing = null;
					objectId = "[[InformationMissingServizioApplicativo-"+i+"]]"; 
					objectIdDescription = saMissingInfo.getDescrizione();	
					if(this.importInformationMissingCollection!=null){
						importInformationMissing = this.importInformationMissingCollection.get(objectId);
					}
					
					// *** campi da verificare ***
					boolean updateInfo = false;
					switch (saMissingInfo.getTipo()) {
					case RIFERIMENTO:
						// nop; non gestito (Da realizzare per assegnare lo stesso servizio applicativo a piu' porte)
						break;
					case CONNETTORE:
						if(importInformationMissing!=null && importInformationMissing.getInvocazioneServizio()!=null){
							updateInfo = true;
						}
						else{
							infoException = new ImportInformationMissingException(objectId,objectIdDescription);
							infoException.setMissingInfoInvocazioneServizio(true);
							throwException = true;
						}
						break;
					case CREDENZIALI_ACCESSO_PDD:
						if(importInformationMissing!=null && importInformationMissing.getCredenziali()!=null){
							updateInfo = true;
						}
						else{
							infoException = new ImportInformationMissingException(objectId,objectIdDescription);
							infoException.setMissingInfoCredenziali(true);
							throwException = true;
						}
						break;
					case ALLINEA_CREDENZIALI_PD:
						updateInfo = true;
						break;
					}
					
					if(!throwException && updateInfo){
						// Se non sono state lanciate eccezioni a questo punto posso usare le informazioni salvate di information missing per riempire
						// le informazioni mancanti negli altri archivi, altrimenti poi i metodi sottostanti lanceranno le relative informazioni
						// di information missing
						InvocazioneServizio importInformationMissing_invocazioneServizio = null;
						Credenziali importInformationMissing_credenziali = null;
						if(importInformationMissing!=null){
							importInformationMissing_invocazioneServizio = importInformationMissing.getInvocazioneServizio();
							importInformationMissing_credenziali = importInformationMissing.getCredenziali();
						}
						ImporterInformationMissingSetter.setInformationMissingServizioApplicativo(this.archive, saMissingInfo, 
								importInformationMissing_invocazioneServizio,importInformationMissing_credenziali);
					}
					else{
						break;
					}
				}			
			}
			
			// -------- accordiCooperazione --------------
			
			if(!throwException && archiveInformationMissing.sizeAccordoCooperazioneList()>0){
				for (int i = 0; i < archiveInformationMissing.sizeAccordoCooperazioneList(); i++) {
					org.openspcoop2.protocol.information_missing.AccordoCooperazione acMissingInfo = 
							archiveInformationMissing.getAccordoCooperazione(i);
					
					// *** object id ***
					
					importInformationMissing = null;
					objectId = "[[InformationMissingAccordoCooperazione-"+i+"]]"; 
					objectIdDescription = acMissingInfo.getDescrizione();	
					if(this.importInformationMissingCollection!=null){
						importInformationMissing = this.importInformationMissingCollection.get(objectId);
					}
					
					// *** campi da verificare ***
					boolean updateInfo = false;
					switch (acMissingInfo.getTipo()) {
					case RIFERIMENTO:
						if(importInformationMissing!=null && importInformationMissing.getIdAccordoCooperazione()!=null){
							updateInfo = true;
						}
						else{
							infoException = new ImportInformationMissingException(objectId,objectIdDescription);
							infoException.setMissingInfoAccordoCooperazione(true);
							throwException = true;
						}
						break;
					case STATO_ARCHIVIO:
						updateInfo = true;
						break;
					}
					
					if(!throwException && updateInfo){
						// Se non sono state lanciate eccezioni a questo punto posso usare le informazioni salvate di information missing per riempire
						// le informazioni mancanti negli altri archivi, altrimenti poi i metodi sottostanti lanceranno le relative informazioni
						// di information missing
						IDAccordoCooperazione importInformationMissing_idAccordoCooperazione = null;
						if(importInformationMissing!=null){
							importInformationMissing_idAccordoCooperazione = importInformationMissing.getIdAccordoCooperazione();
						}
						ImporterInformationMissingSetter.setInformationMissingAccordoCooperazione(this.archive, acMissingInfo, 
								importInformationMissing_idAccordoCooperazione);
					}
					else{
						break;
					}
				}			
			}
			
			
			// -------- accordiServizioParteComune --------------
			
			if(!throwException && archiveInformationMissing.sizeAccordoServizioParteComuneList()>0){
				for (int i = 0; i < archiveInformationMissing.sizeAccordoServizioParteComuneList(); i++) {
					org.openspcoop2.protocol.information_missing.AccordoServizioParteComune asMissingInfo = 
							archiveInformationMissing.getAccordoServizioParteComune(i);
					
					// *** object id ***
					
					importInformationMissing = null;
					objectId = "[[InformationMissingAccordoServizioParteComune-"+i+"]]"; 
					objectIdDescription = asMissingInfo.getDescrizione();	
					if(this.importInformationMissingCollection!=null){
						importInformationMissing = this.importInformationMissingCollection.get(objectId);
					}
					
					// *** campi da verificare ***
					boolean updateInfo = false;
					switch (asMissingInfo.getTipo()) {
					case RIFERIMENTO:
						if(importInformationMissing!=null && importInformationMissing.getIdAccordoServizioParteComune()!=null){
							updateInfo = true;
						}
						else{
							infoException = new ImportInformationMissingException(objectId,objectIdDescription);
							infoException.setMissingInfoAccordoServizioParteComune(true);
							throwException = true;
						}
						break;
					case STATO_ARCHIVIO:
						updateInfo = true;
						break;
					}
					
					if(!throwException && updateInfo){
						// Se non sono state lanciate eccezioni a questo punto posso usare le informazioni salvate di information missing per riempire
						// le informazioni mancanti negli altri archivi, altrimenti poi i metodi sottostanti lanceranno le relative informazioni
						// di information missing
						IDAccordo importInformationMissing_idAccordo = null;
						if(importInformationMissing!=null){
							importInformationMissing_idAccordo = importInformationMissing.getIdAccordoServizioParteComune();
						}
						ImporterInformationMissingSetter.setInformationMissingAccordoServizioParteComune(this.archive, asMissingInfo, 
								importInformationMissing_idAccordo);
					}
					else{
						break;
					}
				}			
			}
			
			
			// -------- accordoServizioParteSpecifica --------------
			
			if(!throwException && archiveInformationMissing.sizeAccordoServizioParteSpecificaList()>0){
				for (int i = 0; i < archiveInformationMissing.sizeAccordoServizioParteSpecificaList(); i++) {
					org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica aspsMissingInfo = 
							archiveInformationMissing.getAccordoServizioParteSpecifica(i);
					
					// *** object id ***
					
					importInformationMissing = null;
					objectId = "[[InformationMissingAPS-"+i+"]]"; 
					objectIdDescription = aspsMissingInfo.getDescrizione();	
					if(this.importInformationMissingCollection!=null){
						importInformationMissing = this.importInformationMissingCollection.get(objectId);
					}
					
					// *** campi da verificare ***
					boolean updateInfo = false;
					switch (aspsMissingInfo.getTipo()) {
					case CONNETTORE:
						if(importInformationMissing!=null && importInformationMissing.getConnettore()!=null){
							updateInfo = true;
						}
						else{
							infoException = new ImportInformationMissingException(objectId,objectIdDescription);
							infoException.setMissingInfoConnettore(true);
							throwException = true;
						}
						break;
					case STATO_ARCHIVIO:
						updateInfo = true;
						break;
					}					
					
					if(!throwException && updateInfo){
						// Se non sono state lanciate eccezioni a questo punto posso usare le informazioni salvate di information missing per riempire
						// le informazioni mancanti negli altri archivi, altrimenti poi i metodi sottostanti lanceranno le relative informazioni
						// di information missing
						Connettore importInformationMissing_connettore = null;
						if(importInformationMissing!=null){
							importInformationMissing_connettore = importInformationMissing.getConnettore();
						}
						ImporterInformationMissingSetter.setInformationMissingAccordoServizioParteSpecifica(this.archive, aspsMissingInfo, 
								importInformationMissing_connettore);
					}
					else{
						break;
					}
				}			
			}

			// -------- accordiServizioComposto --------------
			
			if(!throwException && archiveInformationMissing.sizeAccordoServizioCompostoList()>0){
				for (int i = 0; i < archiveInformationMissing.sizeAccordoServizioCompostoList(); i++) {
					org.openspcoop2.protocol.information_missing.AccordoServizioParteComune asMissingInfo = 
							archiveInformationMissing.getAccordoServizioComposto(i);
					
					// *** object id ***
					
					importInformationMissing = null;
					objectId = "[[InformationMissingAccordoServizioComposto-"+i+"]]"; 
					objectIdDescription = asMissingInfo.getDescrizione();	
					if(this.importInformationMissingCollection!=null){
						importInformationMissing = this.importInformationMissingCollection.get(objectId);
					}
					
					// *** campi da verificare ***
					boolean updateInfo = false;
					switch (asMissingInfo.getTipo()) {
					case RIFERIMENTO:
						if(importInformationMissing!=null && importInformationMissing.getIdAccordoServizioParteComune()!=null){
							updateInfo = true;
						}
						else{
							infoException = new ImportInformationMissingException(objectId,objectIdDescription);
							infoException.setMissingInfoAccordoServizioParteComune(true);
							throwException = true;
						}
						break;
					case STATO_ARCHIVIO:
						updateInfo = true;
						break;
					}
					
					if(!throwException && updateInfo){
						// Se non sono state lanciate eccezioni a questo punto posso usare le informazioni salvate di information missing per riempire
						// le informazioni mancanti negli altri archivi, altrimenti poi i metodi sottostanti lanceranno le relative informazioni
						// di information missing
						IDAccordo importInformationMissing_idAccordo = null;
						if(importInformationMissing!=null){
							importInformationMissing_idAccordo = importInformationMissing.getIdAccordoServizioParteComune();
						}
						ImporterInformationMissingSetter.setInformationMissingAccordoServizioComposto(this.archive, asMissingInfo, 
								importInformationMissing_idAccordo);
					}
					else{
						break;
					}
				}			
			}
			
			// -------- fruitori --------------
			
			if(!throwException && archiveInformationMissing.sizeFruitoreList()>0){
				for (int i = 0; i < archiveInformationMissing.sizeFruitoreList(); i++) {
					org.openspcoop2.protocol.information_missing.Fruitore fruitoreMissingInfo = 
							archiveInformationMissing.getFruitore(i);
					
					// *** object id ***
					
					importInformationMissing = null;
					objectId = "[[InformationMissingFruitore-"+i+"]]"; 
					objectIdDescription = fruitoreMissingInfo.getDescrizione();	
					if(this.importInformationMissingCollection!=null){
						importInformationMissing = this.importInformationMissingCollection.get(objectId);
					}
					
					// *** campi da verificare ***
					boolean updateInfo = false;
					switch (fruitoreMissingInfo.getTipo()) {
					case STATO_ARCHIVIO:
						updateInfo = true;
						break;
					}
					
					if(!throwException && updateInfo){
						// Se non sono state lanciate eccezioni a questo punto posso usare le informazioni salvate di information missing per riempire
						// le informazioni mancanti negli altri archivi, altrimenti poi i metodi sottostanti lanceranno le relative informazioni
						// di information missing
						ImporterInformationMissingSetter.setInformationMissingFruitore(this.archive, fruitoreMissingInfo);
					}
					else{
						break;
					}
				}			
			}
			
			// se e' stata rilevata una mancanza sollevo eccezione
			if(throwException){
				throw infoException;
			}		
				
						
		}catch(ImportInformationMissingException e){
			throw e;
		}catch(Exception e){
			throw new Exception(objectIdDescription+" validazione fallita: "+e.getMessage(),e);
		}
	}
	
	
	public void validateAndFillServizioApplicativo(ArchiveServizioApplicativo archiveServizioApplicativo) throws Exception{
		
		// *** object id ***
		
		ServizioApplicativo sa = archiveServizioApplicativo.getServizioApplicativo();
		String uri = sa.getNome();
		if(sa.getTipoSoggettoProprietario()!=null && sa.getNomeSoggettoProprietario()!=null){
			uri = uri +":"+sa.getTipoSoggettoProprietario()+"/"+sa.getNomeSoggettoProprietario();
		}
		String objectId = "[[SA]]"+uri; 
		String objectIdDescription = "Servizio Applicativo ["+uri+"]";		
		ImportInformationMissing importInformationMissing = null;
		if(this.importInformationMissingCollection!=null){
			importInformationMissing = this.importInformationMissingCollection.get(objectId);
		}
		
		try{
			
			// *** campi da verificare ***
			
			String tipoSoggettoProprietario = sa.getTipoSoggettoProprietario();
			String nomeSoggettoProprietario = sa.getNomeSoggettoProprietario();
			if(tipoSoggettoProprietario==null || nomeSoggettoProprietario==null){
				
				// provo ad utilizzare le informazioni eventualmente presenti nell'oggetto importInformationMissing
				// se non sono presenti nemmeno in tale oggetto, sollevo eccezione
				ImportInformationMissingException infoException = new ImportInformationMissingException(objectId,objectIdDescription);
				boolean throwException = false;
				
				// soggetto
				if(tipoSoggettoProprietario==null || nomeSoggettoProprietario==null){
					if(importInformationMissing!=null && importInformationMissing.getSoggetto()!=null){
						if(!this.registryReader.existsSoggetto(importInformationMissing.getSoggetto())){
							// verifico che non esista nell'archivio che sto importanto
							boolean found = false;
							if(this.archive.getSoggetti()!=null && this.archive.getSoggetti().size()>0){
								for (int i = 0; i < this.archive.getSoggetti().size(); i++) {
									ArchiveSoggetto archiveSoggetto = this.archive.getSoggetti().get(i);
									if(archiveSoggetto.getIdSoggetto().equals(importInformationMissing.getSoggetto())){
										found = true;
										break;
									}
								}
							}
							if(!found){
								throw new ProtocolException("Il Soggetto "+importInformationMissing.getSoggetto().toString()+" non esiste (indicato in ImportInformationMissing parameter??)");
							}
						}
						sa.setTipoSoggettoProprietario(importInformationMissing.getSoggetto().getTipo());
						sa.setNomeSoggettoProprietario(importInformationMissing.getSoggetto().getNome());
					}else{
						infoException.setMissingInfoSoggetto(true);
						throwException = true;
					}
				}
								
				// se e' stata rilevata una mancanza sollevo eccezione
				if(throwException)
					throw infoException;
			}
			
			
			// *** Verifica documenti presenti all'interno dell'archivio ***

			// non esistono
						
		}catch(ImportInformationMissingException e){
			throw e;
		}catch(Exception e){
			throw new Exception(objectIdDescription+" validazione fallita: "+e.getMessage(),e);
		}
	}
	
	
	
	public void validateAndFillPortaDelegata(ArchivePortaDelegata archivePortaDelegata) throws Exception{
		
		// *** object id ***
		
		PortaDelegata pd = archivePortaDelegata.getPortaDelegata();
		String uri = pd.getNome();
		if(pd.getTipoSoggettoProprietario()!=null && pd.getNomeSoggettoProprietario()!=null){
			uri = uri +":"+pd.getTipoSoggettoProprietario()+"/"+pd.getNomeSoggettoProprietario();
		}
		String objectId = "[[PD]]"+uri; 
		String objectIdDescription = "PortaDelegata ["+uri+"]";		
		ImportInformationMissing importInformationMissing = null;
		if(this.importInformationMissingCollection!=null){
			importInformationMissing = this.importInformationMissingCollection.get(objectId);
		}
		
		try{
			
			// *** campi da verificare ***
			
			String tipoSoggettoProprietario = pd.getTipoSoggettoProprietario();
			String nomeSoggettoProprietario = pd.getNomeSoggettoProprietario();
			if(tipoSoggettoProprietario==null || nomeSoggettoProprietario==null){
				
				// provo ad utilizzare le informazioni eventualmente presenti nell'oggetto importInformationMissing
				// se non sono presenti nemmeno in tale oggetto, sollevo eccezione
				ImportInformationMissingException infoException = new ImportInformationMissingException(objectId,objectIdDescription);
				boolean throwException = false;
				
				// soggetto
				if(tipoSoggettoProprietario==null || nomeSoggettoProprietario==null){
					if(importInformationMissing!=null && importInformationMissing.getSoggetto()!=null){
						if(!this.registryReader.existsSoggetto(importInformationMissing.getSoggetto())){
							// verifico che non esista nell'archivio che sto importanto
							boolean found = false;
							if(this.archive.getSoggetti()!=null && this.archive.getSoggetti().size()>0){
								for (int i = 0; i < this.archive.getSoggetti().size(); i++) {
									ArchiveSoggetto archiveSoggetto = this.archive.getSoggetti().get(i);
									if(archiveSoggetto.getIdSoggetto().equals(importInformationMissing.getSoggetto())){
										found = true;
										break;
									}
								}
							}
							if(!found){
								throw new ProtocolException("Il Soggetto "+importInformationMissing.getSoggetto().toString()+" non esiste (indicato in ImportInformationMissing parameter??)");
							}
						}
						pd.setTipoSoggettoProprietario(importInformationMissing.getSoggetto().getTipo());
						pd.setNomeSoggettoProprietario(importInformationMissing.getSoggetto().getNome());
					}else{
						infoException.setMissingInfoSoggetto(true);
						throwException = true;
					}
				}
								
				// se e' stata rilevata una mancanza sollevo eccezione
				if(throwException)
					throw infoException;
			}
			
			
			// *** Verifica documenti presenti all'interno dell'archivio ***

			// non esistono
						
		}catch(ImportInformationMissingException e){
			throw e;
		}catch(Exception e){
			throw new Exception(objectIdDescription+" validazione fallita: "+e.getMessage(),e);
		}
	}
	
	
	
	
	public void validateAndFillPortaApplicativa(ArchivePortaApplicativa archivePortaApplicativa) throws Exception{
		
		// *** object id ***
		
		PortaApplicativa pa = archivePortaApplicativa.getPortaApplicativa();
		String uri = pa.getNome();
		if(pa.getTipoSoggettoProprietario()!=null && pa.getNomeSoggettoProprietario()!=null){
			uri = uri +":"+pa.getTipoSoggettoProprietario()+"/"+pa.getNomeSoggettoProprietario();
		}
		String objectId = "[[PD]]"+uri; 
		String objectIdDescription = "PortaApplicativa ["+uri+"]";		
		ImportInformationMissing importInformationMissing = null;
		if(this.importInformationMissingCollection!=null){
			importInformationMissing = this.importInformationMissingCollection.get(objectId);
		}
		
		try{
			
			// *** campi da verificare ***
			
			String tipoSoggettoProprietario = pa.getTipoSoggettoProprietario();
			String nomeSoggettoProprietario = pa.getNomeSoggettoProprietario();
			if(tipoSoggettoProprietario==null || nomeSoggettoProprietario==null){
				
				// provo ad utilizzare le informazioni eventualmente presenti nell'oggetto importInformationMissing
				// se non sono presenti nemmeno in tale oggetto, sollevo eccezione
				ImportInformationMissingException infoException = new ImportInformationMissingException(objectId,objectIdDescription);
				boolean throwException = false;
				
				// soggetto
				if(tipoSoggettoProprietario==null || nomeSoggettoProprietario==null){
					if(importInformationMissing!=null && importInformationMissing.getSoggetto()!=null){
						if(!this.registryReader.existsSoggetto(importInformationMissing.getSoggetto())){
							// verifico che non esista nell'archivio che sto importanto
							boolean found = false;
							if(this.archive.getSoggetti()!=null && this.archive.getSoggetti().size()>0){
								for (int i = 0; i < this.archive.getSoggetti().size(); i++) {
									ArchiveSoggetto archiveSoggetto = this.archive.getSoggetti().get(i);
									if(archiveSoggetto.getIdSoggetto().equals(importInformationMissing.getSoggetto())){
										found = true;
										break;
									}
								}
							}
							if(!found){
								throw new ProtocolException("Il Soggetto "+importInformationMissing.getSoggetto().toString()+" non esiste (indicato in ImportInformationMissing parameter??)");
							}
						}
						pa.setTipoSoggettoProprietario(importInformationMissing.getSoggetto().getTipo());
						pa.setNomeSoggettoProprietario(importInformationMissing.getSoggetto().getNome());
					}else{
						infoException.setMissingInfoSoggetto(true);
						throwException = true;
					}
				}
								
				// se e' stata rilevata una mancanza sollevo eccezione
				if(throwException)
					throw infoException;
			}
			
			
			// *** Verifica documenti presenti all'interno dell'archivio ***

			// non esistono
						
		}catch(ImportInformationMissingException e){
			throw e;
		}catch(Exception e){
			throw new Exception(objectIdDescription+" validazione fallita: "+e.getMessage(),e);
		}
	}
	
	
	
	public void validateAndFillAccordoCooperazione(ArchiveAccordoCooperazione archiveAccordoCooperazione) throws Exception{
		
		// *** object id ***
		
		AccordoCooperazione ac = archiveAccordoCooperazione.getAccordoCooperazione();
		String uri = this.idAccordoCooperazioneFactory.getUriFromAccordo(ac);
		String objectId = "[[AC]]"+uri; 
		String objectIdDescription = "Accordo di Cooperazione ["+uri+"]";		
		ImportInformationMissing importInformationMissing = null;
		if(this.importInformationMissingCollection!=null){
			importInformationMissing = this.importInformationMissingCollection.get(objectId);
		}
		
		try{
			
			// *** campi da verificare ***
			
			IdSoggetto acSoggettoReferente = ac.getSoggettoReferente();
			String versione = ac.getVersione();
			if( (acSoggettoReferente==null || acSoggettoReferente.getTipo()==null || acSoggettoReferente.getNome()==null) 
					|| 
					versione==null){
				
				// provo ad utilizzare le informazioni eventualmente presenti nell'oggetto importInformationMissing
				// se non sono presenti nemmeno in tale oggetto, sollevo eccezione
				ImportInformationMissingException infoException = new ImportInformationMissingException(objectId,objectIdDescription);
				boolean throwException = false;
				
				// soggetto
				if(acSoggettoReferente==null || acSoggettoReferente.getTipo()==null || acSoggettoReferente.getNome()==null){
					if(importInformationMissing!=null && importInformationMissing.getSoggetto()!=null){
						if(!this.registryReader.existsSoggetto(importInformationMissing.getSoggetto())){
							// verifico che non esista nell'archivio che sto importanto
							boolean found = false;
							if(this.archive.getSoggetti()!=null && this.archive.getSoggetti().size()>0){
								for (int i = 0; i < this.archive.getSoggetti().size(); i++) {
									ArchiveSoggetto archiveSoggetto = this.archive.getSoggetti().get(i);
									if(archiveSoggetto.getIdSoggetto().equals(importInformationMissing.getSoggetto())){
										found = true;
										break;
									}
								}
							}
							if(!found){
								throw new ProtocolException("Il Soggetto "+importInformationMissing.getSoggetto().toString()+" non esiste (indicato in ImportInformationMissing parameter??)");
							}
						}
						IdSoggetto acSoggettoReferenteNew = new IdSoggetto();
						acSoggettoReferenteNew.setTipo(importInformationMissing.getSoggetto().getTipo());
						acSoggettoReferenteNew.setNome(importInformationMissing.getSoggetto().getNome());
						ac.setSoggettoReferente(acSoggettoReferenteNew);
					}else{
						infoException.setMissingInfoSoggetto(true);
						throwException = true;
					}
				}
				
				// versione
				if(versione==null){
					if(importInformationMissing!=null && importInformationMissing.getVersione()!=null){
						ac.setVersione(importInformationMissing.getVersione()+"");
					}else{
						infoException.setMissingInfoVersione(true);
						throwException = true;
					}
				}
								
				// se e' stata rilevata una mancanza sollevo eccezione
				if(throwException)
					throw infoException;
			}
			
			
			// *** Verifica documenti presenti all'interno dell'archivio ***
			
			if(this.validateDocuments){
				
				ValidazioneResult result = this.validatoreDocumenti.validaDocumenti(ac);
				if(result.isEsito()==false){
					if(result.getException()!=null)
						throw new Exception(result.getMessaggioErrore(),result.getException());
					else
						throw new Exception(result.getMessaggioErrore());
				}
				
			}
			
						
		}catch(ImportInformationMissingException e){
			throw e;
		}catch(Exception e){
			throw new Exception(objectIdDescription+" validazione fallita: "+e.getMessage(),e);
		}
	}
	
	public void validateAndFillAccordoServizioParteComune(ArchiveAccordoServizioParteComune archiveAspc,boolean checkCorrelazioneAsincrona) throws Exception{
		this.validateAndFillAccordoServizioEngine(archiveAspc.getAccordoServizioParteComune(),checkCorrelazioneAsincrona);
	}
	public void validateAndFillAccordoServizioParteComune(ArchiveAccordoServizioComposto archiveAsc,boolean checkCorrelazioneAsincrona) throws Exception{
		this.validateAndFillAccordoServizioEngine(archiveAsc.getAccordoServizioParteComune(),checkCorrelazioneAsincrona);
	}
	private void validateAndFillAccordoServizioEngine(AccordoServizioParteComune aspc,boolean checkCorrelazioneAsincrona) throws Exception{
		
		// *** object id ***
		
		String uri = this.idAccordoFactory.getUriFromAccordo(aspc);
		String tipoAccordo = "[[ASPC]]";
		if(aspc.getServizioComposto()!=null){
			tipoAccordo = "[[ASC]]";
		}
		String objectId = tipoAccordo+uri; 
		String objectIdDescription = "Accordo di Servizio Parte Comune ["+uri+"]";
		if(aspc.getServizioComposto()!=null){
			objectIdDescription = "Accordo di Servizio Composto ["+uri+"]";
			if(aspc.getServizioComposto().getAccordoCooperazione()!=null && !"".equals(aspc.getServizioComposto().getAccordoCooperazione().trim())){
				objectIdDescription = objectIdDescription + " (rifAccordo: "+aspc.getServizioComposto().getAccordoCooperazione()+")";
			}
		}
		ImportInformationMissing importInformationMissing = null;
		if(this.importInformationMissingCollection!=null){
			importInformationMissing = this.importInformationMissingCollection.get(objectId);
		}
		
		
		try{
		
			// *** campi da verificare ***
			
			IdSoggetto aspcSoggettoReferente = aspc.getSoggettoReferente();
			String versione = aspc.getVersione();
			boolean informazioniProfiloServiziPresenti = this.isInformazioniProfiloServiziPresenti(aspc,checkCorrelazioneAsincrona);
			String uriAccordoCooperazione = null;
			if(aspc.getServizioComposto()!=null){
				if(aspc.getServizioComposto().getAccordoCooperazione()!=null && !"".equals(aspc.getServizioComposto().getAccordoCooperazione().trim())){
					uriAccordoCooperazione = aspc.getServizioComposto().getAccordoCooperazione();
				}
			}
				
			if( (aspcSoggettoReferente==null || aspcSoggettoReferente.getTipo()==null || aspcSoggettoReferente.getNome()==null) 
					|| 
					versione==null 
					|| 
					!informazioniProfiloServiziPresenti
					||
					( (aspc.getServizioComposto()!=null) && (uriAccordoCooperazione==null) )
				){
				
				// provo ad utilizzare le informazioni eventualmente presenti nell'oggetto importInformationMissing
				// se non sono presenti nemmeno in tale oggetto, sollevo eccezione
				ImportInformationMissingException infoException = new ImportInformationMissingException(objectId,objectIdDescription);
				boolean throwException = false;
				
				// soggetto
				if(aspcSoggettoReferente==null || aspcSoggettoReferente.getTipo()==null || aspcSoggettoReferente.getNome()==null){
					if(importInformationMissing!=null && importInformationMissing.getSoggetto()!=null){
						if(!this.registryReader.existsSoggetto(importInformationMissing.getSoggetto())){
							// verifico che non esista nell'archivio che sto importanto
							boolean found = false;
							if(this.archive.getSoggetti()!=null && this.archive.getSoggetti().size()>0){
								for (int i = 0; i < this.archive.getSoggetti().size(); i++) {
									ArchiveSoggetto archiveSoggetto = this.archive.getSoggetti().get(i);
									if(archiveSoggetto.getIdSoggetto().equals(importInformationMissing.getSoggetto())){
										found = true;
										break;
									}
								}
							}
							if(!found){
								throw new ProtocolException("Il Soggetto "+importInformationMissing.getSoggetto().toString()+" non esiste (indicato in ImportInformationMissing parameter??)");
							}
						}
						IdSoggetto aspcSoggettoReferenteNew = new IdSoggetto();
						aspcSoggettoReferenteNew.setTipo(importInformationMissing.getSoggetto().getTipo());
						aspcSoggettoReferenteNew.setNome(importInformationMissing.getSoggetto().getNome());
						aspc.setSoggettoReferente(aspcSoggettoReferenteNew);
					}else{
						infoException.setMissingInfoSoggetto(true);
						throwException = true;
					}
				}
				
				// versione
				if(versione==null){
					if(importInformationMissing!=null && importInformationMissing.getVersione()!=null){
						aspc.setVersione(importInformationMissing.getVersione()+"");
					}else{
						infoException.setMissingInfoVersione(true);
						throwException = true;
					}
				}
				
				// info sui servizi
				if(!informazioniProfiloServiziPresenti){
					if(importInformationMissing!=null && importInformationMissing.sizePortTypeList()>0){
						for(int i=0; i<importInformationMissing.sizePortTypeList(); i++){
							aspc.addPortType(importInformationMissing.getPortType(i));
						}
					}
					// ricalcolo se adesso tutte le informazioni sono valide
					informazioniProfiloServiziPresenti = this.isInformazioniProfiloServiziPresenti(aspc,checkCorrelazioneAsincrona);
					if(!informazioniProfiloServiziPresenti){
						infoException.setMissingInfoProfiliServizi(true);
						// elimino tanto verranno ricreati dall'interfaccia
						while(aspc.sizePortTypeList()>0){
							aspc.removePortType(0);
						}
						infoException.setObject(aspc);
						infoException.setClassObject(aspc.getClass());
						throwException = true;
					}
				}
								
				// accordoCooperazione
				if( (aspc.getServizioComposto()!=null) && (uriAccordoCooperazione==null) ){
					if(importInformationMissing!=null && importInformationMissing.getIdAccordoCooperazione()!=null){
						aspc.getServizioComposto().setAccordoCooperazione(this.idAccordoCooperazioneFactory.getUriFromIDAccordo(importInformationMissing.getIdAccordoCooperazione()));
					}else{
						infoException.setMissingInfoAccordoCooperazione(true);
						throwException = true;
					}
				}
								
				// se e' stata rilevata una mancanza sollevo eccezione
				if(throwException)
					throw infoException;
			}
			
			
			// *** Verifica documenti presenti all'interno del package ***
			
			if(this.validateDocuments){
				
				ValidazioneResult result = this.validatoreDocumenti.validaInterfacciaWsdlParteComune(aspc);
				if(result.isEsito()==false){
					if(result.getException()!=null)
						throw new Exception(result.getMessaggioErrore(),result.getException());
					else
						throw new Exception(result.getMessaggioErrore());
				}
				
				result = this.validatoreDocumenti.validaSpecificaConversazione(aspc);
				if(result.isEsito()==false){
					if(result.getException()!=null)
						throw new Exception(result.getMessaggioErrore(),result.getException());
					else
						throw new Exception(result.getMessaggioErrore());
				}
				
				result = this.validatoreDocumenti.validaDocumenti(aspc);
				if(result.isEsito()==false){
					if(result.getException()!=null)
						throw new Exception(result.getMessaggioErrore(),result.getException());
					else
						throw new Exception(result.getMessaggioErrore());
				}
				
			}
			
		}catch(ImportInformationMissingException e){
			throw e;
		}catch(Exception e){
			throw new Exception(objectIdDescription+" validazione fallita: "+e.getMessage(),e);
		}

	}
	
	private boolean  isInformazioniProfiloServiziPresenti(AccordoServizioParteComune aspc,boolean checkCorrelazioneAsincrona){
		if(aspc==null){
			return false;
		}
		
		if(aspc.getProfiloCollaborazione()==null && aspc.sizePortTypeList()<=0){
			return false;
		}
		
		if(aspc.sizePortTypeList()>0){
			for (int i = 0; i < aspc.sizePortTypeList(); i++) {
				PortType pt = aspc.getPortType(i);
				if(pt==null){
					return false;
				}
				//String profiloCollaborazionePT = pt.getProfiloCollaborazione();
				if(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT.equals(pt.getProfiloPT())){
					// default
					if(aspc.getProfiloCollaborazione()==null){
						return false;
					}
					//profiloCollaborazionePT = aspc.getProfiloCollaborazione();
				}
				else{
					// ridefinisci
					if(pt.getProfiloCollaborazione()==null && aspc.getProfiloCollaborazione()==null){
						return false;
					}
				}
				for (int j = 0; j < pt.sizeAzioneList(); j++) {
					Operation op = pt.getAzione(j);
					//String profiloCollaborazioneAzione = null;
					if(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT.equals(op.getProfAzione())){
						// default
						if(aspc.getProfiloCollaborazione()==null && pt.getProfiloCollaborazione()==null){
							return false;
						}
						//profiloCollaborazioneAzione = profiloCollaborazionePT;
					}
					else{
						// ridefinisci
						if(op.getProfiloCollaborazione()==null && pt.getProfiloCollaborazione()==null && aspc.getProfiloCollaborazione()==null){
							return false;
						}
						//profiloCollaborazioneAzione = op.getProfiloCollaborazione();
					}
					
					// check correlazione asincrona
					// Si creano dipendenze circolari.
					// Inoltre non e' possibile importare un package, se prima non e' stato importato quello del correlato.
//					if(checkCorrelazioneAsincrona){
//						if(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO.equals(profiloCollaborazioneAzione) ||
//								CostantiRegistroServizi.ASINCRONO_SIMMETRICO.equals(profiloCollaborazioneAzione)){
//							if(op.getCorrelata()==null){
//								// search correlazione
//								if(existsCorrelazioneAsincrona(aspc.getPortTypeList(), pt.getNome(), op.getNome())==false){
//									return false;
//								}
//							}
//						}
//					}
				}
			}
		}
		else{
			
		}
		
		return true;
	}
	
//	private boolean existsCorrelazioneAsincrona(List<PortType> listPortTypes, String ptName, String opName){
//		for (PortType portType : listPortTypes) {
//			for (Operation operation : portType.getAzioneList()) {
//				if(operation.getNome().equals(opName) && portType.getNome().equals(ptName)){
//					continue;
//				}
//				if(operation.getCorrelata()!=null && opName.equals(operation.getCorrelata())){
//					if(operation.getCorrelataServizio()==null && portType.getNome().equals(ptName)){
//						return true; // correlazione verso altra azione dello stesso port type indicato come parametro;
//					}
//					if(operation.getCorrelataServizio()!=null && operation.getCorrelataServizio().equals(ptName)){
//						return true; // correlazione verso il servizio e l'azione indicata come parametro;
//					}
//				}
//			}
//		}
//		return false;
//	}
	
	public void validateAndFillAccordoServizioParteSpecifica(ArchiveAccordoServizioParteSpecifica archiveAsps) throws Exception{
		
		// *** object id ***
		AccordoServizioParteSpecifica asps = archiveAsps.getAccordoServizioParteSpecifica();
		String uri = this.idAccordoFactory.getUriFromAccordo(asps);
		String objectId = "[[ASPS]]"+uri; 
		String objectIdDescription = "Accordo di Servizio Parte Specifica ["+uri+"]";
		if(asps.getAccordoServizioParteComune()!=null && !"".equals(asps.getAccordoServizioParteComune().trim())){
			objectIdDescription = objectIdDescription +" (rifParteComune: "+asps.getAccordoServizioParteComune()+")";
		}
		ImportInformationMissing importInformationMissing = null;
		if(this.importInformationMissingCollection!=null){
			importInformationMissing = this.importInformationMissingCollection.get(objectId);
		}
		
		
		try{
		
			// *** Verifica riferimento Parte Comune ***
			AccordoServizioParteComune aspc = null;
			if(asps.getAccordoServizioParteComune()!=null && !"".equals(asps.getAccordoServizioParteComune().trim())){
				try{
					aspc = this.registryReader.getAccordoServizioParteComune(this.idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune()));
					if(aspc==null){
						throw new Exception("getAccordoServizioParteComune return null"); 
					}
				//}catch(RegistryNotFound notFound){
				}catch(Exception notFound){ // Se non esiste il soggetto, il metodo sopra ritorna null, poichè il driver non ritorna notFound. Questo succede se il soggetto è nell'archivio
					// verifico che non esista nell'archivio che sto importanto
					boolean found = false;
					if(this.archive.getAccordiServizioParteComune()!=null && this.archive.getAccordiServizioParteComune().size()>0){
						for (int i = 0; i < this.archive.getAccordiServizioParteComune().size(); i++) {
							ArchiveAccordoServizioParteComune archiveAccordo = this.archive.getAccordiServizioParteComune().get(i);
							IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromAccordo(archiveAccordo.getAccordoServizioParteComune());
							if(idAccordo.equals(this.idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune()))){
								found = true;
								aspc = archiveAccordo.getAccordoServizioParteComune();
								break;
							}
						}
					}
					if(!found){
						if(this.archive.getAccordiServizioComposto()!=null && this.archive.getAccordiServizioComposto().size()>0){
							for (int i = 0; i < this.archive.getAccordiServizioComposto().size(); i++) {
								ArchiveAccordoServizioParteComune archiveAccordo = this.archive.getAccordiServizioComposto().get(i);
								IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromAccordo(archiveAccordo.getAccordoServizioParteComune());
								if(idAccordo.equals(this.idAccordoFactory.getIDAccordoFromUri(asps.getAccordoServizioParteComune()))){
									found = true;
									aspc = archiveAccordo.getAccordoServizioParteComune();
									break;
								}
							}
						}
					}
					if(!found){
						throw new ProtocolException("Accordo di Servizio Parte Comune ["+asps.getAccordoServizioParteComune()+"], riferito dall'archivio, non esiste",notFound);
					}
				}
			}
			
			
			// *** campi da verificare ***
			String tipoSoggettoErogatore = asps.getServizio().getTipoSoggettoErogatore();
			String nomeSoggettoErogatore = asps.getServizio().getNomeSoggettoErogatore();
			String versione = asps.getVersione();
								
			if(tipoSoggettoErogatore==null || nomeSoggettoErogatore==null || versione==null || aspc==null ){
				
				// provo ad utilizzare le informazioni eventualmente presenti nell'oggetto importInformationMissing
				// se non sono presenti nemmeno in tale oggetto, sollevo eccezione
				ImportInformationMissingException infoException = new ImportInformationMissingException(objectId,objectIdDescription);
				boolean throwException = false;
				
				// soggetto
				if(tipoSoggettoErogatore==null || nomeSoggettoErogatore==null){
					if(importInformationMissing!=null && importInformationMissing.getSoggetto()!=null){
						if(!this.registryReader.existsSoggetto(importInformationMissing.getSoggetto())){
							// verifico che non esista nell'archivio che sto importanto
							boolean found = false;
							if(this.archive.getSoggetti()!=null && this.archive.getSoggetti().size()>0){
								for (int i = 0; i < this.archive.getSoggetti().size(); i++) {
									ArchiveSoggetto archiveSoggetto = this.archive.getSoggetti().get(i);
									if(archiveSoggetto.getIdSoggetto().equals(importInformationMissing.getSoggetto())){
										found = true;
										break;
									}
								}
							}
							if(!found){
								throw new ProtocolException("Il Soggetto "+importInformationMissing.getSoggetto().toString()+" non esiste (indicato in ImportInformationMissing parameter??)");
							}
						}
						asps.getServizio().setTipoSoggettoErogatore(importInformationMissing.getSoggetto().getTipo());
						asps.getServizio().setNomeSoggettoErogatore(importInformationMissing.getSoggetto().getNome());
//						if(asps.getServizio().getConnettore().getNome()==null){
//							String nomeConn = "CNT_" + importInformationMissing.getSoggetto().getTipo() + "/" + importInformationMissing.getSoggetto().getNome() + "_" +
//									asps.getServizio().getTipo() + "/" + asps.getServizio().getNome();
//							asps.getServizio().getConnettore().setNome(nomeConn);
//						}
					}else{
						infoException.setMissingInfoSoggetto(true);
						throwException = true;
					}
				}
				
				// versione
				if(versione==null){
					if(importInformationMissing!=null && importInformationMissing.getVersione()!=null){
						asps.setVersione(importInformationMissing.getVersione()+"");
					}else{
						infoException.setMissingInfoVersione(true);
						throwException = true;
					}
				}
				
				// accordoServizioParteComune
				if(aspc==null){
					if(importInformationMissing!=null && importInformationMissing.getIdAccordoServizioParteComune()!=null){
						asps.setAccordoServizioParteComune(this.idAccordoFactory.getUriFromIDAccordo(importInformationMissing.getIdAccordoServizioParteComune()));
					}else{
						infoException.setMissingInfoAccordoServizioParteComune(true);
						throwException = true;
					}
				}
				
				// se e' stata rilevata una mancanza sollevo eccezione
				if(throwException)
					throw infoException;
			}
			
			
			if(aspc!=null){
				
				// *** Verifica documenti presenti all'interno del package ***
				List<String> serviziIdentificatiNellaParteComune = this.letturaServiziDefinitiParteComune(asps, aspc);
				if(this.validateDocuments){						
					
					ValidazioneResult result = this.validatoreDocumenti.validaInterfacciaWsdlParteSpecifica(asps, aspc);
					if(result.isEsito()==false){
						if(result.getException()!=null)
							throw new Exception(result.getMessaggioErrore(),result.getException());
						else
							throw new Exception(result.getMessaggioErrore());
					}
					
					result = this.validatoreDocumenti.validaDocumenti(asps);
					if(result.isEsito()==false){
						if(result.getException()!=null)
							throw new Exception(result.getMessaggioErrore(),result.getException());
						else
							throw new Exception(result.getMessaggioErrore());
					}
					
					verificaMappingPortTypeBindingInRiferimentoParteComune(asps, aspc, serviziIdentificatiNellaParteComune);
				
				}
				
				
				// *** Verifica portType riferito ***
				if(importInformationMissing!=null && importInformationMissing.getPortTypeImplemented()!=null){
					if(asps.getPortType()!=null && asps.getPortType().equals(asps.getServizio().getNome())){
						asps.getServizio().setNome(importInformationMissing.getPortTypeImplemented());
					}
					asps.setPortType(importInformationMissing.getPortTypeImplemented());
				}
				if(asps.getPortType()!=null){
					if(serviziIdentificatiNellaParteComune.contains(asps.getPortType())==false){
						ImportInformationMissingException infoException = new ImportInformationMissingException(objectId,objectIdDescription);
						infoException.setMismatchPortTypeRifServiziParteComune(true, asps.getPortType(), serviziIdentificatiNellaParteComune);
						throw infoException;
					}
				}	
				
			}
			
		}catch(ImportInformationMissingException e){
			throw e;
		}catch(Exception e){
			throw new Exception(objectIdDescription+" validazione fallita: "+e.getMessage(),e);
		}
		
	}
	
	private List<String> letturaServiziDefinitiParteComune(Fruitore fruitore,AccordoServizioParteSpecifica asps,AccordoServizioParteComune aspc){
		Boolean correlato = null;
		
		// Comprensione attraverso WSDL Implementativo
		if(fruitore.getByteWsdlImplementativoErogatore()!=null){
			correlato = false;
		}
		else if(fruitore.getByteWsdlImplementativoFruitore()!=null){
			correlato = true;
		}
		else if(asps.getByteWsdlImplementativoErogatore()!=null){
			correlato = false;
		}
		else if(asps.getByteWsdlImplementativoFruitore()!=null){
			correlato = true;
		}
		
		// Comprensione attraverso tipologia
		if(correlato==null){
			TipologiaServizio tipologiaServizio = TipologiaServizio.NORMALE;
			if(asps.getServizio()!=null){
				tipologiaServizio = asps.getServizio().getTipologiaServizio();
			}
			correlato = TipologiaServizio.CORRELATO.equals(tipologiaServizio);
		}
		
		return this.letturaServiziDefinitiParteComune(correlato, aspc);	
	}
	private List<String> letturaServiziDefinitiParteComune(AccordoServizioParteSpecifica asps,AccordoServizioParteComune aspc){
		Boolean correlato = null;
		
		// Comprensione attraverso WSDL Implementativo
		if(asps.getByteWsdlImplementativoErogatore()!=null){
			correlato = false;
		}
		else if(asps.getByteWsdlImplementativoFruitore()!=null){
			correlato = true;
		}
		
		// Comprensione attraverso tipologia
		if(correlato==null){
			TipologiaServizio tipologiaServizio = TipologiaServizio.NORMALE;
			if(asps.getServizio()!=null){
				tipologiaServizio = asps.getServizio().getTipologiaServizio();
			}
			correlato = TipologiaServizio.CORRELATO.equals(tipologiaServizio);
		}
		
		return this.letturaServiziDefinitiParteComune(correlato, aspc);	
	}
	private List<String> letturaServiziDefinitiParteComune(boolean correlato,AccordoServizioParteComune aspc){
		List<String> serviziIdentificatiNellaParteComune = new ArrayList<String>();
				
		// Lettura Accordo di Servizio Parte Comune di OpenSPCoop
		// NOTA: aspc di openspcoop contiene sia le parti comuni "normali" che quelle "servizio composto"
		for(int i=0; i<aspc.sizePortTypeList(); i++){
			// faccio vedere solo i servizi correlati o i servizi normali, a seconda se e' stato fornito il wsdl implementativo fruitore o erogatore
			PortType pt = aspc.getPortType(i);
			boolean servizioCorrelato = false;
			for(int j=0; j<pt.sizeAzioneList(); j++){
				Operation op = pt.getAzione(j);
				// NOTA: solo un asincrono simmetrico o asincrono asimmetrico (verso servizio diverso) e' correlato!
				if(op.getCorrelataServizio()!=null && !pt.getNome().equals(op.getCorrelataServizio()) && op.getCorrelata()!=null){
					servizioCorrelato = true;
					break;
				}
			}
			if(correlato){
				if(servizioCorrelato){
					serviziIdentificatiNellaParteComune.add(aspc.getPortType(i).getNome());
				}
			}else{
				if(!servizioCorrelato){
					serviziIdentificatiNellaParteComune.add(aspc.getPortType(i).getNome());
				}
			}
		}
		
		return serviziIdentificatiNellaParteComune;
	}
	
	private void verificaMappingPortTypeBindingInRiferimentoParteComune(Fruitore fruitore,AccordoServizioParteSpecifica asps, 
			AccordoServizioParteComune aspc,
			List<String> serviziIdentificatiNellaParteComune) throws Exception {
		// Comprensione WSDL Implementativo
		byte[]wsdlImplementativo = null;
		String tipoWSDL = "WSDL Implementativo";
		if(fruitore.getByteWsdlImplementativoErogatore()!=null){
			wsdlImplementativo = asps.getByteWsdlImplementativoErogatore();
			tipoWSDL = "WSDL Implementativo Erogatore";
		}
		else if(fruitore.getByteWsdlImplementativoFruitore()!=null){
			wsdlImplementativo = asps.getByteWsdlImplementativoFruitore();
			tipoWSDL = "WSDL Implementativo Fruitore";
		}
		else if(asps.getByteWsdlImplementativoErogatore()!=null){
			wsdlImplementativo = asps.getByteWsdlImplementativoErogatore();
			tipoWSDL = "WSDL Implementativo Erogatore";
		}
		else if(asps.getByteWsdlImplementativoFruitore()!=null){
			wsdlImplementativo = asps.getByteWsdlImplementativoFruitore();
			tipoWSDL = "WSDL Implementativo Fruitore";
		}
		if(wsdlImplementativo!=null){
			this.verificaMappingPortTypeBindingInRiferimentoParteComune(wsdlImplementativo, tipoWSDL, aspc, serviziIdentificatiNellaParteComune);
		}
	}
	private void verificaMappingPortTypeBindingInRiferimentoParteComune(AccordoServizioParteSpecifica asps, 
			AccordoServizioParteComune aspc,
			List<String> serviziIdentificatiNellaParteComune) throws Exception {
		// Comprensione WSDL Implementativo
		byte[]wsdlImplementativo = null;
		String tipoWSDL = "WSDL Implementativo";
		if(asps.getByteWsdlImplementativoErogatore()!=null){
			wsdlImplementativo = asps.getByteWsdlImplementativoErogatore();
			tipoWSDL = "WSDL Implementativo Erogatore";
		}
		else if(asps.getByteWsdlImplementativoFruitore()!=null){
			wsdlImplementativo = asps.getByteWsdlImplementativoFruitore();
			tipoWSDL = "WSDL Implementativo Fruitore";
		}
		if(wsdlImplementativo!=null){
			this.verificaMappingPortTypeBindingInRiferimentoParteComune(wsdlImplementativo, tipoWSDL, aspc, serviziIdentificatiNellaParteComune);
		}
	}
	private void verificaMappingPortTypeBindingInRiferimentoParteComune( byte[]wsdlImplementativo,String tipoWSDL,
			AccordoServizioParteComune aspc,
			List<String> serviziIdentificatiNellaParteComune) throws Exception {
		
		List<String> portTypesImplemented = new ArrayList<String>();
		try{
									
			// Lettura WSDL Parte Specifica
			Document d = this.xmlUtils.newDocument(wsdlImplementativo);
			DefinitionWrapper wsdl = new DefinitionWrapper(d,this.xmlUtils,false,false);
			Hashtable<QName,QName> mapBindingToPortTypeImplemented = wsdl.getMapPortTypesImplementedBinding();
			Enumeration<QName> bindings = mapBindingToPortTypeImplemented.keys();
			while (bindings.hasMoreElements()) {
				QName binding = (QName) bindings.nextElement();
				QName portType = mapBindingToPortTypeImplemented.get(binding);
				String portTypeName = portType.getLocalPart();
				if(portTypesImplemented.contains(portTypeName)==false){
					portTypesImplemented.add(portTypeName);
				}
			}
		}catch(Exception e){
			String msgErrore = "La verifica dei port-types, implementati dai binding del "+tipoWSDL+", rispetto ai servizi definiti nell'accordo di servizio parte comune, ha riscontrato un errore: "+e.getMessage();
			this.protocolFactory.getLogger().error(msgErrore,e);
			throw new Exception(msgErrore,e);
		}
					
		// Controllo
		for (String pt : portTypesImplemented) {				
			boolean trovato = false;
			for (String servizioOpenSPCoop : serviziIdentificatiNellaParteComune) {
				if(pt.equals(servizioOpenSPCoop)){
					trovato=true;
					break;
				}
			}
			if(!trovato){
				throw new Exception("Il PortType "+pt+" implementato nei binding presenti nel "+tipoWSDL+" non è uno dei servizi definiti nell'accordo di servizio parte comune");
			}
		}

	}
	
	
	
	@SuppressWarnings("deprecation")
	public void validateAndFillFruitore(ArchiveFruitore archiveFruitore) throws Exception{
		
		// *** object id ***
		Fruitore fruitore = archiveFruitore.getFruitore();
		IDAccordo asps = archiveFruitore.getIdAccordoServizioParteSpecifica();
		String uri = fruitore.getTipo()+"/"+fruitore.getNome()+"_"+this.idAccordoFactory.getUriFromIDAccordo(asps);
		String objectId = "[[Fruitore]]"+uri; 
		String objectIdDescription = "Fruitore ["+fruitore.getTipo()+"/"+fruitore.getNome()
				+"] dell'Accordo di Servizio Parte Specifica ["+this.idAccordoFactory.getUriFromIDAccordo(asps)+"]";
		ImportInformationMissing importInformationMissing = null;
		if(this.importInformationMissingCollection!=null){
			importInformationMissing = this.importInformationMissingCollection.get(objectId);
		}
		
		
		try{
					
			// *** campi da verificare ***
			String tipoSoggettoErogatore = null;
			String nomeSoggettoErogatore = null;
			if(asps.getSoggettoReferente()!=null){
				tipoSoggettoErogatore = asps.getSoggettoReferente().getTipo();
				nomeSoggettoErogatore = asps.getSoggettoReferente().getNome();
			}
			String versione = asps.getVersione();
								
			if(tipoSoggettoErogatore==null || nomeSoggettoErogatore==null || versione==null ){
				
				// provo ad utilizzare le informazioni eventualmente presenti nell'oggetto importInformationMissing
				// se non sono presenti nemmeno in tale oggetto, sollevo eccezione
				ImportInformationMissingException infoException = new ImportInformationMissingException(objectId,objectIdDescription);
				boolean throwException = false;
				
				// soggetto
				if(tipoSoggettoErogatore==null || nomeSoggettoErogatore==null){
					if(importInformationMissing!=null && importInformationMissing.getSoggetto()!=null){
						if(!this.registryReader.existsSoggetto(importInformationMissing.getSoggetto())){
							// verifico che non esista nell'archivio che sto importanto
							boolean found = false;
							if(this.archive.getSoggetti()!=null && this.archive.getSoggetti().size()>0){
								for (int i = 0; i < this.archive.getSoggetti().size(); i++) {
									ArchiveSoggetto archiveSoggetto = this.archive.getSoggetti().get(i);
									if(archiveSoggetto.getIdSoggetto().equals(importInformationMissing.getSoggetto())){
										found = true;
										break;
									}
								}
							}
							if(!found){
								throw new ProtocolException("Il Soggetto "+importInformationMissing.getSoggetto().toString()+" non esiste (indicato in ImportInformationMissing parameter??)");
							}
						}
						if(asps.getSoggettoReferente()==null){
							asps.setSoggettoReferente(importInformationMissing.getSoggetto());
						}
					}else{
						infoException.setMissingInfoSoggetto(true);
						throwException = true;
					}
				}
				
				// versione
				if(versione==null){
					if(importInformationMissing!=null && importInformationMissing.getVersione()!=null){
						asps.setVersione(importInformationMissing.getVersione()+"");
					}else{
						infoException.setMissingInfoVersione(true);
						throwException = true;
					}
				}
				
				// se e' stata rilevata una mancanza sollevo eccezione
				if(throwException)
					throw infoException;
			}
			
			
			// *** Verifica riferimento Parte Specifica ***
			AccordoServizioParteSpecifica accordoAsps = null;
			try{
				accordoAsps = this.registryReader.getAccordoServizioParteSpecifica(asps);
				if(accordoAsps==null){
					throw new Exception("getAccordoServizioParteSpecifica return null"); 
				}
			//}catch(RegistryNotFound notFound){
			}catch(Exception notFound){ // Se non esiste il soggetto, il metodo sopra ritorna null, poichè il driver non ritorna notFound. Questo succede se il soggetto è nell'archivio
				// verifico che non esista nell'archivio che sto importanto
				boolean found = false;
				if(this.archive.getAccordiServizioParteSpecifica()!=null && this.archive.getAccordiServizioParteSpecifica().size()>0){
					for (int i = 0; i < this.archive.getAccordiServizioParteSpecifica().size(); i++) {
						ArchiveAccordoServizioParteSpecifica archiveAccordo = this.archive.getAccordiServizioParteSpecifica().get(i);
						IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromAccordo(archiveAccordo.getAccordoServizioParteSpecifica());
						if(idAccordo.equals(asps)){
							found = true;
							accordoAsps = archiveAccordo.getAccordoServizioParteSpecifica();
							break;
						}
					}
				}
				if(!found){
					throw new ProtocolException("Accordo di Servizio Parte Specifica ["+asps+"], riferito dall'archivio fruitore, non esiste",notFound);
				}
			}
			
			
			
			// *** Verifica riferimento Parte Comune ***
			AccordoServizioParteComune aspc = null;
			try{
				aspc = this.registryReader.getAccordoServizioParteComune(this.idAccordoFactory.getIDAccordoFromUri(accordoAsps.getAccordoServizioParteComune()));
				if(aspc==null){
					throw new Exception("getAccordoServizioParteComune return null"); 
				}
			//}catch(RegistryNotFound notFound){
			}catch(Exception notFound){ // Se non esiste il soggetto, il metodo sopra ritorna null, poichè il driver non ritorna notFound. Questo succede se il soggetto è nell'archivio
				// verifico che non esista nell'archivio che sto importanto
				boolean found = false;
				if(this.archive.getAccordiServizioParteComune()!=null && this.archive.getAccordiServizioParteComune().size()>0){
					for (int i = 0; i < this.archive.getAccordiServizioParteComune().size(); i++) {
						ArchiveAccordoServizioParteComune archiveAccordo = this.archive.getAccordiServizioParteComune().get(i);
						IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromAccordo(archiveAccordo.getAccordoServizioParteComune());
						if(idAccordo.equals(this.idAccordoFactory.getIDAccordoFromUri(accordoAsps.getAccordoServizioParteComune()))){
							found = true;
							aspc = archiveAccordo.getAccordoServizioParteComune();
							break;
						}
					}
				}
				if(!found){
					if(this.archive.getAccordiServizioComposto()!=null && this.archive.getAccordiServizioComposto().size()>0){
						for (int i = 0; i < this.archive.getAccordiServizioComposto().size(); i++) {
							ArchiveAccordoServizioParteComune archiveAccordo = this.archive.getAccordiServizioComposto().get(i);
							IDAccordo idAccordo = this.idAccordoFactory.getIDAccordoFromAccordo(archiveAccordo.getAccordoServizioParteComune());
							if(idAccordo.equals(this.idAccordoFactory.getIDAccordoFromUri(accordoAsps.getAccordoServizioParteComune()))){
								found = true;
								aspc = archiveAccordo.getAccordoServizioParteComune();
								break;
							}
						}
					}
				}
				if(!found){
					throw new ProtocolException("Accordo di Servizio Parte Comune ["+accordoAsps.getAccordoServizioParteComune()+
							"], riferito dall'accordo parte specifica dell'archivio fruitore, non esiste",notFound);
				}
			}
			
			
			
			// *** Verifica documenti presenti all'interno del package ***
			List<String> serviziIdentificatiNellaParteComune = this.letturaServiziDefinitiParteComune(fruitore,accordoAsps, aspc);
			if(this.validateDocuments){						
				
				ValidazioneResult result = this.validatoreDocumenti.validaInterfacciaWsdlParteSpecifica(fruitore,accordoAsps, aspc);
				if(result.isEsito()==false){
					if(result.getException()!=null)
						throw new Exception(result.getMessaggioErrore(),result.getException());
					else
						throw new Exception(result.getMessaggioErrore());
				}
								
				verificaMappingPortTypeBindingInRiferimentoParteComune(fruitore, accordoAsps, aspc, serviziIdentificatiNellaParteComune);
			
			}
						
		}catch(ImportInformationMissingException e){
			throw e;
		}catch(Exception e){
			throw new Exception(objectIdDescription+" validazione fallita: "+e.getMessage(),e);
		}
		
	}
}
