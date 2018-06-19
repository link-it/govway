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
package org.openspcoop2.core.commons.search.model;

import org.openspcoop2.core.commons.search.PortaDelegata;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model PortaDelegata 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PortaDelegataModel extends AbstractModel<PortaDelegata> {

	public PortaDelegataModel(){
	
		super();
	
		this.NOME = new Field("nome",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.STATO = new Field("stato",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.ID_SOGGETTO = new org.openspcoop2.core.commons.search.model.IdSoggettoModel(new Field("id-soggetto",org.openspcoop2.core.commons.search.IdSoggetto.class,"porta-delegata",PortaDelegata.class));
		this.TIPO_SOGGETTO_EROGATORE = new Field("tipo_soggetto_erogatore",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.NOME_SOGGETTO_EROGATORE = new Field("nome_soggetto_erogatore",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.TIPO_SERVIZIO = new Field("tipo_servizio",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.NOME_SERVIZIO = new Field("nome_servizio",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.VERSIONE_SERVIZIO = new Field("versione_servizio",java.lang.Integer.class,"porta-delegata",PortaDelegata.class);
		this.MODE_AZIONE = new Field("mode_azione",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.NOME_AZIONE = new Field("nome_azione",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.NOME_PORTA_DELEGANTE_AZIONE = new Field("nome_porta_delegante_azione",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.PORTA_DELEGATA_SERVIZIO_APPLICATIVO = new org.openspcoop2.core.commons.search.model.PortaDelegataServizioApplicativoModel(new Field("porta-delegata-servizio-applicativo",org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo.class,"porta-delegata",PortaDelegata.class));
		this.PORTA_DELEGATA_AZIONE = new org.openspcoop2.core.commons.search.model.PortaDelegataAzioneModel(new Field("porta-delegata-azione",org.openspcoop2.core.commons.search.PortaDelegataAzione.class,"porta-delegata",PortaDelegata.class));
	
	}
	
	public PortaDelegataModel(IField father){
	
		super(father);
	
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.STATO = new ComplexField(father,"stato",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.ID_SOGGETTO = new org.openspcoop2.core.commons.search.model.IdSoggettoModel(new ComplexField(father,"id-soggetto",org.openspcoop2.core.commons.search.IdSoggetto.class,"porta-delegata",PortaDelegata.class));
		this.TIPO_SOGGETTO_EROGATORE = new ComplexField(father,"tipo_soggetto_erogatore",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.NOME_SOGGETTO_EROGATORE = new ComplexField(father,"nome_soggetto_erogatore",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.TIPO_SERVIZIO = new ComplexField(father,"tipo_servizio",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.NOME_SERVIZIO = new ComplexField(father,"nome_servizio",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.VERSIONE_SERVIZIO = new ComplexField(father,"versione_servizio",java.lang.Integer.class,"porta-delegata",PortaDelegata.class);
		this.MODE_AZIONE = new ComplexField(father,"mode_azione",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.NOME_AZIONE = new ComplexField(father,"nome_azione",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.NOME_PORTA_DELEGANTE_AZIONE = new ComplexField(father,"nome_porta_delegante_azione",java.lang.String.class,"porta-delegata",PortaDelegata.class);
		this.PORTA_DELEGATA_SERVIZIO_APPLICATIVO = new org.openspcoop2.core.commons.search.model.PortaDelegataServizioApplicativoModel(new ComplexField(father,"porta-delegata-servizio-applicativo",org.openspcoop2.core.commons.search.PortaDelegataServizioApplicativo.class,"porta-delegata",PortaDelegata.class));
		this.PORTA_DELEGATA_AZIONE = new org.openspcoop2.core.commons.search.model.PortaDelegataAzioneModel(new ComplexField(father,"porta-delegata-azione",org.openspcoop2.core.commons.search.PortaDelegataAzione.class,"porta-delegata",PortaDelegata.class));
	
	}
	
	

	public IField NOME = null;
	 
	public IField STATO = null;
	 
	public org.openspcoop2.core.commons.search.model.IdSoggettoModel ID_SOGGETTO = null;
	 
	public IField TIPO_SOGGETTO_EROGATORE = null;
	 
	public IField NOME_SOGGETTO_EROGATORE = null;
	 
	public IField TIPO_SERVIZIO = null;
	 
	public IField NOME_SERVIZIO = null;
	 
	public IField VERSIONE_SERVIZIO = null;
	 
	public IField MODE_AZIONE = null;
	 
	public IField NOME_AZIONE = null;
	 
	public IField NOME_PORTA_DELEGANTE_AZIONE = null;
	 
	public org.openspcoop2.core.commons.search.model.PortaDelegataServizioApplicativoModel PORTA_DELEGATA_SERVIZIO_APPLICATIVO = null;
	 
	public org.openspcoop2.core.commons.search.model.PortaDelegataAzioneModel PORTA_DELEGATA_AZIONE = null;
	 

	@Override
	public Class<PortaDelegata> getModeledClass(){
		return PortaDelegata.class;
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