package com.website.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.website.main.model.Picture;
import java.util.List;

public interface PictureRepository extends JpaRepository<Picture, Integer> {
    List<Picture> findByPostId(Integer postId);
}
