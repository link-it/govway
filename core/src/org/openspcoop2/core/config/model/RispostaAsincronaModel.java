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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.RispostaAsincrona;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RispostaAsincrona 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RispostaAsincronaModel extends AbstractModel<RispostaAsincrona> {

	public RispostaAsincronaModel(){
	
		super();
	
		this.CREDENZIALI = new org.openspcoop2.core.config.model.CredenzialiModel(new Field("credenziali",org.openspcoop2.core.config.Credenziali.class,"risposta-asincrona",RispostaAsincrona.class));
		this.CONNETTORE = new org.openspcoop2.core.config.model.ConnettoreModel(new Field("connettore",org.openspcoop2.core.config.Connettore.class,"risposta-asincrona",RispostaAsincrona.class));
		this.GESTIONE_ERRORE = new org.openspcoop2.core.config.model.GestioneErroreModel(new Field("gestione-errore",org.openspcoop2.core.config.GestioneErrore.class,"risposta-asincrona",RispostaAsincrona.class));
		this.SBUSTAMENTO_SOAP = new Field("sbustamento-soap",java.lang.String.class,"risposta-asincrona",RispostaAsincrona.class);
		this.SBUSTAMENTO_INFORMAZIONI_PROTOCOLLO = new Field("sbustamento-informazioni-protocollo",java.lang.String.class,"risposta-asincrona",RispostaAsincrona.class);
		this.GET_MESSAGE = new Field("get-message",java.lang.String.class,"risposta-asincrona",RispostaAsincrona.class);
		this.AUTENTICAZIONE = new Field("autenticazione",java.lang.String.class,"risposta-asincrona",RispostaAsincrona.class);
		this.INVIO_PER_RIFERIMENTO = new Field("invio-per-riferimento",java.lang.String.class,"risposta-asincrona",RispostaAsincrona.class);
		this.RISPOSTA_PER_RIFERIMENTO = new Field("risposta-per-riferimento",java.lang.String.class,"risposta-asincrona",RispostaAsincrona.class);
	
	}
	
	public RispostaAsincronaModel(IField father){
	
		super(father);
	
		this.CREDENZIALI = new org.openspcoop2.core.config.model.CredenzialiModel(new ComplexField(father,"credenziali",org.openspcoop2.core.config.Credenziali.class,"risposta-asincrona",RispostaAsincrona.class));
		this.CONNETTORE = new org.openspcoop2.core.config.model.ConnettoreModel(new ComplexField(father,"connettore",org.openspcoop2.core.config.Connettore.class,"risposta-asincrona",RispostaAsincrona.class));
		this.GESTIONE_ERRORE = new org.openspcoop2.core.config.model.GestioneErroreModel(new ComplexField(father,"gestione-errore",org.openspcoop2.core.config.GestioneErrore.class,"risposta-asincrona",RispostaAsincrona.class));
		this.SBUSTAMENTO_SOAP = new ComplexField(father,"sbustamento-soap",java.lang.String.class,"risposta-asincrona",RispostaAsincrona.class);
		this.SBUSTAMENTO_INFORMAZIONI_PROTOCOLLO = new ComplexField(father,"sbustamento-informazioni-protocollo",java.lang.String.class,"risposta-asincrona",RispostaAsincrona.class);
		this.GET_MESSAGE = new ComplexField(father,"get-message",java.lang.String.class,"risposta-asincrona",RispostaAsincrona.class);
		this.AUTENTICAZIONE = new ComplexField(father,"autenticazione",java.lang.String.class,"risposta-asincrona",RispostaAsincrona.class);
		this.INVIO_PER_RIFERIMENTO = new ComplexField(father,"invio-per-riferimento",java.lang.String.class,"risposta-asincrona",RispostaAsincrona.class);
		this.RISPOSTA_PER_RIFERIMENTO = new ComplexField(father,"risposta-per-riferimento",java.lang.String.class,"risposta-asincrona",RispostaAsincrona.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.CredenzialiModel CREDENZIALI = null;
	 
	public org.openspcoop2.core.config.model.ConnettoreModel CONNETTORE = null;
	 
	public org.openspcoop2.core.config.model.GestioneErroreModel GESTIONE_ERRORE = null;
	 
	public IField SBUSTAMENTO_SOAP = null;
	 
	public IField SBUSTAMENTO_INFORMAZIONI_PROTOCOLLO = null;
	 
	public IField GET_MESSAGE = null;
	 
	public IField AUTENTICAZIONE = null;
	 
	public IField INVIO_PER_RIFERIMENTO = null;
	 
	public IField RISPOSTA_PER_RIFERIMENTO = null;
	 

	@Override
	public Class<RispostaAsincrona> getModeledClass(){
		return RispostaAsincrona.class;
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