/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AttivazionePolicy 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AttivazionePolicyModel extends AbstractModel<AttivazionePolicy> {

	public AttivazionePolicyModel(){
	
		super();
	
		this.ID_ACTIVE_POLICY = new Field("id-active-policy",java.lang.String.class,"attivazione-policy",AttivazionePolicy.class);
		this.ALIAS = new Field("alias",java.lang.String.class,"attivazione-policy",AttivazionePolicy.class);
		this.UPDATE_TIME = new Field("update-time",java.util.Date.class,"attivazione-policy",AttivazionePolicy.class);
		this.ID_POLICY = new Field("id-policy",java.lang.String.class,"attivazione-policy",AttivazionePolicy.class);
		this.ENABLED = new Field("enabled",boolean.class,"attivazione-policy",AttivazionePolicy.class);
		this.WARNING_ONLY = new Field("warning-only",boolean.class,"attivazione-policy",AttivazionePolicy.class);
		this.RIDEFINISCI = new Field("ridefinisci",boolean.class,"attivazione-policy",AttivazionePolicy.class);
		this.VALORE = new Field("valore",java.lang.Long.class,"attivazione-policy",AttivazionePolicy.class);
		this.FILTRO = new org.openspcoop2.core.controllo_traffico.model.AttivazionePolicyFiltroModel(new Field("filtro",org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro.class,"attivazione-policy",AttivazionePolicy.class));
		this.GROUP_BY = new org.openspcoop2.core.controllo_traffico.model.AttivazionePolicyRaggruppamentoModel(new Field("group-by",org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento.class,"attivazione-policy",AttivazionePolicy.class));
	
	}
	
	public AttivazionePolicyModel(IField father){
	
		super(father);
	
		this.ID_ACTIVE_POLICY = new ComplexField(father,"id-active-policy",java.lang.String.class,"attivazione-policy",AttivazionePolicy.class);
		this.ALIAS = new ComplexField(father,"alias",java.lang.String.class,"attivazione-policy",AttivazionePolicy.class);
		this.UPDATE_TIME = new ComplexField(father,"update-time",java.util.Date.class,"attivazione-policy",AttivazionePolicy.class);
		this.ID_POLICY = new ComplexField(father,"id-policy",java.lang.String.class,"attivazione-policy",AttivazionePolicy.class);
		this.ENABLED = new ComplexField(father,"enabled",boolean.class,"attivazione-policy",AttivazionePolicy.class);
		this.WARNING_ONLY = new ComplexField(father,"warning-only",boolean.class,"attivazione-policy",AttivazionePolicy.class);
		this.RIDEFINISCI = new ComplexField(father,"ridefinisci",boolean.class,"attivazione-policy",AttivazionePolicy.class);
		this.VALORE = new ComplexField(father,"valore",java.lang.Long.class,"attivazione-policy",AttivazionePolicy.class);
		this.FILTRO = new org.openspcoop2.core.controllo_traffico.model.AttivazionePolicyFiltroModel(new ComplexField(father,"filtro",org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro.class,"attivazione-policy",AttivazionePolicy.class));
		this.GROUP_BY = new org.openspcoop2.core.controllo_traffico.model.AttivazionePolicyRaggruppamentoModel(new ComplexField(father,"group-by",org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento.class,"attivazione-policy",AttivazionePolicy.class));
	
	}
	
	

	public IField ID_ACTIVE_POLICY = null;
	 
	public IField ALIAS = null;
	 
	public IField UPDATE_TIME = null;
	 
	public IField ID_POLICY = null;
	 
	public IField ENABLED = null;
	 
	public IField WARNING_ONLY = null;
	 
	public IField RIDEFINISCI = null;
	 
	public IField VALORE = null;
	 
	public org.openspcoop2.core.controllo_traffico.model.AttivazionePolicyFiltroModel FILTRO = null;
	 
	public org.openspcoop2.core.controllo_traffico.model.AttivazionePolicyRaggruppamentoModel GROUP_BY = null;
	 

	@Override
	public Class<AttivazionePolicy> getModeledClass(){
		return AttivazionePolicy.class;
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