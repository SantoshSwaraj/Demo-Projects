package aisaac.repository;

import java.math.BigInteger;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.Threat;

public interface ThreatRepository extends JpaRepository<Threat, BigInteger> {

}
