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


package org.openspcoop2.core.protocolli.modipa.testsuite.rest.sicurezza_messaggio;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.modipa.testsuite.ConfigLoader;
import org.openspcoop2.utils.certificate.ocsp.test.OpenSSLThread;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import com.intuit.karate.core.MockServer;
import com.intuit.karate.resource.ResourceUtils;


/**
* BloccanteRestSicurezzaMessaggioOCSPTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/

public class BloccanteRestSicurezzaMessaggioOCSPTest extends ConfigLoader {
    
	private static MockServer server;
    private static MockServer proxy;
    private static OpenSSLThread sslThread_case2;
    private static OpenSSLThread sslThread_case3;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@BeforeClass
    public static void beforeClass() {
        File file = ResourceUtils.getFileRelativeTo(BloccanteRestSicurezzaMessaggioOCSPTest.class, "mock.feature");
        server = MockServer
                .feature(file)
                .args(new HashMap<String,Object>((Map) prop))
                .http(Integer.valueOf(prop.getProperty("http_mock_port")))
                .build();

        file = ResourceUtils.getFileRelativeTo(BloccanteRestSicurezzaMessaggioOCSPTest.class, "proxy.feature");
        proxy = MockServer
    			.feature(file)
    			.args(new HashMap<String,Object>((Map) prop))
    			.http(Integer.valueOf(prop.getProperty("http_port")))
    			.build();
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
