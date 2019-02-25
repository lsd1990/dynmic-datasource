package com.lsd.test.dynmic.source.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
public class User implements Serializable{


    private static final long serialVersionUID = -4476347882559530852L;

    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String name;

}
