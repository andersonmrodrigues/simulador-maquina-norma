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
    private File file;
    private LinkedHashMap<String, BigInteger> registersMap;
    private LinkedHashMap<Integer, String> fileLines;
    private Logger logger;

    public void createRegistersMap() {
        logger.info("Gerando registradores");
        registersMap = new LinkedHashMap<>();
        String[] alphabet = "abcdefghijklmnopqrstuvwxyz".split("");
        for (int i = 0; i < registers.intValue(); i++) {
            registersMap.put(alphabet[i], BigInteger.ZERO);
        }
        logger.info("Registradores Gerados");
    }

    public void createFileMap() {
        int count = 1;
        fileLines = new LinkedHashMap<>();
        logger.info("Lendo linhas do arquivo");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] value = line.split(":");
                fileLines.put(Integer.valueOf(value[0]), value[1].trim());
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

}
