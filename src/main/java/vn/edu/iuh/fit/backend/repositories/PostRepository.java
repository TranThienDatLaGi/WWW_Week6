package vn.edu.iuh.fit.backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.iuh.fit.backend.models.Post;
import vn.edu.iuh.fit.backend.models.User;

import java.util.List;
@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    Page<Post> findAllByPublished(boolean published, Pageable pageable);
    Page<Post> findAllByAuthorAndPublished(User author, boolean published,Pageable pageable);


}
