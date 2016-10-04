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



package org.openspcoop2.protocol.sdk.diagnostica;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.diagnostica.DominioDiagnostico;
import org.openspcoop2.core.diagnostica.DominioSoggetto;
import org.openspcoop2.core.diagnostica.MessaggioDiagnostico;
import org.openspcoop2.core.diagnostica.Proprieta;
import org.openspcoop2.core.diagnostica.Protocollo;
import org.openspcoop2.core.id.IDSoggetto;


/**
 * Bean Contenente le informazioni relative ai messaggi diagnostici
 * 
 * @author Stefano Corallo <corallo@link.it>
 * @author Lorenzo Nardi <nardi@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class MsgDiagnostico implements Serializable{

	private static final long serialVersionUID = -3157816024001587816L;

    // msgdiagnostico
    private org.openspcoop2.core.diagnostica.MessaggioDiagnostico messaggioDiagnostico;

	
    public MsgDiagnostico(){
    	this.messaggioDiagnostico = new MessaggioDiagnostico();
    }
    public MsgDiagnostico(org.openspcoop2.core.diagnostica.MessaggioDiagnostico messaggioDiagnostico) {
    	this.messaggioDiagnostico = messaggioDiagnostico;
    }
    
    
    
    // base
    
    public org.openspcoop2.core.diagnostica.MessaggioDiagnostico getMessaggioDiagnostico() {
		return this.messaggioDiagnostico;
	}
	public void setMessaggioDiagnostico(org.openspcoop2.core.diagnostica.MessaggioDiagnostico messaggioDiagnostico) {
		this.messaggioDiagnostico = messaggioDiagnostico;
	}
    
    
	
	// id  [Wrapper]
	
	public Long getId() {
		return this.messaggioDiagnostico.getId();
	}
	public void setId(Long id) {
		this.messaggioDiagnostico.setId(id);
	}
	
	
	
    
	// gdo  [Wrapper]
	
	public Date getGdo() {
		return this.messaggioDiagnostico.getOraRegistrazione();
	}
	public void setGdo(Date gdo) {
		this.messaggioDiagnostico.setOraRegistrazione(gdo);
	}
	
	
	
	// dominio  [Wrapper]
	
	public IDSoggetto getIdSoggetto() {
		if(this.messaggioDiagnostico.getDominio()!=null){
			IDSoggetto idSoggetto = null;
			if(this.messaggioDiagnostico.getDominio().getIdentificativoPorta()!=null){
				if(idSoggetto==null){
					idSoggetto = new IDSoggetto();
				}
				idSoggetto.setCodicePorta(this.messaggioDiagnostico.getDominio().getIdentificativoPorta());
			}
			if(this.messaggioDiagnostico.getDominio().getSoggetto()!=null){
				if(idSoggetto==null){
					idSoggetto = new IDSoggetto();
				}
				idSoggetto.setTipo(this.messaggioDiagnostico.getDominio().getSoggetto().getTipo());
				idSoggetto.setNome(this.messaggioDiagnostico.getDominio().getSoggetto().getBase());
			}
			return idSoggetto;
		}
		
		return null;
	}
	public void setIdSoggetto(IDSoggetto idPorta) {
		if(idPorta!=null){
			if(this.messaggioDiagnostico.getDominio()==null){
				this.messaggioDiagnostico.setDominio(new DominioDiagnostico());
			}
			this.messaggioDiagnostico.getDominio().setIdentificativoPorta(idPorta.getCodicePorta());
			if(this.messaggioDiagnostico.getDominio().getSoggetto()==null){
				this.messaggioDiagnostico.getDominio().setSoggetto(new DominioSoggetto());
			}
			this.messaggioDiagnostico.getDominio().getSoggetto().setBase(idPorta.getNome());
			this.messaggioDiagnostico.getDominio().getSoggetto().setTipo(idPorta.getTipo());
		}else{
			if(this.messaggioDiagnostico.getDominio()!=null){
				if(this.messaggioDiagnostico.getDominio().getModulo()!=null){
					if(this.messaggioDiagnostico.getDominio().getSoggetto()!=null){
						this.messaggioDiagnostico.getDominio().setSoggetto(null);
					}
					this.messaggioDiagnostico.getDominio().setIdentificativoPorta(null);
				}
				else{
					this.messaggioDiagnostico.setDominio(null);
				}
				
			}
		}
	}
	public String getIdFunzione() {
		if(this.messaggioDiagnostico.getDominio()!=null){
			return this.messaggioDiagnostico.getDominio().getModulo();
		}
		return null;
	}
	public void setIdFunzione(String idFunzione) {
		if(idFunzione!=null){
			if(this.messaggioDiagnostico.getDominio()==null){
				this.messaggioDiagnostico.setDominio(new DominioDiagnostico());
			}
			this.messaggioDiagnostico.getDominio().setModulo(idFunzione);
		}else{
			if(this.messaggioDiagnostico.getDominio()!=null){
				if(this.messaggioDiagnostico.getDominio().getSoggetto()!=null || this.messaggioDiagnostico.getDominio().getIdentificativoPorta()!=null){
					this.messaggioDiagnostico.getDominio().setModulo(null);
				}
				else{
					this.messaggioDiagnostico.setDominio(null);
				}
				
			}
		}
	}
	
	
	
	// severita  [Wrapper]
	
	public int getSeverita() {
		if(this.messaggioDiagnostico.getSeverita()!=null){
			return this.messaggioDiagnostico.getSeverita();
		}
		return -1;
	}
	public void setSeverita(int severita) {
		this.messaggioDiagnostico.setSeverita(severita);
	}
	
	
	
	// messaggio  [Wrapper]
	
	public String getMessaggio() {
		return this.messaggioDiagnostico.getMessaggio();
	}
	public void setMessaggio(String messaggio) {
		this.messaggioDiagnostico.setMessaggio(messaggio);
	}
	
	
	
	// identificativi busta [Wrapper]
	
	public String getIdBusta() {
		return this.messaggioDiagnostico.getIdentificativoRichiesta();
	}
	public void setIdBusta(String idBusta) {
		this.messaggioDiagnostico.setIdentificativoRichiesta(idBusta);
	}
	public String getIdBustaRisposta() {
		return this.messaggioDiagnostico.getIdentificativoRisposta();
	}
	public void setIdBustaRisposta(String idBustaRisposta) {
		this.messaggioDiagnostico.setIdentificativoRisposta(idBustaRisposta);
	}
	
	
	
	// codice [Wrapper]
	
	public String getCodice() {
		return this.messaggioDiagnostico.getCodice();
	}
	public void setCodice(String codice) {
		this.messaggioDiagnostico.setCodice(codice);
	}
	
	
	
	
	// protocollo [wrapper]
	
	public String getProtocollo() {
		if(this.messaggioDiagnostico.getProtocollo()!=null)
			return this.messaggioDiagnostico.getProtocollo().getIdentificativo();
		return null;
	}

	public void setProtocollo(String protocollo) {
		if(protocollo!=null){
			if(this.messaggioDiagnostico.getProtocollo()==null){
				this.messaggioDiagnostico.setProtocollo(new Protocollo());
			}
			this.messaggioDiagnostico.getProtocollo().setIdentificativo(protocollo);
		}
		else{
			if(this.messaggioDiagnostico.getProtocollo()!=null){
				if(this.messaggioDiagnostico.getProtocollo().sizeProprietaList()<=0){
					this.messaggioDiagnostico.setProtocollo(null);
				}
				else{
					this.messaggioDiagnostico.getProtocollo().setIdentificativo(null);
				}
			}
		}
	}
	
	
	
	
	// properties [wrapped]
	
    public void addProperty(String key,String value){
    	// Per evitare nullPointer durante la serializzazione
		// Non deve essere inserito nemmeno il valore ""
		if(value!=null && !"".equals(value)){
			if(this.messaggioDiagnostico.getProtocollo()==null){
				this.messaggioDiagnostico.setProtocollo(new Protocollo());
			}
			Proprieta proprieta = new Proprieta();
			proprieta.setNome(key);
			proprieta.setValore(value);
			this.removeProperty(key); // per evitare doppioni
			this.messaggioDiagnostico.getProtocollo().addProprieta(proprieta);
		}
    }
    
    public int sizeProperties(){
    	if(this.messaggioDiagnostico.getProtocollo()!=null){
			return this.messaggioDiagnostico.getProtocollo().sizeProprietaList();
		}
		return 0;
    }

    public String getProperty(String key){
    	if(this.messaggioDiagnostico.getProtocollo()!=null){
			for (int i = 0; i < this.messaggioDiagnostico.getProtocollo().sizeProprietaList(); i++) {
				Proprieta proprieta = this.messaggioDiagnostico.getProtocollo().getProprieta(i);
				if(proprieta.getNome().equals(key)){
					return proprieta.getValore();
				}
			}
		}
		return null;
    }
    
    public String removeProperty(String key){
    	if(this.messaggioDiagnostico.getProtocollo()!=null){
			for (int i = 0; i < this.messaggioDiagnostico.getProtocollo().sizeProprietaList(); i++) {
				Proprieta proprieta = this.messaggioDiagnostico.getProtocollo().getProprieta(i);
				if(proprieta.getNome().equals(key)){
					this.messaggioDiagnostico.getProtocollo().removeProprieta(i);
					return proprieta.getValore();
				}
			}
		}
		return null;
    }
    
    public String[] getPropertiesValues() {
    	List<String> propertiesValues = new ArrayList<String>();
		if(this.messaggioDiagnostico.getProtocollo()!=null){
			for (int i = 0; i < this.messaggioDiagnostico.getProtocollo().sizeProprietaList(); i++) {
				Proprieta proprieta = this.messaggioDiagnostico.getProtocollo().getProprieta(i);
				propertiesValues.add(proprieta.getValore());
			}
		}
		if(propertiesValues.size()>0){
			return propertiesValues.toArray(new String[1]);
		}
		else{
			return null;
		}
    }
    
    public String[] getPropertiesNames() {
    	List<String> propertiesValues = new ArrayList<String>();
		if(this.messaggioDiagnostico.getProtocollo()!=null){
			for (int i = 0; i < this.messaggioDiagnostico.getProtocollo().sizeProprietaList(); i++) {
				Proprieta proprieta = this.messaggioDiagnostico.getProtocollo().getProprieta(i);
				propertiesValues.add(proprieta.getNome());
			}
		}
		if(propertiesValues.size()>0){
			return propertiesValues.toArray(new String[1]);
		}
		else{
			return null;
		}
    }
    
    // Non devono essere usati.
 	// Altrimenti poi se viene effettuato una add o remove sulla lista o hashtable ritornata, la modifica non ha effetto
//    public void setProperties(Hashtable<String, String> params) {
//    	Enumeration<String> keys = params.keys();
//		while (keys.hasMoreElements()) {
//			String key = (String) keys.nextElement();
//			this.addProperty(key, params.get(key));
//		}
//    }
//    
//    public Hashtable<String, String> getProperties() {
//    	Hashtable<String, String> map = new Hashtable<String, String>();
//		if(this.messaggioDiagnostico.getProtocollo()!=null){
//			for (int i = 0; i < this.messaggioDiagnostico.getProtocollo().sizeProprietaList(); i++) {
//				Proprieta proprieta = this.messaggioDiagnostico.getProtocollo().getProprieta(i);
//				map.put(proprieta.getNome(), proprieta.getValore());
//			}
//		}
//		return map;
//    }



}


