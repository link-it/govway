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


package org.openspcoop2.web.ctrlstat.plugins.servlet;

import java.sql.Connection;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.DBManager;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedException;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedBean;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedFormServlet;
import org.openspcoop2.web.ctrlstat.plugins.WrapperExtendedBean;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.DataElementType;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * routing
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public abstract class AbstractServletNewWindowChangeExtended extends Action {

	protected abstract ConsoleHelper getConsoleHelper(HttpServletRequest request, PageData pd, HttpSession session) throws Exception;
	
	protected abstract ControlStationCore getConsoleCore() throws Exception;
	
	protected abstract IExtendedFormServlet getExtendedServlet(ControlStationCore core, String uniqueID) throws Exception;
	
	protected abstract List<Parameter> getTitle(Object object, HttpServletRequest request, HttpSession session) throws Exception;
	
	protected abstract Object getObject(ControlStationCore core,HttpServletRequest request) throws Exception;
	
	protected abstract String getObjectName() throws Exception;
	
	protected abstract ForwardParams getForwardParams() throws Exception;
	
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
			ConsoleHelper consoleHelper = this.getConsoleHelper(request, pd, session);

			ControlStationCore consoleCore = this.getConsoleCore();
			
			IExtendedFormServlet extendedServlet = this.getExtendedServlet(consoleCore,request.getParameter(CostantiControlStation.PARAMETRO_EXTENDED_FORM_ID));
			
			// Preparo il menu
			consoleHelper.makeMenu();

			// prelevo oggetto
			Object object = this.getObject(consoleCore,request);
			
			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitle(object,request,session);
			lstParam.add(new Parameter(extendedServlet.getFormTitle(TipoOperazione.CHANGE, consoleHelper), null));
			ServletUtils.setPageDataTitle(pd, lstParam);

			IExtendedBean extendedBean = null;
			DBManager dbManager = null;
			Connection con = null;
			try{
				dbManager = DBManager.getInstance();
				con = dbManager.getConnection();
				extendedBean = extendedServlet.getExtendedBean(con, extendedServlet.getId(request));
			}finally{
				dbManager.releaseConnection(con);
			}
			
			extendedBean = extendedServlet.readHttpParameters(object, TipoOperazione.CHANGE, extendedBean, request);


			if (!ServletUtils.isEditModeInProgress(request)) {
				
				boolean isOk = true;
				try{
					extendedServlet.checkDati(TipoOperazione.CHANGE, consoleHelper, consoleCore, object, extendedBean);
				}catch(Exception e){
					isOk = false;
					pd.setMessage(e.getMessage());
				}
				
				// Controlli sui campi immessi
				if (!isOk) {
					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();

					dati.addElement(ServletUtils.getDataElementForEditModeFinished());

					DataElement de = new DataElement();
					de.setValue(extendedServlet.getUniqueID());
					de.setType(DataElementType.HIDDEN);
					de.setName(CostantiControlStation.PARAMETRO_EXTENDED_FORM_ID);
					dati.addElement(de);
					
					extendedServlet.addToDati(dati, TipoOperazione.CHANGE, consoleHelper, consoleCore, object, extendedBean);
					
					pd.setDati(dati);
					
					ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
							this.getObjectName(),
							this.getForwardParams());
				}

				// Modifico i dati della porta di dominio nel db
				WrapperExtendedBean wrapperExtendedBean = new WrapperExtendedBean();
				wrapperExtendedBean.setExtendedBean(extendedBean);
				wrapperExtendedBean.setExtendedServlet(extendedServlet);
				wrapperExtendedBean.setOriginalBean(object);
				wrapperExtendedBean.setManageOriginalBean(false);
				consoleCore.performUpdateOperation(userLogin, consoleHelper.smista(), wrapperExtendedBean);

				Vector<DataElement> dati = new Vector<DataElement>();

				String testModificaEffettuata = extendedServlet.getTestoModificaEffettuata(TipoOperazione.CHANGE, consoleHelper); // questa chiamata fatta prima della addDati consente di informare la addDati che la gestione è terminata
				
				DataElement de = new DataElement();
				de.setValue(extendedServlet.getUniqueID());
				de.setType(DataElementType.HIDDEN);
				de.setName(CostantiControlStation.PARAMETRO_EXTENDED_FORM_ID);
				dati.addElement(de);
				
				extendedServlet.addToDati(dati, TipoOperazione.CHANGE, consoleHelper, consoleCore, object, extendedBean);
				
				pd.setDati(dati);
				
				pd.setMessage(testModificaEffettuata);
				
				pd.disableEditMode();
				
				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeFinished(mapping,
						this.getObjectName(),
						this.getForwardParams()
						);
				
			}

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();

			dati.add(ServletUtils.getDataElementForEditModeFinished());

			DataElement de = new DataElement();
			de.setValue(extendedServlet.getUniqueID());
			de.setType(DataElementType.HIDDEN);
			de.setName(CostantiControlStation.PARAMETRO_EXTENDED_FORM_ID);
			dati.addElement(de);
			
			extendedServlet.addToDati(dati, TipoOperazione.CHANGE, consoleHelper, consoleCore, object, extendedBean);
			
			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
					this.getObjectName(),
					this.getForwardParams()
					);
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					this.getObjectName(), 
					this.getForwardParams());
		}
	}


	public static void addToDatiNewWindow(IExtendedFormServlet extendedServlet,Vector<DataElement> dati, ConsoleHelper consoleHelper,
			ControlStationCore core, Object originalObject, IExtendedBean extendedBean, String servletName) throws ExtendedException{
		
		DataElement de = new DataElement();
		de.setValue(extendedServlet.getUniqueID());
		de.setType(DataElementType.HIDDEN);
		de.setName(CostantiControlStation.PARAMETRO_EXTENDED_FORM_ID);
		dati.addElement(de);
		
		String servletNameWithId = new String(servletName);
		if(servletNameWithId.contains("?")){
			servletNameWithId = servletNameWithId + "&";
		}
		else{
			servletNameWithId = servletNameWithId + "?";
		}
		servletNameWithId = servletNameWithId + CostantiControlStation.PARAMETRO_EXTENDED_FORM_ID + "=" + extendedServlet.getUniqueID();
		
		extendedServlet.addToDatiNewWindow(dati, consoleHelper, core, originalObject, extendedBean, servletNameWithId);
		
	}
	
}
