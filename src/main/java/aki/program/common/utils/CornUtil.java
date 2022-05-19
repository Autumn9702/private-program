package aki.program.common.utils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * @author cf
 * Created in 14:51 2022/5/19
 * Dynamically generate corn expressions
 */
public class CornUtil {
    private static final String[] DATE_FORMATS = {"HH:mm:ss", "yyyy-MM-dd HH:mm:ss"};
    private static final String WEEK_FORMAT = "[00-23]\\d*:[00-59]\\d*:[00-59]\\d* [1-7]\\d*";
    private static final String MONTH_FORMAT = "[01-12]\\d*-[01-31]\\d* [00-23]\\d*:[00-59]\\d*:[00-59]\\d*";
    /**
     * auto generate expressions
     * dateStr format yyyy-MM-dd HH:mm:ss | HH:mm:ss | MM-dd HH:mm:ss | HH:mm:ss 3
     * @param dateStr The date time
     * @return The corn expression
     */
    public static String generateCornExpression(String dateStr){

        String cornFormat = cornFormatOf(dateStr);
        assert cornFormat != null;

        LocalDateTime time = generateLocalDateTime(dateStr);

        if(Objects.isNull(time)){
            return generateLocalTime(dateStr).format(DateTimeFormatter.ofPattern(cornFormat));
        }

        return time.format(DateTimeFormatter.ofPattern(cornFormat));
    }

    private static LocalTime generateLocalTime(String dateStr){
        String normalFormat =  specialFormatProcess(dateStr);

        for (String dateFormat : DATE_FORMATS) {
            try {
                return LocalTime.parse(normalFormat, DateTimeFormatter.ofPattern(dateFormat));
            }catch (DateTimeParseException dtp){
                continue;
            }
        }
        return null;
    }

    private static LocalDateTime generateLocalDateTime(String dateStr){

        String normalFormat =  specialFormatProcess(dateStr);

        for (String dateFormat : DATE_FORMATS) {
            try {
                return LocalDateTime.parse(normalFormat, DateTimeFormatter.ofPattern(dateFormat));
            }catch (DateTimeParseException dtp){
                continue;
            }
        }
        return null;
    }

    /**
     * Match format
     * @return The corn format
     */
    private static String cornFormatOf(String dataStr){

        if(dataStr.matches("00:00:[01-59]\\d*")){
            return "ss * * * * ?";
        }else if(dataStr.matches("00:[01-59]\\d*:00")){
            return "* mm * * * ?";
        }else if(dataStr.matches("[01-59]\\d*:00:00")) {
            return "* * HH * * ?";
        }else if(dataStr.matches("00:[01-59]\\d*:[01-59]\\d*")){
            return "ss mm * * * ?";
        }else if(dataStr.matches("[01-59]\\d*:[01-59]\\d*:[01-59]\\d*")){
            return "ss mm HH * * ?";
        }else if(dataStr.matches("[01-59]\\d*:[01-59]\\d*:[01-59]\\d* [1-7]\\d*")){
            return "ss mm HH ? * " + ((Integer.parseInt(dataStr.substring(9))) == 7 ? 1 : Integer.parseInt(dataStr.substring(9)) +1);
        }else if(dataStr.matches("[01-12]\\d*-[01-31]\\d* [00-23]\\d*:[00-59]\\d*:[00-59]\\d*")){
            return "ss mm HH dd * ?";
        }else if(dataStr.matches("[0-9]{4}-[01-12]\\d*-[01-31]\\d* [00-23]\\d*:[00-59]\\d*:[00-59]\\d*")){
            return "ss mm HH dd MM *";
        }else {
            return null;
        }
    }

    /**
     * Handing special formats
     * @return LocalDateTime convertible formats
     */
    private static String specialFormatProcess(String dateStr){

        String afterProcessOfDate = null;

        if(dateStr.matches(WEEK_FORMAT)){
            afterProcessOfDate = dateStr.substring(0, 8);
        }

        if(dateStr.matches(MONTH_FORMAT)){
            afterProcessOfDate = (LocalDateTime.ofEpochSecond(System.currentTimeMillis() / 1000, 0, ZoneOffset.ofHours(8))).getYear() + "-" + dateStr;
        }

        if(afterProcessOfDate != null){
            return dateStr;
        }
        return afterProcessOfDate;
    }

    public static void main(String[] args) {
        String aa = "00:00:59 3";
        System.out.println(aa.matches("00:00:[01-59]\\d*"));
        String corn = generateCornExpression(aa);
        System.out.println(corn);
    }
}
