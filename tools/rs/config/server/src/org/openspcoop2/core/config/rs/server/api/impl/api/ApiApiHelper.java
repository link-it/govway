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
package org.openspcoop2.core.config.rs.server.api.impl.api;

import static org.openspcoop2.utils.service.beans.utils.BaseHelper.evalnull;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.rs.server.api.impl.Enums;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.Api;
import org.openspcoop2.core.config.rs.server.model.ApiAllegato;
import org.openspcoop2.core.config.rs.server.model.ApiAllegatoGenerico;
import org.openspcoop2.core.config.rs.server.model.ApiAllegatoItem;
import org.openspcoop2.core.config.rs.server.model.ApiAllegatoItemGenerico;
import org.openspcoop2.core.config.rs.server.model.ApiAllegatoItemSpecificaSemiformale;
import org.openspcoop2.core.config.rs.server.model.ApiAllegatoSpecificaSemiformale;
import org.openspcoop2.core.config.rs.server.model.ApiAzione;
import org.openspcoop2.core.config.rs.server.model.ApiCanale;
import org.openspcoop2.core.config.rs.server.model.ApiInterfacciaRest;
import org.openspcoop2.core.config.rs.server.model.ApiInterfacciaSoap;
import org.openspcoop2.core.config.rs.server.model.ApiItem;
import org.openspcoop2.core.config.rs.server.model.ApiRisorsa;
import org.openspcoop2.core.config.rs.server.model.ApiServizio;
import org.openspcoop2.core.config.rs.server.model.CanaleEnum;
import org.openspcoop2.core.config.rs.server.model.FormatoRestEnum;
import org.openspcoop2.core.config.rs.server.model.FormatoSoapEnum;
import org.openspcoop2.core.config.rs.server.model.HttpMethodEnum;
import org.openspcoop2.core.config.rs.server.model.RuoloAllegatoAPI;
import org.openspcoop2.core.config.rs.server.model.StatoApiEnum;
import org.openspcoop2.core.config.rs.server.model.TipoApiEnum;
import org.openspcoop2.core.config.rs.server.model.TipoSpecificaSemiformaleEnum;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Message;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.beans.GruppoSintetico;
import org.openspcoop2.core.registry.constants.BindingUse;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.HttpMethod;
import org.openspcoop2.core.registry.constants.ProfiloCollaborazione;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.StatoFunzionalita;
import org.openspcoop2.core.registry.constants.TipiDocumentoSemiformale;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.basic.archive.APIUtils;
import org.openspcoop2.protocol.information_missing.constants.StatoType;
import org.openspcoop2.protocol.manifest.constants.InterfaceType;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;

