//Clase ImagenVault:

/* 
 * Esta clase se encarga de cargar una vez todas las imágenes
 * usadas en el juego, y de pasarlas a quien las necesite. De esta
 * forma todas las imágenes se cargan una única vez desde el mismo
 * lugar.
 * Todas las funciones son estáticas por lo que pueden ser llamadas
 * sin instanciar un objeto. Antes de pedir cualquier imagen se debe
 * llamar a CargarImagenes().
 */

package pkgUtils;

import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import pkgUI.BatallaMidlet;

public class ImagenVault {

	//---------------------------------------------------------------
	//Mensaje de error:
	//---------------------------------------------------------------	
	private static final String msg_error = "Error cargando imágenes.";
	
	
	//---------------------------------------------------------------
	//objetos Image:
	//---------------------------------------------------------------
	private static Image tileset, nave1, nave2, nave3, nave4, genkidama,
							explosion, yuyo, gameover, oro, plata, mega,
							winner, vida, escudo;
	
	
	//---------------------------------------------------------------
	//MIDlet: para mostrar un mensaje de error
	//---------------------------------------------------------------	
	private static BatallaMidlet midlet;
	
	
	//---------------------------------------------------------------
	//void CargarImagenes(BatallaMidlet m):
	//---------------------------------------------------------------
	//
	//Esta función debe ser la primera en llamarse. Se encarga de
	//cargar todas las imágenes en memoria y dejarlas disponibles
	//para cualquier uso. Sólo carga las imágenes que se necesitan
	//sí o sí en el juego, las que pueden no necesitarse se cargan
	//en el momento de su uso.
	//Recibe la referencia del MIDlet y la guarda.
	//
	public static void CargarImagenes(BatallaMidlet m) {
		midlet = m;
		try {
			tileset = Image.createImage("/tileset.png");
			yuyo = Image.createImage("/yuyo.png");
			genkidama = Image.createImage("/genkidama.png");
			oro = Image.createImage("/oro.png");
			plata = Image.createImage("/plata.png");
			vida = Image.createImage("/vida.png");
			escudo = Image.createImage("/escudo.png");
		}
		catch (IOException e) {
			System.err.println(msg_error);
		}
	}
	
	
	//---------------------------------------------------------------
	//Image getTileset():
	//---------------------------------------------------------------
	//
	//Devuelve la imagen correspondiente al tileset para armar el TiledLayer.
	//
	public static Image getTileset() { return tileset; }
	
	
	//---------------------------------------------------------------
	//Image getYuyo():
	//---------------------------------------------------------------
	//
	//Devuelve la imagen correspondiente al pasto para armar el fondo del
	//TiledLayer.
	//
	public static Image getYuyo() { return yuyo; }
	
	
	//---------------------------------------------------------------
	//Image getImgNave(int i):
	//---------------------------------------------------------------
	//
	//Devuelve la imagen de una nave. Según el entero i pasado devuelve una imagen
	//correspondiente. i va desde 0 a 3 y se corresponde con el ID de un jugador
	//(es decir al jugador de ID 0 le toca la nave 1, etc.)
	//Carga en el momento la imagen necesitada.
	//
	public static Image getImgNave(int i) {
		try {
			switch (i){
				case 0: {
					nave1 = Image.createImage("/nave1.png");
					return nave1;
				}
				case 1: {
					nave2 = Image.createImage("/nave2.png");
					return nave2;
				}
				case 2: {
					nave3 = Image.createImage("/nave3.png");
					return nave3;
				}
				case 3: {
					nave4 = Image.createImage("/nave4.png");
					return nave4;
				}
			}
		} catch (IOException e) {
			System.err.println(msg_error);
			midlet.MostrarMensaje(Utils.MSG_ERROR_FATAL, null, 2);
			return Image.createImage(48, 48);
		}
		return null; //nunca llega acá pero Java molesta
	}
	
	
	//---------------------------------------------------------------
	//Image getGenkidama():
	//---------------------------------------------------------------
	//
	//Devuelve la imagen de los tiros (una genkidama de Gokú).
	//
	public static Image getGenkidama() { return genkidama; }
	
	
	//---------------------------------------------------------------
	//Image getExplosion():
	//---------------------------------------------------------------
	//
	//Devuelve la imagen de la explosión.
	//
	public static Image getExplosion() { return explosion; }
	
	
	//---------------------------------------------------------------
	//Image getGameOver():
	//---------------------------------------------------------------
	//
	//Carga y devuelve la imagen con el texto de Game Over (sólo para
	//el que perdió).
	//
	public static Image getGameOver() {
		if (gameover == null){
			try {
				gameover = Image.createImage("/gameover.png");
			} catch (IOException e) {
				System.err.println(msg_error);
			}
		}
		return gameover;
	}
	
	
	//---------------------------------------------------------------
	//Image getWinner():
	//---------------------------------------------------------------
	//
	//Carga y devuelve la imagen con el texto de ganador (sólo para
	//el que ganó).
	//
	public static Image getWinner() {
		if (winner == null){
			try {
				winner = Image.createImage("/win.png");
			} catch (IOException e) {
				System.err.println(msg_error);
			}
		}
		return winner;
	}
	
	
	//---------------------------------------------------------------
	//Image getMonedaOro():
	//---------------------------------------------------------------
	//
	//Devuelve la imagen de la moneda de más valor (de oro).
	//
	public static Image getMonedaOro() { return oro; }
	
	
	//---------------------------------------------------------------
	//Image getMonedaPlata():
	//---------------------------------------------------------------
	//
	//Devuelve la imagen de la moneda de menor valor (de plata).
	//
	public static Image getMonedaPlata() { return plata; }
	
	
	//---------------------------------------------------------------
	//Image getVida():
	//---------------------------------------------------------------
	//
	//Devuelve la imagen de las vidas (un corazón).
	//
	public static Image getVida() { return vida; }
	
	
	//---------------------------------------------------------------
	//Image getShield():
	//---------------------------------------------------------------
	//
	//Devuelve la imagen del escudo.
	//
	public static Image getShield() { return escudo; }
	
	
	//---------------------------------------------------------------
	//Image getCampoVision(int radio, int w, int h):
	//---------------------------------------------------------------
	//
	//Genera y devuelve la imagen que corresponde al campo de visión,
	//o sea toda una pantalla negra con el radio de visión transparente.
	//Esta función se llama cada vez que se compra una mejora para 
	//el campo de visión.
	//Se le pasa el radio de visión, el ancho y alto de la pantalla.
	//
	public static Image getCampoVision(int radio, int w, int h) {
		
		Image img = Image.createImage(w, h);
		Graphics g = img.getGraphics();
		
		//pantalla toda negra:
		g.setColor(0);
		g.fillRect(0, 0, w, h);
		
		//círculo de visión:
		g.setColor(0xFFFFFF);
		g.fillArc(w/2 - radio, h/2 - radio, 2*radio, 2*radio, 0, 360);
		
		//crear máscara:
		int data[] = new int[w*h];
		img.getRGB(data, 0, w, 0, 0, w, h);
		for (int i=0; i<data.length; ++i){
			if (data[i] == -1)
				data[i] = 0;
		}
		
		//nueva imagen:
		return Image.createRGBImage(data, w, h, true);
		
	}
	
	
	//---------------------------------------------------------------
	//Image getMegaman():
	//---------------------------------------------------------------
	//
	//Devuelve la imagen de Megaman...
	//
	public static Image getMegaman() {
		try {
			mega = Image.createImage("/megaman.png");
		} catch (IOException e) {
			System.err.println(msg_error);
			mega = Image.createImage(48, 48);
			midlet.MostrarMensaje(Utils.MSG_ERROR_FATAL, null, 2);
		}
		return mega;
	}
}
