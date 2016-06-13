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



package org.openspcoop2.testsuite.units;

import java.util.Hashtable;
import java.util.Vector;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.jboss.security.SecurityAssociation;
import org.jboss.security.SimplePrincipal;
import org.openspcoop2.testsuite.core.TestSuiteProperties;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
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
public class RisorseEsterne {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "RisorseEsterne";
	
	
	private UnitsDatabaseProperties unitsDatabaseProperties;
	private UnitsTestSuiteProperties unitsTestsuiteProperties;
	
	public RisorseEsterne(UnitsTestSuiteProperties unitsTestsuiteProperties, UnitsDatabaseProperties unitsDatabaseProperties){
		this.unitsTestsuiteProperties = unitsTestsuiteProperties;
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
	
	
	
	
	
	@Test(groups={RisorseEsterne.ID_GRUPPO,RisorseEsterne.ID_GRUPPO+".RISORSE_JMX"})
	public void testRisorseJMX() throws Exception{
		try{
			
			
			// Controllo JMX
			
			String version_jbossas = this.unitsTestsuiteProperties.getApplicationServerVersion();
			//System.out.println("JBOSS_VERSIONE["+version_jbossas+"]");
			
			MBeanServerConnection jmxconn = null;
			if("jboss7".equals(version_jbossas) || 
					(version_jbossas!=null && version_jbossas.startsWith("wildfly")) || 
					version_jbossas.startsWith("tomcat")){
				
				String as = version_jbossas;
				if(version_jbossas.startsWith("tomcat")){
					as = "tomcat";
				}
				
				JMXServiceURL serviceURL = new JMXServiceURL(this.unitsTestsuiteProperties.getJMXServiceURL(as));   
				Hashtable<String, Object> env = null;
				if(this.unitsTestsuiteProperties.getJMXUsername()!=null && this.unitsTestsuiteProperties.getJMXPassword()!=null){
					String[] creds = {this.unitsTestsuiteProperties.getJMXUsername(), this.unitsTestsuiteProperties.getJMXPassword()};
					env = new Hashtable<String, Object>();
					env.put(JMXConnector.CREDENTIALS, creds);
				}
				JMXConnector jmxConnector = JMXConnectorFactory.connect(serviceURL, env);           
				jmxconn = jmxConnector.getMBeanServerConnection();	
			}else{
				Hashtable<String, Object> env = new Hashtable<String, Object>();
				env.put(Context.INITIAL_CONTEXT_FACTORY, this.unitsTestsuiteProperties.getJMXFactory());
				env.put(Context.PROVIDER_URL, this.unitsTestsuiteProperties.getJMXServer());
				
				Context ctx = new InitialContext(env);
				jmxconn = (MBeanServerConnection) ctx.lookup("jmx/invoker/RMIAdaptor");
				SecurityAssociation.setPrincipal(new SimplePrincipal(this.unitsTestsuiteProperties.getJMXUsername()));
				SecurityAssociation.setCredential(this.unitsTestsuiteProperties.getJMXPassword());
			}			

			
			ObjectName jmxname = new ObjectName("org.openspcoop2.pdd:type=MonitoraggioRisorse");
						
			Reporter.log("Controllo risorse jmx: connessioni database ...");
			Object response = jmxconn.invoke(jmxname, "getUsedDBConnections", null, null);
			Reporter.log("Controllo risorse jmx: connessioni database  ["+response+"]");
			Assert.assertTrue("Nessuna connessione allocata".equals(response));
			
			Reporter.log("Controllo risorse jmx: connessioni jms ...");
			response = jmxconn.invoke(jmxname, "getUsedQueueConnections", null, null);
			Reporter.log("Controllo risorse jmx: connessioni jms  ["+response+"]");
			Assert.assertTrue("Nessuna connessione allocata".equals(response));
			
			Reporter.log("Controllo risorse jmx: connessioni http PD ...");
			response = jmxconn.invoke(jmxname, "getActivePDConnections", null, null);
			Reporter.log("Controllo risorse jmx: connessioni http PD  ["+response+"]");
			Assert.assertTrue("Nessuna connessione allocata".equals(response));
			
			Reporter.log("Controllo risorse jmx: connessioni http PA ...");
			response = jmxconn.invoke(jmxname, "getActivePAConnections", null, null);
			Reporter.log("Controllo risorse jmx: connessioni http PA  ["+response+"]");
			Assert.assertTrue("Nessuna connessione allocata".equals(response));
			
		}catch(Exception e){
			throw e;
		}
	}
}
