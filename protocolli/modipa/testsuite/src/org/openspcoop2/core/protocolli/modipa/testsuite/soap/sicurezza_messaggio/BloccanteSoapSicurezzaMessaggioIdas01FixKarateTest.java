package org.openspcoop2.core.protocolli.modipa.testsuite.soap.sicurezza_messaggio;

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

public class BloccanteSoapSicurezzaMessaggioIdas01FixKarateTest extends ConfigLoader {

	private static MockServer server;
	private static MockServer proxy;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@BeforeClass
	public static void beforeClass() {

		File file = ResourceUtils.getFileRelativeTo(BloccanteSoapSicurezzaMessaggioIdas02FixKarateTest.class,
				"mock.feature");
		server = MockServer.feature(file).args(new HashMap<String, Object>((Map) prop))
				.http(Integer.valueOf(prop.getProperty("http_mock_port"))).build();

		file = ResourceUtils.getFileRelativeTo(BloccanteSoapSicurezzaMessaggioIdas02FixKarateTest.class,
				"proxy.feature");
		proxy = MockServer.feature(file).args(new HashMap<String, Object>((Map) prop))
				.http(Integer.valueOf(prop.getProperty("http_port"))).build();
	}
	
	
	  @Test
	  public void test() {
	    	Results results = Runner.path(Arrays.asList( 
	    			"classpath:test/soap/sicurezza-messaggio/idas01.feature",
	    		    "classpath:test/soap/sicurezza-messaggio/idas01-no-disclosure.feature"))	  
	    			.parallel(1);
	    	assertEquals(0, results.getFailCount());
	   
	    }

	     
    
	@AfterClass
	public static void afterClass() {
		proxy.stop();
		server.stop();
	}

}
