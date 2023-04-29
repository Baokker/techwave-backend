package com.techwave.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: JiKeSpace
 * @description: 对返回值封装
 * @packagename: com.tjsse.jikespace.entity.vo
 * @author: peng peng
 * @date: 2022-11-29 18:15
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Result {
    private Integer code;

    private String msg;

    private Object data;

    public static Result success() {
        Result result = new Result();
        result.setCode(TCode.SUCCESS.getCode());
        result.setMsg("");
        result.setData(null);
        return result;
    }

    public static Result success(Object data) {
        Result result = new Result();
        result.setCode(TCode.SUCCESS.getCode());
        result.setMsg("");
        result.setData(data);
        return result;
    }

    public static Result success(Integer code, Object data) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg("");
        result.setData(data);
        return result;
    }
    public static Result success(Integer code, String msg, Object data) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }


    public static Result fail() {
        Result result = new Result();
        result.setCode(TCode.FAIL.getCode());
        result.setMsg("");
        result.setData(null);
        return result;
    }

    public static Result fail(Object data) {
        Result result = new Result();
        result.setCode(TCode.FAIL.getCode());
        result.setMsg("");
        result.setData(data);
        return result;
    }

    public static Result fail(Integer code, Object data) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg("");
        result.setData(data);
        return result;
    }
    public static Result fail(Integer code, String msg, Object data) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
}
