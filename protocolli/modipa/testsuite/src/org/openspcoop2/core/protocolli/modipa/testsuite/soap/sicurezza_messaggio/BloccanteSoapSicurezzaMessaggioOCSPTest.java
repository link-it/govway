/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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


package org.openspcoop2.core.protocolli.modipa.testsuite.soap.sicurezza_messaggio;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.openspcoop2.core.protocolli.modipa.testsuite.ConfigLoader;
import org.openspcoop2.utils.certificate.ocsp.test.OpenSSLThread;

import com.intuit.karate.FileUtils;
import com.intuit.karate.KarateOptions;
import com.intuit.karate.junit4.Karate;
import com.intuit.karate.netty.FeatureServer;


/**
* BloccanteSoapSicurezzaMessaggioOCSPTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
@RunWith(Karate.class)
@KarateOptions(features = {
    "classpath:test/soap/sicurezza-messaggio/idas01-ocsp.feature",
    })

public class BloccanteSoapSicurezzaMessaggioOCSPTest extends ConfigLoader {
    
    private static FeatureServer server;
    private static FeatureServer proxy;
    private static OpenSSLThread sslThread_case2;
    private static OpenSSLThread sslThread_case3;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@BeforeClass
    public static void beforeClass() {       
        File file = FileUtils.getFileRelativeTo(BloccanteSoapSicurezzaMessaggioTest.class, "mock.feature");
        server = FeatureServer.start(file, Integer.valueOf(prop.getProperty("http_mock_port")), false, new HashMap<>((Map) prop));

        file = FileUtils.getFileRelativeTo(BloccanteSoapSicurezzaMessaggioTest.class, "proxy.feature");
        proxy = FeatureServer.start(file, Integer.valueOf(prop.getProperty("http_port")), false, new HashMap<>((Map) prop));
        
    	String opensslCommand = prop.getProperty("ocsp.opensslCommand");
    	int waitStartupServer = Integer.valueOf(prop.getProperty("ocsp.waitStartupServer"));
    	try {
    		sslThread_case2 = OpenSSLThread.newOpenSSLThread_case2(opensslCommand, waitStartupServer);
    	}catch(Throwable t) {
    		t.printStackTrace(System.out);
    	}
    	try {
    		sslThread_case3 = OpenSSLThread.newOpenSSLThread_case3(opensslCommand, waitStartupServer);
    	}catch(Throwable t) {
    		t.printStackTrace(System.out);
    	}
    }
        
    @AfterClass
    public static void afterClass() {
        proxy.stop();
        server.stop();
        int waitStopServer = Integer.valueOf(prop.getProperty("ocsp.waitStopServer"));
        try {
        	OpenSSLThread.stopOpenSSLThread(sslThread_case2, waitStopServer);
    	}catch(Throwable t) {
    		t.printStackTrace(System.out);
    	}
        try {
        	OpenSSLThread.stopOpenSSLThread(sslThread_case3, waitStopServer);
    	}catch(Throwable t) {
    		t.printStackTrace(System.out);
    	}
    }     
    
}
