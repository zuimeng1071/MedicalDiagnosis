package com.ai.medical_diagnosis.controller.user;


import com.ai.medical_diagnosis.domain.dto.UserLoginDto;
import com.ai.medical_diagnosis.domain.po.User;
import com.ai.medical_diagnosis.domain.vo.UseInfoVo;
import com.ai.medical_diagnosis.result.Result;
import com.ai.medical_diagnosis.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController("UserController_User")
@RequestMapping("/user/user")
@Tag(name = "用户管理")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    //  登录
    @PostMapping("login")
    @Operation(summary = "登录")
    public Result<String> login(@RequestBody UserLoginDto userLoginDto) {
        return userService.login(userLoginDto);
    }

    //  注册
    @PostMapping("register")
    @Operation(summary = "注册")
    public Result<Boolean> register(@RequestBody User user) {
        return userService.register(user);
    }

    // 退出登录
    @GetMapping("logout")
    @Operation(summary = "退出登录")
    public Result<Boolean> logout() {
        return userService.logout();
    }

    @PostMapping("updateUser")
    @Operation(summary = "更新用户")
    public Result<UseInfoVo> updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("getUserInfo")
    @Operation(summary = "获取用户信息")
    public Result<UseInfoVo> getUserInfo() {
        return userService.getUserInfo();
    }
}
