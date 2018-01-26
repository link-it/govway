/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.config.AutorizzazioneRuoli;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaAzione;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.PortaApplicativaAzioneIdentificazione;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoAutorizzazione;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.web.ctrlstat.core.AutorizzazioneUtilities;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCore;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeHelper;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * AccordiServizioParteSpecificaPorteApplicativeAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author: pintori $
 * @version $Rev: 13452 $, $Date: 2017-11-21 18:45:22 +0100 (Tue, 21 Nov 2017) $
 * 
 */
public final class AccordiServizioParteSpecificaPorteApplicativeAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session);
		if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;

		try {
			AccordiServizioParteSpecificaHelper apsHelper = new AccordiServizioParteSpecificaHelper(request, pd, session);
			String idAsps = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			String idSoggettoErogatoreDelServizio = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);
			if ((idSoggettoErogatoreDelServizio == null) || idSoggettoErogatoreDelServizio.equals("")) {
				PageData oldPD = ServletUtils.getPageDataFromSession(session);

				idSoggettoErogatoreDelServizio = oldPD.getHidden(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID_SOGGETTO_EROGATORE);
			}
			String azione = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONE);
			String nome = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME);
			String modeCreazione = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_CREAZIONE);
			String identificazione = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_IDENTIFICAZIONE);
			String mappingPA = request.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MAPPING);

			String erogazioneRuolo = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_RUOLO);
			String erogazioneAutenticazione = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE);
			String erogazioneAutenticazioneOpzionale = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTENTICAZIONE_OPZIONALE);
			String erogazioneAutorizzazione = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE);
			String erogazioneAutorizzazioneAutenticati = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_AUTENTICAZIONE);
			String erogazioneAutorizzazioneRuoli = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLI);
			String erogazioneAutorizzazioneRuoliTipologia = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_TIPOLOGIA);
			String erogazioneAutorizzazioneRuoliMatch = request.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_AUTORIZZAZIONE_RUOLO_MATCH);

			String nomeSA = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SA);

			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);

			// Preparo il menu
			apsHelper.makeMenu();

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			SoggettiCore soggettiCore = new SoggettiCore(porteApplicativeCore);
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(porteApplicativeCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(porteApplicativeCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(porteApplicativeCore);
			int idServizio = Integer.parseInt(idAsps);
			AccordoServizioParteSpecifica asps  =apsCore.getAccordoServizioParteSpecifica(idServizio);
			IDServizio idServizio2 = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps); 
			int soggInt = Integer.parseInt(idSoggettoErogatoreDelServizio);
			List<MappingErogazionePortaApplicativa> listaMappingErogazione = apsCore.mappingServiziPorteAppList(idServizio2,idServizio, soggInt, null);
			MappingErogazionePortaApplicativa mappingSelezionato = null, mappingDefault = null;

			String[] listaMappingLabels = null;
			String[] listaMappingValues = null;
			List<String> azioniOccupate = new ArrayList<>();
			int listaMappingErogazioneSize = listaMappingErogazione != null ? listaMappingErogazione.size() : 0;
			if(listaMappingErogazioneSize > 0) {
				for (int i = 0; i < listaMappingErogazione.size(); i++) {
					MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa = listaMappingErogazione.get(i);
					if(mappingErogazionePortaApplicativa.isDefault()) {
						mappingDefault = mappingErogazionePortaApplicativa;
						break;
					}
				}
				
				if(mappingPA != null) {
					for (int i = 0; i < listaMappingErogazione.size(); i++) {
						MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa = listaMappingErogazione.get(i);
						if(mappingErogazionePortaApplicativa.getNome().equals(mappingPA)) {
							mappingSelezionato = mappingErogazionePortaApplicativa;
							break;
						}
					}
				}

				if(mappingSelezionato == null) {
					mappingSelezionato = mappingDefault;
				}

				listaMappingLabels = new String[listaMappingErogazioneSize];
				listaMappingValues = new String[listaMappingErogazioneSize];
				for (int i = 0; i < listaMappingErogazione.size(); i++) {
					MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa = listaMappingErogazione.get(i);
					listaMappingLabels[i] = mappingErogazionePortaApplicativa.isDefault()? PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_MAPPING_EROGAZIONE_PA_NOME_DEFAULT: mappingErogazionePortaApplicativa.getNome();
					listaMappingValues[i] = mappingErogazionePortaApplicativa.getNome();
					
					// colleziono le azioni gia' configurate
					PortaApplicativa portaApplicativa = porteApplicativeCore.getPortaApplicativa(mappingErogazionePortaApplicativa.getIdPortaApplicativa());
					if(portaApplicativa.getAzione() != null && portaApplicativa.getAzione().getAzioneDelegataList() != null)
						azioniOccupate.addAll(portaApplicativa.getAzione().getAzioneDelegataList());
				}
			}

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

			AccordoServizioParteComune as = null;
			ServiceBinding serviceBinding = null;
			if (asps != null) {
				IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
				as = apcCore.getAccordoServizio(idAccordo);
				serviceBinding = apcCore.toMessageServiceBinding(as.getServiceBinding());
			}

			// Prendo le azioni  disponibili
			boolean addTrattinoSelezioneNonEffettuata = true;
			int sogliaAzioni = addTrattinoSelezioneNonEffettuata ? 1 : 0;
			List<String> azioni = porteApplicativeCore.getAzioni(asps, as, addTrattinoSelezioneNonEffettuata, true, azioniOccupate);
			String[] azioniDisponibiliList = null;
			if(azioni!=null && azioni.size()>0) {
				azioniDisponibiliList = new String[azioni.size()];
				for (int i = 0; i < azioni.size(); i++) {
					azioniDisponibiliList[i] = "" + azioni.get(i);
				}
			}
			
			String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoSoggettoProprietario);
			boolean erogazioneIsSupportatoAutenticazioneSoggetti = soggettiCore.isSupportatoAutenticazioneSoggetti(protocollo);

			String postBackElementName = ServletUtils.getPostBackElementName(request);

			// Controllo se ho modificato l'azione allora ricalcolo il nome
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_AZIONE)){
					nome = null;
				}

				if(postBackElementName.equalsIgnoreCase(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_MODE_CREAZIONE)){
					// 
				}
			}




			String [] saSoggetti = null;
			if ((idSoggettoErogatoreDelServizio != null) && !idSoggettoErogatoreDelServizio.equals("")) {
				int idErogatore = Integer.parseInt(idSoggettoErogatoreDelServizio);

				List<ServizioApplicativo> listaSA = saCore.getServiziApplicativiByIdErogatore(new Long(idErogatore));

				// rif bug #45
				// I servizi applicativi da visualizzare sono quelli che hanno
				// -Integration Manager (getMessage abilitato)
				// -connettore != disabilitato
				ArrayList<ServizioApplicativo> validSA = new ArrayList<ServizioApplicativo>();
				for (ServizioApplicativo sa : listaSA) {
					InvocazioneServizio invServizio = sa.getInvocazioneServizio();
					org.openspcoop2.core.config.Connettore connettore = invServizio != null ? invServizio.getConnettore() : null;
					StatoFunzionalita getMessage = invServizio != null ? invServizio.getGetMessage() : null;

					if ((connettore != null && !TipiConnettore.DISABILITATO.getNome().equals(connettore.getTipo())) || CostantiConfigurazione.ABILITATO.equals(getMessage)) {
						// il connettore non e' disabilitato oppure il get
						// message e' abilitato
						// Lo aggiungo solo se gia' non esiste tra quelli
						// aggiunti
						validSA.add(sa);
					}
				}

				// Prendo la lista di servizioApplicativo associati al soggetto
				// e la metto in un array
				saSoggetti = new String[validSA.size()+1];
				saSoggetti[0] = "-"; // elemento nullo di default
				for (int i = 0; i < validSA.size(); i++) {
					ServizioApplicativo sa = validSA.get(i);
					saSoggetti[i+1] = sa.getNome();
				}
			}

			List<Parameter> lstParm = porteApplicativeHelper.getTitoloPA(parentPA, idSoggettoErogatoreDelServizio, idAsps, tipoNomeSoggettoProprietario);

			lstParm.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));

			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (ServletUtils.isEditModeInProgress(request)) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd,lstParm); 

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				if(azioniDisponibiliList==null || azioniDisponibiliList.length<= sogliaAzioni) {
					// si controlla 1 poiche' c'e' il trattino nelle azioni disponibili
					
					pd.setMessage(AccordiServizioParteSpecificaCostanti.LABEL_AGGIUNTA_AZIONI_COMPLETATA, Costanti.MESSAGE_TYPE_INFO);

					pd.disableEditMode();
					
				}
				else {
				
					if(azione == null) {
						azione = "-";
					}
	
					if(nome == null) {
						if(azione.equals("-")) {
							nome = "";
						} else {
							// nome mapping suggerito coincide con l'azione scelta
							nome =  azione;
						}
	
	
						if(identificazione == null)
							identificazione = PortaApplicativaAzioneIdentificazione.DELEGATED_BY.toString();
					}
					
					if(modeCreazione == null)
						modeCreazione = PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MODO_CREAZIONE_EREDITA;
	
					if(erogazioneRuolo==null || "".equals(erogazioneRuolo))
						erogazioneRuolo = "-";
					if(erogazioneAutenticazione==null || "".equals(erogazioneAutenticazione))
						erogazioneAutenticazione = apsCore.getAutenticazione_generazioneAutomaticaPorteApplicative();
					if(erogazioneAutorizzazione==null || "".equals(erogazioneAutorizzazione)){
						String tipoAutorizzazione = apsCore.getAutorizzazione_generazioneAutomaticaPorteApplicative();
						erogazioneAutorizzazione = AutorizzazioneUtilities.convertToStato(tipoAutorizzazione);
						if(TipoAutorizzazione.isAuthenticationRequired(tipoAutorizzazione))
							erogazioneAutorizzazioneAutenticati = Costanti.CHECK_BOX_ENABLED;
						if(TipoAutorizzazione.isRolesRequired(tipoAutorizzazione))
							erogazioneAutorizzazioneRuoli = Costanti.CHECK_BOX_ENABLED;
						erogazioneAutorizzazioneRuoliTipologia = AutorizzazioneUtilities.convertToRuoloTipologia(tipoAutorizzazione).getValue();
					} 
	
					dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idAsps, null, null, dati);
					dati = apsHelper.addConfigurazioneErogazioneToDati(TipoOperazione.ADD, dati, nome, azione, azioniDisponibiliList, idAsps, idSoggettoErogatoreDelServizio,
							identificazione, asps, as, serviceBinding, modeCreazione, listaMappingLabels, listaMappingValues,
							mappingPA, nomeSA, saSoggetti, erogazioneAutenticazione, erogazioneAutenticazioneOpzionale, 
							erogazioneIsSupportatoAutenticazioneSoggetti, erogazioneAutorizzazione, erogazioneAutorizzazioneAutenticati, 
							erogazioneAutorizzazioneRuoli, erogazioneRuolo, erogazioneAutorizzazioneRuoliTipologia, erogazioneAutorizzazioneRuoliMatch);
				}
					
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE,
						ForwardParams.ADD());

			}

			// Controlli sui campi immessi
			boolean isOk = apsHelper.configurazioneErogazioneCheckData(TipoOperazione.ADD, nome, azione, asps, azioniOccupate,modeCreazione,null,erogazioneIsSupportatoAutenticazioneSoggetti);
			if (!isOk) {
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd,lstParm); 

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.ADD, idAsps, null, null, dati);

				dati = apsHelper.addConfigurazioneErogazioneToDati(TipoOperazione.ADD, dati, nome, azione, azioniDisponibiliList, idAsps, idSoggettoErogatoreDelServizio,
						identificazione, asps, as, serviceBinding, modeCreazione, listaMappingLabels, listaMappingValues,
						mappingPA, nomeSA, saSoggetti, erogazioneAutenticazione, erogazioneAutenticazioneOpzionale, 
						erogazioneIsSupportatoAutenticazioneSoggetti, erogazioneAutorizzazione, erogazioneAutorizzazioneAutenticati, 
						erogazioneAutorizzazioneRuoli, erogazioneRuolo, erogazioneAutorizzazioneRuoliTipologia, erogazioneAutorizzazioneRuoliMatch);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE, 
						ForwardParams.ADD());
			}

			List<Object> listaOggettiDaCreare = new ArrayList<Object>();

			PortaApplicativa pa = null;
			// porta delegante e' quella di default
			PortaApplicativa paDefault = porteApplicativeCore.getPortaApplicativa(mappingDefault.getIdPortaApplicativa());
			String nomePortaDelegante = paDefault.getNome();
			String nomeNuovaPortaApplicativa = paDefault.getNome() + "/" + nome;
						
			// creo una nuova porta applicativa clonando quella selezionata 
			if(modeCreazione.equals(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_MODO_CREAZIONE_EREDITA)) {
				PortaApplicativa paSelezionata = porteApplicativeCore.getPortaApplicativa(mappingSelezionato.getIdPortaApplicativa());
				pa = (PortaApplicativa) paSelezionata.clone();
				// annullo il table id
				pa.setId(null);
				pa.setNome(nomeNuovaPortaApplicativa);
			
			} else {
				pa = new PortaApplicativa();
				pa.setNomeSoggettoProprietario(nomeSoggettoProprietario);
				pa.setTipoSoggettoProprietario(tipoSoggettoProprietario);
				pa.setDescrizione("Servizio "+asps.getTipo()+asps.getNome()+" erogato da "+tipoSoggettoProprietario+nomeSoggettoProprietario);
				pa.setNome(nomeNuovaPortaApplicativa);

				pa.setAutenticazione(erogazioneAutenticazione);
				if(erogazioneAutenticazioneOpzionale != null){
					if(ServletUtils.isCheckBoxEnabled(erogazioneAutenticazioneOpzionale))
						pa.setAutenticazioneOpzionale(StatoFunzionalita.ABILITATO);
					else 
						pa.setAutenticazioneOpzionale(StatoFunzionalita.DISABILITATO);
				} else 
					pa.setAutenticazioneOpzionale(null);
				pa.setAutorizzazione(AutorizzazioneUtilities.convertToTipoAutorizzazioneAsString(erogazioneAutorizzazione, 
						ServletUtils.isCheckBoxEnabled(erogazioneAutorizzazioneAutenticati), ServletUtils.isCheckBoxEnabled(erogazioneAutorizzazioneRuoli), 
						RuoloTipologia.toEnumConstant(erogazioneAutorizzazioneRuoliTipologia)));
				
				if(erogazioneAutorizzazioneRuoliMatch!=null && !"".equals(erogazioneAutorizzazioneRuoliMatch)){
					RuoloTipoMatch tipoRuoloMatch = RuoloTipoMatch.toEnumConstant(erogazioneAutorizzazioneRuoliMatch);
					if(tipoRuoloMatch!=null){
						if(pa.getRuoli()==null){
							pa.setRuoli(new AutorizzazioneRuoli());
						}
						pa.getRuoli().setMatch(tipoRuoloMatch);
					}
				}
				
				pa.setAllegaBody(StatoFunzionalita.toEnumConstant(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO));
				pa.setScartaBody(StatoFunzionalita.toEnumConstant(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO));
				// Devo lasciare a null !! pa.setGestioneManifest(CostantiConfigurazione.ABILITATO);
				pa.setRicevutaAsincronaSimmetrica(CostantiConfigurazione.ABILITATO);
				pa.setRicevutaAsincronaAsimmetrica(CostantiConfigurazione.ABILITATO);

				PortaApplicativaServizio pas = new PortaApplicativaServizio();
				pas.setTipo(asps.getTipo());
				pas.setNome(asps.getNome());
				pa.setServizio(pas);

				pa.setStatoMessageSecurity(PorteApplicativeCostanti.DEFAULT_VALUE_PARAMETRO_PORTE_APPLICATIVE_DISABILITATO);
				pa.setIdSoggetto((long) soggInt);

				if(nomeSA!=null && !"".equals(nomeSA) && !"-".equals(nomeSA)){
					PortaApplicativaServizioApplicativo sa = new PortaApplicativaServizioApplicativo();
					sa.setNome(nomeSA);
					pa.addServizioApplicativo(sa);
				}

				// ruolo
				if(erogazioneRuolo!=null && !"".equals(erogazioneRuolo) && !"-".equals(erogazioneRuolo)){
					if(pa.getRuoli()==null){
						pa.setRuoli(new AutorizzazioneRuoli());
					}
					Ruolo ruolo = new Ruolo();
					ruolo.setNome(erogazioneRuolo);
					pa.getRuoli().addRuolo(ruolo);
				}
			}

			if ((azione != null) && !azione.equals("") && !azione.equals("-")) {
				PortaApplicativaAzione paa = new PortaApplicativaAzione();

				//paa.setNome(azione);

				paa.setIdentificazione(PortaApplicativaAzioneIdentificazione.DELEGATED_BY); 
				paa.setNomePortaDelegante(nomePortaDelegante);
				paa.addAzioneDelegata(azione); 
				pa.setAzione(paa);
			}

			listaOggettiDaCreare.add(pa);


			MappingErogazionePortaApplicativa mappingErogazione = new MappingErogazionePortaApplicativa();
			IDPortaApplicativa idPortaApplicativa = new IDPortaApplicativa();
			idPortaApplicativa.setNome(pa.getNome());
			mappingErogazione.setIdPortaApplicativa(idPortaApplicativa);
			mappingErogazione.setIdServizio(idServizio2);
			mappingErogazione.setDefault(false);
			mappingErogazione.setNome(nome); 					

			listaOggettiDaCreare.add(mappingErogazione);

			String userLogin = ServletUtils.getUserLoginFromSession(session);		

			porteApplicativeCore.performCreateOperation(userLogin, porteApplicativeHelper.smista(), listaOggettiDaCreare.toArray());

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			int idLista = Liste.CONFIGURAZIONE_EROGAZIONE;

			ricerca = porteApplicativeHelper.checkSearchParameters(idLista, ricerca);

			List<MappingErogazionePortaApplicativa> lista = apsCore.mappingServiziPorteAppList(idServizio2,idServizio, Integer.parseInt(idSoggettoErogatoreDelServizio), ricerca);

			apsHelper.prepareServiziConfigurazioneList(lista, idAsps, null, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			// Forward control to the specified success URI
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE, 
					ForwardParams.ADD());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteSpecificaCostanti.OBJECT_NAME_APS_PORTE_APPLICATIVE,
					ForwardParams.ADD());
		}  
	}
}
