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
	private LimitedHashMap.ElementDateInfo<K> elementInfos[] = null;
	private int startIx = -1;
	private int insIx = -1;
	private org.openspcoop2.utils.Semaphore semaphore = null;
	
	private static class ElementDateInfo<K> {
		private final long timeMillis;
		private final K key;
		ElementDateInfo( long timeMillis, K key ) {
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

	/*
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
	*/

	@SuppressWarnings("unchecked")
	private void initElementInfos( String cacheName, int maxSize, int maxLifeTime ) {
		if ( maxSize <= 0 && maxLifeTime > 0 )
			throw new IllegalArgumentException( "Cannot use maxLifeTime without maxSize" );
		this.maxSize = maxSize;
		this.maxLifeTime = maxLifeTime;
		if ( maxSize > 0 ) {
			this.elementInfos = new ElementDateInfo[ maxSize ];
			this.startIx = 0;
			this.insIx = 0;
		}
		this.semaphore = new org.openspcoop2.utils.Semaphore(cacheName!=null ? "LimitedHashMap_"+cacheName : "LimitedHashMap");
	}

	private int previousCircular( int ix ) {
		if ( ix == 0 )
			return (this.maxSize - 1);
		return ix - 1;
	}

	private int nextCircular( int ix ) {
		if ( ix == (this.maxSize - 1) )
			return 0;
		return ix + 1;
	}

	private void decrementInsIx() {
		this.insIx = previousCircular( this.insIx );
	}

	private void incrementStartIx() {
		this.startIx = nextCircular( this.startIx );
	}

	private int posSize() {
		if ( this.insIx < 0 )
			return 0;
		if ( this.startIx == this.insIx )
			return ( this.elementInfos[this.startIx] != null ? this.maxSize : 0 );
		if ( this.startIx < this.insIx )
			return this.insIx - this.startIx;
		return ( (this.maxSize + this.insIx) - this.startIx );
	}

	private void cleanUpExtraTime() {
		if ( this.insIx < 0 || this.startIx < 0 )
			return;
		long timeMillis = DateManager.getTimeMillis() - (this.maxLifeTime * 1000);
		while ( posSize() > 0 ) {
			ElementDateInfo<K> oldestInfo = this.elementInfos[ this.startIx ];
			if ( oldestInfo.getTimeMillis() > timeMillis )
				break;
			K curKey = oldestInfo.getKey();
			var removedValue = super.remove( curKey );
			if ( removedValue == null )
				throw new RuntimeException( "fail to remove by cleanUp: " + curKey );
			removeElementInfo( curKey );
		}
	}
	private void syncCleanUpExtraTime() {
		if ( this.insIx < 0 )
			return;
		if(posSize()<=0) {
			return;
		}
		long timeMillis = DateManager.getTimeMillis() - (this.maxLifeTime * 1000);
		ElementDateInfo<K> oldestInfo = this.elementInfos[ this.startIx ];
		if ( oldestInfo!=null && oldestInfo.getTimeMillis() <= timeMillis ) {

			this.semaphore.acquireThrowRuntime("cleanUpExtraTime");
			try {
				cleanUpExtraTime();
			}finally {
				this.semaphore.release("cleanUpExtraTime");
			}
			
		}
	}

	private void addElementInfo( K key ) {
		ElementDateInfo<K> elInfo = new ElementDateInfo<K>( DateManager.getTimeMillis(), key );
		if ( this.insIx >= 0 ) {
			int posIx = this.insIx;
			this.insIx = (this.insIx + 1) % this.maxSize;
			ElementDateInfo<K> startElInfo = this.elementInfos[this.startIx];
			if ( posIx == this.startIx && startElInfo != null ) {
				var removedValue = super.remove( startElInfo.getKey() );
				if ( removedValue == null )
					throw new RuntimeException( "fail to remove by add: " + startElInfo.getKey() + " adding " + key );
				incrementStartIx();
			}
			this.elementInfos[ posIx ] = elInfo;
		} else
			throw new RuntimeException( "Invalid insIx: not initialized" );
	}

	private void removeElementInfo( K key ) {
		int posIx = -1;
		int size = posSize();
		for ( int ix = this.startIx, jx = 0; posIx < 0 && jx < size; jx++ ) {
			if ( this.elementInfos[ix] != null && this.elementInfos[ix].getKey().equals( key ) )
				posIx = ix;
			ix = nextCircular(ix);
		}
		if ( posIx < 0 )
			return;

		int prevInsIx = previousCircular( this.insIx );
		if ( posIx == prevInsIx ) {
			this.elementInfos[ prevInsIx ] = null;
			decrementInsIx();
		} else
		if ( posIx == this.startIx ) {
			this.elementInfos[ posIx ] = null;
			incrementStartIx();
		} else
		if ( posIx < this.startIx ) {
			if ( posIx < prevInsIx ) {
				System.arraycopy( this.elementInfos, posIx + 1, this.elementInfos, posIx, prevInsIx - posIx );
				this.elementInfos[ prevInsIx ] = null;
				decrementInsIx();
			} else
				throw new RuntimeException( "Invalid index position: " + posIx +
											" (startIx: " + this.startIx + ") (insIx: " + this.insIx );
		} else {
			if ( posIx < this.insIx ) {
				if ( this.startIx < this.insIx ) {
					System.arraycopy( this.elementInfos, posIx + 1, this.elementInfos, posIx, prevInsIx - posIx );
					this.elementInfos[ prevInsIx ] = null;
					decrementInsIx();
				} else
					throw new RuntimeException( "Invalid index position: " + posIx +
												" (startIx: " + this.startIx + ") (insIx: " + this.insIx );
			} else {
				System.arraycopy( this.elementInfos, posIx + 1, this.elementInfos, posIx, this.maxSize - 1 - posIx );
				if ( this.insIx > 0 ) {
					this.elementInfos[ this.maxSize - 1 ] = this.elementInfos[0];
					// TBK
					if ( prevInsIx > 0 )
						System.arraycopy( this.elementInfos, 1, this.elementInfos, 0, prevInsIx );
				}
				this.elementInfos[ prevInsIx ] = null;
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
	}
}
