package org.abondar.industrial.widgetstack.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "widget")
public class Widget implements Comparable<Widget>{

    @Id
    private String id;

    @JsonProperty("x")
    @Column(name = "x_coord")
    private Integer xCoord;

    @JsonProperty("y")
    @Column(name = "y_coord")
    private Integer yCoord;

    @JsonProperty("z")
    @Column(name = "z_index")
    private Integer zIndex;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "last_modified")
    private Date lastModified;

    public Widget(Integer xCoord, Integer yCoord, Integer zIndex, Integer width, Integer height) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zIndex = zIndex;
        this.width = width;
        this.height = height;
    }

    public Widget(Widget widget){
        this.id = widget.id;
        this.height = widget.height;
        this.width = widget.width;
        this.xCoord = widget.xCoord;
        this.yCoord = widget.yCoord;
        this.zIndex = widget.zIndex;
        this.lastModified = widget.lastModified;
    }

   public Widget(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getxCoord() {
        return xCoord;
    }

    public void setxCoord(Integer xCoord) {
        this.xCoord = xCoord;
    }

    public Integer getyCoord() {
        return yCoord;
    }

    public void setyCoord(Integer yCoord) {
        this.yCoord = yCoord;
    }

    public Integer getzIndex() {
        return zIndex;
    }

    public void setzIndex(Integer zIndex) {
        this.zIndex = zIndex;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    @Override
    public String toString() {
        return "Widget{" +
                "id='" + id + '\'' +
                ", zIndex=" + zIndex +
                '}';
    }

    @Override
    public int compareTo(Widget w) {
        return w.zIndex-this.zIndex;
    }
}
