package kr.co.expernet.rest.contoller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.CharacterEncodingFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.expernet.common.config.AppConfig;
import kr.co.expernet.common.domain.Book;
import kr.co.expernet.rest.config.RestAppConfig;
import kr.co.expernet.rest.controller.BookController;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { AppConfig.class, RestAppConfig.class })
@Transactional
@Rollback(true)
public class BookControllerAsJsonTest {
	Logger logger = LoggerFactory.getLogger(BookControllerAsJsonTest.class);
	private MockMvc mockMvc;

	@Autowired
	BookController bookController;

	@Before
	public void initMockMvc() {
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		filter.setEncoding("UTF-8");
		filter.setForceEncoding(true);
		mockMvc = MockMvcBuilders.standaloneSetup(bookController).addFilter(filter).build();
	}

	@Test
	public void testGetBooks() throws Exception {
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/books")
				.accept(MediaType.APPLICATION_JSON);
		this.mockMvc.perform(requestBuilder).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.content", Matchers.hasSize(3)))
				.andExpect(jsonPath("$.content[0].bookId", is(1)))
				.andExpect(jsonPath("$.content[0].title", is("명예의 조각들")))
				.andExpect(jsonPath("$.content[0].creator", is("로이스 맥마스터 부졸드"))).andExpect(jsonPath("$.content[1].bookId", is(2)))
				.andExpect(jsonPath("$.content[1].title", is("바라야 내전")))
				.andExpect(jsonPath("$.content[1].creator", is("로이스 맥마스터 부졸드")));
	}
	
	@Test
	public void testGetBook() throws Exception {
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/books/2")
				.accept(MediaType.APPLICATION_JSON);
		this.mockMvc.perform(requestBuilder).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.bookId", is(2)))
				.andExpect(jsonPath("$.title", is("바라야 내전")))
				.andExpect(jsonPath("$.creator", is("로이스 맥마스터 부졸드")));
	}
	
	@Test
	public void testCreateBook() throws Exception {
		String content = "{\"id\":100, \"title\":\"바라야 내전\", \"creator\":\"로이스 맥마스터 부졸드\", \"type\":\"외국판타지소설\", \"date\":1313378460000}";
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/books")
				.contentType(MediaType.APPLICATION_JSON).content(content)
				.accept(MediaType.APPLICATION_JSON);
		this.mockMvc.perform(requestBuilder).andDo(print())
				.andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	}
	
	@Test
	public void testUpdateBook() throws Exception {
		Book updateBook = new Book(3L, "어시스의 마법사", "어슐러 K. 르귄", "판타지소설", new Date());
		ObjectMapper mapper = new ObjectMapper();
		String content = mapper.writeValueAsString(updateBook);
		logger.debug("content = {}", content);
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/books/3")
				.contentType(MediaType.APPLICATION_JSON).content(content).accept(MediaType.APPLICATION_JSON);
		this.mockMvc.perform(requestBuilder).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id", is(3)))
				.andExpect(jsonPath("$.title", is("어시스의 마법사")))
				.andExpect(jsonPath("$.creator", is("어슐러 K. 르귄")));
	}
	
	@Test
	public void testDeleteBook() throws Exception {
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/books/3")
				.accept(MediaType.APPLICATION_JSON);
		this.mockMvc.perform(requestBuilder).andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id", is(3)));
	}
}
