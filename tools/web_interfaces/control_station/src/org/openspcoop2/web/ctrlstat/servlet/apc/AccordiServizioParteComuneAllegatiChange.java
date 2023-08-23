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


package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.govway.struts.upload.FormFile;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.constants.RuoliDocumento;
import org.openspcoop2.core.registry.constants.TipiDocumentoCoordinamento;
import org.openspcoop2.core.registry.constants.TipiDocumentoSemiformale;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.servlet.FileUploadForm;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiCostanti;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviCore;
import org.openspcoop2.web.ctrlstat.servlet.archivi.ArchiviHelper;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * accordiAllegatiChange
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class AccordiServizioParteComuneAllegatiChange extends Action {

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
			
			FileUploadForm fileUpload = (FileUploadForm) form;
			
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);
			ArchiviHelper archiviHelper = new ArchiviHelper(request, pd, session);

			String idAllegato = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ALLEGATO);
			String idAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_ID_ACCORDO);
			String nomeDocumento = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_NOME_DOCUMENTO);
			String tipoFile = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ALLEGATI_TIPO_FILE);
			
			long idAllegatoLong = Long.parseLong(idAllegato);
			long idAccordoLong = Long.parseLong(idAccordo);
			
			String tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;
			
			FormFile ff = fileUpload.getTheFile();

			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			ArchiviCore archiviCore = new ArchiviCore(apcCore);

			// Preparo il menu
			apcHelper.makeMenu();

			// Prendo il nome
			AccordoServizioParteComune as = apcCore.getAccordoServizioFull(idAccordoLong);
			String labelASTitle = apcHelper.getLabelIdAccordo(as); 
			
			Documento doc = archiviCore.getDocumento(idAllegatoLong,false);
			
			IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
			String uri = idAccordoFactory.getUriFromAccordo(as);
			Parameter pTipoAccordo = AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo);
			Parameter pIdAccordo = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, idAccordo);
			Parameter pNomeAccordo = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, uri);
			IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByOrganizationType(as.getSoggettoReferente().getTipo());
			
			Boolean isModalitaVistaApiCustom = ServletUtils.getBooleanAttributeFromSession(ApiCostanti.SESSION_ATTRIBUTE_VISTA_APC_API, session, request, false).getValue();
			List<Parameter> listaParams = apcHelper.getTitoloApc(TipoOperazione.ADD, as, tipoAccordo, labelASTitle, null, false);
			
			String labelAllegati = (isModalitaVistaApiCustom!=null && isModalitaVistaApiCustom.booleanValue()) ? AccordiServizioParteComuneCostanti.LABEL_ALLEGATI : AccordiServizioParteComuneCostanti.LABEL_ALLEGATI + " di " + labelASTitle;
			 
			listaParams.add(new Parameter(labelAllegati, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_ALLEGATI_LIST, pIdAccordo, pNomeAccordo, pTipoAccordo));
			listaParams.add(new Parameter(nomeDocumento, null));
			
			// Se idhid = null, devo visualizzare la pagina per la
			// modifica dati
			if(apcHelper.isEditModeInProgress()){
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);
				
				// preparo i campi
				List<Object> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				apcHelper.addAccordiAllegatiToDati(dati,TipoOperazione.CHANGE,idAccordo,
						null,null,null,null,tipoAccordo,tipoFile,
						idAllegato,doc,as,null,null,null);
							
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_ALLEGATI, ForwardParams.CHANGE());
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
				case specificaCoordinamento:
					toCheck.setTipo(TipiDocumentoCoordinamento.toEnumConstant(tipoFile).getNome());
					break;
				default:
					break;
			}
			toCheck.setIdProprietarioDocumento(as.getId());
			toCheck.setOraRegistrazione(new Date());
			toCheck.setId(doc.getId());
			
			// Controlli sui campi immessi
			boolean isOk = archiviHelper.accordiAllegatiCheckData(TipoOperazione.CHANGE,ff,toCheck,ProprietariDocumento.accordoServizio, pf);
			if (!isOk) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, listaParams);
				
				// preparo i campi
				List<Object> dati = new ArrayList<>();

				dati.add(ServletUtils.getDataElementForEditModeFinished());

				apcHelper.addAccordiAllegatiToDati(dati,TipoOperazione.CHANGE,idAccordo,
						null,null,null,null,tipoAccordo,tipoFile,
						idAllegato,doc,as,null,null,null);
								
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_ALLEGATI, ForwardParams.CHANGE());
			}

			AccordiServizioParteComuneUtilities.updateAccordoServizioParteComuneAllegati(as, doc, toCheck);			

			// effettuo le operazioni
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), as);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			
			List<Documento> lista = apcCore.accordiAllegatiList(as.getId().intValue(), ricerca);

			apcHelper.prepareAccordiAllegatiList(as, ricerca, lista, tipoAccordo);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_ALLEGATI, ForwardParams.CHANGE());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_ALLEGATI, ForwardParams.CHANGE());
		} 
	}
}
