package br.com.trabalho;

import java.io.IOException;

public class MainApplication {
    /**
     * Classe responsável por inicializar a aplicação.
     *
     * @param args
     * @throws IOException
     */

    public static void main(String[] args) throws IOException {
        new MachineService().execute();
    }
}
