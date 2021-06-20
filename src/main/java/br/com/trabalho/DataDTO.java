package br.com.trabalho;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.io.*;
import java.math.BigInteger;
import java.util.LinkedHashMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Log
public class DataDTO {

    public static final String SEPARATOR = "------------------------------------";
    private BigInteger registers;
    private File file;
    private LinkedHashMap<String, BigInteger> registersMap;
    private LinkedHashMap<Integer, String> fileLines;

    public void createRegistersMap() {
        log.info("Gerando registradores");
        registersMap = new LinkedHashMap<>();
        String[] alphabet = "abcdefghijklmnopqrstuvwxyz".split("");
        for (int i = 0; i < registers.intValue(); i++) {
            registersMap.put(alphabet[i], BigInteger.ZERO);
        }
        log.info("Registradores Gerados");
    }

    public void createFileMap() {
        int count = 1;
        fileLines = new LinkedHashMap<>();
        log.info("Lendo linhas do arquivos");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] value = line.split(":");
                fileLines.put(Integer.valueOf(value[0]), value[1].trim());
                count++;
            }
        } catch (FileNotFoundException e) {
            log.warning("Arquivo não encontrado");
            e.printStackTrace();
        } catch (IOException e) {
            log.warning("Erro ao ler arquivo");
            e.printStackTrace();
        }
        log.info("Total de " + count + " linhas lidas");

    }
}
