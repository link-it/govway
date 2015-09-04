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

package org.openspcoop2.protocol.basic.archive;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.BindingOperation;

import org.apache.log4j.Logger;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDPortaApplicativaByNome;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.wsdl.AccordoServizioWrapperUtilities;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.archive.Archive;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoCooperazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioComposto;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteComune;
import org.openspcoop2.protocol.sdk.archive.ArchiveAccordoServizioParteSpecifica;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImport;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImportDetail;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImportDetailConfigurazione;
import org.openspcoop2.protocol.sdk.archive.ArchiveFruitore;
import org.openspcoop2.protocol.sdk.archive.ArchiveMode;
import org.openspcoop2.protocol.sdk.archive.ArchiveModeType;
import org.openspcoop2.protocol.sdk.archive.ArchivePdd;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaApplicativa;
import org.openspcoop2.protocol.sdk.archive.ArchivePortaDelegata;
import org.openspcoop2.protocol.sdk.archive.ArchiveServizioApplicativo;
import org.openspcoop2.protocol.sdk.archive.ArchiveSoggetto;
import org.openspcoop2.protocol.sdk.archive.ExportMode;
import org.openspcoop2.protocol.sdk.archive.IArchive;
import org.openspcoop2.protocol.sdk.archive.IRegistryReader;
import org.openspcoop2.protocol.sdk.archive.ImportMode;
import org.openspcoop2.protocol.sdk.archive.MapPlaceholder;
import org.openspcoop2.protocol.sdk.archive.MappingModeTypesExtensions;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.utils.wsdl.DefinitionWrapper;
import org.openspcoop2.utils.wsdl.WSDLUtilities;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.w3c.dom.Document;

