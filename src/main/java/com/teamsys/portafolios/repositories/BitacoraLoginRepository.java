package com.teamsys.portafolios.repositories;

import com.teamsys.portafolios.entities.BitacoraLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BitacoraLoginRepository extends JpaRepository<BitacoraLogin, Long> {
}