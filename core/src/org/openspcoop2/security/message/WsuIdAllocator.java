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


package org.openspcoop2.security.message;

import org.apache.xml.security.stax.impl.util.IDGenerator;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.RandomBasedGenerator;


/**
 * WsuIdAllocator 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WsuIdAllocator implements org.apache.wss4j.dom.WsuIdAllocator {
	
	private static String secureRandomAlgorithm = null;
	public static String getSecureRandomAlgorithm() {
		return secureRandomAlgorithm;
	}
	public static void setSecureRandomAlgorithm(String secureRandomAlgorithm) {
		WsuIdAllocator.secureRandomAlgorithm = secureRandomAlgorithm;
	}

    private static ThreadLocal<RandomBasedGenerator> randomBasedGeneratorThreadLocal = new ThreadLocal<RandomBasedGenerator>();
    public static RandomBasedGenerator getRandomBasedGenerator() {
    	if(secureRandomAlgorithm==null) {
    		return null;
    	}
    	RandomBasedGenerator randomGenerator = randomBasedGeneratorThreadLocal.get();
    	if(randomGenerator==null) {
    		try {
    			randomGenerator = Generators.randomBasedGenerator(java.security.SecureRandom.getInstance(secureRandomAlgorithm));
    			randomBasedGeneratorThreadLocal.set(randomGenerator);
    		}catch(Throwable t) {
    			System.out.println("Inizializzazione RandomBasedGenerator fallita: "+t.getMessage());
    			t.printStackTrace(System.out);
    		}
    	}
    	return randomGenerator;
    }
	
	
	
	private String prefixComponent;
	
	public WsuIdAllocator(String prefix){
		this.prefixComponent=prefix;
	}
	    
    @Override
	public String createId(String prefix, Object o) {
    	RandomBasedGenerator generator = null;
    	if(secureRandomAlgorithm!=null) {
    		generator = getRandomBasedGenerator();
    	}
    	if(generator!=null) {
    		return _generateID(generator, prefix);
    	}
    	else {
	        if (prefix == null) {
	            return IDGenerator.generateID(this.prefixComponent);
	        }
	
	        return IDGenerator.generateID(this.prefixComponent+prefix);
    	}
    }

    @Override
	public String createSecureId(String prefix, Object o) {
    	RandomBasedGenerator generator = null;
    	if(secureRandomAlgorithm!=null) {
    		generator = getRandomBasedGenerator();
    	}
    	if(generator!=null) {
    		return _generateID(generator, prefix);
    	}
    	else {
	    	if (prefix == null) {
	            return IDGenerator.generateID(this.prefixComponent);
	        }
	
	        return IDGenerator.generateID(this.prefixComponent+prefix);
    	}
    }

    private static String _generateID(RandomBasedGenerator generator, String prefix) {
    	// Implementazione uguale a quella presente in org.apache.xml.security.stax.impl.util.IDGenerator, con la differenza che viene usato un RandomBasedGenerator
        String id = generator.generate().toString();
        if (prefix != null) {
            return prefix + id;
        } else {
            //always prepend a constant character to get a schema-valid id!:
            return "G" + id;
        }
    }
}
