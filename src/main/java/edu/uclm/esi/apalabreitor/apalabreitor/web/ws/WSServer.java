package edu.uclm.esi.apalabreitor.apalabreitor.web.ws;

import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import edu.uclm.esi.apalabreitor.apalabreitor.model.Match;
import edu.uclm.esi.apalabreitor.apalabreitor.model.User;
import edu.uclm.esi.apalabreitor.apalabreitor.web.controllers.WebController;

@Component
public class WSServer extends TextWebSocketHandler {
	private static ConcurrentHashMap<String, WebSocketSession> sessionsById=new ConcurrentHashMap<>();
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessionsById.put(session.getId(), session);
		User user = (User) session.getAttributes().get("user");
		user.setWebSocketSession(session);
	}
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		System.out.println(message.getPayload());
		JSONObject jso=new JSONObject(message.getPayload());
		String type = jso.getString("type");
		switch (type) {
		case "INICIAR PARTIDA" :
			String idPartida = jso.getString("idPartida");
			Match match = WebController.inPlayMatches.get(idPartida);
			match.start();
			break;
		case "MOVIMIENTO" : // el jugador ha puesto letras y hay que...
			idPartida = jso.getString("idPartida");
			match = WebController.inPlayMatches.get(idPartida);
			match.playerPlays(session.getId(), jso.getJSONArray("casillas"));
			break;
		case "CAMBIAR LETRAS" :
			idPartida = jso.getString("idPartida");
			match = WebController.inPlayMatches.get(idPartida);
			match.cambiarLetras(session.getId());
			break;
		case "PASO" :
			idPartida = jso.getString("idPartida");
			match = WebController.inPlayMatches.get(idPartida);
			match.pasarTurno();
			break;
		case "RENDIRSE" :
			idPartida = jso.getString("idPartida");
			match = WebController.inPlayMatches.remove(idPartida);
			match.rendirse(session.getId());
		}
	}

	private void sendError(WebSocketSession session, String message) throws Exception {
		JSONObject jso = new JSONObject();
		jso.put("TYPE", "ERROR");
		jso.put("MESSAGE", message);
		WebSocketMessage<?> wsMessage=new TextMessage(jso.toString());
		session.sendMessage(wsMessage);
	}
}
