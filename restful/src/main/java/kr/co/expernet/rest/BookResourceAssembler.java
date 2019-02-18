package kr.co.expernet.rest;

import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import kr.co.expernet.common.domain.Book;
import kr.co.expernet.rest.controller.BookController;
import kr.co.expernet.rest.domain.BookResource;

public class BookResourceAssembler extends ResourceAssemblerSupport<Book, BookResource> {

	public BookResourceAssembler() {
		super(BookController.class, BookResource.class);
	}

	@Override
	public BookResource toResource(Book book) {
		BookResource resource = createResourceWithId(book.getId(), book);
		resource.setBookId(book.getId());
		resource.setTitle(book.getTitle());
		resource.setCreator(book.getCreator());
		resource.setDate(book.getDate());
		resource.setType(book.getType());
		
		return resource;
	}

}
