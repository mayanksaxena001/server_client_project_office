package com.util;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDetail {

    String firstName;
    String lastName;
    String eMailId;
    Date  dateOfBirth;
    Gender gender;
    User user;

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public String geteMailId() {
	return eMailId;
    }

    public void seteMailId(String eMailId) {
	this.eMailId = eMailId;
    }

    public User getUser() {
	return user;
    }

    public void setUser(User user) {
	this.user = user;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((dateOfBirth == null) ? 0 : dateOfBirth.hashCode());
	result = prime * result + ((eMailId == null) ? 0 : eMailId.hashCode());
	result = prime * result
		+ ((firstName == null) ? 0 : firstName.hashCode());
	result = prime * result + ((gender == null) ? 0 : gender.hashCode());
	result = prime * result
		+ ((lastName == null) ? 0 : lastName.hashCode());
	result = prime * result + ((user == null) ? 0 : user.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	UserDetail other = (UserDetail) obj;
	if (dateOfBirth == null) {
	    if (other.dateOfBirth != null)
		return false;
	} else if (!dateOfBirth.equals(other.dateOfBirth))
	    return false;
	if (eMailId == null) {
	    if (other.eMailId != null)
		return false;
	} else if (!eMailId.equals(other.eMailId))
	    return false;
	if (firstName == null) {
	    if (other.firstName != null)
		return false;
	} else if (!firstName.equals(other.firstName))
	    return false;
	if (gender != other.gender)
	    return false;
	if (lastName == null) {
	    if (other.lastName != null)
		return false;
	} else if (!lastName.equals(other.lastName))
	    return false;
	if (user == null) {
	    if (other.user != null)
		return false;
	} else if (!user.equals(other.user))
	    return false;
	return true;
    }

	@Override
	public String toString() {
		return "UserDetail [firstName=" + firstName + ", lastName=" + lastName + ", eMailId=" + eMailId
				+ ", dateOfBirth=" + dateOfBirth + ", gender=" + gender + ", user=" + user + "]";
	}
	
	@JsonIgnore
	public String getUniqueName(){
	    return this.getUser().getUserName()+"_"+this.hashCode();
	}

}
