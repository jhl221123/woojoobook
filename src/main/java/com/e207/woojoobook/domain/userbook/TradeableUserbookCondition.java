package com.e207.woojoobook.domain.userbook;

import java.util.List;

import lombok.Builder;

@Builder
public record TradeableUserbookCondition(String keyword, Long userId, List<String> areaCodeList, RegisterType registerType) {
}
