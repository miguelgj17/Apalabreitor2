var self;
function UserViewModel() {
	self = this;
	self.ws = null;

	this.email = ko.observable();
	this.userName = ko.observable();
	this.pwd = ko.observable();
	this.turno = ko.observable();
	this.turnoOponente = ko.observable();
	this.turno = ko.observable("");
	this.turnoOponente = ko.observable("");
	this.playerA = ko.observable();
	this.playerB = ko.observable();
	this.puntuacionA = ko.observable();
	this.puntuacionB = ko.observable();
	
	
	this.cambio = ko.observable();
	this.ganador = ko.observable();
	this.perdedor = ko.observable();
	
	
	this.tablero = ko.observable(new Tablero(ko));
		
	//Controlling hide & show tablero and reg+login forms
	this.shouldShowRegLog = ko.observable(true);
	this.shouldShowTablero = ko.observable(false);
	this.shouldShowWelcome = ko.observable(false);
	this.shouldShowWinner= ko.observable(false);
	this.shouldShowCambiarLetras = ko.observable(false);
	
	//Textarea for logs
	this.message = ko.observable();
	var puntosA=0;
	var puntosB=0;
	
	this.nuevaPartida = function() {
		var info = {
			action : "Nueva partida"
		};
		var data = {
			data : info,
			url : "solicitarPartida",
			type : "post",
			success : partidaOK,
			error : error
		};
		$.ajax(data);
	}
	
	function partidaOK(respuesta) {
		self.message(respuesta);
		respuesta=JSON.parse(respuesta);
		if (respuesta.idPartida)
			sessionStorage.idPartida = respuesta.idPartida;
		self.ws = new WebSocket("ws://localhost:8080/wsServer");
		self.ws.onopen = function(event) {
			if (respuesta.type == "PARTIDA LISTA") {
				var mensaje = {
						type : "INICIAR PARTIDA",
						idPartida : respuesta.idPartida
				};
				self.ws.send(JSON.stringify(mensaje));
			}	
		}
		self.ws.onerror = function(event) {
			self.message("Error");
		}
		self.ws.onclose = function(event) {
			self.message("WebSocket cerrado");
		}
		self.ws.onmessage = function(event) {
			var jso = event.data;
			jso = JSON.parse(jso);
			self.message(event.data);
			if (jso.type == "START") {
				var r = (jso.turno ? "Tienes " : "No tienes ") +
				"el turno. Tus letras son: " + jso.letras;

				$("#jugar").attr("disabled", !jso.turno);
				$("#pasar").attr("disabled", !jso.turno);		
				$("#mezclar").attr("disabled", !jso.turno);
				$("#cambiar").attr("desabled", !jso.turno);



				//Show Tablero and Hide Reg and Log forms
				self.shouldShowRegLog(false);
				self.shouldShowTablero(true);

				self.message(r);

				//Initialize Tablero with letras
				for (var i=0; i<jso.letras.length; i++)
					self.tablero().panel.push(jso.letras[i]);

				var player1 = jso.nameA;
				self.playerA(player1);

				var player2 = jso.nameB;
				self.playerB(player2);

				self.puntuacionA("0");
				self.puntuacionB("0");
				self.cambio=false;
			}

			if(jso.type == "resultado"){
				if(jso.nombre == self.playerA()){
					puntosA += jso.puntos;
					self.puntuacionA(puntosA);
				}else{
					puntosB += jso.puntos;
					self.puntuacionB(puntosB);
				}


				if(jso.partidaTerminada){
					self.message("PARTIDA TERMINADA WINNER: "+ jso.ganador);
					self.ganador(jso.ganador);
					self.perdedor(jso.perdedor);
					self.shouldShowWinner(true);
					self.shouldShowTablero(false);
				}else{
					if(!jso.cambio && jso.valid.length > 0){
						recargar(jso.board);
					}

					if(jso.cambio){
						while(self.tablero().panel().length > 0)
							self.tablero().panel.pop();

						for(var i=0; i<jso.letrasCambiadas.length; i++){
							self.tablero().panel.push(jso.letrasCambiadas[i]);
						}
					}

					if(jso.turno){
						if(puntosA>=30 || puntosB>=30){
							self.tablero().rendirse();
						}

						if(jso.exceptions.length>0 ||jso.invalid.length>0){
							self.tablero().llamar();
							recargar(jso.board);
							self.tablero().casillasJugada.length=0;
						}else if(jso.exceptions.length==0 && jso.invalid.length == 0){
							$("#jugar").attr("disabled", !jso.turno);
							$("#pasar").attr("disabled", !jso.turno);
							$("#cambiar").attr("disabled", !jso.turno);
							$("#mezclar").attr("disabled", !jso.turno);
							$("#rendirse").attr("disabled", !jso.turno);
						}
					}else if(jso.exceptions.length==0 && jso.invalid.length == 0){
						var puntos = parseInt(self.puntuacionA()) + parseInt(jso.puntos);

						$("#jugar").attr("disabled", !jso.turno);
						$("#pasar").attr("disabled", !jso.turno);
						$("#cambiar").attr("disabled", !jso.turno);
						$("#mezclar").attr("disabled", !jso.turno);
						$("#rendirse").attr("disabled", !jso.turno);

						var auxiliar = 0;
						while(auxiliar<self.tablero().casillasJugada.length){
							self.tablero().panel.push(jso.letrasNuevas[auxiliar]);
							auxiliar++;
						}
						self.tablero().casillasJugada.length=0;
					}
				}
			}
		}
	}
	
	function recargar(tablero){
		if(tablero){
			var contador = 0;
			var letras = 0;
			
			for(var i=0; i<15; i++){
				var fila = self.tablero().casillas()[i];
				for(var j=0; j<15; j++){
					var celda = fila[j];
					
					if(tablero[contador] != '-'){
						celda.letter(tablero[contador])
						celda.bloquear();
						letras++;
					}
					contador++;
				}
			}
		}
	}
	
	this.unirAPartida = function() {
		var info = {
			action : "Unir a partida"
		};
		var data = {
			data : info,
			url : "solicitarPartida",
			type : "post",
			success : partidaOK,
			error : error
		};
		$.ajax(data);
	}
	
	this.getUsers = function() {
		$.get("listaUsuarios", mostrarUsuarios);
	}
	
	function mostrarUsuarios(respuesta) {
		self.message(JSON.stringify(respuesta));
	}
	
	this.login = function() {
		var info = {
			userName : $("#loginUserName").val(),
			pwd : this.pwd(),
			withEmail : ($("#loginUserName").val().indexOf("@")!=-1)
		};
		
		var data = {
			data : info,
			url : "login",
			type : "post",
			success : loginOk,
			error : error
		};
		$.ajax(data);
	}
	
	this.register = function() {
		var info = {
			email : $("#inputEmail").val(),
			userName : $("#inputUserName").val(),
			pwd1 : $("#inputPwd1").val(),
			pwd2 : $("#inputPwd2").val()
		};
		var data = {
			data : info,
			url : "register",
			type : "post",
			success : registerOk,
			error : error
		};
		$.ajax(data);		
	}
	
	function registerOk() {
		self.userName($("#inputUserName").val());
		self.email($("#inputEmail").val());
		self.pwd($("#inputPwd1").val());
		$("#message").attr("style", "color:blue");
		self.message("Register OK");
	}
	
	function loginOk() {
		$("#message").attr("style", "color:blue");
		self.message("Login OK");
	}
	
	function error(response) {
		$("#message").attr("style", "color:red");
		self.message(response.responseText);
	}
}

