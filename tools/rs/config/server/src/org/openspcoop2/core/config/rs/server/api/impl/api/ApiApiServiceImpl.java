/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.rs.server.api.ApiApi;
import org.openspcoop2.core.config.rs.server.api.impl.Enums;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.Api;
import org.openspcoop2.core.config.rs.server.model.ApiAllegato;
import org.openspcoop2.core.config.rs.server.model.ApiAzione;
import org.openspcoop2.core.config.rs.server.model.ApiCanale;
import org.openspcoop2.core.config.rs.server.model.ApiDescrizione;
import org.openspcoop2.core.config.rs.server.model.ApiInformazioniGenerali;
import org.openspcoop2.core.config.rs.server.model.ApiInformazioniGeneraliView;
import org.openspcoop2.core.config.rs.server.model.ApiInterfaccia;
import org.openspcoop2.core.config.rs.server.model.ApiInterfacciaRest;
import org.openspcoop2.core.config.rs.server.model.ApiInterfacciaSoap;
import org.openspcoop2.core.config.rs.server.model.ApiInterfacciaView;
import org.openspcoop2.core.config.rs.server.model.ApiItem;
import org.openspcoop2.core.config.rs.server.model.ApiModI;
import org.openspcoop2.core.config.rs.server.model.ApiReferenteView;
import org.openspcoop2.core.config.rs.server.model.ApiRisorsa;
import org.openspcoop2.core.config.rs.server.model.ApiServizio;
import org.openspcoop2.core.config.rs.server.model.ApiTags;
import org.openspcoop2.core.config.rs.server.model.ApiViewItem;
import org.openspcoop2.core.config.rs.server.model.CanaleEnum;
import org.openspcoop2.core.config.rs.server.model.ConfigurazioneApiCanale;
import org.openspcoop2.core.config.rs.server.model.ConfigurazioneCanaleEnum;
import org.openspcoop2.core.config.rs.server.model.HttpMethodEnum;
import org.openspcoop2.core.config.rs.server.model.ListaApi;
import org.openspcoop2.core.config.rs.server.model.ListaApiAllegati;
import org.openspcoop2.core.config.rs.server.model.ListaApiAzioni;
import org.openspcoop2.core.config.rs.server.model.ListaApiRisorse;
import org.openspcoop2.core.config.rs.server.model.ListaApiServizi;
import org.openspcoop2.core.config.rs.server.model.RuoloAllegatoAPI;
import org.openspcoop2.core.config.rs.server.model.TipoApiEnum;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortType;
import org.openspcoop2.core.id.IDPortTypeAzione;
import org.openspcoop2.core.id.IDResource;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.GruppiAccordo;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.GruppoAccordo;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.HttpMethod;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.basic.archive.APIUtils;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.utils.json.YAMLUtils;
import org.openspcoop2.utils.service.BaseImpl;
import org.openspcoop2.utils.service.authorization.AuthorizationConfig;
import org.openspcoop2.utils.service.authorization.AuthorizationManager;
import org.openspcoop2.utils.service.beans.ProfiloCollaborazioneEnum;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.beans.utils.ListaUtils;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.SerialiableFormFile;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviHelper;
import org.openspcoop2.web.ctrlstat.servlet.gruppi.GruppiCore;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ApiApiServiceImpl
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ApiApiServiceImpl extends BaseImpl implements ApiApi {

	public ApiApiServiceImpl() {
		super(org.slf4j.LoggerFactory.getLogger(ApiApiServiceImpl.class));
	}

	private AuthorizationConfig getAuthorizationConfig() throws Exception {
		return new AuthorizationConfig(ServerProperties.getInstance().getProperties());
	}

	/**
	 * Creazione di un'API
	 *
	 * Questa operazione consente di creare una API
	 *
	 */
	@Override
	public void createApi(Api body, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			if (body == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");

			if (body.getReferente() == null || "".equals(body.getReferente().trim())) {
				body.setReferente(soggetto);
			}

			ApiEnv env = new ApiEnv(profilo, soggetto, context);

			AccordoServizioParteComune as = ApiApiHelper.accordoApiToRegistro(body, env);

			ProtocolProperties protocolProperties = null;
			if(profilo != null) {
				protocolProperties = ApiApiHelper.getProtocolProperties(body, profilo);
	
				if(protocolProperties != null) {
					as.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(protocolProperties, ConsoleOperationType.ADD, null));
				}
			}
			
			IDAccordo idAccordoFromAccordo = env.idAccordoFactory.getIDAccordoFromAccordo(as);
			if (env.apcCore.existsAccordoServizio(idAccordoFromAccordo))
				throw FaultCode.CONFLITTO.toException("Api già esistente");

			boolean validazioneDocumenti = ServerProperties.getInstance().isValidazioneDocumenti();

			StringBuilder bfTags = new StringBuilder();
			if(body!=null && body.getTags()!=null && !body.getTags().isEmpty()) {
				for (String tag : body.getTags()) {
					if(bfTags.length()>0) {
						bfTags.append(",");
					}
					bfTags.append(tag);
				}
			}
			
			boolean gestioneCanaliEnabled = env.gestioneCanali;
			String canale = as.getCanale();
			String canaleStato = null;
			if(canale == null) {
				canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_DEFAULT;
			} else {
				canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_RIDEFINITO;
			}
						
			if (!env.apcHelper.accordiCheckData(TipoOperazione.ADD, as.getNome(), as.getDescrizione(),
					as.getProfiloCollaborazione().toString(), Helper.toBinaryParameter(as.getByteWsdlDefinitorio()), // wsdldef
					Helper.toBinaryParameter(as.getByteWsdlConcettuale()), // wsdlconc,
					Helper.toBinaryParameter(as.getByteWsdlLogicoErogatore()), // wsdlserv
					Helper.toBinaryParameter(as.getByteWsdlLogicoFruitore()), // wsdlservcorr,
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getFiltroDuplicati()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConfermaRicezione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdCollaborazione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdRiferimentoRichiesta()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConsegnaInOrdine()), "", // scadenza
					"0", // Id intero del servizio applicativo
					as.getSoggettoReferente().getId().toString(), // IDSoggetto?
					as.getVersione().toString(), null, // accordoCooperazione
					false, // visibilitaAccordoServizio
					false, // visibilitaAccordoCooperazione
					null, // ID AccordoOld
					new BinaryParameter(), // wsblconc
					new BinaryParameter(), // wsblserv
					new BinaryParameter(), // wsblservcorr
					validazioneDocumenti, env.tipo_protocollo, null, // backToStato
					env.apcCore.toMessageServiceBinding(as.getServiceBinding()), null, // messageType
					Enums.interfaceTypeFromFormatoSpecifica.get(as.getFormatoSpecifica()), env.gestisciSoggettoReferente,
					bfTags.toString(),
					canaleStato, canale, gestioneCanaliEnabled
					)) {

				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			ApiApiHelper.validateProperties(env, protocolProperties, idAccordoFromAccordo, ConsoleOperationType.ADD);
			

			List<Object> objectToCreate = new ArrayList<>();
			
			if(body.getTags()!=null && !body.getTags().isEmpty()) {
				as.setGruppi(new GruppiAccordo());
				
				GruppiCore gruppiCore = new GruppiCore(env.stationCore);
				
				for (String tag : body.getTags()) {
					GruppoAccordo gruppo = new GruppoAccordo();
					gruppo.setNome(tag);
					as.getGruppi().addGruppo(gruppo);
					
					if(!gruppiCore.existsGruppo(tag)) {
						Gruppo nuovoGruppo = new Gruppo();
						nuovoGruppo.setNome(tag);
						nuovoGruppo.setSuperUser(as.getSuperUser());
						objectToCreate.add(nuovoGruppo);
					}
				}
			}
			
			objectToCreate.add(as);
			
			// effettuo le operazioni
			env.apcCore.performCreateOperation(env.userLogin, false, objectToCreate.toArray(new Object[objectToCreate.size()]));
			
			context.getLogger().info("Invocazione completata con successo");
			
			// Bug Fix: altrimenti viene generato 204
			context.getServletResponse().setStatus(201);
			
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Creazione di un allegato di una API
	 *
	 * Questa operazione consente di aggiungere un allegato alla API identificata
	 * dal nome e dalla versione
	 *
	 */
	@Override
	public void createApiAllegato(ApiAllegato body, String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {

			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			if (body == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Body non presente");

			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = Helper.getAccordoFull(nome, versione, env.idSoggetto.toIDSoggetto(), env.apcCore);

			if (as == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Nessuna Api corrisponde ai parametri indicati");

			Documento documento = ApiApiHelper.apiAllegatoToDocumento(body, as, env);

			if(RuoloAllegatoAPI.ALLEGATO.equals(body.getAllegato().getRuolo())) {
				as.addAllegato(documento);
			}
			else if(RuoloAllegatoAPI.SPECIFICASEMIFORMALE.equals(body.getAllegato().getRuolo())) {
				as.addSpecificaSemiformale(documento);
			}else {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Ruolo " + body.getAllegato().getRuolo() + " sconosciuto");
			}

			ArchiviCore archiviCore = new ArchiviCore(env.stationCore);
			SerialiableFormFile filewrap = new SerialiableFormFile(documento.getFile(), documento.getByteContenuto());

			env.requestWrapper.overrideParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_RUOLO,
					documento.getRuolo());

			ArchiviHelper archiviHelper = new ArchiviHelper(env.stationCore, env.requestWrapper, env.pd,
					env.requestWrapper.getSession());

			boolean documentoUnivocoIndipendentementeTipo = true;
			if (archiviCore.existsDocumento(documento, ProprietariDocumento.accordoServizio,
					documentoUnivocoIndipendentementeTipo)) {
				throw FaultCode.CONFLITTO.toException("Allegato con nome " + documento.getFile() + " già presente nella API");
			}

			if (!archiviHelper.accordiAllegatiCheckData(TipoOperazione.ADD, filewrap, documento,
					ProprietariDocumento.accordoServizio, env.protocolFactory)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}

			env.apcCore.performUpdateOperation(env.userLogin, false, as);

			context.getLogger().info("Invocazione completata con successo");
			
			// Bug Fix: altrimenti viene generato 204
			context.getServletResponse().setStatus(201);
			
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Creazione di un'azione di una API
	 *
	 * Questa operazione consente di aggiungere una azione al servizio della API
	 * identificata dal nome e dalla versione
	 *
	 */
	@Override
	public void createApiAzione(ApiAzione body, String nome, Integer versione, String nomeServizio, ProfiloEnum profilo,
			String soggetto) {
		IContext context = this.getContext();
		// body.
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			if (body == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");

			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Nessuna Api con nome: " + nome + " e versione " + versione);
			if (as.getServiceBinding() == ServiceBinding.REST)
				throw FaultCode.RICHIESTA_NON_VALIDA
						.toException("Non è possibile registrare Azioni e Servizi su Api con interfaccia REST");

			PortType pt = as.getPortTypeList().stream().filter(p -> nomeServizio.equals(p.getNome())).findFirst()
					.orElse(null);

			if (pt == null)
				throw FaultCode.NOT_FOUND.toException("Nessun Servizio con nome: " + nomeServizio);

			Operation newOp = ApiApiHelper.apiAzioneToOperazione(body, pt);

			if (env.apcCore.existsAccordoServizioPorttypeOperation(newOp.getNome(), pt.getId()))
				throw FaultCode.CONFLITTO.toException("L'azione " + newOp.getNome() + " è già stata associata alla Api");

			ProtocolProperties protocolProperties = null;
			if(profilo != null) {
				protocolProperties = ApiApiHelper.getProtocolProperties(body, profilo, as, newOp, env);
	
				if(protocolProperties != null) {
					newOp.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(protocolProperties, ConsoleOperationType.ADD, null));
				}
			}

			
			pt.addAzione(newOp);
			
			ProfiloCollaborazioneEnum profiloBody = body.getProfiloCollaborazione()!=null ? body.getProfiloCollaborazione() : ProfiloCollaborazioneEnum.SINCRONO;
			
			if (! env.apcHelper.accordiPorttypeOperationCheckData(
					TipoOperazione.ADD,
					as.getId().toString(),
					nomeServizio,
					newOp.getNome(),
					newOp.getProfAzione(),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newOp.getFiltroDuplicati()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newOp.getConfermaRicezione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newOp.getIdCollaborazione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newOp.getIdRiferimentoRichiesta()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newOp.getConsegnaInOrdine()),
					newOp.getScadenza() != null ? newOp.getScadenza() : "", "-", // Servizio Correlato
					"-", // Azione Correlata
					Enums.profiloCollaborazioneFromApiEnum.get(profiloBody).toString(), "0", // styleOp
					"", // soapActionOp,
					"literal", // useOp,
					null, // opTypeOp,
					"" // nsWSDLOp
			)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			IDPortType idPT = new IDPortType();
			idPT.setNome(pt.getNome());
			idPT.setIdAccordo(IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as));
			IDPortTypeAzione idAccordoAzione = new IDPortTypeAzione();
			idAccordoAzione.setNome(newOp.getNome());
			idAccordoAzione.setIdPortType(idPT);
			
			ApiApiHelper.validateProperties(env, protocolProperties, idAccordoAzione, ConsoleOperationType.ADD);
			

			AccordiServizioParteComuneUtilities.createPortTypeOperation(env.apcCore.isEnableAutoMappingWsdlIntoAccordo(),
					env.apcCore, env.apcHelper, as, pt, env.userLogin);

			context.getLogger().info("Invocazione completata con successo");
			
			// Bug Fix: altrimenti viene generato 204
			context.getServletResponse().setStatus(201);
			
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Creazione di una risorsa di una API
	 *
	 * Questa operazione consente di aggiungere una risorsa alla API identificata
	 * dal nome e dalla versione
	 *
	 */
	@Override
	public void createApiRisorsa(ApiRisorsa body, String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			if (body == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");

			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Nessuna Api con nome: " + nome + " e versione " + versione);

			if (as.getServiceBinding() != ServiceBinding.REST) {
				throw FaultCode.RICHIESTA_NON_VALIDA
						.toException("E' possibile registrare risorse solo su API con interfaccia REST");
			}

			Resource newRes = ApiApiHelper.apiRisorsaToRegistro(body, as);

			ProtocolProperties protocolProperties = null;
			if(profilo != null) {
				protocolProperties = ApiApiHelper.getProtocolProperties(body, profilo, newRes, env);
	
				if(protocolProperties != null) {
					newRes.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(protocolProperties, ConsoleOperationType.ADD, null));
				}
			}
			

			if (StringUtils.isEmpty(newRes.getNome()) && newRes.getMethod() == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il campo nome non è stato definito");

			if (env.apcCore.existsAccordoServizioResource(newRes.getMethodRawEnumValue(), newRes.getPath(), as.getId(), null))
				throw FaultCode.CONFLITTO.toException("La risorsa " + newRes.getNome() + " è già stata associata alla Api");

			if (!env.apcHelper.accordiResourceCheckData(TipoOperazione.ADD, as.getId().toString(),
					body.getNome() != null ? body.getNome() : "", newRes.getNome(), newRes.getPath(), newRes.getMethodRawEnumValue(),
					newRes.getDescrizione(), env.apcCore.toMessageMessageType(newRes.getMessageType()), null, // oldNomeRisorsa
					null, // oldNomeRisorsaGenerato
					null, // oldPath
					null, // oldHttpMethod
					AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_DEFAULT,
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newRes.getFiltroDuplicati()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newRes.getConfermaRicezione()), // confricaz,
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newRes.getIdCollaborazione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newRes.getIdRiferimentoRichiesta()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newRes.getConsegnaInOrdine()), // consordaz,
					"" // scadenzaaz
			)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}

			IDResource idResource = new IDResource();
			idResource.setNome(newRes.getNome());
			idResource.setIdAccordo(IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as));
			
			ApiApiHelper.validateProperties(env, protocolProperties, idResource, newRes.getMethod()!=null ? newRes.getMethod().toString() : null, newRes.getPath(), ConsoleOperationType.ADD);
			
			as.addResource(newRes);

			AccordiServizioParteComuneUtilities.createResource(env.apcCore.isEnableAutoMappingWsdlIntoAccordo(), env.apcCore,
					env.apcHelper, as, env.userLogin);

			context.getLogger().info("Invocazione completata con successo");
			
			// Bug Fix: altrimenti viene generato 204
			context.getServletResponse().setStatus(201);
			
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Creazione di un servizio di una API
	 *
	 * Questa operazione consente di aggiungere un servizio alla API identificata
	 * dal nome e dalla versione
	 *
	 */
	@Override
	public void createApiServizio(ApiServizio body, String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			if (body == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");

			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Nessuna Api con nome: " + nome + " e versione " + versione);

			if (as.getServiceBinding() == ServiceBinding.REST) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non è possibile associare un servizio ad una api REST");
			}

			// Il Servizio assumiamo che di base RIDEFINISCA il profilo della api, dato che
			// abbiamo nei campi dei valori che dalla console sono configurabili sono in
			// quel caso.
			PortType newPT = ApiApiHelper.apiServizioToRegistro(body, as, env);

			if (env.apcCore.existsAccordoServizioPorttype(newPT.getNome(), as.getId()))
				throw FaultCode.CONFLITTO.toException("Il servizio " + newPT.getNome() + " è già stato associato alla API");

			if (!env.apcHelper.accordiPorttypeCheckData(TipoOperazione.ADD, as.getId().toString(), newPT.getNome(),
					newPT.getDescrizione(), AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO,
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newPT.getFiltroDuplicati()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newPT.getConfermaRicezione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newPT.getIdCollaborazione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newPT.getIdRiferimentoRichiesta()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newPT.getConsegnaInOrdine()),
					newPT.getScadenza())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			as.addPortType(newPT);

			env.apcCore.performUpdateOperation(env.userLogin, false, as);

			context.getLogger().info("Invocazione completata con successo");
			
			// Bug Fix: altrimenti viene generato 204
			context.getServletResponse().setStatus(201);
			
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Elimina un'api
	 *
	 * Questa operazione consente di eliminare un API identificata dal nome e dalla
	 * versione
	 *
	 */
	@Override
	public void deleteApi(String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			ApiEnv env = new ApiEnv(profilo, soggetto, context);

			AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as != null) {
				StringBuilder inUsoMessage = new StringBuilder();
				
				AccordiServizioParteComuneUtilities.deleteAccordoServizioParteComune(as, env.userLogin, env.apcCore,
						env.apcHelper, inUsoMessage, "\n");
				if (inUsoMessage.length() > 0)
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(inUsoMessage.toString()));
			}

			if (env.delete_404 && as == null)
				throw FaultCode.NOT_FOUND.toException("Api non trovata");

			context.getLogger().info("Invocazione completata con successo");

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Elimina un allegato di una API
	 *
	 * Questa operazione consente di eliminare un'allegato della API identificata
	 * dal nome e dalla versione
	 *
	 */
	@Override
	public void deleteApiAllegato(String nome, Integer versione, String nomeAllegato, ProfiloEnum profilo,
			String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = BaseHelper
					.supplyOrNotFound(() -> ApiApiHelper.getAccordoFull(nome, versione, env), "API");
			final Documento toDel = BaseHelper.evalnull(() -> env.archiviCore.getDocumento(nomeAllegato, null, null,
					as.getId(), false, ProprietariDocumento.accordoServizio)

			);

			if (toDel != null) {
				AccordiServizioParteComuneUtilities.deleteAccordoServizioParteComuneAllegati(as, env.userLogin, env.apcCore,
						env.apcHelper, Arrays.asList(toDel.getId()));
			}

			if (env.delete_404 && toDel == null)
				throw FaultCode.NOT_FOUND
						.toException("Allegato con nome " + nomeAllegato + " non presente per il servizio applicativo scelto.");

			context.getLogger().info("Invocazione completata con successo");
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Elimina un'azione del servizio di una API
	 *
	 * Questa operazione consente di eliminare un'azione del servizio della API
	 * identificata dal nome e dalla versione
	 *
	 */
	@Override
	public void deleteApiAzione(String nome, Integer versione, String nomeServizio, String nomeAzione,
			ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = BaseHelper
					.supplyOrNotFound(() -> ApiApiHelper.getAccordoFull(nome, versione, env), "API");

			PortType pt = null;
			for (int i = 0; i < as.sizePortTypeList(); i++) {
				pt = as.getPortType(i);
				if (nomeServizio.equals(pt.getNome()))
					break;
			}

			if (pt == null)
				throw FaultCode.NOT_FOUND.toException("Nessun Servizio registrato sulla api con nome " + nomeServizio);

			if (BaseHelper.findFirst(pt.getAzioneList(), op -> op.getNome().equals(nomeAzione)).isPresent()) {
				StringBuilder inUsoMessage = new StringBuilder();

				AccordiServizioParteComuneUtilities.deleteAccordoServizioParteComuneOperations(as, env.userLogin, env.apcCore,
						env.apcHelper, inUsoMessage, "\n", pt, Arrays.asList(nomeAzione));

				if (inUsoMessage.length() > 0)
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(inUsoMessage.toString()));

			}

			else if (env.delete_404) {
				throw FaultCode.NOT_FOUND.toException("Azione " + nomeAzione + " non registrata sul servizio " + nomeServizio);
			}

			context.getLogger().info("Invocazione completata con successo");

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Elimina una risorsa di una API
	 *
	 * Questa operazione consente di eliminare una risorsa della API identificata
	 * dal nome e dalla versione
	 *
	 */
	@Override
	public void deleteApiRisorsa(String nome, Integer versione, String nomeRisorsa, ProfiloEnum profilo,
			String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = BaseHelper
					.supplyOrNotFound(() -> ApiApiHelper.getAccordoFull(nome, versione, env), "API");

			if (BaseHelper.findFirst(as.getResourceList(), res -> res.getNome().equals(nomeRisorsa)).isPresent()) {

				StringBuilder inUsoMessage = new StringBuilder();
				AccordiServizioParteComuneUtilities.deleteAccordoServizioParteComuneRisorse(as, env.userLogin, env.apcCore,
						env.apcHelper, inUsoMessage, "\n", Arrays.asList(nomeRisorsa));

				if (inUsoMessage.length() > 0)
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(inUsoMessage.toString()));

			} else if (env.delete_404) {
				throw FaultCode.NOT_FOUND.toException("Risorsa " + nomeRisorsa + " non associata alla API ");
			}

			context.getLogger().info("Invocazione completata con successo");
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Elimina un servizio di una API
	 *
	 * Questa operazione consente di eliminare un servizio della API identificata
	 * dal nome e dalla versione
	 *
	 */
	@Override
	public void deleteApiServizio(String nome, Integer versione, String nomeServizio, ProfiloEnum profilo,
			String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = BaseHelper
					.supplyOrNotFound(() -> ApiApiHelper.getAccordoFull(nome, versione, env), "API");

			if (BaseHelper.findFirst(as.getPortTypeList(), pt -> pt.getNome().equals(nomeServizio)).isPresent()) {

				final StringBuilder inUsoMessage = new StringBuilder();
				AccordiServizioParteComuneUtilities.deleteAccordoServizioParteComunePortTypes(as, env.userLogin, env.apcCore,
						env.apcHelper, inUsoMessage, "\n", Arrays.asList(nomeServizio));

				if (inUsoMessage.length() > 0) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(inUsoMessage.toString()));
				}

			} else if (env.delete_404) {
				throw FaultCode.NOT_FOUND.toException("Servizio " + nomeServizio + " non associato alla API");
			}

			context.getLogger().info("Invocazione completata con successo");
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Restituisce l'allegato di una API
	 *
	 * Questa operazione consente di ottenere l'allegato di una API identificata dal
	 * nome e dalla versione
	 *
	 */
	@Override
	public byte[] downloadApiAllegato(String nome, Integer versione, String nomeAllegato, ProfiloEnum profilo,
			String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Api non trovata");

			Documento doc = as.getAllegatoList().stream().filter(a -> nomeAllegato.equals(a.getFile())).findFirst()
					.orElse(null);

			if (doc == null) {
				doc = as.getSpecificaSemiformaleList().stream().filter(a -> nomeAllegato.equals(a.getFile())).findFirst()
						.orElse(null);
			}
			
			if (doc == null)
				throw FaultCode.NOT_FOUND
						.toException("Nessun allegato con nome " + nomeAllegato + " registrato per la Api specificata");

			doc = env.archiviCore.getDocumento(doc.getFile(), doc.getTipo(), doc.getRuolo(), doc.getIdProprietarioDocumento(),
					true, ProprietariDocumento.accordoServizio);

			context.getLogger().info("Invocazione completata con successo");

			Helper.setContentType(context, doc.getFile());
			
			return doc.getByteContenuto();
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Restituisce l'interfaccia di una API
	 *
	 * Questa operazione consente di ottenere l'interfaccia di una API identificata
	 * dal nome e dalla versione
	 *
	 */
	@Override
	public byte[] downloadApiInterfaccia(String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			// ApiInterfacciaView a = new ApiInterfacciaView();

			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Api non trovata");

			context.getLogger().info("Invocazione completata con successo");

			byte [] spec = null;
			switch (as.getServiceBinding()) {
			case REST:
				spec = as.getByteWsdlConcettuale();
				break;
			case SOAP:
				spec = as.getByteWsdlLogicoErogatore();
				break;
			}
			
			String fileName = null;
			switch (as.getFormatoSpecifica()) {
			case WSDL_11:
				fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WSDL_CONCETTUALE_WSDL;
				break;
			case OPEN_API_3:
				YAMLUtils yamlUtils = YAMLUtils.getInstance();
				if(yamlUtils.isYaml(spec)) {
					fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_OPENAPI_3_0_YAML;
				}
				else {	
					fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_OPENAPI_3_0_JSON;
				}
				break;
			case SWAGGER_2:
				yamlUtils = YAMLUtils.getInstance();
				if(yamlUtils.isYaml(spec)) {
					fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_SWAGGER_2_0_YAML;
				}
				else {
					fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_SWAGGER_2_0_JSON;
				}
				break;
			case WADL:
				fileName = Costanti.OPENSPCOOP2_ARCHIVE_ACCORDI_FILE_WADL;
				break;
			}
			
			if(fileName!=null) {
				Helper.setContentType(context, fileName);
			}
			
			return spec;
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Ricerca api
	 *
	 * Elenca le API registrate
	 *
	 */
	@Override
	public ListaApi findAllApi(ProfiloEnum profilo, String soggetto, String q, Integer limit, Integer offset,
			TipoApiEnum tipoApi, String tag, Boolean profiloQualsiasi) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ApiEnv env = new ApiEnv(profilo, soggetto, context);

			final int idLista = Liste.ACCORDI;
			final ConsoleSearch ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista, env.idSoggetto.toIDSoggetto(),
					env.tipo_protocollo);

			if(profiloQualsiasi!=null && profiloQualsiasi) {
				ricerca.clearFilter(idLista, Filtri.FILTRO_PROTOCOLLO);
			}
			
			if (tipoApi != null)
				ricerca.addFilter(idLista, Filtri.FILTRO_SERVICE_BINDING, Enums.serviceBindingFromTipo.get(tipoApi).toString().toLowerCase());
			
			if(tag!=null) {
				ricerca.addFilter(idLista, Filtri.FILTRO_GRUPPO, tag);
			}

			final String tipoAccordo = "apc"; // Dal debug.
			final List<AccordoServizioParteComuneSintetico> lista = AccordiServizioParteComuneUtilities
					.accordiList(env.apcCore, env.userLogin, ricerca, tipoAccordo);
			
			final ListaApi ret = ListaUtils.costruisciListaPaginata(context.getUriInfo(), 
					ricerca.getIndexIniziale(idLista),
					ricerca.getPageSize(idLista), 
					ricerca.getNumEntries(idLista), ListaApi.class);

			if (env.findall_404 && lista.isEmpty())
				throw FaultCode.NOT_FOUND.toException("Nessuna Api corrisponde ai criteri di ricerca");

			lista.forEach(as -> ret
					.addItemsItem(ApiApiHelper.apiToItem(ApiApiHelper.accordoSpcRegistroToApi(as, env.soggettiCore), as, env)));

			context.getLogger().info("Invocazione completata con successo");
			return ret;

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Elenco allegati di una API
	 *
	 * Questa operazione consente di ottenere gli allegati di una API identificata
	 * dal nome e dalla versione
	 *
	 */
	@Override
	public ListaApiAllegati findAllApiAllegati(String nome, Integer versione, ProfiloEnum profilo, String soggetto,
			String q, Integer limit, Integer offset) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Api non trovata");

			int idLista = Liste.ACCORDI_ALLEGATI;
			ConsoleSearch ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista, env.idSoggetto.toIDSoggetto(),
					env.tipo_protocollo);

			List<Documento> docsRegistro = env.apcCore.accordiAllegatiList(as.getId().intValue(), ricerca);

			if (docsRegistro.size() == 0 && env.findall_404)
				throw FaultCode.NOT_FOUND.toException("Nessun allegato dell'Api specificata corrisponde ai criteri di ricerca");

			final ListaApiAllegati ret = ListaUtils.costruisciListaPaginata(context.getUriInfo(),
					ricerca.getIndexIniziale(idLista),
					ricerca.getPageSize(idLista), 
					ricerca.getNumEntries(idLista), ListaApiAllegati.class);
			docsRegistro.forEach(d -> ret.addItemsItem(ApiApiHelper.documentoToApiAllegatoItem(d)));

			context.getLogger().info("Invocazione completata con successo");

			return ret;

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Elenco servizi di una API
	 *
	 * Questa operazione consente di ottenere le azioni di un servizio della API
	 * identificata dal nome e dalla versione
	 *
	 */
	@Override
	public ListaApiAzioni findAllApiAzioni(String nome, Integer versione, String nomeServizio, ProfiloEnum profilo,
			String soggetto, String q, Integer limit, Integer offset) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Api non trovata");

			int idLista = Liste.ACCORDI_PORTTYPE_AZIONI;
			ConsoleSearch ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista, env.idSoggetto.toIDSoggetto(),
					env.tipo_protocollo);

			PortType pt = as.getPortTypeList().stream().filter(p -> nomeServizio.equals(p.getNome())).findFirst()
					.orElse(null);

			if (pt == null)
				throw FaultCode.NOT_FOUND
						.toException("Non è stato trovato alcun servizio con nome " + nomeServizio + " legato alla Api");

			List<Operation> azioniServizio = env.apcCore.accordiPorttypeOperationList(pt.getId().intValue(), ricerca);

			if (azioniServizio.size() == 0 && env.findall_404)
				throw FaultCode.NOT_FOUND.toException("Nessua azione dell'Api specificata corrisponde ai criteri di ricerca");

			final ListaApiAzioni ret = ListaUtils.costruisciListaPaginata(context.getUriInfo(), 
					ricerca.getIndexIniziale(idLista),
					ricerca.getPageSize(idLista), 
					ricerca.getNumEntries(idLista), ListaApiAzioni.class);

			azioniServizio.forEach(op -> ret.addItemsItem(ApiApiHelper.operazioneToApiAzione(op)));

			context.getLogger().info("Invocazione completata con successo");
			return ret;

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Elenco risorse di una API
	 *
	 * Questa operazione consente di ottenere le risorse di una API identificata dal
	 * nome e dalla versione
	 *
	 */
	@Override
	public ListaApiRisorse findAllApiRisorse(String nome, Integer versione, ProfiloEnum profilo, String soggetto,
			String q, Integer limit, Integer offset, HttpMethodEnum httpMethod) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Api non trovata");

			int idLista = Liste.ACCORDI_API_RESOURCES;

			ConsoleSearch ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista, env.idSoggetto.toIDSoggetto(),
					env.tipo_protocollo);

			if (httpMethod != null)
				ricerca.addFilter(idLista, Filtri.FILTRO_HTTP_METHOD, httpMethod.toString());

			final ListaApiRisorse ret = ListaUtils.costruisciListaPaginata(context.getUriInfo(),
					ricerca.getIndexIniziale(idLista),
					ricerca.getPageSize(idLista), 
					ricerca.getNumEntries(idLista), ListaApiRisorse.class);
			List<Resource> risorse = env.apcCore.accordiResourceList(as.getId().intValue(), ricerca);

			if (risorse.size() == 0 && env.findall_404)
				throw FaultCode.NOT_FOUND.toException("Nessun allegato dell'Api specificata corrisponde ai criteri di ricerca");

			risorse.forEach(r -> ret.addItemsItem(ApiApiHelper.risorsaRegistroToApi(r)));

			context.getLogger().info("Invocazione completata con successo");
			return ret;

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Elenco servizi di una API
	 *
	 * Questa operazione consente di ottenere i servizi di una API identificata dal
	 * nome e dalla versione
	 *
	 */
	@Override
	public ListaApiServizi findAllApiServizi(String nome, Integer versione, ProfiloEnum profilo, String soggetto,
			String q, Integer limit, Integer offset) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Api non trovata");

			int idLista = Liste.ACCORDI_PORTTYPE;

			ConsoleSearch ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista, env.idSoggetto.toIDSoggetto(),
					env.tipo_protocollo);

			List<PortType> servizi = env.apcCore.accordiPorttypeList(as.getId().intValue(), ricerca);

			final ListaApiServizi ret = ListaUtils.costruisciListaPaginata(context.getUriInfo(),
					ricerca.getIndexIniziale(idLista),
					ricerca.getPageSize(idLista), 
					ricerca.getNumEntries(idLista), ListaApiServizi.class);

			if (servizi.size() == 0 && env.findall_404)
				throw FaultCode.NOT_FOUND.toException("Nessun allegato dell'Api specificata corrisponde ai criteri di ricerca");

			servizi.forEach(s -> ret.addItemsItem(ApiApiHelper.servizioRegistroToApi(s)));

			context.getLogger().info("Invocazione completata con successo");
			return ret;
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Restituisce i dettagli di una API
	 *
	 * Questa operazione consente di ottenere i dettagli di una API identificata dal
	 * nome e dalla versione
	 *
	 */
	@Override
	public ApiViewItem getApi(String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComuneSintetico as = ApiApiHelper.getAccordoSintetico(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);

			ApiItem item = ApiApiHelper.apiToItem(ApiApiHelper.accordoSpcRegistroToApi(as, env.soggettiCore), as, env);
			ApiViewItem ret = new ApiViewItem();

			ret.setDescrizione(item.getDescrizione());
			ret.setTipoInterfaccia(item.getTipoInterfaccia());
			ret.setNome(item.getNome());
			ret.setProfilo(item.getProfilo());
			ret.setReferente(item.getSoggetto());
			ret.setSoggetto(item.getSoggetto());
			ret.setVersione(item.getVersione());
			ret.setStato(item.getStato());
			ret.setStatoDescrizione(item.getStatoDescrizione());
			ret.setTags(item.getTags());
			ret.setCanale(item.getCanale());
			context.getLogger().info("Invocazione completata con successo");
			return ret;

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Restituisce il dettaglio di un allegato di una API
	 *
	 * Questa operazione consente di ottenere il dettaglio di un allegato della API
	 * identificata dal nome e dalla versione
	 *
	 */
	@Override
	public ApiAllegato getApiAllegato(String nome, Integer versione, String nomeAllegato, ProfiloEnum profilo,
			String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);

			ApiAllegato ret = as.getAllegatoList().stream().filter(a -> nomeAllegato.equals(a.getFile())).map(a -> {
				try {
					return ApiApiHelper.documentoToApiAllegato(env.archiviCore.getDocumento(a.getId(), true));
				} catch (Exception e) {
					return null;
				}
			}).findFirst().orElse(null);
			
			if(ret==null) {
				ret = as.getSpecificaSemiformaleList().stream().filter(a -> nomeAllegato.equals(a.getFile())).map(a -> {
					try {
						return ApiApiHelper.documentoToApiAllegato(env.archiviCore.getDocumento(a.getId(), true));
					} catch (Exception e) {
						return null;
					}
				}).findFirst().orElse(null);
			}

			if (ret == null)
				throw FaultCode.NOT_FOUND
						.toException("Nessun allegato con nome " + nomeAllegato + " registrato per la Api specificata");

			context.getLogger().info("Invocazione completata con successo");
			return ret;

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Restituisce il dettaglio di un'azione di un servizio della API
	 *
	 * Questa operazione consente di ottenere il dettaglio di un'azione nel servizio
	 * della API identificata dal nome e dalla versione
	 *
	 */
	@Override
	public ApiAzione getApiAzione(String nome, Integer versione, String nomeServizio, String nomeAzione,
			ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);

			PortType pt = as.getPortTypeList().stream().filter(p -> nomeServizio.equals(p.getNome())).findFirst()
					.orElse(null);

			if (pt == null)
				throw FaultCode.NOT_FOUND.toException("Nessun Servizio con nome: " + nomeServizio);

			Operation az = pt.getAzioneList().stream().filter(a -> nomeAzione.equals(a.getNome()))
					.findFirst().orElse(null);

			if (az == null)
				throw FaultCode.NOT_FOUND
						.toException("Nessuna azione con nome " + nomeAzione + " registrato per il servizio " + nomeServizio);

			ApiAzione ret = ApiApiHelper.operazioneToApiAzione(az);

			ApiApiHelper.populateApiAzioneWithProtocolInfo(as, az, env, profilo, ret);

			context.getLogger().info("Invocazione completata con successo");
			return ret;

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/*
     * Restituisce il canale associato all'API
     *
     * Questa operazione consente di ottenere il canale associato all'API identificata dal nome e dalla versione
     *
     */
	@Override
    public ApiCanale getApiCanale(String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);

			ApiCanale canale = null;
			if(env.gestioneCanali) {
				canale = new ApiCanale();
				if(as.getCanale()!=null && !"".equals(as.getCanale())) {
					canale.setNome(as.getCanale());
					canale.setConfigurazione(CanaleEnum.API);
				}
				else {
					canale.setNome(env.canaleDefault);
					canale.setConfigurazione(CanaleEnum.DEFAULT);
				}
			}
			else {
				throw new Exception("Gestione dei canali non abilitata");
			}
			
			context.getLogger().info("Invocazione completata con successo");
			return canale;
     
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }

	/**
	 * Restituisce la descrizione di una API
	 *
	 * Questa operazione consente di ottenere la descrizione di una API identificata
	 * dal nome e dalla versione
	 *
	 */
	@Override
	public ApiDescrizione getApiDescrizione(String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);

			ApiDescrizione ret = new ApiDescrizione();
			ret.setDescrizione(as.getDescrizione());

			context.getLogger().info("Invocazione completata con successo");
			return ret;

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Restituisce le informazioni generali di una API
	 *
	 * Questa operazione consente di ottenere le informazioni generali di una API
	 * identificata dal nome e dalla versione
	 *
	 */
	@Override
	public ApiInformazioniGeneraliView getApiInformazioniGenerali(String nome, Integer versione, ProfiloEnum profilo,
			String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);

			ApiInformazioniGeneraliView ret = new ApiInformazioniGeneraliView();

			ret.setNome(as.getNome());
			ret.setProfilo(env.profilo);
			ret.setVersione(as.getVersione());

			context.getLogger().info("Invocazione completata con successo");
			return ret;

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Restituisce l'interfaccia di una API
	 *
	 * Questa operazione consente di ottenere l'interfaccia di una API identificata
	 * dal nome e dalla versione
	 *
	 */
	@Override
	public ApiInterfacciaView getApiInterfaccia(String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);

			ApiInterfacciaView ret = new ApiInterfacciaView();

			ret.setProfilo(env.profilo);
			
			TipoApiEnum protocollo = as.getServiceBinding() == ServiceBinding.REST ? TipoApiEnum.REST : TipoApiEnum.SOAP;
			
			switch (protocollo) {
			case REST:
				ApiInterfacciaRest iRest = new ApiInterfacciaRest();
				iRest.setProtocollo(protocollo);
				iRest.setFormato(Enums.formatoRestFromSpecifica.get(as.getFormatoSpecifica()));
				ret.setTipoInterfaccia(iRest);
				ret.setInterfaccia(as.getByteWsdlConcettuale());
				break;
			case SOAP:
				ApiInterfacciaSoap iSoap = new ApiInterfacciaSoap();
				iSoap.setProtocollo(protocollo);
				iSoap.setFormato(Enums.formatoSoapFromSpecifica.get(as.getFormatoSpecifica()));
				ret.setTipoInterfaccia(iSoap);
				ret.setInterfaccia(as.getByteWsdlLogicoErogatore());
				break;
			default:
				throw FaultCode.ERRORE_INTERNO
						.toException("Tipologia interfaccia registro sconosciuta: " + as.getServiceBinding().name());
			}

			context.getLogger().info("Invocazione completata con successo");
			return ret;

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	* Restituisce le informazioni ModI associate all'API
	*
	* Questa operazione consente di ottenere le informazioni ModI associato all'API identificata dal nome e dalla versione
	*
	*/
	@Override
	public ApiModI getApiModI(String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
		        
			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);

			ApiModI ret = ModiApiApiHelper.getApiModI(as, profilo, env);
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;

		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}
    

	/**
	 * Restituisce il nome del soggetto referente dell'api
	 *
	 * Questa operazione consente di ottenere il nome del soggetto referente
	 * dell'API identificata dal nome e dalla versione
	 *
	 */
	@Override
	public ApiReferenteView getApiReferente(String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);

			ApiReferenteView ret = new ApiReferenteView();
			ret.setProfilo(env.profilo);
			ret.setReferente(as.getSoggettoReferente().getNome());

			context.getLogger().info("Invocazione completata con successo");
			return ret;
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Restituisce il dettaglio di una risorsa di una API
	 *
	 * Questa operazione consente di ottenere il dettaglio di una risorsa della API
	 * identificata dal nome e dalla versione
	 *
	 */
	@Override
	public ApiRisorsa getApiRisorsa(String nome, Integer versione, String nomeRisorsa, ProfiloEnum profilo,
			String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);

			Resource res = as.getResourceList().stream().filter(r -> nomeRisorsa.equals(r.getNome()))
					.findFirst().orElse(null);

			if (res == null)
				throw FaultCode.NOT_FOUND
						.toException("Nessuna risorsa con nome " + nomeRisorsa + " è registrata per la Api indicata");


			ApiRisorsa ret = ApiApiHelper.risorsaRegistroToApi(res);

			ApiApiHelper.populateApiRisorsaWithProtocolInfo(as, res, env, profilo, ret);

			context.getLogger().info("Invocazione completata con successo");
			return ret;

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Restituisce il dettaglio di un servizio di una API
	 *
	 * Questa operazione consente di ottenere il dettaglio di un servizio della API
	 * identificata dal nome e dalla versione
	 *
	 */
	@Override
	public ApiServizio getApiServizio(String nome, Integer versione, String nomeServizio, ProfiloEnum profilo,
			String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);

			ApiServizio ret = as.getPortTypeList().stream().filter(p -> nomeServizio.equals(p.getNome()))
					.map(p -> ApiApiHelper.servizioRegistroToApi(p)).findFirst().orElse(null);

			if (ret == null)
				throw FaultCode.NOT_FOUND.toException("Nessun Servizio con nome: " + nomeServizio);

			context.getLogger().info("Invocazione completata con successo");
			return ret;

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

    /**
     * Restituisce i tags associati all'API
     *
     * Questa operazione consente di ottenere i tags associati all'API identificata dal nome e dalla versione
     *
     */
	@Override
	public ApiTags getApiTags(String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			ApiEnv env = new ApiEnv(profilo, soggetto, context);
			AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);

			ApiTags ret = new ApiTags();
			if(as.getGruppi()!=null && as.getGruppi().getGruppoList()!=null && !as.getGruppi().getGruppoList().isEmpty()) {
				for (GruppoAccordo tag : as.getGruppi().getGruppoList()) {
					ret.addTagsItem(tag.getNome());
				}
			}
			
			context.getLogger().info("Invocazione completata con successo");
			return ret;

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	
	/**
	 * Modifica i dati di un allegato di una API
	 *
	 * Questa operazione consente di aggiornare i dettagli di un allegato della API
	 * identificata dal nome e dalla versione
	 *
	 */
	@Override
	public void updateApiAllegato(ApiAllegato body, String nome, Integer versione, String nomeAllegato,
			ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			if (body == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");

			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);

			final Documento oldDoc = BaseHelper.supplyOrNotFound(() -> env.archiviCore.getDocumento(nomeAllegato, null, null,
					as.getId(), false, ProprietariDocumento.accordoServizio),
					"Allegato con nome " + nomeAllegato + " per la API scelta.");

			final Documento newDoc = ApiApiHelper.apiAllegatoToDocumento(body, as, env);
			newDoc.setId(oldDoc.getId());

			if (!newDoc.getRuolo().equals(oldDoc.getRuolo()))
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non puoi modificare il ruolo di un allegato");

			SerialiableFormFile filewrap = new SerialiableFormFile(newDoc.getFile(), newDoc.getByteContenuto());

			env.requestWrapper.overrideParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_RUOLO,
					newDoc.getRuolo());

			ArchiviHelper archiviHelper = new ArchiviHelper(env.stationCore, env.requestWrapper, env.pd,
					env.requestWrapper.getSession());

			if (!archiviHelper.accordiAllegatiCheckData(TipoOperazione.CHANGE, filewrap, newDoc,
					ProprietariDocumento.accordoServizio, env.protocolFactory)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}

			AccordiServizioParteComuneUtilities.updateAccordoServizioParteComuneAllegati(as, oldDoc, newDoc);

			env.apcCore.performUpdateOperation(env.userLogin, false, as);

			context.getLogger().info("Invocazione completata con successo");
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Modifica i dati di un'azione nel servizio di una API
	 *
	 * Questa operazione consente di aggiornare i dettagli di un'azione della API
	 * identificata dal nome e dalla versione
	 *
	 */
	@Override
	public void updateApiAzione(ApiAzione body, String nome, Integer versione, String nomeServizio, String nomeAzione,
			ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			if (body == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");

			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);

			final PortType pt = as.getPortTypeList().stream().filter(p -> nomeServizio.equals(p.getNome())).findFirst()
					.orElse(null);

			if (pt == null)
				throw FaultCode.NOT_FOUND.toException("Nessun Servizio con nome: " + nomeServizio);

			final Operation oldOp = BaseHelper.findAndRemoveFirst(pt.getAzioneList(),
					(op -> nomeAzione.equals(op.getNome())));

			if (oldOp == null)
				throw FaultCode.NOT_FOUND
						.toException("Nessuna Azione con nome: " + nomeAzione + " associata al servizio " + nomeServizio);

			if (!nomeAzione.equals(oldOp.getNome())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non puoi modificare il nome dell'azione");
			}

			ApiApiHelper.updateOperation(body, pt, oldOp);
			final Operation newOp = oldOp;
			
			ProfiloCollaborazioneEnum profiloBody = body.getProfiloCollaborazione()!=null ? body.getProfiloCollaborazione() : ProfiloCollaborazioneEnum.SINCRONO;
			
			ProtocolProperties protocolProperties = null;
			if(profilo != null) {
				protocolProperties = ApiApiHelper.getProtocolProperties(body, profilo, as, newOp, env);
	
				if(protocolProperties != null) {
					newOp.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(protocolProperties, ConsoleOperationType.CHANGE, null));
				}
			}

			if (! env.apcHelper.accordiPorttypeOperationCheckData(
					TipoOperazione.CHANGE,
					as.getId().toString(),
					nomeServizio,
					newOp.getNome(),
					newOp.getProfAzione(),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newOp.getFiltroDuplicati()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newOp.getConfermaRicezione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newOp.getIdCollaborazione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newOp.getIdRiferimentoRichiesta()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newOp.getConsegnaInOrdine()),
					newOp.getScadenza() != null ? newOp.getScadenza() : "", // Scadenza operazione
					"-", // Servizio Correlato
					"-", // Azione Correlata
					Enums.profiloCollaborazioneFromApiEnum.get(profiloBody).toString(), "0", // styleOp
					"", // soapActionOp,
					"literal", // useOp,
					null, // opTypeOp,
					"" // nsWSDLOp
			)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}

			IDPortType idPT = new IDPortType();
			idPT.setNome(pt.getNome());
			idPT.setIdAccordo(IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as));
			IDPortTypeAzione idAccordoAzione = new IDPortTypeAzione();
			idAccordoAzione.setNome(oldOp.getNome());
			idAccordoAzione.setIdPortType(idPT);
			
			ApiApiHelper.validateProperties(env, protocolProperties, idAccordoAzione, ConsoleOperationType.CHANGE);
			
			// Dopo aver fatto i controlli sui dati aggiungo la nuova azione
			pt.addAzione(newOp);

			AccordiServizioParteComuneUtilities.createPortTypeOperation(env.apcCore.isEnableAutoMappingWsdlIntoAccordo(),
					env.apcCore, env.apcHelper, as, pt, env.userLogin);

			context.getLogger().info("Invocazione completata con successo");

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

    /**
     * Consente di modificare il canale associato all'API
     *
     * Questa operazione consente di aggiornare il canale associato all'API identificata dal nome e dalla versione
     *
     */
	@Override
    public void updateApiCanale(ConfigurazioneApiCanale body, String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			if (body == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");
			
			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);

			
			final IDAccordo oldIdAccordo = env.idAccordoFactory.getIDAccordoFromAccordo(as);

			if(!env.gestioneCanali) {
				throw new Exception("Gestione dei canali non abilitata");
			}
			
			if(ConfigurazioneCanaleEnum.RIDEFINITO.equals(body.getConfigurazione())){
				if(body.getCanale()==null || "".equals(body.getCanale())) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un canale");
				}
				if(!env.canali.contains(body.getCanale())) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il canale fornito '" + body.getCanale() + "' non è presente nel registro");
				}
				as.setCanale(body.getCanale());
			}
			else {
				as.setCanale(null);
			}

			boolean validazioneDocumenti = ServerProperties.getInstance().isValidazioneDocumenti();

			BinaryParameter wsdlserv = new BinaryParameter();
			wsdlserv.setValue(as.getByteWsdlLogicoErogatore());

			BinaryParameter wsdlconc = new BinaryParameter();
			wsdlconc.setValue(as.getByteWsdlConcettuale());

			BinaryParameter wsdldef = new BinaryParameter();
			wsdldef.setValue(as.getByteWsdlDefinitorio());

			/*
			 * boolean visibilitaAccordoCooperazione=false;
			 * if("-".equals(this.accordoCooperazioneId)==false &&
			 * "".equals(this.accordoCooperazioneId)==false &&
			 * this.accordoCooperazioneId!=null){ AccordoCooperazione ac =
			 * acCore.getAccordoCooperazione(Long.parseLong(this.accordoCooperazioneId));
			 * visibilitaAccordoCooperazione=ac.getPrivato()!=null && ac.getPrivato(); }
			 */

			StringBuilder bfTags = new StringBuilder();
			if(as.getGruppi()!=null && as.getGruppi().getGruppoList()!=null && !as.getGruppi().getGruppoList().isEmpty()) {
				for (GruppoAccordo tag : as.getGruppi().getGruppoList()) {
					if(bfTags.length()>0) {
						bfTags.append(",");
					}
					bfTags.append(tag.getNome());
				}
			}
			
			boolean gestioneCanaliEnabled = env.gestioneCanali;
			String canale = as.getCanale();
			String canaleStato = null;
			if(canale == null) {
				canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_DEFAULT;
			} else {
				canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_RIDEFINITO;
			}
			
			if (!env.apcHelper.accordiCheckData(TipoOperazione.CHANGE, as.getNome(), as.getDescrizione(),
					as.getProfiloCollaborazione().toString(), Helper.toBinaryParameter(as.getByteWsdlDefinitorio()),
					Helper.toBinaryParameter(as.getByteWsdlConcettuale()),
					Helper.toBinaryParameter(as.getByteWsdlLogicoErogatore()),
					Helper.toBinaryParameter(as.getByteWsdlLogicoFruitore()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getFiltroDuplicati()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConfermaRicezione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdCollaborazione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdRiferimentoRichiesta()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConsegnaInOrdine()),
					as.getScadenza() == null ? "" : as.getScadenza(), as.getId().toString(), as.getSoggettoReferente().getNome(),
					as.getVersione().toString(), null, // AccordoCooperazioneID
					false, // privato false, null,
					false, // visibilitaAccordoCooperazione
					oldIdAccordo, // idAccordoOld
					null, // wsblconc
					null, // wsblserv
					null, // wsblservrorr
					validazioneDocumenti, env.tipo_protocollo, null, // backToStato
					env.apcCore.toMessageServiceBinding(as.getServiceBinding()), null, // MessageType
					Enums.interfaceTypeFromFormatoSpecifica.get(as.getFormatoSpecifica()), env.gestisciSoggettoReferente,
					bfTags.toString(),
					canaleStato, canale, gestioneCanaliEnabled)) {

				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}

			// Il profilo di collaborazione di base è Sincrono.
			// Quindi Nella AccordiServizioPArteComuneChange così come nella Add possiamo
			// assumerlo costante.
			// Sono i Servizi e le Azioni ad avere un profilo di collaborazione

			as.setOldIDAccordoForUpdate(oldIdAccordo);
			List<Object> operazioniList = new ArrayList<>(Arrays.asList(as));

			// Questa roba non serve qui perchè la updateDescrizione non cambia il nome e
			// quindi nemmeno l'ID.
			IDAccordo idNEW = env.idAccordoFactory.getIDAccordoFromAccordo(as);
			if (idNEW.equals(oldIdAccordo) == false) {
				AccordiServizioParteComuneUtilities.findOggettiDaAggiornare(oldIdAccordo, as, env.apcCore, operazioniList);
			}

			env.apcCore.performUpdateOperation(env.userLogin, false, operazioniList.toArray());

			context.getLogger().info("Invocazione completata con successo");
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }

	/**
	 * Consente di modificare la descrizione di una API
	 *
	 * Questa operazione consente di aggiornare la descrizione di una API
	 * identificata dal nome e dalla versione
	 *
	 */
	@Override
	public void updateApiDescrizione(ApiDescrizione body, String nome, Integer versione, ProfiloEnum profilo,
			String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			if (body == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");

			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);

			final IDAccordo oldIdAccordo = env.idAccordoFactory.getIDAccordoFromAccordo(as);
			
			as.setDescrizione(body.getDescrizione());

			boolean validazioneDocumenti = ServerProperties.getInstance().isValidazioneDocumenti();

			BinaryParameter wsdlserv = new BinaryParameter();
			wsdlserv.setValue(as.getByteWsdlLogicoErogatore());

			BinaryParameter wsdlconc = new BinaryParameter();
			wsdlconc.setValue(as.getByteWsdlConcettuale());

			BinaryParameter wsdldef = new BinaryParameter();
			wsdldef.setValue(as.getByteWsdlDefinitorio());

			/*
			 * boolean visibilitaAccordoCooperazione=false;
			 * if("-".equals(this.accordoCooperazioneId)==false &&
			 * "".equals(this.accordoCooperazioneId)==false &&
			 * this.accordoCooperazioneId!=null){ AccordoCooperazione ac =
			 * acCore.getAccordoCooperazione(Long.parseLong(this.accordoCooperazioneId));
			 * visibilitaAccordoCooperazione=ac.getPrivato()!=null && ac.getPrivato(); }
			 */

			StringBuilder bfTags = new StringBuilder();
			if(as.getGruppi()!=null && as.getGruppi().getGruppoList()!=null && !as.getGruppi().getGruppoList().isEmpty()) {
				for (GruppoAccordo tag : as.getGruppi().getGruppoList()) {
					if(bfTags.length()>0) {
						bfTags.append(",");
					}
					bfTags.append(tag.getNome());
				}
			}
			
			boolean gestioneCanaliEnabled = env.gestioneCanali;
			String canale = as.getCanale();
			String canaleStato = null;
			if(canale == null) {
				canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_DEFAULT;
			} else {
				canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_RIDEFINITO;
			}
			
			if (!env.apcHelper.accordiCheckData(TipoOperazione.CHANGE, as.getNome(), as.getDescrizione(),
					as.getProfiloCollaborazione().toString(), Helper.toBinaryParameter(as.getByteWsdlDefinitorio()),
					Helper.toBinaryParameter(as.getByteWsdlConcettuale()),
					Helper.toBinaryParameter(as.getByteWsdlLogicoErogatore()),
					Helper.toBinaryParameter(as.getByteWsdlLogicoFruitore()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getFiltroDuplicati()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConfermaRicezione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdCollaborazione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdRiferimentoRichiesta()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConsegnaInOrdine()),
					as.getScadenza() == null ? "" : as.getScadenza(), as.getId().toString(), as.getSoggettoReferente().getNome(),
					as.getVersione().toString(), null, // AccordoCooperazioneID
					false, // privato false, null,
					false, // visibilitaAccordoCooperazione
					oldIdAccordo, // idAccordoOld
					null, // wsblconc
					null, // wsblserv
					null, // wsblservrorr
					validazioneDocumenti, env.tipo_protocollo, null, // backToStato
					env.apcCore.toMessageServiceBinding(as.getServiceBinding()), null, // MessageType
					Enums.interfaceTypeFromFormatoSpecifica.get(as.getFormatoSpecifica()), env.gestisciSoggettoReferente,
					bfTags.toString(),
					canaleStato, canale, gestioneCanaliEnabled)) {

				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}

			// Il profilo di collaborazione di base è Sincrono.
			// Quindi Nella AccordiServizioPArteComuneChange così come nella Add possiamo
			// assumerlo costante.
			// Sono i Servizi e le Azioni ad avere un profilo di collaborazione

			as.setOldIDAccordoForUpdate(oldIdAccordo);
			List<Object> operazioniList = new ArrayList<>(Arrays.asList(as));

			// Questa roba non serve qui perchè la updateDescrizione non cambia il nome e
			// quindi nemmeno l'ID.
			IDAccordo idNEW = env.idAccordoFactory.getIDAccordoFromAccordo(as);
			if (idNEW.equals(oldIdAccordo) == false) {
				AccordiServizioParteComuneUtilities.findOggettiDaAggiornare(oldIdAccordo, as, env.apcCore, operazioniList);
			}

			env.apcCore.performUpdateOperation(env.userLogin, false, operazioniList.toArray());

			context.getLogger().info("Invocazione completata con successo");
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Consente di modificare le informazioni generali di una API
	 *
	 * Questa operazione consente di aggiornare le informazioni generali di una API
	 * identificata dal nome e dalla versione
	 *
	 */
	@Override
	public void updateApiInformazioniGenerali(ApiInformazioniGenerali body, String nome, Integer versione,
			ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			BaseHelper.throwIfNull(body);

			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);

			final IDAccordo oldIdAccordo = env.idAccordoFactory.getIDAccordoFromAccordo(as);

			as.setNome(body.getNome());
			as.setVersione(body.getVersione());

			boolean validazioneDocumenti = ServerProperties.getInstance().isValidazioneDocumenti();

			/*
			 * boolean visibilitaAccordoCooperazione=false;
			 * if("-".equals(this.accordoCooperazioneId)==false &&
			 * "".equals(this.accordoCooperazioneId)==false &&
			 * this.accordoCooperazioneId!=null){ AccordoCooperazione ac =
			 * acCore.getAccordoCooperazione(Long.parseLong(this.accordoCooperazioneId));
			 * visibilitaAccordoCooperazione=ac.getPrivato()!=null && ac.getPrivato(); }
			 */

			StringBuilder bfTags = new StringBuilder();
			if(as.getGruppi()!=null && as.getGruppi().getGruppoList()!=null && !as.getGruppi().getGruppoList().isEmpty()) {
				for (GruppoAccordo tag : as.getGruppi().getGruppoList()) {
					if(bfTags.length()>0) {
						bfTags.append(",");
					}
					bfTags.append(tag.getNome());
				}
			}
			
			boolean gestioneCanaliEnabled = env.gestioneCanali;
			String canale = as.getCanale();
			String canaleStato = null;
			if(canale == null) {
				canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_DEFAULT;
			} else {
				canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_RIDEFINITO;
			}
			
			if (!env.apcHelper.accordiCheckData(TipoOperazione.CHANGE, as.getNome(), as.getDescrizione(),
					as.getProfiloCollaborazione().toString(), Helper.toBinaryParameter(as.getByteWsdlDefinitorio()),
					Helper.toBinaryParameter(as.getByteWsdlConcettuale()),
					Helper.toBinaryParameter(as.getByteWsdlLogicoErogatore()),
					Helper.toBinaryParameter(as.getByteWsdlLogicoFruitore()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getFiltroDuplicati()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConfermaRicezione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdCollaborazione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdRiferimentoRichiesta()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConsegnaInOrdine()),
					as.getScadenza() == null ? "" : as.getScadenza(), as.getId().toString(), as.getSoggettoReferente().getNome(),
					as.getVersione().toString(), null, // AccordoCooperazioneID
					false, // privato false, null,
					false, // visibilitaAccordoCooperazione
					oldIdAccordo, // idAccordoOld
					null, // wsblconc
					null, // wsblserv
					null, // wsblservrorr
					validazioneDocumenti, env.tipo_protocollo, null, // backToStato
					env.apcCore.toMessageServiceBinding(as.getServiceBinding()), null, // MessageType
					Enums.interfaceTypeFromFormatoSpecifica.get(as.getFormatoSpecifica()), env.gestisciSoggettoReferente,
					bfTags.toString(),
					canaleStato, canale, gestioneCanaliEnabled)) {

				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}

			as.setOldIDAccordoForUpdate(oldIdAccordo);
			List<Object> operazioniList = new ArrayList<>(Arrays.asList(as));

			IDAccordo idNEW = env.idAccordoFactory.getIDAccordoFromAccordo(as);
			if (idNEW.equals(oldIdAccordo) == false) {
				AccordiServizioParteComuneUtilities.findOggettiDaAggiornare(oldIdAccordo, as, env.apcCore, operazioniList);
			}

			env.apcCore.performUpdateOperation(env.userLogin, false, operazioniList.toArray());
			context.getLogger().info("Invocazione completata con successo");
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Consente di modificare l'interfaccia di una API
	 *
	 * Questa operazione consente di aggiornare l'interfaccia di una API
	 * identificata dal nome e dalla versione
	 *
	 */
	@Override
	public void updateApiInterfaccia(ApiInterfaccia body, String nome, Integer versione, ProfiloEnum profilo,
			String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);

			final String tipoWsdl = as.getServiceBinding() == ServiceBinding.REST
					? AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_CONCETTUALE
					: AccordiServizioParteComuneCostanti.PARAMETRO_APC_WSDL_EROGATORE;

			final String wsdl = new String(body.getInterfaccia());

			ServerProperties serverProperties = ServerProperties.getInstance();
			final boolean validazioneDocumenti = serverProperties.isValidazioneDocumenti();

			if (!env.apcHelper.accordiWSDLCheckData(env.pd, tipoWsdl, wsdl, as, validazioneDocumenti, env.tipo_protocollo)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}

			final boolean facilityUnicoWSDL_interfacciaStandard = as.getServiceBinding() == ServiceBinding.SOAP;

			ServerProperties properties = ServerProperties.getInstance();

			boolean aggiornaEsistenti = serverProperties.isUpdateInterfacciaApi_updateIfExists();
			boolean eliminaNonPresentiNuovaInterfaccia = serverProperties.isUpdateInterfacciaApi_deleteIfNotFound();
			List<IDResource> risorseEliminate = new ArrayList<IDResource>();
			List<IDPortType> portTypeEliminati = new ArrayList<IDPortType>();
			List<IDPortTypeAzione> operationEliminate = new ArrayList<IDPortTypeAzione>();
			
			AccordiServizioParteComuneUtilities.updateInterfacciaAccordoServizioParteComune(tipoWsdl, wsdl, as,
					properties.isEnabledAutoMapping(), properties.isValidazioneDocumenti(),
					properties.isEnabledAutoMappingEstraiXsdSchemiFromWsdlTypes(), facilityUnicoWSDL_interfacciaStandard,
					env.tipo_protocollo, env.apcCore,
					aggiornaEsistenti, eliminaNonPresentiNuovaInterfaccia,
					risorseEliminate, portTypeEliminati, operationEliminate);

			// effettuo le operazioni
			env.apcCore.performUpdateOperation(env.userLogin, false, as);

			context.getLogger().info("Invocazione completata con successo");
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Consente di modificare le informazioni ModI associate all'API
	 *
	 * Questa operazione consente di aggiornare le informazioni ModI associate all'API identificata dal nome e dalla versione
	 *
	*/
	@Override
	public void updateApiModI(ApiModI body, String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
		        
			if (body == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");

			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);

			ProtocolProperties updateModiProtocolProperties = ModiApiApiHelper.updateModiProtocolProperties(as, profilo, body);
			
			IDAccordo idAccordoFromAccordo = env.idAccordoFactory.getIDAccordoFromAccordo(as);
			ApiApiHelper.validateProperties(env, updateModiProtocolProperties, idAccordoFromAccordo, ConsoleOperationType.CHANGE);
			
			if(updateModiProtocolProperties != null) {
				as.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(updateModiProtocolProperties, ConsoleOperationType.ADD, null));
			}

			env.apcCore.performUpdateOperation(env.userLogin, false, as);

			context.getLogger().info("Invocazione completata con successo");
		}
		catch(javax.ws.rs.WebApplicationException e) {
			context.getLogger().error("Invocazione terminata con errore '4xx': %s",e, e.getMessage());
			throw e;
		}
		catch(Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s",e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Modifica i dati di una risorsa di una API
	 *
	 * Questa operazione consente di aggiornare i dettagli di una risorsa della API
	 * identificata dal nome e dalla versione
	 *
	 */
	@Override
	public void updateApiRisorsa(ApiRisorsa body, String nome, Integer versione, String nomeRisorsa, ProfiloEnum profilo,
			String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			if (body == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");

			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);

			Resource oldResource = BaseHelper.findAndRemoveFirst(as.getResourceList(), r -> nomeRisorsa.equals(r.getNome()));

			if (oldResource == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna risorsa trovata con nome " + nomeRisorsa);

			final String oldPath = oldResource.getPath();
			final String oldHttpMethod = oldResource.getMethodRawEnumValue();
			final String oldNomeRisorsaGenerato = oldHttpMethod != null
					? APIUtils.normalizeResourceName(HttpMethod.toEnumConstant(oldHttpMethod), oldPath)
					: null;

			ApiApiHelper.updateRisorsa(body, oldResource);
			final Resource newRes = oldResource;

			ProtocolProperties protocolProperties = null;
			if(profilo != null) {
				protocolProperties = ApiApiHelper.getProtocolProperties(body, profilo, newRes, env);
	
				if(protocolProperties != null) {
					newRes.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(protocolProperties, ConsoleOperationType.CHANGE, null));
				}
			}

			if (!env.apcHelper.accordiResourceCheckData(TipoOperazione.CHANGE, as.getId().toString(),
					body.getNome() != null ? body.getNome() : "", newRes.getNome(), newRes.getPath(), newRes.getMethodRawEnumValue(),
					newRes.getDescrizione(), env.apcCore.toMessageMessageType(newRes.getMessageType()), nomeRisorsa,
					oldNomeRisorsaGenerato, oldPath, oldHttpMethod,
					AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO,
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newRes.getFiltroDuplicati()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newRes.getConfermaRicezione()), // confricaz,
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newRes.getIdCollaborazione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newRes.getIdRiferimentoRichiesta()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newRes.getConsegnaInOrdine()), // consordaz,
					newRes.getScadenza() != null ? newRes.getScadenza() : "")) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			IDResource idResource = new IDResource();
			idResource.setNome(oldResource.getNome());
			idResource.setIdAccordo(IDAccordoFactory.getInstance().getIDAccordoFromAccordo(as));
			
			ApiApiHelper.validateProperties(env, protocolProperties, idResource, newRes.getMethod()!=null ? newRes.getMethod().toString() : null, newRes.getPath(), ConsoleOperationType.ADD);
			

			as.addResource(newRes);

			AccordiServizioParteComuneUtilities.createResource(env.apcCore.isEnableAutoMappingWsdlIntoAccordo(), env.apcCore,
					env.apcHelper, as, env.userLogin);

			context.getLogger().info("Invocazione completata con successo");
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Modifica i dati di un servizio di una API
	 *
	 * Questa operazione consente di aggiornare i dettagli di un servizio della API
	 * identificata dal nome e dalla versione
	 *
	 */
	@Override
	public void updateApiServizio(ApiServizio body, String nome, Integer versione, String nomeServizio,
			ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);

			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);

			final PortType oldPt = BaseHelper.findAndRemoveFirst(as.getPortTypeList(),
					(p -> nomeServizio.equals(p.getNome())));

			if (oldPt == null)
				throw FaultCode.NOT_FOUND.toException("Nessun Servizio registrato con nome: " + nomeServizio);

			ApiApiHelper.updatePortType(body, oldPt, env);
			final PortType newPt = oldPt;

			if (!env.apcHelper.accordiPorttypeCheckData(TipoOperazione.CHANGE, as.getId().toString(), newPt.getNome(),
					newPt.getDescrizione(), AccordiServizioParteComuneCostanti.INFORMAZIONI_PROTOCOLLO_MODALITA_RIDEFINITO,
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newPt.getFiltroDuplicati()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newPt.getConfermaRicezione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newPt.getIdCollaborazione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newPt.getIdRiferimentoRichiesta()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(newPt.getConsegnaInOrdine()),
					newPt.getScadenza() != null ? newPt.getScadenza() : "")) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}

			as.addPortType(newPt);

			env.apcCore.performUpdateOperation(env.userLogin, false, as);

			context.getLogger().info("Invocazione completata con successo");
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}
	
    /**
     * Consente di modificare i tags associati all'API
     *
     * Questa operazione consente di aggiornare i tags associati all'API identificata dal nome e dalla versione
     *
     */
	@Override
	public void updateApiTags(ApiTags body, String nome, Integer versione, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			if (body == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");

			final ApiEnv env = new ApiEnv(profilo, soggetto, context);
			final AccordoServizioParteComune as = ApiApiHelper.getAccordoFull(nome, versione, env);
			
			if (as == null)
				throw FaultCode.NOT_FOUND.toException("Nessuna Api registrata con nome " + nome + " e versione " + versione);
			
			final IDAccordo oldIdAccordo = env.idAccordoFactory.getIDAccordoFromAccordo(as);
			
			List<Object> objectToCreate = new ArrayList<>();
			
			if(body.getTags()==null || body.getTags().isEmpty()) {
				as.setGruppi(null);
			}
			else {
				as.setGruppi(new GruppiAccordo()); // annullo eventuali tags presenti.
				
				GruppiCore gruppiCore = new GruppiCore(env.stationCore);
				
				for (String tag : body.getTags()) {
					GruppoAccordo gruppo = new GruppoAccordo();
					gruppo.setNome(tag);
					as.getGruppi().addGruppo(gruppo);
					
					if(!gruppiCore.existsGruppo(tag)) {
						Gruppo nuovoGruppo = new Gruppo();
						nuovoGruppo.setNome(tag);
						nuovoGruppo.setSuperUser(as.getSuperUser());
						objectToCreate.add(nuovoGruppo);
					}
				}
			}
			
			boolean validazioneDocumenti = ServerProperties.getInstance().isValidazioneDocumenti();

			BinaryParameter wsdlserv = new BinaryParameter();
			wsdlserv.setValue(as.getByteWsdlLogicoErogatore());

			BinaryParameter wsdlconc = new BinaryParameter();
			wsdlconc.setValue(as.getByteWsdlConcettuale());

			BinaryParameter wsdldef = new BinaryParameter();
			wsdldef.setValue(as.getByteWsdlDefinitorio());

			/*
			 * boolean visibilitaAccordoCooperazione=false;
			 * if("-".equals(this.accordoCooperazioneId)==false &&
			 * "".equals(this.accordoCooperazioneId)==false &&
			 * this.accordoCooperazioneId!=null){ AccordoCooperazione ac =
			 * acCore.getAccordoCooperazione(Long.parseLong(this.accordoCooperazioneId));
			 * visibilitaAccordoCooperazione=ac.getPrivato()!=null && ac.getPrivato(); }
			 */

			StringBuilder bfTags = new StringBuilder();
			if(as.getGruppi()!=null && as.getGruppi().getGruppoList()!=null && !as.getGruppi().getGruppoList().isEmpty()) {
				for (GruppoAccordo tag : as.getGruppi().getGruppoList()) {
					if(bfTags.length()>0) {
						bfTags.append(",");
					}
					bfTags.append(tag.getNome());
				}
			}
			
			boolean gestioneCanaliEnabled = env.gestioneCanali;
			String canale = as.getCanale();
			String canaleStato = null;
			if(canale == null) {
				canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_DEFAULT;
			} else {
				canaleStato = AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_CANALE_STATO_RIDEFINITO;
			}
			
			if (!env.apcHelper.accordiCheckData(TipoOperazione.CHANGE, as.getNome(), as.getDescrizione(),
					as.getProfiloCollaborazione().toString(), Helper.toBinaryParameter(as.getByteWsdlDefinitorio()),
					Helper.toBinaryParameter(as.getByteWsdlConcettuale()),
					Helper.toBinaryParameter(as.getByteWsdlLogicoErogatore()),
					Helper.toBinaryParameter(as.getByteWsdlLogicoFruitore()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getFiltroDuplicati()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConfermaRicezione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdCollaborazione()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getIdRiferimentoRichiesta()),
					AccordiServizioParteComuneHelper.convertAbilitatoDisabilitatoDB2View(as.getConsegnaInOrdine()),
					as.getScadenza() == null ? "" : as.getScadenza(), as.getId().toString(), as.getSoggettoReferente().getNome(),
					as.getVersione().toString(), null, // AccordoCooperazioneID
					false, // privato false, null,
					false, // visibilitaAccordoCooperazione
					oldIdAccordo, // idAccordoOld
					null, // wsblconc
					null, // wsblserv
					null, // wsblservrorr
					validazioneDocumenti, env.tipo_protocollo, null, // backToStato
					env.apcCore.toMessageServiceBinding(as.getServiceBinding()), null, // MessageType
					Enums.interfaceTypeFromFormatoSpecifica.get(as.getFormatoSpecifica()), env.gestisciSoggettoReferente,
					bfTags.toString(),
					canaleStato, canale, gestioneCanaliEnabled)) {

				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}

			// Il profilo di collaborazione di base è Sincrono.
			// Quindi Nella AccordiServizioPArteComuneChange così come nella Add possiamo
			// assumerlo costante.
			// Sono i Servizi e le Azioni ad avere un profilo di collaborazione

			as.setOldIDAccordoForUpdate(oldIdAccordo);
			List<Object> operazioniList = new ArrayList<>(Arrays.asList(as));

			// Questa roba non serve qui perchè la updateDescrizione non cambia il nome e
			// quindi nemmeno l'ID.
			IDAccordo idNEW = env.idAccordoFactory.getIDAccordoFromAccordo(as);
			if (idNEW.equals(oldIdAccordo) == false) {
				AccordiServizioParteComuneUtilities.findOggettiDaAggiornare(oldIdAccordo, as, env.apcCore, operazioniList);
			}

			
			// effettuo le operazioni
			if(objectToCreate.size()>0) {
				env.apcCore.performCreateOperation(env.userLogin, false, objectToCreate.toArray(new Object[objectToCreate.size()]));
			}
			
			env.apcCore.performUpdateOperation(env.userLogin, false, operazioniList.toArray());

			context.getLogger().info("Invocazione completata con successo");
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
    }


}
