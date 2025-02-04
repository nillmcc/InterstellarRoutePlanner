package com.irp.InterstellarRoutePlanner.mvcRest.Repo;

import com.irp.InterstellarRoutePlanner.mvcRest.Model.Gate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<Gate, String> {
}
