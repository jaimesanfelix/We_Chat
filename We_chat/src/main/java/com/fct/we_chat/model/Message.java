package com.fct.we_chat.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity class representing a message in the chat application.
 */
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /*@ManyToOne
    @JoinColumn(name = "user_to_id", nullable = false)
    private User userTo;

    @ManyToOne
    @JoinColumn(name = "user_from_id", nullable = false)
    private User userFrom;*/

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

   /* @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp; */



    //Usuario a quien se envia
    @Column(name = "user_to_id", nullable = false)
    private int user_to_id;

    //Usuario desde el que se envia
    @Column(name = "user_from_id", nullable = false)
    private int user_from_id;

    @Column(name = "group_id", nullable = false)
    private int group_id;
    
   
    @Column(name = "timestamp", nullable = false)
    private String timestamp;

    public Message() {}

    /**
     * Constructs a new Message with the specified sender, content, and timestamp.
     * 
     * @param sender the sender of the message
     * @param content the content of the message
     * @param timestamp the timestamp of the message
     */
    public Message(int user_to_id, int user_from_id, String content, int group_id, String timestamp) {
        this.user_to_id = user_to_id;
        this.user_from_id = user_from_id;
        this.content = content;
        this.group_id = group_id;
        this.timestamp = timestamp;
    }

    // Getters y Setters

    public int getUser_to_id() {
        return user_to_id;
    }

    public void setUser_to_id(int user_to_id) {
        this.user_to_id = user_to_id;
    }

    public int getUser_from_id() {
        return user_from_id;
    }

    public void setUser_from_id(int user_from_id) {
        this.user_from_id = user_from_id;
    }

    /**
     * Returns the ID of the message.
     * 
     * @return the ID of the message
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the message.
     * 
     * @param id the ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    

    /**
     * Returns the content of the message.
     * 
     * @return the content of the message
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the message.
     * 
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Returns the timestamp of the message.
     * 
     * @return the timestamp of the message
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of the message.
     * 
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the group_id
     */
    public int getGroup_id() {
        return group_id;
    }

    /**
     * @param group_id the group_id to set
     */
    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    

}

