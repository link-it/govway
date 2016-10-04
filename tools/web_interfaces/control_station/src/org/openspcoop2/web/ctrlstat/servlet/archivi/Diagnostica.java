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


package org.openspcoop2.web.ctrlstat.servlet.archivi;

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
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.OpenspcoopSorgenteDati;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.aps.AccordiServizioParteSpecificaCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
//import org.openspcoop2.web.lib.users.dao.User;

/**
 * diagnostica
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class Diagnostica extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			
			ArchiviHelper archiviHelper = new ArchiviHelper(request, pd, session);
		
			String severita = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DIAGNOSTICI_SEVERITA);
			String idfunzione = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_ID_FUNZIONE);
			String datainizio = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_INIZIO);
			String datafine = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_DATA_FINE);
			String nomeDs = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_NOME_DATASOURCE);
			
			String index = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_INDEX);
			String postBackElementName=ServletUtils.getPostBackElementName(request);
			boolean showList = false;
			if(index!=null && !ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO.equals(postBackElementName) ){
				showList = true;
			}

			ArchiviCore archiviCore = new ArchiviCore();
			SoggettiCore soggettiCore = new SoggettiCore(archiviCore);
			AccordiServizioParteSpecificaCore apsCore = new AccordiServizioParteSpecificaCore(archiviCore);
			
			// Preparo il menu
			archiviHelper.makeMenu();
			//User user = ServletUtils.getUserFromSession(session);
			
			String protocollo = request.getParameter(ArchiviCostanti.PARAMETRO_ARCHIVI_PROTOCOLLO);
			
            List<String> tipiSoggetti = new ArrayList<String>();
            tipiSoggetti.add("-");
            if(protocollo!=null && !"".equals(protocollo) && !"-".equals(protocollo)){
            	tipiSoggetti.addAll(soggettiCore.getTipiSoggettiGestitiProtocollo(protocollo));
            }else{
            	tipiSoggetti.addAll(soggettiCore.getTipiSoggettiGestiti());
            }
            String[] tipiLabel = tipiSoggetti.toArray(new String[1]);
            
			List<String> tipiServizi = new ArrayList<String>();
			tipiServizi.add("-");
			if(protocollo!=null && !"".equals(protocollo) && !"-".equals(protocollo)){
				tipiServizi.addAll(apsCore.getTipiServiziGestitiProtocollo(protocollo));
			}else{
				tipiServizi.addAll(apsCore.getTipiServiziGestiti());
			}
			String[] tipiServiziLabel = tipiServizi.toArray(new String[1]);
			
			List<String> protocolliP = new ArrayList<String>();
			protocolliP.add("-");
			protocolliP.addAll(archiviCore.getProtocolli());
			String[]protocolli = protocolliP.toArray(new String[1]);
			
			// Prendo la lista di sorgenti dati e la metto in un array
			String[] datasourceList = null;
			Configurazione newConfigurazione = archiviCore.getConfigurazioneGenerale();
			MessaggiDiagnostici md = newConfigurazione.getMessaggiDiagnostici();
			List<OpenspcoopSorgenteDati> lista = null;
			if (md != null) {
				lista = md.getOpenspcoopSorgenteDatiList();
				if (md.sizeOpenspcoopSorgenteDatiList() > 0) {
					datasourceList = new String[md.sizeOpenspcoopSorgenteDatiList()+1];
					datasourceList[0] = "-";
					int i = 1;
					for (OpenspcoopSorgenteDati ds : lista) {
						datasourceList[i] = ds.getNome();
						i++;
					}
				}
			}
			if(datasourceList==null){
				datasourceList = new String[1];
				datasourceList[0] = "-";
			}
			
			if(showList==false){
			
				// Se severita == null, devo visualizzare la pagina con il pulsante
				if(ServletUtils.isEditModeInProgress(request)){
	
					// setto la barra del titolo
					ServletUtils.setPageDataTitle(pd, 
							new Parameter(ArchiviCostanti.LABEL_REPORTISTICA,null),
							new Parameter(ArchiviCostanti.LABEL_DIAGNOSTICA,null));
	
					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();
	
					dati.addElement(ServletUtils.getDataElementForEditModeFinished());
	
					archiviHelper.addDiagnosticaToDati(dati, datasourceList,nomeDs,
							datainizio,datafine,severita,idfunzione,protocolli,tipiLabel,tipiServiziLabel,"0");
					
					pd.setDati(dati);
	
					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
					
					return ServletUtils.getStrutsForwardEditModeInProgress(mapping, ArchiviCostanti.OBJECT_NAME_ARCHIVI_DIAGNOSTICA, 
							ArchiviCostanti.TIPO_OPERAZIONE_DIAGNOSTICA);
				}
				
			}
			
			// Controlli sui campi immessi
			boolean isOk = archiviHelper.diagnosticaCheckData();
			if (!isOk) {
				
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(ArchiviCostanti.LABEL_REPORTISTICA,null),
						new Parameter(ArchiviCostanti.LABEL_DIAGNOSTICA,null));

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				archiviHelper.addDiagnosticaToDati(dati, datasourceList,nomeDs,
						datainizio,datafine,severita,idfunzione,protocolli,tipiLabel,tipiServiziLabel,"0");
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
				
				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ArchiviCostanti.OBJECT_NAME_ARCHIVI_DIAGNOSTICA, 
						ArchiviCostanti.TIPO_OPERAZIONE_DIAGNOSTICA);
			}

			// ritorno pagina a lista
			boolean forceChange = false;
			if (nomeDs != null && !nomeDs.equals("") && !nomeDs.equals("-")){
				forceChange = true;
			}
			if(nomeDs!=null && nomeDs.equals("")){
				nomeDs = null;
			}
			if(nomeDs!=null && nomeDs.equals("-")){
				nomeDs = null;
			}
			
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);
			
			int idLista = Liste.MESSAGGI_DIAGNOSTICI;
			ricerca = archiviHelper.checkSearchParameters(idLista, ricerca);
			
			boolean ersql = archiviHelper.diagnosticaStatoList(nomeDs, forceChange, ricerca);
			if (ersql) {
				return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), null, pd, session, gd, mapping, 
						ArchiviCostanti.OBJECT_NAME_ARCHIVI_DIAGNOSTICA,
						ArchiviCostanti.TIPO_OPERAZIONE_DIAGNOSTICA);
			}

			ServletUtils.setSearchObjectIntoSession(session, ricerca);
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ArchiviCostanti.OBJECT_NAME_ARCHIVI_DIAGNOSTICA, 
					ArchiviCostanti.TIPO_OPERAZIONE_DIAGNOSTICA);
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ArchiviCostanti.OBJECT_NAME_ARCHIVI_DIAGNOSTICA,
					ArchiviCostanti.TIPO_OPERAZIONE_DIAGNOSTICA);
		}
	}
}
