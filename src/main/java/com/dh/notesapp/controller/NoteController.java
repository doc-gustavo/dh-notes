package com.dh.notesapp.controller;

import com.dh.notesapp.model.Note;
import com.dh.notesapp.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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
            @ApiResponse(responseCode = "400", description = "ID inválido"),
            @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> getNoteById(@PathVariable Long id) {
        try {
            Note note = noteService.getNoteById(id);
            return ResponseEntity.ok(note);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "ID inválido", "message", e.getMessage()));
        }
    }

    @Operation(summary = "Buscar notas", description = "Busca notas por título o contenido con paginación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notas encontradas"),
            @ApiResponse(responseCode = "400", description = "Parámetro de búsqueda inválido")
    })
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> searchNotes(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        try {
            Page<Note> notes = noteService.searchNotes(query, page, size);
            if (notes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No se encontraron notas."));
            }
            return ResponseEntity.ok(notes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Parámetros de paginación no válidos."));
        }
    }

    @Operation(summary = "Crear una nueva nota", description = "Permite crear una nueva nota válida.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Nota creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<?> createNote(@Valid @RequestBody Note note) {
        try {
            Note savedNote = noteService.createNote(note);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedNote);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
