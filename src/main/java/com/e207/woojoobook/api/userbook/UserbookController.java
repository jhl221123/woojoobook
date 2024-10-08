package com.e207.woojoobook.api.userbook;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.e207.woojoobook.api.rental.RentalService;
import com.e207.woojoobook.api.userbook.request.UserbookCreateRequest;
import com.e207.woojoobook.api.userbook.request.UserbookListRequest;
import com.e207.woojoobook.api.userbook.request.UserbookUpdateRequest;
import com.e207.woojoobook.api.userbook.response.UserbookCountResponse;
import com.e207.woojoobook.api.userbook.response.UserbookResponse;
import com.e207.woojoobook.api.userbook.response.UserbookWithLike;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/userbooks")
@RestController
public class UserbookController {

	private final UserbookService userbookService;
	private final RentalService rentalService;

	@GetMapping
	public ResponseEntity<List<UserbookWithLike>> findUserbookPageList(
		@ModelAttribute UserbookListRequest request) {
		var userbookWithLikeResponse = this.userbookService.findUserbookWithLikeResponse(
			request);
		return ResponseEntity.ok(userbookWithLikeResponse);
	}

	@PostMapping
	public ResponseEntity<UserbookResponse> createUserbook(
		@Valid @RequestBody UserbookCreateRequest userbookCreateRequest) {
		// todo 예외 처리: Validation 예외 처리
		return ResponseEntity.ok(this.userbookService.createUserbook(userbookCreateRequest));
	}

	@PutMapping("/{userbookId}")
	public ResponseEntity<UserbookResponse> updateUserbook(
		@Valid @RequestBody UserbookUpdateRequest userbookUpdateRequest, @PathVariable Long userbookId) {
		// todo 예외 처리: Validation 예외 처리
		return ResponseEntity.ok(this.userbookService.updateUserbook(userbookId, userbookUpdateRequest));
	}

	@ResponseStatus(HttpStatus.OK)
	@PutMapping("/{userbookId}/return")
	public void returnUserbook(@PathVariable Long userbookId) {
		rentalService.giveBackByUserbookId(userbookId);
	}

	@GetMapping("/count")
	public ResponseEntity<UserbookCountResponse> countUserbooks() {
		Long count = this.userbookService.countAllUserbook();
		return ResponseEntity.ok(new UserbookCountResponse(count));
	}
}
