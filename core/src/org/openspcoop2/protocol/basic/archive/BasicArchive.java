/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.basic.archive;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.BindingOperation;
import javax.xml.namespace.QName;

import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.ResourceParameter;
import org.openspcoop2.core.registry.ResourceRepresentation;
import org.openspcoop2.core.registry.ResourceRepresentationJson;
import org.openspcoop2.core.registry.ResourceRepresentationXml;
import org.openspcoop2.core.registry.ResourceRequest;
import org.openspcoop2.core.registry.ResourceResponse;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.HttpMethod;
import org.openspcoop2.core.registry.constants.ParameterType;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.RepresentationType;
import org.openspcoop2.core.registry.constants.RepresentationXmlType;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.wsdl.AccordoServizioWrapperUtilities;
import org.openspcoop2.message.xml.MessageXMLUtils;
import org.openspcoop2.protocol.basic.BasicComponentFactory;
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
import org.openspcoop2.protocol.sdk.archive.ImportMode;
import org.openspcoop2.protocol.sdk.archive.MapPlaceholder;
import org.openspcoop2.protocol.sdk.archive.MappingModeTypesExtensions;
import org.openspcoop2.protocol.sdk.constants.ArchiveType;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.utils.rest.ApiFactory;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.ParseWarningException;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.rest.api.ApiBodyParameter;
import org.openspcoop2.utils.rest.api.ApiCookieParameter;
import org.openspcoop2.utils.rest.api.ApiHeaderParameter;
import org.openspcoop2.utils.rest.api.ApiOperation;
import org.openspcoop2.utils.rest.api.ApiReference;
import org.openspcoop2.utils.rest.api.ApiRequestDynamicPathParameter;
import org.openspcoop2.utils.rest.api.ApiRequestFormParameter;
import org.openspcoop2.utils.rest.api.ApiRequestQueryParameter;
import org.openspcoop2.utils.rest.api.ApiResponse;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.wsdl.DefinitionWrapper;
import org.openspcoop2.utils.wsdl.WSDLUtilities;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.slf4j.Logger;
import org.w3c.dom.Document;

