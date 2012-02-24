//------------------------------------------------------------------------
//Diccionario RAE ME
//------------------------------------------------------------------------
//
//Aplicación Java ME que permite buscar una palabra en el Diccionario
//de la Real Academia Española usando una conexión de datos y obtener
//así su definición.
//Antes de mostrar los resultados, filtra el contenido devuelto por el
//servicio del buscador de la RAE a través de un convertidor de HTML a
//texto plano (de esta forma no hay que hacer un parseo de las etiquetas
//HTML y además se reduce la cantidad de datos a transmitir).
//
//------------------------------------------------------------------------
//Versión 1.0.0
//9 de enero de 2012
//Creado por Juan Bertinetti
//E-mail: juanbertinetti@gmail.com
//
//Este programa está liberado como Software Libre, se puede hacer con él
//lo que se quiera.
//
//Visitá "El Rincón de Johny": http://www.johny65corp.com.ar
//------------------------------------------------------------------------

package pkgMain;

import java.io.DataInputStream;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public class RaeMidlet extends MIDlet implements CommandListener, Runnable {

	
	//--------------------------------------------------------------------
	//estáticas:
	//--------------------------------------------------------------------

	//h2t: URL del convertidor de HTML a texto:
	private static String h2t = "http://cgi.w3.org/cgi-bin/html2txt?noinlinerefs=on&url=";	
	//rae: URL del diccionario:
	private static String rae = "http://buscon.rae.es/draeI/SrvltGUIBusUsual?TIPO_HTML=2&TIPO_BUS=3&LEMA=";
	//tamaño del bloque al recibir datos:
	private static int MTUsize = 512;
	
	
	//--------------------------------------------------------------------
	//objetos globales:
	//--------------------------------------------------------------------
	private Display pantalla;
	private Form main;
	private String url;
	private TextField tf;
	private Thread t;
	
	
	//--------------------------------------------------------------------
	//comandos:
	//--------------------------------------------------------------------
	private Command salir, buscar, otra;
	
	
	//--------------------------------------------------------------------
	//Constructor:
	//--------------------------------------------------------------------
	public RaeMidlet() {
		salir = new Command("Salir", Command.EXIT, 1);
		buscar = new Command("Buscar", Command.OK, 1);
		otra = new Command("Otra", Command.OK, 1);
		main = new Form("");
		tf = new TextField("Palabra a buscar:", "", 100, TextField.ANY);
	}

	
	//--------------------------------------------------------------------
	//Ciclo de vida de la aplicación:
	//--------------------------------------------------------------------	
	
	public void startApp() {
		InitForm();
		main.addCommand(salir);
		main.addCommand(buscar);
		main.setCommandListener(this);
		pantalla = Display.getDisplay(this);
		pantalla.setCurrent(main);
	}

	public void destroyApp(boolean unconditional) {
		notifyDestroyed();
	}

	protected void pauseApp() {}

	
	//--------------------------------------------------------------------
	//CommandAction:
	//--------------------------------------------------------------------
	public void commandAction(Command c, Displayable d) {
		if (c == salir)
			destroyApp(true);
		else if (c == buscar){
			main.removeCommand(buscar);
			main.deleteAll();
			main.append("Obteniendo datos...");
			url = rae + tf.getString();
			url = h2t + encodeURL(url);
			t = new Thread(this);
			t.start();
		}
		else if (c == otra)
			InitForm();
	}

	
	//--------------------------------------------------------------------
	//run() del thread:
	//--------------------------------------------------------------------
	public void run() {
		try {
			HttpConnection conexion = (HttpConnection)Connector.open(url);
			DataInputStream in = conexion.openDataInputStream();
			byte data[];
			String datos;
			int len = (int)conexion.getLength();
			if (len != -1){
				data = new byte[len];
				in.readFully(data);
				datos = new String(data, "UTF-8");
			}
			else {
				data = new byte[MTUsize];
				int leidos, index = 0;
				do {
					if (data.length < index + MTUsize) {
						byte newData[] = new byte[index + MTUsize];
						System.arraycopy(data, 0, newData, 0, data.length);
						data = newData;
					}
					leidos = in.read(data, index, MTUsize);
					index += leidos;
					if (leidos != MTUsize && leidos != -1)
						Thread.sleep(100); //espero un poco a que lleguen más datos
				} while (leidos != -1);
				datos = new String(data, "UTF-8");
				if (datos.length() > 10000)
					datos = datos.substring(0, 10000); //limito a 10000 caracteres
			}
			conexion.close();
			datos = datos.trim();
			datos = Limpiar(datos);
			main.deleteAll();
			main.append(datos);
			main.setTitle("Diccionario: " + tf.getString());
			main.addCommand(otra);
		} catch (IOException e) {
			main.append("Error al obtener datos, problema con la conexión.");
		} catch (SecurityException s) {
			main.append("Error al obtener datos, no se tiene permiso para acceder a la Red.");
		} catch (Exception ex) {
			ex.printStackTrace();
			destroyApp(true);
		}
	}
	
	
	
	//--------------------------------------------------------------------
	//Funciones auxiliares:
	//--------------------------------------------------------------------

	
	
	//--------------------------------------------------------------------
	//void InitForm():
	//--------------------------------------------------------------------
	//Limpia e inicializa el formulario para que quede en la pantalla principal.
	//
	private void InitForm() {
		main.deleteAll();
		main.setTitle("Diccionario");
		main.append("Ingresa una palabra para consultar su definición en el Diccionario de la Real Academia Española.");
		main.append(tf);
		tf.setString("");
		main.removeCommand(otra);
		main.addCommand(buscar);
	}
	
	
	//--------------------------------------------------------------------
	//String encodeURL(String URL):
	//--------------------------------------------------------------------
	//Codifica una URL (cambia los caracteres como '.' o '/' a sus representaciones
	//en hexadecimal.
	//
	private String encodeURL(String URL) {
		String encoded = "";
		for (int i=0; i<URL.length(); ++i){
			char c = URL.charAt(i);
			switch (c) {
				case ':':
					encoded += "%3A"; break;
				case '/':
					encoded += "%2F"; break;
				case ' ':
					encoded += "%20"; break;				
				case '?':
					encoded += "%3F"; break;
				case '=':
					encoded += "%3D"; break;
				case '&':
					encoded += "%26"; break;
				default:
					encoded += c;
			}
		}
		return encoded;
	}
	
	
	//--------------------------------------------------------------------
	//String Limpiar(String s):
	//--------------------------------------------------------------------
	//Hace una limpieza a la cadena s, es decir le quita líneas innecesarias
	//que quedan de la página web de la RAE.
	private String Limpiar(String s) {
		int pos = 0, ini = 0;
		String l;
		while (ini < s.length()){
			pos = s.indexOf("\n", ini);
			if (pos == -1)
				break;
			l = s.substring(ini, pos).trim();
			if (l.length() != 0 && (l.charAt(0) == '1' || l.charAt(0) == '(')){
				s = s.substring(ini);
				break;
			}
			ini = pos+1;
		}
		return s;
	}
	
} //fin del MIDlet