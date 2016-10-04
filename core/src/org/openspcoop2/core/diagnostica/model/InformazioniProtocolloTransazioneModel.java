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
package org.openspcoop2.core.diagnostica.model;

import org.openspcoop2.core.diagnostica.InformazioniProtocolloTransazione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model InformazioniProtocolloTransazione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InformazioniProtocolloTransazioneModel extends AbstractModel<InformazioniProtocolloTransazione> {

	public InformazioniProtocolloTransazioneModel(){
	
		super();
	
		this.TIPO_PD_D = new Field("tipoPdD",java.lang.String.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class);
		this.IDENTIFICATIVO_RICHIESTA = new Field("identificativo-richiesta",java.lang.String.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class);
		this.DOMINIO = new org.openspcoop2.core.diagnostica.model.DominioTransazioneModel(new Field("dominio",org.openspcoop2.core.diagnostica.DominioTransazione.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class));
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class);
		this.NOME_PORTA = new Field("nome-porta",java.lang.String.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class);
		this.FRUITORE = new org.openspcoop2.core.diagnostica.model.SoggettoModel(new Field("fruitore",org.openspcoop2.core.diagnostica.Soggetto.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class));
		this.EROGATORE = new org.openspcoop2.core.diagnostica.model.SoggettoModel(new Field("erogatore",org.openspcoop2.core.diagnostica.Soggetto.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class));
		this.SERVIZIO = new org.openspcoop2.core.diagnostica.model.ServizioModel(new Field("servizio",org.openspcoop2.core.diagnostica.Servizio.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class));
		this.AZIONE = new Field("azione",java.lang.String.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class);
		this.IDENTIFICATIVO_CORRELAZIONE_RICHIESTA = new Field("identificativo-correlazione-richiesta",java.lang.String.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class);
		this.IDENTIFICATIVO_CORRELAZIONE_RISPOSTA = new Field("identificativo-correlazione-risposta",java.lang.String.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class);
		this.CORRELAZIONE_APPLICATIVA_AND_MATCH = new Field("correlazione-applicativa-and-match",boolean.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class);
		this.PROTOCOLLO = new org.openspcoop2.core.diagnostica.model.ProtocolloModel(new Field("protocollo",org.openspcoop2.core.diagnostica.Protocollo.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class));
		this.SERVIZIO_APPLICATIVO = new Field("servizio-applicativo",java.lang.String.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class);
		this.FILTRO_SERVIZIO_APPLICATIVO = new Field("filtro-servizio-applicativo",java.lang.String.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class);
		this.FILTRO_INFORMAZIONI_DIAGNOSTICI = new org.openspcoop2.core.diagnostica.model.FiltroInformazioniDiagnosticiModel(new Field("filtro-informazioni-diagnostici",org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class));
	
	}
	
	public InformazioniProtocolloTransazioneModel(IField father){
	
		super(father);
	
		this.TIPO_PD_D = new ComplexField(father,"tipoPdD",java.lang.String.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class);
		this.IDENTIFICATIVO_RICHIESTA = new ComplexField(father,"identificativo-richiesta",java.lang.String.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class);
		this.DOMINIO = new org.openspcoop2.core.diagnostica.model.DominioTransazioneModel(new ComplexField(father,"dominio",org.openspcoop2.core.diagnostica.DominioTransazione.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class));
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class);
		this.NOME_PORTA = new ComplexField(father,"nome-porta",java.lang.String.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class);
		this.FRUITORE = new org.openspcoop2.core.diagnostica.model.SoggettoModel(new ComplexField(father,"fruitore",org.openspcoop2.core.diagnostica.Soggetto.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class));
		this.EROGATORE = new org.openspcoop2.core.diagnostica.model.SoggettoModel(new ComplexField(father,"erogatore",org.openspcoop2.core.diagnostica.Soggetto.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class));
		this.SERVIZIO = new org.openspcoop2.core.diagnostica.model.ServizioModel(new ComplexField(father,"servizio",org.openspcoop2.core.diagnostica.Servizio.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class));
		this.AZIONE = new ComplexField(father,"azione",java.lang.String.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class);
		this.IDENTIFICATIVO_CORRELAZIONE_RICHIESTA = new ComplexField(father,"identificativo-correlazione-richiesta",java.lang.String.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class);
		this.IDENTIFICATIVO_CORRELAZIONE_RISPOSTA = new ComplexField(father,"identificativo-correlazione-risposta",java.lang.String.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class);
		this.CORRELAZIONE_APPLICATIVA_AND_MATCH = new ComplexField(father,"correlazione-applicativa-and-match",boolean.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class);
		this.PROTOCOLLO = new org.openspcoop2.core.diagnostica.model.ProtocolloModel(new ComplexField(father,"protocollo",org.openspcoop2.core.diagnostica.Protocollo.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class));
		this.SERVIZIO_APPLICATIVO = new ComplexField(father,"servizio-applicativo",java.lang.String.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class);
		this.FILTRO_SERVIZIO_APPLICATIVO = new ComplexField(father,"filtro-servizio-applicativo",java.lang.String.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class);
		this.FILTRO_INFORMAZIONI_DIAGNOSTICI = new org.openspcoop2.core.diagnostica.model.FiltroInformazioniDiagnosticiModel(new ComplexField(father,"filtro-informazioni-diagnostici",org.openspcoop2.core.diagnostica.FiltroInformazioniDiagnostici.class,"informazioni-protocollo-transazione",InformazioniProtocolloTransazione.class));
	
	}
	
	

	public IField TIPO_PD_D = null;
	 
	public IField IDENTIFICATIVO_RICHIESTA = null;
	 
	public org.openspcoop2.core.diagnostica.model.DominioTransazioneModel DOMINIO = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 
	public IField NOME_PORTA = null;
	 
	public org.openspcoop2.core.diagnostica.model.SoggettoModel FRUITORE = null;
	 
	public org.openspcoop2.core.diagnostica.model.SoggettoModel EROGATORE = null;
	 
	public org.openspcoop2.core.diagnostica.model.ServizioModel SERVIZIO = null;
	 
	public IField AZIONE = null;
	 
	public IField IDENTIFICATIVO_CORRELAZIONE_RICHIESTA = null;
	 
	public IField IDENTIFICATIVO_CORRELAZIONE_RISPOSTA = null;
	 
	public IField CORRELAZIONE_APPLICATIVA_AND_MATCH = null;
	 
	public org.openspcoop2.core.diagnostica.model.ProtocolloModel PROTOCOLLO = null;
	 
	public IField SERVIZIO_APPLICATIVO = null;
	 
	public IField FILTRO_SERVIZIO_APPLICATIVO = null;
	 
	public org.openspcoop2.core.diagnostica.model.FiltroInformazioniDiagnosticiModel FILTRO_INFORMAZIONI_DIAGNOSTICI = null;
	 

	@Override
	public Class<InformazioniProtocolloTransazione> getModeledClass(){
		return InformazioniProtocolloTransazione.class;
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