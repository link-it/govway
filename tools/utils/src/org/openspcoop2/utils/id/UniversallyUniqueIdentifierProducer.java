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
package org.openspcoop2.utils.id;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.BaseThread;
import org.slf4j.Logger;

/**
 * UniversallyUniqueIdentifierProducer
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UniversallyUniqueIdentifierProducer extends BaseThread {

	private static UniversallyUniqueIdentifierProducer staticInstance;
	public static synchronized void initialize(int buffer, Logger log) {
		if(staticInstance==null) {
			staticInstance = new UniversallyUniqueIdentifierProducer(buffer, log);
		}
	}
	public static UniversallyUniqueIdentifierProducer getInstance() {
		// volutamente non inizializzo se null, viene fatto allo startup
		return staticInstance;
	}
	
	
	private ArrayBlockingQueue<IUniqueIdentifier> idsQueue;
	private Logger log;
	
	public UniversallyUniqueIdentifierProducer(int buffer, Logger log) {
		this.setTimeout(-1); // equivale ad un while true
		this.idsQueue = new ArrayBlockingQueue<IUniqueIdentifier>(buffer);
		this.log = log;
		this.log.info( "Started UUID producer with buffer size: " + buffer );
	}
	
	public IUniqueIdentifier newUniqueIdentifier() throws UniqueIdentifierException {
		try {
			return this.idsQueue.take();
		} catch ( Throwable e ) {
			throw new UniqueIdentifierException( e );
		} /*finally {
			System.out.println("PRESO DALLA CODA");
		}*/
	}
	
	@Override
	protected void process() {
		try {
			//System.out.println("RUN");
			IUniqueIdentifier newUUID = UniqueIdentifierManager.newUniqueIdentifier(false);
			//this.idsQueue.put( newUUID ); causa un blocco nello shutdown
			while(this.isStop() == false){
				try {
					boolean insert = this.idsQueue.offer(newUUID, 1000, TimeUnit.MILLISECONDS);
					if(insert) {
						break;
					}
					//else {
					//	System.out.println("ATTENDO");
					//}
				}catch(InterruptedException ie) {
					//System.out.println("IE!");
				}
			}
			//System.out.println("AGGIUNTO IN CODA");
		} catch( Throwable t ) {
			this.log.error("UniversallyUniqueIdentifierProducer - generation failed: "+t.getMessage(),t);
			Utilities.sleep(5000); // per non continuare nel loop di errori (non dovrebbe comunque mai entrare in questo catch)
		}
	}

}
