package com.smartcontactmanager.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cId;
    private String name;
    private String secondName;
    @Column(unique = true)
    private String email;
    private String work;
    private String phone;
    private String imgUrl;
    @Column
    private String description;
    @ManyToOne
    @JsonIgnore
    private User user;

}
