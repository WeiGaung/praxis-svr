package com.noriental.praxissvr.question.request;

import com.noriental.validate.bean.BaseRequest;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by liujiang on 2017/10/17.
 * 更新题中的html请求
 */
public class UpdateQuestionHtmlImgRequest extends BaseRequest {

    //习题ID
    private long id;
    //含html的json习题内容
    @NotBlank
    private String html;

    public UpdateQuestionHtmlImgRequest() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    @Override
    public String toString() {
        return "UpdateQuestionHtmlImgRequest{" +
                "id=" + id +
                ", html='" + html + '\'' +
                '}';
    }
}
