package br.com.trabalho;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.io.File;
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

    public void createRegistersMap() {
        log.info("Gerando registradores");
        registersMap = new LinkedHashMap<>();
        String[] alphabet = "abcdefghijklmnopqrstuvwxyz".split("");
        for (int i = 0; i < registers.intValue(); i++) {
            registersMap.put(alphabet[i], BigInteger.ZERO);
        }
        log.info("Registradores Gerados");
    }
}
