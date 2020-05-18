package edu.uclm.esi.apalabreitor.apalabreitor.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResultadoJugada {
	private List<Cadena> valid;
	private List<Cadena> invalid;
	private List<String> exceptions;
	private Board board;
	private String letrasNuevas, letrasCambiadas;
	private boolean cambio, turno, partidaTerminada;
	private String ganador, perdedor;
	private User jugador;
	
	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public String getLetrasNuevas() {
		return letrasNuevas;
	}

	public void setLetrasNuevas(String letrasNuevas) {
		this.letrasNuevas = letrasNuevas;
	}

	public String getLetrasCambiadas() {
		return letrasCambiadas;
	}

	public void setLetrasCambiadas(String letrasCambiadas) {
		this.letrasCambiadas = letrasCambiadas;
	}

	public boolean isCambio() {
		return cambio;
	}

	public void setCambio(boolean cambio) {
		this.cambio = cambio;
	}

	public boolean isTurno() {
		return turno;
	}

	public void setTurno(boolean turno) {
		this.turno = turno;
	}

	public boolean isPartidaTerminada() {
		return partidaTerminada;
	}

	public void setPartidaTerminada(boolean partidaTerminada) {
		this.partidaTerminada = partidaTerminada;
	}

	public String getGanador() {
		return ganador;
	}

	public void setGanador(String ganador) {
		this.ganador = ganador;
	}

	public String getPerdedor() {
		return perdedor;
	}

	public void setPerdedor(String perdedor) {
		this.perdedor = perdedor;
	}

	public User getJugador() {
		return jugador;
	}

	public void setJugador(User jugador) {
		this.jugador = jugador;
	}

	public void setValid(List<Cadena> valid) {
		this.valid = valid;
	}

	public void setInvalid(List<Cadena> invalid) {
		this.invalid = invalid;
	}

	public void setExceptions(List<String> exceptions) {
		this.exceptions = exceptions;
	}

	public ResultadoJugada() {
		this.valid=new ArrayList<>();
		this.invalid = new ArrayList<>();
		this.exceptions = new ArrayList<>();
	}

	public int getPoints() {
		int r=0;
		for (Cadena cadena : valid)
			r+=cadena.getPoints();
		return r;
	}
	
	public List<Cadena> getValid() {
		return valid;
	}
	
	public List<Cadena> getInvalid() {
		return invalid;
	}
	
	public List<String> getExceptions() {
		return exceptions;
	}

	public void addNotAccepted(Cadena cadena) {
		this.invalid.add(cadena);
	}

	public void addAccepted(Cadena cadena) {
		this.valid.add(cadena);
	}

	public boolean accepts(String... palabras) {
		for (String palabra : palabras) {
			if (!in(palabra, this.valid))
				return false;
		}
		return true;
	}	
	
	public boolean notAccepts(String... palabras) {
		for (String palabra : palabras) {
			if (!in(palabra, this.invalid))
				return false;
		}	
		return true;
	}

	private boolean in(String palabra, List<Cadena> cadenas) {
		for (Cadena cadena : cadenas)
			if (cadena.getText().equals(palabra))
				return true;
		return false;
	}

	public boolean acceptsAll() {
		return invalid.isEmpty();
	}

	public void addException(String message) {
		this.exceptions.add(message);
	}

	public List<Cadena> invalid() {
		return this.invalid;
	}
	
	public int getPuntos() {
		int r=0;
		for (Cadena cadena : valid) r+=cadena.getPoints();
		return r;
	}
	
	public void ocultarLetras() {
		this.letrasNuevas="";
	}
	
	@Override
	public String toString() {
		return "Válidas: " + valid.toString() + "\nInválidas: " + invalid.toString() + "\nPoints: " + this.getPuntos();
	}

	public JSONObject toJSON() throws JSONException {
		JSONObject jso = new JSONObject();
		JSONArray jsaValid=new JSONArray();
		for (Cadena cadena : valid)
			jsaValid.put(cadena.toJSON());
		jso.put("valid", jsaValid);
		JSONArray jsaInvalid=new JSONArray();
		for (Cadena cadena : invalid)
			jsaInvalid.put(cadena.toJSON());
		jso.put("invalid", jsaInvalid);
		JSONArray jsaExceptions=new JSONArray();
		for (String ex : exceptions)
			jsaExceptions.put(ex);
		jso.put("exceptions", jsaExceptions);
		jso.put("type", "resultado");
		jso.put("partidaTerminada", this.partidaTerminada);
		jso.put("ganador", this.ganador);
		jso.put("perdedor", this.perdedor);
		jso.put("cambio", this.cambio);
		jso.put("letrasCambiadas", this.letrasCambiadas);
		
		if (board!=null)
			jso.put("board", this.board.toString());
		jso.put("turno",this.turno);
		jso.put("letrasNuevas", this.letrasNuevas);
		if(jugador!=null)
			jso.put("nombre", this.jugador.getUserName());
		jso.put("puntos", this.getPuntos());
		return jso;
	}
}
