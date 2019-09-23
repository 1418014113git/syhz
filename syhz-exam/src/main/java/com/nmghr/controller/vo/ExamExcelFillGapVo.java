package com.nmghr.controller.vo;

import com.sargeraswang.util.ExcelUtil.ExcelCell;

public class ExamExcelFillGapVo {

    @ExcelCell(defaultValue="题目内容", index = 1)
    private String content;
    @ExcelCell(defaultValue="次序", index = 2)
    private String order;
    @ExcelCell(defaultValue="题目解析", index = 3)
    private String answerReason;
    @ExcelCell(defaultValue="正确答案", index = 4)
    private String answer;
    @ExcelCell(defaultValue="出处", index = 5)
    private String from;

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAnswerReason() {
        return answerReason;
    }

    public void setAnswerReason(String answerReason) {
        this.answerReason = answerReason;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public String toString() {
        return "ExamExcelFillGapVo{" +
                "content='" + content + '\'' +
                ", order='" + order + '\'' +
                ", answerReason='" + answerReason + '\'' +
                ", answer='" + answer + '\'' +
                ", from='" + from + '\'' +
                '}';
    }
}
