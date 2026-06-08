/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

package org.openspcoop2.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * VersionInfoPopup
 *
 * Descrittore generico di un popup informativo mostrato dalla console dopo il login,
 * valorizzato opzionalmente da una implementazione di {@link IVersionInfo}.
 * I campi testuali sono frammenti HTML 'fidati' (forniti dalla configurazione del plugin,
 * non da input utente): la console ne compone la struttura (titolo, paragrafi, elenco,
 * box nota, bottone) e li rende cosi' come sono.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class VersionInfoPopup implements Serializable {

	private static final long serialVersionUID = 1L;

	private String severity;          // "warning" | "error": stile header/icona
	private String icon;              // nome material icon
	private String title;             // HTML
	private List<String> paragraphs = new ArrayList<>(); // frammenti HTML (solo i non vuoti)
	private String listHead;          // HTML
	private List<String> listItems = new ArrayList<>();  // frammenti HTML (possono contenere <a>/mailto)
	private String note;              // HTML (puo' essere null)
	private String noteStyle;         // "warning" | "info": colore box nota
	private String buttonLabel;       // testo bottone di chiusura
	private boolean closable;         // se true la console mostra la 'X' di chiusura nell'header

	public String getSeverity() {
		return this.severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public String getIcon() {
		return this.icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getTitle() {
		return this.title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<String> getParagraphs() {
		return this.paragraphs;
	}
	public void setParagraphs(List<String> paragraphs) {
		this.paragraphs = paragraphs;
	}
	public void addParagraph(String paragraph) {
		this.paragraphs.add(paragraph);
	}
	public String getListHead() {
		return this.listHead;
	}
	public void setListHead(String listHead) {
		this.listHead = listHead;
	}
	public List<String> getListItems() {
		return this.listItems;
	}
	public void setListItems(List<String> listItems) {
		this.listItems = listItems;
	}
	public void addListItem(String listItem) {
		this.listItems.add(listItem);
	}
	public String getNote() {
		return this.note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getNoteStyle() {
		return this.noteStyle;
	}
	public void setNoteStyle(String noteStyle) {
		this.noteStyle = noteStyle;
	}
	public String getButtonLabel() {
		return this.buttonLabel;
	}
	public void setButtonLabel(String buttonLabel) {
		this.buttonLabel = buttonLabel;
	}
	public boolean isClosable() {
		return this.closable;
	}
	public void setClosable(boolean closable) {
		this.closable = closable;
	}

}
