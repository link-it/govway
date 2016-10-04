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
package org.openspcoop2.core.tracciamento.model;

import org.openspcoop2.core.tracciamento.Busta;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Busta 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BustaModel extends AbstractModel<Busta> {

	public BustaModel(){
	
		super();
	
		this.MITTENTE = new org.openspcoop2.core.tracciamento.model.SoggettoModel(new Field("mittente",org.openspcoop2.core.tracciamento.Soggetto.class,"busta",Busta.class));
		this.DESTINATARIO = new org.openspcoop2.core.tracciamento.model.SoggettoModel(new Field("destinatario",org.openspcoop2.core.tracciamento.Soggetto.class,"busta",Busta.class));
		this.PROFILO_COLLABORAZIONE = new org.openspcoop2.core.tracciamento.model.ProfiloCollaborazioneModel(new Field("profilo-collaborazione",org.openspcoop2.core.tracciamento.ProfiloCollaborazione.class,"busta",Busta.class));
		this.SERVIZIO = new org.openspcoop2.core.tracciamento.model.ServizioModel(new Field("servizio",org.openspcoop2.core.tracciamento.Servizio.class,"busta",Busta.class));
		this.AZIONE = new Field("azione",java.lang.String.class,"busta",Busta.class);
		this.SERVIZIO_CORRELATO = new org.openspcoop2.core.tracciamento.model.ServizioModel(new Field("servizio-correlato",org.openspcoop2.core.tracciamento.Servizio.class,"busta",Busta.class));
		this.COLLABORAZIONE = new Field("collaborazione",java.lang.String.class,"busta",Busta.class);
		this.IDENTIFICATIVO = new Field("identificativo",java.lang.String.class,"busta",Busta.class);
		this.RIFERIMENTO_MESSAGGIO = new Field("riferimento-messaggio",java.lang.String.class,"busta",Busta.class);
		this.ORA_REGISTRAZIONE = new org.openspcoop2.core.tracciamento.model.DataModel(new Field("ora-registrazione",org.openspcoop2.core.tracciamento.Data.class,"busta",Busta.class));
		this.SCADENZA = new Field("scadenza",java.util.Date.class,"busta",Busta.class);
		this.PROFILO_TRASMISSIONE = new org.openspcoop2.core.tracciamento.model.ProfiloTrasmissioneModel(new Field("profilo-trasmissione",org.openspcoop2.core.tracciamento.ProfiloTrasmissione.class,"busta",Busta.class));
		this.SERVIZIO_APPLICATIVO_FRUITORE = new Field("servizio-applicativo-fruitore",java.lang.String.class,"busta",Busta.class);
		this.SERVIZIO_APPLICATIVO_EROGATORE = new Field("servizio-applicativo-erogatore",java.lang.String.class,"busta",Busta.class);
		this.DIGEST = new Field("digest",java.lang.String.class,"busta",Busta.class);
		this.TRASMISSIONI = new org.openspcoop2.core.tracciamento.model.TrasmissioniModel(new Field("trasmissioni",org.openspcoop2.core.tracciamento.Trasmissioni.class,"busta",Busta.class));
		this.RISCONTRI = new org.openspcoop2.core.tracciamento.model.RiscontriModel(new Field("riscontri",org.openspcoop2.core.tracciamento.Riscontri.class,"busta",Busta.class));
		this.ECCEZIONI = new org.openspcoop2.core.tracciamento.model.EccezioniModel(new Field("eccezioni",org.openspcoop2.core.tracciamento.Eccezioni.class,"busta",Busta.class));
		this.PROTOCOLLO = new org.openspcoop2.core.tracciamento.model.ProtocolloModel(new Field("protocollo",org.openspcoop2.core.tracciamento.Protocollo.class,"busta",Busta.class));
	
	}
	
	public BustaModel(IField father){
	
		super(father);
	
		this.MITTENTE = new org.openspcoop2.core.tracciamento.model.SoggettoModel(new ComplexField(father,"mittente",org.openspcoop2.core.tracciamento.Soggetto.class,"busta",Busta.class));
		this.DESTINATARIO = new org.openspcoop2.core.tracciamento.model.SoggettoModel(new ComplexField(father,"destinatario",org.openspcoop2.core.tracciamento.Soggetto.class,"busta",Busta.class));
		this.PROFILO_COLLABORAZIONE = new org.openspcoop2.core.tracciamento.model.ProfiloCollaborazioneModel(new ComplexField(father,"profilo-collaborazione",org.openspcoop2.core.tracciamento.ProfiloCollaborazione.class,"busta",Busta.class));
		this.SERVIZIO = new org.openspcoop2.core.tracciamento.model.ServizioModel(new ComplexField(father,"servizio",org.openspcoop2.core.tracciamento.Servizio.class,"busta",Busta.class));
		this.AZIONE = new ComplexField(father,"azione",java.lang.String.class,"busta",Busta.class);
		this.SERVIZIO_CORRELATO = new org.openspcoop2.core.tracciamento.model.ServizioModel(new ComplexField(father,"servizio-correlato",org.openspcoop2.core.tracciamento.Servizio.class,"busta",Busta.class));
		this.COLLABORAZIONE = new ComplexField(father,"collaborazione",java.lang.String.class,"busta",Busta.class);
		this.IDENTIFICATIVO = new ComplexField(father,"identificativo",java.lang.String.class,"busta",Busta.class);
		this.RIFERIMENTO_MESSAGGIO = new ComplexField(father,"riferimento-messaggio",java.lang.String.class,"busta",Busta.class);
		this.ORA_REGISTRAZIONE = new org.openspcoop2.core.tracciamento.model.DataModel(new ComplexField(father,"ora-registrazione",org.openspcoop2.core.tracciamento.Data.class,"busta",Busta.class));
		this.SCADENZA = new ComplexField(father,"scadenza",java.util.Date.class,"busta",Busta.class);
		this.PROFILO_TRASMISSIONE = new org.openspcoop2.core.tracciamento.model.ProfiloTrasmissioneModel(new ComplexField(father,"profilo-trasmissione",org.openspcoop2.core.tracciamento.ProfiloTrasmissione.class,"busta",Busta.class));
		this.SERVIZIO_APPLICATIVO_FRUITORE = new ComplexField(father,"servizio-applicativo-fruitore",java.lang.String.class,"busta",Busta.class);
		this.SERVIZIO_APPLICATIVO_EROGATORE = new ComplexField(father,"servizio-applicativo-erogatore",java.lang.String.class,"busta",Busta.class);
		this.DIGEST = new ComplexField(father,"digest",java.lang.String.class,"busta",Busta.class);
		this.TRASMISSIONI = new org.openspcoop2.core.tracciamento.model.TrasmissioniModel(new ComplexField(father,"trasmissioni",org.openspcoop2.core.tracciamento.Trasmissioni.class,"busta",Busta.class));
		this.RISCONTRI = new org.openspcoop2.core.tracciamento.model.RiscontriModel(new ComplexField(father,"riscontri",org.openspcoop2.core.tracciamento.Riscontri.class,"busta",Busta.class));
		this.ECCEZIONI = new org.openspcoop2.core.tracciamento.model.EccezioniModel(new ComplexField(father,"eccezioni",org.openspcoop2.core.tracciamento.Eccezioni.class,"busta",Busta.class));
		this.PROTOCOLLO = new org.openspcoop2.core.tracciamento.model.ProtocolloModel(new ComplexField(father,"protocollo",org.openspcoop2.core.tracciamento.Protocollo.class,"busta",Busta.class));
	
	}
	
	

	public org.openspcoop2.core.tracciamento.model.SoggettoModel MITTENTE = null;
	 
	public org.openspcoop2.core.tracciamento.model.SoggettoModel DESTINATARIO = null;
	 
	public org.openspcoop2.core.tracciamento.model.ProfiloCollaborazioneModel PROFILO_COLLABORAZIONE = null;
	 
	public org.openspcoop2.core.tracciamento.model.ServizioModel SERVIZIO = null;
	 
	public IField AZIONE = null;
	 
	public org.openspcoop2.core.tracciamento.model.ServizioModel SERVIZIO_CORRELATO = null;
	 
	public IField COLLABORAZIONE = null;
	 
	public IField IDENTIFICATIVO = null;
	 
	public IField RIFERIMENTO_MESSAGGIO = null;
	 
	public org.openspcoop2.core.tracciamento.model.DataModel ORA_REGISTRAZIONE = null;
	 
	public IField SCADENZA = null;
	 
	public org.openspcoop2.core.tracciamento.model.ProfiloTrasmissioneModel PROFILO_TRASMISSIONE = null;
	 
	public IField SERVIZIO_APPLICATIVO_FRUITORE = null;
	 
	public IField SERVIZIO_APPLICATIVO_EROGATORE = null;
	 
	public IField DIGEST = null;
	 
	public org.openspcoop2.core.tracciamento.model.TrasmissioniModel TRASMISSIONI = null;
	 
	public org.openspcoop2.core.tracciamento.model.RiscontriModel RISCONTRI = null;
	 
	public org.openspcoop2.core.tracciamento.model.EccezioniModel ECCEZIONI = null;
	 
	public org.openspcoop2.core.tracciamento.model.ProtocolloModel PROTOCOLLO = null;
	 

	@Override
	public Class<Busta> getModeledClass(){
		return Busta.class;
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