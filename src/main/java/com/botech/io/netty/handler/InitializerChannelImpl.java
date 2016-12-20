package com.botech.io.netty.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

import com.botech.io.netty.bean.BcPackageBuilder;
import com.botech.io.netty.bean.BcPackageBuilder.BcPackage;

/**
 * 【初始化handler】
* @ClassName: InitializerChannelImpl 
* @author luanxd
* @date 2015-8-13 下午6:02:48 
*
 */
public class InitializerChannelImpl extends ChannelInitializer<SocketChannel> {
	
	@Override
	protected void initChannel(SocketChannel sc) throws Exception {

		sc.pipeline().addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
		sc.pipeline().addLast("protobufDecoder", new ProtobufDecoder(BcPackageBuilder.BcPackage.getDefaultInstance()));
		sc.pipeline().addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
		sc.pipeline().addLast("protobufEncoder", new ProtobufEncoder());
		
		
		//SSLEngine engine=null;//安全性,分为：单 向，双向(暂时不需要做) see:http://www.infoq.com/cn/articles/netty-security/
		//sc.pipeline().addFirst(new SslHandler(engine));
		
		sc.pipeline().addLast(new AuthServerHandler());//验证bctype=0 ,通知我正常退出bctype=7
//		sc.pipeline().addLast(new AuthServerHandlerTest());//验证(测试版，测试是带业务数据方式)
		
		//测试的时候可能要去掉超时
		sc.pipeline().addLast("ping", new IdleStateHandler(5, 5, 5, TimeUnit.MINUTES));//产生超时事件.(5分钟超时与微信是一样的)
		sc.pipeline().addLast("pong", new HeartBeatReqHandler());//心跳(超时事件会在心跳里面取)  ,心跳请求bctype=2,心跳应答bctype=3
		
		sc.pipeline().addLast(new BusinessHandler());//全部业务
		sc.pipeline().addLast(new MessageHandler());//向全部人发消息, bctype=5
		
		
		

		//服务器主动打招乎
		BcPackage msg = BcPackageBuilder.BcPackage.newBuilder().setBctype("0").build();
		
		sc.writeAndFlush(msg);
		
	}
	
}

