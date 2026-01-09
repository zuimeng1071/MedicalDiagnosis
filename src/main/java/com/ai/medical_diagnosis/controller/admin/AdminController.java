package com.ai.medical_diagnosis.controller.admin;


import com.ai.medical_diagnosis.domain.dto.AdminLoginDto;
import com.ai.medical_diagnosis.domain.dto.UserPageQueryDto;
import com.ai.medical_diagnosis.domain.po.AdminUser;
import com.ai.medical_diagnosis.domain.vo.UseInfoVo;
import com.ai.medical_diagnosis.result.PageResult;
import com.ai.medical_diagnosis.result.Result;
import com.ai.medical_diagnosis.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/admin/admin")
@Tag(name = "管理员管理")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    //  登录
    @PostMapping("login")
    @Operation(summary = "登录")
    public Result<String> login(@RequestBody AdminLoginDto adminLoginDto) {
        return adminService.login(adminLoginDto);
    }

    //  注册
    @PostMapping("register")
    @Operation(summary = "注册")
    public Result<Boolean> register(@RequestBody AdminUser adminUser) {
        return adminService.register(adminUser);
    }

    // 退出登录
    @GetMapping("logout")
    @Operation(summary = "退出登录")
    public Result<Boolean> logout() {
        return adminService.logout();
    }

    @GetMapping("deleteUser/{id}")
    @Operation(summary = "删除用户")
    public Result<Boolean> deleteUser(@PathVariable Long id) {
        return adminService.deleteUserbyId(id);
    }

    @PostMapping("queryUser")
    @Operation(summary = "查询用户")
    public Result<PageResult<UseInfoVo>> queryUser(@RequestBody UserPageQueryDto dto) {
        return adminService.queryUser(dto);
    }
}
