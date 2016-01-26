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

package org.openspcoop2.utils.datasource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.naming.RefAddr;

import org.openspcoop2.utils.UtilsAlreadyExistsException;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.utils.resources.RisorseJMXException;

/**
 * DatasourceFactory
 *   
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DataSourceFactory {

	private static Hashtable<String, org.openspcoop2.utils.datasource.DataSource> mapUUIDtoDatasources = new Hashtable<String, org.openspcoop2.utils.datasource.DataSource>();
	private static Hashtable<String, String> mapApplicativeIDtoUUID = new Hashtable<String, String>();
	private static Hashtable<String, String> mapJndiNametoUUID = new Hashtable<String, String>();
	private static org.openspcoop2.utils.resources.GestoreRisorseJMX gestoreRisorse = null;
	
	private static synchronized void initGestoreRisorseJMX() throws RisorseJMXException{
		if(gestoreRisorse==null)
			gestoreRisorse = new org.openspcoop2.utils.resources.GestoreRisorseJMX();
	}
	
	public static int sizeDatasources(){
		return mapUUIDtoDatasources.size();
	}
	
	private static final String DATE_FORMAT = "yyyy-MM-dd_HH:mm:ss.SSS";
	
	public static String[] getJmxStatus() throws UtilsException{	
		if(mapUUIDtoDatasources==null || mapUUIDtoDatasources.size()<=0)
			return null;
	
		Collection<org.openspcoop2.utils.datasource.DataSource> list = mapUUIDtoDatasources.values();
		Iterator<org.openspcoop2.utils.datasource.DataSource> it = list.iterator();
		List<String> listResource = new ArrayList<String>();
		while (it.hasNext()) {
			org.openspcoop2.utils.datasource.DataSource datasource = (org.openspcoop2.utils.datasource.DataSource) it.next();
			StringBuffer bf = new StringBuffer();
			SimpleDateFormat dateformat = new SimpleDateFormat (DATE_FORMAT); // SimpleDateFormat non e' thread-safe
			bf.append("(").append(dateformat.format(datasource.getDate())).append(") ");
			bf.append("idDatasource:");
			bf.append(datasource.getUuidDatasource());
			if(datasource.getJndiName()!=null){
				if(bf.length() > 0){
					bf.append(" ");
				}
				bf.append("jndiName:");
				bf.append(datasource.getJndiName());
			}
			if(datasource.getApplicativeIdDatasource()!=null){
				if(bf.length() > 0){
					bf.append(" ");
				}
				bf.append("idApplicativo:");
				bf.append(datasource.getApplicativeIdDatasource());
			}
			if(bf.length() > 0){
				bf.append(" ");
			}
			bf.append("ConnessioniAttive:");
			bf.append(datasource.size());
			listResource.add(bf.toString());
		}
		if(listResource.size()>0){
			Collections.sort(listResource);
			return listResource.toArray(new String[1]);
		}else
			return null;
		
	}
	
	
	// **** Metodi per la creazione ***
	
	public static org.openspcoop2.utils.datasource.DataSource newInstance(String jndiName,Properties jndiContext, DataSourceParams params) throws UtilsException, UtilsAlreadyExistsException{
		if(jndiContext==null){
			jndiContext = new Properties();
		}
		GestoreJNDI jndi = new GestoreJNDI(jndiContext);
		Object oSearch = null;
		try{
			oSearch = jndi.lookup(jndiName);
		}catch(Throwable t){
			throw new UtilsException("Lookup jndiResource ["+jndiName+"] failed: "+t.getMessage(),t);
		}
		if(oSearch==null){
			throw new UtilsException("Lookup jndiResource ["+jndiName+"] not found");
		}
		javax.sql.DataSource datasource = null;
		try{
			datasource = (javax.sql.DataSource) oSearch;
		}catch(Throwable t){
			StringBuffer bf = new StringBuffer();
			if(oSearch instanceof javax.naming.Reference){
				javax.naming.Reference r = (javax.naming.Reference) oSearch;
				bf.append(" (Factory=");
				bf.append(r.getFactoryClassName());
				bf.append(" FactoryLocation=");
				bf.append(r.getFactoryClassLocation());
				Enumeration<RefAddr> enR = r.getAll();
				if(enR!=null){
					while (enR.hasMoreElements()) {
						RefAddr refAddr = (RefAddr) enR.nextElement();
						bf.append(" [").
							append("type=").
							append(refAddr.getType()).
							append(" content=").
							append(refAddr.getContent()).
							append("]");
					}
				}
				bf.append(")");
			}
			throw new UtilsException("lookup failed (object class: "+oSearch.getClass().getName()+")"+bf.toString()+": "+t.getMessage(),t);
		}
		
		return newInstance(datasource, params, jndiName);
	}

	public static org.openspcoop2.utils.datasource.DataSource newInstance(javax.sql.DataSource datasource, DataSourceParams params) throws UtilsException, UtilsAlreadyExistsException{
		return newInstance(datasource, params, null);
	}
	
	public static synchronized DataSource newInstance(javax.sql.DataSource datasource, DataSourceParams params, String jndiName) throws UtilsException, UtilsAlreadyExistsException{
		
		if(params==null){
			throw new UtilsException("Parameters undefined");
		}
		if(params.getDatabaseType()==null){
			throw new UtilsException("Parameters.databaseType undefined");
		}
		try{
			if(params.getApplicativeId()!=null){
				if(mapApplicativeIDtoUUID.containsKey(params.getApplicativeId())){
					throw new UtilsAlreadyExistsException("Datasource with applicative id ["+params.getApplicativeId()+"] already exists");
				}
			}
			
			if(jndiName!=null){
				if(mapJndiNametoUUID.containsKey(jndiName)){
					throw new UtilsAlreadyExistsException("Datasource with jndiName ["+jndiName+"] already exists");
				}
			}
			
			org.openspcoop2.utils.datasource.DataSource ds = 
					new org.openspcoop2.utils.datasource.DataSource(datasource, params.getDatabaseType(), params.isWrapOriginalMethods(), jndiName, params.getApplicativeId());
			
			String uuidDatasource = ds.getUuidDatasource();
			mapUUIDtoDatasources.put(uuidDatasource, ds);
			if(params.getApplicativeId()!=null){
				mapApplicativeIDtoUUID.put(params.getApplicativeId(), uuidDatasource);
			}
			if(jndiName!=null){
				mapJndiNametoUUID.put(jndiName, uuidDatasource);
			}
		
			if(params.isBindJmx()){
				if(gestoreRisorse==null){
					initGestoreRisorseJMX();
					gestoreRisorse.registerMBean(JmxDataSource.class, params.getJmxDomain(), params.getJmxType(), params.getJmxName());
				}
			}
			
			return ds;
		}
		catch(UtilsAlreadyExistsException e){
			throw e;
		}
		catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		
	}
	
	// **** Metodi per recuperare il datasource ***
	
	public static org.openspcoop2.utils.datasource.DataSource getInstance(String id) throws UtilsException{
		String uuid = id;
		if(mapApplicativeIDtoUUID.containsKey(id)){
			uuid = mapApplicativeIDtoUUID.get(id);
		}
		else if(mapJndiNametoUUID.containsKey(id)){
			uuid = mapJndiNametoUUID.get(id);
		}
		org.openspcoop2.utils.datasource.DataSource d = mapUUIDtoDatasources.get(uuid);
		if(d==null){
			throw new UtilsException("Datasource with id ["+id+"] not exists");
		}
		return d;
	}

	// **** Metodi per rilasciare le risorse ***
	public static void closeResources() throws UtilsException{

		mapApplicativeIDtoUUID.clear();
		mapJndiNametoUUID.clear();

		Collection<org.openspcoop2.utils.datasource.DataSource> list = mapUUIDtoDatasources.values();
		Iterator<org.openspcoop2.utils.datasource.DataSource> it = list.iterator();
		while (it.hasNext()) {
			org.openspcoop2.utils.datasource.DataSource datasource = (org.openspcoop2.utils.datasource.DataSource) it.next();
			datasource.setClosed(true);
		}
		
		boolean waitCloseConnection = true;
		int maxWait = 60000;
		int index = 0;
		while(waitCloseConnection && index<maxWait){
			list = mapUUIDtoDatasources.values();
			it = list.iterator();
			boolean closeAll = true;
			while (it.hasNext()) {
				org.openspcoop2.utils.datasource.DataSource datasource = (org.openspcoop2.utils.datasource.DataSource) it.next();
				if(datasource.size()>0){
					closeAll = false;
					break;
				}
			}
			if(closeAll==false){
				System.out.println("Wait close connection ...");
				try{
					Thread.sleep(1000);
					index = index + 1000;
				}catch(Exception e){}
			}
			else{
				waitCloseConnection = false;
			}
		}
		if(waitCloseConnection==true){
			System.out.println("Forzo chiusura connessioni");
			// forzo chiusura connessioni
			list = mapUUIDtoDatasources.values();
			it = list.iterator();
			while (it.hasNext()) {
				org.openspcoop2.utils.datasource.DataSource datasource = (org.openspcoop2.utils.datasource.DataSource) it.next();
				if(datasource.size()>0){
					datasource.releaseConnnections();
				}
			}
		}
		mapUUIDtoDatasources.clear();	
		
		if(gestoreRisorse!=null){
			gestoreRisorse.unregisterMBeans();
		}
	}
}
