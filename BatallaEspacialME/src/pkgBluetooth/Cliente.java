package pkgBluetooth;

import javax.bluetooth.*;
import java.io.*;
import java.util.*;

import javax.microedition.io.*;

import pkgJuego.Moneda;
import pkgJuego.Nave;
import pkgJuego.TiroPos;
import pkgUI.MenuCliente;
import pkgUtils.Utils;

public class Cliente implements DiscoveryListener {

	public static final UUID[] SERVICIOS = new UUID[]{ Utils.getUUIDServicio() };
	public static final int[] ATRIBUTOS = null;	//no necesitamos ningun atributo de servicio
	private	Vector busquedas;
	private DiscoveryAgent discoveryAgent;
	private StreamConnection connection;
	private DataInputStream in;
	private DataOutputStream out;
	private MenuCliente menu;
	private String nombre_disp, nombre_jug;
	protected int id = -1;
	protected int num_jugadores;
	protected int vivos;
	protected Nave naves[];
	protected TiroPos tiros[];
	protected String mapastring;
	protected int miFR; //framerate establecido mandado por el servidor para cumplir
	protected int framerate;
	protected String miasesino;
	private Thread hilo_escucha;
	protected String jugadores[];
	protected boolean nombres_recibidos = false;
	
	//---------------------------------------------------------------
	//variables locales que se estarían creando y destruyendo a cada rato:
	//---------------------------------------------------------------
	private String sin, sout; //cadenas recibida y enviada
	private int idin, xin, yin, din, ein, n, txin, tyin, tpin, asesin;
	
	
	//---------------------------------------------------------------
	//para el control de las monedas:
	//---------------------------------------------------------------
	protected Hashtable monedas;
	protected Vector monedas_nuevas;
	protected Vector monedas_eliminadas;
	protected int id_moneda_agarrada = -1;
	
	
	public Cliente(MenuCliente mc, String nombre) {
		menu = mc;
		framerate = 30;
		nombre_jug = nombre;
		busquedas = new Vector();
		LocalDevice localDevice = null;
		try {
			localDevice = LocalDevice.getLocalDevice();
			localDevice.setDiscoverable(DiscoveryAgent.GIAC);
			discoveryAgent = localDevice.getDiscoveryAgent();
		} catch(Exception ex) {
			System.err.println("Cliente: No se puede hacer uso de Bluetooth: " + ex.getMessage());
			menu.Error("Error: No se puede hacer uso de Bluetooth.");
		}
		
		monedas = new Hashtable();
		monedas_nuevas = new Vector();
		monedas_eliminadas = new Vector();
	}
	
	public void Destruir() {
		hilo_escucha.interrupt();
		//cerrar la conexión
		if (connection != null){
			try {
				in.close();
				out.close();
				connection.close();
			} catch (IOException ex) {
				System.err.println("Cliente: hubo un error cerrando las conexiones: " + ex.getMessage());
			}
		}
		menu = null;
	}
	
	public void IniciarBusqueda() {
		try {
			if (discoveryAgent != null)
				discoveryAgent.startInquiry(DiscoveryAgent.GIAC, this);
		} catch(BluetoothStateException ex) {
			System.err.println("Cliente: error iniciando búsqueda: " + ex.getMessage());
			menu.Mensaje("Error: No se pudo comenzar la búsqueda");
		}
	}
	
	public void CancelarBusqueda() {
		discoveryAgent.cancelInquiry(this);
		Enumeration en = busquedas.elements();
		Integer i;
		while(en.hasMoreElements()) {
			i = (Integer) en.nextElement();
			discoveryAgent.cancelServiceSearch(i.intValue());
		}
	}
	
	
	//métodos de la interfaz DiscoveryListener:
	
	public void deviceDiscovered(RemoteDevice remoteDevice,	DeviceClass deviceClass) {
		String address = remoteDevice.getBluetoothAddress();
		String friendlyName = null;
		try {
			friendlyName = remoteDevice.getFriendlyName(true);
		} catch(IOException ex) { }
		String device = null;
		if(friendlyName == null) {
			device = address;
		} else {
			device = friendlyName + " ("+address+")";
		}
		nombre_disp = device;
		//System.out.println("Dispositivo encontrado: " + device);
		try {
			int transId = discoveryAgent.searchServices(ATRIBUTOS, SERVICIOS, remoteDevice, this);
			//System.out.println("Comenzada busqueda de serivicios en: "+device+"; "+transId);
			busquedas.addElement(new Integer(transId));
		} catch(BluetoothStateException ex) {
			System.err.println("Cliente: No se pudo comenzar la búsqueda de servicios: " + ex.getMessage());
			menu.Mensaje("Error: No se pudo comenzar la búsqueda");
		}
	}
	
