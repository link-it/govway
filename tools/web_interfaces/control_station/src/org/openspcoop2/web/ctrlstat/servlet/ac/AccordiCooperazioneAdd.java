/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package org.openspcoop2.web.ctrlstat.servlet.ac;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.core.registry.driver.ValidazioneStatoPackageException;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
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
 * accordiCooperazioneAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiCooperazioneAdd extends Action {

	private String nome, descr, referente, versione;
	private boolean privato;// showPrivato;
	private String statoPackage;

	private String editMode = null;

	private String tipoProtocollo = null;

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);

		try {
			AccordiCooperazioneHelper acHelper = new AccordiCooperazioneHelper(request, pd, session);

			this.editMode = null;

			this.nome = request.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME);
			this.descr = request.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_DESCRIZIONE);
			this.referente = request.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_REFERENTE);
			this.versione = request.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_VERSIONE);
			this.tipoProtocollo = request.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO);
			// patch per version spinner fino a che non si trova un modo piu' elegante
			/*if(ch.core.isBackwardCompatibilityAccordo11()){
				if("0".equals(this.versione))
					this.versione = "";
			}*/
			this.privato = (request.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PRIVATO) != null)
					&& Costanti.CHECK_BOX_ENABLED.equals(request.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PRIVATO)) ? true : false;
			this.statoPackage = request.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_STATO);
			String tipoSICA = request.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_SICA);
			if("".equals(tipoSICA))
				tipoSICA = null;

			// boolean decodeReq = false;
			String ct = request.getContentType();
			if ((ct != null) && (ct.indexOf(Costanti.MULTIPART) != -1)) {
				// decodeReq = true;
				//this.decodeRequest(request,ch.core.isBackwardCompatibilityAccordo11());
				this.decodeRequest(request,false);
			}

			AccordiCooperazioneCore acCore = new AccordiCooperazioneCore();
			SoggettiCore soggettiCore = new SoggettiCore(acCore);

			// Preparo il menu
			acHelper.makeMenu();

			// Tipi protocollo supportati
			List<String> listaTipiProtocollo = acCore.getProtocolli();
			// primo accesso inizializzo con il protocollo di default
			if(this.tipoProtocollo == null){
				this.tipoProtocollo = acCore.getProtocolloDefault();
			}

			//Carico la lista dei tipi di soggetti gestiti dal protocollo
			List<String> tipiSoggettiGestitiProtocollo = soggettiCore.getTipiSoggettiGestitiProtocollo(this.tipoProtocollo);

			// Prendo la lista di provider e la metto in un array
			String[] providersList = null;
			String[] providersListLabel = null;

			// Provider
			/*
				int totProv = ch.getCounterFromDB("soggetti", "superuser", userLogin);
				if (totProv != 0) {
					providersList = new String[totProv+1];
					providersListLabel = new String[totProv+1];
					int i = 1;
					providersList[0]="-";
					providersListLabel[0]="-";

					String[] selectFields = new String[3];
					selectFields[0] = "id";
					selectFields[1] = "tipo_soggetto";
					selectFields[2] = "nome_soggetto";
					this.queryString = ch.getQueryStringForList(selectFields, "soggetti", "superuser", userLogin, "", 0, 0);
					this.stmt = con.prepareStatement(this.queryString);
					this.risultato = this.stmt.executeQuery();
					while (this.risultato.next()) {
						providersList[i] = "" + this.risultato.getInt("id");
						providersListLabel[i] = this.risultato.getString("tipo_soggetto") + "/" + this.risultato.getString("nome_soggetto");
						i++;
					}
					this.risultato.close();
					this.stmt.close();
				}else{
					providersList = new String[1];
					providersListLabel = new String[1];
					providersList[0]="-";
					providersListLabel[0]="-";
				}
			 */
			List<Soggetto> lista = null;
			if(acCore.isVisioneOggettiGlobale(userLogin)){
				lista = soggettiCore.soggettiRegistroList(null, new Search(true));
			}else{
				lista = soggettiCore.soggettiRegistroList(userLogin, new Search(true));
			}
			
			List<String> soggettiListTmp = new ArrayList<String>();
			List<String> soggettiListLabelTmp = new ArrayList<String>();
			soggettiListTmp.add("-");
			soggettiListLabelTmp.add("-");
			
			if (lista.size() > 0) {
				for (Soggetto soggetto : lista) {
					if(tipiSoggettiGestitiProtocollo.contains(soggetto.getTipo())){
						soggettiListTmp.add(soggetto.getId().toString());
						soggettiListLabelTmp.add(soggetto.getTipo() + "/" + soggetto.getNome());
					}
				}
			}
			providersList = soggettiListTmp.toArray(new String[1]);
			providersListLabel = soggettiListLabelTmp.toArray(new String[1]);
			
			// Se nome = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (ServletUtils.isEditModeInProgress(this.editMode) && ServletUtils.isEditModeInProgress(request)) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				if(acCore.isShowGestioneWorkflowStatoDocumenti()){
					if(this.statoPackage==null)
						this.statoPackage=StatiAccordo.bozza.toString();
				}else{
					this.statoPackage=StatiAccordo.finale.toString();
				}

				//if(core.isBackwardCompatibilityAccordo11()){
				//	this.versione="0";
				//}else{
				this.versione="1";
				//}
				
				if(this.nome == null)
					this.nome = "";
				
				if(this.descr == null)
					this.descr = "";
				
				if(this.referente == null)
					this.referente = "";


				dati = acHelper.addAccordiCooperazioneToDati(dati, this.nome, this.descr, "0", TipoOperazione.ADD, this.referente,
						this.versione, providersList, providersListLabel, false,this.statoPackage,this.statoPackage, this.tipoProtocollo, listaTipiProtocollo,false);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE,
						ForwardParams.ADD());
			}

			// Controlli sui campi immessi
			boolean isOk = acHelper.accordiCooperazioneCheckData(TipoOperazione.ADD, this.nome, this.descr, "0",
					this.referente, this.versione, this.privato, null);
			if (!isOk) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = acHelper.addAccordiCooperazioneToDati(dati, this.nome, this.descr, "0", TipoOperazione.ADD, 
						this.referente, this.versione, providersList, providersListLabel, this.privato,this.statoPackage,this.statoPackage, this.tipoProtocollo, listaTipiProtocollo,false);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE,
						ForwardParams.ADD());
			}

			// Inserisco l'accordo nel db
			AccordoCooperazione ac = new AccordoCooperazione();
			ac.setNome(this.nome);
			ac.setDescrizione(this.descr);
			ac.setOraRegistrazione(Calendar.getInstance().getTime());
			if(this.referente!=null && !"".equals(this.referente) && !"-".equals(this.referente)){
				int idRef = 0;
				try {
					idRef = Integer.parseInt(this.referente);
				} catch (Exception e) {
				}
				if (idRef != 0) {
					int idReferente = Integer.parseInt(this.referente);
					Soggetto s = soggettiCore.getSoggettoRegistro(idReferente);			
					IdSoggetto acsr = new IdSoggetto();
					acsr.setTipo(s.getTipo());
					acsr.setNome(s.getNome());
					ac.setSoggettoReferente(acsr);
				}
			}else{
				ac.setSoggettoReferente(null);
			}
			ac.setVersione(this.versione);
			ac.setPrivato(this.privato ? Boolean.TRUE : Boolean.FALSE);
			ac.setSuperUser(userLogin);

			// stato
			ac.setStatoPackage(this.statoPackage);

			// Check stato
			if(acCore.isShowGestioneWorkflowStatoDocumenti()){

				try{
					acCore.validaStatoAccordoCooperazione(ac);
				}catch(ValidazioneStatoPackageException validazioneException){

					// Setto messaggio di errore
					pd.setMessage(validazioneException.toString());

					// setto la barra del titolo
					List<Parameter> lstParam = new ArrayList<Parameter>();
					lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, null));
					lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null));

					ServletUtils.setPageDataTitle(pd, lstParam);

					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();

					dati.addElement(ServletUtils.getDataElementForEditModeFinished());

					dati = acHelper.addAccordiCooperazioneToDati(dati, this.nome, this.descr, "0", TipoOperazione.ADD, 
							this.referente, this.versione, providersList, providersListLabel, this.privato,this.statoPackage,this.statoPackage, this.tipoProtocollo, listaTipiProtocollo,false);

					pd.setDati(dati);

					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE,
							ForwardParams.ADD());
				}
			}


			// effettuo le operazioni
			acCore.performCreateOperation(userLogin, acHelper.smista(), ac);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<AccordoCooperazione> listaAC = null;
			if(acCore.isVisioneOggettiGlobale(userLogin)){
				listaAC = acCore.accordiCooperazioneList(null, ricerca);
			}else{
				listaAC = acCore.accordiCooperazioneList(userLogin, ricerca);
			}

			acHelper.prepareAccordiCooperazioneList(listaAC, ricerca);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE,
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiCooperazioneCostanti.OBJECT_NAME_ACCORDI_COOPERAZIONE, 
					ForwardParams.ADD());
		}  
	}

	public void decodeRequest(HttpServletRequest request,boolean isBackwardCompatibilityAccordo11) throws Exception {
		try {
			ServletInputStream in = request.getInputStream();
			BufferedReader dis = new BufferedReader(new InputStreamReader(in));
			String line = dis.readLine();
			while (line != null) {
				if (line.indexOf("\""+Costanti.DATA_ELEMENT_EDIT_MODE_NAME+"\"") != -1) {
					line = dis.readLine();
					this.editMode = dis.readLine();
				}
				if (line.indexOf("\""+AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME+"\"") != -1) {
					line = dis.readLine();
					this.nome = dis.readLine();
				}
				if (line.indexOf("\""+AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PROTOCOLLO+"\"") != -1) {
					line = dis.readLine();
					this.tipoProtocollo = dis.readLine();
				}
				if (line.indexOf("\""+AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_PRIVATO+"\"") != -1) {
					line = dis.readLine();
					this.privato = "yes".equals(dis.readLine().trim());
				}
				if (line.indexOf("\""+AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_DESCRIZIONE+"\"") != -1) {
					line = dis.readLine();
					this.descr = dis.readLine();
				}
				if (line.indexOf("\""+AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_REFERENTE+"\"") != -1) {
					line = dis.readLine();
					this.referente = dis.readLine();
				}
				if (line.indexOf("\""+AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_VERSIONE+"\"") != -1) {
					line = dis.readLine();
					this.versione = dis.readLine();
					// patch per version spinner fino a che non si trova un modo piu' elegante
					if(isBackwardCompatibilityAccordo11){
						if("0".equals(this.versione))
							this.versione = "";
					}
				}
				if (line.indexOf("\""+AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_STATO+"\"") != -1) {
					line = dis.readLine();
					this.statoPackage = dis.readLine();
				}
				line = dis.readLine();
			}
			in.close();
		} catch (IOException ioe) {
			throw new Exception(ioe);
		}
	}
}
