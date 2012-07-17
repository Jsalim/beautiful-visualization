package es.tid.haewoon.cdr;

import static java.util.Calendar.HOUR_OF_DAY;

import java.util.Calendar;

public class HourFilter implements CDRFilter {
    private final int startHour;
    private final int endHour;
    
    public HourFilter(int startHour, int endHour) {
        this.startHour = startHour;
        this.endHour = endHour;
    }
    
    @Override
    public boolean filter(CDR cdr) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(cdr.getDatetime());
        int hour = calendar.get(HOUR_OF_DAY);
             
        return (hour > startHour) && (hour < endHour);
    }
    
    @Override
    public String toString() {
        return startHour + "-" + endHour;
    }

}
