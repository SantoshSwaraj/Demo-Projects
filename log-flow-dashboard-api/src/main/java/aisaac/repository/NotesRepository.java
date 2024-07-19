package aisaac.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import aisaac.entities.Notes;

public interface NotesRepository extends JpaRepository<Notes, Long> {

	public List<Notes> findAllByTypeRecIdInAndNoteTypeOrderByCreatedDateDesc(List<Long> typeRecIds, String noteType);

	public List<Notes> findAllByTypeRecIdAndNoteTypeOrderByCreatedDateDesc(Long typeRecId, String noteType);
	
	
}
