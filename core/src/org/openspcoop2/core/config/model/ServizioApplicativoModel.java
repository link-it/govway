/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.ServizioApplicativo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ServizioApplicativo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServizioApplicativoModel extends AbstractModel<ServizioApplicativo> {

	public ServizioApplicativoModel(){
	
		super();
	
		this.INVOCAZIONE_PORTA = new org.openspcoop2.core.config.model.InvocazionePortaModel(new Field("invocazione-porta",org.openspcoop2.core.config.InvocazionePorta.class,"servizio-applicativo",ServizioApplicativo.class));
		this.INVOCAZIONE_SERVIZIO = new org.openspcoop2.core.config.model.InvocazioneServizioModel(new Field("invocazione-servizio",org.openspcoop2.core.config.InvocazioneServizio.class,"servizio-applicativo",ServizioApplicativo.class));
		this.RISPOSTA_ASINCRONA = new org.openspcoop2.core.config.model.RispostaAsincronaModel(new Field("risposta-asincrona",org.openspcoop2.core.config.RispostaAsincrona.class,"servizio-applicativo",ServizioApplicativo.class));
		this.PROPRIETA = new org.openspcoop2.core.config.model.ProprietaModel(new Field("proprieta",org.openspcoop2.core.config.Proprieta.class,"servizio-applicativo",ServizioApplicativo.class));
		this.PROTOCOL_PROPERTY = new org.openspcoop2.core.config.model.ProtocolPropertyModel(new Field("protocol-property",org.openspcoop2.core.config.ProtocolProperty.class,"servizio-applicativo",ServizioApplicativo.class));
		this.ID_SOGGETTO = new Field("id-soggetto",java.lang.Long.class,"servizio-applicativo",ServizioApplicativo.class);
		this.TIPO_SOGGETTO_PROPRIETARIO = new Field("tipo-soggetto-proprietario",java.lang.String.class,"servizio-applicativo",ServizioApplicativo.class);
		this.NOME_SOGGETTO_PROPRIETARIO = new Field("nome-soggetto-proprietario",java.lang.String.class,"servizio-applicativo",ServizioApplicativo.class);
		this.TIPOLOGIA_FRUIZIONE = new Field("tipologia-fruizione",java.lang.String.class,"servizio-applicativo",ServizioApplicativo.class);
		this.TIPOLOGIA_EROGAZIONE = new Field("tipologia-erogazione",java.lang.String.class,"servizio-applicativo",ServizioApplicativo.class);
		this.NOME = new Field("nome",java.lang.String.class,"servizio-applicativo",ServizioApplicativo.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"servizio-applicativo",ServizioApplicativo.class);
		this.USE_AS_CLIENT = new Field("use-as-client",boolean.class,"servizio-applicativo",ServizioApplicativo.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"servizio-applicativo",ServizioApplicativo.class);
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"servizio-applicativo",ServizioApplicativo.class);
	
	}
	
	public ServizioApplicativoModel(IField father){
	
		super(father);
	
		this.INVOCAZIONE_PORTA = new org.openspcoop2.core.config.model.InvocazionePortaModel(new ComplexField(father,"invocazione-porta",org.openspcoop2.core.config.InvocazionePorta.class,"servizio-applicativo",ServizioApplicativo.class));
		this.INVOCAZIONE_SERVIZIO = new org.openspcoop2.core.config.model.InvocazioneServizioModel(new ComplexField(father,"invocazione-servizio",org.openspcoop2.core.config.InvocazioneServizio.class,"servizio-applicativo",ServizioApplicativo.class));
		this.RISPOSTA_ASINCRONA = new org.openspcoop2.core.config.model.RispostaAsincronaModel(new ComplexField(father,"risposta-asincrona",org.openspcoop2.core.config.RispostaAsincrona.class,"servizio-applicativo",ServizioApplicativo.class));
		this.PROPRIETA = new org.openspcoop2.core.config.model.ProprietaModel(new ComplexField(father,"proprieta",org.openspcoop2.core.config.Proprieta.class,"servizio-applicativo",ServizioApplicativo.class));
		this.PROTOCOL_PROPERTY = new org.openspcoop2.core.config.model.ProtocolPropertyModel(new ComplexField(father,"protocol-property",org.openspcoop2.core.config.ProtocolProperty.class,"servizio-applicativo",ServizioApplicativo.class));
		this.ID_SOGGETTO = new ComplexField(father,"id-soggetto",java.lang.Long.class,"servizio-applicativo",ServizioApplicativo.class);
		this.TIPO_SOGGETTO_PROPRIETARIO = new ComplexField(father,"tipo-soggetto-proprietario",java.lang.String.class,"servizio-applicativo",ServizioApplicativo.class);
		this.NOME_SOGGETTO_PROPRIETARIO = new ComplexField(father,"nome-soggetto-proprietario",java.lang.String.class,"servizio-applicativo",ServizioApplicativo.class);
		this.TIPOLOGIA_FRUIZIONE = new ComplexField(father,"tipologia-fruizione",java.lang.String.class,"servizio-applicativo",ServizioApplicativo.class);
		this.TIPOLOGIA_EROGAZIONE = new ComplexField(father,"tipologia-erogazione",java.lang.String.class,"servizio-applicativo",ServizioApplicativo.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"servizio-applicativo",ServizioApplicativo.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"servizio-applicativo",ServizioApplicativo.class);
		this.USE_AS_CLIENT = new ComplexField(father,"use-as-client",boolean.class,"servizio-applicativo",ServizioApplicativo.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"servizio-applicativo",ServizioApplicativo.class);
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"servizio-applicativo",ServizioApplicativo.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.InvocazionePortaModel INVOCAZIONE_PORTA = null;
	 
	public org.openspcoop2.core.config.model.InvocazioneServizioModel INVOCAZIONE_SERVIZIO = null;
	 
	public org.openspcoop2.core.config.model.RispostaAsincronaModel RISPOSTA_ASINCRONA = null;
	 
	public org.openspcoop2.core.config.model.ProprietaModel PROPRIETA = null;
	 
	public org.openspcoop2.core.config.model.ProtocolPropertyModel PROTOCOL_PROPERTY = null;
	 
	public IField ID_SOGGETTO = null;
	 
	public IField TIPO_SOGGETTO_PROPRIETARIO = null;
	 
	public IField NOME_SOGGETTO_PROPRIETARIO = null;
	 
	public IField TIPOLOGIA_FRUIZIONE = null;
	 
	public IField TIPOLOGIA_EROGAZIONE = null;
	 
	public IField NOME = null;
	 
	public IField TIPO = null;
	 
	public IField USE_AS_CLIENT = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 

	@Override
	public Class<ServizioApplicativo> getModeledClass(){
		return ServizioApplicativo.class;
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