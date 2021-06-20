package br.com.trabalho;

import lombok.extern.java.Log;

import java.io.File;
import java.math.BigInteger;
import java.util.Scanner;
import java.util.Set;

@Log
public class MachineService {

    private DataDTO data;
    private static final String FILEPATH = "D:\\Projetos\\simulador-maquina-norma\\exemplo_mono.txt";


    MachineService() {
        this.data = DataDTO.builder().build();
    }

    void execute() {
        log.info("Verificando se existe o arquivo na caminho especificado");
        data.setFile(checkIfFileExists());
        log.info("Arquivo encontrado");
        data.setRegisters(getIntWithQuestion("Informe a quantidade de Registradores"));
        data.createRegistersMap();
        initializeRegisters();
        showRegistersInitialized();
    }

    private void showRegistersInitialized() {
        Set<String> keys = data.getRegistersMap().keySet();
        System.out.println("------------Registradores-----------");
        for (String key : keys) {
            System.out.println(key + " = "
                    + data.getRegistersMap().get(key));
        }
        System.out.println(DataDTO.SEPARATOR);
    }

    private void initializeRegisters() {
        Set<String> keys = data.getRegistersMap().keySet();
        for (String key : keys) {
            String question = "Informe o valor para o registrador " + key;
            data.getRegistersMap().put(key, getIntWithQuestion(question));
        }
    }

    private BigInteger getIntWithQuestion(String question) {
        System.out.println(DataDTO.SEPARATOR);
        System.out.println(question);
        System.out.println(DataDTO.SEPARATOR);
        return new Scanner(System.in).nextBigInteger();
    }

    public File checkIfFileExists() {
        File file = new File(FILEPATH);
        if (!file.exists()) {
            log.warning("Arquivo não encontrado");
            System.exit(1);
        }
        return file;
    }
}
