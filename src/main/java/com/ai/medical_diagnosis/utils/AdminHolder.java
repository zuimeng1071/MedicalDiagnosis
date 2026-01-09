package com.ai.medical_diagnosis.utils;


import com.ai.medical_diagnosis.domain.dto.AdminInfoDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdminHolder {
    private static final ThreadLocal<AdminInfoDTO> tl = new ThreadLocal<>();

    public static void saveAdmin(AdminInfoDTO user){
        tl.set(user);
    }

    public static AdminInfoDTO getAdmin(){
        return tl.get();
    }

    public static void removeAdmin(){
        tl.remove();
    }
}
