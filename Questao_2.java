import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Questao_2 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        LinkedBlockingQueue<Tarefa> listaTarefas = new LinkedBlockingQueue<Tarefa>();

        System.out.println("Digite a quantidade de operários: ");
        int operarios = in.nextInt();
        ExecutorService es = Executors.newFixedThreadPool(operarios);

        System.out.println("Digite a quantidade de tarefas a serem realizadas: ");
        int tarefas = in.nextInt();

        for(int i = 0; i < tarefas; i++){
            System.out.println("Id da"+(i+1)+"ª tarefa: ");
            int id = in.nextInt();
            System.out.println("Tempo para resolução em ms: ");
            int tempo = in.nextInt();
            List<Integer> dependencias = new ArrayList<Integer>();
            System.out.println("Digite as ids das dependencias (ou 0 para encerrar): ");
            int dependencia;
            while ((dependencia = in.nextInt()) != 0) {
                dependencias.add(dependencia);
            }
            listaTarefas.add(new Tarefa(id, tempo, dependencias));
        }

        for (int i = 0; i < operarios; i++){
            es.execute(new Operario(listaTarefas));
        }
        es.shutdown();
    }

    public static class Tarefa{
        private int id;
        private int tempo;
        private List<Integer> dependencias;

        public Tarefa(int id, int tempo, List<Integer> dependencias){
            this.id = id;
            this.tempo = tempo;
            this.dependencias = dependencias;
        }

        public int getTempo() {
            return tempo;
        }

        public List<Integer> getDependencias() {
            return dependencias;
        }

        public int getId() {
            return id;
        }


    }

    public static class Operario implements Runnable{
        LinkedBlockingQueue<Tarefa> listaTarefas;
        public Operario(LinkedBlockingQueue<Tarefa> tarefas){
            this.listaTarefas = tarefas;
        }

        public void run(){
            while(true){
                Tarefa tarefa = this.listaTarefas.peek();
                if (tarefa == null) {
                    break;
                }synchronized (this.listaTarefas) {
                    boolean temDependencia = false;
                    List<Tarefa> copy = new ArrayList<>(this.listaTarefas);
                    for (int dependente : tarefa.getDependencias()) {
                        for (Tarefa tarefaChecar : copy) {
                            if (dependente == tarefaChecar.id) {
                                temDependencia = true;
                                break;
                            }
                        }
                        if (temDependencia) {
                            break;
                        }
                    }
                    if (temDependencia) {
                        if (!this.listaTarefas.isEmpty()) {
                            Tarefa tarefaFeita = listaTarefas.remove();
                            listaTarefas.add(tarefaFeita);
                        }
                    } else {
                        if (!this.listaTarefas.isEmpty()) {
                            Tarefa tarefaFeita = listaTarefas.remove();
                            System.out.println("tarefa " + tarefaFeita.getId() + " feita");

                            try {
                                Thread.sleep(tarefaFeita.getTempo());
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }

            }
        }
    }
}
