package com.nmghr.controller.vo;

import com.sargeraswang.util.ExcelUtil.ExcelCell;

public class ExamExcelCaseAnalysisVo {
    //论述
    @ExcelCell(defaultValue="序号", index = 0)
    private String index;
    @ExcelCell(defaultValue="题目内容", index = 1)
    private String content;
    @ExcelCell(defaultValue="题目解析", index = 2)
    private String analysis;
    @ExcelCell(defaultValue="出处", index = 3)
    private String source;
    @ExcelCell(defaultValue ="次序",index = 4)
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
