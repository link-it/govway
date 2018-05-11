package org.openspcoop2.monitor.sdk.constants;


/**
 * SearchType
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum SearchType {

	EROGAZIONE,FRUIZIONE,ALL;
	
	
	public static String[] toEnumNameArray(){
		String[] res = new String[SearchType.values().length];
		int i=0;
		for (SearchType tmp : SearchType.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}



	
	@Override
	public String toString(){
		return this.name();
	}
	public boolean equals(SearchType esito){
		return this.toString().equals(esito.toString());
	}
}

