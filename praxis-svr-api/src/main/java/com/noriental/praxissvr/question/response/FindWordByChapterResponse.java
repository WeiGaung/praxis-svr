package com.noriental.praxissvr.question.response;

import com.noriental.praxissvr.question.bean.Word;
import com.noriental.validate.bean.CommonDes;

import java.util.List;

/**
 * Created by hushuang on 2017/7/6.
 * 通过章节ID查询单词列表的响应
 */
public class FindWordByChapterResponse extends CommonDes {

    private List<Word> wordList;

    public FindWordByChapterResponse() {
    }

    public FindWordByChapterResponse(List<Word> wordList) {
        this.wordList = wordList;
    }

    public List<Word> getWordList() {
        return wordList;
    }

    public void setWordList(List<Word> wordList) {
        this.wordList = wordList;
    }

    @Override
    public String toString() {
        return "FindWordByChapterResponse{" +
                "wordList=" + wordList +
                '}';
    }
}
