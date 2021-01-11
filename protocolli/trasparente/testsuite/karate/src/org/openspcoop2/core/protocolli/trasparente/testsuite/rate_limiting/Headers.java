/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting;

/**
* ConfigLoader
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/

public class Headers {
	
	public static final String EchoInvoke = "GovWay-TestSuite-Echo-Invoke";
	public static final String TransactionId = "GovWay-Transaction-ID";
	
	public static final String RateLimitLimit = "X-RateLimit-Limit";
	public static final String RateLimitReset = "X-RateLimit-Reset";
	public static final String RateLimitRemaining = "X-RateLimit-Remaining";
	public static final String ReturnCode = "ReturnCode";
	public static final String RetryAfter = "Retry-After";
	public static final String ConcurrentRequestsLimit = "GovWay-RateLimit-ConcurrentRequest-Limit";
	public static final String ConcurrentRequestsRemaining = "GovWay-RateLimit-ConcurrentRequest-Remaining";
	public static final String GovWayTransactionErrorType = "GovWay-Transaction-ErrorType";
	
	public static final String RateLimitTimeResponseQuotaReset = "GovWay-RateLimit-TimeResponseQuota-Reset";
	public static final String RateLimitTimeResponseQuotaLimit = "GovWay-RateLimit-TimeResponseQuota-Limit";
	public static final String RateLimitTimeResponseQuotaRemaining =  "GovWay-RateLimit-TimeResponseQuota-Remaining";
		
	public static final String BandWidthQuotaReset = "GovWay-RateLimit-BandwithQuota-Reset";
	public static final String BandWidthQuotaLimit = "GovWay-RateLimit-BandwithQuota-Limit";
	public static final String BandWidthQuotaRemaining = "GovWay-RateLimit-BandwithQuota-Remaining";
	
	public static final String RequestSuccesfulReset = "GovWay-RateLimit-RequestSuccessful-Reset";
	public static final String RequestSuccesfulLimit = "GovWay-RateLimit-RequestSuccessful-Limit";
	public static final String RequestSuccesfulRemaining = "GovWay-RateLimit-RequestSuccessful-Remaining";
	
	public static final String FaultReset = "GovWay-RateLimit-Fault-Reset";
	public static final String FaultLimit = "GovWay-RateLimit-Fault-Limit";
	public static final String FaultRemaining = "GovWay-RateLimit-Fault-Remaining";	
	
	public static final String FailedReset = "GovWay-RateLimit-RequestFailed-Reset";
	public static final String FailedLimit = "GovWay-RateLimit-RequestFailed-Limit";
	public static final String FailedRemaining = "GovWay-RateLimit-RequestFailed-Remaining";
	
	public static final String FailedOrFaultReset = "GovWay-RateLimit-RequestFailedOrFault-Reset";
	public static final String FailedOrFaultLimit = "GovWay-RateLimit-RequestFailedOrFault-Limit";
	public static final String FailedOrFaultRemaining = "GovWay-RateLimit-RequestFailedOrFault-Remaining";
	
	public static final String AvgTimeResponseReset = "GovWay-RateLimit-AvgTimeResponse-Reset";
	public static final String AvgTimeResponseLimit = "GovWay-RateLimit-AvgTimeResponse-Limit";
	

}