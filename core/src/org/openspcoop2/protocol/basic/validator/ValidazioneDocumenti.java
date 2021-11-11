/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.basic.validator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.constants.TipiDocumentoConversazione;
import org.openspcoop2.core.registry.constants.TipiDocumentoCoordinamento;
import org.openspcoop2.core.registry.constants.TipiDocumentoInterfaccia;
import org.openspcoop2.core.registry.constants.TipiDocumentoLivelloServizio;
import org.openspcoop2.core.registry.constants.TipiDocumentoSemiformale;
import org.openspcoop2.core.registry.constants.TipiDocumentoSicurezza;
import org.openspcoop2.protocol.basic.BasicComponentFactory;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.validator.IValidazioneDocumenti;
import org.openspcoop2.protocol.sdk.validator.ValidazioneResult;
import org.openspcoop2.utils.CopyStream;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.json.YAMLUtils;
import org.openspcoop2.utils.rest.ApiFactory;
import org.openspcoop2.utils.rest.ApiFormats;
import org.openspcoop2.utils.rest.ApiReaderConfig;
import org.openspcoop2.utils.rest.IApiReader;
import org.openspcoop2.utils.rest.ParseWarningException;
import org.openspcoop2.utils.rest.api.Api;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.wsdl.DefinitionWrapper;
import org.openspcoop2.utils.wsdl.WSDLUtilities;
import org.openspcoop2.utils.xacml.MarshallUtilities;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.XMLUtils;
import org.openspcoop2.utils.xml.XSDUtils;
import org.w3c.dom.Document;

