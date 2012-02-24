package pkgBluetooth;

import javax.bluetooth.*;
import javax.microedition.io.*;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import pkgJuego.Moneda;
import pkgJuego.Nave;
import pkgJuego.TiroPos;
import pkgUI.MenuServidor;
import pkgUtils.Utils;

public class Servidor {

	private StreamConnection conexiones[];
	private DataInputStream entradas[];
	private DataOutputStream salidas[];
	protected int num_jugadores; //cantidad de jugadores en total
	private int num_conexiones; //número de clientes conectados
	protected String jugadores[]; //nombres de los jugadores
	private static boolean escuchando;
	private MenuServidor menu;
	private StreamConnectionNotifier not;
	protected Nave naves[];
	protected TiroPos tiros[];
	protected int vivos;
	protected int FRmodelo; //framerate establecido a mandar a todos
	protected int miFR;
	private int FRleido;
	private Thread hiloEscucha;
	private Enumeration enum;
	
	//---------------------------------------------------------------
	//para el control de las monedas:
	//---------------------------------------------------------------
	protected Hashtable monedas;
	protected Vector monedas_nuevas;
	protected Vector monedas_eliminadas;
	protected int idmonedas = 0;
	
	
	//---------------------------------------------------------------
	//variables locales que se estarían creando y destruyendo a cada rato:
	//---------------------------------------------------------------
	private String sin, sout, sasesin;
	private int txin, tyin;
	private int i, j, minfr, asesin;
	protected Moneda m;

	
	public Servidor(int cantidadJugadores, String nombre, MenuServidor m) {
		menu = m;
		FRmodelo = 30; //estimación inicial
		num_jugadores = cantidadJugadores;
		num_conexiones = num_jugadores - 1;
		jugadores = new String[num_jugadores];
		jugadores[num_jugadores-1] = nombre; //servidor tiene la última ID
		
		//posiciones iniciales:
		naves = new Nave[num_jugadores];
		for (i=0; i<naves.length; ++i){
			switch (i){
			case 0:
				naves[i] = new Nave(70, 70, i, 100, false); break;
			case 1:
				naves[i] = new Nave(920, 920, i, 100, false); break;
			case 2:
				naves[i] = new Nave(70, 920, i, 100, false); break;
			case 3:
				naves[i] = new Nave(920, 70, i, 100, false); break;
			}
		}
		for (i=0; i<num_jugadores; ++i)
			naves[i].setVivo(true);
		
		vivos = num_jugadores;
		
		tiros = new TiroPos[num_jugadores];
		for (i=0; i<tiros.length; ++i)
			tiros[i] = new TiroPos(0, false);
		
		
		monedas = new Hashtable();
		monedas_nuevas = new Vector();
		monedas_eliminadas = new Vector();
		
		escuchando = false;
		
	}
	
	public void Destruir() {
		//cerrar todas las conexiones
		System.out.println("Servidor: Cerrando servidor...");
		escuchando = false;
		hiloEscucha.interrupt();
		try {
			if (not != null)
				not.close();
			for (i=0; i<num_conexiones; ++i){
				if (entradas[i] != null) entradas[i].close();
				if (salidas[i] != null) salidas[i].close();
				if (conexiones[i] != null) conexiones[i].close();
			}
		} catch (IOException ex) {
			System.err.println("Servidor: Error al cerrar conexión.");
		}
		menu = null;
	}
	
