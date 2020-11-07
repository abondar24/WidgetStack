package org.abondar.industrial.widgetstack.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "widget")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
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


    @Override
    public int compareTo(Widget w) {
        return this.zIndex-w.getZIndex();
    }
}
