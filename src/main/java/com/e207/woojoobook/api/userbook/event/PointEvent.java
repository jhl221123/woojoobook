package com.e207.woojoobook.api.userbook.event;

import com.e207.woojoobook.domain.user.User;
import com.e207.woojoobook.domain.user.point.PointHistory;

public record PointEvent (User user, PointHistory history){
}
