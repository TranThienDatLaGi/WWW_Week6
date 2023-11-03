package vn.edu.iuh.fit.backend.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "bigint(20)", nullable = false)
    private long id;
    @Column(name = "first_name", columnDefinition = "varchar(50)")
    private String firstName;
    @Column(name = "middle_name", columnDefinition = "varchar(50)")
    private String middleName;
    @Column(name = "last_name", columnDefinition = "varchar(50)")
    private String lastName;
    @Column(name = "mobile", columnDefinition = "varchar(15)")
    private String mobile;
    @Column(name = "email", columnDefinition = "varchar(50)")
    private String email;
    @Column(name = "password_hash", columnDefinition = "varchar(32)")
    private String passwordHash;
    @Column(name = "registered_at", columnDefinition = "datetime(6)")
    private Instant registeredAt;
    @Column(name = "last_login", columnDefinition = "datetime(6)")
    private Instant lastLogin;
    @Lob
    @Column(name = "intro")
    private String intro;
    @Lob
    @Column(name = "profile")
    private String profile;

    @OneToMany(mappedBy = "author")
    private Set<Post> posts = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<PostComment> postComments = new LinkedHashSet<>();
}
