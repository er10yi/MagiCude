package com.tiji.center.pojo;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * location实体类
 *
 * @author 贰拾壹
 */
@Entity
@Table(name = "tb_location")
public class Location implements Serializable {

    @Id
    private String id;//位置编号


    private String assetipid;//资产ip编号
    private String country;//国家
    private String province;//省份
    private String road;//道路
    private String building;//大厦
    private String floor;//楼层
    private String direction;//方位


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssetipid() {
        return assetipid;
    }

    public void setAssetipid(String assetipid) {
        this.assetipid = assetipid;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }


}
