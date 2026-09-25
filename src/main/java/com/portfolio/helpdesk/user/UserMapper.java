package com.portfolio.helpdesk.user;

import com.portfolio.helpdesk.common.config.MapStructConfig;
import com.portfolio.helpdesk.user.dto.UserResponse;
import com.portfolio.helpdesk.user.dto.UserSummary;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface UserMapper {

    UserResponse toResponse(User user);

    UserSummary toSummary(User user);
}