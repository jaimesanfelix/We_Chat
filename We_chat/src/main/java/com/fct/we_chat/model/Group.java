package com.fct.we_chat.model;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "date_creation", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;

    // Relación con UsersByGroup
    @OneToMany(mappedBy = "group")
    private ArrayList<User> userGroups;

    


    /**
     * @param id
     * @param name
     * @param dateCreation
     * @param userGroups
     */
    public Group(String name, ArrayList<User> userGroups) {
        this.name = name;
        this.userGroups = userGroups;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
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
    public Date getDateCreation() {
        return dateCreation;
    }

    /**
     * @param dateCreation the dateCreation to set
     */
    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    /**
     * @return the userGroups
     */
    public ArrayList<User> getUserGroups() {
        return userGroups;
    }

    /**
     * @param userGroups the userGroups to set
     */
    public void setUserGroups(ArrayList<User> userGroups) {
        this.userGroups = userGroups;
    }

    

}

