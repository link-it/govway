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

package org.openspcoop2.web.lib.mvc;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.protocol.sdk.properties.IConsoleHelper;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;
import org.openspcoop2.web.lib.mvc.Dialog.BodyElement;
import org.openspcoop2.web.lib.mvc.properties.beans.ConfigBean;
import org.openspcoop2.web.lib.users.dao.User;
import org.slf4j.Logger;

/**
 * ServletUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServletUtils {


	/* ------ STRUTS -FORWARD -ERROR */

	public static ActionForward getStrutsForwardError(Logger log,Throwable e,PageData pd,HttpServletRequest request, HttpSession session, GeneralData gd, ActionMapping mapping, String objectName,ForwardParams forwardType){
		return getStrutsForwardError(log, e, pd, request, session, gd, mapping, objectName, forwardType, Costanti.MESSAGGIO_SISTEMA_NON_DISPONIBILE);
	}
	
	public static ActionForward getStrutsForwardError(Logger log,Throwable e,PageData pd,HttpServletRequest request, HttpSession session, GeneralData gd, ActionMapping mapping, String objectName,ForwardParams forwardType,String message){
		if(e!=null)
			log.error("SistemaNonDisponibile: "+e.getMessage(), e);
		else
			log.error("SistemaNonDisponibile");
		pd.disableEditMode();
		pd.setMessage(message);
		pd.setMostraLinkHome(true); 
		ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
		return ServletUtils.getStrutsForwardGeneralError(mapping, objectName, forwardType);
	}



	/* ------ STRUTS - FORWARD ---- */

	private static String getStrutsForwardName(String objectName,ForwardParams forwardParams){
		String forward = objectName;
		if(TipoOperazione.ADD.equals(forwardParams.getTipoOperazione())){
			forward = forward + "Add";
		}
		else if(TipoOperazione.CHANGE.equals(forwardParams.getTipoOperazione())){
			forward = forward + "Change";
		}
		else if(TipoOperazione.DEL.equals(forwardParams.getTipoOperazione())){
			forward = forward + "Del";
		}
		else if(TipoOperazione.LIST.equals(forwardParams.getTipoOperazione())){
			forward = forward + "List";
		}
		else if(TipoOperazione.OTHER.equals(forwardParams.getTipoOperazione())){
			forward = forward + forwardParams.getOtherContext();
		}
		return forward;
	}

	public static ActionForward getStrutsForwardEditModeInProgress(ActionMapping mapping,String objectName,ForwardParams forwardParams){
		return mapping.findForward(getStrutsForwardName(objectName, forwardParams)+Costanti.STRUTS_FORWARD_FORM);
	}
	public static ActionForward getStrutsForwardEditModeConfirm(ActionMapping mapping,String objectName,ForwardParams forwardParams){
		return mapping.findForward(getStrutsForwardName(objectName, forwardParams)+Costanti.STRUTS_FORWARD_CONFIRM_FORM);
	}
	public static ActionForward getStrutsForwardEditModeFinished(ActionMapping mapping,String objectName,ForwardParams forwardParams){
		return mapping.findForward(getStrutsForwardName(objectName, forwardParams)+Costanti.STRUTS_FORWARD_OK);
	}
	public static ActionForward getStrutsForwardEditModeCheckError(ActionMapping mapping,String objectName,ForwardParams forwardParams){
		return mapping.findForward(getStrutsForwardName(objectName, forwardParams)+Costanti.STRUTS_FORWARD_CHECK_ERROR);
	}
	public static ActionForward getStrutsForwardGeneralError(ActionMapping mapping,String objectName,ForwardParams forwardParams){
		return mapping.findForward(objectName+Costanti.STRUTS_FORWARD_ERRORE_GENERALE);
	}
	public static ActionForward getStrutsForward(ActionMapping mapping,String objectName,ForwardParams forwardParams){
		return mapping.findForward(getStrutsForwardName(objectName, forwardParams));
	}
	public static ActionForward getStrutsForward(ActionMapping mapping,String objectName,ForwardParams forwardParams,String suffix){
		return mapping.findForward(getStrutsForwardName(objectName, forwardParams)+suffix);
	}




	/* ------ CHECKBOX ---- */
	
	public static String boolToCheckBoxStatus(Boolean value) {
		if (value == null) value = false;
		
		return value ? Costanti.CHECK_BOX_ENABLED : Costanti.CHECK_BOX_DISABLED;
	}
	
	public static boolean isCheckBoxEnabled(String value){
		return Costanti.CHECK_BOX_ENABLED.equals(value) || 
				Costanti.CHECK_BOX_ENABLED_TRUE.equalsIgnoreCase(value) || 
				Costanti.CHECK_BOX_ENABLED_ABILITATO.equalsIgnoreCase(value);
	}
	public static void setCheckBox(DataElement de,String checkBoxValue){
		if(isCheckBoxEnabled(checkBoxValue)){
			de.setSelected(Costanti.CHECK_BOX_ENABLED);
		}
		else{
			de.setSelected(Costanti.CHECK_BOX_DISABLED);
		}
	}
	public static void setCheckBox(DataElement de,boolean checkBoxValue){
		if(checkBoxValue){
			de.setSelected(Costanti.CHECK_BOX_ENABLED);
		}
		else{
			de.setSelected(Costanti.CHECK_BOX_DISABLED);
		}
	}



	/* ------ EDIT - SAVE MODE ---- */

	public static DataElement getDataElementForEditModeInProgress(){
		return Costanti.DATA_ELEMENT_HIDDENT_EDIT_MODE_IN_PROGRESS;
	}

	public static DataElement getDataElementForEditModeFinished(){
		return Costanti.DATA_ELEMENT_HIDDENT_EDIT_MODE_END;
	}

	public static Parameter getParameterForEditModeFinished(){
		return Costanti.PARAMETER_EDIT_MODE_END;
	}

	public static boolean isEditModeInProgress(String editMode){
		return !Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END.equals(editMode);
	}

	public static boolean isEditModeFinished(String editMode){
		return !Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END.equals(editMode);
	}

	public static boolean isSearchDone(IConsoleHelper consoleHelper){
		try {
			String v = consoleHelper.getParameter(Costanti.PARAMETER_NAME_SEARCH_LIST_DONE);
			return "true".equals(v); // listElement.jsp:   addHidden(form, '_searchDone' , true);
		}catch(Exception e) {}
		return false;
	}


	/* ------ PAGE DATA (TITLE) ---- */

	public static Parameter getParameterAggiungi() {
		return new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null);
	}
	
	public static void setPageDataTitle_ServletFirst(PageData pd,String t1Label, String t1Url){
		setPageDataTitle(pd, 
				new Parameter(t1Label,t1Url));
	}
	public static void setPageDataTitle_ServletAdd(PageData pd,String t1Label, String t1Url){
		setPageDataTitle(pd, 
				new Parameter(t1Label,t1Url), 
				new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI,null));
	}	
	public static void setPageDataTitle_ServletChange(PageData pd,String t1Label, String t1Url, String t2Label){
		setPageDataTitle(pd, 
				new Parameter(t1Label,t1Url),
				new Parameter(t2Label,null));
	}

	public static void setPageDataTitle(PageData pd, List<Parameter> listaParametri){
		setPageDataTitle(pd, listaParametri.toArray(new Parameter[listaParametri.size()]));
	}
	public static void setPageDataTitle(PageData pd, Parameter ... param){
		setPageDataTitle(pd,false,param);
	}
	public static void appendPageDataTitle(PageData pd, Parameter ... param){
		setPageDataTitle(pd,true,param);
	}
	private static void setPageDataTitle(PageData pd, boolean append, Parameter ... param){

		Vector<GeneralLink> titlelist = null;
		if(!append){
			titlelist = new Vector<GeneralLink>();
		}
		else{
			titlelist = pd.getTitleList();
		}

		for (int i = 0; i < param.length; i++) {
			GeneralLink tl = new GeneralLink();
			if(param[i].getName()!=null){
				tl.setLabel(param[i].getName());
			}
			if(param[i].getValue()!=null){
				tl.setUrl(param[i].getValue());
			}
			titlelist.addElement(tl);
		}

		pd.setTitleList(titlelist);
	}

	
	
	
	/* ------ PAGE DATA (DISABLE EDIT MODE) ---- */
	
	public static void disableEditMode(PageData pd){
		pd.disableEditMode();
	}
	
	
	/* ------ PAGE DATA (SEARCH) ---- */
	
	public static void enabledPageDataSearch(PageData pd,String soggetto,String search){
		pd.setSearch(Costanti.SEARCH_ENABLED);
		pd.setSearchDescription(soggetto+" contenenti la stringa '" + search + "'");
	}

	public static void disabledPageDataSearch(PageData pd){
		pd.setSearch(Costanti.SEARCH_DISABLED);
	}





	/* ------ DATA ELEMENT ---- */

	public static void setDataElementVisualizzaLabel(DataElement de){
		setDataElementVisualizzaLabel(de, null);
	}
	public static void setDataElementVisualizzaLabel(DataElement de,Long count){
		if(count!=null){
			de.setValue(Costanti.LABEL_VISUALIZZA+"(" + count + ")");
		} else {
			de.setValue(Costanti.LABEL_VISUALIZZA);
		}
	}

	public static void setDataElementVisualizzaCustomLabel(DataElement de, String customLabel){
		setDataElementVisualizzaCustomLabel(de, customLabel,null);
	}
	public static void setDataElementVisualizzaCustomLabel(DataElement de, String customLabel, Long count){
		if(count!=null){
			de.setValue(Costanti.LABEL_VISUALIZZA+ " "+customLabel+" (" + count + ")");
		} else {
			de.setValue(Costanti.LABEL_VISUALIZZA+ " "+customLabel );
		}
	}

	public static void setDataElementCustomLabel(DataElement de, String customLabel){
		setDataElementCustomLabel(de, customLabel,null,null);
	}
	public static void setDataElementCustomLabel(DataElement de, String customLabel,Long count){
		setDataElementCustomLabel(de, customLabel,count,null);
	}
	public static void setDataElementCustomLabel(DataElement de, String customLabel,String msg){
		setDataElementCustomLabel(de, customLabel,null,msg);
	}
	public static void setDataElementCustomLabel(DataElement de, String customLabel, Long count, String msg){
		if(count!=null){
			de.setValue(customLabel+" (" + count + ")");
		} else if(msg!=null){
			de.setValue(customLabel+" (" + msg + ")");
		} else {
			de.setValue(customLabel);
		}
	}
	
	
	public static String getTabIdFromRequestAttribute(HttpServletRequest request)  {
		Object idTabObj = request.getAttribute(Costanti.PARAMETER_TAB_KEY);
			
		if(idTabObj != null)
			return (String) idTabObj;
		
		return null;
	}

	
	/* ------ SESSION ---- */
	
	public static void addListElementIntoSession(HttpServletRequest request, HttpSession session,String objectName, List<Parameter> parameters){
		Parameter[] parameter = parameters != null ? parameters.toArray(new Parameter[parameters.size()]) : null;
		addListElementIntoSession(request, session, objectName, parameter);
	}

	public static void addListElementIntoSession(HttpServletRequest request, HttpSession session,String objectName, Parameter ... parameter){
		ListElement listElement = new ListElement();
		listElement.setOggetto(objectName);
		if(parameter!=null && parameter.length>0){
			for (int i = 0; i < parameter.length; i++) {
				if(parameter[i].getValue()==null){
					listElement.addParameter(parameter[i].getName(), "");
				}else{
					listElement.addParameter(parameter[i].getName(), parameter[i].getValue());
				}
			}
		}
		setObjectIntoSession(request, session, listElement, Costanti.SESSION_ATTRIBUTE_LIST_ELEMENT);
	}

	public static void setGeneralAndPageDataIntoSession(HttpServletRequest request, HttpSession session,GeneralData gd,PageData pd){
		setGeneralAndPageDataIntoSession(request, session, gd, pd, false);
	}
	public static void setGeneralAndPageDataIntoSession(HttpServletRequest request, HttpSession session,GeneralData gd,PageData pd,boolean readOnlyDisabled){

		//		if(readOnlyDisabled==false){
		//			/*
		//			CON UN UNICO INTERVENTO SI OTTIENE IL READ ONLY
		//			
		//			IN PRATICA RECUPERO L'UTENZA DALLA SESSIONE
		//			SE POSSIEDE IL PERMESSO READ-ONLY ('R' indica che tutte le maschere visualizzate tramite gli altri permessi sono in read-only mode)
		//			DEVE POI ESSERE GESTITA NEL FILTRO DI AUTORIZZAZIONE IL CONTROLLO CHE UN UNTENTE IN READ ONLY NON RICHIEDA UNA ELIMINAZIONE, UNA ADD O UNA MODIFICA
		//			*/ 
		//			pd.disableEditMode();
		//			pd.setAddButton(false);
		//			pd.setRemoveButton(false);
		//			pd.setSelect(false);
		//			pd.setAreaBottoni(null);
		//			pd.setBottoni(null);
		//			pd.setInserisciBottoni(false);
		//		}
		//		
		setObjectIntoSession(request, session, gd, Costanti.SESSION_ATTRIBUTE_GENERAL_DATA);
		setObjectIntoSession(request, session, pd, Costanti.SESSION_ATTRIBUTE_PAGE_DATA);
	}

	public static PageData getPageDataFromSession(HttpServletRequest request, HttpSession session){
		return getObjectFromSession(request, session, PageData.class, Costanti.SESSION_ATTRIBUTE_PAGE_DATA);
	}

	public static boolean existsSearchObjectFromSession(HttpServletRequest request, HttpSession session) {
		return getObjectFromSession(request, session, ISearch.class, Costanti.SESSION_ATTRIBUTE_RICERCA) != null;
	}
	
	public static ISearch getSearchObjectFromSession(HttpServletRequest request, HttpSession session, Class<?> searchImpl) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		ISearch ricerca = getObjectFromSession(request, session, ISearch.class, Costanti.SESSION_ATTRIBUTE_RICERCA);

		if (ricerca == null) {
			ricerca = (ISearch) ClassLoaderUtilities.newInstance(searchImpl);
		}
		return ricerca;
	}
	public static void setSearchObjectIntoSession(HttpServletRequest request, HttpSession session,ISearch ricerca){
		setObjectIntoSession(request, session, ricerca, Costanti.SESSION_ATTRIBUTE_RICERCA);
	}
	public static void removeSearchObjectFromSession(HttpServletRequest request, HttpSession session){
		removeObjectFromSession(request, session, ISearch.class, Costanti.SESSION_ATTRIBUTE_RICERCA);
	}

	public static String getSearchFromSession(ISearch ricerca, int idLista){
		String search = (Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		return search;
	}

	private static String getKeyRisultatiRicerca(int idLista) {
		return Costanti.SESSION_ATTRIBUTE_RISULTATI_LISTA+"_"+idLista;
	}
	public static void setRisultatiRicercaIntoSession(HttpServletRequest request, HttpSession session, int idLista, List<?> risultatiRicerca){
		setObjectIntoSession(request, session, risultatiRicerca, getKeyRisultatiRicerca(idLista));
	}
	@SuppressWarnings("unchecked")
	public static <T> List<T> getRisultatiRicercaFromSession(HttpServletRequest request, HttpSession session, int idLista, Class<T> classType){
		return getObjectFromSession(request, session, List.class, getKeyRisultatiRicerca(idLista));
	}
	public static List<?> removeRisultatiRicercaFromSession(HttpServletRequest request, HttpSession session, int idLista){
		Object o = removeObjectFromSession(request, session, List.class, getKeyRisultatiRicerca(idLista));
		if(o!=null) {
			return (List<?>) o;
		}
		return null;
	}
	
	public static String getUserLoginFromSession(HttpSession session){
		String userLogin = (String) session.getAttribute(Costanti.SESSION_ATTRIBUTE_LOGIN);
		return userLogin;
	}
	public static void setUserLoginIntoSession(HttpSession session,String userLogin){
		session.setAttribute(Costanti.SESSION_ATTRIBUTE_LOGIN, userLogin);
	}
	public static void removeUserLoginFromSession(HttpSession session){
		session.removeAttribute(Costanti.SESSION_ATTRIBUTE_LOGIN);
	}

	public static User getUserFromSession(HttpServletRequest request, HttpSession session){
		return getObjectFromSession(request, session, User.class, Costanti.SESSION_ATTRIBUTE_USER);
	}
	public static void setUserIntoSession(HttpServletRequest request, HttpSession session,User user){
		setObjectIntoSession(request, session, user, Costanti.SESSION_ATTRIBUTE_USER);
	}
	public static void removeUserFromSession(HttpServletRequest request, HttpSession session){
		removeObjectFromSession(request, session, Costanti.SESSION_ATTRIBUTE_USER);
	}

	public static Boolean getContaListeFromSession(HttpSession session){
		Boolean contaListe = (Boolean) session.getAttribute(Costanti.SESSION_ATTRIBUTE_CONTA_LISTE);
		return contaListe;
	}
	public static void setContaListeIntoSession(HttpSession session,Boolean contaListe){
		session.setAttribute(Costanti.SESSION_ATTRIBUTE_CONTA_LISTE, contaListe);
	}
	public static void removeContaListeFromSession(HttpSession session){
		session.removeAttribute(Costanti.SESSION_ATTRIBUTE_CONTA_LISTE);
	}

	public static Boolean getConfigurazioniPersonalizzateFromSession(
			HttpSession session) {
		return(Boolean) session.getAttribute(Costanti.SESSION_ATTRIBUTE_CONFIGURAZIONI_PERSONALIZZATE);
	}

	public static BooleanNullable getBooleanAttributeFromSession(String attributeName, HttpSession session, HttpServletRequest request) {
		return getBooleanAttributeFromSession(attributeName,session,request,null);
	}
	public static BooleanNullable getBooleanAttributeFromSession(String attributeName, HttpSession session, HttpServletRequest request, Boolean defaultValue) {
		Boolean obj = getObjectFromSession(request, session, Boolean.class, attributeName);
//		Object obj = session.getAttribute(attributeName);

		if(obj == null) {
			if(defaultValue==null) {
				return BooleanNullable.NULL();
			}
			else {
				return defaultValue ? BooleanNullable.TRUE() : BooleanNullable.FALSE();
			}
		}
		else {
			return obj ? BooleanNullable.TRUE() : BooleanNullable.FALSE();
		}
	}

	@SuppressWarnings("unchecked")
	public static void setObjectIntoSession(HttpServletRequest request, HttpSession session,Object obj, String objectName){
		if(objectName.startsWith(Costanti.SESSION_ATTRIBUTE_TAB_KEY_PREFIX)) {
			// lettura dalla sessione associata all'id del tab
			String tabId = (String) request.getAttribute(Costanti.PARAMETER_TAB_KEY);
			
			String prevTabId = request.getParameter(Costanti.PARAMETER_PREV_TAB_KEY);
			
			Map<String,Map<String, Object>> sessionMap = (Map<String,Map<String, Object>>) session.getAttribute(Costanti.SESSION_ATTRIBUTE_TAB_KEYS_MAP);
			
			if(sessionMap == null) {
				sessionMap = new ConcurrentHashMap<String, Map<String,Object>>(); 
			}
			
			Map<String, Object> mapTabId = null;
			if(sessionMap.containsKey(tabId)) {
				mapTabId = sessionMap.get(tabId);
				
			} else {
				// primo accesso copio le informazioni dalla mappa relativa al tab precedente
				copiaAttributiSessioneTab(session, prevTabId, tabId);
				sessionMap = (Map<String,Map<String, Object>>) session.getAttribute(Costanti.SESSION_ATTRIBUTE_TAB_KEYS_MAP);
				mapTabId = sessionMap.get(tabId);
			}
				
			mapTabId.put(objectName, obj);
			
			session.setAttribute(Costanti.SESSION_ATTRIBUTE_TAB_KEYS_MAP,sessionMap);
			return;
		}
		
		session.setAttribute(objectName,obj);
	}

	public static void removeObjectFromSession(HttpServletRequest request, HttpSession session,String objectName){
		removeObjectFromSession(request, session, null, objectName, false);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getObjectFromSession(HttpServletRequest request, HttpSession session,Class<T> objectClass, String objectName){
		if(objectName.startsWith(Costanti.SESSION_ATTRIBUTE_TAB_KEY_PREFIX)) {
		
			// lettura dalla sessione associata all'id del tab
			String tabId = (String) request.getAttribute(Costanti.PARAMETER_TAB_KEY);
			
			String prevTabId = request.getParameter(Costanti.PARAMETER_PREV_TAB_KEY);
			
			Map<String,Map<String, Object>> sessionMap = (Map<String,Map<String, Object>>) session.getAttribute(Costanti.SESSION_ATTRIBUTE_TAB_KEYS_MAP);
			
			if(sessionMap == null) {
				sessionMap = new ConcurrentHashMap<String, Map<String,Object>>(); 
			}
			
			Map<String, Object> mapTabId = null;
			if(sessionMap.containsKey(tabId)) {
				mapTabId = sessionMap.get(tabId);
				
			} else {
				// primo accesso copio le informazioni dalla mappa relativa al tab precedente
				copiaAttributiSessioneTab(session, prevTabId, tabId);
				sessionMap = (Map<String,Map<String, Object>>) session.getAttribute(Costanti.SESSION_ATTRIBUTE_TAB_KEYS_MAP);
				mapTabId = sessionMap.get(tabId);
			}
			
			if(mapTabId.containsKey(objectName))
				return objectClass.cast(mapTabId.get(objectName));
			
			// leggi dall request
//			if(request != null) {
//				String parameterValue = request.getParameter(objectName);
//				
//				if(parameterValue != null) {
//					return objectClass.cast(parameterValue);
//				}
//			}
		}
		
		// comportamento normale
		Object obj = session.getAttribute(objectName);

		if(obj == null)
			return null;

		return objectClass.cast(obj);
	}
	
	@SuppressWarnings("unchecked")
	public static Object getObjectFromSession(HttpServletRequest request, HttpSession session, String objectName){
		if(objectName.startsWith(Costanti.SESSION_ATTRIBUTE_TAB_KEY_PREFIX)) {
		
			// lettura dalla sessione associata all'id del tab
			String tabId = (String) request.getAttribute(Costanti.PARAMETER_TAB_KEY);
			
			String prevTabId = request.getParameter(Costanti.PARAMETER_PREV_TAB_KEY);
			
			Map<String,Map<String, Object>> sessionMap = (Map<String,Map<String, Object>>) session.getAttribute(Costanti.SESSION_ATTRIBUTE_TAB_KEYS_MAP);
			
			if(sessionMap == null) {
				sessionMap = new ConcurrentHashMap<String, Map<String,Object>>(); 
			}
			
			Map<String, Object> mapTabId = null;
			if(sessionMap.containsKey(tabId)) {
				mapTabId = sessionMap.get(tabId);
				
			} else {
				// primo accesso copio le informazioni dalla mappa relativa al tab precedente
				copiaAttributiSessioneTab(session, prevTabId, tabId);
				sessionMap = (Map<String,Map<String, Object>>) session.getAttribute(Costanti.SESSION_ATTRIBUTE_TAB_KEYS_MAP);
				mapTabId = sessionMap.get(tabId);
			}
			
			if(mapTabId.containsKey(objectName))
				return mapTabId.get(objectName);
			
			// leggi dall request
//			if(request != null) {
//				String parameterValue = request.getParameter(objectName);
//				
//				if(parameterValue != null) {
//					return objectClass.cast(parameterValue);
//				}
//			}
		}
		
		// comportamento normale
		Object obj = session.getAttribute(objectName);

		return obj;
	}
	
	public static String getStringAttributeFromSession(String attributeName, HttpSession session, HttpServletRequest request) {
		return getObjectFromSession(request, session, String.class, attributeName);
	}
	
	public static Integer getIntegerAttributeFromSession(String attributeName, HttpSession session, HttpServletRequest request) {
		return getObjectFromSession(request, session, Integer.class, attributeName);
	}
	
	@SuppressWarnings("unchecked")
	public static void copiaAttributiSessioneTab(HttpSession session, String idSessioneTabSrc, String idSessioneTabDest) {
		Map<String,Map<String, Object>> sessionMap = (Map<String,Map<String, Object>>) session.getAttribute(Costanti.SESSION_ATTRIBUTE_TAB_KEYS_MAP);
		
		if(sessionMap == null) {
			sessionMap = new ConcurrentHashMap<String, Map<String,Object>>();
		}
		
		Map<String, Object> mapDest = null;
		if(StringUtils.isNotBlank(idSessioneTabSrc) && sessionMap.containsKey(idSessioneTabSrc)) { // identificativo del tab sorgente definito
			// cerco una sessione da cui copiare altrimenti creo una sessione vuota 
			Map<String, Object> mapSrc = sessionMap.get(idSessioneTabSrc);
			mapDest = (HashMap<String, Object>) SerializationUtils.clone(((HashMap<String, Object>)mapSrc));
		} else { // se non ricevo un id tab provo a cercare l'ultima sessione creata altrimenti creo una sessione vuota
			Date date = new Date(0l); // imposto una data molto vecchia
			String idSessionTab = null;
			
			List<String> keys = new ArrayList<>();
			for (String mapTabKey : sessionMap.keySet()) {
				keys.add(mapTabKey);
			}
			
			for (String idSessioneTmp : keys) {
				Map<String, Object> mapTmp = sessionMap.get(idSessioneTmp);
				Date dTmp = (Date) mapTmp.get(Costanti.SESSION_ATTRIBUTE_TAB_MAP_CREATION_DATE);
				
				if(dTmp.after(date)) { // il tab analizzato e' stato creato dopo
					date = dTmp; // salvo la data 
					idSessionTab = idSessioneTmp; // salvo l'id del tab
				}
			}
			
			if(idSessionTab != null) {
				Map<String, Object> mapSrc = sessionMap.get(idSessionTab);
				mapDest = (HashMap<String, Object>) SerializationUtils.clone(((HashMap<String, Object>)mapSrc));
			} else {
				mapDest = new HashMap<String, Object>();
			}
		}
		
		//salvo la data di creazione del tab
		mapDest.put(Costanti.SESSION_ATTRIBUTE_TAB_MAP_CREATION_DATE, new Date());
		// creo il primo token CSFR per il tab
		mapDest.put(Costanti.SESSION_ATTRIBUTE_CSRF_TOKEN, generaTokenCSRF(idSessioneTabDest));
		
		sessionMap.put(idSessioneTabDest, mapDest);
		
		session.setAttribute(Costanti.SESSION_ATTRIBUTE_TAB_KEYS_MAP, sessionMap);
	}
	
	public static <T> T removeObjectFromSession(HttpServletRequest request, HttpSession session,Class<T> objectClass, String objectName){
		return removeObjectFromSession(request, session, objectClass, objectName, true);
	}
	@SuppressWarnings("unchecked")
	private static <T> T removeObjectFromSession(HttpServletRequest request, HttpSession session,Class<T> objectClass, String objectName, boolean returnValue){
		if(objectName.startsWith(Costanti.SESSION_ATTRIBUTE_TAB_KEY_PREFIX)) {
			
			// lettura dalla sessione associata all'id del tab
			String tabId = (String) request.getAttribute(Costanti.PARAMETER_TAB_KEY);
			
			Map<String,Map<String, Object>> sessionMap = (Map<String,Map<String, Object>>) session.getAttribute(Costanti.SESSION_ATTRIBUTE_TAB_KEYS_MAP);
			
			if(sessionMap == null) {
				sessionMap = new ConcurrentHashMap<String, Map<String,Object>>(); 
			}
			
			List<String> keys = new ArrayList<>();
			for (String mapTabKey : sessionMap.keySet()) {
				keys.add(mapTabKey);
			}
			
			Object obj = null;
			// la remove deve essere fatta da tutti i tab session
			for (String mapTabKey : keys) {
				Map<String, Object> mapTabId = sessionMap.get(mapTabKey);
				// se il tab e' quello corrente allora restituisco anche l'oggetto
				if(tabId.equals(mapTabKey)) {
					obj = mapTabId.remove(objectName);
				} else {
					mapTabId.remove(objectName);
				}
			}
			
			if(obj != null && returnValue) {
				return objectClass.cast(obj);
			}
		}
		
		// comportamento normale
		Object obj = session.getAttribute(objectName);

		if(obj != null) {
		
			session.removeAttribute(objectName);
	
			if(returnValue) {
				return objectClass.cast(obj);
			}
		}
		return null;
	}

	public static String getParametersAsString(boolean createFirstParameter, Parameter ... parameter ) {
		StringBuilder sb = new StringBuilder();
		if(parameter!=null && parameter.length>0){
			if(createFirstParameter)
				sb.append("?");
			else 
				sb.append("&");

			for (int i = 0; i < parameter.length; i++) {
				if(i>0){
					sb.append("&");
				}
				sb.append(parameter[i].toString());
			}
		}

		return sb.toString();
	}

	public static void removeCookieFromResponse(String cookieName, HttpServletRequest request, HttpServletResponse response){
		Cookie[] cookies = request.getCookies();

		if(cookies != null){
			for(int i = 0; i< cookies.length ; ++i){
				
				if(cookies[i].getName().equalsIgnoreCase(cookieName)){
//					Cookie cookie = new Cookie(cookies[i].getName(), cookies[i].getValue());
//					cookie.setMaxAge(0);
//					response.addCookie(cookie);
					String contextPath = request.getContextPath(); 
					cookies[i].setPath(contextPath);
					cookies[i].setMaxAge(0);
					cookies[i].setValue("NO_DATA");
					cookies[i].setVersion(0);
	                response.addCookie(cookies[i]); 
	                break;
				}
			}
		}
	}
	
	public static void saveConfigurazioneBeanIntoSession(HttpServletRequest request, HttpSession session,ConfigBean configurazioneBean, String objectName){
		setObjectIntoSession(request, session, configurazioneBean, Costanti.SESSION_PARAMETRO_OLD_CONFIGURAZIONE_PROPERTIES_PREFIX + objectName);
	}
	
	public static void removeConfigurazioneBeanFromSession(HttpServletRequest request, HttpSession session,String objectName){
		removeObjectFromSession(request, session, Costanti.SESSION_PARAMETRO_OLD_CONFIGURAZIONE_PROPERTIES_PREFIX +  objectName);
	}

	public static ConfigBean readConfigurazioneBeanFromSession(HttpServletRequest request, HttpSession session, String objectName){
		return getObjectFromSession(request, session, ConfigBean.class, Costanti.SESSION_PARAMETRO_OLD_CONFIGURAZIONE_PROPERTIES_PREFIX + objectName);
	}
	
	
	
	
	/* ------ IN USO ---- */
	
	public static void addInUsoButton(String servletName, List<DataElement> e, DataElementType deType, String titolo, String id, String inUsoType,
			String tooltip, String icon, String headerRiga1, 
			Boolean resizable, Boolean draggable) {
		DataElement de = new DataElement();
		de.setType(deType);
		de.setToolTip(tooltip);
		de.setWidthPx(15);	
		Dialog deDialog = new Dialog();
		deDialog.setIcona(icon);
		deDialog.setTitolo(titolo);
		deDialog.setHeaderRiga1(headerRiga1);
		if(resizable!=null) {
			deDialog.setResizable(resizable);
		}
		if(draggable!=null) {
			deDialog.setDraggable(draggable);
		}
		deDialog.setWidth("800px"); // modifico il default, che è più piccolo e calibrato per la creazione delle credenziali
		
		// Inserire sempre la url utilizzando la funzione setUrlElement
		BodyElement bodyElementURL = new Dialog().new BodyElement();
		bodyElementURL.setType(DataElementType.HIDDEN);
		bodyElementURL.setName(Costanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_URL);
		Parameter pIdOggetto = new Parameter(Costanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_ID_OGGETTO, id);
		Parameter pTipoOggetto = new Parameter(Costanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_OGGETTO, inUsoType);
		Parameter pTipoRisposta = new Parameter(Costanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA, Costanti.VALUE_PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA_TEXT);
		bodyElementURL.setUrl(servletName, pIdOggetto,pTipoOggetto,pTipoRisposta);
		deDialog.setUrlElement(bodyElementURL);
		
		// TextArea
		BodyElement bodyElement = new Dialog().new BodyElement();
		bodyElement.setType(DataElementType.TEXT_AREA);
		bodyElement.setLabel("");
		bodyElement.setValue("");
		bodyElement.setRows(15);
		if(resizable!=null) {
			bodyElement.setResizable(resizable);
		}
		deDialog.addBodyElement(bodyElement );
		
		de.setDialog(deDialog );
		e.add(de);
	}
	
	public static void generaESalvaTokenCSRF(HttpServletRequest request, HttpSession session) {
//		String tabId = (String) request.getAttribute(Costanti.PARAMETER_TAB_KEY);
		String uuId = UUID.randomUUID().toString().replace("-", ""); 
		String nuovoToken = generaTokenCSRF(uuId);
		setObjectIntoSession(request, session, nuovoToken, Costanti.SESSION_ATTRIBUTE_CSRF_TOKEN);
	}

	public static String generaTokenCSRF(String sessionTabID) {
		String nuovoToken = sessionTabID + "_" + System.currentTimeMillis();
		return nuovoToken;
	}
	
	public static String leggiTokenCSRF(HttpServletRequest request, HttpSession session) {
		return getObjectFromSession(request, session, String.class, Costanti.SESSION_ATTRIBUTE_CSRF_TOKEN);
	}
	
	public static boolean verificaTokenCSRF(String tokenToCheck, HttpServletRequest request, HttpSession session, Integer validitaTokenCsrf) {
		String csfrTokenFromSession = ServletUtils.leggiTokenCSRF(request, session);
		return verificaTokenCSRF(tokenToCheck, csfrTokenFromSession, validitaTokenCsrf);
	}
	
	public static boolean verificaTokenCSRF(String tokenToCheck, String csfrTokenFromSession, Integer validitaTokenCsrf) {
		// non ho trovato il token nella request non passo il controllo
		if(tokenToCheck == null) return false;
		
		// controllo che il token coincida per intero con quello salvato in sessione
		if(!tokenToCheck.equals(csfrTokenFromSession)) return false;
		
		// Controllo scadenza del token in base alla soglia impostata nelle properties
		if(validitaTokenCsrf != null) {
			String[] split = tokenToCheck.split("_");
			String millisSToCheck = split[1];
			
			long sogliaMS = validitaTokenCsrf.longValue() * 1000;
			long now = System.currentTimeMillis() ;
			long rif = sogliaMS + Long.parseLong(millisSToCheck);
			
			return now < rif;
		}
		
		return true;
	}
}
