package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

public class OrderSchedule implements Serializable {
    private String bookTitle;
    private Integer tick;

    public OrderSchedule(String bookTitle, int tick){
        this.bookTitle = bookTitle;
        this.tick = tick;
    }

    public String getBookTitle(){
        return this.bookTitle;
    }

    public int getCorrespondingTick(){
        return this.tick;
    }
}
