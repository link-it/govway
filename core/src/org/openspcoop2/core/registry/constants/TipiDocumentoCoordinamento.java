/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
public enum TipiDocumentoCoordinamento {

	
	BPEL ("BPEL"),
	WSCDL ("WS-CDL");
	
	
	private final String nome;

	TipiDocumentoCoordinamento(String nome)
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
		String[] res = new String[TipiDocumentoCoordinamento.values().length];
		int i=0;
		for (TipiDocumentoCoordinamento tmp : TipiDocumentoCoordinamento.values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[TipiDocumentoCoordinamento.values().length];
		int i=0;
		for (TipiDocumentoCoordinamento tmp : TipiDocumentoCoordinamento.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static TipiDocumentoCoordinamento toEnumConstant(String val){
		
		TipiDocumentoCoordinamento res = null;
		
		if(TipiDocumentoCoordinamento.BPEL.toString().equals(val)){
			res = TipiDocumentoCoordinamento.BPEL;
		}else if(TipiDocumentoCoordinamento.WSCDL.toString().equals(val)){
			res = TipiDocumentoCoordinamento.WSCDL;
		} 
		return res;
	}
}


