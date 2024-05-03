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


package org.openspcoop2.core.protocolli.modipa.testsuite.rest.bloccante;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspcoop2.core.protocolli.modipa.testsuite.ConfigLoader;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import com.intuit.karate.core.MockServer;
import com.intuit.karate.resource.ResourceUtils;


/**
* BloccanteRestTest
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/

public class BloccanteRestTest extends ConfigLoader {
    
    private static MockServer server;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@BeforeClass
    public static void beforeClass() {       
        File file = ResourceUtils.getFileRelativeTo(BloccanteRestTest.class, "modipa-mock.feature");
        server = MockServer
    			.feature(file)
    			.args(new HashMap<String,Object>((Map) prop))
    			.http(Integer.valueOf(prop.getProperty("http_port")))
    			.build();
    }
    
    @Test
    public void test() {
    	Results results = Runner.path(Arrays.asList( 
    		    "classpath:test/rest/bloccante/modipa-proxy.feature",
    		    "classpath:test/rest/bloccante/echo.feature",
    		    "classpath:test/rest/bloccante/idac02.feature"))    		        			
    			.parallel(1);
    	assertEquals(0, results.getFailCount());
    }
        
    @AfterClass
    public static void afterClass() {
        server.stop();
    }     
    
}
