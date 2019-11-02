package com.tericcabrel.osiris.models;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity(name = "users")
public class User {
    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(unique = true, insertable = false, updatable = false, nullable = false)
    protected int id;

    @Column(unique = true, length = 10)
    private String uid;

    @Column(length = 100)
    private String name;

    @Column(name = "birth_date", length = 10)
    private String birthDate;

    @Column()
    private String finger;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    public int getId() {
        return id;
    }

    public User setId(int id) {
        this.id = id;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public User setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public User setBirthDate(String birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public String getFinger() {
        return finger;
    }

    public User setFinger(String finger) {
        this.finger = finger;
        return this;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public User setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public User setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
}