	public void IniciarServidor() {
		conexiones = new StreamConnection[num_conexiones];
		entradas = new DataInputStream[num_conexiones];
		salidas = new DataOutputStream[num_conexiones];
		escuchando = true;
		hiloEscucha = new Thread() {
            public void run() { iniciar(); }
		};
		hiloEscucha.start();
	}
	
	
	//acá mandar las posiciones de todos
	protected void enviar() {
		
		if (FRmodelo == 0) FRmodelo = 1; //una última comprobación para no mandar un fr de 0
		sout = Utils.Rellenar("" + FRmodelo, 3);
		
		//datos de los jugadores:
		for (j=0; j<num_jugadores; ++j){
			sout += naves[j].Id(); //Id == j, pero por las dudas
			sout += Utils.Rellenar(jugadores[j], 10);
			sout += Utils.Rellenar("" + naves[j].E(), 4);
			sout += Utils.Rellenar("" + naves[j].X(), 4);
			sout += Utils.Rellenar("" + naves[j].Y(), 4);
			sout += naves[j].Dir();
			
			sout += Utils.Rellenar("" + tiros[j].Potencia(), 3);
			sout += Utils.Rellenar("" + tiros[j].X(), 4);
			sout += Utils.Rellenar("" + tiros[j].Y(), 4);
			
			sout += naves[j].Asesino();
			//para evitar enviar 2 veces el id del asesino:
			if (!naves[j].Asesino().equals("V")) naves[j].setAsesino("V");
		}

		
		//datos de las monedas:
		enum = monedas.elements();
		while (enum != null && enum.hasMoreElements()){
			Moneda coin = (Moneda)enum.nextElement();
			sout += Utils.Rellenar("" + coin.Id(), 4);
			sout += Utils.Rellenar("" + coin.getRefPixelX(), 4);
			sout += Utils.Rellenar("" + coin.getRefPixelY(), 4);
			sout += Utils.Rellenar("" + coin.Valor(), 2);
		}
		
		
		for (j=0; j<num_conexiones; ++j){
			try {
				salidas[j].writeUTF(sout);
				salidas[j].flush();
			} catch (IOException e) {
				//Cliente i caído
				//mandarlo como muerto
				if (naves[j].Vivo()){
					naves[j].setVivo(false);
					naves[j].setEscudo(-1);
					vivos--;
					tiros[j].setX(-1);
					tiros[j].setY(-1);
					tiros[j].setExiste(false);
				}
			}
		}
		
	}
	
