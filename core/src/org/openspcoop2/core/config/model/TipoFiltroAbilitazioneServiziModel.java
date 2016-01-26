/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

import org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model TipoFiltroAbilitazioneServizi 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TipoFiltroAbilitazioneServiziModel extends AbstractModel<TipoFiltroAbilitazioneServizi> {

	public TipoFiltroAbilitazioneServiziModel(){
	
		super();
	
		this.TIPO_SOGGETTO_FRUITORE = new Field("tipo-soggetto-fruitore",java.lang.String.class,"tipo-filtro-abilitazione-servizi",TipoFiltroAbilitazioneServizi.class);
		this.SOGGETTO_FRUITORE = new Field("soggetto-fruitore",java.lang.String.class,"tipo-filtro-abilitazione-servizi",TipoFiltroAbilitazioneServizi.class);
		this.IDENTIFICATIVO_PORTA_FRUITORE = new Field("identificativo-porta-fruitore",java.lang.String.class,"tipo-filtro-abilitazione-servizi",TipoFiltroAbilitazioneServizi.class);
		this.TIPO_SOGGETTO_EROGATORE = new Field("tipo-soggetto-erogatore",java.lang.String.class,"tipo-filtro-abilitazione-servizi",TipoFiltroAbilitazioneServizi.class);
		this.SOGGETTO_EROGATORE = new Field("soggetto-erogatore",java.lang.String.class,"tipo-filtro-abilitazione-servizi",TipoFiltroAbilitazioneServizi.class);
		this.IDENTIFICATIVO_PORTA_EROGATORE = new Field("identificativo-porta-erogatore",java.lang.String.class,"tipo-filtro-abilitazione-servizi",TipoFiltroAbilitazioneServizi.class);
		this.TIPO_SERVIZIO = new Field("tipo-servizio",java.lang.String.class,"tipo-filtro-abilitazione-servizi",TipoFiltroAbilitazioneServizi.class);
		this.SERVIZIO = new Field("servizio",java.lang.String.class,"tipo-filtro-abilitazione-servizi",TipoFiltroAbilitazioneServizi.class);
		this.AZIONE = new Field("azione",java.lang.String.class,"tipo-filtro-abilitazione-servizi",TipoFiltroAbilitazioneServizi.class);
	
	}
	
	public TipoFiltroAbilitazioneServiziModel(IField father){
	
		super(father);
	
		this.TIPO_SOGGETTO_FRUITORE = new ComplexField(father,"tipo-soggetto-fruitore",java.lang.String.class,"tipo-filtro-abilitazione-servizi",TipoFiltroAbilitazioneServizi.class);
		this.SOGGETTO_FRUITORE = new ComplexField(father,"soggetto-fruitore",java.lang.String.class,"tipo-filtro-abilitazione-servizi",TipoFiltroAbilitazioneServizi.class);
		this.IDENTIFICATIVO_PORTA_FRUITORE = new ComplexField(father,"identificativo-porta-fruitore",java.lang.String.class,"tipo-filtro-abilitazione-servizi",TipoFiltroAbilitazioneServizi.class);
		this.TIPO_SOGGETTO_EROGATORE = new ComplexField(father,"tipo-soggetto-erogatore",java.lang.String.class,"tipo-filtro-abilitazione-servizi",TipoFiltroAbilitazioneServizi.class);
		this.SOGGETTO_EROGATORE = new ComplexField(father,"soggetto-erogatore",java.lang.String.class,"tipo-filtro-abilitazione-servizi",TipoFiltroAbilitazioneServizi.class);
		this.IDENTIFICATIVO_PORTA_EROGATORE = new ComplexField(father,"identificativo-porta-erogatore",java.lang.String.class,"tipo-filtro-abilitazione-servizi",TipoFiltroAbilitazioneServizi.class);
		this.TIPO_SERVIZIO = new ComplexField(father,"tipo-servizio",java.lang.String.class,"tipo-filtro-abilitazione-servizi",TipoFiltroAbilitazioneServizi.class);
		this.SERVIZIO = new ComplexField(father,"servizio",java.lang.String.class,"tipo-filtro-abilitazione-servizi",TipoFiltroAbilitazioneServizi.class);
		this.AZIONE = new ComplexField(father,"azione",java.lang.String.class,"tipo-filtro-abilitazione-servizi",TipoFiltroAbilitazioneServizi.class);
	
	}
	
	

	public IField TIPO_SOGGETTO_FRUITORE = null;
	 
	public IField SOGGETTO_FRUITORE = null;
	 
	public IField IDENTIFICATIVO_PORTA_FRUITORE = null;
	 
	public IField TIPO_SOGGETTO_EROGATORE = null;
	 
	public IField SOGGETTO_EROGATORE = null;
	 
	public IField IDENTIFICATIVO_PORTA_EROGATORE = null;
	 
	public IField TIPO_SERVIZIO = null;
	 
	public IField SERVIZIO = null;
	 
	public IField AZIONE = null;
	 

	@Override
	public Class<TipoFiltroAbilitazioneServizi> getModeledClass(){
		return TipoFiltroAbilitazioneServizi.class;
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