/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.cache.test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.cache.LimitedHashMap;

/**
 * LimitedHashMap
 *
 * @author Luigi Buoncristiani (Kali.Blu@gmail.com)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LimitedHashMapTest {
	
	public static void main( String[] args ) throws Exception {
		test();
	}
	public static void test() throws Exception {
		int maxSize = 50;
		int maxLifeTime = 10000;
		int threads = 10;
		ExecutorService threadsPool = Executors.newFixedThreadPool(threads);
		Random random = new Random();
		
		LimitedHashMap<Object,Serializable> map = new LimitedHashMap<>( "TEST", maxSize, maxLifeTime );

		for ( int ix = 0; ix < 100; ix++ ) {
			String key = Integer.toString(ix);
			map.put( key, "test-" + key );
		}
		for ( int ix = 0; ix < 100; ix++ ) {
			String key = Integer.toString(ix);
			System.out.println( key + " : " +  map.get( key ) );
		}
		System.out.println("Attuale size: "+map.size());
		
		// Registrazione con threads
		System.out.println("\n\nTest con "+threads+" threads");
		
		Map<String, Runnable> threadsMap = new HashMap<String, Runnable>();
		CountDownLatch latch = new CountDownLatch(threads);
		for (int i = 0; i < threads; i++) {
			String id = "thread-"+i;
			threadsMap.put(id, new Runnable() {
				@Override
				public void run() {
					for ( int ix = 0; ix < 100; ix++ ) {
						Utilities.sleep(random.nextInt(50));
						String key = Integer.toString(ix);
						map.put( key, id+"_test-" + key );
					}
					latch.countDown();
				}
			});
			threadsPool.execute(threadsMap.get(id));
		}
		latch.await();
		threadsPool.shutdown();
		
		for ( int ix = 0; ix < 100; ix++ ) {
			String key = Integer.toString(ix);
			//Utilities.sleep(random.nextInt(50));
			System.out.println( key + " : " +  map.get( key ) );
		}
		System.out.println("Attuale size: "+map.size());
		
		System.out.println("\n\nTestsuite terminata");
	}
}
