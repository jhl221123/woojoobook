package com.e207.woojoobook.api.user.request;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class VerificationMail {
	@Email
	private String email;
	private String verificationCode;
}