class Tablero {
	constructor(ko) {
		this.casillasNoKO = new Array();
		for (var i=0; i<15; i++) {
			this.casillasNoKO.push(new Array());
			for (var j=0; j<15; j++)
				this.casillasNoKO[i][j]=new Casilla(ko, this, i, j);
		}
		var tp=[[0, 2], [0, 12], [2, 0], [2, 14], [12, 0], [12, 14], [14, 2], [14, 12]];
		for (var i=0; i<tp.length; i++) {
			var coords = tp[i];
			this.casillasNoKO[coords[0]][coords[1]].letter("TP");
			this.casillasNoKO[coords[0]][coords[1]].clazz("scrabble-td triple-word");
		}
		tp=[[0, 4], [0, 10], [1, 1], [1, 13], [2, 6], [2, 8], [3, 3], [3, 11], [4, 0], [4, 14], [5, 5], [5, 9], [6, 2], [6, 12], [8, 2], [8, 12], [9, 5], [9, 9], 
			[10, 0], [10, 14], [11, 3], [11, 11], [12, 6], [12, 8], [13, 1], [13, 13], [14, 4], [14, 10]];
		for (var i=0; i<tp.length; i++) {
			var coords = tp[i];
			this.casillasNoKO[coords[0]][coords[1]].letter("TL");
			this.casillasNoKO[coords[0]][coords[1]].clazz("scrabble-td triple-letter");
		}
		tp=[[1, 5], [1, 9], [3, 7], [5, 1], [5, 13], [7,3], [7, 11], [9, 1], [9, 13], [11, 7], [13, 5], [13, 9]];
		for (var i=0; i<tp.length; i++) {
			var coords = tp[i];
			this.casillasNoKO[coords[0]][coords[1]].letter("DP");
			this.casillasNoKO[coords[0]][coords[1]].clazz("scrabble-td double-word");
		}
		tp=[[2, 2], [2, 12], [4, 6], [4, 8], [6, 4], [6, 10], [8, 4], [8, 10], [10, 6], [10, 8], [12, 2], [12, 12]];
		for (var i=0; i<tp.length; i++) {
			var coords = tp[i];
			this.casillasNoKO[coords[0]][coords[1]].letter("DL");
			this.casillasNoKO[coords[0]][coords[1]].clazz("scrabble-td double-letter");
		}
		this.casillasNoKO[7][7].letter("★");
		this.casillas=ko.observableArray(this.casillasNoKO);
		this.casillasJugada = [];
		this.panel = ko.observableArray([]);
		this.casillaSeleccionada = null;
	}
	
