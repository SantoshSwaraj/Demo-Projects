package aisaac.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.GeoCountryMaster;

public interface GeoCountryMasterRepository extends JpaRepository<GeoCountryMaster, Long>{

	List<GeoCountryMaster> findAllByCountryCodeIn(Set<String> countryCodeList);
}
