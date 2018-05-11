package org.openspcoop2.monitor.sdk.constants;

/**
 * SearchType
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum StatisticType{

	ORARIA,
	GIORNALIERA,
	SETTIMANALE,
	MENSILE;

	public static String[] toEnumNameArray(){
		String[] res = new String[StatisticType.values().length];
		int i=0;
		for (StatisticType tmp : StatisticType.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}



	
	@Override
	public String toString(){
		return this.name();
	}
	public boolean equals(StatisticType esito){
		return this.toString().equals(esito.toString());
	}

}

