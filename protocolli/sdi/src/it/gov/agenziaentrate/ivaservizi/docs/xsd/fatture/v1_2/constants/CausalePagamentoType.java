/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it).
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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.constants;

import java.io.Serializable;
import java.util.List;

import org.openspcoop2.generic_project.beans.IEnumeration;

/**     
 * Enumeration dell'elemento CausalePagamentoType xsd (tipo:string) 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@javax.xml.bind.annotation.XmlType(name = "CausalePagamentoType")
@javax.xml.bind.annotation.XmlEnum(String.class)
public enum CausalePagamentoType implements IEnumeration , Serializable , Cloneable {

	@javax.xml.bind.annotation.XmlEnumValue("A")
	A ("A"),
	@javax.xml.bind.annotation.XmlEnumValue("B")
	B ("B"),
	@javax.xml.bind.annotation.XmlEnumValue("C")
	C ("C"),
	@javax.xml.bind.annotation.XmlEnumValue("D")
	D ("D"),
	@javax.xml.bind.annotation.XmlEnumValue("E")
	E ("E"),
	@javax.xml.bind.annotation.XmlEnumValue("G")
	G ("G"),
	@javax.xml.bind.annotation.XmlEnumValue("H")
	H ("H"),
	@javax.xml.bind.annotation.XmlEnumValue("I")
	I ("I"),
	@javax.xml.bind.annotation.XmlEnumValue("L")
	L ("L"),
	@javax.xml.bind.annotation.XmlEnumValue("M")
	M ("M"),
	@javax.xml.bind.annotation.XmlEnumValue("N")
	N ("N"),
	@javax.xml.bind.annotation.XmlEnumValue("O")
	O ("O"),
	@javax.xml.bind.annotation.XmlEnumValue("P")
	P ("P"),
	@javax.xml.bind.annotation.XmlEnumValue("Q")
	Q ("Q"),
	@javax.xml.bind.annotation.XmlEnumValue("R")
	R ("R"),
	@javax.xml.bind.annotation.XmlEnumValue("S")
	S ("S"),
	@javax.xml.bind.annotation.XmlEnumValue("T")
	T ("T"),
	@javax.xml.bind.annotation.XmlEnumValue("U")
	U ("U"),
	@javax.xml.bind.annotation.XmlEnumValue("V")
	V ("V"),
	@javax.xml.bind.annotation.XmlEnumValue("W")
	W ("W"),
	@javax.xml.bind.annotation.XmlEnumValue("X")
	X ("X"),
	@javax.xml.bind.annotation.XmlEnumValue("Y")
	Y ("Y"),
	@javax.xml.bind.annotation.XmlEnumValue("Z")
	Z ("Z"),
	@javax.xml.bind.annotation.XmlEnumValue("L1")
	L1 ("L1"),
	@javax.xml.bind.annotation.XmlEnumValue("M1")
	M1 ("M1"),
	@javax.xml.bind.annotation.XmlEnumValue("O1")
	O1 ("O1"),
	@javax.xml.bind.annotation.XmlEnumValue("V1")
	V1 ("V1");
	
	
	/** Value */
	private String value;
	@Override
	public String getValue()
	{
		return this.value;
	}


	/** Official Constructor */
	CausalePagamentoType(String value)
	{
		this.value = value;
	}


	
	@Override
	public String toString(){
		return this.value;
	}
	public boolean equals(CausalePagamentoType object){
		if(object==null)
			return false;
		if(object.getValue()==null)
			return false;
		return object.getValue().equals(this.getValue());	
	}
	public boolean equals(String object){
		if(object==null)
			return false;
		return object.equals(this.getValue());	
	}
	
		
	
	/** compatibility with the generated bean (reflection) */
	public boolean equals(Object object,List<String> fieldsNotCheck){
		if( !(object instanceof CausalePagamentoType) ){
			throw new RuntimeException("Wrong type: "+object.getClass().getName());
		}
		return this.equals(((CausalePagamentoType)object));
	}
	public String toString(boolean reportHTML){
		return toString();
	}
  	public String toString(boolean reportHTML,List<String> fieldsNotIncluded){
  		return toString();
  	}
  	public String diff(Object object,StringBuffer bf,boolean reportHTML){
		return bf.toString();
	}
	public String diff(Object object,StringBuffer bf,boolean reportHTML,List<String> fieldsNotIncluded){
		return bf.toString();
	}
	
	
	/** Utilities */
	
	public static String[] toArray(){
		String[] res = new String[values().length];
		int i=0;
		for (CausalePagamentoType tmp : values()) {
			res[i]=tmp.getValue();
			i++;
		}
		return res;
	}	
	public static String[] toStringArray(){
		String[] res = new String[values().length];
		int i=0;
		for (CausalePagamentoType tmp : values()) {
			res[i]=tmp.toString();
			i++;
		}
		return res;
	}
	public static String[] toEnumNameArray(){
		String[] res = new String[values().length];
		int i=0;
		for (CausalePagamentoType tmp : values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}
	
	public static boolean contains(String value){
		return toEnumConstant(value)!=null;
	}
	
	public static CausalePagamentoType toEnumConstant(String value){
		CausalePagamentoType res = null;
		if(CausalePagamentoType.A.getValue().equals(value)){
			res = CausalePagamentoType.A;
		}else if(CausalePagamentoType.B.getValue().equals(value)){
			res = CausalePagamentoType.B;
		}else if(CausalePagamentoType.C.getValue().equals(value)){
			res = CausalePagamentoType.C;
		}else if(CausalePagamentoType.D.getValue().equals(value)){
			res = CausalePagamentoType.D;
		}else if(CausalePagamentoType.E.getValue().equals(value)){
			res = CausalePagamentoType.E;
		}else if(CausalePagamentoType.G.getValue().equals(value)){
			res = CausalePagamentoType.G;
		}else if(CausalePagamentoType.H.getValue().equals(value)){
			res = CausalePagamentoType.H;
		}else if(CausalePagamentoType.I.getValue().equals(value)){
			res = CausalePagamentoType.I;
		}else if(CausalePagamentoType.L.getValue().equals(value)){
			res = CausalePagamentoType.L;
		}else if(CausalePagamentoType.M.getValue().equals(value)){
			res = CausalePagamentoType.M;
		}else if(CausalePagamentoType.N.getValue().equals(value)){
			res = CausalePagamentoType.N;
		}else if(CausalePagamentoType.O.getValue().equals(value)){
			res = CausalePagamentoType.O;
		}else if(CausalePagamentoType.P.getValue().equals(value)){
			res = CausalePagamentoType.P;
		}else if(CausalePagamentoType.Q.getValue().equals(value)){
			res = CausalePagamentoType.Q;
		}else if(CausalePagamentoType.R.getValue().equals(value)){
			res = CausalePagamentoType.R;
		}else if(CausalePagamentoType.S.getValue().equals(value)){
			res = CausalePagamentoType.S;
		}else if(CausalePagamentoType.T.getValue().equals(value)){
			res = CausalePagamentoType.T;
		}else if(CausalePagamentoType.U.getValue().equals(value)){
			res = CausalePagamentoType.U;
		}else if(CausalePagamentoType.V.getValue().equals(value)){
			res = CausalePagamentoType.V;
		}else if(CausalePagamentoType.W.getValue().equals(value)){
			res = CausalePagamentoType.W;
		}else if(CausalePagamentoType.X.getValue().equals(value)){
			res = CausalePagamentoType.X;
		}else if(CausalePagamentoType.Y.getValue().equals(value)){
			res = CausalePagamentoType.Y;
		}else if(CausalePagamentoType.Z.getValue().equals(value)){
			res = CausalePagamentoType.Z;
		}else if(CausalePagamentoType.L1.getValue().equals(value)){
			res = CausalePagamentoType.L1;
		}else if(CausalePagamentoType.M1.getValue().equals(value)){
			res = CausalePagamentoType.M1;
		}else if(CausalePagamentoType.O1.getValue().equals(value)){
			res = CausalePagamentoType.O1;
		}else if(CausalePagamentoType.V1.getValue().equals(value)){
			res = CausalePagamentoType.V1;
		}
		return res;
	}
	
	public static IEnumeration toEnumConstantFromString(String value){
		CausalePagamentoType res = null;
		if(CausalePagamentoType.A.toString().equals(value)){
			res = CausalePagamentoType.A;
		}else if(CausalePagamentoType.B.toString().equals(value)){
			res = CausalePagamentoType.B;
		}else if(CausalePagamentoType.C.toString().equals(value)){
			res = CausalePagamentoType.C;
		}else if(CausalePagamentoType.D.toString().equals(value)){
			res = CausalePagamentoType.D;
		}else if(CausalePagamentoType.E.toString().equals(value)){
			res = CausalePagamentoType.E;
		}else if(CausalePagamentoType.G.toString().equals(value)){
			res = CausalePagamentoType.G;
		}else if(CausalePagamentoType.H.toString().equals(value)){
			res = CausalePagamentoType.H;
		}else if(CausalePagamentoType.I.toString().equals(value)){
			res = CausalePagamentoType.I;
		}else if(CausalePagamentoType.L.toString().equals(value)){
			res = CausalePagamentoType.L;
		}else if(CausalePagamentoType.M.toString().equals(value)){
			res = CausalePagamentoType.M;
		}else if(CausalePagamentoType.N.toString().equals(value)){
			res = CausalePagamentoType.N;
		}else if(CausalePagamentoType.O.toString().equals(value)){
			res = CausalePagamentoType.O;
		}else if(CausalePagamentoType.P.toString().equals(value)){
			res = CausalePagamentoType.P;
		}else if(CausalePagamentoType.Q.toString().equals(value)){
			res = CausalePagamentoType.Q;
		}else if(CausalePagamentoType.R.toString().equals(value)){
			res = CausalePagamentoType.R;
		}else if(CausalePagamentoType.S.toString().equals(value)){
			res = CausalePagamentoType.S;
		}else if(CausalePagamentoType.T.toString().equals(value)){
			res = CausalePagamentoType.T;
		}else if(CausalePagamentoType.U.toString().equals(value)){
			res = CausalePagamentoType.U;
		}else if(CausalePagamentoType.V.toString().equals(value)){
			res = CausalePagamentoType.V;
		}else if(CausalePagamentoType.W.toString().equals(value)){
			res = CausalePagamentoType.W;
		}else if(CausalePagamentoType.X.toString().equals(value)){
			res = CausalePagamentoType.X;
		}else if(CausalePagamentoType.Y.toString().equals(value)){
			res = CausalePagamentoType.Y;
		}else if(CausalePagamentoType.Z.toString().equals(value)){
			res = CausalePagamentoType.Z;
		}else if(CausalePagamentoType.L1.toString().equals(value)){
			res = CausalePagamentoType.L1;
		}else if(CausalePagamentoType.M1.toString().equals(value)){
			res = CausalePagamentoType.M1;
		}else if(CausalePagamentoType.O1.toString().equals(value)){
			res = CausalePagamentoType.O1;
		}else if(CausalePagamentoType.V1.toString().equals(value)){
			res = CausalePagamentoType.V1;
		}
		return res;
	}
}
