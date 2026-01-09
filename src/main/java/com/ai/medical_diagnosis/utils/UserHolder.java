package com.ai.medical_diagnosis.utils;


import com.ai.medical_diagnosis.domain.dto.UserInfoDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserHolder {
    private static final ThreadLocal<UserInfoDTO> tl = new ThreadLocal<>();

    public static void saveUser(UserInfoDTO user){
        tl.set(user);
    }

    public static UserInfoDTO getUser(){
        return tl.get();
    }

    public static void removeUser(){
        tl.remove();
    }
}
