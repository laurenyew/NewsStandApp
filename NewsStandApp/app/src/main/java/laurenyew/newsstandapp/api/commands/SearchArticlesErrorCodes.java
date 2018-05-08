package laurenyew.newsstandapp.api.commands;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class SearchArticlesErrorCodes {
    @Retention(SOURCE)
    @IntDef({SEARCH_API_CALL_FAILED, EXCEPTION_OCCURRED_WHILE_MAKING_CALL, EXCEPTION_OCCURRED_WHILE_PARSING_DATA, SEARCH_API_RATE_EXCEEDED})
    public @interface SEARCH_ARTICLES_ERROR_CODES {
    }

    public static final int SEARCH_API_CALL_FAILED = 1;
    public static final int EXCEPTION_OCCURRED_WHILE_MAKING_CALL = 2;
    public static final int EXCEPTION_OCCURRED_WHILE_PARSING_DATA = 3;
    public static final int SEARCH_API_RATE_EXCEEDED = 4;
}