	seleccionar(letra) {
		if (this.casillaSeleccionada == null) {
			alert("No has seleccionado la casilla");
			return;
		}else if(!this.casillaSeleccionada.definitiva){
			for (var i=0; i<this.panel().length; i++) {
				if (this.panel()[i]==letra) {
					this.panel.splice(i, 1);
					this.casillaSeleccionada.letter(letra);
					this.casillaSeleccionada=null;
					break;
				}
			}
		}
		
	}
	
	jugar() {
		var casillas = [];
		for (var i=0; i<this.casillasJugada.length; i++) {
			casillas.push({
				row : this.casillasJugada[i].row,
				col : this.casillasJugada[i].column,
				letter : this.casillasJugada[i].letter()
			});
		}
		var msg = {
			type : "MOVIMIENTO",
			idPartida : sessionStorage.idPartida,
			casillas : casillas
		};
		self.ws.send(JSON.stringify(msg));
		console.log(this);
	}
	
	pasar(){
		var msg = {
			type : "PASO",
			idPartida: sessionStorage.idPartida
		};
		self.ws.send(JSON.stringify(msg));
	}
	
	mezclar(){
		var panel2 = [];
		for(var i=0; i<this.panel().length; i++)
			panel2.push(this.panel()[i]);
			var j, x, i;
			
		    for (i = this.panel().length - 1; i > 0; i--) {
		        j = Math.floor(Math.random() * (i + 1));
		        x = panel2[i];
		        panel2[i] = panel2[j];
		        panel2[j] = x;
		    }
		    this.panel(panel2);
	}
	
	cambiar(){
		var msg = {
				type : "CAMBIAR LETRAS",
				idPartida: sessionStorage.idPartida
		};
		self.ws.send(JSON.stringify(msg));
	}
	
	rendirse(){
		var msg = {
				type : "RENDIRSE",
				idPartida: sessionStorage.idPartida
		};
		self.ws.send(JSON.stringify(msg));
	}
	
	
}

class Casilla {
	constructor(ko, tablero, row, column, turno) {
		this.tablero = tablero;
		this.letter = ko.observable('');
		this.clazz = ko.observable("scrabble-td");
		this.row = row;
		this.column = column;
		this.turno= turno;
		this.definitiva = false;
	}
	
	seleccionar() {
		if (this.letter()!='' && this.letter()!='★' && this.definitiva == false && this.letter()!='TL' && this.letter()!='TP' && this.letter()!='DL' && this.letter()!='DP') {
			this.tablero.panel.push(this.letter());
			this.letter('');
			var pos = this.tablero.casillasJugada.indexOf(this);
			this.tablero.casillasJugada.splice(pos, 1);
			return;
		}
		this.tablero.casillaSeleccionada = this;
		this.tablero.casillasJugada.push(this);
	}
	
	bloquear(){
		this.definitiva = true;
	}
}

this.onSingIn = function(googleUser){
	var profile = googleUser.getBasicProfile();
	console.log('ID: ' + profile.getId());
	console.log('Name: ' + profile.getName());
	console.log('Image URL: ' + profile.getImageUrl());
	console.log('Email: ' + profile.getEmail());
	
	$("#message").attr("style", "color:green");
	self.message("LoginOK");
	self.shouldShowRegLog(false);
	self.shouldShowWelcome(true);
	
	var info = {
		userName : profile.getName(),
		email : profile.getEmail()
	};
	
	var data = {
			data : info,
			url : "login2",
			type : "post"
	};
	
	$.ajax(data);
	
}

var user = new UserViewModel();
ko.applyBindings(user);