/**
 * ValidazioneDocumenti
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ValidazioneDocumenti extends BasicComponentFactory implements IValidazioneDocumenti{

	protected AbstractXMLUtils xmlUtils = null;
	protected XSDUtils xsdUtils = null;
	protected WSDLUtilities wsdlUtilities = null;
	protected JSONUtils jsonUtils = null;
	protected YAMLUtils yamlUtils = null;
	
	public ValidazioneDocumenti(IProtocolFactory<?> factory) throws ProtocolException{
		super(factory);
		this.xmlUtils = XMLUtils.getInstance();
		this.xsdUtils = new XSDUtils(this.xmlUtils);
		this.wsdlUtilities = WSDLUtilities.getInstance(this.xmlUtils);
		this.jsonUtils = JSONUtils.getInstance();
		this.yamlUtils = YAMLUtils.getInstance();
	}

	@Override
	public ValidazioneResult validaSpecificaInterfaccia(
			AccordoServizioParteComune accordoServizioParteComune) {

		String objectInEsame = null;
		ValidazioneResult result = new ValidazioneResult();
		result.setEsito(false);

		if(ServiceBinding.SOAP.equals(accordoServizioParteComune.getServiceBinding())) {

			// interfaccia wsdl Definitoria
			try{
				objectInEsame = "[InterfacciaWSDL Definitoria] ";
	
				byte[]wsdlDefinitorio = null;
				if(accordoServizioParteComune.getByteWsdlDefinitorio()!=null){
					wsdlDefinitorio = accordoServizioParteComune.getByteWsdlDefinitorio();
				}
				else if(accordoServizioParteComune.getWsdlDefinitorio()!=null){
					wsdlDefinitorio = this.readDocumento(accordoServizioParteComune.getWsdlDefinitorio());		
				}
				if(wsdlDefinitorio!=null){
					// Verifico che sia un documento xml valido
					this.xmlUtils.newDocument(wsdlDefinitorio);
					// Verifico che sia un xsd, leggendone il targetNamespace
					this.xsdUtils.getTargetNamespace(wsdlDefinitorio);				
				}	
			}catch(Exception e){
				result.setMessaggioErrore(objectInEsame+" Documento non valido: "+e.getMessage());
				result.setException(e);
				return result;
			}
	
	
			// interfaccia wsdl Concettuale
			try{
				objectInEsame = "[InterfacciaWSDL Concettuale] ";
	
				byte[]wsdlConcettuale = null;
				if(accordoServizioParteComune.getByteWsdlConcettuale()!=null){
					wsdlConcettuale = accordoServizioParteComune.getByteWsdlConcettuale();
				}
				else if(accordoServizioParteComune.getWsdlConcettuale()!=null){
					wsdlConcettuale = this.readDocumento(accordoServizioParteComune.getWsdlConcettuale());		
				}
				if(wsdlConcettuale!=null){
					// Verifico che sia un documento xml valido
					Document d = this.xmlUtils.newDocument(wsdlConcettuale);
					// Verifico che sia un wsdl, leggendone il targetNamespace
					this.wsdlUtilities.getTargetNamespace(wsdlConcettuale);
					// Costruisco wsdl
					this.wsdlUtilities.removeSchemiIntoTypes(d);
					DefinitionWrapper wsdl = new DefinitionWrapper(d,this.xmlUtils,false,false);
					// Valido
					wsdl.valida(false);
				}
			}catch(Exception e){
				result.setMessaggioErrore(objectInEsame+" Documento non valido: "+e.getMessage());
				result.setException(e);
				return result;
			}	
	
	
			// interfaccia wsdl Erogatore
			try{
				objectInEsame = "[InterfacciaWSDL Erogatore] ";
	
				byte[]wsdlErogatore = null;
				if(accordoServizioParteComune.getByteWsdlLogicoErogatore()!=null){
					wsdlErogatore = accordoServizioParteComune.getByteWsdlLogicoErogatore();
				}
				else if(accordoServizioParteComune.getWsdlLogicoErogatore()!=null){
					wsdlErogatore = this.readDocumento(accordoServizioParteComune.getWsdlLogicoErogatore());		
				}
				if(wsdlErogatore!=null){
					// Verifico che sia un documento xml valido
					Document d = this.xmlUtils.newDocument(wsdlErogatore);
					// Verifico che sia un wsdl, leggendone il targetNamespace
					this.wsdlUtilities.getTargetNamespace(wsdlErogatore);
					// Costruisco wsdl
					this.wsdlUtilities.removeSchemiIntoTypes(d);
					DefinitionWrapper wsdl = new DefinitionWrapper(d, this.xmlUtils,false,false);
					// Valido
					wsdl.valida(false);
				}	
			}catch(Exception e){
				result.setMessaggioErrore(objectInEsame+" Documento non valido: "+e.getMessage());
				result.setException(e);
				return result;
			}
	
	
			// interfaccia wsdl Fruitore
			try{
				objectInEsame = "[InterfacciaWSDL Fruitore] ";
	
				byte[]wsdlFruitore = null;
				if(accordoServizioParteComune.getByteWsdlLogicoFruitore()!=null){
					wsdlFruitore = accordoServizioParteComune.getByteWsdlLogicoFruitore();
				}
				else if(accordoServizioParteComune.getWsdlLogicoFruitore()!=null){
					wsdlFruitore = this.readDocumento(accordoServizioParteComune.getWsdlLogicoFruitore());		
				}
				if(wsdlFruitore!=null){
					// Verifico che sia un documento xml valido
					Document d = this.xmlUtils.newDocument(wsdlFruitore);
					// Verifico che sia un wsdl, leggendone il targetNamespace
					this.wsdlUtilities.getTargetNamespace(wsdlFruitore);
					// Costruisco wsdl
					this.wsdlUtilities.removeSchemiIntoTypes(d);
					DefinitionWrapper wsdl = new DefinitionWrapper(d,this.xmlUtils,false,false);
					// Valido
					wsdl.valida(false);
				}	
			}catch(Exception e){
				result.setMessaggioErrore(objectInEsame+" Documento non valido: "+e.getMessage());
				result.setException(e);
				return result;
			}
			
		}
		else {
			
			
			// interfaccia
			try{
				
				byte[]wsdlConcettuale = null;
				if(accordoServizioParteComune.getByteWsdlConcettuale()!=null){
					wsdlConcettuale = accordoServizioParteComune.getByteWsdlConcettuale();
				}
				else if(accordoServizioParteComune.getWsdlConcettuale()!=null){
					wsdlConcettuale = this.readDocumento(accordoServizioParteComune.getWsdlConcettuale());		
				}
				if(wsdlConcettuale!=null){
				
					if(accordoServizioParteComune.getFormatoSpecifica()!=null) {
						
						ApiFormats format = null;
						switch (accordoServizioParteComune.getFormatoSpecifica()) {
						case WADL:
							objectInEsame = "[Interfaccia WADL] ";
							format=ApiFormats.WADL;
							break;
						case SWAGGER_2:
							objectInEsame = "[Interfaccia Swagger 2] ";
							format=ApiFormats.SWAGGER_2;
							break;
						case OPEN_API_3:
							objectInEsame = "[Interfaccia OpenAPI 3] ";
							format=ApiFormats.OPEN_API_3;
							break;
						default:
							// altre interfacce non supportate per rest
							break;
						}
						
						ApiReaderConfig config = new ApiReaderConfig();
						config.setProcessInclude(false);
						IApiReader apiReader = ApiFactory.newApiReader(format);
						apiReader.init(this.log, wsdlConcettuale, config);
						Api api = apiReader.read();
						try {
							api.validate();
						}catch(ParseWarningException warning) {
							result.setEsito(true);
							result.setMessaggioWarning(objectInEsame+" Documento contenente anomalie: "+warning.getMessage());
							return result;
						}
					}
					

				}
			}
			catch(Exception e){
				result.setMessaggioErrore(objectInEsame+" Documento non valido: "+e.getMessage());
				result.setException(e);
				return result;
			}	
			
		}


		// result
		result.setEsito(true);
		return result;
	}

	@Override
	public ValidazioneResult validaSpecificaConversazione(
			AccordoServizioParteComune accordoServizioParteComune) {
		ValidazioneResult result = new ValidazioneResult();
		result.setEsito(true);
		return result;
	}

	@Override
	public ValidazioneResult validaSpecificaInterfaccia(
			AccordoServizioParteSpecifica accordoServizioParteSpecifica,
			AccordoServizioParteComune accordoServizioParteComune) {

		ValidazioneResult result = new ValidazioneResult();
		result.setEsito(false);

		// Raccolta wsdl
		String tipoWsdl = null;
		byte[]wsdlImplementativoFruitore = null;
		byte[]wsdlImplementativoErogatore = null;
		byte[]wsdlLogicoFruitore = null;
		byte[]wsdlLogicoErogatore = null;
		try{
			tipoWsdl = "ImplementativoFruitore";
			if(accordoServizioParteSpecifica.getByteWsdlImplementativoFruitore()!=null){
				wsdlImplementativoFruitore = accordoServizioParteSpecifica.getByteWsdlImplementativoFruitore();
			}
			else if(accordoServizioParteSpecifica.getWsdlImplementativoFruitore()!=null){
				wsdlImplementativoFruitore = this.readDocumento(accordoServizioParteSpecifica.getWsdlImplementativoFruitore());		
			}

			tipoWsdl = "ImplementativoErogatore";
			if(accordoServizioParteSpecifica.getByteWsdlImplementativoErogatore()!=null){
				wsdlImplementativoErogatore = accordoServizioParteSpecifica.getByteWsdlImplementativoErogatore();
			}
			else if(accordoServizioParteSpecifica.getWsdlImplementativoErogatore()!=null){
				wsdlImplementativoErogatore = this.readDocumento(accordoServizioParteSpecifica.getWsdlImplementativoErogatore());		
			}

			tipoWsdl = "LogicoFruitore";
			if(accordoServizioParteComune.getByteWsdlLogicoFruitore()!=null){
				wsdlLogicoFruitore = accordoServizioParteComune.getByteWsdlLogicoFruitore();
			}
			else if(accordoServizioParteComune.getWsdlLogicoFruitore()!=null){
				wsdlLogicoFruitore = this.readDocumento(accordoServizioParteComune.getWsdlLogicoFruitore());		
			}

			tipoWsdl = "LogicoErogatore";
			if(accordoServizioParteComune.getByteWsdlLogicoErogatore()!=null){
				wsdlLogicoErogatore = accordoServizioParteComune.getByteWsdlLogicoErogatore();
			}
			else if(accordoServizioParteComune.getWsdlLogicoErogatore()!=null){
				wsdlLogicoErogatore = this.readDocumento(accordoServizioParteComune.getWsdlLogicoErogatore());		
			}
		}catch(Exception e){
			result.setMessaggioErrore("Raccolta wsdl "+tipoWsdl+" non riuscita: "+e.getMessage());
			result.setException(e);
			return result;
		}

		
		
		

		// WSDL Erogatore
		List<String> portTypesWSDL_erogatore = new ArrayList<String>();
		tipoWsdl = "LogicoErogatore";
		try{
			if(wsdlLogicoErogatore!=null){
				Document dParteComune = this.xmlUtils.newDocument(wsdlLogicoErogatore);
				this.wsdlUtilities.removeSchemiIntoTypes(dParteComune);
				DefinitionWrapper wsdlParteCoumune = new DefinitionWrapper(dParteComune,this.xmlUtils,false,false);

				Iterator<?> it = wsdlParteCoumune.getAllPortTypes().keySet().iterator();
				while(it.hasNext()){
					javax.xml.namespace.QName pt = (javax.xml.namespace.QName) it.next();
					portTypesWSDL_erogatore.add(pt.getLocalPart());
				}
			}

		}catch(Exception e){
			result.setMessaggioErrore("Lettura wsdl "+tipoWsdl+" (identificazione port types) non riuscita: "+e.getMessage());
			result.setException(e);
			return result;
		}
		if(portTypesWSDL_erogatore.size()==0){
			if(wsdlImplementativoErogatore!=null){
				result.setMessaggioErrore("Per poter fornire un WSDL Implementativo Erogatore, è necessario prima definire un wsdl logico erogatore valido nell'accordo di servizio parte comune");
				return result;
			}
		}
		
		
		
		
		// WSDL Fruitore
		List<String> portTypesWSDL_fruitore = new ArrayList<String>();
		tipoWsdl = "LogicoFruitore";
		try{
			if(wsdlLogicoFruitore!=null){
				Document dParteComune = this.xmlUtils.newDocument(wsdlLogicoFruitore);
				this.wsdlUtilities.removeSchemiIntoTypes(dParteComune);
				DefinitionWrapper wsdlParteCoumune = new DefinitionWrapper(dParteComune,this.xmlUtils,false,false);

				Iterator<?> it = wsdlParteCoumune.getAllPortTypes().keySet().iterator();
				while(it.hasNext()){
					javax.xml.namespace.QName pt = (javax.xml.namespace.QName) it.next();
					portTypesWSDL_fruitore.add(pt.getLocalPart());
				}
			}

		}catch(Exception e){
			result.setMessaggioErrore("Lettura wsdl "+tipoWsdl+" (identificazione port types) non riuscita: "+e.getMessage());
			result.setException(e);
			return result;
		}
		if(portTypesWSDL_fruitore.size()==0){
			if(wsdlImplementativoFruitore!=null){
				result.setMessaggioErrore("Per poter fornire un WSDL Implementativo Fruitore, è necessario prima definire un wsdl logico fruitore valido nell'accordo di servizio parte comune");
				return result;
			}
		}

		
		
		
		
		// Check Presenza WSDL Implementativo
		// Check spostato nello stato finale
//		if(wsdlImplementativoErogatore==null &&
//				wsdlImplementativoFruitore==null){
//			result.setMessaggioErrore("Non è presente alcun WSDL implementativo");
//			return result;
//		}
		if(wsdlImplementativoErogatore!=null &&
				wsdlImplementativoFruitore!=null){
			result.setMessaggioErrore("Non è possibile definire sia il WSDL implementativo erogatore che il WSDL implementativo fruitore");
			return result;
		}
		
		
		
		

		// Check WSDL Implementativo erogatore
		try{
			if(wsdlImplementativoErogatore!=null){
				// Verifico che sia un documento xml valido
				Document d = this.xmlUtils.newDocument(wsdlImplementativoErogatore);
				// Verifico che sia un wsdl, leggendone il targetNamespace
				this.wsdlUtilities.getTargetNamespace(wsdlImplementativoErogatore);
				// Costruisco wsdl
				this.wsdlUtilities.removeSchemiIntoTypes(d);
				DefinitionWrapper wsdlParteSpecifica = new DefinitionWrapper(d,this.xmlUtils,false,false);
				// Valida Bindings
				wsdlParteSpecifica.validaBinding(portTypesWSDL_erogatore);
				// Check presenza al massimo di un solo port type riferito dai vari binding
				checkPortTypeInBinding(wsdlParteSpecifica);
			}
		}catch(Exception e){
			result.setMessaggioErrore("WSDL Implementativo Erogatore non valido: "+e.getMessage());
			result.setException(e);
			return result;
		}
		
		
		
		
		// Check WSDL Implementativo fruitore
		try{
			if(wsdlImplementativoFruitore!=null){
				// Verifico che sia un documento xml valido
				Document d = this.xmlUtils.newDocument(wsdlImplementativoFruitore);
				// Verifico che sia un wsdl, leggendone il targetNamespace
				this.wsdlUtilities.getTargetNamespace(wsdlImplementativoFruitore);
				// Costruisco wsdl
				this.wsdlUtilities.removeSchemiIntoTypes(d);
				DefinitionWrapper wsdlParteSpecifica = new DefinitionWrapper(d,this.xmlUtils,false,false);
				// Valida Bindings
				wsdlParteSpecifica.validaBinding(portTypesWSDL_fruitore);
				// Check presenza al massimo di un solo port type riferito dai vari binding
				checkPortTypeInBinding(wsdlParteSpecifica);
			}
		}catch(Exception e){
			result.setMessaggioErrore("WSDL Implementativo Fruitore non valido: "+e.getMessage());
			result.setException(e);
			return result;
		}

		
		
		// result
		result.setEsito(true);
		return result;
	}
	private void checkPortTypeInBinding(DefinitionWrapper wsdlParteSpecifica) throws Exception{
		Map<QName,QName> mapBindingToPortTypeImplemented = wsdlParteSpecifica.getMapPortTypesImplementedBinding();
		List<String> portTypes = new ArrayList<String>();
		StringBuilder bf = new StringBuilder();
		for (QName binding : mapBindingToPortTypeImplemented.keySet()) {
			QName portType = mapBindingToPortTypeImplemented.get(binding);
			String portTypeName = portType.getLocalPart();
			if(portTypes.contains(portTypeName)==false){
				portTypes.add(portTypeName);
				if(bf.length()>0){
					bf.append(",");
				}
				bf.append(portTypeName);
			}
		}
		
		if(portTypes.size()>1){
			throw new Exception("Trovato più di un port type implementato dai binding presenti: "+bf.toString());
		}
	}

	
	@Override
	public ValidazioneResult validaSpecificaInterfaccia(Fruitore fruitore, AccordoServizioParteSpecifica accordoServizioParteSpecifica , AccordoServizioParteComune accordoServizioParteComune){
		AccordoServizioParteSpecifica as = new AccordoServizioParteSpecifica();
		as.setByteWsdlImplementativoErogatore(fruitore.getByteWsdlImplementativoErogatore());
		as.setByteWsdlImplementativoFruitore(fruitore.getByteWsdlImplementativoFruitore());
		as.setWsdlImplementativoErogatore(fruitore.getWsdlImplementativoErogatore());
		as.setWsdlImplementativoFruitore(fruitore.getWsdlImplementativoFruitore());
		return this.validaSpecificaInterfaccia(as, accordoServizioParteComune);
	}
	
	@Override
	public ValidazioneResult valida(Documento documento) {
		
		ValidazioneResult result = new ValidazioneResult();
		result.setEsito(false);
				
		if(documento.getTipo()!=null) {
			if(TipiDocumentoInterfaccia.WSDL.toString().toLowerCase().equals(documento.getTipo().toLowerCase()) ||
					TipiDocumentoConversazione.WSBL.toString().toLowerCase().equals(documento.getTipo().toLowerCase()) ||
					TipiDocumentoConversazione.BPEL.toString().toLowerCase().equals(documento.getTipo().toLowerCase()) ||
					TipiDocumentoCoordinamento.BPEL.toString().toLowerCase().equals(documento.getTipo().toLowerCase()) ||
					TipiDocumentoCoordinamento.WSCDL.toString().toLowerCase().equals(documento.getTipo().toLowerCase()) ||
					TipiDocumentoLivelloServizio.WSAGREEMENT.toString().toLowerCase().equals(documento.getTipo().toLowerCase()) ||
					TipiDocumentoLivelloServizio.WSLA.toString().toLowerCase().equals(documento.getTipo().toLowerCase()) ||
					TipiDocumentoSemiformale.XML.toString().toLowerCase().equals(documento.getTipo().toLowerCase()) ||
					TipiDocumentoSicurezza.WSPOLICY.toString().toLowerCase().equals(documento.getTipo().toLowerCase()) ||
					TipiDocumentoSicurezza.XACML_POLICY.toString().toLowerCase().equals(documento.getTipo().toLowerCase()) 
			){
				// Valido che sia un documento XML corretto.
				try{
					
					byte[]doc = null;
					if(documento.getByteContenuto()!=null){
						doc = documento.getByteContenuto();
					}
					else if(documento.getFile()!=null){
						doc = this.readDocumento(documento.getFile());		
					}
					if(doc!=null){
						// Verifico che sia un documento xml valido
						this.xmlUtils.newDocument(doc);		
					}	
					
					if(TipiDocumentoSicurezza.XACML_POLICY.toString().toLowerCase().equals(documento.getTipo().toLowerCase())){
						if(MarshallUtilities.unmarshallPolicy(doc)==null) {
							throw new Exception("Policy non corretta");
						}
					}
					
				}catch(Exception e){
					result.setMessaggioErrore("Documento "+documento.getFile()+" (ruolo:"+documento.getRuolo()+") non valido: "+e.getMessage());
					result.setException(e);
					return result;
				}
			}
			else if(TipiDocumentoSemiformale.XSD.toString().toLowerCase().equals(documento.getTipo().toLowerCase())){
				// Valido che sia un documento XSD corretto.
				try{
					
					byte[]doc = null;
					if(documento.getByteContenuto()!=null){
						doc = documento.getByteContenuto();
					}
					else if(documento.getFile()!=null){
						doc = this.readDocumento(documento.getFile());		
					}
					if(doc!=null){
						// Verifico che sia un documento valido
						this.xmlUtils.newDocument(doc);
						if(this.xsdUtils.isXSDSchema(doc)==false) {
							throw new Exception("non riconosciuto come documento xsd");
						}
					}	
				}catch(Exception e){
					result.setMessaggioErrore("Documento "+documento.getFile()+" (ruolo:"+documento.getRuolo()+") non valido: "+e.getMessage());
					result.setException(e);
					return result;
				}
			}
			else if(TipiDocumentoSemiformale.JSON.toString().toLowerCase().equals(documento.getTipo().toLowerCase())){
				// Valido che sia un documento JSON corretto.
				try{
					
					byte[]doc = null;
					if(documento.getByteContenuto()!=null){
						doc = documento.getByteContenuto();
					}
					else if(documento.getFile()!=null){
						doc = this.readDocumento(documento.getFile());		
					}
					if(doc!=null){
						// Verifico che sia un documento valido
						this.jsonUtils.getAsNode(doc);
						if(this.jsonUtils.isJson(doc)==false) {
							throw new Exception("non riconosciuto come documento json");
						}
					}	
				}catch(Exception e){
					result.setMessaggioErrore("Documento "+documento.getFile()+" (ruolo:"+documento.getRuolo()+") non valido: "+e.getMessage());
					result.setException(e);
					return result;
				}
			}
			else if(TipiDocumentoSemiformale.YAML.toString().toLowerCase().equals(documento.getTipo().toLowerCase())){
				// Valido che sia un documento YAML corretto.
				try{
					
					byte[]doc = null;
					if(documento.getByteContenuto()!=null){
						doc = documento.getByteContenuto();
					}
					else if(documento.getFile()!=null){
						doc = this.readDocumento(documento.getFile());		
					}
					if(doc!=null){
						// Verifico che sia un documento valido
						this.yamlUtils.getAsNode(doc);
						if(this.yamlUtils.isYaml(doc)==false) {
							throw new Exception("non riconosciuto come documento yaml");
						}
					}	
				}catch(Exception e){
					result.setMessaggioErrore("Documento "+documento.getFile()+" (ruolo:"+documento.getRuolo()+") non valido: "+e.getMessage());
					result.setException(e);
					return result;
				}
			}
		}
		
		// result
		result.setEsito(true);
		return result;
	}

	@Override
	public ValidazioneResult validaDocumenti(
			AccordoServizioParteComune accordoServizioParteComune) {
//		// wsdl
//		ValidazioneDocumentiResult v = this.validaInterfacciaWsdlParteComune(accordoServizioParteComune);
//		if(v.isEsito()==false){
//			return v;
//		}
//		
//		// conversazione
//		v = this.validaSpecificaConversazione(accordoServizioParteComune);
//		if(v.isEsito()==false){
//			return v;
//		}
		
		// allegati
		for (int i = 0; i < accordoServizioParteComune.sizeAllegatoList(); i++) {
			ValidazioneResult v = this.valida(accordoServizioParteComune.getAllegato(i));
			if(v.isEsito()==false){
				return v;
			}
		}
		
		// specifica semiformale
		for (int i = 0; i < accordoServizioParteComune.sizeSpecificaSemiformaleList(); i++) {
			ValidazioneResult v = this.valida(accordoServizioParteComune.getSpecificaSemiformale(i));
			if(v.isEsito()==false){
				return v;
			}
		}
		
		// servizio composto
		if(accordoServizioParteComune.getServizioComposto()!=null){
			for (int i = 0; i < accordoServizioParteComune.getServizioComposto().sizeSpecificaCoordinamentoList(); i++) {
				ValidazioneResult v = this.valida(accordoServizioParteComune.getServizioComposto().getSpecificaCoordinamento(i));
				if(v.isEsito()==false){
					return v;
				}
			}
		}
		
		ValidazioneResult result = new ValidazioneResult();
		result.setEsito(true);
		return result;
	}

	@Override
	public ValidazioneResult validaDocumenti(
			AccordoServizioParteSpecifica accordoServizioParteSpecifica) {
		
		// allegati
		for (int i = 0; i < accordoServizioParteSpecifica.sizeAllegatoList(); i++) {
			ValidazioneResult v = this.valida(accordoServizioParteSpecifica.getAllegato(i));
			if(v.isEsito()==false){
				return v;
			}
		}
		
		// specifica semiformale
		for (int i = 0; i < accordoServizioParteSpecifica.sizeSpecificaSemiformaleList(); i++) {
			ValidazioneResult v = this.valida(accordoServizioParteSpecifica.getSpecificaSemiformale(i));
			if(v.isEsito()==false){
				return v;
			}
		}
		
		// specifica livello servizio
		for (int i = 0; i < accordoServizioParteSpecifica.sizeSpecificaLivelloServizioList(); i++) {
			ValidazioneResult v = this.valida(accordoServizioParteSpecifica.getSpecificaLivelloServizio(i));
			if(v.isEsito()==false){
				return v;
			}
		}
		
		// specifica sicurezza
		for (int i = 0; i < accordoServizioParteSpecifica.sizeSpecificaSicurezzaList(); i++) {
			ValidazioneResult v = this.valida(accordoServizioParteSpecifica.getSpecificaSicurezza(i));
			if(v.isEsito()==false){
				return v;
			}
		}
		
		ValidazioneResult result = new ValidazioneResult();
		result.setEsito(true);
		return result;
	}

	@Override
	public ValidazioneResult validaDocumenti(
			AccordoCooperazione accordoCooperazione) {
		
		// allegati
		for (int i = 0; i < accordoCooperazione.sizeAllegatoList(); i++) {
			ValidazioneResult v = this.valida(accordoCooperazione.getAllegato(i));
			if(v.isEsito()==false){
				return v;
			}
		}
		
		// specifica semiformale
		for (int i = 0; i < accordoCooperazione.sizeSpecificaSemiformaleList(); i++) {
			ValidazioneResult v = this.valida(accordoCooperazione.getSpecificaSemiformale(i));
			if(v.isEsito()==false){
				return v;
			}
		}
				
		ValidazioneResult result = new ValidazioneResult();
		result.setEsito(true);
		return result;
		
	}



	protected byte[] readDocumento(String fileName) throws ProtocolException{

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try{
			if(fileName.startsWith("http://") || fileName.startsWith("file://")){
				byte[] file = HttpUtilities.requestHTTPFile(fileName);
				if(file==null)
					throw new Exception("byte[] is null");
				else
					bout.write(file);
			}else{
				File f = new File(fileName);
				if(f.exists()){
					FileInputStream file = null;
					try{
						file = new FileInputStream(f);
//						byte [] reads = new byte[Utilities.DIMENSIONE_BUFFER];
//						int letti = 0;
//						while( (letti=file.read(reads)) >=0 ){
//							bout.write(reads,0,letti);
//						}
						CopyStream.copy(file, bout);
					}finally{
						try{
							if(file!=null){
								file.close();
							}
						}catch(Exception eClose){}
					}		
				}
				else{
					throw new Exception("File ["+fileName+"] non esistente");
				}
			}
			bout.flush();
			bout.close();
			if(bout.size()>0){
				return bout.toByteArray();
			}

		}catch(Exception e){
			this.log.warn("File "+fileName+", lettura documento non riuscita:",e);
		}	

		throw new ProtocolException("Contenuto non letto?? File ["+fileName+"]");
	}
}
