package com.nmghr.controller.vo;


import com.sargeraswang.util.ExcelUtil.ExcelCell;

public class ExamExcelMutiChoiceVo {
    @ExcelCell(defaultValue="题目内容", index = 1)
    private String content;
    @ExcelCell(defaultValue="次序", index = 2)
    private String order;
    @ExcelCell(defaultValue="题目解析", index = 3)
    private String answerReason;
    @ExcelCell(defaultValue="选项A", index = 4)
    private String choiceA;
    @ExcelCell(defaultValue="选项B", index = 5)
    private String choiceB;
    @ExcelCell(defaultValue="选项C", index = 6)
    private String choiceC;
    @ExcelCell(defaultValue="选项D", index = 7)
    private String choiceD;
    @ExcelCell(defaultValue="正确答案", index = 8)
    private String answer;
    @ExcelCell(defaultValue="出处", index = 9)
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

    public String getChoiceA() {
        return choiceA;
    }

    public void setChoiceA(String choiceA) {
        this.choiceA = choiceA;
    }

    public String getChoiceB() {
        return choiceB;
    }

    public void setChoiceB(String choiceB) {
        this.choiceB = choiceB;
    }

    public String getChoiceC() {
        return choiceC;
    }

    public void setChoiceC(String choiceC) {
        this.choiceC = choiceC;
    }

    public String getChoiceD() {
        return choiceD;
    }

    public void setChoiceD(String choiceD) {
        this.choiceD = choiceD;
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
        return "ExamExcelMutiChoiceVo{" +
                "content='" + content + '\'' +
                ", order='" + order + '\'' +
                ", answerReason='" + answerReason + '\'' +
                ", choiceA='" + choiceA + '\'' +
                ", choiceB='" + choiceB + '\'' +
                ", choiceC='" + choiceC + '\'' +
                ", choiceD='" + choiceD + '\'' +
                ", answer='" + answer + '\'' +
                ", from='" + from + '\'' +
                '}';
    }
}
