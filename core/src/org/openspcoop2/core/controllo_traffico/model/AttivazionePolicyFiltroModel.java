/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AttivazionePolicyFiltro 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AttivazionePolicyFiltroModel extends AbstractModel<AttivazionePolicyFiltro> {

	public AttivazionePolicyFiltroModel(){
	
		super();
	
		this.ENABLED = new Field("enabled",boolean.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.PROTOCOLLO = new Field("protocollo",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.RUOLO_PORTA = new Field("ruolo-porta",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.NOME_PORTA = new Field("nome-porta",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.TIPO_FRUITORE = new Field("tipo-fruitore",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.NOME_FRUITORE = new Field("nome-fruitore",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.RUOLO_FRUITORE = new Field("ruolo-fruitore",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.SERVIZIO_APPLICATIVO_FRUITORE = new Field("servizio-applicativo-fruitore",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.TIPO_EROGATORE = new Field("tipo-erogatore",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.NOME_EROGATORE = new Field("nome-erogatore",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.RUOLO_EROGATORE = new Field("ruolo-erogatore",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.SERVIZIO_APPLICATIVO_EROGATORE = new Field("servizio-applicativo-erogatore",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.TAG = new Field("tag",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.TIPO_SERVIZIO = new Field("tipo-servizio",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.NOME_SERVIZIO = new Field("nome-servizio",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.VERSIONE_SERVIZIO = new Field("versione-servizio",java.lang.Integer.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.AZIONE = new Field("azione",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.INFORMAZIONE_APPLICATIVA_ENABLED = new Field("informazione-applicativa-enabled",boolean.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.INFORMAZIONE_APPLICATIVA_TIPO = new Field("informazione-applicativa-tipo",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.INFORMAZIONE_APPLICATIVA_NOME = new Field("informazione-applicativa-nome",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.INFORMAZIONE_APPLICATIVA_VALORE = new Field("informazione-applicativa-valore",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
	
	}
	
	public AttivazionePolicyFiltroModel(IField father){
	
		super(father);
	
		this.ENABLED = new ComplexField(father,"enabled",boolean.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.PROTOCOLLO = new ComplexField(father,"protocollo",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.RUOLO_PORTA = new ComplexField(father,"ruolo-porta",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.NOME_PORTA = new ComplexField(father,"nome-porta",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.TIPO_FRUITORE = new ComplexField(father,"tipo-fruitore",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.NOME_FRUITORE = new ComplexField(father,"nome-fruitore",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.RUOLO_FRUITORE = new ComplexField(father,"ruolo-fruitore",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.SERVIZIO_APPLICATIVO_FRUITORE = new ComplexField(father,"servizio-applicativo-fruitore",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.TIPO_EROGATORE = new ComplexField(father,"tipo-erogatore",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.NOME_EROGATORE = new ComplexField(father,"nome-erogatore",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.RUOLO_EROGATORE = new ComplexField(father,"ruolo-erogatore",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.SERVIZIO_APPLICATIVO_EROGATORE = new ComplexField(father,"servizio-applicativo-erogatore",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.TAG = new ComplexField(father,"tag",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.TIPO_SERVIZIO = new ComplexField(father,"tipo-servizio",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.NOME_SERVIZIO = new ComplexField(father,"nome-servizio",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.VERSIONE_SERVIZIO = new ComplexField(father,"versione-servizio",java.lang.Integer.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.AZIONE = new ComplexField(father,"azione",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.INFORMAZIONE_APPLICATIVA_ENABLED = new ComplexField(father,"informazione-applicativa-enabled",boolean.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.INFORMAZIONE_APPLICATIVA_TIPO = new ComplexField(father,"informazione-applicativa-tipo",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.INFORMAZIONE_APPLICATIVA_NOME = new ComplexField(father,"informazione-applicativa-nome",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
		this.INFORMAZIONE_APPLICATIVA_VALORE = new ComplexField(father,"informazione-applicativa-valore",java.lang.String.class,"attivazione-policy-filtro",AttivazionePolicyFiltro.class);
	
	}
	
	

	public IField ENABLED = null;
	 
	public IField PROTOCOLLO = null;
	 
	public IField RUOLO_PORTA = null;
	 
	public IField NOME_PORTA = null;
	 
	public IField TIPO_FRUITORE = null;
	 
	public IField NOME_FRUITORE = null;
	 
	public IField RUOLO_FRUITORE = null;
	 
	public IField SERVIZIO_APPLICATIVO_FRUITORE = null;
	 
	public IField TIPO_EROGATORE = null;
	 
	public IField NOME_EROGATORE = null;
	 
	public IField RUOLO_EROGATORE = null;
	 
	public IField SERVIZIO_APPLICATIVO_EROGATORE = null;
	 
	public IField TAG = null;
	 
	public IField TIPO_SERVIZIO = null;
	 
	public IField NOME_SERVIZIO = null;
	 
	public IField VERSIONE_SERVIZIO = null;
	 
	public IField AZIONE = null;
	 
	public IField INFORMAZIONE_APPLICATIVA_ENABLED = null;
	 
	public IField INFORMAZIONE_APPLICATIVA_TIPO = null;
	 
	public IField INFORMAZIONE_APPLICATIVA_NOME = null;
	 
	public IField INFORMAZIONE_APPLICATIVA_VALORE = null;
	 

	@Override
	public Class<AttivazionePolicyFiltro> getModeledClass(){
		return AttivazionePolicyFiltro.class;
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