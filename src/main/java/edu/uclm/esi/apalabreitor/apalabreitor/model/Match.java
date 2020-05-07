package edu.uclm.esi.apalabreitor.apalabreitor.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Match {
	private String id;
	private User playerA;
	private User playerB;
	private User jugadorConElTurno;
	private User ganador;
	
	private String lettersA, lettersB;
	
	private int puntosA=0, puntosB=0;
	private Board board;
	
	private Randomizer randomizer;
	
	public Match() {
		this.id = UUID.randomUUID().toString();
		this.randomizer = new Randomizer(false); // FALSE para probar el juego
	}

	public void setPlayerA(User user) {
		this.playerA = user;
	}

	public void setPlayerB(User playerB) {
		this.playerB = playerB;
	}
	
	public User getGanador() {
		return ganador;
	}

	public void setGanador(User ganador) {
		this.ganador = ganador;
	}

	public String getLettersA() {
		return lettersA;
	}

	public void setLettersA(String lettersA) {
		this.lettersA = lettersA;
	}

	public String getLettersB() {
		return lettersB;
	}

	public void setLettersB(String lettersB) {
		this.lettersB = lettersB;
	}

	public int getPuntosA() {
		return puntosA;
	}

	public void setPuntosA(int puntosA) {
		this.puntosA = puntosA;
	}

	public int getPuntosB() {
		return puntosB;
	}

	public void setPuntosB(int puntosB) {
		this.puntosB = puntosB;
	}

	public String getId() {
		return id;
	}

	public void start() {
		this.jugadorConElTurno = this.randomizer.getJugadorConElTurno(this.playerA, this.playerB);
		this.board = new Board(this.randomizer);
		this.lettersA=this.board.getLetters(7);
		this.lettersB=this.board.getLetters(7);
		
		
		try {
			JSONObject jsaA = new JSONObject();
			jsaA.put("type", "START");
			jsaA.put("letras", this.lettersA);
			jsaA.put("turno", jugadorConElTurno==playerA ? true : false);
			jsaA.put("unameA", this.playerA.getUserName());
			jsaA.put("unameB", this.playerB.getUserName());
			jsaA.put("puntosA", this.puntosA);
			jsaA.put("puntosB", this.puntosB);
			this.playerA.sendMessage(jsaA.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			
		}
		try {
			JSONObject jsaB = new JSONObject();
			jsaB.put("type", "START");
			jsaB.put("letras", this.lettersB);
			jsaB.put("turno", jugadorConElTurno==playerB ? true : false);
			jsaB.put("unameA", this.playerA.getUserName());
			jsaB.put("unameB", this.playerB.getUserName());
			jsaB.put("puntosA", this.puntosA);
			jsaB.put("puntosB", this.puntosB);
			this.playerB.sendMessage(jsaB.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			
		}
	}

	public void playerPlays(String idSession, JSONArray jsaJugada) throws Exception {
		ResultadoJugada resultado;
		User player = this.playerA.getSession().getId().equals(idSession) ? playerA : playerB;
		if (player!=this.jugadorConElTurno) {
			resultado = new ResultadoJugada();
			resultado.addException("No tienes el turno");
			player.sendMessage(resultado);
		} else {
			ArrayList<JSONObject> jugada = new ArrayList<>();
			for (int i=0; i<jsaJugada.length(); i++)
				jugada.add(jsaJugada.getJSONObject(i));
			resultado = this.board.movement(jugada);
			
			if(resultado.getExceptions().isEmpty() && resultado.invalid().isEmpty()) {
				resultado.setTurno(false);
				resultado.setJugador(player);
				player.sendMessage(resultado);
				
				User contrincante = this.playerA==player ? playerB : playerA;
				resultado.ocultarLetras();
				resultado.setCambio(false);
				resultado.setTurno(true);
				
				contrincante.sendMessage(resultado);
				cambiarTurno();
			} else {
				resultado.setTurno(true);
				player.sendMessage(resultado);
				this.board.deshacer(jugada);
			}
		}
	}
	
	public void pasarTurno() {
		cambiarTurno();
		ResultadoJugada resultado = new ResultadoJugada();
		resultado.setTurno(true);
		try {
			this.jugadorConElTurno.sendMessage(resultado);
			resultado.setTurno(false);
			User otro = (this.playerA==this.jugadorConElTurno ? this.playerB : this.playerA);
			otro.sendMessage(resultado);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}


	private void cambiarTurno() {
		this.jugadorConElTurno = (this.playerA==this.jugadorConElTurno ? this.playerB : this.playerA);
	}
	
	
}
