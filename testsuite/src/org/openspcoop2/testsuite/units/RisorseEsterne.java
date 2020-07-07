/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

import java.util.Hashtable;
import java.util.Vector;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.jmx.MonitoraggioRisorse;
import org.openspcoop2.testsuite.core.TestSuiteProperties;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.utils.jmx.CostantiJMX;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;



/**
 * Controlla che le risorse sia state rilasciate correttamente, e i log non possiedano keyword non trasformate
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RisorseEsterne extends GestioneViaJmx {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "RisorseEsterne";
	
	
	private UnitsDatabaseProperties unitsDatabaseProperties;
	
	public RisorseEsterne(UnitsTestSuiteProperties unitsTestsuiteProperties, UnitsDatabaseProperties unitsDatabaseProperties){
		super(unitsTestsuiteProperties);
		this.unitsDatabaseProperties = unitsDatabaseProperties;
	}
	
	
	@DataProvider (name="testDatabaseOpenSPCoop")
	public Object[][]testDatabaseOpenSPCoop()throws Exception{
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
		return new Object[][]{
				{this.unitsDatabaseProperties.newInstanceDatabaseComponentFruitore()},
				{this.unitsDatabaseProperties.newInstanceDatabaseComponentErogatore()}
		};
	}
	@Test(groups={RisorseEsterne.ID_GRUPPO,RisorseEsterne.ID_GRUPPO+".DB_OPENSPCOOP"},
			dataProvider="testDatabaseOpenSPCoop")
	public void testDatabaseOpenSPCoop(DatabaseComponent data) throws Exception{
		try{
			Reporter.log("Controllo database...");
			Vector<String> anomalie = data.getVerificatoreMessaggi().getTabelleNonCorrettamenteSvuotate(TestSuiteProperties.getInstance().getTipoRepositoryBuste());
			Reporter.log("Anomalie riscontrate: "+anomalie.size());
			for(int i=0; i<anomalie.size(); i++){
				Reporter.log(anomalie.get(i));
			}
			Assert.assertTrue(anomalie.size()==0);
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	@DataProvider (name="testDatabaseMsgDiagnostici")
	public Object[][]testDatabaseMsgDiagnostici()throws Exception{
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
		return new Object[][]{
				{this.unitsDatabaseProperties.newInstanceDatabaseComponentDiagnosticaFruitore()},
				{this.unitsDatabaseProperties.newInstanceDatabaseComponentDiagnosticaFruitore()}
		};
	}
	@Test(groups={RisorseEsterne.ID_GRUPPO,RisorseEsterne.ID_GRUPPO+".DB_MSG_DIAGNOSTICI"},
			dataProvider="testDatabaseMsgDiagnostici")
	public void testDatabaseMsgDiagnostici(DatabaseMsgDiagnosticiComponent data) throws Exception{
		try{
			
			Reporter.log("Controllo Keyword non trasformate (@KEYWORD@)...");
			Vector<String> anomalie = data.getMessaggiNonTrasformatiCorrettamente();
			Reporter.log("Anomalie riscontrate: "+anomalie.size());
			for(int i=0; i<anomalie.size(); i++){
				Reporter.log(anomalie.get(i));
			}
			Assert.assertTrue(anomalie.size()==0);
			
			Reporter.log("Controllo NullPointerException ...");
			Vector<String> anomalieNullPointer = data.getMessaggiCheSegnalanoNullPointer();
			Reporter.log("Anomalie riscontrate: "+anomalieNullPointer.size());
			for(int i=0; i<anomalieNullPointer.size(); i++){
				Reporter.log(anomalieNullPointer.get(i));
			}
			Assert.assertTrue(anomalieNullPointer.size()==0);
			
			Reporter.log("Controllo Codici diagnostici ...");
			Vector<String> anomalieCodiciDiagnostici = data.getMessaggiSenzaCodici();
			Reporter.log("Anomalie Codici diagnostici: "+anomalieCodiciDiagnostici.size());
			for(int i=0; i<anomalieCodiciDiagnostici.size(); i++){
				Reporter.log(anomalieCodiciDiagnostici.get(i));
			}
			Assert.assertTrue(anomalieCodiciDiagnostici.size()==0);
			
			Reporter.log("Controllo errori di tracciamento non riuscito ...");
			Vector<String> anomalieTracciamentiNonRiusciti = data.getTracciamentoNonRiuscito();
			Reporter.log("Anomalie tracciamento non riuscito: "+anomalieTracciamentiNonRiusciti.size());
			for(int i=0; i<anomalieTracciamentiNonRiusciti.size(); i++){
				Reporter.log(anomalieTracciamentiNonRiusciti.get(i));
			}
			Assert.assertTrue(anomalieTracciamentiNonRiusciti.size()==0);
						
			

			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	public static MBeanServerConnection getMBeanServerConnection(UnitsTestSuiteProperties unitsTestsuiteProperties) throws Exception{
		// Controllo JMX
		
		String version_jbossas = unitsTestsuiteProperties.getApplicationServerVersion();
		//System.out.println("JBOSS_VERSIONE["+version_jbossas+"]");
		
		MBeanServerConnection jmxconn = null;
		// eliminato supporto di jboss
//		if("jboss7".equals(version_jbossas) || 
//				(version_jbossas!=null && version_jbossas.startsWith("wildfly")) || 
//				version_jbossas.startsWith("tomcat")){
			
		String as = version_jbossas;
		if(version_jbossas.startsWith("tomcat")){
			as = "tomcat";
		}
		
		JMXServiceURL serviceURL = new JMXServiceURL(unitsTestsuiteProperties.getJMXServiceURL(as));   
		Hashtable<String, Object> env = null;
		if(unitsTestsuiteProperties.getJMXUsername()!=null && unitsTestsuiteProperties.getJMXPassword()!=null){
			String[] creds = {unitsTestsuiteProperties.getJMXUsername(), unitsTestsuiteProperties.getJMXPassword()};
			env = new Hashtable<String, Object>();
			env.put(JMXConnector.CREDENTIALS, creds);
		}
		JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceURL, env);           
		jmxconn = jmxConnector.getMBeanServerConnection();	
//		}else{
//			Hashtable<String, Object> env = new Hashtable<String, Object>();
//			env.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, this.unitsTestsuiteProperties.getJMXFactory());
//			env.put(javax.naming.Context.PROVIDER_URL, this.unitsTestsuiteProperties.getJMXServer());
//			
//			javax.naming.Context ctx = new javax.naming.InitialContext(env);
//			jmxconn = (MBeanServerConnection) ctx.lookup("jmx/invoker/RMIAdaptor");
//			SecurityAssociation.setPrincipal(new SimplePrincipal(this.unitsTestsuiteProperties.getJMXUsername()));
//			SecurityAssociation.setCredential(this.unitsTestsuiteProperties.getJMXPassword());
//		}
		
		return jmxconn;
	}
	
	@Test(groups={RisorseEsterne.ID_GRUPPO,RisorseEsterne.ID_GRUPPO+".RISORSE_JMX"})
	public void testRisorseJMX() throws Exception{
		try{
			
			// Controllo JMX
			MBeanServerConnection jmxconn = RisorseEsterne.getMBeanServerConnection(this.unitsTestsuiteProperties);
						
			ObjectName jmxname = new ObjectName(CostantiJMX.JMX_DOMINIO+":"+CostantiJMX.JMX_TYPE+"="+CostantiPdD.JMX_MONITORAGGIO_RISORSE);
			
			Reporter.log("Controllo risorse jmx: connessioni database ...");
			Object response = jmxconn.invoke(jmxname, MonitoraggioRisorse.CONNESSIONI_ALLOCATE_DB_MANAGER, null, null);
			Reporter.log("Controllo risorse jmx: connessioni database  ["+response+"]");
			boolean statoOk1 = "Nessuna connessione allocata".equals(response);
			boolean statoOk2 = (response instanceof String) && ((String)response).contains("1 risorse allocate") && ((String)response).contains("GovWay.Timer");
			Reporter.log("Verifiche connessioni database stato1["+statoOk1+"] stato2["+statoOk2+"]");
			Assert.assertTrue(statoOk1 || statoOk2);
			
			Reporter.log("Controllo risorse jmx: connessioni jms ...");
			response = jmxconn.invoke(jmxname, MonitoraggioRisorse.CONNESSIONI_ALLOCATE_QUEUE_MANAGER, null, null);
			Reporter.log("Controllo risorse jmx: connessioni jms  ["+response+"]");
			Assert.assertTrue("Nessuna connessione allocata".equals(response));
			
			Reporter.log("Controllo risorse jmx: connessioni http PD ...");
			response = jmxconn.invoke(jmxname, MonitoraggioRisorse.CONNESSIONI_ALLOCATE_CONNETTORI_PD, null, null);
			Reporter.log("Controllo risorse jmx: connessioni http PD  ["+response+"]");
			Assert.assertTrue("Nessuna connessione allocata".equals(response));
			
			Reporter.log("Controllo risorse jmx: connessioni http PA ...");
			response = jmxconn.invoke(jmxname,  MonitoraggioRisorse.CONNESSIONI_ALLOCATE_CONNETTORI_PA, null, null);
			Reporter.log("Controllo risorse jmx: connessioni http PA  ["+response+"]");
			Assert.assertTrue("Nessuna connessione allocata".equals(response));
			
			Reporter.log("Controllo risorse jmx: transazioni attive ...");
			response = jmxconn.invoke(jmxname,  MonitoraggioRisorse.TRANSAZIONI_ATTIVE_ID, null, null);
			Reporter.log("Controllo risorse jmx: transazioni attive  ["+response+"]");
			Assert.assertTrue("Nessuna transazione attiva".equals(response));
			
			// ripristino sistema di default
			super.disableGovWayStatus();
			super.disableGovWayDetails();
			super.disableGovWayRequestError();
			super.disableGovWayResponseError();
			super.disableGovWayInternalError();
			
		}catch(Exception e){
			throw e;
		}
	}
}
