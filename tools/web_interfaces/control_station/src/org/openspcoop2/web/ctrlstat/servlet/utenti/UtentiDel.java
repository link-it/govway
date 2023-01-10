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


package org.openspcoop2.web.ctrlstat.servlet.utenti;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.users.dao.Permessi;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.lib.users.dao.UserObjects;

/**
 * suDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public final class UtentiDel extends Action {

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
			UtentiHelper utentiHelper = new UtentiHelper(request, pd, session);
	
			String objToRemove = utentiHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			String singleSuServizi = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_SINGLE_SU_SERVIZI);
			String singleSuAccordiCooperazione = utentiHelper.getParameter(UtentiCostanti.PARAMETRO_UTENTI_SINGLE_SU_ACCORDI_COOPERAZIONE);
	
			// Preparo il menu
			utentiHelper.makeMenu();
	
			UtentiCore utentiCore = new UtentiCore();
			SoggettiCore soggettiCore = new SoggettiCore(utentiCore);
			
			// Elimino i superutenti dal db
			StringTokenizer objTok = new StringTokenizer(objToRemove, ",");
			int[] idToRemove = new int[objTok.countTokens()];
			List<User> utentiDaRimuovere = new ArrayList<User>();
			List<String> nomiUtentiDaRimuovere = new ArrayList<String>();
	
			int k = 0;
			while (objTok.hasMoreElements()) {
				String id = objTok.nextToken();
				idToRemove[k++] = Integer.parseInt(id);
				User user = utentiCore.getUser(Long.parseLong(id));
				utentiDaRimuovere.add(user);
				nomiUtentiDaRimuovere.add(user.getLogin());
			}
			
			// controllo protocolli associati agli utenti che provo ad eliminare
			String msgErroreModalita = null;
			List<String> utentiDaNonEliminare = new ArrayList<String>();
			for (User user : utentiDaRimuovere) {
				if(!user.hasOnlyPermessiUtenti())
					utentiHelper.controlloModalitaUtenteDaEliminare(nomiUtentiDaRimuovere, utentiDaNonEliminare, user);
			}
			
			if(utentiDaNonEliminare.size()> 0) {
				if(utentiDaNonEliminare.size()> 1) {
					StringBuilder sbUL = new StringBuilder();
					for (String userL : utentiDaNonEliminare) {
						if(sbUL.length() >0 )
							sbUL.append(", ");
						sbUL.append(userL);
					}
					msgErroreModalita = "Gli utenti "+sbUL.toString()+" non sono stati eliminati poich&egrave; sono stati rilevati oggetti, appartenenti a dei "+
							org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLI_DI_HTML_ESCAPE+", non assegnabili a nessun altro utente";
				} else {
					msgErroreModalita = "L'utente " +utentiDaNonEliminare.get(0) +" non &egrave; stato eliminato poich&egrave; sono stati rilevati oggetti, appartenenti a dei "+
							org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLI_DI_HTML_ESCAPE+", non assegnabili a nessun altro utente";
				}

				List<String> newList = new ArrayList<>();
				for (String uDE : nomiUtentiDaRimuovere) {
					if(utentiDaNonEliminare.contains(uDE) == false) {
						newList.add(uDE);
					}
				}
				if(newList.isEmpty()) {
					Vector<DataElement> dati = new Vector<DataElement>();
					
					dati.addElement(ServletUtils.getDataElementForEditModeFinished());
					
					pd.disableEditMode();
					
					pd.setDati(dati);
					
					// Preparo il menu
					pd.setMessage(msgErroreModalita);

					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
					
					return ServletUtils.getStrutsForwardGeneralError(mapping, UtentiCostanti.OBJECT_NAME_UTENTI, ForwardParams.DEL());	
				}
				else {
					nomiUtentiDaRimuovere = newList;
				}
			}
			
			
			
			
		    List<String> usersWithU = utentiCore.getUsersWithType(Permessi.UTENTI.toString());
		    List<String> usersWithS = utentiCore.getUsersWithType(Permessi.SERVIZI.toString());
		    
		    String[] uws = null;
		    if (usersWithS != null && usersWithS.size() > 0) {
		    	List<String> usersWithPermessoS = new Vector<String>();
		    	Iterator<String> itUWS = usersWithS.iterator();
		    	while (itUWS.hasNext()) {
		    		String singleUWS = itUWS.next();
		    		if (nomiUtentiDaRimuovere.contains(singleUWS) == false) {
		    			// controllo compatibilita
		    			boolean compatibile = true;
		    			for (String user : nomiUtentiDaRimuovere) {
							compatibile = utentiHelper.checkUsersModalitaGatewayCompatibili(user,singleUWS);
							
							if(!compatibile)
								break;
						}
		    			if(compatibile)
		    				usersWithPermessoS.add(singleUWS);
		    		}
		    	}
		    	if(usersWithPermessoS.size()>0){
		    		uws = new String[1];
		    		uws = usersWithPermessoS.toArray(uws);
		    	}
		    }
		    
		    // Se singleSuAccordi = null, controllo se l'utente aveva permessi P
		    List<String> usersWithP = utentiCore.getUsersWithType(Permessi.ACCORDI_COOPERAZIONE.toString());
		    
		    String[] uwp = null;
		    if (usersWithP != null && usersWithP.size() > 0) {
		    	List<String> usersWithPermessoP = new Vector<String>();
		    	Iterator<String> itUWS = usersWithP.iterator();
		    	while (itUWS.hasNext()) {
		    		String singleUWP = itUWS.next();
		    		if (nomiUtentiDaRimuovere.contains(singleUWP) == false) {
		    			// controllo compatibilita
		    			boolean compatibile = true;
		    			for (String user : nomiUtentiDaRimuovere) {
							compatibile = utentiHelper.checkUsersModalitaGatewayCompatibili(user,singleUWP);
							if(!compatibile)
								break;
						}
		    			
		    			if(compatibile)
		    				usersWithPermessoP.add(singleUWP);
		    		}
		    	}
		    	if(usersWithPermessoP.size()>0){
		    		uwp = new String[1];
		    		uwp = usersWithPermessoP.toArray(uwp);
		    	}
		    }
		    
			String nomesu  = "";
	
			// Se singleSu != null, controllo che il superutente non sia
			// tra quelli da eliminare
			// Se singleSu = null, controllo se almeno uno degli utenti da
			// eliminare aveva permessi S
			String msgServizi = "";
			boolean paginaSuServizi = false;
			
			if(!utentiCore.isVisioneOggettiGlobaleIndipendenteUtente()) {
				if (singleSuServizi != null) {
					for (int i = 0; i < nomiUtentiDaRimuovere.size(); i++) {
						nomesu = nomiUtentiDaRimuovere.get(i);
						if (nomesu.equals(singleSuServizi)) {
							paginaSuServizi = true;
							msgServizi = "Scegliere un utente che non &egrave; stato chiesto di eliminare<br>";
							break;
						}
					}
				} else {
					for (int i = 0; i < nomiUtentiDaRimuovere.size(); i++) {
						nomesu = nomiUtentiDaRimuovere.get(i);
						if (usersWithS.contains(nomesu)) {
							if(uws==null){
								
								Vector<DataElement> dati = new Vector<DataElement>();
		
								dati.addElement(ServletUtils.getDataElementForEditModeFinished());
								
								pd.disableEditMode();
								
								pd.setDati(dati);
								
								// Preparo il menu
								pd.setMessage("Non è possibile eliminare l'utente '"+nomesu+"', poichè non esistono altri utenti con il permesso per la gestione dei 'Servizi'");
		
								ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
								
								return ServletUtils.getStrutsForwardGeneralError(mapping, UtentiCostanti.OBJECT_NAME_UTENTI, ForwardParams.DEL());	
								
							}else{
								paginaSuServizi = true;
								if(nomiUtentiDaRimuovere.size()==1){
									msgServizi = "Scegliere un utente a cui verranno assegnati tutti gli oggetti con permessi 'Servizi' appartenenti all'utente '"+nomiUtentiDaRimuovere.get(0)+"'<br>";
								}else{
									StringBuilder bf = new StringBuilder();
									for(int j=0; j<nomiUtentiDaRimuovere.size(); j++){
										if(j>0)
											bf.append("','");
										bf.append(nomiUtentiDaRimuovere.get(j));
									}
									msgServizi = "Scegliere un utente a cui verranno assegnati tutti gli oggetti con permessi 'Servizi' appartenenti agli utenti '"+bf.toString()+"'<br>";
								}
								break;
							}
						}
					}
				}
			}
			
			String msgAccordi = "";
			boolean paginaSuAccordi = false;
			boolean checkOggettiAccordi = false;
			if(!utentiCore.isVisioneOggettiGlobaleIndipendenteUtente()) {
				if (singleSuAccordiCooperazione  != null) {
					for (int i = 0; i < nomiUtentiDaRimuovere.size(); i++) {
						nomesu = nomiUtentiDaRimuovere.get(i);
						if (nomesu.equals(singleSuAccordiCooperazione)) {
							paginaSuAccordi = true;
							msgAccordi = "Scegliere un utente che non &egrave; stato chiesto di eliminare<br>";
							break;
						}
					}
				} else {
					for (int i = 0; i < nomiUtentiDaRimuovere.size(); i++) {
						nomesu = nomiUtentiDaRimuovere.get(i);
						if (usersWithP.contains(nomesu)) {
							if(uwp==null){
								// controllare che l'utente possieda degli oggetti
								checkOggettiAccordi = true;
							}else{
								paginaSuAccordi = true;
								if(nomiUtentiDaRimuovere.size()==1){
									msgAccordi = "Scegliere un utente a cui verranno assegnati tutti gli oggetti con permessi 'Accordi Cooperazione' appartenenti all'utente '"+nomiUtentiDaRimuovere.get(0)+"'<br>";
								}else{
									StringBuilder bf = new StringBuilder();
									for(int j=0; j<nomiUtentiDaRimuovere.size(); j++){
										if(j>0)
											bf.append("','");
										bf.append(nomiUtentiDaRimuovere.get(j));
									}
									msgAccordi = "Scegliere un utente a cui verranno assegnati tutti gli oggetti con permessi 'Accordi Cooperazione' appartenenti agli utenti '"+bf.toString()+"'<br>";
								}
								break;
							}
						}
					}
				}
			}
			
			boolean paginaSu = paginaSuAccordi || paginaSuServizi;
			// CHECK
			String msg = msgServizi + msgAccordi;
			
			// Se paginaSu = true, propongo una pagina in cui faccio scegliere
			// un superutente con permessi S a cui assegnare gli oggetti
			// degli utenti eliminati
			// In caso contrario, elimino gli utenti, assegnando gli eventuali
			// oggetti a singleSu
			if (paginaSu) {
				// Faccio scegliere il superutente a cui assegnare gli oggetti
	
				// Preparo il menu
				utentiHelper.makeMenu();
	
				// setto la barra del titolo
				ServletUtils.setPageDataTitle(pd, 
						new Parameter(UtentiCostanti.LABEL_UTENTI ,UtentiCostanti.SERVLET_NAME_UTENTI_LIST),
						new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELIMINA,null));
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
	
				utentiHelper.addChooseUtenteForPermessiSToDati(dati, objToRemove, paginaSuServizi, uws , paginaSuAccordi , uwp);
					
				pd.setDati(dati);
				pd.setMessage(msg, Costanti.MESSAGE_TYPE_INFO);
	
				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForward(mapping, UtentiCostanti.OBJECT_NAME_UTENTI, ForwardParams.DEL(), UtentiCostanti.STRUTS_FORWARD_INFO);	

			} else {
	
				// Elimino gli utenti e ritorno la pagina a lista
				for (int i = 0; i < nomiUtentiDaRimuovere.size(); i++) {
					nomesu = nomiUtentiDaRimuovere.get(i);
					
										//TODO Aggiungi controllo se permesso D e ha servizi /soggetti deve prima eliminarli
					
					// Non posso rimuovere me stesso
					if (nomesu.equals(userLogin))
						msg += "Non è possibile rimuovere l'utente ("+nomesu+") con cui si è collegati all'interfaccia<br>";
					else {
						// Non posso rimuovere un utente con permessi U se e' l'unico
						// presente nel db
					    if (usersWithU.size() == 1 &&
					    		usersWithU.get(0).equals(nomesu))
							msg += nomesu + " non rimosso perch&egrave; deve esistere almeno un utente con permesso 'Utenti'<br><br>";
					    else {
							// Elimino l'utente
							User mySU = utentiCore.getUser(nomesu);
							// Se singleSu != null, devo recuperare gli oggetti
							// dell'utente ed assegnarli a singleSu
					        List<Object> oggetti = new ArrayList<Object>();
					        List<Integer> tipoModifica = new ArrayList<Integer>();
					        if(soggettiCore.isRegistroServiziLocale()){
					        	if (singleSuServizi != null && !singleSuServizi.equals("")) {
					        		UserObjects results = utentiCore.updateUserServizi(nomesu, singleSuServizi);
									ControlStationCore.logInfo("Modificata utenza ["+nomesu+"]->["+singleSuServizi+"] per permesso relativo ai servizi (L'utenza '"+nomesu+"' verrà eliminata). Risultati modifica: "+results.toString(false));	
					        	}
					        }

							if(soggettiCore.isRegistroServiziLocale()){
								if ((singleSuAccordiCooperazione != null && !singleSuAccordiCooperazione.equals("")) || checkOggettiAccordi) {
									
									if(checkOggettiAccordi){
										UserObjects results = utentiCore.countUserCooperazione(nomesu);
										if(results.accordi_accoperazione>0 || results.accordi_parte_comune>0) {
											Vector<DataElement> dati = new Vector<DataElement>();
											
											dati.addElement(ServletUtils.getDataElementForEditModeFinished());
	
											pd.disableEditMode();
	
											pd.setDati(dati);
	
											// Preparo il menu
											pd.setMessage("Non è possibile eliminare il permesso 'Accordi Cooperazione', poichè non esistono altri utenti con tale permesso");
	
											ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
	
											return ServletUtils.getStrutsForwardGeneralError(mapping, UtentiCostanti.OBJECT_NAME_UTENTI, ForwardParams.CHANGE());
										}
									}
									
									UserObjects results = utentiCore.updateUserCooperazione(nomesu, singleSuAccordiCooperazione);
									ControlStationCore.logInfo("Modificata utenza ["+nomesu+"]->["+singleSuAccordiCooperazione+"] per permesso relativo agli accordi di cooperazione (L'utenza '"+nomesu+"' verrà eliminata). Risultati modifica: "+results.toString(true));
																	 
								}
							}
							
							// Alla fine, elimino l'utente
					        oggetti.add(mySU);
					        tipoModifica.add(CostantiControlStation.PERFORM_OPERATION_DELETE);
							int[] operationTypes = new int[tipoModifica.size()];
							for (int cO = 0; cO < tipoModifica.size(); cO++)
								operationTypes[cO] = tipoModifica.get(cO);
							utentiCore.performOperationMultiTypes(userLogin, utentiHelper.smista(), operationTypes, oggetti.toArray());
							if (usersWithU.contains(nomesu))
								usersWithU.remove(nomesu);
					    }
					}
				}
				
				if(msgErroreModalita!=null) {
					if (!msg.equals("")) {
						msg += "<br>";
						msg += msgErroreModalita;
					}
					else {
						msg = msgErroreModalita;
					}
				}
				
				if (!msg.equals(""))
					pd.setMessage(msg);
		
				// Preparo la lista
				
				ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session,ConsoleSearch.class);
		
				int idLista = Liste.SU;
		
				ricerca = utentiHelper.checkSearchParameters(idLista, ricerca);
		
				List<User> lista = utentiCore.userList(ricerca);
		
				utentiHelper.prepareUtentiList(ricerca, lista, utentiCore.isSinglePdD());
		
				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForward(mapping, UtentiCostanti.OBJECT_NAME_UTENTI, ForwardParams.DEL());	
			}
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					UtentiCostanti.OBJECT_NAME_UTENTI, ForwardParams.DEL());
		} 
	}
	
}
