package com.example.lab10http.note;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {

  List<Note> findAllByOwnerUsername(String ownerUsername);

  Optional<Note> findByIdAndOwnerUsername(Long id, String ownerUsername);

  long deleteByIdAndOwnerUsername(Long id, String ownerUsername);
}
