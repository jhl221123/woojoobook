package com.e207.woojoobook.api.userbook;

import static com.e207.woojoobook.domain.userbook.QualityStatus.*;
import static com.e207.woojoobook.domain.userbook.RegisterType.*;
import static java.lang.Boolean.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import com.e207.woojoobook.api.book.response.BookItem;
import com.e207.woojoobook.api.rental.RentalService;
import com.e207.woojoobook.api.user.response.UserResponse;
import com.e207.woojoobook.api.userbook.request.UserbookCreateRequest;
import com.e207.woojoobook.api.userbook.request.UserbookListRequest;
import com.e207.woojoobook.api.userbook.request.UserbookUpdateRequest;
import com.e207.woojoobook.api.userbook.response.UserbookCountResponse;
import com.e207.woojoobook.api.userbook.response.UserbookResponse;
import com.e207.woojoobook.api.userbook.response.UserbookWithLike;
import com.e207.woojoobook.domain.user.UserRepository;
import com.e207.woojoobook.domain.userbook.TradeStatus;
import com.e207.woojoobook.global.security.SecurityConfig;
import com.e207.woojoobook.restdocs.AbstractRestDocsTest;

@Import({SecurityConfig.class})
@WebMvcTest(controllers = UserbookController.class)
class UserbookControllerTest extends AbstractRestDocsTest {

	@MockBean
	private UserbookService userbookService;
	@MockBean
	private UserRepository userRepository;
	@MockBean
	private RentalService rentalService;

	@WithMockUser
	@DisplayName("사용자 도서를 등록한다.")
	@Test
	void createSuccess() throws Exception {
		// given
		UserbookCreateRequest request = new UserbookCreateRequest("001", RENTAL_EXCHANGE, GOOD);
		String requestJson = objectMapper.writeValueAsString(request);

		UserbookResponse response = createUserbookResponse(1);
		String responseJson = objectMapper.writeValueAsString(response);
		given(userbookService.createUserbook(request)).willReturn(response);

		// expected
		mockMvc.perform(post("/userbooks").contentType(MediaType.APPLICATION_JSON).content(requestJson))
			.andExpect(content().json(responseJson))
			.andExpect(status().isOk());
	}

	@WithMockUser
	@DisplayName("사용자 도서를 수정한다.")
	@Test
	void updateSuccess() throws Exception {
		// given
		Long userId = 1L;
		UserbookUpdateRequest request = new UserbookUpdateRequest(TRUE, TRUE, VERY_GOOD);
		String requestJson = objectMapper.writeValueAsString(request);

		UserbookResponse response = createUserbookResponse(1);
		String responseJson = objectMapper.writeValueAsString(response);
		given(userbookService.updateUserbook(userId, request)).willReturn(response);

		// expected
		mockMvc.perform(
				put("/userbooks/{userbookId}", userId).contentType(MediaType.APPLICATION_JSON).content(requestJson))
			.andExpect(content().json(responseJson))
			.andExpect(status().isOk());

	}

	@WithMockUser
	@DisplayName("사용자 도서 목록을 조회한다.")
	@Test
	void findListSuccess() throws Exception {
		// given
		List<UserbookWithLike> response = createUserbookWithLikeList();
		String responseJson = objectMapper.writeValueAsString(response);
		given(userbookService.findUserbookWithLikeResponse(any())).willReturn(response);

		// expected
		mockMvc.perform(get("/userbooks")
				.param("areaCode", "12345678")
				.param("keyword", "")
				.param("userbookId", "")
				.param("pageSize", "10"))
			.andExpect(status().isOk())
			.andExpect(content().json(responseJson));
	}

	@DisplayName("총 사용자 도서 개수를 조회한다.")
	@Test
	void countAllUserbookSuccess() throws Exception {
		// given
		Long expectedCount = 1_000_000_000L;
		String expectedResponse = objectMapper.writeValueAsString(new UserbookCountResponse(expectedCount));
		given(userbookService.countAllUserbook()).willReturn(expectedCount);

		// when
		ResultActions action = mockMvc.perform(get("/userbooks/count"));

		// then
		action.andExpect(status().isOk())
			.andExpect(content().json(expectedResponse));
	}

	private UserbookListRequest createFindRequest() {
		return new UserbookListRequest("search keyword", null,
			null);
	}

	private List<UserbookWithLike> createUserbookWithLikeList() {
		List<UserbookWithLike> userbookList = new ArrayList<>();
		for (int i = 1; i <= 3; i++) {
			BookItem bookItem = createBookResponse(String.valueOf(i), "title" + i);
			UserResponse userResponse = createUserResponse(Long.valueOf(i), "test" + i + "@test.com", "test" + i);
			userbookList.add(new UserbookWithLike(Long.valueOf(i), bookItem, userResponse,
				RENTAL_EXCHANGE, RENTAL_EXCHANGE.getDefaultTradeStatus(), GOOD, "12345678", true));
		}
		return userbookList;
	}

	private UserbookResponse createUserbookResponse(int idx) {
		return UserbookResponse.builder()
			.id((long)idx)
			.bookInfo(createBookResponse("00" + idx, "title" + idx))
			.ownerInfo(createUserResponse((long)idx, "user" + idx + "@email.com", "nickname" + idx))
			.registerType(RENTAL_EXCHANGE)
			.tradeStatus(TradeStatus.RENTAL_EXCHANGE_AVAILABLE)
			.qualityStatus(GOOD)
			.areaCode("2644056000")
			.build();
	}

	private UserResponse createUserResponse(Long id, String email, String nickname) {
		return UserResponse.builder().id(id).email(email).nickname(nickname).build();
	}

	private BookItem createBookResponse(String isbn, String title) {
		return BookItem.builder()
			.isbn(isbn)
			.title(title)
			.author("author")
			.publisher("publisher")
			.publicationDate(LocalDate.of(2021, 6, 15))
			.thumbnail("http://example.com/image.jpg")
			.description("description")
			.build();
	}

}