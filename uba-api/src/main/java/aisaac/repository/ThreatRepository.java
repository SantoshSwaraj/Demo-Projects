package aisaac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import aisaac.entities.Threat;

public interface ThreatRepository extends JpaRepository<Threat, Long>,JpaSpecificationExecutor<Threat>{

}
