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

package org.openspcoop2.protocol.basic.archive;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.BindingOperation;

import org.slf4j.Logger;
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
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoDelete;
import org.openspcoop2.protocol.sdk.archive.ArchiveEsitoImport;
import org.openspcoop2.protocol.sdk.archive.ArchiveMode;
import org.openspcoop2.protocol.sdk.archive.ArchiveModeType;
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
	protected EsitoUtils esitoUtils;
	public BasicArchive(IProtocolFactory protocolFactory){
		this.protocolFactory = protocolFactory;
		this.idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
		this.idAccordoFactory = IDAccordoFactory.getInstance();
		this.esitoUtils = new EsitoUtils(protocolFactory);
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
							foundPortType = true;
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
		return this.esitoUtils.toString(esito,false,true);
	}
	
	@Override
	public String toString(ArchiveEsitoDelete esito, ArchiveMode archiveMode) throws ProtocolException{
		return this.esitoUtils.toString(esito,false,false);
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
