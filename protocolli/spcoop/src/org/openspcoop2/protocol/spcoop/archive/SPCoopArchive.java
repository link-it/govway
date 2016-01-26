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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.protocol.basic.archive.BasicArchive;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoCooperazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioComposto;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteComune;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteSpecifica;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoDelete;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImport;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImportDetail;
import org.openspcoop2.protocol.sdk.archive.ArchiveMode;
import org.openspcoop2.protocol.sdk.archive.ArchiveModeType;
import org.openspcoop2.protocol.sdk.archive.ExportMode;
import org.openspcoop2.protocol.sdk.archive.IRegistryReader;
import org.openspcoop2.protocol.sdk.archive.ImportMode;
import org.openspcoop2.protocol.sdk.archive.MapPlaceholder;
import org.openspcoop2.protocol.sdk.archive.MappingModeTypesExtensions;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostantiArchivi;
import org.openspcoop2.utils.Utilities;

import it.gov.spcoop.sica.dao.Costanti;
import it.gov.spcoop.sica.wsbl.ConceptualBehavior;

/**
 * SPCoopArchive 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopArchive extends BasicArchive {

	private SPCoopArchiveImport importEngine = null;
	private SPCoopArchiveExport exportEngine = null;
	
	public SPCoopArchive(IProtocolFactory protocolFactory) {
		super(protocolFactory);
		this.importEngine = new SPCoopArchiveImport(protocolFactory);
		this.exportEngine = new SPCoopArchiveExport(protocolFactory);
	}

	
	
	
	
	/* ----- Utilita' generali ----- */
	
	@Override
	public MappingModeTypesExtensions getMappingTypesExtensions(ArchiveMode mode)
			throws ProtocolException {
		if(SPCoopCostantiArchivi.CNIPA_MODE.equals(mode) || 
			SPCoopCostantiArchivi.EXPORT_MODE_COMPATIBILITA_CLIENT_SICA.equals(mode) ||
			SPCoopCostantiArchivi.EXPORT_MODE_INFORMAZIONI_COMPLETE.equals(mode) ){

			MappingModeTypesExtensions m = new MappingModeTypesExtensions();
						
			m.add(org.openspcoop2.protocol.basic.Costanti.OPENSPCOOP_ARCHIVE_EXT, 
						SPCoopCostantiArchivi.TYPE_ALL,
						SPCoopCostantiArchivi.TYPE_APC,
						SPCoopCostantiArchivi.TYPE_APS,
						SPCoopCostantiArchivi.TYPE_ADC,
						SPCoopCostantiArchivi.TYPE_ASC);
			
			m.add(Costanti.ESTENSIONE_ACCORDO_SERVIZIO_PARTE_COMUNE, 
						ArchiveType.ACCORDO_SERVIZIO_PARTE_COMUNE,
						SPCoopCostantiArchivi.TYPE_APC,
						SPCoopCostantiArchivi.TYPE_ALL);
			
			m.add(Costanti.ESTENSIONE_ACCORDO_SERVIZIO_PARTE_SPECIFICA, 
						ArchiveType.ACCORDO_SERVIZIO_PARTE_SPECIFICA,
						SPCoopCostantiArchivi.TYPE_APS,
						SPCoopCostantiArchivi.TYPE_ALL);
			
			m.add(Costanti.ESTENSIONE_ACCORDO_COOPERAZIONE, 
						ArchiveType.ACCORDO_COOPERAZIONE,
						SPCoopCostantiArchivi.TYPE_ADC,
						SPCoopCostantiArchivi.TYPE_ALL);
			
			m.add(Costanti.ESTENSIONE_ACCORDO_SERVIZIO_COMPOSTO, 
						ArchiveType.ACCORDO_SERVIZIO_COMPOSTO,
						SPCoopCostantiArchivi.TYPE_ASC,
						SPCoopCostantiArchivi.TYPE_ALL);
			
			return m;
		}
		else{
			return super.getMappingTypesExtensions(mode);
		}
	}
	
	/**
	 * Imposta per ogni portType e operation presente nell'accordo fornito come parametro 
	 * le informazioni di protocollo analizzando i documenti interni agli archivi
	 */
	@Override
	public void setProtocolInfo(AccordoServizioParteComune accordoServizioParteComune) throws ProtocolException{
		
		super.setProtocolInfo(accordoServizioParteComune);
		
		try{
			
			String operationAsincronaRichiesta = null;
			String operationAsincronaRisposta = null;
			boolean presenzaWSBLLogicoFruitore = false;
			
			if(accordoServizioParteComune.getByteSpecificaConversazioneConcettuale()!=null){
				
				byte [] wsbl = accordoServizioParteComune.getByteSpecificaConversazioneConcettuale();
				
				if(!it.gov.spcoop.sica.wsbl.driver.XMLUtils.isConceptualBehavior(wsbl)){
					if(it.gov.spcoop.sica.wsbl.driver.XMLUtils.isMessageBehavior(wsbl)){
						throw new Exception("La specifica di conversazione concettuale non e' un documento WSBL ConceptualBehavior, ma erroneamente un documento WSBL MessageBehavior");
					}else{
						throw new Exception("La specifica di conversazione concettuale non e' un documento WSBL ConceptualBehavior");
					}
				}
	
				ConceptualBehavior wsblConcettuale = it.gov.spcoop.sica.wsbl.driver.XMLUtils.getConceptualBehavior(this.getProtocolFactory().getLogger(),wsbl);
				String[]operazioniAsincrone = it.gov.spcoop.sica.wsbl.driver.XMLUtils.getOperazioniAsincrone(wsblConcettuale);
				
				operationAsincronaRichiesta = operazioniAsincrone[0];
				operationAsincronaRisposta = operazioniAsincrone[1];
				//System.out.println("1: "+operazioniAsincrone[0]);
				//System.out.println("2: "+operazioniAsincrone[1]);
				
				if(accordoServizioParteComune.getByteSpecificaConversazioneFruitore()!=null){
					presenzaWSBLLogicoFruitore = it.gov.spcoop.sica.wsbl.driver.XMLUtils.isMessageBehavior(wsbl);
				}
			
				
				// Verifico profilo di collaborazione e correlazione in base ai valori presenti nel WSBL
				// Verifico come nome:
				// 1. azione
				// 2. servizio_azione
				for (PortType ptOpenSPCoop : accordoServizioParteComune.getPortTypeList()) {
					
					for (Operation opOpenSPCoop : ptOpenSPCoop.getAzioneList()) {
						
						// 1. azione
						String tmp = opOpenSPCoop.getNome();
						if(tmp.equals(operationAsincronaRichiesta) || tmp.equals(operationAsincronaRisposta)){
							if(presenzaWSBLLogicoFruitore){
								opOpenSPCoop.setProfiloCollaborazione(CostantiRegistroServizi.ASINCRONO_SIMMETRICO);
							}else{
								opOpenSPCoop.setProfiloCollaborazione(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO);
							}
							if(tmp.equals(operationAsincronaRisposta)){
								opOpenSPCoop.setCorrelata(operationAsincronaRichiesta);
								// search servizio della richiesta
								for (PortType ptSearch : accordoServizioParteComune.getPortTypeList()) {
									boolean found = false;
									for (Operation opSearch : ptSearch.getAzioneList()) {
										if(opSearch.equals(operationAsincronaRichiesta)){
											opOpenSPCoop.setCorrelataServizio(ptSearch.getNome());
											found = true;
											break;
										}
									}
									if(found){
										break;
									}
								}
							}
						}
						
						else {
							// 2. servizio_azione
							int lengthServizioAzione = (ptOpenSPCoop.getNome().length()+1+opOpenSPCoop.getNome().length());
							boolean richiesta = (operationAsincronaRichiesta!=null) 
								&& (operationAsincronaRichiesta.length()==lengthServizioAzione)
								&& (operationAsincronaRichiesta.startsWith(ptOpenSPCoop.getNome()))
								&& (operationAsincronaRichiesta.endsWith(opOpenSPCoop.getNome()));
							boolean risposta = (operationAsincronaRisposta!=null) 
								&& (operationAsincronaRisposta.length()==lengthServizioAzione)
								&& (operationAsincronaRisposta.startsWith(ptOpenSPCoop.getNome()))
								&& (operationAsincronaRisposta.endsWith(opOpenSPCoop.getNome()));
							if(richiesta || risposta){
								//System.out.println("TROVATO ["+richiesta+"] ["+risposta+"]");
								if(presenzaWSBLLogicoFruitore){
									opOpenSPCoop.setProfiloCollaborazione(CostantiRegistroServizi.ASINCRONO_SIMMETRICO);
								}else{
									opOpenSPCoop.setProfiloCollaborazione(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO);
								}
								//System.out.println("PROFILO ["+opOpenSPCoop.getProfiloCollaborazione()+"]");
								if(risposta){
									// search servizio della richiesta
									for (PortType ptSearch : accordoServizioParteComune.getPortTypeList()) {
										boolean found = false;
										for (Operation opSearch : ptSearch.getAzioneList()) {
											int lengthServizioAzioneSearch = (ptSearch.getNome().length()+1+opSearch.getNome().length());
											boolean richiestaSearch = (operationAsincronaRichiesta.length()==lengthServizioAzioneSearch)
												&& (operationAsincronaRichiesta.startsWith(ptSearch.getNome()))
												&& (operationAsincronaRichiesta.endsWith(opSearch.getNome()));
											if(richiestaSearch){
												opOpenSPCoop.setCorrelata(opSearch.getNome());
												opOpenSPCoop.setCorrelataServizio(ptSearch.getNome());
												//System.out.println("TROVATA CORRELAZIONE ["+opOpenSPCoop.getCorrelata()+"] ["+opOpenSPCoop.getCorrelataServizio()+"]");
												found = true;
												break;
											}
										}
										if(found){
											break;
										}
									}
								}
							}
						}
					}
				}
			}
			
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	/* ----- Utilita' generali di interpretazione di un Esito ----- */
	
	private String toString(ArchiveEsitoImport archive, boolean importOperation){
		
		StringBuffer bfEsito = new StringBuffer();
		
		// Nel CNIPA ci sono sempre e solo un oggetto.
		
		// Accordi di Cooperazione
		if(archive.getAccordiCooperazione().size()>0){
			bfEsito.append("Accordo di Cooperazione\n");
			try{
				ArchiveEsitoImportDetail archiveAccordoCooperazione = archive.getAccordiCooperazione().get(0);
				IDAccordoCooperazione idAccordoCooperazione = ((ArchiveAccordoCooperazione)archiveAccordoCooperazione.getArchiveObject()).getIdAccordoCooperazione();
				String uriAccordo = this.idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordoCooperazione);
				bfEsito.append("\t- [").append(uriAccordo).append("] ");
				this.esitoUtils.serializeStato(archiveAccordoCooperazione, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- non importato: ").append(e.getMessage());
			}
		}
		
		
		// Accordi di Servizio Parte Comune
		else if(archive.getAccordiServizioParteComune().size()>0){
			bfEsito.append("Accordi di Servizio Parte Comune\n");
			try{
				ArchiveEsitoImportDetail archiveAccordoServizioParteComune = archive.getAccordiServizioParteComune().get(0);
				IDAccordo idAccordo = ((ArchiveAccordoServizioParteComune)archiveAccordoServizioParteComune.getArchiveObject()).getIdAccordoServizioParteComune();
				String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
				bfEsito.append("\t- [").append(uriAccordo).append("] ");
				this.esitoUtils.serializeStato(archiveAccordoServizioParteComune, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- non importato: ").append(e.getMessage());
			}
		}
		
		
		// Accordi di Servizio Parte Specifica (implementano accordi di servizio parte comune)
		else if(archive.getAccordiServizioParteSpecifica().size()>0){
			bfEsito.append("Accordi di Servizio Parte Specifica\n");
			try{
				ArchiveEsitoImportDetail archiveAccordoServizioParteSpecifica = archive.getAccordiServizioParteSpecifica().get(0);
				IDAccordo idAccordo = ((ArchiveAccordoServizioParteSpecifica)archiveAccordoServizioParteSpecifica.getArchiveObject()).getIdAccordoServizioParteSpecifica();
				String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
				bfEsito.append("\t- [").append(uriAccordo).append("] ");
				this.esitoUtils.serializeStato(archiveAccordoServizioParteSpecifica, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- non importato: ").append(e.getMessage());
			}
		}
		
		
		// Accordi di Servizio Composto
		else if(archive.getAccordiServizioComposto().size()>0){
			bfEsito.append("Accordi di Servizio Composto\n");
			try{
				ArchiveEsitoImportDetail archiveAccordoServizioComposto = archive.getAccordiServizioComposto().get(0);
				IDAccordo idAccordo = ((ArchiveAccordoServizioComposto)archiveAccordoServizioComposto.getArchiveObject()).getIdAccordoServizioParteComune();
				String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
				bfEsito.append("\t- [").append(uriAccordo).append("] ");
				this.esitoUtils.serializeStato(archiveAccordoServizioComposto, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- non importato: ").append(e.getMessage());
			}
		}
		
		
		// Accordi di Servizio Parte Specifica (implementano accordi di servizio composto)
		if(archive.getAccordiServizioParteSpecificaServiziComposti().size()>0){
			bfEsito.append("Accordi di Servizio Composto Parte Specifica\n");
			try{
				ArchiveEsitoImportDetail archiveAccordoServizioParteSpecifica = archive.getAccordiServizioParteSpecificaServiziComposti().get(0);
				IDAccordo idAccordo = ((ArchiveAccordoServizioParteSpecifica)archiveAccordoServizioParteSpecifica.getArchiveObject()).getIdAccordoServizioParteSpecifica();
				String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
				bfEsito.append("\t- [").append(uriAccordo).append("] ");
				this.esitoUtils.serializeStato(archiveAccordoServizioParteSpecifica, bfEsito, importOperation);
			}catch(Exception e){
				bfEsito.append("\t- non importato: ").append(e.getMessage());
			}
		}
		
		return bfEsito.toString();
	}
	
	
	
	
	
	
	
	
	
	
	/* ----- Import ----- */
	
	@Override
	public List<ImportMode> getImportModes() throws ProtocolException {
		List<ImportMode> list = super.getImportModes();
		list.add(new ImportMode(SPCoopCostantiArchivi.CNIPA_MODE));
		return list;
	}

	@Override
	public Archive importArchive(byte[]archive,ArchiveMode mode,ArchiveModeType type,
			IRegistryReader registryReader,boolean validationDocuments,
			MapPlaceholder placeholder) throws ProtocolException {	
		
		if(org.openspcoop2.protocol.basic.Costanti.OPENSPCOOP_IMPORT_ARCHIVE_MODE.equals(mode)){
			return super.importArchive(archive, mode, type, registryReader,validationDocuments,
					placeholder);
		}
		else if(SPCoopCostantiArchivi.CNIPA_MODE.equals(mode)){
			
			if(SPCoopCostantiArchivi.TYPE_APC.equals(type)){
				try{
					return this.importEngine.buildAccordoServizioParteComune(archive, registryReader,validationDocuments);
				}catch(SPCoopConvertToPackageCNIPAException e){
					throw new ProtocolException(e.getMessage(), e);
				}
			}
			else if(SPCoopCostantiArchivi.TYPE_ASC.equals(type)){
				try{
					return this.importEngine.buildAccordoServizioComposto(archive, registryReader,validationDocuments);
				}catch(SPCoopConvertToPackageCNIPAException e){
					throw new ProtocolException(e.getMessage(), e);
				}
			}
			else if(SPCoopCostantiArchivi.TYPE_APS.equals(type)){
				try{
					return this.importEngine.buildAccordoServizioParteSpecifica(archive, registryReader,validationDocuments);
				}catch(SPCoopConvertToPackageCNIPAException e){
					throw new ProtocolException(e.getMessage(), e);
				}
			}
			else if(SPCoopCostantiArchivi.TYPE_ADC.equals(type)){
				try{
					return this.importEngine.buildAccordoCooperazione(archive, registryReader);
				}catch(SPCoopConvertToPackageCNIPAException e){
					throw new ProtocolException(e.getMessage(), e);
				}
			}
			else if(SPCoopCostantiArchivi.TYPE_ALL.equals(type)){
				try{
					return this.importEngine.buildAccordoServizioParteComune(archive, registryReader,validationDocuments);
				}catch(SPCoopConvertToPackageCNIPAException eAPC){
					try{
						return this.importEngine.buildAccordoServizioComposto(archive, registryReader,validationDocuments);
					}catch(SPCoopConvertToPackageCNIPAException eAComposto){
						try{
							return this.importEngine.buildAccordoServizioParteSpecifica(archive, registryReader,validationDocuments);
						}catch(SPCoopConvertToPackageCNIPAException eAPS){
							try{
								return this.importEngine.buildAccordoCooperazione(archive, registryReader);
							}catch(SPCoopConvertToPackageCNIPAException eADC){
								throw new ProtocolException("L'archivio fornito non e' stato possibile interpretarlo come nessuno dei package supportati (per ottenere maggiori dettagli re-importarlo indicando un tipo specifico): "+
											SPCoopCostantiArchivi.TYPE_APC
											+","+
											SPCoopCostantiArchivi.TYPE_APS
											+","+
											SPCoopCostantiArchivi.TYPE_ASC
											+","+
											SPCoopCostantiArchivi.TYPE_ADC);
							}
						}
					}
				}
			}
			else{
				throw new ProtocolException("Type ["+type+"] unknown");
			}
		}
		else{
			throw new ProtocolException("Mode ["+mode+"] unknown");
		}		
			
	}
	
	@Override
	public Archive importArchive(InputStream archive,ArchiveMode mode,ArchiveModeType type,
			IRegistryReader registryReader,boolean validationDocuments,
			MapPlaceholder placeholder) throws ProtocolException {	
		
		byte[] bytes = null;
		try{
			bytes = Utilities.getAsByteArray(archive);
		}
		catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
		finally{
			try{
				if(archive!=null){
					archive.close();
				}
			}catch(Exception eClose){}
		}
		return this.importArchive(bytes, mode, type, registryReader, validationDocuments, placeholder);
	}
	
	@Override
	public String toString(ArchiveEsitoImport esito, ArchiveMode archiveMode) throws ProtocolException{
		if(org.openspcoop2.protocol.basic.Costanti.OPENSPCOOP_IMPORT_ARCHIVE_MODE.equals(archiveMode)){
			return super.toString(esito, archiveMode);
		}
		else if(SPCoopCostantiArchivi.CNIPA_MODE.equals(archiveMode)){
			return this.toString(esito,true);
		}
		else{
			throw new ProtocolException("Mode ["+archiveMode+"] unknown");
		}	
	}
	
	@Override
	public String toString(ArchiveEsitoDelete esito, ArchiveMode archiveMode) throws ProtocolException{
		if(org.openspcoop2.protocol.basic.Costanti.OPENSPCOOP_IMPORT_ARCHIVE_MODE.equals(archiveMode)){
			return super.toString(esito, archiveMode);
		}
		else if(SPCoopCostantiArchivi.CNIPA_MODE.equals(archiveMode)){
			return this.toString(esito,false);
		}
		else{
			throw new ProtocolException("Mode ["+archiveMode+"] unknown");
		}
	}
	
	
	
	
	
	
	/* ----- Export ----- */
	
	@Override
	public List<ExportMode> getExportModes(ArchiveType archiveType) throws ProtocolException {
		List<ExportMode> list = super.getExportModes(archiveType);
		switch (archiveType) {
		case ACCORDO_SERVIZIO_PARTE_COMUNE:
		case ACCORDO_SERVIZIO_PARTE_SPECIFICA:
		case ACCORDO_SERVIZIO_COMPOSTO:
		case ACCORDO_COOPERAZIONE:
			ExportMode exportSICA = new ExportMode(SPCoopCostantiArchivi.EXPORT_MODE_COMPATIBILITA_CLIENT_SICA);
			ExportMode exportComplete = new ExportMode(SPCoopCostantiArchivi.EXPORT_MODE_INFORMAZIONI_COMPLETE);
			list.add(exportSICA);
			list.add(exportComplete);
			break;
		default:
			break;
		}
		return list;
	}
	
	@Override
	public byte[] exportArchive(Archive archive, ArchiveMode mode,
			IRegistryReader registroReader)
			throws ProtocolException {
		
		if(org.openspcoop2.protocol.basic.Costanti.OPENSPCOOP_EXPORT_ARCHIVE_MODE.equals(mode)){
			return super.exportArchive(archive, mode, registroReader);
		}
		else if(SPCoopCostantiArchivi.EXPORT_MODE_COMPATIBILITA_CLIENT_SICA.equals(mode) ||
				SPCoopCostantiArchivi.EXPORT_MODE_INFORMAZIONI_COMPLETE.equals(mode)){
			try{
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				this.exportEngine.exportArchive(archive, mode, registroReader, bout);
				bout.flush();
				bout.close();
				return bout.toByteArray();
			}catch(Exception e){
				throw new ProtocolException(e.getMessage(),e);
			}
		}
		else{
			throw new ProtocolException("Mode ["+mode+"] unknown");
		}	
		
	}

	@Override
	public void exportArchive(Archive archive, OutputStream out, ArchiveMode mode,
			IRegistryReader registroReader)
			throws ProtocolException {
		if(org.openspcoop2.protocol.basic.Costanti.OPENSPCOOP_EXPORT_ARCHIVE_MODE.equals(mode)){
			super.exportArchive(archive, out, mode, registroReader);
		}
		else if(SPCoopCostantiArchivi.EXPORT_MODE_COMPATIBILITA_CLIENT_SICA.equals(mode) ||
				SPCoopCostantiArchivi.EXPORT_MODE_INFORMAZIONI_COMPLETE.equals(mode)){
			this.exportEngine.exportArchive(archive, mode, registroReader, out);
		}
		else{
			throw new ProtocolException("Mode ["+mode+"] unknown");
		}	
	}

	


	
}
