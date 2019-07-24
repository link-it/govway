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


package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.AutorizzazioneRuoli;
import org.openspcoop2.core.config.AutorizzazioneScope;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneToken;
import org.openspcoop2.core.config.GestioneTokenAutenticazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import org.openspcoop2.core.config.constants.ScopeTipoMatch;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.config.constants.ValidazioneContenutiApplicativiTipo;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaServizi;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.autorizzazione.CostantiAutorizzazione;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.BinaryParameter;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * porteAppAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class PorteApplicativeAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);



		try {
			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			String nomePorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME_PORTA);
			String idPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			int soggInt = Integer.parseInt(idsogg);
			String descr = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_DESCRIZIONE);
			String statoPorta = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_STATO_PORTA);
			String soggvirt = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SOGGETTO_VIRTUALE);
			if (soggvirt == null || "".equals(soggvirt)) {
				soggvirt = "-";
			}
			String servizio = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVIZIO);
			String azione = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONE);
			String stateless = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_STATELESS);
			String behaviour = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_BEHAVIOUR);
			String gestBody = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_GESTIONE_BODY);
			String gestManifest = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_GESTIONE_MANIFEST);
			String ricsim = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_RICEVUTA_ASINCRONA_SIMMETRICA);
			String ricasim = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_RICEVUTA_ASINCRONA_ASIMMETRICA);
			String statoValidazione = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_XSD);
			String tipoValidazione = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE);
			String autorizzazioneContenuti = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI);
			String autorizzazioneContenutiStato = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_STATO);
			String autorizzazioneContenutiProperties = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AUTORIZZAZIONE_CONTENUTI_PROPERTIES);
			String applicaMTOM = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_APPLICA_MTOM);

			String autenticazione = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE);
			String autenticazioneOpzionale = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_OPZIONALE);
			String autenticazionePrincipalTipo = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_PRINCIPAL_TIPO);
			TipoAutenticazionePrincipal autenticazionePrincipal = TipoAutenticazionePrincipal.toEnumConstant(autenticazionePrincipalTipo, false);
			List<String> autenticazioneParametroList = porteApplicativeHelper.convertFromDataElementValue_parametroAutenticazioneList(autenticazione, autenticazionePrincipal);
			String autenticazioneCustom = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM);
			String autorizzazione = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE);
			String autorizzazioneCustom = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM);
			
			String autorizzazioneAutenticati = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_AUTENTICAZIONE);
			String autorizzazioneRuoli = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_RUOLI);
			String autorizzazioneRuoliTipologia = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_RUOLO_TIPOLOGIA);
			String ruoloMatch = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_RUOLO_MATCH);
			
			String azid = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONE_ID);
			String modeaz = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_AZIONE);
			String forceWsdlBased = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_FORCE_INTERFACE_BASED);
			
			String serviceBindingS = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVICE_BINDING);
			ServiceBinding serviceBinding = null;
			if(StringUtils.isNotEmpty(serviceBindingS))
				serviceBinding = ServiceBinding.valueOf(serviceBindingS);
			
			if(servizio == null) {
				servizio = "";
			}
			
			if(modeaz == null) {
				modeaz = PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT;
			}
			 
			if ((modeaz != null) && !modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT) && (azione == null)) {
				azione = "";
			}
			
			String gestioneToken = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN);
			String gestioneTokenPolicy = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_POLICY);
			String gestioneTokenOpzionale = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_OPZIONALE);
			String gestioneTokenValidazioneInput = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_VALIDAZIONE_INPUT);
			String gestioneTokenIntrospection = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_INTROSPECTION);
			String gestioneTokenUserInfo = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_USERINFO);
			String gestioneTokenTokenForward = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_GESTIONE_TOKEN_TOKEN_FORWARD);
			
			String autenticazioneTokenIssuer = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_ISSUER);
			String autenticazioneTokenClientId = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_CLIENT_ID);
			String autenticazioneTokenSubject = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_SUBJECT);
			String autenticazioneTokenUsername = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_USERNAME);
			String autenticazioneTokenEMail = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTENTICAZIONE_TOKEN_MAIL);
			
			String autorizzazione_token = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN);
			String autorizzazione_tokenOptions = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_TOKEN_OPTIONS);
			String autorizzazioneScope = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_PORTE_AUTORIZZAZIONE_SCOPE);
			String autorizzazioneScopeMatch = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_SCOPE_MATCH);
			
			BinaryParameter allegatoXacmlPolicy = porteApplicativeHelper.getBinaryParameter(CostantiControlStation.PARAMETRO_DOCUMENTO_SICUREZZA_XACML_POLICY);
			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			ConfigurazioneCore confCore = new ConfigurazioneCore(porteApplicativeCore);

			// Prendo nome, tipo e pdd del soggetto
			String tipoNomeSoggettoProprietario = null;
			String tipoSoggettoProprietario = null;
			String nomeSoggettoProprietario = null;
			if(porteApplicativeCore.isRegistroServiziLocale()){
				org.openspcoop2.core.registry.Soggetto soggetto = soggettiCore.getSoggettoRegistro(soggInt);
				tipoNomeSoggettoProprietario = soggetto.getTipo() + "/" + soggetto.getNome();
				tipoSoggettoProprietario = soggetto.getTipo();
				nomeSoggettoProprietario = soggetto.getNome();
			}else{
				org.openspcoop2.core.config.Soggetto soggetto = soggettiCore.getSoggetto(soggInt);
				tipoNomeSoggettoProprietario = soggetto.getTipo() + "/" + soggetto.getNome();
				tipoSoggettoProprietario = soggetto.getTipo();
				nomeSoggettoProprietario = soggetto.getNome();
			}

			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoSoggettoProprietario);
			String tmpTitle = porteApplicativeHelper.getLabelNomeSoggetto(protocollo, tipoSoggettoProprietario, nomeSoggettoProprietario);

			boolean isSupportatoAutenticazioneSoggetti = soggettiCore.isSupportatoAutenticazioneSoggetti(protocollo);
			
			
			String postBackElementName = porteApplicativeHelper.getPostBackElementName();
			
			// se ho modificato il soggetto ricalcolo il servizio e il service binding
			if (postBackElementName != null) {
				if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SOGGETTO_VIRTUALE)) {
					servizio = null;
					serviceBinding = null;
				} else if(postBackElementName.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_SERVIZIO)) {
					serviceBinding = null;
				} 
			}
			
			List<String> tipiServizioCompatibiliAccordo = new ArrayList<String>();
			if(serviceBinding == null) {
				List<ServiceBinding> serviceBindingListProtocollo = apsCore.getServiceBindingListProtocollo(protocollo);
				
				if(serviceBindingListProtocollo != null && serviceBindingListProtocollo.size() > 0) {
					for (ServiceBinding serviceBinding2 : serviceBindingListProtocollo) {
						List<String> tipiServizioCompatibiliAccordoTmp = apsCore.getTipiServiziGestitiProtocollo(protocollo,serviceBinding2);
						
						for (String tipoTmp : tipiServizioCompatibiliAccordoTmp) {
							if(!tipiServizioCompatibiliAccordo.contains(tipoTmp))
								tipiServizioCompatibiliAccordo.add(tipoTmp);
						}
					}
				}
			} else {
				tipiServizioCompatibiliAccordo = apsCore.getTipiServiziGestitiProtocollo(protocollo,serviceBinding);
			}
			
			// Informazioni sul numero di ServiziApplicativi, Correlazione Applicativa e stato Message-Security
			int numSA = 0;
			int numRuoli =0;
			int numScope = 0;
			String statoMessageSecurity  =  "";
			String statoMTOM  = "";
			int numCorrelazioneReq =0; 
			int numCorrelazioneRes =0;
			int numProprProt = 0;

			// Prendo la lista di soggetti (tranne il mio) e li metto in un
			// array per la funzione SoggettoVirtuale
			String[] soggettiList = null;
			String[] soggettiListLabel = null;
			Boolean soggVirt = (Boolean) session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_GESTIONE_SOGGETTI_VIRTUALI);
			if (soggVirt) {
				List<IDServizio> list = null;
				List<IDSoggetto> listSoggetti = new ArrayList<IDSoggetto>();
				Vector<String> identitaSoggetti = new Vector<String>();
				try{
					list = apsCore.getAllIdServizi(new FiltroRicercaServizi());
				}catch(DriverRegistroServiziNotFound dNotFound){}
				if(list!=null){
					for (int i = 0; i < list.size(); i++) {
						String idSoggetto = list.get(i).getSoggettoErogatore().toString();
						if (!idSoggetto.equals(tipoNomeSoggettoProprietario)){ // non aggiungo il soggetto proprietario della porta applicativa
							if(identitaSoggetti.contains(idSoggetto)==false){
								identitaSoggetti.add(idSoggetto);
								IDSoggetto soggettoErogatore = list.get(i).getSoggettoErogatore();
								String protocolloSoggettoErogatore = soggettiCore.getProtocolloAssociatoTipoSoggetto(soggettoErogatore.getTipo());
								if(protocolloSoggettoErogatore.equals(protocollo)){
									listSoggetti.add(soggettoErogatore);
								}
							}
						}
					}
				}
				int totEl = listSoggetti.size() + 1;
				soggettiList = new String[totEl];
				soggettiListLabel = new String[totEl];
				soggettiList[0] = "-";
				soggettiListLabel[0] = "-";
				Map<String, String> soggettiMapTmp = new HashMap<String,String>();
				List<String> listSoggettiOrdered = new ArrayList<String>();
				for (IDSoggetto idSoggetto : listSoggetti) {
					listSoggettiOrdered.add(idSoggetto.getTipo() + "/" + idSoggetto.getNome());
					soggettiMapTmp.put(idSoggetto.getTipo() + "/" + idSoggetto.getNome(), porteApplicativeHelper.getLabelNomeSoggetto(protocollo,
							idSoggetto.getTipo(), idSoggetto.getNome()));
				}
				Collections.sort(listSoggettiOrdered);
				int i = 1;
				for (String idSOrdered : listSoggettiOrdered) {
					soggettiList[i] = idSOrdered;
					soggettiListLabel[i] = soggettiMapTmp.get(idSOrdered); 
					i++;
				}
			}

			// Prendo la lista di servizi e li metto in un array
			String[] serviziList = null;
			String[] serviziListLabel = null;
			FiltroRicercaServizi filtroServizi = new FiltroRicercaServizi();
			if ( (!soggvirt.equals("")) && (!soggvirt.equals("-")) ){
				filtroServizi.setTipoSoggettoErogatore(soggvirt.split("/")[0]);
				filtroServizi.setNomeSoggettoErogatore(soggvirt.split("/")[1]);
			}
			else{
				filtroServizi.setTipoSoggettoErogatore(tipoSoggettoProprietario);
				filtroServizi.setNomeSoggettoErogatore(nomeSoggettoProprietario);
			}
			List<IDServizio> listaServizi = null;
			try{
				listaServizi = apsCore.getAllIdServizi(filtroServizi);
			}catch(DriverRegistroServiziNotFound not){}
			if(listaServizi!=null && listaServizi.size()>0){
				List<String> serviziListTmp = new ArrayList<String>();
				Map<String, IDServizio> serviziMapTmp = new HashMap<String,IDServizio>();
				for (IDServizio idServizio : listaServizi) {
					if(tipiServizioCompatibiliAccordo.contains(idServizio.getTipo())){
						String keyServizio = idServizio.getSoggettoErogatore().getTipo() + "/" + idServizio.getSoggettoErogatore().getNome() + " " + idServizio.getTipo() + "/" + idServizio.getNome() + "/" + idServizio.getVersione().intValue();
						serviziListTmp.add(keyServizio); 
						serviziMapTmp.put(keyServizio, idServizio); 
					}
				}

				Collections.sort(serviziListTmp);
				serviziList = serviziListTmp.toArray(new String[1]);
				serviziListLabel = new String[serviziList.length];
				for (int i = 0; i < serviziList.length; i++) {
					String idServTmp = serviziList[i];
					serviziListLabel[i] = porteApplicativeHelper.getLabelIdServizio(protocollo, serviziMapTmp.get(idServTmp));
				}
			}

			IDServizio idServizio = null;
			AccordoServizioParteSpecifica servS = null;
			if (servizio != null) {
				boolean servizioPresenteInLista  = false;
				if(serviziList!=null && serviziList.length>0){
					for (int i = 0; i < serviziList.length; i++) {
						if(serviziList[i].equals(servizio)){
							servizioPresenteInLista = true;
							break;
						}
					}
				}
				if(servizioPresenteInLista){
					String [] tmp = servizio.split(" ");
					idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tmp[1].split("/")[0],tmp[1].split("/")[1], 
							tmp[0].split("/")[0],tmp[0].split("/")[1], 
							Integer.parseInt(tmp[1].split("/")[2])); 
					try{
						servS = apsCore.getServizio(idServizio);
					}catch(DriverRegistroServiziNotFound dNotFound){
					}
				}
				if(servS==null){
					
					// è cambiato il soggetto erogatore. non è più valido il servizio
					servizio = null;
					idServizio = null;
					if(serviziList!=null && serviziList.length>0){
						servizio = serviziList[0];
						String []tmp = servizio.split(" ");
						idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tmp[1].split("/")[0],tmp[1].split("/")[1], 
								tmp[0].split("/")[0],tmp[0].split("/")[1], 
								Integer.parseInt(tmp[1].split("/")[2])); 
						try{
							servS = apsCore.getServizio(idServizio);
						}catch(DriverRegistroServiziNotFound dNotFound){
						}
						if(servS==null){
							servizio = null;
							idServizio = null;
						}
					}
				}
			}
			
			AccordoServizioParteComuneSintetico as = null;
			if (servS != null) {
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(servS.getAccordoServizioParteComune());
				as = apcCore.getAccordoServizioSintetico(idAccordo);
				if(serviceBinding == null)
					serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
			}
			
			String[] azioniList = null;
			String[] azioniListLabel = null;
			boolean addTrattinoSelezioneNonEffettuata = false;
			List<String> filtraAzioniUtilizzate = new ArrayList<String>(); // TODO controllare
			if ((modeaz != null) && modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT)) {
			
				Map<String,String> azioni = porteApplicativeCore.getAzioniConLabel(servS, as, addTrattinoSelezioneNonEffettuata , true, filtraAzioniUtilizzate);
				if(azioni != null && azioni.size() > 0) {
					azioniList = new String[azioni.size()];
					azioniListLabel = new String[azioni.size()];
					int i = 0;
					for (String string : azioni.keySet()) {
						azioniList[i] = string;
						azioniListLabel[i] = azioni.get(string);
						i++;
					}
				}
			}
			
			List<GenericProperties> gestorePolicyTokenList = confCore.gestorePolicyTokenList(null, ConfigurazioneCostanti.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TIPOLOGIA_GESTIONE_POLICY_TOKEN, null);
			String [] policyLabels = new String[gestorePolicyTokenList.size() + 1];
			String [] policyValues = new String[gestorePolicyTokenList.size() + 1];
			
			policyLabels[0] = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
			policyValues[0] = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
			
			for (int i = 0; i < gestorePolicyTokenList.size(); i++) {
			GenericProperties genericProperties = gestorePolicyTokenList.get(i);
				policyLabels[(i+1)] = genericProperties.getNome();
				policyValues[(i+1)] = genericProperties.getNome();
			}
			
			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (porteApplicativeHelper.isEditModeInProgress()) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(SoggettiCostanti.LABEL_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
						new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tmpTitle, 
								PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST ,
								new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)),
								ServletUtils.getParameterAggiungi()
						);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, dati);

				if (statoValidazione == null) {
					if(porteApplicativeCore.isSinglePdD()){
						Configurazione config = porteApplicativeCore.getConfigurazioneGenerale();
						if(config.getValidazioneContenutiApplicativi()!=null){
							if(config.getValidazioneContenutiApplicativi().getStato()!=null)
								statoValidazione = config.getValidazioneContenutiApplicativi().getStato().toString();
							if(config.getValidazioneContenutiApplicativi().getTipo()!=null)
								tipoValidazione = config.getValidazioneContenutiApplicativi().getTipo().toString();
							if(StatoFunzionalita.ABILITATO.equals(config.getValidazioneContenutiApplicativi().getAcceptMtomMessage())){
								applicaMTOM = Costanti.CHECK_BOX_ENABLED_ABILITATO;
							}
						}
					}
				}

				if (statoValidazione == null) {
					statoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_VALIDAZIONE_DISABILITATO;
				}
				if (tipoValidazione == null) {
					tipoValidazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_TIPO_VALIDAZIONE_INTERFACE;
				}
				if (applicaMTOM == null) {
					applicaMTOM = "";
				}
				if (stateless == null) {
					stateless = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_STATELESS_DEFAULT;
				}
				if (gestManifest == null) {
					gestManifest = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_MANIFEST_DEFAULT;
				}
				
				if (autenticazione == null) {
					autenticazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_AUTENTICAZIONE;
				}
				if (autorizzazione == null) {
					String defaultAutorizzazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_AUTORIZZAZIONE;
					if (defaultAutorizzazione != null &&
							!TipoAutorizzazione.getAllValues().contains(defaultAutorizzazione)) {
						autorizzazioneCustom = defaultAutorizzazione;
						autorizzazione = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM;
					}
					else{
						autorizzazione = AutorizzazioneUtilities.convertToStato(defaultAutorizzazione);
						if(TipoAutorizzazione.isAuthenticationRequired(defaultAutorizzazione))
							autorizzazioneAutenticati = Costanti.CHECK_BOX_ENABLED;
						if(TipoAutorizzazione.isRolesRequired(defaultAutorizzazione))
							autorizzazioneRuoli = Costanti.CHECK_BOX_ENABLED;
						autorizzazioneRuoliTipologia = AutorizzazioneUtilities.convertToRuoloTipologia(defaultAutorizzazione).getValue();
					}
				}
				
				if(gestioneToken == null) {
					gestioneToken = StatoFunzionalita.DISABILITATO.getValue();
					gestioneTokenPolicy = CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO;
					gestioneTokenOpzionale = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_OPZIONALE;
					gestioneTokenValidazioneInput = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_VALIDAZIONE_INPUT;
					gestioneTokenIntrospection = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_INTROSPECTION;
					gestioneTokenUserInfo = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_USER_INFO;
					gestioneTokenTokenForward = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_TOKEN_FORWARD;
					autenticazioneTokenIssuer = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_ISSUER;
					autenticazioneTokenClientId = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_CLIENT_ID;
					autenticazioneTokenSubject = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_SUBJECT;
					autenticazioneTokenUsername = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_USERNAME;
					autenticazioneTokenEMail = CostantiControlStation.DEFAULT_VALUE_PARAMETRO_CONFIGURAZIONE_GESTORE_POLICY_TOKEN_AUTENTICAZIONE_EMAIL;
				}
				
				if(autorizzazioneScope == null) {
					autorizzazioneScope = "";
				}
				
				if(autorizzazioneContenutiStato == null)
					autorizzazioneContenutiStato = StatoFunzionalita.DISABILITATO.getValue();
				

				dati = porteApplicativeHelper.addPorteAppToDati(TipoOperazione.ADD,dati, nomePorta, descr, soggvirt, soggettiList,
						soggettiListLabel, servizio, serviziList, serviziListLabel, azione, azioniList, azioniListLabel,
						stateless, ricsim, ricasim, null, null, statoValidazione, tipoValidazione, gestBody, gestManifest,
						null,0,null,autorizzazioneContenutiStato, autorizzazioneContenuti, autorizzazioneContenutiProperties,protocollo,
						numSA,numRuoli, ruoloMatch,
						statoMessageSecurity,statoMTOM,numCorrelazioneReq,numCorrelazioneRes,numProprProt,applicaMTOM,
						behaviour,null,null,null,
						autenticazione, autorizzazione,
						autenticazioneOpzionale, autenticazionePrincipal, autenticazioneParametroList, autenticazioneCustom, autorizzazioneCustom,
						isSupportatoAutenticazioneSoggetti,autorizzazioneAutenticati,autorizzazioneRuoli,autorizzazioneRuoliTipologia,
						servS,as,serviceBinding,
						statoPorta, modeaz,  azid,  azione, forceWsdlBased,false,false,
						false,null,gestioneToken,policyLabels, policyValues,
						gestioneTokenPolicy, gestioneTokenOpzionale,
						gestioneTokenValidazioneInput,gestioneTokenIntrospection,gestioneTokenUserInfo,gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazione_token, autorizzazione_tokenOptions,
						autorizzazioneScope,numScope, autorizzazioneScopeMatch,allegatoXacmlPolicy); 

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE,
						ForwardParams.ADD());

			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.porteAppCheckData(TipoOperazione.ADD, null, isSupportatoAutenticazioneSoggetti);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(SoggettiCostanti.LABEL_SOGGETTI, SoggettiCostanti.SERVLET_NAME_SOGGETTI_LIST),
						new Parameter(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_PORTE_APPLICATIVE_DI + tmpTitle, 
								PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_LIST ,
								new Parameter( PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg)),
								ServletUtils.getParameterAggiungi()
						);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idPorta, idsogg, idPorta, dati);

				dati = porteApplicativeHelper.addPorteAppToDati(TipoOperazione.ADD,dati, nomePorta, descr, soggvirt, soggettiList,
						soggettiListLabel, servizio, serviziList,
						serviziListLabel, azione, azioniList, azioniListLabel, stateless, ricsim, ricasim, 
						null, null, statoValidazione, tipoValidazione, gestBody, gestManifest,null,0,null,autorizzazioneContenutiStato, autorizzazioneContenuti, autorizzazioneContenutiProperties,protocollo,
						numSA,numRuoli, ruoloMatch,
						statoMessageSecurity,statoMTOM,numCorrelazioneReq,numCorrelazioneRes,numProprProt,applicaMTOM,
						behaviour,null,null,null,
						autenticazione, autorizzazione,
						autenticazioneOpzionale, autenticazionePrincipal, autenticazioneParametroList, autenticazioneCustom, autorizzazioneCustom,
						isSupportatoAutenticazioneSoggetti,autorizzazioneAutenticati,autorizzazioneRuoli,autorizzazioneRuoliTipologia,
						servS,as,serviceBinding,
						statoPorta, modeaz,  azid, azione, forceWsdlBased, false,false,
						false,null,gestioneToken,policyLabels, policyValues,
						gestioneTokenPolicy,gestioneTokenOpzionale,
						gestioneTokenValidazioneInput,gestioneTokenIntrospection,gestioneTokenUserInfo,gestioneTokenTokenForward,
						autenticazioneTokenIssuer, autenticazioneTokenClientId, autenticazioneTokenSubject, autenticazioneTokenUsername, autenticazioneTokenEMail,
						autorizzazione_token, autorizzazione_tokenOptions,
						autorizzazioneScope,numScope, autorizzazioneScopeMatch,allegatoXacmlPolicy);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE, 
						ForwardParams.ADD());
			}

			// Inserisco la porta applicativa nel db
			PortaApplicativa pa = new PortaApplicativa();
			pa.setNome(nomePorta);
			pa.setNomeSoggettoProprietario(nomeSoggettoProprietario);
			pa.setTipoSoggettoProprietario(tipoSoggettoProprietario);
			pa.setDescrizione(descr);
			if(statoPorta==null || "".equals(statoPorta) || CostantiConfigurazione.ABILITATO.toString().equals(statoPorta)){
				pa.setStato(StatoFunzionalita.ABILITATO);
			}
			else{
				pa.setStato(StatoFunzionalita.DISABILITATO);
			}
			if(autorizzazioneContenutiStato.equals(StatoFunzionalita.DISABILITATO.getValue())) {
				pa.setAutorizzazioneContenuto(null);
				pa.getProprietaAutorizzazioneContenutoList().clear();
			} else if(autorizzazioneContenutiStato.equals(StatoFunzionalita.ABILITATO.getValue())) {
				pa.setAutorizzazioneContenuto(CostantiAutorizzazione.AUTORIZZAZIONE_CONTENUTO_BUILT_IN);
				pa.getProprietaAutorizzazioneContenutoList().clear();
				Properties convertTextToProperties = PropertiesUtilities.convertTextToProperties(autorizzazioneContenutiProperties);
				
				Enumeration<Object> keys = convertTextToProperties.keys();
				while (keys.hasMoreElements()) {
					String nome = (String) keys.nextElement();
					String valore = convertTextToProperties.getProperty(nome);
					Proprieta proprietaAutorizzazioneContenuto = new Proprieta();
					proprietaAutorizzazioneContenuto.setNome(nome);
					proprietaAutorizzazioneContenuto.setValore(valore);
					pa.addProprietaAutorizzazioneContenuto(proprietaAutorizzazioneContenuto);
				}
			} else {
				pa.setAutorizzazioneContenuto(autorizzazioneContenuti);
			}
			
			if (autenticazione == null ||
					!autenticazione.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTENTICAZIONE_CUSTOM))
				pa.setAutenticazione(autenticazione);
			else
				pa.setAutenticazione(autenticazioneCustom);
			if(autenticazioneOpzionale != null){
				if(ServletUtils.isCheckBoxEnabled(autenticazioneOpzionale))
					pa.setAutenticazioneOpzionale(StatoFunzionalita.ABILITATO);
				else 
					pa.setAutenticazioneOpzionale(StatoFunzionalita.DISABILITATO);
			} else 
				pa.setAutenticazioneOpzionale(null);
			List<Proprieta> proprietaAutenticazione = porteApplicativeCore.convertToAutenticazioneProprieta(autenticazione, autenticazionePrincipal, autenticazioneParametroList);
			if(proprietaAutenticazione!=null && !proprietaAutenticazione.isEmpty()) {
				pa.getProprietaAutenticazioneList().addAll(proprietaAutenticazione);
			}
			
			if (autorizzazione == null || 
					!autorizzazione.equals(CostantiControlStation.DEFAULT_VALUE_PARAMETRO_PORTE_AUTORIZZAZIONE_CUSTOM))
				pa.setAutorizzazione(AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString(autorizzazione, 
						ServletUtils.isCheckBoxEnabled(autorizzazioneAutenticati), 
						ServletUtils.isCheckBoxEnabled(autorizzazioneRuoli),
						ServletUtils.isCheckBoxEnabled(autorizzazioneScope),
						autorizzazione_tokenOptions,
						RuoloTipologia.toEnumConstant(autorizzazioneRuoliTipologia)));
			else
				pa.setAutorizzazione(autorizzazioneCustom);
						
			if(ruoloMatch!=null && !"".equals(ruoloMatch)){
				RuoloTipoMatch tipoRuoloMatch = RuoloTipoMatch.toEnumConstant(ruoloMatch);
				if(tipoRuoloMatch!=null){
					if(pa.getRuoli()==null){
						pa.setRuoli(new AutorizzazioneRuoli());
					}
					pa.getRuoli().setMatch(tipoRuoloMatch);
				}
			}
			if(ServletUtils.isCheckBoxEnabled(autorizzazioneScope )) {
				if(pa.getScope() == null)
					pa.setScope(new AutorizzazioneScope());
				
				pa.getScope().setStato(StatoFunzionalita.ABILITATO); 
			}
			else {
				pa.setScope(null);
			}
			if(autorizzazioneScopeMatch!=null && !"".equals(autorizzazioneScopeMatch)){
				ScopeTipoMatch scopeTipoMatch = ScopeTipoMatch.toEnumConstant(autorizzazioneScopeMatch);
				if(scopeTipoMatch!=null){
					if(pa.getScope()==null){
						pa.setScope(new AutorizzazioneScope());
					}
					pa.getScope().setMatch(scopeTipoMatch);
				}
			}
			
			if(pa.getGestioneToken() == null)
				pa.setGestioneToken(new GestioneToken());
			
			if(gestioneToken.equals(StatoFunzionalita.ABILITATO.getValue())) {
				pa.getGestioneToken().setPolicy(gestioneTokenPolicy);
				if(ServletUtils.isCheckBoxEnabled(gestioneTokenOpzionale)) {
					pa.getGestioneToken().setTokenOpzionale(StatoFunzionalita.ABILITATO);
				}
				else {
					pa.getGestioneToken().setTokenOpzionale(StatoFunzionalita.DISABILITATO);
				}
				pa.getGestioneToken().setValidazione(StatoFunzionalitaConWarning.toEnumConstant(gestioneTokenValidazioneInput));
				pa.getGestioneToken().setIntrospection(StatoFunzionalitaConWarning.toEnumConstant(gestioneTokenIntrospection));
				pa.getGestioneToken().setUserInfo(StatoFunzionalitaConWarning.toEnumConstant(gestioneTokenUserInfo));
				pa.getGestioneToken().setForward(StatoFunzionalita.toEnumConstant(gestioneTokenTokenForward)); 
				pa.getGestioneToken().setOptions(autorizzazione_tokenOptions);
				if(pa.getGestioneToken().getAutenticazione()==null) {
					pa.getGestioneToken().setAutenticazione(new GestioneTokenAutenticazione());
				}
				pa.getGestioneToken().getAutenticazione().setIssuer(ServletUtils.isCheckBoxEnabled(autenticazioneTokenIssuer) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenIssuer)); 
				pa.getGestioneToken().getAutenticazione().setClientId(ServletUtils.isCheckBoxEnabled(autenticazioneTokenClientId) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenClientId)); 
				pa.getGestioneToken().getAutenticazione().setSubject(ServletUtils.isCheckBoxEnabled(autenticazioneTokenSubject) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenSubject)); 
				pa.getGestioneToken().getAutenticazione().setUsername(ServletUtils.isCheckBoxEnabled(autenticazioneTokenUsername) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenUsername)); 
				pa.getGestioneToken().getAutenticazione().setEmail(ServletUtils.isCheckBoxEnabled(autenticazioneTokenEMail) ? StatoFunzionalita.ABILITATO : StatoFunzionalita.toEnumConstant(autenticazioneTokenEMail)); 
			} else {
				pa.getGestioneToken().setPolicy(null);
				pa.getGestioneToken().setTokenOpzionale(StatoFunzionalita.DISABILITATO); 
				pa.getGestioneToken().setValidazione(StatoFunzionalitaConWarning.DISABILITATO);
				pa.getGestioneToken().setValidazione(StatoFunzionalitaConWarning.DISABILITATO);
				pa.getGestioneToken().setIntrospection(StatoFunzionalitaConWarning.DISABILITATO);
				pa.getGestioneToken().setUserInfo(StatoFunzionalitaConWarning.DISABILITATO);
				pa.getGestioneToken().setForward(StatoFunzionalita.DISABILITATO); 
				pa.getGestioneToken().setOptions(null);
				if(pa.getGestioneToken().getAutenticazione()!=null) {
					pa.getGestioneToken().setAutenticazione(null);
				}
			}
			
			if (stateless!=null && !stateless.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_STATELESS_DEFAULT))
				pa.setStateless(StatoFunzionalita.toEnumConstant(stateless));
			if (PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_BODY_ALLEGA.equals(gestBody))
				pa.setAllegaBody(StatoFunzionalita.toEnumConstant(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_ABILITATO));
			else
				pa.setAllegaBody(StatoFunzionalita.toEnumConstant(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO));
			if (PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_BODY_SCARTA.equals(gestBody))
				pa.setScartaBody(StatoFunzionalita.toEnumConstant(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_ABILITATO));
			else
				pa.setScartaBody(StatoFunzionalita.toEnumConstant(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO));
			if (gestManifest!=null && !gestManifest.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_GEST_MANIFEST_DEFAULT))
				pa.setGestioneManifest(StatoFunzionalita.toEnumConstant(gestManifest));
			pa.setRicevutaAsincronaSimmetrica(StatoFunzionalita.toEnumConstant(ricsim));
			pa.setRicevutaAsincronaAsimmetrica(StatoFunzionalita.toEnumConstant(ricasim));
			if ( (!soggvirt.equals("")) && (!soggvirt.equals("-")) ){
				String tipoSoggVirt = soggvirt.split("/")[0];
				String nomeSoggVirt = soggvirt.split("/")[1];
				PortaApplicativaSoggettoVirtuale pasv = new PortaApplicativaSoggettoVirtuale();
				pasv.setTipo(tipoSoggVirt);
				pasv.setNome(nomeSoggVirt);
				pa.setSoggettoVirtuale(pasv);
			}

			if (servizio!=null) {
				String [] tmp = servizio.split(" ");
				idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(tmp[1].split("/")[0],tmp[1].split("/")[1], 
						tmp[0].split("/")[0],tmp[0].split("/")[1], 
						Integer.parseInt(tmp[1].split("/")[2])); 
				PortaApplicativaServizio pas = new PortaApplicativaServizio();
				pas.setTipo(idServizio.getTipo());
				pas.setNome(idServizio.getNome());
				pas.setVersione(idServizio.getVersione());
				pa.setServizio(pas);
			}
			
			// se l azione e' settata allora creo il bean
			if (((!azione.equals("") || 
							modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_INPUT_BASED) ||
							modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_SOAP_ACTION_BASED) ||
							modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_PROTOCOL_BASED) ||
							modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_INTERFACE_BASED))) ||
							!azid.equals("")) {
				PortaApplicativaAzione paa = new PortaApplicativaAzione();

				if (modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT)) {
					azione = azid;
					if(paa.getId()<=0){
						paa.setId(-2l);
					}
				}
				
				paa.setIdentificazione(PortaApplicativaAzioneIdentificazione.toEnumConstant(modeaz));
				
				if (modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_HEADER_BASED) ||
						modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_URL_BASED) ||
						modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_CONTENT_BASED)
						) {
					paa.setNome(null);
					paa.setPattern(azione);
				}
				else if (modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT)
						) {
					paa.setNome(azione);
					paa.setPattern(null);
				}
				else {
					paa.setNome(null);
					paa.setPattern(null);
				}

				//FORCE WSDL BASED
				if(!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_REGISTER_INPUT) && 
						!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_PROTOCOL_BASED) &&
						!modeaz.equals(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_INTERFACE_BASED)){

					if(forceWsdlBased != null && (ServletUtils.isCheckBoxEnabled(forceWsdlBased))){
						paa.setForceInterfaceBased(StatoFunzionalita.ABILITATO);
					}else {
						paa.setForceInterfaceBased(StatoFunzionalita.DISABILITATO);
					}
				}else {
					paa.setForceInterfaceBased(null);
				}

				pa.setAzione(paa);
			} else
				pa.setAzione(null);

			pa.setStatoMessageSecurity(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO);
			pa.setIdSoggetto(Long.valueOf(soggInt));

			ValidazioneContenutiApplicativi vx = new ValidazioneContenutiApplicativi();
			vx.setStato(StatoFunzionalitaConWarning.toEnumConstant(statoValidazione));
			vx.setTipo(ValidazioneContenutiApplicativiTipo.toEnumConstant(tipoValidazione));
			
			if(applicaMTOM != null){
				if(ServletUtils.isCheckBoxEnabled(applicaMTOM))
					vx.setAcceptMtomMessage(StatoFunzionalita.ABILITATO);
				else 
					vx.setAcceptMtomMessage(StatoFunzionalita.DISABILITATO);
			} else 
				vx.setAcceptMtomMessage(null);
			
			pa.setValidazioneContenutiApplicativi(vx);

			if(behaviour!=null && !"".equals(behaviour))
				pa.setBehaviour(behaviour);
			else 
				pa.setBehaviour(null);
			
			if(autorizzazione != null && autorizzazione.equals(AutorizzazioneUtilities.STATO_XACML_POLICY) && allegatoXacmlPolicy.getValue() != null) {
				pa.setXacmlPolicy(new String(allegatoXacmlPolicy.getValue()));
			} else {
				pa.setXacmlPolicy(null);
			}
			
			String userLogin = ServletUtils.getUserLoginFromSession(session);		

			porteApplicativeCore.performCreateOperation(userLogin, porteApplicativeHelper.smista(), pa);
			
			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.PORTE_APPLICATIVE_BY_SOGGETTO;
			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);
			List<PortaApplicativa> lista = porteApplicativeCore.porteAppList(soggInt, ricerca);

			porteApplicativeHelper.preparePorteAppList(ricerca, lista, idLista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE, 
					ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE,
					ForwardParams.ADD());
		}  
	}
}