	public void inquiryCompleted(int discType) {
		switch(discType) {
			case DiscoveryListener.INQUIRY_COMPLETED:
				System.out.println("Búsqueda de dispositivos concluida con normalidad");
				break;
			case DiscoveryListener.INQUIRY_TERMINATED:
				System.out.println("Búsqueda de dispositivos cancelada");
				break;
			case DiscoveryListener.INQUIRY_ERROR:
				System.out.println("Búsqueda de dispositivos finalizada debido a un error");
				break;
		}
		menu.BusquedaTerminada(); //avisar que se terminó la búsqueda
	}
	
	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
		//System.out.println("Se encontró un servicio.");
		ServiceRecord service = servRecord[0];
		String url = service.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
		menu.AgregarServicio(nombre_disp, url);
	}
	
	public void Conectar(final String url) {
		hilo_escucha = new Thread(){
			public void run() { iniciar_conexion(url); }
		};
		hilo_escucha.start();
	}
	
	protected void iniciar_conexion(String url) {
		try {
			connection = (StreamConnection)Connector.open(url);
			in = connection.openDataInputStream();
			out = connection.openDataOutputStream();
			//enviar mi nombre:
			out.writeUTF(Utils.Rellenar(nombre_jug, 10));
			out.flush();
			//recibir mi id y posición (y mapa):
			sin = in.readUTF();
			num_jugadores = Integer.parseInt(sin.substring(0, 1));
			jugadores = new String[4];
			id = Integer.parseInt(sin.substring(1, 2));
			menu.Mensaje("Conectado. ID recibida: " + id);
			int x = Integer.parseInt(sin.substring(2, 6).trim());
			int y = Integer.parseInt(sin.substring(6, 10).trim());
			
			//mapa:
			mapastring = sin.substring(10, sin.length());
			
			//System.out.println("x: " + x + " y: " + y + " id: " + id);
			//naves[id] = new Nave(x, y, id, true);
			naves = new Nave[num_jugadores];
			tiros = new TiroPos[num_jugadores];
			for (int i=0; i<num_jugadores; ++i){
				naves[i] = new Nave(0, 0, i, 100, false);
				tiros[i] = new TiroPos(0, false);
			}
			naves[id].setX(x);
			naves[id].setY(y);
			
			vivos = num_jugadores;
			//naves[id].setVivo(true);
			//menu.Mensaje(s);
			//System.out.println(s);
			
			menu.ClienteListo();
			//empezar a recibir datos:
			escuchar();
		} catch(IOException ex) {
			System.err.println("Cliente: error al conectarse al servidor: " + ex.getMessage());
			menu.Error("Ha ocurrido un error mientras se conectaba al servidor.");
		}
	}
	
	protected void escuchar() {
		while (true){
			try {
				if (in.available() != 0){
					sin = in.readUTF();
					if (sin.equals("start"))
						menu.ClienteStart(); //empezar juego
					else {
						//recibir framerate
						framerate = Integer.parseInt(sin.substring(0, 3).trim());
						//recibir posiciones de todos
						n = 3; int nn = 0;
						while (nn < num_jugadores){
							idin = Integer.parseInt(sin.substring(n+0, n+1).trim());
							if (!nombres_recibidos)
								jugadores[idin] = sin.substring(n+1, n+11).trim();
							ein = Integer.parseInt(sin.substring(n+11, n+15).trim());
							xin = Integer.parseInt(sin.substring(n+15, n+19).trim());
							yin = Integer.parseInt(sin.substring(n+19, n+23).trim());
							din = Integer.parseInt(sin.substring(n+23, n+24).trim());
							
							naves[idin].setX(xin);
							naves[idin].setY(yin);
							naves[idin].setDir(din);
							naves[idin].setEscudo(ein);
							
							if (ein != -1)
								naves[idin].setVivo(true);
							else {
								if (naves[idin].Vivo()){
									naves[idin].setVivo(false);
									vivos--;
								}
							}
							
							tpin = Integer.parseInt(sin.substring(n+24, n+27).trim());
							txin = Integer.parseInt(sin.substring(n+27, n+31).trim());
							tyin = Integer.parseInt(sin.substring(n+31, n+35).trim());
							tiros[idin].setPotencia(tpin);
							tiros[idin].setX(txin);
							tiros[idin].setY(tyin);
							if (txin == -1 && tyin == -1)
								tiros[idin].setExiste(false);
							else
								tiros[idin].setExiste(true);
							
							naves[idin].setAsesino(sin.substring(n+35, n+36));
							
							//sumar punto al que mató:
							if (!naves[idin].Asesino().equals("V")){
								asesin = Integer.parseInt(naves[idin].Asesino());
								naves[asesin].sumarPunto();
								//System.out.println("un punto para " + asesin);
							}
							
							n += 36;
							nn++;
						}
						//hasta acá ya recibí todos los nombres
						nombres_recibidos = true;
	
						//recibir monedas:
						//guardo las recibidas porque tengo que ver si las que tengo no se borraron
						Hashtable monedas_recibidas = new Hashtable();
						while (n < sin.length()){
							int mx, my, mid, mv;
							mid = Integer.parseInt(sin.substring(n+0, n+4).trim());
							mx = Integer.parseInt(sin.substring(n+4, n+8).trim());
							my = Integer.parseInt(sin.substring(n+8, n+12).trim());
							mv = Integer.parseInt(sin.substring(n+12, n+14).trim());
							Moneda coin = new Moneda(mid, mx, my, (mv == 20) ? 'O' : 'P');
							monedas_recibidas.put(coin.IdOb(), coin);
							if (monedas.get(coin.IdOb()) == null){
								monedas.put(coin.IdOb(), coin);
								monedas_nuevas.addElement(coin);
							}
							n += 14;
						}
						
						//actualizar mi base de datos de monedas:
						Enumeration enum = monedas.elements();
						while (enum != null && enum.hasMoreElements()){
							Moneda m = (Moneda)enum.nextElement();
							if (monedas_recibidas.get(m.IdOb()) == null && !monedas_eliminadas.contains(m))
								monedas_eliminadas.addElement(m);
						}
						
					}
				}
				Thread.sleep(10);
			}
			catch (Exception ex) {
				System.err.println("Cliente: Error recibiendo datos: " + ex.getMessage());
				break;
			}
			
		}
	}
	
	
	//enviar mi posición
	protected void enviar() {
		try {
			sout = "" + id;
			sout += Utils.Rellenar(nombre_jug, 10);
			sout += Utils.Rellenar("" + naves[id].E(), 4); //escudo
			sout += Utils.Rellenar("" + naves[id].X(), 4);
			sout += Utils.Rellenar("" + naves[id].Y(), 4);
			sout += naves[id].Dir();
			sout += miasesino;
			sout += Utils.Rellenar("" + tiros[id].Potencia(), 3);
			sout += Utils.Rellenar("" + tiros[id].X(), 4);
			sout += Utils.Rellenar("" + tiros[id].Y(), 4);
			sout += Utils.Rellenar("" + id_moneda_agarrada, 4);
			sout += Utils.Rellenar("" + miFR, 3);
			out.writeUTF(sout);
			out.flush();
			
			//System.out.println(sout);
		} catch (IOException ex) {
			//no hay más servidor
			//System.err.println("El servidor no existe más.");
			menu.Error("El servidor se ha desconectado.\nImposible seguir jugando.");
		} catch (Exception ex) {
			//menu.Error(Utils.MSG_ERROR_FATAL);
		}
	}
	
	public void serviceSearchCompleted(int transID, int respCode) {
		//System.out.println("Busqueda de servicios "+transID+ " completada");
		switch(respCode) {
			case DiscoveryListener.SERVICE_SEARCH_COMPLETED:
				System.out.println("Búsqueda de servicios completada con normalidad");
				break;
			case DiscoveryListener.SERVICE_SEARCH_TERMINATED:
				System.out.println("Búsqueda de servicios cancelada");
				break;
			case DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
				System.out.println("Dispositivo no alcanzable");
				break;
			case DiscoveryListener.SERVICE_SEARCH_NO_RECORDS:
				System.out.println("No se encontraron registros de servicio");
				break;
			case DiscoveryListener.SERVICE_SEARCH_ERROR:
				System.out.println("Error en la búsqueda de servicios");
				break;
		}
	}

	
	////////////////////////////////////////////////////////////////////////



}
