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
package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.pdd.core.jmx.JMXUtils;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.erogazioni.ErogazioniCostanti;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/***
 * 
 * PorteApplicativeVerificaConnettore
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PorteApplicativeVerificaConnettore extends Action {

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
			Boolean vistaErogazioni = ServletUtils.getBooleanAttributeFromSession(ErogazioniCostanti.ASPS_EROGAZIONI_ATTRIBUTO_VISTA_EROGAZIONI, session, request).getValue();
			// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte applicative
			Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session, request);
			if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;
			
			boolean isModalitaCompleta = porteApplicativeHelper.isModalitaCompleta();
			
			String id = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			
			String idTab = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteApplicativeHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(request, session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}
			
			String idConnTab = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_ID_CONN_TAB);
			if(StringUtils.isNotEmpty(idConnTab)) {
				ServletUtils.setObjectIntoSession(request, session, idConnTab, CostantiControlStation.PARAMETRO_ID_CONN_TAB);
			}

			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();
			ConfigurazioneCore confCore = new ConfigurazioneCore(porteApplicativeCore);
			ConnettoriCore connettoriCore = new ConnettoriCore(porteApplicativeCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(porteApplicativeCore);

			String tmpIdConnettore = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ID);
			long idConnettore = Long.parseLong(tmpIdConnettore);
			
			String tmpAccessoDaGruppi = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI);
			boolean accessoDaGruppi = "true".equalsIgnoreCase(tmpAccessoDaGruppi);
			
			String tmpConnettoreRegistro = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO);
			boolean connettoreRegistro = "true".equalsIgnoreCase(tmpConnettoreRegistro);
			
			boolean accessoDaListaAPS = false;
			String accessoDaAPSParametro = null;
			// nell'erogazione vale sempre
			accessoDaAPSParametro = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS);
			if(Costanti.CHECK_BOX_ENABLED_TRUE.equals(accessoDaAPSParametro)) {
				accessoDaListaAPS = true;
			}
			
			boolean accessoDaListaConnettoriMultipli = false;
			String accessoDaListaConnettoriMultipliParametro = null;
			accessoDaListaConnettoriMultipliParametro = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_LISTA_CONNETTORI_MULTIPLI);
			if(Costanti.CHECK_BOX_ENABLED_TRUE.equals(accessoDaListaConnettoriMultipliParametro)) {
				accessoDaListaConnettoriMultipli = true;
			}

			String alias = porteApplicativeHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER);
						
			// Prendo la lista di aliases
			List<String> aliases = confCore.getJmxPdDAliases();
			if(aliases==null || aliases.isEmpty()){
				throw new Exception("Pagina non prevista, la sezione configurazione non permette di accedere a questa pagina, se la configurazione non e' corretta");
			}
			
			
			org.openspcoop2.core.config.Connettore connettore = null;
			if(connettoreRegistro) {
				connettore = connettoriCore.getConnettoreRegistro(idConnettore).mappingIntoConnettoreConfigurazione();
			}
			else {
				connettore = connettoriCore.getConnettoreConfig(idConnettore);
			}
			String labelConnettore = porteApplicativeHelper.getLabelConnettore(connettore,false,false);
			
			long idSA = saCore.getIdServizioApplicativoByConnettore(idConnettore);
			ServizioApplicativo sa = null;
			String nomeServer = null;
			if(idSA>0) {
				sa = saCore.getServizioApplicativo(idSA);
				if(CostantiConfigurazione.SERVER.equals(sa.getTipo())) {
					nomeServer = sa.getNome();
				}
			}
			
			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String idporta = pa.getNome();

			
			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);
			
			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				if(!accessoDaGruppi) {
					lstParam.remove(lstParam.size()-1);
				}
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VERIFICA_CONNETTORE_CONFIGURAZIONE_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VERIFICA_CONNETTORE_CONFIGURAZIONE,
						pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VERIFICA_CONNETTORE_CONFIGURAZIONE_CONFIG_DI+idporta;
			}
			
			if(!accessoDaGruppi &&
				labelPerPorta.contains(CostantiControlStation.LABEL_DEL_GRUPPO)) {
				labelPerPorta = labelPerPorta.substring(0, labelPerPorta.indexOf(CostantiControlStation.LABEL_DEL_GRUPPO));
			}
			
			if(accessoDaListaConnettoriMultipli) {
				String labelListaConnettoriMultipli = null;
				if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
					
					AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(saCore);
					AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Integer.parseInt(idAsps));
					
					if(accessoDaListaAPS) {
						if(!isModalitaCompleta) {
							if(vistaErogazioni != null && vistaErogazioni.booleanValue()) {
								labelListaConnettoriMultipli = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI;
							} else {
								labelListaConnettoriMultipli = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DI+
										porteApplicativeHelper.getLabelIdServizio(asps);
							}
						}
						else {
							labelListaConnettoriMultipli = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI;
						}
					}
					else {
						labelListaConnettoriMultipli = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
								PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DI,
								PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI,
								pa);
					}
				}
				else {
					labelListaConnettoriMultipli = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_DI+pa.getNome();
				}
				
				List<Parameter> listParametersConfigutazioneConnettoriMultipli = new ArrayList<>();
				Parameter pIdPorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID, id);
				Parameter pNomePorta = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_NOME, pa.getNome());
				Parameter pIdSogg = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO, idsogg);
				Parameter pIdAsps = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS, idAsps);
				String idTabP = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
				Parameter pIdTab = new Parameter(CostantiControlStation.PARAMETRO_ID_TAB, idTabP != null ? idTabP : "");
				Parameter pAccessoDaAPS = new Parameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS, accessoDaAPSParametro != null ? accessoDaAPSParametro : "");
				
				Parameter pConnettoreAccessoDaGruppi = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_ACCESSO_DA_GRUPPI, tmpAccessoDaGruppi);
				Parameter pConnettoreAccesso = new Parameter(CostantiControlStation.PARAMETRO_VERIFICA_CONNETTORE_REGISTRO, tmpConnettoreRegistro);
				
				listParametersConfigutazioneConnettoriMultipli.add(pIdSogg);
				listParametersConfigutazioneConnettoriMultipli.add(pIdPorta);
				listParametersConfigutazioneConnettoriMultipli.add(pNomePorta);
				listParametersConfigutazioneConnettoriMultipli.add(pIdAsps);
				listParametersConfigutazioneConnettoriMultipli.add(pIdTab);
				listParametersConfigutazioneConnettoriMultipli.add(pAccessoDaAPS);
				listParametersConfigutazioneConnettoriMultipli.add(pConnettoreAccessoDaGruppi);
				listParametersConfigutazioneConnettoriMultipli.add(pConnettoreAccesso);
				
				lstParam.add(new Parameter(labelListaConnettoriMultipli,  PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_LIST, listParametersConfigutazioneConnettoriMultipli.toArray(new Parameter[1])));
				
				PortaApplicativaServizioApplicativo paSA = null;
				for (PortaApplicativaServizioApplicativo paSATmp : pa.getServizioApplicativoList()) {
					if(paSATmp.getNome().equals(sa.getNome())) {
						paSA = paSATmp;
						break;
					}
				}
				
				labelPerPorta = porteApplicativeHelper.getLabelNomePortaApplicativaServizioApplicativo(paSA);
			}
			
			lstParam.add(new Parameter(labelPerPorta,  null));

			
			
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// preparo i campi
			List<DataElement> dati = new ArrayList<>();
			dati.add(ServletUtils.getDataElementForEditModeFinished());

			DataElement deTestConnettivita = new DataElement();
			deTestConnettivita.setType(DataElementType.TITLE);
			deTestConnettivita.setLabel(PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_VERIFICA_CONNETTORE_TITLE);
			dati.add(deTestConnettivita);
			
			if(aliases.size()==1 || alias!=null) {

				porteApplicativeHelper.addDescrizioneVerificaConnettoreToDati(dati, nomeServer, labelConnettore, connettore, false, 
						(CostantiControlStation.LABEL_VERIFICA_CONNETTORE_TUTTI_I_NODI.equals(alias)) ? aliases.get(0) : (alias!=null ? alias : aliases.get(0))
						);
				
				if (!porteApplicativeHelper.isEditModeInProgress()) {
				
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
					String messagePerOperazioneEffettuata = "";
					int index = 0;
					for (String aliasForVerificaConnettore : aliasesForCheck) {
						
						String risorsa = null;
						if(connettoreRegistro) {
							risorsa = confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaAccessoRegistroServizi(aliasForVerificaConnettore);
						}
						else {
							risorsa = confCore.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(aliasForVerificaConnettore);
						}
						
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
							String stato = confCore.getInvoker().invokeJMXMethod(aliasForVerificaConnettore, confCore.getJmxPdDConfigurazioneSistemaType(aliasForVerificaConnettore),
									risorsa, 
									confCore.getJmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreById(aliasForVerificaConnettore),
									slowOperation,
									idConnettore+"");
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
								if(stato!=null) {
									if(stato.startsWith(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA)) {
										bfExternal.append(stato.substring(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA.length()));
									}
									else {
										bfExternal.append(stato);
									}
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
	
						if(messagePerOperazioneEffettuata.length()>0){
							messagePerOperazioneEffettuata+= org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE;
						}
						messagePerOperazioneEffettuata+= bfExternal.toString();
						
						index++;
					}
					if(messagePerOperazioneEffettuata!=null){
						if(rilevatoErrore)
							pd.setMessage(messagePerOperazioneEffettuata);
						else 
							pd.setMessage(messagePerOperazioneEffettuata,Costanti.MESSAGE_TYPE_INFO);
					}
	
					pd.disableEditMode();
					
				}
				
			}
			else {
				porteApplicativeHelper.addVerificaConnettoreSceltaAlias(aliases, dati);			
			}
			
			pd.setLabelBottoneInvia(CostantiControlStation.LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE_BOTTONE);
			
			porteApplicativeHelper.addVerificaConnettoreHidden(dati,
					id, idsogg, idAsps, null,
					idConnettore, accessoDaGruppi, connettoreRegistro);
			
			dati = porteApplicativeHelper.addInformazioniGruppiAsHiddenToDati(TipoOperazione.OTHER, dati, idTab, idConnTab, accessoDaAPSParametro != null ? accessoDaAPSParametro : "", 
					null, null, accessoDaListaConnettoriMultipliParametro);
			
			dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);

			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_VERIFICA_CONNETTORE,	ForwardParams.OTHER(""));
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_VERIFICA_CONNETTORE, ForwardParams.OTHER(""));
		}
	}

}
