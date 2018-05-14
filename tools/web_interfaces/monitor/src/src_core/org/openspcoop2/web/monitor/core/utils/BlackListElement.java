package org.openspcoop2.web.monitor.core.utils;

import java.io.Serializable;


/*****
 * 
 * Oggetto che identifica un metodo setter da non richiamare durante la copia delle properties tra due bean.
 * 
 * Le informazioni necessarie sono:
 * 
 * 
 * Nome metodo setter da scartare;
 * 
 * Elenco dei tipi dei parametri del metodo;
 * 
 * 
 * 
 * @author pintori
 *
 */
public class BlackListElement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String nomeMetodo;

	private Class<?>[] classiParametri;

	public BlackListElement(String nome, Class<?>... classes) {
		this.nomeMetodo = nome;
		this.classiParametri = classes;
	}

	public String getNomeMetodo() {
		return this.nomeMetodo;
	}

	public void setNomeMetodo(String nomeMetodo) {
		this.nomeMetodo = nomeMetodo;
	}

	public Class<?>[] getClassiParametri() {
		return this.classiParametri;
	}

	public void setClassiParametri(Class<?>[] classiParametri) {
		this.classiParametri = classiParametri;
	}

	
	/****
	 * 
	 * Override del metodo equals:
	 * 
	 * Realizza il confronto tra due metodi.
	 * 
	 * 
	 * 
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}
		
		if(!(obj instanceof BlackListElement)){
			return false;
		}
		
		BlackListElement ble = (BlackListElement) obj;
		
		// controllo sul nome (Nome diverso = elemento non uguale)
		if(!this.nomeMetodo.equals(ble.getNomeMetodo())){
			return false;			
		}
		
		// Stesso nome ma elenco dei parametri diverso: return false;
		if(this.classiParametri == null && ble.getClassiParametri() != null){
			return false;
		}
		
		// Stesso nome ma elenco dei parametri diverso: return false;
		if(this.classiParametri != null && ble.getClassiParametri() == null){
			return false;
		}
		
		// Stesso nome ed elenco dei parametri non presente: return true;
		if(this.classiParametri == null && ble.getClassiParametri() == null){
			return true;
		}
		
		//stesso nome ed elenco dei parametri con un diverso numero di elementi: return false;
		if(this.classiParametri.length != ble.getClassiParametri().length){
			return false;
		}
		
		// assumo che se passa tutti i controlli precedenti i due oggetti abbiano lo stesso esatto numero di parametri.
		for (int i = 0; i < this.classiParametri.length; i++) {
			Class<?> _class = this.classiParametri[i];
			Class<?> bleClass = ble.getClassiParametri()[i];
			
			// quando trovo la prima discordanza nell'elenco dei parametri mi fermo: return false;
			if(!_class.getName().equals(bleClass.getName())){
				return false;
			}
		}
		
		return true;
	}

}
