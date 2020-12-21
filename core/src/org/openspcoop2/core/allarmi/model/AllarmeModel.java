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

import org.openspcoop2.core.allarmi.Allarme;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Allarme 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllarmeModel extends AbstractModel<Allarme> {

	public AllarmeModel(){
	
		super();
	
		this.NOME = new Field("nome",java.lang.String.class,"allarme",Allarme.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"allarme",Allarme.class);
		this.TIPO_ALLARME = new Field("tipo-allarme",java.lang.String.class,"allarme",Allarme.class);
		this.MAIL = new org.openspcoop2.core.allarmi.model.AllarmeMailModel(new Field("mail",org.openspcoop2.core.allarmi.AllarmeMail.class,"allarme",Allarme.class));
		this.SCRIPT = new org.openspcoop2.core.allarmi.model.AllarmeScriptModel(new Field("script",org.openspcoop2.core.allarmi.AllarmeScript.class,"allarme",Allarme.class));
		this.STATO_PRECEDENTE = new Field("stato-precedente",java.lang.Integer.class,"allarme",Allarme.class);
		this.STATO = new Field("stato",java.lang.Integer.class,"allarme",Allarme.class);
		this.DETTAGLIO_STATO = new Field("dettaglio-stato",java.lang.String.class,"allarme",Allarme.class);
		this.LASTTIMESTAMP_CREATE = new Field("lasttimestamp-create",java.util.Date.class,"allarme",Allarme.class);
		this.LASTTIMESTAMP_UPDATE = new Field("lasttimestamp-update",java.util.Date.class,"allarme",Allarme.class);
		this.ENABLED = new Field("enabled",java.lang.Integer.class,"allarme",Allarme.class);
		this.ACKNOWLEDGED = new Field("acknowledged",java.lang.Integer.class,"allarme",Allarme.class);
		this.TIPO_PERIODO = new Field("tipo-periodo",java.lang.String.class,"allarme",Allarme.class);
		this.PERIODO = new Field("periodo",java.lang.Integer.class,"allarme",Allarme.class);
		this.FILTRO = new org.openspcoop2.core.allarmi.model.AllarmeFiltroModel(new Field("filtro",org.openspcoop2.core.allarmi.AllarmeFiltro.class,"allarme",Allarme.class));
		this.GROUP_BY = new org.openspcoop2.core.allarmi.model.AllarmeRaggruppamentoModel(new Field("group-by",org.openspcoop2.core.allarmi.AllarmeRaggruppamento.class,"allarme",Allarme.class));
		this.ALLARME_PARAMETRO = new org.openspcoop2.core.allarmi.model.AllarmeParametroModel(new Field("allarme-parametro",org.openspcoop2.core.allarmi.AllarmeParametro.class,"allarme",Allarme.class));
	
	}
	
	public AllarmeModel(IField father){
	
		super(father);
	
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"allarme",Allarme.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"allarme",Allarme.class);
		this.TIPO_ALLARME = new ComplexField(father,"tipo-allarme",java.lang.String.class,"allarme",Allarme.class);
		this.MAIL = new org.openspcoop2.core.allarmi.model.AllarmeMailModel(new ComplexField(father,"mail",org.openspcoop2.core.allarmi.AllarmeMail.class,"allarme",Allarme.class));
		this.SCRIPT = new org.openspcoop2.core.allarmi.model.AllarmeScriptModel(new ComplexField(father,"script",org.openspcoop2.core.allarmi.AllarmeScript.class,"allarme",Allarme.class));
		this.STATO_PRECEDENTE = new ComplexField(father,"stato-precedente",java.lang.Integer.class,"allarme",Allarme.class);
		this.STATO = new ComplexField(father,"stato",java.lang.Integer.class,"allarme",Allarme.class);
		this.DETTAGLIO_STATO = new ComplexField(father,"dettaglio-stato",java.lang.String.class,"allarme",Allarme.class);
		this.LASTTIMESTAMP_CREATE = new ComplexField(father,"lasttimestamp-create",java.util.Date.class,"allarme",Allarme.class);
		this.LASTTIMESTAMP_UPDATE = new ComplexField(father,"lasttimestamp-update",java.util.Date.class,"allarme",Allarme.class);
		this.ENABLED = new ComplexField(father,"enabled",java.lang.Integer.class,"allarme",Allarme.class);
		this.ACKNOWLEDGED = new ComplexField(father,"acknowledged",java.lang.Integer.class,"allarme",Allarme.class);
		this.TIPO_PERIODO = new ComplexField(father,"tipo-periodo",java.lang.String.class,"allarme",Allarme.class);
		this.PERIODO = new ComplexField(father,"periodo",java.lang.Integer.class,"allarme",Allarme.class);
		this.FILTRO = new org.openspcoop2.core.allarmi.model.AllarmeFiltroModel(new ComplexField(father,"filtro",org.openspcoop2.core.allarmi.AllarmeFiltro.class,"allarme",Allarme.class));
		this.GROUP_BY = new org.openspcoop2.core.allarmi.model.AllarmeRaggruppamentoModel(new ComplexField(father,"group-by",org.openspcoop2.core.allarmi.AllarmeRaggruppamento.class,"allarme",Allarme.class));
		this.ALLARME_PARAMETRO = new org.openspcoop2.core.allarmi.model.AllarmeParametroModel(new ComplexField(father,"allarme-parametro",org.openspcoop2.core.allarmi.AllarmeParametro.class,"allarme",Allarme.class));
	
	}
	
	

	public IField NOME = null;
	 
	public IField TIPO = null;
	 
	public IField TIPO_ALLARME = null;
	 
	public org.openspcoop2.core.allarmi.model.AllarmeMailModel MAIL = null;
	 
	public org.openspcoop2.core.allarmi.model.AllarmeScriptModel SCRIPT = null;
	 
	public IField STATO_PRECEDENTE = null;
	 
	public IField STATO = null;
	 
	public IField DETTAGLIO_STATO = null;
	 
	public IField LASTTIMESTAMP_CREATE = null;
	 
	public IField LASTTIMESTAMP_UPDATE = null;
	 
	public IField ENABLED = null;
	 
	public IField ACKNOWLEDGED = null;
	 
	public IField TIPO_PERIODO = null;
	 
	public IField PERIODO = null;
	 
	public org.openspcoop2.core.allarmi.model.AllarmeFiltroModel FILTRO = null;
	 
	public org.openspcoop2.core.allarmi.model.AllarmeRaggruppamentoModel GROUP_BY = null;
	 
	public org.openspcoop2.core.allarmi.model.AllarmeParametroModel ALLARME_PARAMETRO = null;
	 

	@Override
	public Class<Allarme> getModeledClass(){
		return Allarme.class;
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