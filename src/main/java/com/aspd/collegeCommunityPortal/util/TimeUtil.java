package com.aspd.collegeCommunityPortal.util;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class TimeUtil {

    public String getCreationTimestamp(LocalDateTime creationDateTime){
        String result="";
        if(creationDateTime.toLocalDate().equals(LocalDate.now())){
            long seconds = Duration.between(creationDateTime.toLocalTime(), LocalTime.now()).getSeconds();
            result=(seconds/3600)+" hours ago";
        }
        else{
            result=creationDateTime.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE).toString();
        }
        return result;
    }

    public String getUserJoinDate(LocalDateTime joinDate){
        return joinDate.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE).toString();
    }

    public String getLastLoginTimestamp(LocalDateTime lastLogin){
        String loginDate = lastLogin.toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE).toString();
        String loginTime = lastLogin.toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME).toString();
        return loginDate+" AT "+loginTime;
    }
}
