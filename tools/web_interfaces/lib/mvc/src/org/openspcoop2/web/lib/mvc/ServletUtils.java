/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.protocol.sdk.properties.IConsoleHelper;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;
import org.openspcoop2.web.lib.mvc.Dialog.BodyElement;
import org.openspcoop2.web.lib.mvc.properties.beans.ConfigBean;
import org.openspcoop2.web.lib.mvc.security.Validatore;
import org.openspcoop2.web.lib.mvc.security.exception.ValidationException;
import org.openspcoop2.web.lib.users.dao.User;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

/**
 * ServletUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServletUtils {
	
	private ServletUtils() {}


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
		ServletUtils.setErrorStatusCodeInRequestAttribute(request, ServletUtils.getErrorStatusCodeFromException(e));
		ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
		return ServletUtils.getStrutsForwardGeneralError(mapping, objectName, forwardType);
	}

	public static HttpStatus getErrorStatusCodeFromException(Throwable t) {
		// ricerco gli errori di validazione all'interno di eventuali altre eccezioni poiche i metodi degli helper lanciano eccezioni generiche.
		if(Utilities.existsInnerException(t, ValidationException.class)) {
			Throwable validationEx = Utilities.getInnerInstanceException(t, ValidationException.class, true);
			if(validationEx != null) {
				return HttpStatus.BAD_REQUEST;
			}
		}
		
		return HttpStatus.SERVICE_UNAVAILABLE;
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
		if(forwardParams!=null) {
			// nop
		}
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
			return "true".equals(v); /** listElement.jsp:   addHidden(form, '_searchDone' , true);*/
		}catch(Exception e) {
			// ignore
		}
		return false;
	}


	/* ------ PAGE DATA (TITLE) ---- */

	public static Parameter getParameterAggiungi() {
		return new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI, null);
	}
	
	public static void setPageDataTitleServletFirst(PageData pd,String t1Label, String t1Url){
		setPageDataTitle(pd, 
				new Parameter(t1Label,t1Url));
	}
	public static void setPageDataTitleServletAdd(PageData pd,String t1Label, String t1Url){
		setPageDataTitle(pd, 
				new Parameter(t1Label,t1Url), 
				new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI,null));
	}	
	public static void setPageDataTitleServletChange(PageData pd,String t1Label, String t1Url, String t2Label){
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

		List<GeneralLink> titlelist = null;
		if(!append){
			titlelist = new ArrayList<>();
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
			titlelist.add(tl);
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

		if(!readOnlyDisabled){
			/**					
					//CON UN UNICO INTERVENTO SI OTTIENE IL READ ONLY
					
					//IN PRATICA RECUPERO L'UTENZA DALLA SESSIONE
					//SE POSSIEDE IL PERMESSO READ-ONLY ('R' indica che tutte le maschere visualizzate tramite gli altri permessi sono in read-only mode)
					//DEVE POI ESSERE GESTITA NEL FILTRO DI AUTORIZZAZIONE IL CONTROLLO CHE UN UNTENTE IN READ ONLY NON RICHIEDA UNA ELIMINAZIONE, UNA ADD O UNA MODIFICA
					
					pd.disableEditMode();
					pd.setAddButton(false);
					pd.setRemoveButton(false);
					pd.setSelect(false);
					pd.setAreaBottoni(null);
					pd.setBottoni(null);
					pd.setInserisciBottoni(false);
			*/	
		}
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
		return (Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
	}

	private static String getKeyRisultatiRicerca(int idLista) {
		return Costanti.SESSION_ATTRIBUTE_RISULTATI_LISTA+"_"+idLista;
	}
	public static void setRisultatiRicercaIntoSession(HttpServletRequest request, HttpSession session, int idLista, List<?> risultatiRicerca){
		setObjectIntoSession(request, session, risultatiRicerca, getKeyRisultatiRicerca(idLista));
	}
	@SuppressWarnings("unchecked")
	public static <T> List<T> getRisultatiRicercaFromSession(HttpServletRequest request, HttpSession session, int idLista, Class<T> classType){
		if(classType!=null) {
			// nop
		}
		return getObjectFromSession(request, session, List.class, getKeyRisultatiRicerca(idLista));
	}
	public static List<?> removeRisultatiRicercaFromSession(HttpServletRequest request, HttpSession session, int idLista){
		List<?> returnNull = null;
		Object o = removeObjectFromSession(request, session, List.class, getKeyRisultatiRicerca(idLista));
		if(o!=null) {
			return (List<?>) o;
		}
		return returnNull;
	}
	
	public static String getUserLoginFromSession(HttpSession session){
		return (String) session.getAttribute(Costanti.SESSION_ATTRIBUTE_LOGIN);
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
		return (Boolean) session.getAttribute(Costanti.SESSION_ATTRIBUTE_CONTA_LISTE);
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
			
			Map<String,Map<String, Object>> sessionMap = (Map<String,Map<String, Object>>) session.getAttribute(Costanti.SESSION_ATTRIBUTE_TAB_KEYS_MAP);
			
			if(sessionMap == null) {
				sessionMap = new ConcurrentHashMap<>(); 
			}
			
			Map<String, Object> mapTabId = null;
			if(sessionMap.containsKey(tabId)) {
				mapTabId = sessionMap.get(tabId);
				
			} else {
				// primo accesso copio le informazioni dalla mappa relativa al tab precedente
				String prevTabId = request.getParameter(Costanti.PARAMETER_PREV_TAB_KEY);
				
				// validazione prevTabId
				prevTabId = Validatore.getInstance().validatePrevTabId(prevTabId);
				
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
			
			Map<String,Map<String, Object>> sessionMap = (Map<String,Map<String, Object>>) session.getAttribute(Costanti.SESSION_ATTRIBUTE_TAB_KEYS_MAP);
			
			if(sessionMap == null) {
				sessionMap = new ConcurrentHashMap<>(); 
			}
			
			Map<String, Object> mapTabId = null;
			if(sessionMap.containsKey(tabId)) {
				mapTabId = sessionMap.get(tabId);
				
			} else {
				// primo accesso copio le informazioni dalla mappa relativa al tab precedente
				String prevTabId = request.getParameter(Costanti.PARAMETER_PREV_TAB_KEY);
				
				// validazione prevTabId
				prevTabId = Validatore.getInstance().validatePrevTabId(prevTabId);
				
				copiaAttributiSessioneTab(session, prevTabId, tabId);
				sessionMap = (Map<String,Map<String, Object>>) session.getAttribute(Costanti.SESSION_ATTRIBUTE_TAB_KEYS_MAP);
				mapTabId = sessionMap.get(tabId);
			}
			
			if(mapTabId.containsKey(objectName))
				return objectClass.cast(mapTabId.get(objectName));
			
			// leggi dall request
			/**if(request != null) {
				String parameterValue = request.getParameter(objectName);
				
				if(parameterValue != null) {
					return objectClass.cast(parameterValue);
				}
			}*/
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
			
			Map<String,Map<String, Object>> sessionMap = (Map<String,Map<String, Object>>) session.getAttribute(Costanti.SESSION_ATTRIBUTE_TAB_KEYS_MAP);
			
			if(sessionMap == null) {
				sessionMap = new ConcurrentHashMap<>(); 
			}
			
			Map<String, Object> mapTabId = null;
			if(sessionMap.containsKey(tabId)) {
				mapTabId = sessionMap.get(tabId);
				
			} else {
				// primo accesso copio le informazioni dalla mappa relativa al tab precedente
				String prevTabId = request.getParameter(Costanti.PARAMETER_PREV_TAB_KEY);
				
				// validazione prevTabId
				prevTabId = Validatore.getInstance().validatePrevTabId(prevTabId);
				
				copiaAttributiSessioneTab(session, prevTabId, tabId);
				sessionMap = (Map<String,Map<String, Object>>) session.getAttribute(Costanti.SESSION_ATTRIBUTE_TAB_KEYS_MAP);
				mapTabId = sessionMap.get(tabId);
			}
			
			if(mapTabId.containsKey(objectName))
				return mapTabId.get(objectName);
			
			// leggi dall request
			/**if(request != null) {
				String parameterValue = request.getParameter(objectName);
				
				if(parameterValue != null) {
					return objectClass.cast(parameterValue);
				}
			}*/
		}
		
		// comportamento normale
		return session.getAttribute(objectName);
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
			sessionMap = new ConcurrentHashMap<>();
		}
		
		Map<String, Object> mapDest = null;
		if(StringUtils.isNotBlank(idSessioneTabSrc) && sessionMap.containsKey(idSessioneTabSrc)) { // identificativo del tab sorgente definito
			// cerco una sessione da cui copiare altrimenti creo una sessione vuota 
			Map<String, Object> mapSrc = sessionMap.get(idSessioneTabSrc);
			mapDest = SerializationUtils.clone(((HashMap<String, Object>)mapSrc));
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
				// in questo ramo devo riconoscere se ho cliccato su un tab dopo che e' scaduta la sessione, ma e' stato fatto login in un tab diverso
				// in questo caso mi arrivano entrambi gli id ma non e' stato trovato una mappa sorgente nella lista delle mappe salvate
				// gli id possono essere uguali = clic sullo stesso tab
				// gli id possono non coincidere = apertura di un nuovo tab da un tab scaduto
				if(idSessioneTabSrc != null && idSessioneTabDest != null) {
					mapDest = new HashMap<>(); // nuova mappa dopo click su tab dove non e' stata rinnovata la sessione
					// vengono riportati gli attributi relativi all'utenza collegata
					if(mapSrc.containsKey(Costanti.SESSION_ATTRIBUTE_USER)) {
						mapDest.put(Costanti.SESSION_ATTRIBUTE_USER, mapSrc.get(Costanti.SESSION_ATTRIBUTE_USER));
					}
					// page data
					if(mapSrc.containsKey(Costanti.SESSION_ATTRIBUTE_PAGE_DATA)) {
						mapDest.put(Costanti.SESSION_ATTRIBUTE_PAGE_DATA, mapSrc.get(Costanti.SESSION_ATTRIBUTE_PAGE_DATA));
					}
					// general data
					if(mapSrc.containsKey(Costanti.SESSION_ATTRIBUTE_GENERAL_DATA)) {
						mapDest.put(Costanti.SESSION_ATTRIBUTE_GENERAL_DATA, mapSrc.get(Costanti.SESSION_ATTRIBUTE_GENERAL_DATA));
					}
					
					// attributo che indica l'id del tab con la sessione su cui fare refresh
					mapDest.put(Costanti.SESSION_ATTRIBUTE_TAB_MAP_REFRESH_TAB_ID, idSessioneTabDest); 
				} else {
					mapDest = (HashMap<String, Object>) SerializationUtils.clone(((HashMap<String, Object>)mapSrc)); // mappa clonata dalla piu' vecchia sessione creata
				}
			} else {
				mapDest = new HashMap<>(); // nuova mappa dopo login.
				mapDest.put(Costanti.SESSION_ATTRIBUTE_TAB_MAP_CREATION_TAB_ID, idSessioneTabDest); // salvo l'identificativo del tab che ha creato la sessione
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
				sessionMap = new ConcurrentHashMap<>(); 
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
					/**Cookie cookie = new Cookie(cookies[i].getName(), cookies[i].getValue());
					cookie.setMaxAge(0);
					response.addCookie(cookie);*/
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
			Boolean resizable, Boolean draggable, boolean contextMenu) {
		DataElement de = new DataElement();
		de.setType(deType);
		de.setToolTip(tooltip);
		de.setWidthPx(15);	
		de.setContextMenu(contextMenu);
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
	
	public static void addAjaxButton(List<DataElement> e, DataElementType deType, String icon, String tooltip, String titolo, String body, boolean contextMenu, String servletName, List<Parameter> parameters, String inUsoType) {
		DataElement de = new DataElement();
		de.setType(deType);
		de.setIcon(icon);
		de.setToolTip(tooltip);
		de.setWidthPx(15);	
		de.setContextMenu(contextMenu);
		if(parameters == null) {
			parameters = new ArrayList<>();
		}
		parameters.add(new Parameter(Costanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_OGGETTO, inUsoType));
		parameters.add(new Parameter(Costanti.PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA, Costanti.VALUE_PARAMETRO_INFORMAZIONI_UTILIZZO_OGGETTO_TIPO_RISPOSTA_TEXT));
		
		de.setUrl(servletName, parameters.toArray(new Parameter[parameters.size()]));
		
		DataElementConfirm confirm = new DataElementConfirm();
		confirm.setTitolo(titolo);
		confirm.setBody(body);
		de.setConfirm(confirm );
		
		e.add(de);
	}
	
	public static void generaESalvaTokenCSRF(HttpServletRequest request, HttpSession session) {
		String uuId = UUID.randomUUID().toString(); 
		String nuovoToken = generaTokenCSRF(uuId);
		setObjectIntoSession(request, session, nuovoToken, Costanti.SESSION_ATTRIBUTE_CSRF_TOKEN);
	}

	public static String generaTokenCSRF(String sessionTabID) {
		return  sessionTabID + "_" + System.currentTimeMillis();
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
		
		// verifica sintassi token
		if(!verificaSintassiTokenCsrf(tokenToCheck)) return false;
		
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
	
	/**
	 * Validazione del parametro per il controllo delle sessioni nei tab.
	 * I valori ammessi sono uuid senza caratteri non alfanumerici
	 * 
	 * @param request
	 * @param parameterToCheck
	 * @return
	 */
	public static boolean checkCsrfParameter(HttpServletRequest request, String parameterToCheck) {
		String parameterValueFiltrato = request.getParameter(parameterToCheck);
		String parameterValueOriginale = Validatore.getInstance().getParametroOriginale(request, parameterToCheck);
		
		// parametro originale e' vuoto o null allora e' valido
		if(StringUtils.isEmpty(parameterValueOriginale)) {
			return true;
		}
		
		// parametro filtrato vuoto o null perche' e' stato filtrato dall'utility di sicurezza, quindi non valido
		if(StringUtils.isEmpty(parameterValueFiltrato)) { 
			return false;
		}
		
		return verificaSintassiTokenCsrf(parameterValueFiltrato);
	}


	private static boolean verificaSintassiTokenCsrf(String token) {
		// il parametro e' formato da un UUID + '_' + System.currentTimeMillis()
		String[] split = token.split("_");
		
		// split deve avere due parti altrimenti non e' valido
		if(split == null || split.length != 2) {
			return false;
		}
		
		// validazione  parte UUID
		try {
			UUID.fromString(split[0]);
		}catch (IllegalArgumentException | NullPointerException e) {
			return false;
		}

		// validazione parte millis
		try {
			Long.parseLong(split[1]);
		}catch(IllegalArgumentException | NullPointerException e) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Validazione del parametro per il controllo delle sessioni nei tab.
	 * I valori ammessi sono uuid senza caratteri non alfanumerici
	 * 
	 * @param request
	 * @param parameterToCheck
	 * @return
	 */
	public static boolean checkTabKeyParameter(HttpServletRequest request, String parameterToCheck) {
		String parameterValueFiltrato = request.getParameter(parameterToCheck);
		String parameterValueOriginale = Validatore.getInstance().getParametroOriginale(request, parameterToCheck);
		
		// parametro originale e' vuoto o null allora e' valido
		if(StringUtils.isEmpty(parameterValueOriginale)) {
			return true;
		}
		
		// parametro filtrato vuoto o null perche' e' stato filtrato dall'utility di sicurezza, quindi non valido
		if(StringUtils.isEmpty(parameterValueFiltrato)) { 
			return false;
		}
		
		// validazione parametro originale
		try {
			UUID.fromString(parameterValueFiltrato);
		}catch (IllegalArgumentException | NullPointerException e) {
			return false;
		}
		
		return true;
	}
	
	/***
	 * Validazione del valore ricevuto per il parametro di tipo integer
	 * 
	 * @param request
	 * @param parameterToCheck
	 * @return
	 */
	public static boolean checkIntegerParameter(HttpServletRequest request, String parameterToCheck) {
		String parameterValueFiltrato = request.getParameter(parameterToCheck);
		String parameterValueOriginale = Validatore.getInstance().getParametroOriginale(request, parameterToCheck);
		
		// parametro originale e' vuoto o null allora e' valido
		if(StringUtils.isEmpty(parameterValueOriginale)) {
			return true;
		}
		
		// parametro filtrato vuoto o null perche' e' stato filtrato dall'utility di sicurezza, quindi non valido
		if(StringUtils.isEmpty(parameterValueFiltrato)) { 
			return false;
		}
		
		try {
			Integer.parseInt(parameterValueFiltrato);
		}catch(IllegalArgumentException e) {
			return false;
		}

		return true;
	}
	
	/***
	 * Validazione del valore ricevuto per il parametro di tipo long
	 * 
	 * @param request
	 * @param parameterToCheck
	 * @return
	 */
	public static boolean checkLongParameter(HttpServletRequest request, String parameterToCheck) {
		String parameterValueFiltrato = request.getParameter(parameterToCheck);
		String parameterValueOriginale = Validatore.getInstance().getParametroOriginale(request, parameterToCheck);
		
		// parametro originale e' vuoto o null allora e' valido
		if(StringUtils.isEmpty(parameterValueOriginale)) {
			return true;
		}
		
		// parametro filtrato vuoto o null perche' e' stato filtrato dall'utility di sicurezza, quindi non valido
		if(StringUtils.isEmpty(parameterValueFiltrato)) { 
			return false;
		}
		
		try {
			Long.parseLong(parameterValueFiltrato);
		}catch(IllegalArgumentException e) {
			return false;
		}

		return true;
	}
	
	/***
	 * Validazione del valore ricevuto per il parametro di tipo boolean
	 * 
	 * @param request
	 * @param parameterToCheck
	 * @return
	 */
	public static boolean checkBooleanParameter(HttpServletRequest request, String parameterToCheck) {
		String parameterValueFiltrato = request.getParameter(parameterToCheck);
		String parameterValueOriginale = Validatore.getInstance().getParametroOriginale(request, parameterToCheck);
		
		// parametro originale e' vuoto o null allora e' valido
		if(StringUtils.isEmpty(parameterValueOriginale)) {
			return true;
		}
		
		// parametro filtrato vuoto o null perche' e' stato filtrato dall'utility di sicurezza, quindi non valido
		if(StringUtils.isEmpty(parameterValueFiltrato)) { 
			return false;
		}
		
		// i valori accettati sono solo i seguenti
		return  Costanti.CHECK_BOX_ENABLED.equals(parameterValueFiltrato) ||  Costanti.CHECK_BOX_DISABLED.equals(parameterValueFiltrato) 
				|| Costanti.CHECK_BOX_ENABLED_ABILITATO.equals(parameterValueFiltrato) ||  Costanti.CHECK_BOX_DISABLED_DISABILITATO.equals(parameterValueFiltrato)
				|| Costanti.CHECK_BOX_ENABLED_TRUE.equals(parameterValueFiltrato) ||  Costanti.CHECK_BOX_DISABLED_FALSE.equals(parameterValueFiltrato)
				;
	}
	
	/**
	 * Controlla che il parametro richiesto sia valido, l'utility di validazione restituisce null per i parametri che violano le condizioni di validazione, 
	 * l'applicazione ha bisogno di sapere se si tratta di un valore originale non valido o non presente. 
	 * 
	 * @param request
	 * @param parameterToCheck
	 * @return
	 */
	public static boolean checkParametro(HttpServletRequest request, String parameterToCheck) {
		String parameterValueFiltrato = request.getParameter(parameterToCheck);
		String parameterValueOriginale = Validatore.getInstance().getParametroOriginale(request, parameterToCheck);
		
		// parametro originale e' vuoto o null allora e' valido
		if(StringUtils.isEmpty(parameterValueOriginale)) {
			return true;
		}
		
		// parametro filtrato vuoto o null perche' e' stato filtrato dall'utility di sicurezza, quindi non valido
		return !StringUtils.isEmpty(parameterValueFiltrato);
	}
	
	/***
	 * Validazione del valore ricevuto per il parametro 
	 * 
	 * @param request
	 * @param parameterToCheck
	 * @return
	 */
	public static boolean checkParametroResetSearch(HttpServletRequest request, String parameterToCheck) {
		String parameterValueFiltrato = request.getParameter(parameterToCheck);
		String parameterValueOriginale = Validatore.getInstance().getParametroOriginale(request, parameterToCheck);
		
		// parametro originale e' vuoto o null allora e' valido
		if(StringUtils.isEmpty(parameterValueOriginale)) {
			return true;
		}
		
		// parametro filtrato vuoto o null perche' e' stato filtrato dall'utility di sicurezza, quindi non valido
		if(StringUtils.isEmpty(parameterValueFiltrato)) { 
			return false;
		}
		
		// i valori accettati sono Costanti.CHECK_BOX_ENABLED o Costanti.CHECK_BOX_DISABLED
		return  Costanti.CHECK_BOX_ENABLED.equals(parameterValueFiltrato) ||  Costanti.CHECK_BOX_DISABLED.equals(parameterValueFiltrato);
	}
	
	/**
	 * Controlla che il parametro richiesto sia valido, l'utility di validazione restituisce null per i parametri che violano le condizioni di validazione, 
	 * l'applicazione ha bisogno di sapere se si tratta di un valore originale non valido o non presente. 
	 * 
	 * @param request
	 * @param parameterToCheck
	 * @return
	 */
	public static boolean checkParametroAzione(HttpServletRequest request, String parameterToCheck) {
		String parameterValueFiltrato = request.getParameter(parameterToCheck);
		String parameterValueOriginale = Validatore.getInstance().getParametroOriginale(request, parameterToCheck);
		
		// parametro originale e' vuoto o null allora e' valido
		if(StringUtils.isEmpty(parameterValueOriginale)) {
			return true;
		}
		
		// parametro filtrato vuoto o null perche' e' stato filtrato dall'utility di sicurezza, quindi non valido
		if(StringUtils.isEmpty(parameterValueFiltrato)) { 
			return false;
		}
		
		// i valori accettati sono 'salva' e 'removeEntries'
		return  Costanti.VALUE_PARAMETRO_AZIONE_SALVA.equals(parameterValueFiltrato) 
				|| Costanti.VALUE_PARAMETRO_AZIONE_REMOVE_ENTRIES.equals(parameterValueFiltrato)
				|| Costanti.VALUE_PARAMETRO_AZIONE_CONFERMA.equals(parameterValueFiltrato)
				|| Costanti.VALUE_PARAMETRO_AZIONE_ANNULLA.equals(parameterValueFiltrato);
	}
	
	/**
	 * Controlla che il parametro edit-mode sia valido
	 * 
	 * @param request
	 * @param parameterToCheck
	 * @return
	 */
	public static boolean checkParametroEditMode(HttpServletRequest request, String parameterToCheck) {
		String parameterValueFiltrato = request.getParameter(parameterToCheck);
		String parameterValueOriginale = Validatore.getInstance().getParametroOriginale(request, parameterToCheck);
		
		// parametro originale e' vuoto o null allora e' valido
		if(StringUtils.isEmpty(parameterValueOriginale)) {
			return true;
		}
		
		// parametro filtrato vuoto o null perche' e' stato filtrato dall'utility di sicurezza, quindi non valido
		if(StringUtils.isEmpty(parameterValueFiltrato)) { 
			return false;
		}
		
		// i valori accettati sono 'salva' e 'removeEntries'
		return  Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_IN_PROGRESS_POSTBACK.equals(parameterValueFiltrato) 
				|| Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_IN_PROGRESS.equals(parameterValueFiltrato)
				|| Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END.equals(parameterValueFiltrato);
	}
	
	/**
	 * Validazione del parametro HttpMethod
	 * 
	 * @param parameterToCheck
	 * @return
	 */
	public static boolean validaParametroResourcePath(HttpServletRequest request, String parameterToCheck) {
		String parameterValueOriginale = Validatore.getInstance().getParametroOriginale(request, parameterToCheck);
		
		// parametro originale e' vuoto o null allora e' valido
		if(StringUtils.isEmpty(parameterValueOriginale)) {
			return true;
		}
		
		try {
			Validatore.getInstance().validate("Il valore del parametro [" + parameterToCheck + "]:["+parameterValueOriginale+"]", parameterValueOriginale, null, true, false, org.openspcoop2.web.lib.mvc.security.Costanti.PATTERN_REQUEST_HTTP_PARAMETER_VALUE_TEXT_AREA);
		}catch(ValidationException e) {
			return false;
		}
		
		return true;
	}
	
	public static boolean usaValidazioneTextArea(HttpServletRequest request, String parameterToCheck) {
		String parametroIdentificativi = Validatore.getInstance().getParametroOriginale(request, Costanti.PARAMETRO_IDENTIFICATIVI_TEXT_AREA);
		
		if(parametroIdentificativi != null) {
			try {
				Validatore.getInstance().validate("Il valore del parametro [" + Costanti.PARAMETRO_IDENTIFICATIVI_TEXT_AREA + "]:["+parametroIdentificativi+"]",
						parametroIdentificativi, false, org.openspcoop2.web.lib.mvc.security.Costanti.PATTERN_ID_TEXT_AREA);
			}catch(ValidationException e) {
				// se il contenuto del parametro con gli id text area non rispetta il suo pattern allora non abilito la validazione custom
				return false;
			}
			
			String[] ids = parametroIdentificativi.split(Costanti.VALUE_PARAMETRO_IDENTIFICATIVI_TEXT_AREA_SEPARATORE);
			
			if(ids != null && ids.length > 0) {
				List<String> asList = Arrays.asList(ids);
				return asList.contains(parameterToCheck);
			}
		} else {
			// casi speciali per il monitor
			
			// select list azione, vengono inviati due parametri azCombo e azCombocomboboxField generato dal framework
			if(parameterToCheck.startsWith(Costanti.PARAMETRO_MONITOR_RICERCA_AZIONE)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static String getIdentificativiTextArea(List<?> dati) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < dati.size(); i++) {
			DataElement de = (DataElement) dati.get(i);
			String deName = !de.getName().equals("") ? de.getName() : "de_name_"+i;
			if (de.getType().equals("textarea") || de.getType().equals("textarea-noedit")) {
				if(sb.length() > 0) {
					sb.append(Costanti.VALUE_PARAMETRO_IDENTIFICATIVI_TEXT_AREA_SEPARATORE);
				}
				sb.append(deName);
			}
		}
		
		return sb.length() > 0 ? sb.toString() : null;
	}
	
	public static String getIdentificativiTextAreaFiltriRicerca(List<DataElement> filterValues) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < filterValues.size(); i++) {
			DataElement filtro = filterValues.get(i);
			String filterName = filtro.getName();
			if (filtro.getType().equals("textarea") || filtro.getType().equals("textarea-noedit")) {
				if(sb.length() > 0) {
					sb.append(Costanti.VALUE_PARAMETRO_IDENTIFICATIVI_TEXT_AREA_SEPARATORE);
				}
				sb.append(filterName);
			}
		}
		
		return sb.length() > 0 ? sb.toString() : null;
	}
	
	
	public static String getJsonPair(String key, String val) {
		StringBuilder sb = new StringBuilder();
		sb.append(Costanti.CHAR_QUOTA_JSON).append(key).append(Costanti.CHAR_QUOTA_JSON).append(Costanti.CHAR_DUE_PUNTI_JSON);
		if(val != null) {
			sb.append(Costanti.CHAR_QUOTA_JSON).append(val).append(Costanti.CHAR_QUOTA_JSON);
		} else {
			sb.append(Costanti.NULL_VALUE_JSON);
		}
		return sb.toString();
	}
	
	public static String getJson(String ... pairs) {
		StringBuilder sb = new StringBuilder();
		sb.append(Costanti.CHAR_APERTURA_JSON);
		
		if(pairs != null) {
			for (int i = 0; i < pairs.length; i++) {
				if(i > 0) {
					sb.append(Costanti.CHAR_VIRGOLA_JSON);
				}
				sb.append(pairs[i]);
			}
		}
		
		sb.append(Costanti.CHAR_CHIUSURA_JSON);
		return sb.toString();
	}
	
	public static void setErrorStatusCodeInRequestAttribute(HttpServletRequest request, HttpStatus httpStatus){
		request.setAttribute(Costanti.REQUEST_ATTRIBUTE_SET_ERROR_CODE, httpStatus);
	}
	
	/**
	 * Elimina il carattere ':' dall'identificativo passato come parametro, gli id contenenti i ':' non sono validi in jquery
	 * @return
	 */
	public static String normalizeId(String input) {
		if(input == null) {
			return null;
		}
		
		return input.replaceAll("[:/]", "_");
	}
}
