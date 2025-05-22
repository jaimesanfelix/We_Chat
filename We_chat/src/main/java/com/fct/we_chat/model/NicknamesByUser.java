package com.fct.we_chat.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "nicknames_by_user")
public class NicknamesByUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "target_user_id")
    private int target_user_id;

    @Column(name = "user_connected_id")
    private int user_connected_id;

    public NicknamesByUser(String nickname, int target_user_id, int user_connected_id) {
        this.nickname = nickname;
        this.target_user_id = target_user_id;
        this.user_connected_id = user_connected_id;
    }

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

    public int getUser_id() {
        return target_user_id;
    }

    public void setUser_id(int target_user_id) {
        this.target_user_id = target_user_id;
    }

    public int getUser_connected_id() {
        return user_connected_id;
    }

    public void setUser_connected_id(int user_connected_id) {
        this.user_connected_id = user_connected_id;
    }

}
