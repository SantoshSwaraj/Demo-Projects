package aisaac.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.AisaacApiQueue;

public interface AisaacApiQueueRepository extends JpaRepository<AisaacApiQueue, Long> {

}
