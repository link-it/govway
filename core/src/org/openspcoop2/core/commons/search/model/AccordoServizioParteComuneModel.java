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

import org.openspcoop2.core.commons.search.AccordoServizioParteComune;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AccordoServizioParteComune 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoServizioParteComuneModel extends AbstractModel<AccordoServizioParteComune> {

	public AccordoServizioParteComuneModel(){
	
		super();
	
		this.NOME = new Field("nome",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.VERSIONE = new Field("versione",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.ID_REFERENTE = new org.openspcoop2.core.commons.search.model.IdSoggettoModel(new Field("id-referente",org.openspcoop2.core.commons.search.IdSoggetto.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
		this.ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE = new org.openspcoop2.core.commons.search.model.AccordoServizioParteComuneAzioneModel(new Field("accordo-servizio-parte-comune-azione",org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
		this.PORT_TYPE = new org.openspcoop2.core.commons.search.model.PortTypeModel(new Field("port-type",org.openspcoop2.core.commons.search.PortType.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
	
	}
	
	public AccordoServizioParteComuneModel(IField father){
	
		super(father);
	
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.VERSIONE = new ComplexField(father,"versione",java.lang.String.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class);
		this.ID_REFERENTE = new org.openspcoop2.core.commons.search.model.IdSoggettoModel(new ComplexField(father,"id-referente",org.openspcoop2.core.commons.search.IdSoggetto.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
		this.ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE = new org.openspcoop2.core.commons.search.model.AccordoServizioParteComuneAzioneModel(new ComplexField(father,"accordo-servizio-parte-comune-azione",org.openspcoop2.core.commons.search.AccordoServizioParteComuneAzione.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
		this.PORT_TYPE = new org.openspcoop2.core.commons.search.model.PortTypeModel(new ComplexField(father,"port-type",org.openspcoop2.core.commons.search.PortType.class,"accordo-servizio-parte-comune",AccordoServizioParteComune.class));
	
	}
	
	

	public IField NOME = null;
	 
	public IField VERSIONE = null;
	 
	public org.openspcoop2.core.commons.search.model.IdSoggettoModel ID_REFERENTE = null;
	 
	public org.openspcoop2.core.commons.search.model.AccordoServizioParteComuneAzioneModel ACCORDO_SERVIZIO_PARTE_COMUNE_AZIONE = null;
	 
	public org.openspcoop2.core.commons.search.model.PortTypeModel PORT_TYPE = null;
	 

	@Override
	public Class<AccordoServizioParteComune> getModeledClass(){
		return AccordoServizioParteComune.class;
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