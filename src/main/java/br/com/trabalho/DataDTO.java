package br.com.trabalho;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataDTO {

    public static final String SEPARATOR = "------------------------------------";
    private BigInteger registers;
    private File fileMonolithic;
    private File fileIterative;
    private File fileRecursive;
    private LinkedHashMap<String, BigInteger> registersMap;
    private LinkedHashMap<Integer, String> fileMonolithicLines;
    private LinkedHashMap<Integer, String> fileIterativeLines;
    private LinkedHashMap<Integer, String> fileRecursiveLines;
    private Integer iterativeLastLineNumber;
    private Integer recursiveLastLineNumber;
    private String firstDef;
    private Logger logger;

    /**
     * Método responsável pela criação dos registradores conforme a quantidade especificada pelo usuário,
     * cada registrador receberá um letra do alfabeto de forma sequencial.
     */
    public void createRegistersMap() {
        logger.info("Gerando registradores");
        registersMap = new LinkedHashMap<>();
        String[] alphabet = "abcdefghijklmnopqrstuvwxyz".split("");
        for (int i = 0; i < registers.intValue(); i++) {
            registersMap.put(alphabet[i], BigInteger.ZERO);
        }
        logger.info("Registradores Gerados");
    }

    /**
     * Método utilizado para mapear o “Map” com as linhas do arquivo do programa iterativo.
     */
    public void iterativeMap() {
        int count = 1;
        fileIterativeLines = new LinkedHashMap<>();
        logger.info("Lendo linhas do arquivo");
        try (BufferedReader br = new BufferedReader(new FileReader(fileIterative))) {
            String line;
            while ((line = br.readLine()) != null) {
                fileIterativeLines.put(count, line
                        .trim().replaceAll(";", "")
                        .replaceAll("\\)", "")
                        .replaceAll("\\(", "")
                );
                count++;
            }
        } catch (FileNotFoundException e) {
            logger.warning("Arquivo não encontrado");
            e.printStackTrace();
        } catch (IOException e) {
            logger.warning("Erro ao ler arquivo");
            e.printStackTrace();
        }
        this.iterativeLastLineNumber = count;
        logger.info("Total de " + count + " linhas lidas");
    }

    /**
     * Método utilizado para mapear o “Map” com as linhas do arquivo do programa monolítico.
     */
    public void monolithicMap() {
        int count = 1;
        fileMonolithicLines = new LinkedHashMap<>();
        logger.info("Lendo linhas do arquivo");
        try (BufferedReader br = new BufferedReader(new FileReader(fileMonolithic))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] value = line.split(":");
                fileMonolithicLines.put(Integer.valueOf(value[0]), value[1].trim());
                count++;
            }
        } catch (FileNotFoundException e) {
            logger.warning("Arquivo não encontrado");
            e.printStackTrace();
        } catch (IOException e) {
            logger.warning("Erro ao ler arquivo");
            e.printStackTrace();
        }
        logger.info("Total de " + count + " linhas lidas");
    }

    /**
     * Método utilizado para verificar se a linha que está sendo passada via parâmetro
     * possui algum comando de “enquanto/faça”.
     *
     * @param line
     * @return
     */
    public boolean isMatchesEnquantoFaca(String line) {
        return line.matches("(.*)enquanto ([a-zA-Z]*)_zero faça(.*)");
    }

    /**
     * Método utilizado para se a linha que está sendo passada via parâmetro possui algum comando de “se/senao”
     *
     * @param line
     * @return
     */
    public boolean isMatchesSeSenao(String line) {
        return line.matches("(.*)se ([a-zA-Z]*)_zero então (.+) senão (.+)(.*)");
    }

    /**
     * Método utilizado para verificar se a linha que está sendo passada via parâmetro possui alguma
     * instrução para a operação de adicionar.
     *
     * @param line
     * @return
     */
    public boolean isMatchesAd(String line) {
        return line.matches("(.*)ad_([a-zA-Z]*)(.*)");
    }

    /**
     * Método utilizado para verificar se a linha que está sendo passada via parâmetro possui alguma instrução
     * para a operação de subtrair.
     *
     * @param line
     * @return
     */
    public boolean isMatchesSub(String line) {
        return line.matches("(.*)sub_([a-zA-Z]*)(.*)");
    }

    /**
     * Método utilizado para verificar se a linha que está sendo passada via parâmetro possui algum comando “até/faça”.
     *
     * @param line
     * @return
     */
    public boolean isMatchesAteFaca(String line) {
        return line.matches("(.*)até ([a-zA-Z]*)_zero faça(.*)");
    }

    /**
     * Método utilizado para verificar se a linha que está sendo passada via parâmetro possui algum “def”.
     *
     * @param line
     * @return
     */
    public boolean isMatchesDef(String line) {
        return line.matches("(.*)def(.*)");
    }

    /**
     * Método utilizado para mapear o “Map” com as linhas do arquivo recursivo.
     */
    public void recursiveMap() {
        int count = 1;
        fileRecursiveLines = new LinkedHashMap<>();
        logger.info("Lendo linhas do arquivo");
        try (BufferedReader br = new BufferedReader(new FileReader(fileRecursive))) {
            String line;
            while ((line = br.readLine()) != null) {
                fileRecursiveLines.put(count, line
                        .trim());
                count++;
            }
        } catch (FileNotFoundException e) {
            logger.warning("Arquivo não encontrado");
            e.printStackTrace();
        } catch (IOException e) {
            logger.warning("Erro ao ler arquivo");
            e.printStackTrace();
        }
        this.recursiveLastLineNumber = count;
        logger.info("Total de " + count + " linhas lidas");
    }
}
