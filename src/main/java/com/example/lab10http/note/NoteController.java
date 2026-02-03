package com.example.lab10http.note;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

  private final NoteRepository noteRepository;

  public NoteController(NoteRepository noteRepository) {
    this.noteRepository = noteRepository;
  }

  // Create note (owner is always the logged-in user)
  @PostMapping
  public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, String> body,
      Authentication auth) {
    String content = body.get("content");
    if (content == null || content.isBlank()) {
      return ResponseEntity.badRequest().body(Map.of(
          "status", 400,
          "error", "content is required"));
    }

    String username = auth.getName();
    Note saved = noteRepository.save(new Note(content, username));

    return ResponseEntity.status(201).body(Map.of(
        "id", saved.getId(),
        "content", saved.getContent(),
        "ownerUsername", saved.getOwnerUsername()));
  }

  // List only my notes
  @GetMapping
  public ResponseEntity<List<Note>> myNotes(Authentication auth) {
    return ResponseEntity.ok(noteRepository.findAllByOwnerUsername(auth.getName()));
  }

  // Read note ONLY if it belongs to me (404 if not)
  @GetMapping("/{id}")
  public ResponseEntity<?> getOne(@PathVariable Long id, Authentication auth) {
    return noteRepository.findByIdAndOwnerUsername(id, auth.getName())
        .<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.status(404).body(Map.of(
            "status", 404,
            "error", "Note not found")));
  }

  // Update note ONLY if it belongs to me
  @PutMapping("/{id}")
  public ResponseEntity<?> update(@PathVariable Long id,
      @RequestBody Map<String, String> body,
      Authentication auth) {
    String newContent = body.get("content");
    if (newContent == null || newContent.isBlank()) {
      return ResponseEntity.badRequest().body(Map.of(
          "status", 400,
          "error", "content is required"));
    }

    return noteRepository.findByIdAndOwnerUsername(id, auth.getName())
        .<ResponseEntity<?>>map(note -> {
          note.setContent(newContent);
          Note saved = noteRepository.save(note);
          return ResponseEntity.ok(saved);
        })
        .orElseGet(() -> ResponseEntity.status(404).body(Map.of(
            "status", 404,
            "error", "Note not found")));
  }

  // Delete note ONLY if it belongs to me
  @Transactional
  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable Long id, Authentication auth) {
    long deleted = noteRepository.deleteByIdAndOwnerUsername(id, auth.getName());
    if (deleted == 0) {
      return ResponseEntity.status(404).body(Map.of(
          "status", 404,
          "error", "Note not found"));
    }
    return ResponseEntity.noContent().build();
  }
}
