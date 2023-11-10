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

package org.openspcoop2.core.config.rs.server.api.impl.fruizioni;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.rs.server.api.FruizioniApi;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.api.impl.IdServizio;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ConnettoreAPIHelper;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniApiHelper;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ErogazioniEnv;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.ModiErogazioniApiHelper;
import org.openspcoop2.core.config.rs.server.config.ServerProperties;
import org.openspcoop2.core.config.rs.server.model.ApiImplAllegato;
import org.openspcoop2.core.config.rs.server.model.ApiImplInformazioniGenerali;
import org.openspcoop2.core.config.rs.server.model.ApiImplInformazioniGeneraliView;
import org.openspcoop2.core.config.rs.server.model.ApiImplUrlInvocazione;
import org.openspcoop2.core.config.rs.server.model.ApiImplUrlInvocazioneView;
import org.openspcoop2.core.config.rs.server.model.ApiImplVersioneApi;
import org.openspcoop2.core.config.rs.server.model.ApiImplVersioneApiView;
import org.openspcoop2.core.config.rs.server.model.ConnettoreApplicativoServer;
import org.openspcoop2.core.config.rs.server.model.ConnettoreFruizione;
import org.openspcoop2.core.config.rs.server.model.ConnettoreMessageBox;
import org.openspcoop2.core.config.rs.server.model.Fruizione;
import org.openspcoop2.core.config.rs.server.model.FruizioneModI;
import org.openspcoop2.core.config.rs.server.model.FruizioneViewItem;
import org.openspcoop2.core.config.rs.server.model.ListaApiImplAllegati;
import org.openspcoop2.core.config.rs.server.model.ListaFruizioni;
import org.openspcoop2.core.config.rs.server.model.ModalitaIdentificazioneAzioneEnum;
import org.openspcoop2.core.config.rs.server.model.TipoApiEnum;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.beans.OperationSintetica;
import org.openspcoop2.core.registry.beans.PortTypeSintetico;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.protocol.information_missing.constants.StatoType;
import org.openspcoop2.protocol.modipa.constants.ModICostanti;
import org.openspcoop2.protocol.sdk.constants.ConsoleInterfaceType;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.utils.service.BaseImpl;
import org.openspcoop2.utils.service.authorization.AuthorizationConfig;
import org.openspcoop2.utils.service.authorization.AuthorizationManager;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.beans.utils.ListaUtils;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaUtilities;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * FruizioniApiServiceImpl
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class FruizioniApiServiceImpl extends BaseImpl implements FruizioniApi {

	public FruizioniApiServiceImpl() {
		super(org.slf4j.LoggerFactory.getLogger(FruizioniApiServiceImpl.class));
	}

	private AuthorizationConfig getAuthorizationConfig() throws Exception {
		return new AuthorizationConfig(ServerProperties.getInstance().getProperties());
	}

	/**
	 * Creazione di una fruizione di API
	 *
	 * Questa operazione consente di creare una fruizione di API
	 *
	 */
	@Override
	public void createFruizione(Fruizione body, ProfiloEnum profilo, String soggetto) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			BaseHelper.throwIfNull(body);

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);

			final IdSoggetto idero = new IdSoggetto(new IDSoggetto(env.tipo_soggetto, body.getErogatore()));
			final Soggetto erogatore = BaseHelper
					.supplyOrNotFound(() -> env.soggettiCore.getSoggettoRegistro(idero.toIDSoggetto()), "Soggetto Erogatore");
			idero.setId(erogatore.getId());
			final Soggetto fruitore = env.soggettiCore.getSoggettoRegistro(env.idSoggetto.toIDSoggetto());

			IDSoggetto idReferente = ErogazioniApiHelper.getIdReferente(body, env);
			final AccordoServizioParteComuneSintetico as = Helper.getAccordoSintetico(body.getApiNome(),
					body.getApiVersione(), idReferente, env.apcCore);

			String referente = "";
			if( env.apcCore.isSupportatoSoggettoReferente(env.tipo_protocollo) && idReferente!=null ){
				referente = " (referente: "+idReferente+")";
			}
			
			if (as == null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(
						"Nessuna Api registrata con nome " + body.getApiNome() + " e versione " + body.getApiVersione()+referente);
			}
			if(ServiceBinding.SOAP.equals(as.getServiceBinding())) {
				if(StringUtils.isEmpty(body.getApiSoapServizio())) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(
							"Per una API di tipo SOAP il claim 'api_soap_servizio' deve essere obbligatoriamente fornito");
				}
				boolean ptFind = false;
				if(as.getPortType()!=null && !as.getPortType().isEmpty()) {
					for (PortTypeSintetico pt : as.getPortType()) {
						if(pt.getNome().equals(body.getApiSoapServizio())) {
							ptFind = true;
							break;
						}
					}
				}
				if(!ptFind) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(
							"Nel claim 'api_soap_servizio' è stato indicato un servizio non esistente nell'API "+body.getApiNome()+" v"+body.getApiVersione()+referente);
				}
			}

			AccordoServizioParteSpecifica asps = ErogazioniApiHelper.apiImplToAps(body, erogatore, as, env);
			
			final IDServizio idAps = env.idServizioFactory.getIDServizioFromValues(asps.getTipo(), asps.getNome(),
					new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore()), asps.getVersione());
			final boolean alreadyExists = env.apsCore.existsAccordoServizioParteSpecifica(idAps);

			if (alreadyExists)
				asps = env.apsCore.getServizio(idAps);

			
			ServletUtils.setObjectIntoSession(context.getServletRequest(), context.getServletRequest().getSession(),
					AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE,
					AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			ErogazioniApiHelper.serviziCheckData(TipoOperazione.ADD, env, as, asps, Optional.of(env.idSoggetto), body);

			IDFruizione idFruizione = new IDFruizione();
			
			
			idFruizione.setIdServizio(idAps);
			idFruizione.setIdFruitore(new IDSoggetto(env.idSoggetto.getTipo(), env.idSoggetto.getNome()));

			org.openspcoop2.core.registry.Connettore regConnettore = ErogazioniApiHelper.buildConnettoreRegistro(env,
					body.getConnettore());

			Fruitore f = new Fruitore();
			f.setTipo(fruitore.getTipo());
			f.setNome(fruitore.getNome());
			f.setStatoPackage(StatoType.FINALE.getValue());
			f.setConnettore(regConnettore);
			env.apsCore.setDataCreazioneFruitore(f);
			asps.addFruitore(f);

			ProtocolProperties protocolProperties = null;
			if(profilo != null) {
				
				boolean required = false;
				if(env.isProfiloModi()) {
					AccordoServizioParteComune accordoFull = Helper.getAccordoFull(body.getApiNome(),
							body.getApiVersione(), idReferente, env.apcCore);
					if(accordoFull.sizeProtocolPropertyList()>0) {
						for (org.openspcoop2.core.registry.ProtocolProperty pp : accordoFull.getProtocolPropertyList()) {
							if(ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO.equals(pp.getName())) {
								String v = pp.getValue();
								if(v!=null && StringUtils.isNotEmpty(v) && !ModICostanti.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_VALUE_UNDEFINED.equals(v)) {
									required = true;
								}
							}
						}
					}
				}
				
				if(env.isProfiloModi()) {
					ErogazioniApiHelper.addInfoTokenPolicyForModI(regConnettore, env, true);
				}
				
				protocolProperties = ErogazioniApiHelper.getProtocolProperties(body, profilo, asps, env, required);
	
				if(protocolProperties != null) {
					f.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(protocolProperties, ConsoleOperationType.ADD, null));
				}
			}


			ErogazioniApiHelper.validateProperties(env, protocolProperties, idFruizione, ConsoleOperationType.ADD);

			ErogazioniApiHelper.createAps(env, asps, regConnettore, body, alreadyExists, false);

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
	 * Creazione di un allegato nella fruizione di API
	 *
	 * Questa operazione consente di aggiungere un allegato alla fruizione di API
	 * identificata dall'erogatore, dal nome e dalla versione
	 *
	 */
	@Override
	public void createFruizioneAllegato(ApiImplAllegato body, String erogatore, String nome, Integer versione,
			ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			BaseHelper.throwIfNull(body);

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione");
			final IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);

			try {
				env.pdCore.getIDPorteDelegateAssociate(idServizio, env.idSoggetto.toIDSoggetto());
			} catch (Exception e) {
				throw FaultCode.NOT_FOUND.toException(e.getMessage());
			}

			ErogazioniApiHelper.createAllegatoAsps(body, env, asps);

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
	 * Elimina una fruizione di api
	 *
	 * Questa operazione consente di eliminare una fruizione di API identificata
	 * dall'erogatore, dal nome e dalla versione
	 *
	 */
	@Override
	public void deleteFruizione(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto,
			String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.evalnull(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env));

			if (asps != null) {
				final IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);
				final StringBuilder inUsoMessage = new StringBuilder();

				AccordiServizioParteSpecificaUtilities.deleteAccordoServizioParteSpecifica(asps, true, // gestioneFruitori,
						false, // gestioneErogatori,
						env.idSoggetto.toIDSoggetto(), idServizio, env.paCore.getExtendedServletPortaApplicativa(), env.userLogin,
						env.apsCore, env.apsHelper, inUsoMessage, "\n");

				if (inUsoMessage.length() > 0) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(inUsoMessage.toString());
				}
			} else if (env.delete_404) {
				throw FaultCode.NOT_FOUND.toException("Fruizione inesistente");
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
	 * Elimina un allegato dalla fruizione
	 *
	 * Questa operazione consente di eliminare un'allegato dalla fruizione di API
	 * identificata dall'erogatore, dal nome e dalla versione
	 *
	 */
	@Override
	public void deleteFruizioneAllegato(String erogatore, String nome, Integer versione, String nomeAllegato,
			ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione");

			ErogazioniApiHelper.deleteAllegato(nomeAllegato, env, asps);

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
	 * Restituisce l'allegato di una fruizione
	 *
	 * Questa operazione consente di ottenere l'allegato di un'erogazione di API
	 * identificata dall'erogatore, dal nome e dalla versione
	 *
	 */
	@Override
	public byte[] downloadFruizioneAllegato(String erogatore, String nome, Integer versione, String nomeAllegato,
			ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione");

			final Optional<Long> idDoc = ErogazioniApiHelper.getIdDocumento(nomeAllegato, asps);

			if (!idDoc.isPresent())
				throw FaultCode.NOT_FOUND.toException("Allegato di nome " + nomeAllegato + " non presente.");

			final Documento allegato = env.archiviCore.getDocumento(idDoc.get(), true);

			context.getLogger().info("Invocazione completata con successo");

			Helper.setContentType(context, allegato.getFile());
			
			return allegato.getByteContenuto();
		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Ricerca fruizioni di api
	 *
	 * Elenca le fruizioni di API
	 *
	 */
	@Override
	public ListaFruizioni findAllFruizioni(ProfiloEnum profilo, String soggetto, String q, Integer limit, Integer offset,
			TipoApiEnum tipoApi, String tag, String uriApiImplementata, Boolean profiloQualsiasi, Boolean soggettoQualsiasi) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);

			final int idLista = Liste.SERVIZI;
			final ConsoleSearch ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista, env.idSoggetto.toIDSoggetto(),
					env.tipo_protocollo);

			if(profiloQualsiasi!=null && profiloQualsiasi) {
				ricerca.clearFilter(idLista, Filtri.FILTRO_PROTOCOLLO);
			}
			if(soggettoQualsiasi!=null && soggettoQualsiasi) {
				ricerca.clearFilter(idLista, Filtri.FILTRO_SOGGETTO);
			}
			
			if (tipoApi != null)
				ricerca.addFilter(idLista, Filtri.FILTRO_SERVICE_BINDING, tipoApi.toString().toLowerCase());

			if(tag!=null) {
				ricerca.addFilter(idLista, Filtri.FILTRO_GRUPPO, tag);
			}
			
			if(uriApiImplementata!=null) {
				ErogazioniApiHelper.setFiltroApiImplementata(uriApiImplementata, idLista, ricerca, env);
			}
			
			List<AccordoServizioParteSpecifica> lista = env.apsCore.soggettiServizioList(null, ricerca, null, true, false);

			if (env.findall_404 && lista.isEmpty()) {
				throw FaultCode.NOT_FOUND.toException("Nessuna fruizione presente nel registro");
			}

			final ListaFruizioni ret = ListaUtils.costruisciListaPaginata(context.getUriInfo(), 
					ricerca.getIndexIniziale(idLista),
					ricerca.getPageSize(idLista), 
					ricerca.getNumEntries(idLista), ListaFruizioni.class);

			// Prendo le fruizioni una ad una dagli asps
			lista.forEach(asps -> {
				asps.getFruitoreList().forEach(fruitore -> {

					try {
						IdSoggetto idFruitore;
						idFruitore = new IdSoggetto(env.soggettiCore.getIdSoggettoRegistro(fruitore.getIdSoggetto()));
						idFruitore.setId(fruitore.getIdSoggetto());
						ret.addItemsItem(ErogazioniApiHelper
								.fruizioneViewItemToFruizioneItem(ErogazioniApiHelper.aspsToFruizioneViewItem(env, asps, idFruitore)));

					} catch (Exception e) {
						// Cosa fare in questi casi? Errore interno perchè il fruitore deve essere
						// necessariamente presente
						// dato che l'erogazione l'abbiamo appena presa dal db?
						// Però altri utenti avrebbero potuto contemporaneamente modificare il db e
						// l'assenza del fruitore potrebbe essere perchè esso è stato appena eliminato.
						// Questa osservazione vale in generale.
						throw new RuntimeException(e);
					}
				});

			});

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
	 * Elenco allegati di una fruizione di API
	 *
	 * Questa operazione consente di ottenere gli allegati di una fruizione di API
	 * identificata dall'erogatore, dal nome e dalla versione
	 *
	 */
	@Override
	public ListaApiImplAllegati findAllFruizioneAllegati(String erogatore, String nome, Integer versione,
			ProfiloEnum profilo, String soggetto, String tipoServizio, String q, Integer limit, Integer offset) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione");

			ListaApiImplAllegati ret = ErogazioniApiHelper.findAllAllegati(q, limit, offset,
					context.getUriInfo(), env, asps);

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
	 * Restituisce i dettagli di una fruizione di API
	 *
	 * Questa operazione consente di ottenere i dettagli di una fruizione di API
	 * identificata dall'erogatore, dal nome e dalla versione
	 *
	 */
	@Override
	public FruizioneViewItem getFruizione(String erogatore, String nome, Integer versione, ProfiloEnum profilo,
			String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione");

			FruizioneViewItem ret = ErogazioniApiHelper.aspsToFruizioneViewItem(env, asps, env.idSoggetto);

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
	 * Restituisce le informazioni sull'API implementata dalla fruizione
	 *
	 * Questa operazione consente di ottenere le informazioni sull'API implementata
	 * dall'erogazione identificata dall'erogatore, dal nome e dalla versione
	 *
	 */
	@Override
	public ApiImplVersioneApiView getFruizioneAPI(String erogatore, String nome, Integer versione, ProfiloEnum profilo,
			String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione");

			ApiImplVersioneApiView ret = ErogazioniApiHelper.aspsToApiImplVersioneApiView(env, asps);

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
	 * Restituisce il dettaglio di un allegato della fruizione
	 *
	 * Questa operazione consente di ottenere il dettaglio di un allegato della
	 * fruizione di API identificata dall'erogatore, dal nome e dalla versione
	 *
	 */
	@Override
	public ApiImplAllegato getFruizioneAllegato(String erogatore, String nome, Integer versione, String nomeAllegato,
			ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Accordo servizio parte specifica");

			final Optional<Long> idDoc = ErogazioniApiHelper.getIdDocumento(nomeAllegato, asps);

			if (!idDoc.isPresent())
				throw FaultCode.NOT_FOUND.toException("Allegato di nome " + nomeAllegato + " non presente.");

			final Documento doc = env.archiviCore.getDocumento(idDoc.get(), true);

			context.getLogger().info("Invocazione completata con successo");
			return ErogazioniApiHelper.documentoToImplAllegato(doc);

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Restituisce le informazioni su connettore associato alla fruizione
	 *
	 * Questa operazione consente di ottenere le informazioni sul connettore
	 * associato alla fruizione identificata dall'erogatore, dal nome e dalla
	 * versione
	 *
	 */
	@Override
	public ConnettoreFruizione getFruizioneConnettore(String erogatore, String nome, Integer versione, ProfiloEnum profilo,
			String soggetto, String tipoServizio, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione");

			
			
			org.openspcoop2.core.registry.Connettore regConn = null;
			
			if(gruppo != null) {
				
				IdServizio idAsps = new IdServizio(env.idServizioFactory.getIDServizioFromAccordo(asps), asps.getId());

				IDPortaDelegata idPd = BaseHelper.supplyOrNotFound( () -> ErogazioniApiHelper.getIDGruppoPD(gruppo, env.idSoggetto.toIDSoggetto(), idAsps, env.apsCore), "Gruppo per l'erogazione scelta");
				PortaDelegata pd = env.pdCore.getPortaDelegata(idPd);
				MappingFruizionePortaDelegata mapping = env.pdCore.getMappingFruizionePortaDelegata(pd);
				
				if(!mapping.isDefault()) {
					List<String> listaAzioniPDAssociataMappingNonDefault = pd.getAzione().getAzioneDelegataList();
					String azioneConnettore =  null;
					if(listaAzioniPDAssociataMappingNonDefault!=null && listaAzioniPDAssociataMappingNonDefault.size()>0) {
						azioneConnettore = listaAzioniPDAssociataMappingNonDefault.get(0);
					}

					
					long idServizioFruitoreInt = env.apsCore
							.getServizioFruitore(IDServizioFactory.getInstance().getIDServizioFromAccordo(asps), env.idSoggetto.getId());
					final Fruitore servFru = env.apsCore.getServizioFruitore(idServizioFruitoreInt);

					if (servFru == null)
						throw FaultCode.NOT_FOUND
								.toException("Soggetto fruitore " + env.idSoggetto.toString() + "non registrato per la fruizione scelta");

					// Prendo pero poi immagine del fruitore dall'asps
					final Fruitore fruitore = BaseHelper.findFirst(asps.getFruitoreList(),
							f -> f.getTipo().equals(servFru.getTipo()) && f.getNome().equals(servFru.getNome()))
							.orElseThrow(() -> FaultCode.RICHIESTA_NON_VALIDA.toException("Fruizione non presente nel registro."));
							

					if(azioneConnettore!=null && !"".equals(azioneConnettore)) {
						for (ConfigurazioneServizioAzione check : fruitore.getConfigurazioneAzioneList()) {
							if(check.getAzioneList().contains(azioneConnettore)) {
								regConn = check.getConnettore();
							}
						}
					}
				}
			}

			if(regConn == null) {
			       regConn = ErogazioniApiHelper.getConnettoreFruizione(asps,
	                       env.idSoggetto, env);
			}
	       
			ConnettoreFruizione c = ConnettoreAPIHelper.buildConnettoreFruizione(regConn);

			context.getLogger().info("Invocazione completata con successo");
			return c;

		} catch (javax.ws.rs.WebApplicationException e) {
			context.getLogger().error_except404("Invocazione terminata con errore '4xx': %s", e, e.getMessage());
			throw e;
		} catch (Throwable e) {
			context.getLogger().error("Invocazione terminata con errore: %s", e, e.getMessage());
			throw FaultCode.ERRORE_INTERNO.toException(e);
		}
	}

	/**
	 * Restituisce le informazioni generali di una fruizione di API
	 *
	 * Questa operazione consente di ottenere le informazioni generali di una
	 * fruizione di API identificata dall'erogatore, dal nome e dalla versione
	 *
	 */
	@Override
	public ApiImplInformazioniGeneraliView getFruizioneInformazioniGenerali(String erogatore, String nome,
			Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione");

			ApiImplInformazioniGeneraliView ret = ErogazioniApiHelper.fruizioneToApiImplInformazioniGeneraliView(env, asps,
					env.idSoggetto);

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
     * Restituisce le informazioni ModI associate alla fruizione
     *
     * Questa operazione consente di ottenere le informazioni ModI associate all&#x27;erogazione identificata dall&#x27;erogatore, dal nome e dalla versione
     *
     */
	@Override
    public FruizioneModI getFruizioneModI(String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione");

			IDFruizione idFruizione = new IDFruizione();
			IDServizio idAps = new IDServizio();

			if(asps!=null) {
				idAps.setUriAccordoServizioParteComune(asps.getAccordoServizioParteComune());
				idAps.setPortType(asps.getPortType());
			}

			idFruizione.setIdServizio(idAps);
			idFruizione.setIdFruitore(new IDSoggetto(env.idSoggetto.getTipo(), env.idSoggetto.getNome()));

			
			Fruitore f = ErogazioniApiHelper.getFruitore(asps, env.idSoggetto.getNome());
			
			if(env.isProfiloModi()) {
				ErogazioniApiHelper.addInfoTokenPolicyForModI(f.getConnettore(), env, false);
			}
			
			FruizioneModI ret = ModiErogazioniApiHelper.getFruizioneModI(asps, env, profilo, ErogazioniApiHelper.getProtocolPropertiesMap(idFruizione, f, env));

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
	 * Restituisce le informazioni sull'url di invocazione necessaria ad invocare la
	 * fruizione
	 *
	 * Questa operazione consente di ottenere le informazioni sull'url di
	 * invocazione necessaria ad invocare la fruizione identificata dall'erogatore,
	 * dal nome e dalla versione
	 *
	 */
	@Override
	public ApiImplUrlInvocazioneView getFruizioneUrlInvocazione(String erogatore, String nome, Integer versione,
			ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione");
			final AccordoServizioParteComuneSintetico aspc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
			final IDPortaDelegata idPorta = BaseHelper.supplyOrNotFound(() -> env.pdCore.getIDPortaDelegataAssociataDefault(
					env.idServizioFactory.getIDServizioFromAccordo(asps), env.idSoggetto.toIDSoggetto()), "Porta Delegata");
			final PortaDelegata pd = BaseHelper.supplyOrNotFound(() -> env.pdCore.getPortaDelegata(idPorta),
					"Porta Delegata");
			final PortaDelegataAzione pdAzione = pd.getAzione();

			String urlInvocazione = ErogazioniApiHelper.getUrlInvocazioneFruizione(asps, env.idSoggetto.toIDSoggetto(), env);
			ApiImplUrlInvocazioneView ret = new ApiImplUrlInvocazioneView();

			Map<String, String> azioni = env.paCore.getAzioniConLabel(asps, aspc, false, true, new ArrayList<>());
			ret.setAzioni(Arrays.asList(azioni.keySet().toArray(new String[azioni.size()])));
			ret.setForceInterface(Helper.statoFunzionalitaConfToBool(pdAzione.getForceInterfaceBased()));
			ret.setModalita(Enum.valueOf(ModalitaIdentificazioneAzioneEnum.class, pdAzione.getIdentificazione().name()));
			switch (ret.getModalita()) {
			case CONTENT_BASED:
			case URL_BASED:
				ret.setPattern(pdAzione.getPattern());
				break;
			case HEADER_BASED:
				ret.setNome(pdAzione.getPattern());
				break;
			case INPUT_BASED:
			case PROTOCOL_BASED:
			case SOAP_ACTION_BASED:
				break;
			case STATIC:
			case INTERFACE_BASED:
				ret.setForceInterface(false);
				break;
			}
			ret.setUrlInvocazione(urlInvocazione);

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
	 * Consente di modificare la versione dell'API implementata dalla fruizione
	 *
	 * Questa operazione consente di aggiornare la versione dell'API implementata
	 * dall'erogazione identificata dall'erogatore, dal nome e dalla versione
	 *
	 */
	@Override
	public void updateFruizioneAPI(ApiImplVersioneApi body, String erogatore, String nome, Integer versione,
			ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			BaseHelper.throwIfNull(body);

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IdSoggetto idErogatore = new IdSoggetto(new IDSoggetto(env.tipo_soggetto, erogatore));
			final Soggetto soggErogatore = BaseHelper.supplyOrNotFound(
					() -> env.soggettiCore.getSoggettoRegistro(idErogatore.toIDSoggetto()), "Soggetto Erogatore");
			idErogatore.setId(soggErogatore.getId());

			final AccordoServizioParteSpecifica asps = BaseHelper
					.supplyOrNotFound(() -> ErogazioniApiHelper.getServizioIfFruizione(tipoServizio, nome, versione,
							idErogatore.toIDSoggetto(), env.idSoggetto.toIDSoggetto(), env), "Fruizione");
			final AccordoServizioParteComuneSintetico as = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());

			List<AccordoServizioParteComune> asParteComuneCompatibili = env.apsCore.findAccordiParteComuneBySoggettoAndNome(
					as.getNome(), new IDSoggetto(as.getSoggettoReferente().getTipo(), as.getSoggettoReferente().getNome()));

			Optional<AccordoServizioParteComune> newApc = BaseHelper.findFirst(asParteComuneCompatibili,
					a -> (a.getVersione()!=null && body.getApiVersione()!=null && (a.getVersione().intValue() == body.getApiVersione().intValue())));

			if (!newApc.isPresent()) {
				throw FaultCode.RICHIESTA_NON_VALIDA
						.toException("Nessuna api " + as.getNome() + " e versione " + body.getApiVersione() + " registrata");
			}

			asps.setAccordoServizioParteComune(env.idAccordoFactory.getUriFromAccordo(newApc.get()));
			asps.setIdAccordo(newApc.get().getId());

			asps.setOldIDServizioForUpdate(env.idServizioFactory.getIDServizioFromAccordo(asps));

			ErogazioniApiHelper.serviziUpdateCheckData(as, asps, false, env);

			List<Object> oggettiDaAggiornare = AccordiServizioParteSpecificaUtilities.getOggettiDaAggiornare(asps,
					env.apsCore);

			env.apsCore.performUpdateOperation(env.userLogin, false, oggettiDaAggiornare.toArray());
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
	 * Modifica i dati di un allegato della fruizione
	 *
	 * Questa operazione consente di aggiornare i dettagli di un allegato della
	 * fruizione di API identificata dall'erogatore, dal nome e dalla versione
	 *
	 */
	@Override
	public void updateFruizioneAllegato(ApiImplAllegato body, String erogatore, String nome, Integer versione,
			String nomeAllegato, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione");

			ErogazioniApiHelper.updateAllegatoAsps(body, nomeAllegato, env, asps);

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
	 * Consente di modificare la configurazione del connettore associato alla
	 * fruizione
	 *
	 * Questa operazione consente di aggiornare la configurazione del connettore
	 * associato alla fruizione identificata dall'erogatore, dal nome e dalla
	 * versione
	 *
	 */
	@Override
	public void updateFruizioneConnettore(ConnettoreFruizione body, String erogatore, String nome, Integer versione,
			ProfiloEnum profilo, String soggetto, String tipoServizio, String gruppo) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			BaseHelper.throwIfNull(body);

			if(body.getConnettore() instanceof ConnettoreMessageBox) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Impossibile associare a una fruizione il connettore di tipo message-box");
			}
			if(body.getConnettore() instanceof ConnettoreApplicativoServer) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Impossibile associare a una fruizione il connettore di tipo applicativo server");
			}
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);

			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione");

			final List<Fruitore> fruitori = asps.getFruitoreList();

			long idServizioFruitoreInt = env.apsCore
					.getServizioFruitore(IDServizioFactory.getInstance().getIDServizioFromAccordo(asps), env.idSoggetto.getId());
			final Fruitore servFru = env.apsCore.getServizioFruitore(idServizioFruitoreInt);

			if (servFru == null)
				throw FaultCode.NOT_FOUND
						.toException("Soggetto fruitore " + env.idSoggetto.toString() + "non registrato per la fruizione scelta");

			org.openspcoop2.core.registry.Connettore connettore = null;

			IDPortaDelegata idPd = null;
			
			// Prendo pero poi immagine del fruitore dall'asps
			final Fruitore fruitore = BaseHelper.findAndRemoveFirst(fruitori,
					f -> f.getTipo().equals(servFru.getTipo()) && f.getNome().equals(servFru.getNome()));


			IdServizio idAsps = new IdServizio(env.idServizioFactory.getIDServizioFromAccordo(asps), asps.getId());

			if(gruppo != null) {
				idPd = BaseHelper.supplyOrNonValida( () -> ErogazioniApiHelper.getIDGruppoPD(gruppo, env.idSoggetto.toIDSoggetto(), idAsps, env.apsCore), "Gruppo per l'erogazione scelta");
				PortaDelegata pd = env.pdCore.getPortaDelegata(idPd);
				MappingFruizionePortaDelegata mapping = env.pdCore.getMappingFruizionePortaDelegata(pd);
				
				if(!mapping.isDefault()) {
					List<String> listaAzioniPDAssociataMappingNonDefault = pd.getAzione().getAzioneDelegataList();
					String azioneConnettore =  null;
					if(listaAzioniPDAssociataMappingNonDefault!=null && listaAzioniPDAssociataMappingNonDefault.size()>0) {
						azioneConnettore = listaAzioniPDAssociataMappingNonDefault.get(0);
					}

					if(azioneConnettore!=null && !"".equals(azioneConnettore)) {
						for (ConfigurazioneServizioAzione check : fruitore.getConfigurazioneAzioneList()) {
							if(check.getAzioneList().contains(azioneConnettore)) {
								connettore = check.getConnettore();
							}
						}
					}
				}

			} else {
				idPd = env.pdCore.getIDPortaDelegataAssociataDefault(idAsps, env.idSoggetto.toIDSoggetto());
			}

			PortaDelegata pd = env.pdCore.getPortaDelegata(idPd);


			if(gruppo != null) {
				if(connettore != null) {
					String oldConnT = connettore.getTipo();
					if ((connettore.getCustom() != null && connettore.getCustom())
							&& !connettore.getTipo().equals(TipiConnettore.HTTPS.toString())
							&& !connettore.getTipo().equals(TipiConnettore.FILE.toString())) {
						oldConnT = TipiConnettore.CUSTOM.toString();
					}

					ConnettoreAPIHelper.fillConnettoreRegistro(connettore, env, body.getConnettore(), oldConnT);

					if (!ConnettoreAPIHelper.connettoreCheckData(body.getConnettore(), env, false)) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
					}

				} else {
	
					ConfigurazioneServizioAzione configurazioneAzione = new ConfigurazioneServizioAzione();
					Connettore connettoreN = new Connettore();
	
					String oldConnT = connettoreN.getTipo();
					if ((connettoreN.getCustom() != null && connettoreN.getCustom())
							&& !connettoreN.getTipo().equals(TipiConnettore.HTTPS.toString())
							&& !connettoreN.getTipo().equals(TipiConnettore.FILE.toString())) {
						oldConnT = TipiConnettore.CUSTOM.toString();
					}
	
					ConnettoreAPIHelper.fillConnettoreRegistro(connettoreN, env, body.getConnettore(), oldConnT);
	
					if (!ConnettoreAPIHelper.connettoreCheckData(body.getConnettore(), env, false)) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
					}

					configurazioneAzione.setConnettore(connettoreN);
					for (int i = 0; i < pd.getAzione().sizeAzioneDelegataList(); i++) {
						configurazioneAzione.addAzione(pd.getAzione().getAzioneDelegata(i));
					}
					fruitore.addConfigurazioneAzione(configurazioneAzione);
	
				} 
			} else {
				
				connettore = fruitore.getConnettore();
				
				String oldConnT = connettore.getTipo();
				if ((connettore.getCustom() != null && connettore.getCustom())
						&& !connettore.getTipo().equals(TipiConnettore.HTTPS.toString())
						&& !connettore.getTipo().equals(TipiConnettore.FILE.toString())) {
					oldConnT = TipiConnettore.CUSTOM.toString();
				}

				ConnettoreAPIHelper.fillConnettoreRegistro(connettore, env, body.getConnettore(), oldConnT);

				if (!ConnettoreAPIHelper.connettoreCheckData(body.getConnettore(), env, false)) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
				}

				fruitore.setConnettore(ConnettoreAPIHelper.buildConnettoreRegistro(env, body.getConnettore()));
			}
			
			fruitori.add(fruitore);
			asps.setFruitoreList(fruitori);

			env.apsCore.performUpdateOperation(env.userLogin, false, asps);

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
	 * Consente di modificare le informazioni generali di una fruizione di API
	 *
	 * Questa operazione consente di aggiornare le informazioni generali di una
	 * fruizione di API identificata dall'erogatore, dal nome e dalla versione
	 *
	 */
	@Override
	public void updateFruizioneInformazioniGenerali(ApiImplInformazioniGenerali body, String erogatore, String nome,
			Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione");

			ErogazioniApiHelper.updateInformazioniGenerali(body, env, asps, false);

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
     * Consente di modificare le informazioni ModI associate alla fruizione
     *
     * Questa operazione consente di aggiornare le informazioni ModI associate all&#x27;erogazione identificata dall&#x27;erogatore, dal nome e dalla versione
     *
     */
	@Override
    public void updateFruizioneModI(FruizioneModI body, String erogatore, String nome, Integer versione, ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");     

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");     
                        
			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione");
			
			final IDServizio idAps = env.idServizioFactory.getIDServizioFromValues(asps.getTipo(), asps.getNome(),
					new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore()), asps.getVersione());

			IDFruizione idFruizione = new IDFruizione();
			
			idFruizione.setIdServizio(idAps);
			idFruizione.setIdFruitore(new IDSoggetto(env.idSoggetto.getTipo(), env.idSoggetto.getNome()));


			ProtocolProperties protocolProperties = null;
			if(profilo != null) {
				protocolProperties = ModiErogazioniApiHelper.updateModiProtocolProperties(asps, profilo, body.getModi(), env);
	
				if(protocolProperties != null) {
					Fruitore f = ErogazioniApiHelper.getFruitore(asps, env.idSoggetto.getNome());
					f.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(protocolProperties, ConsoleOperationType.ADD, null));
				}
			}

			asps.setOldIDServizioForUpdate(env.idServizioFactory.getIDServizioFromAccordo(asps));

			ErogazioniApiHelper.validateProperties(env, protocolProperties, idFruizione, ConsoleOperationType.CHANGE);

			List<Object> oggettiDaAggiornare = AccordiServizioParteSpecificaUtilities.getOggettiDaAggiornare(asps,
					env.apsCore);

			env.apsCore.performUpdateOperation(env.userLogin, false, oggettiDaAggiornare.toArray());

        
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
	 * Consente di modificare la configurazione utilizzata per identificare l'azione
	 * invocata dell'API implementata dalla fruizione
	 *
	 * Questa operazione consente di aggiornare la configurazione utilizzata dal
	 * Gateway per identificare l'azione invocata
	 *
	 */
	@Override
	public void updateFruizioneUrlInvocazione(ApiImplUrlInvocazione body, String erogatore, String nome, Integer versione,
			ProfiloEnum profilo, String soggetto, String tipoServizio) {
		IContext context = this.getContext();
		try {
			context.getLogger().info("Invocazione in corso ...");

			AuthorizationManager.authorize(context, getAuthorizationConfig());
			context.getLogger().debug("Autorizzazione completata con successo");

			BaseHelper.throwIfNull(body);

			if (body.getModalita() == null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare una modalità di identificazione azione valida");
			}

			final ErogazioniEnv env = new ErogazioniEnv(context.getServletRequest(), profilo, soggetto, context);
			final IDSoggetto idErogatore = new IDSoggetto(env.tipo_soggetto, erogatore);
			final AccordoServizioParteSpecifica asps = BaseHelper.supplyOrNotFound(() -> ErogazioniApiHelper
					.getServizioIfFruizione(tipoServizio, nome, versione, idErogatore, env.idSoggetto.toIDSoggetto(), env),
					"Fruizione");
			final IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);
			final IDPortaDelegata idPorta = BaseHelper.supplyOrNotFound(() -> env.pdCore.getIDPortaDelegataAssociataDefault(
					env.idServizioFactory.getIDServizioFromAccordo(asps), env.idSoggetto.toIDSoggetto()), "Porta Delegata");
			final PortaDelegata pd = BaseHelper.supplyOrNotFound(() -> env.pdCore.getPortaDelegata(idPorta),
					"Porta Delegata");
			final PortaDelegataAzione pdAzione = new PortaDelegataAzione(); // pd.getAzione() == null ? new
																																			// PortaDelegataAzione() : pd.getAzione();

			final AccordoServizioParteComuneSintetico apc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());

			List<PortaDelegataAzioneIdentificazione> identModes = env.pdHelper.getModalitaIdentificazionePorta(
					env.tipo_protocollo, env.apcCore.toMessageServiceBinding(apc.getServiceBinding()),
					ConsoleInterfaceType.AVANZATA); // uso avanzata per usare tutte le modalita
			if(!identModes.contains(PortaDelegataAzioneIdentificazione.STATIC))
				identModes.add(PortaDelegataAzioneIdentificazione.STATIC); // gestisco sotto
			
			if (!identModes.contains(PortaDelegataAzioneIdentificazione.valueOf(body.getModalita().name())))
				throw FaultCode.RICHIESTA_NON_VALIDA
						.toException("La modalità di identificazione azione deve essere una fra: " + identModes.toString());

			boolean setPattern = true;
			long idAzione = -1;
			switch (body.getModalita()) {
			case CONTENT_BASED:
				if(body.getPattern()==null) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("La modalità di identificazione azione indicata ("+body.getModalita().toString()+") richiede che sia definito il parametro 'pattern'");
				}
				pdAzione.setPattern(body.getPattern());
				pdAzione.setForceInterfaceBased(Helper.boolToStatoFunzionalitaConf(body.isForceInterface()));
				break;
			case HEADER_BASED:
				if(body.getNome()==null && body.getPattern()==null) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("La modalità di identificazione azione indicata ("+body.getModalita().toString()+") richiede che sia definito il parametro 'nome'");
				}
				if(body.getNome()!=null && body.getPattern()!=null) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("La modalità di identificazione azione indicata ("+body.getModalita().toString()+") richiede che sia definito solo il parametro 'nome'");
				}
				if(body.getNome()!=null) {
					pdAzione.setPattern(body.getNome());
				}
				else {
					pdAzione.setPattern(body.getPattern());
				}
				setPattern = true;
				pdAzione.setForceInterfaceBased(Helper.boolToStatoFunzionalitaConf(body.isForceInterface()));
				break;
			case INPUT_BASED:
				pdAzione.setForceInterfaceBased(Helper.boolToStatoFunzionalitaConf(body.isForceInterface()));
				break;
			case INTERFACE_BASED:
				break;
			case SOAP_ACTION_BASED:
				// permesso solo su API SOAP con 1 operazione
				if(!ServiceBinding.SOAP.equals(apc.getServiceBinding())) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("La modalità di identificazione azione indicata ("+body.getModalita().toString()+") non è permessa per API di tipo "+apc.getServiceBinding());
				}
				pdAzione.setForceInterfaceBased(Helper.boolToStatoFunzionalitaConf(body.isForceInterface()));
				break;
			case URL_BASED:
				// permesso solo su API SOAP con 1 operazione
				if(!ServiceBinding.SOAP.equals(apc.getServiceBinding())) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("La modalità di identificazione azione indicata ("+body.getModalita().toString()+") non è permessa per API di tipo "+apc.getServiceBinding());
				}
				if(body.getPattern()==null) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("La modalità di identificazione azione indicata ("+body.getModalita().toString()+") richiede che sia definito il parametro 'pattern'");
				}
				pdAzione.setPattern(body.getPattern());
				pdAzione.setForceInterfaceBased(Helper.boolToStatoFunzionalitaConf(body.isForceInterface()));
				break;
			case PROTOCOL_BASED:
				break;
			case STATIC:
				// permesso solo su API SOAP con 1 operazione
				if(!ServiceBinding.SOAP.equals(apc.getServiceBinding())) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("La modalità di identificazione azione indicata ("+body.getModalita().toString()+") non è permessa per API di tipo "+apc.getServiceBinding());
				}
				PortTypeSintetico pt = null;
				for (PortTypeSintetico ptCheck : apc.getPortType()) {
					if(ptCheck.getNome().equals(asps.getPortType())) {
						pt = ptCheck;
						break;
					}
				}
				if(pt==null) {
					throw FaultCode.RICHIESTA_NON_VALIDA
						.toException("La modalità di identificazione azione indicata ("+body.getModalita().toString()+") è permessa solamente per API di tipo SOAP che definiscono 1 sola operazione: port-type non trovato");
				}
				if(pt.getAzione()==null || pt.getAzione().size()<=0) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("La modalità di identificazione azione indicata ("+body.getModalita().toString()+") è permessa solamente per API di tipo SOAP che definiscono 1 sola operazione: non sono state trovate azioni");
				}
				if(pt.getAzione().size()>1) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("La modalità di identificazione azione indicata ("+body.getModalita().toString()+") è permessa solamente per API di tipo SOAP che definiscono 1 sola operazione: sono state trovate "+pt.getAzione().size()+" azioni");
				}
				if(body.getNome()==null) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("La modalità di identificazione azione indicata ("+body.getModalita().toString()+") richiede che sia definito il parametro 'nome'");
				}
				boolean findAzione = false;
				for (OperationSintetica op : pt.getAzione()) {
					if(op.getNome().equals(body.getNome())){
						findAzione = true;
						idAzione = op.getId();
						break;
					}
				}
				if(!findAzione) {
					throw FaultCode.RICHIESTA_NON_VALIDA
					.toException("La modalità di identificazione azione indicata ("+body.getModalita().toString()+") riferisce una azione '"+body.getNome()+"' non esistente");
				}
				pdAzione.setNome(body.getNome());
				setPattern = false;
				break;
			}

			pdAzione.setIdentificazione(Enum.valueOf(PortaDelegataAzioneIdentificazione.class, body.getModalita().name()));

			ErogazioniApiHelper.overrideFruizioneUrlInvocazione(env.requestWrapper, idErogatore, idServizio, pd, pdAzione, setPattern, idAzione);

			org.openspcoop2.message.constants.ServiceBinding serviceBinding = org.openspcoop2.message.constants.ServiceBinding.valueOf(apc.getServiceBinding().name());
			if (!env.pdHelper.porteDelegateCheckData(TipoOperazione.CHANGE, pd.getNome(), false,
					serviceBinding)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(env.pd.getMessage());
			}

			pd.setAzione(pdAzione);
			pd.setOldIDPortaDelegataForUpdate(idPorta);
			env.pdCore.performUpdateOperation(env.userLogin, false, pd);

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
