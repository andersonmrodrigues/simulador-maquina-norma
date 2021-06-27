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
    private static final String PATHMONOLITHIC = "D:\\Projetos\\simulador-maquina-norma\\monolitico.txt";
    private static final String PATHITERATIVE = "D:\\Projetos\\simulador-maquina-norma\\iterativo.txt";
    private static final String FILELOGPATH = "D:\\Projetos\\simulador-maquina-norma\\log.txt";


    MachineService() {
        this.data = DataDTO.builder().build();
        this.data.setLogger(log);
    }

    void execute() throws IOException {
        setLogParams();
        int choice;
        do {

            System.out.println(DataDTO.SEPARATOR);
            System.out.println("Opções de Execução");
            System.out.println("1 - Ler programa monolítico do arquivo TXT");
            System.out.println("2 - Converter programa iterativo para monolítico");
            System.out.println("8 - Ver Registradores");
            System.out.println("9 - Reiniciar tudo (ler arquivo, inicializar registradores, etc.)");
            System.out.println("0 - Encerrar Execução");
            System.out.println(DataDTO.SEPARATOR);
            choice = new Scanner(System.in).nextInt();
            switch (choice) {
                case 1:
                    setupMonolithic();
                    monolithicProgram();
                    break;
                case 2:
                    setupIterative();
                    convertIterative();
                    break;
                case 8:
                    showRegisters();
                    break;
                case 9:
                    setupMonolithic();
                    break;
            }
        } while (choice != 0);
    }

    private void setLogParams() throws IOException {
        FileHandler fh = new FileHandler(FILELOGPATH);
        log.addHandler(fh);
        fh.setFormatter(new SimpleFormatter());
        log.setUseParentHandlers(false);
    }

    private void convertIterative() {
        StringBuilder monolithic = new StringBuilder();
        Set<Integer> keys = data.getFileIterativeLines().keySet();
        int instruction = 0;
        for (Integer key : keys) {
            String line = data.getFileIterativeLines().get(key);
            instruction = setInstruction(monolithic, instruction);
            String[] commands = line.split(" ");
            if (data.isMatchesAteFaca(line) || data.isMatchesEnquantoFaca(line)) {
                monolithic
                        .append("se ")
                        .append(commands[1])
                        .append(" então vá_para 999 senão vá_para ")
                        .append(instruction + 1)
                        .append(System.lineSeparator());
            } else if (!data.isMatchesSeSenao(line) && (data.isMatchesAd(line) || data.isMatchesSub(line))) {
                monolithic
                        .append("faça ")
                        .append(commands[0])
                        .append(" vá_para ")
                        .append(instruction >= getLast() ? 1 : instruction + 1)
                        .append(System.lineSeparator());
            } else if (data.isMatchesSeSenao(line)) {
                monolithic
                        .append("se ")
                        .append(commands[1])
                        .append(" então vá_para ")
                        .append(instruction + 1)
                        .append(" senão vá_para ")
                        .append(instruction + 2)
                        .append(System.lineSeparator());
                instruction = setInstruction(monolithic, instruction);
                monolithic.append("faça ")
                        .append(commands[3])
                        .append(" vá_para ")
                        .append(instruction >= getLast() ? 1 : instruction + 1)
                        .append(System.lineSeparator());
                instruction = setInstruction(monolithic, instruction);
                monolithic.append("faça ")
                        .append(commands[5])
                        .append(" vá_para ")
                        .append(instruction >= getLast() ? 1 : instruction + 1)
                        .append(System.lineSeparator());
            }
        }
        printMonolithic(monolithic);
    }

    private int getLast() {
        return data.getIterativeLastLineNumber() - 1;
    }

    private void printMonolithic(StringBuilder monolithic) {
        System.out.println(DataDTO.SEPARATOR);
        System.out.println("Resultado da Conversão:");
        System.out.println(monolithic.toString());
        System.out.println(DataDTO.SEPARATOR);
    }


    private int setInstruction(StringBuilder monolithic, int instruction) {
        instruction++;
        monolithic.append(instruction);
        monolithic.append(": ");
        return instruction;
    }

    private void setupIterative() {
        log.info("Verificando se existe o arquivo programa iterativo no caminho especificado");
        data.setFileIterative(checkIfFileExists(PATHITERATIVE));
        log.info("Arquivo programa iterativo encontrado");
        data.iterativeMap();
    }

    private void setupMonolithic() {
        log.info("Verificando se existe o arquivo programa monolitico no caminho especificado");
        data.setFileMonolithic(checkIfFileExists(PATHMONOLITHIC));
        log.info("Arquivo programa monolitico encontrado");
        data.monolithicMap();
        data.setRegisters(getIntWithQuestion("Informe a quantidade de Registradores"));
        data.createRegistersMap();
        initializeRegisters();
        showRegisters();
    }

    private void monolithicProgram() {
        log.info("Lendo primeira instrução");
        executeLine(data.getFileMonolithicLines().get(1));
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
        String line = data.getFileMonolithicLines().getOrDefault(Integer.valueOf(command), null);
        log.info("Indo para a instrução " + command);
        if (line != null) {
            executeLine(line);
        } else {
            log.info("Instrução não encontrada, fim do programa!");
            return;
        }
    }

    private boolean verifyRegister(String command) {
        String register = command.replaceAll("_zero", "");
        log.info("registrador '" + register + "' é igual a ZERO");
        return data.getRegistersMap().get(register).equals(BigInteger.ZERO) || data.getRegistersMap().get(register).equals(new BigInteger("0"));
    }

    private void showRegisters() {
        if (data == null || data.getRegistersMap() == null || data.getRegistersMap().isEmpty())
            return;
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

    public File checkIfFileExists(String path) {
        File file = new File(path);
        if (!file.exists()) {
            log.warning("Arquivo não encontrado");
            System.exit(1);
        }
        return file;
    }
}
