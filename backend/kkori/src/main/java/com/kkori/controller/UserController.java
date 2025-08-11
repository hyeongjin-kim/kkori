package com.kkori.controller;

import com.kkori.common.CommonApiResponse;
import com.kkori.dto.response.UserProfileResponse;
import com.kkori.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kkori.annotation.LoginUser;
import com.kkori.entity.User;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<CommonApiResponse<UserProfileResponse>> getUserInfo(@LoginUser Long userId) {
        User user = userService.findById(userId);
        System.out.println(user.getNickname());
        return ResponseEntity.ok(CommonApiResponse.ok(new UserProfileResponse(user.getNickname())));
    }
}
