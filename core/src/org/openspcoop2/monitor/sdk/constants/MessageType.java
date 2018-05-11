package org.openspcoop2.monitor.sdk.constants;

/**
 * MessageType
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum MessageType{

	Richiesta,
	Risposta;

	public static String[] toEnumNameArray(){
		String[] res = new String[MessageType.values().length];
		int i=0;
		for (MessageType tmp : MessageType.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}



	
	@Override
	public String toString(){
		return this.name();
	}
	public boolean equals(MessageType esito){
		return this.toString().equals(esito.toString());
	}

}

