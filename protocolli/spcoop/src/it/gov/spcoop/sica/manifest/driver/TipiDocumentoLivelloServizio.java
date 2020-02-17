/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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


package it.gov.spcoop.sica.manifest.driver;
/**
 *
 *
 * @author Stefano Corallo (corallo@link.it)
* @author $Author$
* @version $Rev$, $Date$
 */
public enum TipiDocumentoLivelloServizio {

	
	WSAGREEMENT ("WS-Agreement"),
	WSLA ("WSLA");
	
	
	private final String nome;

	TipiDocumentoLivelloServizio(String nome)
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
		String[] res = new String[TipiDocumentoLivelloServizio.values().length];
		int i=0;
		for (TipiDocumentoLivelloServizio tmp : TipiDocumentoLivelloServizio.values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	
	public static String[] toEnumNameArray(){
		String[] res = new String[TipiDocumentoLivelloServizio.values().length];
		int i=0;
		for (TipiDocumentoLivelloServizio tmp : TipiDocumentoLivelloServizio.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static TipiDocumentoLivelloServizio toEnumConstant(String val){
		
		TipiDocumentoLivelloServizio res = null;
		
		if(TipiDocumentoLivelloServizio.WSLA.toString().equals(val)){
			res = TipiDocumentoLivelloServizio.WSLA;
		}else if(TipiDocumentoLivelloServizio.WSAGREEMENT.toString().equals(val)){
			res = TipiDocumentoLivelloServizio.WSAGREEMENT;
		}
		return res;
	}
}


