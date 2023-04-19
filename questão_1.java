import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class Q1 {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        System.out.print("Digite o número N de aviões para sair: ");
        int N = scan.nextInt();

        System.out.print("Digite o número K de pistas disponíveis: ");
        int K = scan.nextInt();

        Aeroporto aeroporto = new Aeroporto(K);
        //tempo atual quando o programa inicia
        long tempoInicio = System.currentTimeMillis();
        //adicionando cada um dos N avioes que vao sair com seus respectivos tempos esperados de saída
        for (int i = 0; i < N; i++) {
            System.out.printf("Digite o tempo de saída (em ms) do avião %d: ", i + 1);
            long tempo = scan.nextLong();
            aeroporto.addAviao(new Aviao(i + 1, tempo, true));
        }

        System.out.print("Digite o número M de aviões para chegar: ");
        int M = scan.nextInt();
        //adicionando cada um dos N avioes que vao chegar com seus respectivos tempos esperados de chegada
        for (int i = 0; i < M; i++) {
            System.out.printf("Digite o tempo de chegada (em ms) do avião %d: ", i + 1);
            long tempo1 = scan.nextLong();
            aeroporto.addAviao(new Aviao(N + i + 1,  tempo1, false));
        }

        // Iniciar uma que chama o método run do objeto aeroporto
        new Thread(() -> aeroporto.run(tempoInicio)).start();
    }
}
//definindo a classe aviao 
class Aviao {
    int id;
    long tempoEsperado;
    boolean partiu;

    public Aviao(int id, long tempoEsperado, boolean partiu) {
        this.id = id;
        this.tempoEsperado = tempoEsperado;
        this.partiu = partiu;
    }
}

//definindo a classe pistas que extende Thread
class Pistas extends Thread{
    public int id;
    public boolean disponivel;

    public Pistas(int id) {
        this.id = id;
        this.disponivel = true;
    }
}
//definindo a classe aeroporto
class Aeroporto {
    private PriorityQueue<Aviao> filaAviao;
    private List<Pistas> pistas;
    private Lock lock;
    private Condition pistaDisponivel;
    
    public Aeroporto(int numeroDePistas) {
        filaAviao = new PriorityQueue<>((a, b) -> Long.compare(a.tempoEsperado, b.tempoEsperado));
        pistas = new ArrayList<>();
        lock = new ReentrantLock();
        //condicao
        pistaDisponivel = lock.newCondition();

        for (int i = 0; i < numeroDePistas; i++) {
            pistas.add(new Pistas(i));
        }
    }
    //adiciona cada aviao na fila de prioridade filaAviao e sinaliza que uma nova pista tá disponivel
    public void addAviao(Aviao aviao) {
        lock.lock();
        try {
            filaAviao.add(aviao);
            pistaDisponivel.signalAll();
        } finally {
            lock.unlock();
        }
    }
    
    public void run(long tempoInicio) {
        while (true) {
            lock.lock();
            try {
                while (filaAviao.isEmpty()) {
                    pistaDisponivel.await();
                }
                //pega primeiro aviao da fila de prioridade
                Aviao aviao = filaAviao.peek();
                
                Pistas pistaDisponivelAtual = null;
                for (Pistas pista : pistas) {
                    if (pista.disponivel) {
                        pistaDisponivelAtual = pista;
                        break;
                    }
                }
                
                long agora = System.currentTimeMillis();
                if (pistaDisponivel != null && agora >= (aviao.tempoEsperado + tempoInicio)) {
                    filaAviao.poll();
                    pistaDisponivelAtual.disponivel = false;
                    String acao = aviao.partiu ? "decolar" : "aterrissar";
                    //o atraso é o tempo atual - o tempo do inicio do programa - o tempo esperado do voo
                    long atraso = (agora - tempoInicio) - aviao.tempoEsperado;
                    //printa ação de cada voo, a hora esperada do voo, a hora real(a hora atual - a hora do inicio do programa) e o atraso
                    System.out.printf("Avião %d %s. Hora esperada: %d, Hora real: %d, Atraso: %d%n", aviao.id, acao, aviao.tempoEsperado, agora - tempoInicio, atraso);

                    lock.unlock();

                    try {
                    	//espera 500ms a cada decolagem ou aterrisagem de aviao
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    lock.lock();
                    pistaDisponivelAtual.disponivel = true;
                    //sinaliza que a thread esta disponivel novamente
                    pistaDisponivel.signalAll();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }


	
}