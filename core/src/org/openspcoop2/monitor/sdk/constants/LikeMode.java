package org.openspcoop2.monitor.sdk.constants;


/**
 * LikeMode
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum LikeMode {

	ANYWHERE(org.openspcoop2.generic_project.expression.LikeMode.ANYWHERE),
	EXACT(org.openspcoop2.generic_project.expression.LikeMode.EXACT),
	END(org.openspcoop2.generic_project.expression.LikeMode.END),
	START(org.openspcoop2.generic_project.expression.LikeMode.START);

	private org.openspcoop2.generic_project.expression.LikeMode likeGenericProjectValue;
	
	public org.openspcoop2.generic_project.expression.LikeMode getLikeGenericProjectValue() {
		return this.likeGenericProjectValue;
	}

	LikeMode(org.openspcoop2.generic_project.expression.LikeMode likeGenericProjectValue){
		this.likeGenericProjectValue = likeGenericProjectValue;
	}
	
	public static String[] toEnumNameArray(){
		String[] res = new String[LikeMode.values().length];
		int i=0;
		for (LikeMode tmp : LikeMode.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}



	
	@Override
	public String toString(){
		return this.name();
	}
	public boolean equals(LikeMode esito){
		return this.toString().equals(esito.toString());
	}

}

