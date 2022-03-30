/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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


package org.openspcoop2.utils;


/**
 * Contiene i tipi di Database supportati
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public enum TipiDatabase {

	POSTGRESQL ("postgresql"),
	MYSQL ("mysql"),
	ORACLE ("oracle"),
	HSQL ("hsql"),
	DERBY ("derby"),
	SQLSERVER ("sqlserver"),
	DB2 ("db2"),
	DEFAULT ("default");
	
	
	private final String nome;

	TipiDatabase(String nome)
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
		String[] res = new String[TipiDatabase.values().length];
		int i=0;
		for (TipiDatabase tmp : TipiDatabase.values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	
	public static String[] toEnumNameArray(){
		String[] res = new String[TipiDatabase.values().length];
		int i=0;
		for (TipiDatabase tmp : TipiDatabase.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static TipiDatabase toEnumConstant(String val){
		
		if(TipiDatabase.POSTGRESQL.toString().equals(val)){
			return TipiDatabase.POSTGRESQL;
		}else if(TipiDatabase.MYSQL.toString().equals(val)){
			return TipiDatabase.MYSQL;
		}else if(TipiDatabase.ORACLE.toString().equals(val)){
			return TipiDatabase.ORACLE;
		}else if(TipiDatabase.HSQL.toString().equals(val)){
			return TipiDatabase.HSQL;
		}else if(TipiDatabase.DERBY.toString().equals(val)){
			return TipiDatabase.DERBY;
		}else if(TipiDatabase.SQLSERVER.toString().equals(val)){
			return TipiDatabase.SQLSERVER;
		}else if(TipiDatabase.DB2.toString().equals(val)){
			return TipiDatabase.DB2;
		}else{
			return TipiDatabase.DEFAULT;
		}
		
	}
	
	
	public static boolean isAMember(String tipoDatabase){		
		for (TipiDatabase val : TipiDatabase.values()){			
			if (val.equals(tipoDatabase))
				return true;
		}
		return false;
	}
	
	public boolean equals(TipiDatabase tipoDatabase){

		return this.toString().equalsIgnoreCase(tipoDatabase.toString());
	}
	public boolean equals(String tipoDatabase){	
		return this.toString().equalsIgnoreCase(tipoDatabase);
	}
}
