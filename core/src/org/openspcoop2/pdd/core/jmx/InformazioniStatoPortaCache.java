package org.openspcoop2.pdd.core.jmx;

public class InformazioniStatoPortaCache {

	private String nomeCache = null;
	private boolean enabled = false;
	private String statoCache = null;
	
	public InformazioniStatoPortaCache(String nomeCache,boolean enabled){
		this.nomeCache = nomeCache;
		this.enabled = enabled;
	}
	
	public String getNomeCache() {
		return this.nomeCache;
	}

	public void setNomeCache(String nomeCache) {
		this.nomeCache = nomeCache;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getStatoCache() {
		return this.statoCache;
	}

	public void setStatoCache(String statoCache) {
		this.statoCache = statoCache;
	}
}
