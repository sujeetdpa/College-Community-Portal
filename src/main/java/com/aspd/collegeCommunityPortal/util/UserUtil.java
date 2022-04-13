package com.aspd.collegeCommunityPortal.util;

import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.time.LocalDate;

@Service
public class UserUtil {
    public boolean validateUsername(String username){
        String domain="mmmut.ac.in";
        String[] split = username.split("@");
        if (!split[1].equals(domain)){
            return false;
        }
        return true;
    }

    public String getUniversityId(String username){
        return username.split("@")[0];
    }
}
