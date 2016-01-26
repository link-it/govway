/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.core.diagnostica.model;

import org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model FiltroInformazioniDiagnostici 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FiltroInformazioniDiagnosticiModel extends AbstractModel<FiltroInformazioniDiagnostici> {

	public FiltroInformazioniDiagnosticiModel(){
	
		super();
	
		this.CODICE = new Field("codice",java.lang.String.class,"filtro-informazioni-diagnostici",FiltroInformazioniDiagnostici.class);
		this.MESSAGGIO = new Field("messaggio",java.lang.String.class,"filtro-informazioni-diagnostici",FiltroInformazioniDiagnostici.class);
		this.SEVERITA = new Field("severita",java.lang.Integer.class,"filtro-informazioni-diagnostici",FiltroInformazioniDiagnostici.class);
		this.MODULO = new Field("modulo",java.lang.String.class,"filtro-informazioni-diagnostici",FiltroInformazioniDiagnostici.class);
		this.IDENTIFICATIVO_RISPOSTA = new Field("identificativo-risposta",java.lang.String.class,"filtro-informazioni-diagnostici",FiltroInformazioniDiagnostici.class);
	
	}
	
	public FiltroInformazioniDiagnosticiModel(IField father){
	
		super(father);
	
		this.CODICE = new ComplexField(father,"codice",java.lang.String.class,"filtro-informazioni-diagnostici",FiltroInformazioniDiagnostici.class);
		this.MESSAGGIO = new ComplexField(father,"messaggio",java.lang.String.class,"filtro-informazioni-diagnostici",FiltroInformazioniDiagnostici.class);
		this.SEVERITA = new ComplexField(father,"severita",java.lang.Integer.class,"filtro-informazioni-diagnostici",FiltroInformazioniDiagnostici.class);
		this.MODULO = new ComplexField(father,"modulo",java.lang.String.class,"filtro-informazioni-diagnostici",FiltroInformazioniDiagnostici.class);
		this.IDENTIFICATIVO_RISPOSTA = new ComplexField(father,"identificativo-risposta",java.lang.String.class,"filtro-informazioni-diagnostici",FiltroInformazioniDiagnostici.class);
	
	}
	
	

	public IField CODICE = null;
	 
	public IField MESSAGGIO = null;
	 
	public IField SEVERITA = null;
	 
	public IField MODULO = null;
	 
	public IField IDENTIFICATIVO_RISPOSTA = null;
	 

	@Override
	public Class<FiltroInformazioniDiagnostici> getModeledClass(){
		return FiltroInformazioniDiagnostici.class;
	}
	
	@Override
	public String toString(){
		if(this.getModeledClass()!=null){
			return this.getModeledClass().getName();
		}else{
			return "N.D.";
		}
	}

}