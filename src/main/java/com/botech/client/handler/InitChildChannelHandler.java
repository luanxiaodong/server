package com.botech.client.handler;



import java.util.concurrent.TimeUnit;

import com.botech.io.netty.bean.BcPackageBuilder;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;




public class InitChildChannelHandler extends ChannelInitializer<SocketChannel>{

	@Override
	protected void initChannel(SocketChannel sc) throws Exception {

		sc.pipeline().addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
		sc.pipeline().addLast("protobufDecoder", new ProtobufDecoder(BcPackageBuilder.BcPackage.getDefaultInstance()));
		sc.pipeline().addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
		sc.pipeline().addLast("protobufEncoder", new ProtobufEncoder());
		sc.pipeline().addLast("handler", new TimeClientHandler());
		
		//测试的时候可能要去掉超时
		sc.pipeline().addLast("ping", new IdleStateHandler(65, 60, 60, TimeUnit.SECONDS));//客户端1分种5秒没收到服务器的返回则关闭，如果关闭会调用重连接。
		sc.pipeline().addLast("pong", new HeartBeatReqHandler());//心跳(超时事件会在心跳里面取)  ,心跳请求bctype=2,心跳应答bctype=3
		
	}

}
