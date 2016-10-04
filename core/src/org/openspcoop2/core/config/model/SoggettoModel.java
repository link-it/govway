/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import org.openspcoop2.core.config.Soggetto;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Soggetto 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoggettoModel extends AbstractModel<Soggetto> {

	public SoggettoModel(){
	
		super();
	
		this.PORTA_DELEGATA = new org.openspcoop2.core.config.model.PortaDelegataModel(new Field("porta-delegata",org.openspcoop2.core.config.PortaDelegata.class,"soggetto",Soggetto.class));
		this.PORTA_APPLICATIVA = new org.openspcoop2.core.config.model.PortaApplicativaModel(new Field("porta-applicativa",org.openspcoop2.core.config.PortaApplicativa.class,"soggetto",Soggetto.class));
		this.SERVIZIO_APPLICATIVO = new org.openspcoop2.core.config.model.ServizioApplicativoModel(new Field("servizio-applicativo",org.openspcoop2.core.config.ServizioApplicativo.class,"soggetto",Soggetto.class));
		this.CONNETTORE = new org.openspcoop2.core.config.model.ConnettoreModel(new Field("connettore",org.openspcoop2.core.config.Connettore.class,"soggetto",Soggetto.class));
		this.SUPER_USER = new Field("super-user",java.lang.String.class,"soggetto",Soggetto.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"soggetto",Soggetto.class);
		this.NOME = new Field("nome",java.lang.String.class,"soggetto",Soggetto.class);
		this.IDENTIFICATIVO_PORTA = new Field("identificativo-porta",java.lang.String.class,"soggetto",Soggetto.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"soggetto",Soggetto.class);
		this.ROUTER = new Field("router",boolean.class,"soggetto",Soggetto.class);
		this.PD_URL_PREFIX_REWRITER = new Field("pd-url-prefix-rewriter",java.lang.String.class,"soggetto",Soggetto.class);
		this.PA_URL_PREFIX_REWRITER = new Field("pa-url-prefix-rewriter",java.lang.String.class,"soggetto",Soggetto.class);
		this.ORA_REGISTRAZIONE = new Field("ora-registrazione",java.util.Date.class,"soggetto",Soggetto.class);
	
	}
	
	public SoggettoModel(IField father){
	
		super(father);
	
		this.PORTA_DELEGATA = new org.openspcoop2.core.config.model.PortaDelegataModel(new ComplexField(father,"porta-delegata",org.openspcoop2.core.config.PortaDelegata.class,"soggetto",Soggetto.class));
		this.PORTA_APPLICATIVA = new org.openspcoop2.core.config.model.PortaApplicativaModel(new ComplexField(father,"porta-applicativa",org.openspcoop2.core.config.PortaApplicativa.class,"soggetto",Soggetto.class));
		this.SERVIZIO_APPLICATIVO = new org.openspcoop2.core.config.model.ServizioApplicativoModel(new ComplexField(father,"servizio-applicativo",org.openspcoop2.core.config.ServizioApplicativo.class,"soggetto",Soggetto.class));
		this.CONNETTORE = new org.openspcoop2.core.config.model.ConnettoreModel(new ComplexField(father,"connettore",org.openspcoop2.core.config.Connettore.class,"soggetto",Soggetto.class));
		this.SUPER_USER = new ComplexField(father,"super-user",java.lang.String.class,"soggetto",Soggetto.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"soggetto",Soggetto.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"soggetto",Soggetto.class);
		this.IDENTIFICATIVO_PORTA = new ComplexField(father,"identificativo-porta",java.lang.String.class,"soggetto",Soggetto.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"soggetto",Soggetto.class);
		this.ROUTER = new ComplexField(father,"router",boolean.class,"soggetto",Soggetto.class);
		this.PD_URL_PREFIX_REWRITER = new ComplexField(father,"pd-url-prefix-rewriter",java.lang.String.class,"soggetto",Soggetto.class);
		this.PA_URL_PREFIX_REWRITER = new ComplexField(father,"pa-url-prefix-rewriter",java.lang.String.class,"soggetto",Soggetto.class);
		this.ORA_REGISTRAZIONE = new ComplexField(father,"ora-registrazione",java.util.Date.class,"soggetto",Soggetto.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.PortaDelegataModel PORTA_DELEGATA = null;
	 
	public org.openspcoop2.core.config.model.PortaApplicativaModel PORTA_APPLICATIVA = null;
	 
	public org.openspcoop2.core.config.model.ServizioApplicativoModel SERVIZIO_APPLICATIVO = null;
	 
	public org.openspcoop2.core.config.model.ConnettoreModel CONNETTORE = null;
	 
	public IField SUPER_USER = null;
	 
	public IField TIPO = null;
	 
	public IField NOME = null;
	 
	public IField IDENTIFICATIVO_PORTA = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField ROUTER = null;
	 
	public IField PD_URL_PREFIX_REWRITER = null;
	 
	public IField PA_URL_PREFIX_REWRITER = null;
	 
	public IField ORA_REGISTRAZIONE = null;
	 

	@Override
	public Class<Soggetto> getModeledClass(){
		return Soggetto.class;
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