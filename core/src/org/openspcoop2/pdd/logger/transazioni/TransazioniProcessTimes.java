/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.logger.transazioni;

import java.util.List;

/**
 * TransazioniProcessTimes
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class TransazioniProcessTimes {

	String idTransazione;
	long fillTransaction = -1;
	List<String> fillTransactionDetails = null;
	long controlloTraffico = -1;
	long controlloTrafficoRemoveThread = -1;
	long controlloTrafficoPreparePolicy = -1;
	List<String> controlloTrafficoPolicyTimes = null;
	long fileTrace = -1;
	long processTransactionInfo = -1;
	long getConnection = -1;
	long insertTransaction = -1;
	long insertDiagnostics = -1;
	long insertTrace = -1;
	long insertContents = -1;
	long insertResources = -1;
	long commit = -1;
	
	public void setControlloTrafficoRemoveThread(long controlloTrafficoRemoveThread) {
		this.controlloTrafficoRemoveThread = controlloTrafficoRemoveThread;
	}
	public void setControlloTrafficoPreparePolicy(long controlloTrafficoPreparePolicy) {
		this.controlloTrafficoPreparePolicy = controlloTrafficoPreparePolicy;
	}
	public void setControlloTrafficoPolicyTimes(List<String> controlloTrafficoPolicyTimes) {
		this.controlloTrafficoPolicyTimes = controlloTrafficoPolicyTimes;
	}
	public List<String> getControlloTrafficoPolicyTimes() {
		return this.controlloTrafficoPolicyTimes;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		fillTransactionDetails(sb);
		
		fillControlloTraffico(sb);
		
		if(this.fileTrace>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("fileTrace:").append(this.fileTrace);
		}
		if(this.processTransactionInfo>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("processTransactionInfo:").append(this.processTransactionInfo);
		}
		fillDB(sb);
		return sb.toString();
	}
	private void fillTransactionDetails(StringBuilder sb) {
		if(this.fillTransaction>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("buildTransaction:").append(this.fillTransaction);
		}
		if(this.fillTransactionDetails!=null && !this.fillTransactionDetails.isEmpty()) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("buildTransactionDetails:{");
			boolean first = true;
			for (String det : this.fillTransactionDetails) {
				if(!first) {
					sb.append(" ");
				}
				sb.append(det);
				first=false;
			}
			sb.append("}");
		}
	}
	private void fillControlloTraffico(StringBuilder sb) {
		if(this.controlloTraffico>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("rateLimiting:").append(this.controlloTraffico);
		}
		if(this.controlloTrafficoRemoveThread>=0 || this.controlloTrafficoPreparePolicy>=0 || 
				(this.controlloTrafficoPolicyTimes!=null && !this.controlloTrafficoPolicyTimes.isEmpty())) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("rateLimitingDetails:{");
			sb.append("del:").append(this.controlloTrafficoRemoveThread);
			sb.append(" init:").append(this.controlloTrafficoPreparePolicy);
			if(this.controlloTrafficoPolicyTimes!=null && !this.controlloTrafficoPolicyTimes.isEmpty()) {
				sb.append(" policy:").append(this.controlloTrafficoPolicyTimes.toString());
			}
			else {
				sb.append(" policy:-");
			}
			sb.append("}");
		}
	}
	private void fillDB(StringBuilder sb) {
		if(this.getConnection>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("getConnection:").append(this.getConnection);
		}
		fillDBInsert(sb);
		if(this.commit>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("commit:").append(this.commit);
		}
	}
	private void fillDBInsert(StringBuilder sb) {
		if(this.insertTransaction>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("insertTransaction:").append(this.insertTransaction);
		}
		if(this.insertDiagnostics>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("insertDiagnostics:").append(this.insertDiagnostics);
		}
		if(this.insertTrace>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("insertTrace:").append(this.insertTrace);
		}
		if(this.insertContents>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("insertContents:").append(this.insertContents);
		}
		if(this.insertResources>=0) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			sb.append("insertResources:").append(this.insertResources);
		}
	}
	
}
