package com.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.boot.Service.BookService;
import com.boot.domain.Book;


@RestController
public class BookContoller {
	
	@Autowired
	private BookService bookService;
	
	/*
	@GetMapping("/")
	public ResponseEntity<?> findAll() {
		return new ResponseEntity<>("ok", HttpStatus.OK);
		//return new ResponseEntity<>("ok", HttpStatus.NOT_FOUND);
	}
	*/
	
	@CrossOrigin //자바스크립트 요청을 허용
	@GetMapping("/book")
	public ResponseEntity<?> findAll() {
		return new ResponseEntity<>(bookService.getBookAll(), HttpStatus.OK);
	}
	
	@CrossOrigin
	@PostMapping("/book")
	//public ResponseEntity<Book> saveBook(Book book) {
	public ResponseEntity<Book> saveBook(@RequestBody Book book) {
		//@RequestBody: xml이나 json기반의 메시지를 사용하는 요청의 경우
		return new ResponseEntity<>(bookService.saveBook(book), HttpStatus.OK);
	}
	
	@CrossOrigin
	@GetMapping("/book/{id}")
	public ResponseEntity<Book> getBookOne(@PathVariable(name = "id") Long id) {
		return new ResponseEntity<>(bookService.getBookOne(id), HttpStatus.OK);
	}
	
	@CrossOrigin
	@PutMapping("/book/{id}")
	public ResponseEntity<Book> getBookOne(@PathVariable(name = "id") Long id, @RequestBody Book book) {
		return new ResponseEntity<>(bookService.modifyBook(id, book), HttpStatus.OK);
	}
	
	@CrossOrigin
	@DeleteMapping("/book/{id}")
	public ResponseEntity<String> delBook(@PathVariable(name = "id") Long id) {
		return new ResponseEntity<>(bookService.delBook(id), HttpStatus.OK);
	}
}