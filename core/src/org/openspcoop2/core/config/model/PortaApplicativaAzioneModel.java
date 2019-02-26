/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import org.openspcoop2.core.config.PortaApplicativaAzione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PortaApplicativaAzione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaApplicativaAzioneModel extends AbstractModel<PortaApplicativaAzione> {

	public PortaApplicativaAzioneModel(){
	
		super();
	
		this.AZIONE_DELEGATA = new Field("azione-delegata",java.lang.String.class,"porta-applicativa-azione",PortaApplicativaAzione.class);
		this.IDENTIFICAZIONE = new Field("identificazione",java.lang.String.class,"porta-applicativa-azione",PortaApplicativaAzione.class);
		this.PATTERN = new Field("pattern",java.lang.String.class,"porta-applicativa-azione",PortaApplicativaAzione.class);
		this.NOME = new Field("nome",java.lang.String.class,"porta-applicativa-azione",PortaApplicativaAzione.class);
		this.NOME_PORTA_DELEGANTE = new Field("nome-porta-delegante",java.lang.String.class,"porta-applicativa-azione",PortaApplicativaAzione.class);
		this.FORCE_INTERFACE_BASED = new Field("force-interface-based",java.lang.String.class,"porta-applicativa-azione",PortaApplicativaAzione.class);
	
	}
	
	public PortaApplicativaAzioneModel(IField father){
	
		super(father);
	
		this.AZIONE_DELEGATA = new ComplexField(father,"azione-delegata",java.lang.String.class,"porta-applicativa-azione",PortaApplicativaAzione.class);
		this.IDENTIFICAZIONE = new ComplexField(father,"identificazione",java.lang.String.class,"porta-applicativa-azione",PortaApplicativaAzione.class);
		this.PATTERN = new ComplexField(father,"pattern",java.lang.String.class,"porta-applicativa-azione",PortaApplicativaAzione.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"porta-applicativa-azione",PortaApplicativaAzione.class);
		this.NOME_PORTA_DELEGANTE = new ComplexField(father,"nome-porta-delegante",java.lang.String.class,"porta-applicativa-azione",PortaApplicativaAzione.class);
		this.FORCE_INTERFACE_BASED = new ComplexField(father,"force-interface-based",java.lang.String.class,"porta-applicativa-azione",PortaApplicativaAzione.class);
	
	}
	
	

	public IField AZIONE_DELEGATA = null;
	 
	public IField IDENTIFICAZIONE = null;
	 
	public IField PATTERN = null;
	 
	public IField NOME = null;
	 
	public IField NOME_PORTA_DELEGANTE = null;
	 
	public IField FORCE_INTERFACE_BASED = null;
	 

	@Override
	public Class<PortaApplicativaAzione> getModeledClass(){
		return PortaApplicativaAzione.class;
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