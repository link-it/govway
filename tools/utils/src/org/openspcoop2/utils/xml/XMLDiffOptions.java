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

package org.openspcoop2.utils.xml;

/**	
 * XMLDiffOptions
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XMLDiffOptions {

	private boolean ignoreDiffBetweenTextAndCDATA = true;
	private boolean ignoreWhitespace = true;
	private boolean ignoreComments = true;
	private boolean normalize = true;
	
	public boolean isIgnoreDiffBetweenTextAndCDATA() {
		return this.ignoreDiffBetweenTextAndCDATA;
	}
	public void setIgnoreDiffBetweenTextAndCDATA(boolean ignoreDiffBetweenTextAndCDATA) {
		this.ignoreDiffBetweenTextAndCDATA = ignoreDiffBetweenTextAndCDATA;
	}
	public boolean isIgnoreWhitespace() {
		return this.ignoreWhitespace;
	}
	public void setIgnoreWhitespace(boolean ignoreWhitespace) {
		this.ignoreWhitespace = ignoreWhitespace;
	}
	public boolean isIgnoreComments() {
		return this.ignoreComments;
	}
	public void setIgnoreComments(boolean ignoreComments) {
		this.ignoreComments = ignoreComments;
	}
	public boolean isNormalize() {
		return this.normalize;
	}
	public void setNormalize(boolean normalize) {
		this.normalize = normalize;
	}
}
