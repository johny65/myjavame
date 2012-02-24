//Clase BatallaMidlet:

/* Clase correspondiente al MIDlet.
 * Controla las pantallas que se muestran y el inicio del juego en sí.
 */

package pkgUI;

import javax.microedition.midlet.*;
import pkgJuego.Juego;
import pkgUniverso.Universo;
import pkgUtils.ImagenVault;
import pkgUtils.Utils;

import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.plaf.UIManager;
import com.sun.lwuit.util.Resources;


public class BatallaMidlet extends MIDlet implements ActionListener {

	private javax.microedition.lcdui.Display pantalla;
	private Display d;
	private Principal main;
	private Juego game;
	private MsgDialog msg;
	private boolean pausa;
	
	public BatallaMidlet() {
		pausa = false;
	}
	
	public void startApp() {
		
		if (!pausa){
			pantalla = javax.microedition.lcdui.Display.getDisplay(this);
			//pantalla.setCurrent(main);
			
			Display.init(this);
			d = Display.getInstance();
			d.addEdtErrorHandler(this);
			// Setting the application theme is discussed
			// later in the theme chapter and the resources chapter
			try {
				Resources r = Resources.open("/tema.res");
				UIManager.getInstance().setThemeProps(
				r.getTheme(r.getThemeResourceNames()[0]));
			} catch (java.io.IOException e) {}
			
			msg = new MsgDialog(this);
			main = new Principal(this);
			main.show();

		}
		else pausa = false;

	}

	public void destroyApp(boolean unconditional) {
		Terminar();
		notifyDestroyed();
	}

	protected void pauseApp() {
		//pauso el juego, el juego por abajo sigue funcionando,
		//si tengo suerte y nadie me mata mientras está pausado
		//voy a poder seguir jugando después
		pausa = true;
	}
	
	public void Terminar() {
		if (game != null){ //si había un juego creado, destruirlo
			game.Fin();
			game = null;
		}
	}
	
	//Vuelve a la pantalla principal:
	public void Volver() {
		Terminar();
		main.showBack();
	}

	//void Inicializar(Universo u):
	//
	//Empieza a cargar las cosas necesarias para el juego. Carga
	//las imágenes (a través de ImagenVault) y crea la instancia
	//del objeto Juego.
	//Esta función es llamada cuando todos los jugadores están conectados
	//y sólo resta esperar que el servidor empiece la partida (con "start").
	//
	public void Inicializar(Universo u) {
		ImagenVault.CargarImagenes(this);
		game = new Juego(this, u);
	}
	
	//void Jugar():
	//
	//Es la que inicia el juego. Una vez invocada tenemos en pantalla a las navecitas
	//para llevar a cabo la partida. Es llamada cuando está todo listo para empezar
	//el juego (jugadores conectados, cosas cargadas).
	//
	public void Jugar() {
		pantalla.setCurrent(game);
		game.IniciarJuego();
	}

	public void MostrarMensaje(String s, Form d, int op) {
		msg.Mensaje(s, d, op);
	}

	public void actionPerformed(ActionEvent a) {
		msg.Mensaje(Utils.MSG_ERROR_FATAL, null, 2);
	}

	public void Vibrar() {
		pantalla.vibrate(500);
	}
	
}
