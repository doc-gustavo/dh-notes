package com.dh.notesapp.repository;

import com.dh.notesapp.model.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // Método de búsqueda por título o contenido
    Page<Note> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
}
