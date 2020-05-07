package edu.uclm.esi.apalabreitor.apalabreitor.model;

import java.security.SecureRandom;
import java.util.HashMap;

public class Randomizer {
	private boolean random;
	private LettersCollection letters;
	private HashMap<Character, Integer> puntos;
	private static SecureRandom dado = new SecureRandom();
	
	public Randomizer(boolean random) {
		this.random = random;
	}
	
	public User getJugadorConElTurno(User playerA, User playerB) {
		if(random) return dado.nextBoolean() ? playerA : playerB;
		return playerA;
	}
	
	private void addLetter(int cantidad, char letra) {
		this.letters.add(cantidad, letra, puntos.get(letra));
	}
	
	public void disorderLetters(LettersCollection letters, HashMap<Character, Integer> puntos) {
		this.letters = letters;
		this.puntos = puntos;
		
		if (this.random) {
			addLetter(12, 'A'); addLetter(2, 'B'); addLetter(4, 'C'); addLetter(5, 'D');
			addLetter(12, 'E'); addLetter(1, 'F'); addLetter(2, 'G'); addLetter(2, 'H');
			addLetter(6, 'I'); addLetter(1, 'J'); addLetter(4, 'L'); addLetter(2, 'M');
			addLetter(5, 'N'); addLetter(1, 'Ñ'); addLetter(9, 'O'); addLetter(2, 'P');
			addLetter(1, 'Q'); addLetter(5, 'R'); addLetter(6, 'S'); addLetter(4, 'T');
			addLetter(5, 'U'); addLetter(1, 'V'); addLetter(1, 'X'); addLetter(1, 'Y'); addLetter(1, 'Z');
			
			for (int i=0; i<300; i++) {
				int posA = dado.nextInt(letters.size());
				int posB = dado.nextInt(letters.size());
				Letter letraPosA = letters.get(posA);
				letters.set(posA, letters.get(posB));
				letters.set(posB, letraPosA);
			}
		} else {
			this.addLetter(1, 'H'); this.addLetter(1, 'O'); this.addLetter(1, 'L'); this.addLetter(1, 'A'); this.addLetter(1, 'A'); this.addLetter(1, 'L'); this.addLetter(1, 'E');   
			this.addLetter(1, 'A'); this.addLetter(1, 'D'); this.addLetter(1, 'I'); this.addLetter(1, 'O'); this.addLetter(1, 'S'); this.addLetter(1, 'O'); this.addLetter(1, 'D'); 
			this.addLetter(1, 'O'); this.addLetter(1, 'C'); this.addLetter(1, 'E'); this.addLetter(1, 'A'); this.addLetter(1, 'S'); this.addLetter(1, 'A'); 
			this.addLetter(1, 'T'); this.addLetter(1, 'R'); this.addLetter(1, 'M'); this.addLetter(1, 'E'); this.addLetter(1, 'L'); this.addLetter(1, 'X');
			this.addLetter(1, 'Z'); this.addLetter(1, 'V'); this.addLetter(1, 'X'); this.addLetter(1, 'Y'); this.addLetter(1, 'Z'); this.addLetter(1, 'Y');
		}
	}
	
}
