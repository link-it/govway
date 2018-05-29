package org.openspcoop2.pdd.core.credenziali.engine;

import java.io.Serializable;


public enum TipoAutenticazioneGestoreCredenziali implements Serializable {

	NONE ("none"),
	SSL ("ssl"),
	BASIC ("basic"),
	PRINCIPAL ("principal");

	private final String valore;

	TipoAutenticazioneGestoreCredenziali(String valore)
	{
		this.valore = valore;
	}

	public String getValore()
	{
		return this.valore;
	}

	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoAutenticazioneGestoreCredenziali tmp : values()) {
			res[i]=tmp.getValore();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (TipoAutenticazioneGestoreCredenziali tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}

	public static TipoAutenticazioneGestoreCredenziali toEnumConstant(String val){
		TipoAutenticazioneGestoreCredenziali res = null;
		for (TipoAutenticazioneGestoreCredenziali tmp : values()) {
			if(tmp.getValore().equals(val)){
				res = tmp;
				break;
			}
		}
		return res;
	}

	
	@Override
	public String toString(){
		return this.valore;
	}
	public boolean equals(TipoAutenticazioneGestoreCredenziali esito){
		return this.toString().equals(esito.toString());
	}

}

