/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.json.validation.test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.openspcoop2.utils.json.IJsonSchemaValidator;
import org.openspcoop2.utils.json.JsonSchemaValidatorConfig;
import org.openspcoop2.utils.json.JsonSchemaValidatorConfig.POLITICA_INCLUSIONE_TIPI;
import org.openspcoop2.utils.json.JsonValidatorAPI.ApiName;
import org.openspcoop2.utils.json.ValidationResponse;
import org.openspcoop2.utils.json.ValidationResponse.ESITO;
import org.openspcoop2.utils.json.ValidatorFactory;

/**
 * ValidatorTest
 *
 * @author Giovanni Bussu (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ValidatorTest {

	private static byte[] schema;
	private static ExecutorService executor;

	public static void main(String[] args) throws Exception {
		ValidatorTest.executor = Executors.newFixedThreadPool(100);
		ValidatorTest.schema = ValidatorTest.loadResource("schema.json");
		byte[] json2M = ValidatorTest.loadResource("file2M.json");
		byte[] jsonInvalid = ValidatorTest.loadResource("file1K_invalid.json");
		byte[] json1K = ValidatorTest.loadResource("file1K.json");
		byte[] json50K = ValidatorTest.loadResource("file50K.json");
		byte[] json500K = ValidatorTest.loadResource("file500K.json");
		
		List<byte[]> file1K = new ArrayList<byte[]>();
		file1K.add(json1K);

		List<byte[]> file50K = new ArrayList<byte[]>();
		file50K.add(json50K);

		List<byte[]> file500K = new ArrayList<byte[]>();
		file500K.add(json500K);

		List<byte[]> fileNonValidi = new ArrayList<byte[]>();
		fileNonValidi.add(jsonInvalid);

		List<byte[]> file2M = new ArrayList<byte[]>();
		file2M.add(json2M);

		for(ApiName name : ApiName.values()) {
			ValidatorTest.validazioneListaFile("fileNonValidi", name, fileNonValidi, 10, false);
			ValidatorTest.validazioneListaFile("file1K", name, file1K, 10000, true);
			ValidatorTest.validazioneListaFile("file50K", name, file50K, 1000, true);
			ValidatorTest.validazioneListaFile("file500K", name, file500K, 100, true);
			ValidatorTest.validazioneListaFile("file2M", name, file2M, 10, true);
		}
		ValidatorTest.executor.shutdown();
	}

	private static byte[] loadResource(String resourceName) throws Exception {
		InputStream is = ValidatorTest.class.getResourceAsStream(resourceName);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int c = is.read(buf);
		while(c >=0 ) {
			baos.write(buf, 0, c);
			c = is.read(buf);
		}
		baos.flush();
		baos.close();
		return baos.toByteArray();
	}

	private static void validazioneListaFile(String testName, ApiName name, List<byte[]> files, int nTimes, boolean expectedSuccess) throws Exception {

		IJsonSchemaValidator validator = ValidatorFactory.newJsonSchemaValidator(name);

		JsonSchemaValidatorConfig config = new JsonSchemaValidatorConfig();
		config.setPoliticaInclusioneTipi(POLITICA_INCLUSIONE_TIPI.ANY);
		config.setTipi(Arrays.asList("#/definitions/Pet"));
		validator.setSchema(ValidatorTest.schema, config);

		List<TestRunner> lst = new ArrayList<TestRunner>();
		for(byte[] file: files) {
			for(int i =0; i < nTimes; i++) {
				lst.add(new TestRunner(validator, file, expectedSuccess));
			}
		}
		long before = System.currentTimeMillis();
		List<Future<Boolean>> futures = ValidatorTest.executor.invokeAll(lst);
		for(Future<Boolean> result: futures) {
			if(!result.get())
				if(expectedSuccess) {
					throw new Exception("Riscontrato errore validazione non atteso con validatore ["+validator.getClass().getName()+"]");	
				} else {
					throw new Exception("Atteso errore validazione non riscontrato con validatore ["+validator.getClass().getName()+"]");
				}
				
		}
		long after = System.currentTimeMillis();
		System.out.println("test ["+testName+"] validatore["+name+"] Tempo ["+((after-before)/1000.0)+"]");
	}

	static class TestRunner implements Callable<Boolean> {

		private IJsonSchemaValidator validator;
		private byte[] instance;
		private boolean expectedSuccess;

		public TestRunner(IJsonSchemaValidator validator, byte[] instance, boolean expectedSuccess){
			this.validator = validator;
			this.instance = instance;
			this.expectedSuccess = expectedSuccess;
		}

		@Override
		public Boolean call() throws Exception {
			ValidationResponse response = this.validator.validate(this.instance);

			if(response.getErrors() != null && this.expectedSuccess) {
				for(String error: response.getErrors()) {
					System.out.println(error);
				}
			}
			
			boolean valid = response.getEsito().equals(ESITO.OK);
			return valid == this.expectedSuccess;
		}

	}

}
