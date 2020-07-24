package test.com.ifcc.irpc.codec.serialization.protocol;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.ifcc.irpc.codec.serialization.protocol.IrpcProtocol;
import com.ifcc.irpc.codec.serialization.protocol.ProtocolSerialization;
import com.ifcc.irpc.common.IrpcRequest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * @author chenghaifeng
 * @date 2020-06-11
 * @description
 */
public class IrpcProtocolTest {

    public static void main(String[] args) throws InvalidProtocolBufferException {
        IrpcProtocol.Request.Builder builder = IrpcProtocol.Request.newBuilder();
        builder.putMetadata("name", ByteString.copyFromUtf8("ifcc"));
        builder.putMetadata("age", ByteString.copyFromUtf8("11"));
        builder.setPayload(ByteString.copyFromUtf8("test"));
        builder.setServicePath("/data/fdf");
        IrpcProtocol.Request request = builder.build();
        byte[] data = request.toByteArray();
        System.out.println(Arrays.toString(data));
        IrpcProtocol.Request parseFrom = IrpcProtocol.Request.parseFrom(data);
        System.out.println(parseFrom.getMetadataMap());
    }

    @Test
    void test1() {
        ProtocolSerialization serialization = new ProtocolSerialization();
        IrpcRequest request = new IrpcRequest();
        request.setService("/test");
        request.setPayload(new IrpcRequest());
        byte[] bytes = serialization.marshal(request);
        System.out.println(bytes);
    }
}
