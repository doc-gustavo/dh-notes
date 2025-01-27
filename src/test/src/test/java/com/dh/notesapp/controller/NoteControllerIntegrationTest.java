package com.dh.notesapp.integration;

import com.dh.notesapp.model.Note;
import com.dh.notesapp.repository.NoteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NoteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateNote() throws Exception {
        Note note = new Note();
        note.setTitle("Prueba de integración");
        note.setContent("Contenido de la prueba");

        ResultActions response = mockMvc.perform(post("/api/notes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(note)));

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Prueba de integración"))
                .andExpect(jsonPath("$.content").value("Contenido de la prueba"));
    }

    @Test
    void testGetAllNotes() throws Exception {
        noteRepository.saveAll(List.of(
                new Note(null, "Nota 1", "Contenido 1", null, null),
                new Note(null, "Nota 2", "Contenido 2", null, null)
        ));

        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void testGetNoteById() throws Exception {
        Note note = noteRepository.save(new Note(null, "Nota de prueba", "Contenido", null, null));

        mockMvc.perform(get("/api/notes/{id}", note.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Nota de prueba"))
                .andExpect(jsonPath("$.content").value("Contenido"));
    }

    @Test
    void testUpdateNote() throws Exception {
        Note existingNote = noteRepository.save(new Note(null, "Antigua Nota", "Viejo Contenido", null, null));
        existingNote.setTitle("Nueva Nota");
        existingNote.setContent("Nuevo Contenido");

        mockMvc.perform(put("/api/notes/{id}", existingNote.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingNote)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Nueva Nota"))
                .andExpect(jsonPath("$.content").value("Nuevo Contenido"));
    }

    @Test
    void testDeleteNote() throws Exception {
        Note note = noteRepository.save(new Note(null, "Eliminar Nota", "Contenido a eliminar", null, null));

        mockMvc.perform(delete("/api/notes/{id}", note.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/notes/{id}", note.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearchNotes() throws Exception {
        noteRepository.saveAll(List.of(
                new Note(null, "Buscar Nota 1", "Contenido 1", null, null),
                new Note(null, "Buscar Nota 2", "Contenido 2", null, null)
        ));

        mockMvc.perform(get("/api/notes/search")
                        .param("query", "Buscar")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(2));
    }
}
