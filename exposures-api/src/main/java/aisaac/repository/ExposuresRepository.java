package aisaac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import aisaac.entities.EasmIssue;

public interface ExposuresRepository extends JpaRepository<EasmIssue, Long>, JpaSpecificationExecutor<EasmIssue> {

}
