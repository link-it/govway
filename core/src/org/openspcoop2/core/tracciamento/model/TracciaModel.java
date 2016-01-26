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
package org.openspcoop2.core.tracciamento.model;

import org.openspcoop2.core.tracciamento.Traccia;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Traccia 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TracciaModel extends AbstractModel<Traccia> {

	public TracciaModel(){
	
		super();
	
		this.DOMINIO = new org.openspcoop2.core.tracciamento.model.DominioModel(new Field("dominio",org.openspcoop2.core.tracciamento.Dominio.class,"traccia",Traccia.class));
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"traccia",Traccia.class);
		this.ESITO_ELABORAZIONE = new org.openspcoop2.core.tracciamento.model.TracciaEsitoElaborazioneModel(new Field("esito-elaborazione",org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione.class,"traccia",Traccia.class));
		this.IDENTIFICATIVO_CORRELAZIONE_RICHIESTA = new Field("identificativo-correlazione-richiesta",java.lang.String.class,"traccia",Traccia.class);
		this.IDENTIFICATIVO_CORRELAZIONE_RISPOSTA = new Field("identificativo-correlazione-risposta",java.lang.String.class,"traccia",Traccia.class);
		this.CORRELAZIONE_APPLICATIVA_AND_MATCH = new Field("correlazione-applicativa-and-match",boolean.class,"traccia",Traccia.class);
		this.LOCATION = new Field("location",java.lang.String.class,"traccia",Traccia.class);
		this.BUSTA = new org.openspcoop2.core.tracciamento.model.BustaModel(new Field("busta",org.openspcoop2.core.tracciamento.Busta.class,"traccia",Traccia.class));
		this.RICERCA_SOLO_BUSTE_ERRORE = new Field("ricerca-solo-buste-errore",boolean.class,"traccia",Traccia.class);
		this.BUSTA_XML = new Field("busta-xml",java.lang.String.class,"traccia",Traccia.class);
		this.ALLEGATI = new org.openspcoop2.core.tracciamento.model.AllegatiModel(new Field("allegati",org.openspcoop2.core.tracciamento.Allegati.class,"traccia",Traccia.class));
		this.TIPO = new Field("tipo",java.lang.String.class,"traccia",Traccia.class);
	
	}
	
	public TracciaModel(IField father){
	
		super(father);
	
		this.DOMINIO = new org.openspcoop2.core.tracciamento.model.DominioModel(new ComplexField(father,"dominio",org.openspcoop2.core.tracciamento.Dominio.class,"traccia",Traccia.class));
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"traccia",Traccia.class);
		this.ESITO_ELABORAZIONE = new org.openspcoop2.core.tracciamento.model.TracciaEsitoElaborazioneModel(new ComplexField(father,"esito-elaborazione",org.openspcoop2.core.tracciamento.TracciaEsitoElaborazione.class,"traccia",Traccia.class));
		this.IDENTIFICATIVO_CORRELAZIONE_RICHIESTA = new ComplexField(father,"identificativo-correlazione-richiesta",java.lang.String.class,"traccia",Traccia.class);
		this.IDENTIFICATIVO_CORRELAZIONE_RISPOSTA = new ComplexField(father,"identificativo-correlazione-risposta",java.lang.String.class,"traccia",Traccia.class);
		this.CORRELAZIONE_APPLICATIVA_AND_MATCH = new ComplexField(father,"correlazione-applicativa-and-match",boolean.class,"traccia",Traccia.class);
		this.LOCATION = new ComplexField(father,"location",java.lang.String.class,"traccia",Traccia.class);
		this.BUSTA = new org.openspcoop2.core.tracciamento.model.BustaModel(new ComplexField(father,"busta",org.openspcoop2.core.tracciamento.Busta.class,"traccia",Traccia.class));
		this.RICERCA_SOLO_BUSTE_ERRORE = new ComplexField(father,"ricerca-solo-buste-errore",boolean.class,"traccia",Traccia.class);
		this.BUSTA_XML = new ComplexField(father,"busta-xml",java.lang.String.class,"traccia",Traccia.class);
		this.ALLEGATI = new org.openspcoop2.core.tracciamento.model.AllegatiModel(new ComplexField(father,"allegati",org.openspcoop2.core.tracciamento.Allegati.class,"traccia",Traccia.class));
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"traccia",Traccia.class);
	
	}
	
	

	public org.openspcoop2.core.tracciamento.model.DominioModel DOMINIO = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 
	public org.openspcoop2.core.tracciamento.model.TracciaEsitoElaborazioneModel ESITO_ELABORAZIONE = null;
	 
	public IField IDENTIFICATIVO_CORRELAZIONE_RICHIESTA = null;
	 
	public IField IDENTIFICATIVO_CORRELAZIONE_RISPOSTA = null;
	 
	public IField CORRELAZIONE_APPLICATIVA_AND_MATCH = null;
	 
	public IField LOCATION = null;
	 
	public org.openspcoop2.core.tracciamento.model.BustaModel BUSTA = null;
	 
	public IField RICERCA_SOLO_BUSTE_ERRORE = null;
	 
	public IField BUSTA_XML = null;
	 
	public org.openspcoop2.core.tracciamento.model.AllegatiModel ALLEGATI = null;
	 
	public IField TIPO = null;
	 

	@Override
	public Class<Traccia> getModeledClass(){
		return Traccia.class;
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