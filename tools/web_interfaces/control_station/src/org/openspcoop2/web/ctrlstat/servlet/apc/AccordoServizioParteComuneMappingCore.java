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
package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.xml.MessageXMLUtils;
import org.openspcoop2.message.xml.XMLDiff;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.rest.ApiFactory;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.wsdl.WSDLUtilities;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.XSDUtils;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaAdd;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * AccordoServizioParteComuneMappingCore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoServizioParteComuneMappingCore extends ControlStationCore {

	protected AccordoServizioParteComuneMappingCore(ControlStationCore core) throws Exception {
		super(core);
	}
	
	public void mappingAutomatico(String protocollo , AccordoServizioParteComune as, boolean validazioneDocumenti) throws DriverRegistroServiziException {
		String nomeMetodo = "mappingAutomatico";
		try {
			IProtocolFactory<?> protocol = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
			protocol.createArchive().setProtocolInfo(as);
		}catch (Exception e) {
			if(validazioneDocumenti) {
				ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
				throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
			}
		}
	}

	
	public void popolaResourceDaUnAltroASPC(AccordoServizioParteComune aspcDestinazione, AccordoServizioParteComune aspcSorgente, 
			boolean aggiornaRisorseEsistenti, boolean eliminaRisorseNonPresentiNuovaInterfaccia, List<IDResource> risorseEliminate) throws DriverRegistroServiziException{
		String nomeMetodo = "popolaResourceDaUnAltroASPC";
		try {
			if(aspcSorgente.sizeResourceList() > 0){
				for (Resource nuovoResource : aspcSorgente.getResourceList()) {
					
					Resource vecchioResourceByMethodPath = find(nuovoResource, aspcDestinazione.getResourceList());
					
					// non ho trovato l'elemento corrente nel aspc destinazione
					if(vecchioResourceByMethodPath == null){
						
						// prima di aggiungerlo, avendolo cercato per method/path verifico che lo stesso nome non sia stato utilizzato per unl'altra risorsa
						boolean foundName = true;
						int index = 2;
						while(foundName) {
							foundName = false;
							for (Resource vecchioResourceTMP : aspcDestinazione.getResourceList()) {
								if(vecchioResourceTMP.getNome().equals(nuovoResource.getNome())){
									foundName = true;
									break;
								}
							}
							
							if(foundName) {
								nuovoResource.setNome(nuovoResource.getNome()+"_"+index);
								index++;
							}
						}
						
						aspcDestinazione.addResource(nuovoResource);
					} else {
						if(aggiornaRisorseEsistenti) {
							// ho trovato l'elemento, aggiorno i valori  rimpiazzando la risorsa
							Resource vecchiaResource = null;
							for (int i = 0; i < aspcDestinazione.sizeResourceList(); i++) {
								if(aspcDestinazione.getResource(i).getNome().equals(vecchioResourceByMethodPath.getNome())) {
									vecchiaResource = aspcDestinazione.removeResource(i);
									break;
								}
							}
							aspcDestinazione.addResource(nuovoResource);
							
							if(vecchiaResource!=null) {

								// riporto eventuali properties
								if(vecchiaResource.sizeProtocolPropertyList()>0) {
									for (int i = 0; i < vecchiaResource.sizeProtocolPropertyList(); i++) {
										nuovoResource.addProtocolProperty(vecchiaResource.getProtocolProperty(i));		
									}
								}
								
								if(!CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(vecchiaResource.getProfAzione())) {
									continue;
								}

								boolean ridefinisci = false;
								if(vecchiaResource.getConfermaRicezione()!=null && org.openspcoop2.core.registry.constants.StatoFunzionalita.ABILITATO.equals(vecchiaResource.getConfermaRicezione())) {
									nuovoResource.setConfermaRicezione(vecchiaResource.getConfermaRicezione());
									ridefinisci = true;
								}
								if(vecchiaResource.getConsegnaInOrdine()!=null && org.openspcoop2.core.registry.constants.StatoFunzionalita.ABILITATO.equals(vecchiaResource.getConsegnaInOrdine())) {
									nuovoResource.setConsegnaInOrdine(vecchiaResource.getConsegnaInOrdine());
									ridefinisci = true;
								}
								if(vecchiaResource.getIdCollaborazione()!=null && org.openspcoop2.core.registry.constants.StatoFunzionalita.ABILITATO.equals(vecchiaResource.getIdCollaborazione())) {
									nuovoResource.setIdCollaborazione(vecchiaResource.getIdCollaborazione());
									ridefinisci = true;
								}
								if(vecchiaResource.getIdRiferimentoRichiesta()!=null && org.openspcoop2.core.registry.constants.StatoFunzionalita.ABILITATO.equals(vecchiaResource.getIdRiferimentoRichiesta())) {
									nuovoResource.setIdRiferimentoRichiesta(vecchiaResource.getIdRiferimentoRichiesta());
									ridefinisci = true;
								}
								if(vecchiaResource.getScadenza()!=null && !"".equals(vecchiaResource.getScadenza())) {
									nuovoResource.setScadenza(vecchiaResource.getScadenza());
									ridefinisci = true;
								}
								if(vecchiaResource.getDescrizione()!=null && !"".equals(vecchiaResource.getDescrizione())) {
									nuovoResource.setDescrizione(vecchiaResource.getDescrizione());
									ridefinisci = true;
								}
								// filtro duplicati gestito nel BasicArchive.setProtocolInfo
								if(vecchiaResource.getFiltroDuplicati()!=null &&
									!vecchiaResource.getFiltroDuplicati().equals(nuovoResource.getFiltroDuplicati())) {
									nuovoResource.setFiltroDuplicati(vecchiaResource.getFiltroDuplicati());
									ridefinisci = true;
								}
								if(ridefinisci) {
									nuovoResource.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
								}
								
							}
						}
					}
				}
			}
			
			if(eliminaRisorseNonPresentiNuovaInterfaccia) {
				
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromAccordo(aspcDestinazione);
				
				if(aspcDestinazione.sizeResourceList() > 0){
					List<Resource> risorseDaEliminare = new ArrayList<>();
					for (Resource oldResource : aspcDestinazione.getResourceList()) {
						
						Resource find = find(oldResource, aspcSorgente.getResourceList());
						if(find==null) {
							risorseDaEliminare.add(oldResource);
						}
						
					}
					if(!risorseDaEliminare.isEmpty()) {
						while(!risorseDaEliminare.isEmpty()) {
							Resource risorsaDaEliminare = risorseDaEliminare.remove(0);
							for (int i = 0; i < aspcDestinazione.sizeResourceList(); i++) {
								if(aspcDestinazione.getResource(i).getNome().equals(risorsaDaEliminare.getNome())) {
									aspcDestinazione.removeResource(i);
									IDResource idResource = new IDResource();
									idResource.setIdAccordo(idAccordo);
									idResource.setNome(risorsaDaEliminare.getNome());
									
									risorseEliminate.add(idResource);
									
									break;
								}
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw e;
		}
	}
	
	private Resource find(Resource resourceSearched, List<Resource> resources) {
		if(resources==null || resources.isEmpty()) {
			return null;
		}
		Resource resourceByMethodPath = null;
		for (Resource resourceTMP : resources) {
			
			if(resourceTMP.getMethod()==null) {
				if(resourceSearched.getMethod()!=null) {
					continue;
				}
			}else {
				if(!resourceTMP.getMethod().equals(resourceSearched.getMethod())) {
					continue;
				}
			}
			
			if(resourceTMP.getPath()==null) {
				if(resourceSearched.getPath()!=null) {
					continue;
				}
			}else {
				if(!resourceTMP.getPath().equals(resourceSearched.getPath())) {
					continue;
				}
			}
			
			resourceByMethodPath = resourceTMP;
			break;
		}	
		return resourceByMethodPath;
	}
	
	public void popolaPorttypeOperationDaUnAltroASPC(AccordoServizioParteComune aspcDestinazione, AccordoServizioParteComune aspcSorgente, 
			boolean aggiornaServiziAzioniEsistenti, boolean eliminaServiziAzioniNonPresentiNuovaInterfaccia,
			List<IDPortType> portTypeEliminati, List<IDPortTypeAzione> operationEliminate) throws DriverRegistroServiziException{
		String nomeMetodo = "popolaPorttypeOperationDaUnAltroASPC";
		try {
			IDAccordo idAccordo = null;
			if(eliminaServiziAzioniNonPresentiNuovaInterfaccia) {
				idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromAccordo(aspcDestinazione);
			}
			
			if(aspcSorgente.sizePortTypeList() > 0){
				for (PortType nuovoPortType : aspcSorgente.getPortTypeList()) {
					PortType vecchioPortType = null;
					for (PortType vecchioPortTypeTMP : aspcDestinazione.getPortTypeList()) {
						if(vecchioPortTypeTMP.getNome().equals(nuovoPortType.getNome())){
							vecchioPortType = vecchioPortTypeTMP;
							break;
						}
					}
					
					// non ho trovato l'elemento corrente nel aspc destinazione
					if(vecchioPortType == null){
						aspcDestinazione.addPortType(nuovoPortType);
					} else {
						if(aggiornaServiziAzioniEsistenti) {
							// ho trovato l'elemento, aggiorno i valori  rimpiazzando la risorsa
							PortType oldPT = null;
							for (int i = 0; i < aspcDestinazione.sizePortTypeList(); i++) {
								if(aspcDestinazione.getPortType(i).getNome().equals(vecchioPortType.getNome())) {
									oldPT = aspcDestinazione.removePortType(i);
									break;
								}
							}
							aspcDestinazione.addPortType(nuovoPortType);
							
							if(oldPT!=null) {
								
								// riporto eventuali properties
								if(oldPT.sizeProtocolPropertyList()>0) {
									for (int i = 0; i < oldPT.sizeProtocolPropertyList(); i++) {
										nuovoPortType.addProtocolProperty(oldPT.getProtocolProperty(i));		
									}
								}
								
								// riporto funzionalità non gestite nel BasicArchive.setProtocolInfo
								for (Operation vecchiaAzione : oldPT.getAzioneList()) {
									
									if(!CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(vecchiaAzione.getProfAzione()) && vecchiaAzione.sizeProtocolPropertyList()<=0 ) {
										continue;
									}
																			
									Operation nuovaAzione = null;
									for (Operation azCheck : nuovoPortType.getAzioneList()) {
										if(azCheck.getNome().equals(vecchiaAzione.getNome())) {
											nuovaAzione = azCheck;
											break;
										}
									}
									
									if(nuovaAzione!=null && vecchiaAzione.sizeProtocolPropertyList()>0) {
										
										// riporto eventuali properties
										for (int i = 0; i < vecchiaAzione.sizeProtocolPropertyList(); i++) {
											nuovaAzione.addProtocolProperty(vecchiaAzione.getProtocolProperty(i));		
										}
																				
									}
									
									if(nuovaAzione!=null && CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(vecchiaAzione.getProfAzione())) {
										
										boolean ridefinisci = false;
										if(vecchiaAzione.getConfermaRicezione()!=null && org.openspcoop2.core.registry.constants.StatoFunzionalita.ABILITATO.equals(vecchiaAzione.getConfermaRicezione())) {
											nuovaAzione.setConfermaRicezione(vecchiaAzione.getConfermaRicezione());
											ridefinisci = true;
										}
										if(vecchiaAzione.getConsegnaInOrdine()!=null && org.openspcoop2.core.registry.constants.StatoFunzionalita.ABILITATO.equals(vecchiaAzione.getConsegnaInOrdine())) {
											nuovaAzione.setConsegnaInOrdine(vecchiaAzione.getConsegnaInOrdine());
											ridefinisci = true;
										}
										if(vecchiaAzione.getIdCollaborazione()!=null && org.openspcoop2.core.registry.constants.StatoFunzionalita.ABILITATO.equals(vecchiaAzione.getIdCollaborazione())) {
											nuovaAzione.setIdCollaborazione(vecchiaAzione.getIdCollaborazione());
											ridefinisci = true;
										}
										if(vecchiaAzione.getIdRiferimentoRichiesta()!=null && org.openspcoop2.core.registry.constants.StatoFunzionalita.ABILITATO.equals(vecchiaAzione.getIdRiferimentoRichiesta())) {
											nuovaAzione.setIdRiferimentoRichiesta(vecchiaAzione.getIdRiferimentoRichiesta());
											ridefinisci = true;
										}
										if(vecchiaAzione.getScadenza()!=null && !"".equals(vecchiaAzione.getScadenza())) {
											nuovaAzione.setScadenza(vecchiaAzione.getScadenza());
											ridefinisci = true;
										}
										// filtro duplicati gestito nel BasicArchive.setProtocolInfo
										if(vecchiaAzione.getFiltroDuplicati()!=null &&
											!vecchiaAzione.getFiltroDuplicati().equals(nuovaAzione.getFiltroDuplicati())) {
											nuovaAzione.setFiltroDuplicati(vecchiaAzione.getFiltroDuplicati());
											ridefinisci = true;
										}
										if(ridefinisci) {
											nuovaAzione.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
											if(nuovaAzione.getProfiloCollaborazione()!=null) {
												nuovaAzione.setProfiloCollaborazione(nuovaAzione.getProfiloCollaborazione());
											}
											else if(nuovoPortType.getProfiloCollaborazione()!=null) {
												nuovaAzione.setProfiloCollaborazione(nuovoPortType.getProfiloCollaborazione());
											}
											else {
												nuovaAzione.setProfiloCollaborazione(aspcDestinazione.getProfiloCollaborazione());
											}
										}
									}
								}
							}
						
							// riporto le azioni presenti nel vecchio e non nel nuovo.
							if(nuovoPortType.sizeAzioneList() > 0){
								for (Operation vecchiaAzione : vecchioPortType.getAzioneList()) {
									boolean find = false;
									for (Operation nuovaAzione : nuovoPortType.getAzioneList()) {
										if(nuovaAzione.getNome().equals(vecchiaAzione.getNome())) {
											find = true;
											break;
										}
									}
									if(!find) {
										if(eliminaServiziAzioniNonPresentiNuovaInterfaccia) {
											IDPortType idPortType = new IDPortType();
											idPortType.setIdAccordo(idAccordo);
											idPortType.setNome(vecchioPortType.getNome());
											
											IDPortTypeAzione idPortTypeAzione = new IDPortTypeAzione();
											idPortTypeAzione.setIdPortType(idPortType);
											idPortTypeAzione.setNome(vecchiaAzione.getNome());
											operationEliminate.add(idPortTypeAzione);
										}
										else {
											nuovoPortType.addAzione(vecchiaAzione);
										}
									}
								}
							}
							else {
								if(vecchioPortType.sizeAzioneList()>0) {
									for (Operation op : vecchioPortType.getAzioneList()) {
										if(eliminaServiziAzioniNonPresentiNuovaInterfaccia) {
											IDPortType idPortType = new IDPortType();
											idPortType.setIdAccordo(idAccordo);
											idPortType.setNome(vecchioPortType.getNome());
											
											IDPortTypeAzione idPortTypeAzione = new IDPortTypeAzione();
											idPortTypeAzione.setIdPortType(idPortType);
											idPortTypeAzione.setNome(op.getNome());
											operationEliminate.add(idPortTypeAzione);
										}
										else {
											nuovoPortType.addAzione(op);
										}
									}
								}
							}
						}
						else {
							// Aggiungo solo le azioni nuove
							if(nuovoPortType.sizeAzioneList() > 0){
								for (Operation nuovaAzione : nuovoPortType.getAzioneList()) {
									boolean find = false;
									for (Operation vecchiaAzione : vecchioPortType.getAzioneList()) {
										if(nuovaAzione.getNome().equals(vecchiaAzione.getNome())) {
											find = true;
											break;
										}
									}
									if(!find) {
										vecchioPortType.addAzione(nuovaAzione);
									}
								}
							}
						}
					}
				}
			}
			if(eliminaServiziAzioniNonPresentiNuovaInterfaccia) {
				
				// elimino port type non più presenti
				if(aspcDestinazione.sizePortTypeList() > 0){
					List<PortType> portTypeDaEliminare = new ArrayList<>();
					for (PortType oldPortType : aspcDestinazione.getPortTypeList()) {
						
						if(aspcSorgente.sizePortTypeList() > 0){
							boolean find = false;
							for (PortType nuovoPortType : aspcSorgente.getPortTypeList()) {
								if(nuovoPortType.getNome().equals(oldPortType.getNome())){
									find = true;
									break;
								}
							}
							if(!find) {
								portTypeDaEliminare.add(oldPortType);
							}
						}
						
					}
					if(!portTypeDaEliminare.isEmpty()) {
						while(!portTypeDaEliminare.isEmpty()) {
							PortType ptDaEliminare = portTypeDaEliminare.remove(0);
							for (int i = 0; i < aspcDestinazione.sizePortTypeList(); i++) {
								if(aspcDestinazione.getPortType(i).getNome().equals(ptDaEliminare.getNome())) {
									aspcDestinazione.removePortType(i);
									
									IDPortType idPortType = new IDPortType();
									idPortType.setIdAccordo(idAccordo);
									idPortType.setNome(ptDaEliminare.getNome());
									portTypeEliminati.add(idPortType);
									
									break;
								}
							}
						}
					}
				}
				
				// elimino azioni non più presenti
				if(aspcDestinazione.sizePortTypeList() > 0){
					for (PortType oldPortType : aspcDestinazione.getPortTypeList()) {
						
						List<Operation> operationDaEliminare = new ArrayList<Operation>();
						if(oldPortType.sizeAzioneList() > 0){
							for (Operation oldOperation : oldPortType.getAzioneList()) {
								
								// find operation in new
								PortType newPortType = null;
								for (PortType nuovoPortType : aspcSorgente.getPortTypeList()) {
									if(nuovoPortType.getNome().equals(oldPortType.getNome())){
										newPortType = nuovoPortType;
										break;
									}
								}
								boolean find = false;
								if(newPortType!=null && newPortType.sizeAzioneList()>0) {
									for (Operation nuovaAzione : newPortType.getAzioneList()) {
										if(nuovaAzione.getNome().equals(oldOperation.getNome())) {
											find = true;
											break;
										}
									}
								}
								if(!find) {
									operationDaEliminare.add(oldOperation);
								}
							}
						}
						while(!operationDaEliminare.isEmpty()) {
							Operation opDaEliminare = operationDaEliminare.remove(0);
							for (int i = 0; i < oldPortType.sizeAzioneList(); i++) {
								if(oldPortType.getAzione(i).getNome().equals(opDaEliminare.getNome())) {
									oldPortType.removeAzione(i);
									
									IDPortType idPortType = new IDPortType();
									idPortType.setIdAccordo(idAccordo);
									idPortType.setNome(oldPortType.getNome());
									
									IDPortTypeAzione idPortTypeAzione = new IDPortTypeAzione();
									idPortTypeAzione.setIdPortType(idPortType);
									idPortTypeAzione.setNome(opDaEliminare.getNome());
									operationEliminate.add(idPortTypeAzione);
									
									break;
								}
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw e;
		}
	}
	
	private static final String WSDL_PREFIX = "WsdlTypes_";
	private static final String COMPARE_ERROR = "Compare external failed: ";
	
	public void estraiSchemiFromWSDLTypesAsAllegati(AccordoServizioParteComune as, byte[] wsdl, String tipoWSDL, Map<String, byte[]> schemiAggiuntiInQuestaOperazione) throws Exception{
				
		String nomeMetodo = "addSchemiFromWSDLTypesAsAllegati";
		try {
			
			// Allegati
			List<byte[]> schemiPresentiInternamenteTypes = new ArrayList<>();
			List<String> nomiSchemiPresentiInternamenteTypes = new ArrayList<>();
			
			try{
				
				AbstractXMLUtils xmlUtils = MessageXMLUtils.DEFAULT; 
				XSDUtils xsdUtils = new XSDUtils(xmlUtils);
				WSDLUtilities wsdlUtilities = new WSDLUtilities(xmlUtils);
				
				ArchiviCore archiviCore = null;
				
				Document dConAllegati = xmlUtils.newDocument(wsdl);
				
				Map<String, String> declarationNamespacesWSDL = xmlUtils.getNamespaceDeclaration(dConAllegati.getDocumentElement());
				
				List<Node> schemi = wsdlUtilities.getSchemiXSD(dConAllegati);
				if(!schemi.isEmpty()){
					wsdlUtilities.removeSchemiIntoTypes(dConAllegati);
					
					for (int i = 0; i < schemi.size(); i++) {
						
						Node schema = schemi.get(i);
						
						// NOTA: Volendo si potrebbe utilizzare la solita gestione anche per schemi che hanno solo gli include
						//		  Al momento dell'implementazione non avevo chiaro però se era utile avere tra gli XSD Collections (utilizzati per la validazione)
						//		  Lo schema originale che contiene gli include (per cambi di namespace)
						//		  Quindi ho preferito tenere l'originale schema con gli include tra gli schemi e qua importarlo.
						boolean schemaWithOnlyImport = xsdUtils.isSchemaWithOnlyImports(schema);
						if(schemaWithOnlyImport){
							// riaggiungo l'import
							wsdlUtilities.addSchemaIntoTypes(dConAllegati, schema);
							continue;
						}
						
						if(archiviCore==null)
							archiviCore = new ArchiviCore(this);
						
						String targetNamespace = xsdUtils.getTargetNamespace(schema);
						if(targetNamespace!=null){
											
							if(declarationNamespacesWSDL!=null && declarationNamespacesWSDL.size()>0){
								xmlUtils.addNamespaceDeclaration(declarationNamespacesWSDL, (Element) schema);
							}	
							
							String nomeSchema = null;
							if(schemiAggiuntiInQuestaOperazione!=null && schemiAggiuntiInQuestaOperazione.size()>0){
								for (String nomeFile : schemiAggiuntiInQuestaOperazione.keySet()) {
									byte[] content = schemiAggiuntiInQuestaOperazione.get(nomeFile);
									// check se si tratta di questo documento
									try{
										String tmp = checkXsdAlreadyExists(xmlUtils, content, nomeFile, schema);
										if(tmp!=null){
											nomeSchema = tmp;
											break;
										}
									}catch(Exception t){
										logError(COMPARE_ERROR+t.getMessage(),t);
									}
								}
							}
							if(nomeSchema==null &&
								as.getByteWsdlDefinitorio()!=null){
								// check se si tratta di questo documento
								try{
									String tmp = checkXsdAlreadyExists(xmlUtils, as.getByteWsdlDefinitorio(), 
											Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_INTERFACCIA_DEFINITORIA, schema);
									if(tmp!=null){
										nomeSchema = tmp;
									}
								}catch(Exception t){
									logError(COMPARE_ERROR+t.getMessage(),t);
								}
							}
							if(nomeSchema==null &&
								as.sizeAllegatoList()>0){
								for (Documento docTMP : as.getAllegatoList()) {
									String nomeDocumento = docTMP.getFile();
									try{
										Documento doc = archiviCore.getDocumento(docTMP.getId(),true);
										if(xsdUtils.isXSDSchema(doc.getByteContenuto())){
											// check se si tratta di questo documento
											String tmp = checkXsdAlreadyExists(xmlUtils, doc.getByteContenuto(), nomeDocumento, schema);
											if(tmp!=null){
												nomeSchema = tmp;
												break;
											}
										}
									}catch(Exception t){
										logError(COMPARE_ERROR+t.getMessage(),t);
									}
								}
							}
							if(nomeSchema==null &&
								as.sizeSpecificaSemiformaleList()>0){
								for (Documento docTMP : as.getSpecificaSemiformaleList()) {
									String nomeDocumento = docTMP.getFile();
									try{
										Documento doc = archiviCore.getDocumento(docTMP.getId(),true);
										if(xsdUtils.isXSDSchema(doc.getByteContenuto())){
											// check se si tratta di questo documento
											String tmp = checkXsdAlreadyExists(xmlUtils, doc.getByteContenuto(), nomeDocumento, schema);
											if(tmp!=null){
												nomeSchema = tmp;
												break;
											}
										}
									}catch(Exception t){
										logError(COMPARE_ERROR+t.getMessage(),t);
									}
								}
							}
							
							boolean alreadyExistsSchema = (nomeSchema!=null);
							
							if(nomeSchema==null){
								// build new nome
								int index = 1;
								nomeSchema = WSDL_PREFIX+(index)+".xsd";
								while(index<10000){ // 10000 allegati??
									boolean found = false;
									if(nomiSchemiPresentiInternamenteTypes.contains(nomeSchema)){
										found = true;
									}
									else{
										for (Documento vecchioAllegatoTMP : as.getAllegatoList()) {
											if(vecchioAllegatoTMP.getFile().startsWith(WSDL_PREFIX) && vecchioAllegatoTMP.getFile().equals(nomeSchema)){
												found = true;
												break;
											}
										}
									}
									if(!found){
										break;
									}
									index++;
									nomeSchema = WSDL_PREFIX+(index)+".xsd";
								}
							}
							
							wsdlUtilities.addImportSchemaIntoTypes(dConAllegati, targetNamespace, nomeSchema);
							
							if(!alreadyExistsSchema){
								
								nomiSchemiPresentiInternamenteTypes.add(nomeSchema);
								schemiPresentiInternamenteTypes.add(xmlUtils.toByteArray(schema));

							}
														
						}
						else{
							try{
								logError("Presente schema senza targetNamespace? (Viene usato l'originale) ["+xmlUtils.toString(schema)+"]");
							}catch(Exception t){
								logError("Presente schema senza targetNamespace? (Viene usato l'originale)");
							}
							wsdlUtilities.addSchemaIntoTypes(dConAllegati, schema);
						}
						
					}
					
					// Aggiorno WSDL
					
					byte [] wsdlPulito = xmlUtils.toByteArray(dConAllegati);
					
					if(AccordiServizioParteComuneCostanti.TIPO_WSDL_CONCETTUALE.equals(tipoWSDL)){
						as.setByteWsdlConcettuale(wsdlPulito);
					}
					else if(AccordiServizioParteComuneCostanti.TIPO_WSDL_EROGATORE.equals(tipoWSDL)){
						as.setByteWsdlLogicoErogatore(wsdlPulito);
					}
					else if(AccordiServizioParteComuneCostanti.TIPO_WSDL_FRUITORE.equals(tipoWSDL)){
						as.setByteWsdlLogicoFruitore(wsdlPulito);
					}
				}
				
			}catch(Exception t){
				logError("Errore durante la lettura degli schemi presenti all'interno del wsdl: "+t.getMessage(),t);
			}
			
			if(!nomiSchemiPresentiInternamenteTypes.isEmpty()){
				int i = 0;
				for (String nomeNuovoAllegato : nomiSchemiPresentiInternamenteTypes) {
					
					try{
					
						if(schemiAggiuntiInQuestaOperazione!=null){
							schemiAggiuntiInQuestaOperazione.put(nomeNuovoAllegato, schemiPresentiInternamenteTypes.get(i));
						}
						
						Documento vecchioAllegato = null;
						for (Documento vecchioAllegatoTMP : as.getAllegatoList()) {
							if(vecchioAllegatoTMP.getFile().equals(nomeNuovoAllegato)){
								vecchioAllegato = vecchioAllegatoTMP;
								break;
							}
						}
						
						// non ho trovato l'elemento corrente nel aspc destinazione
						if(vecchioAllegato == null){
							Documento allegato = new Documento();
							allegato.setRuolo(RuoliDocumento.allegato.toString());
							allegato.setByteContenuto(schemiPresentiInternamenteTypes.get(i));
							allegato.setFile(nomeNuovoAllegato);
							allegato.setTipo("xsd");
							allegato.setIdProprietarioDocumento(as.getId());
							as.addAllegato(allegato);
						} else {
							
							// CASO CHE NON DOVREBBE OCCORRERE MAI VISTO LA LOGICA CON XMLDIFF
							
							// ho trovato l'elemento, aggiorno i valori 
							vecchioAllegato.setByteContenuto(schemiPresentiInternamenteTypes.get(i));
						}
						
						i++;
						
					}catch(Exception t){
						logError("Errore durante l'aggiornamento dello schema ["+nomeNuovoAllegato+"] estratto dal wsdl: "+t.getMessage(),t);
					}
				}
			}
			
		}
		catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw e;
		}
	}
	
	private String checkXsdAlreadyExists(AbstractXMLUtils xmlUtils, byte[] xsdEsistenteContenuto, String xsdEsistenteNome,  Node schema){
		// check se si tratta di questo documento
		try{
			Node n = xmlUtils.newElement(xsdEsistenteContenuto);
			XMLDiff xmlDiff = new XMLDiff(OpenSPCoop2MessageFactory.getDefaultMessageFactory());
			/**System.out.println("["+vecchioAllegatoTMP.getFile()+"] N:"+xmlUtils.toString(n));
			System.out.println("["+vecchioAllegatoTMP.getFile()+"] Schema:"+xmlUtils.toString(schema));*/
			// NOTA: la ricostruzione in N2 è necessaria, poichè schema si porta dietro il definition wsdl (non ho capito perchè)
			Node n2 = xmlUtils.newElement(xmlUtils.toString(schema).getBytes());
			/**System.out.println("["+vecchioAllegatoTMP.getFile()+"] N2:"+xmlUtils.toString(n2));*/
			if(xmlDiff.diff(n, n2)){
				return xsdEsistenteNome;
				/**System.out.println("["+vecchioAllegatoTMP.getFile()+"] TROVATO UGUALE ["+nomeSchema+"]");*/
			}
			/**else{
				System.out.println("["+vecchioAllegatoTMP.getFile()+"] TROVATO NON UGUALE: \n"+xmlDiff.getDifferenceDetails());
			}*/
			return null;
		}catch(Exception t){
			logError("Compare failed: "+t.getMessage(),t);
			return null;
		}
	}
	
	public String readEndpoint(AccordoServizioParteComuneSintetico as, String portTypeParam, String servcorr, 
			BinaryParameter wsdlimpler, BinaryParameter wsdlimplfru) {
		if(as==null) {
			return null;
		}
		String portType = portTypeParam;
		if("".equals(portTypeParam)) {
			portType = null;
		}
		try {
			FormatoSpecifica formato = as.getFormatoSpecifica();
			String urlSuggerita = null;
			switch (formato) {
			case OPEN_API_3:
			case SWAGGER_2:
			case WADL:
				if(as.getByteWsdlConcettuale()!=null) {
					IApiReader apiReader = null;
					if(FormatoSpecifica.OPEN_API_3.equals(formato)) {
						apiReader = ApiFactory.newApiReader(ApiFormats.OPEN_API_3);
					}
					else if(FormatoSpecifica.SWAGGER_2.equals(formato)) {
						apiReader = ApiFactory.newApiReader(ApiFormats.SWAGGER_2);
					}
					else {
						apiReader = ApiFactory.newApiReader(ApiFormats.WADL);
					}
					ApiReaderConfig config = new ApiReaderConfig();
					config.setProcessInclude(false);
					config.setProcessInlineSchema(false);
					apiReader.init(LoggerWrapperFactory.getLogger(AccordiServizioParteSpecificaAdd.class), as.getByteWsdlConcettuale(), config);
					Api api = apiReader.read();
					if(api.getBaseURL()!=null) {
						urlSuggerita = api.getBaseURL().toString();
					}
				}
				break;
			case WSDL_11:
				byte [] wsdl = null;
				if(ServletUtils.isCheckBoxEnabled(servcorr)) {
					if(wsdlimplfru!=null && wsdlimplfru.getValue()!=null) {
						wsdl = wsdlimplfru.getValue();
					}
					else {
						wsdl = as.getByteWsdlLogicoFruitore();
					}
				}
				else{
					if(wsdlimpler!=null && wsdlimpler.getValue()!=null) {
						wsdl = wsdlimpler.getValue();
					}
					else {
						wsdl = as.getByteWsdlLogicoErogatore();
					}
				}
				if(wsdl==null) {
					wsdl = as.getByteWsdlConcettuale();
				}
				if(wsdl!=null) {
					WSDLUtilities utilities = new WSDLUtilities(org.openspcoop2.utils.xml.XMLUtils.getInstance());
					urlSuggerita = utilities.getServiceEndpoint(wsdl, portType);
				}
				break;
			}
			return urlSuggerita;
		}catch(Throwable t){
			if( portType!=null || !FormatoSpecifica.WSDL_11.equals(as.getFormatoSpecifica()) ) {
				log.error("Read endpoint from interface failed: "+t.getMessage(),t);
			}
			else {
				log.debug("Read endpoint from interface failed: "+t.getMessage(),t);
			}
			return null;
		}
	}
}
