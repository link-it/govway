/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

package org.openspcoop2.web.lib.mvc;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.web.lib.users.dao.User;

/**
 * ServletUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServletUtils {


	/* ------ STRUTS -FORWARD -ERROR */

	public static ActionForward getStrutsForwardError(Logger log,Throwable e,PageData pd,HttpSession session, GeneralData gd, ActionMapping mapping,
			String objectName,ForwardParams forwardType){
		if(e!=null)
			log.error("SistemaNonDisponibile: "+e.getMessage(), e);
		else
			log.error("SistemaNonDisponibile");
		pd.disableEditMode();
		pd.setMessage(Costanti.MESSAGGIO_SISTEMA_NON_DISPONIBILE);
		ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
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

	public static boolean isEditModeInProgress(HttpServletRequest request){
		String editMode = request.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
		return isEditModeInProgress(editMode);		
	}

	public static boolean isEditModeFinished(HttpServletRequest request){
		String editMode = request.getParameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME);
		return isEditModeFinished(editMode);		
	}

	public static boolean isEditModeInProgress(String editMode){
		return !Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END.equals(editMode);
	}

	public static boolean isEditModeFinished(String editMode){
		return !Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_END.equals(editMode);
	}






	/* ------ POST BACK ---- */

	public static String getPostBackElementName(HttpServletRequest request){
		return request.getParameter(Costanti.POSTBACK_ELEMENT_NAME);
	}




	/* ------ PAGE DATA ---- */

	public static void setPageDataTitle_ServletAdd(PageData pd,String t1Label){
		setPageDataTitle(pd, 
				new Parameter(t1Label,null), 
				new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_AGGIUNGI,null));
	}	
	public static void setPageDataTitle_ServletChange(PageData pd,String t1Label, String t2Url, String t3Label){
		setPageDataTitle(pd, 
				new Parameter(t1Label,null), 
				new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO,t2Url),
				new Parameter(t3Label,null));
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




	/* ------ SESION ---- */

	public static void addListElementIntoSession(HttpSession session,String objectName, Parameter ... parameter){
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
		session.setAttribute(Costanti.SESSION_ATTRIBUTE_LIST_ELEMENT, listElement);
	}

	public static void setGeneralAndPageDataIntoSession(HttpSession session,GeneralData gd,PageData pd){
		setGeneralAndPageDataIntoSession(session, gd, pd, false);
	}
	public static void setGeneralAndPageDataIntoSession(HttpSession session,GeneralData gd,PageData pd,boolean readOnlyDisabled){

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
		session.setAttribute(Costanti.SESSION_ATTRIBUTE_GENERAL_DATA, gd);
		session.setAttribute(Costanti.SESSION_ATTRIBUTE_PAGE_DATA, pd);
	}

	public static PageData getPageDataFromSession(HttpSession session){
		return (PageData) session.getAttribute(Costanti.SESSION_ATTRIBUTE_PAGE_DATA);
	}

	public static ISearch getSearchObjectFromSession(HttpSession session, Class<?> searchImpl) throws InstantiationException, IllegalAccessException{
		ISearch ricerca = (ISearch) session.getAttribute(Costanti.SESSION_ATTRIBUTE_RICERCA);
		if (ricerca == null) {
			ricerca = (ISearch) searchImpl.newInstance();
		}
		return ricerca;
	}
	public static void setSearchObjectIntoSession(HttpSession session,ISearch ricerca){
		session.setAttribute(Costanti.SESSION_ATTRIBUTE_RICERCA, ricerca);
	}
	public static void removeSearchObjectFromSession(HttpSession session){
		session.removeAttribute(Costanti.SESSION_ATTRIBUTE_RICERCA);
	}

	public static String getSearchFromSession(ISearch ricerca, int idLista){
		String search = (Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		return search;
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

	public static User getUserFromSession(HttpSession session){
		User user = (User) session.getAttribute(Costanti.SESSION_ATTRIBUTE_USER);
		return user;
	}
	public static void setUserIntoSession(HttpSession session,User user){
		session.setAttribute(Costanti.SESSION_ATTRIBUTE_USER, user);
	}
	public static void removeUserFromSession(HttpSession session){
		session.removeAttribute(Costanti.SESSION_ATTRIBUTE_USER);
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

	public static Boolean getGenerazioneAutomaticaPDFromSession(
			HttpSession session) {
		return(Boolean) session.getAttribute(Costanti.SESSION_ATTRIBUTE_GENERAZIONE_AUTOMATICA_PD);
	}

	public static Boolean getConfigurazioniPersonalizzateFromSession(
			HttpSession session) {
		return(Boolean) session.getAttribute(Costanti.SESSION_ATTRIBUTE_CONFIGURAZIONI_PERSONALIZZATE);
	}

	public static Boolean getBooleanAttributeFromSession(String attributeName, HttpSession session) {
		Object obj = session.getAttribute(attributeName);

		if(obj == null)
			return null;

		return(Boolean) obj;
	}

	public static void setObjectIntoSession(HttpSession session,Object obj, String objectName){
		session.setAttribute(objectName,obj);
	}

	public static <T> T getObjectFromSession(HttpSession session,Class<T> objectClass, String objectName){
		Object obj = session.getAttribute(objectName);

		if(obj == null)
			return null;

		return objectClass.cast(obj);
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
}
