package br.pucpr.system;

import java.util.Comparator;
import java.util.List;

public class ComparadorUsuarios implements Comparator<List<String>> {

    /**
     * @param usuario1 o primeiro objeto a ser comparado.
     * @param usuario2 o segundo objeto a ser comparado.
     * @return a comparaçao entre os dois gastos totais dos usuarios
     */
    public int compare(List<String> usuario1, List<String> usuario2) {
        return Double.compare(Double.parseDouble(usuario1.get(1)), Double.parseDouble(usuario2.get(1)));
    }
}
