package com.boot.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.boot.domain.Book;
import com.boot.repository.BookRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookService {
	@Autowired
	private BookRepository bookRepository;
	
	public Book saveBook(Book book) {
		log.info("@# saveBook()");
		log.info("@# Book ==> " + book);
		
		return bookRepository.save(book);
	}
	
	public Book getBookOne(long id) {
		log.info("@# getBookOne()");
		
		return bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("id를 확인해 주세요!"));
	}
	
	public List<Book> getBookAll() {
		log.info("@# getBookAll()");
		
		return bookRepository.findAll();
	}
	
	public Book modifyBook(Long id, Book book) {
		log.info("@# modifyBook()");
		Book bookEntity = bookRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("id를 확인해 주세요!"));
		
		bookEntity.setTitle(book.getTitle());
		bookEntity.setAuthor(book.getAuthor());
		
		bookRepository.save(bookEntity);
		
		return bookEntity;
	}
	
	public String delBook(Long id) {
		log.info("@# delBook()");
		log.info("@# delBook() id ==> " + id);
		
		bookRepository.deleteById(id);
		
		return "delete";
	}
}
