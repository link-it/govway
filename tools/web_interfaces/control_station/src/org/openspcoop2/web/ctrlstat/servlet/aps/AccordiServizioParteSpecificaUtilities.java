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

package org.openspcoop2.web.ctrlstat.servlet.aps;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.InvocazioneCredenziali;
import org.openspcoop2.core.config.InvocazionePorta;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.PortaDelegataAzione;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.InvocazioneServizioTipoAutenticazione;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.PortaDelegataAzioneIdentificazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.id.IdentificativiFruizione;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.ConfigurazioneServizioAzione;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.beans.PortTypeSintetico;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.db.IDAccordoDB;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.config.Implementation;
import org.openspcoop2.protocol.sdk.config.Subscription;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedBean;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.plugins.WrapperExtendedBean;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniHelper;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;
import org.openspcoop2.web.lib.users.dao.PermessiUtente;

/**	
 * AccordiServizioParteSpecificaUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordiServizioParteSpecificaUtilities {

	public static boolean [] getPermessiUtente(AccordiServizioParteSpecificaHelper apsHelper) {
		PermessiUtente pu = null;
		if(apsHelper.getCore().isUsedByApi()) {
			pu = new PermessiUtente();
			pu.setServizi(true);
			pu.setAccordiCooperazione(false);
		}
		else {
			pu = ServletUtils.getUserFromSession(apsHelper.getSession()).getPermessi();
		}
		boolean [] permessi = new boolean[2];
		permessi[0] = pu.isServizi();
		permessi[1] = pu.isAccordiCooperazione();		
		return permessi;
	}
	
	public static List<AccordoServizioParteComuneSintetico> getListaAPI(String tipoProtocollo, String userLogin, AccordiServizioParteSpecificaCore apsCore, AccordiServizioParteSpecificaHelper apsHelper) throws Exception {
		
		AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(apsCore);
		
		Search searchAccordi = new Search(true);
		searchAccordi.addFilter(Liste.ACCORDI, Filtri.FILTRO_PROTOCOLLO, tipoProtocollo);
		List<AccordoServizioParteComuneSintetico> listaTmp =  
				AccordiServizioParteComuneUtilities.accordiListFromPermessiUtente(apcCore, userLogin, searchAccordi, 
						getPermessiUtente(apsHelper));
		
		List<AccordoServizioParteComuneSintetico> listaAPI = null;
		if(apsHelper.isModalitaCompleta()) {
			listaAPI = listaTmp;
		}
		else {
			// filtro accordi senza risorse o senza pt/operation
			listaAPI = new ArrayList<AccordoServizioParteComuneSintetico>();
			for (AccordoServizioParteComuneSintetico accordoServizioParteComune : listaTmp) {
				if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(accordoServizioParteComune.getServiceBinding())) {
					if(accordoServizioParteComune.getResource().size()>0) {
						listaAPI.add(accordoServizioParteComune);	
					}
				}
				else {
					boolean ptValido = false;
					for (PortTypeSintetico pt : accordoServizioParteComune.getPortType()) {
						if(pt.getAzione().size()>0) {
							ptValido = true;
							break;
						}
					}
					if(ptValido) {
						listaAPI.add(accordoServizioParteComune);	
					}
				}
			}
		}
		return listaAPI;
	}
	
	public static List<IDAccordoDB> getListaIdAPI(String tipoProtocollo, String userLogin, AccordiServizioParteSpecificaCore apsCore, AccordiServizioParteSpecificaHelper apsHelper) throws Exception {
		
		AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(apsCore);
		
		boolean soloAccordiConsistentiRest = false;
		boolean soloAccordiConsistentiSoap = false;
		if(!apsHelper.isModalitaCompleta()) {
			// filtro accordi senza risorse o senza pt/operation
			soloAccordiConsistentiRest = true;
			soloAccordiConsistentiSoap = true;
		}
		
		Search searchAccordi = new Search(true);
		searchAccordi.addFilter(Liste.ACCORDI, Filtri.FILTRO_PROTOCOLLO, tipoProtocollo);
		List<IDAccordoDB> listaTmp =  
				AccordiServizioParteComuneUtilities.idAccordiListFromPermessiUtente(apcCore, userLogin, searchAccordi, 
						getPermessiUtente(apsHelper), soloAccordiConsistentiRest, soloAccordiConsistentiSoap);
		
		return listaTmp;
	}
	
	public static List<PortTypeSintetico> getListaPortTypes(AccordoServizioParteComuneSintetico as, AccordiServizioParteSpecificaCore apsCore, AccordiServizioParteSpecificaHelper apsHelper) throws Exception{
		
		//AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(apsCore);
		
		//List<PortType> portTypesTmp = apcCore.accordiPorttypeList(as.getId().intValue(), new Search(true));
		List<PortTypeSintetico> portTypesTmp = as.getPortType();
		List<PortTypeSintetico> portTypes = null;
		
		if(apsHelper.isModalitaCompleta()) {
			portTypes = portTypesTmp;
		}
		else {
			// filtro pt senza op
			portTypes = new ArrayList<PortTypeSintetico>();
			for (PortTypeSintetico portType : portTypesTmp) {
				if(portType.getAzione().size()>0) {
					portTypes.add(portType);
				}
			}
		}
		
		return portTypes; 
	}
	
	public static boolean isSoggettoOperativo(IDSoggetto idSoggettoErogatore, AccordiServizioParteSpecificaCore apsCore) throws Exception {
		SoggettiCore soggettiCore = new SoggettiCore(apsCore);
		PddCore pddCore = new PddCore(apsCore);
		Soggetto soggetto = soggettiCore.getSoggettoRegistro(idSoggettoErogatore );
		if(pddCore.isPddEsterna(soggetto.getPortaDominio())){
			return false;
		}
		return true;
	}
	
	public static void create(AccordoServizioParteSpecifica asps,boolean alreadyExists,
			IDServizio idServizio, IDSoggetto idFruitore, String tipoProtocollo, ServiceBinding serviceBinding,
			long idProv, // id del database relativo al soggetto. 
			Connettore connettore,		
			boolean generaPortaApplicativa, boolean generaPortaDelegata,
			String autenticazione, String autenticazioneOpzionale, TipoAutenticazionePrincipal autenticazionePrincipal, List<String> autenticazioneParametroList,
			String autorizzazione, String autorizzazioneAutenticati, String autorizzazioneRuoli, String autorizzazioneRuoliTipologia, String autorizzazioneRuoliMatch,
			String servizioApplicativo, String ruolo, String soggettoAutenticato, 
			String autorizzazione_tokenOptions,
			String autorizzazioneScope, String scope, String autorizzazioneScopeMatch,BinaryParameter allegatoXacmlPolicy,
			String gestioneToken, 
			String gestioneTokenPolicy,  String gestioneTokenOpzionale,  
			String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autenticazioneTokenIssuer, String autenticazioneTokenClientId, String autenticazioneTokenSubject, String autenticazioneTokenUsername, String autenticazioneTokenEMail,
			ProtocolProperties protocolProperties, ConsoleOperationType consoleOperationType,
			AccordiServizioParteSpecificaCore apsCore, ErogazioniHelper apsHelper, String nomeSAServer, String canaleStato, String canale, boolean gestioneCanaliEnabled) throws Exception {
		
		List<Object> listaOggettiDaCreare = new ArrayList<Object>();
		if(!alreadyExists) {
			listaOggettiDaCreare.add(asps);
		}

		// Creo Porta Applicativa (opzione??)
		if(generaPortaApplicativa){
			
			AccordiServizioParteSpecificaUtilities.generaPortaApplicativa(listaOggettiDaCreare, idServizio,tipoProtocollo, serviceBinding, 
					idProv, connettore, 
					autenticazione, autenticazioneOpzionale, autenticazionePrincipal, autenticazioneParametroList,
					autorizzazione, autorizzazioneAutenticati, autorizzazioneRuoli, autorizzazioneRuoliTipologia, autorizzazioneRuoliMatch,
					servizioApplicativo, ruolo,soggettoAutenticato,
					autorizzazione_tokenOptions,
					autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy,
					gestioneToken,
					gestioneTokenPolicy,  gestioneTokenOpzionale,
					gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward,
					autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
					apsCore, apsHelper, nomeSAServer, canaleStato, canale, gestioneCanaliEnabled);
					
		}
		
		if(generaPortaDelegata){
			
			AccordiServizioParteSpecificaUtilities.generaPortaDelegata(listaOggettiDaCreare, 
					idFruitore, idServizio, tipoProtocollo, serviceBinding, 
					idProv, 
					autenticazione, autenticazioneOpzionale, autenticazionePrincipal, autenticazioneParametroList,
					autorizzazione, autorizzazioneAutenticati, autorizzazioneRuoli, autorizzazioneRuoliTipologia, autorizzazioneRuoliMatch,
					servizioApplicativo, ruolo,
					autorizzazione_tokenOptions,
					autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy,
					gestioneToken, 
					gestioneTokenPolicy,  gestioneTokenOpzionale,
					gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward,
					autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
					apsCore, canaleStato, canale, gestioneCanaliEnabled);
			
		}

		//imposto properties custom
		if(generaPortaDelegata){
			for (Fruitore fruitore : asps.getFruitoreList()) {
				if(fruitore.getTipo().equals(idFruitore.getTipo()) && fruitore.getNome().equals(idFruitore.getNome())) {
					fruitore.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(protocolProperties, consoleOperationType,null));
				}
			}
		}
		else {
			if(!alreadyExists) {
				asps.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolPropertiesRegistry(protocolProperties, consoleOperationType,null));
			}
		}

		if(alreadyExists) {
			apsCore.performUpdateOperation(asps.getSuperUser(), apsHelper.smista(), asps); // aggiorno aps
		}
		apsCore.performCreateOperation(asps.getSuperUser(), apsHelper.smista(), listaOggettiDaCreare.toArray());
	}
	
	public static void generaPortaApplicativa(List<Object> listaOggettiDaCreare,
			IDServizio idServizio, String tipoProtocollo, ServiceBinding serviceBinding,
			long idProv, // id del database relativo al soggetto. 
			Connettore connettore,			
			String erogazioneAutenticazione, String erogazioneAutenticazioneOpzionale, TipoAutenticazionePrincipal erogazioneAutenticazionePrincipal, List<String> erogazioneAutenticazioneParametroList,
			String erogazioneAutorizzazione, String erogazioneAutorizzazioneAutenticati, String erogazioneAutorizzazioneRuoli, String erogazioneAutorizzazioneRuoliTipologia, String erogazioneAutorizzazioneRuoliMatch,
			String nomeSA, String erogazioneRuolo, String erogazioneSoggettoAutenticato, 
			String autorizzazione_tokenOptions,
			String autorizzazioneScope, String scope, String autorizzazioneScopeMatch,BinaryParameter allegatoXacmlPolicy,
			String gestioneToken, 
			String gestioneTokenPolicy,  String gestioneTokenOpzionale,  
			String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autenticazioneTokenIssuer, String autenticazioneTokenClientId, String autenticazioneTokenSubject, String autenticazioneTokenUsername, String autenticazioneTokenEMail,
			AccordiServizioParteSpecificaCore apsCore, ErogazioniHelper apsHelper, String nomeSAServer, String canaleStato, String canale, boolean gestioneCanaliEnabled) throws Exception {
		
		PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apsCore);
			
		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tipoProtocollo);
		

		Implementation implementationDefault = protocolFactory.createProtocolIntegrationConfiguration().
				createDefaultImplementation(serviceBinding, idServizio);
		
		PortaApplicativa portaApplicativa = implementationDefault.getPortaApplicativa();
		MappingErogazionePortaApplicativa mappingErogazione = implementationDefault.getMapping();
		portaApplicativa.setIdSoggetto((long) idProv);
		
		IDSoggetto idSoggettoAutenticatoErogazione = null;
		if(erogazioneSoggettoAutenticato != null && !"".equals(erogazioneSoggettoAutenticato) && !"-".equals(erogazioneSoggettoAutenticato)) {
			String [] splitSoggetto = erogazioneSoggettoAutenticato.split("/");
			if(splitSoggetto != null) {
				idSoggettoAutenticatoErogazione = new IDSoggetto();
				if(splitSoggetto.length == 2) {
					idSoggettoAutenticatoErogazione.setTipo(splitSoggetto[0]);
					idSoggettoAutenticatoErogazione.setNome(splitSoggetto[1]);
				} else {
					idSoggettoAutenticatoErogazione.setNome(splitSoggetto[0]);
				}
			}
		}
		
		String nomeServizioApplicativoErogatore = nomeSA;
		ServizioApplicativo sa = null;
		
		if(apsHelper.isModalitaCompleta()==false) {
			// Creo il servizio applicativo
			
			nomeServizioApplicativoErogatore = portaApplicativa.getNome();
			
			sa = new ServizioApplicativo();
			sa.setNome(nomeServizioApplicativoErogatore);
			sa.setTipologiaFruizione(TipologiaFruizione.DISABILITATO.getValue());
			sa.setTipologiaErogazione(TipologiaErogazione.TRASPARENTE.getValue());
			sa.setIdSoggetto((long) idProv);
			sa.setTipoSoggettoProprietario(portaApplicativa.getTipoSoggettoProprietario());
			sa.setNomeSoggettoProprietario(portaApplicativa.getNomeSoggettoProprietario());
			
			RispostaAsincrona rispostaAsinc = new RispostaAsincrona();
			rispostaAsinc.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
			rispostaAsinc.setGetMessage(CostantiConfigurazione.DISABILITATO);
			sa.setRispostaAsincrona(rispostaAsinc);
			
			InvocazioneServizio invServizio = new InvocazioneServizio();
			String user = null;
			String password = null;
			if(connettore.sizePropertyList()>0) {
				for (int i = 0; i < connettore.sizePropertyList(); i++) {
					if(CostantiDB.CONNETTORE_USER.equals(connettore.getProperty(i).getNome())) {
						user = connettore.getProperty(i).getValore();
					}
					else if(CostantiDB.CONNETTORE_PWD.equals(connettore.getProperty(i).getNome())) {
						password = connettore.getProperty(i).getValore();
					}
				}
			}
			if(user!=null) {
				invServizio.setAutenticazione(InvocazioneServizioTipoAutenticazione.BASIC);
				InvocazioneCredenziali credenziali = new InvocazioneCredenziali();
				credenziali.setUser(user);
				credenziali.setPassword(password);
				invServizio.setCredenziali(credenziali);
			}
			else {
				invServizio.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
			}
			invServizio.setGetMessage(CostantiConfigurazione.DISABILITATO);
			invServizio.setConnettore(connettore.mappingIntoConnettoreConfigurazione());
			sa.setInvocazioneServizio(invServizio);
			
			listaOggettiDaCreare.add(sa);
			
		}
		
		// Scelto un servizio applicativo server, creo il servizio di default e poi associo quello server
		if(StringUtils.isNotEmpty(nomeSAServer)) {
			portaApplicativa.setServizioApplicativoDefault(nomeServizioApplicativoErogatore);
			nomeServizioApplicativoErogatore = nomeSAServer;
		}
			
		porteApplicativeCore.configureControlloAccessiPortaApplicativa(portaApplicativa,
				erogazioneAutenticazione, erogazioneAutenticazioneOpzionale, erogazioneAutenticazionePrincipal, erogazioneAutenticazioneParametroList,
				erogazioneAutorizzazione, erogazioneAutorizzazioneAutenticati, erogazioneAutorizzazioneRuoli, erogazioneAutorizzazioneRuoliTipologia, erogazioneAutorizzazioneRuoliMatch,
				nomeServizioApplicativoErogatore, erogazioneRuolo,idSoggettoAutenticatoErogazione,
				autorizzazione_tokenOptions,
				autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy);
		
		porteApplicativeCore.configureControlloAccessiGestioneToken(portaApplicativa, gestioneToken, 
				gestioneTokenPolicy,  gestioneTokenOpzionale,
				gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward,
				autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
				autorizzazione_tokenOptions
				);
		
		// canali
		if(gestioneCanaliEnabled) {
			if(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CANALE_STATO_RIDEFINITO.equals(canaleStato)) {
				portaApplicativa.setCanale(canale);
			} else {
				portaApplicativa.setCanale(null);
			}
		} else {
			portaApplicativa.setCanale(null);
		}
		
		listaOggettiDaCreare.add(portaApplicativa);						
		listaOggettiDaCreare.add(mappingErogazione);
	
	}
	
	public static void crea() {
		
	}
	
	public static void generaPortaDelegata(List<Object> listaOggettiDaCreare,
			IDSoggetto idFruitore, IDServizio idServizio, String tipoProtocollo, ServiceBinding serviceBinding,
			long idProv, // id del database relativo al soggetto. 
			String fruizioneAutenticazione, String fruizioneAutenticazioneOpzionale, TipoAutenticazionePrincipal fruizioneAutenticazionePrincipal, List<String> fruizioneAutenticazioneParametroList,
			String fruizioneAutorizzazione, String fruizioneAutorizzazioneAutenticati, String fruizioneAutorizzazioneRuoli, String fruizioneAutorizzazioneRuoliTipologia, String fruizioneAutorizzazioneRuoliMatch,
			String fruizioneServizioApplicativo, String fruizioneRuolo, 
			String autorizzazione_tokenOptions,
			String autorizzazioneScope, String scope, String autorizzazioneScopeMatch,BinaryParameter allegatoXacmlPolicy,
			String gestioneToken, 
			String gestioneTokenPolicy,  String gestioneTokenOpzionale,  
			String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autenticazioneTokenIssuer, String autenticazioneTokenClientId, String autenticazioneTokenSubject, String autenticazioneTokenUsername, String autenticazioneTokenEMail,
			AccordiServizioParteSpecificaCore apsCore, String canaleStato, String canale, boolean gestioneCanaliEnabled) throws Exception {
		
		PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
		
		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tipoProtocollo);
		
		
		Subscription subscriptionDefault = protocolFactory.createProtocolIntegrationConfiguration().
				createDefaultSubscription(serviceBinding, idFruitore, idServizio);
		
		PortaDelegata portaDelegata = subscriptionDefault.getPortaDelegata();
		MappingFruizionePortaDelegata mappingFruizione = subscriptionDefault.getMapping();
		portaDelegata.setIdSoggetto((long) idProv);

		porteDelegateCore.configureControlloAccessiPortaDelegata(portaDelegata, 
				fruizioneAutenticazione, fruizioneAutenticazioneOpzionale, fruizioneAutenticazionePrincipal, fruizioneAutenticazioneParametroList,
				fruizioneAutorizzazione, fruizioneAutorizzazioneAutenticati, fruizioneAutorizzazioneRuoli, fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch,
				fruizioneServizioApplicativo, fruizioneRuolo,
				autorizzazione_tokenOptions,
				autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy);
		
		porteDelegateCore.configureControlloAccessiGestioneToken(portaDelegata, gestioneToken, 
				gestioneTokenPolicy,  gestioneTokenOpzionale,
				gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward,
				autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
				autorizzazione_tokenOptions
				);
		
		// canali
		if(gestioneCanaliEnabled) {
			if(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CANALE_STATO_RIDEFINITO.equals(canaleStato)) {
				portaDelegata.setCanale(canale);
			} else {
				portaDelegata.setCanale(null);
			}
		} else {
			portaDelegata.setCanale(null);
		}
					
		// Verifico prima che la porta delegata non esista già
		if (!porteDelegateCore.existsPortaDelegata(mappingFruizione.getIdPortaDelegata())){
			listaOggettiDaCreare.add(portaDelegata);
		}
		listaOggettiDaCreare.add(mappingFruizione);	
	}
	
	public static List<Object> getOggettiDaAggiornare(AccordoServizioParteSpecifica asps, AccordiServizioParteSpecificaCore apsCore) throws Exception {

		PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
		PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apsCore);
		ServiziApplicativiCore saCore = new ServiziApplicativiCore(apsCore);
		AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(apsCore);
		ConfigurazioneCore confCore = new ConfigurazioneCore(apcCore);
		
		// Se sono cambiati il tipo o il nome allora devo aggiornare
		// anche le porte delegate e porte applicative
		List<PortaDelegata> listaPD = new ArrayList<PortaDelegata>();
		List<PortaApplicativa> listaPA = new ArrayList<PortaApplicativa>();
		List<ServizioApplicativo> listaPA_SA = new ArrayList<ServizioApplicativo>();
		Hashtable<String, AttivazionePolicy> listaPolicyPA = new Hashtable<String, AttivazionePolicy>();
		
		// check dati modificati
		String newUri = IDServizioFactory.getInstance().getUriFromAccordo(asps);
		String oldUri = IDServizioFactory.getInstance().getUriFromIDServizio(asps.getOldIDServizioForUpdate());
		if (!newUri.equals(oldUri)) {


			// check PD
			FiltroRicercaPorteDelegate filtroPD = new FiltroRicercaPorteDelegate();
			filtroPD.setTipoSoggettoErogatore(asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo());
			filtroPD.setNomeSoggettoErogatore(asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome());
			filtroPD.setTipoServizio(asps.getOldIDServizioForUpdate().getTipo());
			filtroPD.setNomeServizio(asps.getOldIDServizioForUpdate().getNome());
			filtroPD.setVersioneServizio(asps.getOldIDServizioForUpdate().getVersione());
			List<IDPortaDelegata> listIdsPorteDelegate = porteDelegateCore.getAllIdPorteDelegate(filtroPD);
			if(listIdsPorteDelegate!=null && !listIdsPorteDelegate.isEmpty()) {

				String tmpLocationSuffix = "/" + asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo() + "_" + asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome() + 
						"/" + asps.getOldIDServizioForUpdate().getTipo() + "_" + asps.getOldIDServizioForUpdate().getNome();

				String locationSuffix = tmpLocationSuffix +
						"/v" + asps.getOldIDServizioForUpdate().getVersione().intValue();

				// backward compatibility: provare ad eliminare la v, che prima non veniva utilizzata
				String locationSuffix_oldWithoutV = tmpLocationSuffix +
						"/" + asps.getOldIDServizioForUpdate().getVersione().intValue();

				String newLocationSuffix = "/" + asps.getTipoSoggettoErogatore() + "_" + asps.getNomeSoggettoErogatore() + 
						"/" + asps.getTipo() + "_" + asps.getNome() +
						"/v" + asps.getVersione().intValue();

				for (IDPortaDelegata idPortaDelegata : listIdsPorteDelegate) {
					PortaDelegata tmpPorta = porteDelegateCore.getPortaDelegata(idPortaDelegata);	

					// aggiorno dati erogatore
					tmpPorta.getSoggettoErogatore().setTipo(asps.getTipoSoggettoErogatore());
					tmpPorta.getSoggettoErogatore().setNome(asps.getNomeSoggettoErogatore());
					
					// aggiorno dati servizio
					tmpPorta.getServizio().setTipo(asps.getTipo());
					tmpPorta.getServizio().setNome(asps.getNome());
					tmpPorta.getServizio().setVersione(asps.getVersione());

					String locationPrefix = tmpPorta.getTipoSoggettoProprietario()+"_"+tmpPorta.getNomeSoggettoProprietario();
					String check1 = locationPrefix+locationSuffix;
					String check2 = "__"+locationPrefix+locationSuffix;
					String check1_oldWithoutV = locationPrefix+locationSuffix_oldWithoutV;
					String check2_oldWithoutV = "__"+locationPrefix+locationSuffix_oldWithoutV;
					String parteRimanente = "";
					String nuovoNome = null;
					boolean match = false;
					if(tmpPorta.getNome().equals(check1)) {
						match = true;	
						nuovoNome = locationPrefix+newLocationSuffix;
					}
					else if(tmpPorta.getNome().startsWith(check2)) {
						match = true;	
						parteRimanente = tmpPorta.getNome().substring(check2.length());
						nuovoNome = "__"+locationPrefix+newLocationSuffix+parteRimanente;
					}
					else if(tmpPorta.getNome().equals(check1_oldWithoutV)) {
						match = true;	
						nuovoNome = locationPrefix+newLocationSuffix;
					}
					else if(tmpPorta.getNome().startsWith(check2_oldWithoutV)) {
						match = true;	
						parteRimanente = tmpPorta.getNome().substring(check2_oldWithoutV.length());
						nuovoNome = "__"+locationPrefix+newLocationSuffix+parteRimanente;
					}

					if(match) {
						IDPortaDelegata oldIDPortaDelegataForUpdate = new IDPortaDelegata();
						oldIDPortaDelegataForUpdate.setNome(tmpPorta.getNome());
						tmpPorta.setOldIDPortaDelegataForUpdate(oldIDPortaDelegataForUpdate);
						tmpPorta.setNome(nuovoNome);

						// modifica della descrizione
						String descrizionePD = tmpPorta.getDescrizione();
						if (descrizionePD != null && !descrizionePD.equals("")) {

							// Caso 1: subscription default
							// Subscription from gw/ENTE for service gw/ErogatoreEsterno:gw/EsempioREST:1
							String soggettoOldCaso1 = asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo()+"/"+asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome();
							String soggettoNewCaso1 = asps.getTipoSoggettoErogatore()+"/"+asps.getNomeSoggettoErogatore();
							String match_caso = soggettoOldCaso1+":"+asps.getOldIDServizioForUpdate().getTipo()+"/"+asps.getOldIDServizioForUpdate().getNome()+":"+asps.getOldIDServizioForUpdate().getVersione().intValue();
							if(descrizionePD.endsWith(match_caso)) {
								String replace_caso = soggettoNewCaso1+":"+asps.getTipo()+"/"+asps.getNome()+":"+asps.getVersione().intValue();
								descrizionePD = descrizionePD.replace(match_caso, replace_caso);
							}

							// Caso 2: altra subscription
							// Internal Subscription 'Specific1' for gw_ENTE/gw_ErogatoreEsterno/gw_EsempioREST/1
							String soggettoOldCaso2 = asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo()+"_"+asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome();
							String soggettoNewCaso2 = asps.getTipoSoggettoErogatore()+"_"+asps.getNomeSoggettoErogatore();
							String tmpMatchCaso2 = soggettoOldCaso2+"/"+asps.getOldIDServizioForUpdate().getTipo()+"_"+asps.getOldIDServizioForUpdate().getNome()+"/";
							String match_caso2 = tmpMatchCaso2 +"v"+ asps.getOldIDServizioForUpdate().getVersione().intValue();
							String match_caso2_oldWithoutV = tmpMatchCaso2 + asps.getOldIDServizioForUpdate().getVersione().intValue();
							if(descrizionePD.contains(match_caso2)) {
								String replace_caso2 = soggettoNewCaso2+"/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue();
								descrizionePD = descrizionePD.replace(match_caso2, replace_caso2);
							}
							else if(descrizionePD.contains(match_caso2_oldWithoutV)) {
								String replace_caso2 = soggettoNewCaso2+"/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue();
								descrizionePD = descrizionePD.replace(match_caso2_oldWithoutV, replace_caso2);
							}

							tmpPorta.setDescrizione(descrizionePD);

						}

						// regex del pattern azione
						// .*(fruitore)/(erogatore)/(servizio)/([^/|^?]*).*
						PortaDelegataAzione pdAzione = tmpPorta.getAzione();
						PortaDelegataAzioneIdentificazione identificazione = pdAzione != null ? pdAzione.getIdentificazione() : null;
						String patterAzione = pdAzione != null ? (pdAzione.getPattern() != null ? pdAzione.getPattern() : "") : "";
						String patternAzionePrefix = ".*/";
						String patternAzioneSuffix1 = "/([^/|^?]*).*";
						// se identificazione urlbased procedo con i controlli
						if (PortaDelegataAzioneIdentificazione.URL_BASED.equals(identificazione)) {
							if (patterAzione.startsWith(patternAzionePrefix) && patterAzione.endsWith(patternAzioneSuffix1)) {
								// caso1
								int startidx = patternAzionePrefix.length();
								int endidx = patterAzione.lastIndexOf(patternAzioneSuffix1);
								String tmpPat = patterAzione.substring(startidx, endidx);
								// a questo punto ottengo una stringa del tipo
								// (fruitore)/(erogatore)/(servizio)/(versioneServizio)
								// se rispetta la regex allora vuol dire che il
								// pattern azione e' quello di default
								// e devo effettuare i cambiamenti
								String regex = "(.*)\\/(.*)\\/(.*)\\/(.*)";
								if (tmpPat.matches(regex)) {
									String[] val = tmpPat.split("/");
									String partFruitore = val[0];
									String partErogatore = val[1];
									String partServizio = val[2];
									String partVersione = val[3];
									String rimanenteRegExp = "";
									int lengthParteRimanenteRegExp = (partFruitore+"/"+partErogatore+"/"+partServizio+"/"+partVersione).length();
									if(tmpPat.length()>lengthParteRimanenteRegExp){
										rimanenteRegExp = tmpPat.substring(lengthParteRimanenteRegExp);
									}	

									boolean matchURL = false;
									
									// vedo se matcha l'erogatore
									String partErogatoreOld = "(?:"+asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo()+"_)?"+asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome()+"";
									String partErogatoreNew = "(?:"+asps.getTipoSoggettoErogatore()+"_)?"+asps.getNomeSoggettoErogatore()+"";
									if (partErogatore.equals(partErogatoreOld)) {
										partErogatore = partErogatoreNew;
										matchURL = true;
									}
									
									// vedo se matcha il servizio
									String partServizioOld = "(?:"+asps.getOldIDServizioForUpdate().getTipo()+"_)?"+asps.getOldIDServizioForUpdate().getNome()+"";
									String partServizioNew = "(?:"+asps.getTipo()+"_)?"+asps.getNome()+"";
									if (partServizio.equals(partServizioOld)) {
										partServizio = partServizioNew;
										matchURL = true;
									}

									// vedo se matcha verione
									String versioneOld = "v"+(asps.getOldIDServizioForUpdate().getVersione().intValue()+"");
									String versioneOld_oldWithoutV = (asps.getOldIDServizioForUpdate().getVersione().intValue()+"");
									if (partVersione.equals(versioneOld) || partVersione.equals(versioneOld_oldWithoutV)) {
										partVersione = "v"+asps.getVersione().intValue()+"";
										matchURL = true;
									}

									if(matchURL){
										String newPatternAzione = patternAzionePrefix + partFruitore + "/" + partErogatore+ "/" + partServizio+ "/" + partVersione + rimanenteRegExp + patternAzioneSuffix1;
										pdAzione.setPattern(newPatternAzione);
										tmpPorta.setAzione(pdAzione);
									}

								}
							}
						}// fine controllo azione

						// DelegatedBy
						String nomePortaDelegante = pdAzione != null ? (pdAzione.getNomePortaDelegante() != null ? pdAzione.getNomePortaDelegante() : null) : null;
						if (PortaDelegataAzioneIdentificazione.DELEGATED_BY.equals(identificazione) && nomePortaDelegante!=null ) {
							String nuovoNomeDelegate = null;
							boolean matchDelegate = false;
							if(nomePortaDelegante.equals(check1)) {
								matchDelegate = true;	
								nuovoNomeDelegate = locationPrefix+newLocationSuffix;
							}
							else if(nomePortaDelegante.equals(check1_oldWithoutV)) {
								matchDelegate = true;	
								nuovoNomeDelegate = locationPrefix+newLocationSuffix;
							}
							if(matchDelegate) {
								tmpPorta.getAzione().setNomePortaDelegante(nuovoNomeDelegate);
							}
						}// fine controllo DelegatedBy
					
						// Controllo policy di Rate Limiting
						if(tmpPorta.getOldIDPortaDelegataForUpdate()!=null && tmpPorta.getOldIDPortaDelegataForUpdate().getNome()!=null) {
							Search ricercaPolicies = new Search(true);
							List<AttivazionePolicy> listaPolicies = null;
							try {
								listaPolicies = confCore.attivazionePolicyList(ricercaPolicies, RuoloPolicy.DELEGATA, tmpPorta.getOldIDPortaDelegataForUpdate().getNome());
							}catch(Exception e) {}
							if(listaPolicies!=null && !listaPolicies.isEmpty()) {
								for (AttivazionePolicy ap : listaPolicies) {
									if(ap.getFiltro()!=null && tmpPorta.getOldIDPortaDelegataForUpdate().getNome().equals(ap.getFiltro().getNomePorta())) {
										
										// aggiorno nome porta
										ap.getFiltro().setNomePorta(tmpPorta.getNome());
																		
										listaPolicyPA.put(ap.getIdActivePolicy(), ap);
									}
								}
							}
						}
						// fine Controllo policy di Rate Limiting
					}
					
					listaPD.add(tmpPorta); // la porta la aggiungo cmq per modificare i dati
				}
			}




			// check PA
			FiltroRicercaPorteApplicative filtroPA = new FiltroRicercaPorteApplicative();
			filtroPA.setTipoSoggetto(asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo());
			filtroPA.setNomeSoggetto(asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome());
			filtroPA.setTipoServizio(asps.getOldIDServizioForUpdate().getTipo());
			filtroPA.setNomeServizio(asps.getOldIDServizioForUpdate().getNome());
			filtroPA.setVersioneServizio(asps.getOldIDServizioForUpdate().getVersione());
			List<IDPortaApplicativa> listIdsPorteApplicative = porteApplicativeCore.getAllIdPorteApplicative(filtroPA);
			if(listIdsPorteApplicative!=null && !listIdsPorteApplicative.isEmpty()) {

				String tmpLocationSuffix = asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo() + "_" + asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome() + 
						"/" + asps.getOldIDServizioForUpdate().getTipo() + "_" + asps.getOldIDServizioForUpdate().getNome();

				String locationSuffix = tmpLocationSuffix+
						"/v" + asps.getOldIDServizioForUpdate().getVersione().intValue();

				// backward compatibility: provare ad eliminare la v, che prima non veniva utilizzata

				String locationSuffix_oldWithoutV = tmpLocationSuffix +
						"/" + asps.getOldIDServizioForUpdate().getVersione().intValue();

				String newLocationSuffix = asps.getTipoSoggettoErogatore() + "_" + asps.getNomeSoggettoErogatore() + 
						"/" + asps.getTipo() + "_" + asps.getNome() +
						"/v" + asps.getVersione().intValue();

				for (IDPortaApplicativa idPortaApplicativa : listIdsPorteApplicative) {
					PortaApplicativa tmpPorta = porteApplicativeCore.getPortaApplicativa(idPortaApplicativa);	

					// aggiorno dati erogatore
					tmpPorta.setTipoSoggettoProprietario(asps.getTipoSoggettoErogatore());
					tmpPorta.setNomeSoggettoProprietario(asps.getNomeSoggettoErogatore());
					
					// aggiorno dati servizio
					tmpPorta.getServizio().setTipo(asps.getTipo());
					tmpPorta.getServizio().setNome(asps.getNome());
					tmpPorta.getServizio().setVersione(asps.getVersione());

					String check1 = locationSuffix;
					String check2 = "__"+locationSuffix;
					String check1_oldWithoutV = locationSuffix_oldWithoutV;
					String check2_oldWithoutV = "__"+locationSuffix_oldWithoutV;
					String parteRimanente = "";
					String nuovoNome = null;
					boolean match = false;
					if(tmpPorta.getNome().equals(check1)) {
						match = true;	
						nuovoNome = newLocationSuffix;
					}
					else if(tmpPorta.getNome().startsWith(check2)) {
						match = true;	
						parteRimanente = tmpPorta.getNome().substring(check2.length());
						nuovoNome = "__"+newLocationSuffix+parteRimanente;
					}
					else if(tmpPorta.getNome().equals(check1_oldWithoutV)) {
						match = true;	
						nuovoNome = newLocationSuffix;
					}
					else if(tmpPorta.getNome().startsWith(check2_oldWithoutV)) {
						match = true;	
						parteRimanente = tmpPorta.getNome().substring(check2_oldWithoutV.length());
						nuovoNome = "__"+newLocationSuffix+parteRimanente;
					}

					IDPortaApplicativa oldIDPortaApplicativaForUpdate = null;
					if(match) {
						oldIDPortaApplicativaForUpdate = new IDPortaApplicativa();
						oldIDPortaApplicativaForUpdate.setNome(tmpPorta.getNome());
						tmpPorta.setOldIDPortaApplicativaForUpdate(oldIDPortaApplicativaForUpdate);
						tmpPorta.setNome(nuovoNome);

						// modifica della descrizione
						String descrizionePA = tmpPorta.getDescrizione();
						if (descrizionePA != null && !descrizionePA.equals("")) {

							// Caso 1: implementation default
							// Service implementation gw/ENTE:gw/TEST:1
							String soggettoOldCaso1 = asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo()+"/"+asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome();
							String soggettoNewCaso1 = asps.getTipoSoggettoErogatore()+"/"+asps.getNomeSoggettoErogatore();
							String match_caso = soggettoOldCaso1+":"+asps.getOldIDServizioForUpdate().getTipo()+"/"+asps.getOldIDServizioForUpdate().getNome()+":"+asps.getOldIDServizioForUpdate().getVersione().intValue();
							if(descrizionePA.endsWith(match_caso)) {
								String replace_caso = soggettoNewCaso1+":"+asps.getTipo()+"/"+asps.getNome()+":"+asps.getVersione().intValue();
								descrizionePA = descrizionePA.replace(match_caso, replace_caso);
							}

							// Caso 2: altra i
							// Internal Implementation 'Specific1' for gw_ENTE/gw_TEST/1
							String soggettoOldCaso2 = asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo()+"_"+asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome();
							String soggettoNewCaso2 = asps.getTipoSoggettoErogatore()+"_"+asps.getNomeSoggettoErogatore();
							String tmpMatch_caso2 = soggettoOldCaso2+"/"+asps.getOldIDServizioForUpdate().getTipo()+"_"+asps.getOldIDServizioForUpdate().getNome()+"/";
							String match_caso2 = tmpMatch_caso2+"v"+asps.getOldIDServizioForUpdate().getVersione().intValue();
							String match_caso2_oldWithoutV = tmpMatch_caso2+asps.getOldIDServizioForUpdate().getVersione().intValue();
							if(descrizionePA.contains(match_caso2)) {
								String replace_caso2 = soggettoNewCaso2+"/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue();
								descrizionePA = descrizionePA.replace(match_caso2, replace_caso2);
							}
							else if(descrizionePA.contains(match_caso2_oldWithoutV)) {
								String replace_caso2 = soggettoNewCaso2+"/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue();
								descrizionePA = descrizionePA.replace(match_caso2_oldWithoutV, replace_caso2);
							}

							tmpPorta.setDescrizione(descrizionePA);

						}

						// regex del pattern azione
						// .*(erogatore)/(servizio)/([^/|^?]*).*
						PortaApplicativaAzione paAzione = tmpPorta.getAzione();
						PortaApplicativaAzioneIdentificazione identificazione = paAzione != null ? paAzione.getIdentificazione() : null;
						String patterAzione = paAzione != null ? (paAzione.getPattern() != null ? paAzione.getPattern() : "") : "";
						String patternAzionePrefix = ".*/";
						String patternAzioneSuffix1 = "/([^/|^?]*).*";
						// se identificazione urlbased procedo con i controlli
						if (PortaApplicativaAzioneIdentificazione.URL_BASED.equals(identificazione)) {
							if (patterAzione.startsWith(patternAzionePrefix) && patterAzione.endsWith(patternAzioneSuffix1)) {
								// caso1
								int startidx = patternAzionePrefix.length();
								int endidx = patterAzione.lastIndexOf(patternAzioneSuffix1);
								String tmpPat = patterAzione.substring(startidx, endidx);
								// a questo punto ottengo una stringa del tipo
								// (fruitore)/(erogatore)/(servizio)
								// se rispetta la regex allora vuol dire che il
								// pattern azione e' quello di default
								// e devo effettuare i cambiamenti
								String regex = "(.*)\\/(.*)\\/(.*)";
								if (tmpPat.matches(regex)) {
									String[] val = tmpPat.split("/");
									String partErogatore = val[0];
									String partServizio = val[1];
									String partVersione = val[2];
									String rimanenteRegExp = "";
									int lengthParteRimanenteRegExp = (partErogatore+"/"+partServizio+"/"+partVersione).length();
									if(tmpPat.length()>lengthParteRimanenteRegExp){
										rimanenteRegExp = tmpPat.substring(lengthParteRimanenteRegExp);
									}	

									boolean matchURL = false;
									
									// vedo se matcha l'erogatore
									String partErogatoreOld = "(?:"+asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo()+"_)?"+asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome()+"";
									String partErogatoreNew = "(?:"+asps.getTipoSoggettoErogatore()+"_)?"+asps.getNomeSoggettoErogatore()+"";
									if (partErogatore.equals(partErogatoreOld)) {
										partErogatore = partErogatoreNew;
										matchURL = true;
									}
									
									// vedo se matcha il servizio
									String partOldServizio = "(?:"+asps.getOldIDServizioForUpdate().getTipo()+"_)?"+asps.getOldIDServizioForUpdate().getNome()+"";
									String partNewServizio = "(?:"+asps.getTipo()+"_)?"+asps.getNome()+"";
									if (partServizio.equals(partOldServizio)) {
										partServizio = partNewServizio;
										matchURL = true;
									}

									// vedo se matcha versione
									String versioneOld = "v"+(asps.getOldIDServizioForUpdate().getVersione().intValue()+"");
									String versioneOld_oldWithoutV = (asps.getOldIDServizioForUpdate().getVersione().intValue()+"");
									if (partVersione.equals(versioneOld) || partVersione.equals(versioneOld_oldWithoutV)) {
										partVersione = "v"+asps.getVersione().intValue()+"";
										matchURL = true;
									}

									if(matchURL){
										String newPatternAzione = patternAzionePrefix + partErogatore+ "/" + partServizio+ "/" + partVersione + rimanenteRegExp + patternAzioneSuffix1;
										paAzione.setPattern(newPatternAzione);
										tmpPorta.setAzione(paAzione);
									}

								}
							}
						}// fine controllo azione


						// DelegatedBy
						String nomePortaDelegante = paAzione != null ? (paAzione.getNomePortaDelegante() != null ? paAzione.getNomePortaDelegante() : null) : null;
						if (PortaApplicativaAzioneIdentificazione.DELEGATED_BY.equals(identificazione) && nomePortaDelegante!=null ) {
							String nuovoNomeDelegate = null;
							boolean matchDelegate = false;
							if(nomePortaDelegante.equals(check1)) {
								matchDelegate = true;	
								nuovoNomeDelegate = newLocationSuffix;
							}
							else if(nomePortaDelegante.equals(check1_oldWithoutV)) {
								matchDelegate = true;	
								nuovoNomeDelegate = newLocationSuffix;
							}
							if(matchDelegate) {
								tmpPorta.getAzione().setNomePortaDelegante(nuovoNomeDelegate);
							}
						}// fine controllo DelegatedBy
						
						// Controllo policy di Rate Limiting
						if(tmpPorta.getOldIDPortaApplicativaForUpdate()!=null && tmpPorta.getOldIDPortaApplicativaForUpdate().getNome()!=null) {
							Search ricercaPolicies = new Search(true);
							List<AttivazionePolicy> listaPolicies = null;
							try {
								listaPolicies = confCore.attivazionePolicyList(ricercaPolicies, RuoloPolicy.APPLICATIVA, tmpPorta.getOldIDPortaApplicativaForUpdate().getNome());
							}catch(Exception e) {}
							if(listaPolicies!=null && !listaPolicies.isEmpty()) {
								for (AttivazionePolicy ap : listaPolicies) {
									if(ap.getFiltro()!=null && tmpPorta.getOldIDPortaApplicativaForUpdate().getNome().equals(ap.getFiltro().getNomePorta())) {
										
										// aggiorno nome porta
										ap.getFiltro().setNomePorta(tmpPorta.getNome());
																		
										listaPolicyPA.put(ap.getIdActivePolicy(), ap);
									}
								}
							}
						}
						// fine Controllo policy di Rate Limiting
					}

					listaPA.add(tmpPorta); // la porta la aggiungo cmq per modificare i dati


					// modifica nome Servizi Applicativi che riflette il nome della PA
					if(oldIDPortaApplicativaForUpdate!=null && tmpPorta.sizeServizioApplicativoList()>0) {
						for (PortaApplicativaServizioApplicativo portaApplicativaSA : tmpPorta.getServizioApplicativoList()) {
							
							// gw_ENTE/gw_TEST/v1 e __gw_ENTE/gw_TEST/v1__Specific2  
							boolean equalsName = portaApplicativaSA.getNome().equals(oldIDPortaApplicativaForUpdate.getNome());
						
							// gw_ENTE/gw_MULTIPLO/v1__SA1 e __gw_ENTE/gw_MULTIPLO/v1__Specific1__SA1
							boolean equalsConSuffissoConnettoriMultipli = false;
							String suffixConnettoreMultiplo = null;
							if(!equalsName) {
								if(portaApplicativaSA.getNome().startsWith(oldIDPortaApplicativaForUpdate.getNome())) {
									String withoutPrefix = portaApplicativaSA.getNome().substring(oldIDPortaApplicativaForUpdate.getNome().length());
									if(withoutPrefix!=null && withoutPrefix.startsWith(ConnettoriCostanti.PARAMETRO_CONNETTORI_MULTIPLI_SAX_PREFIX) && 
											!withoutPrefix.equals(ConnettoriCostanti.PARAMETRO_CONNETTORI_MULTIPLI_SAX_PREFIX)) {
										String checkNumero = withoutPrefix.substring(ConnettoriCostanti.PARAMETRO_CONNETTORI_MULTIPLI_SAX_PREFIX.length());
										if(checkNumero!=null) {
											try {
												int numero = Integer.valueOf(checkNumero);
												if(numero>0) {
													suffixConnettoreMultiplo = withoutPrefix;
													equalsConSuffissoConnettoriMultipli = true;
													//System.out.println("TROVATO ("+portaApplicativaSA.getNome()+")!!!! suffix["+suffixConnettoreMultiplo+"]");
												}
											}catch(Throwable t) {}
										}
									}
								}
							}
							
							if(equalsName || equalsConSuffissoConnettoriMultipli) {
								// devo aggiornare il nome del SA
								IDServizioApplicativo idSA = new IDServizioApplicativo();
								idSA.setNome(portaApplicativaSA.getNome());
								//idSA.setIdSoggettoProprietario(new IDSoggetto(tmpPorta.getTipoSoggettoProprietario(), tmpPorta.getNomeSoggettoProprietario()));
								idSA.setIdSoggettoProprietario(new IDSoggetto(asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo(),asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome()));
								ServizioApplicativo sa = saCore.getServizioApplicativo(idSA);

								IDServizioApplicativo oldIDServizioApplicativoForUpdate = new IDServizioApplicativo();
								oldIDServizioApplicativoForUpdate.setNome(sa.getNome());
								oldIDServizioApplicativoForUpdate.setIdSoggettoProprietario(idSA.getIdSoggettoProprietario());
								sa.setOldIDServizioApplicativoForUpdate(oldIDServizioApplicativoForUpdate);
								sa.setTipoSoggettoProprietario(asps.getTipoSoggettoErogatore());
								sa.setNomeSoggettoProprietario(asps.getNomeSoggettoErogatore());

								if(equalsName) {
									// __gw_ENTE/gw_TEST/1__Specific2
									// gw_ENTE/gw_TEST/1
									
									String tmp_check_nomeSA = "/"+asps.getOldIDServizioForUpdate().getTipo()+"_"+asps.getOldIDServizioForUpdate().getNome()+"/";
									String check_nomeSA = tmp_check_nomeSA+"v"+asps.getOldIDServizioForUpdate().getVersione().intValue();
									String check_nomeSA_oldWithoutV = tmp_check_nomeSA+asps.getOldIDServizioForUpdate().getVersione().intValue();
									if(sa.getNome().endsWith(check_nomeSA)) {
										sa.setNome(sa.getNome().replace(check_nomeSA, 
												"/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue()));
									}
									else if(sa.getNome().startsWith("__") && sa.getNome().contains(check_nomeSA)) {
										sa.setNome(sa.getNome().replace(check_nomeSA, 
												"/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue()));
									}
									else if(sa.getNome().endsWith(check_nomeSA_oldWithoutV)) {
										sa.setNome(sa.getNome().replace(check_nomeSA_oldWithoutV, 
												"/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue()));
									}
									else if(sa.getNome().startsWith("__") && sa.getNome().contains(check_nomeSA_oldWithoutV)) {
										sa.setNome(sa.getNome().replace(check_nomeSA_oldWithoutV, 
												"/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue()));
									}
									listaPA_SA.add(sa);
								}
								else if(equalsConSuffissoConnettoriMultipli){
									
									// gw_ENTE/gw_MULTIPLO/v1__SA1 e __gw_ENTE/gw_MULTIPLO/v1__Specific1__SA1
									
									String tmp_check_nomeSA = "/"+asps.getOldIDServizioForUpdate().getTipo()+"_"+asps.getOldIDServizioForUpdate().getNome()+"/";
									String check_nomeSA = tmp_check_nomeSA+"v"+asps.getOldIDServizioForUpdate().getVersione().intValue();
									String check_nomeSA_oldWithoutV = tmp_check_nomeSA+asps.getOldIDServizioForUpdate().getVersione().intValue();
									if(sa.getNome().endsWith(check_nomeSA+suffixConnettoreMultiplo)) {
										sa.setNome(sa.getNome().replace(check_nomeSA, 
												"/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue()));
									}
									else if(sa.getNome().startsWith("__") && sa.getNome().contains(check_nomeSA) && sa.getNome().endsWith(suffixConnettoreMultiplo)) {
										sa.setNome(sa.getNome().replace(check_nomeSA, 
												"/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue()));
									}
									else if(sa.getNome().endsWith(check_nomeSA_oldWithoutV+suffixConnettoreMultiplo)) {
										sa.setNome(sa.getNome().replace(check_nomeSA_oldWithoutV, 
												"/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue()));
									}
									else if(sa.getNome().startsWith("__") && sa.getNome().contains(check_nomeSA_oldWithoutV) && sa.getNome().endsWith(suffixConnettoreMultiplo)) {
										sa.setNome(sa.getNome().replace(check_nomeSA_oldWithoutV, 
												"/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue()));
									}
									listaPA_SA.add(sa);
								}
								
							}
							
						}
					}
					// modifica nome Servizi Applicativi che riflette il nome della PA
				}
			}

		}

		List<Object> oggettiDaAggiornare = new ArrayList<Object>();
		
		// Aggiorno il servizio
		oggettiDaAggiornare.add(asps);

		// Aggiorno le eventuali porte delegate
		for (PortaDelegata portaDelegata : listaPD) {
			oggettiDaAggiornare.add(portaDelegata);
		}

		// aggiorno le eventuali porte applicative
		for (PortaApplicativa portaApplicativa : listaPA) {
			oggettiDaAggiornare.add(portaApplicativa);
		}
		
		// aggiorno gli eventuali servizi applicativi
		for (ServizioApplicativo sa : listaPA_SA) {
			oggettiDaAggiornare.add(sa);
		}

		// Se ho cambiato i dati significativi del servizio devo effettuare anche l'update degli accordi di servizio
		// che includono questi servizi come servizi componenti.
		if (!newUri.equals(oldUri)) {

			IDServizio idServizioOLD =  asps.getOldIDServizioForUpdate();
			String uriOLD = IDServizioFactory.getInstance().getUriFromIDServizio(idServizioOLD);
			List<AccordoServizioParteComune> ass = apcCore.accordiServizio_serviziComponenti(idServizioOLD);
			for(int i=0; i<ass.size(); i++){
				AccordoServizioParteComune accordoServizioComposto = ass.get(i);
				if(accordoServizioComposto.getServizioComposto()!=null){
					for(int j=0;j<accordoServizioComposto.getServizioComposto().sizeServizioComponenteList();j++){
						IDServizio idServizioComponente = IDServizioFactory.getInstance().
								getIDServizioFromValues(accordoServizioComposto.getServizioComposto().getServizioComponente(j).getTipo(), accordoServizioComposto.getServizioComposto().getServizioComponente(j).getNome(), 
										accordoServizioComposto.getServizioComposto().getServizioComponente(j).getTipoSoggetto(),accordoServizioComposto.getServizioComposto().getServizioComponente(j).getNomeSoggetto(), 
										accordoServizioComposto.getServizioComposto().getServizioComponente(j).getVersione());
						String uriServizioComponente = IDServizioFactory.getInstance().getUriFromIDServizio(idServizioComponente);
						if(uriServizioComponente.equals(uriOLD)){
							accordoServizioComposto.getServizioComposto().getServizioComponente(j).setTipoSoggetto(asps.getTipoSoggettoErogatore());
							accordoServizioComposto.getServizioComposto().getServizioComponente(j).setNomeSoggetto(asps.getNomeSoggettoErogatore());
							accordoServizioComposto.getServizioComposto().getServizioComponente(j).setTipo(asps.getTipo());
							accordoServizioComposto.getServizioComposto().getServizioComponente(j).setNome(asps.getNome());
							accordoServizioComposto.getServizioComposto().getServizioComponente(j).setVersione(asps.getVersione());
						}
					}
					oggettiDaAggiornare.add(accordoServizioComposto);
					//System.out.println("As SERVIZIO COMPONENTE ["+IDAccordo.getUriFromAccordo(accordoServizioComposto)+"]");
				}
			}
		}
		
		// Se ho cambiato i dati significativi del servizio devo effettuare anche l'update delle policy di Rate Limiting globali
		// che includono questi servizi come servizi componenti.
		if (!newUri.equals(oldUri)) {

			IDServizio idServizioOLD =  asps.getOldIDServizioForUpdate();
			
			Search ricercaPolicies = new Search(true);
			List<AttivazionePolicy> listaPolicies = null;
			try {
				listaPolicies = confCore.attivazionePolicyListByFilter(ricercaPolicies, null, null,
						null, null, null,
						null, null,
						idServizioOLD, null);
			}catch(Exception e) {}
			if(listaPolicies!=null && !listaPolicies.isEmpty()) {
				for (AttivazionePolicy ap : listaPolicies) {
					if(ap.getFiltro()!=null) {
						
						if(idServizioOLD.getSoggettoErogatore().getTipo().equals(ap.getFiltro().getTipoErogatore())) {
							ap.getFiltro().setTipoErogatore(asps.getTipoSoggettoErogatore());
						}
						
						if(idServizioOLD.getSoggettoErogatore().getNome().equals(ap.getFiltro().getNomeErogatore())) {
							ap.getFiltro().setNomeErogatore(asps.getNomeSoggettoErogatore());
						}
						
						if(idServizioOLD.getTipo().equals(ap.getFiltro().getTipoServizio())) {
							ap.getFiltro().setTipoServizio(asps.getTipo());
						}
						
						if(idServizioOLD.getNome().equals(ap.getFiltro().getNomeServizio())) {
							ap.getFiltro().setNomeServizio(asps.getNome());
						}
						
						if(ap.getFiltro().getVersioneServizio()!=null && (idServizioOLD.getVersione().intValue() == ap.getFiltro().getVersioneServizio().intValue())) {
							ap.getFiltro().setVersioneServizio(asps.getVersione());
						}
														
						listaPolicyPA.put(ap.getIdActivePolicy(), ap);
					}
				}
			}
		}
		// fine Controllo policy di Rate Limiting
		
		// aggiorno le policy di rate limiting
		Enumeration<AttivazionePolicy> enPolicy = listaPolicyPA.elements();
		while (enPolicy.hasMoreElements()) {
			AttivazionePolicy ap = (AttivazionePolicy) enPolicy.nextElement();
			oggettiDaAggiornare.add(ap);
		}
		
		return oggettiDaAggiornare;
	}

	public static boolean deleteAccordoServizioParteSpecifica(AccordoServizioParteSpecifica asps, 
			boolean gestioneFruitori, boolean gestioneErogatori,
			IDSoggetto idSoggettoFruitore, IDServizio idServizio,
			IExtendedListServlet extendedServlet,
			String superUser, AccordiServizioParteSpecificaCore apsCore, AccordiServizioParteSpecificaHelper apsHelper, 
			StringBuilder inUsoMessage, String newLine) throws Exception {
		
		SoggettiCore soggettiCore = new SoggettiCore(apsCore);
		PddCore pddCore = new PddCore(apsCore);
		PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
		PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apsCore);
		ConfigurazioneCore confCore = new ConfigurazioneCore(apsCore);
		ServiziApplicativiCore saCore = new ServiziApplicativiCore(apsCore);
		
		List<PortaApplicativa> paGenerateAutomcaticamente = null;
		List<IDPortaApplicativa> idPAGenerateAutomaticamente = null;
		
		List<PortaDelegata> pdGenerateAutomcaticamente = null;
		List<IDPortaDelegata> idPDGenerateAutomaticamente = null;
		
		// Verifico se sono in modalità di interfaccia 'standard' che non si tratti della PortaApplicativa generata automaticamente.
		// In tal caso la posso eliminare.
		if(asps!=null){
			boolean generaPACheckSoggetto = true;
			IDSoggetto idSoggettoEr = new IDSoggetto(asps.getTipoSoggettoErogatore(), asps.getNomeSoggettoErogatore());
			Soggetto soggetto = soggettiCore.getSoggettoRegistro(idSoggettoEr );
			if(pddCore.isPddEsterna(soggetto.getPortaDominio())){
				generaPACheckSoggetto = false;
			}	
				
			if(gestioneFruitori) {
								
				// Verifico se esiste il mapping con la fruizione
				idPDGenerateAutomaticamente = porteDelegateCore.getIDPorteDelegateAssociate(idServizio, idSoggettoFruitore);
				if(idPDGenerateAutomaticamente!=null && idPDGenerateAutomaticamente.size()>0){
					for (IDPortaDelegata idPortaDelegata : idPDGenerateAutomaticamente) {
						if(idPortaDelegata.getIdentificativiFruizione()==null) {
							idPortaDelegata.setIdentificativiFruizione(new IdentificativiFruizione());
						}
						if(idPortaDelegata.getIdentificativiFruizione().getSoggettoFruitore()==null) {
							idPortaDelegata.getIdentificativiFruizione().setSoggettoFruitore(idSoggettoFruitore);
						}
						if(pdGenerateAutomcaticamente==null) {
							pdGenerateAutomcaticamente=new ArrayList<>();
						}
						pdGenerateAutomcaticamente.add(porteDelegateCore.getPortaDelegata(idPortaDelegata));
					}
				}
				
			}
			else if(generaPACheckSoggetto){
					
				// Verifico se esiste il mapping con l'erogazione
				idPAGenerateAutomaticamente = porteApplicativeCore.getIDPorteApplicativeAssociate(idServizio);
				if(idPAGenerateAutomaticamente!=null && idPAGenerateAutomaticamente.size()>0){
					for (IDPortaApplicativa idPortaApplicativa : idPAGenerateAutomaticamente) {
						if(paGenerateAutomcaticamente==null) {
							paGenerateAutomcaticamente=new ArrayList<>();
						}
						paGenerateAutomcaticamente.add(porteApplicativeCore.getPortaApplicativa(idPortaApplicativa));
					}
				}
				
			}
			
		}
		
		HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
		
		boolean normalizeObjectIds = !apsHelper.isModalitaCompleta();
		
		// Prima verifico che l'aps non sia associato ad altre fruizioni od erogazioni
		boolean apsEliminabile = true;
		List<IDPortaDelegata> idPDGenerateAutomaticamenteCheckInUso = new ArrayList<>();
		List<IDPortaApplicativa> idPAGenerateAutomaticamenteCheckInUso = new ArrayList<>();
		if(gestioneErogatori) {
			if(idPAGenerateAutomaticamente!=null && idPAGenerateAutomaticamente.size()>0){
				idPAGenerateAutomaticamenteCheckInUso.addAll(idPAGenerateAutomaticamente);
			}
			
			// verifico che non sia utilizzato in altre fruizioni
			if(asps.sizeFruitoreList()>0) {
				for (Fruitore fruitore : asps.getFruitoreList()) {
					IDSoggetto idSoggettoFruitoreCheck = new IDSoggetto(fruitore.getTipo(), fruitore.getNome());
					Soggetto soggettoCheck = soggettiCore.getSoggettoRegistro(idSoggettoFruitoreCheck );
					if(!pddCore.isPddEsterna(soggettoCheck.getPortaDominio())){
						List<IDPortaDelegata> idPDGenerateAutomaticamenteTmp = porteDelegateCore.getIDPorteDelegateAssociate(idServizio, idSoggettoFruitoreCheck);
						if(idPDGenerateAutomaticamenteTmp!=null && !idPDGenerateAutomaticamenteTmp.isEmpty()) {
							apsEliminabile = false;
							break;
						}
					}	
						
				}
			}
		}
		else if(gestioneFruitori) {
			
			if(idPDGenerateAutomaticamente!=null && idPDGenerateAutomaticamente.size()>0){
				idPDGenerateAutomaticamenteCheckInUso.addAll(idPDGenerateAutomaticamente);
			}
			
			// verifico che non sia utilizzato in una erogazione
			List<IDPortaApplicativa> idPAGenerateAutomaticamenteTmp = porteApplicativeCore.getIDPorteApplicativeAssociate(idServizio);
			if(idPAGenerateAutomaticamenteTmp!=null && !idPAGenerateAutomaticamenteTmp.isEmpty()) {
				apsEliminabile = false;
			}
			
			if(apsEliminabile) {
				// verifico che non sia utilizzato in altre fruizioni diverse da quella che sto osservando
				if(asps.sizeFruitoreList()>0) {
					for (Fruitore fruitore : asps.getFruitoreList()) {
						IDSoggetto idSoggettoFruitoreCheck = new IDSoggetto(fruitore.getTipo(), fruitore.getNome());
						if(!idSoggettoFruitoreCheck.equals(idSoggettoFruitore)) {
							Soggetto soggettoCheck = soggettiCore.getSoggettoRegistro(idSoggettoFruitoreCheck );
							if(!pddCore.isPddEsterna(soggettoCheck.getPortaDominio())){
								List<IDPortaDelegata> idPDGenerateAutomaticamenteTmp = porteDelegateCore.getIDPorteDelegateAssociate(idServizio, idSoggettoFruitore);
								if(idPDGenerateAutomaticamenteTmp!=null && !idPDGenerateAutomaticamenteTmp.isEmpty()) {
									apsEliminabile = false;
									break;
								}
							}	
						}
					}
				}
			}
		}
		
		
		boolean inUso = false;
		if(apsEliminabile) {
			inUso = apsCore.isAccordoServizioParteSpecificaInUso(asps, whereIsInUso, 
					idPDGenerateAutomaticamente, idPAGenerateAutomaticamente, normalizeObjectIds);
		}
		
		if (inUso) {// accordo in uso
			String tipo = null;
			if(gestioneFruitori) {
				tipo = "Fruizione del Servizio";
			}
			else {
				if(apsHelper.isModalitaCompleta()) {
					tipo = "Servizio";
				}
				else {
					tipo = "Erogazione del Servizio";
				}
			}
			inUsoMessage.append(DBOggettiInUsoUtils.toString(idServizio, whereIsInUso, true, newLine,normalizeObjectIds,tipo));
			inUsoMessage.append(newLine);
		} else {// accordo non in uso
			
			List<Object> listaOggettiDaEliminare = new ArrayList<Object>();
			
			if(paGenerateAutomcaticamente!=null && paGenerateAutomcaticamente.size()>0){
				
				for (PortaApplicativa paGenerataAutomcaticamente : paGenerateAutomcaticamente) {
					
					if(extendedServlet!=null){
						List<IExtendedBean> listExt = null;
						try{
							listExt = extendedServlet.extendedBeanList(TipoOperazione.DEL,apsHelper,apsCore,paGenerataAutomcaticamente);
						}catch(Exception e){
							ControlStationCore.logError(e.getMessage(), e);
						}
						if(listExt!=null && listExt.size()>0){
							for (IExtendedBean iExtendedBean : listExt) {
								WrapperExtendedBean wrapper = new WrapperExtendedBean();
								wrapper.setExtendedBean(iExtendedBean);
								wrapper.setExtendedServlet(extendedServlet);
								wrapper.setOriginalBean(paGenerataAutomcaticamente);
								wrapper.setManageOriginalBean(false);		
								listaOggettiDaEliminare.add(wrapper);
							}
						}
					}
					
					// cancellazione del mapping
					MappingErogazionePortaApplicativa mappingErogazione = new MappingErogazionePortaApplicativa();
					IDSoggetto soggettoErogatore = new IDSoggetto(paGenerataAutomcaticamente.getTipoSoggettoProprietario(),paGenerataAutomcaticamente.getNomeSoggettoProprietario());
					IDPortaApplicativa idPortaApplicativa = new IDPortaApplicativa();
					idPortaApplicativa.setNome(paGenerataAutomcaticamente.getNome());
					mappingErogazione.setIdPortaApplicativa(idPortaApplicativa);
					IDServizio idServizioPA = IDServizioFactory.getInstance().getIDServizioFromValues(paGenerataAutomcaticamente.getServizio().getTipo(),
							paGenerataAutomcaticamente.getServizio().getNome(), soggettoErogatore, paGenerataAutomcaticamente.getServizio().getVersione());
					mappingErogazione.setIdServizio(idServizioPA);
					if(porteApplicativeCore.existsMappingErogazionePortaApplicativa(mappingErogazione)) {
						listaOggettiDaEliminare.add(mappingErogazione);
					}
					
					// cancello per policy associate alla porta se esistono
					List<AttivazionePolicy> listAttivazione = confCore.attivazionePolicyList(new Search(true), RuoloPolicy.APPLICATIVA, paGenerataAutomcaticamente.getNome());
					if(listAttivazione!=null && !listAttivazione.isEmpty()) {
						listaOggettiDaEliminare.addAll(listAttivazione);
					}
					
					// cancellazione della porta
					listaOggettiDaEliminare.add(paGenerataAutomcaticamente);
					
					// cancellazione eventuale applicativo di default
					if(paGenerataAutomcaticamente.getServizioApplicativoDefault() != null) {
						IDServizioApplicativo idSA = new IDServizioApplicativo();
						idSA.setIdSoggettoProprietario(soggettoErogatore);
						idSA.setNome(paGenerataAutomcaticamente.getServizioApplicativoDefault());
						ServizioApplicativo saGeneratoAutomaticamente = saCore.getServizioApplicativo(idSA);
						if(!ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(saGeneratoAutomaticamente.getTipo()))
							listaOggettiDaEliminare.add(saGeneratoAutomaticamente);	
					}
					
					// cancellazione degli applicativi generati automaticamente
					for (PortaApplicativaServizioApplicativo paSA : paGenerataAutomcaticamente.getServizioApplicativoList()) {
						if(paSA.getNome().equals(paGenerataAutomcaticamente.getNome())) {
							IDServizioApplicativo idSA = new IDServizioApplicativo();
							idSA.setIdSoggettoProprietario(soggettoErogatore);
							idSA.setNome(paSA.getNome());
							ServizioApplicativo saGeneratoAutomaticamente = saCore.getServizioApplicativo(idSA);
							
							if(!ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(saGeneratoAutomaticamente.getTipo()))
								listaOggettiDaEliminare.add(saGeneratoAutomaticamente);
						}
					}
				}
				
			}
			
			if(pdGenerateAutomcaticamente!=null && pdGenerateAutomcaticamente.size()>0){
				
				for (PortaDelegata pdGenerataAutomcaticamente : pdGenerateAutomcaticamente) {
					
					if(extendedServlet!=null){
						List<IExtendedBean> listExt = null;
						try{
							listExt = extendedServlet.extendedBeanList(TipoOperazione.DEL,apsHelper,apsCore,pdGenerataAutomcaticamente);
						}catch(Exception e){
							ControlStationCore.logError(e.getMessage(), e);
						}
						if(listExt!=null && listExt.size()>0){
							for (IExtendedBean iExtendedBean : listExt) {
								WrapperExtendedBean wrapper = new WrapperExtendedBean();
								wrapper.setExtendedBean(iExtendedBean);
								wrapper.setExtendedServlet(extendedServlet);
								wrapper.setOriginalBean(pdGenerataAutomcaticamente);
								wrapper.setManageOriginalBean(false);		
								listaOggettiDaEliminare.add(wrapper);
							}
						}
					}
					
					// cancellazione del mapping
					MappingFruizionePortaDelegata mappingFruizione = new MappingFruizionePortaDelegata();
					mappingFruizione.setIdFruitore(idSoggettoFruitore);
					mappingFruizione.setIdServizio(idServizio);
					IDPortaDelegata idPortaDelegata = new IDPortaDelegata();
					idPortaDelegata.setNome(pdGenerataAutomcaticamente.getNome());
					mappingFruizione.setIdPortaDelegata(idPortaDelegata);
					if(porteDelegateCore.existsMappingFruizionePortaDelegata(mappingFruizione)) {
						listaOggettiDaEliminare.add(mappingFruizione);
					}
					
					// cancello per policy associate alla porta se esistono
					List<AttivazionePolicy> listAttivazione = confCore.attivazionePolicyList(new Search(true), RuoloPolicy.DELEGATA, pdGenerataAutomcaticamente.getNome());
					if(listAttivazione!=null && !listAttivazione.isEmpty()) {
						listaOggettiDaEliminare.addAll(listAttivazione);
					}
					
					// cancellazione della porta
					listaOggettiDaEliminare.add(pdGenerataAutomcaticamente);
					
				}
				
			}
			
			boolean updateAPS = false;
			if(apsEliminabile) {
				listaOggettiDaEliminare.add(asps);
			}
			else if(gestioneFruitori) {
				// elimino fruitore
				if(asps.sizeFruitoreList()>0) {
					for (int j = 0; j < asps.sizeFruitoreList(); j++) {
						Fruitore fruitore = asps.getFruitore(j);
						IDSoggetto idSoggettoFruitoreCheck = new IDSoggetto(fruitore.getTipo(), fruitore.getNome());
						if(idSoggettoFruitoreCheck.equals(idSoggettoFruitore)) {
							asps.removeFruitore(j);
							updateAPS = true;
							break;
						}
					}
				}
			}
			
			apsCore.performDeleteOperation(superUser, apsHelper.smista(), listaOggettiDaEliminare.toArray());
			if(updateAPS) {
				apsCore.performUpdateOperation(superUser, apsHelper.smista(), asps);
			}
			return true;

		}
		
		return false;
	}

	public static void deleteAccordoServizioParteSpecificaPorteApplicative(IDPortaApplicativa idPortaApplicativa, IDServizio idServizio,
			String superUser, AccordiServizioParteSpecificaCore apsCore, AccordiServizioParteSpecificaHelper apsHelper,
			StringBuilder inUsoMessage) throws Exception {
		
		PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apsCore);
		ConfigurazioneCore confCore = new ConfigurazioneCore(apsCore);
		ServiziApplicativiCore saCore = new ServiziApplicativiCore(apsCore);
		
		List<Object> listaOggettiDaEliminare = new ArrayList<Object>();
		
		// leggo la pa
		PortaApplicativa tmpPA = porteApplicativeCore.getPortaApplicativa(idPortaApplicativa);
		// controllo se il mapping e' di default, se lo e' salto questo elemento
		
		boolean isDefault = apsCore.isDefaultMappingErogazione(idServizio, idPortaApplicativa );
		
		if(!isDefault) {
			//cancello il mapping
			MappingErogazionePortaApplicativa mappingErogazione = new MappingErogazionePortaApplicativa();
			mappingErogazione.setIdServizio(idServizio);
			mappingErogazione.setIdPortaApplicativa(idPortaApplicativa);
			listaOggettiDaEliminare.add(mappingErogazione);
			
			// cancello per policy associate alla porta se esistono
			List<AttivazionePolicy> listAttivazione = confCore.attivazionePolicyList(new Search(true), RuoloPolicy.APPLICATIVA, tmpPA.getNome());
			if(listAttivazione!=null && !listAttivazione.isEmpty()) {
				listaOggettiDaEliminare.addAll(listAttivazione);
			}
			
			// cancello la porta associata
			listaOggettiDaEliminare.add(tmpPA);
			
			// cancellazione eventuale applicativo di default
			if(tmpPA.getServizioApplicativoDefault() != null) {
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setIdSoggettoProprietario(new IDSoggetto(tmpPA.getTipoSoggettoProprietario(), tmpPA.getNomeSoggettoProprietario()));
				idSA.setNome(tmpPA.getServizioApplicativoDefault());
				ServizioApplicativo saGeneratoAutomaticamente = saCore.getServizioApplicativo(idSA);
				if(!ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(saGeneratoAutomaticamente.getTipo()))
					listaOggettiDaEliminare.add(saGeneratoAutomaticamente);	
			}
			
			for (PortaApplicativaServizioApplicativo paSA : tmpPA.getServizioApplicativoList()) {
				if(paSA.getNome().equals(tmpPA.getNome())) {
					IDServizioApplicativo idSA = new IDServizioApplicativo();
					idSA.setIdSoggettoProprietario(new IDSoggetto(tmpPA.getTipoSoggettoProprietario(), tmpPA.getNomeSoggettoProprietario()));
					idSA.setNome(paSA.getNome());
					ServizioApplicativo saGeneratoAutomaticamente = saCore.getServizioApplicativo(idSA);
					
					// elimino solo i SA non server
					if(!ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(saGeneratoAutomaticamente.getTipo()))
						listaOggettiDaEliminare.add(saGeneratoAutomaticamente);
				}
			}
			
			// Elimino entrambi gli oggetti
			apsCore.performDeleteOperation(superUser, apsHelper.smista(), listaOggettiDaEliminare.toArray(new Object[1]));
		} else {
			inUsoMessage.append(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_IMPOSSIBILE_ELIMINARE_LA_CONFIGURAZIONE_DI_DEFAULT_EROGAZIONE);
		}
		
	}
	
	public static void deleteAccordoServizioParteSpecificaFruitoriPorteDelegate(List<IDPortaDelegata> listPortaDelegataDaELiminare,
			AccordoServizioParteSpecifica asps, IDSoggetto idSoggettoFruitore,
			String superUser, AccordiServizioParteSpecificaCore apsCore, AccordiServizioParteSpecificaHelper apsHelper,
			StringBuilder inUsoMessage) throws Exception {
		
		PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
		ConfigurazioneCore confCore = new ConfigurazioneCore(apsCore);
		
		IDServizio idServizioFromAccordo = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
		
		List<Object> listaOggettiDaModificare = new ArrayList<Object>();
		Fruitore fruitore = null;
		for (Fruitore fruitoreCheck : asps.getFruitoreList()) {
			if(fruitoreCheck.getTipo().equals(idSoggettoFruitore.getTipo()) && fruitoreCheck.getNome().equals(idSoggettoFruitore.getNome())) {
				fruitore = fruitoreCheck;
				break;
			}
		}
		
		boolean updateASPS = false;
		
		for (int i = 0; i < listPortaDelegataDaELiminare.size(); i++) {
			
			List<Object> listaOggettiDaEliminare = new ArrayList<Object>();
			
			// ricevo come parametro l'id della pd associata al mapping da cancellare
			IDPortaDelegata idPortaDelegata = listPortaDelegataDaELiminare.get(i);

			// Prendo la porta delegata
			PortaDelegata tmpPD = porteDelegateCore.getPortaDelegata(idPortaDelegata);
			
			// controllo se il mapping e' di default, se lo e' salto questo elemento
			boolean isDefault = apsCore.isDefaultMappingFruizione(idServizioFromAccordo, idSoggettoFruitore, idPortaDelegata );
			
			if(!isDefault) {
				//cancello il mapping
				MappingFruizionePortaDelegata mappingFruizione = new MappingFruizionePortaDelegata();
				mappingFruizione.setIdFruitore(idSoggettoFruitore);
				mappingFruizione.setIdPortaDelegata(idPortaDelegata);
				mappingFruizione.setIdServizio(idServizioFromAccordo);
				listaOggettiDaEliminare.add(mappingFruizione);
			
				// cancello per policy associate alla porta se esistono
				List<AttivazionePolicy> listAttivazione = confCore.attivazionePolicyList(new Search(true), RuoloPolicy.DELEGATA, tmpPD.getNome());
				if(listAttivazione!=null && !listAttivazione.isEmpty()) {
					listaOggettiDaEliminare.addAll(listAttivazione);
				}
				
				// cancello la porta associata
				listaOggettiDaEliminare.add(tmpPD);
				
				// Elimino entrambi gli oggetti
				apsCore.performDeleteOperation(superUser, apsHelper.smista(), listaOggettiDaEliminare.toArray(new Object[1]));
				
				// Connettore della fruizione
				int index = -1;
				for (int j = 0; j < fruitore.sizeConfigurazioneAzioneList(); j++) {
					ConfigurazioneServizioAzione config = fruitore.getConfigurazioneAzione(j);
					if(config!=null) {
						String azione = tmpPD.getAzione().getAzioneDelegata(0); // prendo la prima
						if(config.getAzioneList().contains(azione)) {
							index = j;
							break;
						}
					}
				}
				if(index>=0) {
					updateASPS = true;
					fruitore.removeConfigurazioneAzione(index);
				}
				
			}else {
				inUsoMessage.append(AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_IMPOSSIBILE_ELIMINARE_LA_CONFIGURAZIONE_DI_DEFAULT_FRUIZIONE);
			}
			
		}// for
		
		
		if(updateASPS) {
			
			listaOggettiDaModificare.add(asps);
			
		}
		
		if(listaOggettiDaModificare.size()>0) {
			porteDelegateCore.performUpdateOperation(superUser, apsHelper.smista(), listaOggettiDaModificare.toArray());
		}
	}
	
	public static final MappingErogazionePortaApplicativa getDefaultMappingPA(List<MappingErogazionePortaApplicativa> listaMappingErogazione) {
		return listaMappingErogazione.stream().filter( m -> m.isDefault()).findFirst().orElse(null);
	}
	
	public static final MappingErogazionePortaApplicativa getMappingPA(List<MappingErogazionePortaApplicativa> listaMappingErogazione, String mappingPA) {
		return listaMappingErogazione.stream().filter( m -> m.getNome().equals(mappingPA)).findFirst().orElse(null);
	}
	
	public static final MappingErogazionePortaApplicativa getMappingPA_filterByDescription(List<MappingErogazionePortaApplicativa> listaMappingErogazione, String descrizione) {
		return listaMappingErogazione.stream().filter( m -> m.getDescrizione().equals(descrizione)).findFirst().orElse(null);
	}
	
	public static AccordiServizioParteSpecificaPorteApplicativeMappingInfo getMappingInfo(String mappingPA, AccordoServizioParteSpecifica asps, 
			AccordiServizioParteSpecificaCore apsCore) throws Exception {
		
		PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apsCore);
		
		IDServizio idServizio2 = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps); 
		
		List<MappingErogazionePortaApplicativa> listaMappingErogazione = apsCore.mappingServiziPorteAppList(idServizio2,asps.getId(), null);
		MappingErogazionePortaApplicativa mappingSelezionato = null, mappingDefault = null;
		boolean paMappingSelezionatoMulti=false;
		
		String mappingLabel = "";
		String[] listaMappingLabels = null;
		String[] listaMappingValues = null;
		List<String> azioniOccupate = new ArrayList<>();
		
		String nomeNuovaConfigurazione = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MAPPING_EROGAZIONE_PA_AZIONE_SPECIFIC_PREFIX + "1";
		int idxConfigurazione = 0;
		int listaMappingErogazioneSize = listaMappingErogazione != null ? listaMappingErogazione.size() : 0;
		
		if(listaMappingErogazioneSize > 0) {

			mappingDefault = getDefaultMappingPA(listaMappingErogazione);
			
			if(mappingPA != null) {
				mappingSelezionato = getMappingPA(listaMappingErogazione, mappingPA);
			}

			if(mappingSelezionato == null) {
				mappingSelezionato = mappingDefault;
			}

			PortaApplicativa paMappingTmp = porteApplicativeCore.getPortaApplicativa(mappingSelezionato.getIdPortaApplicativa());
			paMappingSelezionatoMulti = paMappingTmp.getBehaviour() != null;
			
			if(!mappingSelezionato.isDefault()) {
				mappingLabel = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(null,null,paMappingTmp,Integer.MAX_VALUE);
			}
			
			listaMappingLabels = new String[listaMappingErogazioneSize];
			listaMappingValues = new String[listaMappingErogazioneSize];
			for (int i = 0; i < listaMappingErogazione.size(); i++) {
				MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa = listaMappingErogazione.get(i);
				//String nomeMappingNoDefault = mappingErogazionePortaApplicativa.getNome();
				String nomeMappingNoDefault = null;
				//if(!mappingErogazionePortaApplicativa.isDefault()) {
				PortaApplicativa paMapping = porteApplicativeCore.getPortaApplicativa(mappingErogazionePortaApplicativa.getIdPortaApplicativa());
				nomeMappingNoDefault = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(null,null,paMapping,70,true);
				//}
//				listaMappingLabels[i] = mappingErogazionePortaApplicativa.isDefault()?
//						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MAPPING_EROGAZIONE_PA_NOME_DEFAULT: nomeMappingNoDefault;
				listaMappingLabels[i] = nomeMappingNoDefault;
				listaMappingValues[i] = mappingErogazionePortaApplicativa.getNome();
				
				// calcolo del nome automatico
				if(!mappingErogazionePortaApplicativa.isDefault())  {
					int idx = mappingErogazionePortaApplicativa.getNome().indexOf(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MAPPING_EROGAZIONE_PA_AZIONE_SPECIFIC_PREFIX);
					if(idx > -1) {
						String idxTmp = mappingErogazionePortaApplicativa.getNome().substring(idx + PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MAPPING_EROGAZIONE_PA_AZIONE_SPECIFIC_PREFIX.length());
						int idxMax = -1;
						try {
							idxMax = Integer.parseInt(idxTmp);
						}catch(Exception e) {
							idxMax = 0;
						}
						idxConfigurazione = Math.max(idxConfigurazione, idxMax);
					}
				}
				
				// colleziono le azioni gia' configurate
				PortaApplicativa portaApplicativa = porteApplicativeCore.getPortaApplicativa(mappingErogazionePortaApplicativa.getIdPortaApplicativa());
				if(portaApplicativa.getAzione() != null && portaApplicativa.getAzione().getAzioneDelegataList() != null)
					azioniOccupate.addAll(portaApplicativa.getAzione().getAzioneDelegataList());
			}
		}
		
		nomeNuovaConfigurazione = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MAPPING_EROGAZIONE_PA_AZIONE_SPECIFIC_PREFIX + ( ++ idxConfigurazione);
		
		AccordiServizioParteSpecificaPorteApplicativeMappingInfo mappingInfo = new AccordiServizioParteSpecificaPorteApplicativeMappingInfo();
		mappingInfo.setListaMappingErogazione(listaMappingErogazione);
		mappingInfo.setMappingSelezionato(mappingSelezionato);
		mappingInfo.setMappingDefault(mappingDefault);
		mappingInfo.setMappingLabel(mappingLabel);
		mappingInfo.setListaMappingLabels(listaMappingLabels);
		mappingInfo.setListaMappingValues(listaMappingValues);
		mappingInfo.setAzioniOccupate(azioniOccupate);
		mappingInfo.setNomeNuovaConfigurazione(nomeNuovaConfigurazione);
		mappingInfo.setPaMappingSelezionatoMulti(paMappingSelezionatoMulti);
		
		return mappingInfo;
	}
	
	public static void addAccordoServizioParteSpecificaPorteApplicative(MappingErogazionePortaApplicativa mappingDefault,
			MappingErogazionePortaApplicativa mappingSelezionato,
			String nome, String nomeGruppo, String[] azioni, String modeCreazione, String modeCreazioneConnettore,
			String endpointtype, String tipoconn, String autenticazioneHttp,
			String connettoreDebug,
			String url,
			String nomeCodaJms, String tipoJms, 
			String initcont, String urlpgk, String provurl, String connfact, String tipoSendas, 
			String user, String password,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			boolean httpsTrustVerifyCert, String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String httpsKeyAlias, String httpsTrustStoreCRLs,
			String proxy_enabled, String proxy_hostname, String proxy_port, String proxy_username, String proxy_password,
			String tempiRisposta_enabled, String tempiRisposta_connectionTimeout, String tempiRisposta_readTimeout, String tempiRisposta_tempoMedioRisposta,
			String opzioniAvanzate, String transfer_mode, String transfer_mode_chunk_size, String redirect_mode, String redirect_max_hop,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime,
			boolean autenticazioneToken, String tokenPolicy,
			List<ExtendedConnettore> listExtendedConnettore,
			String erogazioneAutenticazione, String erogazioneAutenticazioneOpzionale, TipoAutenticazionePrincipal erogazioneAutenticazionePrincipal, List<String> erogazioneAutenticazioneParametroList,
			String erogazioneAutorizzazione, String erogazioneAutorizzazioneAutenticati, String erogazioneAutorizzazioneRuoli, String erogazioneAutorizzazioneRuoliTipologia, String erogazioneAutorizzazioneRuoliMatch,
			String nomeSA, String erogazioneRuolo, String erogazioneSoggettoAutenticato, 
			String autorizzazione_tokenOptions,
			String autorizzazioneScope, String scope, String autorizzazioneScopeMatch,BinaryParameter allegatoXacmlPolicy,
			String gestioneToken, 
			String gestioneTokenPolicy,  String gestioneTokenOpzionale,  
			String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autenticazioneTokenIssuer, String autenticazioneTokenClientId, String autenticazioneTokenSubject, String autenticazioneTokenUsername, String autenticazioneTokenEMail,
			AccordoServizioParteSpecifica asps, 
			String protocollo, String userLogin,
			AccordiServizioParteSpecificaCore apsCore, AccordiServizioParteSpecificaHelper apsHelper, String nomeSAServer) throws Exception {
	
		PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apsCore);
		AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(apsCore);
		SoggettiCore soggettiCore = new SoggettiCore(apcCore);
		ServiziApplicativiCore saCore = new ServiziApplicativiCore(apsCore);
		
		IDServizio idServizio2 = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps); 
	
		AccordoServizioParteComuneSintetico as = null;
		ServiceBinding serviceBinding = null;
		if (asps != null) {
			IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
			as = apcCore.getAccordoServizioSintetico(idAccordo);
			serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
		}
		
		List<Object> listaOggettiDaCreare = new ArrayList<Object>();

		PortaApplicativa portaApplicativaDefault = porteApplicativeCore.getPortaApplicativa(mappingDefault.getIdPortaApplicativa());
		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
		Implementation implementation = null;
		
		IConfigIntegrationReader configIntegrationReader = apsCore.getConfigIntegrationReader(protocolFactory);
		
		PortaApplicativa portaApplicativaDaCopiare = null;
		if(modeCreazione.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MODO_CREAZIONE_EREDITA)) {
			portaApplicativaDaCopiare = porteApplicativeCore.getPortaApplicativa(mappingSelezionato.getIdPortaApplicativa());
			implementation = protocolFactory.createProtocolIntegrationConfiguration().createImplementation(configIntegrationReader, serviceBinding, idServizio2, 
					portaApplicativaDefault, portaApplicativaDaCopiare, nome, nomeGruppo, azioni);
		}
		else {
			implementation = protocolFactory.createProtocolIntegrationConfiguration().createImplementation(configIntegrationReader, serviceBinding, idServizio2, 
					portaApplicativaDefault, nome, nomeGruppo, azioni);
		}
		
		PortaApplicativa portaApplicativa = implementation.getPortaApplicativa();
		MappingErogazionePortaApplicativa mappingErogazione = implementation.getMapping();
		List<AttivazionePolicy> rateLimitingPolicies = implementation.getRateLimitingPolicies();
		long soggInt = soggettiCore.getIdSoggetto(idServizio2.getSoggettoErogatore().getNome(), idServizio2.getSoggettoErogatore().getTipo());
		portaApplicativa.setIdSoggetto((long) soggInt);
		
		Connettore connettore = null;
		if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
			// Connettore
			connettore = new Connettore();
			// this.nomeservizio);
			if (endpointtype.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM))
				connettore.setTipo(tipoconn);
			else
				connettore.setTipo(endpointtype);

			apsHelper.fillConnettore(connettore, connettoreDebug, endpointtype, endpointtype, tipoconn, url,
					nomeCodaJms, tipoJms, user, password,
					initcont, urlpgk, provurl, connfact,
					tipoSendas, httpsurl, httpstipologia, httpshostverify, 
					httpsTrustVerifyCert, httpspath, httpstipo,
					httpspwd, httpsalgoritmo, httpsstato,
					httpskeystore, httpspwdprivatekeytrust,
					httpspathkey, httpstipokey,
					httpspwdkey, httpspwdprivatekey,
					httpsalgoritmokey,
					httpsKeyAlias, httpsTrustStoreCRLs,
					proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
					tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
					opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
					requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
					tokenPolicy,
					listExtendedConnettore);
		}
		boolean addSpecSicurezza = false;
		
		if(!modeCreazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODO_CREAZIONE_EREDITA)) {
			// nuova porta applicativa
						
			//String nomeServizioApplicativoErogatore = portaApplicativa.getServizioApplicativo(0).getNome();
			String nomeServizioApplicativoErogatore = portaApplicativaDefault.getServizioApplicativo(0).getNome();
//			String nomeServizioApplicativoDefault = portaApplicativa.getServizioApplicativoDefault();
			
			if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
								
				nomeServizioApplicativoErogatore = portaApplicativa.getNome();
				
				ServizioApplicativo sa = new ServizioApplicativo();
				sa.setNome(nomeServizioApplicativoErogatore);
				sa.setTipologiaFruizione(TipologiaFruizione.DISABILITATO.getValue());
				sa.setTipologiaErogazione(TipologiaErogazione.TRASPARENTE.getValue());
				sa.setIdSoggetto((long) soggInt);
				sa.setTipoSoggettoProprietario(portaApplicativa.getTipoSoggettoProprietario());
				sa.setNomeSoggettoProprietario(portaApplicativa.getNomeSoggettoProprietario());
				
				RispostaAsincrona rispostaAsinc = new RispostaAsincrona();
				rispostaAsinc.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
				rispostaAsinc.setGetMessage(CostantiConfigurazione.DISABILITATO);
				sa.setRispostaAsincrona(rispostaAsinc);
				
				InvocazioneServizio invServizio = new InvocazioneServizio();
				if(ServletUtils.isCheckBoxEnabled(autenticazioneHttp)) {
					invServizio.setAutenticazione(InvocazioneServizioTipoAutenticazione.BASIC);
					InvocazioneCredenziali invCredenziali = new InvocazioneCredenziali();
					invCredenziali.setUser(user);
					invCredenziali.setPassword(password);
					invServizio.setCredenziali(invCredenziali);
				}
				else {
					invServizio.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
				}
				invServizio.setGetMessage(CostantiConfigurazione.DISABILITATO);
				invServizio.setConnettore(connettore.mappingIntoConnettoreConfigurazione());
				if (endpointtype.equals(TipiConnettore.JMS.getNome())) {
					if(user!=null && !"".equals(user) &&
							password!=null && !"".equals(password)) {
						invServizio.setAutenticazione(InvocazioneServizioTipoAutenticazione.BASIC);
						InvocazioneCredenziali invCredenziali = new InvocazioneCredenziali();
						invCredenziali.setUser(user);
						invCredenziali.setPassword(password);
						invServizio.setCredenziali(invCredenziali);
					}
				}
				sa.setInvocazioneServizio(invServizio);
				
				listaOggettiDaCreare.add(sa);
			}
			
			// Scelto un servizio applicativo server, creo il servizio di default e poi associo quello server
			if(StringUtils.isNotEmpty(nomeSAServer)) {
				portaApplicativa.setServizioApplicativoDefault(nomeServizioApplicativoErogatore);
				nomeServizioApplicativoErogatore = nomeSAServer;
			}
			
			IDSoggetto idSoggettoAutenticatoErogazione = null;
			if(erogazioneSoggettoAutenticato != null && !"".equals(erogazioneSoggettoAutenticato) && !"-".equals(erogazioneSoggettoAutenticato)) {
				String [] splitSoggetto = erogazioneSoggettoAutenticato.split("/");
				if(splitSoggetto != null) {
					idSoggettoAutenticatoErogazione = new IDSoggetto();
					if(splitSoggetto.length == 2) {
						idSoggettoAutenticatoErogazione.setTipo(splitSoggetto[0]);
						idSoggettoAutenticatoErogazione.setNome(splitSoggetto[1]);
					} else {
						idSoggettoAutenticatoErogazione.setNome(splitSoggetto[0]);
					}
				}
			}
			
			
			
			porteApplicativeCore.configureControlloAccessiPortaApplicativa(portaApplicativa,
					erogazioneAutenticazione, erogazioneAutenticazioneOpzionale, erogazioneAutenticazionePrincipal, erogazioneAutenticazioneParametroList,
					erogazioneAutorizzazione, erogazioneAutorizzazioneAutenticati, erogazioneAutorizzazioneRuoli, erogazioneAutorizzazioneRuoliTipologia, erogazioneAutorizzazioneRuoliMatch,
					nomeServizioApplicativoErogatore, erogazioneRuolo,idSoggettoAutenticatoErogazione,
					autorizzazione_tokenOptions,
					autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy);
			
			porteApplicativeCore.configureControlloAccessiGestioneToken(portaApplicativa, gestioneToken, 
					gestioneTokenPolicy, gestioneTokenOpzionale,
					gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward,
					autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
					autorizzazione_tokenOptions);
		}
		else {
			// clona porta applicativa
			
			portaApplicativa.getServizioApplicativoList().clear();
			
			org.openspcoop2.core.config.Connettore connettorePDClonato = null;
			InvocazioneServizioTipoAutenticazione tipoAutenticazioneClonata = null;
			InvocazioneCredenziali invocazioneCredenzialiClonata = null;
			if(!ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore) && portaApplicativaDaCopiare!=null && !mappingSelezionato.isDefault()) {
				PortaApplicativaServizioApplicativo portaApplicativaDaCopiareServizioApplicativo = portaApplicativaDaCopiare.getServizioApplicativoList().get(0);
				if(portaApplicativaDaCopiareServizioApplicativo.getNome().equals(portaApplicativaDaCopiare.getNome())) { 
					// ridefinito
					IDServizioApplicativo idSA = new IDServizioApplicativo();
					idSA.setNome(portaApplicativaDaCopiareServizioApplicativo.getNome());
					idSA.setIdSoggettoProprietario(new IDSoggetto(portaApplicativaDaCopiare.getTipoSoggettoProprietario(), portaApplicativaDaCopiare.getNomeSoggettoProprietario()));
					ServizioApplicativo saDaCopiare = saCore.getServizioApplicativo(idSA);
					connettorePDClonato = (org.openspcoop2.core.config.Connettore) saDaCopiare.getInvocazioneServizio().getConnettore().clone();
					if(saDaCopiare.getInvocazioneServizio().getAutenticazione()!=null) {
						tipoAutenticazioneClonata = saDaCopiare.getInvocazioneServizio().getAutenticazione();
					}
					if(saDaCopiare.getInvocazioneServizio().getCredenziali()!=null) {
						invocazioneCredenzialiClonata = (InvocazioneCredenziali) saDaCopiare.getInvocazioneServizio().getCredenziali().clone();
					}
				}
			}
			
			if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore) || (connettorePDClonato!=null)) {
				PortaApplicativa portaApplicativaSelezionata = porteApplicativeCore.getPortaApplicativa(mappingSelezionato.getIdPortaApplicativa());
				
				// porta applicativa clonata, ridefinisco solo il connettore default e non gli eventuali server
				for (PortaApplicativaServizioApplicativo paSADefault : portaApplicativaSelezionata.getServizioApplicativoList()) {
					IDServizioApplicativo idServizioApplicativoDefault = new IDServizioApplicativo();
					idServizioApplicativoDefault.setNome(paSADefault.getNome());
					idServizioApplicativoDefault.setIdSoggettoProprietario(new IDSoggetto(portaApplicativaSelezionata.getTipoSoggettoProprietario(), portaApplicativaSelezionata.getNomeSoggettoProprietario()));
					ServizioApplicativo saDefault = saCore.getServizioApplicativo(idServizioApplicativoDefault);
					
					// clona e modifica connettore
					ServizioApplicativo sa = (ServizioApplicativo) saDefault.clone();
					sa.setNome(portaApplicativa.getNome());
					if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
						if(ServletUtils.isCheckBoxEnabled(autenticazioneHttp)) {
							sa.getInvocazioneServizio().setAutenticazione(InvocazioneServizioTipoAutenticazione.BASIC);
							InvocazioneCredenziali invCredenziali = new InvocazioneCredenziali();
							invCredenziali.setUser(user);
							invCredenziali.setPassword(password);
							sa.getInvocazioneServizio().setCredenziali(invCredenziali);
						}
						else {
							sa.getInvocazioneServizio().setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
							sa.getInvocazioneServizio().setCredenziali(null);
						}
						if (endpointtype.equals(TipiConnettore.JMS.getNome())) {
							if(user!=null && !"".equals(user) &&
									password!=null && !"".equals(password)) {
								sa.getInvocazioneServizio().setAutenticazione(InvocazioneServizioTipoAutenticazione.BASIC);
								InvocazioneCredenziali invCredenziali = new InvocazioneCredenziali();
								invCredenziali.setUser(user);
								invCredenziali.setPassword(password);
								sa.getInvocazioneServizio().setCredenziali(invCredenziali);
							}
						}
					}
					else {
						sa.getInvocazioneServizio().setAutenticazione(tipoAutenticazioneClonata);
						sa.getInvocazioneServizio().setCredenziali(invocazioneCredenzialiClonata);
					}
					if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
						sa.getInvocazioneServizio().setConnettore(connettore.mappingIntoConnettoreConfigurazione());
						
						// elimino eventuale configurazione I.M. presente sulla configurazione clonata
						sa.getInvocazioneServizio().setGetMessage(StatoFunzionalita.DISABILITATO);
						sa.setInvocazionePorta(new InvocazionePorta());
					}
					else {
						sa.getInvocazioneServizio().setConnettore(connettorePDClonato);
					}
					sa.setTipo(null);
					
					listaOggettiDaCreare.add(sa);
					
					PortaApplicativaServizioApplicativo paSa = new PortaApplicativaServizioApplicativo();
					paSa.setNome(sa.getNome());
					portaApplicativa.getServizioApplicativoList().add(paSa);
				}
					
				// controllo se ho ridefinito un connettore e il mapping clonato aveva un SA di tipo Server
				if(portaApplicativaSelezionata.getServizioApplicativoDefault() != null) {
					// 1. ho selezionato un server differente
					if(StringUtils.isNotEmpty(nomeSAServer)) {
						
						PortaApplicativaServizioApplicativo paSAtmp = null;
						for (PortaApplicativaServizioApplicativo paSADefault : portaApplicativa.getServizioApplicativoList()) {
							if(paSADefault.getNome().equals(portaApplicativa.getNome())) {
								paSAtmp = paSADefault;
								break;
							}
						}
						if(paSAtmp!= null) {
							portaApplicativa.getServizioApplicativoList().remove(paSAtmp); 	
							portaApplicativa.setServizioApplicativoDefault(paSAtmp.getNome());
						}
						
						PortaApplicativaServizioApplicativo paSa = new PortaApplicativaServizioApplicativo();
						paSa.setNome(nomeSAServer);
						portaApplicativa.getServizioApplicativoList().add(paSa);
						
					}else {
						// 	oppure ho ridefinito un connettore non server
						PortaApplicativaServizioApplicativo paSAtmp = null;
						for (PortaApplicativaServizioApplicativo paSADefault : portaApplicativa.getServizioApplicativoList()) {
							if(paSADefault.getNome().equals(portaApplicativa.getNome())) {
								paSAtmp = paSADefault;
								break;
							}
						}
						if(paSAtmp!= null) {
							portaApplicativa.getServizioApplicativoList().remove(paSAtmp); 	
						}
						
						PortaApplicativaServizioApplicativo paSa = new PortaApplicativaServizioApplicativo();
						paSa.setNome(portaApplicativa.getNome());
						portaApplicativa.getServizioApplicativoList().add(paSa);
						portaApplicativa.setServizioApplicativoDefault(null);
					}
				}else {
					if(StringUtils.isNotEmpty(nomeSAServer)) {
						// aggiorno il default
						PortaApplicativaServizioApplicativo paSAtmp = null;
						for (PortaApplicativaServizioApplicativo paSADefault : portaApplicativa.getServizioApplicativoList()) {
							if(paSADefault.getNome().equals(portaApplicativa.getNome())) {
								paSAtmp = paSADefault;
								break;
							}
						}
						
						if(paSAtmp!= null) {
							// SA di default da conservare
							portaApplicativa.getServizioApplicativoList().remove(paSAtmp);
							portaApplicativa.setServizioApplicativoDefault(paSAtmp.getNome());
							
							// nuovo SA da aggiungere
							PortaApplicativaServizioApplicativo paSa = new PortaApplicativaServizioApplicativo();
							paSa.setNome(nomeSAServer);
							portaApplicativa.getServizioApplicativoList().add(paSa);
						}
					}
				}
			}
			else {
				// se ho clonato una porta applicativa multi connettore devo clonare tutti i conettori associati
				if(portaApplicativaDaCopiare.getBehaviour() != null) {
					for (PortaApplicativaServizioApplicativo paSADefault : portaApplicativaDaCopiare.getServizioApplicativoList()) {
						PortaApplicativaServizioApplicativo paSa = new PortaApplicativaServizioApplicativo();
						paSa.setDatiConnettore(paSADefault.getDatiConnettore());
						paSa.setNome(paSADefault.getNome());
						
						IDServizioApplicativo idServizioApplicativoDefault = new IDServizioApplicativo();
						idServizioApplicativoDefault.setNome(paSADefault.getNome());
						idServizioApplicativoDefault.setIdSoggettoProprietario(new IDSoggetto(portaApplicativaDaCopiare.getTipoSoggettoProprietario(), portaApplicativaDaCopiare.getNomeSoggettoProprietario()));
						ServizioApplicativo saDefault = saCore.getServizioApplicativo(idServizioApplicativoDefault);
						if(!ServiziApplicativiCostanti.VALUE_SERVIZI_APPLICATIVI_TIPO_SERVER.equals(saDefault.getTipo())) {
							ServizioApplicativo sa = (ServizioApplicativo) saDefault.clone();
							sa.setNome(portaApplicativa.getNome());
							if(!apsHelper.isConnettoreDefault(paSa)) {
								String nuovoNomeSA = portaApplicativa.getNome() + PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_SAX_PREFIX + 
										apsHelper.getIdxNuovoConnettoreMultiplo(portaApplicativa);
								sa.setNome(nuovoNomeSA);
							} 
							paSa.setNome(sa.getNome());
							
							listaOggettiDaCreare.add(sa);
						} 
						portaApplicativa.getServizioApplicativoList().add(paSa);
					}
				} else {
					// assegno sempre il connettore della pa di default in caso di eredita'
					for (PortaApplicativaServizioApplicativo paSADefault : portaApplicativaDefault.getServizioApplicativoList()) {
						PortaApplicativaServizioApplicativo paSa = new PortaApplicativaServizioApplicativo();
						paSa.setNome(paSADefault.getNome());
						portaApplicativa.getServizioApplicativoList().add(paSa);
					}
				}
			}
		}
		
		listaOggettiDaCreare.add(portaApplicativa);
		listaOggettiDaCreare.add(mappingErogazione);
		if(rateLimitingPolicies!=null && !rateLimitingPolicies.isEmpty()) {
			for (AttivazionePolicy attivazionePolicy : rateLimitingPolicies) {
				listaOggettiDaCreare.add(attivazionePolicy);
			}
		}
		

		porteApplicativeCore.performCreateOperation(userLogin, apsHelper.smista(), listaOggettiDaCreare.toArray());
		if(addSpecSicurezza) {
			porteApplicativeCore.performUpdateOperation(userLogin, apsHelper.smista(), asps);
		}
	}
	
	public static final MappingFruizionePortaDelegata getDefaultMappingPD(List<MappingFruizionePortaDelegata> listaMappingFruizione) {
		return listaMappingFruizione.stream().filter( m -> m.isDefault()).findFirst().orElse(null);
	}
	
	public static final MappingFruizionePortaDelegata getMappingPD(List<MappingFruizionePortaDelegata> listaMappingFruizione, String mappingPD) {
		return listaMappingFruizione.stream().filter( m -> m.getNome().equals(mappingPD)).findFirst().orElse(null);
	}
	
	public static final MappingFruizionePortaDelegata getMappingPD_filterByDescription(List<MappingFruizionePortaDelegata> listaMappingFruizione, String descrizione) {
		return listaMappingFruizione.stream().filter( m -> m.getDescrizione().equals(descrizione)).findFirst().orElse(null);
	}
	
	public static AccordiServizioParteSpecificaFruitoriPorteDelegateMappingInfo getMappingInfo(String mappingPD, 
			IDSoggetto idSoggettoFruitore, AccordoServizioParteSpecifica asps, 
			AccordiServizioParteSpecificaCore apsCore) throws Exception {
		
		PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
		
		IDServizio idServizio2 = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
		
		long idFru = apsCore.getIdFruizioneAccordoServizioParteSpecifica(idSoggettoFruitore, idServizio2);
				
		List<MappingFruizionePortaDelegata> listaMappingFruizione = apsCore.serviziFruitoriMappingList(idFru, idSoggettoFruitore, idServizio2, null);
		
		MappingFruizionePortaDelegata mappingSelezionato = null, mappingDefault = null;
		
		String mappingLabel = "";
		String[] listaMappingLabels = null;
		String[] listaMappingValues = null;
		List<String> azioniOccupate = new ArrayList<>();
		String nomeNuovaConfigurazione = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING_FRUIZIONE_PD_AZIONE_SPECIFIC_PREFIX + "1";
		int idxConfigurazione = 0;
		int listaMappingFruizioneSize = listaMappingFruizione != null ? listaMappingFruizione.size() : 0;
		if(listaMappingFruizioneSize > 0) {
			
			mappingDefault = getDefaultMappingPD(listaMappingFruizione);
			
			if(mappingPD != null) {
				mappingSelezionato = getMappingPD(listaMappingFruizione, mappingPD);
			}

			if(mappingSelezionato == null) {
				mappingSelezionato = mappingDefault;
			}
			
			if(!mappingSelezionato.isDefault()) {
				PortaDelegata pdMapping = porteDelegateCore.getPortaDelegata(mappingSelezionato.getIdPortaDelegata());
				mappingLabel = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(null,null,pdMapping,Integer.MAX_VALUE);
			}

			listaMappingLabels = new String[listaMappingFruizioneSize];
			listaMappingValues = new String[listaMappingFruizioneSize];
			for (int i = 0; i < listaMappingFruizione.size(); i++) {
				MappingFruizionePortaDelegata mappingFruizionePortaDelegata = listaMappingFruizione.get(i);
				//String nomeMappingNoDefault = mappingFruizionePortaDelegata.getNome();
				String nomeMappingNoDefault = null;
				//if(!mappingFruizionePortaDelegata.isDefault()) {
				PortaDelegata pdMapping = porteDelegateCore.getPortaDelegata(mappingFruizionePortaDelegata.getIdPortaDelegata());
				nomeMappingNoDefault = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(null,null,pdMapping,70,true);
				//}
//				listaMappingLabels[i] = mappingFruizionePortaDelegata.isDefault()? 
//						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING_FRUIZIONE_PD_NOME_DEFAULT: nomeMappingNoDefault;
				listaMappingLabels[i] = nomeMappingNoDefault;
				listaMappingValues[i] = mappingFruizionePortaDelegata.getNome();
				
				// calcolo del nome automatico
				if(!mappingFruizionePortaDelegata.isDefault())  {
					int idx = mappingFruizionePortaDelegata.getNome().indexOf(PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING_FRUIZIONE_PD_AZIONE_SPECIFIC_PREFIX);
					if(idx > -1) {
						String idxTmp = mappingFruizionePortaDelegata.getNome().substring(idx + PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING_FRUIZIONE_PD_AZIONE_SPECIFIC_PREFIX.length());
						int idxMax = -1;
						try {
							idxMax = Integer.parseInt(idxTmp);
						}catch(Exception e) {
							idxMax = 0;
						}
						idxConfigurazione = Math.max(idxConfigurazione, idxMax);
					}
				}
				
				// colleziono le azioni gia' configurate
				PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(mappingFruizionePortaDelegata.getIdPortaDelegata());
				if(portaDelegata.getAzione() != null && portaDelegata.getAzione().getAzioneDelegataList() != null)
					azioniOccupate.addAll(portaDelegata.getAzione().getAzioneDelegataList());
			}
		}
		
		nomeNuovaConfigurazione = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_MAPPING_FRUIZIONE_PD_AZIONE_SPECIFIC_PREFIX + (++ idxConfigurazione);

		
		AccordiServizioParteSpecificaFruitoriPorteDelegateMappingInfo info = new AccordiServizioParteSpecificaFruitoriPorteDelegateMappingInfo();
		info.setListaMappingFruizione(listaMappingFruizione);
		info.setMappingSelezionato(mappingSelezionato);
		info.setMappingDefault(mappingDefault);
		info.setMappingLabel(mappingLabel);
		info.setListaMappingLabels(listaMappingLabels);
		info.setListaMappingValues(listaMappingValues);
		info.setAzioniOccupate(azioniOccupate);
		info.setNomeNuovaConfigurazione(nomeNuovaConfigurazione);
		return info;
	}
	
	public static void addAccordoServizioParteSpecificaPorteDelegate(
			MappingFruizionePortaDelegata mappingDefault,
			MappingFruizionePortaDelegata mappingSelezionato,
			String nome, String nomeGruppo, String[] azioni, String modeCreazione, String modeCreazioneConnettore,
			String endpointtype, String tipoconn, String autenticazioneHttp,
			String connettoreDebug,
			String url,
			String nomeCodaJms, String tipoJms, 
			String initcont, String urlpgk, String provurl, String connfact, String tipoSendas, 
			String user, String password,
			String httpsurl, String httpstipologia, boolean httpshostverify,
			boolean httpsTrustVerifyCert, String httpspath, String httpstipo, String httpspwd,
			String httpsalgoritmo, boolean httpsstato, String httpskeystore,
			String httpspwdprivatekeytrust, String httpspathkey,
			String httpstipokey, String httpspwdkey,
			String httpspwdprivatekey, String httpsalgoritmokey,
			String httpsKeyAlias, String httpsTrustStoreCRLs,
			String proxy_enabled, String proxy_hostname, String proxy_port, String proxy_username, String proxy_password,
			String tempiRisposta_enabled, String tempiRisposta_connectionTimeout, String tempiRisposta_readTimeout, String tempiRisposta_tempoMedioRisposta,
			String opzioniAvanzate, String transfer_mode, String transfer_mode_chunk_size, String redirect_mode, String redirect_max_hop,
			String requestOutputFileName,String requestOutputFileNameHeaders,String requestOutputParentDirCreateIfNotExists,String requestOutputOverwriteIfExists,
			String responseInputMode, String responseInputFileName, String responseInputFileNameHeaders, String responseInputDeleteAfterRead, String responseInputWaitTime,
			boolean autenticazioneToken, String tokenPolicy,
			List<ExtendedConnettore> listExtendedConnettore,
			String fruizioneAutenticazione, String fruizioneAutenticazioneOpzionale, TipoAutenticazionePrincipal fruizioneAutenticazionePrincipal, List<String> fruizioneAutenticazioneParametroList,
			String fruizioneAutorizzazione, String fruizioneAutorizzazioneAutenticati, String fruizioneAutorizzazioneRuoli, String fruizioneAutorizzazioneRuoliTipologia, String fruizioneAutorizzazioneRuoliMatch,
			String fruizioneServizioApplicativo, String fruizioneRuolo, 
			String autorizzazione_tokenOptions,
			String autorizzazioneScope, String scope, String autorizzazioneScopeMatch,BinaryParameter allegatoXacmlPolicy,
			String gestioneToken, 
			String gestioneTokenPolicy,  String gestioneTokenOpzionale,  
			String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autenticazioneTokenIssuer, String autenticazioneTokenClientId, String autenticazioneTokenSubject, String autenticazioneTokenUsername, String autenticazioneTokenEMail,
			IDSoggetto idSoggettoFruitore, AccordoServizioParteSpecifica asps, 
			String userLogin,
			AccordiServizioParteSpecificaCore apsCore, AccordiServizioParteSpecificaHelper apsHelper) throws Exception {
	
		PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
		AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(apsCore);
		SoggettiCore soggettiCore = new SoggettiCore(apcCore);
		
		IDServizio idServizio2 = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps); 
	
		String tipoSoggettoFruitore = idSoggettoFruitore.getTipo();
		String nomeSoggettoFruitore = idSoggettoFruitore.getNome();
		
		AccordoServizioParteComuneSintetico as = null;
		ServiceBinding serviceBinding = null;
		if (asps != null) {
			IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
			as = apcCore.getAccordoServizioSintetico(idAccordo);
			serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
		}
	
		List<Object> listaOggettiDaCreare = new ArrayList<Object>();
		List<Object> listaOggettiDaModificare = new ArrayList<Object>();

		PortaDelegata portaDelegataDefault = porteDelegateCore.getPortaDelegata(mappingDefault.getIdPortaDelegata());
		String protocollo = apsCore.getProtocolloAssociatoTipoServizio(idServizio2.getTipo());
		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
	
		Connettore connettore = null;
		if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
			connettore = new Connettore();
			// this.nomeservizio);
			if (endpointtype.equals(ConnettoriCostanti.DEFAULT_CONNETTORE_TYPE_CUSTOM))
				connettore.setTipo(tipoconn);
			else
				connettore.setTipo(endpointtype);

			apsHelper.fillConnettore(connettore, connettoreDebug, endpointtype, endpointtype, tipoconn, url,
					nomeCodaJms, tipoJms, user, password,
					initcont, urlpgk, provurl, connfact,
					tipoSendas, httpsurl, httpstipologia, httpshostverify, 
					httpsTrustVerifyCert, httpspath, httpstipo,
					httpspwd, httpsalgoritmo, httpsstato,
					httpskeystore, httpspwdprivatekeytrust,
					httpspathkey, httpstipokey,
					httpspwdkey, httpspwdprivatekey,
					httpsalgoritmokey,
					httpsKeyAlias, httpsTrustStoreCRLs,
					proxy_enabled, proxy_hostname, proxy_port, proxy_username, proxy_password,
					tempiRisposta_enabled, tempiRisposta_connectionTimeout, tempiRisposta_readTimeout, tempiRisposta_tempoMedioRisposta,
					opzioniAvanzate, transfer_mode, transfer_mode_chunk_size, redirect_mode, redirect_max_hop,
					requestOutputFileName,requestOutputFileNameHeaders,requestOutputParentDirCreateIfNotExists,requestOutputOverwriteIfExists,
					responseInputMode, responseInputFileName, responseInputFileNameHeaders, responseInputDeleteAfterRead, responseInputWaitTime,
					tokenPolicy,
					listExtendedConnettore);
		}
		
		
		IConfigIntegrationReader configIntegrationReader = apsCore.getConfigIntegrationReader(protocolFactory);
		
		Subscription subscription = null;
		PortaDelegata portaDelegataDaCopiare = null;
		if(modeCreazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODO_CREAZIONE_EREDITA)) {
			portaDelegataDaCopiare = porteDelegateCore.getPortaDelegata(mappingSelezionato.getIdPortaDelegata());
			subscription = protocolFactory.createProtocolIntegrationConfiguration().createSubscription(configIntegrationReader, serviceBinding, idSoggettoFruitore, idServizio2, 
					portaDelegataDefault, portaDelegataDaCopiare, nome, nomeGruppo, azioni);
		}
		else {
			subscription = protocolFactory.createProtocolIntegrationConfiguration().createSubscription(configIntegrationReader, serviceBinding, idSoggettoFruitore, idServizio2, 
					portaDelegataDefault, nome, nomeGruppo, azioni);
			
		}
		
		boolean clonatoDaPDConConnettoreRidefinito = false;
		if(!ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore) && portaDelegataDaCopiare!=null) {
			if(portaDelegataDaCopiare.getAzione()!=null && PortaDelegataAzioneIdentificazione.DELEGATED_BY.equals(portaDelegataDaCopiare.getAzione().getIdentificazione())) {
				// devo clonare il connettore
				String azioneConnettoreDaPortaDelegataDaClonare = null; // prendo la prima
				if(portaDelegataDaCopiare.getAzione().sizeAzioneDelegataList()>0) {
					azioneConnettoreDaPortaDelegataDaClonare = portaDelegataDaCopiare.getAzione().getAzioneDelegata(0);
				}
				Connettore connettorePortaDelegataDaClonare = null;
				Fruitore fruitore = null;
				if(azioneConnettoreDaPortaDelegataDaClonare!=null) {
					
					for (Fruitore fruitoreCheck : asps.getFruitoreList()) {
						if(fruitoreCheck.getTipo().equals(tipoSoggettoFruitore) && fruitoreCheck.getNome().equals(nomeSoggettoFruitore)) {
							fruitore = fruitoreCheck;
							break;
						}
					}
					if(fruitore!=null) {
						for (ConfigurazioneServizioAzione check : fruitore.getConfigurazioneAzioneList()) {
							if(check.getAzioneList().contains(azioneConnettoreDaPortaDelegataDaClonare)) {
								connettorePortaDelegataDaClonare = check.getConnettore();
								break;
							}
						}
					}
				}
				if(connettorePortaDelegataDaClonare!=null) {
					clonatoDaPDConConnettoreRidefinito = true;
					
					Connettore newConnettoreRidefinito = (Connettore) connettorePortaDelegataDaClonare.clone();
					ConfigurazioneServizioAzione configurazioneAzione = new ConfigurazioneServizioAzione();
					configurazioneAzione.setConnettore(newConnettoreRidefinito);
					for (int i = 0; i < azioni.length; i++) {
						configurazioneAzione.addAzione(azioni[i]);
					}
					fruitore.addConfigurazioneAzione(configurazioneAzione);
				}
			}
		}
		
		if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore)) {
			
			Fruitore fruitore = null;
			for (Fruitore fruitoreCheck : asps.getFruitoreList()) {
				if(fruitoreCheck.getTipo().equals(tipoSoggettoFruitore) && fruitoreCheck.getNome().equals(nomeSoggettoFruitore)) {
					fruitore = fruitoreCheck;
					break;
				}
			}
			
			ConfigurazioneServizioAzione configurazioneAzione = new ConfigurazioneServizioAzione();
			configurazioneAzione.setConnettore(connettore);
			for (int i = 0; i < azioni.length; i++) {
				configurazioneAzione.addAzione(azioni[i]);
			}
			fruitore.addConfigurazioneAzione(configurazioneAzione);
		}
		
		PortaDelegata portaDelegata = subscription.getPortaDelegata();
		MappingFruizionePortaDelegata mappingFruizione = subscription.getMapping();
		List<AttivazionePolicy> rateLimitingPolicies = subscription.getRateLimitingPolicies();
		long idSoggFru = soggettiCore.getIdSoggetto(nomeSoggettoFruitore, tipoSoggettoFruitore);
		portaDelegata.setIdSoggetto((long) idSoggFru);

		if(!modeCreazione.equals(PorteDelegateCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_DELEGATE_MODO_CREAZIONE_EREDITA)) {
			porteDelegateCore.configureControlloAccessiPortaDelegata(portaDelegata, 
					fruizioneAutenticazione, fruizioneAutenticazioneOpzionale, fruizioneAutenticazionePrincipal, fruizioneAutenticazioneParametroList,
					fruizioneAutorizzazione, fruizioneAutorizzazioneAutenticati, fruizioneAutorizzazioneRuoli, fruizioneAutorizzazioneRuoliTipologia, fruizioneAutorizzazioneRuoliMatch,
					fruizioneServizioApplicativo, fruizioneRuolo,
					autorizzazione_tokenOptions,
					autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy);
			
			porteDelegateCore.configureControlloAccessiGestioneToken(portaDelegata, gestioneToken, 
					gestioneTokenPolicy, gestioneTokenOpzionale,
					gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward,
					autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
					autorizzazione_tokenOptions
					);
		}
		
		listaOggettiDaCreare.add(portaDelegata);
		listaOggettiDaCreare.add(mappingFruizione);
		if(rateLimitingPolicies!=null && !rateLimitingPolicies.isEmpty()) {
			for (AttivazionePolicy attivazionePolicy : rateLimitingPolicies) {
				listaOggettiDaCreare.add(attivazionePolicy);
			}
		}

		porteDelegateCore.performCreateOperation(userLogin, apsHelper.smista(), listaOggettiDaCreare.toArray());

		if(ServletUtils.isCheckBoxEnabled(modeCreazioneConnettore) || clonatoDaPDConConnettoreRidefinito) {
			listaOggettiDaModificare.add(asps);
		}
		
		if(listaOggettiDaModificare.size()>0) {
			porteDelegateCore.performUpdateOperation(userLogin, apsHelper.smista(), listaOggettiDaModificare.toArray());
		}
	}
	
	public static void deleteAccordoServizioParteSpecificaAllegati(AccordoServizioParteSpecifica asps, String userLogin, 
			AccordiServizioParteSpecificaCore apsCore, AccordiServizioParteSpecificaHelper apsHelper, 
			List<Long> idAllegati) throws Exception {
		
		ArchiviCore archiviCore = new ArchiviCore(apsCore);
		
		for (int i = 0; i < idAllegati.size(); i++) {
			long idAllegato = idAllegati.get(i);
		
			Documento doc = archiviCore.getDocumento(idAllegato, false);
			
			switch (RuoliDocumento.valueOf(doc.getRuolo())) {
			case allegato:
				//rimuovo il vecchio doc dalla lista
				for (int j = 0; j < asps.sizeAllegatoList(); j++) {
					Documento documento = asps.getAllegato(j);						
					if(documento.getFile().equals(doc.getFile())){
						asps.removeAllegato(j);
						break;
					}
				}
	
				break;
	
			case specificaSemiformale:
	
				for (int j = 0; j < asps.sizeSpecificaSemiformaleList(); j++) {
					Documento documento = asps.getSpecificaSemiformale(j);						
					if(documento.getFile().equals(doc.getFile())){
						asps.removeSpecificaSemiformale(j);
						break;
					}
				}
				break;
				
			case specificaCoordinamento:
				break;
				
			case specificaSicurezza:
				for (int j = 0; j < asps.sizeSpecificaSicurezzaList(); j++) {
					Documento documento = asps.getSpecificaSicurezza(j);						
					if(documento.getFile().equals(doc.getFile())){
						asps.removeSpecificaSicurezza(j);
						break;
					}
				}
				break;
	
			case specificaLivelloServizio:
				for (int j = 0; j < asps.sizeSpecificaLivelloServizioList(); j++) {
					Documento documento = asps.getSpecificaLivelloServizio(j);						
					if(documento.getFile().equals(doc.getFile())){
						asps.removeSpecificaLivelloServizio(j);
						break;
					}
				}
				break;
			}

		}
		
		// effettuo le operazioni
		apsCore.performUpdateOperation(userLogin, apsHelper.smista(), asps);
	}

	public static void sostituisciDocumentoAsps(AccordoServizioParteSpecifica asps, Documento doc, Documento toCheck) {
		switch (RuoliDocumento.valueOf(doc.getRuolo())) {
		case allegato:
			//rimuovo il vecchio doc dalla lista
			for (int i = 0; i < asps.sizeAllegatoList(); i++) {
				Documento documento = asps.getAllegato(i);						
				if(documento.getId().equals(doc.getId())){
					asps.removeAllegato(i);
					break;
				}
			}
			//aggiungo il nuovo
			asps.addAllegato(toCheck);
	
			break;
	
		case specificaSemiformale:
	
			for (int i = 0; i < asps.sizeSpecificaSemiformaleList(); i++) {
				Documento documento = asps.getSpecificaSemiformale(i);						
				if(documento.getId().equals(doc.getId())){
					asps.removeSpecificaSemiformale(i);
					break;
				}
			}
			//aggiungo il nuovo
			asps.addSpecificaSemiformale(toCheck);
			break;
		case specificaSicurezza:
			for (int i = 0; i < asps.sizeSpecificaSicurezzaList(); i++) {
				Documento documento = asps.getSpecificaSicurezza(i);						
				if(documento.getId().equals(doc.getId())){
					asps.removeSpecificaSicurezza(i);
					break;
				}
			}
			//aggiungo il nuovo
			asps.addSpecificaSicurezza(toCheck);
			break;
		case specificaLivelloServizio:
			for (int i = 0; i < asps.sizeSpecificaLivelloServizioList(); i++) {
				Documento documento = asps.getSpecificaLivelloServizio(i);						
				if(documento.getId().equals(doc.getId())){
					asps.removeSpecificaLivelloServizio(i);
					break;
				}
			}
			//aggiungo il nuovo
			asps.addSpecificaLivelloServizio(toCheck);
			break;
		case specificaCoordinamento:
			// non supportato
			break;
		}
	}
	
	public static boolean alreadyExists(AccordiServizioParteSpecificaCore apsCore, AccordiServizioParteSpecificaHelper apsHelper, 
			long idSoggettoErogatore, IDServizio idAccordoServizioParteSpecifica, String uriAccordoServizioParteComune,
			String tipoFruitore, String nomeFruitore,
			String protocollo, String profilo, String portType,
			boolean gestioneFruitori, boolean gestioneErogatori,
			StringBuilder inUsoMessage) throws Exception {
		
		PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore(apsCore);
		
		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
		
		String tiposervizio = idAccordoServizioParteSpecifica.getTipo();
		String nomeservizio = idAccordoServizioParteSpecifica.getNome();
		Integer versioneInt = idAccordoServizioParteSpecifica.getVersione();
		String tipoErogatore = idAccordoServizioParteSpecifica.getSoggettoErogatore().getTipo();
		String nomeErogatore = idAccordoServizioParteSpecifica.getSoggettoErogatore().getNome();
		
		if (apsCore.existServizio(nomeservizio, tiposervizio, versioneInt, idSoggettoErogatore) > 0) {
			String labelServizio = apsHelper.getLabelNomeServizio(protocollo, tiposervizio, nomeservizio, versioneInt);
			String labelSoggetto = apsHelper.getLabelNomeSoggetto(protocollo, tipoErogatore, nomeErogatore);
			
			AccordoServizioParteSpecifica asps = apsCore.getServizio(idAccordoServizioParteSpecifica, false);
			if(gestioneFruitori || gestioneErogatori) {
				// verifico che l'api indicata sia la stessa dell'api del servizio già esistente
				String uri_apc = asps.getAccordoServizioParteComune();
				if(uriAccordoServizioParteComune.equals(uri_apc)==false) {
					String msg = AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ESISTE_UN_SERVIZIO_CON_IL_TIPO_E_NOME_DEFINITO_EROGATO_DAL_SOGGETTO_CON_API_DIFFERENTE;
					msg = MessageFormat.format(msg,	labelServizio, labelSoggetto, apsHelper.getLabelIdAccordo(idAccordoFactory.getIDAccordoFromUri(uri_apc)));
					inUsoMessage.append(msg);
					return true;
				}
				if (profilo!=null && !"".equals(profilo) && "-".equals(profilo) == false && !profilo.equals(asps.getVersioneProtocollo())) {
					String msg = AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ESISTE_UN_SERVIZIO_CON_IL_TIPO_E_NOME_DEFINITO_EROGATO_DAL_SOGGETTO_CON_VERSIONE_PROTOCOLLO_DIFFERENTE;
					msg = MessageFormat.format(msg,	labelServizio, labelSoggetto, 
							asps.getVersioneProtocollo()==null? AccordiServizioParteSpecificaCostanti.LABEL_APS_USA_VERSIONE_EROGATORE : asps.getVersioneProtocollo());
					inUsoMessage.append(msg);
					return true;
				}
				if (portType != null && !"".equals(portType) && !"-".equals(portType) && !portType.equals(asps.getPortType())) {
					String msg = AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ESISTE_UN_SERVIZIO_CON_IL_TIPO_E_NOME_DEFINITO_EROGATO_DAL_SOGGETTO_CON_PORT_TYPE_DIFFERENTE;
					msg = MessageFormat.format(msg,	labelServizio, labelSoggetto, 
							asps.getPortType()==null? "Nessun Servizio" : asps.getPortType());
					inUsoMessage.append(msg);
					return true;
				}
			}
			
			String msg = null;
			if(gestioneFruitori) {
				
				boolean found = false;
				for (Fruitore fruitore : asps.getFruitoreList()) {
					if(fruitore.getTipo().equals(tipoFruitore) && fruitore.getNome().equals(nomeFruitore)) {
						found = true;
						break;
					}
				}
				
				if(found) {
					String labelSoggettoFruitore = apsHelper.getLabelNomeSoggetto(protocollo, tipoFruitore, nomeFruitore);
					msg = AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ESISTE_UN_SERVIZIO_CON_IL_TIPO_E_NOME_DEFINITO_EROGATO_DAL_SOGGETTO_CON_PARAMETRI_FRUIZIONE;
					msg = MessageFormat.format(msg,	labelSoggettoFruitore, labelServizio, labelSoggetto);
				}
			}
			else if(gestioneErogatori) {
				
				List<IDPortaApplicativa> l = porteApplicativeCore.getIDPorteApplicativeAssociate(idAccordoServizioParteSpecifica);
				if(l!=null && l.size()>0) {
					msg = AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ESISTE_UN_SERVIZIO_CON_IL_TIPO_E_NOME_DEFINITO_EROGATO_DAL_SOGGETTO_CON_PARAMETRI;
					msg = MessageFormat.format(msg,	labelServizio, labelSoggetto);
				}
				
			}
			else {
				msg = AccordiServizioParteSpecificaCostanti.MESSAGGIO_ERRORE_ESISTE_UN_SERVIZIO_CON_IL_TIPO_E_NOME_DEFINITO_EROGATO_DAL_SOGGETTO_CON_PARAMETRI;
				msg = MessageFormat.format(msg,	labelServizio, labelSoggetto);
			}
			if(msg!=null) {
				inUsoMessage.append(msg);
				return true;
			}
		}
		
		return false;
	}
}
