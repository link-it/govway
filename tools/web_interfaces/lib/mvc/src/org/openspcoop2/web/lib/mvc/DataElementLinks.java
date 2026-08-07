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
package org.openspcoop2.web.lib.mvc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Insieme dei collegamenti verso gli elementi correlati al campo di una edit page,
 * resi graficamente tramite una singola icona posizionata a fianco del campo.
 *
 * Se il collegamento e' unico, l'icona porta direttamente all'elemento; se i collegamenti
 * sono piu' di uno, l'icona apre una finestra modale in cui scegliere l'elemento da visualizzare.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DataElementLinks implements Serializable {

	private static final long serialVersionUID = 1L;

	private String buttonIcon = Costanti.ICONA_VISUALIZZA;

	/** tooltip dell'icona utilizzato quando i collegamenti sono piu' di uno */
	private String toolTip;

	/** titolo della finestra modale di selezione; se non indicato viene utilizzata la label del campo */
	private String headerFinestraModale;

	/** testo introduttivo della finestra modale di selezione */
	private String headerBody = Costanti.LABEL_SELEZIONE_LINK_HEADER_BODY;

	private List<DataElementLink> links = new ArrayList<>();

	public DataElementLinks() {
		// default constructor
	}

	public void addLink(DataElementLink link) {
		this.links.add(link);
	}
	public List<DataElementLink> getLinks() {
		return this.links;
	}
	public DataElementLink getLink(int index) {
		return this.links.get(index);
	}
	public int size() {
		return this.links.size();
	}
	public boolean isEmpty() {
		return this.links.isEmpty();
	}

	public String getButtonIcon() {
		return this.buttonIcon;
	}
	public void setButtonIcon(String buttonIcon) {
		this.buttonIcon = buttonIcon;
	}

	public String getToolTip() {
		return DataElement.checkNull(this.toolTip);
	}
	public void setToolTip(String toolTip) {
		this.toolTip = toolTip;
	}

	public String getHeaderFinestraModale() {
		return DataElement.checkNull(this.headerFinestraModale);
	}
	public void setHeaderFinestraModale(String headerFinestraModale) {
		this.headerFinestraModale = headerFinestraModale;
	}

	public String getHeaderBody() {
		return this.headerBody;
	}
	public void setHeaderBody(String headerBody) {
		this.headerBody = headerBody;
	}

	/**
	 * Corpo html della finestra modale di selezione: testo introduttivo ed elenco dei collegamenti.
	 */
	public String getBodyHtml() {
		StringBuilder sb = new StringBuilder();

		if(StringUtils.isNotEmpty(this.headerBody)) {
			sb.append("<p><span>").append(StringEscapeUtils.escapeHtml(this.headerBody)).append("</span></p>");
		}

		sb.append("<ul class=\"dataElementLinksList\">");
		for (DataElementLink link : this.links) {
			sb.append("<li><a href=\"").append(link.getUrl()).append("\"");
			if(StringUtils.isNotEmpty(link.getTarget())) {
				sb.append(" target=\"").append(link.getTarget()).append("\" rel=\"noopener\"");
			}
			sb.append(">").append(StringEscapeUtils.escapeHtml(link.getLabel())).append("</a></li>");
		}
		sb.append("</ul>");

		return sb.toString();
	}

}
