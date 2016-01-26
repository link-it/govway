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
package org.openspcoop2.protocol.manifest.model;

import org.openspcoop2.protocol.manifest.Funzionalita;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Funzionalita 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FunzionalitaModel extends AbstractModel<Funzionalita> {

	public FunzionalitaModel(){
	
		super();
	
		this.FILTRO_DUPLICATI = new Field("filtroDuplicati",boolean.class,"funzionalita",Funzionalita.class);
		this.CONFERMA_RICEZIONE = new Field("confermaRicezione",boolean.class,"funzionalita",Funzionalita.class);
		this.COLLABORAZIONE = new Field("collaborazione",boolean.class,"funzionalita",Funzionalita.class);
		this.CONSEGNA_IN_ORDINE = new Field("consegnaInOrdine",boolean.class,"funzionalita",Funzionalita.class);
		this.SCADENZA = new Field("scadenza",boolean.class,"funzionalita",Funzionalita.class);
		this.MANIFEST_ATTACHMENTS = new Field("manifestAttachments",boolean.class,"funzionalita",Funzionalita.class);
	
	}
	
	public FunzionalitaModel(IField father){
	
		super(father);
	
		this.FILTRO_DUPLICATI = new ComplexField(father,"filtroDuplicati",boolean.class,"funzionalita",Funzionalita.class);
		this.CONFERMA_RICEZIONE = new ComplexField(father,"confermaRicezione",boolean.class,"funzionalita",Funzionalita.class);
		this.COLLABORAZIONE = new ComplexField(father,"collaborazione",boolean.class,"funzionalita",Funzionalita.class);
		this.CONSEGNA_IN_ORDINE = new ComplexField(father,"consegnaInOrdine",boolean.class,"funzionalita",Funzionalita.class);
		this.SCADENZA = new ComplexField(father,"scadenza",boolean.class,"funzionalita",Funzionalita.class);
		this.MANIFEST_ATTACHMENTS = new ComplexField(father,"manifestAttachments",boolean.class,"funzionalita",Funzionalita.class);
	
	}
	
	

	public IField FILTRO_DUPLICATI = null;
	 
	public IField CONFERMA_RICEZIONE = null;
	 
	public IField COLLABORAZIONE = null;
	 
	public IField CONSEGNA_IN_ORDINE = null;
	 
	public IField SCADENZA = null;
	 
	public IField MANIFEST_ATTACHMENTS = null;
	 

	@Override
	public Class<Funzionalita> getModeledClass(){
		return Funzionalita.class;
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