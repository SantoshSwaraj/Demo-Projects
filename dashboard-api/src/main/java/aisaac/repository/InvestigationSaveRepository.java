package aisaac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import aisaac.entities.InvestigationSave;

public interface InvestigationSaveRepository
		extends JpaRepository<InvestigationSave, Long>, JpaSpecificationExecutor<InvestigationSave> {

}
