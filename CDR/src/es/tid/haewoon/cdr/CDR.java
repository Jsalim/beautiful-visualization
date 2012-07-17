package es.tid.haewoon.cdr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CDR {
    String origNum;
    String destNum;
    Date datetime;
    int duration;
    String origOpr;
    String destOpr;
    String initCellID;
    String finCellID;
    String[] errCode;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    
    public CDR(String line) throws ParseException {
        String[] tokens = line.split("\\|");
        origNum = tokens[0];
        destNum = tokens[1];
        
        // 13/09/2009 13:56:33
        datetime = sdf.parse(tokens[2]);
        
        
        duration = Integer.valueOf(tokens[3]);
        origOpr = tokens[4];
        destOpr = tokens[5];
        initCellID = tokens[6];
        finCellID = tokens[7];
        
        errCode = new String[3];
        errCode[0] = tokens[8];
        errCode[1] = tokens[9];
        errCode[2] = tokens[10];
    }

    public String getOrigNum() {
        return origNum;
    }

    public String getDestNum() {
        return destNum;
    }

    public Date getDatetime() {
        return datetime;
    }

    public int getDuration() {
        return duration;
    }

    public String getOrigOpr() {
        return origOpr;
    }

    public String getDestOpr() {
        return destOpr;
    }

    public String getInitCellID() {
        return initCellID;
    }

    public String getFinCellID() {
        return finCellID;
    }

    public String[] getErrCode() {
        return errCode;
    }
}
