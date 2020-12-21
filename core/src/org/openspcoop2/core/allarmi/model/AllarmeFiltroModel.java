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
package org.openspcoop2.core.allarmi.model;

import org.openspcoop2.core.allarmi.AllarmeFiltro;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AllarmeFiltro 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmeFiltroModel extends AbstractModel<AllarmeFiltro> {

	public AllarmeFiltroModel(){
	
		super();
	
		this.ENABLED = new Field("enabled",boolean.class,"allarme-filtro",AllarmeFiltro.class);
		this.PROTOCOLLO = new Field("protocollo",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.RUOLO_PORTA = new Field("ruolo-porta",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.NOME_PORTA = new Field("nome-porta",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.TIPO_FRUITORE = new Field("tipo-fruitore",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.NOME_FRUITORE = new Field("nome-fruitore",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.RUOLO_FRUITORE = new Field("ruolo-fruitore",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.SERVIZIO_APPLICATIVO_FRUITORE = new Field("servizio-applicativo-fruitore",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.TIPO_EROGATORE = new Field("tipo-erogatore",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.NOME_EROGATORE = new Field("nome-erogatore",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.RUOLO_EROGATORE = new Field("ruolo-erogatore",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.TAG = new Field("tag",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.TIPO_SERVIZIO = new Field("tipo-servizio",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.NOME_SERVIZIO = new Field("nome-servizio",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.VERSIONE_SERVIZIO = new Field("versione-servizio",java.lang.Integer.class,"allarme-filtro",AllarmeFiltro.class);
		this.AZIONE = new Field("azione",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
	
	}
	
	public AllarmeFiltroModel(IField father){
	
		super(father);
	
		this.ENABLED = new ComplexField(father,"enabled",boolean.class,"allarme-filtro",AllarmeFiltro.class);
		this.PROTOCOLLO = new ComplexField(father,"protocollo",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.RUOLO_PORTA = new ComplexField(father,"ruolo-porta",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.NOME_PORTA = new ComplexField(father,"nome-porta",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.TIPO_FRUITORE = new ComplexField(father,"tipo-fruitore",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.NOME_FRUITORE = new ComplexField(father,"nome-fruitore",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.RUOLO_FRUITORE = new ComplexField(father,"ruolo-fruitore",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.SERVIZIO_APPLICATIVO_FRUITORE = new ComplexField(father,"servizio-applicativo-fruitore",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.TIPO_EROGATORE = new ComplexField(father,"tipo-erogatore",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.NOME_EROGATORE = new ComplexField(father,"nome-erogatore",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.RUOLO_EROGATORE = new ComplexField(father,"ruolo-erogatore",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.TAG = new ComplexField(father,"tag",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.TIPO_SERVIZIO = new ComplexField(father,"tipo-servizio",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.NOME_SERVIZIO = new ComplexField(father,"nome-servizio",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
		this.VERSIONE_SERVIZIO = new ComplexField(father,"versione-servizio",java.lang.Integer.class,"allarme-filtro",AllarmeFiltro.class);
		this.AZIONE = new ComplexField(father,"azione",java.lang.String.class,"allarme-filtro",AllarmeFiltro.class);
	
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
	 
	public IField TAG = null;
	 
	public IField TIPO_SERVIZIO = null;
	 
	public IField NOME_SERVIZIO = null;
	 
	public IField VERSIONE_SERVIZIO = null;
	 
	public IField AZIONE = null;
	 

	@Override
	public Class<AllarmeFiltro> getModeledClass(){
		return AllarmeFiltro.class;
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