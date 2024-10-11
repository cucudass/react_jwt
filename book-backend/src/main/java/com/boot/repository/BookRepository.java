package com.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.boot.domain.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

}
