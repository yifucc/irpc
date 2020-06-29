package com.ifcc.irpc.codec.serialization.msgpack;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ifcc.irpc.codec.serialization.Serialization;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.util.ArrayList;

/**
 * @author chenghaifeng
 * @date 2020-06-11
 * @description
 */
@Slf4j
public class MsgpackSerialization implements Serialization {

    private ObjectMapper mapper;

    public MsgpackSerialization() {
        this.mapper = new ObjectMapper(new MessagePackFactory());
    }

    @Override
    public byte[] marshal(Object o) {
        try {
            return mapper.writeValueAsBytes(o);
        } catch (Exception e) {
            log.error("[MsgpackSerialization] Marshal object error", e);
            return null;
        }
    }

    @Override
    public <T> T unMarshal(Class<T> clazz, byte[] data) {
        try {
            return mapper.readValue(data, clazz);
        } catch (Exception e) {
            log.error("[MsgpackSerialization] UnMarshal object error", e);
            return null;
        }
    }

    private JavaType list(Class<?> clazz) {
        return mapper.getTypeFactory().constructParametricType(ArrayList.class, clazz);
    }

}
