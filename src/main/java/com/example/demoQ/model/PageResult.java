package com.example.demoQ.model;

import java.util.List;

public class PageResult<E> {

    private List<E> data;

    private int total;

    public List<E> getData() {
        return data;
    }

    public void setData(List<E> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
