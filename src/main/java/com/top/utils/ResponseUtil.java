package com.top.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AsciiString;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author lubeilin
 * @date 2021/1/13
 */
public class ResponseUtil {
    private static final AsciiString CONTENT_TYPE = AsciiString.cached("Content-Type");
    private static final AsciiString CONTENT_LENGTH = AsciiString.cached("Content-Length");
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final FullHttpResponse NOT_FOUND = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.NOT_FOUND);
    private static final FullHttpResponse ERROR = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);

    public static FullHttpResponse success(Object object) throws JsonProcessingException {
        byte[] bytes = objectMapper.writeValueAsBytes(object);
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(bytes));
        response.headers().set(CONTENT_TYPE, "application/json");
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
        return response;
    }

    public static FullHttpResponse notFound() {
        return NOT_FOUND.copy();
    }

    public static FullHttpResponse error() {
        return ERROR.copy();
    }
}
