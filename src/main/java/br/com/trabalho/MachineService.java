package br.com.trabalho;

import lombok.extern.java.Log;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

@Log
public class MachineService {

    private DataDTO data;
    private static final String FILEPATH = "D:\\Projetos\\simulador-maquina-norma\\exemplo_mono.txt";
    private static final String FILELOGPATH = "D:\\Projetos\\simulador-maquina-norma\\log.txt";


    MachineService() {
        this.data = DataDTO.builder().build();
        this.data.setLogger(log);
    }

    void execute() throws IOException {
        setup();
        int choice;
        do {

            System.out.println(DataDTO.SEPARATOR);
            System.out.println("Opções de Execução");
            System.out.println("1 - Ler programa monolítico do arquivo TXT");
            System.out.println("8 - Ver Registradores");
            System.out.println("9 - Reiniciar tudo (ler arquivo, inicializar registradores, etc.)");
            System.out.println("0 - Encerrar Execução");
            System.out.println(DataDTO.SEPARATOR);
            choice = new Scanner(System.in).nextInt();
            switch (choice) {
                case 1:
                    monolithicProgram();
                    break;
                case 8:
                    showRegisters();
                    break;
                case 9:
                    setup();
                    break;
            }
        } while (choice != 0);
    }

    private void setup() throws IOException {
        FileHandler fh = new FileHandler(FILELOGPATH);
        log.addHandler(fh);
        fh.setFormatter(new SimpleFormatter());
        log.setUseParentHandlers(false);
        log.info("Verificando se existe o arquivo na caminho especificado");
        data.setFile(checkIfFileExists());
        log.info("Arquivo encontrado");
        data.createFileMap();
        data.setRegisters(getIntWithQuestion("Informe a quantidade de Registradores"));
        data.createRegistersMap();
        initializeRegisters();
        showRegisters();
    }

    private void monolithicProgram() {
        //   0       1        2           3       4     5         6      7
//        1: se     zero_b   então      vá_para   9   senão    vá_para   2
//        2: faça   ad_a     vá_para    3
//        3: faça   ad_a     vá_para    4
//        4: faça   sub_b    vá_para    1
        log.info("Lendo primeira instrução");
        executeLine(data.getFileLines().get(1));
    }

    private void executeLine(String line) {
        log.info("Instrução: " + line);
        String[] commands = line.split(" ");
        if (commands[0].contains("se")) {
            log.info("Vericando SE o ");
            if (verifyRegister(commands[1])) {
                log.info("Registrador é igual ZERO");
                verifyIfExistsAndCallExecute(commands[4]);
            } else {
                log.info("Registrador não era igual ZERO");
                verifyIfExistsAndCallExecute(commands[7]);
            }
        } else if (commands[0].contains("faça") || commands[0].contains("faca")) {
            log.info("Executando a ");
            verifySumOrSubAndGoToLine(commands[1], commands[3]);
        }
    }

    private void verifySumOrSubAndGoToLine(String command, String goTo) {
        String register = command.replaceAll("ad_", "").replaceAll("sub_", "");
        if (command.contains("ad_")) {
            log.info("adição de +1 no registrador " + register);
            BigInteger value = getValueAndValid(register);
            data.getRegistersMap().put(register, value.add(BigInteger.ONE));
        } else if (command.contains("sub_")) {
            log.info("substração de -1 no registrador " + register);
            BigInteger value = getValueAndValid(register);
            data.getRegistersMap().put(register, value.subtract(BigInteger.ONE));
        } else {
            log.warning("COMMAND INVALID");
            System.exit(1);
        }
        verifyIfExistsAndCallExecute(goTo);
    }

    private BigInteger getValueAndValid(String register) {
        BigInteger value = data.getRegistersMap().getOrDefault(register, null);
        if (value == null) {
            log.warning("Registrador não encontrado");
            System.exit(1);
        }
        return value;
    }

    private void verifyIfExistsAndCallExecute(String command) {
        String line = data.getFileLines().getOrDefault(Integer.valueOf(command), null);
        log.info("Indo para a instrução " + command);
        if (line != null) {
            executeLine(line);
        } else {
            log.info("Instrução não encontrada, fim do programa!");
            return;
        }
    }

    private boolean verifyRegister(String command) {
        String register = command.replace("zero_", "");
        log.info("registrador '" + register + "' é igual a ZERO");
        return data.getRegistersMap().get(register).equals(BigInteger.ZERO) || data.getRegistersMap().get(register).equals(new BigInteger("0"));
    }

    private void showRegisters() {
        Set<String> keys = data.getRegistersMap().keySet();
        log.info("------------Registradores-----------");
        for (String key : keys) {
            log.info(key + " = "
                    + data.getRegistersMap().get(key));
        }
        log.info(DataDTO.SEPARATOR);
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
