package com.dh.notesapp.service;

import com.dh.notesapp.model.Note;
import com.dh.notesapp.repository.NoteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j
@Service
public class NoteService {
    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    // Obtener todas las notas sin paginación
    public List<Note> getAllNotes() {
        log.info("Obteniendo todas las notas.");
        return noteRepository.findAll();
    }

    // Obtener una nota por ID con manejo de excepciones
    public Note getNoteById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID proporcionado no es válido.");
        }
        return noteRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Intento de acceder a nota inexistente con ID: {}", id);
                    return new NoSuchElementException("La nota con ID " + id + " no existe.");
                });
    }

    // Crear una nueva nota con validación
    public Note createNote(Note note) {
        log.info("Creando nueva nota: {}", note);
        Objects.requireNonNull(note, "La nota no puede ser nula.");
        validateNote(note);
        return noteRepository.save(note);
    }

    // Actualizar una nota existente con validación
    public Note updateNote(Note note) {
        log.info("Actualizando nota con ID: {}", note.getId());
        Objects.requireNonNull(note, "La nota no puede ser nula.");
        Objects.requireNonNull(note.getId(), "El ID de la nota no puede ser nulo.");

        if (!noteRepository.existsById(note.getId())) {
            throw new NoSuchElementException("La nota con ID " + note.getId() + " no existe.");
        }

        validateNote(note);
        return noteRepository.save(note);
    }

    // Eliminar una nota por ID con verificación previa
    public void deleteNoteById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID proporcionado no es válido.");
        }

        if (!noteRepository.existsById(id)) {
            throw new NoSuchElementException("La nota con ID " + id + " no existe.");
        }

        noteRepository.deleteById(id);
        log.info("Nota con ID: {} eliminada correctamente.", id);
    }

    // Búsqueda de notas con paginación y verificación de valores
    public Page<Note> searchNotes(String keyword, int page, int size) {
        log.info("Buscando notas con keyword: '{}' en página {} con tamaño {}", keyword, page, size);
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("Los parámetros de paginación no son válidos.");
        }

        Pageable pageable = PageRequest.of(page, size);

        if (keyword == null || keyword.trim().isEmpty()) {
            log.info("No se proporcionó keyword, devolviendo página vacía.");
            return Page.empty(pageable);
        }

        return noteRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
    }

    // Validar que la nota tenga un título y contenido válidos
    private void validateNote(Note note) {
        if (note.getTitle() == null || note.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("El título de la nota no puede estar vacío.");
        }
        if (note.getContent() == null || note.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido de la nota no puede estar vacío.");
        }
    }
}
