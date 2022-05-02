package com.aspd.collegeCommunityPortal.util;

import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;

@Service
public class TimeUtil {

    public String getCreationTimestamp(LocalDateTime creationDateTime){
        String result="";
        long hours = ChronoUnit.HOURS.between(creationDateTime, LocalDateTime.now());
        long days = ChronoUnit.DAYS.between(creationDateTime, LocalDateTime.now());
        if(hours<24){
            result=hours+" hours ago";
        }
        else if(days<2){
            result=days+" day ago";
        }
        else if(days <10){
            result=days+ " days ago";
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
        String loginTime = lastLogin.toLocalTime().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
        return loginDate+" AT "+loginTime;
    }
}
