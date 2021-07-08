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

package org.openspcoop2.pdd.services.connector;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AsyncThreadPool
 *
 * @author Poli Andrea (apoli@link.it)
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AsyncThreadPool {

	private static ExecutorService pool;
	
	public static void initialize(int size) {
		 pool = Executors.newFixedThreadPool(size);  
	}
	public static void execute(Runnable runnable) {
		pool.execute(runnable);
	}
	public static void shutdown() {
		if(pool!=null) {
			pool.shutdown();
		}
	}
	
}
