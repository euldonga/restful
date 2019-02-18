package kr.co.expernet.rest.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.Marshaller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Controller;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "kr.co.expernet.rest.controller" }, useDefaultFilters = false, includeFilters = {
		@Filter(Controller.class), @Filter(ControllerAdvice.class) })
public class RestAppConfig extends WebMvcConfigurerAdapter {
	
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		/* MappingJackson2HttpMessageConverter 등록 */
		converters.add(mappingJacksonHttpMessageConverter());
		/* MarshallingHttpMessageConverter 등록 */
		converters.add(marshallingHttpMessageConverter());
	}
	
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		/* 첫째, favorPathExtenstion 속성값이 true(기본값 true)이고, 요청 경로에 파일 확장 자가 포함되어 있다면
		ContentNegotiationConfigurer에 정의한 mediaTypes 정보를 사용한다. 적절한 미디어 타입을 찾지 못했을 때
		useJaf 속성값이 true(기본값true)이면 Java Activation Framework의
		FileTypeMap.getContentType(String filename) 메소드의 반환 값을 미디어 타입으로 사용한다. */
		
		/* 둘째, favorParameter 속성값이 true(기본값 false)이고, 요청 파라미터에 미디 어 타입을 정의하는 값이 포함되어 있다면
		ContentNegotiationConfigurer에 정의한 mediaTypes 정보를 사용한다. 파라미터의 기본 이름은
		‘format’이고, parameterName이라는 속성으로 변경할 수 있다. */
		
		/* 셋째, 이전 과정에서 미디어 타입을 찾지 못했을 때 ignoreAcceptHeader의 속성 값이 false(기본값 false)이면
		HTTP Header 값의 Accept를 사용한다. */
		
		/* 넷째, 이전 과정을 거치고도 미디어 타입을 찾지 못했을 때 ContentNegotiation Configurer에
		defaultContentType 속성값이 정의되어 있다면 그 값을 미디어 타입으로 사용한다. */
		configurer
		.useJaf(true)
		.favorPathExtension(true)
		.favorParameter(false)
		.ignoreAcceptHeader(false)
		.defaultContentType(MediaType.APPLICATION_JSON)
		.mediaType("json", MediaType.APPLICATION_JSON)
		.mediaType("xml", MediaType.APPLICATION_XML);
	}
	
	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		exceptionResolvers.add(exceptionHandlerExceptionResolver());
	}
	
	@Bean
	public ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver() {
		ExceptionHandlerExceptionResolver resolver = new ExceptionHandlerExceptionResolver();
		List<HttpMessageConverter<?>> converters = resolver.getMessageConverters();
		converters.add(mappingJacksonHttpMessageConverter());
		converters.add(marshallingHttpMessageConverter());
		resolver.setMessageConverters(converters);

		ContentNegotiationManagerFactoryBean contentNegotiationManager = new ContentNegotiationManagerFactoryBean();
		contentNegotiationManager.addMediaType("json", MediaType.APPLICATION_JSON);
		contentNegotiationManager.addMediaType("xml", MediaType.APPLICATION_XML);
		contentNegotiationManager.afterPropertiesSet();
		resolver.setContentNegotiationManager(contentNegotiationManager.getObject());

		return resolver;
	}

	@Bean
	public MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		/* JSON Set Pretty Print */
		converter.setPrettyPrint(true);
		
		/* Json 형식의 데이터에 Root Element를 설정하여 XML 데이터 형식에 맞춤.
		@XmlRootElement(name = "book")
		@JsonRootName("book")
		public class Book {
		...
		} */
		ObjectMapper objectMapper =converter.getObjectMapper();
		objectMapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
		objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
		 
		/*jackson-module-jaxb-annotations 라이브러리를 사용. 
		Json 어노테이션을 선언하지 않고 이미 선언한 JAXB 어노테이션 값을 사용하여 Json 형식을 XML 형식에 맞춤
		@XmlRootElement(name = "book") 하나만 명시하면 됨. */
		JaxbAnnotationModule module = new JaxbAnnotationModule();
		objectMapper.registerModule(module);
		
		return converter;
	}

	@Bean
	public MarshallingHttpMessageConverter marshallingHttpMessageConverter() {
		/* Spring OXM(Object Xml mapping) : XML Marshalling */
		MarshallingHttpMessageConverter converter = new MarshallingHttpMessageConverter();
		/* JAXB marshaller 등록. */ 
		converter.setMarshaller(jaxb2Marshaller());
		converter.setUnmarshaller(jaxb2Marshaller());
		return converter;
	}

	@Bean
	public Jaxb2Marshaller jaxb2Marshaller() {
		/* XML 처리를 위한 JAXB Marshaller/Unmarshaller 추가. */
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setPackagesToScan(new String[] {
				"kr.co.expernet.common.domain",
				"kr.co.expernet.rest.domain",
				"org.springframework.hateoas"
		});
		/* XML Set Pretty Print */
		Map<String, Object> marshallerProperties = new HashMap<>();
		marshallerProperties.put(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setMarshallerProperties(marshallerProperties);
		
		return marshaller;
	}
}
