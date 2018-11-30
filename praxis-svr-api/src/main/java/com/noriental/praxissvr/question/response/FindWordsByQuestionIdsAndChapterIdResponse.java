package com.noriental.praxissvr.question.response;

import com.github.miemiedev.mybatis.paginator.domain.PageList;
import com.noriental.praxissvr.question.bean.WordAndChapter;
import com.noriental.validate.bean.CommonDes;

import java.util.List;
import java.util.Map;

/**
 * Created by liujiang on 2017/11/14.
 */
public class FindWordsByQuestionIdsAndChapterIdResponse extends CommonDes {

    /*

    */
    private PageList<WordAndChapter> wordList;

    @Override
    public String toString() {
        return "FindWordsByQuestionIdsAndChapterIdResponse{" +
                "wordList=" + wordList +
                '}';
    }

    public PageList<WordAndChapter> getWordList() {
        return wordList;
    }

    public void setWordList(PageList<WordAndChapter> wordList) {
        this.wordList = wordList;
    }

    public FindWordsByQuestionIdsAndChapterIdResponse(PageList<WordAndChapter> wordList) {
        this.wordList = wordList;
    }
}
