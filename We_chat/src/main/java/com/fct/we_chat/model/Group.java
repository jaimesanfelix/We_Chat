package com.fct.we_chat.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "date_creation", nullable = false)
    //@Temporal(TemporalType.TIMESTAMP)
    private String dateCreation;

    // Relación con UsersByGroup
    //@OneToMany(mappedBy = "group")
    //private ArrayList<User> userGroups;

    //@OneToMany(mappedBy = "group_id")
    //private List<String> userGroups;


    /**
     * @param id
     * @param name
     * @param dateCreation
     * @param userGroups
     */

    public Group() {
        
    }
    public Group(String name/*, ArrayList<User> userGroups*/, String dataCreation) {
        this.name = name;
        //this.userGroups = userGroups;
        this.dateCreation = dataCreation;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return  name;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the dateCreation
     */
    public String getDateCreation() {
        return dateCreation;
    }

    /**
     * @param dateCreation the dateCreation to set
     */
    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }
/* 
    public List<String> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(List<String> newUserGroups) {
        this.userGroups = new ArrayList<>(newUserGroups);
    } */
    /**
     * @return the userGroups
     */
    /*public ArrayList<User> getUserGroups() {
        return userGroups;
    }*/

    /**
     * @param userGroups the userGroups to set
     */
    /*public void setUserGroups(ArrayList<User> userGroups) {
        this.userGroups = userGroups;
    }*/
    

}

