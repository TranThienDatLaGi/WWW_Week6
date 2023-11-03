package vn.edu.iuh.fit.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "post")
@Data @AllArgsConstructor @NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;
    @Column(name = "published", columnDefinition = "bit(1)")
    private boolean published;
    @Lob
    private String content;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Post parent;
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
    @Column(name = "meta_title", length = 100)
    private String metaTitle;
    @Column(name = "title", length = 75)
    private String title;
    @Lob
    private String summary;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;
    @Column(name = "published_at")
    private Instant publishedAt;

    @OneToMany(mappedBy = "parent")
    private Set<Post> posts = new LinkedHashSet<>();

    @OneToMany(mappedBy = "post")
    private Set<PostComment> postComments = new LinkedHashSet<>();
}
