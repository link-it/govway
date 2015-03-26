package org.openspcoop2.web.ctrlstat.plugins;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;

public interface IExtendedCoreServlet {

	public void performCreate(Connection connection, Object originalObject, IExtendedBean extendedBean) throws ExtendedException;
	public void performUpdate(Connection connection, Object originalObject, IExtendedBean extendedBean) throws ExtendedException;
	public void performDelete(Connection connection, Object originalObject, IExtendedBean extendedBean) throws ExtendedException;
	
	public IExtendedBean getExtendedBean(Connection connection, String id) throws ExtendedException; 
	
	IExtendedBean readHttpParameters(Object originalObject, IExtendedBean extendedBean, HttpServletRequest request) throws ExtendedException;
	public String getId(HttpServletRequest request) throws ExtendedException; 
}