/**
 *  BasicArchive
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicArchive implements IArchive {

	protected IProtocolFactory protocolFactory = null;
	protected IDAccordoCooperazioneFactory idAccordoCooperazioneFactory;
	protected IDAccordoFactory idAccordoFactory;
	public BasicArchive(IProtocolFactory protocolFactory){
		this.protocolFactory = protocolFactory;
		this.idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
		this.idAccordoFactory = IDAccordoFactory.getInstance();
	}
	
	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}

	
	
	
	/* ----- Utilita' generali ----- */
	
	@Override
	public MappingModeTypesExtensions getMappingTypesExtensions(ArchiveMode mode)
			throws ProtocolException {
		MappingModeTypesExtensions m = new MappingModeTypesExtensions();
		m.add(Costanti.OPENSPCOOP_ARCHIVE_EXT, Costanti.OPENSPCOOP_ARCHIVE_MODE_TYPE);
		return m;
	}
	
	/**
	 * Imposta per ogni portType e operation presente nell'accordo fornito come parametro 
	 * le informazioni di protocollo analizzando i documenti interni agli archivi
	 */
	@Override
	public void setProtocolInfo(AccordoServizioParteComune accordoServizioParteComune) throws ProtocolException{
		_setProtocolInfo(accordoServizioParteComune, this.protocolFactory.getLogger());
	}
	public void setProtocolInfo(AccordoServizioParteComune accordoServizioParteComune,Logger log) throws ProtocolException{
		_setProtocolInfo(accordoServizioParteComune, log);
	}
	private void _setProtocolInfo(AccordoServizioParteComune accordoServizioParteComune,Logger log) throws ProtocolException{
		
		// NOTA non usare in questo metodo e nel metodo _setProtocolInfo il protocolFactory e dipendenze su di uno specifico protocollo.
		//      Viene usato dal meccanismo di import per definire la struttura di un accordo in base al wsdl, indipendentemente dallo specifico protocollo
		
		if(accordoServizioParteComune.sizePortTypeList()>0){
			throw new ProtocolException("Protocol Info already exists");
		}
		
		byte[] wsdlConcettuale = accordoServizioParteComune.getByteWsdlConcettuale();
		if(wsdlConcettuale!=null){
			_setProtocolInfo(wsdlConcettuale, accordoServizioParteComune, "Concettuale", log);
		}
		else{
			if(accordoServizioParteComune.getByteWsdlLogicoErogatore()!=null){
				_setProtocolInfo(accordoServizioParteComune.getByteWsdlLogicoErogatore(), accordoServizioParteComune, "LogicoErogatore", log);
			}
			if(accordoServizioParteComune.getByteWsdlLogicoFruitore()!=null){
				_setProtocolInfo(accordoServizioParteComune.getByteWsdlLogicoFruitore(), accordoServizioParteComune, "LogicoFruitore", log);
			}
		}
	}
	private void _setProtocolInfo(byte [] wsdlBytes,AccordoServizioParteComune accordoServizioParteComune,String tipo,Logger log) throws ProtocolException{
		
		try{
		
			AbstractXMLUtils xmlUtils = XMLUtils.getInstance(); 
			WSDLUtilities wsdlUtilities = new WSDLUtilities(xmlUtils);
			Document d = xmlUtils.newDocument(wsdlBytes);
			wsdlUtilities.removeTypes(d);
			DefinitionWrapper wsdl = new DefinitionWrapper(d,xmlUtils,false,false);
			
			// port types
			Map<?, ?> porttypesWSDL = wsdl.getAllPortTypes();
			if(porttypesWSDL==null || porttypesWSDL.size()<=0){
				throw new ProtocolException("WSDL"+tipo+" corrotto: non contiene la definizione di nessun port-type");
			}
			if(porttypesWSDL!=null && porttypesWSDL.size()>0){
	
				Iterator<?> it = porttypesWSDL.keySet().iterator();
				while(it.hasNext()){
					javax.xml.namespace.QName key = (javax.xml.namespace.QName) it.next();
					javax.wsdl.PortType ptWSDL = (javax.wsdl.PortType) porttypesWSDL.get(key);
					String ptName = ptWSDL.getQName().getLocalPart();
					
					// cerco portType
					boolean foundPortType = false;
					PortType ptOpenSPCoop = null;
					for (PortType ptCheck : accordoServizioParteComune.getPortTypeList()) {
						if(ptCheck.getNome().equals(ptName)){
							ptOpenSPCoop = ptCheck;
							break;
						}
					}
					
					// cerco binding (se il wsdl contiene la parte implementativa)
					Map<?, ?> bindingsWSDL = wsdl.getAllBindings();
					javax.wsdl.Binding bindingWSDL = null;
					if(bindingsWSDL!=null && bindingsWSDL.size()>0){
						Iterator<?> itBinding = bindingsWSDL.keySet().iterator();
						while (itBinding.hasNext()) {
							javax.xml.namespace.QName tmp = (javax.xml.namespace.QName) itBinding.next();
							if(tmp!=null){
								javax.wsdl.Binding tmpBinding = wsdl.getBinding(tmp);
								if(tmpBinding!=null && tmpBinding.getPortType()!=null &&
										tmpBinding.getPortType().getQName()!=null &&
										ptName.equals(tmpBinding.getPortType().getQName().getLocalPart())){
									bindingWSDL = tmpBinding;
									break;
								}
							}
						}
					}
					
					// se non esiste creo il port-type
					if(ptOpenSPCoop==null){
						ptOpenSPCoop = new PortType();
						ptOpenSPCoop.setNome(ptName);
						ptOpenSPCoop.setProfiloCollaborazione(CostantiRegistroServizi.SINCRONO);
						ptOpenSPCoop.setProfiloPT(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
						ptOpenSPCoop.setFiltroDuplicati(CostantiRegistroServizi.ABILITATO);					
					}
					
					// SoapBinding
					if(bindingWSDL!=null){
						AccordoServizioWrapperUtilities.setPortTypeSoapBindingStyle(bindingWSDL, log, ptOpenSPCoop);
					}
					
					// itero sulle operation
					for(int i=0; i<ptWSDL.getOperations().size();i++){
						javax.wsdl.Operation opWSDL = (javax.wsdl.Operation) ptWSDL.getOperations().get(i);
						String opNome = opWSDL.getName();
						
						boolean foundOperation = false;
						Operation opOpenSPCoop = null;
						for (Operation opCheck : ptOpenSPCoop.getAzioneList()) {
							if(opCheck.getNome().equals(opNome)){
								foundOperation = true; 
								break;
							}
						}
						if(foundOperation){
							continue;// gia definito in un altro wsdl (normale e correlato) ??
						}
						
						// imposto dati base operazione
						opOpenSPCoop = new Operation();
						opOpenSPCoop.setNome(opNome);
						opOpenSPCoop.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
						opOpenSPCoop.setFiltroDuplicati(CostantiRegistroServizi.ABILITATO);
						
						// Prendo la definizione del messaggio di input
						AccordoServizioWrapperUtilities.addMessageInputOperation(opWSDL, log, opOpenSPCoop);
						
						// Prendo la definizione del messaggio di output
						AccordoServizioWrapperUtilities.addMessageOutputOperation(opWSDL, log, opOpenSPCoop);
						
						// profilo di collaborazione (non basta guardare l'output, poiche' puo' avere poi un message vuoto e quindi equivale a non avere l'output)
						//if(opWSDL.getOutput()!=null){
						if(opOpenSPCoop.getMessageOutput()!=null){
							opOpenSPCoop.setProfiloCollaborazione(CostantiRegistroServizi.SINCRONO);
						}else{
							opOpenSPCoop.setProfiloCollaborazione(CostantiRegistroServizi.ONEWAY);
						}
						
						// cerco operation binding (se il wsdl contiene la parte implementativa)
						if(bindingWSDL!=null){
								
							List<?> bindingsOperation = bindingWSDL.getBindingOperations();
							for(int j=0; j<bindingsOperation.size();j++){
								BindingOperation bindingOperationWSDL = (BindingOperation) bindingsOperation.get(j);
								
								if(bindingOperationWSDL.getOperation()!=null && 
										opNome.equals(bindingOperationWSDL.getOperation().getName())){
								
									// SoapBinding Operation
									AccordoServizioWrapperUtilities.
										setOperationSoapBindingInformation(bindingOperationWSDL, log, 
												opOpenSPCoop, ptOpenSPCoop);
									
									// Raccolgo Message-Input
									if(opOpenSPCoop.getMessageInput()!=null){
										AccordoServizioWrapperUtilities.
											setMessageInputSoapBindingInformation(bindingOperationWSDL, log, 
													opOpenSPCoop, ptOpenSPCoop);
									}
									
									// Raccolgo Message-Output
									if(opOpenSPCoop.getMessageOutput()!=null){
										AccordoServizioWrapperUtilities.
											setMessageOutputSoapBindingInformation(bindingOperationWSDL, log, 
													opOpenSPCoop, ptOpenSPCoop);
									}
									
								}
							}
						}
						
						// aggiunto l'azione al port type
						ptOpenSPCoop.addAzione(opOpenSPCoop);
						
					}
					
					if(!foundPortType){
						accordoServizioParteComune.addPortType(ptOpenSPCoop);
					}
					
				}
			}
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	/* ----- Utilita' generali di interpretazione di un Esito ----- */
	
	private String toString(ArchiveEsitoImport archive){
		
		StringBuffer bfEsito = new StringBuffer();
		
		// Pdd
		if(archive.getPdd().size()>0){
			bfEsito.append("PorteDominio (").append(archive.getPdd().size()).append(")\n");
		}
		for (int i = 0; i < archive.getPdd().size(); i++) {
			try{
				ArchiveEsitoImportDetail archivePdd = archive.getPdd().get(i);
				String nomePdd = ((ArchivePdd)archivePdd.getArchiveObject()).getNomePdd();
				bfEsito.append("\t- [").append(nomePdd).append("] ");
				serializeStato(archivePdd, bfEsito);
			}catch(Throwable e){
				bfEsito.append("\t- [").append((i+1)).append("] non importato: ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getPdd().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Soggetti
		if(archive.getSoggetti().size()>0){
			bfEsito.append("Soggetti (").append(archive.getSoggetti().size()).append(")\n");
		}
		for (int i = 0; i < archive.getSoggetti().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveSoggetto = archive.getSoggetti().get(i);
				IDSoggetto idSoggetto =((ArchiveSoggetto)archiveSoggetto.getArchiveObject()).getIdSoggetto();
				bfEsito.append("\t- [").append(idSoggetto.toString()).append("] ");
				serializeStato(archiveSoggetto, bfEsito);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] non importato: ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getSoggetti().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Servizi Applicativi
		if(archive.getServiziApplicativi().size()>0){
			bfEsito.append("ServiziApplicativi (").append(archive.getServiziApplicativi().size()).append(")\n");
		}
		for (int i = 0; i < archive.getServiziApplicativi().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveServizioApplicativo = archive.getServiziApplicativi().get(i);
				IDServizioApplicativo idServizioApplicativo = ((ArchiveServizioApplicativo)archiveServizioApplicativo.getArchiveObject()).getIdServizioApplicativo();
				bfEsito.append("\t- [").append(idServizioApplicativo.getIdSoggettoProprietario().toString()).
						append("_").append(idServizioApplicativo.getNome()).append("] ");
				serializeStato(archiveServizioApplicativo, bfEsito);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] non importato: ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getServiziApplicativi().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Accordi di Cooperazione
		if(archive.getAccordiCooperazione().size()>0){
			bfEsito.append("Accordi di Cooperazione (").append(archive.getAccordiCooperazione().size()).append(")\n");
		}
		for (int i = 0; i < archive.getAccordiCooperazione().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveAccordoCooperazione = archive.getAccordiCooperazione().get(i);
				IDAccordoCooperazione idAccordoCooperazione = ((ArchiveAccordoCooperazione)archiveAccordoCooperazione.getArchiveObject()).getIdAccordoCooperazione();
				String uriAccordo = this.idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordoCooperazione);
				bfEsito.append("\t- [").append(uriAccordo).append("] ");
				serializeStato(archiveAccordoCooperazione, bfEsito);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] non importato: ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getAccordiCooperazione().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Accordi di Servizio Parte Comune
		if(archive.getAccordiServizioParteComune().size()>0){
			bfEsito.append("Accordi di Servizio Parte Comune (").append(archive.getAccordiServizioParteComune().size()).append(")\n");
		}
		for (int i = 0; i < archive.getAccordiServizioParteComune().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveAccordoServizioParteComune = archive.getAccordiServizioParteComune().get(i);
				IDAccordo idAccordo = ((ArchiveAccordoServizioParteComune)archiveAccordoServizioParteComune.getArchiveObject()).getIdAccordoServizioParteComune();
				String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
				bfEsito.append("\t- [").append(uriAccordo).append("] ");
				serializeStato(archiveAccordoServizioParteComune, bfEsito);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] non importato: ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getAccordiServizioParteComune().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Accordi di Servizio Parte Specifica (implementano accordi di servizio parte comune)
		if(archive.getAccordiServizioParteSpecifica().size()>0){
			bfEsito.append("Accordi di Servizio Parte Specifica (").append(archive.getAccordiServizioParteSpecifica().size()).append(")\n");
		}
		for (int i = 0; i < archive.getAccordiServizioParteSpecifica().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveAccordoServizioParteSpecifica = archive.getAccordiServizioParteSpecifica().get(i);
				IDAccordo idAccordo = ((ArchiveAccordoServizioParteSpecifica)archiveAccordoServizioParteSpecifica.getArchiveObject()).getIdAccordoServizioParteSpecifica();
				String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
				bfEsito.append("\t- [").append(uriAccordo).append("] ");
				serializeStato(archiveAccordoServizioParteSpecifica, bfEsito);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] non importato: ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getAccordiServizioParteSpecifica().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Accordi di Servizio Composto
		if(archive.getAccordiServizioComposto().size()>0){
			bfEsito.append("Accordi di Servizio Composto (").append(archive.getAccordiServizioComposto().size()).append(")\n");
		}
		for (int i = 0; i < archive.getAccordiServizioComposto().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveAccordoServizioComposto = archive.getAccordiServizioComposto().get(i);
				IDAccordo idAccordo = ((ArchiveAccordoServizioComposto)archiveAccordoServizioComposto.getArchiveObject()).getIdAccordoServizioParteComune();
				String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
				bfEsito.append("\t- [").append(uriAccordo).append("] ");
				serializeStato(archiveAccordoServizioComposto, bfEsito);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] non importato: ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getAccordiServizioComposto().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Accordi di Servizio Parte Specifica (implementano accordi di servizio composto)
		if(archive.getAccordiServizioParteSpecificaServiziComposti().size()>0){
			bfEsito.append("Accordi di Servizio Parte Specifica (").
				append(archive.getAccordiServizioParteSpecificaServiziComposti().size()).append(") [accordi di servizio composto]\n");
		}
		for (int i = 0; i < archive.getAccordiServizioParteSpecificaServiziComposti().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveAccordoServizioParteSpecifica = archive.getAccordiServizioParteSpecificaServiziComposti().get(i);
				IDAccordo idAccordo = ((ArchiveAccordoServizioParteSpecifica)archiveAccordoServizioParteSpecifica.getArchiveObject()).getIdAccordoServizioParteSpecifica();
				String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
				bfEsito.append("\t- [").append(uriAccordo).append("] ");
				serializeStato(archiveAccordoServizioParteSpecifica, bfEsito);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] non importato: ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getAccordiServizioParteSpecificaServiziComposti().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Fruitori
		if(archive.getAccordiFruitori().size()>0){
			bfEsito.append("Fruitori (").append(archive.getAccordiFruitori().size()).append(")\n");
		}
		for (int i = 0; i < archive.getAccordiFruitori().size(); i++) {
			try{
				ArchiveEsitoImportDetail archiveFruitore = archive.getAccordiFruitori().get(i);
				IDAccordo idAccordo = ((ArchiveFruitore)archiveFruitore.getArchiveObject()).getIdAccordoServizioParteSpecifica();
				String uriAccordo = this.idAccordoFactory.getUriFromIDAccordo(idAccordo);
				IDSoggetto idFruitore = ((ArchiveFruitore)archiveFruitore.getArchiveObject()).getIdSoggettoFruitore();
				bfEsito.append("\t- ["+idFruitore+"] -> [").append(uriAccordo).append("] ");
				serializeStato(archiveFruitore, bfEsito);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] non importato: ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getAccordiFruitori().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// PorteDelegate
		if(archive.getPorteDelegate().size()>0){
			bfEsito.append("PorteDelegate (").append(archive.getPorteDelegate().size()).append(")\n");
		}
		for (int i = 0; i < archive.getPorteDelegate().size(); i++) {
			try{
				ArchiveEsitoImportDetail archivePortaDelegata = archive.getPorteDelegate().get(i);
				IDPortaDelegata idPortaDelegata = ((ArchivePortaDelegata)archivePortaDelegata.getArchiveObject()).getIdPortaDelegata();
				IDSoggetto idProprietario = ((ArchivePortaDelegata)archivePortaDelegata.getArchiveObject()).getIdSoggettoProprietario();
				bfEsito.append("\t- ["+idProprietario+"]["+idPortaDelegata.getLocationPD()+"] ");
				serializeStato(archivePortaDelegata, bfEsito);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] non importato: ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getPorteDelegate().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// PorteApplicative
		if(archive.getPorteApplicative().size()>0){
			bfEsito.append("PorteApplicative (").append(archive.getPorteApplicative().size()).append(")\n");
		}
		for (int i = 0; i < archive.getPorteApplicative().size(); i++) {
			try{
				ArchiveEsitoImportDetail archivePortaApplicativa = archive.getPorteApplicative().get(i);
				IDPortaApplicativaByNome idPortaApplicativa = ((ArchivePortaApplicativa)archivePortaApplicativa.getArchiveObject()).getIdPortaApplicativaByNome();
				bfEsito.append("\t- ["+idPortaApplicativa.getSoggetto()+"]["+idPortaApplicativa.getNome()+"] ");
				serializeStato(archivePortaApplicativa, bfEsito);
			}catch(Exception e){
				bfEsito.append("\t- [").append((i+1)).append("] non importato: ").append(e.getMessage());
			}
			bfEsito.append("\n");
		}
		if(archive.getPorteApplicative().size()>0){
			bfEsito.append("\n");	
		}
		
		
		// Configurazione
		if(archive.getConfigurazionePdD()!=null){
			bfEsito.append("Configurazione\n");
			try{
				ArchiveEsitoImportDetailConfigurazione configurazione = archive.getConfigurazionePdD();
				bfEsito.append("\t- ");
				serializeStato(configurazione, bfEsito);
			}catch(Exception e){
				bfEsito.append("\t- non importata: ").append(e.getMessage());
			}
			bfEsito.append("\n");
			bfEsito.append("\n");
		}
		
		
		return bfEsito.toString();
	}
	private void serializeStato(ArchiveEsitoImportDetail detail,StringBuffer bfEsito){
		String stateDetail = "";
		if(detail.getStateDetail()!=null){
			stateDetail = detail.getStateDetail();
		}
		switch (detail.getState()) {
		case UPDATE_NOT_PERMISSED:
			bfEsito.append("non importato: già presente (aggiornamento non abilitato)").append(stateDetail);
			break;
		case ERROR:
			if(detail.getStateDetail()!=null){
				stateDetail = " ["+detail.getStateDetail()+"]";
			}
			bfEsito.append("non importato"+stateDetail+": ").append(detail.getException().getMessage());
			break;
		case CREATED:
			bfEsito.append("importato correttamente").append(stateDetail);
			break;
		case UPDATED:
			bfEsito.append("già presente, aggiornato correttamente").append(stateDetail);
			break;
		}
	}
	private void serializeStato(ArchiveEsitoImportDetailConfigurazione detail,StringBuffer bfEsito){
		String stateDetail = "";
		if(detail.getStateDetail()!=null){
			stateDetail = detail.getStateDetail();
		}
		switch (detail.getState()) {
		case UPDATE_NOT_PERMISSED:
			// Stato mai usato per questo oggetto
			break;
		case ERROR:
			if(detail.getStateDetail()!=null){
				stateDetail = " ["+detail.getStateDetail()+"]";
			}
			bfEsito.append("non importata"+stateDetail+": ").append(detail.getException().getMessage());
			break;
		case CREATED:
			// Stato mai usato per questo oggetto
			break;
		case UPDATED:
			bfEsito.append("aggiornata correttamente").append(stateDetail);
			break;
		}
	}
	
	
	
	
	
	
	
	
	
	/* ----- Import ----- */
	
	@Override
	public List<ImportMode> getImportModes() throws ProtocolException {
		List<ImportMode> list = new ArrayList<ImportMode>();
		list.add(Costanti.OPENSPCOOP_IMPORT_ARCHIVE_MODE);
		return list;
	}

	@Override
	public Archive importArchive(byte[]archive,ArchiveMode mode,ArchiveModeType type,
			IRegistryReader registryReader,boolean validationDocuments,
			MapPlaceholder placeholder) throws ProtocolException {
		
		ZIPUtils zipUtils = new ZIPUtils(this.protocolFactory.getLogger(),registryReader);
		return zipUtils.getArchive(archive,placeholder,validationDocuments);
		
	}
	
	@Override
	public Archive importArchive(InputStream archive,ArchiveMode mode,ArchiveModeType type,
			IRegistryReader registryReader,boolean validationDocuments,
			MapPlaceholder placeholder) throws ProtocolException {
		
		try{
			ZIPUtils zipUtils = new ZIPUtils(this.protocolFactory.getLogger(),registryReader);
			return zipUtils.getArchive(archive,placeholder,validationDocuments);
		}finally{
			try{
				if(archive!=null){
					archive.close();
				}
			}catch(Exception eClose){}
		}
		
	}
	
	@Override
	public String toString(ArchiveEsitoImport esito, ArchiveMode archiveMode) throws ProtocolException{
		return this.toString(esito);
	}
	
	
	
	
	
	
	
	/* ----- Export ----- */
	
	@Override
	public List<ExportMode> getExportModes(ArchiveType archiveType) throws ProtocolException {
		List<ExportMode> list = new ArrayList<ExportMode>();
		return list;
	}
	
	@Override
	public byte[] exportArchive(Archive archive, ArchiveMode mode,
			IRegistryReader registroReader)
			throws ProtocolException {
		throw new ProtocolException("Not Implemented");
	}

	@Override
	public void exportArchive(Archive archive, OutputStream out, ArchiveMode mode,
			IRegistryReader registroReader)
			throws ProtocolException {
		throw new ProtocolException("Not Implemented");
	}
	




}
