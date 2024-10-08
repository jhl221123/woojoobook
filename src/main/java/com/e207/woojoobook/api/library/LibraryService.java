package com.e207.woojoobook.api.library;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.e207.woojoobook.api.library.request.LibraryCreateRequest;
import com.e207.woojoobook.api.library.request.LibraryUpdateRequest;
import com.e207.woojoobook.api.library.response.LibraryListResponse;
import com.e207.woojoobook.api.library.response.LibraryResponse;
import com.e207.woojoobook.api.user.UserService;
import com.e207.woojoobook.domain.library.Library;
import com.e207.woojoobook.domain.library.LibraryRepository;
import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.global.exception.ErrorCode;
import com.e207.woojoobook.global.exception.ErrorException;
import com.e207.woojoobook.global.helper.UserHelper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LibraryService {

	private final LibraryRepository libraryRepository;
	private final UserHelper userHelper;
	private final UserService userService;

	private Library findByIdAndUserId(Long categoryId, Long userId) {
		return libraryRepository.findByIdAndUserId(categoryId, userId)
			.orElseThrow(() -> new ErrorException(ErrorCode.NotFound));
	}

	private void verifyOwnership(Long userId, Long currentUserId) {
		if (!userId.equals(currentUserId)) {
			throw new ErrorException(ErrorCode.ForbiddenError);
		}
	}

	@Transactional(readOnly = true)
	public LibraryListResponse findList(Long userId) {
		User user = userService.findDomainById(userId);
		List<Library> categoryList = libraryRepository.findByUserId(userId);

		List<LibraryResponse> libraryResponses = categoryList.stream()
			.map(LibraryResponse::of)
			.collect(Collectors.toList());

		return LibraryListResponse.builder()
			.nickName(user.getNickname())
			.libraryList(libraryResponses)
			.build();
	}

	@Transactional(readOnly = true)
	public LibraryResponse find(Long userId, Long categoryId) {
		Library library = findByIdAndUserId(categoryId, userId);
		return LibraryResponse.of(library);
	}

	@Transactional
	public LibraryResponse create(Long userId, LibraryCreateRequest request) {
		User user = userHelper.findCurrentUser();
		Long newOrderNumber = findMaxOrderNumber(userId) + 1L;
		Library library = libraryRepository.save(Library.builder()
			.user(user)
			.name(request.categoryName())
			.bookList(request.books())
			.orderNumber(newOrderNumber)
			.build());

		return LibraryResponse.of(library);
	}

	@Transactional
	public LibraryResponse update(Long userId, Long categoryId, LibraryUpdateRequest request) {
		Library library = findByIdAndUserId(categoryId, userId);

		Long currentUserId = userHelper.findCurrentUser().getId();
		verifyOwnership(userId, currentUserId);

		library.update(request.categoryName(), request.books());
		return LibraryResponse.of(library);
	}

	@Transactional
	public void delete(Long userId, Long categoryId) {
		Library library = findByIdAndUserId(categoryId, userId);

		Long currentUserId = userHelper.findCurrentUser().getId();
		verifyOwnership(userId, currentUserId);

		libraryRepository.delete(library);
	}

	@Transactional
	public void swapOrderNumber(Long userId, Long fromId, Long toId) {
		Library fromLibrary = findByIdAndUserId(fromId, userId);
		Library toLibrary = findByIdAndUserId(toId, userId);

		Long currentUserId = userHelper.findCurrentUser().getId();
		verifyOwnership(userId, currentUserId);

		Long tempOrderNumber = fromLibrary.getOrderNumber();
		fromLibrary.updateOrderNumber(toLibrary.getOrderNumber());
		toLibrary.updateOrderNumber(tempOrderNumber);

		libraryRepository.save(fromLibrary);
		libraryRepository.save(toLibrary);
	}

	@Transactional(readOnly = true)
	public Long findMaxOrderNumber(Long userId) {
		return libraryRepository.findMaxOrderNumber(userId).orElse(0L);
	}
}
