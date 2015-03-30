package org.openspcoop2.web.ctrlstat.plugins;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

public interface IExtendedFormServlet extends IExtendedCoreServlet {

	public boolean showExtendedInfoAdd(HttpServletRequest request,HttpSession session);
	public boolean showExtendedInfoUpdate(HttpServletRequest request,HttpSession session);
	public boolean extendedUpdateToNewWindow(HttpServletRequest request,HttpSession session); // la gestione dei dati avviene all'interno di una nuova finestra.
		
	public String getFormTitle();
	
	public void addToDati(Vector<DataElement> dati,TipoOperazione tipoOperazione,ConsoleHelper consoleHelper, ControlStationCore core, 
			Object originalObject,IExtendedBean extendedBean) throws ExtendedException;
	
	// metodi utilizzati solo se extendedUpdateToNewWindow() == 'true'
	public void addToDatiNewWindow(Vector<DataElement> dati,ConsoleHelper consoleHelper, ControlStationCore core, 
			Object originalObject, IExtendedBean extendedBean, String urlExtendedPage) throws ExtendedException;
	
	public void checkDati(TipoOperazione tipoOperazione, ConsoleHelper consoleHelper, ControlStationCore core, 
			Object originalObject,IExtendedBean extendedBean) throws ExtendedException;
	
	public String getTestoModificaEffettuata();
	
}
