package com.dh.notesapp.controller;

import com.dh.notesapp.model.Note;
import com.dh.notesapp.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @Operation(summary = "Obtener todas las notas", description = "Devuelve una lista de todas las notas disponibles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notas recuperadas exitosamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<Note>> getAllNotes() {
        return ResponseEntity.ok(noteService.getAllNotes());
    }

    @Operation(summary = "Obtener una nota por ID", description = "Devuelve una nota si el ID es válido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota encontrada"),
            @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Note> getNoteById(@PathVariable("id") Long id) {
        Optional<Note> note = noteService.getNoteById(id);
        return note.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear una nueva nota", description = "Permite crear una nueva nota válida.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Nota creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Note> createNote(@Valid @RequestBody Note note) {
        Note savedNote = noteService.createNote(note);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNote);
    }

    @Operation(summary = "Actualizar una nota existente", description = "Permite actualizar una nota si el ID es válido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nota actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Nota no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> updateNote(@PathVariable("id") Long id, @Valid @RequestBody Note updatedNote) {
        Optional<Note> existingNote = noteService.getNoteById(id);

        if (existingNote.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "La nota con ID " + id + " no fue encontrada."));
        }

        Note note = existingNote.get();
        note.setTitle(updatedNote.getTitle());
        note.setContent(updatedNote.getContent());
        note.setUpdatedAt(LocalDateTime.now());
        noteService.updateNote(note);

        return ResponseEntity.ok(note);
    }

    @Operation(summary = "Eliminar una nota por ID", description = "Permite eliminar una nota si el ID es válido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Nota eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Nota no encontrada"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> deleteNoteById(@PathVariable("id") Long id) {
        Optional<Note> existingNote = noteService.getNoteById(id);

        if (existingNote.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "La nota con ID " + id + " no fue encontrada."));
        }

        noteService.deleteNoteById(id);
        return ResponseEntity.noContent().build();
    }
}