	protected void iniciar() {
		//iniciar el servidor
		int jugador_actual = 0;
		try {
			LocalDevice localDevice = LocalDevice.getLocalDevice();
			if(!localDevice.setDiscoverable(DiscoveryAgent.GIAC)) {
				System.err.println("Servidor: imposible ofrecer un servicio");
				menu.Mensaje("Error: No se puede iniciar la conexión.");
				return;
			}
			System.out.println("Servidor iniciado, esperando jugadores...");
			StringBuffer url = new StringBuffer("btspp://localhost:");
			url.append(Utils.getUUIDServicio().toString());
			url.append(";name=Jueguito;authorize=false");
			
			//esperar a los jugadores:
			menu.Mensaje("Jugador 1 (" + jugadores[getServerID()] + ") listo."); //yo mismo
			while (jugador_actual < num_conexiones && escuchando){
				not = (StreamConnectionNotifier)Connector.open(url.toString());
				menu.Mensaje("Esperando jugador " + (jugador_actual+2) + "/" + num_jugadores + "...");
				DataInputStream in = null;
				DataOutputStream out = null;
				try {
					conexiones[jugador_actual] = not.acceptAndOpen();
					StreamConnection conex = conexiones[jugador_actual];
					not.close(); //cierro el notificador pero no la conexión
					entradas[jugador_actual] = conex.openDataInputStream();
					salidas[jugador_actual] = conex.openDataOutputStream();
					in = entradas[jugador_actual];
					out = salidas[jugador_actual];
					sin = in.readUTF(); //el cliente debe mandar su nombre
					jugadores[jugador_actual] = sin.trim();
					
					menu.Mensaje("Jugador " + (jugador_actual+2) + " (" + jugadores[jugador_actual] + ") conectado.");
					
					//enviar ID y posición (y mapa):
					//cadena: IIIIXXXXYYYYTTTT...
					String xs = Utils.Rellenar(Integer.toString(naves[jugador_actual].X()), 4); 
					String ys = Utils.Rellenar(Integer.toString(naves[jugador_actual].Y()), 4);
					String data = "" + num_jugadores + jugador_actual + xs + ys;
					//mapa:
					data += Utils.Mapa();
					
					out.writeUTF(data);
					out.flush();
					
					jugador_actual++;
				} catch(IOException ex) {
					System.err.println("Servidor: Error esperando conexión: " + ex.getMessage());
					menu.Error("Ha ocurrido un error esperando las conexiones de los clientes.");
					return;
				}
			}
			if (escuchando){
				menu.Mensaje("Listo, todos conectados.");
				menu.ServidorListo();
				escuchando = false;
			}
		} catch(Exception ex) {
			System.err.println("Servidor: error iniciando Bluetooth: " + ex.getMessage());
			menu.Error("Error: No se puede hacer uso de BlueTooth.");
		}
	}
	
	
	public void IniciarComunicacion() {
		for (i=0; i<num_conexiones; ++i){
			try {
				salidas[i].writeUTF("start");
				salidas[i].flush();
			} catch (IOException ex) {
				System.err.println("Servidor: Error enviando mensaje: " + ex.getMessage());
				menu.Error("Ha ocurrido un error iniciando la partida.\nNo se puede continuar.");
				return;
			}
		}
		escuchando = true;
		new Thread(){
			public void run(){ escuchar(); }
		}.start();
	}

	
	protected void escuchar() {
		//recibe las posiciones de cada jugador
		while (true){
			minfr = miFR;
			for (i=0; i<num_conexiones; ++i){
				try {
					if (entradas[i].available() != 0){
						
						sin = entradas[i].readUTF();
						naves[i].setEscudo(Integer.parseInt(sin.substring(11, 15).trim()));
						naves[i].setX(Integer.parseInt(sin.substring(15, 19).trim()));
						naves[i].setY(Integer.parseInt(sin.substring(19, 23).trim()));
						naves[i].setDir(Integer.parseInt(sin.substring(23, 24)));
						
						//Asesino:
						//cuando me llega un IDAsesino, sumarle un punto a ese id
						//y guardar ese id para la nave i para difundirlo a todos.
						//Puede pasar que antes de difundirlo, me llegue el próximo mensaje
						//del cliente ya sin un IDAsesino entonces nunca lo difundí.
						//Para evitar esto, sólo actualizar el id del asesino de la nave i
						//si antes valía V (esto quiere decir que si había un IDAsesino ya
						//lo mandé porque en el enviar() los reseteo a V).
						
						sasesin = sin.substring(24, 25);
						
						//sumarle un punto al que mató:
						if (!sasesin.equals("V")){
							asesin = Integer.parseInt(sasesin);
							naves[asesin].sumarPunto();
						}
						
						//guardarlo si debo hacerlo:
						if (naves[i].Asesino().equals("V"))
							naves[i].setAsesino(sasesin);
						
						
						if (naves[i].E() == 0){
							//System.out.println("moneda nueva");
							Moneda coin = new Moneda(idmonedas++, naves[i].X(), naves[i].Y(), 'O');
							monedas.put(coin.IdOb(), coin);
							monedas_nuevas.addElement(coin);
						}
						
						else if (naves[i].E() == -1){
							if (naves[i].Vivo()){
								naves[i].setVivo(false);
								vivos--;
							}
						}
						
						tiros[i].setPotencia(Integer.parseInt(sin.substring(25, 28).trim()));
						txin = Integer.parseInt(sin.substring(28, 32).trim());
						tyin = Integer.parseInt(sin.substring(32, 36).trim());
						tiros[i].setX(txin);
						tiros[i].setY(tyin);
						if (txin == -1 && tyin == -1)
							tiros[i].setExiste(false);
						else
							tiros[i].setExiste(true);
						
						//monedas:
						int mid = Integer.parseInt(sin.substring(36, 40).trim());
						if (mid != -1) {
							m = (Moneda)monedas.get(new Integer(mid));
							monedas_eliminadas.addElement(m);
						}
						
						//framerate:
						FRleido = Integer.parseInt(sin.substring(40, 43).trim());
						if (FRleido == 0) FRleido = 1;
						
						//ir viendo el fr del más lento:
						if (FRleido < minfr) minfr = FRleido;

					}
				} catch (IOException ex) {
					//System.err.println("Servidor: Error leyendo datos.");
					//System.err.println("Cliente con ID " + i + " caído.");
				}
				catch (Exception ex) {
					menu.Error(Utils.MSG_ERROR_FATAL);
					return;
				}
			}
			
			//framerate
			if (minfr < 30) FRmodelo = minfr;
			else FRmodelo = 30;
			
			//enviar();
			
			
			try { Thread.sleep(10); }
			catch (InterruptedException ie) {
				System.err.println("Servidor: " + ie.getMessage());
			}
			
			
		}
	}
	
	protected int getServerID() {
		return (num_jugadores - 1); //ID del servidor
	}
	
	/////////////////////////////////////////////////////////////


}
