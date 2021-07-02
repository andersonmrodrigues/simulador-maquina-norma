package br.com.trabalho;

import lombok.extern.java.Log;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

@Log
public class MachineService {

    private final DataDTO data;
    private static final String PATHMONOLITHIC = "D:\\Projetos\\simulador-maquina-norma\\monolitico2.txt";
    private static final String PATHITERATIVE = "D:\\Projetos\\simulador-maquina-norma\\iterativo.txt";
    private static final String PATHRECURSIVE = "D:\\Projetos\\simulador-maquina-norma\\recursivo.txt";
    private static final String FILELOGPATH = "D:\\Projetos\\simulador-maquina-norma\\log.txt";


    MachineService() {
        this.data = DataDTO.builder().build();
        this.data.setLogger(log);
    }

    /**
     * Método responsável pela inicialização do programa, apresentando no terminal a lista de
     * opções que o usuário pode executar.
     *
     * @throws IOException
     */
    void execute() throws IOException {
        setLogParams();
        int choice;
        do {
            System.out.println(DataDTO.SEPARATOR);
            System.out.println("Opções de Execução");
            System.out.println("1 - Ler programa monolítico");
            System.out.println("2 - Converter programa iterativo para monolítico");
            System.out.println("3 - Ler programa recursivo");
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
                case 3:
                    setupRecursive();
                    recursiveProgram();
                case 8:
                    showRegisters();
                    break;
                case 9:
                    setupMonolithic();
                    break;
            }
        } while (choice != 0);
    }

    /**
     * Método responsável pela validação da existência do arquivo com o programa recursivo, registrar as
     * linhas e inicializar os registradores.
     */
    private void setupRecursive() {
        log.info("Verificando se existe o arquivo programa recursivo no caminho especificado");
        data.setFileRecursive(checkIfFileExists(PATHRECURSIVE));
        log.info("Arquivo programa recursivo encontrado");
        data.recursiveMap();
        data.setRegisters(getIntWithQuestion("Informe a quantidade de Registradores"));
        data.createRegistersMap();
        initializeRegisters();
    }

    /**
     * Método responsável pela execução do programa recursivo, verificando os “def” definidos e comandos e os
     * chamando de forma recursiva até atingir as condições.
     */
    private void recursiveProgram() {
        Set<Integer> keys = data.getFileRecursiveLines().keySet();
        LinkedHashMap<String, String> mapDefs = new LinkedHashMap<>();
        int instruction = 0;
        mapDefs = mappingDefs(keys, mapDefs);
        execDef(mapDefs.get(data.getFirstDef()), mapDefs);
    }

    /**
     * Método responsável pela execução das linhas dos programa recursivo, conforme as “def”, o próprio método pode
     * fazer uma chamada a ele mesmo caso a instrução seja essa. Nele é verificado se existe alguma condição ou
     * operação a ser executada na linha.
     *
     * @param line
     * @param mapDefs
     */
    private void execDef(String line, LinkedHashMap<String, String> mapDefs) {
        log.info("Executando " + line);
        String[] commands = line.split(" ");
        if (data.isMatchesSeSenao(line)) {
            if (verifyRegister(commands[1])) {
                return;
            }
            execCommands(mapDefs, commands[5]);
        } else if (data.isMatchesAd(line) || data.isMatchesSub(line)) {
            execCommands(mapDefs, commands[0]);
        }
    }

    /**
     * Método responsável por verificar cada comando passado na linha, caso seja uma referência a uma outra “ref”,
     * a chamada recursiva é feita, caso seja uma operação (adição ou subtração), é chamado o método responsável.
     *
     * @param mapDefs
     * @param command
     */
    private void execCommands(LinkedHashMap<String, String> mapDefs, String command) {
        String[] execs = command.split(";");
        for (int i = 0; i < execs.length; i++) {
            String exec = execs[i].replaceAll("\\)", "").replaceAll(",", "");
            if (mapDefs.containsKey(exec)) {
                execDef(mapDefs.get(exec), mapDefs);
            } else {
                doSumOrSub(exec);
            }
        }
    }

    /**
     * Método responsável pela execução da adição ou subtração no registrar informado.
     *
     * @param exec
     */
    private void doSumOrSub(String exec) {
        String register = exec.replaceAll("ad_", "").replaceAll("sub_", "");
        if (exec.contains("ad_")) {
            log.info("adição de +1 no registrador " + register);
            BigInteger value = getValueAndValid(register);
            data.getRegistersMap().put(register, value.add(BigInteger.ONE));
        } else if (exec.contains("sub_")) {
            log.info("substração de -1 no registrador " + register);
            BigInteger value = getValueAndValid(register);
            data.getRegistersMap().put(register, value.subtract(BigInteger.ONE));
        } else {
            log.warning("COMMAND INVALID");
            System.exit(1);
        }
    }

    /**
     * Método responsável por mapear as instruções de todos os “def” do programa recursivo.
     *
     * @param keys
     * @param mapDefs
     * @return
     */
    private LinkedHashMap<String, String> mappingDefs(Set<Integer> keys, LinkedHashMap<String, String> mapDefs) {
        int count = 0;
        for (Integer key : keys) {
            String line = data.getFileRecursiveLines().get(key);
            if (data.isMatchesDef(line)) {
                String[] commands = line.split(" ");
                if (count == 0) data.setFirstDef(commands[0]);
                count++;
                if (data.isMatchesSeSenao(line)) {
                    StringBuilder commandsToExec = new StringBuilder();
                    commandsToExec
                            .append(commands[2] + " ")
                            .append(commands[3] + " ")
                            .append(commands[4] + " ")
                            .append(commands[5] + " ")
                            .append(commands[6] + " ")
                            .append(commands[7]);
                    mapDefs.put(commands[0], commandsToExec.toString());
                } else if (data.isMatchesAd(line) || data.isMatchesSub(line)) {
                    StringBuilder commandsToExec = new StringBuilder();
                    commandsToExec
                            .append(commands[2]);
                    mapDefs.put(commands[0], commandsToExec.toString());
                }
            }
        }
        return mapDefs;
    }

    /**
     * Método responsável por configurar os logs e mapear a localização onde o arquivo de logs será salvo.
     *
     * @throws IOException
     */
    private void setLogParams() throws IOException {
        FileHandler fh = new FileHandler(FILELOGPATH);
        log.setUseParentHandlers(false);
        log.addHandler(fh);
        fh.setFormatter(new SimpleFormatter());
    }

    /**
     * Método responsável por converter o programa iterativo em monolitico.
     */
    private void convertIterative() {
        StringBuilder monolithic = new StringBuilder();
        Set<Integer> keys = data.getFileIterativeLines().keySet();
        int instruction = 0;
        for (Integer key : keys) {
            String line = data.getFileIterativeLines().get(key);
            instruction = setInstruction(monolithic, instruction);
            String[] commands = line.split(" ");
            if (data.isMatchesAteFaca(line) || data.isMatchesEnquantoFaca(line)) {
                log.info("Convertendo AteFaca ou EnquantoFaca para condição SeEntaoSenao");
                monolithic
                        .append("se ")
                        .append(commands[1])
                        .append(" então vá_para 999 senão vá_para ")
                        .append(instruction + 1)
                        .append(System.lineSeparator());
            } else if (!data.isMatchesSeSenao(line) && (data.isMatchesAd(line) || data.isMatchesSub(line))) {
                log.info("Convertendo Operações para FaçaVaPara");
                monolithic
                        .append("faça ")
                        .append(commands[0])
                        .append(" vá_para ")
                        .append(instruction >= getLast() ? 1 : instruction + 1)
                        .append(System.lineSeparator());
            } else if (data.isMatchesSeSenao(line)) {
                log.info("Convertendo condição para SeEntaoSenao");
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

    /**
     * Método responsável por retornar o número da última linha do programa iterativo
     *
     * @return
     */
    private int getLast() {
        return data.getIterativeLastLineNumber() - 1;
    }

    /**
     * Método responsável por “printar” o resultado da conversão do programa iterativo.
     *
     * @param monolithic
     */
    private void printMonolithic(StringBuilder monolithic) {
        System.out.println(DataDTO.SEPARATOR);
        System.out.println("Resultado da Conversão:");
        System.out.println(monolithic.toString());
        System.out.println(DataDTO.SEPARATOR);
        log.info("Resultado da Conversão:");
        log.info(monolithic.toString());
    }

    /**
     * Método responsável por criar a “string” com o número da instrução na conversão do programa iterativo
     *
     * @param monolithic
     * @param instruction
     * @return
     */
    private int setInstruction(StringBuilder monolithic, int instruction) {
        instruction++;
        monolithic.append(instruction);
        monolithic.append(": ");
        return instruction;
    }

    /**
     * Método responsável por inicializar os dados necessários para a execução da conversão do programa iterativo para monolítico.
     */
    private void setupIterative() {
        log.info("Verificando se existe o arquivo programa iterativo no caminho especificado");
        data.setFileIterative(checkIfFileExists(PATHITERATIVE));
        log.info("Arquivo programa iterativo encontrado");
        data.iterativeMap();
    }

    /**
     * Método responsável por inicializar os dados necessários para a execução do programa monolítico.
     */
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

    /**
     * Método responsável pela execução do programa monolítico, iniciando pela primeira linha.
     */
    private void monolithicProgram() {
        log.info("Lendo primeira instrução");
        executeLine(data.getFileMonolithicLines().get(1));
    }

    /**
     * Método responsável pela execução da linha do programa monolítico.
     *
     * @param line
     */
    private void executeLine(String line) {
        log.info("Instrução: " + line);
        String[] commands = line.split(" ");
        if (commands[0].contains("se")) {
            if (verifyRegister(commands[1])) {
                verifyIfExistsAndCallExecute(commands[4]);
            } else {
                verifyIfExistsAndCallExecute(commands[7]);
            }
        } else if (commands[0].contains("faça") || commands[0].contains("faca")) {
            log.info("Executando a ");
            verifySumOrSubAndGoToLine(commands[1], commands[3]);
        }
    }

    /**
     * Método responsável pela validação se a instrução se trata de um adição ou subtração, interrompendo a
     * execução caso seja uma instrução não suportada.
     *
     * @param command
     * @param goTo
     */
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

    /**
     * Método responsável por receber o registrador e retornar o valor que está salvo, caso não encontre o
     * registrador interrompe a execução.
     *
     * @param register
     * @return
     */
    private BigInteger getValueAndValid(String register) {
        BigInteger value = data.getRegistersMap().getOrDefault(register, null);
        if (value == null) {
            log.warning("Registrador não encontrado");
            System.exit(1);
        }
        return value;
    }

    /**
     * Método responsável por verificar se o número da linha que constava na linha sendo executada existe,
     * caso existir a linha é executada, caso contrário o programa chegou ao seu fim
     *
     * @param command
     */
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

    /**
     * Método responsável por verificar se o registrador está igual a zero.
     *
     * @param command
     * @return
     */
    private boolean verifyRegister(String command) {
        log.info("Vericando SE o ");
        String register = command.replaceAll("_zero", "");
        log.info("registrador '" + register + "' é igual a ZERO");
        boolean equalsZero = data.getRegistersMap().get(register).equals(BigInteger.ZERO) || data.
                getRegistersMap().get(register).equals(new BigInteger("0"));
        if (equalsZero) {
            log.info("Registrador é igual ZERO");
        } else {
            log.info("Registrador não era igual ZERO");
        }
        return equalsZero;
    }

    /**
     * Método responsável por “printar” todos os registradores.
     */
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

    /**
     * Método responsável por solicitar ao usuário que preencha os registradores informados.
     */
    private void initializeRegisters() {
        Set<String> keys = data.getRegistersMap().keySet();
        for (String key : keys) {
            String question = "Informe o valor para o registrador " + key;
            data.getRegistersMap().put(key, getIntWithQuestion(question));
        }
    }

    /**
     * Método responsável por “printar” a pergunta e receber um valor inteiro.
     *
     * @param question
     * @return
     */
    private BigInteger getIntWithQuestion(String question) {
        System.out.println(DataDTO.SEPARATOR);
        System.out.println(question);
        System.out.println(DataDTO.SEPARATOR);
        return new Scanner(System.in).nextBigInteger();
    }

    /**
     * Método responsável por verificar se o arquivo existe no caminho especificado.
     *
     * @param path
     * @return
     */
    public File checkIfFileExists(String path) {
        File file = new File(path);
        if (!file.exists()) {
            log.warning("Arquivo não encontrado");
            System.exit(1);
        }
        return file;
    }
}
