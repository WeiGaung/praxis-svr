package com.noriental.praxissvr.utils;

import java.util.Map;

/**
 * @author chenlihua
 * @date 2015/12/25
 * @time 15:47
 */
public class MapParamsUtils {
    public static void setChapter(long chapterId, int chapterLevel, Map<String, Object> params) {
        switch (chapterLevel) {
            case 1:
                params.put("chapter1Id", chapterId);
                break;
            case 2:
                params.put("chapter2Id", chapterId);
                break;
            case 3:
                params.put("chapter3Id", chapterId);
                break;
            default:

        }
    }
}