/**
 *  BasicArchive
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicArchive extends BasicComponentFactory implements IArchive {

	protected IDAccordoCooperazioneFactory idAccordoCooperazioneFactory;
	protected IDAccordoFactory idAccordoFactory;
	protected IDServizioFactory idServizioFactory;
	protected EsitoUtils esitoUtils;
	public BasicArchive(IProtocolFactory<?> protocolFactory) throws ProtocolException{
		super(protocolFactory);
		this.idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
		this.idAccordoFactory = IDAccordoFactory.getInstance();
		this.idServizioFactory = IDServizioFactory.getInstance();
		this.esitoUtils = new EsitoUtils(protocolFactory);
	}

	
	
	
	/* ----- Utilita' generali ----- */
	
	@Override
	public MappingModeTypesExtensions getMappingTypesExtensions(ArchiveMode mode)
			throws ProtocolException {
		MappingModeTypesExtensions m = new MappingModeTypesExtensions();
		m.add(Costanti.OPENSPCOOP_ARCHIVE_EXT, Costanti.OPENSPCOOP_ARCHIVE_MODE_TYPE);
		return m;
	}
	
	private static boolean normalizeDescription255 = false;
	public static boolean isNormalizeDescription255() {
		return normalizeDescription255;
	}
	public static void setNormalizeDescription255(boolean normalizeDescription255) {
		BasicArchive.normalizeDescription255 = normalizeDescription255;
	}
	private static boolean normalizeDescription4000 = false;
	public static boolean isNormalizeDescription4000() {
		return normalizeDescription4000;
	}
	public static void setNormalizeDescription4000(boolean normalizeDescription4000) {
		BasicArchive.normalizeDescription4000 = normalizeDescription4000;
	}
	private String normalizeDescriptionApi(String description) {
		if(BasicArchive.normalizeDescription255) {
			return normalizeDescriptionEngine(description, 255);
		}
		else if(BasicArchive.normalizeDescription4000) {
			return normalizeDescriptionEngine(description, 4000);
		}
		else {
			return description;
		}
	}
	private String normalizeDescriptionOtherElementApi(String description) {
		return normalizeDescriptionEngine(description, 255);
	}
	private String normalizeDescriptionEngine(String description, int maxSize) {
		if(description==null) {
			return null;
		}
		 String descr = description;
		 while(descr.contains("\r")) {
			 descr = descr.replace("\r", "");
		 }
		 int offset = 15; // uso maxSize-15 per essere sicuro che finisca in oracle con caratteri strani (value too large for column "GOVWAY330"."ACCORDI"."DESCRIZIONE" (actual: 257, maximum: 255))
		 if(descr.length()<=(maxSize-offset)) { 
			 return descr;
		 }
		 else {
			 int ulterioreOffset = 10;
			 if(maxSize==4000) {
				 ulterioreOffset = 50;
			 }
			 return descr.substring(0, ((maxSize-ulterioreOffset)-offset))+ " ...";
		 }
	}
	
	/**
	 * Imposta per ogni portType e operation presente nell'accordo fornito come parametro 
	 * le informazioni di protocollo analizzando i documenti interni agli archivi
	 */
	@Override
	public void setProtocolInfo(AccordoServizioParteComune accordoServizioParteComune) throws ProtocolException{
		setProtocolInfoEngine(accordoServizioParteComune, this.protocolFactory.getLogger());
	}
	public void setProtocolInfo(AccordoServizioParteComune accordoServizioParteComune,Logger log) throws ProtocolException{
		setProtocolInfoEngine(accordoServizioParteComune, log);
	}
	private void setProtocolInfoEngine(AccordoServizioParteComune accordoServizioParteComune,Logger log) throws ProtocolException{
		
		// NOTA non usare in questo metodo e nel metodo _setProtocolInfo il protocolFactory e dipendenze su di uno specifico protocollo.
		//      Viene usato dal meccanismo di import per definire la struttura di un accordo in base al wsdl, indipendentemente dallo specifico protocollo
		

		
		if(ServiceBinding.SOAP.equals(accordoServizioParteComune.getServiceBinding())) {
		
			if(accordoServizioParteComune.sizePortTypeList()>0){
				throw new ProtocolException("Protocol Info already exists");
			}
			
			if(accordoServizioParteComune.getFormatoSpecifica()==null || FormatoSpecifica.WSDL_11.equals(accordoServizioParteComune.getFormatoSpecifica())) {
				byte[] wsdlConcettuale = accordoServizioParteComune.getByteWsdlConcettuale();
				if(wsdlConcettuale!=null){
					setProtocolInfoFromWsdlEngine(wsdlConcettuale, accordoServizioParteComune, "Concettuale", log);
				}
				else{
					if(accordoServizioParteComune.getByteWsdlLogicoErogatore()!=null){
						setProtocolInfoFromWsdlEngine(accordoServizioParteComune.getByteWsdlLogicoErogatore(), accordoServizioParteComune, "LogicoErogatore", log);
					}
					if(accordoServizioParteComune.getByteWsdlLogicoFruitore()!=null){
						setProtocolInfoFromWsdlEngine(accordoServizioParteComune.getByteWsdlLogicoFruitore(), accordoServizioParteComune, "LogicoFruitore", log);
					}
				}
			}
			
		}
		else {
			
			if(accordoServizioParteComune.sizeResourceList()>0){
				throw new ProtocolException("Protocol Info already exists");
			}
			
			if(accordoServizioParteComune.getFormatoSpecifica()!=null) {
				
				byte[] wsdlConcettuale = accordoServizioParteComune.getByteWsdlConcettuale();
				if(wsdlConcettuale!=null){
					switch (accordoServizioParteComune.getFormatoSpecifica()) {
					case SWAGGER_2:
						setProtocolInfoFromRestInterfaceEngine(wsdlConcettuale, accordoServizioParteComune, ApiFormats.SWAGGER_2, log);
						break;
					case OPEN_API_3:
						setProtocolInfoFromRestInterfaceEngine(wsdlConcettuale, accordoServizioParteComune, ApiFormats.OPEN_API_3, log);
						break;
					default:
						// altre interfacce non supportate per rest
						break;
					}
				}
				
			}
			
		}
	}
	private void setProtocolInfoFromWsdlEngine(byte [] wsdlBytes,AccordoServizioParteComune accordoServizioParteComune,String tipo,Logger log) throws ProtocolException{
		
		try{
		
			AbstractXMLUtils xmlUtils = MessageXMLUtils.DEFAULT;
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
					
					// Calcolo profilo del port-type
					ProfiloCollaborazione profiloPt = accordoServizioParteComune.getProfiloCollaborazione();
					if(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(ptOpenSPCoop.getProfiloPT()) && ptOpenSPCoop.getProfiloCollaborazione()!=null) {
						profiloPt = ptOpenSPCoop.getProfiloCollaborazione();
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
						HashMap<String,QName> mapPartQNameInput = new HashMap<>();
						AccordoServizioWrapperUtilities.addMessageInputOperation(opWSDL, log, opOpenSPCoop, mapPartQNameInput);
						
						// Prendo la definizione del messaggio di output
						HashMap<String,QName> mapPartQNameOutput = new HashMap<>();
						AccordoServizioWrapperUtilities.addMessageOutputOperation(opWSDL, log, opOpenSPCoop, mapPartQNameOutput);
						
						// profilo di collaborazione (non basta guardare l'output, poiche' puo' avere poi un message vuoto e quindi equivale a non avere l'output)
						/**if(opWSDL.getOutput()!=null){*/
						ProfiloCollaborazione profiloOp = null;
						if(opOpenSPCoop.getMessageOutput()!=null){
							profiloOp = CostantiRegistroServizi.SINCRONO;
						}else{
							profiloOp = CostantiRegistroServizi.ONEWAY;
						}
						opOpenSPCoop.setProfiloCollaborazione(profiloOp);
						if(profiloPt.equals(profiloOp)) {
							opOpenSPCoop.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
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
													opOpenSPCoop, ptOpenSPCoop, mapPartQNameInput,
													wsdl.getTargetNamespace());
									}
									
									// Raccolgo Message-Output
									if(opOpenSPCoop.getMessageOutput()!=null){
										AccordoServizioWrapperUtilities.
											setMessageOutputSoapBindingInformation(bindingOperationWSDL, log, 
													opOpenSPCoop, ptOpenSPCoop, mapPartQNameOutput,
													wsdl.getTargetNamespace());
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
	
	private static final String TROVATO_PARAMETRO_COOKIE_PREFIX = "Trovato parametro cookie '";
	private static final String TROVATO_PARAMETRO_HEADER_PREFIX = "Trovato parametro header '";
	private static final String SENZA_TIPO_SUFFIX = "' senza tipo";
	private static final String SENZA_SCHEMA_SUFFIX = "' senza schema";
	
	private void setProtocolInfoFromRestInterfaceEngine(byte [] bytes,AccordoServizioParteComune accordoServizioParteComune,ApiFormats format,Logger log) throws ProtocolException{
		
		try {
			
			ApiReaderConfig config = new ApiReaderConfig();
			config.setProcessInclude(false);
			
			IApiReader apiReader = ApiFactory.newApiReader(format);
			apiReader.init(log, bytes, config);
			
	         Api api = apiReader.read();
	         try {
	        	 boolean usingFromSetProtocolInfo = true;
	        	 api.validate(usingFromSetProtocolInfo, false);
	         }catch(ParseWarningException warning) {
	        	 // ignore
	         }
	         
	         if( 
	        	(accordoServizioParteComune.getDescrizione()==null || "".equals(accordoServizioParteComune.getDescrizione())) 
	        	&&
	        	(api.getDescription()!=null) 
	        	 ){
	        	 accordoServizioParteComune.setDescrizione(this.normalizeDescriptionApi(api.getDescription()));
	         }
	         
	         if(api.sizeOperations()>0) {
	        	 for (ApiOperation apiOp : api.getOperations()) {
					
					// cerco resource
					Resource resourceOpenSPCoop = null;
					for (Resource resourceCheck : accordoServizioParteComune.getResourceList()) {
						if(resourceCheck.getPath()!=null && resourceCheck.getMethod()!=null) {
							HttpRequestMethod httpMethodCheck = HttpRequestMethod.valueOf(resourceCheck.getMethod().getValue());
							if(resourceCheck.getPath().equals(apiOp.getPath()) && 
									httpMethodCheck!=null &&
									httpMethodCheck.equals(apiOp.getHttpMethod())) {
								resourceOpenSPCoop = resourceCheck;
								break;
							}
						}
					}
	        		
					// se non esiste creo la risorsa
					if(resourceOpenSPCoop==null){
						resourceOpenSPCoop = new Resource();
						resourceOpenSPCoop.setNome(APIUtils.normalizeResourceName(apiOp.getHttpMethod(), apiOp.getPath()));
						resourceOpenSPCoop.setDescrizione(this.normalizeDescriptionOtherElementApi(apiOp.getDescription()));	
						resourceOpenSPCoop.setMethod(HttpMethod.toEnumConstant(apiOp.getHttpMethod().name()));
						resourceOpenSPCoop.setPath(apiOp.getPath());
						accordoServizioParteComune.addResource(resourceOpenSPCoop);
					}

					// Richiesta
					if(resourceOpenSPCoop.getRequest()==null) {
						resourceOpenSPCoop.setRequest(new ResourceRequest());
					}
					if(apiOp.getRequest()!=null) {
						
						if(apiOp.getRequest().sizeBodyParameters()>0) {
							for (ApiBodyParameter body : apiOp.getRequest().getBodyParameters()) {
								String mediaType = body.getMediaType();
								ResourceRepresentation rr = null;
								for (ResourceRepresentation rrCheck : resourceOpenSPCoop.getRequest().getRepresentationList()) {
									if(mediaType.equals(rrCheck.getMediaType())) {
										rr = rrCheck;
										break;
									}
								}
								if(rr==null) {
									rr = new ResourceRepresentation();
									rr.setMediaType(mediaType);
									resourceOpenSPCoop.getRequest().addRepresentation(rr);
								}
								rr.setNome(body.getName());
								rr.setDescrizione(this.normalizeDescriptionOtherElementApi(body.getDescription()));
								if(body.getElement()!=null) {
									if(body.getElement() instanceof QName) {
										QName qname = (QName)body.getElement();
										rr.setRepresentationType(RepresentationType.XML);
										ResourceRepresentationXml xml = new ResourceRepresentationXml();
										xml.setXmlType(RepresentationXmlType.ELEMENT);
										xml.setNome(qname.getLocalPart());
										xml.setNamespace(qname.getNamespaceURI());
										rr.setXml(xml);
									}
									else if(body.getElement() instanceof String) {
										String jsonType = (String)body.getElement();
										rr.setRepresentationType(RepresentationType.JSON);
										ResourceRepresentationJson json = new ResourceRepresentationJson();
										json.setTipo(jsonType);
										rr.setJson(json);
									}
									else if(body.getElement() instanceof ApiReference) {
										ApiReference apiRef = (ApiReference) body.getElement();
										String jsonType = apiRef.getType();
										rr.setRepresentationType(RepresentationType.JSON);
										ResourceRepresentationJson json = new ResourceRepresentationJson();
										json.setTipo(apiRef.getSchemaRef()+"#"+jsonType);
										rr.setJson(json);
									}
									else {
										rr.setRepresentationType(null);
										rr.setJson(null);
										rr.setXml(null);
									}
								}
								else {
									rr.setRepresentationType(null);
									rr.setJson(null);
									rr.setXml(null);
								}
							}
						}

						if(apiOp.getRequest().sizeCookieParameters()>0) {
							for (ApiCookieParameter cookie : apiOp.getRequest().getCookieParameters()) {
								String nome = cookie.getName();
								if(nome==null) {
									throw new ProtocolException("Trovato parametro cookie senza nome");
								}
								ResourceParameter rp = null;
								for (ResourceParameter rpCheck : resourceOpenSPCoop.getRequest().getParameterList()) {
									if(ParameterType.COOKIE.equals(rpCheck.getParameterType()) && nome.equals(rpCheck.getNome())) {
										rp = rpCheck;
										break;
									}
								}
								if(rp==null) {
									rp = new ResourceParameter();
									rp.setParameterType(ParameterType.COOKIE);
									rp.setNome(cookie.getName());
									resourceOpenSPCoop.getRequest().addParameter(rp);
								}
								rp.setDescrizione(this.normalizeDescriptionOtherElementApi(cookie.getDescription()));
								rp.setRequired(cookie.isRequired());
								if(cookie.getApiParameterSchema()!=null) {
									String type = cookie.getApiParameterSchema().getType();
									if(type==null) {
										throw new ProtocolException(TROVATO_PARAMETRO_COOKIE_PREFIX+rp.getNome()+SENZA_TIPO_SUFFIX);
									}
									rp.setTipo(type);
									rp.setRestrizioni(cookie.getApiParameterSchema().toString());
								}
								else {
									throw new ProtocolException(TROVATO_PARAMETRO_COOKIE_PREFIX+rp.getNome()+SENZA_SCHEMA_SUFFIX);
								}
							}							
						}
						
						if(apiOp.getRequest().sizeDynamicPathParameters()>0) {
							for (ApiRequestDynamicPathParameter dynamicPath : apiOp.getRequest().getDynamicPathParameters()) {
								String nome = dynamicPath.getName();
								if(nome==null) {
									throw new ProtocolException("Trovato parametro dynamic path senza nome");
								}
								ResourceParameter rp = null;
								for (ResourceParameter rpCheck : resourceOpenSPCoop.getRequest().getParameterList()) {
									if(ParameterType.DYNAMIC_PATH.equals(rpCheck.getParameterType()) && nome.equals(rpCheck.getNome())) {
										rp = rpCheck;
										break;
									}
								}
								if(rp==null) {
									rp = new ResourceParameter();
									rp.setParameterType(ParameterType.DYNAMIC_PATH);
									rp.setNome(dynamicPath.getName());
									resourceOpenSPCoop.getRequest().addParameter(rp);
								}
								rp.setDescrizione(this.normalizeDescriptionOtherElementApi(dynamicPath.getDescription()));
								rp.setRequired(dynamicPath.isRequired());
								if(dynamicPath.getApiParameterSchema()!=null) {
									String type = dynamicPath.getApiParameterSchema().getType();
									if(type==null) {
										throw new ProtocolException("Trovato parametro dynamic path '"+rp.getNome()+SENZA_TIPO_SUFFIX);
									}
									rp.setTipo(type);
									rp.setRestrizioni(dynamicPath.getApiParameterSchema().toString());
								}
								else {
									throw new ProtocolException("Trovato parametro dynamic path '"+rp.getNome()+SENZA_SCHEMA_SUFFIX);
								}
							}							
						}
						
						if(apiOp.getRequest().sizeFormParameters()>0) {
							for (ApiRequestFormParameter form : apiOp.getRequest().getFormParameters()) {
								String nome = form.getName();
								if(nome==null) {
									throw new ProtocolException("Trovato parametro form senza nome");
								}
								ResourceParameter rp = null;
								for (ResourceParameter rpCheck : resourceOpenSPCoop.getRequest().getParameterList()) {
									if(ParameterType.FORM.equals(rpCheck.getParameterType()) && nome.equals(rpCheck.getNome())) {
										rp = rpCheck;
										break;
									}
								}
								if(rp==null) {
									rp = new ResourceParameter();
									rp.setParameterType(ParameterType.FORM);
									rp.setNome(form.getName());
									resourceOpenSPCoop.getRequest().addParameter(rp);
								}
								rp.setDescrizione(this.normalizeDescriptionOtherElementApi(form.getDescription()));
								rp.setRequired(form.isRequired());
								if(form.getApiParameterSchema()!=null) {
									String type = form.getApiParameterSchema().getType();
									if(type==null) {
										throw new ProtocolException("Trovato parametro form '"+rp.getNome()+SENZA_TIPO_SUFFIX);
									}
									rp.setTipo(type);
									rp.setRestrizioni(form.getApiParameterSchema().toString());
								}
								else {
									throw new ProtocolException("Trovato parametro form '"+rp.getNome()+SENZA_SCHEMA_SUFFIX);
								}
							}							
						}
						
						if(apiOp.getRequest().sizeHeaderParameters()>0) {
							for (ApiHeaderParameter header : apiOp.getRequest().getHeaderParameters()) {
								String nome = header.getName();
								if(nome==null) {
									throw new ProtocolException("Trovato parametro header senza nome");
								}
								ResourceParameter rp = null;
								for (ResourceParameter rpCheck : resourceOpenSPCoop.getRequest().getParameterList()) {
									if(ParameterType.HEADER.equals(rpCheck.getParameterType()) && nome.equals(rpCheck.getNome())) {
										rp = rpCheck;
										break;
									}
								}
								if(rp==null) {
									rp = new ResourceParameter();
									rp.setParameterType(ParameterType.HEADER);
									rp.setNome(header.getName());
									resourceOpenSPCoop.getRequest().addParameter(rp);
								}
								rp.setDescrizione(this.normalizeDescriptionOtherElementApi(header.getDescription()));
								rp.setRequired(header.isRequired());
								if(header.getApiParameterSchema()!=null) {
									String type = header.getApiParameterSchema().getType();
									if(type==null) {
										throw new ProtocolException(TROVATO_PARAMETRO_HEADER_PREFIX+rp.getNome()+SENZA_TIPO_SUFFIX);
									}
									rp.setTipo(type);
									rp.setRestrizioni(header.getApiParameterSchema().toString());
								}
								else {
									throw new ProtocolException(TROVATO_PARAMETRO_HEADER_PREFIX+rp.getNome()+SENZA_SCHEMA_SUFFIX);
								}
							}							
						}
						
						if(apiOp.getRequest().sizeQueryParameters()>0) {
							for (ApiRequestQueryParameter query : apiOp.getRequest().getQueryParameters()) {
								String nome = query.getName();
								if(nome==null) {
									throw new ProtocolException("Trovato parametro query senza nome");
								}
								ResourceParameter rp = null;
								for (ResourceParameter rpCheck : resourceOpenSPCoop.getRequest().getParameterList()) {
									if(ParameterType.QUERY.equals(rpCheck.getParameterType()) && nome.equals(rpCheck.getNome())) {
										rp = rpCheck;
										break;
									}
								}
								if(rp==null) {
									rp = new ResourceParameter();
									rp.setParameterType(ParameterType.QUERY);
									rp.setNome(query.getName());
									resourceOpenSPCoop.getRequest().addParameter(rp);
								}
								rp.setDescrizione(this.normalizeDescriptionOtherElementApi(query.getDescription()));
								rp.setRequired(query.isRequired());
								if(query.getApiParameterSchema()!=null) {
									String type = query.getApiParameterSchema().getType();
									if(type==null) {
										throw new ProtocolException("Trovato parametro query '"+rp.getNome()+SENZA_TIPO_SUFFIX);
									}
									rp.setTipo(type);
									rp.setRestrizioni(query.getApiParameterSchema().toString());
								}
								else {
									throw new ProtocolException("Trovato parametro query '"+rp.getNome()+SENZA_SCHEMA_SUFFIX);
								}
							}							
						}
						
					}
					
					// Risposta
					if(apiOp.sizeResponses()>0) {
						for (ApiResponse apiResponse : apiOp.getResponses()) {
							
							int status = apiResponse.getHttpReturnCode();
							boolean defaultResponse = apiResponse.isDefaultHttpReturnCode();
							ResourceResponse resourceOpenSPCoopResponse = null;
							for (ResourceResponse resourceCheck : resourceOpenSPCoop.getResponseList()) {
								if( (status == resourceCheck.getStatus()) || (defaultResponse && ApiResponse.isDefaultHttpReturnCode(resourceCheck.getStatus()))) {
									resourceOpenSPCoopResponse = resourceCheck;
									break;
								}
							}
							if(resourceOpenSPCoopResponse==null) {
								resourceOpenSPCoopResponse = new ResourceResponse();
								if(defaultResponse) {
									resourceOpenSPCoopResponse.setStatus(ApiResponse.getDefaultHttpReturnCode());
								}
								else {
									resourceOpenSPCoopResponse.setStatus(status);
								}
								resourceOpenSPCoop.addResponse(resourceOpenSPCoopResponse);
							}
							resourceOpenSPCoopResponse.setDescrizione(this.normalizeDescriptionOtherElementApi(apiResponse.getDescription()));
							
							if(apiResponse.sizeBodyParameters()>0) {
								for (ApiBodyParameter body : apiResponse.getBodyParameters()) {
									String mediaType = body.getMediaType();
									ResourceRepresentation rr = null;
									for (ResourceRepresentation rrCheck : resourceOpenSPCoopResponse.getRepresentationList()) {
										if(mediaType.equals(rrCheck.getMediaType())) {
											rr = rrCheck;
											break;
										}
									}
									if(rr==null) {
										rr = new ResourceRepresentation();
										rr.setMediaType(mediaType);
										resourceOpenSPCoopResponse.addRepresentation(rr);
									}
									rr.setNome(body.getName());
									rr.setDescrizione(this.normalizeDescriptionOtherElementApi(body.getDescription()));
									if(body.getElement()!=null) {
										if(body.getElement() instanceof QName) {
											QName qname = (QName)body.getElement();
											rr.setRepresentationType(RepresentationType.XML);
											ResourceRepresentationXml xml = new ResourceRepresentationXml();
											xml.setXmlType(RepresentationXmlType.ELEMENT);
											xml.setNome(qname.getLocalPart());
											xml.setNamespace(qname.getNamespaceURI());
											rr.setXml(xml);
										}
										else if(body.getElement() instanceof String) {
											String jsonType = (String)body.getElement();
											rr.setRepresentationType(RepresentationType.JSON);
											ResourceRepresentationJson json = new ResourceRepresentationJson();
											json.setTipo(jsonType);
											rr.setJson(json);
										}
										else if(body.getElement() instanceof ApiReference) {
											ApiReference apiRef = (ApiReference) body.getElement();
											String jsonType = apiRef.getType();
											rr.setRepresentationType(RepresentationType.JSON);
											ResourceRepresentationJson json = new ResourceRepresentationJson();
											json.setTipo(apiRef.getSchemaRef()+"#"+jsonType);
											rr.setJson(json);
										}
										else {
											rr.setRepresentationType(null);
											rr.setJson(null);
											rr.setXml(null);
										}
									}
									else {
										rr.setRepresentationType(null);
										rr.setJson(null);
										rr.setXml(null);
									}
								}
							}
							
							if(apiResponse.sizeCookieParameters()>0) {
								for (ApiCookieParameter cookie : apiResponse.getCookieParameters()) {
									String nome = cookie.getName();
									if(nome==null) {
										throw new ProtocolException("Trovato parametro cookie nella risposta senza nome");
									}
									ResourceParameter rp = null;
									for (ResourceParameter rpCheck : resourceOpenSPCoopResponse.getParameterList()) {
										if(ParameterType.COOKIE.equals(rpCheck.getParameterType()) && nome.equals(rpCheck.getNome())) {
											rp = rpCheck;
											break;
										}
									}
									if(rp==null) {
										rp = new ResourceParameter();
										rp.setParameterType(ParameterType.COOKIE);
										rp.setNome(cookie.getName());
										resourceOpenSPCoopResponse.addParameter(rp);
									}
									rp.setDescrizione(this.normalizeDescriptionOtherElementApi(cookie.getDescription()));
									rp.setRequired(cookie.isRequired());
									if(cookie.getApiParameterSchema()!=null) {
										String type = cookie.getApiParameterSchema().getType();
										if(type==null) {
											throw new ProtocolException(TROVATO_PARAMETRO_COOKIE_PREFIX+rp.getNome()+"' nella risposta senza tipo");
										}
										rp.setTipo(type);
										rp.setRestrizioni(cookie.getApiParameterSchema().toString());
									}
									else {
										throw new ProtocolException(TROVATO_PARAMETRO_COOKIE_PREFIX+rp.getNome()+"' nella risposta senza schema");
									}
								}							
							}
							
							if(apiResponse.sizeHeaderParameters()>0) {
								for (ApiHeaderParameter header : apiResponse.getHeaderParameters()) {
									String nome = header.getName();
									if(nome==null) {
										throw new ProtocolException("Trovato parametro header nella risposta senza nome");
									}
									ResourceParameter rp = null;
									for (ResourceParameter rpCheck : resourceOpenSPCoopResponse.getParameterList()) {
										if(ParameterType.HEADER.equals(rpCheck.getParameterType()) && nome.equals(rpCheck.getNome())) {
											rp = rpCheck;
											break;
										}
									}
									if(rp==null) {
										rp = new ResourceParameter();
										rp.setParameterType(ParameterType.HEADER);
										rp.setNome(header.getName());
										resourceOpenSPCoopResponse.addParameter(rp);
									}
									rp.setDescrizione(this.normalizeDescriptionOtherElementApi(header.getDescription()));
									rp.setRequired(header.isRequired());
									if(header.getApiParameterSchema()!=null) {
										String type = header.getApiParameterSchema().getType();
										if(type==null) {
											throw new ProtocolException(TROVATO_PARAMETRO_HEADER_PREFIX+rp.getNome()+"' nella risposta senza tipo");
										}
										rp.setTipo(type);
										rp.setRestrizioni(header.getApiParameterSchema().toString());
									}
									else {
										throw new ProtocolException(TROVATO_PARAMETRO_HEADER_PREFIX+rp.getNome()+"' nella risposta senza schema");
									}
								}							
							}
						}
					}
				}
	         }
	         
	         //	Check nomi univoci delle risorse
	         if(accordoServizioParteComune.sizeResourceList()>0) {
	        	 boolean foundEquals = true;
	        	 while (foundEquals) {
	        		 foundEquals = false;
	        		 
	        		 for (int i = 0; i < accordoServizioParteComune.sizeResourceList(); i++) {
		        		 Resource resource = accordoServizioParteComune.getResource(i);
		        		 List<Resource> lR = new ArrayList<>();
		        		 for (Resource resourceCheck : accordoServizioParteComune.getResourceList()) {
		        			 if(resourceCheck.getNome().equals(resource.getNome())) {
		        				 lR.add(resourceCheck);
		        			 }
		        		 }
		        		 if(lR.size()>1) {
		        			 for (int j = 0; j < lR.size(); j++) {
		        				 Resource resourceDaModificare = lR.get(j);
		        				 String newName = resourceDaModificare.getNome();
		        				 if(newName.length()>250) {
		        					 newName = newName.substring(0, 250)+"_"+(j+1);
		        				 }
		        				 else {
		        					 newName = newName +"_"+(j+1);
		        				 }
		        				 resourceDaModificare.setNome(newName);
		        			 }
		        			 foundEquals = true;
		        		 }
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
		List<ImportMode> list = new ArrayList<>();
		list.add(Costanti.OPENSPCOOP_IMPORT_ARCHIVE_MODE);
		return list;
	}

	@Override
	public Archive importArchive(byte[]archive,ArchiveMode mode,ArchiveModeType type,
			IRegistryReader registryReader,IConfigIntegrationReader configIntegrationReader,
			boolean validationDocuments, MapPlaceholder placeholder) throws ProtocolException {
		
		ZIPReadUtils zipUtils = new ZIPReadUtils(this.protocolFactory.getLogger(),registryReader,configIntegrationReader);
		return zipUtils.getArchive(archive,placeholder,validationDocuments);
		
	}
	
	@Override
	public Archive importArchive(InputStream archive,ArchiveMode mode,ArchiveModeType type,
			IRegistryReader registryReader,IConfigIntegrationReader configIntegrationReader,
			boolean validationDocuments, MapPlaceholder placeholder) throws ProtocolException {
		
		try{
			ZIPReadUtils zipUtils = new ZIPReadUtils(this.protocolFactory.getLogger(),registryReader,configIntegrationReader);
			return zipUtils.getArchive(archive,placeholder,validationDocuments);
		}finally{
			try{
				if(archive!=null){
					archive.close();
				}
			}catch(Exception eClose){
				// close
			}
		}
		
	}
	
	@Override
	public void finalizeImportArchive(Archive archive,ArchiveMode mode,ArchiveModeType type,
			IRegistryReader registryReader,IConfigIntegrationReader configIntegrationReader,
			boolean validationDocuments, MapPlaceholder placeholder) throws ProtocolException {
		// nop
	}
	
	@Override
	public String toString(ArchiveEsitoImport esito, ArchiveMode archiveMode) throws ProtocolException{
		return this.esitoUtils.toString(esito,true,true);
	}
	
	@Override
	public String toString(ArchiveEsitoDelete esito, ArchiveMode archiveMode) throws ProtocolException{
		return this.esitoUtils.toString(esito,true,false);
	}
	
	
	
	
	
	
	
	/* ----- Export ----- */
	
	@Override
	public List<ExportMode> getExportModes(ArchiveType archiveType)
			throws ProtocolException {
		List<ExportMode> list = new ArrayList<>();
		list.add((ExportMode)Costanti.OPENSPCOOP_EXPORT_ARCHIVE_MODE.clone()); // vengono supportati tutti i tipi
		return list;
	}
	
	@Override
	public MappingModeTypesExtensions getExportMappingTypesExtensions(Archive archive, ArchiveMode mode,
			IRegistryReader registroReader, IConfigIntegrationReader configIntegrationReader) throws ProtocolException{
		return this.getMappingTypesExtensions(mode); // basic ignora archive
	}

	@Override
	public byte[] exportArchive(Archive archive, ArchiveMode mode,
			IRegistryReader registryReader,IConfigIntegrationReader configIntegrationReader)
			throws ProtocolException {
		ZIPWriteUtils zipUtils = new ZIPWriteUtils(super.getProtocolFactory().getLogger(),registryReader,configIntegrationReader);
		return zipUtils.generateArchive(archive);
	}
	
	@Override
	public void exportArchive(Archive archive, OutputStream out, ArchiveMode mode,
			IRegistryReader registryReader,IConfigIntegrationReader configIntegrationReader)
			throws ProtocolException {
		ZIPWriteUtils zipUtils = new ZIPWriteUtils(super.getProtocolFactory().getLogger(),registryReader,configIntegrationReader);
		zipUtils.generateArchive(archive, out);
	}
	




}
