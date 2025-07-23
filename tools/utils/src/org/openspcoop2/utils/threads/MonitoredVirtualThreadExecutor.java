/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.threads;

import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * MonitoredVirtualThreadExecutor
 *  
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MonitoredVirtualThreadExecutor extends AbstractExecutorService {

    private final ExecutorService delegate;
	private final AtomicLong submitted = new AtomicLong();
    private final AtomicLong active = new AtomicLong();
    private final AtomicLong completed = new AtomicLong();

    public MonitoredVirtualThreadExecutor(String poolName) {
        this.delegate = Executors.newThreadPerTaskExecutor(
            Thread.ofVirtual().name(poolName + "-worker-", 0).factory()
        );
    }

    @Override
    public void execute(java.lang.Runnable command) {
        this.submitted.incrementAndGet();
        this.delegate.execute(() -> {
        	this.active.incrementAndGet();
            try {
                command.run();
            } finally {
            	this.active.decrementAndGet();
            	this.completed.incrementAndGet();
            }
        });
    }

    // --- Metriche ---
    public long getSubmittedCount()  { return this.submitted.get(); }
    public long getActiveCount()     { return this.active.get(); }
    public long getCompletedCount()  { return this.completed.get(); }

    // --- Stato delegato ---
    @Override public void shutdown()               { this.delegate.shutdown(); }
    @Override public java.util.List<java.lang.Runnable> shutdownNow() { return this.delegate.shutdownNow(); }
    @Override public boolean isShutdown()          { return this.delegate.isShutdown(); }
    @Override public boolean isTerminated()        { return this.delegate.isTerminated(); }
    @Override public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return this.delegate.awaitTermination(timeout, unit);
    }

    public String getStatus() {
        return String.format("(submitted: %d) Active: %d, Completed: %d, isShutdown: %s, isTerminated: %s",
                getSubmittedCount(),
                getActiveCount(),
                getCompletedCount(),
                isShutdown(),
                isTerminated());
    }
    
    // ---- Delegato ----
    public ExecutorService getDelegate() {
		return this.delegate;
	}
}

