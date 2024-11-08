package br.bossini;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class EleicaoDeLider {
    private static final String HOST = "localhost";
    private static final String PORTA = "2181";
    private static final int TIMEOUT = 5000;
    private ZooKeeper zooKeeper;

    public static void main(String[] args) throws IOException, InterruptedException {
        var eleicaoDeLider = new EleicaoDeLider();
        eleicaoDeLider.conectar();
        eleicaoDeLider.executar();
    }

    public void conectar() throws IOException{
        zooKeeper = new ZooKeeper(
            String.format("%s:%s", HOST, PORTA),
            TIMEOUT,
            (evento) -> {
                if(evento.getType() == Watcher.Event.EventType.None){
                    if(evento.getState() == Watcher.Event.KeeperState.SyncConnected){
                        System.out.println("Conectou");
                    }
                    else if(evento.getState() == Watcher.Event.KeeperState.Disconnected){
                        synchronized (zooKeeper){
                            System.out.println("Desconectou...\n");
                            System.out.printf("Estamos na thread: %s\n", Thread.currentThread().getName());
                            zooKeeper.notify();
                        }
                    }
                }
            }
        );
    }

    public void executar() throws InterruptedException{
        synchronized (zooKeeper){
            zooKeeper.wait();
        }
    }
}
