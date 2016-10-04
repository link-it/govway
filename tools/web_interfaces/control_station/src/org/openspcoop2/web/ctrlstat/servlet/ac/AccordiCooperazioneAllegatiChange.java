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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.TipiDocumentoSemiformale;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.FileUploadForm;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * accordiCoopAllegatiChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiCooperazioneAllegatiChange extends Action {

	@SuppressWarnings("incomplete-switch")
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);
		
		IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();

		try {
			
			FileUploadForm fileUpload = (FileUploadForm) form;
			
			AccordiCooperazioneHelper acHelper = new AccordiCooperazioneHelper(request, pd, session);
			ArchiviHelper archiviHelper = new ArchiviHelper(request, pd, session);

			String idAllegato = request.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID_ALLEGATO);
			String idAccordo = request.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID_ACCORDO);
			String nomeDocumento = request.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME_DOCUMENTO);
			int idAllegatoInt = Integer.parseInt(idAllegato);
			int idAccordoInt = Integer.parseInt(idAccordo);
			String tipoFile = request.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_FILE);
			
			String tipoSICA = request.getParameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_TIPO_SICA);
			if("".equals(tipoSICA))
				tipoSICA = null;
			
			FormFile ff = fileUpload.getTheFile();

			AccordiCooperazioneCore acCore = new AccordiCooperazioneCore();
			ArchiviCore archiviCore = new ArchiviCore(acCore);

			// Preparo il menu
			acHelper.makeMenu();

			// Prendo il nome
			AccordoCooperazione ac = acCore.getAccordoCooperazione(new Long(idAccordoInt));
			String uriAS = idAccordoCooperazioneFactory.getUriFromAccordo(ac);
			
			Documento doc = archiviCore.getDocumento(idAllegatoInt,false);
			
			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if (ServletUtils.isEditModeInProgress(request)) {
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
						AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST));
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_ALLEGATI_DI+ uriAS,
						AccordiCooperazioneCostanti.SERVLET_NAME_AC_ALLEGATI_LIST,
						new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, idAccordo),
						new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME, ac.getNome())));
				lstParam.add(new Parameter(nomeDocumento, null));
				
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = acHelper.addAllegatiToDati(TipoOperazione.CHANGE, idAllegato, idAccordo, doc, null,null, dati, ac.getStatoPackage(), true);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_AC_ALLEGATI,
						ForwardParams.CHANGE());
			}

			Documento toCheck = new Documento();
			toCheck.setRuolo(RuoliDocumento.valueOf(doc.getRuolo()).toString());
			toCheck.setByteContenuto(ff.getFileData());
			toCheck.setFile(ff.getFileName());
			switch (RuoliDocumento.valueOf(doc.getRuolo())) {
				case allegato:
					toCheck.setTipo(ff.getFileName().substring(ff.getFileName().lastIndexOf('.')+1, ff.getFileName().length()));
					break;

				case specificaSemiformale:
					toCheck.setTipo(TipiDocumentoSemiformale.toEnumConstant(tipoFile).getNome());
					break;
			}
			toCheck.setIdProprietarioDocumento(ac.getId());
			toCheck.setOraRegistrazione(new Date());
			toCheck.setId(doc.getId());
			// Controlli sui campi immessi
			boolean isOk = archiviHelper.accordiAllegatiCheckData(TipoOperazione.CHANGE,ff,toCheck,ProprietariDocumento.accordoCooperazione);
			if (!isOk) {
				 
				// setto la barra del titolo
				List<Parameter> lstParam = new ArrayList<Parameter>();
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE, null));
				lstParam.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
						AccordiCooperazioneCostanti.SERVLET_NAME_ACCORDI_COOPERAZIONE_LIST));
				lstParam.add(new Parameter(AccordiCooperazioneCostanti.LABEL_ACCORDI_COOPERAZIONE_ALLEGATI_DI+ uriAS,
						AccordiCooperazioneCostanti.SERVLET_NAME_AC_ALLEGATI_LIST,
						new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_ID, idAccordo),
						new Parameter(AccordiCooperazioneCostanti.PARAMETRO_ACCORDI_COOPERAZIONE_NOME, ac.getNome())));
				lstParam.add(new Parameter(nomeDocumento, null));
				
				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
			 
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				dati = acHelper.addAllegatiToDati(TipoOperazione.CHANGE, idAllegato, idAccordo, doc, null,null, 
						dati, null, false);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_AC_ALLEGATI,
						ForwardParams.CHANGE());
			}

			

			switch (RuoliDocumento.valueOf(doc.getRuolo())) {
				case allegato:
					//rimuovo il vecchio doc dalla lista
					for (int i = 0; i < ac.sizeAllegatoList(); i++) {
						Documento documento = ac.getAllegato(i);						
						if(documento.getId().equals(doc.getId())){
							ac.removeAllegato(i);
							break;
						}
					}
					//aggiungo il nuovo
					ac.addAllegato(toCheck);
					
					break;

				case specificaSemiformale:
					
					for (int i = 0; i < ac.sizeSpecificaSemiformaleList(); i++) {
						Documento documento = ac.getSpecificaSemiformale(i);						
						if(documento.getId().equals(doc.getId())){
							ac.removeSpecificaSemiformale(i);
							break;
						}
					}
					//aggiungo il nuovo
					ac.addSpecificaSemiformale(toCheck);
					break;
					
				default:
					break;
			}
			

			// effettuo le operazioni
			acCore.performUpdateOperation(userLogin, acHelper.smista(), ac);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			List<Documento> lista = acCore.accordiCoopAllegatiList(ac.getId().intValue(), ricerca);

			acHelper.prepareAccordiCoopAllegatiList(ac, ricerca, lista);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiCooperazioneCostanti.OBJECT_NAME_AC_ALLEGATI,
					ForwardParams.CHANGE());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiCooperazioneCostanti.OBJECT_NAME_AC_ALLEGATI, 
					ForwardParams.CHANGE());
		}  
	}
}
