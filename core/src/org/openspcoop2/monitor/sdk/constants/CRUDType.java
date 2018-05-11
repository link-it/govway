package org.openspcoop2.monitor.sdk.constants;


/**
 * CRUDType
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum CRUDType {

	CREATE,UPDATE,DELETE,SEARCH;
	
	
	public static String[] toEnumNameArray(){
		String[] res = new String[CRUDType.values().length];
		int i=0;
		for (CRUDType tmp : CRUDType.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}



	
	@Override
	public String toString(){
		return this.name();
	}
	public boolean equals(CRUDType esito){
		return this.toString().equals(esito.toString());
	}
}

