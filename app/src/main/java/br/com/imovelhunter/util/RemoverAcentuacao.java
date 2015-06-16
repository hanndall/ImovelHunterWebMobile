package br.com.imovelhunter.util;

import java.text.Normalizer;

public class RemoverAcentuacao {

	public String removerAcentos(String texto) {
		texto = texto.toLowerCase();
		return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}

}
