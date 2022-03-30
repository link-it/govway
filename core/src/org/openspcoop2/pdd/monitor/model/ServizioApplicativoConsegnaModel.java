/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.monitor.model;

import org.openspcoop2.pdd.monitor.ServizioApplicativoConsegna;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ServizioApplicativoConsegna 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServizioApplicativoConsegnaModel extends AbstractModel<ServizioApplicativoConsegna> {

	public ServizioApplicativoConsegnaModel(){
	
		super();
	
		this.AUTORIZZAZIONE_INTEGRATION_MANAGER = new Field("autorizzazione-integration-manager",boolean.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.ERRORE_PROCESSAMENTO = new Field("errore-processamento",java.lang.String.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.NOME = new Field("nome",java.lang.String.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.SBUSTAMENTO_SOAP = new Field("sbustamento-soap",boolean.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.SBUSTAMENTO_INFORMAZIONI_PROTOCOLLO = new Field("sbustamento-informazioni-protocollo",boolean.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.TIPO_CONSEGNA = new Field("tipo-consegna",java.lang.String.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.DATA_RISPEDIZIONE = new Field("data-rispedizione",java.util.Date.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.NOME_PORTA = new Field("nome-porta",java.lang.String.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.CODA = new Field("coda",java.lang.String.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.PRIORITA = new Field("priorita",java.lang.String.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.ATTESA_ESITO = new Field("attesa-esito",boolean.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
	
	}
	
	public ServizioApplicativoConsegnaModel(IField father){
	
		super(father);
	
		this.AUTORIZZAZIONE_INTEGRATION_MANAGER = new ComplexField(father,"autorizzazione-integration-manager",boolean.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.ERRORE_PROCESSAMENTO = new ComplexField(father,"errore-processamento",java.lang.String.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.SBUSTAMENTO_SOAP = new ComplexField(father,"sbustamento-soap",boolean.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.SBUSTAMENTO_INFORMAZIONI_PROTOCOLLO = new ComplexField(father,"sbustamento-informazioni-protocollo",boolean.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.TIPO_CONSEGNA = new ComplexField(father,"tipo-consegna",java.lang.String.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.DATA_RISPEDIZIONE = new ComplexField(father,"data-rispedizione",java.util.Date.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.NOME_PORTA = new ComplexField(father,"nome-porta",java.lang.String.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.CODA = new ComplexField(father,"coda",java.lang.String.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.PRIORITA = new ComplexField(father,"priorita",java.lang.String.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
		this.ATTESA_ESITO = new ComplexField(father,"attesa-esito",boolean.class,"servizio-applicativo-consegna",ServizioApplicativoConsegna.class);
	
	}
	
	

	public IField AUTORIZZAZIONE_INTEGRATION_MANAGER = null;
	 
	public IField ERRORE_PROCESSAMENTO = null;
	 
	public IField NOME = null;
	 
	public IField SBUSTAMENTO_SOAP = null;
	 
	public IField SBUSTAMENTO_INFORMAZIONI_PROTOCOLLO = null;
	 
	public IField TIPO_CONSEGNA = null;
	 
	public IField DATA_RISPEDIZIONE = null;
	 
	public IField NOME_PORTA = null;
	 
	public IField CODA = null;
	 
	public IField PRIORITA = null;
	 
	public IField ATTESA_ESITO = null;
	 

	@Override
	public Class<ServizioApplicativoConsegna> getModeledClass(){
		return ServizioApplicativoConsegna.class;
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