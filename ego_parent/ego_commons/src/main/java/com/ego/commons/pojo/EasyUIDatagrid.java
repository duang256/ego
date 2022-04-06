package com.ego.commons.pojo;

import java.util.List;

/**
 * EasyUI datagrid数据模板
 */
public class EasyUIDatagrid {
    private List<?> rows;
    private long total;

    public EasyUIDatagrid() {
    }

    public EasyUIDatagrid(List<?> rows, long total) {
        this.rows = rows;
        this.total = total;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
