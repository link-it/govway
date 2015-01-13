/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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



package org.openspcoop2.protocol.spcoop.testsuite.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.Loader;
import org.testng.annotations.ITestAnnotation;
import org.testng.internal.annotations.IAnnotationTransformer;

/**
 * Permette di personalizzare la dimensione dei pool ed il numero di invocazioni per metodo
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TestSuiteTransformer implements IAnnotationTransformer{

	public static Boolean sequentialForced = false;
	
	@Override
	public void transform(ITestAnnotation annotation, @SuppressWarnings("rawtypes") Class testClass, @SuppressWarnings("rawtypes") Constructor testConstructor, Method testMethod){
		
		try{
			DateManager.initializeDataManager(org.openspcoop2.utils.date.SystemDate.class.getName(), new Properties(), Logger.getLogger(TestSuiteTransformer.class));
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(), e);
		}
		
		try{
			ConfigurazionePdD config = new ConfigurazionePdD();
			config.setLoader(new Loader());
			PropertyConfigurator.configure(TestSuiteTransformer.class.getResource("/testsuite_spcoop.log4j.properties"));
			Logger log = Logger.getLogger("TestSuite.tracer");
			config.setLog(log);
			ProtocolFactoryManager.initializeSingleProtocol(log, config, CostantiTestSuite.PROTOCOL_NAME);
		}catch(Exception e){
			throw new RuntimeException(e.getMessage(),e);
		}
		
		if(testMethod!=null){
			String methodName = testMethod.getName();
			if("startServerRicezioneRispostaAsincronaSimmetrica_modalitaAsincrona".equals(methodName)==false &&
			   "startServerRicezioneRispostaAsincronaSimmetrica_modalitaSincrona".equals(methodName)==false &&
			   "startServerRicezioneRispostaAsincronaSimmetrica_modalitaSincronaWSS".equals(methodName)==false &&
			   "startServerRicezioneRispostaAsincronaSimmetrica_modalitaAsincrona_Stateful".equals(methodName)==false &&
			   "startServerRicezioneRispostaAsincronaSimmetrica_modalitaSincrona_Stateful".equals(methodName)==false &&
			   "stopServerRicezioneRispostaAsincronaSimmetrica_modalitaAsincrona".equals(methodName)==false &&
			   "stopServerRicezioneRispostaAsincronaSimmetrica_modalitaSincrona".equals(methodName)==false &&
			   "stopServerRicezioneRispostaAsincronaSimmetrica_modalitaSincronaWSS".equals(methodName)==false &&
			   "stopServerRicezioneRispostaAsincronaSimmetrica_modalitaAsincrona_Stateful".equals(methodName)==false &&
			   "stopServerRicezioneRispostaAsincronaSimmetrica_modalitaSincrona_Stateful".equals(methodName)==false ){
					annotation.setInvocationCount(Utilities.testSuiteProperties.getWorkerNumber());
					annotation.setThreadPoolSize(Utilities.testSuiteProperties.getPoolSize());
					if(TestSuiteTransformer.sequentialForced)
						annotation.setSequential(true);
					else
						annotation.setSequential(Utilities.testSuiteProperties.sequentialTests());
			}
		}
	}
	
}
