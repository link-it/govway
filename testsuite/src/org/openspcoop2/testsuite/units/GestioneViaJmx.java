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



package org.openspcoop2.testsuite.units;

import javax.management.Attribute;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.jmx.ConfigurazionePdD;
import org.openspcoop2.utils.jmx.CostantiJMX;



/**
 * Controlla la gestione degli errori via JMX
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class GestioneViaJmx {

	
	protected UnitsTestSuiteProperties unitsTestsuiteProperties;
	
	public GestioneViaJmx(UnitsTestSuiteProperties unitsTestsuiteProperties){
		this.unitsTestsuiteProperties = unitsTestsuiteProperties;
	}
	
	private static final String _lock_semaphore = "semaphore"; 
	private static String _lock = null; 
	public void lock(String name) throws Exception {
		
		int maxAttesa = 5 * 12 * 2; // 2 minuti 
		int i = 0;
		
		while(!name.equals(_lock) && i<maxAttesa) {
			if(_lock==null) {
				synchronized (_lock_semaphore) {
					if(_lock==null) {
						_lock=name;
						return;
					}
				}
			}
			
			org.openspcoop2.utils.Utilities.sleep(5000); // dormo 5 secondi
						
			i++;
		}
		
		throw new Exception("Lock non ottenibile dopo 10 minuti di attesa");
	}
	public void unlock(String name) {
		synchronized (_lock_semaphore) {
			if(_lock!=null && _lock.equals(name)) {
				_lock=null;
			}
		}
	}
	
	
	public void lockForCode(boolean genericCode, boolean unwrap) throws Exception {
		String name = genericCode ? "GENERIC" : "SPECIFIC";
		//System.out.println("getCode '"+name+"' '"+this.genericCode+"'....");
		this.lock(name);
		//System.out.println("getCode '"+name+"' '"+this.genericCode+"' ok");
		
		if(genericCode) {
			this.disableGovWayDetails();
			this.disableGovWayStatus();
		}
		else {
			this.enableGovWayDetails();
			this.enableGovWayStatus();
		}
		
		if(unwrap) {
			this.enableGovWayRequestError();
			this.enableGovWayResponseError();
			this.enableGovWayInternalError();
		}
	}
	public void unlockForCode(boolean genericCode) throws Exception {
		String name = genericCode ? "GENERIC" : "SPECIFIC";
		//System.out.println("rilascio '"+name+"' '"+this.genericCode+"'....");
		this.unlock(name);
		//System.out.println("rilascio '"+name+"' '"+this.genericCode+"' ok");
		
		// ripristino
		this.disableGovWayDetails();
		this.disableGovWayStatus();
		this.disableGovWayRequestError();
		this.disableGovWayResponseError();
		this.disableGovWayInternalError();
	}
	
	

	public void enableGovWayStatus() throws Exception{
		try{
			
			MBeanServerConnection jmxconn = RisorseEsterne.getMBeanServerConnection(this.unitsTestsuiteProperties);
			
			ObjectName jmxname = new ObjectName(CostantiJMX.JMX_DOMINIO+":"+CostantiJMX.JMX_TYPE+"="+CostantiPdD.JMX_CONFIGURAZIONE_PDD);
						
			Attribute attribute = new Attribute(ConfigurazionePdD.ERRORI_STATUS_CODE_ABILITATO, true);
			jmxconn.setAttribute(jmxname, attribute );
			
			Attribute attribute2 = new Attribute(ConfigurazionePdD.ERRORI_SOAP_USE_GOVWAY_STATUS_AS_FAULT_CODE, true);
			jmxconn.setAttribute(jmxname, attribute2 );
			
		}catch(Exception e){
			throw e;
		}
	}
	
	public void disableGovWayStatus() throws Exception{
		try{
			
			MBeanServerConnection jmxconn = RisorseEsterne.getMBeanServerConnection(this.unitsTestsuiteProperties);
			
			ObjectName jmxname = new ObjectName(CostantiJMX.JMX_DOMINIO+":"+CostantiJMX.JMX_TYPE+"="+CostantiPdD.JMX_CONFIGURAZIONE_PDD);
						
			Attribute attribute = new Attribute(ConfigurazionePdD.ERRORI_STATUS_CODE_ABILITATO, false);
			jmxconn.setAttribute(jmxname, attribute );
			
			Attribute attribute2 = new Attribute(ConfigurazionePdD.ERRORI_SOAP_USE_GOVWAY_STATUS_AS_FAULT_CODE, false);
			jmxconn.setAttribute(jmxname, attribute2 );
			
		}catch(Exception e){
			throw e;
		}
	}
	
	public void enableGovWayDetails() throws Exception{
		try{
			
			MBeanServerConnection jmxconn = RisorseEsterne.getMBeanServerConnection(this.unitsTestsuiteProperties);
			
			ObjectName jmxname = new ObjectName(CostantiJMX.JMX_DOMINIO+":"+CostantiJMX.JMX_TYPE+"="+CostantiPdD.JMX_CONFIGURAZIONE_PDD);
						
			Attribute attribute = new Attribute(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_DETAILS, true);
			jmxconn.setAttribute(jmxname, attribute );
			
		}catch(Exception e){
			throw e;
		}
	}
	
	public void disableGovWayDetails() throws Exception{
		try{
			
			MBeanServerConnection jmxconn = RisorseEsterne.getMBeanServerConnection(this.unitsTestsuiteProperties);
			
			ObjectName jmxname = new ObjectName(CostantiJMX.JMX_DOMINIO+":"+CostantiJMX.JMX_TYPE+"="+CostantiPdD.JMX_CONFIGURAZIONE_PDD);
						
			Attribute attribute = new Attribute(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_DETAILS, false);
			jmxconn.setAttribute(jmxname, attribute );
			
		}catch(Exception e){
			throw e;
		}
	}
	
	
	public void enableGovWayRequestError() throws Exception{
		try{
			
			MBeanServerConnection jmxconn = RisorseEsterne.getMBeanServerConnection(this.unitsTestsuiteProperties);
			
			ObjectName jmxname = new ObjectName(CostantiJMX.JMX_DOMINIO+":"+CostantiJMX.JMX_TYPE+"="+CostantiPdD.JMX_CONFIGURAZIONE_PDD);
						
			Attribute attribute = new Attribute(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST, true);
			jmxconn.setAttribute(jmxname, attribute );

		}catch(Exception e){
			throw e;
		}
	}
	
	public void disableGovWayRequestError() throws Exception{
		try{
			
			MBeanServerConnection jmxconn = RisorseEsterne.getMBeanServerConnection(this.unitsTestsuiteProperties);
			
			ObjectName jmxname = new ObjectName(CostantiJMX.JMX_DOMINIO+":"+CostantiJMX.JMX_TYPE+"="+CostantiPdD.JMX_CONFIGURAZIONE_PDD);
						
			Attribute attribute = new Attribute(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST, false);
			jmxconn.setAttribute(jmxname, attribute );

		}catch(Exception e){
			throw e;
		}
	}
	
	
	public void enableGovWayResponseError() throws Exception{
		try{
			
			MBeanServerConnection jmxconn = RisorseEsterne.getMBeanServerConnection(this.unitsTestsuiteProperties);
			
			ObjectName jmxname = new ObjectName(CostantiJMX.JMX_DOMINIO+":"+CostantiJMX.JMX_TYPE+"="+CostantiPdD.JMX_CONFIGURAZIONE_PDD);
						
			Attribute attribute = new Attribute(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR, true);
			jmxconn.setAttribute(jmxname, attribute );
			
			Attribute attribute2 = new Attribute(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE, true);
			jmxconn.setAttribute(jmxname, attribute2 );

		}catch(Exception e){
			throw e;
		}
	}
	
	public void disableGovWayResponseError() throws Exception{
		try{
			
			MBeanServerConnection jmxconn = RisorseEsterne.getMBeanServerConnection(this.unitsTestsuiteProperties);
			
			ObjectName jmxname = new ObjectName(CostantiJMX.JMX_DOMINIO+":"+CostantiJMX.JMX_TYPE+"="+CostantiPdD.JMX_CONFIGURAZIONE_PDD);
						
			Attribute attribute = new Attribute(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR, false);
			jmxconn.setAttribute(jmxname, attribute );

			Attribute attribute2 = new Attribute(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE, false);
			jmxconn.setAttribute(jmxname, attribute2 );
			
		}catch(Exception e){
			throw e;
		}
	}
	
	public void enableGovWayInternalError() throws Exception{
		try{
			
			MBeanServerConnection jmxconn = RisorseEsterne.getMBeanServerConnection(this.unitsTestsuiteProperties);
			
			ObjectName jmxname = new ObjectName(CostantiJMX.JMX_DOMINIO+":"+CostantiJMX.JMX_TYPE+"="+CostantiPdD.JMX_CONFIGURAZIONE_PDD);
						
			Attribute attribute = new Attribute(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR, true);
			jmxconn.setAttribute(jmxname, attribute );

		}catch(Exception e){
			throw e;
		}
	}
	
	public void disableGovWayInternalError() throws Exception{
		try{
			
			MBeanServerConnection jmxconn = RisorseEsterne.getMBeanServerConnection(this.unitsTestsuiteProperties);
			
			ObjectName jmxname = new ObjectName(CostantiJMX.JMX_DOMINIO+":"+CostantiJMX.JMX_TYPE+"="+CostantiPdD.JMX_CONFIGURAZIONE_PDD);
						
			Attribute attribute = new Attribute(ConfigurazionePdD.ERRORI_FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR, false);
			jmxconn.setAttribute(jmxname, attribute );

		}catch(Exception e){
			throw e;
		}
	}
}
