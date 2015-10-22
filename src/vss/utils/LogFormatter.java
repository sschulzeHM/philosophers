package vss.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Created by stefanschulze on 14.10.15.
 */
public final class LogFormatter extends Formatter
{

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Override
    public String format(LogRecord record)
    {
        StringBuilder sb = new StringBuilder();

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
        Date date = new Date(record.getMillis());
        sb.append(dateFormat.format(date))
                .append(" ")
                .append("[" + record.getThreadID() + "]")
                .append(" ")
                .append(record.getLevel().getLocalizedName())
                .append(": ")
                .append(formatMessage(record))
                .append(LINE_SEPARATOR);

        if (record.getThrown() != null)
        {
            try
            {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            }
            catch (Exception ex)
            {
                // ignore
            }
        }

        return sb.toString();
    }
}