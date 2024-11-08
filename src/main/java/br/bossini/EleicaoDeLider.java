package br.bossini;

import org.apache.zookeeper.ZooKeeper;
import java.io.IOException;

public class EleicaoDeLider {
    private static final String HOST = "localhost";
    private static final String PORTA = "2181";
    private static final int TIMEOUT = 5000;
    private ZooKeeper zooKeeper;
    public static void main(String[] args) {

    }

    public void conectar() throws IOException {
        zooKeeper = new ZooKeeper(
            String.format("%s:%s", HOST, PORTA),
            TIMEOUT,
            (evento) -> {

            }
        );
    }
}