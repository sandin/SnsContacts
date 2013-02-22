package com.lds.snscontacts.model;

import java.util.List;

public class WeiboUsers {

    private List<WeiboUser> users;

    private long next_cursor;
    private long previous_cursor;

    private long total_number;

    public List<WeiboUser> getUsers() {
        return users;
    }

    public void setUsers(List<WeiboUser> users) {
        this.users = users;
    }

    public long getNext_cursor() {
        return next_cursor;
    }

    public void setNext_cursor(long next_cursor) {
        this.next_cursor = next_cursor;
    }

    public long getPrevious_cursor() {
        return previous_cursor;
    }

    public void setPrevious_cursor(long previous_cursor) {
        this.previous_cursor = previous_cursor;
    }

    public long getTotal_number() {
        return total_number;
    }

    public void setTotal_number(long total_number) {
        this.total_number = total_number;
    }

    @Override
    public String toString() {
        return "WeiboUsers [users=" + users + ", next_cursor=" + next_cursor
                + ", previous_cursor=" + previous_cursor + ", total_number="
                + total_number + "]";
    }

}
