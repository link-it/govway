/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.xacml.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.herasaf.xacml.core.context.impl.DecisionType;
import org.herasaf.xacml.core.context.impl.RequestType;
import org.herasaf.xacml.core.context.impl.ResultType;
import org.openspcoop2.utils.xacml.PolicyDecisionPoint;
import org.openspcoop2.utils.xacml.ResultCombining;

/**
 * Test
 *
 * @author Bussu Giovanni (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Test {

	private static String policy = "policy1.xml";
	private static String okRequest = "okRequestCittadinoeCitta.xml";
	private static String koRequestCitta = "koRequestCitta.xml";

	public static void main(String[] args) {
		
		boolean pdpUnico = true;
		int nPolicy = 100000;
		int nThread = 100;
		int nRichiestePerThread = 10;
		try {
			execute(pdpUnico, nPolicy, nThread, nRichiestePerThread);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace(System.err);
		}
	}

	private static String getResourceString(String resource) throws IOException {
		InputStream is = null;
		try{
			is = Test.class.getResourceAsStream(resource);
			int b = is.read();
			StringBuilder sb = new StringBuilder();
			while(b != -1) {
				sb.append((char)b);
				b = is.read();
			}
			
			return sb.toString();
		} finally {
			if(is != null) {
				try {
					is.close();
				} catch (IOException e) {}
			}
		}
	}

	private static void execute(boolean singlePdp, int nPolicy, int nThread, int nRichiestePerThread) throws Exception {

		String policyString = Test.getResourceString(policy);
		String okRequestResourceString = Test.getResourceString(okRequest);
		String koRequestResourceString = Test.getResourceString(koRequestCitta);
		System.out.println("Esecuzione con singlePDP ["+singlePdp+"] nPolicy ["+nPolicy+"] nThread ["+nThread+"] nRichiestePerThread ["+nRichiestePerThread+"]...");

		long inizioCaricamentoPolicies = System.currentTimeMillis();
		PolicyDecisionPoint processor = new PolicyDecisionPoint(singlePdp);
		for(int i = 0; i < nPolicy; i++) {
			processor.addPolicy(policyString, "resource_"+i);
		}
		
		long fineCaricamentoPolicies = System.currentTimeMillis();
		
		System.out.println("Tempo caricamento policy: "+(fineCaricamentoPolicies-inizioCaricamentoPolicies)+" ms");

		List<RequestEvaluator> lst = new ArrayList<Test.RequestEvaluator>();
		for(int i = 0; i < nThread / 2; i++) {
			lst.add(new Test().new RequestEvaluator(processor, PolicyDecisionPoint._unmarshalRequest(okRequestResourceString), DecisionType.PERMIT, nRichiestePerThread));
			lst.add(new Test().new RequestEvaluator(processor, PolicyDecisionPoint._unmarshalRequest(koRequestResourceString), DecisionType.DENY, nRichiestePerThread));
		}

		
		long inizio = System.currentTimeMillis();
		for(RequestEvaluator item: lst) {
			item.start();
		}
		
		for(RequestEvaluator item: lst) {
			item.join();
		}
		long fine = System.currentTimeMillis();
		
		long complessivo = fine - inizio;
		System.out.println("Tempo complessivo: " + complessivo + " ms");
		System.out.println("Tempo medio thread: " + (complessivo / (lst.size() * 1.0)) + " ms");
		
		
		long maxTotale = Long.MIN_VALUE;
		for(RequestEvaluator item: lst) {
			long maxTimeElapsed = item.getMaxTimeElapsed();
			if(maxTotale < maxTimeElapsed) {
				maxTotale = maxTimeElapsed;
			}
			
		}
		System.out.println("Tempo massimo thread: " + maxTotale + " ms");
		
		
		System.out.println("\n\n");
		System.out.println("****************************************");
		System.out.println("Informazioni sui singoli thread");
		System.out.println("****************************************");
		
		for (int i = 0; i < lst.size(); i++) {
			RequestEvaluator item = lst.get(i);

			int countBadRequests = item.countBadRequests();
			if(countBadRequests > 0) {
				System.err.println("Richieste errate : " + countBadRequests);
			}
			
			long maxTimeElapsed = item.getMaxTimeElapsed();

			System.out.println("Thread["+i+"] Max time: " + maxTimeElapsed + " ms");
			System.out.println("Thread["+i+"] Min time: " + item.getMinTimeElapsed() + " ms");
			System.out.println("Thread["+i+"] Avg time: " + item.getAvgTimeElapsed() + " ms");
			
		}
		
		
		System.out.println("Esecuzione con singlePDP ["+singlePdp+"] nPolicy ["+nPolicy+"] nThread ["+nThread+"] nRichiestePerThread ["+nRichiestePerThread+"] completata");
	}
	
	
	/**
	 *  Classi di appoggio
	 */
	
	
	class RequestEvaluator extends Thread {
		
		private PolicyDecisionPoint processor;
		private RequestType request;
		private DecisionType decisionTypeExpected;
		private DecisionType[] decisionTypeActual;
		private long[] timeElapsed;
		public RequestEvaluator(PolicyDecisionPoint processor, RequestType request, DecisionType decisionTypeExpected, int nRequests) {
			this.processor = processor;
			this.request = request;
			this.decisionTypeExpected = decisionTypeExpected;
			this.timeElapsed = new long[nRequests];
			this.decisionTypeActual = new DecisionType[nRequests];
		}
		
		@Override
		public void run() {
			
				for(int i =0; i< this.timeElapsed.length; i++) {
					try {
						long inizio = System.currentTimeMillis();
						
						List<ResultType> results = this.processor.evaluate(this.request);
						this.decisionTypeActual[i] = ResultCombining.combineDenyOverrides(results);
		
						long fine = System.currentTimeMillis();
						
						this.timeElapsed[i] = fine - inizio;
					}catch(Exception e) {
						System.err.println(e.getMessage());
						e.printStackTrace(System.err);
						this.timeElapsed[i] = Long.MAX_VALUE;
						this.decisionTypeActual[i] = DecisionType.INDETERMINATE;
					}
					
				}
		}
		
		public int countBadRequests() {
			int bad = 0;
			for(int i =0; i< this.decisionTypeActual.length; i++) {
				if(!this.decisionTypeExpected.equals(this.decisionTypeActual[i])) {
					bad++; 
				}
			}
			return bad;
		}

		public long getMaxTimeElapsed() {
			long max = Long.MIN_VALUE;
			for(int i =0; i< this.timeElapsed.length; i++) {
				if(this.timeElapsed[i] > max) {
					max = this.timeElapsed[i];
				}
			}
			return max;
		}

		public long getMinTimeElapsed() {
			long min = Long.MAX_VALUE;
			for(int i =0; i< this.timeElapsed.length; i++) {
				if(this.timeElapsed[i] < min) {
					min = this.timeElapsed[i];
				}
			}
			return min;
		}

		public double getAvgTimeElapsed() {
			double avg = 0.0;
			for(int i =0; i< this.timeElapsed.length; i++) {
				avg += this.timeElapsed[i];
			}
			return avg / this.timeElapsed.length;
		}

	}
	

}