/**
 * ApiApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ApiApiHelper {



	public static void validateProperties(ApiEnv env, ProtocolProperties protocolProperties, IDAccordo idAccordoFromAccordo,
			ConsoleOperationType operationType)
			throws DriverConfigurazioneException {
		if(protocolProperties!=null) {
			try{

				ConsoleConfiguration consoleConf = getConsoleConfiguration(env, idAccordoFromAccordo);

				env.apcHelper.validaProtocolProperties(consoleConf, ConsoleOperationType.ADD, protocolProperties);
				
				IConsoleDynamicConfiguration consoleDynamicConfiguration = env.protocolFactory.createDynamicConfigurationConsole();
				IRegistryReader registryReader = env.soggettiCore.getRegistryReader(env.protocolFactory); 
				IConfigIntegrationReader configRegistryReader = env.soggettiCore.getConfigIntegrationReader(env.protocolFactory);
				consoleDynamicConfiguration.updateDynamicConfigAccordoServizioParteComune(consoleConf, operationType, env.apcHelper, protocolProperties, 
						registryReader, configRegistryReader, idAccordoFromAccordo);
				
				consoleDynamicConfiguration.validateDynamicConfigAccordoServizioParteComune(consoleConf, operationType, env.apcHelper, protocolProperties, 
						registryReader, configRegistryReader, idAccordoFromAccordo);
				
			}catch(ProtocolException e){
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(e.getMessage());
			}
		}
	}

	public static ConsoleConfiguration getConsoleConfiguration(ApiEnv env, IDAccordo idAccordoFromAccordo)
			throws ProtocolException, DriverConfigurazioneException {
		IConsoleDynamicConfiguration consoleDynamicConfiguration = env.protocolFactory.createDynamicConfigurationConsole();

		IRegistryReader registryReader = env.soggettiCore.getRegistryReader(env.protocolFactory); 
		IConfigIntegrationReader configRegistryReader = env.soggettiCore.getConfigIntegrationReader(env.protocolFactory);

		ConsoleConfiguration consoleConf = consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteComune(ConsoleOperationType.ADD, env.apcHelper, 
				registryReader, configRegistryReader, idAccordoFromAccordo);
		return consoleConf;
	}

	public static void validateProperties(ApiEnv env, ProtocolProperties protocolProperties, IDResource idResource, String method, String path,
			ConsoleOperationType operationType)
			throws DriverConfigurazioneException {
		if(protocolProperties!=null) {
			try{

				ConsoleConfiguration consoleConf = getConsoleConfiguration(env, idResource, method, path);

				env.apcHelper.validaProtocolProperties(consoleConf, ConsoleOperationType.ADD, protocolProperties);
				
				IConsoleDynamicConfiguration consoleDynamicConfiguration = env.protocolFactory.createDynamicConfigurationConsole();
				IRegistryReader registryReader = env.soggettiCore.getRegistryReader(env.protocolFactory); 
				IConfigIntegrationReader configRegistryReader = env.soggettiCore.getConfigIntegrationReader(env.protocolFactory);
				consoleDynamicConfiguration.updateDynamicConfigResource(consoleConf, operationType, env.apcHelper, protocolProperties, 
						registryReader, configRegistryReader, idResource, method, path);
				
				consoleDynamicConfiguration.validateDynamicConfigResource(consoleConf, operationType, env.apcHelper, protocolProperties, 
						registryReader, configRegistryReader, idResource, method, path);
				
			}catch(ProtocolException e){
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(e.getMessage());
			}
		}
	}
	
	public static ConsoleConfiguration getConsoleConfiguration(ApiEnv env, IDResource idResource, String method, String path)
			throws ProtocolException, DriverConfigurazioneException {
		IConsoleDynamicConfiguration consoleDynamicConfiguration = env.protocolFactory.createDynamicConfigurationConsole();

		IRegistryReader registryReader = env.soggettiCore.getRegistryReader(env.protocolFactory); 
		IConfigIntegrationReader configRegistryReader = env.soggettiCore.getConfigIntegrationReader(env.protocolFactory);

		ConsoleConfiguration consoleConf = consoleDynamicConfiguration.getDynamicConfigResource(ConsoleOperationType.ADD, env.apcHelper, registryReader, configRegistryReader,
				idResource, method, path);
		return consoleConf;
	}

	public static void validateProperties(ApiEnv env, ProtocolProperties protocolProperties, IDPortTypeAzione idAccordoAzione,
			ConsoleOperationType operationType)
			throws DriverConfigurazioneException {
		if(protocolProperties!=null) {
			try{

				ConsoleConfiguration consoleConf = getConsoleConfiguration(env, idAccordoAzione);

				env.apcHelper.validaProtocolProperties(consoleConf, ConsoleOperationType.ADD, protocolProperties);
				
				IConsoleDynamicConfiguration consoleDynamicConfiguration = env.protocolFactory.createDynamicConfigurationConsole();
				IRegistryReader registryReader = env.soggettiCore.getRegistryReader(env.protocolFactory); 
				IConfigIntegrationReader configRegistryReader = env.soggettiCore.getConfigIntegrationReader(env.protocolFactory);
				consoleDynamicConfiguration.updateDynamicConfigOperation(consoleConf, operationType, env.apcHelper, protocolProperties, 
						registryReader, configRegistryReader, idAccordoAzione);
				
				consoleDynamicConfiguration.validateDynamicConfigOperation(consoleConf, operationType, env.apcHelper, protocolProperties, 
						registryReader, configRegistryReader, idAccordoAzione);
				
			}catch(ProtocolException e){
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(e.getMessage());
			}
		}
	}
	
	public static ConsoleConfiguration getConsoleConfiguration(ApiEnv env, IDPortTypeAzione idAccordoAzione)
			throws ProtocolException, DriverConfigurazioneException {
		
		IConsoleDynamicConfiguration consoleDynamicConfiguration = env.protocolFactory.createDynamicConfigurationConsole();

		IRegistryReader registryReader = env.soggettiCore.getRegistryReader(env.protocolFactory); 
		IConfigIntegrationReader configRegistryReader = env.soggettiCore.getConfigIntegrationReader(env.protocolFactory);

		ConsoleConfiguration consoleConf = consoleDynamicConfiguration.getDynamicConfigOperation(ConsoleOperationType.ADD, env.apcHelper, registryReader, configRegistryReader,
				idAccordoAzione);
		return consoleConf;
	}

	public static ProtocolProperties getProtocolProperties(ApiAzione body, ProfiloEnum profilo, AccordoServizioParteComune as, Operation op, ApiEnv env) throws Exception {
		if(!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA) && body.getModi() != null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione 'ModI' non conforme con il profilo '"+profilo+"' indicato");
		}

		switch(profilo) {
		case APIGATEWAY:
			return null;// trasparente 
		case EDELIVERY:
			return EDeliveryApiApiHelper.getProtocolProperties(body);
		case FATTURAPA:
			return FatturaPAApiApiHelper.getProtocolProperties(body);
		case MODI:
		case MODIPA:
			return ModiApiApiHelper.getProtocolProperties(body, as, op, env);
		case SPCOOP:
			return SPCoopApiApiHelper.getProtocolProperties(body);
		}
		return null;
	}


	public static ProtocolProperties getProtocolProperties(ApiRisorsa body, ProfiloEnum profilo, Resource res, ApiEnv env) throws Exception {
		if(!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA) && body.getModi() != null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione 'ModI' non conforme con il profilo '"+profilo+"' indicato");
		}

		switch(profilo) {
		case APIGATEWAY:
			return null;// trasparente 
		case EDELIVERY:
			return EDeliveryApiApiHelper.getProtocolProperties(body);
		case FATTURAPA:
			return FatturaPAApiApiHelper.getProtocolProperties(body);
		case MODI:
		case MODIPA:
			return ModiApiApiHelper.getProtocolProperties(body, res, env);
		case SPCOOP:
			return SPCoopApiApiHelper.getProtocolProperties(body);
		}
		return null;
	}


	public static ProtocolProperties getProtocolProperties(Api body, ProfiloEnum profilo) throws Exception {


		if(!profilo.equals(ProfiloEnum.MODI) && !profilo.equals(ProfiloEnum.MODIPA) && body.getModi() != null) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione 'ModI' non conforme con il profilo '"+profilo+"' indicato");
		}

		switch(profilo) {
		case APIGATEWAY:
			return null;// trasparente 
		case EDELIVERY:
			return EDeliveryApiApiHelper.getProtocolProperties(body);
		case FATTURAPA:
			return FatturaPAApiApiHelper.getProtocolProperties(body);
		case MODI:
		case MODIPA:
			return ModiApiApiHelper.getProtocolProperties(body);
		case SPCOOP:
			return SPCoopApiApiHelper.getProtocolProperties(body);
		}
		return null;
	}

	public static final AccordoServizioParteComune accordoApiToRegistro(Api body, ApiEnv env) throws Exception {
		AccordoServizioParteComune as = new AccordoServizioParteComune(); 

		as.setNome(body.getNome());
		as.setDescrizione(body.getDescrizione());
		as.setProfiloCollaborazione(ProfiloCollaborazione.SINCRONO);

		// Quando sono in SPCoopSoap Specifico di tutti i vari wsdl\wsbl solo il wsdlserv, ovvero  AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE
		// Quando invece sono in modalità ApiGateway e passo una OpenApi, imposto il wsdlconcettuale
		String interfaccia = body.getInterfaccia() != null ? new String(body.getInterfaccia()) : null;

		// defaults
		// Questo codice e il controllo sotto non dovrebbero mai intervenire essendo tipo required
		if (env.profilo != ProfiloEnum.APIGATEWAY) {

			if (body.getTipoInterfaccia() == null) {
				ApiInterfacciaSoap iSoap = new ApiInterfacciaSoap();
				iSoap.setProtocollo(TipoApiEnum.SOAP);
				iSoap.setFormato(FormatoSoapEnum._1);
				body.setTipoInterfaccia(iSoap);	
			}
		}

		if ( env.profilo == ProfiloEnum.APIGATEWAY ) {

			if ( body.getTipoInterfaccia() == null ) {
				ApiInterfacciaRest iRest = new ApiInterfacciaRest();
				iRest.setProtocollo(TipoApiEnum.REST);
				iRest.setFormato(FormatoRestEnum.OPENAPI3_0);
				body.setTipoInterfaccia(iRest);	
			}

		}

		if ( body.getTipoInterfaccia() == null )	// Questo non può mai accadere, lo tengo perchè in futuro il codice sopra potrà cambiare.
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un tipo di interfaccia per la Api");

		switch (body.getTipoInterfaccia().getProtocollo()) {
		case REST:

			if(body.getTipoInterfaccia() instanceof ApiInterfacciaSoap) {
				ApiInterfacciaSoap soap = (ApiInterfacciaSoap) body.getTipoInterfaccia();
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il formato dell'interfaccia ("+soap.getFormato()+") non è compatibile con il protocollo REST indicato");
			}
			else if(body.getTipoInterfaccia() instanceof ApiInterfacciaRest) {
				ApiInterfacciaRest iRest = (ApiInterfacciaRest) body.getTipoInterfaccia();

				as.setByteWsdlConcettuale(interfaccia != null && !interfaccia.trim().replaceAll("\n", "").equals("") ? interfaccia.trim().getBytes() : null);
				FormatoRestEnum formatoRest = iRest.getFormato();
				as.setFormatoSpecifica( BaseHelper.evalorElse( () -> 
				Enums.formatoSpecificaFromRest.get(formatoRest),
				FormatoSpecifica.OPEN_API_3
						)); 
			}
			else {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il formato dell'interfaccia ("+body.getTipoInterfaccia().getClass().getName()+") risulta sconosciuto e non compatibile con il protocollo REST indicato");
			}

			break;
		case SOAP: 

			if(body.getTipoInterfaccia() instanceof ApiInterfacciaRest) {
				ApiInterfacciaRest rest = (ApiInterfacciaRest) body.getTipoInterfaccia();
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il formato dell'interfaccia ("+rest.getFormato()+") non è compatibile con il protocollo SOAP indicato");
			}
			else if(body.getTipoInterfaccia() instanceof ApiInterfacciaSoap) {
				ApiInterfacciaSoap iSoap = (ApiInterfacciaSoap) body.getTipoInterfaccia();

				FormatoSoapEnum formatoSoap = iSoap.getFormato();
				as.setFormatoSpecifica( BaseHelper.evalorElse( 
						() -> Enums.formatoSpecificaFromSoap.get(formatoSoap), 
						FormatoSpecifica.WSDL_11 
						));
				as.setByteWsdlLogicoErogatore(interfaccia != null && !interfaccia.trim().replaceAll("\n", "").equals("") ? interfaccia.trim().getBytes() : null);	// Da commenti e audit, WSDL solo logico ed erogatore
			}
			else {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il formato dell'interfaccia ("+body.getTipoInterfaccia().getClass().getName()+") risulta sconosciuto e non compatibile con il protocollo SOAP indicato");
			}

			break;
		}

		as.setServiceBinding(env.apcCore.fromMessageServiceBinding(Enums.serviceBindingFromTipo.get(body.getTipoInterfaccia().getProtocollo())));

		boolean facilityUnicoWSDL_interfacciaStandard = false;
		if(as.getByteWsdlLogicoErogatore()!=null && as.getByteWsdlLogicoFruitore()==null && as.getByteWsdlConcettuale()==null){
			as.setByteWsdlConcettuale(as.getByteWsdlLogicoErogatore());
			facilityUnicoWSDL_interfacciaStandard = true;
		}

		boolean filtroDuplicatiSupportato = env.stationCore.isFunzionalitaProtocolloSupportataDalProtocollo(
				env.tipo_protocollo, 
				env.apcCore.toMessageServiceBinding(as.getServiceBinding())
				, FunzionalitaProtocollo.FILTRO_DUPLICATI
				);

		as.setFiltroDuplicati(Helper.boolToStatoFunzionalita(filtroDuplicatiSupportato));
		as.setConfermaRicezione(StatoFunzionalita.DISABILITATO);
		as.setIdCollaborazione(StatoFunzionalita.DISABILITATO);
		as.setConsegnaInOrdine(StatoFunzionalita.DISABILITATO);
		as.setIdRiferimentoRichiesta(StatoFunzionalita.DISABILITATO);
		as.setUtilizzoSenzaAzione(true);	// Default a true.
		as.setPrivato(false);	// Da Audit è false.
		as.setStatoPackage(StatoType.FINALE.getValue());	// Come da Audit

		if (body.getVersione() != null)
			as.setVersione(body.getVersione());

		as.setSuperUser(env.userLogin);

		// Questo resta a null dal debug anche quando specifico un wsdl
		// as.setMessageType(apcCore.fromMessageMessageType(this.messageType));

		// Setto il soggetto referente, true in caso di "spcoop" "sdi", "as4"
		// Se non lo gestisco (caso "trasparente") allora è il sistema a legare l'applicativo con un soggetto di default.
		IdSoggetto idSoggReferente = new IdSoggetto();
		if (env.gestisciSoggettoReferente) {			
			idSoggReferente.setNome(Helper.getSoggettoOrDefault(body.getReferente(), env.profilo));
			idSoggReferente.setTipo(env.tipo_soggetto);
		}
		else {
			IDSoggetto idSogg = env.apcCore.getSoggettoOperativoDefault(env.userLogin, env.tipo_protocollo);
			idSoggReferente.setNome(idSogg.getNome());
			idSoggReferente.setTipo(idSogg.getTipo());
		}

		if (!env.soggettiCore.existsSoggetto(idSoggReferente.toIDSoggetto())) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il soggetto referente " + idSoggReferente.toIDSoggetto().toString() + " non è presente nel registro");
		}

		idSoggReferente.setId(env.soggettiCore.getIdSoggetto(idSoggReferente.getNome(),idSoggReferente.getTipo()));

		as.setSoggettoReferente(idSoggReferente);

		// Canale
		if(env.gestioneCanali && body.getCanale()!=null) {
			if(!env.canali.contains(body.getCanale())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il canale fornito '" + body.getCanale() + "' non è presente nel registro");
			}
			as.setCanale(body.getCanale());
		}

		// Automapping
		ServerProperties properties = ServerProperties.getInstance();
		InterfaceType interfaceType = Enums.interfaceTypeFromFormatoSpecifica.get(as.getFormatoSpecifica());

		AccordiServizioParteComuneUtilities.mapppingAutomaticoInterfaccia(
				as,
				env.apcCore, 
				properties.isEnabledAutoMapping(),
				properties.isValidazioneDocumenti(),
				properties.isEnabledAutoMappingEstraiXsdSchemiFromWsdlTypes(),
				facilityUnicoWSDL_interfacciaStandard, 
				env.tipo_protocollo, interfaceType);


		return as;
	}


	public static final Documento apiAllegatoToDocumento(ApiAllegato body, AccordoServizioParteComune as, ApiEnv env) {

		Documento documento = new Documento();
		documento.setIdProprietarioDocumento(as.getId());

		RuoloAllegatoAPI ruoloAllegato = body.getAllegato().getRuolo();

		documento.setRuolo(Enums.ruoliDocumentoFromApi.get(ruoloAllegato).toString());

		switch (ruoloAllegato) {
		case ALLEGATO:

			if(! (body.getAllegato() instanceof ApiAllegatoGenerico)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'allegato fornito possiede una struttura dati '"+body.getAllegato().getClass().getName()+"' non compatibile con il ruolo '"+ruoloAllegato+"'");
			}
			ApiAllegatoGenerico allegatoGenerico = (ApiAllegatoGenerico) body.getAllegato();
			documento.setFile(allegatoGenerico.getNome());
			documento.setByteContenuto(allegatoGenerico.getDocumento());
			documento.setTipo( evalnull( () -> allegatoGenerico.getNome().substring( allegatoGenerico.getNome().lastIndexOf('.')+1, allegatoGenerico.getNome().length())) );
			break;

		case SPECIFICASEMIFORMALE:

			if(! (body.getAllegato() instanceof ApiAllegatoSpecificaSemiformale)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'allegato fornito possiede una struttura dati '"+body.getAllegato().getClass().getName()+"' non compatibile con il ruolo '"+ruoloAllegato+"'");
			}
			ApiAllegatoSpecificaSemiformale allegatoSS = (ApiAllegatoSpecificaSemiformale) body.getAllegato();
			documento.setFile(allegatoSS.getNome());
			documento.setByteContenuto(allegatoSS.getDocumento());
			if(ApiCheckNotNull.isNotNullTipoSpecifica(allegatoSS, documento)) {
				TipoSpecificaSemiformaleEnum tipoAllegato = (TipoSpecificaSemiformaleEnum) allegatoSS.getTipoSpecifica();
				documento.setTipo( evalnull( () -> Enums.tipoDocumentoSemiFormaleFromSpecifica.get(tipoAllegato) ).toString() );
			}
			break;
		}

		return documento;
	}


	public static final ApiAllegato documentoToApiAllegato(Documento d) {
		ApiAllegato ret = new ApiAllegato();

		RuoloAllegatoAPI ruoloAllegato = Enums.ruoliApiFromDocumento.get(Enum.valueOf(RuoliDocumento.class, d.getRuolo()));

		switch (ruoloAllegato) {
		case ALLEGATO:

			ApiAllegatoGenerico allegatoGenerico = new ApiAllegatoGenerico();
			allegatoGenerico.setRuolo(ruoloAllegato);
			allegatoGenerico.setDocumento(d.getByteContenuto());
			allegatoGenerico.setNome(d.getFile());
			ret.setAllegato(allegatoGenerico);
			break;

		case SPECIFICASEMIFORMALE:

			ApiAllegatoSpecificaSemiformale allegatoSS = new ApiAllegatoSpecificaSemiformale();
			allegatoSS.setRuolo(ruoloAllegato);
			allegatoSS.setDocumento(d.getByteContenuto());
			allegatoSS.setNome(d.getFile());
			TipiDocumentoSemiformale tipo = TipiDocumentoSemiformale.toEnumConstant(d.getTipo());
			allegatoSS.setTipoSpecifica((Helper.apiEnumToGovway(tipo, TipoSpecificaSemiformaleEnum.class)));
			ret.setAllegato(allegatoSS);
			break;
		}

		return ret;
	}

	public static final ApiAllegatoItem documentoToApiAllegatoItem(Documento d) {
		ApiAllegatoItem ret = new ApiAllegatoItem();

		RuoloAllegatoAPI ruoloAllegato = Enums.ruoliApiFromDocumento.get(Enum.valueOf(RuoliDocumento.class, d.getRuolo()));

		switch (ruoloAllegato) {
		case ALLEGATO:

			ApiAllegatoItemGenerico allegatoGenerico = new ApiAllegatoItemGenerico();
			allegatoGenerico.setRuolo(ruoloAllegato);
			allegatoGenerico.setNome(d.getFile());
			ret.setAllegato(allegatoGenerico);
			break;

		case SPECIFICASEMIFORMALE:

			ApiAllegatoItemSpecificaSemiformale allegatoSS = new ApiAllegatoItemSpecificaSemiformale();
			allegatoSS.setRuolo(ruoloAllegato);
			allegatoSS.setNome(d.getFile());
			TipiDocumentoSemiformale tipo = TipiDocumentoSemiformale.toEnumConstant(d.getTipo());
			allegatoSS.setTipoSpecifica((Helper.apiEnumToGovway(tipo, TipoSpecificaSemiformaleEnum.class)));
			ret.setAllegato(allegatoSS);
			break;
		}

		return ret;
	}

	public static final void updatePortType(ApiServizio body, PortType pt, ApiEnv env) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {

		pt.setNome(body.getNome());
		pt.setDescrizione(body.getDescrizione());

		boolean filtroDuplicatiSupportato = env.stationCore.isFunzionalitaProtocolloSupportataDalProtocollo(
				env.tipo_protocollo, 
				ServiceBinding.SOAP
				, FunzionalitaProtocollo.FILTRO_DUPLICATI
				);

		pt.setFiltroDuplicati(Helper.boolToStatoFunzionalita(filtroDuplicatiSupportato));	
		pt.setIdCollaborazione(Helper.boolToStatoFunzionalita(body.isIdCollaborazione()));
		pt.setIdRiferimentoRichiesta(Helper.boolToStatoFunzionalita(body.isRiferimentoIdRichiesta()));
		pt.setProfiloPT(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
		pt.setProfiloCollaborazione(Enums.profiloCollaborazioneFromApiEnum.get(body.getProfiloCollaborazione()));			
	}


	public static final void updateOperation(ApiAzione azione, PortType parent, Operation to_update) {

		to_update.setNome(azione.getNome());	

		if (azione.isProfiloRidefinito()) {
			to_update.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
			to_update.setIdCollaborazione(Helper.boolToStatoFunzionalita(azione.isIdCollaborazione()));
			to_update.setIdRiferimentoRichiesta(Helper.boolToStatoFunzionalita(azione.isRiferimentoIdRichiesta()));
			to_update.setProfiloCollaborazione(Enums.profiloCollaborazioneFromApiEnum.get(azione.getProfiloCollaborazione()));
		}
		else {
			to_update.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_DEFAULT);
			to_update.setFiltroDuplicati(parent.getFiltroDuplicati());
			to_update.setConfermaRicezione(parent.getConfermaRicezione());
			to_update.setConsegnaInOrdine(parent.getConsegnaInOrdine());
			to_update.setIdCollaborazione(parent.getIdCollaborazione());
			to_update.setIdRiferimentoRichiesta(parent.getIdRiferimentoRichiesta());
			to_update.setProfiloCollaborazione(parent.getProfiloCollaborazione());
			to_update.setScadenza(parent.getScadenza());
		}		
	}


	public static final Operation apiAzioneToOperazione(ApiAzione azione, PortType parent) {
		final Operation ret = new Operation();
		updateOperation(azione, parent, ret);

		final String useOp = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_USE;
		final String namespaceWsdlOp = "";

		final BindingUse use = BindingUse.toEnumConstant(useOp);
		ret.setMessageInput(new Message());
		ret.getMessageInput().setSoapNamespace(namespaceWsdlOp);
		ret.getMessageInput().setUse(use);

		// Se il profilo non è oneWay dobbiamo specificare anche in mesaggio di i uscita, la comunicazione diventa a due vie.
		if (ret.getProfiloCollaborazione() != ProfiloCollaborazione.ONEWAY) {
			ret.setMessageOutput(new Message());
			ret.getMessageOutput().setSoapNamespace(namespaceWsdlOp);
			ret.getMessageOutput().setUse(use);
		}

		return ret;
	}

	public static final ApiAzione operazioneToApiAzione(Operation op) {
		ApiAzione ret = new ApiAzione();

		ret.setNome(op.getNome());
		ret.setIdCollaborazione(op.getIdCollaborazione() == StatoFunzionalita.ABILITATO ? true : false);
		ret.setRiferimentoIdRichiesta(op.getIdRiferimentoRichiesta() == StatoFunzionalita.ABILITATO ? true : false);
		ret.setProfiloCollaborazione(Enums.profiloCollaborazioneApiFromRegistro.get(op.getProfiloCollaborazione()));
		ret.setProfiloRidefinito(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO.equals(op.getProfAzione()) ? true : false);

		return ret;
	}


	public static final PortType apiServizioToRegistro(ApiServizio body, AccordoServizioParteComune parent, ApiEnv env) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {

		PortType ret = new PortType();

		updatePortType(body, ret, env);

		ret.setScadenza("");
		ret.setConfermaRicezione(parent.getConfermaRicezione());
		ret.setConsegnaInOrdine(parent.getConsegnaInOrdine());

		return ret;
	}




	public static final ApiServizio servizioRegistroToApi(PortType pt) {
		ApiServizio ret = new ApiServizio();
		ret.setNome(pt.getNome());
		ret.setDescrizione(pt.getDescrizione());
		ret.setIdCollaborazione(Helper.statoFunzionalitaToBool(pt.getIdCollaborazione()));
		ret.setRiferimentoIdRichiesta(Helper.statoFunzionalitaToBool(pt.getIdRiferimentoRichiesta()));
		ret.setProfiloCollaborazione(Enums.profiloCollaborazioneApiFromRegistro.get(pt.getProfiloCollaborazione()));

		return ret;
	}


	public static final void updateRisorsa(ApiRisorsa body, Resource res) {

		res.setNome(body.getNome());
		res.setDescrizione(body.getDescrizione());

		// 1. se il path non inizia per '/' aggiungo all'inizio della stringa
		String pathNormalizzato = body.getPath();

		if(pathNormalizzato !=null && !"".equals(pathNormalizzato)) {
			pathNormalizzato = pathNormalizzato.trim();
			if(!pathNormalizzato.startsWith("/"))
				pathNormalizzato = "/" + pathNormalizzato;
		}

		res.setPath(pathNormalizzato);

		res.setMethod(Helper.apiEnumToGovway(body.getHttpMethod(), HttpMethod.class));

		if (res.getMethod() != null)
			res.set_value_method(res.getMethod().toString());

		if (StringUtils.isEmpty(res.getNome()) && res.getMethod() != null)
			res.setNome(APIUtils.normalizeResourceName(res.getMethod(), pathNormalizzato));

		res.setIdCollaborazione( Helper.boolToStatoFunzionalita(body.isIdCollaborazione()));
		res.setIdRiferimentoRichiesta( Helper.boolToStatoFunzionalita(body.isRiferimentoIdRichiesta()));		
		res.setProfAzione(CostantiRegistroServizi.PROFILO_AZIONE_RIDEFINITO);
	}

	// Todo prendiFiltroDuplicati e roba dal parent.
	public static final Resource apiRisorsaToRegistro(ApiRisorsa body, AccordoServizioParteComune parent) {
		Resource ret = new Resource();

		ret.setMessageType(null);
		ret.setRequestMessageType(null);
		ret.setResponseMessageType(null);
		ret.setFiltroDuplicati(parent.getFiltroDuplicati());
		ret.setConfermaRicezione(parent.getConfermaRicezione());
		ret.setIdCollaborazione(parent.getIdCollaborazione());
		ret.setIdRiferimentoRichiesta(parent.getIdRiferimentoRichiesta());
		ret.setConsegnaInOrdine(parent.getConsegnaInOrdine());
		ret.setScadenza(parent.getScadenza());		

		updateRisorsa(body,ret);

		return ret;
	}


	public static final ApiRisorsa risorsaRegistroToApi(Resource r) {
		ApiRisorsa ret = new ApiRisorsa();

		ret.setNome(r.getNome());
		ret.setDescrizione(r.getDescrizione());

		ret.setHttpMethod( r.get_value_method() == null || HttpMethodEnum.valueOf(r.get_value_method()) == null 
				? HttpMethodEnum.QUALSIASI 
						: HttpMethodEnum.valueOf(r.get_value_method())
				);

		ret.setIdCollaborazione(r.getIdCollaborazione() == StatoFunzionalita.ABILITATO ? true : false);
		ret.setRiferimentoIdRichiesta(r.getIdRiferimentoRichiesta() == StatoFunzionalita.ABILITATO ? true : false);
		ret.setPath(r.getPath());

		return ret;
	}

	public static void populateApiRisorsaWithProtocolInfo(AccordoServizioParteComune as, Resource res, ApiEnv env, ProfiloEnum profilo, ApiRisorsa ret) throws Exception {
		
		if(profilo != null) {
			switch(profilo) {
			case APIGATEWAY:
				return ;// trasparente 
			case EDELIVERY:
				EDeliveryApiApiHelper.populateApiRisorsaWithProtocolInfo(as, res, env, ret);
				break;
			case FATTURAPA:
				FatturaPAApiApiHelper.populateApiRisorsaWithProtocolInfo(as, res, env, ret);
				break;
			case MODI:
			case MODIPA:
				ModiApiApiHelper.populateApiRisorsaWithProtocolInfo(as, res,  env, ret);
				break;
			case SPCOOP:
				SPCoopApiApiHelper.populateApiRisorsaWithProtocolInfo(as, res, env, ret);
			}
		}		
	}

	public static void populateApiAzioneWithProtocolInfo(AccordoServizioParteComune as, Operation az, ApiEnv env, ProfiloEnum profilo, ApiAzione ret) throws Exception {
		
		if(profilo != null) {
			switch(profilo) {
			case APIGATEWAY:
				return ;// trasparente 
			case EDELIVERY:
				EDeliveryApiApiHelper.populateApiAzioneWithProtocolInfo(as, az, env, ret);
				break;
			case FATTURAPA:
				FatturaPAApiApiHelper.populateApiAzioneWithProtocolInfo(as, az, env, ret);
				break;
			case MODI:
			case MODIPA:
				ModiApiApiHelper.populateApiAzioneWithProtocolInfo(as, az,  env, ret);
				break;
			case SPCOOP:
				SPCoopApiApiHelper.populateApiAzioneWithProtocolInfo(as, az, env, ret);
			}
		}		
	}

	public static final ApiItem apiToItem(Api api, AccordoServizioParteComuneSintetico as, ApiEnv env) {
		ApiItem ret = new ApiItem();

		ret.setDescrizione(api.getDescrizione());
		ret.setTipoInterfaccia(api.getTipoInterfaccia());
		ret.setNome(api.getNome());
		ret.setProfilo(env.profilo);	// TODO: In multitenant questo va cambiato al profilo relativo al tip del soggetto referente dell'as
		ret.setSoggetto(api.getReferente());
		ret.setVersione(api.getVersione());
		ret.setReferente(api.getReferente());


		StatoApiEnum stato = null;
		String descrizioneStato = "";
		ConsoleSearch searchForCount = new ConsoleSearch(true);
		switch (ret.getTipoInterfaccia().getProtocollo()) {
		case REST:
			// caso REST: l'API e' abilitata se ha almeno una risorsa
			try {
				env.apcCore.accordiResourceList(as.getId().intValue(), searchForCount);
			} catch (Exception e) { throw new RuntimeException(e); }

			int numRisorse = searchForCount.getNumEntries(Liste.ACCORDI_API_RESOURCES);

			if(numRisorse > 0) {
				stato = StatoApiEnum.OK;
			}
			else {
				stato = StatoApiEnum.ERROR;
				descrizioneStato = ApiCostanti.APC_API_ICONA_STATO_RISORSE_TUTTE_DISABILITATE_TOOLTIP;
			}
			break;
		case SOAP:
		default:

			List<PortType> accordiPorttypeList = null;
			try {
				accordiPorttypeList = env.apcCore.accordiPorttypeList(as.getId().intValue(), searchForCount);
			} catch (Exception e) { throw new RuntimeException(e); }

			int numeroTotaleServizi = accordiPorttypeList.size();
			int numeroServiziAbilitati = 0;

			for (PortType portType : accordiPorttypeList) {
				if(portType.sizeAzioneList()>0) {
					numeroServiziAbilitati ++;
				}	
			}

			if(numeroTotaleServizi == 0) {
				stato = StatoApiEnum.ERROR;
				descrizioneStato = ApiCostanti.APC_API_ICONA_STATO_SERVIZI_TUTTI_DISABILITATI_TOOLTIP;
			}
			else if(numeroServiziAbilitati==0) {
				stato = StatoApiEnum.ERROR;
				if(numeroTotaleServizi==1) {
					descrizioneStato = ApiCostanti.APC_API_ICONA_STATO_SERVIZIO_PARZIALMENTE_CONFIGURATO_DISABILITATI_TOOLTIP;
				}
				else {
					descrizioneStato = ApiCostanti.APC_API_ICONA_STATO_SERVIZI_TUTTI_SENZA_AZIONE_TOOLTIP;
				}
			} else if(numeroServiziAbilitati == numeroTotaleServizi) {
				stato = StatoApiEnum.OK;
			} 
			else {
				stato = StatoApiEnum.WARN;
				descrizioneStato = ApiCostanti.APC_API_ICONA_STATO_SERVIZI_PARZIALMENTE_ABILITATI_TOOLTIP;
			}
			break;
		}

		ret.setStatoDescrizione(descrizioneStato);
		ret.setStato(stato);

		if(as.getGruppo()!=null &&  !as.getGruppo().isEmpty()) {
			ret.setTags(new ArrayList<String>());
			for (GruppoSintetico tag : as.getGruppo()) {
				ret.addTagsItem(tag.getNome());
			}
		}

		if(env.gestioneCanali) {
			ApiCanale canale = new ApiCanale();
			if(as.getCanale()!=null && !"".equals(as.getCanale())) {
				canale.setNome(as.getCanale());
				canale.setConfigurazione(CanaleEnum.API);
			}
			else {
				canale.setNome(env.canaleDefault);
				canale.setConfigurazione(CanaleEnum.DEFAULT);
			}
			ret.setCanale(canale);
		}

		return ret;

	}

	public static final Api accordoSpcRegistroToApi(AccordoServizioParteComuneSintetico as, SoggettiCore soggettiCore) {
		Api ret = new Api();

		ret.setNome(as.getNome());
		ret.setDescrizione(as.getDescrizione());

		switch (as.getServiceBinding()) {
		case REST:
			ApiInterfacciaRest iRest = new ApiInterfacciaRest();
			iRest.setProtocollo(TipoApiEnum.REST);
			iRest.setFormato(Enums.formatoRestFromSpecifica.get(as.getFormatoSpecifica()));
			ret.setTipoInterfaccia(iRest);
			ret.setInterfaccia(as.getByteWsdlConcettuale());
			break;
		case SOAP:
			ApiInterfacciaSoap iSoap = new ApiInterfacciaSoap();
			iSoap.setProtocollo(TipoApiEnum.SOAP);
			iSoap.setFormato(Enums.formatoSoapFromSpecifica.get(as.getFormatoSpecifica()));
			ret.setTipoInterfaccia(iSoap);
			ret.setInterfaccia(as.getByteWsdlLogicoErogatore());
			break;
		}

		ret.setVersione(as.getVersione());
		ret.setReferente(as.getSoggettoReferente().getNome());

		return ret;	
	}


	// Versione "deprecata" in favore di quella più generica nell'Helper
	public static final AccordoServizioParteComune getAccordoFull(String nome, Integer versione, ApiEnv env) throws CoreException {
		return Helper.getAccordoFull(nome, versione, env.idSoggetto.toIDSoggetto(), env.apcCore);
	}
	public static final AccordoServizioParteComuneSintetico getAccordoSintetico(String nome, Integer versione, ApiEnv env) throws CoreException {
		return Helper.getAccordoSintetico(nome, versione, env.idSoggetto.toIDSoggetto(), env.apcCore);
	}

}
