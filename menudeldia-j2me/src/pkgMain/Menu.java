package pkgMain;

import java.io.DataInputStream;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public class Menu extends MIDlet implements CommandListener, Runnable {

	private Display pantalla;
	private TextBox main;
	private Command salir, act;
	private String url = "http://menu-de-hoy.com.ar/j2me.php"; 
	
	public void startApp() {
		salir = new Command("Salir", Command.EXIT, 1);
		act = new Command("Actualizar", Command.OK, 1);
		main = new TextBox("Menú del día", "Presiona el botón Actualizar para obtener la información del día.", 500, TextField.ANY | TextField.UNEDITABLE);
		main.setTicker(new Ticker("Comedor Universitario - Universidad Nacional del Litoral"));
		main.addCommand(salir);
		main.addCommand(act);
		main.setCommandListener(this);
		pantalla = Display.getDisplay(this);
		pantalla.setCurrent(main);
	}

	public void destroyApp(boolean unconditional) {
		notifyDestroyed();
	}

	protected void pauseApp() {}

	public void commandAction(Command c, Displayable d) {
		if (c == salir)
			destroyApp(true);
		else if (c == act){
			main.setString("Obteniendo datos...");
			Thread t = new Thread(this);
			t.start();
		}
	}

	public void run() {
		try {
			HttpConnection conexion = (HttpConnection)Connector.open(url);
			DataInputStream in = conexion.openDataInputStream();
			byte data[];
			int len = (int)conexion.getLength();
			if (len != -1){
				data = new byte[len];
				in.readFully(data);
			}
			else {
				data = new byte[500];
				in.read(data, 0, 500);
			}
			String datos = new String(data, "ISO-8859-1");
			if (datos.indexOf("<!--") != -1)
				datos = datos.substring(0, datos.indexOf("<!--"));
			main.setString(datos);
			conexion.close();
		} catch (IOException e) {
			main.setString("Error al obtener datos, problema con la conexión.");
		} catch (SecurityException s) {
			main.setString("Error al obtener datos, no se tiene permiso para acceder a la Red.");
		}
	}

}
