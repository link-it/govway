/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.rs.server.api.impl.erogazioni;

import static org.openspcoop2.utils.service.beans.utils.BaseHelper.deserializeDefault;
import static org.openspcoop2.utils.service.beans.utils.BaseHelper.evalnull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.AutorizzazioneRuoli;
import org.openspcoop2.core.config.AutorizzazioneScope;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazione;
import org.openspcoop2.core.config.CorrelazioneApplicativaElemento;
import org.openspcoop2.core.config.CorrelazioneApplicativaRispostaElemento;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.DumpConfigurazioneRegola;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.GestioneTokenAutenticazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaGestioneIdentificazioneFallita;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaRichiestaIdentificazione;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaRispostaIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataSoggettiErogatori;
import org.openspcoop2.core.config.constants.RuoloContesto;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import org.openspcoop2.core.config.constants.ScopeTipoMatch;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaCacheDigestQueryParameter;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.constants.TipoGestioneCORS;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.rs.server.api.impl.Enums;
import org.openspcoop2.core.config.rs.server.api.impl.Helper;
import org.openspcoop2.core.config.rs.server.api.impl.HttpRequestWrapper;
import org.openspcoop2.core.config.rs.server.api.impl.IdServizio;
import org.openspcoop2.core.config.rs.server.api.impl.StatoDescrizione;
import org.openspcoop2.core.config.rs.server.api.impl.erogazioni.configurazione.ErogazioniConfEnv;
import org.openspcoop2.core.config.rs.server.api.impl.fruizioni.configurazione.FruizioniConfEnv;
import org.openspcoop2.core.config.rs.server.model.*;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento;
import org.openspcoop2.core.controllo_traffico.beans.InfoPolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoFiltroApplicativo;
import org.openspcoop2.core.controllo_traffico.constants.TipoPeriodoRealtime;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.core.controllo_traffico.utils.PolicyUtilities;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizio;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.beans.GruppoSintetico;
import org.openspcoop2.core.registry.beans.PortTypeSintetico;
import org.openspcoop2.core.registry.constants.CredenzialeTipo;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.constants.TipiDocumentoLivelloServizio;
import org.openspcoop2.core.registry.constants.TipiDocumentoSemiformale;
import org.openspcoop2.core.registry.constants.TipiDocumentoSicurezza;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.core.registry.driver.db.IDSoggettoDB;
import org.openspcoop2.core.transazioni.utils.TipoCredenzialeMittente;
import org.openspcoop2.pdd.config.UrlInvocazioneAPI;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazioneBasic;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazionePrincipal;
import org.openspcoop2.pdd.core.autorizzazione.CostantiAutorizzazione;
import org.openspcoop2.protocol.basic.Utilities;
import org.openspcoop2.protocol.information_missing.constants.StatoType;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.beans.utils.ListaUtils;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.SerialiableFormFile;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaUtilities;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneHelper;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeHelper;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.ruoli.RuoliCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;


