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
package org.openspcoop2.core.protocolli.modipa.testsuite.soap.sicurezza_messaggio;

import org.openspcoop2.core.protocolli.modipa.testsuite.ConfigLoader;
import org.openspcoop2.utils.certificate.ocsp.test.OpenSSLThread;
import com.intuit.karate.junit5.Karate;
import com.intuit.karate.junit5.Karate.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.intuit.karate.resource.ResourceUtils;
import com.intuit.karate.core.MockServer;

/**
 * BloccanteSoapSicurezzaMessaggioTest
 * 
 * This class sets up the MockServer and runs the tests for each feature.
 * 
 * @version $Rev$, $Date$
 */
public class BloccanteSoapSicurezzaMessaggioTest extends ConfigLoader {
    
    private static MockServer server;
    private static MockServer proxy;
    private static OpenSSLThread sslThread_case2;
    private static OpenSSLThread sslThread_case3;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @BeforeAll
    public static void beforeAll() {
        File file = ResourceUtils.getFileRelativeTo(BloccanteSoapSicurezzaMessaggioTest.class, "mock.feature");
        server = MockServer
                .feature(file)
                .args(new HashMap<String,Object>((Map) prop))
                .http(Integer.valueOf(prop.getProperty("http_mock_port")))
                .build();

        file = ResourceUtils.getFileRelativeTo(BloccanteSoapSicurezzaMessaggioTest.class, "proxy.feature");
        proxy = MockServer
                .feature(file)
                .args(new HashMap<String,Object>((Map) prop))
                .http(Integer.valueOf(prop.getProperty("http_port")))
                .build();

        String opensslCommand = prop.getProperty("ocsp.opensslCommand");
        int waitStartupServer = Integer.valueOf(prop.getProperty("ocsp.waitStartupServer"));
        try {
            sslThread_case2 = OpenSSLThread.newOpenSSLThread_case2(opensslCommand, waitStartupServer);
        } catch (Throwable t) {
            t.printStackTrace(System.out);
        }
        try {
            sslThread_case3 = OpenSSLThread.newOpenSSLThread_case3(opensslCommand, waitStartupServer);
        } catch (Throwable t) {
            t.printStackTrace(System.out);
        }
    }
    /*
    @Test
    Karate testIdas01() {
        return Karate.run("classpath:test/soap/sicurezza-messaggio/idas01.feature").relativeTo(getClass());
    }

    @Test
    Karate testIdas01NoDisclosure() {
        return Karate.run("classpath:test/soap/sicurezza-messaggio/idas01-no-disclosure.feature").relativeTo(getClass());
    }

    @Test
    Karate testIdas02() {
        return Karate.run("classpath:test/soap/sicurezza-messaggio/idas02.feature").relativeTo(getClass());
    }

    @Test
    Karate testIdas03() {
        return Karate.run("classpath:test/soap/sicurezza-messaggio/idas03.feature").relativeTo(getClass());
    }

    @Test
    Karate testIdas0302() {
        return Karate.run("classpath:test/soap/sicurezza-messaggio/idas0302.feature").relativeTo(getClass());
    }

    @Test
    Karate testAutorizzazioneMessaggio() {
        return Karate.run("classpath:test/soap/sicurezza-messaggio/autorizzazioneMessaggio.feature").relativeTo(getClass());
    }

    @Test
    Karate testAutorizzazioneToken() {
        return Karate.run("classpath:test/soap/sicurezza-messaggio/autorizzazioneToken.feature").relativeTo(getClass());
    }
            */
    @Test
    Karate testAll() {
                    return Karate.run("classpath:test/soap/sicurezza-messaggio/idas01.feature",
           "classpath:test/soap/sicurezza-messaggio/idas01-no-disclosure.feature",
         "classpath:test/soap/sicurezza-messaggio/idas02.feature",
            "classpath:test/soap/sicurezza-messaggio/idas03.feature",
            "classpath:test/soap/sicurezza-messaggio/idas0302.feature",
	     "classpath:test/soap/sicurezza-messaggio/autorizzazioneMessaggio.feature",
	"classpath:test/soap/sicurezza-messaggio/autorizzazioneToken.feature").relativeTo(getClass());
     }
     
     
    @AfterAll
    public static void afterAll() {
        proxy.stop();
        server.stop();
        int waitStopServer = Integer.valueOf(prop.getProperty("ocsp.waitStopServer"));
        try {
            OpenSSLThread.stopOpenSSLThread(sslThread_case2, waitStopServer);
        } catch (Throwable t) {
            t.printStackTrace(System.out);
        }
        try {
            OpenSSLThread.stopOpenSSLThread(sslThread_case3, waitStopServer);
        } catch (Throwable t) {
            t.printStackTrace(System.out);
        }
}
}

