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

import org.openspcoop2.core.protocolli.modipa.testsuite.ConfigLoader;
import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import com.intuit.karate.junit5.Karate;
import com.intuit.karate.junit5.Karate.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.intuit.karate.resource.ResourceUtils;
import com.intuit.karate.core.MockServer;


/**
 * BloccanteRestSicurezzaMessaggioTest
 * 
 * This class sets up the MockServer and Proxy for the Karate tests,
 * and runs the tests for each feature.
 * 
 * @version $Rev$, $Date$
 */
public class BloccanteRestSicurezzaMessaggioTest extends ConfigLoader {

    private static MockServer server;
    private static MockServer proxy;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @BeforeAll
    public static void beforeAll() {
        File file = ResourceUtils.getFileRelativeTo(BloccanteRestSicurezzaMessaggioTest.class, "mock.feature");
        server = MockServer
                .feature(file)
                .args(new HashMap<String,Object>((Map) prop))
                .http(Integer.valueOf(prop.getProperty("http_mock_port")))
                .build();

        file = ResourceUtils.getFileRelativeTo(BloccanteRestSicurezzaMessaggioTest.class, "proxy.feature");
        proxy = MockServer
                .feature(file)
                .args(ConfigLoader.getConfig())
                .http(Integer.valueOf(prop.getProperty("http_port")))
                .build();
    }

/*
    @Test
    Karate testIdar01() {
        return Karate.run("classpath:test/rest/sicurezza-messaggio/idar01.feature").relativeTo(getClass());
    }
    @Test
    Karate testIdar01NoDisclosure() {
        return Karate.run("classpath:test/rest/sicurezza-messaggio/idar01-no-disclosure.feature").relativeTo(getClass());
        
    }
    @Test
    Karate testIdar02() {
        return Karate.run("classpath:test/rest/sicurezza-messaggio/idar02.feature").relativeTo(getClass());
    }

    @Test
    Karate testIdar03() {
        return Karate.run("classpath:test/rest/sicurezza-messaggio/idar03.feature").relativeTo(getClass());
    }

    @Test
    Karate testIdar04() {
        return Karate.run("classpath:test/rest/sicurezza-messaggio/idar04.feature").relativeTo(getClass());
    }

    @Test
    Karate testIdar0402() {
        return Karate.run("classpath:test/rest/sicurezza-messaggio/idar0402.feature").relativeTo(getClass());
    }

    @Test
    Karate testIdar0302() {
        return Karate.run("classpath:test/rest/sicurezza-messaggio/idar0302.feature").relativeTo(getClass());
    }

    @Test
    Karate testIdar03custom() {
        return Karate.run("classpath:test/rest/sicurezza-messaggio/idar03custom.feature").relativeTo(getClass());
    }

    @Test
    Karate testIdar04custom() {
        return Karate.run("classpath:test/rest/sicurezza-messaggio/idar04custom.feature").relativeTo(getClass());
    }

    @Test
    Karate testAutorizzazioneMessaggio() {
        return Karate.run("classpath:test/rest/sicurezza-messaggio/autorizzazioneMessaggio.feature").relativeTo(getClass());
    }

    @Test
    Karate testAutorizzazioneToken() {
        return Karate.run("classpath:test/rest/sicurezza-messaggio/autorizzazioneToken.feature").relativeTo(getClass());
    }

    @Test
    Karate testAutorizzazioneMessaggioToken() {
        return Karate.run("classpath:test/rest/sicurezza-messaggio/autorizzazioneMessaggioToken.feature").relativeTo(getClass());
    }

    @Test
    Karate testNegoziazioneToken() {
        return Karate.run("classpath:test/rest/sicurezza-messaggio/negoziazioneToken.feature").relativeTo(getClass());
    }
    
    */
    
    @Test
    Karate testAll() {
        return Karate.run("classpath:test/rest/sicurezza-messaggio/idar01.feature",
                    "classpath:test/rest/sicurezza-messaggio/idar01-no-disclosure.feature",
            "classpath:test/rest/sicurezza-messaggio/idar02.feature",
            "classpath:test/rest/sicurezza-messaggio/idar03.feature",
            "classpath:test/rest/sicurezza-messaggio/idar04.feature",
            "classpath:test/rest/sicurezza-messaggio/idar0402.feature", // lasciare prima degli altri 03 altrimenti vanno in conflitto di attachments
            "classpath:test/rest/sicurezza-messaggio/idar0302.feature",
            "classpath:test/rest/sicurezza-messaggio/idar03custom.feature",
            "classpath:test/rest/sicurezza-messaggio/idar04custom.feature",
            "classpath:test/rest/sicurezza-messaggio/autorizzazioneMessaggio.feature",
            "classpath:test/rest/sicurezza-messaggio/autorizzazioneToken.feature",
            "classpath:test/rest/sicurezza-messaggio/autorizzazioneMessaggioToken.feature",
            "classpath:test/rest/sicurezza-messaggio/negoziazioneToken.feature").relativeTo(getClass());
        }
        

    @AfterAll
    public static void afterAll() {
        proxy.stop();
        server.stop();
    }
}
