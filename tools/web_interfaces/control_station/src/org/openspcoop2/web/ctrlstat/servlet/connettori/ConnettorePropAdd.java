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


package org.openspcoop2.web.ctrlstat.servlet.connettori;

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
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pa.PorteApplicativeCostanti;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCore;
import org.openspcoop2.web.ctrlstat.servlet.sa.ServiziApplicativiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCostanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * connettorePropAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class ConnettorePropAdd extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo PageData
		PageData pd = new PageData();

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			ConnettoriHelper connettoriHelper = new ConnettoriHelper(request, pd, session);

			String servlet = connettoriHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_SERVLET);
			String id = connettoriHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID);
			String nomeprov = connettoriHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SOGGETTO);
			String tipoprov = connettoriHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SOGGETTO);
			String nomeservizio = connettoriHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO);
			String tiposervizio = connettoriHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_SERVIZIO);
			String versioneservizio = connettoriHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_VERSIONE_SERVIZIO);
			String myId = connettoriHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_MY_ID);
			String correlato = connettoriHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_CORRELATO);
			String idSoggErogatore = connettoriHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SOGGETTO_EROGATORE);
			String nomeservizioApplicativo = connettoriHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME_SERVIZIO_APPLICATIVO);
			String idsil = connettoriHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_SERVIZIO_APPLICATIVO);

			String nome = connettoriHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_NOME);
			String valore = connettoriHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_VALORE);

			String provider = connettoriHelper.getParameter(ServiziApplicativiCostanti.PARAMETRO_SERVIZI_APPLICATIVI_PROVIDER);
			
			String tipoAccordo = connettoriHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;
			
			String idPorta = connettoriHelper.getParameter(ConnettoriCostanti.PARAMETRO_CONNETTORE_CUSTOM_ID_PORTA);
			if(idPorta == null)
				idPorta = "";
			
			String accessoDaAPSParametro = connettoriHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_CONNETTORE_DA_LISTA_APS);
			if(accessoDaAPSParametro == null)
				accessoDaAPSParametro = "";
			
			String azioneConnettoreIdPorta = connettoriHelper.getParameter(AccordiServizioParteSpecificaCostanti.PARAMETRO_APS_FRUITORE_VIEW_CONNETTORE_MAPPING_AZIONE_ID_PORTA);
			if(azioneConnettoreIdPorta==null) {
				azioneConnettoreIdPorta="";
			}
			
			ConnettoriCore connettoriCore = new ConnettoriCore();
			SoggettiCore soggettiCore = new SoggettiCore(connettoriCore);
			ServiziApplicativiCore saCore = new ServiziApplicativiCore(connettoriCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(connettoriCore);

			// Preparo il menu
			connettoriHelper.makeMenu();

			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if(connettoriHelper.isEditModeInProgress()){
				
				// setto la barra del titolo
				connettoriHelper.setTitleProprietaConnettoriCustom(pd, TipoOperazione.ADD, servlet, id, nomeprov, tipoprov, nomeservizio, tiposervizio, 
						myId, correlato, idSoggErogatore, nomeservizioApplicativo, idsil, tipoAccordo, provider,idPorta);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				ConnettoreCustomUtils.addProprietaConnettoriCustom(dati, nome, valore, servlet, id, nomeprov, tipoprov, nomeservizio, tiposervizio, versioneservizio,
						myId, correlato, idSoggErogatore, nomeservizioApplicativo, idsil, tipoAccordo, provider,accessoDaAPSParametro, idPorta, azioneConnettoreIdPorta);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, ConnettoriCostanti.OBJECT_NAME_CONNETTORI_CUSTOM_PROPERTIES, ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = connettoriHelper.connettorePropCheckData();
			if (!isOk) {
				
				// setto la barra del titolo
				connettoriHelper.setTitleProprietaConnettoriCustom(pd, TipoOperazione.ADD, servlet, id, nomeprov, tipoprov, nomeservizio, tiposervizio, 
						myId, correlato, idSoggErogatore, nomeservizioApplicativo, idsil, tipoAccordo, provider,idPorta);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				ConnettoreCustomUtils.addProprietaConnettoriCustom(dati, nome, valore, servlet, id, nomeprov, tipoprov, nomeservizio, tiposervizio, versioneservizio,
						myId, correlato, idSoggErogatore, nomeservizioApplicativo, idsil, tipoAccordo, provider,accessoDaAPSParametro, idPorta, azioneConnettoreIdPorta);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ConnettoriCostanti.OBJECT_NAME_CONNETTORI_CUSTOM_PROPERTIES, ForwardParams.ADD());
			}

			// Aggiorno il connettore nel db
			String userLogin = ServletUtils.getUserLoginFromSession(session);
			
			String saveNomeFru = "", saveTipoFru = "";
			if (servlet.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE)) {
				AccordoServizioParteSpecifica asps = apsCore.getAccordoServizioParteSpecifica(Long.parseLong(id));
				Connettore connettore = asps.getConfigurazioneServizio().getConnettore();
				connettore.setCustom(true);
				Property cp = new Property();
				cp.setNome(nome);
				cp.setValore(valore);
				connettore.addProperty(cp);
				asps.getConfigurazioneServizio().setConnettore(connettore);
				connettoriCore.performUpdateOperation(userLogin, connettoriHelper.smista(), asps);
			}
			else if (servlet.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE)) {
				int idServizioInt = Integer.parseInt(id);
				AccordoServizioParteSpecifica serviziosp = apsCore.getAccordoServizioParteSpecifica(idServizioInt);
				int idServizioFruitoreInt = Integer.parseInt(myId);
				Fruitore servFru = apsCore.getServizioFruitore(idServizioFruitoreInt);
				// Elimino il vecchio fruitore ed aggiungo il nuovo
				for (int i = 0; i < serviziosp.sizeFruitoreList(); i++) {
					Fruitore tmpFru = serviziosp.getFruitore(i);
					//System.out.println("["+tmpFru.getId().longValue()+"]==["+servFru.getId().longValue()+"]");
					if (tmpFru.getId().longValue() == servFru.getId().longValue()) {
						serviziosp.removeFruitore(i);
						break;
					}
				}
				Connettore connettore = servFru.getConnettore();
				connettore.setCustom(true);
				Property cp = new Property();
				cp.setNome(nome);
				cp.setValore(valore);
				connettore.addProperty(cp);
				servFru.setConnettore(connettore);
				serviziosp.addFruitore(servFru);
				connettoriCore.performUpdateOperation(userLogin, connettoriHelper.smista(), serviziosp);
				// Mi salvo i dati per identificare il fruitore
				saveNomeFru = servFru.getNome();
				saveTipoFru = servFru.getTipo();
			}
			else if (servlet.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT)) {
				int idSilInt = Integer.parseInt(idsil);
				ServizioApplicativo sa = saCore.getServizioApplicativo(idSilInt);
				InvocazioneServizio is = sa.getInvocazioneServizio();
				org.openspcoop2.core.config.Connettore connettore = is.getConnettore();
				connettore.setCustom(true);
				org.openspcoop2.core.config.Property cp =
					new org.openspcoop2.core.config.Property();
				cp.setNome(nome);
				cp.setValore(valore);
				connettore.addProperty(cp);
				is.setConnettore(connettore);
				sa.setInvocazioneServizio(is);
				connettoriCore.performUpdateOperation(userLogin, connettoriHelper.smista(), sa);
			}
			else if (servlet.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA)) {
				int idSilInt = Integer.parseInt(idsil);
				ServizioApplicativo sa = saCore.getServizioApplicativo(idSilInt);
				RispostaAsincrona ra = sa.getRispostaAsincrona();
				org.openspcoop2.core.config.Connettore connettore = ra.getConnettore();
				connettore.setCustom(true);
				org.openspcoop2.core.config.Property cp =
					new org.openspcoop2.core.config.Property();
				cp.setNome(nome);
				cp.setValore(valore);
				connettore.addProperty(cp);
				ra.setConnettore(connettore);
				sa.setRispostaAsincrona(ra);
				connettoriCore.performUpdateOperation(userLogin, connettoriHelper.smista(), sa);
			}
			else if (servlet.equals(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CHANGE)) {
				int idSilInt = Integer.parseInt(idsil);
				ServizioApplicativo sa = saCore.getServizioApplicativo(idSilInt);
				InvocazioneServizio is = sa.getInvocazioneServizio();
				org.openspcoop2.core.config.Connettore connettore = is.getConnettore();
				connettore.setCustom(true);
				org.openspcoop2.core.config.Property cp =
					new org.openspcoop2.core.config.Property();
				cp.setNome(nome);
				cp.setValore(valore);
				connettore.addProperty(cp);
				is.setConnettore(connettore);
				sa.setInvocazioneServizio(is);
				connettoriCore.performUpdateOperation(userLogin, connettoriHelper.smista(), sa);
			}
			else if (servlet.equals(SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT)) {
				int idInt = Integer.parseInt(id);
				SoggettoCtrlStat scs = soggettiCore.getSoggettoCtrlStat(idInt);
				Soggetto ss = scs.getSoggettoReg();
				org.openspcoop2.core.config.Soggetto ssconf = scs.getSoggettoConf();
				Connettore connettore = ss.getConnettore();
				connettore.setCustom(true);
				Property cp = new Property();
				cp.setNome(nome);
				cp.setValore(valore);
				connettore.addProperty(cp);
				ss.setConnettore(connettore);
				SoggettoCtrlStat newCsc = new SoggettoCtrlStat(ss, ssconf);
				connettoriCore.performUpdateOperation(userLogin, connettoriHelper.smista(), newCsc);
			}

			// Preparo la lista
			Connettore connettore = null;
			org.openspcoop2.core.config.Connettore connettoreC = null;
			int newMyId = 0;
			if (servlet.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_CHANGE)) {
				AccordoServizioParteSpecifica servizio = apsCore.getAccordoServizioParteSpecifica(Long.parseLong(id));
				connettore = servizio.getConfigurazioneServizio().getConnettore();
			}
			else if (servlet.equals(AccordiServizioParteSpecificaCostanti.SERVLET_NAME_APS_FRUITORI_CHANGE)) {
				//int idServizioFruitoreInt = Integer.parseInt(myId);
				int idServizioInt = Integer.parseInt(id);
				AccordoServizioParteSpecifica serviziosp = apsCore.getAccordoServizioParteSpecifica(idServizioInt);
				for (int i = 0; i < serviziosp.sizeFruitoreList(); i++) {
					Fruitore tmpFru = serviziosp.getFruitore(i);
					if (saveNomeFru.equals(tmpFru.getNome()) &&
							saveTipoFru.equals(tmpFru.getTipo())) {
						newMyId = tmpFru.getId().intValue();
						break;
					}
				}
				Fruitore servFru = apsCore.getServizioFruitore(newMyId);
				connettore = servFru.getConnettore();
			}
			else if (servlet.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT)) {
				int idSilInt = Integer.parseInt(idsil);
				ServizioApplicativo sa = saCore.getServizioApplicativo(idSilInt);
				InvocazioneServizio is = sa.getInvocazioneServizio();
				connettoreC = is.getConnettore();
			}
			else if (servlet.equals(ServiziApplicativiCostanti.SERVLET_NAME_SERVIZI_APPLICATIVI_ENDPOINT_RISPOSTA)) {
				int idSilInt = Integer.parseInt(idsil);
				ServizioApplicativo sa = saCore.getServizioApplicativo(idSilInt);
				RispostaAsincrona ra = sa.getRispostaAsincrona();
				connettoreC = ra.getConnettore();
			}
			else if (servlet.equals(PorteApplicativeCostanti.SERVLET_NAME_PORTE_APPLICATIVE_CONNETTORI_MULTIPLI_CHANGE)) {
				int idSilInt = Integer.parseInt(idsil);
				ServizioApplicativo sa = saCore.getServizioApplicativo(idSilInt);
				InvocazioneServizio is = sa.getInvocazioneServizio();
				connettoreC = is.getConnettore();
			}
			else if (servlet.equals(SoggettiCostanti.SERVLET_NAME_SOGGETTI_ENDPOINT)) {
				int idInt = Integer.parseInt(id);
				SoggettoCtrlStat scs = soggettiCore.getSoggettoCtrlStat(idInt);
				Soggetto ss = scs.getSoggettoReg();
				connettore = ss.getConnettore();
			}
			
			List<Object> lista = new ArrayList<Object>();
			if (connettore != null) {
				for (int i = 0; i<connettore.sizePropertyList(); i++){
					if(CostantiDB.CONNETTORE_DEBUG.equals(connettore.getProperty(i).getNome())==false  &&
							connettore.getProperty(i).getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)==false){
						lista.add(connettore.getProperty(i));
					}
				}
			}
			if (connettoreC != null) {
				for (int i = 0; i<connettoreC.sizePropertyList(); i++){
					if(CostantiDB.CONNETTORE_DEBUG.equals(connettoreC.getProperty(i).getNome())==false  &&
							connettoreC.getProperty(i).getNome().startsWith(CostantiConnettori.CONNETTORE_EXTENDED_PREFIX)==false){
						lista.add(connettoreC.getProperty(i));
					}
				}
			}

			connettoriHelper.prepareConnettorePropList(lista, new ConsoleSearch(true), newMyId, tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ConnettoriCostanti.OBJECT_NAME_CONNETTORI_CUSTOM_PROPERTIES, ForwardParams.ADD());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					ConnettoriCostanti.OBJECT_NAME_CONNETTORI_CUSTOM_PROPERTIES, ForwardParams.ADD());
		} 
	}
}