/**
 * ErogazioniApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ErogazioniApiHelper {
		
	
	@SuppressWarnings("unchecked")
	public static final <T> T deserializeModalitaConfGruppo(ModalitaConfigurazioneGruppoEnum discr, Object body) throws UtilsException, InstantiationException, IllegalAccessException {
		
		switch(discr) {
		case EREDITA: {
			GruppoEreditaConfigurazione conf = deserializeDefault(body, GruppoEreditaConfigurazione.class);
			if (StringUtils.isEmpty(conf.getNome())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(GruppoEreditaConfigurazione.class.getName()+": Indicare il campo obbligatorio 'nome'");
			}
			return (T) conf;
		}
		case NUOVA: {
			GruppoNuovaConfigurazione conf = deserializeDefault(body, GruppoNuovaConfigurazione.class);
			if (conf.getAutenticazione() == null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(GruppoNuovaConfigurazione.class.getName()+": Indicare il campo obbligatorio 'autenticazione'");
			}
			
			return (T) conf;
		}
		default: 
			return null;
		}
	}

	
	public static final void fillAps(
			AccordoServizioParteSpecifica specifico, 
			APIImpl impl
		) {	
		
		specifico.setNome(impl.getApiNome());
		specifico.setTipo(impl.getTipoServizio());
		specifico.setVersione(impl.getApiVersione());
		specifico.setPortType(impl.getApiSoapServizio());	
	}
	
	public static final AccordoServizioParteSpecifica apiImplToAps(APIImpl impl, final Soggetto soggErogatore, AccordoServizioParteComuneSintetico as, ErogazioniEnv env) 
			throws DriverRegistroServiziException, DriverRegistroServiziNotFound, CoreException, ProtocolException {
		final AccordoServizioParteSpecifica ret = new AccordoServizioParteSpecifica();
				
		fillAps(ret, impl);
		
		// Questo per seguire la specifica della console, che durante la creazione di un servizio soap
		// vuole che il nome del'asps sia quello del servizio\port_type
		if (as.getServiceBinding() == ServiceBinding.SOAP) {
			ret.setNome(ret.getPortType());
		}
		
		if(env.apsCore.isSupportatoVersionamentoAccordiServizioParteSpecifica(env.tipo_protocollo))
			ret.setVersione(as.getVersione());
		
		if(impl instanceof Erogazione) {
			Erogazione erogazione = (Erogazione) impl;
			if(erogazione.getErogazioneNome()!=null && !"".equals(erogazione.getErogazioneNome())) {
				ret.setNome(erogazione.getErogazioneNome());
			}
			if(env.apsCore.isSupportatoVersionamentoAccordiServizioParteSpecifica(env.tipo_protocollo)) {
				if(erogazione.getErogazioneVersione()!=null && erogazione.getErogazioneVersione()>0) {
					ret.setVersione(erogazione.getErogazioneVersione());
				}
			}
		}
		else if(impl instanceof Fruizione) {
			Fruizione fruizione = (Fruizione) impl;
			if(fruizione.getFruizioneNome()!=null && !"".equals(fruizione.getFruizioneNome())) {
				ret.setNome(fruizione.getFruizioneNome());
			}
			if(env.apsCore.isSupportatoVersionamentoAccordiServizioParteSpecifica(env.tipo_protocollo)) {
				if(fruizione.getFruizioneVersione()!=null && fruizione.getFruizioneVersione()>0) {
					ret.setVersione(fruizione.getFruizioneVersione());
				}
			}
		}

		ret.setIdSoggetto(soggErogatore.getId());
		ret.setTipoSoggettoErogatore(soggErogatore.getTipo());
		ret.setNomeSoggettoErogatore(soggErogatore.getNome());

		ret.setVersioneProtocollo(soggErogatore.getVersioneProtocollo());
		
		ret.setByteWsdlImplementativoErogatore(null);
		ret.setByteWsdlImplementativoFruitore(null);
		
     	ret.setIdAccordo(as.getId());	
		ret.setAccordoServizioParteComune(env.idAccordoFactory.getUriFromAccordo(as));
		ret.setTipologiaServizio(TipologiaServizio.NORMALE); // dal debug servcorr è "no"
		ret.setSuperUser(env.userLogin);
		ret.setPrivato(false);								// Come da debug.
		ret.setStatoPackage(StatoType.FINALE.getValue()); 
		ret.setConfigurazioneServizio(new ConfigurazioneServizio());
				 
		if (StringUtils.isEmpty(ret.getTipo())) {
			String tipoServizio = env.protocolFactoryMgr.getProtocolFactoryByName(env.tipo_protocollo).createProtocolConfiguration().getTipoServizioDefault(Utilities.convert(as.getServiceBinding()));
			ret.setTipo( tipoServizio );
		}
				
		return ret;
	}
	
	
	public static final void serviziUpdateCheckData(AccordoServizioParteComuneSintetico as, AccordoServizioParteSpecifica asps, boolean isErogazione, ErogazioniEnv env) throws Exception {
		
		 // Determino i soggetti compatibili
        Search searchSoggetti = new Search(true);
		searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, env.tipo_protocollo);
		String[] soggettiCompatibili = env.soggettiCore.soggettiRegistroList(null, searchSoggetti).stream()
				.map( s -> s.getId().toString())
				.toArray(String[]::new);
		
		// Determino la lista Api
		String[] accordiList = AccordiServizioParteSpecificaUtilities.getListaAPI(
				env.tipo_protocollo,
				env.userLogin,
				env.apsCore, 
				env.apsHelper
			).stream()
    		.map( a -> a.getId().toString() )
    		.toArray(String[]::new);
		

		// Determino l'erogatore
		IdSoggetto erogatore = new IdSoggetto( new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore()));
		erogatore.setId(asps.getIdSoggetto());
		
		org.openspcoop2.core.registry.Connettore connRegistro = asps.getConfigurazioneServizio().getConnettore();
		
		final String endpointtype = connRegistro.getTipo();
		final String endpoint_url = connRegistro.getProperties().get(CostantiDB.CONNETTORE_HTTP_LOCATION);
		
		// Recupero i Servizi Esposti dalla API 
        final String[] ptArray =  AccordiServizioParteSpecificaUtilities.getListaPortTypes(as, env.apsCore, env.apsHelper)
        		.stream()
         		.map( p -> p.getNome() )
         		.toArray(String[]::new);
		
		final boolean accordoPrivato = as.getPrivato()!=null && as.getPrivato();

		final Connettore connRest = ErogazioniApiHelper.buildConnettore(connRegistro.getProperties());
		final ConnettoreConfigurazioneHttps httpsConf 	 = connRest.getAutenticazioneHttps();

		final ConnettoreConfigurazioneHttpBasic httpConf	 = connRest.getAutenticazioneHttp();
		// Questa è la cosa diversa per i fruitori, Li invece abbiamo le credenziali direttamente nel connettore.
        final ConnettoreConfigurazioneHttpsClient httpsClient = evalnull( () -> httpsConf.getClient() );
      	final ConnettoreConfigurazioneHttpsServer httpsServer = evalnull( () -> httpsConf.getServer() );
      	final ConnettoreConfigurazioneProxy 	  proxy   	  = connRest.getProxy();
      	final ConnettoreConfigurazioneTimeout	  timeoutConf = connRest.getTempiRisposta();
      	final String tokenPolicy = connRest.getTokenPolicy(); 
      	final boolean autenticazioneToken = tokenPolicy!=null;
      	
        
 		final boolean httpsstato = httpsClient != null;
 		final boolean http_stato = connRest.getAutenticazioneHttp() != null;
 		final boolean proxy_enabled = connRest.getProxy() != null;
 		final boolean tempiRisposta_enabled = connRest.getTempiRisposta() != null;
 		
 		String httpskeystore = null;
		if ( httpsClient != null ) {
			if ( httpsClient.getKeystorePath() != null || httpsClient.getKeystoreTipo() != null ) {
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI;  
			}
			else
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
		}
		

		final BinaryParameter xamlPolicy = new BinaryParameter();
		
		String erogazioneAutenticazione = null;
		boolean erogazioneAutenticazioneOpzionale = true;
		TipoAutenticazionePrincipal erogazioneAutenticazionePrincipal = null;
		List<String> erogazioneAutenticazioneParametroList = null;
		String erogazioneAutorizzazione = null;
		boolean erogazioneAutorizzazioneAutenticati = false;
		boolean erogazioneAutorizzazioneRuoli = false;
		String erogazioneAutorizzazioneRuoliTipologia = null;
		String erogazioneAutorizzazioneRuoliMatch = null;

		String fruizioneAutenticazione = null;
		boolean fruizioneAutenticazioneOpzionale = true;
		TipoAutenticazionePrincipal fruizioneAutenticazionePrincipal = null;
		List<String> fruizioneAutenticazioneParametroList = null;
		String fruizioneAutorizzazione = null;
		boolean fruizioneAutorizzazioneAutenticati = false;
		boolean fruizioneAutorizzazioneRuoli = false;
		String fruizioneAutorizzazioneRuoliTipologia = null;
		String fruizioneAutorizzazioneRuoliMatch = null;
		
		if ( isErogazione ) {
			final IDServizio idServizio = asps.getOldIDServizioForUpdate();
			final IDPortaApplicativa idPA = env.paCore.getIDPortaApplicativaAssociataDefault(idServizio);
			final PortaApplicativa pa = env.paCore.getPortaApplicativa(idPA);
						
			erogazioneAutenticazione = pa.getAutenticazione();
			erogazioneAutenticazioneOpzionale = Helper.statoFunzionalitaConfToBool(pa.getAutenticazioneOpzionale());
			erogazioneAutenticazionePrincipal = env.paCore.getTipoAutenticazionePrincipal(pa.getProprietaAutenticazioneList());
			erogazioneAutenticazioneParametroList = env.paCore.getParametroAutenticazione(erogazioneAutenticazione, pa.getProprietaAutenticazioneList());
			
			erogazioneAutorizzazione = pa.getAutorizzazione();
			erogazioneAutorizzazioneAutenticati = TipoAutorizzazione.isAuthenticationRequired(pa.getAutorizzazione());
			erogazioneAutorizzazioneRuoli = TipoAutorizzazione.isRolesRequired(pa.getAutorizzazione());
			erogazioneAutorizzazioneRuoliTipologia =  AutorizzazioneUtilities.convertToRuoloTipologia(pa.getAutorizzazione()).toString();
			erogazioneAutorizzazioneRuoliMatch = evalnull( () -> pa.getRuoli().getMatch().toString() );
			
			if (pa.getXacmlPolicy() != null) {							
		        xamlPolicy.setValue(pa.getXacmlPolicy().getBytes());
		        xamlPolicy.setName(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);
		     }
			
		} else {
			final IDServizio idServizio = asps.getOldIDServizioForUpdate();
			final IDPortaDelegata idPD = env.pdCore.getIDPortaDelegataAssociataDefault(idServizio, env.idSoggetto.toIDSoggetto());
			final PortaDelegata pd = env.pdCore.getPortaDelegata(idPD);
					
			fruizioneAutenticazione = pd.getAutenticazione();
			fruizioneAutenticazioneOpzionale = Helper.statoFunzionalitaConfToBool(pd.getAutenticazioneOpzionale());
			fruizioneAutenticazionePrincipal = env.pdCore.getTipoAutenticazionePrincipal(pd.getProprietaAutenticazioneList());
			fruizioneAutenticazioneParametroList = env.pdCore.getParametroAutenticazione(fruizioneAutenticazione, pd.getProprietaAutenticazioneList());
			
			fruizioneAutorizzazione = pd.getAutorizzazione();
			fruizioneAutorizzazioneAutenticati = TipoAutorizzazione.isAuthenticationRequired(pd.getAutorizzazione());
			fruizioneAutorizzazioneRuoli =  TipoAutorizzazione.isRolesRequired(pd.getAutorizzazione());
			fruizioneAutorizzazioneRuoliTipologia =  AutorizzazioneUtilities.convertToRuoloTipologia(pd.getAutorizzazione()).toString();
			fruizioneAutorizzazioneRuoliMatch = evalnull( () -> pd.getRuoli().getMatch().toString() );
			
			if (pd.getXacmlPolicy() != null) {							
		        xamlPolicy.setValue(pd.getXacmlPolicy().getBytes());
		        xamlPolicy.setName(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);
		     }
		}
		
		
		final Properties parametersPOST = null;
		org.openspcoop2.core.registry.Connettore conTmp = null;
		List<ExtendedConnettore> listExtendedConnettore = ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, env.apsHelper, 
							parametersPOST, false, endpointtype); // uso endpointtype per capire se è la prima volta che entro
				
		
		IDServizio oldId = asps.getOldIDServizioForUpdate();
				
        if (! env.apsHelper.serviziCheckData(            		
        		TipoOperazione.CHANGE,
        		soggettiCompatibili,		
        		accordiList, 		 		
        		oldId.getNome(),		//asps.getNome(),				// oldnome
        		oldId.getTipo(),		//asps.getTipo(),				// oldtipo
        		oldId.getVersione(),	//asps.getVersione(),			// oldversione
        		asps.getNome(),
        		asps.getTipo(),
        		erogatore.getId().toString(), 	// idSoggErogatore
        		erogatore.getNome(),
        		erogatore.getTipo(),
        		as.getId().toString(),
        		env.apcCore.toMessageServiceBinding(as.getServiceBinding()),
        		"no",  //servcorr,
        		endpointtype, // endpointtype determinarlo dal connettore,
        		endpoint_url,
        		null, 	// nome JMS
        		null, 	// tipo JMS,
        		evalnull( () -> httpConf.getUsername() ),	
        		evalnull( () -> httpConf.getUsername() ), 
        		null,   // initcont JMS,
        		null,   // urlpgk JMS,
        		null,   // provurl JMS 
        		null,   // connfact JMS
        		null, 	// sendas JMS, 
        		new BinaryParameter(),		//  wsdlimpler
        		new BinaryParameter(),		//  wsdlimplfru
        		asps.getId().toString(), 	
        		asps.getVersioneProtocollo(),	//  Il profilo è la versione protocollo in caso spcoop, è quello del soggetto erogatore.
        		asps.getPortType(),
        		ptArray,
				accordoPrivato,
				false, 	// this.privato,
        		endpoint_url,	// httpsurl, 
        		evalnull( () ->  httpsConf.getTipologia().toString() ),				// I valori corrispondono con con org.openspcoop2.utils.transport.http.SSLConstants
        		BaseHelper.evalorElse( () -> httpsConf.isHostnameVerifier().booleanValue(), false ),				// this.httpshostverify,
				evalnull( () -> httpsServer.getTruststorePath() ),				// this.httpspath
				evalnull( () -> httpsServer.getTruststoreTipo().toString() ),		// this.httpstipo,
				evalnull( () -> httpsServer.getTruststorePassword() ),			// this.httpspwd,
				evalnull( () -> httpsServer.getAlgoritmo() ),					// this.httpsalgoritmo
				httpsstato,	//
        		httpskeystore, 	
        		"", //  this.httpspwdprivatekeytrust
        		evalnull( () -> httpsClient.getKeystorePath() ),					// httpspathkey
        		evalnull( () -> httpsClient.getKeystoreTipo().toString() ),	 		// httpstipokey, coincide con ConnettoriCostanti.TIPOLOGIE_KEYSTORE
        		evalnull( () -> httpsClient.getKeystorePassword() ), 	 		// httpspwdkey
        		evalnull( () -> httpsClient.getKeyPassword() ),	 				// httpspwdprivatekey
        		evalnull( () -> httpsClient.getAlgoritmo() ),					// httpsalgoritmokey
        		evalnull( () -> httpsClient.getKeyAlias() ),					// httpsKeyAlias
        		evalnull( () -> httpsServer.getTruststoreCrl() ),					// httpsTrustStoreCRLs
        		null, 								// tipoconn Da debug = null.	
        		as.getVersione().toString(), 		// Versione aspc
        		false,								// validazioneDocumenti Da debug = false
        		null,								// Da Codice console
        		ServletUtils.boolToCheckBoxStatus(http_stato),	// "yes" se utilizziamo http.
        		ServletUtils.boolToCheckBoxStatus(proxy_enabled),			
    			evalnull( () -> proxy.getHostname() ),
    			evalnull( () -> proxy.getPorta().toString() ),
    			evalnull( () -> proxy.getUsername() ),
    			evalnull( () -> proxy.getPassword() ),				
    			ServletUtils.boolToCheckBoxStatus(tempiRisposta_enabled), 
    			evalnull( () -> timeoutConf.getConnectionTimeout().toString()),		// this.tempiRisposta_connectionTimeout, 
    			evalnull( () -> timeoutConf.getConnectionReadTimeout().toString()),  // this.tempiRisposta_readTimeout, 
    			evalnull( () -> timeoutConf.getTempoMedioRisposta().toString()),		// this.tempiRisposta_tempoMedioRisposta,
        		ServletUtils.boolToCheckBoxStatus(false),	// opzioniAvanzate 
        		"",		// transfer_mode, 
        		"",		// transfer_mode_chunk_size, 
        		"",		// redirect_mode, 
        		"",		// redirect_max_hop, 
        		null,	// requestOutputFileName,
        		null,	// requestOutputFileNameHeaders, 
        		null,	// requestOutputParentDirCreateIfNotExists, 
        		null,	// requestOutputOverwriteIfExists,
        		null,	// responseInputMode,
        		null,	// responseInputFileName,
        		null,	// responseInputFileNameHeaders,
        		null,	// responseInputDeleteAfterRead,
        		null,	// responseInputWaitTime,
        		null,	// erogazioneSoggetto, Come da codice console.
        		null,	// erogazioneRuolo non viene utilizzato.
        		erogazioneAutenticazione, // erogazioneAutenticazione
        		ServletUtils.boolToCheckBoxStatus(erogazioneAutenticazioneOpzionale),					// erogazioneAutenticazioneOpzionale
        		erogazioneAutenticazionePrincipal, // erogazioneAutenticazionePrincipal
        		erogazioneAutenticazioneParametroList, // erogazioneAutenticazioneParametroList
        		AutorizzazioneUtilities.convertToStato(erogazioneAutorizzazione), // erogazioneAutorizzazione										   					// erogazioneAutorizzazione QUESTO E' lo STATO dell'autorizzazione
        		ServletUtils.boolToCheckBoxStatus( erogazioneAutorizzazioneAutenticati ), 			// erogazioneAutorizzazioneAutenticati, 
        		ServletUtils.boolToCheckBoxStatus( erogazioneAutorizzazioneRuoli ),				// erogazioneAutorizzazioneRuoli, 
        		erogazioneAutorizzazioneRuoliTipologia,				// erogazioneAutorizzazioneRuoliTipologia,
        		erogazioneAutorizzazioneRuoliMatch,  	// erogazioneAutorizzazioneRuoliMatch,  AllAnyEnum == RuoloTipoMatch
        		env.isSupportatoAutenticazioneSoggetti,	
        		isErogazione,													// generaPACheckSoggetto (Un'erogazione genera una porta applicativa)
        		listExtendedConnettore,
        		null,																	// fruizioneServizioApplicativo
        		null,																	// Ruolo fruizione, non viene utilizzato.
        		fruizioneAutenticazione, // fruizioneAutenticazione 
        		ServletUtils.boolToCheckBoxStatus(fruizioneAutenticazioneOpzionale), // fruizioneAutenticazioneOpzionale
        		fruizioneAutenticazionePrincipal, // fruizioneAutenticazionePrincipal
        		fruizioneAutenticazioneParametroList, // fruizioneAutenticazioneParametroList
        		AutorizzazioneUtilities.convertToStato(fruizioneAutorizzazione),		// fruizioneAutorizzazione 	
        		ServletUtils.boolToCheckBoxStatus( fruizioneAutorizzazioneAutenticati ),  				// fruizioneAutorizzazioneAutenticati, 
        		ServletUtils.boolToCheckBoxStatus( fruizioneAutorizzazioneRuoli ), 					// fruizioneAutorizzazioneRuoli,
        		fruizioneAutorizzazioneRuoliTipologia, 		// fruizioneAutorizzazioneRuoliTipologia,
        		fruizioneAutorizzazioneRuoliMatch,		// fruizioneAutorizzazioneRuoliMatch,
        		env.tipo_protocollo, 
        		xamlPolicy, 																//allegatoXacmlPolicy,
        		"",
        		null,		// tipoFruitore 
        		null,	// nomeFruitore
        		autenticazioneToken, 
        		tokenPolicy,
        		false, // erogazioneServizioApplicativoServerEnabled, TODO quando si aggiunge applicativo server
    			null // rogazioneServizioApplicativoServer
        	)) {
        	throw FaultCode.RICHIESTA_NON_VALIDA.toException( StringEscapeUtils.unescapeHtml( env.pd.getMessage()) );
        }
		
		
	}
	
	public static final List<IDSoggettoDB> getSoggettiCompatibiliAutorizzazione( CredenzialeTipo tipoAutenticazione, Boolean appId, IdSoggetto erogatore, ErogazioniEnv env ) throws DriverRegistroServiziNotFound, DriverRegistroServiziException, DriverConfigurazioneException {
		
		PddTipologia pddTipologiaSoggettoAutenticati = null;
		boolean gestioneErogatori_soggettiAutenticati_escludiSoggettoErogatore = false;
		
		
		if(env.apsCore.isMultitenant() && env.apsCore.getMultitenantSoggettiErogazioni()!=null) {
			switch (env.apsCore.getMultitenantSoggettiErogazioni()) {
				case SOLO_SOGGETTI_ESTERNI:
					pddTipologiaSoggettoAutenticati = PddTipologia.ESTERNO;
					break;
				case ESCLUDI_SOGGETTO_EROGATORE:
					gestioneErogatori_soggettiAutenticati_escludiSoggettoErogatore = true;
					break;
				case TUTTI:
					break;
				}
		}
		
		List<String> tipiSoggettiGestitiProtocollo = env.soggettiCore.getTipiSoggettiGestitiProtocollo(env.tipo_protocollo);
		
		// calcolo soggetti compatibili con tipi protocollo supportati dalla pa e credenziali indicate
		List<IDSoggettoDB> list = env.soggettiCore.getSoggettiFromTipoAutenticazione(tipiSoggettiGestitiProtocollo, null, tipoAutenticazione, appId, pddTipologiaSoggettoAutenticati);
		
		if( !list.isEmpty() && gestioneErogatori_soggettiAutenticati_escludiSoggettoErogatore ) {
			for (int i = 0; i < list.size(); i++) {
				IDSoggettoDB soggettoCheck = list.get(i);
				if(soggettoCheck.getTipo().equals(erogatore.getTipo()) && soggettoCheck.getNome().equals(erogatore.getNome())) {
					list.remove(i);
					break;
				}
			}
		}
		
		return list;
	}

	public static Boolean getAutenticazioneOpzionale(Object authn) {
		if(authn!=null) {
			if(authn instanceof APIImplAutenticazioneBasic) {
				return ((APIImplAutenticazioneBasic)authn).isOpzionale();
			}
			else if(authn instanceof APIImplAutenticazioneHttps) {
				return ((APIImplAutenticazioneHttps)authn).isOpzionale();
			}
			else if(authn instanceof APIImplAutenticazionePrincipal) {
				return ((APIImplAutenticazionePrincipal)authn).isOpzionale();
			}
			else if(authn instanceof APIImplAutenticazioneCustom) {
				return ((APIImplAutenticazioneCustom)authn).isOpzionale();
			}
		}
		return null;
	}
	
	public static TipoAutenticazionePrincipal getTipoAutenticazionePrincipal(Object authn){
		if(authn!=null && (authn instanceof APIImplAutenticazionePrincipal)) {
			
			APIImplAutenticazionePrincipal authPrincipal = (APIImplAutenticazionePrincipal) authn;
			TipoAutenticazioneEnum tipo = authPrincipal.getTipo();
			
			if(TipoAutenticazioneEnum.PRINCIPAL.equals(tipo)) {
				try {
					return Enums.tipoAutenticazionePrincipalFromRest.get(authPrincipal.getTipoPrincipal());
				} catch (Exception e) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("La configurazione dell'autenticazione principal non è correttamente definita: " + e.getMessage() );
				}
			}
		}
		return null;
	}

	public static List<String> getAutenticazioneParametroList(ErogazioniEnv env,TipoAutenticazioneEnum tipo, Object authn) {
		if(TipoAutenticazioneEnum.HTTP_BASIC.equals(tipo)) {
			
			APIImplAutenticazioneBasic authnBasic = null;
			if(authn!=null) {
				if(authn instanceof APIImplAutenticazioneBasic) {
					authnBasic = (APIImplAutenticazioneBasic) authn;
				}
				else {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("La configurazione dell'autenticazione '"+tipo+"' non è correttamente definita (trovata configurazione '"+authn.getClass().getName()+"')"  );
				}
			}
			
			if(authnBasic!=null) {
				
				List<Proprieta> listConfig = new ArrayList<>();
        		if(authnBasic.isForward()!=null) {
        			Proprieta propertyAutenticazione = new Proprieta();
					propertyAutenticazione.setNome(ParametriAutenticazioneBasic.CLEAN_HEADER_AUTHORIZATION);
        			if(authnBasic.isForward()) {	
        				propertyAutenticazione.setValore(ParametriAutenticazioneBasic.CLEAN_HEADER_AUTHORIZATION_FALSE);
        			}
        			else {
        				propertyAutenticazione.setValore(ParametriAutenticazioneBasic.CLEAN_HEADER_AUTHORIZATION_TRUE);
        			}
        			listConfig.add(propertyAutenticazione);
        		}
        		
        		if(!listConfig.isEmpty()) {
        			return env.stationCore.getParametroAutenticazione(Enums.tipoAutenticazioneFromRest.get(tipo).toString(), listConfig);
        		}
			}
			
		}
		else if(TipoAutenticazioneEnum.PRINCIPAL.equals(tipo)) {
			
			APIImplAutenticazionePrincipal authnPrincipal = null;
			if(authn!=null) {
				if(authn instanceof APIImplAutenticazionePrincipal) {
					authnPrincipal = (APIImplAutenticazionePrincipal) authn;
				}
				else {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("La configurazione dell'autenticazione '"+tipo+"' non è correttamente definita (trovata configurazione '"+authn.getClass().getName()+"')"  );
				}
			}
			
			if(authnPrincipal!=null) {	
        		
        		TipoAutenticazionePrincipal autenticazionePrincipal = Enums.tipoAutenticazionePrincipalFromRest.get(authnPrincipal.getTipoPrincipal());
        		Proprieta propTipoAuthn = new Proprieta();
        		propTipoAuthn.setNome(ParametriAutenticazionePrincipal.TIPO_AUTENTICAZIONE);
        		propTipoAuthn.setValore(autenticazionePrincipal.getValue());
        		
        		List<Proprieta> listConfig = new ArrayList<>();
        		listConfig.add(propTipoAuthn);
        		switch (autenticazionePrincipal) {
				case CONTAINER:
				case INDIRIZZO_IP:
				case INDIRIZZO_IP_X_FORWARDED_FOR:
					break;
        		case HEADER:
					if(authnPrincipal.getNome()==null) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non è stato indicato il nome di un header http per l'autenticazione principal '"+authnPrincipal.getTipoPrincipal()+"' indicata");
					}
					Proprieta propertyAutenticazione = new Proprieta();
					propertyAutenticazione.setNome(ParametriAutenticazionePrincipal.NOME);
					propertyAutenticazione.setValore(authnPrincipal.getNome());
					listConfig.add(propertyAutenticazione);
					break;
				case FORM:
					if(authnPrincipal.getNome()==null) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non è stato indicato il nome di un parametro della url per l'autenticazione principal '"+authnPrincipal.getTipoPrincipal()+"' indicata");
					}
					propertyAutenticazione = new Proprieta();
					propertyAutenticazione.setNome(ParametriAutenticazionePrincipal.NOME);
					propertyAutenticazione.setValore(authnPrincipal.getNome());
					listConfig.add(propertyAutenticazione);
					break;
				case URL:
					if(authnPrincipal.getPattern()==null) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non è stata fornita una espressione regolare per l'autenticazione principal '"+authnPrincipal.getTipoPrincipal()+"' indicata");
					}
					propertyAutenticazione = new Proprieta();
					propertyAutenticazione.setNome(ParametriAutenticazionePrincipal.PATTERN);
					propertyAutenticazione.setValore(authnPrincipal.getPattern());
					listConfig.add(propertyAutenticazione);
					break;
				case TOKEN:
					if(authnPrincipal.getToken()==null) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non è stato indicato il token claim, da cui estrarre il principal, per l'autenticazione '"+authnPrincipal.getTipoPrincipal()+"' indicata");
					}
					if(TipoAutenticazionePrincipalToken.CUSTOM.equals(authnPrincipal.getToken())) {
						if(authnPrincipal.getNome()==null) {
							throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non è stato indicato il nome del token claim, da cui estrarre il principal, per l'autenticazione '"+authnPrincipal.getTipoPrincipal()+"' indicata");
						}
					}
					propertyAutenticazione = new Proprieta();
					propertyAutenticazione.setNome(ParametriAutenticazionePrincipal.TOKEN_CLAIM);
					switch (authnPrincipal.getToken()) {
					case SUBJECT:
						propertyAutenticazione.setValore(ParametriAutenticazionePrincipal.TOKEN_CLAIM_SUBJECT);	
						break;
					case CLIENTID:
						propertyAutenticazione.setValore(ParametriAutenticazionePrincipal.TOKEN_CLAIM_CLIENT_ID);	
						break;
					case USERNAME:
						propertyAutenticazione.setValore(ParametriAutenticazionePrincipal.TOKEN_CLAIM_USERNAME);	
						break;
					case EMAIL:
						propertyAutenticazione.setValore(ParametriAutenticazionePrincipal.TOKEN_CLAIM_EMAIL);	
						break;
					case CUSTOM:
						propertyAutenticazione.setValore(ParametriAutenticazionePrincipal.TOKEN_CLAIM_CUSTOM);	
						break;
					}
					listConfig.add(propertyAutenticazione);
					
					if(TipoAutenticazionePrincipalToken.CUSTOM.equals(authnPrincipal.getToken())) {
						propertyAutenticazione = new Proprieta();
						propertyAutenticazione.setNome(ParametriAutenticazionePrincipal.NOME);
						propertyAutenticazione.setValore(authnPrincipal.getNome());
						listConfig.add(propertyAutenticazione);
					}
					
					break;
				}
        		
        		if(authnPrincipal.isForward()!=null) {
        			
        			Proprieta propertyAutenticazione = new Proprieta();
					propertyAutenticazione.setNome(ParametriAutenticazionePrincipal.CLEAN_PRINCIPAL);
        			if(authnPrincipal.isForward()) {
        				propertyAutenticazione.setValore(ParametriAutenticazionePrincipal.CLEAN_PRINCIPAL_FALSE);
        			}
        			else {
        				propertyAutenticazione.setValore(ParametriAutenticazionePrincipal.CLEAN_PRINCIPAL_TRUE);
        			}
        			listConfig.add(propertyAutenticazione);
        		}
        		
        		if(!listConfig.isEmpty()) {
        			return env.stationCore.getParametroAutenticazione(tipo.toString(), listConfig);
        		}
        	}
			else {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Autenticazione principal non correttamente formata");
			}
		}
		return null;
	}

	public static final void serviziCheckData(	
			TipoOperazione tipoOp,
			ErogazioniEnv env,
			AccordoServizioParteComuneSintetico as,
			AccordoServizioParteSpecifica asps,
			Optional<IdSoggetto> fruitore,
			APIImpl impl

			) throws Exception {
		
		boolean generaPortaApplicativa = !fruitore.isPresent();
		IdSoggetto erogatore = new IdSoggetto( new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore()));
		erogatore.setId(asps.getIdSoggetto());
		
		 if (impl == null) { 
			 impl = new APIImpl();
			 impl.setConnettore(new Connettore());
		 }
		
		 boolean accordoPrivato = as.getPrivato()!=null && as.getPrivato();		
         
         List<PortTypeSintetico> ptList = AccordiServizioParteSpecificaUtilities.getListaPortTypes(as, env.apsCore, env.apsHelper);
         
         String[] ptArray =  ptList.stream()
         		.map( p -> p.getNome() )
         		.toArray(String[]::new);
		
		 // Determino i soggetti compatibili
        Search searchSoggetti = new Search(true);
		searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, env.tipo_protocollo);
		boolean fruizioniEscludiSoggettoFruitore = false;
		
		// In caso di Fruizione i soggetti erogatori compatibili sono determinati dalla configurazione di GovWay (tutti, escluso erogatore, esterni)
		if ( fruitore.isPresent() ) {
			ConfigurazioneCore confCore = new ConfigurazioneCore(env.stationCore);
			PortaDelegataSoggettiErogatori confFruizioneErogatori = confCore.getConfigurazioneGenerale().getMultitenant().getFruizioneSceltaSoggettiErogatori();
			
			if (confFruizioneErogatori == PortaDelegataSoggettiErogatori.SOGGETTI_ESTERNI) {
				searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE);
			}
			else if ( confFruizioneErogatori == PortaDelegataSoggettiErogatori.ESCLUDI_SOGGETTO_FRUITORE ) {
				fruizioniEscludiSoggettoFruitore = true;
			}
			
		} // In caso di erogazione invece possiamo solo assegnare soggetti appartenenti a una porta di tipo operativo. 
		else {
			searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_DOMINIO, SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE);
		}
		
		List<Soggetto> listSoggetti = env.soggettiCore.soggettiRegistroList(null, searchSoggetti);
		
		final boolean escludiFruitore = fruizioniEscludiSoggettoFruitore;
		String[] soggettiCompatibili = listSoggetti.stream()
				.filter( s -> generaPortaApplicativa || ( !escludiFruitore || s.getId() != fruitore.get().getId() ) )
				.map( s -> s.getId().toString())
				.toArray(String[]::new);
		
		if (soggettiCompatibili.length == 0) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non ci sono soggetti compatibili per erogare il servizio");
		}
		
		
		// Determino la lista Api
		List<AccordoServizioParteComuneSintetico> listaAPI = AccordiServizioParteSpecificaUtilities.getListaAPI(
				env.tipo_protocollo,
				env.userLogin,
				env.apsCore, 
				env.apsHelper
			);
		
        String[] accordiList =  listaAPI.stream()
        		.map( a -> a.getId().toString() )
        		.toArray(String[]::new);
        
        final Connettore conn = impl.getConnettore();
        final ConnettoreConfigurazioneHttps httpsConf 	 = conn.getAutenticazioneHttps();
        final ConnettoreConfigurazioneHttpBasic	httpConf	 = conn.getAutenticazioneHttp();
        
	    final String endpointtype = httpsConf != null ? TipiConnettore.HTTPS.getNome() : TipiConnettore.HTTP.getNome();
	    

    	final Properties parametersPOST = null;
		org.openspcoop2.core.registry.Connettore conTmp = null;
		List<ExtendedConnettore> listExtendedConnettore = 
				ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, env.apsHelper, 
							parametersPOST, false, endpointtype); // uso endpointtype per capire se è la prima volta che entro
	
        
        final ConnettoreConfigurazioneHttpsClient httpsClient = evalnull( () -> httpsConf.getClient() );
      	final ConnettoreConfigurazioneHttpsServer httpsServer = evalnull( () -> httpsConf.getServer() );
      	final ConnettoreConfigurazioneProxy 	  proxy   	  = conn.getProxy();
      	final ConnettoreConfigurazioneTimeout	  timeoutConf = conn.getTempiRisposta();
    	final String tokenPolicy = conn.getTokenPolicy(); 
      	final boolean autenticazioneToken = tokenPolicy!=null;

        
		final boolean httpsstato = httpsClient != null;
		final boolean http_stato = conn.getAutenticazioneHttp() != null;
		final boolean proxy_enabled = conn.getProxy() != null;
		final boolean tempiRisposta_enabled = conn.getTempiRisposta() != null; 
		
		String httpskeystore = null;
		if ( httpsClient != null ) {
			if ( httpsClient.getKeystorePath() != null || httpsClient.getKeystoreTipo() != null ) {
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI;  
			}
			else
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
		}

        // Autenticazione e autorizzazione sono opzionali nel json.
        final OneOfAPIImplAutorizzazione authz = impl.getAutorizzazione();
        final OneOfAPIImplAutenticazione authn = impl.getAutenticazione();
        
        // Vincolo rilasciato
//        // Se sono in modalità SPCoop non posso specificare l'autenticazione
//        if ( env.profilo == ProfiloEnum.SPCOOP && generaPortaApplicativa && authn != null ) {
//        	throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non è possibile specificare l'autenticazione per un servizio spcoop");
//        }
        
        FonteEnum ruoliFonte = FonteEnum.QUALSIASI;
        String erogazioneRuolo = null;
        boolean isRichiedente = false;
        boolean isRuoli = false;
        String statoAutorizzazione = null;
        TipoAutenticazionePrincipal autenticazionePrincipal = null;
        List<String> autenticazioneParametroList = null;
        if(authn!=null) {
        	autenticazionePrincipal = getTipoAutenticazionePrincipal(authn); 
        	autenticazioneParametroList = getAutenticazioneParametroList(env, authn.getTipo(), authn);
        }
        final Boolean autenticazioneOpzionale = getAutenticazioneOpzionale(authn); // gestisce authn se null
        AllAnyEnum ruoliRichiesti = null;
        
        if ( generaPortaApplicativa && as.getServiceBinding() == ServiceBinding.SOAP && authz != null && authz.getTipo() != null ) {
	        switch ( authz.getTipo() ) {
	        case ABILITATO:
	        	
	        	if(authz instanceof APIImplAutorizzazioneAbilitataNew) {
	        	
	        		APIImplAutorizzazioneAbilitataNew authzAbilitata = (APIImplAutorizzazioneAbilitataNew) authz;
	        		
		        	if(authzAbilitata.getRuoliFonte()!=null) {
		        		ruoliFonte = authzAbilitata.getRuoliFonte();
		        	}
		         	erogazioneRuolo = authzAbilitata.getRuolo();
		         	isRichiedente = authzAbilitata.isRichiedente();
		         	isRuoli = authzAbilitata.isRuoli();
		         	statoAutorizzazione = AutorizzazioneUtilities.STATO_ABILITATO;
		         	ruoliRichiesti = authzAbilitata.getRuoliRichiesti();
	        	}
	        	else {
	        		throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione dell'autorizzazione '"+authz.getClass().getName()+"' non compatibile con il tipo impostato '"+authz.getTipo()+"'");
	        	}
	        	break;
	        case XACML_POLICY:
	        	
	        	if(authz instanceof APIImplAutorizzazioneXACML) {
		        	
	        		APIImplAutorizzazioneXACML authzXacml = (APIImplAutorizzazioneXACML) authz;
	        	
		        	if (authzXacml.getPolicy() == null) {
		        		throw FaultCode.RICHIESTA_NON_VALIDA.toException("Campo obbligatorio 'policy' non presente nell'autorizzazione indicata");
		        	}
		        	if(authzXacml.getRuoliFonte()!=null) {
		        		ruoliFonte = authzXacml.getRuoliFonte();
		        	}
		        	statoAutorizzazione = AutorizzazioneUtilities.STATO_XACML_POLICY;
	        	}
	        	else {
	        		throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione dell'autorizzazione '"+authz.getClass().getName()+"' non compatibile con il tipo impostato '"+authz.getTipo()+"'");
	        	}
	        	break;
	        case DISABILITATO:
	        	statoAutorizzazione = AutorizzazioneUtilities.STATO_DISABILITATO;
	        	break;
	        case CUSTOM:
		    	throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo di autorizzazione '"+authz.getTipo()+"' non utilizzabile");
	        }
        }
        
        if (isRichiedente) {
        	
        	final APIImplAutorizzazioneAbilitataNew configAuthz_final = (APIImplAutorizzazioneAbilitataNew) authz;
                    	
            // Se ho abilitata l'autorizzazione puntuale, devo aver anche abilitata l'autenticazione
        	if ( env.isSupportatoAutenticazioneSoggetti && 
        			(authn == null || authn.getTipo() == TipoAutenticazioneEnum.DISABILITATO) ) {
        		if(!generaPortaApplicativa || !env.isSupportatoAutorizzazioneRichiedenteSenzaAutenticazioneErogazione) {
        			throw FaultCode.RICHIESTA_NON_VALIDA.toException(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ABILITARE_AUTENTICAZIONE_PER_AUTORIZZAZIONE_PUNTUALE);
        		}
        	}
       	
        	if ( !StringUtils.isEmpty(configAuthz_final.getSoggetto()) ) {
        		
        		CredenzialeTipo credTipo = evalnull( () -> Enums.credenzialeTipoFromTipoAutenticazione.get(authn.getTipo()) );
        		Boolean appId = null; // TODO-APIKEY
        		Optional<String> soggettoCompatibile = getSoggettiCompatibiliAutorizzazione(credTipo, appId, env.idSoggetto, env)
        	        	.stream()
        	        	.map( IDSoggettoDB::getNome )
        	        	.filter( s -> s.equals( configAuthz_final.getSoggetto() ) )
        	        	.findAny();
        	
        		//Se ho scelto un soggetto, questo deve esistere ed essere compatibile con il profilo di autenticazione
	        	if ( !soggettoCompatibile.isPresent() ) {
	        		throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il soggetto " + configAuthz_final.getSoggetto() + " scelto per l'autorizzazione puntuale non esiste o non è compatibile con la modalità di autenticazione scelta");
	        	}
        	}
        	
        }
        
        if (isRuoli) {
        	
        	final APIImplAutorizzazioneAbilitataNew configAuthz_final = (APIImplAutorizzazioneAbilitataNew) authz;
            
        	if (!StringUtils.isEmpty(configAuthz_final.getRuolo())) {
	        	RuoliCore ruoliCore = new RuoliCore(env.stationCore);
	        	// Il ruolo deve esistere
	        	org.openspcoop2.core.registry.Ruolo regRuolo = null;
				try {
					regRuolo = ruoliCore.getRuolo(configAuthz_final.getRuolo());
				} catch (DriverConfigurazioneException e) {	}
				
				if (regRuolo == null) {
					throw FaultCode.NOT_FOUND.toException("Non esiste nessun ruolo con nome " + configAuthz_final.getRuolo());
				}
        	}
        }
        
	
        final BinaryParameter xamlPolicy = new BinaryParameter();
        if(authz instanceof APIImplAutorizzazioneXACML) {
        	APIImplAutorizzazioneXACML authzXacml = (APIImplAutorizzazioneXACML) authz;
        	xamlPolicy.setValue(authzXacml.getPolicy());
        }
        xamlPolicy.setName(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);
        
        
        StringBuilder inUsoMessage = new StringBuilder();
		
		if ( AccordiServizioParteSpecificaUtilities.alreadyExists(
				env.apsCore, 
				env.apsHelper, 
				erogatore.getId(), 
				env.idServizioFactory.getIDServizioFromAccordo(asps),
				env.idAccordoFactory.getUriFromAccordo(as),
				evalnull( () -> fruitore.get().getTipo() ),
				evalnull( () -> fruitore.get().getNome() ),
				env.tipo_protocollo,
				asps.getVersioneProtocollo(),
				asps.getPortType(),
				! generaPortaApplicativa,
				generaPortaApplicativa,
				inUsoMessage
			) ) {
			throw FaultCode.CONFLITTO.toException(StringEscapeUtils.unescapeHtml( inUsoMessage.toString() ));
		}

		/*final String tipoAutorString =	AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString( authz.getTipo(),
				isPuntuale,
				isRuoli,
				false,
				ServletUtils.isCheckBoxEnabled(autorizzazioneScope),		
				autorizzazione_tokenOptions,					// Questo è il token claims
				tipoRuoloFonte									// RuoliFonte: Qualsiasi, Registro, Esterna
			);*/
		
        if (! env.apsHelper.serviziCheckData(            		
        		tipoOp,
        		soggettiCompatibili,		
        		accordiList, 		 		
        		asps.getNome(),				// oldnome
        		asps.getTipo(),				// oldtipo
        		asps.getVersione(),			// oldversione
        		asps.getNome(),
        		asps.getTipo(),
        		erogatore.getId().toString(), 	// idSoggErogatore
        		erogatore.getNome(),
        		erogatore.getTipo(),
        		as.getId().toString(),
        		env.apcCore.toMessageServiceBinding(as.getServiceBinding()),
        		"no",  //servcorr,
        		endpointtype, // endpointtype determinarlo dal connettore,
        		impl.getConnettore().getEndpoint(),
        		null, 	// nome JMS
        		null, 	// tipo JMS,
        		evalnull( () -> httpConf.getUsername() ),	
        		evalnull( () -> httpConf.getPassword() ), 
        		null,   // initcont JMS,
        		null,   // urlpgk JMS,
        		null,   // provurl JMS 
        		null,   // connfact JMS
        		null, 	// sendas JMS, 
        		new BinaryParameter(),		//  wsdlimpler
        		new BinaryParameter(),		//  wsdlimplfru
        		BaseHelper.evalorElse( () -> asps.getId().toString(), "0"), 	
        		asps.getVersioneProtocollo(),	//  Il profilo è la versione protocollo in caso spcoop, è quello del soggetto erogatore.
        		asps.getPortType(),
        		ptArray,
				accordoPrivato,
				false, 	// this.privato,
        		conn.getEndpoint(),	// httpsurl, 
        		evalnull( () ->  httpsConf.getTipologia().toString() ),				// I valori corrispondono con con org.openspcoop2.utils.transport.http.SSLConstants
        		BaseHelper.evalorElse( () -> httpsConf.isHostnameVerifier().booleanValue(), false ),				// this.httpshostverify,
				evalnull( () -> httpsServer.getTruststorePath() ),				// this.httpspath
				evalnull( () -> httpsServer.getTruststoreTipo().toString() ),		// this.httpstipo,
				evalnull( () -> httpsServer.getTruststorePassword() ),			// this.httpspwd,
				evalnull( () -> httpsServer.getAlgoritmo() ),					// this.httpsalgoritmo
				httpsstato,	//
        		httpskeystore, 	
        		"", //  this.httpspwdprivatekeytrust
        		evalnull( () -> httpsClient.getKeystorePath() ),					// httpspathkey
        		evalnull( () -> httpsClient.getKeystoreTipo().toString() ),	 		// httpstipokey, coincide con ConnettoriCostanti.TIPOLOGIE_KEYSTORE
        		evalnull( () -> httpsClient.getKeystorePassword() ), 	 		// httpspwdkey
        		evalnull( () -> httpsClient.getKeyPassword() ),	 				// httpspwdprivatekey
        		evalnull( () -> httpsClient.getAlgoritmo() ),					// httpsalgoritmokey
        		evalnull( () -> httpsClient.getKeyAlias() ),					// httpsKeyAlias
        		evalnull( () -> httpsServer.getTruststoreCrl() ),					// httpsTrustStoreCRLs
        		null, 								// tipoconn Da debug = null.	
        		asps.getVersione().toString(), //as.getVersione().toString(), 		// Versione aspc
        		false,								// validazioneDocumenti Da debug = false
        		null,								// Da Codice console
        		ServletUtils.boolToCheckBoxStatus(http_stato),	// "yes" se utilizziamo http.
        		ServletUtils.boolToCheckBoxStatus(proxy_enabled),			
    			evalnull( () -> proxy.getHostname() ),
    			evalnull( () -> proxy.getPorta().toString() ),
    			evalnull( () -> proxy.getUsername() ),
    			evalnull( () -> proxy.getPassword() ),				
    			ServletUtils.boolToCheckBoxStatus(tempiRisposta_enabled), 
    			evalnull( () -> timeoutConf.getConnectionTimeout().toString()),		// this.tempiRisposta_connectionTimeout, 
    			evalnull( () -> timeoutConf.getConnectionReadTimeout().toString()),  // this.tempiRisposta_readTimeout, 
    			evalnull( () -> timeoutConf.getTempoMedioRisposta().toString()),		// this.tempiRisposta_tempoMedioRisposta,
        		ServletUtils.boolToCheckBoxStatus(false),	// opzioniAvanzate 
        		"",		// transfer_mode, 
        		"",		// transfer_mode_chunk_size, 
        		"",		// redirect_mode, 
        		"",		// redirect_max_hop, 
        		null,	// requestOutputFileName,
        		null,	// requestOutputFileNameHeaders, 
        		null,	// requestOutputParentDirCreateIfNotExists, 
        		null,	// requestOutputOverwriteIfExists,
        		null,	// responseInputMode,
        		null,	// responseInputFileName,
        		null,	// responseInputFileNameHeaders,
        		null,	// responseInputDeleteAfterRead,
        		null,	// responseInputWaitTime,
        		null,	// erogazioneSoggetto, Come da codice console.
        		erogazioneRuolo,	//, non viene utilizzato.
        		evalnull( () -> Enums.tipoAutenticazioneFromRest.get(authn.getTipo()).toString() ),		// erogazioneAutenticazione
        		evalnull( () -> ServletUtils.boolToCheckBoxStatus( autenticazioneOpzionale ) ),					// erogazioneAutenticazioneOpzionale
        		autenticazionePrincipal, // erogazioneAutenticazionePrincipal
        		autenticazioneParametroList, // erogazioneAutenticazioneParametroList
        		statoAutorizzazione,					   	// erogazioneAutorizzazione QUESTO E' lo STATO dell'autorizzazione
        		ServletUtils.boolToCheckBoxStatus( isRichiedente ), 			// erogazioneAutorizzazioneAutenticati, 
        		ServletUtils.boolToCheckBoxStatus( isRuoli ),				// erogazioneAutorizzazioneRuoli, 
                Enums.ruoloTipologiaFromRest.get(ruoliFonte).toString(),				// erogazioneAutorizzazioneRuoliTipologia,
                ruoliRichiesti!=null ? ruoliRichiesti.toString() : null,  	// erogazioneAutorizzazioneRuoliMatch,  AllAnyEnum == RuoloTipoMatch
        		env.isSupportatoAutenticazioneSoggetti,	
        		generaPortaApplicativa,													// generaPACheckSoggetto (Un'erogazione genera una porta applicativa)
        		listExtendedConnettore,
        		null,																	// fruizioneServizioApplicativo
        		null,																	// Ruolo fruizione, non viene utilizzato.
        		evalnull( () -> Enums.tipoAutenticazioneFromRest.get(authn.getTipo()).toString() ),  // fruizioneAutenticazione 
        		evalnull( () -> ServletUtils.boolToCheckBoxStatus( autenticazioneOpzionale ) ), 			// fruizioneAutenticazioneOpzionale
        		autenticazionePrincipal, // fruizioneAutenticazionePrincipal
        		autenticazioneParametroList, // fruizioneAutenticazioneParametroList
        		statoAutorizzazione,											// fruizioneAutorizzazione 	
        		ServletUtils.boolToCheckBoxStatus( isRichiedente ), 				// fruizioneAutorizzazioneAutenticati, 
        		ServletUtils.boolToCheckBoxStatus( isRuoli ), 					// fruizioneAutorizzazioneRuoli,
                Enums.ruoloTipologiaFromRest.get(ruoliFonte).toString(), 					// fruizioneAutorizzazioneRuoliTipologia,
                ruoliRichiesti!=null ? ruoliRichiesti.toString() : null, 		// fruizioneAutorizzazioneRuoliMatch,
        		env.tipo_protocollo, 
        		xamlPolicy, 																//allegatoXacmlPolicy,
        		"",
        		evalnull( () -> fruitore.get().getTipo() ),		// tipoFruitore 
        		evalnull( () -> fruitore.get().getNome() ),			// nomeFruitore
        		autenticazioneToken,
        		tokenPolicy,
        		false, // erogazioneServizioApplicativoServerEnabled, TODO quando si aggiunge applicativo server
    			null // rogazioneServizioApplicativoServer
        	)) {
        	throw FaultCode.RICHIESTA_NON_VALIDA.toException( StringEscapeUtils.unescapeHtml( env.pd.getMessage()) );
        }
        
        
		
	}
	
	public static final boolean connettoreCheckData(
			final Connettore conn,
			final ErogazioniEnv env,
			boolean erogazione
			) throws Exception {
		
		
		final boolean http_stato  = conn.getAutenticazioneHttp() != null;
		final boolean proxy_enabled = conn.getProxy() != null;
		final boolean tempiRisposta_enabled = conn.getTempiRisposta() != null; 
		
	    final ConnettoreConfigurazioneHttps httpsConf 	 = conn.getAutenticazioneHttps();
	    final ConnettoreConfigurazioneHttpBasic	httpConf	 = conn.getAutenticazioneHttp();

	    final String endpointtype = httpsConf != null ? TipiConnettore.HTTPS.getNome() : TipiConnettore.HTTP.getNome();
	    
	    final Properties parametersPOST = null;
		org.openspcoop2.core.registry.Connettore conTmp = null;
		List<ExtendedConnettore> listExtendedConnettore = 
				ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, env.apsHelper, 
							parametersPOST, false, endpointtype);

	    final ConnettoreConfigurazioneHttpsClient httpsClient = evalnull( () -> httpsConf.getClient() );
	  	final ConnettoreConfigurazioneHttpsServer httpsServer = evalnull( () -> httpsConf.getServer() );
	  	final ConnettoreConfigurazioneProxy 	  proxy   	  = conn.getProxy();
	  	final ConnettoreConfigurazioneTimeout	  timeoutConf = conn.getTempiRisposta();
	  	final String tokenPolicy = conn.getTokenPolicy(); 
      	final boolean autenticazioneToken = tokenPolicy!=null;
	  	
		final boolean httpsstato = httpsClient != null;	// Questo è per l'autenticazione client.
	  	 
		String httpskeystore = null;
		if ( httpsClient != null ) {
			if ( httpsClient.getKeystorePath() != null || httpsClient.getKeystoreTipo() != null ) {
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI;  
			}
			else
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
		}
        			    
		return env.saHelper.endPointCheckData(
				env.tipo_protocollo,
				erogazione,
				endpointtype,
				conn.getEndpoint(),
				null,	// nome
				null,	// tipo
				evalnull( () -> httpConf.getUsername() ),
				evalnull( () -> httpConf.getPassword() ),
				null,	// this.initcont, 
				null,	// this.urlpgk,
				null,	// provurl jms,
				null, 	// connfact, 
				null,	// sendas, 
				conn.getEndpoint(), 													// this.httpsurl, 
				evalnull( () -> httpsConf.getTipologia().toString() ),				// this.httpstipologia
				BaseHelper.evalorElse( () -> httpsConf.isHostnameVerifier().booleanValue(), false ),	// this.httpshostverify,
				evalnull( () -> httpsServer.getTruststorePath() ),				// this.httpspath
				evalnull( () -> httpsServer.getTruststoreTipo().toString() ),	// this.httpstipo,
				evalnull( () -> httpsServer.getTruststorePassword() ),			// this.httpspwd,
				evalnull( () -> httpsServer.getAlgoritmo() ),					// this.httpsalgoritmo
				httpsstato, 
				httpskeystore,
				"",																		// httpspwdprivatekeytrust, 
				evalnull( () -> httpsClient.getKeystorePath() ),				// pathkey
				evalnull( () -> httpsClient.getKeystoreTipo().toString() ), 		// this.httpstipokey
				evalnull( () -> httpsClient.getKeystorePassword() ),			// this.httpspwdkey 
				evalnull( () -> httpsClient.getKeyPassword() ),				// this.httpspwdprivatekey,  
				evalnull( () -> httpsClient.getAlgoritmo() ),				// this.httpsalgoritmokey, 
        		evalnull( () -> httpsClient.getKeyAlias() ),					// httpsKeyAlias
        		evalnull( () -> httpsServer.getTruststoreCrl() ),					// httpsTrustStoreCRLs
				null,																//	tipoconn (personalizzato)
				ServletUtils.boolToCheckBoxStatus( http_stato ),										 	//autenticazioneHttp,
				ServletUtils.boolToCheckBoxStatus( proxy_enabled ),	
				evalnull( () -> proxy.getHostname() ),
				evalnull( () -> proxy.getPorta().toString() ),
				evalnull( () -> proxy.getUsername() ),
				evalnull( () -> proxy.getPassword() ),
				ServletUtils.boolToCheckBoxStatus( tempiRisposta_enabled ),	
				evalnull( () -> timeoutConf.getConnectionTimeout().toString()),	// this.tempiRisposta_connectionTimeout, 
				evalnull( () -> timeoutConf.getConnectionReadTimeout().toString()), //null,	// this.tempiRisposta_readTimeout, 
				evalnull( () -> timeoutConf.getTempoMedioRisposta().toString()),	// this.tempiRisposta_tempoMedioRisposta,
				"no",	// this.opzioniAvanzate, 
				"", 	// this.transfer_mode, 
				"", 	// this.transfer_mode_chunk_size, 
				"", 	// this.redirect_mode, 
				"", 	// this.redirect_max_hop,
				null,	// this.requestOutputFileName,
				null,	// this.requestOutputFileNameHeaders,
				null,	// this.requestOutputParentDirCreateIfNotExists,
				null,	// this.requestOutputOverwriteIfExists,
				null,	// this.responseInputMode, 
				null,	// this.responseInputFileName, 
				null,	// this.responseInputFileNameHeaders, 
				null,	// this.responseInputDeleteAfterRead, 
				null,	// this.responseInputWaitTime,
				autenticazioneToken,
				tokenPolicy,
				listExtendedConnettore,
        		false, // erogazioneServizioApplicativoServerEnabled, TODO quando si aggiunge applicativo server
    			null // rogazioneServizioApplicativoServer
			);
	}
		
	public static final org.openspcoop2.core.registry.Connettore buildConnettoreRegistro(final ErogazioniEnv env, final Connettore conn) throws Exception {
		final org.openspcoop2.core.registry.Connettore regConnettore = new org.openspcoop2.core.registry.Connettore();
		fillConnettoreRegistro(regConnettore, env, conn, "");
		return regConnettore;
	}

	
	public static final void fillConnettoreRegistro(
			final org.openspcoop2.core.registry.Connettore regConnettore,
			final ErogazioniEnv env,
			final Connettore conn,
			final String oldConnT
			) throws Exception {
		
		
		final boolean proxy_enabled = conn.getProxy() != null;
		final boolean tempiRisposta_enabled = conn.getTempiRisposta() != null; 
		
	    final ConnettoreConfigurazioneHttps httpsConf 	 = conn.getAutenticazioneHttps();
	    final ConnettoreConfigurazioneHttpBasic	httpConf	 = conn.getAutenticazioneHttp();

	    final String endpointtype = httpsConf != null ? TipiConnettore.HTTPS.getNome() : TipiConnettore.HTTP.getNome();
	    
	    final Properties parametersPOST = null;
		org.openspcoop2.core.registry.Connettore conTmp = null;
		List<ExtendedConnettore> listExtendedConnettore = 
				ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, env.apsHelper, 
							parametersPOST, false, endpointtype);

	    final ConnettoreConfigurazioneHttpsClient httpsClient = evalnull( () -> httpsConf.getClient() );
	  	final ConnettoreConfigurazioneHttpsServer httpsServer = evalnull( () -> httpsConf.getServer() );
	  	final ConnettoreConfigurazioneProxy 	  proxy   	  = conn.getProxy();
	  	final ConnettoreConfigurazioneTimeout	  timeoutConf = conn.getTempiRisposta();
	  	final String tokenPolicy = conn.getTokenPolicy(); 
	  	
		final boolean httpsstato = httpsClient != null;	// Questo è per l'autenticazione client.
	  	 
		String httpskeystore = null;
		if ( httpsClient != null ) {
			if ( httpsClient.getKeystorePath() != null || httpsClient.getKeystoreTipo() != null ) {
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI;  
			}
			else
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
		}
        			    
	     
		env.apsHelper.fillConnettore(
				regConnettore, 
				"false",				// this.connettoreDebug,
				endpointtype, 			// endpointtype
				oldConnT,						// oldConnT
				"",						// tipoConn Personalizzato
				conn.getEndpoint(),		// this.url,
				null,	// this.nome,
				null, 	// this.tipo,
				evalnull( () -> httpConf.getUsername() ),
				evalnull( () -> httpConf.getPassword() ),
				null,	// this.initcont, 
				null,	// this.urlpgk,
				conn.getEndpoint(),	// this.url, 
				null,	// this.connfact,
				null,	// this.sendas,
				conn.getEndpoint(), 													// this.httpsurl, 
				evalnull( () -> httpsConf.getTipologia().toString() ),				// this.httpstipologia
				BaseHelper.evalorElse( () -> httpsConf.isHostnameVerifier().booleanValue(), false ),	// this.httpshostverify,
				evalnull( () -> httpsServer.getTruststorePath() ),				// this.httpspath
				evalnull( () -> httpsServer.getTruststoreTipo().toString() ),	// this.httpstipo,
				evalnull( () -> httpsServer.getTruststorePassword() ),			// this.httpspwd,
				evalnull( () -> httpsServer.getAlgoritmo() ),					// this.httpsalgoritmo
				httpsstato,
				httpskeystore,			// this.httpskeystore, 
				"",																	//  this.httpspwdprivatekeytrust
				evalnull( () -> httpsClient.getKeystorePath() ),				// pathkey
				evalnull( () -> httpsClient.getKeystoreTipo().toString() ), 		// this.httpstipokey
				evalnull( () -> httpsClient.getKeystorePassword() ),			// this.httpspwdkey 
				evalnull( () -> httpsClient.getKeyPassword() ),				// this.httpspwdprivatekey,  
				evalnull( () -> httpsClient.getAlgoritmo() ),				// this.httpsalgoritmokey,
        		evalnull( () -> httpsClient.getKeyAlias() ),					// httpsKeyAlias
        		evalnull( () -> httpsServer.getTruststoreCrl() ),					// httpsTrustStoreCRLs
			
				ServletUtils.boolToCheckBoxStatus( proxy_enabled ),	
				evalnull( () -> proxy.getHostname() ),
				evalnull( () -> proxy.getPorta().toString() ),
				evalnull( () -> proxy.getUsername() ),
				evalnull( () -> proxy.getPassword() ),
				
				ServletUtils.boolToCheckBoxStatus( tempiRisposta_enabled ),	
				evalnull( () -> timeoutConf.getConnectionTimeout().toString()),	// this.tempiRisposta_connectionTimeout, 
				evalnull( () -> timeoutConf.getConnectionReadTimeout().toString()), //null,	// this.tempiRisposta_readTimeout, 
				evalnull( () -> timeoutConf.getTempoMedioRisposta().toString()),	// this.tempiRisposta_tempoMedioRisposta,
				"no",	// this.opzioniAvanzate, 
				"", 	// this.transfer_mode, 
				"", 	// this.transfer_mode_chunk_size, 
				"", 	// this.redirect_mode, 
				"", 	// this.redirect_max_hop,
				null,	// this.requestOutputFileName,
				null,	// this.requestOutputFileNameHeaders,
				null,	// this.requestOutputParentDirCreateIfNotExists,
				null,	// this.requestOutputOverwriteIfExists,
				null,	// this.responseInputMode, 
				null,	// this.responseInputFileName, 
				null,	// this.responseInputFileNameHeaders, 
				null,	// this.responseInputDeleteAfterRead, 
				null,	// this.responseInputWaitTime,
				tokenPolicy,
				listExtendedConnettore);			
	}
	
	
	/*
	 * Metodo utilizzato per costruire un connettore del registro dato un connettore della API.
	 * Questo metodo è utilizzato sia in caso di costruzione di un nuovo connettore sia in caso di
	 * modifica di un vecchio connettore.
	 * 
	 *  @param oldConnType: Nel caso di un connettore da aggiornare, va specificato il tipo del vecchio connettore
	 */
	
	
	public static final void fillConnettoreConfigurazione(
			final org.openspcoop2.core.config.Connettore regConnettore,
			final ErogazioniEnv env,
			final Connettore conn,
			final String oldConnType
			) throws Exception {
		
		final boolean proxy_enabled = conn.getProxy() != null;
		final boolean tempiRisposta_enabled = conn.getTempiRisposta() != null; 
		
	    final ConnettoreConfigurazioneHttps httpsConf 	 = conn.getAutenticazioneHttps();
	    final ConnettoreConfigurazioneHttpBasic	httpConf	 = conn.getAutenticazioneHttp();

	    final String endpointtype = httpsConf != null ? TipiConnettore.HTTPS.getNome() : TipiConnettore.HTTP.getNome();
	    
	    final Properties parametersPOST = null;
		org.openspcoop2.core.registry.Connettore conTmp = null;
		List<ExtendedConnettore> listExtendedConnettore = 
				ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, env.apsHelper, 
							parametersPOST, false, endpointtype);

	    final ConnettoreConfigurazioneHttpsClient httpsClient = evalnull( () -> httpsConf.getClient() );
	  	final ConnettoreConfigurazioneHttpsServer httpsServer = evalnull( () -> httpsConf.getServer() );
	  	final ConnettoreConfigurazioneProxy 	  proxy   	  = conn.getProxy();
	  	final ConnettoreConfigurazioneTimeout	  timeoutConf = conn.getTempiRisposta();
	  	final String tokenPolicy = conn.getTokenPolicy(); 
	  	
		final boolean httpsstato = httpsClient != null;	// Questo è per l'autenticazione client.
	  	 
		String httpskeystore = null;
		if ( httpsClient != null ) {
			if ( httpsClient.getKeystorePath() != null || httpsClient.getKeystoreTipo() != null ) {
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI;  
			}
			else
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
		}
        			    
	    	     
		env.apsHelper.fillConnettore(
				regConnettore, 
				"false",				// this.connettoreDebug,
				endpointtype, 			// endpointtype
				oldConnType,			// oldConnT
				"",						// tipoConn Personalizzato
				conn.getEndpoint(),		// this.url,
				null,	// this.nome,
				null, 	// this.tipo,
				evalnull( () -> httpConf.getUsername() ),
				evalnull( () -> httpConf.getPassword() ),
				null,	// this.initcont, 
				null,	// this.urlpgk,
				conn.getEndpoint(),	// this.url, 
				null,	// this.connfact,
				null,	// this.sendas,
				conn.getEndpoint(), 													// this.httpsurl, 
				evalnull( () -> httpsConf.getTipologia().toString() ),				// this.httpstipologia
				BaseHelper.evalorElse( () -> httpsConf.isHostnameVerifier().booleanValue(), false ),	// this.httpshostverify,
				evalnull( () -> httpsServer.getTruststorePath() ),				// this.httpspath
				evalnull( () -> httpsServer.getTruststoreTipo().toString() ),	// this.httpstipo,
				evalnull( () -> httpsServer.getTruststorePassword() ),			// this.httpspwd,
				evalnull( () -> httpsServer.getAlgoritmo() ),					// this.httpsalgoritmo
				httpsstato,
				httpskeystore,			// this.httpskeystore, 
				"",																	//  this.httpspwdprivatekeytrust
				evalnull( () -> httpsClient.getKeystorePath() ),				// pathkey
				evalnull( () -> httpsClient.getKeystoreTipo().toString() ), 		// this.httpstipokey
				evalnull( () -> httpsClient.getKeystorePassword() ),			// this.httpspwdkey 
				evalnull( () -> httpsClient.getKeyPassword() ),				// this.httpspwdprivatekey,  
				evalnull( () -> httpsClient.getAlgoritmo() ),				// this.httpsalgoritmokey,
        		evalnull( () -> httpsClient.getKeyAlias() ),					// httpsKeyAlias
        		evalnull( () -> httpsServer.getTruststoreCrl() ),					// httpsTrustStoreCRLs
			
				ServletUtils.boolToCheckBoxStatus( proxy_enabled ),	
				evalnull( () -> proxy.getHostname() ),
				evalnull( () -> proxy.getPorta().toString() ),
				evalnull( () -> proxy.getUsername() ),
				evalnull( () -> proxy.getPassword() ),
				
				ServletUtils.boolToCheckBoxStatus( tempiRisposta_enabled ),	
				evalnull( () -> timeoutConf.getConnectionTimeout().toString()),	// this.tempiRisposta_connectionTimeout, 
				evalnull( () -> timeoutConf.getConnectionReadTimeout().toString()), //null,	// this.tempiRisposta_readTimeout, 
				evalnull( () -> timeoutConf.getTempoMedioRisposta().toString()),	// this.tempiRisposta_tempoMedioRisposta,
				"no",	// this.opzioniAvanzate, 
				"", 	// this.transfer_mode, 
				"", 	// this.transfer_mode_chunk_size, 
				"", 	// this.redirect_mode, 
				"", 	// this.redirect_max_hop,
				null,	// this.requestOutputFileName,
				null,	// this.requestOutputFileNameHeaders,
				null,	// this.requestOutputParentDirCreateIfNotExists,
				null,	// this.requestOutputOverwriteIfExists,
				null,	// this.responseInputMode, 
				null,	// this.responseInputFileName, 
				null,	// this.responseInputFileNameHeaders, 
				null,	// this.responseInputDeleteAfterRead, 
				null,	// this.responseInputWaitTime,
				tokenPolicy,
				listExtendedConnettore);			
	}
	

	
	
	public static final void createAps(
			ErogazioniEnv env,
			AccordoServizioParteSpecifica asps,
			org.openspcoop2.core.registry.Connettore regConnettore,
			APIImpl ero,
			boolean alreadyExists,
			boolean generaPortaApplicativa) throws Exception
	{
		final boolean generaPortaDelegata = !generaPortaApplicativa;
		
		// defaults
		if (ero.getAutenticazione() == null) {
			APIImplAutenticazioneHttps https = new APIImplAutenticazioneHttps();
			https.setTipo(TipoAutenticazioneEnum.HTTPS);
			https.setOpzionale(false);
			ero.setAutenticazione(https);
		}
		
		final OneOfAPIImplAutorizzazione authz = ero.getAutorizzazione();
        final OneOfAPIImplAutenticazione authn = ero.getAutenticazione();
        
        final AccordoServizioParteComuneSintetico as = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());

        FonteEnum ruoliFonte = FonteEnum.QUALSIASI;
        
        boolean isRichiedente = false;
        boolean isRuoli = false;
        String statoAutorizzazione = AutorizzazioneUtilities.STATO_DISABILITATO;
        String soggettoAutenticato = null;
        AllAnyEnum ruoliRichiesti = null;
        String ruolo = null;
        
        TipoAutenticazionePrincipal autenticazionePrincipal = null;
        List<String> autenticazioneParametroList = null;
        if(authn!=null) {
        	autenticazionePrincipal = getTipoAutenticazionePrincipal(authn); 
        	autenticazioneParametroList = getAutenticazioneParametroList(env, authn.getTipo(), authn);
        }
        final Boolean autenticazioneOpzionale = getAutenticazioneOpzionale(authn); // gestisce authn se null
                
        if ( evalnull( () -> authz.getTipo() ) != null) {
		    switch (authz.getTipo()) {
		    case ABILITATO:	
		    	
		    	if(authz instanceof APIImplAutorizzazioneAbilitataNew) {
		    		
		    		APIImplAutorizzazioneAbilitataNew authzAbilitata = (APIImplAutorizzazioneAbilitataNew) authz;
		    		
			    	if(authzAbilitata.getRuoliFonte()!=null) {
		        		ruoliFonte = authzAbilitata.getRuoliFonte();
			    	}
		         	isRichiedente = authzAbilitata.isRichiedente();
		         	isRuoli = authzAbilitata.isRuoli();
		         	statoAutorizzazione = AutorizzazioneUtilities.STATO_ABILITATO;
		         	if ( authzAbilitata.getSoggetto() != null )
		         		soggettoAutenticato = new IDSoggetto(env.tipo_soggetto, authzAbilitata.getSoggetto()).toString();
		         	ruoliRichiesti = authzAbilitata.getRuoliRichiesti();
		         	ruolo = authzAbilitata.getRuolo();
			    }
	        	else {
	        		throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione dell'autorizzazione '"+authz.getClass().getName()+"' non compatibile con il tipo impostato '"+authz.getTipo()+"'");
	        	}
	         	
		    	break;
		    case XACML_POLICY:
		    	
		    	if(authz instanceof APIImplAutorizzazioneXACML) {
		    		
		    		APIImplAutorizzazioneXACML authzXacml = (APIImplAutorizzazioneXACML) authz;
		    	
			    	if (authzXacml.getPolicy() == null) {
			    		throw FaultCode.RICHIESTA_NON_VALIDA.toException("Campo obbligatorio 'policy' non presente nell'autorizzazione indicata");
		        	}
			    	if(authzXacml.getRuoliFonte()!=null) {
		        		ruoliFonte = authzXacml.getRuoliFonte();
			    	}
			    	statoAutorizzazione = AutorizzazioneUtilities.STATO_XACML_POLICY;
		    	}
	        	else {
	        		throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione dell'autorizzazione '"+authz.getClass().getName()+"' non compatibile con il tipo impostato '"+authz.getTipo()+"'");
	        	}
		    	break;
		    case DISABILITATO:
		    	statoAutorizzazione = AutorizzazioneUtilities.STATO_DISABILITATO;
		    	break;
		    case CUSTOM:
		    	throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo di autorizzazione '"+authz.getTipo()+"' non utilizzabile");
		    }
        }
        
    	
        
    	final IDServizio idServizio = env.idServizioFactory.getIDServizioFromValues(asps.getTipo(), asps.getNome(), new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore()), asps.getVersione()); 
	               
        BinaryParameter xamlPolicy = new BinaryParameter();
        if(authz instanceof APIImplAutorizzazioneXACML) {
        	APIImplAutorizzazioneXACML authzXacml = (APIImplAutorizzazioneXACML) authz;
        	xamlPolicy.setValue(authzXacml.getPolicy());
        }
        xamlPolicy.setName(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);
                
        AccordiServizioParteSpecificaUtilities.create(
        		asps,
        		alreadyExists, 
				idServizio,											// idServizio,
				env.idSoggetto.toIDSoggetto(),						// idFruitore,
				env.tipo_protocollo,								// this.tipoProtocollo,
				env.apcCore.toMessageServiceBinding(as.getServiceBinding()), 
				env.idSoggetto.getId(), 							// Questo è l'id del soggetto a cui associare la porta delegata\applicativa
				regConnettore, 
				generaPortaApplicativa,										// generaPortaApplicativa,
				generaPortaDelegata, 										// generaPortaDelegata
				evalnull( () -> Enums.tipoAutenticazioneFromRest.get(authn.getTipo()).toString() ),											// erogazioneAutenticazione
        		evalnull( () -> ServletUtils.boolToCheckBoxStatus( autenticazioneOpzionale ) ), 														// erogazioneAutenticazioneOpzionale
        		autenticazionePrincipal, // erogazioneAutenticazionePrincipal
        		autenticazioneParametroList, // erogazioneAutenticazioneParametroList
        		statoAutorizzazione,	// autorizzazione, è lo STATO 	
				ServletUtils.boolToCheckBoxStatus( isRichiedente ),			// 	autorizzazioneAutenticati,
				ServletUtils.boolToCheckBoxStatus( isRuoli ),				//	autorizzazioneRuoli,
		    	Enums.ruoloTipologiaFromRest.get(ruoliFonte).toString(),				//	erogazioneAutorizzazioneRuoliTipologia
		    	ruoliRichiesti!=null ? ruoliRichiesti.toString() : null,			// 	autorizzazioneRuoliMatch
				null,	// servizioApplicativo Come da Debug, 
				ruolo,	// ruolo: E' il ruolo scelto nella label "Ruolo" 
		    	soggettoAutenticato,	// soggettoAutenticato TODO BISOGNA AGGIUNGERE IL CONTROLLO CHE IL SOGGETTO AUTENTICATO SIA NEL REGISTRO 
				null,	// autorizzazione_tokenOptions, 
				null,	// autorizzazioneScope, 
				null,	// scope, 
				null,	// autorizzazioneScopeMatch,
				xamlPolicy,	// allegatoXacmlPolicy,
				StatoFunzionalita.DISABILITATO.toString(),	// gestioneToken
				null,	// gestioneTokenPolicy,
				null,	// gestioneTokenOpzionale, 
				null,	// gestioneTokenValidazioneInput, 
				null,	// gestioneTokenIntrospection, 
				null,	// gestioneTokenUserInfo, 
				null,	// gestioneTokenTokenForward, 
				null,	// autenticazioneTokenIssuer, 
				null,	// autenticazioneTokenClientId,
				null,	// autenticazioneTokenSubject, 
				null,	// autenticazioneTokenUsername, 
				null,	// autenticazioneTokenEMail, 
				new ProtocolProperties(), // this.protocolProperties, 
				ConsoleOperationType.ADD, 
				env.apsCore, 
				env.erogazioniHelper,
        		null // nomeSAServer TODO quando si aggiunge applicativo server
			);

		
	}

	/*public static final AccordoServizioParteSpecifica getServizio(String tipo, String nome, Integer versione, IDSoggetto idErogatore, ErogazioniEnv env) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		
		
		return getServizio(nome, versione, tipo, idErogatore, env);
	}*/
	
	
	public static final AccordoServizioParteSpecifica getServizio(String tipo, String nome, Integer versione, IDSoggetto idErogatore, ErogazioniEnv env) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		
		if ( tipo == null ) {
			tipo = env.protocolFactoryMgr._getServiceTypes().get(env.tipo_protocollo).get(0);
		} 		
        final IDServizio idAps = env.idServizioFactory.getIDServizioFromValues(tipo, nome, idErogatore, versione);
        
        return env.apsCore.getServizio(idAps, true);
	}
	
	
	public static final boolean isErogazione(AccordoServizioParteSpecifica asps, ErogazioniEnv env) throws DriverConfigurazioneException, DriverRegistroServiziException {
		List<IDPortaApplicativa> pApplicative = env.paCore.getIDPorteApplicativeAssociate(env.idServizioFactory.getIDServizioFromAccordo(asps));
		return pApplicative != null && !pApplicative.isEmpty();
	}
	
	public static final boolean isFruizione(AccordoServizioParteSpecifica asps, IDSoggetto idFruitore, ErogazioniEnv env) throws DriverConfigurazioneException, DriverRegistroServiziException {
		List<IDPortaDelegata> pDelegate = env.pdCore.getIDPorteDelegateAssociate(env.idServizioFactory.getIDServizioFromAccordo(asps), idFruitore);
		return pDelegate != null && !pDelegate.isEmpty();
	}
	
	
	
	
	
	public static final AccordoServizioParteSpecifica getServizioIfErogazione(String tipo, String nome, Integer versione, IDSoggetto idErogatore, ErogazioniEnv env) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		
		final AccordoServizioParteSpecifica ret = getServizio(tipo, nome, versione, idErogatore, env);
		
		try {
			if (!isErogazione(ret,env)) {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
		
		return ret;
	}
	
	
	public static final AccordoServizioParteSpecifica getServizioIfFruizione(String tipo, String nome, Integer versione, IDSoggetto idErogatore, IDSoggetto idFruitore, ErogazioniEnv env) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		
		final AccordoServizioParteSpecifica ret = getServizio(tipo, nome, versione, idErogatore, env);
		
		try {
			if (!isFruizione(ret,idFruitore,env))
				return null;
		} catch (Exception e) {
			return null;
		}
		
		return ret;
	}
	
