package com.raver.iemsmaven.Model;

public class Parent {
    private int parentId;
    private int userId;
    private String parentType;
    private String surname;
    private String firstName;
    private String middleName;
    private String nameExtension;

    public Parent(int parentId, int userId, String parentType, String surname, String firstName, 
                  String middleName, String nameExtension) {
        this.parentId = parentId;
        this.userId = userId;
        this.parentType = parentType;
        this.surname = surname;
        this.firstName = firstName;
        this.middleName = middleName;
        this.nameExtension = nameExtension;
    }

    // Getters and setters
    public int getParentId() { return parentId; }
    public void setParentId(int parentId) { this.parentId = parentId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getParentType() { return parentType; }
    public void setParentType(String parentType) { this.parentType = parentType; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public String getNameExtension() { return nameExtension; }
    public void setNameExtension(String nameExtension) { this.nameExtension = nameExtension; }
}
