package com.nmghr.controller.vo;


import com.sargeraswang.util.ExcelUtil.ExcelCell;

public class ExamExcelMutiChoiceVo {
    @ExcelCell(defaultValue="序号", index = 0)
    private String index;
    @ExcelCell(defaultValue="题目内容", index = 1)
    private String content;
    @ExcelCell(defaultValue="选项A", index = 2)
    private String choiceA;
    @ExcelCell(defaultValue="选项B", index = 3)
    private String choiceB;
    @ExcelCell(defaultValue="选项C", index = 4)
    private String choiceC;
    @ExcelCell(defaultValue="选项D", index = 5)
    private String choiceD;
    @ExcelCell(defaultValue="选项E", index = 6)
    private String choiceE;
    @ExcelCell(defaultValue="选项F", index = 7)
    private String choiceF;
    @ExcelCell(defaultValue="正确答案", index = 8)
    private String answer;
    @ExcelCell(defaultValue="题目解析", index = 9)
    private String analysis;
    @ExcelCell(defaultValue="出处", index = 10)
    private String source;
    @ExcelCell(defaultValue ="次序",index = 11)
    private String sort;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getChoiceE() {
        return choiceE;
    }

    public void setChoiceE(String choiceE) {
        this.choiceE = choiceE;
    }

    public String getChoiceF() {
        return choiceF;
    }

    public void setChoiceF(String choiceF) {
        this.choiceF = choiceF;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
