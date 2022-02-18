package org.openspcoop2.web.monitor.core.bean;

import java.io.Serializable;

public class DialogInfo implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String titolo;
	private String contenuto;
	private boolean resizeable;
	private boolean moveable;
	private String width;
	private String height;
	
	public DialogInfo() {
		this.resizeable = false;
		this.moveable = false;
		this.width = "500";
		this.height = "300";
	}
	
	public String getTitolo() {
		return this.titolo;
	}
	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}
	public String getContenuto() {
		return this.contenuto;
	}
	public void setContenuto(String contenuto) {
		this.contenuto = contenuto;
	}
	public boolean isResizeable() {
		return this.resizeable;
	}
	public void setResizeable(boolean resizeable) {
		this.resizeable = resizeable;
	}
	public boolean isMoveable() {
		return this.moveable;
	}
	public void setMoveable(boolean moveable) {
		this.moveable = moveable;
	}
	public String getWidth() {
		return this.width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getHeight() {
		return this.height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	
	
}
