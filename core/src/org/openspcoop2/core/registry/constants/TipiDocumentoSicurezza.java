/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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


package org.openspcoop2.core.registry.constants;
/**
 *
 *
 * @author Stefano Corallo <corallo@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TipiDocumentoSicurezza {

	
	WSPOLICY ("WS-Policy"),
	XACML_POLICY ("XACML-Policy"),
	LINGUAGGIO_NATURALE ("Linguaggio Naturale");
	
	
	private final String nome;

	TipiDocumentoSicurezza(String nome)
	{
		this.nome = nome;
	}

	public String getNome()
	{
		return this.nome;
	}
	
	@Override
	public String toString(){
		return this.nome;
	}
	
	public static String[] toStringArray(){
		String[] res = new String[TipiDocumentoSicurezza.values().length];
		int i=0;
		for (TipiDocumentoSicurezza tmp : TipiDocumentoSicurezza.values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	
	public static String[] toEnumNameArray(){
		String[] res = new String[TipiDocumentoSicurezza.values().length];
		int i=0;
		for (TipiDocumentoSicurezza tmp : TipiDocumentoSicurezza.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static TipiDocumentoSicurezza toEnumConstant(String val){
		
		if(TipiDocumentoSicurezza.LINGUAGGIO_NATURALE.toString().equals(val)){
			return TipiDocumentoSicurezza.LINGUAGGIO_NATURALE;
		}
		else if(TipiDocumentoSicurezza.WSPOLICY.toString().equals(val)){
			return TipiDocumentoSicurezza.WSPOLICY;
		}
		else{
			return TipiDocumentoSicurezza.XACML_POLICY;
		}
		
		
	}
}


