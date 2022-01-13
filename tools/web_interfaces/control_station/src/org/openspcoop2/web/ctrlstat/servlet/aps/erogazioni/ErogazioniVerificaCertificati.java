/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.pdd.core.jmx.JMXUtils;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
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
			String id = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID);
			long idInt  = Long.parseLong(id);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore();
			AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(idInt);
			String idsogg = apsHelper.getParameter(CostantiControlStation.PARAMETRO_ID_SOGGETTO);
			
			String tipoSoggettoFruitore = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE);
			String nomeSoggettoFruitore = apsHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE);
			IDSoggetto idSoggettoFruitore = null;
			if(tipoSoggettoFruitore!=null && !"".equals(tipoSoggettoFruitore) &&
					nomeSoggettoFruitore!=null && !"".equals(nomeSoggettoFruitore)) {
				idSoggettoFruitore = new IDSoggetto(tipoSoggettoFruitore, nomeSoggettoFruitore);
			}
			
			String alias = apsHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER);
			
			String tipologia = ServletUtils.getObjectFromSession(session, String.class, AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE);
			boolean gestioneFruitori = false;
//			boolean gestioneErogatori = false;
			if(tipologia!=null) {
				if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_FRUIZIONE.equals(tipologia)) {
					gestioneFruitori = true;
				}
//				else if(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_EROGAZIONE_VALUE_EROGAZIONE.equals(tipologia)) {
//					gestioneErogatori = true;
//				}
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
			
			// Preparo il menu
			apsHelper.makeMenu();
			
			ConfigurazioneCore confCore = new ConfigurazioneCore(apsCore);
			SoggettiCore soggettiCore = new SoggettiCore(apsCore);
			
			// Prendo la lista di aliases
			List<String> aliases = confCore.getJmxPdD_aliases();
			if(aliases==null || aliases.size()<=0){
				throw new Exception("Pagina non prevista, la sezione configurazione non permette di accedere a questa pagina, se la configurazione non e' corretta");
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
			Parameter pNomeServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SERVIZIO, asps.getNome());
			Parameter pTipoServizio = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SERVIZIO, asps.getTipo());
			Parameter pTipoSoggettoFruitore = null;
			Parameter pNomeSoggettoFruitore = null;
			if(gestioneFruitori) {
				pTipoSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_TIPO_SOGGETTO_FRUITORE, tipoSoggettoFruitore);
				pNomeSoggettoFruitore = new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_NOME_SOGGETTO_FRUITORE, nomeSoggettoFruitore);
			}
			
			listParameterChange.add(new Parameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_ID, asps.getId() + ""));
			listParameterChange.add(pNomeServizio);
			listParameterChange.add(pTipoServizio);
			listParameterChange.add(pIdsoggErogatore);
			
			List<Parameter> lstParm = new ArrayList<Parameter>();

			if(gestioneFruitori) {
				lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_FRUIZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
				
				listParameterChange.add(pTipoSoggettoFruitore);
				listParameterChange.add(pNomeSoggettoFruitore);
			}
			else {
				lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_EROGAZIONI, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_LIST));
			}
			lstParm.add(new Parameter(tmpTitle, ErogazioniCostanti.SERVLET_NAME_ASPS_EROGAZIONI_CHANGE, listParameterChange.toArray(new Parameter[listParameterChange.size()])));
			lstParm.add(new Parameter(ErogazioniCostanti.LABEL_ASPS_VERIFICA_CERTIFICATI, null));

			// setto la barra del titolo
			ServletUtils.setPageDataTitle(pd, lstParm );
			
			Vector<DataElement> dati = new Vector<DataElement>();
			dati.addElement(ServletUtils.getDataElementForEditModeFinished());

			DataElement deTestConnettivita = new DataElement();
			deTestConnettivita.setType(DataElementType.TITLE);
			deTestConnettivita.setLabel(ErogazioniCostanti.LABEL_ASPS_VERIFICA_CERTIFICATI);
			dati.add(deTestConnettivita);
			
			if(aliases.size()==1 || alias!=null) {
				apsHelper.addDescrizioneVerificaCertificatoToDati(dati, null, true, 
						(CostantiControlStation.LABEL_VERIFICA_CONNETTORE_TUTTI_I_NODI.equals(alias)) ? aliases.get(0) : (alias!=null ? alias : aliases.get(0))
						);
				
				if (!apsHelper.isEditModeInProgress()) {
					
					List<String> aliases_for_check = new ArrayList<>();
					if(aliases.size()==1) {
						aliases_for_check.add(aliases.get(0));
					}
					else if(CostantiControlStation.LABEL_VERIFICA_CONNETTORE_TUTTI_I_NODI.equals(alias)) {
						aliases_for_check.addAll(aliases);
					}
					else {
						aliases_for_check.add(alias);
					}
									
					boolean rilevatoErrore = false;
					String messagePerOperazioneEffettuata = "";
					int index = 0;
					for (String aliasForVerificaConnettore : aliases_for_check) {
						
						// TODO Poli rimuovere
						boolean connettoreRegistro = false;
						long idConnettore = 1; 
						
						String risorsa = null;
						if(connettoreRegistro) {
							risorsa = confCore.getJmxPdD_configurazioneSistema_nomeRisorsaAccessoRegistroServizi(aliasForVerificaConnettore);
						}
						else {
							risorsa = confCore.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(aliasForVerificaConnettore);
						}
						
						StringBuilder bfExternal = new StringBuilder();
						String descrizione = confCore.getJmxPdD_descrizione(aliasForVerificaConnettore);
						if(aliases.size()>1) {
							if(index>0) {
								bfExternal.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
							}
							bfExternal.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER).append(" ").append(descrizione).append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
						}						
						try{
							String stato = confCore.getInvoker().invokeJMXMethod(aliasForVerificaConnettore, confCore.getJmxPdD_configurazioneSistema_type(aliasForVerificaConnettore),
									risorsa, 
									confCore.getJmxPdD_configurazioneSistema_nomeMetodo_checkConnettoreById(aliasForVerificaConnettore), 
									idConnettore+"");
							if(JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO.equals(stato)){
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
				
			} else {
				apsHelper.addVerificaCertificatoSceltaAlias(aliases, dati);	
			}
			
			pd.setLabelBottoneInvia(CostantiControlStation.LABEL_CONFIGURAZIONE_VERIFICA_CONNETTORE_BOTTONE);
			
			
			if(idSoggettoFruitore != null) {
				dati = apsHelper.addHiddenFieldsToDati(tipoOp, id, idsogg, id, asps.getId()+"", idFruizione, tipoSoggettoFruitore, nomeSoggettoFruitore, dati);
			}else {
				dati = apsHelper.addHiddenFieldsToDati(TipoOperazione.OTHER, asps.getId()+"", null, null, dati);
			}

			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeInProgress(mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI_VERIFICA_CERTIFICATI, ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, ErogazioniCostanti.OBJECT_NAME_ASPS_EROGAZIONI_VERIFICA_CERTIFICATI, ForwardParams.OTHER(""));
		}  
	}
}
