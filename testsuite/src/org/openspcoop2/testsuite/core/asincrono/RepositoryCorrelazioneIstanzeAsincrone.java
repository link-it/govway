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

package org.openspcoop2.testsuite.core.asincrono;

import java.util.Vector;

/**
 * Repository degli id utilizzati nelle istanze di risposta di un profilo asincrono.
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RepositoryCorrelazioneIstanzeAsincrone {
private Vector<SetIDCorrelazione> vet;
private int positionOneWay;
private int positionInverse;


public RepositoryCorrelazioneIstanzeAsincrone(){
	this.vet=new Vector<SetIDCorrelazione>();
	this.positionOneWay=0;
	this.positionInverse=0;
}

public synchronized void add(String val,String val2){
	this.vet.add(new SetIDCorrelazione(val,val2));
}

public synchronized String getNextIDRichiesta(){
	String next=this.vet.get(this.positionOneWay).idRichiesta;
	this.positionOneWay++;
	return next;
}

public synchronized String getNextIDRisposta(){
	String next=this.vet.get(this.positionInverse).idRisposta;
	this.positionInverse++;
	return next;
}

public synchronized String getIDRispostaByReference(String idRichiesta){
	for(int i=0;i<this.vet.size();i++){
		String str=this.vet.get(i).idRichiesta;
		if(str.equals(idRichiesta))return this.vet.get(i).idRisposta;
	}
	return null;
}

public synchronized String getIDRichiestaByReference(String idRisposta){
	for(int i=0;i<this.vet.size();i++){
		String str=this.vet.get(i).idRisposta;
		if(str.equals(idRisposta))return this.vet.get(i).idRichiesta;
	}
	return null;
}

public int size(){
	return this.vet.size();
}
}