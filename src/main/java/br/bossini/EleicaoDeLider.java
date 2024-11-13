package br.bossini;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class EleicaoDeLider {
    private static final String HOST = "172.20.10.2";
    private static final String PORTA = "2181";
    private static final int TIMEOUT = 50000;
    private static final String NAMESPACE_ELEICAO = "/eleicao";
    private ZooKeeper zooKeeper;
    private String nomeDoZNodeDesseProcesso;

    public static void main(String[] args) throws Exception {
        var eleicaoDeLider = new EleicaoDeLider();
        eleicaoDeLider.conectar();
        eleicaoDeLider.realizarCandidatura();
        eleicaoDeLider.elegerOLider();
        eleicaoDeLider.executar();
        eleicaoDeLider.fechar();
    }

    public void fechar() throws InterruptedException{
        zooKeeper.close();
    }

    public void realizarCandidatura() throws InterruptedException, KeeperException{
        String prefixo = String.format("%s/cand_", NAMESPACE_ELEICAO);
        String pathInteiro = zooKeeper.create(
            prefixo, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL
        );
        System.out.println(pathInteiro);
        nomeDoZNodeDesseProcesso = pathInteiro.replace(String.format("%s/", NAMESPACE_ELEICAO), "");
    }

    public void elegerOLider () throws InterruptedException, KeeperException{
        List<String> candidatos = zooKeeper.getChildren(NAMESPACE_ELEICAO, false);
        Collections.sort(candidatos);
        String oMenor = candidatos.get(0);
        if(oMenor.equals(nomeDoZNodeDesseProcesso)){
            System.out.printf("Me chamo %s e sou o líder\n", nomeDoZNodeDesseProcesso);
        }
        else{
            System.out.printf("Me chamo %s e não sou o líder. O líder é o %s", nomeDoZNodeDesseProcesso , oMenor);
        }
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
