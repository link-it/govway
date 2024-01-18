/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.generic_project.web.utils;

/****
 * 
 * Classe che definisce un wrapper per coppie di oggetti.
 * 
 * 
 *
 * @param <L>
 * @param <R>
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Pair<L,R> {

	private final L left;
	private final R right;

	public Pair(L left, R right) {
		this.left = left;
		this.right = right;
	}

	public L getLeft() { return this.left; }
	public R getRight() { return this.right; }

	@Override
	public int hashCode() { return this.left.hashCode() ^ this.right.hashCode(); }

	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof Pair)) return false;
		Pair<?, ?> pairo = (Pair<?, ?>) o; 
		return this.left.equals(pairo.getLeft()) &&
				this.right.equals(pairo.getRight());
	}

}