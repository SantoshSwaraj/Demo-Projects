package aisaac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.AssetTag;

public interface AssetTagRepository extends JpaRepository<AssetTag, Long>{

	List<AssetTag> findAllByAssetIdIn(List<Long> assetIds);
}
