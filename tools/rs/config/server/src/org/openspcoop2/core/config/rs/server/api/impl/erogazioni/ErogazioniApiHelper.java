/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.AutorizzazioneRuoli;
import org.openspcoop2.core.config.AutorizzazioneScope;
import org.openspcoop2.core.config.ConfigurazioneProtocollo;
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
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneHashGenerator;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaGestioneIdentificazioneFallita;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaRichiestaIdentificazione;
import org.openspcoop2.core.config.constants.CorrelazioneApplicativaRispostaIdentificazione;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import org.openspcoop2.core.config.constants.ScopeTipoMatch;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
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
import org.openspcoop2.core.config.rs.server.model.APIImpl;
import org.openspcoop2.core.config.rs.server.model.APIImplAutenticazione;
import org.openspcoop2.core.config.rs.server.model.APIImplAutenticazioneNew;
import org.openspcoop2.core.config.rs.server.model.APIImplAutorizzazione;
import org.openspcoop2.core.config.rs.server.model.APIImplAutorizzazioneConfig;
import org.openspcoop2.core.config.rs.server.model.APIImplAutorizzazioneConfigNew;
import org.openspcoop2.core.config.rs.server.model.APIImplAutorizzazioneCustom;
import org.openspcoop2.core.config.rs.server.model.APIImplAutorizzazioneNew;
import org.openspcoop2.core.config.rs.server.model.APIImplAutorizzazioneView;
import org.openspcoop2.core.config.rs.server.model.APIImplAutorizzazioneXACMLConfig;
import org.openspcoop2.core.config.rs.server.model.AllAnyEnum;
import org.openspcoop2.core.config.rs.server.model.AllegatoGenerico;
import org.openspcoop2.core.config.rs.server.model.AllegatoGenericoItem;
import org.openspcoop2.core.config.rs.server.model.AllegatoSpecificaLivelloServizio;
import org.openspcoop2.core.config.rs.server.model.AllegatoSpecificaLivelloServizioItem;
import org.openspcoop2.core.config.rs.server.model.AllegatoSpecificaSemiformale;
import org.openspcoop2.core.config.rs.server.model.AllegatoSpecificaSemiformaleItem;
import org.openspcoop2.core.config.rs.server.model.AllegatoSpecificaSicurezza;
import org.openspcoop2.core.config.rs.server.model.AllegatoSpecificaSicurezzaItem;
import org.openspcoop2.core.config.rs.server.model.ApiImplAllegato;
import org.openspcoop2.core.config.rs.server.model.ApiImplAllegatoItem;
import org.openspcoop2.core.config.rs.server.model.ApiImplInformazioniGenerali;
import org.openspcoop2.core.config.rs.server.model.ApiImplInformazioniGeneraliView;
import org.openspcoop2.core.config.rs.server.model.ApiImplItem;
import org.openspcoop2.core.config.rs.server.model.ApiImplVersioneApiView;
import org.openspcoop2.core.config.rs.server.model.ApiImplViewItem;
import org.openspcoop2.core.config.rs.server.model.AuthenticationHttpBasic;
import org.openspcoop2.core.config.rs.server.model.CachingRisposta;
import org.openspcoop2.core.config.rs.server.model.Connettore;
import org.openspcoop2.core.config.rs.server.model.ConnettoreConfigurazioneHttps;
import org.openspcoop2.core.config.rs.server.model.ConnettoreConfigurazioneHttpsClient;
import org.openspcoop2.core.config.rs.server.model.ConnettoreConfigurazioneHttpsServer;
import org.openspcoop2.core.config.rs.server.model.ConnettoreConfigurazioneProxy;
import org.openspcoop2.core.config.rs.server.model.ConnettoreConfigurazioneTimeout;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutenticazione;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutenticazioneToken;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazione;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiAutorizzazioneView;
import org.openspcoop2.core.config.rs.server.model.ControlloAccessiGestioneToken;
import org.openspcoop2.core.config.rs.server.model.CorrelazioneApplicativaRichiesta;
import org.openspcoop2.core.config.rs.server.model.CorrelazioneApplicativaRichiestaEnum;
import org.openspcoop2.core.config.rs.server.model.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.rs.server.model.CorrelazioneApplicativaRispostaEnum;
import org.openspcoop2.core.config.rs.server.model.ErogazioneItem;
import org.openspcoop2.core.config.rs.server.model.ErogazioneViewItem;
import org.openspcoop2.core.config.rs.server.model.FonteEnum;
import org.openspcoop2.core.config.rs.server.model.FruizioneItem;
import org.openspcoop2.core.config.rs.server.model.FruizioneViewItem;
import org.openspcoop2.core.config.rs.server.model.GestioneCors;
import org.openspcoop2.core.config.rs.server.model.GestioneCorsAccessControl;
import org.openspcoop2.core.config.rs.server.model.GruppoEreditaConfigurazione;
import org.openspcoop2.core.config.rs.server.model.GruppoNuovaConfigurazione;
import org.openspcoop2.core.config.rs.server.model.HttpMethodEnum;
import org.openspcoop2.core.config.rs.server.model.KeystoreEnum;
import org.openspcoop2.core.config.rs.server.model.ListaApiImplAllegati;
import org.openspcoop2.core.config.rs.server.model.ModalitaConfigurazioneGruppoEnum;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyBase;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyBaseErogazione;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyBaseFruizione;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyEnum;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyErogazioneNew;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyErogazioneView;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyFiltro;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyFiltroErogazione;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyFiltroFruizione;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyFruizioneNew;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyFruizioneView;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyGroupBy;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyGroupByErogazione;
import org.openspcoop2.core.config.rs.server.model.RateLimitingPolicyGroupByFruizione;
import org.openspcoop2.core.config.rs.server.model.RegistrazioneMessaggi;
import org.openspcoop2.core.config.rs.server.model.RegistrazioneMessaggiConfigurazione;
import org.openspcoop2.core.config.rs.server.model.RegistrazioneMessaggiConfigurazioneRegola;
import org.openspcoop2.core.config.rs.server.model.SslTipologiaEnum;
import org.openspcoop2.core.config.rs.server.model.StatoApiEnum;
import org.openspcoop2.core.config.rs.server.model.StatoDefaultRidefinitoEnum;
import org.openspcoop2.core.config.rs.server.model.StatoFunzionalitaConWarningEnum;
import org.openspcoop2.core.config.rs.server.model.TipoApiEnum;
import org.openspcoop2.core.config.rs.server.model.TipoAutenticazioneEnum;
import org.openspcoop2.core.config.rs.server.model.TipoAutorizzazioneEnum;
import org.openspcoop2.core.config.rs.server.model.TipoGestioneCorsEnum;
import org.openspcoop2.core.config.rs.server.model.TipoSpecificaLivelloServizioEnum;
import org.openspcoop2.core.config.rs.server.model.TipoSpecificaSemiformaleEnum;
import org.openspcoop2.core.config.rs.server.model.TipoSpecificaSicurezzaEnum;
import org.openspcoop2.core.config.rs.server.model.TipoValidazioneEnum;
import org.openspcoop2.core.config.rs.server.model.Validazione;
import org.openspcoop2.core.config.rs.server.utils.WrapperFormFile;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento;
import org.openspcoop2.core.controllo_traffico.beans.InfoPolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoFiltroApplicativo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizio;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.protocol.basic.Utilities;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.information_missing.constants.StatoType;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.utils.PorteNamingUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.service.beans.utils.ListaUtils;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaUtilities;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeHelper;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
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
			
		// TODO: Renderli statici.
		Map<ModalitaConfigurazioneGruppoEnum, Class<?>> typeMap = new HashMap<ModalitaConfigurazioneGruppoEnum, Class<?>>();
		typeMap.put(ModalitaConfigurazioneGruppoEnum.EREDITA, GruppoEreditaConfigurazione.class);
		typeMap.put(ModalitaConfigurazioneGruppoEnum.NUOVA, GruppoNuovaConfigurazione.class);
		
		try {
			return (T) Helper.deserializeFromSwitch(typeMap, discr, body);
		} catch( Exception e ) {
			return null;
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	public static final<T> T deserializeAutorizzazioneConfig(TipoAutorizzazioneEnum discr, Object body) throws InstantiationException, IllegalAccessException, UtilsException {
		
		if (discr == TipoAutorizzazioneEnum.DISABILITATO) return null;
		
		//	TODO: Renderli statici.
		Map<TipoAutorizzazioneEnum, Class<?>> typeMap = new HashMap<TipoAutorizzazioneEnum, Class<?>>();
		typeMap.put(TipoAutorizzazioneEnum.ABILITATO, APIImplAutorizzazioneConfig.class);
		typeMap.put(TipoAutorizzazioneEnum.CUSTOM, APIImplAutorizzazioneCustom.class);
		typeMap.put(TipoAutorizzazioneEnum.XACML_POLICY, APIImplAutorizzazioneXACMLConfig.class);
		
		return (T) Helper.deserializeFromSwitch(typeMap, discr, body);		
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
	
	public static final AccordoServizioParteSpecifica apiImplToAps(APIImpl impl, final Soggetto soggErogatore, AccordoServizioParteComune as, ErogazioniEnv env) 
			throws DriverRegistroServiziException, DriverRegistroServiziNotFound, CoreException, ProtocolException {
		final AccordoServizioParteSpecifica ret = new AccordoServizioParteSpecifica();
				
		fillAps(ret, impl);
		
		// Questo per seguire la specifica della console, che durante la creazione di un servizio soap
		// vuole che il nome del'asps sia quello del servizio\port_type
		if (as.getServiceBinding() == ServiceBinding.SOAP) {
			ret.setNome(ret.getPortType());
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
		
		
		
		if(env.apsCore.isSupportatoVersionamentoAccordiServizioParteSpecifica(env.tipo_protocollo))
			ret.setVersione(as.getVersione());
		
		 
		if (StringUtils.isEmpty(ret.getTipo())) {
			String tipoServizio = env.protocolFactoryMgr.getProtocolFactoryByName(env.tipo_protocollo).createProtocolConfiguration().getTipoServizioDefault(Utilities.convert(as.getServiceBinding()));
			ret.setTipo( tipoServizio );
		}
				
		return ret;
	}
	
	
	
	/**
	 * Questa funzione posso utilizzarla anche nel caso della updateInformazioniGenerali
	 * @param env
	 * @param as
	 * @param asps
	 * @param idSoggetto
	 * @param impl
	 * @param generaPortaApplicativa
	 * @return
	 * @throws Exception
	 */
	
	@SuppressWarnings("unchecked")
	public static final void serviziCheckData(
			TipoOperazione tipoOp,
			ErogazioniEnv env,
			AccordoServizioParteComune as,
			AccordoServizioParteSpecifica asps,
			IdSoggetto fruitore,
			APIImpl impl,
			boolean generaPortaApplicativa

			) throws Exception {
		
		IdSoggetto erogatore = new IdSoggetto( new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore()));
		erogatore.setId(asps.getIdSoggetto());
		
		 if (impl == null) { 
			 impl = new APIImpl();
			 impl.setConnettore(new Connettore());
		 }
		
		 boolean accordoPrivato = as.getPrivato()!=null && as.getPrivato();		
         
         List<PortType> ptList = AccordiServizioParteSpecificaUtilities.getListaPortTypes(as, env.apsCore, env.apsHelper);
         
         String[] ptArray =  ptList.stream()
         		.map( p -> p.getNome() )
         		.toArray(String[]::new);
		
		 // Determino i soggetti compatibili
        Search searchSoggetti = new Search(true);
		searchSoggetti.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, env.tipo_protocollo);
		List<Soggetto> listSoggetti = env.soggettiCore.soggettiRegistroList(null, searchSoggetti);
		
		String[] soggettiCompatibili = listSoggetti.stream()
				.map( s -> s.getId().toString())
				.toArray(String[]::new);
		
		// Determino la lista Api
		List<AccordoServizioParteComune> listaAPI = AccordiServizioParteSpecificaUtilities.getListaAPI(
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
        final AuthenticationHttpBasic 		httpConf	 = conn.getAutenticazioneHttp();
        
	    final String endpointtype = httpsConf != null ? TipiConnettore.HTTPS.getNome() : TipiConnettore.HTTP.getNome();
	    

    	final Properties parametersPOST = null;
		org.openspcoop2.core.registry.Connettore conTmp = null;
		List<ExtendedConnettore> listExtendedConnettore = 
				ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, env.apsHelper, 
							parametersPOST, false, endpointtype); // uso endpointtype per capire se è la prima volta che entro
	
        
        final ConnettoreConfigurazioneHttpsClient httpsClient = Helper.evalnull( () -> httpsConf.getClient() );
      	final ConnettoreConfigurazioneHttpsServer httpsServer = Helper.evalnull( () -> httpsConf.getServer() );
      	final ConnettoreConfigurazioneProxy 	  proxy   	  = conn.getProxy();
      	final ConnettoreConfigurazioneTimeout	  timeoutConf = conn.getTempiRisposta();

        
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
        final APIImplAutorizzazioneNew authz = impl.getAutorizzazione();
        final APIImplAutenticazioneNew authn = impl.getAutenticazione();
        
        final APIImplAutorizzazioneConfigNew configAuth = new APIImplAutorizzazioneConfigNew();
        final APIImplAutorizzazioneXACMLConfig configAuthXaml = new APIImplAutorizzazioneXACMLConfig();        
        FonteEnum ruoliFonte = FonteEnum.QUALSIASI;
        String erogazioneRuolo = null;
        boolean isPuntuale = false;
        boolean isRuoli = false;
        String statoAutorizzazione = null;
        
        if ( authz != null && authz.getTipo() != null ) {
	        switch ( authz.getTipo() ) {
	        case ABILITATO:
	        	Helper.fillFromMap( (Map<String,Object>) authz.getConfigurazione(), configAuth );
	         	ruoliFonte = configAuth.getRuoliFonte();
	         	erogazioneRuolo = configAuth.getRuolo();
	         	isPuntuale = configAuth.isPuntuale();
	         	isRuoli = configAuth.isRuoli();
	         	statoAutorizzazione = AutorizzazioneUtilities.STATO_ABILITATO;
	        	break;
	        case XACML_POLICY:
	        	Helper.fillFromMap( (Map<String,Object>) authz.getConfigurazione(), configAuthXaml );
	        	ruoliFonte = configAuthXaml.getRuoliFonte();
	        	statoAutorizzazione = AutorizzazioneUtilities.STATO_XACML_POLICY;
	        	
	        	break;
	        case DISABILITATO:
	        	statoAutorizzazione = AutorizzazioneUtilities.STATO_DISABILITATO;
	        	break;
	        default: break;
	        }
        }
        
	
        final BinaryParameter xamlPolicy = new BinaryParameter();
        xamlPolicy.setValue(configAuthXaml.getPolicy());
        xamlPolicy.setName(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);
        
        
        StringBuffer inUsoMessage = new StringBuffer();
		
		if ( AccordiServizioParteSpecificaUtilities.alreadyExists(
				env.apsCore, 
				env.apsHelper, 
				erogatore.getId(), 
				env.idServizioFactory.getIDServizioFromAccordo(asps),
				env.idAccordoFactory.getUriFromAccordo(as),
				Helper.evalnull( () -> fruitore.getTipo() ),
				Helper.evalnull( () -> fruitore.getNome() ),
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
        		Helper.evalnull( () -> httpConf.getUsername() ),	
        		Helper.evalnull( () -> httpConf.getPassword() ), 
        		null,   // initcont JMS,
        		null,   // urlpgk JMS,
        		null,   // provurl JMS 
        		null,   // connfact JMS
        		null, 	// sendas JMS, 
        		new BinaryParameter(),		//  wsdlimpler
        		new BinaryParameter(),		//  wsdlimplfru
        		Helper.evalorElse( () -> asps.getId().toString(), "0"), 	
        		asps.getVersioneProtocollo(),	//  Il profilo è la versione protocollo in caso spcoop, è quello del soggetto erogatore.
        		asps.getPortType(),
        		ptArray,
				accordoPrivato,
				false, 	// this.privato,
        		conn.getEndpoint(),	// httpsurl, 
        		Helper.evalnull( () ->  httpsConf.getTipologia().toString() ),				// I valori corrispondono con con org.openspcoop2.utils.transport.http.SSLConstants
        		Helper.evalorElse( () -> httpsConf.isHostnameVerifier().booleanValue(), false ),				// this.httpshostverify,
				Helper.evalnull( () -> httpsServer.getTruststorePath() ),				// this.httpspath
				Helper.evalnull( () -> httpsServer.getTruststoreTipo().toString() ),		// this.httpstipo,
				Helper.evalnull( () -> httpsServer.getTruststorePassword() ),			// this.httpspwd,
				Helper.evalnull( () -> httpsServer.getAlgoritmo() ),					// this.httpsalgoritmo
				httpsstato,	//
        		httpskeystore, 	
        		"", //  this.httpspwdprivatekeytrust
        		Helper.evalnull( () -> httpsClient.getKeystorePath() ),					// httpspathkey
        		Helper.evalnull( () -> httpsClient.getKeystoreTipo().toString() ),	 		// httpstipokey, coincide con ConnettoriCostanti.TIPOLOGIE_KEYSTORE
        		Helper.evalnull( () -> httpsClient.getKeystorePassword() ), 	 		// httpspwdkey
        		Helper.evalnull( () -> httpsClient.getKeyPassword() ),	 				// httpspwdprivatekey
        		Helper.evalnull( () -> httpsClient.getAlgoritmo() ),					// httpsalgoritmokey
        		null, 								// tipoconn Da debug = null.	
        		as.getVersione().toString(), 		// Versione aspc
        		false,								// validazioneDocumenti Da debug = false
        		null,								// Da Codice console
        		ServletUtils.boolToCheckBoxStatus(http_stato),	// "yes" se utilizziamo http.
        		ServletUtils.boolToCheckBoxStatus(proxy_enabled),			
    			Helper.evalnull( () -> proxy.getHostname() ),
    			Helper.evalnull( () -> proxy.getPorta().toString() ),
    			Helper.evalnull( () -> proxy.getUsername() ),
    			Helper.evalnull( () -> proxy.getPassword() ),				
    			ServletUtils.boolToCheckBoxStatus(tempiRisposta_enabled), 
    			Helper.evalnull( () -> timeoutConf.getConnectionTimeout().toString()),		// this.tempiRisposta_connectionTimeout, 
    			Helper.evalnull( () -> timeoutConf.getConnectionReadTimeout().toString()),  // this.tempiRisposta_readTimeout, 
    			Helper.evalnull( () -> timeoutConf.getTempoMedioRisposta().toString()),		// this.tempiRisposta_tempoMedioRisposta,
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
        		erogazioneRuolo,																						// erogazioneRuolo: E' il ruolo scelto nella label "Ruolo"
        		Helper.evalnull( () -> Enums.tipoAutenticazioneNewFromRest.get(authn.getTipo()).toString() ),		// erogazioneAutenticazione
        		Helper.evalnull( () -> ServletUtils.boolToCheckBoxStatus(authn.isOpzionale()) ),					// erogazioneAutenticazioneOpzionale
        		statoAutorizzazione,					   	// erogazioneAutorizzazione QUESTO E' lo STATO dell'autorizzazione
        		ServletUtils.boolToCheckBoxStatus( isPuntuale ), 			// erogazioneAutorizzazioneAutenticati, 
        		ServletUtils.boolToCheckBoxStatus( isRuoli ),				// erogazioneAutorizzazioneRuoli, 
                Enums.ruoloTipologiaFromRest.get(ruoliFonte).toString(),				// erogazioneAutorizzazioneRuoliTipologia,
                Helper.evalnull( () -> configAuth.getRuoliRichiesti().toString() ),  	// erogazioneAutorizzazioneRuoliMatch,  AllAnyEnum == RuoloTipoMatch
        		env.isSupportatoAutenticazioneSoggetti,	
        		generaPortaApplicativa,													// generaPACheckSoggetto (Un'erogazione genera una porta applicativa)
        		listExtendedConnettore,
        		null,																	// fruizioneServizioApplicativo
        		configAuth.getRuolo(),													// Ruolo fruizione.
        		Helper.evalnull( () -> Enums.tipoAutenticazioneNewFromRest.get(authn.getTipo()).toString() ),  // fruizioneAutenticazione 
        		Helper.evalnull( () -> ServletUtils.boolToCheckBoxStatus( authn.isOpzionale() ) ), 			// fruizioneAutenticazioneOpzionale
        		statoAutorizzazione,											// fruizioneAutorizzazione 	
        		ServletUtils.boolToCheckBoxStatus( isPuntuale ), 				// fruizioneAutorizzazioneAutenticati, 
        		ServletUtils.boolToCheckBoxStatus( isRuoli ), 					// fruizioneAutorizzazioneRuoli,
                Enums.ruoloTipologiaFromRest.get(ruoliFonte).toString(), 					// fruizioneAutorizzazioneRuoliTipologia,
                Helper.evalnull( () -> configAuth.getRuoliRichiesti().toString() ), 		// fruizioneAutorizzazioneRuoliMatch,
        		env.tipo_protocollo, 
        		xamlPolicy, 																//allegatoXacmlPolicy,
        		"",
        		Helper.evalnull( () -> fruitore.getTipo() ),		// tipoFruitore 
        		Helper.evalnull( () -> fruitore.getNome() )			// nomeFruitore
        	)) {
        	throw FaultCode.RICHIESTA_NON_VALIDA.toException( StringEscapeUtils.unescapeHtml( env.pd.getMessage()) );
        }
        
        
		
	}
	
	public static final boolean connettoreCheckData(
			final Connettore conn,
			final ErogazioniEnv env
			) throws Exception {
		
		
		final boolean http_stato  = conn.getAutenticazioneHttp() != null;
		final boolean proxy_enabled = conn.getProxy() != null;
		final boolean tempiRisposta_enabled = conn.getTempiRisposta() != null; 
		
	    final ConnettoreConfigurazioneHttps httpsConf 	 = conn.getAutenticazioneHttps();
	    final AuthenticationHttpBasic 		httpConf	 = conn.getAutenticazioneHttp();

	    final String endpointtype = httpsConf != null ? TipiConnettore.HTTPS.getNome() : TipiConnettore.HTTP.getNome();
	    
	    final Properties parametersPOST = null;
		org.openspcoop2.core.registry.Connettore conTmp = null;
		List<ExtendedConnettore> listExtendedConnettore = 
				ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, env.apsHelper, 
							parametersPOST, false, endpointtype);

	    final ConnettoreConfigurazioneHttpsClient httpsClient = Helper.evalnull( () -> httpsConf.getClient() );
	  	final ConnettoreConfigurazioneHttpsServer httpsServer = Helper.evalnull( () -> httpsConf.getServer() );
	  	final ConnettoreConfigurazioneProxy 	  proxy   	  = conn.getProxy();
	  	final ConnettoreConfigurazioneTimeout	  timeoutConf = conn.getTempiRisposta();
	  	
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
				endpointtype,
				conn.getEndpoint(),
				null,	// nome
				null,	// tipo
				Helper.evalnull( () -> httpConf.getUsername() ),
				Helper.evalnull( () -> httpConf.getPassword() ),
				null,	// this.initcont, 
				null,	// this.urlpgk,
				null,	// provurl jms,
				null, 	// connfact, 
				null,	// sendas, 
				conn.getEndpoint(), 													// this.httpsurl, 
				Helper.evalnull( () -> httpsConf.getTipologia().toString() ),				// this.httpstipologia
				Helper.evalorElse( () -> httpsConf.isHostnameVerifier().booleanValue(), false ),	// this.httpshostverify,
				Helper.evalnull( () -> httpsServer.getTruststorePath() ),				// this.httpspath
				Helper.evalnull( () -> httpsServer.getTruststoreTipo().toString() ),	// this.httpstipo,
				Helper.evalnull( () -> httpsServer.getTruststorePassword() ),			// this.httpspwd,
				Helper.evalnull( () -> httpsServer.getAlgoritmo() ),					// this.httpsalgoritmo
				httpsstato, 
				httpskeystore,
				"",																		// httpspwdprivatekeytrust, 
				Helper.evalnull( () -> httpsClient.getKeystorePath() ),				// pathkey
				Helper.evalnull( () -> httpsClient.getKeystoreTipo().toString() ), 		// this.httpstipokey
				Helper.evalnull( () -> httpsClient.getKeystorePassword() ),			// this.httpspwdkey 
				Helper.evalnull( () -> httpsClient.getKeyPassword() ),				// this.httpspwdprivatekey,  
				Helper.evalnull( () -> httpsClient.getAlgoritmo() ),				// this.httpsalgoritmokey, 
				null,																//	tipoconn (personalizzato)
				ServletUtils.boolToCheckBoxStatus( http_stato ),										 	//autenticazioneHttp,
				ServletUtils.boolToCheckBoxStatus( proxy_enabled ),	
				Helper.evalnull( () -> proxy.getHostname() ),
				Helper.evalnull( () -> proxy.getPorta().toString() ),
				Helper.evalnull( () -> proxy.getUsername() ),
				Helper.evalnull( () -> proxy.getPassword() ),
				ServletUtils.boolToCheckBoxStatus( tempiRisposta_enabled ),	
				Helper.evalnull( () -> timeoutConf.getConnectionTimeout().toString()),	// this.tempiRisposta_connectionTimeout, 
				Helper.evalnull( () -> timeoutConf.getConnectionReadTimeout().toString()), //null,	// this.tempiRisposta_readTimeout, 
				Helper.evalnull( () -> timeoutConf.getTempoMedioRisposta().toString()),	// this.tempiRisposta_tempoMedioRisposta,
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
				listExtendedConnettore
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
	    final AuthenticationHttpBasic 		httpConf	 = conn.getAutenticazioneHttp();

	    final String endpointtype = httpsConf != null ? TipiConnettore.HTTPS.getNome() : TipiConnettore.HTTP.getNome();
	    
	    final Properties parametersPOST = null;
		org.openspcoop2.core.registry.Connettore conTmp = null;
		List<ExtendedConnettore> listExtendedConnettore = 
				ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, env.apsHelper, 
							parametersPOST, false, endpointtype);

	    final ConnettoreConfigurazioneHttpsClient httpsClient = Helper.evalnull( () -> httpsConf.getClient() );
	  	final ConnettoreConfigurazioneHttpsServer httpsServer = Helper.evalnull( () -> httpsConf.getServer() );
	  	final ConnettoreConfigurazioneProxy 	  proxy   	  = conn.getProxy();
	  	final ConnettoreConfigurazioneTimeout	  timeoutConf = conn.getTempiRisposta();
	  	
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
				Helper.evalnull( () -> httpConf.getUsername() ),
				Helper.evalnull( () -> httpConf.getPassword() ),
				null,	// this.initcont, 
				null,	// this.urlpgk,
				conn.getEndpoint(),	// this.url, 
				null,	// this.connfact,
				null,	// this.sendas,
				conn.getEndpoint(), 													// this.httpsurl, 
				Helper.evalnull( () -> httpsConf.getTipologia().toString() ),				// this.httpstipologia
				Helper.evalorElse( () -> httpsConf.isHostnameVerifier().booleanValue(), false ),	// this.httpshostverify,
				Helper.evalnull( () -> httpsServer.getTruststorePath() ),				// this.httpspath
				Helper.evalnull( () -> httpsServer.getTruststoreTipo().toString() ),	// this.httpstipo,
				Helper.evalnull( () -> httpsServer.getTruststorePassword() ),			// this.httpspwd,
				Helper.evalnull( () -> httpsServer.getAlgoritmo() ),					// this.httpsalgoritmo
				httpsstato,
				httpskeystore,			// this.httpskeystore, 
				"",																	//  this.httpspwdprivatekeytrust
				Helper.evalnull( () -> httpsClient.getKeystorePath() ),				// pathkey
				Helper.evalnull( () -> httpsClient.getKeystoreTipo().toString() ), 		// this.httpstipokey
				Helper.evalnull( () -> httpsClient.getKeystorePassword() ),			// this.httpspwdkey 
				Helper.evalnull( () -> httpsClient.getKeyPassword() ),				// this.httpspwdprivatekey,  
				Helper.evalnull( () -> httpsClient.getAlgoritmo() ),				// this.httpsalgoritmokey,
			
				ServletUtils.boolToCheckBoxStatus( proxy_enabled ),	
				Helper.evalnull( () -> proxy.getHostname() ),
				Helper.evalnull( () -> proxy.getPorta().toString() ),
				Helper.evalnull( () -> proxy.getUsername() ),
				Helper.evalnull( () -> proxy.getPassword() ),
				
				ServletUtils.boolToCheckBoxStatus( tempiRisposta_enabled ),	
				Helper.evalnull( () -> timeoutConf.getConnectionTimeout().toString()),	// this.tempiRisposta_connectionTimeout, 
				Helper.evalnull( () -> timeoutConf.getConnectionReadTimeout().toString()), //null,	// this.tempiRisposta_readTimeout, 
				Helper.evalnull( () -> timeoutConf.getTempoMedioRisposta().toString()),	// this.tempiRisposta_tempoMedioRisposta,
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
	    final AuthenticationHttpBasic 		httpConf	 = conn.getAutenticazioneHttp();

	    final String endpointtype = httpsConf != null ? TipiConnettore.HTTPS.getNome() : TipiConnettore.HTTP.getNome();
	    
	    final Properties parametersPOST = null;
		org.openspcoop2.core.registry.Connettore conTmp = null;
		List<ExtendedConnettore> listExtendedConnettore = 
				ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, env.apsHelper, 
							parametersPOST, false, endpointtype);

	    final ConnettoreConfigurazioneHttpsClient httpsClient = Helper.evalnull( () -> httpsConf.getClient() );
	  	final ConnettoreConfigurazioneHttpsServer httpsServer = Helper.evalnull( () -> httpsConf.getServer() );
	  	final ConnettoreConfigurazioneProxy 	  proxy   	  = conn.getProxy();
	  	final ConnettoreConfigurazioneTimeout	  timeoutConf = conn.getTempiRisposta();
	  	
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
				Helper.evalnull( () -> httpConf.getUsername() ),
				Helper.evalnull( () -> httpConf.getPassword() ),
				null,	// this.initcont, 
				null,	// this.urlpgk,
				conn.getEndpoint(),	// this.url, 
				null,	// this.connfact,
				null,	// this.sendas,
				conn.getEndpoint(), 													// this.httpsurl, 
				Helper.evalnull( () -> httpsConf.getTipologia().toString() ),				// this.httpstipologia
				Helper.evalorElse( () -> httpsConf.isHostnameVerifier().booleanValue(), false ),	// this.httpshostverify,
				Helper.evalnull( () -> httpsServer.getTruststorePath() ),				// this.httpspath
				Helper.evalnull( () -> httpsServer.getTruststoreTipo().toString() ),	// this.httpstipo,
				Helper.evalnull( () -> httpsServer.getTruststorePassword() ),			// this.httpspwd,
				Helper.evalnull( () -> httpsServer.getAlgoritmo() ),					// this.httpsalgoritmo
				httpsstato,
				httpskeystore,			// this.httpskeystore, 
				"",																	//  this.httpspwdprivatekeytrust
				Helper.evalnull( () -> httpsClient.getKeystorePath() ),				// pathkey
				Helper.evalnull( () -> httpsClient.getKeystoreTipo().toString() ), 		// this.httpstipokey
				Helper.evalnull( () -> httpsClient.getKeystorePassword() ),			// this.httpspwdkey 
				Helper.evalnull( () -> httpsClient.getKeyPassword() ),				// this.httpspwdprivatekey,  
				Helper.evalnull( () -> httpsClient.getAlgoritmo() ),				// this.httpsalgoritmokey,
			
				ServletUtils.boolToCheckBoxStatus( proxy_enabled ),	
				Helper.evalnull( () -> proxy.getHostname() ),
				Helper.evalnull( () -> proxy.getPorta().toString() ),
				Helper.evalnull( () -> proxy.getUsername() ),
				Helper.evalnull( () -> proxy.getPassword() ),
				
				ServletUtils.boolToCheckBoxStatus( tempiRisposta_enabled ),	
				Helper.evalnull( () -> timeoutConf.getConnectionTimeout().toString()),	// this.tempiRisposta_connectionTimeout, 
				Helper.evalnull( () -> timeoutConf.getConnectionReadTimeout().toString()), //null,	// this.tempiRisposta_readTimeout, 
				Helper.evalnull( () -> timeoutConf.getTempoMedioRisposta().toString()),	// this.tempiRisposta_tempoMedioRisposta,
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
				listExtendedConnettore);			
	}
	

	
	
	@SuppressWarnings("unchecked")
	public static final void createAps(
			ErogazioniEnv env,
			AccordoServizioParteSpecifica asps,
			org.openspcoop2.core.registry.Connettore regConnettore,
			APIImpl ero,
			boolean alreadyExists,
			boolean generaPortaApplicativa) throws Exception
	{
		final boolean generaPortaDelegata = !generaPortaApplicativa;
		
		final APIImplAutorizzazioneNew authz = ero.getAutorizzazione();
        final APIImplAutenticazioneNew authn = ero.getAutenticazione();
        
        final APIImplAutorizzazioneConfigNew configAuthz = new APIImplAutorizzazioneConfigNew();
        final APIImplAutorizzazioneXACMLConfig configAuthzXacml = new APIImplAutorizzazioneXACMLConfig();
		final AccordoServizioParteComune as = env.apcCore.getAccordoServizio(asps.getIdAccordo());

        FonteEnum ruoliFonte = FonteEnum.QUALSIASI;
        
        boolean isPuntuale = false;
        boolean isRuoli = false;
        String statoAutorizzazione = null;
                
        if ( Helper.evalnull( () -> authz.getTipo() ) != null) {
		    switch (authz.getTipo()) {
		    case ABILITATO:	
		    	Helper.fillFromMap( (Map<String,Object>) authz.getConfigurazione(), configAuthz );
		     	ruoliFonte = configAuthz.getRuoliFonte();
	         	isPuntuale = configAuthz.isPuntuale();
	         	isRuoli = configAuthz.isRuoli();
	         	statoAutorizzazione = AutorizzazioneUtilities.STATO_ABILITATO;
		    	break;
		    case XACML_POLICY:
		    	Helper.fillFromMap( (Map<String,Object>) authz.getConfigurazione(), configAuthzXacml );
		    	ruoliFonte = configAuthzXacml.getRuoliFonte();
		    	statoAutorizzazione = AutorizzazioneUtilities.STATO_XACML_POLICY;
		    	break;
		    case DISABILITATO:
		    	statoAutorizzazione = AutorizzazioneUtilities.STATO_DISABILITATO;
		    	break;
		    }
        }
        
    	final IDServizio idServizio = env.idServizioFactory.getIDServizioFromValues(asps.getTipo(), asps.getNome(), new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore()), asps.getVersione()); 
	               
        BinaryParameter xamlPolicy = new BinaryParameter();
        xamlPolicy.setValue(configAuthzXacml.getPolicy());
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
				Helper.evalnull( () -> Enums.tipoAutenticazioneNewFromRest.get(authn.getTipo()).toString() ),											// erogazioneAutenticazione
        		Helper.evalnull( () -> ServletUtils.boolToCheckBoxStatus( authn.isOpzionale() ) ), 														// erogazioneAutenticazioneOpzionale
        		statoAutorizzazione,	// autorizzazione, è lo STATO 	
				ServletUtils.boolToCheckBoxStatus( isPuntuale ),			// 	autorizzazioneAutenticati,
				ServletUtils.boolToCheckBoxStatus( isRuoli ),				//	autorizzazioneRuoli,
		    	Enums.ruoloTipologiaFromRest.get(ruoliFonte).toString(),				//	erogazioneAutorizzazioneRuoliTipologia
		    	Helper.evalnull( () -> configAuthz.getRuoliRichiesti().toString() ),			// 	autorizzazioneRuoliMatch
				null,	// servizioApplicativo Come da Debug, 
		    	Helper.evalnull( () -> configAuthz.getRuolo() ),	// ruolo: E' il ruolo scelto nella label "Ruolo" 
		    	Helper.evalnull( () -> new IDSoggetto(env.tipo_soggetto, configAuthz.getSoggetto()).toString() ),
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
				env.erogazioniHelper
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
		
	public static final Documento implAllegatoToDocumento(ApiImplAllegato body, AccordoServizioParteSpecifica asps) throws InstantiationException, IllegalAccessException {
		
		Documento documento = new Documento();
		documento.setIdProprietarioDocumento(asps.getId());
		documento.setRuolo(RuoliDocumento.valueOf(
				Enums.ruoliDocumentoFromApiImpl
				.get( body.getRuolo()).toString() )
				.toString()
			);
		
		switch (body.getRuolo()) {
		case ALLEGATO: {
			@SuppressWarnings("unchecked") AllegatoGenerico all = Helper.fromMap((Map<String,Object>) body.getAllegato(), AllegatoGenerico.class);
			documento.setByteContenuto(all.getDocumento());
			documento.setFile(all.getNome());
			documento.setTipo(all.getNome().substring( all.getNome().lastIndexOf('.')+1, all.getNome().length()));
			break;
		}
		case SPECIFICASEMIFORMALE: {
			@SuppressWarnings("unchecked") AllegatoSpecificaSemiformale all = Helper.fromMap( (Map<String,Object>) body.getAllegato(), AllegatoSpecificaSemiformale.class);
			documento.setByteContenuto(all.getDocumento());
			documento.setFile(all.getNome());	
			documento.setTipo(all.getTipo().toString());
			break;
		}
		case SPECIFICALIVELLOSERVIZIO: {
			@SuppressWarnings("unchecked") AllegatoSpecificaLivelloServizio all = Helper.fromMap( (Map<String,Object>) body.getAllegato(), AllegatoSpecificaLivelloServizio.class);
			documento.setByteContenuto(all.getDocumento());
			documento.setFile(all.getNome());	
			documento.setTipo(all.getTipo().toString());
			break;
		}
		case SPECIFICASICUREZZA: {
			@SuppressWarnings("unchecked") AllegatoSpecificaSicurezza all = Helper.fromMap( (Map<String,Object>) body.getAllegato(), AllegatoSpecificaSicurezza.class);
			documento.setByteContenuto(all.getDocumento());
			documento.setFile(all.getNome());	
			documento.setTipo(all.getTipo().toString());
			break;
		}
		}

		return documento;
	}
	
	
	public static final ApiImplAllegato documentoToImplAllegato(Documento doc) {
		ApiImplAllegato ret = new ApiImplAllegato();
	    ret.setRuolo(Enums.ruoliApiImplFromDocumento.get( RuoliDocumento.valueOf(doc.getRuolo())) );
	    
	    switch(ret.getRuolo()) {
	    case ALLEGATO: {
	    	AllegatoGenerico a = new AllegatoGenerico();
	    	a.setDocumento(doc.getByteContenuto());
	    	a.setNome(doc.getFile());
	    	ret.setAllegato(a);
	    	break;
	    }
	    case SPECIFICALIVELLOSERVIZIO: {
	    	AllegatoSpecificaLivelloServizio a = new AllegatoSpecificaLivelloServizio();
	    	a.setDocumento(doc.getByteContenuto());
	    	a.setNome(doc.getFile());
	    	a.setTipo(TipoSpecificaLivelloServizioEnum.fromValue(doc.getTipo()));
	    	ret.setAllegato(a);
	    	break;
	    }
	    case SPECIFICASEMIFORMALE: {
	    	AllegatoSpecificaSemiformale a = new AllegatoSpecificaSemiformale();
	    	a.setDocumento(doc.getByteContenuto());
	    	a.setNome(doc.getFile());
	    	a.setTipo(TipoSpecificaSemiformaleEnum.fromValue(doc.getTipo()));
	    	ret.setAllegato(a);
	    	break;
	    }
	    case SPECIFICASICUREZZA: {
	    	AllegatoSpecificaSicurezza a = new AllegatoSpecificaSicurezza();
	    	a.setDocumento(doc.getByteContenuto());
	    	a.setNome(doc.getFile());
	    	a.setTipo(TipoSpecificaSicurezzaEnum.fromValue(doc.getTipo()));
	    	ret.setAllegato(a);
	    	break;
	    }
	    }
	    
	    return ret;
		
	}
	
	public static final ApiImplAllegatoItem ImplAllegatoToItem(ApiImplAllegato allegato) {
		ApiImplAllegatoItem ret = new ApiImplAllegatoItem();
		ret.setRuolo(allegato.getRuolo());
		
		switch(ret.getRuolo()) {
	    case ALLEGATO: {
	    	AllegatoGenericoItem a = new AllegatoGenericoItem();
	    	a.setNome( ( (AllegatoGenerico) allegato.getAllegato()).getNome());
	    	ret.setAllegato(a);
	    	break;
	    }
	    case SPECIFICALIVELLOSERVIZIO: {
	    	AllegatoSpecificaLivelloServizioItem a = new AllegatoSpecificaLivelloServizioItem();
	    	a.setNome( ( (AllegatoSpecificaLivelloServizio) allegato.getAllegato()).getNome());
	    	a.setTipo( ( (AllegatoSpecificaLivelloServizio) allegato.getAllegato()).getTipo());
	    	break;
	    }
	    case SPECIFICASEMIFORMALE: {
	    	AllegatoSpecificaSemiformaleItem a = new AllegatoSpecificaSemiformaleItem();
	    	a.setNome( ( (AllegatoSpecificaSemiformale) allegato.getAllegato()).getNome());
	    	a.setTipo( ( (AllegatoSpecificaSemiformale) allegato.getAllegato()).getTipo());
	    	break;
	    }
	    case SPECIFICASICUREZZA: {
	    	AllegatoSpecificaSicurezzaItem a = new AllegatoSpecificaSicurezzaItem();
	    	a.setNome( ( (AllegatoSpecificaSicurezza) allegato.getAllegato() ).getNome());
	    	a.setTipo( ( (AllegatoSpecificaSicurezza) allegato.getAllegato() ).getTipo());
	    	break;
	    }
	    }
		
		
		return ret;
		
	}
	
	public static void createAllegatoAsps(ApiImplAllegato body, final ErogazioniEnv env,
			AccordoServizioParteSpecifica asps) 
					throws Exception {

		Documento documento = ErogazioniApiHelper.implAllegatoToDocumento(body, asps);
		
		WrapperFormFile filewrap = new WrapperFormFile(documento.getFile(), documento.getByteContenuto());
		env.requestWrapper.overrideParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_RUOLO, documento.getRuolo());			

		boolean documentoUnivocoIndipendentementeTipo = true;
		if (env.archiviCore.existsDocumento(documento,ProprietariDocumento.servizio,documentoUnivocoIndipendentementeTipo)) {
			throw FaultCode.CONFLITTO.toException("Allegato con nome " + documento.getFile() + " già presente per l'erogazione");
		}
		
		if (!env.apsHelper.serviziAllegatiCheckData(TipoOperazione.ADD,filewrap,documento,env.protocolFactory)) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
		}
		
		switch (body.getRuolo()) {
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
		Documento oldDoc = Helper.supplyOrNotFound( () -> env.archiviCore.getDocumento(nomeAllegato, null, null, asps.getId(), false, ProprietariDocumento.servizio), "Allegato di nome " + nomeAllegato); 
		
		Documento newDoc = ErogazioniApiHelper.implAllegatoToDocumento(body, asps);
		newDoc.setId(oldDoc.getId());
		newDoc.setOraRegistrazione(new Date());
		
		if (! StringUtils.equals(newDoc.getRuolo(), oldDoc.getRuolo())) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Non puoi modificare il ruolo di un allegato");
		}

		WrapperFormFile filewrap = new WrapperFormFile(newDoc.getFile(), newDoc.getByteContenuto());
		env.requestWrapper.overrideParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_RUOLO, newDoc.getRuolo());
		
		if (!env.apsHelper.serviziAllegatiCheckData(TipoOperazione.CHANGE,filewrap,newDoc,env.protocolFactory)) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(StringEscapeUtils.unescapeHtml(env.pd.getMessage()));
		}
		
		AccordiServizioParteSpecificaUtilities.sostituisciDocumentoAsps(asps, oldDoc, newDoc);


		env.apsCore.performUpdateOperation(env.userLogin, false, asps);
	}
	
	public static final void updateInformazioniGenerali(ApiImplInformazioniGenerali body, final ErogazioniEnv env, final AccordoServizioParteSpecifica asps) throws Exception{

		AccordoServizioParteComune as = env.apcCore.getAccordoServizio(asps.getIdAccordo());

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
		
		ErogazioniApiHelper.serviziCheckData(TipoOperazione.CHANGE, env, as, asps, null, null, false);
						
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
			AccordoServizioParteComune apc = env.apcCore.getAccordoServizio(asps.getIdAccordo());
		
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
		AccordoServizioParteComune apc = env.apcCore.getAccordoServizio(asps.getIdAccordo());
		
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
					Helper.evalnull( () -> getConnettoreFruizione(asps, fruitore, env).getProperties().get(CostantiDB.CONNETTORE_HTTP_LOCATION) ),
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
			final AccordoServizioParteComune aspc = env.apcCore.getAccordoServizio(asps.getIdAccordo());
			
			toFill.setTipoServizio(idServizio.getTipo());
			toFill.setNome(asps.getNome());
			toFill.setVersione(asps.getVersione());
			toFill.setSoggetto(nomeSoggetto);	// Questo nelle fruizioni è il fruitore, nelle erogazioni è l'erogatore
			toFill.setApiNome(aspc.getNome());
			toFill.setApiTipo(TipoApiEnum.valueOf(aspc.getServiceBinding().name()));
			toFill.setApiVersione(asps.getVersione());
			toFill.setProfilo(env.profilo);
			toFill.setConnettore(urlConnettore);		
			toFill.setApiSoapServizio(asps.getPortType());
			toFill.setGestioneCors(Helper.boolToStatoFunzionalita(gestioneCors != null).toString());
			toFill.setUrlInvocazione(urlInvocazione);
			
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
		final AccordoServizioParteComune aspc = env.apcCore.getAccordoServizio(asps.getIdAccordo());
		ConfigurazioneCore confCore = new ConfigurazioneCore(env.stationCore);
		ConfigurazioneProtocollo configProt = confCore.getConfigurazioneProtocollo(env.tipo_protocollo);
		
		String urlInvocazione = "";

		boolean useInterfaceNameInInvocationURL = env.paHelper.useInterfaceNameInImplementationInvocationURL(env.tipo_protocollo, env.apcCore.toMessageServiceBinding(aspc.getServiceBinding()));

		String prefix = configProt.getUrlInvocazioneServizioPA();
		prefix = prefix.trim();
		if(useInterfaceNameInInvocationURL) {
			if(prefix.endsWith("/")==false) {
				prefix = prefix + "/";
			}
		}

		urlInvocazione = prefix;
		if(useInterfaceNameInInvocationURL) {
			PorteNamingUtils utils = new PorteNamingUtils(ProtocolFactoryManager.getInstance().getProtocolFactoryByName(env.tipo_protocollo));
			urlInvocazione = urlInvocazione + utils.normalizePA(pa.getNome());	
		}
		
		return urlInvocazione;
		
	}
	
	public static final String getUrlInvocazioneFruizione(AccordoServizioParteSpecifica asps, IDSoggetto fruitore, ErogazioniEnv env) throws Exception {
		

		final IDServizio idServizio = env.idServizioFactory.getIDServizioFromAccordo(asps);
		final IDPortaDelegata idPD = env.pdCore.getIDPortaDelegataAssociataDefault(idServizio, fruitore);
		final PortaDelegata pd = env.pdCore.getPortaDelegata(idPD);
		final AccordoServizioParteComune aspc = env.apcCore.getAccordoServizio(asps.getIdAccordo());
		
		ConfigurazioneCore confCore = new ConfigurazioneCore(env.stationCore);
		ConfigurazioneProtocollo configProt = confCore.getConfigurazioneProtocollo(env.tipo_protocollo);
		
		String urlInvocazione = "";

		boolean useInterfaceNameInInvocationURL = env.paHelper.useInterfaceNameInImplementationInvocationURL(env.tipo_protocollo, env.apcCore.toMessageServiceBinding(aspc.getServiceBinding()));

		String prefix = configProt.getUrlInvocazioneServizioPD();
		prefix = prefix.trim();
		if(useInterfaceNameInInvocationURL) {
			if(prefix.endsWith("/")==false) {
				prefix = prefix + "/";
			}
		}

		urlInvocazione = prefix;
		if(useInterfaceNameInInvocationURL) {
			PorteNamingUtils utils = new PorteNamingUtils(ProtocolFactoryManager.getInstance().getProtocolFactoryByName(env.tipo_protocollo));
			urlInvocazione = urlInvocazione + utils.normalizePD(pd.getNome());	
		}
		
		return urlInvocazione;
		
	}
	
	public static final DumpConfigurazione buildDumpConfigurazione(RegistrazioneMessaggi dumpConf, boolean isErogazione, ErogazioniEnv env) throws Exception {
		final RegistrazioneMessaggiConfigurazione richiesta = dumpConf.getRichiesta();
		final RegistrazioneMessaggiConfigurazione risposta = dumpConf.getRisposta();

		final String statoDump = dumpConf.getStato().toString(); 
		final String statoDumpRichiesta 		 	= Helper.boolToStatoFunzionalitaConf( Helper.evalnull( () -> richiesta.isAbilitato() )).toString();
		final String statoDumpRisposta 				= Helper.boolToStatoFunzionalitaConf( Helper.evalnull( () -> risposta.isAbilitato() )).toString();
		final String dumpRichiestaIngressoHeader	= Helper.boolToStatoFunzionalitaConf( Helper.evalnull( () -> richiesta.getIngresso().isHeaders() )).toString();
		final String dumpRichiestaIngressoBody 		= Helper.boolToStatoFunzionalitaConf( Helper.evalnull( () -> richiesta.getIngresso().isBody() )).toString();
		final String dumpRichiestaIngressoAttachments =  Helper.boolToStatoFunzionalitaConf( Helper.evalnull( () -> richiesta.getIngresso().isAttachments()) ).toString();
		final String dumpRichiestaUscitaHeader      = Helper.boolToStatoFunzionalitaConf( Helper.evalnull( () -> richiesta.getUscita().isHeaders() )).toString();
		final String dumpRichiestaUscitaBody        = Helper.boolToStatoFunzionalitaConf( Helper.evalnull( () -> richiesta.getUscita().isBody() )).toString();         
		final String dumpRichiestaUscitaAttachments = Helper.boolToStatoFunzionalitaConf( Helper.evalnull( () -> richiesta.getUscita().isAttachments() )).toString();  
		final String dumpRispostaIngressoHeader     = Helper.boolToStatoFunzionalitaConf( Helper.evalnull( () -> risposta.getIngresso().isHeaders() )).toString();     
		final String dumpRispostaIngressoBody       = Helper.boolToStatoFunzionalitaConf( Helper.evalnull( () -> risposta.getIngresso().isBody() )).toString();        
		final String dumpRispostaIngressoAttachments= Helper.boolToStatoFunzionalitaConf( Helper.evalnull( () -> risposta.getIngresso().isAttachments() )).toString(); 
		final String dumpRispostaUscitaHeader       = Helper.boolToStatoFunzionalitaConf( Helper.evalnull( () -> risposta.getUscita().isHeaders() )).toString();       
		final String dumpRispostaUscitaBody         = Helper.boolToStatoFunzionalitaConf( Helper.evalnull( () -> risposta.getUscita().isBody() )).toString();          
		final String dumpRispostaUscitaAttachments  = Helper.boolToStatoFunzionalitaConf( Helper.evalnull( () -> risposta.getUscita().isAttachments() )).toString();
		
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



	public static final ListaApiImplAllegati findAllAllegati(String q, Integer limit, Integer offset, String requestURI,
			final ErogazioniEnv env, final AccordoServizioParteSpecifica asps)
			throws DriverRegistroServiziException, InstantiationException, IllegalAccessException, CoreException {
		int idLista = Liste.SERVIZI_ALLEGATI;
		
		final Search ricerca = Helper.setupRicercaPaginata(q, limit, offset, idLista, env.idSoggetto.toIDSoggetto(), env.tipo_protocollo);
		final List<Documento> lista = env.apsCore.serviziAllegatiList(asps.getId().intValue(), ricerca);
		
		if ( env.findall_404 && lista.isEmpty() ) {
			throw FaultCode.NOT_FOUND.toException("Nessun allegato associato");
		}
		
		ListaApiImplAllegati ret = ListaUtils.costruisciListaPaginata(
				requestURI,
				offset, 
				limit, 
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
		final AccordoServizioParteComune aspc = env.apcCore.getAccordoServizio(asps.getIdAccordo());
		
		ApiImplVersioneApiView ret = new ApiImplVersioneApiView();
		
		AccordoServizioParteComune api = env.apcCore.getAccordoServizio(asps.getIdAccordo());
		
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
		
		AuthenticationHttpBasic http = new AuthenticationHttpBasic();
		c.setAutenticazioneHttp(http);
		
		http.setPassword(props.get(CostantiDB.CONNETTORE_PWD)); //Forse questi vanno presi dall'invocazione, guarda la updateConnettore.
		http.setUsername(props.get(CostantiDB.CONNETTORE_USER));
	
		ConnettoreConfigurazioneHttps https = new ConnettoreConfigurazioneHttps();
		c.setAutenticazioneHttps(https);
		
		https.setHostnameVerifier( props.get(CostantiDB.CONNETTORE_HTTPS_HOSTNAME_VERIFIER) != null 
				? Boolean.valueOf(props.get(CostantiDB.CONNETTORE_HTTPS_HOSTNAME_VERIFIER))
				: null
			);
		https.setTipologia(
				Helper.evalnull( () -> Enums.fromValue(SslTipologiaEnum.class, props.get(CostantiDB.CONNETTORE_HTTPS_SSL_TYPE)))
			);
		
		ConnettoreConfigurazioneHttpsServer httpsServer = new ConnettoreConfigurazioneHttpsServer();
		https.setServer(httpsServer);
		
		httpsServer.setAlgoritmo( Helper.evalnull( () -> 
			props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM))
			);
		httpsServer.setTruststorePassword(
				Helper.evalnull( () -> props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD))
			);
		httpsServer.setTruststorePath(
				Helper.evalnull( () -> props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_LOCATION))
			);
		httpsServer.setTruststoreTipo(
				Helper.evalnull( () -> Enums.fromValue(KeystoreEnum.class,props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_TYPE)))
			);
		
		ConnettoreConfigurazioneHttpsClient httpsClient = new ConnettoreConfigurazioneHttpsClient();
		https.setClient(httpsClient);
		
		httpsClient.setAlgoritmo(
				Helper.evalnull( () -> props.get(CostantiDB.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM))
			);
		httpsClient.setKeyPassword(
				Helper.evalnull( () -> props.get(CostantiDB.CONNETTORE_HTTPS_KEY_PASSWORD))
			);
		httpsClient.setKeystorePassword(
				Helper.evalnull( () -> props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_PASSWORD))
			);
		httpsClient.setKeystorePath(
				Helper.evalnull( () -> props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_LOCATION))
			);
		httpsClient.setKeystoreTipo(
				Helper.evalnull( () -> Enums.fromValue(KeystoreEnum.class, props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_TYPE)))
			);
		
		
		ConnettoreConfigurazioneProxy proxy = new ConnettoreConfigurazioneProxy();
		c.setProxy(proxy);
		
		proxy.setHostname(
				Helper.evalnull( () -> props.get(CostantiDB.CONNETTORE_PROXY_HOSTNAME))
			);
		proxy.setPassword(
				Helper.evalnull( () -> props.get(CostantiDB.CONNETTORE_PROXY_PASSWORD))
			);
		proxy.setPorta(
				Helper.evalnull( () -> Integer.valueOf(props.get(CostantiDB.CONNETTORE_PROXY_PORT)))
			);
		proxy.setUsername(
				Helper.evalnull( () -> props.get(CostantiDB.CONNETTORE_PROXY_USERNAME))
			);
		
		ConnettoreConfigurazioneTimeout tempiRisposta = new ConnettoreConfigurazioneTimeout();
		c.setTempiRisposta(tempiRisposta);
		
		tempiRisposta.setConnectionReadTimeout( 
				Helper.evalnull( () -> Integer.valueOf(props.get(CostantiDB.CONNETTORE_READ_CONNECTION_TIMEOUT))) 
			);
		tempiRisposta.setConnectionTimeout(
				Helper.evalnull( () -> Integer.valueOf(props.get(CostantiDB.CONNETTORE_CONNECTION_TIMEOUT)))
			);
		tempiRisposta.setTempoMedioRisposta(
				Helper.evalnull( () -> Integer.valueOf(props.get(CostantiDB.CONNETTORE_TEMPO_MEDIO_RISPOSTA)))
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
		final String allowOrigins = String.join(",", Helper.evalorElse( () -> c.getAllowOrigins(), new ArrayList<String>()) );
		final String allowHeaders = String.join(",", Helper.evalorElse( () -> c.getAllowHeaders(), new ArrayList<String>()) );
		final String allowMethods = String.join(
				",",
				Helper.evalorElse( () -> c.getAllowMethods(), new ArrayList<HttpMethodEnum>() )
					.stream().map( m -> m.name()).collect(Collectors.toList()) 
			);
	
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_STATO, corsStato );
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_TIPO, corsTipo.toString() );
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALL_ALLOW_ORIGINS, ServletUtils.boolToCheckBoxStatus(c.isAllAllowOrigins()) );
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_ORIGINS, allowOrigins );
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_HEADERS, allowHeaders );
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_ALLOW_METHODS, allowMethods );
		env.requestWrapper.overrideParameter( CostantiControlStation.PARAMETRO_CONFIGURAZIONE_CORS_EXPOSE_HEADERS, "" );
	
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
				String.join(",", Helper.evalorElse( () -> oldConf.getAccessControlExposeHeaders().getHeaderList(), new ArrayList<String>() )),
				Helper.evalorElse( () -> oldConf.getAccessControlMaxAge() != null, false),
				Helper.evalorElse( () -> oldConf.getAccessControlMaxAge(), -1 )
			);
	}
	
	
	public static final GestioneToken buildGestioneToken(ControlloAccessiGestioneToken body, Object porta, boolean isPortaDelegata, ConsoleHelper coHelper, ErogazioniEnv env) throws Exception {
				
		GestioneToken ret = new GestioneToken();
		
		ret.setPolicy( body.getPolicy() );
		ret.setTokenOpzionale( Helper.boolToStatoFunzionalitaConf(body.isTokenOpzionale()) ); 
		ret.setValidazione( StatoFunzionalitaConWarning.toEnumConstant(  Helper.evalnull(  () -> body.getValidazioneJwt().toString() )) );
		ret.setIntrospection( StatoFunzionalitaConWarning.toEnumConstant( Helper.evalnull( () -> body.getIntrospection().toString() )) );
		ret.setUserInfo( StatoFunzionalitaConWarning.toEnumConstant( Helper.evalnull( () -> body.getUserInfo().toString() )) );
		ret.setForward( Helper.boolToStatoFunzionalitaConf(body.isTokenForward()) ); 	
		
		return ret;
		
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



	public static final ResponseCachingConfigurazione buildResponseCachingConfigurazione(CachingRisposta body, PorteApplicativeHelper paHelper) {
		ResponseCachingConfigurazione newConfigurazione = null;
		if (body.getStato() == StatoDefaultRidefinitoEnum.DEFAULT) {
			
		}
		
		else if ( body.getStato() == StatoDefaultRidefinitoEnum.RIDEFINITO ) {
			newConfigurazione = paHelper.getResponseCaching(
					body.isAbilitato(), 
					body.getCacheTimeoutSeconds(), 
					body.isMaxResponseSize(),
					body.getMaxResponseSizeKb(), 
					body.isHashRequestUri(),
					body.isHashHeaders(),
					body.isHashPayload()
				);
		}
		return newConfigurazione;
	}
	
	public static final CachingRisposta buildCachingRisposta(ResponseCachingConfigurazione conf) {
		CachingRisposta ret = new CachingRisposta();
		
		if (conf == null) {
			ret.setStato(StatoDefaultRidefinitoEnum.DEFAULT);
			ret.setAbilitato(false);
			ret.setHashHeaders(null);
			ret.setHashPayload(null);
			ret.setHashRequestUri(null);
			
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
			
			ret.setHashHeaders(Helper.statoFunzionalitaConfToBool(hashInfo.getHeaders()));
			ret.setHashPayload(Helper.statoFunzionalitaConfToBool(hashInfo.getPayload()));
			ret.setHashRequestUri(Helper.statoFunzionalitaConfToBool(hashInfo.getRequestUri()));			
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
		
		allegatoXacmlPolicy.setValue( Helper.evalorElse(
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
		
		return env.paHelper.controlloAccessiCheck(
				TipoOperazione.OTHER, 
				newPd.getAutenticazione(),				// Autenticazione
				ServletUtils.boolToCheckBoxStatus( Helper.statoFunzionalitaConfToBool( newPd.getAutenticazioneOpzionale() ) ),		// Autenticazione Opzionale
				stato_autorizzazione,	 			
				ServletUtils.boolToCheckBoxStatus( TipoAutorizzazione.isAuthenticationRequired(newPd.getAutorizzazione()) ), 
				ServletUtils.boolToCheckBoxStatus( TipoAutorizzazione.isRolesRequired(newPd.getAutorizzazione()) ), 
				Helper.evalnull( () -> tipoRuoloFonte.toString() ), 
				Helper.evalnull( () -> newPd.getRuoli().getMatch().toString() ), 
				env.isSupportatoAutenticazioneSoggetti, 
				true,		// isPortaDelegata, 
				oldPd,
				Helper.evalnull( () ->
						newPd.getRuoli().getRuoloList().stream().map(Ruolo::getNome).collect(Collectors.toList())
					),
				ServletUtils.boolToCheckBoxStatus( tokenAbilitato ),
				Helper.evalnull( () -> newPd.getGestioneToken().getPolicy() ), 
				Helper.evalnull( () -> newPd.getGestioneToken().getValidazione().toString() ), 
				Helper.evalnull( () -> newPd.getGestioneToken().getIntrospection().toString() ), 
				Helper.evalnull( () -> newPd.getGestioneToken().getUserInfo().toString() ),
				Helper.evalnull( () -> newPd.getGestioneToken().getForward().toString() ),
				Helper.evalnull( () -> ServletUtils.boolToCheckBoxStatus( newPd.getGestioneToken().getOptions() != null ) ),
				Helper.evalnull( () -> newPd.getGestioneToken().getOptions() ),
				Helper.evalnull( () -> ServletUtils.boolToCheckBoxStatus( autorizzazioneScope ) ),
				Helper.evalnull( () -> newPd.getScope().getMatch().toString() ),
				allegatoXacmlPolicy,
				newPd.getAutorizzazioneContenuto(),
				env.tipo_protocollo);
	}



	public static final boolean controlloAccessiCheckPA(ErogazioniConfEnv env, PortaApplicativa oldPa, PortaApplicativa newPa)
			throws Exception {
		
		final BinaryParameter allegatoXacmlPolicy = new BinaryParameter();
		
		allegatoXacmlPolicy.setValue( Helper.evalorElse(
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
		
		return env.paHelper.controlloAccessiCheck(
				TipoOperazione.OTHER, 
				newPa.getAutenticazione(),				// Autenticazione
				ServletUtils.boolToCheckBoxStatus( Helper.statoFunzionalitaConfToBool( newPa.getAutenticazioneOpzionale() ) ),		// Autenticazione Opzionale
				stato_autorizzazione,	 			
				ServletUtils.boolToCheckBoxStatus( TipoAutorizzazione.isAuthenticationRequired(newPa.getAutorizzazione()) ), 
				ServletUtils.boolToCheckBoxStatus( TipoAutorizzazione.isRolesRequired(newPa.getAutorizzazione()) ), 
				Helper.evalnull( () -> tipoRuoloFonte.toString() ), 
				Helper.evalnull( () -> newPa.getRuoli().getMatch().toString() ), 
				env.isSupportatoAutenticazioneSoggetti, 
				false,		// isPortaDelegata, 
				oldPa,
				Helper.evalnull( () ->
						newPa.getRuoli().getRuoloList().stream().map(Ruolo::getNome).collect(Collectors.toList())
					),
				ServletUtils.boolToCheckBoxStatus( tokenAbilitato ),
				Helper.evalnull( () -> newPa.getGestioneToken().getPolicy() ), 
				Helper.evalnull( () -> newPa.getGestioneToken().getValidazione().toString() ), 
				Helper.evalnull( () -> newPa.getGestioneToken().getIntrospection().toString() ), 
				Helper.evalnull( () -> newPa.getGestioneToken().getUserInfo().toString() ),
				Helper.evalnull( () -> newPa.getGestioneToken().getForward().toString() ),
				Helper.evalnull( () -> ServletUtils.boolToCheckBoxStatus( newPa.getGestioneToken().getOptions() != null ) ),
				Helper.evalnull( () -> newPa.getGestioneToken().getOptions() ),
				Helper.evalnull( () -> ServletUtils.boolToCheckBoxStatus( autorizzazioneScope ) ),
				Helper.evalnull( () -> newPa.getScope().getMatch().toString() ),
				allegatoXacmlPolicy,
				newPa.getAutorizzazioneContenuto(),
				env.tipo_protocollo);
		
	}
	
	public static final void fillPortaDelegata(ControlloAccessiAutorizzazione body, final PortaDelegata newPd) throws InstantiationException, IllegalAccessException {
		final APIImplAutorizzazione authz = body.getAutorizzazione();
		
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
			@SuppressWarnings("unchecked") APIImplAutorizzazioneConfig config = Helper.fromMap( (Map<String,Object>) authz.getConfigurazione(), APIImplAutorizzazioneConfig.class);
			

			// defaults
			if ( config.isRuoli() && config.getRuoliFonte() == null ) {
				config.setRuoliFonte(FonteEnum.QUALSIASI);
			}
			
			final String autorizzazione = AutorizzazioneUtilities.STATO_ABILITATO;
			final String autorizzazioneAutenticati = ServletUtils.boolToCheckBoxStatus(config.isPuntuale());
			final String autorizzazioneRuoli = ServletUtils.boolToCheckBoxStatus(config.isRuoli());
			final String autorizzazioneScope = ServletUtils.boolToCheckBoxStatus(config.isScope());
			final String autorizzazione_tokenOptions = config.getTokenClaims();
			final RuoloTipologia tipoRuoloFonte = Enums.ruoloTipologiaFromRest.get(config.getRuoliFonte());
			final RuoloTipoMatch tipoRuoloMatch = RuoloTipoMatch.toEnumConstant( Helper.evalnull( () -> config.getRuoliRichiesti().toString()) );	// Gli enum coincidono
			final ScopeTipoMatch scopeTipoMatch = ScopeTipoMatch.toEnumConstant( Helper.evalnull( () -> config.getScopeRichiesti().toString()) );
	
			
			final String tipoAutorString =	AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString(autorizzazione,
					ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticati),
					ServletUtils.isCheckBoxEnabled(autorizzazioneRuoli),		
					ServletUtils.isCheckBoxEnabled(autorizzazioneScope),		
					autorizzazione_tokenOptions,					// Questo è il token claims
					tipoRuoloFonte									// RuoliFonte: Qualsiasi, Registro, Esterna
				);
			
			newPd.setAutorizzazione(tipoAutorString);
			
			if ( config.isRuoli() ) {
				if ( newPd.getRuoli() == null) newPd.setRuoli(new AutorizzazioneRuoli());
				
				newPd.getRuoli().setMatch(tipoRuoloMatch);
			} else {
				newPd.setRuoli(null);
			}
			
			
			if ( config.isScope() ) {
				if ( newPd.getScope() == null ) newPd.setScope(new AutorizzazioneScope());
				
				newPd.getScope().setMatch(scopeTipoMatch);
				newPd.getScope().setStato(StatoFunzionalita.ABILITATO);
			} else {
				newPd.setScope(null);
			}
			
			
			if ( config.isToken() ) {
				if ( newPd.getGestioneToken() == null ) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Nessun token configurato per l'erogazione");
				}
				newPd.getGestioneToken().setOptions(autorizzazione_tokenOptions);
			}
			else {
				Helper.runNull( () -> newPd.getGestioneToken().setOptions(null) );
			}
			break;
		}
		case CUSTOM:
			@SuppressWarnings("unchecked")
			APIImplAutorizzazioneCustom customConfig = Helper.fromMap( (Map<String,Object>) authz.getConfigurazione(), APIImplAutorizzazioneCustom.class);
			newPd.setAutorizzazione(customConfig.getNome());
	
			break;
		case XACML_POLICY: {
			@SuppressWarnings("unchecked")
			APIImplAutorizzazioneXACMLConfig xacmlConfig = Helper.fromMap( (Map<String,Object>) authz.getConfigurazione(), APIImplAutorizzazioneXACMLConfig.class);
			
			if (xacmlConfig.getRuoliFonte() == null)
				xacmlConfig.setRuoliFonte(FonteEnum.QUALSIASI);
			
			final RuoloTipologia tipoRuoloFonte = Enums.ruoloTipologiaFromRest.get(xacmlConfig.getRuoliFonte());
			
			final String tipoAutorString =	AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString(TipoAutorizzazioneEnum.XACML_POLICY.toString(),
					false,
					false,		
					false,
					"",
					tipoRuoloFonte									// RuoliFonte: Qualsiasi, Registro, Esterna
				);
			
			newPd.setAutorizzazione(tipoAutorString);
			newPd.setXacmlPolicy( Helper.evalnull( () -> new String(xacmlConfig.getPolicy())));				
			break;
		}
		}
		
	}



	public static final void fillPortaApplicativa(ControlloAccessiAutorizzazione body, final PortaApplicativa newPa)
			throws InstantiationException, IllegalAccessException {
		final APIImplAutorizzazione authz = body.getAutorizzazione();
		
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
			@SuppressWarnings("unchecked") APIImplAutorizzazioneConfig config = Helper.fromMap( (Map<String,Object>) authz.getConfigurazione(), APIImplAutorizzazioneConfig.class);
			
			// defaults
			if ( config.isRuoli() && config.getRuoliFonte() == null ) {
				config.setRuoliFonte(FonteEnum.QUALSIASI);
			}
			
			final String statoAutorizzazione = AutorizzazioneUtilities.STATO_ABILITATO;
			final String autorizzazioneAutenticati = ServletUtils.boolToCheckBoxStatus(config.isPuntuale());
			final String autorizzazioneRuoli = ServletUtils.boolToCheckBoxStatus(config.isRuoli());
			final String autorizzazioneScope = ServletUtils.boolToCheckBoxStatus(config.isScope());
			final String autorizzazione_tokenOptions = config.getTokenClaims();
			final RuoloTipologia tipoRuoloFonte = Enums.ruoloTipologiaFromRest.get(config.getRuoliFonte());
			final RuoloTipoMatch tipoRuoloMatch = RuoloTipoMatch.toEnumConstant( Helper.evalnull( () -> config.getRuoliRichiesti().toString()) );	// Gli enum coincidono
			final ScopeTipoMatch scopeTipoMatch = ScopeTipoMatch.toEnumConstant( Helper.evalnull( () -> config.getScopeRichiesti().toString()) );
			
			final String tipoAutorString =	AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString(statoAutorizzazione,
					ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticati),
					ServletUtils.isCheckBoxEnabled(autorizzazioneRuoli),		
					ServletUtils.isCheckBoxEnabled(autorizzazioneScope),		
					autorizzazione_tokenOptions,					// Questo è il token claims
					tipoRuoloFonte									// RuoliFonte: Qualsiasi, Registro, Esterna
				);
			
			newPa.setAutorizzazione(tipoAutorString);
			
			if ( config.isRuoli() ) {
				if ( newPa.getRuoli() == null) newPa.setRuoli(new AutorizzazioneRuoli());
				
				newPa.getRuoli().setMatch(tipoRuoloMatch);
			} else {
				newPa.setRuoli(null);
			}
			
			
			if ( config.isScope() ) {
				if ( newPa.getScope() == null ) newPa.setScope(new AutorizzazioneScope());
				
				newPa.getScope().setMatch(scopeTipoMatch);
				newPa.getScope().setStato(StatoFunzionalita.ABILITATO);
			} else {
				newPa.setScope(null);
			}
			
			
			if ( config.isToken() ) {
				if ( newPa.getGestioneToken() == null ) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Nessun token configurato per l'erogazione");
				}
				newPa.getGestioneToken().setOptions(autorizzazione_tokenOptions);
			}
			else {
				Helper.runNull( () -> newPa.getGestioneToken().setOptions(null) );
			}
			break;
		}
		case CUSTOM:
			@SuppressWarnings("unchecked")
			APIImplAutorizzazioneCustom customConfig = Helper.fromMap( (Map<String,Object>) authz.getConfigurazione(), APIImplAutorizzazioneCustom.class);
			newPa.setAutorizzazione(customConfig.getNome());
	
			break;
		case XACML_POLICY: {
			@SuppressWarnings("unchecked")
			APIImplAutorizzazioneXACMLConfig xacmlConfig = Helper.fromMap( (Map<String,Object>) authz.getConfigurazione(), APIImplAutorizzazioneXACMLConfig.class);
			
			if (xacmlConfig.getRuoliFonte() == null)
				xacmlConfig.setRuoliFonte(FonteEnum.QUALSIASI);
			
			final RuoloTipologia tipoRuoloFonte = Enums.ruoloTipologiaFromRest.get(xacmlConfig.getRuoliFonte());
			
			final String tipoAutorString =	AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString(AutorizzazioneUtilities.STATO_XACML_POLICY,
					false,
					false,		
					false,
					"",
					tipoRuoloFonte									// RuoliFonte: Qualsiasi, Registro, Esterna
				);
			
			
			newPa.setAutorizzazione(tipoAutorString);
			newPa.setXacmlPolicy( Helper.evalnull( () -> new String(xacmlConfig.getPolicy())));				
			break;
		}
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
	
		APIImplAutorizzazioneView retAuthz = new APIImplAutorizzazioneView();
		retAuthz.setTipo(tipoAuthz);
		
		switch ( tipoAuthz ) {
		case ABILITATO: {
			APIImplAutorizzazioneConfig config = new APIImplAutorizzazioneConfig();
			
			config.setPuntuale(TipoAutorizzazione.isAuthenticationRequired(pa.getAutorizzazione()));
			
			config.setRuoli( TipoAutorizzazione.isRolesRequired(pa.getAutorizzazione()) );
			if (config.isRuoli()) {
			
				config.setRuoliRichiesti( Helper.evalnull(  () -> AllAnyEnum.fromValue( pa.getRuoli().getMatch().toString()) ) );
				config.setRuoliFonte( Helper.evalnull( () -> 
						Enums.registroTipologiaToApiFonte(	AutorizzazioneUtilities.convertToRuoloTipologia(pa.getAutorizzazione()) )
					));
			}
			
			
			config.setScope( pa.getScope() != null );
			config.setScopeRichiesti( Helper.evalnull( () -> AllAnyEnum.fromValue( pa.getScope().getMatch().getValue() )));
			
			config.setToken( Helper.evalnull( () -> pa.getGestioneToken().getOptions() != null));
			config.setTokenClaims( Helper.evalnull( () -> pa.getGestioneToken().getOptions() ));
			
			retAuthz.setConfigurazione(config);
			break;
		}
		case CUSTOM: {
			APIImplAutorizzazioneCustom config = new APIImplAutorizzazioneCustom();
			config.setNome(pa.getAutorizzazione());
			retAuthz.setConfigurazione(config);
			break;
		}
		case DISABILITATO: {
			break;
		}
		case XACML_POLICY: {
			APIImplAutorizzazioneXACMLConfig config = new APIImplAutorizzazioneXACMLConfig();
			config.setPolicy( pa.getXacmlPolicy().getBytes() );
			config.setRuoliFonte( Helper.evalnull( () -> 
					Enums.registroTipologiaToApiFonte(	AutorizzazioneUtilities.convertToRuoloTipologia(pa.getAutorizzazione()) )
				));
			retAuthz.setConfigurazione(config);
			break;
		}
		}
		
		ret.setAutorizzazione(retAuthz);
		return ret;
	}
	
	public static final ControlloAccessiAutorizzazioneView controlloAccessiAutorizzazioneFromPD(final PortaDelegata pd) {
		ControlloAccessiAutorizzazioneView ret = new ControlloAccessiAutorizzazioneView();
		
		TipoAutorizzazioneEnum tipoAuthz = getTipoAutorizzazione(pd.getAutorizzazione());		
	
		APIImplAutorizzazioneView retAuthz = new APIImplAutorizzazioneView();
		retAuthz.setTipo(tipoAuthz);
		
		switch ( tipoAuthz ) {
		case ABILITATO: {
			APIImplAutorizzazioneConfig config = new APIImplAutorizzazioneConfig();
			
			config.setPuntuale( TipoAutorizzazione.isAuthenticationRequired(pd.getAutorizzazione()));
			
			
			config.setRuoli( TipoAutorizzazione.isRolesRequired(pd.getAutorizzazione()) );
			if (config.isRuoli()) {
				config.setRuoliRichiesti( Helper.evalnull(  () -> AllAnyEnum.fromValue( pd.getRuoli().getMatch().toString()) ) );
				config.setRuoliFonte( Helper.evalnull( () -> 
						Enums.registroTipologiaToApiFonte(	AutorizzazioneUtilities.convertToRuoloTipologia(pd.getAutorizzazione()) )
					));
			}
			
			
			config.setScope( pd.getScope() != null );
			config.setScopeRichiesti( Helper.evalnull( () -> AllAnyEnum.fromValue( pd.getScope().getMatch().getValue() )));
			
			config.setToken( Helper.evalnull( () -> pd.getGestioneToken().getOptions() != null));
			config.setTokenClaims( Helper.evalnull( () -> pd.getGestioneToken().getOptions() ));
						retAuthz.setConfigurazione(config);
			break;
		}
		case CUSTOM: {
			APIImplAutorizzazioneCustom config = new APIImplAutorizzazioneCustom();
			config.setNome(pd.getAutorizzazione());
			retAuthz.setConfigurazione(config);
			break;
		}
		case DISABILITATO: {
			break;
		}
		case XACML_POLICY: {
			APIImplAutorizzazioneXACMLConfig config = new APIImplAutorizzazioneXACMLConfig();
			config.setPolicy( pd.getXacmlPolicy().getBytes() );
			config.setRuoliFonte( Helper.evalnull( () -> 
					Enums.registroTipologiaToApiFonte(	AutorizzazioneUtilities.convertToRuoloTipologia(pd.getAutorizzazione()) )
				));
			retAuthz.setConfigurazione(config);
			break;
		}
		}
		
		ret.setAutorizzazione(retAuthz);
		return ret;
	}



	public static final void fillPortaApplicativa(ControlloAccessiAutenticazione body, final PortaApplicativa newPa) {
		final APIImplAutenticazione auth = body.getAutenticazione();
		
		newPa.setAutenticazioneOpzionale( Helper.evalnull( () -> Helper.boolToStatoFunzionalitaConf(auth.isOpzionale())) );
		newPa.setAutenticazione( Helper.evalnull( () -> Enums.tipoAutenticazioneFromRest.get(auth.getTipo()).toString()) );
		
		// Imposto l'autenticazione custom
		if ( Helper.evalnull( () -> auth.getTipo() ) == TipoAutenticazioneEnum.CUSTOM) {
			newPa.setAutenticazione(auth.getNome());
		}
		
		final ControlloAccessiAutenticazioneToken gToken = body.getToken();			
		if (gToken != null) {
			if(newPa.getGestioneToken() == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("La gestione token non è abilitata per il gruppo");
			
			if(newPa.getGestioneToken().getAutenticazione()==null) {
				newPa.getGestioneToken().setAutenticazione(new GestioneTokenAutenticazione());
			}
		}
		
		// Se ho abilitata la gestione token imposto i campi, in questo modo li setto tutti a null se da JSON non li ho passati.
		if (newPa.getGestioneToken().getAutenticazione() != null) {
			newPa.getGestioneToken().getAutenticazione().setIssuer(Helper.boolToStatoFunzionalitaConf(gToken.isIssuer())); 
			newPa.getGestioneToken().getAutenticazione().setClientId(Helper.boolToStatoFunzionalitaConf(gToken.isClientId())); 
			newPa.getGestioneToken().getAutenticazione().setSubject(Helper.boolToStatoFunzionalitaConf(gToken.isSubject())); 
			newPa.getGestioneToken().getAutenticazione().setUsername(Helper.boolToStatoFunzionalitaConf(gToken.isUsername())); 
			newPa.getGestioneToken().getAutenticazione().setEmail(Helper.boolToStatoFunzionalitaConf(gToken.isEmail()));	
		}
	}
	
	
	public static final void fillPortaDelegata(ControlloAccessiAutenticazione body, final PortaDelegata newPd) {
		final APIImplAutenticazione auth = body.getAutenticazione();
		
		newPd.setAutenticazioneOpzionale( Helper.evalnull( () -> Helper.boolToStatoFunzionalitaConf(auth.isOpzionale())) );
		newPd.setAutenticazione( Helper.evalnull( () -> Enums.tipoAutenticazioneFromRest.get(auth.getTipo()).toString()) );
		
		// Imposto l'autenticazione custom
		if ( Helper.evalnull( () -> auth.getTipo() ) == TipoAutenticazioneEnum.CUSTOM) {
			newPd.setAutenticazione(auth.getNome());
		}
		
		final ControlloAccessiAutenticazioneToken gToken = body.getToken();			
		if (gToken != null) {
			if(newPd.getGestioneToken() == null)
				throw FaultCode.RICHIESTA_NON_VALIDA.toException("La gestione token non è abilitata per il gruppo");
			
			if(newPd.getGestioneToken().getAutenticazione()==null) {
				newPd.getGestioneToken().setAutenticazione(new GestioneTokenAutenticazione());
			}
		}
		
		// Se ho abilitata la gestione token imposto i campi, in questo modo li setto tutti a null se da JSON non li ho passati.
		if (newPd.getGestioneToken().getAutenticazione() != null) {
			newPd.getGestioneToken().getAutenticazione().setIssuer(Helper.boolToStatoFunzionalitaConf(gToken.isIssuer())); 
			newPd.getGestioneToken().getAutenticazione().setClientId(Helper.boolToStatoFunzionalitaConf(gToken.isClientId())); 
			newPd.getGestioneToken().getAutenticazione().setSubject(Helper.boolToStatoFunzionalitaConf(gToken.isSubject())); 
			newPd.getGestioneToken().getAutenticazione().setUsername(Helper.boolToStatoFunzionalitaConf(gToken.isUsername())); 
			newPd.getGestioneToken().getAutenticazione().setEmail(Helper.boolToStatoFunzionalitaConf(gToken.isEmail()));	
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
				opts.setAllowHeaders( Helper.evalnull( () -> paConf.getAccessControlAllowHeaders().getHeaderList()) );
				opts.setAllowMethods( Helper.evalnull( () -> 
						paConf.getAccessControlAllowMethods().getMethodList().stream().map(HttpMethodEnum::valueOf).collect(Collectors.toList())
						));
				opts.setAllowOrigins( Helper.evalnull( () -> paConf.getAccessControlAllowOrigins().getOriginList()));				
				
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
		
		dest.setApplicativoFruitore(src.isServizioApplicativoFruitore());
		dest.setAzione(src.isAzione());
		
		dest.setChiaveNome(src.getInformazioneApplicativaNome());
		dest.setChiaveTipo(
				Enums.rateLimitingChiaveEnum.get( TipoFiltroApplicativo.toEnumConstant(src.getInformazioneApplicativaTipo()) )
			);
		
		return dest;
	}
	
	
	
	public static final RateLimitingPolicyGroupByFruizione convert( AttivazionePolicyRaggruppamento src, RateLimitingPolicyGroupByFruizione dest) {
		convert(src, (RateLimitingPolicyGroupBy) dest);
		return dest;
	}
	
	
	public static final RateLimitingPolicyGroupByErogazione convert( AttivazionePolicyRaggruppamento src, RateLimitingPolicyGroupByErogazione dest) {
		convert(src, (RateLimitingPolicyGroupBy) dest);
		dest.setSoggettoFruitore(src.isServizioApplicativoFruitore());
		
		return dest;
	}
	
	
	
	// CONVERSIONI POLICY FILTRO
	
	public static final RateLimitingPolicyFiltro  convert ( AttivazionePolicyFiltro src, RateLimitingPolicyFiltro dest ) {
		
		dest.setApplicativoFruitore(src.getServizioApplicativoFruitore());
		dest.setAzione(src.getAzione());
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
		dest.setIdentificativo(src.getIdActivePolicy());
				
		dest.setCriterioCollezionamentoDati(
				convert( src.getGroupBy(), new RateLimitingPolicyGroupByFruizione() )
			);
	 
		
		dest.setFiltro(
				convert( src.getFiltro(), new RateLimitingPolicyFiltroFruizione() )
			);
		
		return dest;
	}

	
	
	public static final RateLimitingPolicyErogazioneView convert ( AttivazionePolicy src, InfoPolicy infoPolicy, RateLimitingPolicyErogazioneView dest ) {
		
		convert( src, infoPolicy, (RateLimitingPolicyBase) dest );

		dest.setDescrizione(infoPolicy.getDescrizione());
		dest.setIdentificativo(src.getIdActivePolicy());
		
		dest.setCriterioCollezionamentoDati(
				convert( src.getGroupBy(), new RateLimitingPolicyGroupByErogazione() )
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



	public static final String getIdInfoPolicy(RateLimitingPolicyEnum policy) {
		
		switch(policy) {
		case NUMERO_RICHIESTE_GIORNALIERE: return "NumeroRichieste-ControlloRealtimeGiornaliero";
		case NUMERO_RICHIESTE_MINUTO: return "NumeroRichieste-ControlloRealtimeMinuti";
		case NUMERO_RICHIESTE_ORARIE: return "NumeroRichieste-ControlloRealtimeOrario";
		case NUMERO_RICHIESTE_SIMULTANEE: return "NumeroRichieste-RichiesteSimultanee";
		case OCCUPAZIONE_BANDA_ORARIA: return "OccupazioneBanda-ControlloRealtimeOrario";
		case TEMPO_MEDIO_ORARIO: return "TempoMedioRisposta-ControlloRealtimeOrario";
		}

		return null;
	}
	
	
	
	public static final void override( RateLimitingPolicyErogazioneNew body, IDSoggetto idPropietarioSa, HttpRequestWrapper wrap ) {
		override ( (RateLimitingPolicyBaseErogazione) body, idPropietarioSa, wrap );
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ID, 
				getIdInfoPolicy(body.getPolicy())	// Questo è l'id intero della policy in caso di update, può essere null
			);		
	}
	

	public static final void override( RateLimitingPolicyFruizioneNew body, IDSoggetto idPropietarioSa, HttpRequestWrapper wrap ) {
		override ( (RateLimitingPolicyBaseFruizione) body, idPropietarioSa, wrap );
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_POLICY_ID, 
				getIdInfoPolicy(body.getPolicy())
			);

	}
	
	
	public static final void override( RateLimitingPolicyBaseErogazione body, IDSoggetto idPropietarioSa, HttpRequestWrapper wrap ) {
		override( (RateLimitingPolicyBase) body, idPropietarioSa, wrap );
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD,
				RuoloPolicy.APPLICATIVA.toString() 
		);
		
		// Campi in più rispetto al padre:
		RateLimitingPolicyFiltroErogazione filtro = body.getFiltro();
		RateLimitingPolicyGroupByErogazione groupCriteria = body.getCriterioCollezionamentoDati();

		final String filtroFruitore = Helper.evalnull( () -> filtro.getSoggettoFruitore() != null 
				? new IDSoggetto(idPropietarioSa.getTipo(), filtro.getSoggettoFruitore()).toString()
				: ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_QUALSIASI );
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_FRUITORE,
				filtroFruitore 
			);
		

		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_FRUITORE,
				Helper.evalnull(  () -> Helper.evalnull(  () -> ServletUtils.boolToCheckBoxStatus( groupCriteria.isSoggettoFruitore() ) ) ) 
			);
	}
	
	
	
	
	public static final void override ( RateLimitingPolicyBaseFruizione body,  IDSoggetto idPropietarioSa,  HttpRequestWrapper wrap ) {	// Questa è in comune alla update.
		override ( (RateLimitingPolicyBase) body, idPropietarioSa, wrap );
		
		wrap.overrideParameter(
			ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_RUOLO_PDD,
			RuoloPolicy.DELEGATA.toString() 
		);
		
		
		// Campi in più rispetto al padre:
		/*	RateLimitingPolicyFiltroFruizione filtro = body.getFiltro();
			RateLimitingPolicyGroupByFruizione groupCriteria = body.getCriterioCollezionamentoDati();
			Che però Non definiscono nessun campo in più rispetto al padre, sono a posto.
		*/
	}
		
	public static final void override ( RateLimitingPolicyBase body,  IDSoggetto idPropietarioSa, HttpRequestWrapper wrap ) {	// Questa è in comune alla update.
		
		
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
				Helper.evalnull( () -> body.getSogliaValore().toString() )
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_ENABLED,
				StatoFunzionalita.ABILITATO.toString() 
			);
		
					
		RateLimitingPolicyFiltro filtro = body.getFiltro();
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_AZIONE,
				Helper.evalnull( () -> filtro.getAzione() )
			);
		
		
		IDServizioApplicativo idSaFiltroFruitore = new IDServizioApplicativo();
		idSaFiltroFruitore.setNome(filtro.getApplicativoFruitore());
		idSaFiltroFruitore.setIdSoggettoProprietario(idPropietarioSa);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_SA_FRUITORE,
				Helper.evalnull( () -> filtro.getApplicativoFruitore() != null
					? idSaFiltroFruitore.toString()
					: ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_QUALSIASI
				)
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_ENABLED,
				Helper.evalnull( () -> ServletUtils.boolToCheckBoxStatus( filtro.getChiaveTipo() != null ))	// TOWAIT: mailandrea, non ho in rest un valore per la checkbox isFiltroAbilitato, quindi deduco il valore della checkbox così 
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_TIPO,
				Helper.evalnull( () -> Enums.tipoFiltroApplicativo.get( filtro.getChiaveTipo() ).toString())	
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_NOME,
				Helper.evalnull( () -> filtro.getChiaveNome() )  
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_FILTRO_PER_CHIAVE_VALORE,
				Helper.evalnull( () -> filtro.getFiltroChiaveValore() ) 
			);
		
		RateLimitingPolicyGroupBy groupCriteria = body.getCriterioCollezionamentoDati();
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_ENABLED,
				groupCriteria != null 
					?  ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_COLLEZIONAMENTO_ABILITATO
					:  ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_STATO_COLLEZIONAMENTO_DISABILITATO
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_SA_FRUITORE,
				Helper.evalnull(  () -> ServletUtils.boolToCheckBoxStatus( groupCriteria.isApplicativoFruitore() ) )
			);
		
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_AZIONE,
				Helper.evalnull(  () -> ServletUtils.boolToCheckBoxStatus( groupCriteria.isAzione() ) )
			);			
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_ENABLED,
				Helper.evalnull(  () -> ServletUtils.boolToCheckBoxStatus( groupCriteria.getChiaveTipo() != null ) )  
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_TIPO,
				Helper.evalnull( () -> Enums.tipoFiltroApplicativo.get(groupCriteria.getChiaveTipo()).toString() )
			);
		
		wrap.overrideParameter(
				ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_CONTROLLO_TRAFFICO_POLICY_ACTIVE_GROUPBY_PER_CHIAVE_NOME,
				Helper.evalnull( () -> groupCriteria.getChiaveNome() )
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
		
		wrap.overrideParameter( CostantiControlStation.PARAMETRO_ID_CORRELAZIONE, Helper.evalnull( () -> idCorrelazione.toString() ) );					// Questo va impostato nella update.
		
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
		
		wrap.overrideParameter( CostantiControlStation.PARAMETRO_ID_CORRELAZIONE, Helper.evalnull( () -> idCorrelazione.toString() ));
	
		
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
    
    
	
	
	
	


}
