/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
package org.openspcoop2.protocol.modipa.handlers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.ProtocolProperty;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.pdd.core.dynamic.DynamicException;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.InRequestContext;
import org.openspcoop2.pdd.core.handlers.InRequestHandler;
import org.openspcoop2.protocol.basic.registry.RegistryReader;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.modipa.config.ModISignalHubConfig;
import org.openspcoop2.protocol.modipa.config.ModISignalHubParamConfig;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.modipa.constants.ModISignalHubOperation;
import org.openspcoop2.protocol.modipa.properties.ModIDynamicConfigurationAccordiParteComuneUtilities;
import org.openspcoop2.protocol.modipa.utils.ModIUtilities;
import org.openspcoop2.protocol.registry.RegistroServiziReader;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.registry.ProtocolFiltroRicercaServizi;
import org.openspcoop2.protocol.sdk.registry.RegistryException;
import org.openspcoop2.protocol.sdk.registry.RegistryNotFound;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.utils.ProfiloUtils;
import org.slf4j.Logger;

/**
 * SignalHubPushInRequestHandler
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SignalHubPushInProtocolRequestHandler implements InRequestHandler {
	
	@Override
	public void invoke(InRequestContext context) throws HandlerException {
		try {
			if (checkSignalHubPushConditions(context)) {
				List<ProtocolProperty> props = getSignalHubPushProperty(context);
				parseSignalHubPushProperty(context, props);
			}
		} catch (ProtocolException 
				| DriverRegistroServiziException 
				| DynamicException 
				| RegistryNotFound 
				| RegistryException e) {
			throw new HandlerException(e);
		} 
	}
	
	private boolean checkSignalHubPushConditions(InRequestContext context) throws ProtocolException, DriverRegistroServiziException, RegistryNotFound, RegistryException {
		Logger logger = context.getLogCore();
		
		
		// controllo che signalhub sia abilitato
		ModIProperties modiProperties = ModIProperties.getInstance();
		if(!modiProperties.isSignalHubEnabled()) { 
			return false;
		}
		
		// controllo che sia una fruizione ModI
		if (context.getTipoPorta() == null
				|| !context.getTipoPorta().equals(TipoPdD.DELEGATA)
				|| context.getPddContext() == null)
			return false;
		
		IRegistryReader registryReader = this.getIRegistryReader(context);

		
		// controllo che l'id dell'accordo sia raggiungibile
		AccordoServizioParteSpecifica asps = registryReader.getAccordoServizioParteSpecifica(this.getRequestInfo(context).getIdServizio());
		IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
		if (idAccordo == null)
			return false;
		
		
		// controllo che l'accordo sia l'accordo built-in per la registrazione dei segnali
		boolean isApiSignalHubPushAPI = false;
			
		Map<String, IDriverRegistroServiziGet> readers = RegistroServiziReader.getDriverRegistroServizi();

		for (Map.Entry<String, IDriverRegistroServiziGet> entry : readers.entrySet()) {
			IRegistryReader reader;
			try {
				reader = new RegistryReader(entry.getValue(), logger);
				isApiSignalHubPushAPI |= ModIDynamicConfigurationAccordiParteComuneUtilities.isApiSignalHubPushAPI(idAccordo, reader, ModIProperties.getInstance(), logger);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				// ignore
			}
		}
		
		if (!isApiSignalHubPushAPI)
			return false;
		
		logger.debug("Nuova richiesta di deposito di segnali SignalHub");
		
		return true;
	}
	
	
	private RequestInfo getRequestInfo(InRequestContext context) {
		return (RequestInfo) context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
	}
	
	private IRegistryReader getIRegistryReader(InRequestContext context) throws ProtocolException {
		return context.getProtocolFactory().getCachedRegistryReader(context.getStato(), this.getRequestInfo(context));
	}
	
	private IDSoggetto getFruitore(InRequestContext context) {
		return this.getRequestInfo(context).getFruitore();
	}
	
	private List<ProtocolProperty> getSignalHubPushProperty(InRequestContext context) throws ProtocolException, RegistryNotFound, RegistryException {
		IRegistryReader reader = this.getIRegistryReader(context);
		IDServizio servizio = this.getRequestInfo(context).getIdServizio();
		IDSoggetto idFruitore = this.getFruitore(context);
		
		AccordoServizioParteSpecifica asps = reader.getAccordoServizioParteSpecifica(servizio, false);
		List<Fruitore> fruitori = Objects.requireNonNullElseGet(asps.getFruitoreList(), ArrayList::new);
		
		Optional<Fruitore> fruitore = fruitori
				.stream().filter(f -> f.getNome().equals(idFruitore.getNome()) && f.getTipo().equals(idFruitore.getTipo()))
				.findAny();
				
		if (fruitore.isEmpty())
			return List.of();
		
		List<ProtocolProperty> props = fruitore.get().getProtocolPropertyList();
		if (props != null && !props.isEmpty())
			return props;
			
		return List.of();
	}

	private String getDynamicProperty(List<ProtocolProperty> props, ModISignalHubParamConfig param, Context context, Map<String, Object> dynamicMap, boolean useDefault) throws ProtocolException {
		try {
			String key = CostantiDB.MODIPA_API_IMPL_PUSH_SIGNAL_HUB_PARAM_VALUE_ID_PREFIX + param.getPropertyId();
			String mode = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(props, CostantiDB.MODIPA_API_IMPL_PUSH_SIGNAL_HUB_PARAM_MODE_ID_PREFIX + param.getPropertyId());
			String ridefinedValue = ProtocolPropertiesUtils.getOptionalStringValuePropertyRegistry(props, CostantiDB.MODIPA_API_IMPL_PUSH_SIGNAL_HUB_PARAM_VALUE_ID_PREFIX + param.getPropertyId());
			List<String> values = null;
					
			// se il campo e' stato ridefinito uso il template ridefinito altrimenti quello di default
			if (!useDefault && mode.equals(CostantiDB.MODIPA_PROFILO_RIDEFINISCI)) {
				values = List.of(ridefinedValue);
			} else {
				values = param.getRules();
			}
	
			return ModIUtilities.getDynamicValue(key, values, dynamicMap, context);
		} catch (ProtocolException e) {
			return null;
		}
	}
	
	private void parseSignalHubPushProperty(InRequestContext context, List<ProtocolProperty> props) throws ProtocolException, DynamicException, HandlerException, RegistryNotFound, RegistryException, NumberFormatException, DriverRegistroServiziException { 
		ModIProperties modiProperties = ModIProperties.getInstance();
		ModISignalHubConfig signalHubConfig = modiProperties.getSignalHubConfig();
		Logger logger = context.getLogCore();

		Map<String, ModISignalHubParamConfig> claims = signalHubConfig.getClaims()
				.stream()
				.collect(Collectors.toMap(v -> v.getPropertyId(), v -> v));
		
		Map<String, Object> dynamicMap = DynamicUtils.buildDynamicMap(context.getMessaggio(), context.getPddContext(), null, logger, modiProperties.isReadByPathBufferEnabled());

		// controllo se il segnale e' un segnale di SEEDUPDATE in tal caso le informazioni saranne messe da govway negli header di default
		boolean isSeedUpdate = false;
		String signalTypeRaw = getDynamicProperty(props, claims.get(ModICostanti.MODIPA_SIGNAL_HUB_ID_SIGNAL_TYPE), context.getPddContext(), dynamicMap, true);
		ModISignalHubOperation signalType = ModISignalHubOperation.fromString(signalTypeRaw);
		
		if (signalType != null && signalType.equals(ModISignalHubOperation.SEEDUPDATE))
			isSeedUpdate = true;
		
		// ottengo le varie informaizoni di deposito del segnale (nel caso di seedupdate usero i template di default)
		String objectId = getDynamicProperty(props, claims.get(ModICostanti.MODIPA_SIGNAL_HUB_ID_OBJECT_ID), context.getPddContext(), dynamicMap, isSeedUpdate);
		String objectType = getDynamicProperty(props, claims.get(ModICostanti.MODIPA_SIGNAL_HUB_ID_OBJECT_TYPE), context.getPddContext(), dynamicMap, isSeedUpdate);
		signalTypeRaw = getDynamicProperty(props, claims.get(ModICostanti.MODIPA_SIGNAL_HUB_ID_SIGNAL_TYPE), context.getPddContext(), dynamicMap, isSeedUpdate);

		if (objectId == null) {
			throw newHandlerException("Parametro obbligatorio 'objectId' non rilevato", IntegrationFunctionError.BAD_REQUEST);
		}
		if (objectType == null) {
			throw newHandlerException("Parametro obbligatorio 'objectType' non rilevato", IntegrationFunctionError.BAD_REQUEST);
		}
		if (signalTypeRaw == null) {
			throw newHandlerException("Parametro obbligatorio 'signalType' non rilevato", IntegrationFunctionError.BAD_REQUEST);
		}
		
		signalType = ModISignalHubOperation.fromString(signalTypeRaw);
		if (signalType == null) {
			throw newHandlerException("Parametro 'signalType' con valore errato '"+signalTypeRaw+"'; valori supportati: UPDATE, CREATE, DELETE", IntegrationFunctionError.BAD_REQUEST);
		}
		
		String service = null;
		String serviceVersion = null;
		String serviceId = null;
		
		service = getDynamicProperty(props, claims.get("service"), context.getPddContext(), dynamicMap, isSeedUpdate);
		serviceVersion = getDynamicProperty(props, claims.get("serviceVersion"), context.getPddContext(), dynamicMap, isSeedUpdate);
		serviceId = getDynamicProperty(props, claims.get("serviceId"), context.getPddContext(), dynamicMap, isSeedUpdate);
		
		
		IRegistryReader reader = this.getIRegistryReader(context);
		
		IDServizio idServizio = null;
		List<ProtocolProperty> eServiceProperties = null;
		
		if (serviceId == null && (service == null || serviceVersion == null)) {
			throw newHandlerException("Un serviceId o in alternativa il nome e la versione devono essere obbligatoriamente indicati per poter individuare il servizio", IntegrationFunctionError.BAD_REQUEST);
		}
		
		// se tutte le informazioni sono presenti ottengo il serviceId a partire dall'id del servizio
		if (serviceId == null) {
			
			int versioneServizio = -1;
			try {
				versioneServizio = Integer.valueOf(serviceVersion);
				if(versioneServizio<=0) {
					throw new CoreException("Valore deve essere maggiore di zero");
				}
			}catch(Exception e) {
				String msg = "Parametro 'serviceVersion' presenta un valore '"+serviceVersion+"' non valido; deve essere un numero intero maggiore di zero";
				logger.error(msg+": "+e.getMessage(), e);
				throw newHandlerException(msg, IntegrationFunctionError.BAD_REQUEST);
			}
			
			try {
				idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(
						ProfiloUtils.toProtocollo(ProfiloEnum.MODIPA), 
						service,
						this.getFruitore(context), 
						versioneServizio);
						AccordoServizioParteSpecifica asps = reader.getAccordoServizioParteSpecifica(idServizio);
						eServiceProperties = asps.getProtocolPropertyList();
			}catch(RegistryNotFound notFound) {
				String nomeSoggetto = idServizio!=null && idServizio.getSoggettoErogatore()!=null && idServizio.getSoggettoErogatore().getNome()!=null ? idServizio.getSoggettoErogatore().getNome() : "Non identificato";
				throw newHandlerException("Il soggetto '"+nomeSoggetto+"' non risulta erogare un servizio con nome '"+service+"'  e versione '"+versioneServizio+"'", IntegrationFunctionError.BAD_REQUEST);
			}
		} else {
			// altrimenti ottengo l'id sdel servizio partendo dal serviceId
			ProtocolFiltroRicercaServizi filter = new ProtocolFiltroRicercaServizi();
			ProtocolProperties filterProps = new ProtocolProperties();
			filterProps.addProperty(ModICostanti.MODIPA_API_IMPL_INFO_ESERVICE_ID, serviceId);
			filter.setProtocolPropertiesServizi(filterProps);
			
			List<IDServizio> idServices = null;
			try {
				idServices = reader.findIdAccordiServizioParteSpecifica(filter);
			}catch(RegistryNotFound notFound) {
				// ignore
			}
			logger.debug("id servizi: {}", idServices);
			
			if (idServices==null || idServices.isEmpty() || idServices.get(0)==null) {
				throw newHandlerException("Non esiste una erogazione di servizio registrato con l'id servizio '"+serviceId+"' indicato", IntegrationFunctionError.BAD_REQUEST);
			}
			
			idServizio = idServices.get(0);
			
			IDSoggetto soggettoFruitore = this.getFruitore(context);
			if (!idServizio.getSoggettoErogatore().equals(soggettoFruitore)) {
				context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.ERRORE_AUTORIZZAZIONE, "true");
				throw newHandlerException("L'erogazione di servizio individuata con l'id servizio '"+serviceId+"' appartiene ad un soggetto '"+idServizio.getSoggettoErogatore().getNome()+"' differente dal soggetto pubblicatore '"+soggettoFruitore.getNome()+"'", 
						IntegrationFunctionError.AUTHORIZATION_DENY);
			}

			AccordoServizioParteSpecifica asps = reader.getAccordoServizioParteSpecifica(idServizio);
			eServiceProperties = asps.getProtocolPropertyList();
		}
		
		try {
			serviceId = ProtocolPropertiesUtils.getRequiredStringValuePropertyRegistry(eServiceProperties, ModICostanti.MODIPA_API_IMPL_INFO_ESERVICE_ID);
		}catch(Exception e) {
			// succede nel caso di name e version
			throw newHandlerException("L'erogazione del servizio indicato non contiene la configurazione relativa al serviceId", IntegrationFunctionError.BAD_REQUEST);
		}
		
		try {
			BooleanNullable signalHub = ProtocolPropertiesUtils.getOptionalBooleanValuePropertyRegistry(eServiceProperties, ModICostanti.MODIPA_API_IMPL_INFO_SIGNAL_HUB_ID);
			if(signalHub==null || signalHub.getValue()==null || !signalHub.getValue().booleanValue()) {
				throw new CoreException("Non Attiva");
			}
		}catch(Exception e) {
			// succede nel caso non sia stata attuata la configurazione
			String msg = "Nell'erogazione del servizio indicato non risulta attiva la funzionalità Signal Hub";
			logger.error(msg+": "+e.getMessage(), e);
			throw newHandlerException(msg, IntegrationFunctionError.BAD_REQUEST);
		}
		
		context.getPddContext().addObject(ModICostanti.MODIPA_KEY_INFO_SIGNAL_HUB_OBJECT_ID, objectId);
		context.getPddContext().addObject(ModICostanti.MODIPA_KEY_INFO_SIGNAL_HUB_ESERVICE_ID, serviceId);
		context.getPddContext().addObject(ModICostanti.MODIPA_KEY_INFO_SIGNAL_HUB_SERVICE, idServizio);
		context.getPddContext().addObject(ModICostanti.MODIPA_KEY_INFO_SIGNAL_HUB_OBJECT_TYPE, objectType);
		context.getPddContext().addObject(ModICostanti.MODIPA_KEY_INFO_SIGNAL_HUB_SIGNAL_TYPE, signalType);
		context.getPddContext().addObject(ModICostanti.MODIPA_KEY_INFO_SIGNAL_HUB_PROPERTIES, eServiceProperties);
	}

	private HandlerException newHandlerException(String msg, IntegrationFunctionError error) {
		HandlerException he = new HandlerException(msg);
		he.setIntegrationFunctionError(error);
		return he;
	}
}
