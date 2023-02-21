package com.billyclub.points.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "roles")
public class Role {

    @Serial
    private static final long serialVersionUID = -1848921295963937021L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_seq")
    @SequenceGenerator(name="role_id_seq", sequenceName = "role_seq", allocationSize = 1, initialValue = 1000)
    @Column(name="id",updatable = false, nullable = false)
    private Long id;

    @Column(nullable=false, unique=true)
    private String name;

    @ManyToMany(mappedBy="roles")
    private List<User> users;
}
