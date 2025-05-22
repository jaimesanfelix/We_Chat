package com.fct.we_chat.model;


import javax.persistence.*;

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

    public User(String nickname, String connectionTime) {
        this.nickname = nickname;
        this.connectionTime = connectionTime;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getConnectionTime() {
        return connectionTime;
    }

    public void setConnectionTime(String connectionTime) {
        this.connectionTime = connectionTime;
    }
}
