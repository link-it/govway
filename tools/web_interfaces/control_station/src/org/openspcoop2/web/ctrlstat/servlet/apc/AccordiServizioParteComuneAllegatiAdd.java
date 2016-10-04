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


package org.openspcoop2.web.ctrlstat.servlet.apc;

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
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.TipiDocumentoCoordinamento;
import org.openspcoop2.core.registry.constants.TipiDocumentoSemiformale;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.FileUploadForm;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * accordiAllegatiAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneAllegatiAdd extends Action {

	@SuppressWarnings("incomplete-switch")
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);
		
		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = (String) ServletUtils.getUserLoginFromSession(session);

		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
		
		try {
			Boolean isShowAccordiCooperazione = (Boolean)session.getAttribute(CostantiControlStation.SESSION_PARAMETRO_VISUALIZZA_ACCORDI_COOPERAZIONE);
			
			FileUploadForm fileUpload = (FileUploadForm) form;
			
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);
			ArchiviHelper archiviHelper = new ArchiviHelper(request, pd, session);

			String idAccordo = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			int idAccordoInt = Integer.parseInt(idAccordo);
			String ruolo = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_RUOLO);
			String tipoFile = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_TIPO_FILE);
			
			String tipoAccordo = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;

			FormFile ff = fileUpload.getTheFile();
			
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();

			// Preparo il menu
			apcHelper.makeMenu();

			// Prendo il nome
			AccordoServizioParteComune as = apcCore.getAccordoServizio(new Long(idAccordoInt));
			String uriAS = idAccordoFactory.getUriFromAccordo(as);
			
			String[] ruoli = null;
			if(isShowAccordiCooperazione){
				ruoli = as.getServizioComposto() ==null ? new String[2] : new String[3];
			}else{
				ruoli = new String[2];
			}
			
			
			ruoli[0]=RuoliDocumento.allegato.toString();
			ruoli[1]=RuoliDocumento.specificaSemiformale.toString();
			
			if(isShowAccordiCooperazione){
				if(as.getServizioComposto()!=null) ruoli[2]=RuoliDocumento.specificaCoordinamento.toString();
			}
			
			String[] tipiAmmessi = null;
			String[] tipiAmmessiLabel = null;
			
			if(ruolo!=null && !"".equals(ruolo)){
				switch (RuoliDocumento.valueOf(ruolo)) {
					case allegato:
						//non ci sono vincoli
						break;
					case specificaCoordinamento:
						tipiAmmessi = TipiDocumentoCoordinamento.toEnumNameArray();
						tipiAmmessiLabel=TipiDocumentoCoordinamento.toStringArray();
						break;
					case specificaSemiformale:
						tipiAmmessi = TipiDocumentoSemiformale.toEnumNameArray();
						tipiAmmessiLabel=TipiDocumentoSemiformale.toStringArray();
						break;
					
				}
			}
			
			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if(ServletUtils.isEditModeInProgress(request)){
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
						new Parameter(AccordiServizioParteComuneCostanti.LABEL_ALLEGATI + " di " + uriAS, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_ALLEGATI_LIST+"?"+
										AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID+"="+idAccordo+"&"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null)
				);

				// preparo i campi
				Vector<Object> dati = new Vector<Object>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				apcHelper.addAccordiAllegatiToDati(dati,TipoOperazione.ADD,idAccordo,
						ruolo,ruoli,tipiAmmessi,tipiAmmessiLabel,tipoAccordo,
						null,null,as,null,null);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_ALLEGATI, ForwardParams.ADD());
			}

			
			Documento documento = new Documento();
			documento.setRuolo(RuoliDocumento.valueOf(ruolo).toString());
			documento.setByteContenuto(ff.getFileData());
			documento.setFile(ff.getFileName());

			switch (RuoliDocumento.valueOf(ruolo)) {
				case allegato:
					documento.setTipo(ff.getFileName().substring(ff.getFileName().lastIndexOf('.')+1, ff.getFileName().length()));
					break;
				case specificaSemiformale:
					documento.setTipo(TipiDocumentoSemiformale.valueOf(tipoFile).getNome());
					break;
				case specificaCoordinamento:
					documento.setTipo(TipiDocumentoCoordinamento.valueOf(tipoFile).getNome());
					break;
			}
			documento.setIdProprietarioDocumento(as.getId());
		
			// Controlli sui campi immessi
			boolean isOk = archiviHelper.accordiAllegatiCheckData(TipoOperazione.ADD,ff,documento,ProprietariDocumento.accordoServizio);
			if (!isOk) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),null),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST+"?"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
						new Parameter(AccordiServizioParteComuneCostanti.LABEL_ALLEGATI + " di " + uriAS, 
								AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_ALLEGATI_LIST+"?"+
										AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID+"="+idAccordo+"&"+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getName()+"="+
										AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo).getValue()),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null)
				);

				// preparo i campi
				Vector<Object> dati = new Vector<Object>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				apcHelper.addAccordiAllegatiToDati(dati,TipoOperazione.ADD,idAccordo,
						ruolo,ruoli,tipiAmmessi,tipiAmmessiLabel,tipoAccordo,
						null,null,as,null,null);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_ALLEGATI, ForwardParams.ADD());
			}

			//inserimento documento in accordo
			
			switch (RuoliDocumento.valueOf(ruolo)) {
				case allegato:
					as.addAllegato(documento);
					break;
				case specificaSemiformale:
					as.addSpecificaSemiformale(documento);
					break;
				case specificaCoordinamento:
					AccordoServizioParteComuneServizioComposto assc = as.getServizioComposto();
					if(assc==null)
						assc=new AccordoServizioParteComuneServizioComposto();
					assc.addSpecificaCoordinamento(documento);
					as.setServizioComposto(assc);
					break;
				default:
					break;
			}
			
			// effettuo le operazioni
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			List<Documento> lista = apcCore.accordiAllegatiList(idAccordoInt, ricerca);

			apcHelper.prepareAccordiAllegatiList(as, ricerca, lista, tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_ALLEGATI, ForwardParams.ADD());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_ALLEGATI, ForwardParams.ADD());
		}
	}
}
