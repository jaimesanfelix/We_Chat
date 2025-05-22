package com.fct.we_chat.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users_by_group")
public class UserByGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //@ManyToOne
    //@JoinColumn(name = "user_id", nullable = false)
    @Column(name = "user_id")
    private int user;

    //@ManyToOne
    //@JoinColumn(name = "group_id", nullable = false)
    @Column(name = "group_id")
    private int group;

    @Column(name = "date_assignation", nullable = false)
    //@Temporal(TemporalType.TIMESTAMP)
    private String dateAssignation;

    /**
     * @param user
     * @param group
     */
    public UserByGroup(int user, int group, String dateAssignation) {
        this.user = user;
        this.group = group;
        this.dateAssignation = dateAssignation;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the user
     */
    public int getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(int user) {
        this.user = user;
    }

    /**
     * @return the group
     */
    public int getGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(int group) {
        this.group = group;
    }

    /**
     * @return the dateAssignation
     */
    public String getDateAssignation() {
        return dateAssignation;
    }

    /**
     * @param dateAssignation the dateAssignation to set
     */
    public void setDateAssignation(String dateAssignation) {
        this.dateAssignation = dateAssignation;
    }

    
}

