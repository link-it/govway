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

package org.openspcoop2.web.ctrlstat.servlet.sa;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazionePortaGestioneErrore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.ProtocolProperty;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.pdd.core.connettori.ConnettoreUtils;
import org.openspcoop2.pdd.core.jmx.JMXUtils;
import org.openspcoop2.pdd.core.token.PolicyNegoziazioneToken;
import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ConsoleConfiguration;
import org.openspcoop2.protocol.sdk.properties.IConsoleDynamicConfiguration;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.KeystoreParams;
import org.openspcoop2.web.ctrlstat.core.CertificateChecker;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriHelper;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.protocol_properties.ProtocolPropertiesCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
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
 * ServiziApplicativiVerificaCertificati
 * 
 * @author Andrea Poli (poli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ServiziApplicativiVerificaCertificati extends Action {
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			ServiziApplicativiHelper saHelper = new ServiziApplicativiHelper(request, pd, session);
			
			ServiziApplicativiCore saCore = new ServiziApplicativiCore();
			SoggettiCore soggettiCore = new SoggettiCore(saCore);
			ConfigurazioneCore confCore = new ConfigurazioneCore(saCore);
			PddCore pddCore = new PddCore(saCore);
			
			String verificaCertificatiFromLista = saHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CERTIFICATI_FROM_LISTA);
			boolean arrivoDaLista = "true".equalsIgnoreCase(verificaCertificatiFromLista);
			
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione
			Integer parentSA = ServletUtils.getIntegerAttributeFromSession(ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT, session, request);
			if(parentSA == null) parentSA = ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
			boolean useIdSogg = (parentSA!=null && parentSA.intValue() == ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO);
			
			String id = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID);
			int idServizioApplicativo = Integer.parseInt(id);
			String idProvider = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			
			String alias = saHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER);
			
			// Preparo il menu
			saHelper.makeMenu();
			
			// Prendo la lista di aliases
			List<String> aliases = confCore.getJmxPdDAliases();
			if(aliases==null || aliases.isEmpty()){
				throw new CoreException("Pagina non prevista, la sezione configurazione non permette di accedere a questa pagina, se la configurazione non e' corretta");
			}

			// Prendo il nome e il provider del servizioApplicativo
			ServizioApplicativo sa = saCore.getServizioApplicativo(idServizioApplicativo);
			String nomeSA = sa.getNome();
			IDSoggetto idSoggettoProprietario = new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario());
			IDServizioApplicativo idServizioApplicativoTmp = new IDServizioApplicativo();
			idServizioApplicativoTmp.setIdSoggettoProprietario(idSoggettoProprietario);
			idServizioApplicativoTmp.setNome(nomeSA);
			String tipoProtocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(sa.getTipoSoggettoProprietario());
			
			Soggetto soggettoProprietario = soggettiCore.getSoggettoRegistro(idSoggettoProprietario);
			String dominio = pddCore.isPddEsterna(soggettoProprietario.getPortaDominio()) ? SoggettiCostanti.SOGGETTO_DOMINIO_ESTERNO_VALUE : SoggettiCostanti.SOGGETTO_DOMINIO_OPERATIVO_VALUE;
		
			boolean tokenWithHttsSupportato = false;
			if(tipoProtocollo!=null) {
				ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
				tokenWithHttsSupportato = protocolFactoryManager.getProtocolFactoryByName(tipoProtocollo).createProtocolConfiguration().isSupportatoAutenticazioneApplicativiHttpsConToken();
			}
						
			List<Parameter> parametersServletSAChange = new ArrayList<>();
			Parameter pIdSA = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_ID, sa.getId()+"");
			parametersServletSAChange.add(pIdSA);
			Parameter pIdSoggettoSA = new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER, sa.getIdSoggetto()+"");
			parametersServletSAChange.add(pIdSoggettoSA);
			if(dominio != null) {
				Parameter pDominio = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO, dominio);
				parametersServletSAChange.add(pDominio);
			}
			
			boolean modalitaCompleta = saHelper.isModalitaCompleta();
			String tmpTitle = null;
			String protocolloSoggetto = null;
			if(useIdSogg){
				if(saCore.isRegistroServiziLocale()){
					Soggetto tmpSogg =  soggettiCore.getSoggettoRegistro(Integer.parseInt(idProvider));
					protocolloSoggetto = soggettiCore.getProtocolloAssociatoTipoSoggetto(tmpSogg.getTipo());
					tmpTitle = saHelper.getLabelNomeSoggetto(protocolloSoggetto, tmpSogg.getTipo() , tmpSogg.getNome());
				}else{
					org.openspcoop2.core.config.Soggetto tmpSogg = soggettiCore.getSoggetto(Integer.parseInt(idProvider));
					protocolloSoggetto = soggettiCore.getProtocolloAssociatoTipoSoggetto(tmpSogg.getTipo());
					tmpTitle = saHelper.getLabelNomeSoggetto(protocolloSoggetto, tmpSogg.getTipo() , tmpSogg.getNome());
				}
				
			}
			
			String verificaConnettivitaS = saHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTIVITA);
			boolean verificaConnettivita = "true".equalsIgnoreCase(verificaConnettivitaS);
			
			// setto la barra del titolo
			String labelApplicativi = ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI;
			String labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_DI;
			if(!modalitaCompleta) {
				labelApplicativi = ServiziApplicativiCostanti.LABEL_APPLICATIVI;
				labelApplicativiDi = ServiziApplicativiCostanti.LABEL_PARAMETRO_APPLICATIVI_DI;
			}
			
			List<Parameter> lstParam = new ArrayList<>();
			if(!useIdSogg){
				lstParam.add(new Parameter(labelApplicativi, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST));
			} else {
				String provider = saHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
				lstParam.add(new Parameter(ServiziApplicativiCostanti.LABEL_PARAMETRO_SERVIZI_APPLICATIVI_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST));
				lstParam.add(new Parameter(labelApplicativiDi + tmpTitle, ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_LIST, new Parameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER,provider)));
			}
			
			String labelApplicativo = sa.getNome();
			
			if(arrivoDaLista) {
				String labelVerifica = (verificaConnettivita ? ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI_VERIFICA_CONNETTIVITA_DI : ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI_VERIFICA_CERTIFICATI_DI)  + 
						labelApplicativo;
				lstParam.add(new Parameter(labelVerifica, null));
			}
			else {
				lstParam.add(new Parameter(sa.getNome(), ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE, parametersServletSAChange.toArray(new Parameter[parametersServletSAChange.size()])));
				String labelVerifica = (verificaConnettivita ? ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI_VERIFICA_CONNETTIVITA : ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI_VERIFICA_CERTIFICATI);
				lstParam.add(new Parameter(labelVerifica, null));
			}
			
			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParam );
						
			List<DataElement> dati = new ArrayList<>();
			dati.add(ServletUtils.getDataElementForEditModeFinished());

			
			
			
			// -- raccolgo dati
			
			// client
			boolean ssl = false;
			boolean sslManuale = false;
			boolean piuCertificatiAssociatiEntita = false;
			boolean sicurezzaMessaggioModi = false;
			
			// server
			boolean serverHttps = false;
			boolean findConnettoreHttpConPrefissoHttps = false;
			String tokenPolicyNegoziazione = null;
			
			InvocazionePorta ip = null;
			Connettore connettore = null;
			
			if(!verificaConnettivita) {
				int countSsl = 0;
				ip = sa.getInvocazionePorta();
				for (int i = 0; i < ip.sizeCredenzialiList(); i++) {
					Credenziali c = ip.getCredenziali(i);
					if(org.openspcoop2.core.config.constants.CredenzialeTipo.SSL.equals(c.getTipo())) {
						if(c.getCertificate()!=null) {
							ssl = true;
							countSsl++;
							}
						else {
							sslManuale=true;
						}
					}
				}
				piuCertificatiAssociatiEntita = countSsl>1;
							
				// Verifica configurazione modi di sicurezza
				boolean modi = saCore.isProfiloModIPA(tipoProtocollo);
				if(modi){
					KeystoreParams keystoreParams =	org.openspcoop2.protocol.utils.ModIUtils.getApplicativoKeystoreParams(sa.getProtocolPropertyList());
					sicurezzaMessaggioModi = keystoreParams!= null;
				}
				
				// connettore https
				if(ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(sa.getTipo()) &&
					sa.getInvocazioneServizio()!=null) {
					connettore = sa.getInvocazioneServizio().getConnettore();
					TipiConnettore tipo = TipiConnettore.toEnumFromName(connettore.getTipo());
											
					if( TipiConnettore.HTTP.equals(tipo) 
							|| 
						TipiConnettore.HTTPS.equals(tipo) ) {
					
						if(TipiConnettore.HTTPS.equals(tipo)) {
							serverHttps = true;
						}
						else {
							String endpoint = ConnettoreUtils.getEndpointConnettore(connettore, false);
							if(endpoint!=null) {
								findConnettoreHttpConPrefissoHttps = endpoint.trim().startsWith("https");
							}
						}
						
						String tokenPolicy = ConnettoreUtils.getNegoziazioneTokenPolicyConnettore(connettore);
						if(tokenPolicy!=null && StringUtils.isNotEmpty(tokenPolicy)) {
							tokenPolicyNegoziazione = tokenPolicy;
						}
						
					}
				}
			}
			else {
				connettore = sa.getInvocazioneServizio().getConnettore();
			}
			
			
			boolean verificaCertificatiEffettuata = false;
			
			if(!verificaConnettivita && 
					!ssl && !sicurezzaMessaggioModi && // client
					!serverHttps && !findConnettoreHttpConPrefissoHttps && tokenPolicyNegoziazione==null
					) {
				if(sslManuale) {
					pd.setMessage(CostantiControlStation.LABEL_VERIFICA_CERTIFICATI_PRESENTE_SOLO_CONFIGURAZIONE_MANUALE,
							Costanti.MESSAGE_TYPE_INFO);
				}
				else {
					pd.setMessage(CostantiControlStation.LABEL_VERIFICA_CERTIFICATI_NON_PRESENTI,
							Costanti.MESSAGE_TYPE_INFO);
				}
				
				pd.disableEditMode();
				
				verificaCertificatiEffettuata = true;
			}
			else {
				
				boolean sceltaClusterId = true;
				if(!verificaConnettivita) {
					sceltaClusterId = soggettiCore.isVerificaCertificati_sceltaClusterId();
				}
				
				if(aliases.size()==1 || alias!=null || !sceltaClusterId) {
					
					if(alias==null && !sceltaClusterId) {
						alias = CostantiControlStation.LABEL_VERIFICA_CONNETTORE_TUTTI_I_NODI;
					}
			
					// -- verifica		
					
					if(verificaConnettivita) {
						String aliasDescrizione = null;
						if(CostantiControlStation.LABEL_VERIFICA_CONNETTORE_TUTTI_I_NODI.equals(alias)){
							aliasDescrizione = aliases.get(0);
						}
						else {
							aliasDescrizione = alias!=null ? alias : aliases.get(0);
						}
						saHelper.addDescrizioneVerificaConnettivitaToDati(dati, connettore, null, 
								aliasDescrizione
								);
						
						if (!saHelper.isEditModeInProgress()) {

							List<String> aliasesForCheck = new ArrayList<>();
							if(aliases.size()==1) {
								aliasesForCheck.add(aliases.get(0));
							}
							else if(CostantiControlStation.LABEL_VERIFICA_CONNETTORE_TUTTI_I_NODI.equals(alias)) {
								aliasesForCheck.addAll(aliases);
							}
							else {
								aliasesForCheck.add(alias);
							}
														
							boolean rilevatoErrore = false;
							StringBuilder sbPerOperazioneEffettuata = new StringBuilder();
							int index = 0;
							for (String aliasForVerificaConnettore : aliasesForCheck) {
								
								String risorsa = confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(aliasForVerificaConnettore);

								StringBuilder bfExternal = new StringBuilder();
								String descrizione = confCore.getJmxPdDDescrizione(aliasForVerificaConnettore);
								if(aliases.size()>1) {
									if(index>0) {
										bfExternal.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
									}
									bfExternal.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER).append(" ").append(descrizione).append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
								}						
								try{
									Boolean slowOperation = true; // altrimenti un eventuale connection timeout (es. 10 secondi) termina dopo il readTimeout associato all'invocazione dell'operazione via http check e quindi viene erroneamenteo ritornato un readTimeout
									String nomeMetodo = confCore.getJmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreById(aliasForVerificaConnettore);
									String stato = confCore.getInvoker().invokeJMXMethod(aliasForVerificaConnettore, confCore.getJmxPdDConfigurazioneSistemaType(aliasForVerificaConnettore),
											risorsa, 
											nomeMetodo,
											slowOperation,
											connettore.getId()+"");
									if(
											JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO.equals(stato)
											||
											(stato!=null && stato.startsWith(JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO_PREFIX))
										){
										bfExternal.append(CostantiControlStation.LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE_EFFETTUATO_CON_SUCCESSO);
									}
									else{
										rilevatoErrore = true;
										bfExternal.append(CostantiControlStation.LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE_FALLITA);
										if(stato.startsWith(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA)) {
											bfExternal.append(stato.substring(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA.length()));
										}
										else {
											bfExternal.append(stato);
										}
									}
								}catch(Exception e){
									ControlStationCore.logError("Errore durante la verifica del connettore (jmxResource '"+risorsa+"') (node:"+aliasForVerificaConnettore+"): "+e.getMessage(),e);
									rilevatoErrore = true;
									String stato = e.getMessage();
									bfExternal.append(CostantiControlStation.LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE_FALLITA);
									if(stato.startsWith(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA)) {
										bfExternal.append(stato.substring(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA.length()));
									}
									else {
										bfExternal.append(stato);
									}
								}
			
								if(sbPerOperazioneEffettuata.length()>0){
									sbPerOperazioneEffettuata.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
								}
								sbPerOperazioneEffettuata.append(bfExternal.toString());
								
								index++;
							}
							if(sbPerOperazioneEffettuata!=null){
								if(rilevatoErrore)
									pd.setMessage(sbPerOperazioneEffettuata.toString());
								else 
									pd.setMessage(sbPerOperazioneEffettuata.toString(),Costanti.MESSAGE_TYPE_INFO);
							}
			
							pd.disableEditMode();
						}
					}
					else {
					
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
							certificateChecker = soggettiCore.getJmxPdDCertificateChecker();
						}
						else {
							certificateChecker = soggettiCore.newJmxPdDCertificateChecker(aliasesForCheck);
						}
						StringBuilder sbDetailsError = new StringBuilder(); 
						
						int sogliaWarningGiorni = soggettiCore.getVerificaCertificati_warning_expirationDays();
						
						
						String posizioneErrore = null;
						String extraErrore = null;
						
						// verifica ssl
						StringBuilder sbDetailsWarningSsl = new StringBuilder();
						String posizioneWarningSsl = null;
						if(ssl) {
							certificateChecker.checkApplicativo(sbDetailsError, sbDetailsWarningSsl,
							    ssl, false, false, sa, 
							    sogliaWarningGiorni);
							if(sbDetailsError.length()>0) {
								posizioneErrore = labelApplicativo;
							}
							else if(sbDetailsWarningSsl.length()>0) {
								posizioneWarningSsl = labelApplicativo;
							}
						}
						
						// verifica modi
						StringBuilder sbDetailsWarningModi = new StringBuilder();
						String posizioneWarningModi = null;
						if(sbDetailsError.length()<=0 && sicurezzaMessaggioModi) {
							certificateChecker.checkApplicativo(sbDetailsError, sbDetailsWarningSsl,
								    false, sicurezzaMessaggioModi, false, sa, 
								    sogliaWarningGiorni);
							if(sbDetailsError.length()>0) {
								posizioneErrore = labelApplicativo;
							}
							else if(sbDetailsWarningModi.length()>0) {
								posizioneWarningModi = labelApplicativo;
							}
						}
						
						// verifica connettore https
						StringBuilder sbDetailsWarningHttps = new StringBuilder();
						String posizioneWarningHttps = null;
						if(sbDetailsError.length()<=0 && serverHttps) {
							certificateChecker.checkApplicativo(sbDetailsError, sbDetailsWarningSsl,
								    false, false, serverHttps, sa, 
								    sogliaWarningGiorni);
							if(sbDetailsError.length()>0) {
								posizioneErrore = labelApplicativo;
							}
							else if(sbDetailsWarningHttps.length()>0) {
								posizioneWarningHttps = labelApplicativo;
							}
						}
						
						// verifica token policy
						StringBuilder sbDetailsWarningPolicy = new StringBuilder();
						String posizioneWarningPolicy = null;
						if(sbDetailsError.length()<=0 && tokenPolicyNegoziazione!=null) {
							GenericProperties gp = confCore.getGenericProperties(tokenPolicyNegoziazione, org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA_RETRIEVE, false);
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
									certificateChecker.checkTokenPolicyNegoziazione(sbDetailsError, sbDetailsWarningPolicy,
										https, signedJwt,
										gp,
										sogliaWarningGiorni);
								}
							}
							if(sbDetailsError.length()>0) {
								posizioneErrore = labelApplicativo;
							}
							else if(sbDetailsWarningPolicy.length()>0) {
								posizioneWarningPolicy = labelApplicativo;
							}
						}
						
						// verifica jvm
						StringBuilder sbDetailsWarningCertificatiJvm = new StringBuilder(); 
						String posizioneWarningCertificatiJvm = null;
						String extraWarningCertificatiJvm = null;
						if(sbDetailsError.length()<=0 && findConnettoreHttpConPrefissoHttps) {
							certificateChecker.checkConfigurazioneJvm(sbDetailsError, sbDetailsWarningCertificatiJvm, sogliaWarningGiorni);
							if(sbDetailsError.length()>0) {
								posizioneErrore = labelApplicativo;
								extraErrore = "Configurazione https nella JVM";
							}
							else if(sbDetailsWarningCertificatiJvm.length()>0) {
								posizioneWarningCertificatiJvm = labelApplicativo;
								extraWarningCertificatiJvm = "Configurazione https nella JVM";
							}
						}
						
						// analisi warning
						String warning = null;
						String posizioneWarning = null;
						String extraWarning = null;
						if(sbDetailsError.length()<=0) {
							if(sbDetailsWarningSsl.length()>0) {
								warning = sbDetailsWarningSsl.toString();
								posizioneWarning = posizioneWarningSsl;
							}
							else if(sbDetailsWarningModi.length()>0) {
								warning = sbDetailsWarningModi.toString();
								posizioneWarning = posizioneWarningModi;
							}
							else if(sbDetailsWarningHttps.length()>0) {
								warning = sbDetailsWarningHttps.toString();
								posizioneWarning = posizioneWarningHttps;
							}
							else if(sbDetailsWarningPolicy.length()>0) {
								warning = sbDetailsWarningPolicy.toString();
								posizioneWarning = posizioneWarningPolicy;
							}
							else if(sbDetailsWarningCertificatiJvm.length()>0) {
								warning = sbDetailsWarningCertificatiJvm.toString();
								posizioneWarning = posizioneWarningCertificatiJvm;
								extraWarning = extraWarningCertificatiJvm;
							}
						}
						
						// esito
						List<String> formatIds = new ArrayList<>();
						soggettiCore.formatVerificaCertificatiEsito(pd, formatIds, 
								(sbDetailsError.length()>0 ? sbDetailsError.toString() : null), extraErrore, posizioneErrore,
								warning, extraWarning, posizioneWarning,
								piuCertificatiAssociatiEntita);
								
						pd.disableEditMode();
					
						verificaCertificatiEffettuata = true;
						
					}
					
				} else {
				
					DataElement deTestConnettivita = new DataElement();
					deTestConnettivita.setType(DataElementType.TITLE);
					deTestConnettivita.setLabel(ServiziApplicativiCostanti.LABEL_SERVIZI_APPLICATIVI_VERIFICA_CERTIFICATI);
					dati.add(deTestConnettivita);
					
					saHelper.addVerificaCertificatoSceltaAlias(aliases, dati);
				}
			}
			
			pd.setLabelBottoneInvia(CostantiControlStation.LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE_BOTTONE);
			
			dati = saHelper.addServizioApplicativoHiddenToDati(dati, id, idProvider, dominio, sa.getNome());
			
			DataElement	de = new DataElement();
			de.setValue(arrivoDaLista+"");
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_VERIFICA_CERTIFICATI_FROM_LISTA);
			dati.add(de);
			
			de = new DataElement();
			de.setValue(verificaConnettivita+"");
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTIVITA);
			dati.add(de);
			
			pd.setDati(dati);

			if(verificaCertificatiEffettuata) {
				
				// verifica richiesta dal link nella lista, torno alla lista
				if(arrivoDaLista) {
				
					parentSA = useIdSogg ? ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_SOGGETTO : ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT_NONE;
					
					// salvo il punto di ingresso
					ServletUtils.setObjectIntoSession(request, session, parentSA, ServiziApplicativiCostanti.ATTRIBUTO_SERVIZI_APPLICATIVI_PARENT);
					
					String userLogin = ServletUtils.getUserLoginFromSession(session);
					
					// preparo lista
					ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
					
					int idLista = -1;
					if(!useIdSogg){
						idLista = Liste.SERVIZIO_APPLICATIVO;
					}
					else {
						idLista = Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO;
					}
	
					List<ServizioApplicativo> lista = null;
										
					// poiche' esistono filtri che hanno necessita di postback salvo in sessione
					if(!ServletUtils.isSearchDone(saHelper)) {
						lista = ServletUtils.getRisultatiRicercaFromSession(request, session, idLista,  ServizioApplicativo.class);
					}
					
					ricerca = saHelper.checkSearchParameters(idLista, ricerca);
					
					saHelper.clearFiltroSoggettoByPostBackProtocollo(0, ricerca, idLista);
					
					if(!useIdSogg){
						boolean filtroSoggetto = false;
						if(saHelper.isSoggettoMultitenantSelezionato()) {
							List<String> protocolli = saCore.getProtocolli(request, session,false);
							if(protocolli!=null && protocolli.size()==1) { // dovrebbe essere l'unico caso in cui un soggetto multitenant è selezionato
								String protocollo = protocolli.get(0);
								filtroSoggetto = !saCore.isSupportatoAutenticazioneApplicativiEsterniErogazione(protocollo);  // devono essere fatti vedere anche quelli
							}
						}
						if(filtroSoggetto) {
							ricerca.addFilter(idLista, Filtri.FILTRO_SOGGETTO, saHelper.getSoggettoMultitenantSelezionato());
						}

						if(lista==null) {
							if(saCore.isVisioneOggettiGlobale(userLogin)){
								lista = saCore.soggettiServizioApplicativoList(null, ricerca);
							}else{
								lista = saCore.soggettiServizioApplicativoList(userLogin, ricerca);
							}
						}
					}else {
						ricerca = saHelper.checkSearchParameters(idLista, ricerca);
						
						lista = saCore.soggettiServizioApplicativoList(ricerca,sa.getIdSoggetto());
					}
					
					if(!saHelper.isPostBackFilterElement()) {
						ServletUtils.setRisultatiRicercaIntoSession(request, session, idLista, lista); // salvo poiche' esistono filtri che hanno necessita di postback
					}
	
					saHelper.prepareServizioApplicativoList(ricerca, lista, useIdSogg, false);
					
					ServletUtils.setSearchObjectIntoSession(request, session, ricerca);
					
					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
					return ServletUtils.getStrutsForwardEditModeFinished(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_VERIFICA_CERTIFICATI, CostantiControlStation.TIPO_OPERAZIONE_VERIFICA_CERTIFICATI);
					
				}
				
				// verifica richiesta dal dettaglio, torno al dettaglio
				else {
					
					ConsoleOperationType consoleOperationType = ConsoleOperationType.CHANGE;
					IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tipoProtocollo);
					IConsoleDynamicConfiguration consoleDynamicConfiguration = protocolFactory.createDynamicConfigurationConsole();
					IRegistryReader registryReader = soggettiCore.getRegistryReader(protocolFactory); 
					IConfigIntegrationReader configRegistryReader = soggettiCore.getConfigIntegrationReader(protocolFactory);
					ConsoleConfiguration consoleConfiguration = consoleDynamicConfiguration.getDynamicConfigServizioApplicativo(consoleOperationType, saHelper, 
							registryReader, configRegistryReader, idServizioApplicativoTmp);
					List<ProtocolProperty> oldProtocolPropertyList = sa.getProtocolPropertyList();
					ProtocolProperties protocolProperties = saHelper.estraiProtocolPropertiesDaRequest(consoleConfiguration, consoleOperationType);
					ProtocolPropertiesUtils.mergeProtocolPropertiesConfig(protocolProperties, oldProtocolPropertyList, consoleOperationType);
					
					Properties propertiesProprietario = new Properties();
					propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_ID_PROPRIETARIO, idServizioApplicativo+"");
					propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO, ProtocolPropertiesCostanti.PARAMETRO_PP_TIPO_PROPRIETARIO_VALUE_SERVIZIO_APPLICATIVO);
					propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_NOME_PROPRIETARIO, idServizioApplicativoTmp.toString());
					propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_URL_ORIGINALE_CHANGE, URLEncoder.encode( ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE + "?" + request.getQueryString(), "UTF-8"));
					propertiesProprietario.setProperty(ProtocolPropertiesCostanti.PARAMETRO_PP_PROTOCOLLO, tipoProtocollo);
					
					String oldNome = sa.getNome();
					String nomeParameter = oldNome;
					String tipoENomeSoggetto = saHelper.getLabelNomeSoggetto(tipoProtocollo, idSoggettoProprietario.getTipo() , idSoggettoProprietario.getNome());
					
					InvocazionePortaGestioneErrore ipge = null;
					Credenziali credenziali = null;
					boolean visualizzaModificaCertificato = false;
					boolean visualizzaAddCertificato = false;
					String servletCredenzialiList = ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_LIST;
					List<Parameter> parametersServletCredenzialiList = null;
					Integer numeroCertificati = 0;
					String servletCredenzialiAdd = ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CREDENZIALI_ADD;
					if (ip != null) {
						ipge = ip.getGestioneErrore();
						numeroCertificati = ip.sizeCredenzialiList();
						if(ip.sizeCredenzialiList()>0) {
							credenziali = ip.getCredenziali(0);
							
							visualizzaAddCertificato = true;
							if(ip.sizeCredenzialiList() == 1) {  // se ho definito solo un certificato c'e' il link diretto alla modifica
								visualizzaModificaCertificato = true;
							}
						}
					}
					
					parametersServletCredenzialiList = new ArrayList<>();
					parametersServletCredenzialiList.add(pIdSA);
					parametersServletCredenzialiList.add(pIdSoggettoSA);
					
					if(dominio != null) {
						Parameter pDominio = new Parameter(SoggettiCostanti.PARAMETRO_SOGGETTO_DOMINIO, dominio);
						parametersServletCredenzialiList.add(pDominio);
					}
					
					String fault = null;
					if (ipge != null &&
						ipge.getFault()!=null) {
						fault = ipge.getFault().toString();
					}
					String faultactor = null;
					if (ipge != null) {
						faultactor = ipge.getFaultActor();
					}
					String genericfault = null;
					if (ipge != null &&
						ipge.getGenericFaultCode()!=null) {
						genericfault = ipge.getGenericFaultCode().toString();
					}
					if ((genericfault == null) || "".equals(genericfault)) {
						genericfault = CostantiConfigurazione.DISABILITATO.toString();
					}
					String prefixfault = null;
					if (ipge != null) {
						prefixfault = ipge.getPrefixFaultCode();
					}
					String invrifRisposta = null;
					if (ip != null &&
						ip.getInvioPerRiferimento()!=null) {
						invrifRisposta = ip.getInvioPerRiferimento().toString();
					}
					if ((invrifRisposta == null) || "".equals(invrifRisposta)) {
						invrifRisposta = CostantiConfigurazione.DISABILITATO.toString();
					}
					String sbustamentoInformazioniProtocolloRisposta = null;
					if (ip != null &&
						ip.getSbustamentoInformazioniProtocollo()!=null) {
						sbustamentoInformazioniProtocolloRisposta = ip.getSbustamentoInformazioniProtocollo().toString();
					}
					if ((sbustamentoInformazioniProtocolloRisposta == null) || "".equals(sbustamentoInformazioniProtocolloRisposta)) {
						sbustamentoInformazioniProtocolloRisposta = CostantiConfigurazione.ABILITATO.toString();
					}
							
					String ruoloFruitore=sa.getTipologiaFruizione();
					String ruoloErogatore=sa.getTipologiaErogazione();
					String tipoSA = sa.getTipo();
					boolean	useAsClient = sa.isUseAsClient();
					if(tipoSA == null)
						tipoSA = "";
					
					InvocazioneServizio is = sa.getInvocazioneServizio();
					if(is==null) {
						is = new InvocazioneServizio();
					}
					String sbustamento = null;
					if(is.getSbustamentoSoap()!=null) {
						sbustamento = is.getSbustamentoSoap().toString();
					}
					String sbustamentoInformazioniProtocolloRichiesta = null;
					if(is.getSbustamentoInformazioniProtocollo()!=null) {
						sbustamentoInformazioniProtocolloRichiesta = is.getSbustamentoInformazioniProtocollo().toString();
					}
					String getmsg = null;
					if(is.getGetMessage()!=null) {
						getmsg = is.getGetMessage().toString();
					}
					boolean integrationManagerEnabled = !saHelper.isModalitaStandard() && saCore.isIntegrationManagerEnabled();
					if(!integrationManagerEnabled && CostantiConfigurazione.ABILITATO.toString().equals(getmsg)) {
						// faccio vedere I.M. anche con interfaccia standard
						integrationManagerEnabled = true;
					}
					
					String invrifRichiesta = null;
					if(is.getInvioPerRiferimento()!=null) {
						invrifRichiesta = is.getInvioPerRiferimento().toString();
					}
					if ((invrifRichiesta == null) || "".equals(invrifRichiesta)) {
						invrifRichiesta = CostantiConfigurazione.DISABILITATO.toString();
					}
					String risprif = null;
					if(is.getRispostaPerRiferimento()!=null) {
						risprif = is.getRispostaPerRiferimento().toString();
					}
					if ((risprif == null) || "".equals(risprif)) {
						risprif = CostantiConfigurazione.DISABILITATO.toString();
					}
					String user = null;
					String password = null;
					InvocazioneCredenziali cis = is.getCredenziali();
					if ((user == null) && (cis != null)) {
						user = cis.getUser();
						password = cis.getPassword();
					}
					String endpointtype = null;
					String tipoconn = null;
					Connettore connis = is.getConnettore();
					if (endpointtype == null) {
						if ((connis!=null && connis.getCustom()!=null && connis.getCustom()) && !connis.getTipo().equals(TipiConnettore.HTTPS.toString())) {
							endpointtype = TipiConnettore.CUSTOM.toString();
							tipoconn = connis.getTipo();
						} else {
							if(connis!=null) {
								endpointtype = connis.getTipo();
							}
						}
					}
					if(endpointtype==null){
						endpointtype=TipiConnettore.DISABILITATO.toString();
					}

					Map<String, String> props = null;
					if(connis!=null)
						props = connis.getProperties();
					
					String connettoreDebug = null;
					if(connettoreDebug==null && props!=null){
						String v = props.get(CostantiDB.CONNETTORE_DEBUG);
						if(v!=null){
							if("true".equals(v)){
								connettoreDebug = Costanti.CHECK_BOX_ENABLED;
							}
							else{
								connettoreDebug = Costanti.CHECK_BOX_DISABLED;
							}
						}
					}
					
					String proxyEnabled = null;
					String proxyHostname = null;
					String proxyPort = null;
					String proxyUsername = null;
					String proxyPassword = null;
					if(proxyEnabled==null && props!=null){
						String v = props.get(CostantiDB.CONNETTORE_PROXY_TYPE);
						if(v!=null && !"".equals(v)){
							proxyEnabled = Costanti.CHECK_BOX_ENABLED_TRUE;
							
							// raccolgo anche altre proprietà
							v = props.get(CostantiDB.CONNETTORE_PROXY_HOSTNAME);
							if(v!=null && !"".equals(v)){
								proxyHostname = v.trim();
							}
							v = props.get(CostantiDB.CONNETTORE_PROXY_PORT);
							if(v!=null && !"".equals(v)){
								proxyPort = v.trim();
							}
							v = props.get(CostantiDB.CONNETTORE_PROXY_USERNAME);
							if(v!=null && !"".equals(v)){
								proxyUsername = v.trim();
							}
							v = props.get(CostantiDB.CONNETTORE_PROXY_PASSWORD);
							if(v!=null && !"".equals(v)){
								proxyPassword = v.trim();
							}
						}
					}
					
					String tempiRispostaEnabled = null;
					String tempiRispostaConnectionTimeout = null;
					String tempiRispostaReadTimeout = null;
					String tempiRispostaTempoMedioRisposta = null;
					if(tempiRispostaEnabled == null ||
							tempiRispostaConnectionTimeout==null || "".equals(tempiRispostaConnectionTimeout) 
							|| 
							tempiRispostaReadTimeout==null || "".equals(tempiRispostaReadTimeout) 
							|| 
							tempiRispostaTempoMedioRisposta==null || "".equals(tempiRispostaTempoMedioRisposta) ){
						
						ConfigurazioneCore configCore = new ConfigurazioneCore(soggettiCore);
						ConfigurazioneGenerale configGenerale = configCore.getConfigurazioneControlloTraffico();
						
						if( props!=null ) {
							if(tempiRispostaConnectionTimeout==null || "".equals(tempiRispostaConnectionTimeout) ) {
								String v = props.get(CostantiDB.CONNETTORE_CONNECTION_TIMEOUT);
								if(v!=null && !"".equals(v)){
									tempiRispostaConnectionTimeout = v.trim();
									tempiRispostaEnabled =  Costanti.CHECK_BOX_ENABLED_TRUE;
								}
								else {
									tempiRispostaConnectionTimeout = configGenerale.getTempiRispostaErogazione().getConnectionTimeout().intValue()+"";
								}
							}
								
							if(tempiRispostaReadTimeout==null || "".equals(tempiRispostaReadTimeout) ) {
								String v = props.get(CostantiDB.CONNETTORE_READ_CONNECTION_TIMEOUT);
								if(v!=null && !"".equals(v)){
									tempiRispostaReadTimeout = v.trim();
									tempiRispostaEnabled =  Costanti.CHECK_BOX_ENABLED_TRUE;
								}
								else {
									tempiRispostaReadTimeout = configGenerale.getTempiRispostaErogazione().getReadTimeout().intValue()+"";
								}
							}
							
							if(tempiRispostaTempoMedioRisposta==null || "".equals(tempiRispostaTempoMedioRisposta) ) {
								String v = props.get(CostantiDB.CONNETTORE_TEMPO_MEDIO_RISPOSTA);
								if(v!=null && !"".equals(v)){
									tempiRispostaTempoMedioRisposta = v.trim();
									tempiRispostaEnabled =  Costanti.CHECK_BOX_ENABLED_TRUE;
								}
								else {
									tempiRispostaTempoMedioRisposta = configGenerale.getTempiRispostaErogazione().getTempoMedioRisposta().intValue()+"";
								}
							}
						}
						else {
							if(tempiRispostaConnectionTimeout==null || "".equals(tempiRispostaConnectionTimeout) ) {
								tempiRispostaConnectionTimeout = configGenerale.getTempiRispostaErogazione().getConnectionTimeout().intValue()+"";
							}
							if(tempiRispostaReadTimeout==null || "".equals(tempiRispostaReadTimeout) ) {
								tempiRispostaReadTimeout = configGenerale.getTempiRispostaErogazione().getReadTimeout().intValue()+"";
							}
							if(tempiRispostaTempoMedioRisposta==null || "".equals(tempiRispostaTempoMedioRisposta) ) {
								tempiRispostaTempoMedioRisposta = configGenerale.getTempiRispostaErogazione().getTempoMedioRisposta().intValue()+"";
							}
						}
					}
					
					String transferMode = null;
					String transferModeChunkSize = null;
					if(transferMode==null && props!=null){
						String v = props.get(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE);
						if(v!=null && !"".equals(v)){
							
							transferMode = v.trim();
							
							if(TransferLengthModes.TRANSFER_ENCODING_CHUNKED.getNome().equals(transferMode)){
								// raccolgo anche altra proprietà correlata
								v = props.get(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE);
								if(v!=null && !"".equals(v)){
									transferModeChunkSize = v.trim();
								}
							}
							
						}
					}
					
					String redirectMode = null;
					String redirectMaxHop = null;
					if(redirectMode==null && props!=null){
						String v = props.get(CostantiDB.CONNETTORE_HTTP_REDIRECT_FOLLOW);
						if(v!=null && !"".equals(v)){
							
							if("true".equalsIgnoreCase(v.trim()) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v.trim())){
								redirectMode = CostantiConfigurazione.ABILITATO.getValue();
							}
							else{
								redirectMode = CostantiConfigurazione.DISABILITATO.getValue();
							}					
							
							if(CostantiConfigurazione.ABILITATO.getValue().equals(redirectMode)){
								// raccolgo anche altra proprietà correlata
								v = props.get(CostantiDB.CONNETTORE_HTTP_REDIRECT_MAX_HOP);
								if(v!=null && !"".equals(v)){
									redirectMaxHop = v.trim();
								}
							}
							
						}
					}
					
					String tokenPolicy = null;
					boolean autenticazioneToken = false;
					if(tokenPolicy==null && props!=null){
						String v = props.get(CostantiDB.CONNETTORE_TOKEN_POLICY);
						if(v!=null && !"".equals(v)){
							tokenPolicy = v;
							autenticazioneToken = true;
						}
					}
					
					String opzioniAvanzate = ConnettoriHelper.getOpzioniAvanzate(saHelper, transferMode, redirectMode);
					
					String autenticazioneHttp = saHelper.getAutenticazioneHttp(null, endpointtype, user);
					
					List<Property> cp = connis!=null ? connis.getPropertyList() : null;
					String url = null;
					String nomeCodaJMS = null;
					String tipoCodaJMS = null;
					String connfact = null;
					String sendas = null;
					String initcont = null;
					String urlpgk = null;
					String provurl = null;
					if(connis!=null) {
						for (int i = 0; i < connis.sizePropertyList(); i++) {
							Property singlecp = cp.get(i);
							if (singlecp.getNome().equals(CostantiDB.CONNETTORE_HTTP_LOCATION) &&
								url == null) {
								url = singlecp.getValore();
							}
							if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_NOME) &&
								nomeCodaJMS == null) {
								nomeCodaJMS = singlecp.getValore();
							}
							if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_TIPO) &&
								tipoCodaJMS == null) {
								tipoCodaJMS = singlecp.getValore();
							}
							if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY) &&
								connfact == null) {
								connfact = singlecp.getValore();
							}
							if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_SEND_AS) &&
								sendas == null) {
								sendas = singlecp.getValore();
							}
							if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL) &&
								initcont == null) {
								initcont = singlecp.getValore();
							}
							if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG) &&
								urlpgk == null) {
								urlpgk = singlecp.getValore();
							}
							if (singlecp.getNome().equals(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL) &&
								provurl == null) {
								provurl = singlecp.getValore();
							}
						}
					}

					String httpstipologia = null;
					String httpsurl = null;
					String httpshostverifyS = null;
					boolean httpshostverify = false;
					String httpsTrustVerifyCertS = null;
					boolean httpsTrustVerifyCert = false;
					String httpspath = null;
					String httpstipo = null;
					String httpspwd = null;
					String httpsalgoritmo = null;
					String httpspwdprivatekeytrust = null;
					String httpspathkey = null;
					String httpstipokey = null;
					String httpspwdkey = null;
					String httpspwdprivatekey = null;
					String httpsalgoritmokey = null;
					String httpsKeyAlias = null;
					String httpsTrustStoreCRLs = null;
					String httpsTrustStoreOCSPPolicy = null;
					boolean httpsstato = false;
					String httpskeystore = null;
					if (httpstipologia == null && props!=null) {
						httpsurl = props.get(CostantiDB.CONNETTORE_HTTPS_LOCATION);
						httpstipologia = props.get(CostantiDB.CONNETTORE_HTTPS_SSL_TYPE);
						httpshostverifyS = props.get(CostantiDB.CONNETTORE_HTTPS_HOSTNAME_VERIFIER);
						if(httpshostverifyS!=null){
							httpshostverify = Boolean.valueOf(httpshostverifyS);
						}
						httpsTrustVerifyCertS = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_ALL_CERTS);
						if(httpsTrustVerifyCertS!=null){
							httpsTrustVerifyCert = !Boolean.valueOf(httpsTrustVerifyCertS);
						}
						else {
							httpsTrustVerifyCert = true; // backward compatibility
						}
						httpspath = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_LOCATION);
						httpstipo = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_TYPE);
						httpspwd = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
						httpsalgoritmo = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM);
						httpspwdprivatekeytrust = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_PASSWORD);
						httpspathkey = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_LOCATION);
						httpstipokey = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_TYPE);
						httpspwdkey = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
						httpspwdprivatekey = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_PASSWORD);
						httpsalgoritmokey = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM);
						httpsKeyAlias = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_ALIAS);
						httpsTrustStoreCRLs = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_CRLS);
						httpsTrustStoreOCSPPolicy = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY);
						if (httpspathkey == null) {
							httpsstato = false;
							httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
						} else {
							httpsstato = true;
							if (httpspathkey.equals(httpspath) &&
									httpstipokey.equals(httpstipo) &&
									httpspwdkey.equals(httpspwd))
								httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
							else
								httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI;
						}
					}
					
					// default
					if(httpsalgoritmo==null || "".equals(httpsalgoritmo)){
						httpsalgoritmo = TrustManagerFactory.getDefaultAlgorithm();
					}
					if(httpsalgoritmokey==null || "".equals(httpsalgoritmokey)){
						httpsalgoritmokey = KeyManagerFactory.getDefaultAlgorithm();
					}
					if(httpstipologia==null || "".equals(httpstipologia)){
						httpstipologia = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TYPE;
					}
					if(httpshostverifyS==null || "".equals(httpshostverifyS)){
						httpshostverifyS = "true";
						httpshostverify = true;
					}
					if(httpsTrustVerifyCertS==null || "".equals(httpsTrustVerifyCertS)){
						httpsTrustVerifyCertS = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS ? Costanti.CHECK_BOX_ENABLED_TRUE : Costanti.CHECK_BOX_DISABLED;
						httpsTrustVerifyCert = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS;
					}
					
					// file
					String responseInputMode = null;
					String requestOutputFileName = null;
					String requestOutputFileNamePermissions = null;
					String requestOutputFileNameHeaders = null;
					String requestOutputFileNameHeadersPermissions = null;
					String requestOutputParentDirCreateIfNotExists = null;
					String requestOutputOverwriteIfExists = null;
					String responseInputFileName = null;
					String responseInputFileNameHeaders = null;
					String responseInputDeleteAfterRead = null;
					String responseInputWaitTime = null;
					if(responseInputMode==null && props!=null){
						
						requestOutputFileName = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE);	
						requestOutputFileNamePermissions = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_PERMISSIONS);
						requestOutputFileNameHeaders = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_FILE_HEADERS);
						requestOutputFileNameHeadersPermissions = saHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_FILE_REQUEST_OUTPUT_FILE_NAME_HEADERS_PERMISSIONS);
						String v = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_AUTO_CREATE_DIR);
						if(v!=null && !"".equals(v)){
							if("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) ){
								requestOutputParentDirCreateIfNotExists = Costanti.CHECK_BOX_ENABLED_TRUE;
							}
						}					
						v = props.get(CostantiDB.CONNETTORE_FILE_REQUEST_OUTPUT_OVERWRITE_FILE);
						if(v!=null && !"".equals(v)){
							if("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) ){
								requestOutputOverwriteIfExists = Costanti.CHECK_BOX_ENABLED_TRUE;
							}
						}	
						
						v = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_MODE);
						if(v!=null && !"".equals(v)){
							if("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) ){
								responseInputMode = CostantiConfigurazione.ABILITATO.getValue();
							}
						}
						if(CostantiConfigurazione.ABILITATO.getValue().equals(responseInputMode)){						
							responseInputFileName = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE);
							responseInputFileNameHeaders = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE_HEADERS);
							v = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_FILE_DELETE_AFTER_READ);
							if(v!=null && !"".equals(v)){
								if("true".equalsIgnoreCase(v) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(v) ){
									responseInputDeleteAfterRead = Costanti.CHECK_BOX_ENABLED_TRUE;
								}
							}						
							responseInputWaitTime = props.get(CostantiDB.CONNETTORE_FILE_RESPONSE_INPUT_WAIT_TIME);						
						}
						
					}
					
					Boolean isConnettoreCustomUltimaImmagineSalvata = connis!=null ? connis.getCustom() : null;
					
					Boolean contaListe = ServletUtils.getContaListeFromSession(session);
					
					String provider = sa.getIdSoggetto()+"";
					
					String tipoauthSA = null;
					String utenteSA = null;
					String passwordSA = null;
					String subjectSA = null;
					String issuerSA = null;
					String principalSA = null;
					String multipleApiKey = null;
					String appId = null;
					String apiKey = null;
					String tokenPolicySA = null;
					String tokenClientIdSA = null;
					boolean tokenWithHttpsEnabledByConfigSA = false;
					BinaryParameter tipoCredenzialiSSLFileCertificato = saHelper.getBinaryParameter(ConnettoriCostanti.PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_FILE_CERTIFICATO);
					String tipoCredenzialiSSLSorgente = null;
					String tipoCredenzialiSSLAliasCertificato = null;
					String tipoCredenzialiSSLAliasCertificatoSubject= null;
					String tipoCredenzialiSSLAliasCertificatoIssuer= null;
					String tipoCredenzialiSSLAliasCertificatoType= null;
					String tipoCredenzialiSSLAliasCertificatoVersion= null;
					String tipoCredenzialiSSLAliasCertificatoSerialNumber= null;
					String tipoCredenzialiSSLAliasCertificatoSelfSigned= null;
					String tipoCredenzialiSSLAliasCertificatoNotBefore= null;
					String tipoCredenzialiSSLAliasCertificatoNotAfter = null;
					String tipoCredenzialiSSLVerificaTuttiICampi = null;
					String tipoCredenzialiSSLConfigurazioneManualeSelfSigned= null;
					org.openspcoop2.utils.certificate.ArchiveType tipoCredenzialiSSLTipoArchivio = org.openspcoop2.utils.certificate.ArchiveType.CER; 
					String tipoCredenzialiSSLFileCertificatoPassword = null;
					List<String> listaAliasEstrattiCertificato = new ArrayList<>();
					
					String tipoCredenzialiSSLWizardStep = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_NO_WIZARD;
					
					if(credenziali!=null && credenziali.getTipo()!=null) {
						tipoauthSA = credenziali.getTipo().getValue();
						if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_BASIC.equals(tipoauthSA)) {
							utenteSA = credenziali.getUser();
							passwordSA = credenziali.getPassword();
							tipoCredenzialiSSLVerificaTuttiICampi = credenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
						}
						if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_APIKEY.equals(tipoauthSA)) {
							utenteSA = credenziali.getUser();
							passwordSA = credenziali.getPassword();
							tipoCredenzialiSSLVerificaTuttiICampi = credenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
							multipleApiKey = credenziali.isAppId() ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
							appId = credenziali.getUser();
							apiKey = credenziali.getPassword();
						}
						if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_PRINCIPAL.equals(tipoauthSA)) {
							principalSA = credenziali.getUser();
						}
						
						if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_TOKEN.equals(tipoauthSA)) {
							tokenClientIdSA = credenziali.getUser();
							tokenPolicySA = credenziali.getTokenPolicy();
						}
						else if(ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL.equals(tipoauthSA) && tokenWithHttsSupportato) {
							tokenClientIdSA = credenziali.getUser();
							tokenPolicySA = credenziali.getTokenPolicy();
							tokenWithHttpsEnabledByConfigSA = ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL.equals(tipoauthSA) && StringUtils.isNotEmpty(tokenClientIdSA);
						}
						tokenWithHttpsEnabledByConfigSA = ConnettoriCostanti.AUTENTICAZIONE_TIPO_SSL.equals(tipoauthSA) && StringUtils.isNotEmpty(tokenClientIdSA);
						
					}
					
					if(credenziali!=null) {
						
						if(credenziali.getCertificate() != null) {
						
							tipoCredenzialiSSLFileCertificato.setValue(credenziali.getCertificate());
							tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_UPLOAD_CERTIFICATO;
							tipoCredenzialiSSLVerificaTuttiICampi = credenziali.isCertificateStrictVerification() ? Costanti.CHECK_BOX_ENABLED :Costanti.CHECK_BOX_DISABLED;
							
							try {
								Certificate cSelezionato = ArchiveLoader.load(tipoCredenzialiSSLFileCertificato.getValue());
								tipoCredenzialiSSLAliasCertificatoIssuer = cSelezionato.getCertificate().getIssuer().getNameNormalized();
								tipoCredenzialiSSLAliasCertificatoSubject = cSelezionato.getCertificate().getSubject().getNameNormalized();
								tipoCredenzialiSSLAliasCertificatoSelfSigned = cSelezionato.getCertificate().isSelfSigned() ? CostantiControlStation.LABEL_SI : CostantiControlStation.LABEL_NO;
								tipoCredenzialiSSLAliasCertificatoSerialNumber = cSelezionato.getCertificate().getSerialNumber() + "";
								tipoCredenzialiSSLAliasCertificatoType = cSelezionato.getCertificate().getType();
								tipoCredenzialiSSLAliasCertificatoVersion = cSelezionato.getCertificate().getVersion() + "";
								tipoCredenzialiSSLAliasCertificatoNotBefore = saHelper.getSdfCredenziali().format(cSelezionato.getCertificate().getNotBefore());
								tipoCredenzialiSSLAliasCertificatoNotAfter = saHelper.getSdfCredenziali().format(cSelezionato.getCertificate().getNotAfter());
							}catch(UtilsException e) {
								pd.setMessage("Il Certificato selezionato non &egrave; valido: "+e.getMessage());
								tipoCredenzialiSSLAliasCertificato = "";
								tipoCredenzialiSSLAliasCertificatoSubject= "";
								tipoCredenzialiSSLAliasCertificatoIssuer= "";
								tipoCredenzialiSSLAliasCertificatoType= "";
								tipoCredenzialiSSLAliasCertificatoVersion= "";
								tipoCredenzialiSSLAliasCertificatoSerialNumber= "";
								tipoCredenzialiSSLAliasCertificatoSelfSigned= "";
								tipoCredenzialiSSLAliasCertificatoNotBefore= "";
								tipoCredenzialiSSLAliasCertificatoNotAfter = "";
							}
							
						} else {
							subjectSA = credenziali.getSubject();
							issuerSA = credenziali.getIssuer();
							tipoCredenzialiSSLSorgente = ConnettoriCostanti.VALUE_PARAMETRO_CREDENZIALI_AUTENTICAZIONE_CONFIGURAZIONE_SSL_CONFIGURAZIONE_MANUALE;
							tipoCredenzialiSSLConfigurazioneManualeSelfSigned = ( subjectSA != null && subjectSA.equals(issuerSA)) ? Costanti.CHECK_BOX_ENABLED :Costanti.CHECK_BOX_DISABLED;
						}
						
					}
					
					String changepwd = null;

					List<ExtendedConnettore> listExtendedConnettore = 
							ServletExtendedConnettoreUtils.getExtendedConnettore(connis, ConnettoreServletType.SERVIZIO_APPLICATIVO_CHANGE, saHelper, 
									(endpointtype==null), endpointtype); // uso endpointtype per capire se è la prima volta che entro
					
					dati.add(ServletUtils.getDataElementForEditModeFinished());

					consoleDynamicConfiguration.updateDynamicConfigServizioApplicativo(consoleConfiguration, consoleOperationType, saHelper, protocolProperties, 
							registryReader, configRegistryReader, idServizioApplicativoTmp); 
					
					dati = saHelper.addServizioApplicativoToDati(dati, oldNome, nomeParameter, tipoENomeSoggetto, fault, 
							TipoOperazione.CHANGE, idServizioApplicativo, contaListe,null,null,provider,dominio,
							utenteSA,passwordSA,subjectSA,principalSA,tipoauthSA,faultactor,genericfault,prefixfault,invrifRisposta,
							sbustamentoInformazioniProtocolloRisposta,
							ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_CHANGE,id,tipoProtocollo,
							ruoloFruitore,ruoloErogatore,
							sbustamento, sbustamentoInformazioniProtocolloRichiesta, getmsg,
							invrifRichiesta, risprif,
							endpointtype, autenticazioneHttp, url, nomeCodaJMS, tipoCodaJMS,
							user, password, initcont, urlpgk,
							provurl, connfact, sendas, 
							httpsurl, httpstipologia, httpshostverify,
							httpsTrustVerifyCert, httpspath, httpstipo, httpspwd,
							httpsalgoritmo, httpsstato, httpskeystore,
							httpspwdprivatekeytrust, httpspathkey,
							httpstipokey, httpspwdkey,
							httpspwdprivatekey, httpsalgoritmokey,
							httpsKeyAlias, httpsTrustStoreCRLs, httpsTrustStoreOCSPPolicy,
							tipoconn, connettoreDebug,
							isConnettoreCustomUltimaImmagineSalvata, 
							proxyEnabled, proxyHostname, proxyPort, proxyUsername, proxyPassword,
							tempiRispostaEnabled, tempiRispostaConnectionTimeout, tempiRispostaReadTimeout, tempiRispostaTempoMedioRisposta,
							opzioniAvanzate, transferMode, transferModeChunkSize, redirectMode, redirectMaxHop,
							requestOutputFileName, requestOutputFileNamePermissions, requestOutputFileNameHeaders, requestOutputFileNameHeadersPermissions,
							requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
							responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
							tipoProtocollo, null, listExtendedConnettore,tipoCredenzialiSSLSorgente, tipoCredenzialiSSLTipoArchivio, tipoCredenzialiSSLFileCertificato, tipoCredenzialiSSLFileCertificatoPassword, listaAliasEstrattiCertificato, 
							tipoCredenzialiSSLAliasCertificato, tipoCredenzialiSSLAliasCertificatoSubject, tipoCredenzialiSSLAliasCertificatoIssuer,
							tipoCredenzialiSSLAliasCertificatoType, tipoCredenzialiSSLAliasCertificatoVersion, tipoCredenzialiSSLAliasCertificatoSerialNumber, 
							tipoCredenzialiSSLAliasCertificatoSelfSigned, tipoCredenzialiSSLAliasCertificatoNotBefore, tipoCredenzialiSSLAliasCertificatoNotAfter, 
							tipoCredenzialiSSLVerificaTuttiICampi, tipoCredenzialiSSLConfigurazioneManualeSelfSigned, issuerSA,tipoCredenzialiSSLWizardStep,
							changepwd,
							multipleApiKey, appId, apiKey,
							autenticazioneToken,tokenPolicy,tipoSA, useAsClient,
							integrationManagerEnabled, 
							visualizzaModificaCertificato, visualizzaAddCertificato, servletCredenzialiList, parametersServletCredenzialiList, numeroCertificati, servletCredenzialiAdd,
							tokenPolicySA, tokenClientIdSA, tokenWithHttpsEnabledByConfigSA);

					// aggiunta campi custom
					dati = saHelper.addProtocolPropertiesToDatiConfig(dati, consoleConfiguration,consoleOperationType, protocolProperties,oldProtocolPropertyList,propertiesProprietario);
								
					pd.setDati(dati);
					
					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
					return ServletUtils.getStrutsForwardEditModeFinished(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI, ForwardParams.CHANGE());
					
				}
			}
			else {
				
				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				return ServletUtils.getStrutsForwardEditModeFinished(mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_VERIFICA_CERTIFICATI, ForwardParams.OTHER(""));
			}
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, ServiziApplicativiCostanti.OBJECT_NAME_SERVIZI_APPLICATIVI_VERIFICA_CERTIFICATI, ForwardParams.OTHER(""));
		}  
	}
}
