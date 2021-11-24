
/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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


package org.openspcoop2.core.protocolli.modipa.testsuite.soap.non_bloccante.push;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.openspcoop2.core.protocolli.modipa.testsuite.ConfigLoader;

import com.intuit.karate.KarateOptions;
import com.intuit.karate.core.MockServer;
import com.intuit.karate.junit4.Karate;
import com.intuit.karate.resource.ResourceUtils;


/**
* NonBloccantePushSoapTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
@RunWith(Karate.class)
@KarateOptions(features = {
    "classpath:test/soap/non-bloccante/push/push.feature",
    "classpath:test/soap/non-bloccante/push/push-no-disclosure.feature",
    })

public class NonBloccantePushSoapTest extends ConfigLoader {
    
    // Il test instanzia due servlet: il mock, contattato dall'erogazione
    // e il proxy, contattato dalla fruizione.
    // Il server di mock si comporta come il server di echo, solo che è più
    // flsessibile nel generare risposte.
	 private static MockServer server;
	 private static MockServer proxy;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@BeforeClass
    public static void beforeClass() {
    	
        File file = ResourceUtils.getFileRelativeTo(NonBloccantePushSoapTest.class, "proxy.feature");
        server = MockServer
                .feature(file)
                .args(new HashMap<String,Object>((Map) prop))
                .http(Integer.valueOf(prop.getProperty("http_mock_port")))
                .build();
        

        file = ResourceUtils.getFileRelativeTo(NonBloccantePushSoapTest.class, "mock.feature");
    	proxy = MockServer
    			.feature(file)
    			.args(new HashMap<String,Object>((Map) prop))
    			.http(Integer.valueOf(prop.getProperty("http_port")))
    			.build();
    }
        
    @AfterClass
    public static void afterClass() {
        server.stop();
        proxy.stop();
    }     
    
}