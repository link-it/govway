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

package org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.AttributeAuthority;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.pdd.config.ConfigurazionePdDReader;
import org.openspcoop2.pdd.core.connettori.ConnettoreUtils;
import org.openspcoop2.pdd.core.token.PolicyGestioneToken;
import org.openspcoop2.pdd.core.token.PolicyNegoziazioneToken;
import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.pdd.core.token.attribute_authority.AttributeAuthorityUtilities;
import org.openspcoop2.pdd.core.token.attribute_authority.PolicyAttributeAuthority;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.registry.RegistroServiziReader;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.utils.ModIUtils;
import org.openspcoop2.utils.certificate.KeystoreParams;
import org.openspcoop2.web.ctrlstat.core.CertificateChecker;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaUtilities;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * ErogazioniVerificaCertificati
 * 
 * @author Andrea Poli (poli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ErogazioniVerificaCertificati  extends Action {
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		// Parametri relativi al tipo operazione
		TipoOperazione tipoOp = TipoOperazione.OTHER;
		
		try {
			ErogazioniHelper apsHelper = new ErogazioniHelper(request, pd, session);
			
			// Preparo il menu
			apsHelper.makeMenu();
			
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apsCore);
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(apsCore);
			ConfigurazioneCore confCore = new ConfigurazioneCore(apsCore);
			
			String verificaCertificatiFromLista = apsHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CERTIFICATI_FROM_LISTA);
			boolean arrivoDaLista = "true".equalsIgnoreCase(verificaCertificatiFromLista);
			
			boolean soloModI = false;
			if(!arrivoDaLista) {
				String par = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_PROFILO);
				soloModI = "true".equalsIgnoreCase(par);
			}
						
			String id = apsHelper.getParametroLong(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			long idInt  = Long.parseLong(id);
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idInt);
			String idsogg = apsHelper.getParametroLong(CostantiControlStation.PARAMETRO_ID_SOGGETTO);
			
			String tipoSoggettoFruitore = apsHelper.getParametroTipoSoggetto(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE);
			String nomeSoggettoFruitore = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE);
			IDSoggetto idSoggettoFruitore = null;
			if(tipoSoggettoFruitore!=null && !"".equals(tipoSoggettoFruitore) &&
					nomeSoggettoFruitore!=null && !"".equals(nomeSoggettoFruitore)) {
				idSoggettoFruitore = new IDSoggetto(tipoSoggettoFruitore, nomeSoggettoFruitore);
			}
			
			String alias = apsHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER);
			
			String tipologia = ServletUtils.getObjectFromSession(request, session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
			boolean gestioneErogatori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
				}
				else if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
					gestioneErogatori = true;
				}
			}
			
			Fruitore fruitore = null;
			String idFruizione = null;
			if(gestioneFruitori) {
				// In questa modalità ci deve essere un fruitore indirizzato
				for (Fruitore check : asps.getFruitoreList()) {
					if(check.getTipo().equals(idSoggettoFruitore.getTipo()) && check.getNome().equals(idSoggettoFruitore.getNome())) {
						fruitore = check;
						break;
					}
				}
			}
			if(fruitore!=null) {
				idFruizione = fruitore.getId()+"";
			}
			
			// Prendo la lista di aliases
			List<String> aliases = apsCore.getJmxPdDAliases();
			if(aliases==null || aliases.isEmpty()){
				throw new CoreException("Pagina non prevista, la sezione configurazione non permette di accedere a questa pagina, se la configurazione non e' corretta");
			}
			
			
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
			String tipoProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(asps.getTipoSoggettoErogatore());
			
			String tmpTitle = null;
			if(gestioneFruitori) {
				tmpTitle = apsHelper.getLabelServizioFruizione(tipoProtocollo, idSoggettoFruitore, idServizio);
			}
			else {
				tmpTitle = apsHelper.getLabelServizioErogazione(tipoProtocollo, idServizio);
			}
			
			// setto la barra del titolo
			List<Parameter> listParameterChange = new ArrayList<>();
			Parameter pIdsoggErogatore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE, ""+asps.getIdSoggetto());
			Parameter pTipoSoggettoFruitore = null;
			Parameter pNomeSoggettoFruitore = null;
			if(gestioneFruitori) {
				pTipoSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE, tipoSoggettoFruitore);
				pNomeSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE, nomeSoggettoFruitore);
			}
			
			listParameterChange.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId() + ""));
			listParameterChange.add(pIdsoggErogatore);
			
			List<Parameter> lstParm = new ArrayList<>();

			if(gestioneFruitori) {
				lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_FRUIZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
				
				listParameterChange.add(pTipoSoggettoFruitore);
				listParameterChange.add(pNomeSoggettoFruitore);
			}
			else {
				lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_EROGAZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
			}
			if(arrivoDaLista) {
				String labelVerifica = ErogazioniCostanti.LABEL_ASPS_VERIFICA_CERTIFICATI_DI  + tmpTitle;
				lstParm.add(new Parameter(labelVerifica, null));
			}
			else {
				lstParm.add(new Parameter(tmpTitle, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_CHANGE, listParameterChange.toArray(new Parameter[listParameterChange.size()])));
				lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_VERIFICA_CERTIFICATI, null));
			}

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParm );
			
			List<DataElement> dati = new ArrayList<>();
			dati.add(ServletUtils.getDataElementForEditModeFinished());
			
			
			// -- raccolgo dati
			
			String nomeApiImpl = null;
			if(gestioneFruitori) {
				nomeApiImpl = apsHelper.getLabelServizioFruizione(tipoProtocollo, idSoggettoFruitore, idServizio);
			}
			else {
				nomeApiImpl = apsHelper.getLabelServizioErogazione(tipoProtocollo, idServizio);
			}
			
			boolean modi = apsCore.isProfiloModIPA(tipoProtocollo);
			boolean sicurezzaModi = false;
			if(modi) {
				IDServizio idAps = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
				idAps.setPortType(asps.getPortType());
				idAps.setUriAccordoServizioParteComune(asps.getAccordoServizioParteComune());
				IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tipoProtocollo);
				IConsoleDynamicConfiguration consoleDynamicConfiguration = protocolFactory.createDynamicConfigurationConsole();
				IRegistryReader registryReader = apsCore.getRegistryReader(protocolFactory); 
				IConfigIntegrationReader configRegistryReader = apsCore.getConfigIntegrationReader(protocolFactory);
				ConsoleConfiguration consoleConfiguration = null;
				if(!gestioneFruitori) {
					consoleConfiguration = consoleDynamicConfiguration.getDynamicConfigAccordoServizioParteSpecifica(ConsoleOperationType.CHANGE, apsHelper, 
							registryReader, configRegistryReader, idAps );
				}
				else {
					IDFruizione idFruizioneObject = new IDFruizione();
					idFruizioneObject.setIdServizio(idAps);
					idFruizioneObject.setIdFruitore(new IDSoggetto(fruitore.getTipo(), fruitore.getNome()));
					consoleConfiguration = consoleDynamicConfiguration.getDynamicConfigFruizioneAccordoServizioParteSpecifica(ConsoleOperationType.CHANGE, apsHelper,  
							registryReader, configRegistryReader, idFruizioneObject);
				}
				if(consoleConfiguration!=null && consoleConfiguration.getConsoleItem()!=null && !consoleConfiguration.getConsoleItem().isEmpty()) {
					sicurezzaModi = true;
				}
				if(sicurezzaModi) {
					boolean includeRemoteStore = false;
					boolean checkModeFruizioneKeystoreId = gestioneFruitori;
					if(gestioneFruitori && fruitore.sizeProtocolPropertyList()>0) {
						sicurezzaModi = ModIUtils.existsStoreConfig(fruitore.getProtocolPropertyList(), includeRemoteStore, checkModeFruizioneKeystoreId);
					}
					else if(gestioneErogatori && asps.sizeProtocolPropertyList()>0) {
						sicurezzaModi = ModIUtils.existsStoreConfig(asps.getProtocolPropertyList(), includeRemoteStore, checkModeFruizioneKeystoreId);
					}
				}
			}
			
			List<String> listConnettoriRegistrati = new ArrayList<>();
			List<String> listPosizioneConnettoriRegistrati = new ArrayList<>();
			List<org.openspcoop2.core.registry.Connettore> listConnettoriRegistry = new ArrayList<>();
			List<org.openspcoop2.core.config.Connettore> listConnettoriConfig = new ArrayList<>();
			
			List<String> listTokenPolicyValidazione = new ArrayList<>();
			List<GestioneToken> listTokenPolicyValidazioneConf = new ArrayList<>();
			List<String> listPosizioneTokenPolicyValidazione = new ArrayList<>();
			
			List<String> listTokenPolicyNegoziazione = new ArrayList<>();
			List<String> listPosizioneTokenPolicyNegoziazione = new ArrayList<>();
			
			List<String> listAttributeAuthority = new ArrayList<>();
			List<String> listPosizioneAttributeAuthority = new ArrayList<>();
			
			boolean findConnettoreHttpConPrefissoHttps = false;
			if(!soloModI) {
				
				if(gestioneFruitori) {
					
					boolean connettoreStatic = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tipoProtocollo).createProtocolVersionManager(null).isStaticRoute();
											
					List<MappingFruizionePortaDelegata> listaMappingFruizionePortaDelegata = apsCore.serviziFruitoriMappingList(fruitore.getId(), idSoggettoFruitore, idServizio, null);
					for(MappingFruizionePortaDelegata mappingFruizione : listaMappingFruizionePortaDelegata) {
						PortaDelegata porta = porteDelegateCore.getPortaDelegata(mappingFruizione.getIdPortaDelegata());
						
						// solo le porte applicative abilitate 
						if(StatoFunzionalita.DISABILITATO.equals(porta.getStato())) {
							continue;
						}
						
						String suffixGruppo = "";
						if(!mappingFruizione.isDefault()) {
							suffixGruppo = org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+
							"Gruppo: "+mappingFruizione.getDescrizione();
						}
						String nomeFruizione = nomeApiImpl+suffixGruppo;
						
						if(porta.getGestioneToken()!=null && porta.getGestioneToken().getPolicy()!=null && !listTokenPolicyValidazione.contains(porta.getGestioneToken().getPolicy())) {
							listTokenPolicyValidazione.add(porta.getGestioneToken().getPolicy());
							listTokenPolicyValidazioneConf.add(porta.getGestioneToken());
							listPosizioneTokenPolicyValidazione.add(nomeFruizione+
									org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+
									"Token Policy Validazione: "+porta.getGestioneToken().getPolicy());
						}
						if(porta.sizeAttributeAuthorityList()>0) {
							for (AttributeAuthority aa : porta.getAttributeAuthorityList()) {
								if(!listAttributeAuthority.contains(aa.getNome())) {
									listAttributeAuthority.add(aa.getNome());
									listPosizioneAttributeAuthority.add(nomeFruizione+
											org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+
											"Attribute Authority: "+aa.getNome());
								}
							}
						}
						
						if(!connettoreStatic) {
							org.openspcoop2.core.registry.Connettore connettore = null;
							if(mappingFruizione.isDefault()) {
								connettore = fruitore.getConnettore();
							}
							else {
								if(porta.getAzione()!=null && porta.getAzione().sizeAzioneDelegataList()>0) {
									for (String az : porta.getAzione().getAzioneDelegataList()) {
										for(ConfigurazioneServizioAzione config : fruitore.getConfigurazioneAzioneList()) {
											if(config.getAzioneList().contains(az)) {
												connettore = config.getConnettore();
												break;
											}
										}
										if(connettore!=null) {
											break;
										}
									}
								}
							}
							
							if(connettore!=null) {
								TipiConnettore tipo = TipiConnettore.toEnumFromName(connettore.getTipo());
								if( (!findConnettoreHttpConPrefissoHttps && TipiConnettore.HTTP.equals(tipo)) 
										|| 
									TipiConnettore.HTTPS.equals(tipo) ) {
									
									String nomeConnettore = connettore.getNome();
									if(listConnettoriRegistrati.contains(nomeConnettore)) {
										continue;
									}
									
									String tokenPolicy = ConnettoreUtils.getNegoziazioneTokenPolicyConnettore(connettore);
									if(tokenPolicy!=null && StringUtils.isNotEmpty(tokenPolicy) && !listTokenPolicyNegoziazione.contains(tokenPolicy)) {
										listTokenPolicyNegoziazione.add(tokenPolicy);
										listPosizioneTokenPolicyNegoziazione.add(nomeFruizione + 
												org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+
												"Token Policy Negoziazione: "+tokenPolicy);
									}
									
									if( TipiConnettore.HTTP.equals(tipo) ) {
										String endpoint = ConnettoreUtils.getEndpointConnettore(connettore, false);
										if(endpoint!=null) {
											findConnettoreHttpConPrefissoHttps = endpoint.trim().startsWith("https");
										}
									}
									else {
										listConnettoriRegistry.add(connettore);
										listConnettoriRegistrati.add(nomeConnettore);
										listPosizioneConnettoriRegistrati.add(nomeFruizione);
									}
								}
							}
						}
					}
				}
				else {
					List<MappingErogazionePortaApplicativa> listaMappingErogazionePortaApplicativa = apsCore.mappingServiziPorteAppList(idServizio,asps.getId(), null);
					for(MappingErogazionePortaApplicativa mappingErogazione : listaMappingErogazionePortaApplicativa) {
						PortaApplicativa porta = porteApplicativeCore.getPortaApplicativa(mappingErogazione.getIdPortaApplicativa());
						
						// solo le porte applicative abilitate 
						if(StatoFunzionalita.DISABILITATO.equals(porta.getStato())) {
							continue;
						}
						
						String suffixGruppo = "";
						if(!mappingErogazione.isDefault()) {
							suffixGruppo = org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+
									"Gruppo: "+mappingErogazione.getDescrizione();
						}
						String nomeErogazione = nomeApiImpl+suffixGruppo;
						
						if(porta.getGestioneToken()!=null && porta.getGestioneToken().getPolicy()!=null && !listTokenPolicyValidazione.contains(porta.getGestioneToken().getPolicy())) {
							listTokenPolicyValidazione.add(porta.getGestioneToken().getPolicy());
							listTokenPolicyValidazioneConf.add(porta.getGestioneToken());
							listPosizioneTokenPolicyValidazione.add(nomeErogazione+
									org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+
									"Token Policy Validazione: "+porta.getGestioneToken().getPolicy());
						}
						if(porta.sizeAttributeAuthorityList()>0) {
							for (AttributeAuthority aa : porta.getAttributeAuthorityList()) {
								if(!listAttributeAuthority.contains(aa.getNome())) {
									listAttributeAuthority.add(aa.getNome());
									listPosizioneAttributeAuthority.add(nomeErogazione+
										org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+
										"Attribute Authority: "+aa.getNome());
								}
							}
						}
						
						boolean connettoreMultiploEnabled = porta.getBehaviour() != null;
						
						for (PortaApplicativaServizioApplicativo paSA : porta.getServizioApplicativoList()) {
							IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
							idServizioApplicativo.setIdSoggettoProprietario(new IDSoggetto(porta.getTipoSoggettoProprietario(), porta.getNomeSoggettoProprietario()));
							idServizioApplicativo.setNome(paSA.getNome());
							ServizioApplicativo sa = saCore.getServizioApplicativo(idServizioApplicativo);
							
							InvocazioneServizio is = sa.getInvocazioneServizio();
							if(is!=null) {
								org.openspcoop2.core.config.Connettore connettore = is.getConnettore();
								if(connettore!=null) {
									TipiConnettore tipo = TipiConnettore.toEnumFromName(connettore.getTipo());
									if( (!findConnettoreHttpConPrefissoHttps && TipiConnettore.HTTP.equals(tipo)) 
											|| 
										TipiConnettore.HTTPS.equals(tipo) ) {
										
										String nomeConnettore = connettore.getNome();
										if(listConnettoriRegistrati.contains(nomeConnettore)) {
											continue;
										}
										
										if(connettoreMultiploEnabled && paSA.getDatiConnettore()!=null &&
											// solo le porte applicative abilitate 
											StatoFunzionalita.DISABILITATO.equals(paSA.getDatiConnettore().getStato())) {
											continue;
										}
										
										String nomeErogazioneConnettore = nomeErogazione;
										if(connettoreMultiploEnabled && paSA.getDatiConnettore()!=null &&
											paSA.getDatiConnettore().getNome()!=null) {
											nomeErogazioneConnettore = nomeErogazioneConnettore + 
													org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+
													"Connettore Multiplo: "+paSA.getDatiConnettore().getNome();
										}
										
										String tokenPolicy = ConnettoreUtils.getNegoziazioneTokenPolicyConnettore(connettore);
										if(tokenPolicy!=null && StringUtils.isNotEmpty(tokenPolicy) && !listTokenPolicyNegoziazione.contains(tokenPolicy)) {
											listTokenPolicyNegoziazione.add(tokenPolicy);
											listPosizioneTokenPolicyNegoziazione.add(nomeErogazioneConnettore +
													org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+
													"Token Policy Negoziazione: "+tokenPolicy);
										}
										
										if( TipiConnettore.HTTP.equals(tipo) ) {
											String endpoint = ConnettoreUtils.getEndpointConnettore(connettore, false);
											if(endpoint!=null) {
												findConnettoreHttpConPrefissoHttps = endpoint.trim().startsWith("https");
											}
										}
										else {
											listConnettoriConfig.add(connettore);
											listConnettoriRegistrati.add(nomeConnettore);
											listPosizioneConnettoriRegistrati.add(nomeErogazioneConnettore);
										}
									}
								}
							}
						}
										
					}
				}
			}
			
			
			boolean verificaCertificatiEffettuata = false;
			
			if(listConnettoriRegistrati.isEmpty() && 
					listTokenPolicyValidazione.isEmpty() &&
					listTokenPolicyNegoziazione.isEmpty() &&
					listAttributeAuthority.isEmpty() &&
					!findConnettoreHttpConPrefissoHttps &&
					!sicurezzaModi) {
				pd.setMessage(CostantiControlStation.LABEL_VERIFICA_CERTIFICATI_NON_PRESENTI,
						Costanti.MESSAGE_TYPE_INFO);
				
				pd.disableEditMode();
				
				verificaCertificatiEffettuata = true;
			}
			else {
				
				boolean sceltaClusterId = apsCore.isVerificaCertificatiSceltaClusterId();
				
				if(aliases.size()==1 || alias!=null || !sceltaClusterId) {
					
					if(alias==null && !sceltaClusterId) {
						alias = CostantiControlStation.LABEL_VERIFICA_CONNETTORE_TUTTI_I_NODI;
					}
															
					// -- verifica
					List<String> aliasesForCheck = new ArrayList<>();
					boolean all = false;
					if(aliases.size()==1) {
						aliasesForCheck.add(aliases.get(0));
					}
					else if(CostantiControlStation.LABEL_VERIFICA_CONNETTORE_TUTTI_I_NODI.equals(alias)) {
						aliasesForCheck.addAll(aliases);
						all = true;
					}
					else {
						aliasesForCheck.add(alias);
					}
					
					CertificateChecker certificateChecker = null;
					if(all) {
						certificateChecker = apsCore.getJmxPdDCertificateChecker();
					}
					else {
						certificateChecker = apsCore.newJmxPdDCertificateChecker(aliasesForCheck);
					}
					StringBuilder sbDetailsError = new StringBuilder(); 
					
					int sogliaWarningGiorni = apsCore.getVerificaCertificatiWarningExpirationDays();
					
					String posizioneErrore = null;
					String extraErrore = null;
					
					// verifica modi
					StringBuilder sbDetailsWarningModi = new StringBuilder();
					String posizioneWarningModi = null;
					if(sicurezzaModi) {
						if(gestioneFruitori) {
							org.openspcoop2.core.registry.Connettore connettore = null;
							certificateChecker.checkFruizione(sbDetailsError, sbDetailsWarningModi,
									false, connettore,
									sicurezzaModi, asps, fruitore,
									sogliaWarningGiorni);
						}
						else {
							org.openspcoop2.core.config.Connettore connettore = null;
							certificateChecker.checkErogazione(sbDetailsError, sbDetailsWarningModi,
									false, connettore,
									sicurezzaModi, asps,
									sogliaWarningGiorni);
						}
						if(sbDetailsError.length()>0) {
							posizioneErrore = nomeApiImpl;
						}
						else if(sbDetailsWarningModi.length()>0) {
							posizioneWarningModi = nomeApiImpl;
						}
					}
					
					// verifica connettori https
					StringBuilder sbDetailsWarningConnettoriHttps = new StringBuilder(); 
					String posizioneWarningConnettoriHttps = null;
					boolean connettoreSsl = !listConnettoriRegistrati.isEmpty();
					if(sbDetailsError.length()<=0 && connettoreSsl) {
						if(gestioneFruitori) {
							for (int i = 0; i < listConnettoriRegistry.size(); i++) {
								org.openspcoop2.core.registry.Connettore connettore = listConnettoriRegistry.get(i);
								String posizione = listPosizioneConnettoriRegistrati.get(i);
								
								StringBuilder sbDetailsWarningConnettoriHttpsSecured = new StringBuilder(); 
								certificateChecker.checkFruizione(sbDetailsError, sbDetailsWarningConnettoriHttpsSecured,
										connettoreSsl, connettore,
										false, asps, fruitore,
										sogliaWarningGiorni);
								if(sbDetailsError.length()>0) {
									posizioneErrore = posizione;
									break;
								}
								else if(sbDetailsWarningConnettoriHttps.length()<=0 && sbDetailsWarningConnettoriHttpsSecured.length()>0) {
									posizioneWarningModi = nomeApiImpl;
									sbDetailsWarningConnettoriHttps.append(sbDetailsWarningConnettoriHttpsSecured.toString());
								}
							}
						}
						else {
							for (int i = 0; i < listConnettoriConfig.size(); i++) {
								org.openspcoop2.core.config.Connettore connettore = listConnettoriConfig.get(i); 
								String posizione = listPosizioneConnettoriRegistrati.get(i);
								
								StringBuilder sbDetailsWarningConnettoriHttpsSecured = new StringBuilder(); 
								certificateChecker.checkErogazione(sbDetailsError, sbDetailsWarningConnettoriHttpsSecured,
										connettoreSsl, connettore,
										false, asps,
										sogliaWarningGiorni);
								if(sbDetailsError.length()>0) {
									posizioneErrore = posizione;
									break;
								}
								else if(sbDetailsWarningConnettoriHttps.length()<=0 && sbDetailsWarningConnettoriHttpsSecured.length()>0) {
									posizioneWarningModi = nomeApiImpl;
									sbDetailsWarningConnettoriHttps.append(sbDetailsWarningConnettoriHttpsSecured.toString()); // tengo solo un warning alla volta, come per gli errori
								}
							}
						}
					}
											
					// verifica token policy validazione
					StringBuilder sbDetailsWarningTokenPolicyValidazione = new StringBuilder(); 
					String posizioneWarningTokenPolicyValidazione = null;
					if(sbDetailsError.length()<=0 && listTokenPolicyValidazione!=null && !listTokenPolicyValidazione.isEmpty()) {
						for (int j = 0; j < listTokenPolicyValidazione.size(); j++) {
							GestioneToken policy = listTokenPolicyValidazioneConf.get(j);
							String posizione = listPosizioneTokenPolicyValidazione.get(j);
							GenericProperties gp = confCore.getGenericProperties(policy.getPolicy(), org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA, false);
							if(gp!=null) {
								PolicyGestioneToken policyToken = TokenUtilities.convertTo(gp, policy);
								boolean httpsIntrospection = policyToken.isIntrospection();
								boolean httpsUserInfo = policyToken.isUserInfo();
								boolean validazioneJwt = policyToken.isValidazioneJWT();
								boolean forwardToJwt = policyToken.isForwardToken();
								
								if(httpsIntrospection &&
									!policyToken.isEndpointHttps()) {
									httpsIntrospection = false;
									
									String endpoint = policyToken.getIntrospectionEndpoint();
									if(endpoint!=null && StringUtils.isNotEmpty(endpoint) &&
										!findConnettoreHttpConPrefissoHttps) {
										findConnettoreHttpConPrefissoHttps = endpoint.trim().startsWith("https");
									}
								}
								if(httpsUserInfo &&
									!policyToken.isEndpointHttps()) {
									httpsUserInfo = false;
									
									String endpoint = policyToken.getUserInfoEndpoint();
									if(endpoint!=null && StringUtils.isNotEmpty(endpoint) &&
										!findConnettoreHttpConPrefissoHttps) {
										findConnettoreHttpConPrefissoHttps = endpoint.trim().startsWith("https");
									}
								}
								
								if(validazioneJwt) {
									try {
										String tokenType = policyToken.getTipoToken();
										KeystoreParams keystoreParams = null;
										if(org.openspcoop2.pdd.core.token.Costanti.POLICY_TOKEN_TYPE_JWS.equals(tokenType) ||
												org.openspcoop2.pdd.core.token.Costanti.POLICY_TOKEN_TYPE_JWE.equals(tokenType)) {
											keystoreParams = TokenUtilities.getValidazioneJwtKeystoreParams(policyToken);
										}
										if(keystoreParams==null) {
											validazioneJwt = false;
										}
									}catch(Exception t) {
										throw new DriverConfigurazioneException(t.getMessage(),t);
									}
								}
								
								if(forwardToJwt) {
									try {
										KeystoreParams keystoreParams = null;
										if(policyToken.isForwardTokenInformazioniRaccolte()) {
											String forwardInformazioniRaccolteMode = policyToken.getForwardTokenInformazioniRaccolteMode();
											if(org.openspcoop2.pdd.core.token.Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JWS.equals(forwardInformazioniRaccolteMode) ||
													org.openspcoop2.pdd.core.token.Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS.equals(forwardInformazioniRaccolteMode) ||
													org.openspcoop2.pdd.core.token.Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE.equals(forwardInformazioniRaccolteMode)) {
												keystoreParams = TokenUtilities.getForwardToJwtKeystoreParams(policyToken);
								    		}
										}
										if(keystoreParams==null) {
											forwardToJwt = false;
										}
									}catch(Exception t) {
										throw new DriverConfigurazioneException(t.getMessage(),t);
									}
								}
								
								if(httpsIntrospection || httpsUserInfo || validazioneJwt || forwardToJwt) {
									StringBuilder sbDetailsWarningTokenPolicyValidazioneSecured = new StringBuilder(); 
									certificateChecker.checkTokenPolicyValidazione(sbDetailsError, sbDetailsWarningTokenPolicyValidazioneSecured, 
											httpsIntrospection, httpsUserInfo, validazioneJwt, forwardToJwt,
											gp,
											sogliaWarningGiorni);
									if(sbDetailsWarningTokenPolicyValidazione.length()<=0 && sbDetailsWarningTokenPolicyValidazioneSecured.length()>0) {
										posizioneWarningTokenPolicyValidazione = posizione;
										sbDetailsWarningTokenPolicyValidazione.append(sbDetailsWarningTokenPolicyValidazioneSecured.toString()); // tengo solo un warning alla volta, come per gli errori
									}
								}
							}
							if(sbDetailsError.length()>0) {
								posizioneErrore = posizione;
								break;
							}
						}
					}
					
					// verifica token policy negoziazione
					StringBuilder sbDetailsWarningTokenPolicyNegoziazione = new StringBuilder(); 
					String posizioneWarningTokenPolicyNegoziazione = null;
					if(sbDetailsError.length()<=0 && listTokenPolicyNegoziazione!=null && !listTokenPolicyNegoziazione.isEmpty()) {
						for (int j = 0; j < listTokenPolicyNegoziazione.size(); j++) {
							String policy = listTokenPolicyNegoziazione.get(j);
							String posizione = listPosizioneTokenPolicyNegoziazione.get(j);
							GenericProperties gp = confCore.getGenericProperties(policy, org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA_RETRIEVE, false);
							if(gp!=null) {
								PolicyNegoziazioneToken policyNegoziazione = TokenUtilities.convertTo(gp);
								
								boolean https = false;
								String endpoint = policyNegoziazione.getEndpoint();
								if(StringUtils.isNotEmpty(endpoint)) {
									if(policyNegoziazione.isEndpointHttps()) {
										https = true;
									}
									else if(endpoint!=null && !findConnettoreHttpConPrefissoHttps) {
										findConnettoreHttpConPrefissoHttps = endpoint.trim().startsWith("https");
									}
								}
																	
								boolean signedJwt = false;
								KeystoreParams keystoreParams = null;
								try {
									if(policyNegoziazione.isRfc7523x509Grant()) {
										// JWS Compact   			
										keystoreParams = TokenUtilities.getSignedJwtKeystoreParams(policyNegoziazione);
									}
								}catch(Exception t) {
									throw new DriverConfigurazioneException(t.getMessage(),t);
								}
								
								boolean riferimentoApplicativoModi = keystoreParams!=null && org.openspcoop2.pdd.core.token.Costanti.KEYSTORE_TYPE_APPLICATIVO_MODI_VALUE.equalsIgnoreCase(keystoreParams.getPath());
								boolean riferimentoFruizioneModi = keystoreParams!=null && org.openspcoop2.pdd.core.token.Costanti.KEYSTORE_TYPE_FRUIZIONE_MODI_VALUE.equalsIgnoreCase(keystoreParams.getPath());
								if(keystoreParams!=null &&
										!riferimentoApplicativoModi &&
										!riferimentoFruizioneModi) {
									signedJwt = true;
								}
								
								if(https || signedJwt) {
									StringBuilder sbDetailsWarningTokenPolicyNegoziazioneSecured = new StringBuilder(); 
									certificateChecker.checkTokenPolicyNegoziazione(sbDetailsError, sbDetailsWarningTokenPolicyNegoziazioneSecured,
										https, signedJwt,
										gp,
										sogliaWarningGiorni);
									if(sbDetailsWarningTokenPolicyNegoziazione.length()<=0 && sbDetailsWarningTokenPolicyNegoziazioneSecured.length()>0) {
										posizioneWarningTokenPolicyNegoziazione = posizione;
										sbDetailsWarningTokenPolicyNegoziazione.append(sbDetailsWarningTokenPolicyNegoziazioneSecured.toString()); // tengo solo un warning alla volta, come per gli errori
									}
								}
							}
							if(sbDetailsError.length()>0) {
								posizioneErrore = posizione;
								break;
							}
						}
					}
					
					// attribute authority
					StringBuilder sbDetailsWarningAttributeAuthority = new StringBuilder(); 
					String posizioneWarningAttributeAuthority = null;
					if(sbDetailsError.length()<=0 && listAttributeAuthority!=null && !listAttributeAuthority.isEmpty()) {
						for (int j = 0; j < listAttributeAuthority.size(); j++) {
							String aa = listAttributeAuthority.get(j);
							String posizione = listPosizioneAttributeAuthority.get(j);
							GenericProperties gp = confCore.getGenericProperties(aa, org.openspcoop2.pdd.core.token.Costanti.ATTRIBUTE_AUTHORITY, false);
							if(gp!=null) {
								PolicyAttributeAuthority policyAA = AttributeAuthorityUtilities.convertTo(gp);
								
								boolean https = false;
								String endpoint = policyAA.getEndpoint();
								if(StringUtils.isNotEmpty(endpoint)) {
									if(policyAA.isEndpointHttps()) {
										https = true;
									}
									else if(endpoint!=null && !findConnettoreHttpConPrefissoHttps) {
										findConnettoreHttpConPrefissoHttps = endpoint.trim().startsWith("https");
									}
								}
																	
								boolean jwtRichiesta = false;
								try {
									KeystoreParams keystoreParams = null;
									if(policyAA.isRequestJws()) {
										// JWS Compact   			
										keystoreParams = AttributeAuthorityUtilities.getRequestJwsKeystoreParams(policyAA);
									}
									if(keystoreParams!=null) {
										jwtRichiesta = true;
									}
								}catch(Exception t) {
									throw new DriverConfigurazioneException(t.getMessage(),t);
								}
								
								boolean jwtRisposta = false;
								try {
									KeystoreParams truststoreParams = null;
									if(policyAA.isResponseJws()) {
										// JWS Compact   			
										truststoreParams = AttributeAuthorityUtilities.getResponseJwsKeystoreParams(policyAA);
									}
									if(truststoreParams!=null) {
										jwtRisposta = true;
									}
								}catch(Exception t) {
									throw new DriverConfigurazioneException(t.getMessage(),t);
								}
								
								if(https || jwtRichiesta || jwtRisposta) {
									StringBuilder sbDetailsWarningAttributeAuthorityDebug = new StringBuilder(); 
									certificateChecker.checkAttributeAuthority(sbDetailsError, sbDetailsWarningAttributeAuthorityDebug,
										https, jwtRichiesta, jwtRisposta,
										gp,
										sogliaWarningGiorni);
									if(sbDetailsWarningAttributeAuthority.length()<=0 && sbDetailsWarningAttributeAuthorityDebug.length()>0) {
										posizioneWarningAttributeAuthority = posizione;
										sbDetailsWarningAttributeAuthority.append(sbDetailsWarningAttributeAuthorityDebug.toString()); // tengo solo un warning alla volta, come per gli errori
									}
								}
							}
							if(sbDetailsError.length()>0) {
								posizioneErrore = posizione;
								break;
							}
						}
					}
					
					// verifica certificati jvm
					StringBuilder sbDetailsWarningCertificatiJvm = new StringBuilder(); 
					String posizioneWarningCertificatiJvm = null;
					String extraWarningCertificatiJvm = null;
					if(sbDetailsError.length()<=0 && findConnettoreHttpConPrefissoHttps) {
						certificateChecker.checkConfigurazioneJvm(sbDetailsError, sbDetailsWarningCertificatiJvm, sogliaWarningGiorni);
						if(sbDetailsError.length()>0) {
							posizioneErrore = nomeApiImpl;
							extraErrore = "Configurazione https nella JVM";
						}
						else if(sbDetailsWarningCertificatiJvm.length()>0) {
							posizioneWarningCertificatiJvm = nomeApiImpl;
							extraWarningCertificatiJvm = "Configurazione https nella JVM";
						}
					}
					
					// analisi warning
					String warning = null;
					String posizioneWarning = null;
					String extraWarning = null;
					if(sbDetailsError.length()<=0) {
						if(sbDetailsWarningModi.length()>0) {
							warning = sbDetailsWarningModi.toString();
							posizioneWarning = posizioneWarningModi;
						}
						else if(sbDetailsWarningConnettoriHttps.length()>0) {
							warning = sbDetailsWarningConnettoriHttps.toString();
							posizioneWarning = posizioneWarningConnettoriHttps;
						}
						else if(sbDetailsWarningTokenPolicyValidazione.length()>0) {
							warning = sbDetailsWarningTokenPolicyValidazione.toString();
							posizioneWarning = posizioneWarningTokenPolicyValidazione;
						}
						else if(sbDetailsWarningTokenPolicyNegoziazione.length()>0) {
							warning = sbDetailsWarningTokenPolicyNegoziazione.toString();
							posizioneWarning = posizioneWarningTokenPolicyNegoziazione;
						}
						else if(sbDetailsWarningAttributeAuthority.length()>0) {
							warning = sbDetailsWarningAttributeAuthority.toString();
							posizioneWarning = posizioneWarningAttributeAuthority;
						}
						else if(sbDetailsWarningCertificatiJvm.length()>0) {
							warning = sbDetailsWarningCertificatiJvm.toString();
							posizioneWarning = posizioneWarningCertificatiJvm;
							extraWarning = extraWarningCertificatiJvm;
						}
					}
												
					// esito
					List<String> formatIds = new ArrayList<>();
					formatIds.add(RegistroServiziReader.ID_CONFIGURAZIONE_CONNETTORE_HTTPS);
					formatIds.add(RegistroServiziReader.ID_CONFIGURAZIONE_FIRMA_MODI);
					formatIds.add(ConfigurazionePdDReader.ID_CONFIGURAZIONE_TOKEN_VALIDAZIONE_JWT);
					formatIds.add(ConfigurazionePdDReader.ID_CONFIGURAZIONE_TOKEN_VALIDAZIONE_FORWARD_TO_JWT);
					formatIds.add(ConfigurazionePdDReader.ID_CONFIGURAZIONE_TOKEN_NEGOZIAZIONE_SIGNED_JWT);
					formatIds.add(ConfigurazionePdDReader.ID_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_JWT_RICHIESTA);
					formatIds.add(ConfigurazionePdDReader.ID_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_JWT_RISPOSTA);
					apsCore.formatVerificaCertificatiEsito(pd, formatIds, 
							(sbDetailsError.length()>0 ? sbDetailsError.toString() : null), extraErrore, posizioneErrore,
							warning, extraWarning, posizioneWarning,
							false);
	
					pd.disableEditMode();
					
					verificaCertificatiEffettuata = true;
					
				} else {
					
					DataElement deTestConnettivita = new DataElement();
					deTestConnettivita.setType(DataElementType.TITLE);
					deTestConnettivita.setLabel(ErogazioniCostanti.LABEL_ASPS_VERIFICA_CERTIFICATI);
					dati.add(deTestConnettivita);
					
					apsHelper.addVerificaCertificatoSceltaAlias(aliases, dati);	
				}
				
			}
			
			pd.setLabelBottoneInvia(CostantiControlStation.LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE_BOTTONE);
			
			
			if(idSoggettoFruitore != null) {
				dati = apsHelper.addHiddenFieldsToDati(tipoOp, id, idsogg, id, asps.getId()+"", idFruizione, tipoSoggettoFruitore, nomeSoggettoFruitore, dati);
			}else {
				dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, asps.getId()+"", null, null, dati);
			}

			DataElement	de = new DataElement();
			de.setValue(arrivoDaLista+"");
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_VERIFICA_CERTIFICATI_FROM_LISTA);
			dati.add(de);
			
			if(soloModI) {
				de = new DataElement();
				de.setValue(soloModI+"");
				de.setType(DataElementType.HIDDEN);
				de.setName(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_MODIFICA_PROFILO);
				dati.add(de);
			}
			
			pd.setDati(dati);

			if(verificaCertificatiEffettuata) {
			
				// verifica richiesta dal link nella lista, torno alla lista
				if(arrivoDaLista) {
					
					String userLogin = ServletUtils.getUserLoginFromSession(session);	
					
					ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
					
					int idLista = Liste.SERVIZI;
					
					// poiche' esistono filtri che hanno necessita di postback salvo in sessione
					List<AccordoServizioParteSpecifica> lista = null;
					if(!ServletUtils.isSearchDone(apsHelper)) {
						lista = ServletUtils.getRisultatiRicercaFromSession(request, session, idLista,  AccordoServizioParteSpecifica.class);
					}
					
					ricerca = apsHelper.checkSearchParameters(idLista, ricerca);
					
					apsHelper.clearFiltroSoggettoByPostBackProtocollo(0, ricerca, idLista);
										
					apsHelper.checkGestione(request, session, ricerca, idLista, tipologia,true);
					
					// preparo lista
					boolean [] permessi = AccordiServizioParteSpecificaUtilities.getPermessiUtente(apsHelper);
					
					if(lista==null) {
						if(apsCore.isVisioneOggettiGlobale(userLogin)){
							lista = apsCore.soggettiServizioList(null, ricerca,permessi, gestioneFruitori, gestioneErogatori);
						}else{
							lista = apsCore.soggettiServizioList(userLogin, ricerca,permessi, gestioneFruitori, gestioneErogatori);
						}
					}

					
					if(!apsHelper.isPostBackFilterElement()) {
						ServletUtils.setRisultatiRicercaIntoSession(request, session, idLista, lista); // salvo poiche' esistono filtri che hanno necessita di postback
					}
					
					apsHelper.prepareErogazioniList(ricerca, lista);
					
					// salvo l'oggetto ricerca nella sessione
					ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
					
					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
					return ServletUtils.getStrutsForwardEditModeFinished(mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI_VERIFICA_CERTIFICATI, CostantiControlStation.TIPO_OPERAZIONE_VERIFICA_CERTIFICATI);
				}
				
				// verifica richiesta dal dettaglio, torno al dettaglio
				else { 
					apsHelper.prepareErogazioneChange(tipoOp, asps, idSoggettoFruitore);
					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
					return ServletUtils.getStrutsForwardEditModeFinished(mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI, ForwardParams.CHANGE());
				}
				
			}
			else {
				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				return ServletUtils.getStrutsForwardEditModeFinished(mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI_VERIFICA_CERTIFICATI, ForwardParams.OTHER(""));
				
			}
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI_VERIFICA_CERTIFICATI, ForwardParams.OTHER(""));
		}  
	}

}
