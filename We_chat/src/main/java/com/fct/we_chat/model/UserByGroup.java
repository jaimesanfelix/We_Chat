package com.fct.we_chat.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "users_by_group")
public class UserByGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name = "date_assignation", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAssignation;

    // Getters y Setters
}

