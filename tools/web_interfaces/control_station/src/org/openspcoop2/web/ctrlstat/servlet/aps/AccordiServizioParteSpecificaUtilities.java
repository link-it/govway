/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
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
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
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
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.config.Implementation;
import org.openspcoop2.protocol.sdk.config.Subscription;
import org.openspcoop2.protocol.sdk.constants.ConsoleOperationType;
import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;
import org.openspcoop2.protocol.sdk.properties.ProtocolPropertiesUtils;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedBean;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.plugins.WrapperExtendedBean;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneUtilities;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniHelper;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pd.PorteDelegateCore;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
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
	
	public static List<AccordoServizioParteComune> getListaAPI(String tipoProtocollo, String userLogin, AccordiServizioParteSpecificaCore apsCore, AccordiServizioParteSpecificaHelper apsHelper) throws Exception {
		
		AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(apsCore);
		
		Search searchAccordi = new Search(true);
		searchAccordi.addFilter(Liste.ACCORDI, Filtri.FILTRO_PROTOCOLLO, tipoProtocollo);
		List<AccordoServizioParteComune> listaTmp =  
				AccordiServizioParteComuneUtilities.accordiListFromPermessiUtente(apcCore, userLogin, searchAccordi, 
						getPermessiUtente(apsHelper));
		
		List<AccordoServizioParteComune> listaAPI = null;
		if(apsHelper.isModalitaCompleta()) {
			listaAPI = listaTmp;
		}
		else {
			// filtro accordi senza risorse o senza pt/operation
			listaAPI = new ArrayList<AccordoServizioParteComune>();
			for (AccordoServizioParteComune accordoServizioParteComune : listaTmp) {
				if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(accordoServizioParteComune.getServiceBinding())) {
					if(accordoServizioParteComune.sizeResourceList()>0) {
						listaAPI.add(accordoServizioParteComune);	
					}
				}
				else {
					boolean ptValido = false;
					for (PortType pt : accordoServizioParteComune.getPortTypeList()) {
						if(pt.sizeAzioneList()>0) {
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
	
	public static List<PortType> getListaPortTypes(AccordoServizioParteComune as, AccordiServizioParteSpecificaCore apsCore, AccordiServizioParteSpecificaHelper apsHelper) throws Exception{
		
		AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(apsCore);
		
		List<PortType> portTypesTmp = apcCore.accordiPorttypeList(as.getId().intValue(), new Search(true));
		List<PortType> portTypes = null;
		
		if(apsHelper.isModalitaCompleta()) {
			portTypes = portTypesTmp;
		}
		else {
			// filtro pt senza op
			portTypes = new ArrayList<PortType>();
			for (PortType portType : portTypesTmp) {
				if(portType.sizeAzioneList()>0) {
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
			String autenticazione, String autenticazioneOpzionale,
			String autorizzazione, String autorizzazioneAutenticati, String autorizzazioneRuoli, String autorizzazioneRuoliTipologia, String autorizzazioneRuoliMatch,
			String servizioApplicativo, String ruolo, String soggettoAutenticato, 
			String autorizzazione_tokenOptions,
			String autorizzazioneScope, String scope, String autorizzazioneScopeMatch,BinaryParameter allegatoXacmlPolicy,
			String gestioneToken, 
			String gestioneTokenPolicy,  String gestioneTokenOpzionale,  
			String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autenticazioneTokenIssuer, String autenticazioneTokenClientId, String autenticazioneTokenSubject, String autenticazioneTokenUsername, String autenticazioneTokenEMail,
			ProtocolProperties protocolProperties, ConsoleOperationType consoleOperationType,
			AccordiServizioParteSpecificaCore apsCore, ErogazioniHelper apsHelper) throws Exception {
		
		List<Object> listaOggettiDaCreare = new ArrayList<Object>();
		if(!alreadyExists) {
			listaOggettiDaCreare.add(asps);
		}

		// Creo Porta Applicativa (opzione??)
		if(generaPortaApplicativa){
			
			AccordiServizioParteSpecificaUtilities.generaPortaApplicativa(listaOggettiDaCreare, idServizio,tipoProtocollo, serviceBinding, 
					idProv, connettore, 
					autenticazione, autenticazioneOpzionale,
					autorizzazione, autorizzazioneAutenticati, autorizzazioneRuoli, autorizzazioneRuoliTipologia, autorizzazioneRuoliMatch,
					servizioApplicativo, ruolo,soggettoAutenticato,
					autorizzazione_tokenOptions,
					autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy,
					gestioneToken,
					gestioneTokenPolicy,  gestioneTokenOpzionale,
					gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward,
					autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
					apsCore, apsHelper);
					
		}
		
		if(generaPortaDelegata){
			
			AccordiServizioParteSpecificaUtilities.generaPortaDelegata(listaOggettiDaCreare, 
					idFruitore, idServizio, tipoProtocollo, serviceBinding, 
					idProv, 
					autenticazione, autenticazioneOpzionale,
					autorizzazione, autorizzazioneAutenticati, autorizzazioneRuoli, autorizzazioneRuoliTipologia, autorizzazioneRuoliMatch,
					servizioApplicativo, ruolo,
					autorizzazione_tokenOptions,
					autorizzazioneScope,scope,autorizzazioneScopeMatch,allegatoXacmlPolicy,
					gestioneToken, 
					gestioneTokenPolicy,  gestioneTokenOpzionale,
					gestioneTokenValidazioneInput, gestioneTokenIntrospection, gestioneTokenUserInfo, gestioneTokenForward,
					autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
					apsCore);
			
		}

		//imposto properties custom
		if(!alreadyExists) {
			asps.setProtocolPropertyList(ProtocolPropertiesUtils.toProtocolProperties(protocolProperties, consoleOperationType,null));
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
			String erogazioneAutenticazione, String erogazioneAutenticazioneOpzionale,
			String erogazioneAutorizzazione, String erogazioneAutorizzazioneAutenticati, String erogazioneAutorizzazioneRuoli, String erogazioneAutorizzazioneRuoliTipologia, String erogazioneAutorizzazioneRuoliMatch,
			String nomeSA, String erogazioneRuolo, String erogazioneSoggettoAutenticato, 
			String autorizzazione_tokenOptions,
			String autorizzazioneScope, String scope, String autorizzazioneScopeMatch,BinaryParameter allegatoXacmlPolicy,
			String gestioneToken, 
			String gestioneTokenPolicy,  String gestioneTokenOpzionale,  
			String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autenticazioneTokenIssuer, String autenticazioneTokenClientId, String autenticazioneTokenSubject, String autenticazioneTokenUsername, String autenticazioneTokenEMail,
			AccordiServizioParteSpecificaCore apsCore, ErogazioniHelper apsHelper) throws Exception {
		
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
			invServizio.setAutenticazione(InvocazioneServizioTipoAutenticazione.NONE);
			invServizio.setGetMessage(CostantiConfigurazione.DISABILITATO);
			invServizio.setConnettore(connettore.mappingIntoConnettoreConfigurazione());
			sa.setInvocazioneServizio(invServizio);
			
			listaOggettiDaCreare.add(sa);
		}
			
		porteApplicativeCore.configureControlloAccessiPortaApplicativa(portaApplicativa,
				erogazioneAutenticazione, erogazioneAutenticazioneOpzionale,
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
		
		listaOggettiDaCreare.add(portaApplicativa);						
		listaOggettiDaCreare.add(mappingErogazione);
	
	}
	
	public static void crea() {
		
	}
	
	public static void generaPortaDelegata(List<Object> listaOggettiDaCreare,
			IDSoggetto idFruitore, IDServizio idServizio, String tipoProtocollo, ServiceBinding serviceBinding,
			long idProv, // id del database relativo al soggetto. 
			String fruizioneAutenticazione, String fruizioneAutenticazioneOpzionale,
			String fruizioneAutorizzazione, String fruizioneAutorizzazioneAutenticati, String fruizioneAutorizzazioneRuoli, String fruizioneAutorizzazioneRuoliTipologia, String fruizioneAutorizzazioneRuoliMatch,
			String fruizioneServizioApplicativo, String fruizioneRuolo, 
			String autorizzazione_tokenOptions,
			String autorizzazioneScope, String scope, String autorizzazioneScopeMatch,BinaryParameter allegatoXacmlPolicy,
			String gestioneToken, 
			String gestioneTokenPolicy,  String gestioneTokenOpzionale,  
			String gestioneTokenValidazioneInput, String gestioneTokenIntrospection, String gestioneTokenUserInfo, String gestioneTokenForward,
			String autenticazioneTokenIssuer, String autenticazioneTokenClientId, String autenticazioneTokenSubject, String autenticazioneTokenUsername, String autenticazioneTokenEMail,
			AccordiServizioParteSpecificaCore apsCore) throws Exception {
		
		PorteDelegateCore porteDelegateCore = new PorteDelegateCore(apsCore);
		
		IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(tipoProtocollo);
		
		
		Subscription subscriptionDefault = protocolFactory.createProtocolIntegrationConfiguration().
				createDefaultSubscription(serviceBinding, idFruitore, idServizio);
		
		PortaDelegata portaDelegata = subscriptionDefault.getPortaDelegata();
		MappingFruizionePortaDelegata mappingFruizione = subscriptionDefault.getMapping();
		portaDelegata.setIdSoggetto((long) idProv);

		porteDelegateCore.configureControlloAccessiPortaDelegata(portaDelegata, 
				fruizioneAutenticazione, fruizioneAutenticazioneOpzionale,
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
		
		// Se sono cambiati il tipo o il nome allora devo aggiornare
		// anche le porte delegate e porte applicative
		List<PortaDelegata> listaPD = new ArrayList<PortaDelegata>();
		List<PortaApplicativa> listaPA = new ArrayList<PortaApplicativa>();
		List<ServizioApplicativo> listaPA_SA = new ArrayList<ServizioApplicativo>();

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

				String newLocationSuffix = "/" + asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo() + "_" + asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome() + 
						"/" + asps.getTipo() + "_" + asps.getNome() +
						"/v" + asps.getVersione().intValue();

				for (IDPortaDelegata idPortaDelegata : listIdsPorteDelegate) {
					PortaDelegata tmpPorta = porteDelegateCore.getPortaDelegata(idPortaDelegata);	

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
							String match_caso = ":"+asps.getOldIDServizioForUpdate().getTipo()+"/"+asps.getOldIDServizioForUpdate().getNome()+":"+asps.getOldIDServizioForUpdate().getVersione().intValue();
							if(descrizionePD.endsWith(match_caso)) {
								String replace_caso = ":"+asps.getTipo()+"/"+asps.getNome()+":"+asps.getVersione().intValue();
								descrizionePD = descrizionePD.replace(match_caso, replace_caso);
							}

							// Caso 2: altra subscription
							// Internal Subscription 'Specific1' for gw_ENTE/gw_ErogatoreEsterno/gw_EsempioREST/1
							String tmpMatchCaso2 = "/"+asps.getOldIDServizioForUpdate().getTipo()+"_"+asps.getOldIDServizioForUpdate().getNome()+"/";
							String match_caso2 = tmpMatchCaso2 +"v"+ asps.getOldIDServizioForUpdate().getVersione().intValue();
							String match_caso2_oldWithoutV = tmpMatchCaso2 + asps.getOldIDServizioForUpdate().getVersione().intValue();
							if(descrizionePD.contains(match_caso2)) {
								String replace_caso2 = "/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue();
								descrizionePD = descrizionePD.replace(match_caso2, replace_caso2);
							}
							else if(descrizionePD.contains(match_caso2_oldWithoutV)) {
								String replace_caso2 = "/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue();
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
								// (fruitore)/(erogatore)/(servizio)
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
									String partOld = "(?:"+asps.getOldIDServizioForUpdate().getTipo()+"_)?"+asps.getOldIDServizioForUpdate().getNome()+"";
									String partNew = "(?:"+asps.getTipo()+"_)?"+asps.getNome()+"";

									// vedo se matcha il fruitore
									if (partServizio.equals(partOld)) {
										partServizio = partNew;
										matchURL = true;
									}

									// vedo se matcha anche erogatore (loopback)
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

				String newLocationSuffix = asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo() + "_" + asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome() + 
						"/" + asps.getTipo() + "_" + asps.getNome() +
						"/v" + asps.getVersione().intValue();

				for (IDPortaApplicativa idPortaApplicativa : listIdsPorteApplicative) {
					PortaApplicativa tmpPorta = porteApplicativeCore.getPortaApplicativa(idPortaApplicativa);	

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

							// Caso 1: subscription default
							// Service implementation gw/ENTE:gw/TEST:1
							String match_caso = ":"+asps.getOldIDServizioForUpdate().getTipo()+"/"+asps.getOldIDServizioForUpdate().getNome()+":"+asps.getOldIDServizioForUpdate().getVersione().intValue();
							if(descrizionePA.endsWith(match_caso)) {
								String replace_caso = ":"+asps.getTipo()+"/"+asps.getNome()+":"+asps.getVersione().intValue();
								descrizionePA = descrizionePA.replace(match_caso, replace_caso);
							}

							// Caso 2: altra subscription
							// Internal Implementation 'Specific1' for gw_ENTE/gw_TEST/1
							String tmpMatch_caso2 = "/"+asps.getOldIDServizioForUpdate().getTipo()+"_"+asps.getOldIDServizioForUpdate().getNome()+"/";
							String match_caso2 = tmpMatch_caso2+"v"+asps.getOldIDServizioForUpdate().getVersione().intValue();
							String match_caso2_oldWithoutV = tmpMatch_caso2+asps.getOldIDServizioForUpdate().getVersione().intValue();
							if(descrizionePA.contains(match_caso2)) {
								String replace_caso2 = "/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue();
								descrizionePA = descrizionePA.replace(match_caso2, replace_caso2);
							}
							else if(descrizionePA.contains(match_caso2_oldWithoutV)) {
								String replace_caso2 = "/"+asps.getTipo()+"_"+asps.getNome()+"/v"+asps.getVersione().intValue();
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
									String partOld = "(?:"+asps.getOldIDServizioForUpdate().getTipo()+"_)?"+asps.getOldIDServizioForUpdate().getNome()+"";
									String partNew = "(?:"+asps.getTipo()+"_)?"+asps.getNome()+"";

									// vedo se matcha il fruitore
									if (partServizio.equals(partOld)) {
										partServizio = partNew;
										matchURL = true;
									}

									// vedo se matcha anche erogatore (loopback)
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
					}

					listaPA.add(tmpPorta); // la porta la aggiungo cmq per modificare i dati


					// modifica nome Servizi Applicativi che riflette il nome della PA
					if(oldIDPortaApplicativaForUpdate!=null && tmpPorta.sizeServizioApplicativoList()>0) {
						for (PortaApplicativaServizioApplicativo portaApplicativaSA : tmpPorta.getServizioApplicativoList()) {
							if(portaApplicativaSA.getNome().equals(oldIDPortaApplicativaForUpdate.getNome())) {
								// devo aggiornare il nome del SA
								IDServizioApplicativo idSA = new IDServizioApplicativo();
								idSA.setNome(oldIDPortaApplicativaForUpdate.getNome());
								idSA.setIdSoggettoProprietario(new IDSoggetto(tmpPorta.getTipoSoggettoProprietario(), tmpPorta.getNomeSoggettoProprietario()));
								ServizioApplicativo sa = saCore.getServizioApplicativo(idSA);

								IDServizioApplicativo oldIDServizioApplicativoForUpdate = new IDServizioApplicativo();
								oldIDServizioApplicativoForUpdate.setNome(sa.getNome());
								oldIDServizioApplicativoForUpdate.setIdSoggettoProprietario(idSA.getIdSoggettoProprietario());
								sa.setOldIDServizioApplicativoForUpdate(oldIDServizioApplicativoForUpdate);
								sa.setTipoSoggettoProprietario(tmpPorta.getTipoSoggettoProprietario());
								sa.setNomeSoggettoProprietario(tmpPorta.getNomeSoggettoProprietario());

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
								break;
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
		
		return oggettiDaAggiornare;
	}

	public static void deleteAccordoServizioParteSpecifica(AccordoServizioParteSpecifica asps, 
			boolean gestioneFruitori, boolean gestioneErogatori,
			IDSoggetto idSoggettoFruitore, IDServizio idServizio,
			IExtendedListServlet extendedServlet,
			String superUser, AccordiServizioParteSpecificaCore apsCore, AccordiServizioParteSpecificaHelper apsHelper, 
			StringBuffer inUsoMessage, String newLine) throws Exception {
		
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
					
					// cancellazione degli applicativi generati automaticamente
					for (PortaApplicativaServizioApplicativo paSA : paGenerataAutomcaticamente.getServizioApplicativoList()) {
						if(paSA.getNome().equals(paGenerataAutomcaticamente.getNome())) {
							IDServizioApplicativo idSA = new IDServizioApplicativo();
							idSA.setIdSoggettoProprietario(soggettoErogatore);
							idSA.setNome(paSA.getNome());
							ServizioApplicativo saGeneratoAutomaticamente = saCore.getServizioApplicativo(idSA);
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

		}
		
	}

	public static void deleteAccordoServizioParteSpecificaPorteApplicative(IDPortaApplicativa idPortaApplicativa, IDServizio idServizio,
			String superUser, AccordiServizioParteSpecificaCore apsCore, AccordiServizioParteSpecificaHelper apsHelper,
			StringBuffer inUsoMessage) throws Exception {
		
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
			
			for (PortaApplicativaServizioApplicativo paSA : tmpPA.getServizioApplicativoList()) {
				if(paSA.getNome().equals(tmpPA.getNome())) {
					IDServizioApplicativo idSA = new IDServizioApplicativo();
					idSA.setIdSoggettoProprietario(new IDSoggetto(tmpPA.getTipoSoggettoProprietario(), tmpPA.getNomeSoggettoProprietario()));
					idSA.setNome(paSA.getNome());
					ServizioApplicativo saGeneratoAutomaticamente = saCore.getServizioApplicativo(idSA);
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
			StringBuffer inUsoMessage) throws Exception {
		
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
}
