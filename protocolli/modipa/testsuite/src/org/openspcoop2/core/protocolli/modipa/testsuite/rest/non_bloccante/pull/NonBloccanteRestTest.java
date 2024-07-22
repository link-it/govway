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
package org.openspcoop2.core.protocolli.modipa.testsuite.rest.non_bloccante.pull;

import org.openspcoop2.core.protocolli.modipa.testsuite.ConfigLoader;
import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import com.intuit.karate.junit5.Karate;
import com.intuit.karate.junit5.Karate.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.intuit.karate.resource.ResourceUtils;
import com.intuit.karate.core.MockServer;

/**
 * NonBloccanteRestTest
 *
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class NonBloccanteRestTest extends ConfigLoader {
    
    private static MockServer server;
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @BeforeAll
    public static void beforeAll() {
        File file = ResourceUtils.getFileRelativeTo(NonBloccanteRestTest.class, "mock-pull.feature");
	server = MockServer
    			.feature(file)
    			.args(new HashMap<String,Object>((Map) prop))
    			.http(Integer.valueOf(prop.getProperty("http_port")))
    			.build();
    }
/*    @Test
    Karate testPull() {
        return Karate.run("classpath:test/rest/non-bloccante/pull/pull.feature").relativeTo(getClass());
    }

    @Test
    Karate testPullErroriFruizione() {
        return Karate.run("classpath:test/rest/non-bloccante/pull/pull-errori-fruizione.feature").relativeTo(getClass());
    }

    @Test
    Karate testPullErroriErogazione() {
        return Karate.run("classpath:test/rest/non-bloccante/pull/pull-errori-erogazione.feature").relativeTo(getClass());
    }

    @Test
    Karate testPullNoDisclosure() {
        return Karate.run("test/rest/non-bloccante/pull/pull-no-disclosure.feature").relativeTo(getClass());
    }

    @Test
    Karate testPullErroriFruizioneNoDisclosure() {
        return Karate.run("classpath:test/rest/non-bloccante/pull/pull-errori-fruizione-no-disclosure").relativeTo(getClass());
    }

    @Test
    Karate testPullErroriEroazioneNoDisclosure() {
        return Karate.run("classpath:test/rest/non-bloccante/pull/pull-errori-erogazione-no-disclosure").relativeTo(getClass());
    }
    */
        @Test
    Karate testAll() {
        return Karate.run(
            "classpath:test/rest/non-bloccante/pull/pull.feature",
            "classpath:test/rest/non-bloccante/pull/pull-errori-fruizione.feature",
            "classpath:test/rest/non-bloccante/pull/pull-errori-erogazione.feature",
            "classpath:test/rest/non-bloccante/pull/pull-no-disclosure.feature",
            "classpath:test/rest/non-bloccante/pull/pull-errori-fruizione-no-disclosure",
            "classpath:test/rest/non-bloccante/pull/pull-errori-erogazione-no-disclosure"
        ).relativeTo(getClass());
    }


    @AfterAll
    public static void afterAll() {
        server.stop();
    }     

}
