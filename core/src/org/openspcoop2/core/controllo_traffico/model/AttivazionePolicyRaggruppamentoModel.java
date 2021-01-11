/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.core.controllo_traffico.model;

import org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AttivazionePolicyRaggruppamento 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AttivazionePolicyRaggruppamentoModel extends AbstractModel<AttivazionePolicyRaggruppamento> {

	public AttivazionePolicyRaggruppamentoModel(){
	
		super();
	
		this.ENABLED = new Field("enabled",boolean.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.RUOLO_PORTA = new Field("ruolo-porta",boolean.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.PROTOCOLLO = new Field("protocollo",boolean.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.FRUITORE = new Field("fruitore",boolean.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.SERVIZIO_APPLICATIVO_FRUITORE = new Field("servizio-applicativo-fruitore",boolean.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.IDENTIFICATIVO_AUTENTICATO = new Field("identificativo-autenticato",boolean.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.TOKEN = new Field("token",java.lang.String.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.EROGATORE = new Field("erogatore",boolean.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.SERVIZIO_APPLICATIVO_EROGATORE = new Field("servizio-applicativo-erogatore",boolean.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.SERVIZIO = new Field("servizio",boolean.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.AZIONE = new Field("azione",boolean.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.INFORMAZIONE_APPLICATIVA_ENABLED = new Field("informazione-applicativa-enabled",boolean.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.INFORMAZIONE_APPLICATIVA_TIPO = new Field("informazione-applicativa-tipo",java.lang.String.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.INFORMAZIONE_APPLICATIVA_NOME = new Field("informazione-applicativa-nome",java.lang.String.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
	
	}
	
	public AttivazionePolicyRaggruppamentoModel(IField father){
	
		super(father);
	
		this.ENABLED = new ComplexField(father,"enabled",boolean.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.RUOLO_PORTA = new ComplexField(father,"ruolo-porta",boolean.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.PROTOCOLLO = new ComplexField(father,"protocollo",boolean.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.FRUITORE = new ComplexField(father,"fruitore",boolean.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.SERVIZIO_APPLICATIVO_FRUITORE = new ComplexField(father,"servizio-applicativo-fruitore",boolean.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.IDENTIFICATIVO_AUTENTICATO = new ComplexField(father,"identificativo-autenticato",boolean.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.TOKEN = new ComplexField(father,"token",java.lang.String.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.EROGATORE = new ComplexField(father,"erogatore",boolean.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.SERVIZIO_APPLICATIVO_EROGATORE = new ComplexField(father,"servizio-applicativo-erogatore",boolean.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.SERVIZIO = new ComplexField(father,"servizio",boolean.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.AZIONE = new ComplexField(father,"azione",boolean.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.INFORMAZIONE_APPLICATIVA_ENABLED = new ComplexField(father,"informazione-applicativa-enabled",boolean.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.INFORMAZIONE_APPLICATIVA_TIPO = new ComplexField(father,"informazione-applicativa-tipo",java.lang.String.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
		this.INFORMAZIONE_APPLICATIVA_NOME = new ComplexField(father,"informazione-applicativa-nome",java.lang.String.class,"attivazione-policy-raggruppamento",AttivazionePolicyRaggruppamento.class);
	
	}
	
	

	public IField ENABLED = null;
	 
	public IField RUOLO_PORTA = null;
	 
	public IField PROTOCOLLO = null;
	 
	public IField FRUITORE = null;
	 
	public IField SERVIZIO_APPLICATIVO_FRUITORE = null;
	 
	public IField IDENTIFICATIVO_AUTENTICATO = null;
	 
	public IField TOKEN = null;
	 
	public IField EROGATORE = null;
	 
	public IField SERVIZIO_APPLICATIVO_EROGATORE = null;
	 
	public IField SERVIZIO = null;
	 
	public IField AZIONE = null;
	 
	public IField INFORMAZIONE_APPLICATIVA_ENABLED = null;
	 
	public IField INFORMAZIONE_APPLICATIVA_TIPO = null;
	 
	public IField INFORMAZIONE_APPLICATIVA_NOME = null;
	 

	@Override
	public Class<AttivazionePolicyRaggruppamento> getModeledClass(){
		return AttivazionePolicyRaggruppamento.class;
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