package com.billyclub.points.repository;

import com.billyclub.points.model.Coverall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CoverallRepository extends JpaRepository<Coverall, Long> {

    @Query("select c from Coverall c where c.status in (0,1)")
    List<Coverall> findOpenCoveralls();

}
