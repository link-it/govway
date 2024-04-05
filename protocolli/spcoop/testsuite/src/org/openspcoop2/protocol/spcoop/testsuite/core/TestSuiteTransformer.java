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



package org.openspcoop2.protocol.spcoop.testsuite.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Properties;

import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.utils.ErroriProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;
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

	@Override
	public void transform(ITestAnnotation annotation, @SuppressWarnings("rawtypes") Class testClass, @SuppressWarnings("rawtypes") Constructor testConstructor, Method testMethod,
			Class<?> occurringClazz) {
		this._transform(annotation, testClass, testConstructor, testMethod);
	}

	@Override
	public void transform(ITestAnnotation annotation, @SuppressWarnings("rawtypes") Class testClass, @SuppressWarnings("rawtypes") Constructor testConstructor, Method testMethod){
		this._transform(annotation, testClass, testConstructor, testMethod);
	}
	
	public static Boolean sequentialForced = false;
	
	public static final String initializedSemaphore = "semaphore";
	public static Boolean initialized = false;
	
	private void _transform(ITestAnnotation annotation, @SuppressWarnings("rawtypes") Class testClass, @SuppressWarnings("rawtypes") Constructor testConstructor, Method testMethod){
		
		synchronized(initializedSemaphore) {
			if(initialized==false) {
				try {
					try{
						DateManager.initializeDataManager(org.openspcoop2.utils.date.SystemDate.class.getName(), new Properties(), LoggerWrapperFactory.getLogger(TestSuiteTransformer.class));
					}catch(Exception e){
						throw new RuntimeException(e.getMessage(), e);
					}
					
					try{
						ConfigurazionePdD config = new ConfigurazionePdD();
						config.setLoader(new Loader());
						LoggerWrapperFactory.setLogConfiguration(TestSuiteTransformer.class.getResource("/testsuite_spcoop.log4j2.properties"));
						Logger log = LoggerWrapperFactory.getLogger("govway.testsuite");
						config.setLog(log);
						ProtocolFactoryManager.initializeSingleProtocol(log, config, CostantiTestSuite.PROTOCOL_NAME);
						ErroriProperties.initialize(null, log, new Loader());
					}catch(Exception e){
						throw new RuntimeException(e.getMessage(),e);
					}
					
					org.openspcoop2.testsuite.core.CostantiTestSuite.setREAD_TIMEOUT(TestSuiteProperties.getInstance().getReadConnectionTimeout());
					org.openspcoop2.testsuite.core.CostantiTestSuite.setCONNECTION_TIMEOUT(TestSuiteProperties.getInstance().getConnectionTimeout());
					
					initialized = true;
				
				}catch(Throwable e){ // Lasciare Throwable
					System.out.println("ERRORE: "+e.getMessage());
					e.printStackTrace(System.err);
				}
			}
		}
		
		if(testMethod!=null){
			
			if(org.openspcoop2.protocol.spcoop.testsuite.units.messaggi_malformati.SOAPMessageScorretti.class.getName().equals(testMethod.getDeclaringClass().getName())){
				//annotation.setSequential(true);
				annotation.setSingleThreaded(true);
			}
			
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
					if(TestSuiteTransformer.sequentialForced) {
						//annotation.setSequential(true);
						annotation.setSingleThreaded(true);
					}else {
						//annotation.setSequential(Utilities.testSuiteProperties.sequentialTests());
						annotation.setSingleThreaded(Utilities.testSuiteProperties.sequentialTests());
					}
			}
		}
		
	}
	
}
