package aisaac.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.WhitelistAuditTrail;

public interface WhiteListAuditTrailRepository extends JpaRepository<WhitelistAuditTrail, Long>{

}
