import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by Ray on 2017/5/20.
 */

public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        int port = 3000;

        new EchoServer(port).start();
    }

    public void start() throws InterruptedException {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();     //创建EventLoopGroup

        try {
            ServerBootstrap b = new ServerBootstrap();      //创建ServerBootstrap
            b.group(group)
                    .channel(NioServerSocketChannel.class)      //指定使用Nio
                    .localAddress(new InetSocketAddress(port))  //指定端口
                    .childHandler(new ChannelInitializer<SocketChannel>() {     //添加一个EchoServerHandler到子Channel的ChannelPipeline
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(serverHandler);           //EchoServerHandler被标注为@Shareable，所以我们可以总是使用同样的实例
                        }
                    });
            ChannelFuture f = b.bind().sync();      //异步地绑定服务器，使用sync()方法阻塞等待指导绑定完成
            f.channel().closeFuture().sync();       //获取Channel的CloseFuture，并且阻塞当前线程直到它完成
        } finally {
            group.shutdownGracefully().sync();      //关闭EventLoopGroup，释放所有的资源
        }



    }
}
