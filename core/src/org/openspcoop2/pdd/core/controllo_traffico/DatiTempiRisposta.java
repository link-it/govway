package org.openspcoop2.pdd.core.controllo_traffico;

public class DatiTempiRisposta {

	private Integer connectionTimeout;
	private Integer readConnectionTimeout;
	private Integer avgResponseTime;


	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		bf.append("connectionTimeout[").append(this.connectionTimeout).append("]");
		bf.append(" readConnectionTimeout[").append(this.readConnectionTimeout).append("]");
		bf.append(" avgResponseTime[").append(this.avgResponseTime).append("]");
		return bf.toString();
	}
	
	public Integer getConnectionTimeout() {
		return this.connectionTimeout;
	}
	public void setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	public Integer getReadConnectionTimeout() {
		return this.readConnectionTimeout;
	}
	public void setReadConnectionTimeout(Integer readConnectionTimeout) {
		this.readConnectionTimeout = readConnectionTimeout;
	}
	public Integer getAvgResponseTime() {
		return this.avgResponseTime;
	}
	public void setAvgResponseTime(Integer avgResponseTime) {
		this.avgResponseTime = avgResponseTime;
	}
}
