/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.protocolli.modipa.testsuite.tracciamento_pdnd;

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
 * TracciamentoPdndTest
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@RunWith(Karate.class)
@KarateOptions( features = { 
    "classpath:test/tracciamento_pdnd/tracciamento_pdnd.feature",
    })

public class TracciamentoPdndTest extends ConfigLoader { 
    
    private static FeatureServer server;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@BeforeClass
    public static void beforeClass() {       
        File file = FileUtils.getFileRelativeTo(TracciamentoPdndTest.class, "mock.feature");
        server = FeatureServer.start(file, Integer.valueOf(prop.getProperty("http_mock_port")),
                false,
                new HashMap<>((Map) prop)
            );
    }
        
    @AfterClass
    public static void afterClass() {
        server.stop();
    }     

}
