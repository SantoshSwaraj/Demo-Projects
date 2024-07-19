package aisaac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.TagValue;

public interface TagValueRepository extends JpaRepository<TagValue, Long>{

	List<TagValue> findAllByTagIdIn(List<Long> tagIds);
}
