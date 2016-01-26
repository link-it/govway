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

import org.openspcoop2.core.diagnostica.FiltroInformazioneProtocollo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model FiltroInformazioneProtocollo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FiltroInformazioneProtocolloModel extends AbstractModel<FiltroInformazioneProtocollo> {

	public FiltroInformazioneProtocolloModel(){
	
		super();
	
		this.TIPO_PORTA = new Field("tipo-porta",java.lang.String.class,"filtro-informazione-protocollo",FiltroInformazioneProtocollo.class);
		this.NOME_PORTA = new Field("nome-porta",java.lang.String.class,"filtro-informazione-protocollo",FiltroInformazioneProtocollo.class);
		this.RICERCA_SOLO_MESSAGGI_CORRELATI_INFORMAZIONI_PROTOCOLLO = new Field("ricerca-solo-messaggi-correlati-informazioni-protocollo",boolean.class,"filtro-informazione-protocollo",FiltroInformazioneProtocollo.class);
		this.FRUITORE = new org.openspcoop2.core.diagnostica.model.SoggettoModel(new Field("fruitore",org.openspcoop2.core.diagnostica.Soggetto.class,"filtro-informazione-protocollo",FiltroInformazioneProtocollo.class));
		this.EROGATORE = new org.openspcoop2.core.diagnostica.model.SoggettoModel(new Field("erogatore",org.openspcoop2.core.diagnostica.Soggetto.class,"filtro-informazione-protocollo",FiltroInformazioneProtocollo.class));
		this.SERVIZIO = new org.openspcoop2.core.diagnostica.model.ServizioModel(new Field("servizio",org.openspcoop2.core.diagnostica.Servizio.class,"filtro-informazione-protocollo",FiltroInformazioneProtocollo.class));
		this.AZIONE = new Field("azione",java.lang.String.class,"filtro-informazione-protocollo",FiltroInformazioneProtocollo.class);
		this.IDENTIFICATIVO_CORRELAZIONE_RICHIESTA = new Field("identificativo-correlazione-richiesta",java.lang.String.class,"filtro-informazione-protocollo",FiltroInformazioneProtocollo.class);
		this.IDENTIFICATIVO_CORRELAZIONE_RISPOSTA = new Field("identificativo-correlazione-risposta",java.lang.String.class,"filtro-informazione-protocollo",FiltroInformazioneProtocollo.class);
		this.CORRELAZIONE_APPLICATIVA_AND_MATCH = new Field("correlazione-applicativa-and-match",boolean.class,"filtro-informazione-protocollo",FiltroInformazioneProtocollo.class);
		this.SERVIZIO_APPLICATIVO = new Field("servizio-applicativo",java.lang.String.class,"filtro-informazione-protocollo",FiltroInformazioneProtocollo.class);
	
	}
	
	public FiltroInformazioneProtocolloModel(IField father){
	
		super(father);
	
		this.TIPO_PORTA = new ComplexField(father,"tipo-porta",java.lang.String.class,"filtro-informazione-protocollo",FiltroInformazioneProtocollo.class);
		this.NOME_PORTA = new ComplexField(father,"nome-porta",java.lang.String.class,"filtro-informazione-protocollo",FiltroInformazioneProtocollo.class);
		this.RICERCA_SOLO_MESSAGGI_CORRELATI_INFORMAZIONI_PROTOCOLLO = new ComplexField(father,"ricerca-solo-messaggi-correlati-informazioni-protocollo",boolean.class,"filtro-informazione-protocollo",FiltroInformazioneProtocollo.class);
		this.FRUITORE = new org.openspcoop2.core.diagnostica.model.SoggettoModel(new ComplexField(father,"fruitore",org.openspcoop2.core.diagnostica.Soggetto.class,"filtro-informazione-protocollo",FiltroInformazioneProtocollo.class));
		this.EROGATORE = new org.openspcoop2.core.diagnostica.model.SoggettoModel(new ComplexField(father,"erogatore",org.openspcoop2.core.diagnostica.Soggetto.class,"filtro-informazione-protocollo",FiltroInformazioneProtocollo.class));
		this.SERVIZIO = new org.openspcoop2.core.diagnostica.model.ServizioModel(new ComplexField(father,"servizio",org.openspcoop2.core.diagnostica.Servizio.class,"filtro-informazione-protocollo",FiltroInformazioneProtocollo.class));
		this.AZIONE = new ComplexField(father,"azione",java.lang.String.class,"filtro-informazione-protocollo",FiltroInformazioneProtocollo.class);
		this.IDENTIFICATIVO_CORRELAZIONE_RICHIESTA = new ComplexField(father,"identificativo-correlazione-richiesta",java.lang.String.class,"filtro-informazione-protocollo",FiltroInformazioneProtocollo.class);
		this.IDENTIFICATIVO_CORRELAZIONE_RISPOSTA = new ComplexField(father,"identificativo-correlazione-risposta",java.lang.String.class,"filtro-informazione-protocollo",FiltroInformazioneProtocollo.class);
		this.CORRELAZIONE_APPLICATIVA_AND_MATCH = new ComplexField(father,"correlazione-applicativa-and-match",boolean.class,"filtro-informazione-protocollo",FiltroInformazioneProtocollo.class);
		this.SERVIZIO_APPLICATIVO = new ComplexField(father,"servizio-applicativo",java.lang.String.class,"filtro-informazione-protocollo",FiltroInformazioneProtocollo.class);
	
	}
	
	

	public IField TIPO_PORTA = null;
	 
	public IField NOME_PORTA = null;
	 
	public IField RICERCA_SOLO_MESSAGGI_CORRELATI_INFORMAZIONI_PROTOCOLLO = null;
	 
	public org.openspcoop2.core.diagnostica.model.SoggettoModel FRUITORE = null;
	 
	public org.openspcoop2.core.diagnostica.model.SoggettoModel EROGATORE = null;
	 
	public org.openspcoop2.core.diagnostica.model.ServizioModel SERVIZIO = null;
	 
	public IField AZIONE = null;
	 
	public IField IDENTIFICATIVO_CORRELAZIONE_RICHIESTA = null;
	 
	public IField IDENTIFICATIVO_CORRELAZIONE_RISPOSTA = null;
	 
	public IField CORRELAZIONE_APPLICATIVA_AND_MATCH = null;
	 
	public IField SERVIZIO_APPLICATIVO = null;
	 

	@Override
	public Class<FiltroInformazioneProtocollo> getModeledClass(){
		return FiltroInformazioneProtocollo.class;
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