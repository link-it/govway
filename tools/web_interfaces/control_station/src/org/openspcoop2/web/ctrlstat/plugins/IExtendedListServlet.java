package org.openspcoop2.web.ctrlstat.plugins;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.UrlParameters;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

public interface IExtendedListServlet extends IExtendedCoreServlet {

	public boolean showExtendedInfo(HttpServletRequest request,HttpSession session);
	
	public String getFormTitle();
	public String getFormItemTitle(IExtendedBean extendedBean);
	public String getListTitle();
	
	public void addToDati(Vector<DataElement> dati,ConsoleHelper consoleHelper, ControlStationCore core, 
			Object originalObject,IExtendedBean extendedBean) throws ExtendedException;
	public void checkDati(TipoOperazione tipoOperazione, ConsoleHelper consoleHelper, ControlStationCore core, 
			Object originalObject,IExtendedBean extendedBean) throws ExtendedException;
	
	public void addDatiToList(Vector<DataElement> dati,ConsoleHelper consoleHelper, ControlStationCore core, 
			Object originalObject, IExtendedBean extendedBean, UrlParameters urlExtendedChange);
	
	public int sizeList(Object originalObject);

	public List<IExtendedBean> extendedBeanList(Object originalObject, int limit, int offset, String search) throws ExtendedException;
	public String[] getColumnLabels() throws ExtendedException; 

}
