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

package org.openspcoop2.core.protocolli.modipa.testsuite.rest.non_bloccante.pull;

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
 * NonBloccanteRestTest
 *
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

@RunWith(Karate.class)
@KarateOptions( features = { 
    "classpath:test/rest/non-bloccante/pull/pull.feature",
    "classpath:test/rest/non-bloccante/pull/pull-errori-fruizione.feature",
    "classpath:test/rest/non-bloccante/pull/pull-errori-erogazione.feature",
    "classpath:test/rest/non-bloccante/pull/pull-no-disclosure.feature",
    "classpath:test/rest/non-bloccante/pull/pull-errori-fruizione-no-disclosure.feature",
    "classpath:test/rest/non-bloccante/pull/pull-errori-erogazione-no-disclosure.feature"

    })
public class NonBloccanteRestTest extends ConfigLoader { 
    
    private static MockServer server;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@BeforeClass
    public static void beforeClass() {       
        File file = ResourceUtils.getFileRelativeTo(NonBloccanteRestTest.class, "mock-pull.feature");
        server = MockServer
    			.feature(file)
    			.args(new HashMap<String,Object>((Map) prop))
    			.http(Integer.valueOf(prop.getProperty("http_port")))
    			.build();
    }
        
    @AfterClass
    public static void afterClass() {
        server.stop();
    }     

}