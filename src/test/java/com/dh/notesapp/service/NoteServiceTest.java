package com.dh.notesapp.service;

import com.dh.notesapp.model.Note;
import com.dh.notesapp.repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NoteServiceTest {
    private final NoteRepository noteRepository = mock(NoteRepository.class);
    private final NoteService noteService = new NoteService(noteRepository);

    @Test
    void testGetAllNotes() {
        Note note1 = new Note();
        note1.setId(1L);
        note1.setTitle("Nota 1");

        Note note2 = new Note();
        note2.setId(2L);
        note2.setTitle("Nota 2");

        when(noteRepository.findAll()).thenReturn(List.of(note1, note2));

        List<Note> notes = noteService.getAllNotes();

        assertEquals(2, notes.size());
        verify(noteRepository, times(1)).findAll();
    }

    @Test
    void testGetNoteByIdSuccess() {
        Note note = new Note();
        note.setId(1L);
        note.setTitle("Nota de prueba");

        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

        Note foundNote = noteService.getNoteById(1L);
        if (foundNote == null) {
            throw new NoSuchElementException("Nota no encontrada");
        }

        assertNotNull(foundNote);
        assertEquals("Nota de prueba", foundNote.getTitle());
        verify(noteRepository, times(1)).findById(1L);
    }

    @Test
    void testGetNoteByIdNotFound() {
        Long invalidId = 999L;

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            noteService.getNoteById(invalidId);
        });

        assertEquals("La nota con ID 999 no existe.", exception.getMessage());
    }

    @Test
    void testCreateNote() {
        Note note = new Note();
        note.setTitle("Nueva nota");
        note.setContent("Contenido de la nueva nota");

        when(noteRepository.save(note)).thenReturn(note);

        Note savedNote = noteService.createNote(note);

        assertNotNull(savedNote);
        assertEquals("Nueva nota", savedNote.getTitle());
        verify(noteRepository, times(1)).save(note);
    }

    @Test
    void testUpdateNote() {
        Note existingNote = new Note();
        existingNote.setId(1L);
        existingNote.setTitle("Nota existente");
        existingNote.setContent("Contenido existente");

        Note updatedNote = new Note();
        updatedNote.setId(1L);
        updatedNote.setTitle("Nota actualizada");
        updatedNote.setContent("Contenido actualizado");

        when(noteRepository.existsById(1L)).thenReturn(true);
        when(noteRepository.save(any(Note.class))).thenReturn(updatedNote);

        Note result = noteService.updateNote(updatedNote);

        assertNotNull(result);
        assertEquals("Nota actualizada", result.getTitle());
        assertEquals("Contenido actualizado", result.getContent());
        verify(noteRepository, times(1)).save(any(Note.class));
    }

    @Test
    void testDeleteNoteById() {
        when(noteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(noteRepository).deleteById(1L);

        noteService.deleteNoteById(1L);

        verify(noteRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteNoteByIdNotFound() {
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            noteService.deleteNoteById(999L);
        });
        assertEquals("La nota con ID 999 no existe.", exception.getMessage());
    }

    @Test
    void testSearchNotes() {
        Note note = new Note();
        note.setId(1L);
        note.setTitle("Nota importante");

        Page<Note> page = new PageImpl<>(List.of(note));
        when(noteRepository.findByTitleContainingOrContentContaining(anyString(), anyString(), any()))
                .thenReturn(page);

        Page<Note> result = noteService.searchNotes("importante", 0, 5);

        assertEquals(1, result.getTotalElements());
        verify(noteRepository, times(1)).findByTitleContainingOrContentContaining("importante", "importante", PageRequest.of(0, 5));
    }
}
