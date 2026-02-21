
package com.raver.iemsmaven.Model;

public class Spouse {
    private int spouseId;
    private int userId;
    private String surname;
    private String firstName;
    private String middleName;
    private String nameExtension;
    private String occupation;
    private String employer;
    private String businessAddress;
    private String telephone;

    public Spouse(int spouseId, int userId, String surname, String firstName, String middleName, 
                 String nameExtension, String occupation, String employer, String businessAddress, String telephone) {
        this.spouseId = spouseId;
        this.userId = userId;
        this.surname = surname;
        this.firstName = firstName;
        this.middleName = middleName;
        this.nameExtension = nameExtension;
        this.occupation = occupation;
        this.employer = employer;
        this.businessAddress = businessAddress;
        this.telephone = telephone;
    }

    // Getters and setters
    public int getSpouseId() { return spouseId; }
    public void setSpouseId(int spouseId) { this.spouseId = spouseId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public String getNameExtension() { return nameExtension; }
    public void setNameExtension(String nameExtension) { this.nameExtension = nameExtension; }
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    public String getEmployer() { return employer; }
    public void setEmployer(String employer) { this.employer = employer; }
    public String getBusinessAddress() { return businessAddress; }
    public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
}