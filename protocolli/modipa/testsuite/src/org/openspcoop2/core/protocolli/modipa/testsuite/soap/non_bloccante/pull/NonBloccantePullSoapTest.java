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
package org.openspcoop2.core.protocolli.modipa.testsuite.soap.non_bloccante.pull;

import org.openspcoop2.core.protocolli.modipa.testsuite.ConfigLoader;
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
 * NonBloccantePullSoapTest
 * 
 * This class sets up the MockServer and runs the tests for each feature.
 * 
 * @version $Rev$, $Date$
 */
public class NonBloccantePullSoapTest extends ConfigLoader {
    
    // Il test instanzia due servlet: il mock, contattato dall'erogazione
    // e il proxy, contattato dalla fruizione.
    // Il server di mock si comporta come il server di echo, solo che è più
    // flessibile nel generare risposte.
    private static MockServer mock;
    private static MockServer proxy;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @BeforeAll
    public static void beforeAll() {
        File file = ResourceUtils.getFileRelativeTo(NonBloccantePullSoapTest.class, "mock.feature");
        mock = MockServer
                .feature(file)
                .args(new HashMap<String,Object>((Map) prop))
                .http(Integer.valueOf(prop.getProperty("http_mock_port")))
                .build();

        file = ResourceUtils.getFileRelativeTo(NonBloccantePullSoapTest.class, "proxy.feature");
        proxy = MockServer
                .feature(file)
                .args(new HashMap<String,Object>((Map) prop))
                .http(Integer.valueOf(prop.getProperty("http_port")))
                .build();
    }
    
	@Test
    Karate testAll() {
        return Karate.run("classpath:test/soap/non-bloccante/pull/pull.feature",
            "classpath:test/soap/non-bloccante/pull/pull-no-disclosure.feature").relativeTo(getClass());

     }

    @AfterAll
    public static void afterAll() {
        mock.stop();
        proxy.stop();
    }
}

