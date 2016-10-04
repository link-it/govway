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
package org.openspcoop2.generic_project.expression.impl.test.beans;


import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.openspcoop2.generic_project.expression.impl.test.model.AuthorModel;

/**
 * Author
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Author {

	public Author(String name,String surname){
		this.name = name;
		this.surname = surname;
	}
	
	private String name;
	private String surname;
	private boolean single;
	private int age;
	private long weight;
	private double bankAccount;
	private float secondBankAccount;
	private Date dateOfBirth;
	private Calendar firstBookReleaseDate;
	private Timestamp lastBookReleaseDate;
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return this.surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public boolean isSingle() {
		return this.single;
	}
	public void setSingle(boolean single) {
		this.single = single;
	}
	public int getAge() {
		return this.age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public long getWeight() {
		return this.weight;
	}
	public void setWeight(long weight) {
		this.weight = weight;
	}
	public double getBankAccount() {
		return this.bankAccount;
	}
	public void setBankAccount(double bankAccount) {
		this.bankAccount = bankAccount;
	}
	public float getSecondBankAccount() {
		return this.secondBankAccount;
	}
	public void setSecondBankAccount(float secondBankAccount) {
		this.secondBankAccount = secondBankAccount;
	}
	public Date getDateOfBirth() {
		return this.dateOfBirth;
	}
	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public Calendar getFirstBookReleaseDate() {
		return this.firstBookReleaseDate;
	}
	public void setFirstBookReleaseDate(Calendar firstBookReleaseDate) {
		this.firstBookReleaseDate = firstBookReleaseDate;
	}
	public Timestamp getLastBookReleaseDate() {
		return this.lastBookReleaseDate;
	}
	public void setLastBookReleaseDate(Timestamp lastBookReleaseDate) {
		this.lastBookReleaseDate = lastBookReleaseDate;
	}
	
	public static AuthorModel model(){
		return new AuthorModel();
	}
}
