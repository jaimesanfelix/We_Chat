package com.fct.we_chat.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity class representing a user in the chat application.
 */
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "connection_time")
    private String connectionTime;

     // Relación con Messages
  /*   @OneToMany(mappedBy = "userFrom")
     private Set<Message> sentMessages;
 
     @OneToMany(mappedBy = "userTo")
     private Set<Message> receivedMessages;
 
     // Relación con UsersByGroup
     @OneToMany(mappedBy = "user")
     private Set<UserByGroup> userGroups;
     */
    public User(String username, String email, String password, String connectionTime) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.connectionTime = connectionTime;
    }

    /**
     * Constructs a new User with the specified username and connection time.
     * 
     * @param username the username of the user
     * @param connectionTime the connection time of the user
     */
   /*  public User(String username, String password, String connectionTime) {
        this.username = username;
        this.password = password;
        this.connectionTime = connectionTime;
    } */

    // Getters y Setters

    /**
     * Returns the ID of the user.
     * 
     * @return the ID of the user
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the user.
     * 
     * @param id the ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the username of the user.
     * 
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     * 
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the connection time of the user.
     * 
     * @return the connection time of the user
     */
    public String getConnectionTime() {
        return connectionTime;
    }

    /**
     * Sets the connection time of the user.
     * 
     * @param connectionTime the connection time to set
     */
    public void setConnectionTime(String connectionTime) {
        this.connectionTime = connectionTime;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
