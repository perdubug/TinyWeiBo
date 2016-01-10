package com.m7yang.tinyweibo.M;

/**
 * Created by m7yang on 15-12-30.
 *
 * This class is model and contains the data I will save in the database and show in the user interface
 *
 * Each class instance present a database record
 */
public class DataModel_WB {

    // Below variables mapping to database table columns
    private Long   m_id;
    private Long   m_wb_id;
    private String m_content;
    private String m_created_date;

    public Long getId() {
        return m_id;
    }

    public void setId(Long id) {
        this.m_id = id;
    }

    public Long getWBId() {
        return m_wb_id;
    }

    public void setWBId(Long wbid) {
        this.m_wb_id = wbid;
    }

    public String getContent() {
        return m_content;
    }

    public void setContent(String content) {
        this.m_content = content;
    }

    public String getCreatedDate() {
        return m_created_date;
    }

    public void setCreatedDate(String created_date) {
        this.m_created_date = created_date;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return m_content;
    }
}
