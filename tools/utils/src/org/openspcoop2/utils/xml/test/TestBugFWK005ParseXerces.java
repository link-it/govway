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
package org.openspcoop2.utils.xml.test;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XMLUtils;
import org.openspcoop2.utils.xml.XPathExpressionEngine;
import org.w3c.dom.Element;

/**
 * TestBugFWK005ParseXerces
 * Il test verifica che non si ripresenti il problema descritto in https://bugs.openjdk.java.net/browse/JDK-8047329
 * 
 * "If multiple threads call evaluate(InputSource) concurrently on different XPathExpression objects, they fail with the following exception:
 * org.xml.sax.SAXException: FWK005 parse may not be called while parsing."
 * 
 * L'unico fix attuale funzionante è quello di aggiungere un synchronized nella classe 'AbstractXPathExpressionEngine' (vedi synchronizedObjectForBugFWK005ParseXerces)
 * Il problema NON si manifesta SE l'oggetto da valutare è già un org.w3c.Element
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestBugFWK005ParseXerces {

	private static boolean errorOccurs = false;
	private static Exception e = null;
	private static int finished = 0;
	private static synchronized void add() {
		finished = finished + 1;
	}
	
    public static void main(String[] args) throws Exception {
    
    	test(true);
    
    }
    
    public static void test(boolean element) throws Exception {
    	String message = "<doc>PROVA</doc>";
    	String pattern = "/doc/text()";
    	
    	Runnable r = () -> {
            try {
            	final Element el = XMLUtils.getInstance().newElement(message.getBytes());
            	XPathExpressionEngine engine = new XPathExpressionEngine();
                for (int i = 0; i < 100; i++) {
                	String s = null;
                	if(element) {
                		s = AbstractXPathExpressionEngine.extractAndConvertResultAsString(el, engine, pattern, LoggerWrapperFactory.getLogger(TestBugFWK005ParseXerces.class));
                	}
                	else {
                		s = AbstractXPathExpressionEngine.extractAndConvertResultAsString(message, new DynamicNamespaceContext(), engine, pattern, LoggerWrapperFactory.getLogger(TestBugFWK005ParseXerces.class));
                	}
                	if(!"PROVA".equals(s)) {
                		throw new Exception("Ritornata una stringa non attesa '"+s+"'");
                	}
                }
                System.out.println("FINE");
            } catch (Exception e) {
                e.printStackTrace();
                TestBugFWK005ParseXerces.e = e;
                errorOccurs=true;
            } finally {
            	add();
            }
        };

        new Thread(r).start();
        new Thread(r).start();
        new Thread(r).start();
        new Thread(r).start();
        new Thread(r).start();
        new Thread(r).start();
        new Thread(r).start();
        new Thread(r).start();
        new Thread(r).start();
        new Thread(r).start();
        
        while(finished!=10) {
        	Utilities.sleep(200);
        }
        
        if(errorOccurs) {
        	System.out.println("Test terminato con errori");
        	throw e;
        }
        else {
        	System.out.println("Test completato con successo");
        }
    }
}
