package kr.co.expernet.rest.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import kr.co.expernet.common.domain.Book;

/**
 * <h1>XML to Object AND Object to XML.</h1> <br>
 * <b>@XmlRootElement</b> : XML의 Root Element 명을 정의. <br>
 * <b>@XmlElement</b> : XML의 Element 명을 정의. <br>
 * <b>@XmlType</b> : XML 스키마 이름과 namespace를 정의. propOrder 속성을 이용해서 XML 표현 시 요소들의 표현 순서 정의. <br>
 * <b>@XmlElementWrapper</b> : 다른 XML 요소들을 감싸는 역할을 한다. List 같은 컬렉션 객체들을 XML 변환할 때 사용<br> 
 * @author kei
 */
@XmlRootElement(name = "books")
public class BookList {
	private List<Book> books;

	public BookList() {
	}

	public BookList(List<Book> books) {
		setBooks(books);
	}

	@XmlElement(name = "book")
	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
	}

}
