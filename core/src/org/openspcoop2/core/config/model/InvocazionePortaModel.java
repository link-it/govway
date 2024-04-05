/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.InvocazionePorta;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model InvocazionePorta 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InvocazionePortaModel extends AbstractModel<InvocazionePorta> {

	public InvocazionePortaModel(){
	
		super();
	
		this.CREDENZIALI = new org.openspcoop2.core.config.model.CredenzialiModel(new Field("credenziali",org.openspcoop2.core.config.Credenziali.class,"invocazione-porta",InvocazionePorta.class));
		this.RUOLI = new org.openspcoop2.core.config.model.ServizioApplicativoRuoliModel(new Field("ruoli",org.openspcoop2.core.config.ServizioApplicativoRuoli.class,"invocazione-porta",InvocazionePorta.class));
		this.GESTIONE_ERRORE = new org.openspcoop2.core.config.model.InvocazionePortaGestioneErroreModel(new Field("gestione-errore",org.openspcoop2.core.config.InvocazionePortaGestioneErrore.class,"invocazione-porta",InvocazionePorta.class));
		this.INVIO_PER_RIFERIMENTO = new Field("invio-per-riferimento",java.lang.String.class,"invocazione-porta",InvocazionePorta.class);
		this.SBUSTAMENTO_INFORMAZIONI_PROTOCOLLO = new Field("sbustamento-informazioni-protocollo",java.lang.String.class,"invocazione-porta",InvocazionePorta.class);
	
	}
	
	public InvocazionePortaModel(IField father){
	
		super(father);
	
		this.CREDENZIALI = new org.openspcoop2.core.config.model.CredenzialiModel(new ComplexField(father,"credenziali",org.openspcoop2.core.config.Credenziali.class,"invocazione-porta",InvocazionePorta.class));
		this.RUOLI = new org.openspcoop2.core.config.model.ServizioApplicativoRuoliModel(new ComplexField(father,"ruoli",org.openspcoop2.core.config.ServizioApplicativoRuoli.class,"invocazione-porta",InvocazionePorta.class));
		this.GESTIONE_ERRORE = new org.openspcoop2.core.config.model.InvocazionePortaGestioneErroreModel(new ComplexField(father,"gestione-errore",org.openspcoop2.core.config.InvocazionePortaGestioneErrore.class,"invocazione-porta",InvocazionePorta.class));
		this.INVIO_PER_RIFERIMENTO = new ComplexField(father,"invio-per-riferimento",java.lang.String.class,"invocazione-porta",InvocazionePorta.class);
		this.SBUSTAMENTO_INFORMAZIONI_PROTOCOLLO = new ComplexField(father,"sbustamento-informazioni-protocollo",java.lang.String.class,"invocazione-porta",InvocazionePorta.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.CredenzialiModel CREDENZIALI = null;
	 
	public org.openspcoop2.core.config.model.ServizioApplicativoRuoliModel RUOLI = null;
	 
	public org.openspcoop2.core.config.model.InvocazionePortaGestioneErroreModel GESTIONE_ERRORE = null;
	 
	public IField INVIO_PER_RIFERIMENTO = null;
	 
	public IField SBUSTAMENTO_INFORMAZIONI_PROTOCOLLO = null;
	 

	@Override
	public Class<InvocazionePorta> getModeledClass(){
		return InvocazionePorta.class;
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