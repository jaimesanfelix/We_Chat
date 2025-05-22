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

    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;

    @Column(name = "connection_time")
    private String connectionTime;

    public User() {}

    /**
     * Constructs a new User with the specified nickname and connection time.
     * 
     * @param nickname the nickname of the user
     * @param connectionTime the connection time of the user
     */
    public User(String nickname, String connectionTime) {
        this.nickname = nickname;
        this.connectionTime = connectionTime;
    }

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
     * Returns the nickname of the user.
     * 
     * @return the nickname of the user
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets the nickname of the user.
     * 
     * @param nickname the nickname to set
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
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
}
