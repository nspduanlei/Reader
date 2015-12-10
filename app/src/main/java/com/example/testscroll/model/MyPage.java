package com.example.testscroll.model;

/**
 * Author: duanlei
 * Date: 2015-12-10
 */
public class MyPage {
    private int pageIndex;
    private int pageSize;

    public MyPage(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "MyPage{" +
                "pageIndex=" + pageIndex +
                ", pageSize=" + pageSize +
                '}';
    }
}
