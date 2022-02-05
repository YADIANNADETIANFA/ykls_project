package com.kai.webby.model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

//新鲜事
@Data
@Accessors(chain = true)
public class Feed implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;//新鲜事的ID
    private int type;//新鲜事的类型
    private int userId;//哪个人产生的该新鲜事
    private Date createdDate;
    private String data;//新鲜事的具体内容（不同新鲜事的内容差别很大，无法固定表示，因此采用JSON）
    private JSONObject dataJSON = null;//为了velocity模板的方便调用

    //当使用lombok时，部分get、set方法需要自定义，lombok不会再生成对应的方法。所以自己写get、set方法和lombok生成的方法不冲突。
    public void setData(String data){
        this.data = data;
        dataJSON = JSONObject.parseObject(data);
    }

    public String get(String key){return dataJSON == null ? null : dataJSON.getString(key);}
}
