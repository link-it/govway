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

package org.openspcoop2.utils.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;

/**
 * LimitedHashMap
 *
 * @author Luigi Buoncristiani (Kali.Blu@gmail.com)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LimitedHashMap<K,V> extends ConcurrentHashMap<K, V> {
	private static final long serialVersionUID = 1L;

	private int maxSize = -1;
	private int maxLifeTime = -1;
	private ElementInfo<K> elementInfos[] = null;
	private int startIx = -1;
	private int insIx = -1;
	private Map<K,Integer> elementPosMap = null;
	private org.openspcoop2.utils.Semaphore semaphore = null;
	
	private static class ElementInfo<K> {
		private final long timeMillis;
		private final K key;
		ElementInfo( long timeMillis, K key ) {
			this.timeMillis = timeMillis;
			this.key = key;
		}
		public long getTimeMillis() {
			return this.timeMillis;
		}
		public K getKey() {
			return this.key;
		}
	}

	public LimitedHashMap( String cacheName, int maxSize, int maxLifeTime ) {
		super();
		initElementInfos( cacheName, maxSize, maxLifeTime );
	}
	public LimitedHashMap( String cacheName, int maxSize, int maxLifeTime, int initialCapacity ) {
		super( initialCapacity );
		initElementInfos( cacheName, maxSize, maxLifeTime );
	}
	public LimitedHashMap( String cacheName, int maxSize, int maxLifeTime, Map<? extends K, ? extends V> m ) {
		super( m );
		initElementInfos( cacheName, maxSize, maxLifeTime );
	}
	public LimitedHashMap( String cacheName, int maxSize, int maxLifeTime, int initialCapacity, float loadFactor ) {
		super( initialCapacity, loadFactor );
		initElementInfos( cacheName, maxSize, maxLifeTime );
	}
	public LimitedHashMap( String cacheName, int maxSize, int maxLifeTime, int initialCapacity, float loadFactor, int concurrencyLevel ) {
		super( initialCapacity, loadFactor, concurrencyLevel );
		initElementInfos( cacheName, maxSize, maxLifeTime );
	}

	public LimitedHashMap() {
		this( null, -1, -1 );
	}
	public LimitedHashMap( int initialCapacity ) {
		this( null, -1, -1, initialCapacity );
	}
	public LimitedHashMap( Map<? extends K, ? extends V> m ) {
		this( null, -1, -1, m );
	}
	public LimitedHashMap( int initialCapacity, float loadFactor ) {
		this( null, -1, -1, initialCapacity, loadFactor );
	}
	public LimitedHashMap( int initialCapacity, float loadFactor, int concurrencyLevel ) {
		this( null, -1, -1, initialCapacity, loadFactor, concurrencyLevel );
	}

	@SuppressWarnings("unchecked")
	private void initElementInfos( String cacheName, int maxSize, int maxLifeTime ) {
		if ( maxSize <= 0 && maxLifeTime > 0 )
			throw new IllegalArgumentException( "Cannot use maxLifeTime without maxSize" );
		this.maxSize = maxSize;
		this.maxLifeTime = maxLifeTime;
		if ( maxSize > 0 ) {
			this.elementInfos = new ElementInfo[ maxSize ];
			this.elementPosMap = new ConcurrentHashMap<K,Integer>( maxSize );
			this.startIx = 0;
			this.insIx = 0;
		}
		this.semaphore = new org.openspcoop2.utils.Semaphore(cacheName!=null ? "LimitedHashMap_"+cacheName : "LimitedHashMap");
	}

	private void decrementInsIx() {
		this.insIx--;
		if ( this.insIx < 0 )
			this.insIx = this.maxSize - 1;
	}

	private void incrementStartIx() {
		this.startIx = (this.startIx + 1) % this.maxSize;
	}

	private int posSize() {
		if ( this.startIx < 0 )
			return 0;
		if ( this.startIx <= this.insIx )
			return this.insIx - this.startIx;
		return ( (this.maxSize + this.insIx) - this.startIx );
	}

	private void cleanUpExtraTime() {
		if ( this.startIx < 0 )
			return;
		long timeMillis = DateManager.getTimeMillis() - (this.maxLifeTime * 1000);
		while ( posSize() > 0 ) {
			ElementInfo<K> oldestInfo = this.elementInfos[ this.startIx ];
			if ( oldestInfo.getTimeMillis() > timeMillis )
				break;
			K curKey = oldestInfo.getKey();
			super.remove( curKey );
			removeElementInfo( curKey );
		}
	}
	private void syncCleanUpExtraTime() {
		if ( this.startIx < 0 )
			return;
		if(posSize()<=0) {
			return;
		}

		this.semaphore.acquireThrowRuntime("cleanUpExtraTime");
		try {
			cleanUpExtraTime();
		}finally {
			this.semaphore.release("cleanUpExtraTime");
		}
	}

	private void addElementInfo( K key ) {
		ElementInfo<K> elInfo = new ElementInfo<K>( DateManager.getTimeMillis(), key );
		if ( this.startIx >= 0 ) {
			int posIx = this.insIx;
			this.insIx = (this.insIx + 1) % this.maxSize;
			if ( this.insIx == this.startIx ) {
				ElementInfo<K> toBeRemove = this.elementInfos[this.startIx];
				super.remove( toBeRemove.getKey() );
				incrementStartIx();
			}
			this.elementInfos[ posIx ] = elInfo;
			this.elementPosMap.put( key, posIx );
		} else
			throw new RuntimeException( "Invalid startIx: not initialized" );
	}

	private void removeElementInfo( K key ) {
		Integer posObj = this.elementPosMap.remove( key );
		if ( posObj == null )
			return;

		int posIx = posObj.intValue();
		if ( posIx == (this.insIx - 1) ) {
			decrementInsIx();
		} else
		if ( posIx == this.startIx ) {
			incrementStartIx();
		} else
		if ( posIx < this.startIx ) {
			if ( posIx < (this.insIx - 1) ) {
				System.arraycopy( this.elementInfos, posIx + 1, this.elementInfos, posIx, this.insIx - 1 - posIx );
				decrementInsIx();
			} else
				throw new RuntimeException( "Invalid index position: " + posIx +
											" (startIx: " + this.startIx + ") (insIx: " + this.insIx );
		} else {
			if ( posIx < this.insIx ) {
				if ( this.startIx < this.insIx ) {
					System.arraycopy( this.elementInfos, posIx + 1, this.elementInfos, posIx, this.insIx - 1 - posIx );
					decrementInsIx();
				} else
					throw new RuntimeException( "Invalid index position: " + posIx +
												" (startIx: " + this.startIx + ") (insIx: " + this.insIx );
			} else {
				System.arraycopy( this.elementInfos, posIx + 1, this.elementInfos, posIx, this.maxSize - 1 - posIx );
				if ( this.insIx > 0 ) {
					this.elementInfos[ this.maxSize - 1 ] = this.elementInfos[0];
					System.arraycopy( this.elementInfos, 1, this.elementInfos, 0, this.insIx - 1 );
				}
				decrementInsIx();
			}
		}
	}

	private void updateElementInfo( K key ) {
		removeElementInfo( key );
		addElementInfo( key );
	}

	@Override
	public V get( Object key ) {
		if ( this.maxLifeTime > 0 )
			syncCleanUpExtraTime();
		return super.get( key );
	}

	@Override
	public V put( K key, V value ) {
		this.semaphore.acquireThrowRuntime("put");
		try {
			if ( this.maxLifeTime > 0 )
				cleanUpExtraTime();
			
			var res = super.put( key, value );
			if ( this.maxSize > 0 ) {
				if ( res == null )
					addElementInfo( key );
				else
					updateElementInfo( key );
			}
			return res;
		}finally {
			this.semaphore.release("put");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public V remove( Object key ) {
		this.semaphore.acquireThrowRuntime("remove");
		try {
			if ( this.maxLifeTime > 0 )
				cleanUpExtraTime();
			var res = super.remove( key );
			if ( res != null )
				removeElementInfo( (K)key );
			return res;
		}finally {
			this.semaphore.release("remove");
		}
	}

	public static void main( String[] args ) throws Exception {
		int maxSize = 50;
		int maxLifeTime = 100000;
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
		
		
		for ( int ix = 0; ix < 100; ix++ ) {
			String key = Integer.toString(ix);
			System.out.println( key + " : " +  map.get( key ) );
		}
		System.out.println("Attuale size: "+map.size());

	}
}
