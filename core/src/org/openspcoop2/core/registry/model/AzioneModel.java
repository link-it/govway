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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.Azione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Azione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AzioneModel extends AbstractModel<Azione> {

	public AzioneModel(){
	
		super();
	
		this.PROF_AZIONE = new Field("prof-azione",java.lang.String.class,"azione",Azione.class);
		this.ID_ACCORDO = new Field("id-accordo",java.lang.Long.class,"azione",Azione.class);
		this.NOME = new Field("nome",java.lang.String.class,"azione",Azione.class);
		this.PROFILO_COLLABORAZIONE = new Field("profilo-collaborazione",java.lang.String.class,"azione",Azione.class);
		this.FILTRO_DUPLICATI = new Field("filtro-duplicati",java.lang.String.class,"azione",Azione.class);
		this.CONFERMA_RICEZIONE = new Field("conferma-ricezione",java.lang.String.class,"azione",Azione.class);
		this.ID_COLLABORAZIONE = new Field("id-collaborazione",java.lang.String.class,"azione",Azione.class);
		this.CONSEGNA_IN_ORDINE = new Field("consegna-in-ordine",java.lang.String.class,"azione",Azione.class);
		this.SCADENZA = new Field("scadenza",java.lang.String.class,"azione",Azione.class);
		this.CORRELATA = new Field("correlata",java.lang.String.class,"azione",Azione.class);
	
	}
	
	public AzioneModel(IField father){
	
		super(father);
	
		this.PROF_AZIONE = new ComplexField(father,"prof-azione",java.lang.String.class,"azione",Azione.class);
		this.ID_ACCORDO = new ComplexField(father,"id-accordo",java.lang.Long.class,"azione",Azione.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"azione",Azione.class);
		this.PROFILO_COLLABORAZIONE = new ComplexField(father,"profilo-collaborazione",java.lang.String.class,"azione",Azione.class);
		this.FILTRO_DUPLICATI = new ComplexField(father,"filtro-duplicati",java.lang.String.class,"azione",Azione.class);
		this.CONFERMA_RICEZIONE = new ComplexField(father,"conferma-ricezione",java.lang.String.class,"azione",Azione.class);
		this.ID_COLLABORAZIONE = new ComplexField(father,"id-collaborazione",java.lang.String.class,"azione",Azione.class);
		this.CONSEGNA_IN_ORDINE = new ComplexField(father,"consegna-in-ordine",java.lang.String.class,"azione",Azione.class);
		this.SCADENZA = new ComplexField(father,"scadenza",java.lang.String.class,"azione",Azione.class);
		this.CORRELATA = new ComplexField(father,"correlata",java.lang.String.class,"azione",Azione.class);
	
	}
	
	

	public IField PROF_AZIONE = null;
	 
	public IField ID_ACCORDO = null;
	 
	public IField NOME = null;
	 
	public IField PROFILO_COLLABORAZIONE = null;
	 
	public IField FILTRO_DUPLICATI = null;
	 
	public IField CONFERMA_RICEZIONE = null;
	 
	public IField ID_COLLABORAZIONE = null;
	 
	public IField CONSEGNA_IN_ORDINE = null;
	 
	public IField SCADENZA = null;
	 
	public IField CORRELATA = null;
	 

	@Override
	public Class<Azione> getModeledClass(){
		return Azione.class;
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