//	public static final AllegatoGenerico deserializeAllegatoGenerico(Object o) {
//		AllegatoGenerico ret = deserializeDefault(o, AllegatoGenerico.class);
//		if (StringUtils.isEmpty(ret.getNome()) || ret.getDocumento() == null) {
//			throw FaultCode.RICHIESTA_NON_VALIDA.toException(AllegatoGenerico.class.getName() + ": Indicare i campi obbligatori 'nome' e 'documento'");
//		}
//		return ret;
//	}
//	
//	public static final AllegatoSpecificaSemiformale deserializeAllegatoSpecificaSemiformale(Object o) {
//		AllegatoSpecificaSemiformale ret = deserializeDefault(o, AllegatoSpecificaSemiformale.class);
//		if (StringUtils.isEmpty(ret.getNome()) || ret.getDocumento() == null || ret.getTipo() == null) {
//			throw FaultCode.RICHIESTA_NON_VALIDA.toException(AllegatoSpecificaSemiformale.class.getName() + ": Indicare i campi obbligatori 'nome', 'documento' e 'tipo'");
//		}
//		return ret;
//	}
	
	
		
	public static final Documento implAllegatoToDocumento(ApiImplAllegato body, AccordoServizioParteSpecifica asps) throws InstantiationException, IllegalAccessException {
		
		Documento documento = new Documento();
		documento.setIdProprietarioDocumento(asps.getId());
		
		RuoloAllegatoAPIImpl ruoloAllegato = body.getAllegato().getRuolo();
		
		documento.setRuolo(RuoliDocumento.valueOf(
				Enums.ruoliDocumentoFromApiImpl
				.get( ruoloAllegato).toString() )
				.toString()
			);
		
		switch (ruoloAllegato) {
		case ALLEGATO:
			
			if(! (body.getAllegato() instanceof ApiImplAllegatoGenerico)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'allegato fornito possiede una struttura dati '"+body.getAllegato().getClass().getName()+"' non compatibile con il ruolo '"+ruoloAllegato+"'");
			}
			ApiImplAllegatoGenerico allegatoGenerico = (ApiImplAllegatoGenerico) body.getAllegato();
			documento.setFile(allegatoGenerico.getNome());
			documento.setByteContenuto(allegatoGenerico.getDocumento());
			documento.setTipo( evalnull( () -> allegatoGenerico.getNome().substring( allegatoGenerico.getNome().lastIndexOf('.')+1, allegatoGenerico.getNome().length())) );
			break;

		case SPECIFICASEMIFORMALE:
			
			if(! (body.getAllegato() instanceof ApiImplAllegatoSpecificaSemiformale)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'allegato fornito possiede una struttura dati '"+body.getAllegato().getClass().getName()+"' non compatibile con il ruolo '"+ruoloAllegato+"'");
			}
			ApiImplAllegatoSpecificaSemiformale allegatoSS = (ApiImplAllegatoSpecificaSemiformale) body.getAllegato();
			documento.setFile(allegatoSS.getNome());
			documento.setByteContenuto(allegatoSS.getDocumento());
			if(allegatoSS.getTipoSpecifica()==null) {
				documento.setTipo(TipiDocumentoSemiformale.LINGUAGGIO_NATURALE.toString()); // default
			}
			else {
				TipoSpecificaSemiformaleEnum tipoAllegato = (TipoSpecificaSemiformaleEnum) allegatoSS.getTipoSpecifica();
				documento.setTipo( evalnull( () -> Enums.tipoDocumentoSemiFormaleFromSpecifica.get(tipoAllegato) ).toString() );
			}
			break;
			
		case SPECIFICALIVELLOSERVIZIO:
			
			if(! (body.getAllegato() instanceof ApiImplAllegatoSpecificaLivelloServizio)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'allegato fornito possiede una struttura dati '"+body.getAllegato().getClass().getName()+"' non compatibile con il ruolo '"+ruoloAllegato+"'");
			}
			ApiImplAllegatoSpecificaLivelloServizio allegatoLS = (ApiImplAllegatoSpecificaLivelloServizio) body.getAllegato();
			documento.setFile(allegatoLS.getNome());
			documento.setByteContenuto(allegatoLS.getDocumento());
			if(allegatoLS.getTipoSpecifica()==null) {
				documento.setTipo(TipiDocumentoLivelloServizio.WSLA.toString()); // default
			}
			else {
				TipoSpecificaLivelloServizioEnum tipoAllegato = (TipoSpecificaLivelloServizioEnum) allegatoLS.getTipoSpecifica();
				documento.setTipo( evalnull( () -> Enums.tipoDocumentoLivelloServizioFromSpecifica.get(tipoAllegato).toString()) );
			}
			break;
			
		case SPECIFICASICUREZZA:
			
			if(! (body.getAllegato() instanceof ApiImplAllegatoSpecificaSicurezza)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'allegato fornito possiede una struttura dati '"+body.getAllegato().getClass().getName()+"' non compatibile con il ruolo '"+ruoloAllegato+"'");
			}
			ApiImplAllegatoSpecificaSicurezza allegatoSSec = (ApiImplAllegatoSpecificaSicurezza) body.getAllegato();
			documento.setFile(allegatoSSec.getNome());
			documento.setByteContenuto(allegatoSSec.getDocumento());
			if(allegatoSSec.getTipoSpecifica()==null) {
				documento.setTipo(TipiDocumentoSicurezza.LINGUAGGIO_NATURALE.toString()); // default
			}
			else {
				TipoSpecificaSicurezzaEnum tipoAllegato = (TipoSpecificaSicurezzaEnum) allegatoSSec.getTipoSpecifica();
				documento.setTipo( evalnull( () -> Enums.tipoDocumentoSicurezzaFromSpecifica.get(tipoAllegato).toString()) );
			}
			break;
		}

		return documento;
	}
	
	
	public static final ApiImplAllegato documentoToImplAllegato(Documento doc) {
		ApiImplAllegato ret = new ApiImplAllegato();
	    
		RuoloAllegatoAPIImpl ruoloAllegato = Enums.ruoliApiImplFromDocumento.get( RuoliDocumento.valueOf(doc.getRuolo()));
		
		switch (ruoloAllegato) {
		case ALLEGATO: {
			
			ApiImplAllegatoGenerico allegatoGenerico = new ApiImplAllegatoGenerico();
			allegatoGenerico.setRuolo(ruoloAllegato);
			allegatoGenerico.setDocumento(doc.getByteContenuto());
			allegatoGenerico.setNome(doc.getFile());
			ret.setAllegato(allegatoGenerico);
			break;
		}
		
		case SPECIFICASEMIFORMALE: {
			
			ApiImplAllegatoSpecificaSemiformale allegatoSS = new ApiImplAllegatoSpecificaSemiformale();
			allegatoSS.setRuolo(ruoloAllegato);
			allegatoSS.setDocumento(doc.getByteContenuto());
			allegatoSS.setNome(doc.getFile());
			TipiDocumentoSemiformale tipo = Enum.valueOf(TipiDocumentoSemiformale.class, doc.getTipo());
			allegatoSS.setTipoSpecifica((Helper.apiEnumToGovway(tipo, TipoSpecificaSemiformaleEnum.class)));
			ret.setAllegato(allegatoSS);
			break;
		}
			
		case SPECIFICALIVELLOSERVIZIO:{
			
			ApiImplAllegatoSpecificaLivelloServizio allegatoSL = new ApiImplAllegatoSpecificaLivelloServizio();
			allegatoSL.setRuolo(ruoloAllegato);
			allegatoSL.setDocumento(doc.getByteContenuto());
			allegatoSL.setNome(doc.getFile());
			TipiDocumentoLivelloServizio tipo = Enum.valueOf(TipiDocumentoLivelloServizio.class, doc.getTipo());
			allegatoSL.setTipoSpecifica((Helper.apiEnumToGovway(tipo, TipoSpecificaLivelloServizioEnum.class)));
			ret.setAllegato(allegatoSL);
			break;
		}
		
		case SPECIFICASICUREZZA:{
			
			ApiImplAllegatoSpecificaSicurezza allegatoSSec = new ApiImplAllegatoSpecificaSicurezza();
			allegatoSSec.setRuolo(ruoloAllegato);
			allegatoSSec.setDocumento(doc.getByteContenuto());
			allegatoSSec.setNome(doc.getFile());
			TipiDocumentoSicurezza tipo = Enum.valueOf(TipiDocumentoSicurezza.class, doc.getTipo());
			allegatoSSec.setTipoSpecifica((Helper.apiEnumToGovway(tipo, TipoSpecificaSicurezzaEnum.class)));
			ret.setAllegato(allegatoSSec);
			break;
		}
		
		}
	    
	    return ret;
		
	}
	
	public static final ApiImplAllegatoItem ImplAllegatoToItem(ApiImplAllegato allegato) {
		ApiImplAllegatoItem ret = new ApiImplAllegatoItem();
		
		RuoloAllegatoAPIImpl ruoloAllegato = allegato.getAllegato().getRuolo();
		
		switch (ruoloAllegato) {
		case ALLEGATO: {
			
			ApiImplAllegatoGenerico allegatoGenerico = (ApiImplAllegatoGenerico) allegato.getAllegato();
			
			ApiImplAllegatoItemGenerico allegatoGenericoItem = new ApiImplAllegatoItemGenerico();
			allegatoGenericoItem.setRuolo(ruoloAllegato);
			allegatoGenericoItem.setNome(allegatoGenerico.getNome());
			ret.setAllegato(allegatoGenericoItem);
			break;
		}
		
		case SPECIFICASEMIFORMALE: {
			
			ApiImplAllegatoSpecificaSemiformale allegatoSS = (ApiImplAllegatoSpecificaSemiformale) allegato.getAllegato();
			
			ApiImplAllegatoItemSpecificaSemiformale allegatoItemSS = new ApiImplAllegatoItemSpecificaSemiformale();
			allegatoItemSS.setRuolo(ruoloAllegato);
			allegatoItemSS.setNome(allegatoSS.getNome());
			allegatoItemSS.setTipoSpecifica(allegatoSS.getTipoSpecifica());
			ret.setAllegato(allegatoItemSS);
			break;
		}
			
		case SPECIFICALIVELLOSERVIZIO:{
			
			ApiImplAllegatoSpecificaLivelloServizio allegatoSL = (ApiImplAllegatoSpecificaLivelloServizio) allegato.getAllegato();
			
			ApiImplAllegatoItemSpecificaLivelloServizio allegatoItemSL = new ApiImplAllegatoItemSpecificaLivelloServizio();
			allegatoItemSL.setRuolo(ruoloAllegato);
			allegatoItemSL.setNome(allegatoSL.getNome());
			allegatoItemSL.setTipoSpecifica(allegatoSL.getTipoSpecifica());
			ret.setAllegato(allegatoItemSL);
			break;
		}
		
		case SPECIFICASICUREZZA:{
			
			ApiImplAllegatoSpecificaSicurezza allegatoSSec = (ApiImplAllegatoSpecificaSicurezza) allegato.getAllegato();
			
			ApiImplAllegatoItemSpecificaSicurezza allegatoItemSSec = new ApiImplAllegatoItemSpecificaSicurezza();
			allegatoItemSSec.setRuolo(ruoloAllegato);
			allegatoItemSSec.setNome(allegatoSSec.getNome());
			allegatoItemSSec.setTipoSpecifica(allegatoSSec.getTipoSpecifica());
			ret.setAllegato(allegatoItemSSec);
			break;
		}
		
		}

		return ret;
		
	}
	
	public static void createAllegatoAsps(ApiImplAllegato body, final ErogazioniEnv env,
			AccordoServizioParteSpecifica asps) 
					throws Exception {

		Documento documento = ErogazioniApiHelper.implAllegatoToDocumento(body, asps);
		
		SerialiableFormFile filewrap = new SerialiableFormFile(documento.getFile(), documento.getByteContenuto());
		env.requestWrapper.overrideParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_RUOLO, documento.getRuolo());			

		boolean documentoUnivocoIndipendentementeTipo = true;
		if (env.archiviCore.existsDocumento(documento,ProprietariDocumento.servizio,documentoUnivocoIndipendentementeTipo)) {
			throw FaultCode.CONFLITTO.toException("Allegato con nome " + documento.getFile() + " già presente per l'erogazione");
		}
		
		if (!env.apsHelper.serviziAllegatiCheckData(TipoOperazione.ADD,filewrap,documento,env.protocolFactory)) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
		}
		
		switch (body.getAllegato().getRuolo()) {
			case ALLEGATO:
				asps.addAllegato(documento);
				break;
			case SPECIFICASEMIFORMALE:
				asps.addSpecificaSemiformale(documento);
				break;
			case SPECIFICASICUREZZA:
				asps.addSpecificaSicurezza(documento);
				break;
			case SPECIFICALIVELLOSERVIZIO:
				asps.addSpecificaLivelloServizio(documento);
				break;
		}

		env.apsCore.performUpdateOperation(env.userLogin, false, asps);
	}
	
	public static final Optional<Documento> getDocumento(List<Documento> list, String name) {
		return list.stream().filter(d -> d.getFile().equals(name)).findFirst();
		
	}
	
	public static final Optional<Long> getIdDocumento(List<Documento> list, String name) {
		return list.stream().filter(d -> d.getFile().equals(name)).findFirst().map( d -> d.getId());
	}
	
	public static final <T> Stream<T> mergeStreams(Stream<T>[] streams) {
		Stream<T> tmp = streams[0];
		
		for(int i = 1; i< streams.length; i++) {
			tmp = Stream.concat(tmp, streams[i]);
		}
		
		return tmp;
	}
	
	public static final Optional<Long> getIdDocumento(String nomeAllegato, final AccordoServizioParteSpecifica asps) {
		Optional<Long> idDoc = ErogazioniApiHelper.getIdDocumento(asps.getSpecificaSemiformaleList(), nomeAllegato);
		
		if (!idDoc.isPresent()) idDoc = getIdDocumento(asps.getSpecificaLivelloServizioList(), nomeAllegato);
		if (!idDoc.isPresent()) idDoc = getIdDocumento(asps.getAllegatoList(), nomeAllegato);
		if (!idDoc.isPresent()) idDoc = getIdDocumento(asps.getSpecificaSicurezzaList(), nomeAllegato);
		return idDoc;
	}
	
	
	public static void updateAllegatoAsps(ApiImplAllegato body, String nomeAllegato, ErogazioniEnv env, AccordoServizioParteSpecifica asps)
			throws Exception {
		Documento oldDoc = BaseHelper.supplyOrNotFound( () -> env.archiviCore.getDocumento(nomeAllegato, null, null, asps.getId(), false, ProprietariDocumento.servizio), "Allegato di nome " + nomeAllegato); 
		
		Documento newDoc = ErogazioniApiHelper.implAllegatoToDocumento(body, asps);
		newDoc.setId(oldDoc.getId());
		newDoc.setOraRegistrazione(new Date());
		
		if (! StringUtils.equals(newDoc.getRuolo(), oldDoc.getRuolo())) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non puoi modificare il ruolo di un allegato");
		}

		SerialiableFormFile filewrap = new SerialiableFormFile(newDoc.getFile(), newDoc.getByteContenuto());
		env.requestWrapper.overrideParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_RUOLO, newDoc.getRuolo());
		
		if (!env.apsHelper.serviziAllegatiCheckData(TipoOperazione.CHANGE,filewrap,newDoc,env.protocolFactory)) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
		}
		
		AccordiServizioParteSpecificaUtilities.sostituisciDocumentoAsps(asps, oldDoc, newDoc);


		env.apsCore.performUpdateOperation(env.userLogin, false, asps);
	}
	
	public static final void updateInformazioniGenerali(ApiImplInformazioniGenerali body, ErogazioniEnv env, AccordoServizioParteSpecifica asps, boolean isErogazione) throws Exception{

		AccordoServizioParteComuneSintetico as = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());

		ServiceBinding tipoApi = as.getServiceBinding();
		
		IDServizio oldIDServizioForUpdate = env.idServizioFactory.getIDServizioFromAccordo(asps);
		asps.setOldIDServizioForUpdate(oldIDServizioForUpdate);
		
		asps.setNome(body.getNome());
		if (!StringUtils.isEmpty(body.getTipo())) {
			asps.setTipo(body.getTipo());
		}
		
		if (tipoApi == ServiceBinding.SOAP && !StringUtils.isEmpty(body.getApiSoapServizio())) {
			asps.setPortType(body.getApiSoapServizio());
		}
		
		serviziUpdateCheckData(as, asps, isErogazione, env);
						
		List<Object> oggettiDaAggiornare = AccordiServizioParteSpecificaUtilities.getOggettiDaAggiornare(asps, env.apsCore);
		
		// eseguo l'aggiornamento
		env.apsCore.performUpdateOperation(env.userLogin, false, oggettiDaAggiornare.toArray());
	}
	
	public static final void overrideFruizioneUrlInvocazione(final HttpRequestWrapper wrap, final IDSoggetto idErogatore,
			final IDServizio idServizio, final PortaDelegata pd, final PortaDelegataAzione pdAzione) {
		wrap.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID, pd.getId().toString());
		wrap.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_NOME_PORTA, pd.getNome());			
		wrap.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO, pd.getIdSoggetto().toString());	
		wrap.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE, pdAzione.getPattern());			// Azione è il contenuto del campo pattern o del campo nome, che vengono settati nel campo pattern.
		wrap.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_AZIONE_ID, null);						// Come da debug
		wrap.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_MODE_AZIONE, pdAzione.getIdentificazione().toString() );
		wrap.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_XSD, null);
		wrap.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_INTEGRAZIONE, "");
		wrap.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SOGGETTO_ID, idErogatore.toString());	// Questo è il nome (uri) del soggetto erogatore
		wrap.overrideParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_SERVIZIO_ID, idServizio.toString());
	}
	
	public static final void fillApiImplViewItemWithErogazione(final ErogazioniEnv env, final AccordoServizioParteSpecifica asps, final ApiImplViewItem toFill) {
		try {
			IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);
			AccordoServizioParteComuneSintetico apc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
		
			List<MappingErogazionePortaApplicativa> listaMappingErogazionePortaApplicativa = env.apsCore.mappingServiziPorteAppList(idServizio,asps.getId(), null);
			List<PortaApplicativa> listaPorteApplicativeAssociate = new ArrayList<>();

			String nomePortaDefault = null;
			for(MappingErogazionePortaApplicativa mappinErogazione : listaMappingErogazionePortaApplicativa) {
				if(mappinErogazione.isDefault()) {
					nomePortaDefault = mappinErogazione.getIdPortaApplicativa().getNome();
				}
				listaPorteApplicativeAssociate.add(env.paCore.getPortaApplicativa(mappinErogazione.getIdPortaApplicativa()));
			}
			
			int numeroAbilitate = 0;
			int numeroConfigurazioni = listaMappingErogazionePortaApplicativa.size();
			boolean allActionRedefined = false;
						
			if(listaMappingErogazionePortaApplicativa.size()>1) {
				List<String> azioniL = new ArrayList<>();
				Map<String,String> azioni = env.paCore.getAzioniConLabel(asps, apc, false, true, new ArrayList<String>());
				if(azioni != null && azioni.size() > 0)
					azioniL.addAll(azioni.keySet());
				allActionRedefined = env.erogazioniHelper.allActionsRedefinedMappingErogazione(azioniL, listaMappingErogazionePortaApplicativa);
			}
			
			for (PortaApplicativa paAssociata : listaPorteApplicativeAssociate) {
				boolean statoPA = paAssociata.getStato().equals(StatoFunzionalita.ABILITATO);
				if(statoPA) {
					if(!allActionRedefined || !paAssociata.getNome().equals(nomePortaDefault)) {
						numeroAbilitate ++;
					}
				}
			}
			
			StatoDescrizione stato = getStatoDescrizione(numeroAbilitate, allActionRedefined, numeroConfigurazioni );
			
			fillApiImplViewItemWithAsps(
					env, 
					asps, 
					toFill, 
					getUrlInvocazioneErogazione(asps, env),
					getConnettoreErogazione(idServizio, env.saCore, env.paCore).getProperties().get(CostantiDB.CONNETTORE_HTTP_LOCATION),
					getGestioneCorsFromErogazione(asps, env),
					idServizio.getSoggettoErogatore().getNome(),
					stato.stato,
					stato.descrizione
				);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
			
	}
	
	public static final StatoDescrizione getStatoDescrizione(int numeroAbilitate, boolean allActionRedefined, int numeroConfigurazioni ) {
		String stato_descrizione;
		StatoApiEnum statoApi;
		
		StatoDescrizione ret = new StatoDescrizione();
		
		if(numeroAbilitate == 0) {
			statoApi = StatoApiEnum.ERROR;
			stato_descrizione = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_TUTTE_DISABILITATE_TOOLTIP;
		} else if( 
				(!allActionRedefined && numeroAbilitate == numeroConfigurazioni) 
				||
				(allActionRedefined && numeroAbilitate == (numeroConfigurazioni-1)) // escludo la regola che non viene usata poiche' tutte le azioni sono ridefinite 
				) {
			statoApi = StatoApiEnum.OK;
			stato_descrizione = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_TUTTE_ABILITATE_TOOLTIP; 
		} else  {
			statoApi = StatoApiEnum.WARN;
			stato_descrizione = ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_PARZIALMENTE_ABILITATE_TOOLTIP;
		}
		
		ret.stato = statoApi;
		ret.descrizione = stato_descrizione;
		
		return ret;
	}
	
	
	public static final void fillApiImplViewItemWithFruizione(final ErogazioniEnv env, final AccordoServizioParteSpecifica asps, final IdSoggetto fruitore, final ApiImplViewItem toFill) throws Exception {
		
		IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);
		AccordoServizioParteComuneSintetico apc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
		
		List<MappingFruizionePortaDelegata> listaMappingFruzionePortaDelegata = env.apsCore.serviziFruitoriMappingList(fruitore.getId(), fruitore.toIDSoggetto(), idServizio, null);	
		List<PortaDelegata> listaPorteDelegateAssociate = new ArrayList<>();

		String nomePortaDefault = null;
		for(MappingFruizionePortaDelegata mappingFruizione : listaMappingFruzionePortaDelegata) {
			if(mappingFruizione.isDefault()) {
				nomePortaDefault = mappingFruizione.getIdPortaDelegata().getNome();
			}
			listaPorteDelegateAssociate.add(env.pdCore.getPortaDelegata(mappingFruizione.getIdPortaDelegata()));
		}
		
		int numeroAbilitate = 0;
		int numeroConfigurazioni = listaMappingFruzionePortaDelegata.size();
		boolean allActionRedefined = false;
		 
		if(listaMappingFruzionePortaDelegata.size()>1) {
			List<String> azioniL = new ArrayList<>();
			Map<String,String> azioni = env.pdCore.getAzioniConLabel(asps, apc, false, true, new ArrayList<String>());
			if(azioni != null && azioni.size() > 0)
				azioniL.addAll(azioni.keySet());
			allActionRedefined = env.erogazioniHelper.allActionsRedefinedMappingFruizione(azioniL, listaMappingFruzionePortaDelegata);
		}
		
		for (PortaDelegata pdAssociata : listaPorteDelegateAssociate) {
			boolean statoPD = pdAssociata.getStato().equals(StatoFunzionalita.ABILITATO);
			if(statoPD) {
				if(!allActionRedefined || !pdAssociata.getNome().equals(nomePortaDefault)) {
					numeroAbilitate ++;
				}
			}
		}
		
		StatoDescrizione stato = getStatoDescrizione(numeroAbilitate, allActionRedefined, numeroConfigurazioni );		
		
		try {
			fillApiImplViewItemWithAsps(
					env, 
					asps, 
					toFill, 
					getUrlInvocazioneFruizione(asps, fruitore.toIDSoggetto(), env),
					evalnull( () -> getConnettoreFruizione(asps, fruitore, env).getProperties().get(CostantiDB.CONNETTORE_HTTP_LOCATION) ),
					getGestioneCorsFromFruizione(asps, fruitore.toIDSoggetto(), env),
					fruitore.getNome(),
					stato.stato,
					stato.descrizione
				);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	
	public static final void fillApiImplViewItemWithAsps(
			final ErogazioniEnv env,
			final AccordoServizioParteSpecifica asps,
			final ApiImplViewItem toFill, 
			final String urlInvocazione, 
			final String urlConnettore,
			final CorsConfigurazione gestioneCors,
			final String nomeSoggetto,
			final StatoApiEnum stato,
			final String statoDescrizione ) {

		try {
			final IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);
			final AccordoServizioParteComuneSintetico aspc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
			
			toFill.setTipoServizio(idServizio.getTipo());
			toFill.setNome(asps.getNome());
			toFill.setVersione(asps.getVersione());
			toFill.setSoggetto(nomeSoggetto);	// Questo nelle fruizioni è il fruitore, nelle erogazioni è l'erogatore
			toFill.setApiNome(aspc.getNome());
			toFill.setApiTipo(TipoApiEnum.valueOf(aspc.getServiceBinding().name()));
			toFill.setApiVersione(aspc.getVersione());
			toFill.setProfilo(env.profilo);
			toFill.setConnettore(urlConnettore);		
			toFill.setApiSoapServizio(asps.getPortType());
			toFill.setGestioneCors(Helper.boolToStatoFunzionalita(gestioneCors != null).toString());
			toFill.setUrlInvocazione(urlInvocazione);
			
			if(aspc.getGruppo()!=null && !aspc.getGruppo().isEmpty()) {
				toFill.setApiTags(new ArrayList<>());
				for (GruppoSintetico tag : aspc.getGruppo()) {
					toFill.addApiTagsItem(tag.getNome());
				}
			}
			
			/*if(numeroAbilitate == 0) {
				de.setValue(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_TUTTE_DISABILITATE);
				de.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_TUTTE_DISABILITATE_TOOLTIP);
			} else if( 
					(!allActionRedefined && numeroAbilitate == numeroConfigurazioni) 
					||
					(allActionRedefined && numeroAbilitate == (numeroConfigurazioni-1)) // escludo la regola che non viene usata poiche' tutte le azioni sono ridefinite 
					) {
				de.setValue(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_TUTTE_ABILITATE);
				de.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_TUTTE_ABILITATE_TOOLTIP);
			} else  {
				de.setValue(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_PARZIALMENTE_ABILITATE);
				de.setToolTip(ErogazioniCostanti.ASPS_EROGAZIONI_ICONA_STATO_CONFIGURAZIONI_PARZIALMENTE_ABILITATE_TOOLTIP);
			}*/
			
			toFill.setStato(stato);
			toFill.setStatoDescrizione(statoDescrizione);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}
	
	
	public static final ErogazioneViewItem aspsToErogazioneViewItem(final ErogazioniEnv env, final AccordoServizioParteSpecifica asps) {
		ErogazioneViewItem ret = new ErogazioneViewItem();
		fillApiImplViewItemWithErogazione(env, asps, ret);
		return ret;
	}
	
	
	public static final FruizioneViewItem aspsToFruizioneViewItem(final ErogazioniEnv env, final AccordoServizioParteSpecifica asps, final IdSoggetto fruitore) {

		FruizioneViewItem ret = new FruizioneViewItem();
		try {
			fillApiImplViewItemWithFruizione(env, asps, fruitore, ret);
			
			final IDSoggetto idErogatore = env.idServizioFactory.getIDServizioFromAccordo(asps).getSoggettoErogatore();	
			ret.setErogatore(idErogatore.getNome());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		return ret;
	}
	
	
	public static final void fillApiImplItemFromView(ApiImplViewItem impl, ApiImplItem toFill) {
		toFill.setApiNome(impl.getApiNome());
		toFill.setApiSoapServizio(impl.getApiSoapServizio());
		toFill.setApiTipo(impl.getApiTipo());
		toFill.setApiVersione(impl.getApiVersione());
		toFill.setNome(impl.getNome());
		toFill.setProfilo(impl.getProfilo());
		toFill.setSoggetto(impl.getSoggetto());
		toFill.setStato(impl.getStato());
		toFill.setStatoDescrizione(impl.getStatoDescrizione());
		toFill.setTipoServizio(impl.getTipoServizio());
		toFill.setVersione(impl.getVersione());
		toFill.setApiTags(impl.getApiTags());
	}
	
	
	public static final FruizioneItem fruizioneViewItemToFruizioneItem(FruizioneViewItem fru) {
		FruizioneItem ret = new FruizioneItem();
		fillApiImplItemFromView(fru, ret);
		ret.setErogatore(fru.getErogatore());
		return ret;
	}
	
	
	public static final ErogazioneItem erogazioneViewItemToErogazioneItem(ErogazioneViewItem ero) {
		ErogazioneItem ret = new ErogazioneItem();
		fillApiImplItemFromView(ero, ret);
		
		return ret;		
	}
	
	
	
	public static final void deleteAllegato(String nomeAllegato, final ErogazioniEnv env, final AccordoServizioParteSpecifica asps)
			throws Exception {
		final Optional<Long> idDoc = ErogazioniApiHelper.getIdDocumento(nomeAllegato, asps);
		
		if ( env.delete_404 && !idDoc.isPresent() ) {
			throw FaultCode.NOT_FOUND.toException("Allegato di nome " + nomeAllegato + " non presente."); 
		}
		
		if (idDoc.isPresent()) {
			AccordiServizioParteSpecificaUtilities.deleteAccordoServizioParteSpecificaAllegati(asps, env.userLogin, env.apsCore, env.apsHelper, Arrays.asList(idDoc.get()));
		}
	}
	
	
	public static final CorsConfigurazione getGestioneCorsFromErogazione(AccordoServizioParteSpecifica asps, ErogazioniEnv env) 
			throws DriverConfigurazioneException, DriverRegistroServiziException, DriverConfigurazioneNotFound, CoreException  {
		
		final IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);
		final IDPortaApplicativa idPA = env.paCore.getIDPortaApplicativaAssociataDefault(idServizio);
		final PortaApplicativa pa = env.paCore.getPortaApplicativa(idPA);
		
		return pa.getGestioneCors();
	}
	
	
	public static final CorsConfigurazione getGestioneCorsFromFruizione(AccordoServizioParteSpecifica asps, IDSoggetto fruitore, ErogazioniEnv env) 
			throws DriverRegistroServiziException, DriverConfigurazioneException, DriverConfigurazioneNotFound {
		
		final IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);
		final IDPortaDelegata idPD = env.pdCore.getIDPortaDelegataAssociataDefault(idServizio, fruitore);
		final PortaDelegata pd = env.pdCore.getPortaDelegata(idPD);

		return pd.getGestioneCors();
	}
	
	
	public static final org.openspcoop2.core.config.Connettore getConnettoreErogazione(IDServizio idServizio, ServiziApplicativiCore saCore, PorteApplicativeCore paCore) 
			throws DriverConfigurazioneNotFound, DriverConfigurazioneException, CoreException, DriverRegistroServiziException, DriverRegistroServiziNotFound {
	
		final IDPortaApplicativa idPA = paCore.getIDPortaApplicativaAssociataDefault(idServizio);
		final ServizioApplicativo sa = saCore.getServizioApplicativo(saCore.getIdServizioApplicativo(idServizio.getSoggettoErogatore(), idPA.getNome()));
		        		
		return sa.getInvocazioneServizio().getConnettore();
	}
	
	
	public static final org.openspcoop2.core.registry.Connettore getConnettoreFruizione(AccordoServizioParteSpecifica asps, IdSoggetto fruitore, ErogazioniEnv env) {
		
		return asps.getFruitoreList().stream()
			.filter( f -> {
				try {
					return (new IDSoggetto(f.getTipo(), f.getNome()).equals(fruitore.toIDSoggetto())) && (f.getConnettore() != null);
				} catch (CoreException e) {
					throw new RuntimeException(e);
				}
			})
			.map( f -> {
				org.openspcoop2.core.registry.Connettore conn = f.getConnettore();
				return conn;
			})
			.findFirst()
			.orElse(null);
					
	}
	
	
	public static final String getUrlInvocazioneErogazione(AccordoServizioParteSpecifica asps, ErogazioniEnv env) throws Exception {
		
		
		final IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);
		final IDPortaApplicativa idPA = env.paCore.getIDPortaApplicativaAssociataDefault(idServizio);
		final PortaApplicativa pa = env.paCore.getPortaApplicativa(idPA);
		final AccordoServizioParteComuneSintetico aspc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
		ConfigurazioneCore confCore = new ConfigurazioneCore(env.stationCore);
		
		Configurazione config = confCore.getConfigurazioneGenerale();
		
		ConfigurazioneUrlInvocazione configurazioneUrlInvocazione = null;
		if(config!=null && config.getUrlInvocazione()!=null) {
			configurazioneUrlInvocazione = config.getUrlInvocazione();
		}
		
		UrlInvocazioneAPI urlInvocazioneAPI = UrlInvocazioneAPI.getConfigurazioneUrlInvocazione(configurazioneUrlInvocazione, env.protocolFactory, RuoloContesto.PORTA_APPLICATIVA, 
				ServiceBinding.REST.equals(aspc.getServiceBinding()) ? org.openspcoop2.message.constants.ServiceBinding.REST : org.openspcoop2.message.constants.ServiceBinding.SOAP, 
						pa.getNome(), idServizio.getSoggettoErogatore());		
		return urlInvocazioneAPI.getUrl();
		
	}
	
	public static final String getUrlInvocazioneFruizione(AccordoServizioParteSpecifica asps, IDSoggetto fruitore, ErogazioniEnv env) throws Exception {
		

		final IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);
		final IDPortaDelegata idPD = env.pdCore.getIDPortaDelegataAssociataDefault(idServizio, fruitore);
		final PortaDelegata pd = env.pdCore.getPortaDelegata(idPD);
		final AccordoServizioParteComuneSintetico aspc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
		
		ConfigurazioneCore confCore = new ConfigurazioneCore(env.stationCore);

		Configurazione config = confCore.getConfigurazioneGenerale();
		
		ConfigurazioneUrlInvocazione configurazioneUrlInvocazione = null;
		if(config!=null && config.getUrlInvocazione()!=null) {
			configurazioneUrlInvocazione = config.getUrlInvocazione();
		}
		
		UrlInvocazioneAPI urlInvocazioneAPI = UrlInvocazioneAPI.getConfigurazioneUrlInvocazione(configurazioneUrlInvocazione, env.protocolFactory, RuoloContesto.PORTA_DELEGATA, 
				ServiceBinding.REST.equals(aspc.getServiceBinding()) ? org.openspcoop2.message.constants.ServiceBinding.REST : org.openspcoop2.message.constants.ServiceBinding.SOAP, 
						pd.getNome(), fruitore);		
		return urlInvocazioneAPI.getUrl();
		
	}
	
	public static final DumpConfigurazione buildDumpConfigurazione(RegistrazioneMessaggi dumpConf, boolean isErogazione, ErogazioniEnv env) throws Exception {
		final RegistrazioneMessaggiConfigurazione richiesta = dumpConf.getRichiesta();
		final RegistrazioneMessaggiConfigurazione risposta = dumpConf.getRisposta();

		final String statoDump = dumpConf.getStato().toString(); 
		final String statoDumpRichiesta 		 	= Helper.boolToStatoFunzionalitaConf( evalnull( () -> richiesta.isAbilitato() )).toString();
		final String statoDumpRisposta 				= Helper.boolToStatoFunzionalitaConf( evalnull( () -> risposta.isAbilitato() )).toString();
		final String dumpRichiestaIngressoHeader	= Helper.boolToStatoFunzionalitaConf( evalnull( () -> richiesta.getIngresso().isHeaders() )).toString();
		final String dumpRichiestaIngressoBody 		= Helper.boolToStatoFunzionalitaConf( evalnull( () -> richiesta.getIngresso().isBody() )).toString();
		final String dumpRichiestaIngressoAttachments =  Helper.boolToStatoFunzionalitaConf( evalnull( () -> richiesta.getIngresso().isAttachments()) ).toString();
		final String dumpRichiestaUscitaHeader      = Helper.boolToStatoFunzionalitaConf( evalnull( () -> richiesta.getUscita().isHeaders() )).toString();
		final String dumpRichiestaUscitaBody        = Helper.boolToStatoFunzionalitaConf( evalnull( () -> richiesta.getUscita().isBody() )).toString();         
		final String dumpRichiestaUscitaAttachments = Helper.boolToStatoFunzionalitaConf( evalnull( () -> richiesta.getUscita().isAttachments() )).toString();  
		final String dumpRispostaIngressoHeader     = Helper.boolToStatoFunzionalitaConf( evalnull( () -> risposta.getIngresso().isHeaders() )).toString();     
		final String dumpRispostaIngressoBody       = Helper.boolToStatoFunzionalitaConf( evalnull( () -> risposta.getIngresso().isBody() )).toString();        
		final String dumpRispostaIngressoAttachments= Helper.boolToStatoFunzionalitaConf( evalnull( () -> risposta.getIngresso().isAttachments() )).toString(); 
		final String dumpRispostaUscitaHeader       = Helper.boolToStatoFunzionalitaConf( evalnull( () -> risposta.getUscita().isHeaders() )).toString();       
		final String dumpRispostaUscitaBody         = Helper.boolToStatoFunzionalitaConf( evalnull( () -> risposta.getUscita().isBody() )).toString();          
		final String dumpRispostaUscitaAttachments  = Helper.boolToStatoFunzionalitaConf( evalnull( () -> risposta.getUscita().isAttachments() )).toString();
		
		DumpConfigurazione ret = new DumpConfigurazione();
		
		// Caso porta applicativa
		if (isErogazione) {
			if (!env.paHelper.checkDataConfigurazioneDump(TipoOperazione.OTHER,
					true,	// showStato
					statoDump,
					false, 	// showRealtime, 
					StatoFunzionalita.ABILITATO.getValue(), 
					statoDumpRichiesta, 
					statoDumpRisposta, 
					dumpRichiestaIngressoHeader,
					dumpRichiestaIngressoBody,
					dumpRichiestaIngressoAttachments, 
					dumpRichiestaUscitaHeader,
					dumpRichiestaUscitaBody,
					dumpRichiestaUscitaAttachments, 
					dumpRispostaIngressoHeader, 
					dumpRispostaIngressoBody, 
					dumpRispostaIngressoAttachments,
					dumpRispostaUscitaHeader, 
					dumpRispostaUscitaBody, 
					dumpRispostaUscitaAttachments
				)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			ret = env.paHelper.getConfigurazioneDump(TipoOperazione.OTHER,
					true,	// showStato
					statoDump,
					false, 	// showRealtime, 
					StatoFunzionalita.ABILITATO.getValue(),			// realtime, Come da debug
					statoDumpRichiesta, 
					statoDumpRisposta, 
					dumpRichiestaIngressoHeader,
					dumpRichiestaIngressoBody,
					dumpRichiestaIngressoAttachments, 
					dumpRichiestaUscitaHeader,
					dumpRichiestaUscitaBody,
					dumpRichiestaUscitaAttachments, 
					dumpRispostaIngressoHeader, 
					dumpRispostaIngressoBody, 
					dumpRispostaIngressoAttachments,
					dumpRispostaUscitaHeader, 
					dumpRispostaUscitaBody, 
					dumpRispostaUscitaAttachments
				);
		} 
		else {	
			
			// Caso porta delegata
			
			if (!env.pdHelper.checkDataConfigurazioneDump(TipoOperazione.OTHER,
					true,	// showStato
					statoDump,
					false, 	// showRealtime, 
					StatoFunzionalita.ABILITATO.getValue(),	// realtime 
					statoDumpRichiesta, 
					statoDumpRisposta, 
					dumpRichiestaIngressoHeader,
					dumpRichiestaIngressoBody,
					dumpRichiestaIngressoAttachments, 
					dumpRichiestaUscitaHeader,
					dumpRichiestaUscitaBody,
					dumpRichiestaUscitaAttachments, 
					dumpRispostaIngressoHeader, 
					dumpRispostaIngressoBody, 
					dumpRispostaIngressoAttachments,
					dumpRispostaUscitaHeader, 
					dumpRispostaUscitaBody, 
					dumpRispostaUscitaAttachments
				)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
			}
			
			ret = env.pdHelper.getConfigurazioneDump(TipoOperazione.OTHER,
					true,	// showStato
					statoDump,
					false, 	// showRealtime, 
					StatoFunzionalita.ABILITATO.getValue(),	// realtime, Come da debug 
					statoDumpRichiesta, 
					statoDumpRisposta, 
					dumpRichiestaIngressoHeader,
					dumpRichiestaIngressoBody,
					dumpRichiestaIngressoAttachments, 
					dumpRichiestaUscitaHeader,
					dumpRichiestaUscitaBody,
					dumpRichiestaUscitaAttachments, 
					dumpRispostaIngressoHeader, 
					dumpRispostaIngressoBody, 
					dumpRispostaIngressoAttachments,
					dumpRispostaUscitaHeader, 
					dumpRispostaUscitaBody, 
					dumpRispostaUscitaAttachments
				);
		}
		
		return ret;
		
	}
	
	
	public static final List<MappingErogazionePortaApplicativa> getMappingGruppiPA(String nomeGruppo, IdServizio idAsps,  
			AccordiServizioParteSpecificaCore apsCore ) throws Exception {
		
		return apsCore.mappingServiziPorteAppList(idAsps,idAsps.getId(), null).stream()
			.filter( m -> m.getDescrizione().equals(nomeGruppo) )
			.collect(Collectors.toList());
		
	}
	
	
	public static final List<MappingFruizionePortaDelegata> getMappingGruppiPD(
			String nomeGruppo,
			IDSoggetto idFruitore,
			IdServizio idAsps,  
			AccordiServizioParteSpecificaCore apsCore ) throws Exception {
		
		return apsCore.serviziFruitoriMappingList(idFruitore, idAsps, null).stream()
			.filter( m -> m.getDescrizione().equals(nomeGruppo))
			.collect(Collectors.toList());
		
	}
	
	public static final IDPortaApplicativa getIDGruppoPA(String nome, IdServizio idAsps, AccordiServizioParteSpecificaCore apsCore) throws Exception {
		
		return getMappingGruppiPA(nome, idAsps, apsCore).stream()
				.map( m -> m.getIdPortaApplicativa() )
				.findFirst()
				.orElse(null);
		
	}
	
	
	public static final IDPortaApplicativa getIDGruppoPADefault(IdServizio idAsps, AccordiServizioParteSpecificaCore apsCore) throws Exception {
		return AccordiServizioParteSpecificaUtilities.getDefaultMappingPA(apsCore.mappingServiziPorteAppList(idAsps,idAsps.getId(), null))
				.getIdPortaApplicativa();
	}
	
	
	public static final IDPortaDelegata getIDGruppoPDDefault(IDSoggetto idFruitore,	IdServizio idAsps,	AccordiServizioParteSpecificaCore apsCore ) throws Exception {
		return AccordiServizioParteSpecificaUtilities.getDefaultMappingPD(apsCore.serviziFruitoriMappingList(idFruitore,idAsps,null))
				.getIdPortaDelegata();
	}
	
	
	public static final IDPortaDelegata getIDGruppoPD(	
			String nome,
			IDSoggetto idFruitore,
			IdServizio idAsps,  
			AccordiServizioParteSpecificaCore apsCore ) throws Exception {
		
		return getMappingGruppiPD(nome, idFruitore, idAsps, apsCore).stream()
				.map( m -> m.getIdPortaDelegata())
				.findFirst()
				.orElse(null);
	}
	
	
	public static final List<String> getAzioniOccupateErogazione(IdServizio idAsps, AccordiServizioParteSpecificaCore apsCore, PorteApplicativeCore paCore) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		
		 return apsCore.mappingServiziPorteAppList(idAsps,idAsps.getId(), null).stream()
		 	.map ( m -> {
					try {
						return paCore.getPortaApplicativa(m.getIdPortaApplicativa());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
		 		})
		 	.filter( pa -> pa.getAzione() != null && pa.getAzione().getAzioneDelegataList() != null )
		 	.flatMap( pa -> pa.getAzione().getAzioneDelegataList().stream())
		 	.collect(Collectors.toList());

	}
	
	public static final List<String> getAzioniDisponibiliRateLimitingPortaApplicativa(IdServizio idAsps, PortaApplicativa pa) {
		return null;
	}
	
	public static final List<String> getAzioniDisponibiliRateLimitingPortaDelegata(PortaDelegata pd, ErogazioniEnv env) {
		if (pd.getAzione() != null && pd.getAzione().getAzioneDelegataList().size() > 0) {
			return pd.getAzione().getAzioneDelegataList();
		}
		
		return null;
		
		//env.paCore.getAzioni(asps, aspc, false, false, getAzioniOccupateFruizione()));
		/*List<String> azioniAll = this.confCore.getAzioni(env.tipo_protocollo, protocolliValue,
				pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario(), 
				pa.getServizio().getTipo(), pa.getServizio().getNome(), pa.getServizio().getVersione());*/
	}
	
	public static final List<String> getAzioniOccupateFruizione(IdServizio idAsps, IDSoggetto idFruitore, AccordiServizioParteSpecificaCore apsCore, PorteDelegateCore pdCore) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		
		 return apsCore.serviziFruitoriMappingList(idFruitore, idAsps, null).stream()
		 	.map ( m -> {
					try {
						return pdCore.getPortaDelegata(m.getIdPortaDelegata());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
		 		})
		 	.filter( pa -> pa.getAzione() != null && pa.getAzione().getAzioneDelegataList() != null )
		 	.flatMap( pa -> pa.getAzione().getAzioneDelegataList().stream())
		 	.collect(Collectors.toList());

	}

	
	public static final List<String> getDescrizioniMappingPA(List<MappingErogazionePortaApplicativa> listaMappingErogazione) {
		return listaMappingErogazione.stream().map( m -> m.getDescrizione() ).collect(Collectors.toList() ); 
	}

	
	public static final List<String> getDescrizioniMappingPD(List<MappingFruizionePortaDelegata> listaMappingErogazione) {
		return listaMappingErogazione.stream().map( m -> m.getDescrizione() ).collect(Collectors.toList() ); 
	}



	public static final ListaApiImplAllegati findAllAllegati(String q, Integer limit, Integer offset, UriInfo uriInfo,
			final ErogazioniEnv env, final AccordoServizioParteSpecifica asps)
			throws DriverRegistroServiziException, InstantiationException, IllegalAccessException, CoreException {
		int idLista = Liste.SERVIZI_ALLEGATI;
		
		final Search ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista, env.idSoggetto.toIDSoggetto(), env.tipo_protocollo);
		final List<Documento> lista = env.apsCore.serviziAllegatiList(asps.getId().intValue(), ricerca);
		
		if ( env.findall_404 && lista.isEmpty() ) {
			throw FaultCode.NOT_FOUND.toException("Nessun allegato associato");
		}
		
		ListaApiImplAllegati ret = ListaUtils.costruisciListaPaginata(
				uriInfo,
				ricerca.getIndexIniziale(idLista),
				ricerca.getPageSize(idLista), 
				ricerca.getNumEntries(idLista), 
				ListaApiImplAllegati.class
			);
		
		lista.forEach( d-> 
			ret.addItemsItem( ImplAllegatoToItem(
					documentoToImplAllegato(d))
				)
		);
		return ret;
	}



	public static final ApiImplVersioneApiView aspsToApiImplVersioneApiView(final ErogazioniEnv env,
			final AccordoServizioParteSpecifica asps)
			throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		final AccordoServizioParteComuneSintetico aspc = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
		
		ApiImplVersioneApiView ret = new ApiImplVersioneApiView();
		
		AccordoServizioParteComuneSintetico api = env.apcCore.getAccordoServizioSintetico(asps.getIdAccordo());
		
		ret.setApiNome(aspc.getNome());
		ret.setApiSoapServizio(asps.getPortType());
		ret.setApiVersione(aspc.getVersione());
		ret.setProfilo(env.profilo);
		ret.setSoggetto(aspc.getSoggettoReferente().getNome());
		ret.setTipoServizio(asps.getTipo());
		
		  // Lista di Accordi Compatibili
        List<AccordoServizioParteComune> asParteComuneCompatibili = env.apsCore.findAccordiParteComuneBySoggettoAndNome(
        		api.getNome(),
                new IDSoggetto(api.getSoggettoReferente().getTipo(), api.getSoggettoReferente().getNome())
        	);
        
      	ret.setVersioni(asParteComuneCompatibili.stream().map( a -> a.getVersione() ).collect(Collectors.toList())); 
		return ret;
	}



	public static final ApiImplInformazioniGeneraliView erogazioneToApiImplInformazioniGeneraliView(final ErogazioniEnv env,
			final AccordoServizioParteSpecifica asps) {
		ApiImplViewItem item = aspsToErogazioneViewItem(env, asps);

		ApiImplInformazioniGeneraliView ret = new ApiImplInformazioniGeneraliView();
		
		ret.setApiSoapServizio(item.getApiSoapServizio());
		ret.setNome(item.getNome());
		ret.setProfilo(item.getProfilo());
		ret.setTipo(item.getTipoServizio());
		return ret;
	}
	
	public static final ApiImplInformazioniGeneraliView fruizioneToApiImplInformazioniGeneraliView(final ErogazioniEnv env,
			final AccordoServizioParteSpecifica asps, IdSoggetto fruitore) {
		
		ApiImplViewItem item = aspsToFruizioneViewItem(env, asps, fruitore);

		ApiImplInformazioniGeneraliView ret = new ApiImplInformazioniGeneraliView();
		
		ret.setApiSoapServizio(item.getApiSoapServizio());
		ret.setNome(item.getNome());
		ret.setProfilo(item.getProfilo());
		ret.setTipo(item.getTipoServizio());
		return ret;
	}




	public static final Connettore buildConnettore(Map<String, String> props) {

		Connettore c = new Connettore();
		c.setEndpoint(props.get(CostantiDB.CONNETTORE_HTTP_LOCATION));
		
		//TODO: Forse questi nel caso delle erogazioni vanno presi dall'invocazione, guarda la updateConnettore.
		ConnettoreConfigurazioneHttpBasic http = new ConnettoreConfigurazioneHttpBasic();
		http.setPassword(evalnull( () -> props.get(CostantiDB.CONNETTORE_PWD).trim())); 
		http.setUsername(evalnull( () -> props.get(CostantiDB.CONNETTORE_USER).trim()));
		if ( !StringUtils.isAllEmpty(http.getPassword(), http.getUsername()) ) {
			c.setAutenticazioneHttp(http);
		}
	
		ConnettoreConfigurazioneHttps https = new ConnettoreConfigurazioneHttps();
		https.setHostnameVerifier( props.get(CostantiDB.CONNETTORE_HTTPS_HOSTNAME_VERIFIER) != null 
				? Boolean.valueOf(props.get(CostantiDB.CONNETTORE_HTTPS_HOSTNAME_VERIFIER))
				: null
			);
		https.setTipologia(
				evalnull( () -> Enums.fromValue(SslTipologiaEnum.class, props.get(CostantiDB.CONNETTORE_HTTPS_SSL_TYPE)))
			);
		
		ConnettoreConfigurazioneHttpsServer httpsServer = new ConnettoreConfigurazioneHttpsServer();
		https.setServer(httpsServer);
		
		httpsServer.setAlgoritmo( evalnull( () -> 
			props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM))
			);
		httpsServer.setTruststorePassword(
				evalnull( () -> props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD))
			);
		httpsServer.setTruststorePath(
				evalnull( () -> props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_LOCATION))
			);
		httpsServer.setTruststoreTipo(
				evalnull( () -> Enums.fromValue(KeystoreEnum.class,props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_TYPE)))
			);
		
		ConnettoreConfigurazioneHttpsClient httpsClient = new ConnettoreConfigurazioneHttpsClient();
		https.setClient(httpsClient);
		
		httpsClient.setAlgoritmo(
				evalnull( () -> props.get(CostantiDB.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM))
			);
		httpsClient.setKeyPassword(
				evalnull( () -> props.get(CostantiDB.CONNETTORE_HTTPS_KEY_PASSWORD))
			);
		httpsClient.setKeystorePassword(
				evalnull( () -> props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_PASSWORD))
			);
		httpsClient.setKeystorePath(
				evalnull( () -> props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_LOCATION))
			);
		httpsClient.setKeystoreTipo(
				evalnull( () -> Enums.fromValue(KeystoreEnum.class, props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_TYPE)))
			);
		
		if ( https.getTipologia() != null ) {
			c.setAutenticazioneHttps(https);
		}
		
		String proxy_type = evalnull( () -> props.get(CostantiDB.CONNETTORE_PROXY_TYPE).trim() );
		if ( !StringUtils.isEmpty(proxy_type)) {
			ConnettoreConfigurazioneProxy proxy = new ConnettoreConfigurazioneProxy();
			c.setProxy(proxy);
			
			proxy.setHostname(
					evalnull( () -> props.get(CostantiDB.CONNETTORE_PROXY_HOSTNAME).trim())
				);
			proxy.setPassword(
					evalnull( () -> props.get(CostantiDB.CONNETTORE_PROXY_PASSWORD).trim())
				);
			proxy.setPorta(
					evalnull( () -> Integer.valueOf(props.get(CostantiDB.CONNETTORE_PROXY_PORT)))
				);
			proxy.setUsername(
					evalnull( () -> props.get(CostantiDB.CONNETTORE_PROXY_USERNAME).trim())
				);
		}
		
		ConnettoreConfigurazioneTimeout tempiRisposta = new ConnettoreConfigurazioneTimeout();		
		tempiRisposta.setConnectionReadTimeout( 
				evalnull( () -> Integer.valueOf(props.get(CostantiDB.CONNETTORE_READ_CONNECTION_TIMEOUT))) 
			);
		tempiRisposta.setConnectionTimeout(
				evalnull( () -> Integer.valueOf(props.get(CostantiDB.CONNETTORE_CONNECTION_TIMEOUT)))
			);
		tempiRisposta.setTempoMedioRisposta(
				evalnull( () -> Integer.valueOf(props.get(CostantiDB.CONNETTORE_TEMPO_MEDIO_RISPOSTA)))
			);
		
		if ( tempiRisposta.getConnectionReadTimeout() != null || tempiRisposta.getConnectionTimeout() != null || tempiRisposta.getTempoMedioRisposta() != null) {
			c.setTempiRisposta(tempiRisposta);
		}
		
		c.setTokenPolicy(
				evalnull( () -> props.get(CostantiDB.CONNETTORE_TOKEN_POLICY).trim())
			);
		
		return c;
	}



	public static final CorsConfigurazione buildCorsConfigurazione(GestioneCors body, final ErogazioniEnv env,
			final CorsConfigurazione oldConf) throws Exception {
		
		
		final String statoCorsPorta = body.isRidefinito()
				? 		CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO
				:       CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
		
		final GestioneCorsAccessControl c = body.getAccessControl();
		
		final String corsStato = Helper.boolToStatoFunzionalita( body.getTipo() != TipoGestioneCorsEnum.DISABILITATO ).toString();
		final TipoGestioneCORS corsTipo =  Enums.tipoGestioneCorsFromRest.get( body.getTipo() );
		final String allowOrigins = String.join(",", BaseHelper.evalorElse( () -> c.getAllowOrigins(), new ArrayList<String>()) );
		final String allowHeaders = String.join(",", BaseHelper.evalorElse( () -> c.getAllowHeaders(), new ArrayList<String>()) );
		final String exposeHeaders = String.join(",", BaseHelper.evalorElse( () -> c.getExposeHeaders(), new ArrayList<String>()) );
		final String allowMethods = String.join(
				",",
				BaseHelper.evalorElse( () -> c.getAllowMethods(), new ArrayList<HttpMethodEnum>() )
					.stream().map( m -> m.name()).collect(Collectors.toList()) 
			);
	
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_STATO, corsStato );
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_TIPO, corsTipo.toString() );
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_ORIGINS, ServletUtils.boolToCheckBoxStatus(c.isAllAllowOrigins()) );
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_ORIGINS, allowOrigins );
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_HEADERS, allowHeaders );
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_METHODS, allowMethods );
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_EXPOSE_HEADERS, exposeHeaders );
	
		if ( !env.paHelper.checkDataConfigurazioneCorsPorta(TipoOperazione.OTHER, true, statoCorsPorta) ) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
		}
		
	
		return env.paHelper.getGestioneCors(
				body.getTipo() != TipoGestioneCorsEnum.DISABILITATO,
				corsTipo,  
				c.isAllAllowOrigins(), 
				allowHeaders, 
				allowOrigins, 
				allowMethods, 
				c.isAllowCredentials(),
				exposeHeaders,
				BaseHelper.evalorElse( () -> oldConf.getAccessControlMaxAge() != null, false),
				BaseHelper.evalorElse( () -> oldConf.getAccessControlMaxAge(), -1 )
			);
	}
	
	
	public static final GestioneToken buildGestioneToken(ControlloAccessiGestioneToken body, Object porta, boolean isPortaDelegata, ConsoleHelper coHelper, ErogazioniEnv env) throws Exception {
				
		GestioneToken ret = new GestioneToken();
		
		ret.setPolicy( body.getPolicy() );
		ret.setTokenOpzionale( Helper.boolToStatoFunzionalitaConf(body.isTokenOpzionale()) ); 
		ret.setValidazione( StatoFunzionalitaConWarning.toEnumConstant(  evalnull(  () -> body.getValidazioneJwt().toString() )) );
		ret.setIntrospection( StatoFunzionalitaConWarning.toEnumConstant( evalnull( () -> body.getIntrospection().toString() )) );
		ret.setUserInfo( StatoFunzionalitaConWarning.toEnumConstant( evalnull( () -> body.getUserInfo().toString() )) );
		ret.setForward( Helper.boolToStatoFunzionalitaConf(body.isTokenForward()) ); 	
		
		return ret;
		
	}
	
	public static final void fillGestioneToken(GestioneToken ret, ControlloAccessiGestioneToken body) {
		ret.setPolicy( body.getPolicy() );
		ret.setTokenOpzionale( Helper.boolToStatoFunzionalitaConf(body.isTokenOpzionale()) ); 
		ret.setValidazione( StatoFunzionalitaConWarning.toEnumConstant(  evalnull(  () -> body.getValidazioneJwt().toString() )) );
		ret.setIntrospection( StatoFunzionalitaConWarning.toEnumConstant( evalnull( () -> body.getIntrospection().toString() )) );
		ret.setUserInfo( StatoFunzionalitaConWarning.toEnumConstant( evalnull( () -> body.getUserInfo().toString() )) );
		ret.setForward( Helper.boolToStatoFunzionalitaConf(body.isTokenForward()) ); 			
	}
	
	public static final RegistrazioneMessaggiConfigurazioneRegola fromDumpConfigurazioneRegola(DumpConfigurazioneRegola r) {
		if (r == null) return null;
		RegistrazioneMessaggiConfigurazioneRegola ret = new RegistrazioneMessaggiConfigurazioneRegola();
		
		ret.setAttachments( Helper.statoFunzionalitaConfToBool( r.getAttachments() ));
		ret.setBody(Helper.statoFunzionalitaConfToBool( r.getBody()) );
		ret.setHeaders(Helper.statoFunzionalitaConfToBool( r.getHeaders()) );
		
		return ret;
	}



	public static final ControlloAccessiAutenticazioneToken fromGestioneTokenAutenticazione(
			final GestioneTokenAutenticazione tokAut) {
		ControlloAccessiAutenticazioneToken token = new ControlloAccessiAutenticazioneToken();
		token.setClientId(Helper.statoFunzionalitaConfToBool( tokAut.getClientId() ));
		token.setEmail(Helper.statoFunzionalitaConfToBool( tokAut.getEmail() ));
		token.setIssuer(Helper.statoFunzionalitaConfToBool( tokAut.getIssuer() ));
		token.setSubject(Helper.statoFunzionalitaConfToBool( tokAut.getSubject() ));
		token.setUsername(Helper.statoFunzionalitaConfToBool(tokAut.getUsername() ));
		return token;
	}



	public static final RegistrazioneMessaggi fromDumpConfigurazione(final DumpConfigurazione paDump) {
		final RegistrazioneMessaggi ret = new RegistrazioneMessaggi();
		
		if (paDump != null) {
			ret.setStato(StatoDefaultRidefinitoEnum.RIDEFINITO);
			
			final RegistrazioneMessaggiConfigurazione richiesta = new RegistrazioneMessaggiConfigurazione();
			
			richiesta.setAbilitato( isDumpConfigurazioneAbilitato(paDump, false) );
			if ( richiesta.isAbilitato() ) {
				richiesta.setIngresso( fromDumpConfigurazioneRegola(paDump.getRichiestaIngresso()));
				richiesta.setUscita( fromDumpConfigurazioneRegola(paDump.getRichiestaUscita()));
			}
			
			ret.setRichiesta(richiesta);
							
			RegistrazioneMessaggiConfigurazione risposta = new RegistrazioneMessaggiConfigurazione();
			risposta.setAbilitato( isDumpConfigurazioneAbilitato(paDump,true) );
			if (risposta.isAbilitato()) {
				risposta.setIngresso( fromDumpConfigurazioneRegola( paDump.getRispostaIngresso()) );
				risposta.setUscita( fromDumpConfigurazioneRegola( paDump.getRispostaUscita()) );
			}
			
			ret.setRisposta(risposta);				
		}
		else {
			ret.setStato(StatoDefaultRidefinitoEnum.DEFAULT);
		}
		return ret;
	}



	public static final Validazione fromValidazioneContenutiApplicativi(final ValidazioneContenutiApplicativi vx) {
		final Validazione ret = new Validazione();
		
		if (vx == null) {
			ret.setStato(StatoFunzionalitaConWarningEnum.DISABILITATO);
		} else {

			ret.setMtom( Helper.statoFunzionalitaConfToBool(vx.getAcceptMtomMessage()) );
			ret.setStato( StatoFunzionalitaConWarningEnum.valueOf(vx.getStato().name()));
			ret.setTipo(  TipoValidazioneEnum.valueOf(vx.getTipo().name()) );
		}
		return ret;
	}


	public static final String readHeadersResponseCaching(List<String> headers) {
		if(headers==null || headers.isEmpty()) {
			return null;
		}
		StringBuilder  bf = new StringBuilder();
		for (String hdr : headers) {
			if(bf.length()>0) {
				bf.append(",");
			}
			bf.append(hdr);
		}
		return bf.toString();
	}
	
	public static final List<ResponseCachingConfigurazioneRegola> getRegoleResponseCaching(List<CachingRispostaRegola> regole){
		if(regole==null || regole.isEmpty()) {
			return null;
		}
		List<ResponseCachingConfigurazioneRegola> returnList = new ArrayList<>();
		for (CachingRispostaRegola cachingRispostaRegola : regole) {
			ResponseCachingConfigurazioneRegola rule = new ResponseCachingConfigurazioneRegola();
			if(cachingRispostaRegola.getReturnCodeMin()!=null)
				rule.setReturnCodeMin(cachingRispostaRegola.getReturnCodeMin());
			if(cachingRispostaRegola.getReturnCodeMax()!=null)
				rule.setReturnCodeMax(cachingRispostaRegola.getReturnCodeMax());
			rule.setFault(cachingRispostaRegola.isFault());
			if(cachingRispostaRegola.getCacheTimeoutSeconds()!=null)
				rule.setCacheTimeoutSeconds(cachingRispostaRegola.getCacheTimeoutSeconds());
			returnList.add(rule);
		}
		return returnList;
	}

	public static final ResponseCachingConfigurazione buildResponseCachingConfigurazione(CachingRisposta body, PorteApplicativeHelper paHelper) {
		ResponseCachingConfigurazione newConfigurazione = null;
		if (body.getStato() == StatoDefaultRidefinitoEnum.DEFAULT) {
			
		}
		
		else if ( body.getStato() == StatoDefaultRidefinitoEnum.RIDEFINITO ) {
			newConfigurazione = paHelper.getResponseCaching(
					body.isAbilitato(),  // responseCachingEnabled
					body.getCacheTimeoutSeconds(), // responseCachingSeconds
					body.isMaxResponseSize(), // responseCachingMaxResponseSize
					body.getMaxResponseSizeKb(), // responseCachingMaxResponseSizeBytes
					body.isHashRequestUri(), // responseCachingDigestUrlInvocazione
					(readHeadersResponseCaching(body.getHashHeaders())!=null), // responseCachingDigestHeaders
					body.isHashPayload(), // responseCachingDigestPayload
					readHeadersResponseCaching(body.getHashHeaders()), // responseCachingDigestHeadersNomiHeaders
					convertToStatoFunzionalitaCacheDigestQueryParameter(body),//responseCachingDigestQueryParameter
					readHeadersResponseCaching(body.getHashQueryParamaters()), // responseCachingDigestNomiParametriQuery
					body.isControlNoCache(), // responseCachingCacheControlNoCache
					body.isControlMaxAge(), // responseCachingCacheControlMaxAge
					body.isControlNoStore(), // responseCachingCacheControlNoStore
					getRegoleResponseCaching(body.getRegole())// listaRegoleCachingConfigurazione
				);
		}
		return newConfigurazione;
	}
	
	private static final StatoFunzionalitaCacheDigestQueryParameter convertToStatoFunzionalitaCacheDigestQueryParameter(CachingRisposta body) {
		if(body!=null && body.isHashAllQueryParameters()!=null) {
			if(body.isHashAllQueryParameters()) {
				return StatoFunzionalitaCacheDigestQueryParameter.ABILITATO;
			}
			else {
				if(body.getHashQueryParamaters()!=null && !body.getHashQueryParamaters().isEmpty()) {
					return StatoFunzionalitaCacheDigestQueryParameter.SELEZIONE_PUNTUALE;
				}
				else {
					return StatoFunzionalitaCacheDigestQueryParameter.DISABILITATO;
				}
			}
		}
		else {
			// default
			return StatoFunzionalitaCacheDigestQueryParameter.ABILITATO;
		}
	}
	
	public static final CachingRisposta buildCachingRisposta(ResponseCachingConfigurazione conf) {
		CachingRisposta ret = new CachingRisposta();
		
		if (conf == null) {
			ret.setStato(StatoDefaultRidefinitoEnum.DEFAULT);
			ret.setAbilitato(false);
			ret.setHashHeaders(null);
			ret.setHashPayload(null);
			ret.setHashRequestUri(null);
			ret.setHashAllQueryParameters(null);
			return ret;
		}
		
		ret.setAbilitato(conf.getStato() == StatoFunzionalita.ABILITATO);
		ret.setStato( conf.getStato() == StatoFunzionalita.ABILITATO
				? StatoDefaultRidefinitoEnum.RIDEFINITO
				: StatoDefaultRidefinitoEnum.DEFAULT
			);
		ret.setCacheTimeoutSeconds(conf.getCacheTimeoutSeconds());
		
		if (ret.isAbilitato()) {
			
			ret.setMaxResponseSize( conf.getMaxMessageSize() != null);
			ret.setMaxResponseSizeKb( conf.getMaxMessageSize() );
			
			ResponseCachingConfigurazioneHashGenerator hashInfo = conf.getHashGenerator();
			if(hashInfo.getQueryParameters()!=null) {
				switch (hashInfo.getQueryParameters()) {
				case ABILITATO:
					ret.setHashAllQueryParameters(true);
					break;
				case DISABILITATO:
					ret.setHashAllQueryParameters(false);
					break;
				case SELEZIONE_PUNTUALE:
					ret.setHashAllQueryParameters(false);
					if(hashInfo.sizeQueryParameterList()>0) {
						ret.setHashQueryParamaters(hashInfo.getQueryParameterList());
					}
					break;
				}
			}
			if(StatoFunzionalita.ABILITATO.equals(hashInfo.getHeaders())) {
				if(hashInfo.sizeHeaderList()>0) {
					ret.setHashHeaders(hashInfo.getHeaderList());
				}
			}
			ret.setHashPayload(Helper.statoFunzionalitaConfToBool(hashInfo.getPayload()));
			ret.setHashRequestUri(Helper.statoFunzionalitaConfToBool(hashInfo.getRequestUri()));
			
			if(conf.getControl()!=null) {
				ret.setControlNoCache(conf.getControl().isNoCache());
				ret.setControlMaxAge(conf.getControl().isMaxAge());
				ret.setControlNoStore(conf.getControl().isNoStore());
			}
			
			if(conf.sizeRegolaList()>0) {
				ret.setRegole(new ArrayList<CachingRispostaRegola>());
				
				for (ResponseCachingConfigurazioneRegola regola : conf.getRegolaList()) {
					CachingRispostaRegola cachingRispostaRegola = new CachingRispostaRegola();
					if(regola.getReturnCodeMin()!=null) {
						cachingRispostaRegola.setReturnCodeMin(regola.getReturnCodeMin());
					}
					if(regola.getReturnCodeMax()!=null) {
						cachingRispostaRegola.setReturnCodeMax(regola.getReturnCodeMax());
					}
					cachingRispostaRegola.setFault(regola.isFault());
					if(regola.getCacheTimeoutSeconds()!=null) {
						cachingRispostaRegola.setCacheTimeoutSeconds(regola.getCacheTimeoutSeconds());
					}
					ret.addRegoleItem(cachingRispostaRegola);
				}
			}
		}
		
		return ret;
	}


	
	@Deprecated	
	public static final RuoloTipologia ruoloTipologiaFromAutorizzazione(final String autorizzazione) {
		RuoloTipologia tipoRuoloFonte = null;
		if (TipoAutorizzazione.isRolesRequired(autorizzazione)) {
			if ( TipoAutorizzazione.isExternalRolesRequired(autorizzazione) )
				tipoRuoloFonte = RuoloTipologia.ESTERNO;
			else if ( TipoAutorizzazione.isInternalRolesRequired(autorizzazione))
				tipoRuoloFonte = RuoloTipologia.INTERNO;
			else
				tipoRuoloFonte = RuoloTipologia.QUALSIASI;				
		}
		return tipoRuoloFonte;
	}
	
	
	public static final boolean controlloAccessiCheckPD(FruizioniConfEnv env, PortaDelegata oldPd, PortaDelegata newPd) throws Exception {
		final BinaryParameter allegatoXacmlPolicy = new BinaryParameter();
		
		allegatoXacmlPolicy.setValue( BaseHelper.evalorElse(
				() -> newPd.getXacmlPolicy(),
				""
				).getBytes()
			);
		RuoloTipologia tipoRuoloFonte = AutorizzazioneUtilities.convertToRuoloTipologia(TipoAutorizzazione.toEnumConstant(newPd.getAutorizzazione()));
		String stato_autorizzazione = null;
		if (TipoAutorizzazione.toEnumConstant(newPd.getAutorizzazione()) != null) {
			stato_autorizzazione = AutorizzazioneUtilities.convertToStato(newPd.getAutorizzazione());
		}
		
		boolean tokenAbilitato = newPd.getGestioneToken() != null;
		boolean autorizzazioneScope = newPd.getScope() != null;
		
		if ( autorizzazioneScope && !tokenAbilitato ) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'autorizzazione per scope richiede una token policy abilitata");
		}
		
		TipoAutenticazionePrincipal	autenticazionePrincipal = env.pdCore.getTipoAutenticazionePrincipal(newPd.getProprietaAutenticazioneList());
		List<String> autenticazioneParametroList = env.pdCore.getParametroAutenticazione(newPd.getAutenticazione(), newPd.getProprietaAutenticazioneList());
        	
		String autorizzazioneContenutiStato = null;
		String autorizzazioneContenuti = newPd.getAutorizzazioneContenuto();
		String autorizzazioneContenutiProperties = null;
		if(autorizzazioneContenuti == null) {
			autorizzazioneContenutiStato = StatoFunzionalita.DISABILITATO.getValue();
		} else if(autorizzazioneContenuti.equals(CostantiAutorizzazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN)) {
			autorizzazioneContenutiStato = StatoFunzionalita.ABILITATO.getValue();
			List<Proprieta> proprietaAutorizzazioneContenutoList = newPd.getProprietaAutorizzazioneContenutoList();
			StringBuilder sb = new StringBuilder();
			for (Proprieta proprieta : proprietaAutorizzazioneContenutoList) {
				if(sb.length() >0)
					sb.append("\n");
				
				sb.append(proprieta.getNome()).append("=").append(proprieta.getValore()); 
			}						
			
			autorizzazioneContenutiProperties = sb.toString();
		} else { // custom
			autorizzazioneContenutiStato = CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_CUSTOM;
		}
		
		return env.paHelper.controlloAccessiCheck(
				TipoOperazione.OTHER, 
				newPd.getAutenticazione(),				// Autenticazione
				ServletUtils.boolToCheckBoxStatus( Helper.statoFunzionalitaConfToBool( newPd.getAutenticazioneOpzionale() ) ),		// Autenticazione Opzionale
				autenticazionePrincipal,
				autenticazioneParametroList,
				stato_autorizzazione,	 			
				ServletUtils.boolToCheckBoxStatus( TipoAutorizzazione.isAuthenticationRequired(newPd.getAutorizzazione()) ), 
				ServletUtils.boolToCheckBoxStatus( TipoAutorizzazione.isRolesRequired(newPd.getAutorizzazione()) ), 
				evalnull( () -> tipoRuoloFonte.toString() ), 
				evalnull( () -> newPd.getRuoli().getMatch().toString() ), 
				env.isSupportatoAutenticazioneSoggetti, 
				true,		// isPortaDelegata, 
				oldPd,
				evalnull( () ->
						newPd.getRuoli().getRuoloList().stream().map(Ruolo::getNome).collect(Collectors.toList())
					),
				Helper.boolToStatoFunzionalita(tokenAbilitato).getValue(),
				evalnull( () -> newPd.getGestioneToken().getPolicy() ), 
				evalnull( () -> newPd.getGestioneToken().getValidazione().toString() ), 
				evalnull( () -> newPd.getGestioneToken().getIntrospection().toString() ), 
				evalnull( () -> newPd.getGestioneToken().getUserInfo().toString() ),
				evalnull( () -> newPd.getGestioneToken().getForward().toString() ),
				evalnull( () -> ServletUtils.boolToCheckBoxStatus( newPd.getGestioneToken().getOptions() != null ) ),
				evalnull( () -> newPd.getGestioneToken().getOptions() ),
				evalnull( () -> ServletUtils.boolToCheckBoxStatus( autorizzazioneScope ) ),
				evalnull( () -> newPd.getScope().getMatch().toString() ),
				allegatoXacmlPolicy,
				autorizzazioneContenutiStato, autorizzazioneContenuti, autorizzazioneContenutiProperties,
				env.tipo_protocollo);
	}



	public static final boolean controlloAccessiCheckPA(ErogazioniConfEnv env, PortaApplicativa oldPa, PortaApplicativa newPa)
			throws Exception {
		
		final BinaryParameter allegatoXacmlPolicy = new BinaryParameter();
		
		allegatoXacmlPolicy.setValue( BaseHelper.evalorElse(
				() -> newPa.getXacmlPolicy(),
				""
				).getBytes()
			);
		RuoloTipologia tipoRuoloFonte = AutorizzazioneUtilities.convertToRuoloTipologia(TipoAutorizzazione.toEnumConstant(newPa.getAutorizzazione()));
		String stato_autorizzazione = null;
		if (TipoAutorizzazione.toEnumConstant(newPa.getAutorizzazione()) != null) {
			stato_autorizzazione = AutorizzazioneUtilities.convertToStato(newPa.getAutorizzazione());
		}
		
		boolean tokenAbilitato = newPa.getGestioneToken() != null;
		boolean autorizzazioneScope = newPa.getScope() != null;
		
		if ( autorizzazioneScope && !tokenAbilitato ) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'autorizzazione per scope richiede una token policy abilitata");
		}
		
		TipoAutenticazionePrincipal	autenticazionePrincipal = env.paCore.getTipoAutenticazionePrincipal(newPa.getProprietaAutenticazioneList());
		List<String> autenticazioneParametroList = env.paCore.getParametroAutenticazione(newPa.getAutenticazione(), newPa.getProprietaAutenticazioneList());
		
		String autorizzazioneContenutiStato = null;
		String autorizzazioneContenuti = newPa.getAutorizzazioneContenuto();
		String autorizzazioneContenutiProperties = null;
		if(autorizzazioneContenuti == null) {
			autorizzazioneContenutiStato = StatoFunzionalita.DISABILITATO.getValue();
		} else if(autorizzazioneContenuti.equals(CostantiAutorizzazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN)) {
			autorizzazioneContenutiStato = StatoFunzionalita.ABILITATO.getValue();
			List<Proprieta> proprietaAutorizzazioneContenutoList = newPa.getProprietaAutorizzazioneContenutoList();
			StringBuilder sb = new StringBuilder();
			for (Proprieta proprieta : proprietaAutorizzazioneContenutoList) {
				if(sb.length() >0)
					sb.append("\n");
				
				sb.append(proprieta.getNome()).append("=").append(proprieta.getValore()); 
			}						
			
			autorizzazioneContenutiProperties = sb.toString();
		} else { // custom
			autorizzazioneContenutiStato = CostantiControlStation.VALUE_PARAMETRO_PORTE_CONTROLLO_ACCESSI_AUTORIZZAZIONE_CONTENUTI_STATO_CUSTOM;
		}
		
		return env.paHelper.controlloAccessiCheck(
				TipoOperazione.OTHER, 
				newPa.getAutenticazione(),				// Autenticazione
				ServletUtils.boolToCheckBoxStatus( Helper.statoFunzionalitaConfToBool( newPa.getAutenticazioneOpzionale() ) ),		// Autenticazione Opzionale
				autenticazionePrincipal,
				autenticazioneParametroList,
				stato_autorizzazione,	 			
				ServletUtils.boolToCheckBoxStatus( TipoAutorizzazione.isAuthenticationRequired(newPa.getAutorizzazione()) ), 
				ServletUtils.boolToCheckBoxStatus( TipoAutorizzazione.isRolesRequired(newPa.getAutorizzazione()) ), 
				evalnull( () -> tipoRuoloFonte.toString() ), 
				evalnull( () -> newPa.getRuoli().getMatch().toString() ), 
				env.isSupportatoAutenticazioneSoggetti, 
				false,		// isPortaDelegata, 
				oldPa,
				evalnull( () ->
						newPa.getRuoli().getRuoloList().stream().map(Ruolo::getNome).collect(Collectors.toList())
					),
				Helper.boolToStatoFunzionalita(tokenAbilitato).getValue(),	// gestioneToken
				evalnull( () -> newPa.getGestioneToken().getPolicy() ), 	// policy
				evalnull( () -> newPa.getGestioneToken().getValidazione().toString() ),	// validazioneInput 
				evalnull( () -> newPa.getGestioneToken().getIntrospection().toString() ), // introspection
				evalnull( () -> newPa.getGestioneToken().getUserInfo().toString() ),		// userInfo
				evalnull( () -> newPa.getGestioneToken().getForward().toString() ),		// forward
				evalnull( () -> ServletUtils.boolToCheckBoxStatus( newPa.getGestioneToken().getOptions() != null ) ),	// autorizzazioneToken
				evalnull( () -> newPa.getGestioneToken().getOptions() ),
				evalnull( () -> ServletUtils.boolToCheckBoxStatus( autorizzazioneScope ) ),
				evalnull( () -> newPa.getScope().getMatch().toString() ),
				allegatoXacmlPolicy,
				autorizzazioneContenutiStato, autorizzazioneContenuti, autorizzazioneContenutiProperties,
				env.tipo_protocollo);
		
	}
	
	public static final void fillPortaDelegata(ControlloAccessiAutorizzazione body, final PortaDelegata newPd) {
		final OneOfControlloAccessiAutorizzazioneAutorizzazione authz = body.getAutorizzazione();
		
		newPd.setXacmlPolicy(null);
		
		switch (authz.getTipo()) {
		case DISABILITATO: {
			
			final String tipoAutorString = TipoAutorizzazione.DISABILITATO.toString();
			newPd.setRuoli(null);
			newPd.setScope(null);
			newPd.setAutorizzazione(tipoAutorString);
			break;
		}
		case ABILITATO: {
			
			if(! (authz instanceof APIImplAutorizzazioneAbilitata)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione dell'autorizzazione '"+authz.getClass().getName()+"' non compatibile con il tipo impostato '"+authz.getTipo()+"'");
        	}
			APIImplAutorizzazioneAbilitata authzAbilitata = (APIImplAutorizzazioneAbilitata) authz;

			
			// defaults
			if ( authzAbilitata.isRuoli() && authzAbilitata.getRuoliFonte() == null ) {
				authzAbilitata.setRuoliFonte(FonteEnum.QUALSIASI);
			}
			
			final String autorizzazione = AutorizzazioneUtilities.STATO_ABILITATO;
			final String autorizzazioneAutenticati = ServletUtils.boolToCheckBoxStatus(authzAbilitata.isRichiedente());
			final String autorizzazioneRuoli = ServletUtils.boolToCheckBoxStatus(authzAbilitata.isRuoli());
			final String autorizzazioneScope = ServletUtils.boolToCheckBoxStatus(authzAbilitata.isScope());
			final String autorizzazione_tokenOptions = authzAbilitata.getTokenClaims();
			final RuoloTipologia tipoRuoloFonte = Enums.ruoloTipologiaFromRest.get(authzAbilitata.getRuoliFonte());
			final RuoloTipoMatch tipoRuoloMatch = RuoloTipoMatch.toEnumConstant( evalnull( () -> authzAbilitata.getRuoliRichiesti().toString()) );	// Gli enum coincidono
			final ScopeTipoMatch scopeTipoMatch = ScopeTipoMatch.toEnumConstant( evalnull( () -> authzAbilitata.getScopeRichiesti().toString()) );
	
			
			final String tipoAutorString =	AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString(autorizzazione,
					ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticati),
					ServletUtils.isCheckBoxEnabled(autorizzazioneRuoli),		
					ServletUtils.isCheckBoxEnabled(autorizzazioneScope),		
					autorizzazione_tokenOptions,					// Questo è il token claims
					tipoRuoloFonte									// RuoliFonte: Qualsiasi, Registro, Esterna
				);
			
			newPd.setAutorizzazione(tipoAutorString);
			
			if ( authzAbilitata.isRuoli() ) {
				if ( newPd.getRuoli() == null) newPd.setRuoli(new AutorizzazioneRuoli());
				
				newPd.getRuoli().setMatch(tipoRuoloMatch);
			} else {
				newPd.setRuoli(null);
			}
			
			
			if ( authzAbilitata.isScope() ) {
				if ( newPd.getScope() == null ) newPd.setScope(new AutorizzazioneScope());
				
				newPd.getScope().setMatch(scopeTipoMatch);
				newPd.getScope().setStato(StatoFunzionalita.ABILITATO);
			} else {
				newPd.setScope(null);
			}
			
			
			if ( authzAbilitata.isToken() ) {
				if ( newPd.getGestioneToken() == null ) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Nessun token configurato per l'erogazione");
				}
				newPd.getGestioneToken().setOptions(autorizzazione_tokenOptions);
			}
			else {
				BaseHelper.runNull( () -> newPd.getGestioneToken().setOptions(null) );
			}
			break;
		}
		case XACML_POLICY: {
			
			if(! (authz instanceof APIImplAutorizzazioneXACML)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione dell'autorizzazione '"+authz.getClass().getName()+"' non compatibile con il tipo impostato '"+authz.getTipo()+"'");
        	}
			APIImplAutorizzazioneXACML authzXacml = (APIImplAutorizzazioneXACML) authz;
			
			if (authzXacml.getPolicy() == null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Campo obbligatorio 'policy' non presente nell'autorizzazione indicata");
			}
			
			if (authzXacml.getRuoliFonte() == null)
				authzXacml.setRuoliFonte(FonteEnum.QUALSIASI);
			
			final RuoloTipologia tipoRuoloFonte = Enums.ruoloTipologiaFromRest.get(authzXacml.getRuoliFonte());
			
			final String tipoAutorString =	AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString(TipoAutorizzazioneEnum.XACML_POLICY.toString(),
					false,
					false,		
					false,
					"",
					tipoRuoloFonte									// RuoliFonte: Qualsiasi, Registro, Esterna
				);
			
			newPd.setAutorizzazione(tipoAutorString);
			newPd.setXacmlPolicy( evalnull( () -> new String(authzXacml.getPolicy())));				
			break;
		}
		case CUSTOM:
			
			if(! (authz instanceof APIImplAutorizzazioneCustom)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione dell'autorizzazione '"+authz.getClass().getName()+"' non compatibile con il tipo impostato '"+authz.getTipo()+"'");
        	}
			APIImplAutorizzazioneCustom authzACustom = (APIImplAutorizzazioneCustom) authz;
			
			if (StringUtils.isEmpty(authzACustom.getNome())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(APIImplAutorizzazioneCustom.class.getName()+": Indicare il campo obbligatorio 'nome' ");
			}
			newPd.setAutorizzazione(authzACustom.getNome());
			break;
		}	
	}



	public static final void fillPortaApplicativa(ControlloAccessiAutorizzazione body, final PortaApplicativa newPa) {
		final OneOfControlloAccessiAutorizzazioneAutorizzazione authz = body.getAutorizzazione();
		
		newPa.setXacmlPolicy(null);

		switch (authz.getTipo()) {
		case DISABILITATO: {
			
			final String tipoAutorString = TipoAutorizzazione.DISABILITATO.toString();
			newPa.setSoggetti(null);
			newPa.setRuoli(null);
			newPa.setScope(null);
			newPa.setAutorizzazione(tipoAutorString);
			break;
		}
		
		
		case ABILITATO: {
			if(! (authz instanceof APIImplAutorizzazioneAbilitata)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione dell'autorizzazione '"+authz.getClass().getName()+"' non compatibile con il tipo impostato '"+authz.getTipo()+"'");
        	}
			APIImplAutorizzazioneAbilitata authzAbilitata = (APIImplAutorizzazioneAbilitata) authz;
			
			// defaults
			if ( authzAbilitata.isRuoli() && authzAbilitata.getRuoliFonte() == null ) {
				authzAbilitata.setRuoliFonte(FonteEnum.QUALSIASI);
			}
			
			final String statoAutorizzazione = AutorizzazioneUtilities.STATO_ABILITATO;
			final String autorizzazioneAutenticati = ServletUtils.boolToCheckBoxStatus(authzAbilitata.isRichiedente());
			final String autorizzazioneRuoli = ServletUtils.boolToCheckBoxStatus(authzAbilitata.isRuoli());
			final String autorizzazioneScope = ServletUtils.boolToCheckBoxStatus(authzAbilitata.isScope());
			final String autorizzazione_tokenOptions = authzAbilitata.getTokenClaims();
			final RuoloTipologia tipoRuoloFonte = Enums.ruoloTipologiaFromRest.get(authzAbilitata.getRuoliFonte());
			final RuoloTipoMatch tipoRuoloMatch = RuoloTipoMatch.toEnumConstant( evalnull( () -> authzAbilitata.getRuoliRichiesti().toString()) );	// Gli enum coincidono
			final ScopeTipoMatch scopeTipoMatch = ScopeTipoMatch.toEnumConstant( evalnull( () -> authzAbilitata.getScopeRichiesti().toString()) );
			
			final String tipoAutorString =	AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString(statoAutorizzazione,
					ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticati),
					ServletUtils.isCheckBoxEnabled(autorizzazioneRuoli),		
					ServletUtils.isCheckBoxEnabled(autorizzazioneScope),		
					autorizzazione_tokenOptions,					// Questo è il token claims
					tipoRuoloFonte									// RuoliFonte: Qualsiasi, Registro, Esterna
				);
			
			newPa.setAutorizzazione(tipoAutorString);
			
			if ( authzAbilitata.isRuoli() ) {
				if ( newPa.getRuoli() == null) newPa.setRuoli(new AutorizzazioneRuoli());
				
				newPa.getRuoli().setMatch(tipoRuoloMatch);
			} else {
				newPa.setRuoli(null);
			}
			
			
			if ( authzAbilitata.isScope() ) {
				if ( newPa.getScope() == null ) newPa.setScope(new AutorizzazioneScope());
				
				newPa.getScope().setMatch(scopeTipoMatch);
				newPa.getScope().setStato(StatoFunzionalita.ABILITATO);
			} else {
				newPa.setScope(null);
			}
			
			
			if ( authzAbilitata.isToken() ) {
				if ( newPa.getGestioneToken() == null ) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Nessun token configurato per l'erogazione");
				}
				newPa.getGestioneToken().setOptions(autorizzazione_tokenOptions);
			}
			else {
				BaseHelper.runNull( () -> newPa.getGestioneToken().setOptions(null) );
			}
			break;
		}
		case XACML_POLICY: {
			
			if(! (authz instanceof APIImplAutorizzazioneXACML)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione dell'autorizzazione '"+authz.getClass().getName()+"' non compatibile con il tipo impostato '"+authz.getTipo()+"'");
        	}
			APIImplAutorizzazioneXACML authzXacml = (APIImplAutorizzazioneXACML) authz;
			
			if (authzXacml.getPolicy() == null) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Campo obbligatorio 'policy' non presente nell'autorizzazione indicata");
			}
			
			if (authzXacml.getRuoliFonte() == null)
				authzXacml.setRuoliFonte(FonteEnum.QUALSIASI);
			
			final RuoloTipologia tipoRuoloFonte = Enums.ruoloTipologiaFromRest.get(authzXacml.getRuoliFonte());
			
			final String tipoAutorString =	AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString(AutorizzazioneUtilities.STATO_XACML_POLICY,
					false,
					false,		
					false,
					"",
					tipoRuoloFonte									// RuoliFonte: Qualsiasi, Registro, Esterna
				);
			
			
			newPa.setAutorizzazione(tipoAutorString);
			newPa.setXacmlPolicy( evalnull( () -> new String(authzXacml.getPolicy())));				
			break;
		}
		case CUSTOM:
			
			if(! (authz instanceof APIImplAutorizzazioneCustom)) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Configurazione dell'autorizzazione '"+authz.getClass().getName()+"' non compatibile con il tipo impostato '"+authz.getTipo()+"'");
        	}
			APIImplAutorizzazioneCustom authzACustom = (APIImplAutorizzazioneCustom) authz;
			
			if (StringUtils.isEmpty(authzACustom.getNome())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException(APIImplAutorizzazioneCustom.class.getName()+": Indicare il campo obbligatorio 'nome' ");
			}
			newPa.setAutorizzazione(authzACustom.getNome());
			break;
		}
	}
	
	



	public static final TipoAutorizzazioneEnum getTipoAutorizzazione(final String tipo_autorizzazione_pa) {
		TipoAutorizzazioneEnum tipoAuthz = null;
		
		if (TipoAutorizzazione.toEnumConstant(tipo_autorizzazione_pa) == null) {
			tipoAuthz = TipoAutorizzazioneEnum.CUSTOM;
		} else {
			String stato_auth = AutorizzazioneUtilities.convertToStato(tipo_autorizzazione_pa);
			
			if (AutorizzazioneUtilities.STATO_ABILITATO.equals(stato_auth))
				tipoAuthz = TipoAutorizzazioneEnum.ABILITATO;
			
			else if ( AutorizzazioneUtilities.STATO_DISABILITATO.equals(stato_auth) )
				tipoAuthz = TipoAutorizzazioneEnum.DISABILITATO;
			
			else if ( AutorizzazioneUtilities.STATO_XACML_POLICY.equals(stato_auth) )
				tipoAuthz = TipoAutorizzazioneEnum.XACML_POLICY;
			else 
				throw FaultCode.ERRORE_INTERNO.toException("Stato autorizzazione " + stato_auth + " sconosciuto");
		}
		return tipoAuthz;
	}



	public static final ControlloAccessiAutorizzazioneView controlloAccessiAutorizzazioneFromPA(final PortaApplicativa pa) {
		ControlloAccessiAutorizzazioneView ret = new ControlloAccessiAutorizzazioneView();
		
		TipoAutorizzazioneEnum tipoAuthz = getTipoAutorizzazione(pa.getAutorizzazione());		
	
		OneOfControlloAccessiAutorizzazioneViewAutorizzazione retAuthz = null;
		
		switch ( tipoAuthz ) {
		case ABILITATO: {
			APIImplAutorizzazioneAbilitata authzAbilitata = new APIImplAutorizzazioneAbilitata();
			authzAbilitata.setTipo(tipoAuthz);
			retAuthz = authzAbilitata;
			
			authzAbilitata.setRichiedente(TipoAutorizzazione.isAuthenticationRequired(pa.getAutorizzazione()));
			
			authzAbilitata.setRuoli( TipoAutorizzazione.isRolesRequired(pa.getAutorizzazione()) );
			if (authzAbilitata.isRuoli()) {
			
				authzAbilitata.setRuoliRichiesti( evalnull(  () -> AllAnyEnum.fromValue( pa.getRuoli().getMatch().toString()) ) );
				authzAbilitata.setRuoliFonte( evalnull( () -> 
						Enums.registroTipologiaToApiFonte(	AutorizzazioneUtilities.convertToRuoloTipologia(pa.getAutorizzazione()) )
					));
			}
			
			
			authzAbilitata.setScope( pa.getScope() != null );
			authzAbilitata.setScopeRichiesti( evalnull( () -> AllAnyEnum.fromValue( pa.getScope().getMatch().getValue() )));
			
			authzAbilitata.setToken( (pa.getGestioneToken()!=null && pa.getGestioneToken().getOptions() != null) ? true : false);
			authzAbilitata.setTokenClaims( evalnull( () -> pa.getGestioneToken().getOptions() ));
			
			break;
		}
		case XACML_POLICY: {
			APIImplAutorizzazioneXACMLView authzXacml = new APIImplAutorizzazioneXACMLView();
			authzXacml.setTipo(tipoAuthz);
			retAuthz = authzXacml;
			
			authzXacml.setRuoliFonte( evalnull( () -> 
					Enums.registroTipologiaToApiFonte(	AutorizzazioneUtilities.convertToRuoloTipologia(pa.getAutorizzazione()) )
				));

			break;
		}
		case CUSTOM: {
			APIImplAutorizzazioneCustom authzCustom = new APIImplAutorizzazioneCustom();
			authzCustom.setTipo(tipoAuthz);
			retAuthz = authzCustom;
			
			authzCustom.setNome(pa.getAutorizzazione());

			break;
		}
		case DISABILITATO: {
			APIImplAutorizzazioneDisabilitata authzDisabilitata = new APIImplAutorizzazioneDisabilitata();
			authzDisabilitata.setTipo(tipoAuthz);
			retAuthz = authzDisabilitata;
			
			break;
		}
		}
		
		ret.setAutorizzazione(retAuthz);
		return ret;
	}
	
	public static final ControlloAccessiAutorizzazioneView controlloAccessiAutorizzazioneFromPD(final PortaDelegata pd) {
		ControlloAccessiAutorizzazioneView ret = new ControlloAccessiAutorizzazioneView();
		
		TipoAutorizzazioneEnum tipoAuthz = getTipoAutorizzazione(pd.getAutorizzazione());		
	
		OneOfControlloAccessiAutorizzazioneViewAutorizzazione retAuthz = null;
		
		switch ( tipoAuthz ) {
		case ABILITATO: {
			APIImplAutorizzazioneAbilitata authzAbilitata = new APIImplAutorizzazioneAbilitata();
			authzAbilitata.setTipo(tipoAuthz);
			retAuthz = authzAbilitata;
			
			authzAbilitata.setRichiedente( TipoAutorizzazione.isAuthenticationRequired(pd.getAutorizzazione()));
			
			
			authzAbilitata.setRuoli( TipoAutorizzazione.isRolesRequired(pd.getAutorizzazione()) );
			if (authzAbilitata.isRuoli()) {
				authzAbilitata.setRuoliRichiesti( evalnull(  () -> AllAnyEnum.fromValue( pd.getRuoli().getMatch().toString()) ) );
				authzAbilitata.setRuoliFonte( evalnull( () -> 
						Enums.registroTipologiaToApiFonte(	AutorizzazioneUtilities.convertToRuoloTipologia(pd.getAutorizzazione()) )
					));
			}
			
			
			authzAbilitata.setScope( pd.getScope() != null );
			authzAbilitata.setScopeRichiesti( evalnull( () -> AllAnyEnum.fromValue( pd.getScope().getMatch().getValue() )));
			
			authzAbilitata.setToken( (pd.getGestioneToken()!=null && pd.getGestioneToken().getOptions() != null) ? true : false);
			authzAbilitata.setTokenClaims( evalnull( () -> pd.getGestioneToken().getOptions() ));

			break;
		}
		case XACML_POLICY: {
			APIImplAutorizzazioneXACMLView authzXacml = new APIImplAutorizzazioneXACMLView();
			authzXacml.setTipo(tipoAuthz);
			retAuthz = authzXacml;
			
			authzXacml.setRuoliFonte( evalnull( () -> 
					Enums.registroTipologiaToApiFonte(	AutorizzazioneUtilities.convertToRuoloTipologia(pd.getAutorizzazione()) )
				));

			break;
		}
		case CUSTOM: {
			APIImplAutorizzazioneCustom authzCustom = new APIImplAutorizzazioneCustom();
			authzCustom.setTipo(tipoAuthz);
			retAuthz = authzCustom;
			
			authzCustom.setNome(pd.getAutorizzazione());

			break;
		}
		case DISABILITATO: {
			APIImplAutorizzazioneDisabilitata authzDisabilitata = new APIImplAutorizzazioneDisabilitata();
			authzDisabilitata.setTipo(tipoAuthz);
			retAuthz = authzDisabilitata;
			
			break;
		}
		}
		
		ret.setAutorizzazione(retAuthz);
		return ret;
	}



	public static final void fillPortaApplicativa(final ErogazioniEnv env, ControlloAccessiAutenticazione body, final PortaApplicativa newPa) throws InstantiationException, IllegalAccessException {
		final OneOfControlloAccessiAutenticazioneAutenticazione auth = body.getAutenticazione();
		final Boolean autenticazioneOpzionale = getAutenticazioneOpzionale(auth); // gestisce auth se null
		
		newPa.setAutenticazioneOpzionale( evalnull( () -> Helper.boolToStatoFunzionalitaConf(autenticazioneOpzionale)) );
		newPa.setAutenticazione( evalnull( () -> Enums.tipoAutenticazioneFromRest.get(auth.getTipo()).toString()) );
		
        TipoAutenticazionePrincipal autenticazionePrincipal = null;
        List<String> autenticazioneParametroList = null;
        if(auth!=null) {
        	autenticazionePrincipal = getTipoAutenticazionePrincipal(auth); 
        	autenticazioneParametroList = getAutenticazioneParametroList(env, auth.getTipo(), auth);
        }
		List<Proprieta> proprietaAutenticazione = env.paCore.convertToAutenticazioneProprieta(newPa.getAutenticazione(), autenticazionePrincipal, autenticazioneParametroList);
		newPa.setProprietaAutenticazioneList(proprietaAutenticazione);
		
		// Imposto l'autenticazione custom
		if ( evalnull( () -> auth.getTipo() ) == TipoAutenticazioneEnum.CUSTOM) {
			if(auth instanceof APIImplAutenticazioneCustom) {
				APIImplAutenticazioneCustom authnCustom = (APIImplAutenticazioneCustom) auth;
				newPa.setAutenticazione(authnCustom.getNome());
			}
			else {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("La configurazione dell'autenticazione '"+auth.getTipo()+"' non è correttamente definita (trovata configurazione '"+auth.getClass().getName()+"')"  );
			}
		}
		// Gestione Token
		final ControlloAccessiAutenticazioneToken gToken = body.getToken();
		if (gToken != null) {
			final boolean isGestioneToken = gToken.isClientId() || gToken.isEmail() || gToken.isIssuer() || gToken.isSubject() || gToken.isUsername();
			
			if (isGestioneToken) {
				if(newPa.getGestioneToken() == null)
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("La gestione token non è abilitata per il gruppo");
				
				if(newPa.getGestioneToken().getAutenticazione()==null) {
					newPa.getGestioneToken().setAutenticazione(new GestioneTokenAutenticazione());
				}
				
				newPa.getGestioneToken().getAutenticazione().setIssuer(Helper.boolToStatoFunzionalitaConf(gToken.isIssuer())); 
				newPa.getGestioneToken().getAutenticazione().setClientId(Helper.boolToStatoFunzionalitaConf(gToken.isClientId())); 
				newPa.getGestioneToken().getAutenticazione().setSubject(Helper.boolToStatoFunzionalitaConf(gToken.isSubject())); 
				newPa.getGestioneToken().getAutenticazione().setUsername(Helper.boolToStatoFunzionalitaConf(gToken.isUsername())); 
				newPa.getGestioneToken().getAutenticazione().setEmail(Helper.boolToStatoFunzionalitaConf(gToken.isEmail()));	
			}
		}
		
	}
	
	
	public static final void fillPortaDelegata(final ErogazioniEnv env, ControlloAccessiAutenticazione body, final PortaDelegata newPd) {
		final OneOfControlloAccessiAutenticazioneAutenticazione auth = body.getAutenticazione();
		final Boolean autenticazioneOpzionale = getAutenticazioneOpzionale(auth); // gestisce auth se null
		
		newPd.setAutenticazioneOpzionale( evalnull( () -> Helper.boolToStatoFunzionalitaConf(autenticazioneOpzionale)) );
		newPd.setAutenticazione( evalnull( () -> Enums.tipoAutenticazioneFromRest.get(auth.getTipo()).toString()) );
		
		TipoAutenticazionePrincipal autenticazionePrincipal = null;
        List<String> autenticazioneParametroList = null;
        if(auth!=null) {
        	autenticazionePrincipal = getTipoAutenticazionePrincipal(auth); 
        	autenticazioneParametroList = getAutenticazioneParametroList(env, auth.getTipo(), auth);
        }
		List<Proprieta> proprietaAutenticazione = env.paCore.convertToAutenticazioneProprieta(newPd.getAutenticazione(), autenticazionePrincipal, autenticazioneParametroList);
		newPd.setProprietaAutenticazioneList(proprietaAutenticazione);
		
		if ( evalnull( () -> auth.getTipo() ) == TipoAutenticazioneEnum.CUSTOM) {
			if(auth instanceof APIImplAutenticazioneCustom) {
				APIImplAutenticazioneCustom authnCustom = (APIImplAutenticazioneCustom) auth;
				newPd.setAutenticazione(authnCustom.getNome());
			}
			else {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("La configurazione dell'autenticazione '"+auth.getTipo()+"' non è correttamente definita (trovata configurazione '"+auth.getClass().getName()+"')"  );
			}
		}
		// Gestione Token
		final ControlloAccessiAutenticazioneToken gToken = body.getToken();
		if (gToken != null) {
			final boolean isGestioneToken = gToken.isClientId() || gToken.isEmail() || gToken.isIssuer() || gToken.isSubject() || gToken.isUsername();
	
			if (isGestioneToken) {
				if(newPd.getGestioneToken() == null)
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("La gestione token non è abilitata per il gruppo");
				
				if(newPd.getGestioneToken().getAutenticazione()==null) {
					newPd.getGestioneToken().setAutenticazione(new GestioneTokenAutenticazione());
				}
				
				newPd.getGestioneToken().getAutenticazione().setIssuer(Helper.boolToStatoFunzionalitaConf(gToken.isIssuer())); 
				newPd.getGestioneToken().getAutenticazione().setClientId(Helper.boolToStatoFunzionalitaConf(gToken.isClientId())); 
				newPd.getGestioneToken().getAutenticazione().setSubject(Helper.boolToStatoFunzionalitaConf(gToken.isSubject())); 
				newPd.getGestioneToken().getAutenticazione().setUsername(Helper.boolToStatoFunzionalitaConf(gToken.isUsername())); 
				newPd.getGestioneToken().getAutenticazione().setEmail(Helper.boolToStatoFunzionalitaConf(gToken.isEmail()));
			}
		}
		
	}



	public static final IDSoggetto getIdReferente(APIImpl body, final ErogazioniEnv env)
			throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		IDSoggetto idReferente = null;
		 
		if( env.apcCore.isSupportatoSoggettoReferente(env.tipo_protocollo) ){
			if ( body.getApiReferente() == null )
				body.setApiReferente(env.idSoggetto.getNome());
			
			idReferente = new IDSoggetto(env.tipo_soggetto,body.getApiReferente());
		}
	
		else {
			idReferente = env.soggettiCore.getSoggettoOperativoDefault(env.userLogin,env.tipo_protocollo);
		}
		return idReferente;
	}



	public static final GestioneCors convert(final CorsConfigurazione paConf) {
		final GestioneCors ret = new GestioneCors();
		
		if (paConf != null) {
			ret.setTipo( Enums.dualizeMap(Enums.tipoGestioneCorsFromRest).get(paConf.getTipo()));
			ret.setRidefinito(true);
			
			if ( ret.getTipo() == TipoGestioneCorsEnum.GATEWAY ) {

				GestioneCorsAccessControl opts = new GestioneCorsAccessControl();
				
				opts.setAllAllowOrigins( Helper.statoFunzionalitaConfToBool( paConf.getAccessControlAllAllowOrigins() ));
				opts.setAllowCredentials(  Helper.statoFunzionalitaConfToBool( paConf.getAccessControlAllowCredentials() ));
				opts.setAllowHeaders( evalnull( () -> paConf.getAccessControlAllowHeaders().getHeaderList()) );
				opts.setAllowMethods( evalnull( () -> 
						paConf.getAccessControlAllowMethods().getMethodList().stream().map(HttpMethodEnum::valueOf).collect(Collectors.toList())
						));
				opts.setAllowOrigins( evalnull( () -> paConf.getAccessControlAllowOrigins().getOriginList()));	
				opts.setExposeHeaders( evalnull( () -> paConf.getAccessControlExposeHeaders().getHeaderList()) );
				
				ret.setAccessControl(opts);
			}
			

		}
		else {
			ret.setRidefinito(false);
		}
		return ret;
	}
	
	// CONVERSIONI POLICY RAGGRUPPAMENTO
	
	public static final RateLimitingPolicyGroupBy convert( AttivazionePolicyRaggruppamento src, RateLimitingPolicyGroupBy dest ) {
		
		dest.setAzione(src.isAzione());
		
		if(src.isServizioApplicativoFruitore() || src.isFruitore() || src.isIdentificativoAutenticato()) {
			dest.setRichiedente(true);
		}
		
		if(src.getToken()!=null) {
			String [] tmp = src.getToken().split(",");
			if(tmp!=null && tmp.length>0) {
				List<TokenClaimEnum> token = new ArrayList<>();
				for (int i = 0; i < tmp.length; i++) {
					TipoCredenzialeMittente tipo = TipoCredenzialeMittente.valueOf(tmp[i]);
					switch (tipo) {
					case token_subject:
						token.add(TokenClaimEnum.SUBJECT);
						break;
					case token_issuer:
						token.add(TokenClaimEnum.ISSUER);
						break;
					case token_clientId:
						token.add(TokenClaimEnum.CLIENT_ID);
						break;
					case token_username:
						token.add(TokenClaimEnum.USERNAME);
						break;
					case token_eMail:
						token.add(TokenClaimEnum.EMAIL);
						break;
					default:
						break;
					}
				}
				dest.setToken(token);
			}
		}
		
		dest.setChiaveNome(src.getInformazioneApplicativaNome());
		dest.setChiaveTipo(
				Enums.rateLimitingChiaveEnum.get( TipoFiltroApplicativo.toEnumConstant(src.getInformazioneApplicativaTipo()) )
			);
		
		return dest;
	}
	
	
	
	// CONVERSIONI POLICY FILTRO
	
	public static final RateLimitingPolicyFiltro  convert ( AttivazionePolicyFiltro src, RateLimitingPolicyFiltro dest ) {
		
		dest.setApplicativoFruitore( src.getServizioApplicativoFruitore() ); 
		
		if(src.getAzione()!=null && !"".equals(src.getAzione())) {
			String [] tmp = src.getAzione().split(",");
			if(tmp!=null && tmp.length>0) {
				List<String> azione = new ArrayList<>();
				for (int i = 0; i < tmp.length; i++) {
					azione.add(tmp[i]);
				}
				dest.setAzione(azione);
			}
		}
		
		dest.setRuoloRichiedente(src.getRuoloFruitore());
		
		dest.setChiaveNome(src.getInformazioneApplicativaNome());
		dest.setChiaveTipo(
				Enums.rateLimitingChiaveEnum.get( TipoFiltroApplicativo.toEnumConstant(src.getInformazioneApplicativaTipo()) )
			);
				
		dest.setFiltroChiaveValore(src.getInformazioneApplicativaValore());
		
		return dest;
	}
	
	
	public static final RateLimitingPolicyFiltroErogazione  convert ( AttivazionePolicyFiltro src, RateLimitingPolicyFiltroErogazione dest ) {
		convert( src, (RateLimitingPolicyFiltro) dest);
		dest.setSoggettoFruitore(src.getNomeFruitore());
			
		return dest;
	}
	
	
	public static final RateLimitingPolicyFiltroFruizione  convert ( AttivazionePolicyFiltro src, RateLimitingPolicyFiltroFruizione dest ) {
		convert(src, (RateLimitingPolicyFiltro) dest);
		return dest;
	}
	
	
	// ATTIVAZIONE POLICY Conversion
	
	public static final RateLimitingPolicyFruizioneView convert ( AttivazionePolicy src, InfoPolicy infoPolicy, RateLimitingPolicyFruizioneView dest ) {
		
		convert( src, infoPolicy, (RateLimitingPolicyBase) dest );

		dest.setDescrizione(infoPolicy.getDescrizione());
		dest.setNome( PolicyUtilities.getNomeActivePolicy(src.getAlias(), src.getIdActivePolicy()));
				
		dest.setRaggruppamento(
				convert( src.getGroupBy(), new RateLimitingPolicyGroupBy() )
			);
	 
		
		dest.setFiltro(
				convert( src.getFiltro(), new RateLimitingPolicyFiltroFruizione() )
			);
		
		return dest;
	}

	
	
	public static final RateLimitingPolicyErogazioneView convert ( AttivazionePolicy src, InfoPolicy infoPolicy, RateLimitingPolicyErogazioneView dest ) {
		
		convert( src, infoPolicy, (RateLimitingPolicyBase) dest );

		dest.setDescrizione(infoPolicy.getDescrizione());
		dest.setNome( PolicyUtilities.getNomeActivePolicy(src.getAlias(), src.getIdActivePolicy()));
		
		dest.setRaggruppamento(
				convert( src.getGroupBy(), new RateLimitingPolicyGroupBy() )
			);
	 
		
		dest.setFiltro(
				convert( src.getFiltro(), new RateLimitingPolicyFiltroErogazione() )
			);
		
		return dest;
	}
	
	
	public static final RateLimitingPolicyBase convert ( AttivazionePolicy src, InfoPolicy infoPolicy, RateLimitingPolicyBase dest ) {
	
		dest.setNome(src.getAlias());
		dest.setSogliaRidefinita(src.isRidefinisci());
		
		if ( src.isRidefinisci()) {
			dest.setSogliaValore(src.getValore().intValue());
		}
		else {
			dest.setSogliaValore(infoPolicy.getValore().intValue());
		}
		
		if ( src.isWarningOnly() )
			dest.setStato( StatoFunzionalitaConWarningEnum.WARNINGONLY );
		
		if ( src.isEnabled() )
			dest.setStato(StatoFunzionalitaConWarningEnum.ABILITATO );
		else
			dest.setStato(StatoFunzionalitaConWarningEnum.DISABILITATO );
		
		if(dest instanceof RateLimitingPolicyBaseConIdentificazione) {
			RateLimitingPolicyBaseConIdentificazione destIdentificazione = (RateLimitingPolicyBaseConIdentificazione) dest;
			
			if(infoPolicy.isBuiltIn()) {
				RateLimitingPolicyCriteri criteri = new RateLimitingPolicyCriteri();
				criteri.setIdentificazione(RateLimitingIdentificazionePolicyEnum.CRITERI);
				boolean intervallo = true;
				switch (infoPolicy.getTipoRisorsa()) {
				case NUMERO_RICHIESTE:
					if(infoPolicy.isCheckRichiesteSimultanee()) {
						criteri.setMetrica(RateLimitingCriteriMetricaEnum.NUMERO_RICHIESTE_SIMULTANEE);
						intervallo = false;
					}
					else {
						criteri.setMetrica(RateLimitingCriteriMetricaEnum.NUMERO_RICHIESTE);
					}
					break;
				case NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO:
					criteri.setMetrica(RateLimitingCriteriMetricaEnum.NUMERO_RICHIESTE_OK);
					break;
				case NUMERO_RICHIESTE_FALLITE:
					criteri.setMetrica(RateLimitingCriteriMetricaEnum.NUMERO_RICHIESTE_FALLITE);
					break;
				case NUMERO_FAULT_APPLICATIVI:
					criteri.setMetrica(RateLimitingCriteriMetricaEnum.NUMERO_FAULT_APPLICATIVI);
					break;
				case NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI:
					criteri.setMetrica(RateLimitingCriteriMetricaEnum.NUMERO_RICHIESTE_FALLITE_O_FAULT_APPLICATIVI);
					break;
				case OCCUPAZIONE_BANDA:
					criteri.setMetrica(RateLimitingCriteriMetricaEnum.OCCUPAZIONE_BANDA);
					break;
				case TEMPO_MEDIO_RISPOSTA:
					criteri.setMetrica(RateLimitingCriteriMetricaEnum.TEMPO_MEDIO_RISPOSTA);
					break;
				case TEMPO_COMPLESSIVO_RISPOSTA:
					criteri.setMetrica(RateLimitingCriteriMetricaEnum.TEMPO_COMPLESSIVO_RISPOSTA);
					break;
				}
				if(intervallo) {
					switch (infoPolicy.getIntervalloUtilizzaRisorseRealtimeTipoPeriodo()) {
					case MINUTI:
						criteri.setIntervallo(RateLimitingCriteriIntervalloEnum.MINUTI);
						break;
					case ORARIO:
						criteri.setIntervallo(RateLimitingCriteriIntervalloEnum.ORARIO);
						break;
					case GIORNALIERO:
						criteri.setIntervallo(RateLimitingCriteriIntervalloEnum.GIORNALIERO);
						break;
					default:
						break;
					}
				}
				criteri.setCongestione(infoPolicy.isControlloCongestione());
				criteri.setDegrado(infoPolicy.isDegradoPrestazione());
				destIdentificazione.setConfigurazione(criteri);
			}
			else {
				RateLimitingPolicyIdentificativo id = new RateLimitingPolicyIdentificativo();
				id.setIdentificazione(RateLimitingIdentificazionePolicyEnum.POLICY);
				id.setPolicy(infoPolicy.getIdPolicy());
				destIdentificazione.setConfigurazione(id);
			}
		}
		
		return dest;
			
	}


	public static final void checkAzioniAdd( List<String> toAdd, List<String> occupate, List<String> presenti ) {
		
		for ( String azione : toAdd ) {
			
			if( occupate.contains(azione) ) {
				throw FaultCode.CONFLITTO.toException(StringEscapeUtils.unescapeHtml(CostantiControlStation.MESSAGGIO_ERRORE_AZIONE_PORTA_GIA_PRESENTE));			
			}
			
			if( !presenti.contains(azione) ) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Azione " + azione + " non presente fra le azioni dell'accordo");
			}
		}
		
	}


	public static final String getDataElementModalita(RateLimitingIdentificazionePolicyEnum identificazione) {
		switch (identificazione) {
		case POLICY:
			return ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_CUSTOM;
		case CRITERI:
			return ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_MODALITA_BUILT_IN;
		}
		return null;
	}
	public static final String getDataElementModalita(boolean builtIt) {
		return builtIt ? getDataElementModalita(RateLimitingIdentificazionePolicyEnum.CRITERI) : getDataElementModalita(RateLimitingIdentificazionePolicyEnum.POLICY);
	}
	
	public static final String getIdPolicy(RateLimitingPolicyBaseConIdentificazione body, ConfigurazioneCore confCore, ConfigurazioneHelper confHelper) throws Exception {
		
		String idPolicy = null;
		switch (body.getConfigurazione().getIdentificazione()) {
		case POLICY:
			RateLimitingPolicyIdentificativo identificativo = new RateLimitingPolicyIdentificativo();
			identificativo = BaseHelper.deserialize(body.getConfigurazione(), RateLimitingPolicyIdentificativo.class);
        	idPolicy = identificativo.getPolicy();
			break;
		case CRITERI:
			RateLimitingPolicyCriteri criteri = new RateLimitingPolicyCriteri();
			criteri = BaseHelper.deserialize(body.getConfigurazione(), RateLimitingPolicyCriteri.class);
        	
        	String modalitaRisorsa = getDataElementModalitaRisorsa(criteri.getMetrica());
        	boolean modalitaSimultaneeEnabled = confHelper.isTipoRisorsaNumeroRichiesteSimultanee(modalitaRisorsa);
        	String modalitaEsiti = null;
        	String modalitaIntervallo = null;
        	if(criteri.getIntervallo()!=null) {
        		modalitaIntervallo = getDataElementModalitaIntervallo(criteri.getIntervallo());
        	}
        	boolean modalitaCongestioneEnabled = criteri.isCongestione()!=null ? criteri.isCongestione() : false;
        	boolean modalitaDegradoEnabled = criteri.isDegrado()!=null ? criteri.isDegrado() : false;
        	boolean modalitaErrorRateEnabled = false;
        	
        	List<InfoPolicy> infoPolicies = confCore.infoPolicyList(true);
        	List<InfoPolicy> idPoliciesSoddisfanoCriteri = new ArrayList<>();
        	confHelper.findPolicyBuiltIn(infoPolicies, idPoliciesSoddisfanoCriteri, 
        			modalitaRisorsa, modalitaEsiti, modalitaSimultaneeEnabled, modalitaIntervallo, 
        			modalitaCongestioneEnabled, modalitaDegradoEnabled, modalitaErrorRateEnabled);
        	if(idPoliciesSoddisfanoCriteri.size()>0) {
        		idPolicy = idPoliciesSoddisfanoCriteri.get(0).getIdPolicy();
        	}
			break;
		}
		
		return idPolicy;
	}
	
	public static String getDataElementModalitaRisorsa(RateLimitingCriteriMetricaEnum risorsa) {
		TipoRisorsaPolicyAttiva tipoRisorsaPolicyAttiva = null;
		switch (risorsa) {
		case NUMERO_RICHIESTE:
			tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE;
			break;
		case NUMERO_RICHIESTE_SIMULTANEE:
			tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_SIMULTANEE;
			break;
		case NUMERO_RICHIESTE_OK:
			tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_COMPLETATE_CON_SUCCESSO;
			break;
		case NUMERO_RICHIESTE_FALLITE:
			tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE;
			break;
		case NUMERO_FAULT_APPLICATIVI:
			tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.NUMERO_FAULT_APPLICATIVI;
			break;
		case NUMERO_RICHIESTE_FALLITE_O_FAULT_APPLICATIVI:
			tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.NUMERO_RICHIESTE_FALLITE_OFAULT_APPLICATIVI;
			break;
		case OCCUPAZIONE_BANDA:
			tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.OCCUPAZIONE_BANDA;
			break;
		case TEMPO_MEDIO_RISPOSTA:
			tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.TEMPO_MEDIO_RISPOSTA;
			break;
		case TEMPO_COMPLESSIVO_RISPOSTA:
			tipoRisorsaPolicyAttiva = TipoRisorsaPolicyAttiva.TEMPO_COMPLESSIVO_RISPOSTA;
			break;
		}
		return tipoRisorsaPolicyAttiva.getValue();
	}
	private static String getDataElementModalitaIntervallo(RateLimitingCriteriIntervalloEnum intervallo) {
    	String modalitaRisorsaEsiti = null;
		switch (intervallo) {
		case MINUTI:
			modalitaRisorsaEsiti = TipoPeriodoRealtime.MINUTI.getValue();
			break;
		case ORARIO:
			modalitaRisorsaEsiti = TipoPeriodoRealtime.ORARIO.getValue();
			break;
		case GIORNALIERO:
			modalitaRisorsaEsiti = TipoPeriodoRealtime.GIORNALIERO.getValue();
			break;
		}
		return modalitaRisorsaEsiti;
	}
	
	
	public static final void override( String idPolicy, RateLimitingPolicyErogazione body, String protocollo, IDSoggetto idPropietarioSa, HttpRequestWrapper wrap ) {
		if (body == null) return;

		override ( (RateLimitingPolicyErogazione) body, protocollo, idPropietarioSa, wrap );
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ID, 
				idPolicy	
			);		
	}
	

	public static final void override( String idPolicy, RateLimitingPolicyFruizione body, String protocollo, IDSoggetto idPropietarioSa, HttpRequestWrapper wrap ) {
		if (body == null) return;

		override ( (RateLimitingPolicyFruizione) body, protocollo, idPropietarioSa, wrap );
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ID, 
				idPolicy
			);
	}
	
	public static final void override( RateLimitingPolicyFiltro body, IDSoggetto idPropietarioSa, HttpRequestWrapper wrap ) {
		if (body == null) return;
		
		if(body.getAzione()!=null && !body.getAzione().isEmpty()) {
		
			wrap.overrideParameterValues(
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE,
					body.getAzione().toArray(new String[1])
				);
		
		}
		
		wrap.overrideParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_FRUITORE, body.getApplicativoFruitore());
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_FRUITORE,
				evalnull( () -> body.getRuoloRichiedente() )  
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_ENABLED,
				evalnull( () -> ServletUtils.boolToCheckBoxStatus( body.getChiaveTipo() != null ))	// TOWAIT: mailandrea, non ho in rest un valore per la checkbox isFiltroAbilitato, quindi deduco il valore della checkbox così 
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_TIPO,
				evalnull( () -> Enums.tipoFiltroApplicativo.get( body.getChiaveTipo() ).toString())	
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_NOME,
				evalnull( () -> body.getChiaveNome() )  
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_VALORE,
				evalnull( () -> body.getFiltroChiaveValore() ) 
			);	
	}
	
	public static final void override(	RateLimitingPolicyGroupBy body,  IDSoggetto idPropietarioSa, HttpRequestWrapper wrap )
	{
		if (body == null) return;

	
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_ENABLED,
				body != null 
					?  ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_COLLEZIONAMENTO_ABILITATO
					:  ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_COLLEZIONAMENTO_DISABILITATO
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_AZIONE,
				evalnull(  () -> ServletUtils.boolToCheckBoxStatus( body.isAzione() ) )
			);	
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_RICHIEDENTE,
				evalnull(  () -> ServletUtils.boolToCheckBoxStatus( body.isRichiedente() ) )
			);
		
		if(body.getToken()!=null && !body.getToken().isEmpty()) {
			
			wrap.overrideParameter(
					ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_TOKEN,
					evalnull(  () -> ServletUtils.boolToCheckBoxStatus( true ) )
				);
			
			List<String> values = new ArrayList<>();
			for (TokenClaimEnum tokenClaim : body.getToken()) {
				switch (tokenClaim) {
				case SUBJECT:
					values.add(TipoCredenzialeMittente.token_subject.name());
					break;
				case ISSUER:
					values.add(TipoCredenzialeMittente.token_issuer.name());
					break;
				case CLIENT_ID:
					values.add(TipoCredenzialeMittente.token_clientId.name());
					break;
				case USERNAME:
					values.add(TipoCredenzialeMittente.token_username.name());
					break;
				case EMAIL:
					values.add(TipoCredenzialeMittente.token_eMail.name());
					break;
				default:
					break;
				}
			}
			if(!values.isEmpty()) {
				wrap.overrideParameterValues(
						ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_TOKEN_CLAIMS,
						values.toArray(new String[1])
					);
			}
		}		
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_ENABLED,
				evalnull(  () -> ServletUtils.boolToCheckBoxStatus( body.getChiaveTipo() != null ) )  
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_TIPO,
				evalnull( () -> Enums.tipoFiltroApplicativo.get(body.getChiaveTipo()).toString() )
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_NOME,
				evalnull( () -> body.getChiaveNome() )
			); 
	}
	
	public static final void override( RateLimitingPolicyErogazioneUpdate body, String protocollo, IDSoggetto idPropietarioSa, HttpRequestWrapper wrap ) {
		if (body == null) return;

		override( (RateLimitingPolicyBase) body, protocollo, idPropietarioSa, wrap );
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD,
				RuoloPolicy.APPLICATIVA.toString() 
		);
		
		// Campi in più rispetto al padre:
		RateLimitingPolicyFiltroErogazione filtro = body.getFiltro();
		override( filtro, idPropietarioSa, wrap );
		
		final String filtroFruitore = evalnull(() -> filtro.getSoggettoFruitore()) != null   
				? new IDSoggetto(idPropietarioSa.getTipo(), filtro.getSoggettoFruitore()).toString()
				: null;
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_FRUITORE,
				filtroFruitore 
			);
		
		RateLimitingPolicyGroupBy groupCriteria = body.getRaggruppamento();
		override( groupCriteria, idPropietarioSa, wrap );

	}
	public static final void override( RateLimitingPolicyErogazione body, String protocollo, IDSoggetto idPropietarioSa, HttpRequestWrapper wrap ) {
		if (body == null) return;

		override( (RateLimitingPolicyBase) body, protocollo, idPropietarioSa, wrap );
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD,
				RuoloPolicy.APPLICATIVA.toString() 
		);
		
		// Campi in più rispetto al padre:
		RateLimitingPolicyFiltroErogazione filtro = body.getFiltro();
		override( filtro, idPropietarioSa, wrap );
		
		final String filtroFruitore = evalnull(() -> filtro.getSoggettoFruitore()) != null   
				? new IDSoggetto(idPropietarioSa.getTipo(), filtro.getSoggettoFruitore()).toString()
				: null;
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_FRUITORE,
				filtroFruitore 
			);
		
		RateLimitingPolicyGroupBy groupCriteria = body.getRaggruppamento();
		override( groupCriteria, idPropietarioSa, wrap );

	}
	
	
	
	public static final void override ( RateLimitingPolicyFruizioneUpdate body,  String protocollo, IDSoggetto idPropietarioSa,  HttpRequestWrapper wrap ) {	// Questa è in comune alla update.
		if (body == null) return;

		override ( (RateLimitingPolicyBase) body, protocollo, idPropietarioSa, wrap );
		override ( body.getFiltro(), idPropietarioSa, wrap );
		override ( body.getRaggruppamento() , idPropietarioSa, wrap );
		
		wrap.overrideParameter(
			ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD,
			RuoloPolicy.DELEGATA.toString() 
		);
	}
	public static final void override ( RateLimitingPolicyFruizione body,  String protocollo, IDSoggetto idPropietarioSa,  HttpRequestWrapper wrap ) {	// Questa è in comune alla update.
		if (body == null) return;

		override ( (RateLimitingPolicyBase) body, protocollo, idPropietarioSa, wrap );
		override ( body.getFiltro(), idPropietarioSa, wrap );
		override ( body.getRaggruppamento() , idPropietarioSa, wrap );
		
		wrap.overrideParameter(
			ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD,
			RuoloPolicy.DELEGATA.toString() 
		);
		
	}
		
	public static final void override ( RateLimitingPolicyBase body, String protocollo, IDSoggetto idPropietarioSa, HttpRequestWrapper wrap ) {	// Questa è in comune alla update.		
		if (body == null) return;

		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ALIAS,
				body.getNome()
			);		
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ENABLED,
				body.getStato().toString()
				);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_RIDEFINISCI,
				ServletUtils.boolToCheckBoxStatus( body.isSogliaRidefinita() )
			);
		
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_VALORE, 
				evalnull( () -> body.getSogliaValore().toString() )
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_ENABLED,
				StatoFunzionalita.ABILITATO.toString() 
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PROTOCOLLO,
				protocollo 
			);
	}


	public static final boolean correlazioneApplicativaRichiestaCheckData(
			TipoOperazione tipoOp,
			HttpRequestWrapper wrap,
			ConsoleHelper paHelper,
			boolean isDelegata,
			CorrelazioneApplicativaRichiesta body,
			Long idPorta,
			Long idCorrelazione
			
		) throws Exception {
		
		wrap.overrideParameter( CostantiControlStation.PARAMETRO_ID, idPorta.toString() );
		
		wrap.overrideParameter( CostantiControlStation.PARAMETRO_ELEMENTO_XML, body.getElemento() );		// Campo elemento oppure "Qualsiasi"
		
		wrap.overrideParameter( 
				CostantiControlStation.PARAMETRO_MODE_CORRELAZIONE_APPLICATIVA, 
				CorrelazioneApplicativaRichiestaIdentificazione.valueOf(body.getIdentificazioneTipo().name()).toString()
			);
		
		wrap.overrideParameter( CostantiControlStation.PARAMETRO_PATTERN, body.getIdentificazione() );
		
		wrap.overrideParameter( CostantiControlStation.PARAMETRO_ID_CORRELAZIONE, evalnull( () -> idCorrelazione.toString() ) );					// Questo va impostato nella update.
		
		return paHelper.correlazioneApplicativaRichiestaCheckData(tipoOp,isDelegata);
		
	}
	
	
	public static final CorrelazioneApplicativaElemento convert(CorrelazioneApplicativaRichiesta body) {
		
		CorrelazioneApplicativaElemento cae = new CorrelazioneApplicativaElemento();
		cae.setNome(body.getElemento());
		cae.setIdentificazione(CorrelazioneApplicativaRichiestaIdentificazione.valueOf(body.getIdentificazioneTipo().name()));
		
		if ( body.getIdentificazioneTipo() == CorrelazioneApplicativaRichiestaEnum.URL_BASED 
			||  body.getIdentificazioneTipo() == CorrelazioneApplicativaRichiestaEnum.HEADER_BASED 
			||  body.getIdentificazioneTipo() == CorrelazioneApplicativaRichiestaEnum.CONTENT_BASED	) {
			
			cae.setPattern(body.getIdentificazione());
		}
		
		if ( body.getIdentificazioneTipo() != CorrelazioneApplicativaRichiestaEnum.DISABILITATO ) {
			
			cae.setIdentificazioneFallita(body.isGenerazioneErroreIdentificazioneFallita()
					? CorrelazioneApplicativaGestioneIdentificazioneFallita.BLOCCA 
					: CorrelazioneApplicativaGestioneIdentificazioneFallita.ACCETTA
				);
		}
		return cae;
	}
	
		
	public static final CorrelazioneApplicativaRispostaElemento convert(CorrelazioneApplicativaRisposta body) {

		CorrelazioneApplicativaRispostaElemento cae = new CorrelazioneApplicativaRispostaElemento();
		cae.setNome(body.getElemento());
		cae.setIdentificazione(CorrelazioneApplicativaRispostaIdentificazione.valueOf(body.getIdentificazioneTipo().name()));
		
		if ( 	 body.getIdentificazioneTipo() == CorrelazioneApplicativaRispostaEnum.HEADER_BASED 
			||   body.getIdentificazioneTipo() == CorrelazioneApplicativaRispostaEnum.CONTENT_BASED	) {
			
			cae.setPattern(body.getIdentificazione());
		}
		
		if ( body.getIdentificazioneTipo() != CorrelazioneApplicativaRispostaEnum.DISABILITATO ) {
			
			cae.setIdentificazioneFallita(body.isGenerazioneErroreIdentificazioneFallita()
					? CorrelazioneApplicativaGestioneIdentificazioneFallita.BLOCCA 
					: CorrelazioneApplicativaGestioneIdentificazioneFallita.ACCETTA
				);
		}
		return cae;
	}
	
	
	public static final boolean correlazioneApplicativaRispostaCheckData(
			TipoOperazione tipoOp,
			HttpRequestWrapper wrap,
			ConsoleHelper paHelper,
			boolean isDelegata,
			CorrelazioneApplicativaRisposta body,
			Long idPorta,
			Long idCorrelazione 
		) throws Exception {
		
		wrap.overrideParameter( CostantiControlStation.PARAMETRO_ID, idPorta.toString() );
		
		wrap.overrideParameter( CostantiControlStation.PARAMETRO_ELEMENTO_XML, body.getElemento() );		// Campo elemento oppure "Qualsiasi"
		
		wrap.overrideParameter( 
				CostantiControlStation.PARAMETRO_MODE_CORRELAZIONE_APPLICATIVA, 
				CorrelazioneApplicativaRichiestaIdentificazione.valueOf(body.getIdentificazioneTipo().name()).toString()
			);
		
		wrap.overrideParameter( CostantiControlStation.PARAMETRO_PATTERN, body.getIdentificazione() );
		
		wrap.overrideParameter( CostantiControlStation.PARAMETRO_ID_CORRELAZIONE, evalnull( () -> idCorrelazione.toString() ));
	
		
		return paHelper.correlazioneApplicativaRichiestaCheckData(TipoOperazione.ADD,isDelegata);
		
	}



	public static final CorrelazioneApplicativaRichiesta convert( CorrelazioneApplicativaElemento  src) {
		CorrelazioneApplicativaRichiesta ret = new CorrelazioneApplicativaRichiesta();
		
		ret.setElemento( StringUtils.isEmpty(src.getNome())
				? "*"
				: src.getNome()
			);
		
		ret.setGenerazioneErroreIdentificazioneFallita(src.getIdentificazioneFallita() == CorrelazioneApplicativaGestioneIdentificazioneFallita.ACCETTA
				? false
				: true
			);
		
		ret.setIdentificazione(src.getPattern());
		ret.setIdentificazioneTipo(CorrelazioneApplicativaRichiestaEnum.valueOf(src.getIdentificazione().name()));
		
		return ret;
	}
	
	
	public static final CorrelazioneApplicativaRisposta convert( CorrelazioneApplicativaRispostaElemento  src) {
		CorrelazioneApplicativaRisposta ret = new CorrelazioneApplicativaRisposta();
		
		ret.setElemento( StringUtils.isEmpty(src.getNome())
				? "*"
				: src.getNome()
			);
		
		ret.setGenerazioneErroreIdentificazioneFallita(src.getIdentificazioneFallita() == CorrelazioneApplicativaGestioneIdentificazioneFallita.ACCETTA
				? false
				: true
			);
		
		ret.setIdentificazione(src.getPattern());
		ret.setIdentificazioneTipo(CorrelazioneApplicativaRispostaEnum.valueOf(src.getIdentificazione().name()));
		
		return ret;
	}
	
	public static final boolean isDumpConfigurazioneAbilitato(DumpConfigurazione configurazione, boolean isRisposta) {
		boolean abilitato = false;
		
		if(configurazione == null)
			return false;
		
		if(isRisposta) {
			DumpConfigurazioneRegola rispostaIngresso = configurazione.getRispostaIngresso();
			
			if(rispostaIngresso != null) {
				if(rispostaIngresso.getHeaders().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(rispostaIngresso.getBody().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(rispostaIngresso.getAttachments().equals(StatoFunzionalita.ABILITATO))
					return true;
			}
			
			DumpConfigurazioneRegola rispostaUscita = configurazione.getRispostaUscita();
			
			if(rispostaUscita != null) {
				if(rispostaUscita.getHeaders().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(rispostaUscita.getBody().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(rispostaUscita.getAttachments().equals(StatoFunzionalita.ABILITATO))
					return true;
			}
		} else {
			DumpConfigurazioneRegola richiestaIngresso = configurazione.getRichiestaIngresso();
			
			if(richiestaIngresso != null) {
				if(richiestaIngresso.getHeaders().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(richiestaIngresso.getBody().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(richiestaIngresso.getAttachments().equals(StatoFunzionalita.ABILITATO))
					return true;
			}
			
			DumpConfigurazioneRegola richiestaUscita = configurazione.getRichiestaUscita();
			
			if(richiestaUscita != null) {
				if(richiestaUscita.getHeaders().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(richiestaUscita.getBody().equals(StatoFunzionalita.ABILITATO))
					return true;
				
				if(richiestaUscita.getAttachments().equals(StatoFunzionalita.ABILITATO))
					return true;
			}
		}
		
		return abilitato;
	}
	
	
	public static final void attivazionePolicyCheckData(
			TipoOperazione tipoOperazione, 
			PortaApplicativa pa,
			AttivazionePolicy policy,
			InfoPolicy infoPolicy,  
			ErogazioniConfEnv env,
			org.openspcoop2.message.constants.ServiceBinding serviceBinding,
			String modalita) throws Exception  {

		final RuoloPolicy ruoloPorta = RuoloPolicy.APPLICATIVA;
		final String nomePorta = pa.getNome();
		// Controllo che l'azione scelta per il filtro sia supportata.
		boolean hasAzioni = pa.getAzione() != null && pa.getAzione().getAzioneDelegataList().size() > 0;
		List<String> azioniSupportate = hasAzioni 
				    ? pa.getAzione().getAzioneDelegataList()
					: env.confCore.getAzioni(
						env.asps,
						env.apcCore.getAccordoServizioSintetico(env.asps.getIdAccordo()), 
						false, 
						true, 
						ErogazioniApiHelper.getAzioniOccupateErogazione(env.idAsps, env.apsCore, env.paCore)
					);
		
		if(policy.getFiltro().getAzione() != null && !policy.getFiltro().getAzione().isEmpty()) {
			String [] tmp = policy.getFiltro().getAzione().split(",");
			if(tmp!=null && tmp.length>0) {
				for (String azCheck : tmp) {
					if ( !azioniSupportate.contains(azCheck)) {
						throw FaultCode.RICHIESTA_NON_VALIDA.toException("L'azione " + azCheck + " non è assegnabile a una policy di rate limiting per il gruppo scelto. le azioni supportare sono: " + azioniSupportate.toString());
					}
				}
			}
		}
		
		if(policy.getFiltro().getRuoloFruitore()!=null) {
			
			FiltroRicercaRuoli filtroRicercaRuoli = new FiltroRicercaRuoli();
			filtroRicercaRuoli.setTipologia(RuoloTipologia.INTERNO);
			List<IDRuolo> listIdRuoli = env.ruoliCore.getAllIdRuoli(filtroRicercaRuoli);
			List<String> ruoli = new ArrayList<>();
			if(listIdRuoli!=null && !listIdRuoli.isEmpty()) {
				for (IDRuolo idRuolo : listIdRuoli) {
					ruoli.add(idRuolo.getNome());
				}
			}
			
			if ( !ruoli.contains(policy.getFiltro().getRuoloFruitore())) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il ruolo " + policy.getFiltro().getRuoloFruitore() + " non esiste.");
			}
		}
		
		// Controllo che l'applicativo fruitore scelto per il filtro sia supportato.
		if ( policy.getFiltro().getServizioApplicativoFruitore() != null &&				
				!env.confCore.getServiziApplicativiFruitore(env.tipo_protocollo, null, env.idSoggetto.getTipo(), env.idSoggetto.getNome())
				.stream()
				.filter( id -> id.getNome().equals(policy.getFiltro().getServizioApplicativoFruitore()))
				.findAny().isPresent()
		) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il servizio applicativo fruitore " + policy.getFiltro().getServizioApplicativoFruitore() + " scelto non è assegnabile alla policy di rate limiting");
		}
		
		org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale configurazioneControlloTraffico = env.confCore.getConfigurazioneControlloTraffico();
		if (! env.confHelper.attivazionePolicyCheckData(tipoOperazione, configurazioneControlloTraffico, 
				policy,infoPolicy, ruoloPorta, nomePorta, serviceBinding, modalita) ) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
		}
		
	
		// Controllo che il soggetto fruitore scelto per il filtro sia supportato, SOLO IN EROGAZIONI
		if ( policy.getFiltro().getTipoFruitore() != null && policy.getFiltro().getNomeFruitore() != null ) {
			List<IDSoggetto> soggettiSupportati = new ArrayList<IDSoggetto>();
			
			boolean multitenant = env.confCore.getConfigurazioneGenerale().getMultitenant().getStato() == StatoFunzionalita.ABILITATO;
			PddCore pddCore = new PddCore(env.stationCore);
			List<IDSoggetto> listSoggetti = env.confCore.getSoggetti(env.tipo_protocollo, null);
	
			for (IDSoggetto idSoggetto : listSoggetti) {
				if(!multitenant && policy.getFiltro().getRuoloPorta()!=null && 
						(policy.getFiltro().getRuoloPorta().equals(RuoloPolicy.APPLICATIVA) || policy.getFiltro().getRuoloPorta().equals(RuoloPolicy.DELEGATA))) {
					
					Soggetto s = env.soggettiCore.getSoggettoRegistro(idSoggetto);
					boolean isPddEsterna = pddCore.isPddEsterna(s.getPortaDominio());
					if( policy.getFiltro().getRuoloPorta().equals(RuoloPolicy.APPLICATIVA)) {
						// devo prendere i soggetti esterni
						if(isPddEsterna) {
							soggettiSupportati.add(idSoggetto);
						}
					}
					else {
						policy.getFiltro().getRuoloPorta().equals(RuoloPolicy.DELEGATA);
						// devo prendere i soggetti interni
						if(!isPddEsterna) {
							soggettiSupportati.add(idSoggetto);
						}
					}
				}
				else {
					soggettiSupportati.add(idSoggetto);
				}
			}
			
			final IDSoggetto idSoggettoScelto = new IDSoggetto(policy.getFiltro().getTipoFruitore(), policy.getFiltro().getNomeFruitore());
			if ( !soggettiSupportati.contains(idSoggettoScelto) ) {
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("Il soggetto fruitore " + idSoggettoScelto.toString() + " scelto non è assegnabile alla policy di rate limiting.\n I soggetti supportati sono: " + soggettiSupportati.toString() ); 
			}
		}
	
		
		
	}


}
