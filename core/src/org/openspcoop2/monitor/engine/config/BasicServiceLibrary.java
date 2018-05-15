package org.openspcoop2.monitor.engine.config;

import java.io.Serializable;

import org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizio;
import org.openspcoop2.monitor.engine.config.base.ConfigurazioneServizioAzione;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;

/**
 * BasicServiceLibrary
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicServiceLibrary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private IDAccordo idAccordoServizioParteComune;
	private String portType;
	private String azione;
	
	private IDServizio idServizio;
	private org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica accordoServizioParteSpecifica;	
	
	private ConfigurazioneServizio serviceLibrary;
	private ConfigurazioneServizioAzione serviceActionLibrary;
	private ConfigurazioneServizioAzione serviceActionAllLibrary; // per azione '*'
	
	public String getPortType() {
		return this.portType;
	}
	public void setPortType(String portType) {
		this.portType = portType;
	}
	public String getAzione() {
		return this.azione;
	}
	public void setAzione(String azione) {
		this.azione = azione;
	}
	public IDAccordo getIdAccordoServizioParteComune() {
		return this.idAccordoServizioParteComune;
	}
	public void setIdAccordoServizioParteComune(
			IDAccordo idAccordoServizioParteComune) {
		this.idAccordoServizioParteComune = idAccordoServizioParteComune;
	}
	public IDServizio getIdServizio() {
		return this.idServizio;
	}
	public void setIdServizio(IDServizio idServizio) {
		this.idServizio = idServizio;
	}
	public org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica getAccordoServizioParteSpecifica() {
		return this.accordoServizioParteSpecifica;
	}
	public void setAccordoServizioParteSpecifica(
			org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica accordoServizioParteSpecifica) {
		this.accordoServizioParteSpecifica = accordoServizioParteSpecifica;
	}
	public ConfigurazioneServizio getServiceLibrary() {
		return this.serviceLibrary;
	}
	public void setServiceLibrary(ConfigurazioneServizio serviceLibrary) {
		this.serviceLibrary = serviceLibrary;
	}
	public ConfigurazioneServizioAzione getServiceActionLibrary() {
		return this.serviceActionLibrary;
	}
	public void setServiceActionLibrary(
			ConfigurazioneServizioAzione serviceActionLibrary) {
		this.serviceActionLibrary = serviceActionLibrary;
	}
	public ConfigurazioneServizioAzione getServiceActionAllLibrary() {
		return this.serviceActionAllLibrary;
	}
	public void setServiceActionAllLibrary(
			ConfigurazioneServizioAzione serviceActionAllLibrary) {
		this.serviceActionAllLibrary = serviceActionAllLibrary;
	}
	
	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();

		bf.append("IdAccordo: ");
		if(this.getIdAccordoServizioParteComune()!=null){
			bf.append(this.getIdAccordoServizioParteComune().toString());
		}else{
			bf.append("-");
		}
		bf.append("\n");
		
		bf.append("PortType: ");
		if(this.getPortType()!=null){
			bf.append(this.getPortType());
		}else{
			bf.append("-");
		}
		bf.append("\n");
		
		bf.append("Azione: ");
		if(this.getAzione()!=null){
			bf.append(this.getAzione());
		}else{
			bf.append("-");
		}
		bf.append("\n");

		bf.append("IdServizio: ");
		if(this.getIdServizio()!=null){
			bf.append(this.getIdServizio().toString());
		}else{
			bf.append("-");
		}
		bf.append("\n");
		
		bf.append("AccordoServizioParteSpecifica: ");
		if(this.getAccordoServizioParteSpecifica()!=null){
			bf.append("presente");
		}else{
			bf.append("-");
		}
		bf.append("\n");

		bf.append("ServiceLibrary: ");
		if(this.getServiceLibrary()!=null){
			bf.append("presente");
		}else{
			bf.append("-");
		}
		bf.append("\n");

		bf.append("ActionLibrary: ");
		if(this.getServiceActionLibrary()!=null){
			bf.append("azione["+this.getServiceActionLibrary().getAzione()+"]");
		}else{
			bf.append("-");
		}
		bf.append("\n");

		bf.append("ActionAllLibrary: ");
		if(this.getServiceActionAllLibrary()!=null){
			bf.append("azione["+this.getServiceActionAllLibrary().getAzione()+"]");
		}else{
			bf.append("-");
		}
		bf.append("\n");

		return bf.toString();
	}
}
