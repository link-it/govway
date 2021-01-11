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
package org.openspcoop2.monitor.engine.config.base.model;

import org.openspcoop2.monitor.engine.config.base.ConfigurazioneFiltro;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ConfigurazioneFiltro 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneFiltroModel extends AbstractModel<ConfigurazioneFiltro> {

	public ConfigurazioneFiltroModel(){
	
		super();
	
		this.NOME = new Field("nome",java.lang.String.class,"configurazione-filtro",ConfigurazioneFiltro.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"configurazione-filtro",ConfigurazioneFiltro.class);
		this.TIPO_MITTENTE = new Field("tipo-mittente",java.lang.String.class,"configurazione-filtro",ConfigurazioneFiltro.class);
		this.NOME_MITTENTE = new Field("nome-mittente",java.lang.String.class,"configurazione-filtro",ConfigurazioneFiltro.class);
		this.IDPORTA_MITTENTE = new Field("idporta-mittente",java.lang.String.class,"configurazione-filtro",ConfigurazioneFiltro.class);
		this.TIPO_DESTINATARIO = new Field("tipo-destinatario",java.lang.String.class,"configurazione-filtro",ConfigurazioneFiltro.class);
		this.NOME_DESTINATARIO = new Field("nome-destinatario",java.lang.String.class,"configurazione-filtro",ConfigurazioneFiltro.class);
		this.IDPORTA_DESTINATARIO = new Field("idporta-destinatario",java.lang.String.class,"configurazione-filtro",ConfigurazioneFiltro.class);
		this.TIPO_SERVIZIO = new Field("tipo-servizio",java.lang.String.class,"configurazione-filtro",ConfigurazioneFiltro.class);
		this.NOME_SERVIZIO = new Field("nome-servizio",java.lang.String.class,"configurazione-filtro",ConfigurazioneFiltro.class);
		this.VERSIONE_SERVIZIO = new Field("versione-servizio",java.lang.Integer.class,"configurazione-filtro",ConfigurazioneFiltro.class);
		this.AZIONE = new Field("azione",java.lang.String.class,"configurazione-filtro",ConfigurazioneFiltro.class);
	
	}
	
	public ConfigurazioneFiltroModel(IField father){
	
		super(father);
	
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"configurazione-filtro",ConfigurazioneFiltro.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"configurazione-filtro",ConfigurazioneFiltro.class);
		this.TIPO_MITTENTE = new ComplexField(father,"tipo-mittente",java.lang.String.class,"configurazione-filtro",ConfigurazioneFiltro.class);
		this.NOME_MITTENTE = new ComplexField(father,"nome-mittente",java.lang.String.class,"configurazione-filtro",ConfigurazioneFiltro.class);
		this.IDPORTA_MITTENTE = new ComplexField(father,"idporta-mittente",java.lang.String.class,"configurazione-filtro",ConfigurazioneFiltro.class);
		this.TIPO_DESTINATARIO = new ComplexField(father,"tipo-destinatario",java.lang.String.class,"configurazione-filtro",ConfigurazioneFiltro.class);
		this.NOME_DESTINATARIO = new ComplexField(father,"nome-destinatario",java.lang.String.class,"configurazione-filtro",ConfigurazioneFiltro.class);
		this.IDPORTA_DESTINATARIO = new ComplexField(father,"idporta-destinatario",java.lang.String.class,"configurazione-filtro",ConfigurazioneFiltro.class);
		this.TIPO_SERVIZIO = new ComplexField(father,"tipo-servizio",java.lang.String.class,"configurazione-filtro",ConfigurazioneFiltro.class);
		this.NOME_SERVIZIO = new ComplexField(father,"nome-servizio",java.lang.String.class,"configurazione-filtro",ConfigurazioneFiltro.class);
		this.VERSIONE_SERVIZIO = new ComplexField(father,"versione-servizio",java.lang.Integer.class,"configurazione-filtro",ConfigurazioneFiltro.class);
		this.AZIONE = new ComplexField(father,"azione",java.lang.String.class,"configurazione-filtro",ConfigurazioneFiltro.class);
	
	}
	
	

	public IField NOME = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField TIPO_MITTENTE = null;
	 
	public IField NOME_MITTENTE = null;
	 
	public IField IDPORTA_MITTENTE = null;
	 
	public IField TIPO_DESTINATARIO = null;
	 
	public IField NOME_DESTINATARIO = null;
	 
	public IField IDPORTA_DESTINATARIO = null;
	 
	public IField TIPO_SERVIZIO = null;
	 
	public IField NOME_SERVIZIO = null;
	 
	public IField VERSIONE_SERVIZIO = null;
	 
	public IField AZIONE = null;
	 

	@Override
	public Class<ConfigurazioneFiltro> getModeledClass(){
		return ConfigurazioneFiltro.class;
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