package kr.co.expernet.rest.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import kr.co.expernet.common.domain.Book;
import kr.co.expernet.common.service.BookService;
import kr.co.expernet.rest.BookResourceAssembler;
import kr.co.expernet.rest.domain.BookResource;
import kr.co.expernet.rest.exception.ResourceNotFoundException;

@RestController
@RequestMapping(value = "/books")
public class BookController {
	@Autowired
	BookService bookService;

	@RequestMapping(method = RequestMethod.GET)
	public Resources<BookResource> getBooks(Model model) {
		List<Book> books = bookService.getBooks();
		BookResourceAssembler assembler = new BookResourceAssembler();
		List<BookResource> resource = assembler.toResources(books);
		Resources<BookResource> wrapped = new Resources<>(resource, linkTo(BookController.class).withSelfRel());
		return wrapped;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public BookResource getBook(@PathVariable("id") Long id) {
		Book book = bookService.getBook(id);
		if (book == null) {
			throw new ResourceNotFoundException();
		}
		BookResourceAssembler assembler = new BookResourceAssembler();
		BookResource resource = assembler.toResource(book);
		Link link = new Link("http://localhost:8080/tnplatform/books/1/reviews", "reviews");
		resource.add(link);
		return resource;
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public Book createBook(@RequestBody Book book) {
		bookService.createBook(book);
		Book selectedBook = bookService.getBook(book.getId());
		return selectedBook;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.OK)
	public Book updateBook(@PathVariable("id") Long id, @RequestBody Book book) {
		bookService.updateBook(book);
		Book selectedBook = bookService.getBook(book.getId());
		return selectedBook;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	public Book deleteBook(@PathVariable("id") Long id) {
		bookService.deleteBook(id);
		Book deleteBook = new Book();
		deleteBook.setId(id);
		return deleteBook;
	}
}
