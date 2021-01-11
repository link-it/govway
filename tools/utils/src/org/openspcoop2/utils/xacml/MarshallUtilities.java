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

package org.openspcoop2.utils.xacml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.herasaf.xacml.core.SyntaxException;
import org.herasaf.xacml.core.WritingException;
import org.herasaf.xacml.core.context.RequestMarshaller;
import org.herasaf.xacml.core.context.ResponseMarshaller;
import org.herasaf.xacml.core.context.impl.RequestType;
import org.herasaf.xacml.core.context.impl.ResponseType;
import org.herasaf.xacml.core.context.impl.ResultType;
import org.herasaf.xacml.core.policy.Evaluatable;
import org.herasaf.xacml.core.policy.PolicyMarshaller;
import org.herasaf.xacml.core.simplePDP.initializers.InitializerExecutor;


/**
 * ResultCombining
 *
 * @author Bussu Giovanni (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MarshallUtilities {

	private static boolean runInitialized = false;
	private static synchronized void _runInitializers(){
		if(runInitialized==false){
			InitializerExecutor.runInitializers(); //Inizializza gli unmarshaller
			runInitialized = true;
		}
	}
	public static void runInitializers(){
		if(runInitialized==false){
			_runInitializers();
		}
	}

	public static List<ResultType> unmarshallResult(byte[] res) throws SyntaxException {

		runInitializers();
		
		InputStream inputStream = null;
		try{
			inputStream = new ByteArrayInputStream(res);
			ResponseType response = ResponseMarshaller.unmarshal(inputStream);
			return response.getResults();
		} finally {
			if(inputStream != null) {
				try {
					inputStream.close();
				} catch(Exception e) {}
			}
		}
	}

	public static Evaluatable unmarshallPolicy(byte[] res) throws SyntaxException {

		runInitializers();
		
		InputStream inputStream = null;
		try{
			inputStream = new ByteArrayInputStream(res);
			Evaluatable response = PolicyMarshaller.unmarshal(inputStream);
			return response;
		} finally {
			if(inputStream != null) {
				try {
					inputStream.close();
				} catch(Exception e) {}
			}
		}
	}

	
	public static byte[] marshallRequest(XacmlRequest request) throws SecurityException {
		return marshallRequest(request.getXacmlRequest());
	}
	public static byte[] marshallRequest(RequestType request) throws SecurityException {
		
		runInitializers();
		
		ByteArrayOutputStream baos = null;
		try{

			baos = new ByteArrayOutputStream();
			RequestMarshaller.marshal(request, baos);

			return baos.toByteArray();
		} catch (WritingException e) {
			throw new SecurityException(e);
		} finally {
			if(baos != null) {
				try{
					baos.flush();
					baos.close();
				} catch (Exception e) {}
			}
		}
	}


}
