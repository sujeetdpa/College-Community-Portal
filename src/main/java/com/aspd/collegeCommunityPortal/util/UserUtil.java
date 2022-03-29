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
        try{
            int year=Integer.parseInt(split[0].substring(0,4));
            int rem=Integer.parseInt(split[0].substring(4));
            if(!(year>=2000 && year<=LocalDate.now().getYear())) {
                return false;
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }

    public String getUniversityId(String username){
        return username.split("@")[0];
    }
}
