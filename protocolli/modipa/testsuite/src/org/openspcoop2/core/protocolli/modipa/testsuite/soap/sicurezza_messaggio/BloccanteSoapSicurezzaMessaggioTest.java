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


package org.openspcoop2.core.protocolli.modipa.testsuite.soap.sicurezza_messaggio;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.openspcoop2.core.protocolli.modipa.testsuite.ConfigLoader;

import com.intuit.karate.FileUtils;
import com.intuit.karate.KarateOptions;
import com.intuit.karate.junit4.Karate;
import com.intuit.karate.netty.FeatureServer;


/**
* BloccanteSoapSicurezzaMessaggioTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
@RunWith(Karate.class)
@KarateOptions(features = {
    "classpath:test/soap/sicurezza-messaggio/idas01.feature",
    "classpath:test/soap/sicurezza-messaggio/idas01-no-disclosure.feature",
    "classpath:test/soap/sicurezza-messaggio/idas02.feature",
    "classpath:test/soap/sicurezza-messaggio/idas03.feature",
    "classpath:test/soap/sicurezza-messaggio/idas0302.feature",
    })

public class BloccanteSoapSicurezzaMessaggioTest extends ConfigLoader {
    
    private static FeatureServer server;
    private static FeatureServer proxy;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@BeforeClass
    public static void beforeClass() {       
        File file = FileUtils.getFileRelativeTo(BloccanteSoapSicurezzaMessaggioTest.class, "mock.feature");
        server = FeatureServer.start(file, Integer.valueOf(prop.getProperty("http_mock_port")), false, new HashMap<String,Object>((Map) prop));

        file = FileUtils.getFileRelativeTo(BloccanteSoapSicurezzaMessaggioTest.class, "proxy.feature");
        proxy = FeatureServer.start(file, Integer.valueOf(prop.getProperty("http_port")), false, new HashMap<String,Object>((Map) prop));
    }
        
    @AfterClass
    public static void afterClass() {
        proxy.stop();
        server.stop();
    }     
    
}