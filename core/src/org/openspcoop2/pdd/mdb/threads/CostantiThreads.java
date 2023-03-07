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


package org.openspcoop2.pdd.mdb.threads;

/**
 * Costanti utilizzate in caso di mancanza del file it.link.threads.properties
 * 
 * @author Fabio Tronci (tronci@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CostantiThreads {

    /** Gestione dei messaggi da ritrasmettere */
    public static final boolean REDELIVERY_STATUS = false;
	public static final long 	REDELIVERY_DELAY  = 60l * 1000l;
	public static final int  	REDELIVERY_COUNT  = 25;

	public static final String POOL_TYPE = "fixed";
	public static final int POOL_DEPTH = 25;
	
	public static final String CODA_TYPE = "array";
	public static final int CODA_DEPTH = 25;
	public static final Long ATTESA_PRODUCER = 200l;
	
}
