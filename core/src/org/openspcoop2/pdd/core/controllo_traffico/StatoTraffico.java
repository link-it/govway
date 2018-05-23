package org.openspcoop2.pdd.core.controllo_traffico;

public class StatoTraffico {

	
	private Long activeThreads = 0l;
	private Boolean pddCongestionata = false;
	
	public Long getActiveThreads() {
		return this.activeThreads;
	}
	public void setActiveThreads(Long activeThreads) {
		this.activeThreads = activeThreads;
	}
	public Boolean getPddCongestionata() {
		return this.pddCongestionata;
	}
	public void setPddCongestionata(Boolean pddCongestionata) {
		this.pddCongestionata = pddCongestionata;
	}
	
}
