//Clase Juego:

/*
 * Clase principal del juego heredera de GameCanvas,
 * donde se lleva a cabo todo.
 */

package pkgJuego;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;

import pkgUI.BatallaMidlet;
import pkgUI.MenuCompra;
import pkgUniverso.Universo;
import pkgUtils.ImagenVault;
import pkgUtils.Utils;


public class Juego extends GameCanvas implements Runnable {
	
	//---------------------------------------------------------------
	//--------------------------ATRIBUTOS:---------------------------
	//---------------------------------------------------------------
	
	//---------------------------------------------------------------
	//variables estáticas:
	//---------------------------------------------------------------
	private static int navesize = Utils.NAVE_SIZE, naverefpixel = navesize/2;
	private static int tirosize = Utils.TIRO_SIZE, tirorefpixel = tirosize/2;
	private static int mapasize = Utils.MAPA_SIZE_TILES, canttiles = mapasize * mapasize;
	
	
	//---------------------------------------------------------------
	//cosas importantes:
	//---------------------------------------------------------------
	private BatallaMidlet midlet;
	private Universo matrix; //el mundo es una Matrix
	private Nave pp[]; //para recibir las posiciones de las naves
	private TiroPos tt[]; //para recibir las posiciones de los tiros
	
	
	//---------------------------------------------------------------
	//informaciones:
	//---------------------------------------------------------------
	private int mDelay; //tiempo a dormirse el hilo
	private int jugadores; //cantidad de jugadores
	private int puntos_ant; //para saber cuándo actualizar mis puntos
	private int eegg = 0; //para activar el huevo de pascua
	
	
	//---------------------------------------------------------------
	//variables de estado:
	//---------------------------------------------------------------
	private boolean mov; //juego en movimiento o no
	private boolean radar; //radar activado o no
	private boolean vidamenos; //si perdí una vida
	private boolean ganador; //si yo fui el ganador
	private boolean ganador_establecido; //si ya se sabe quién fue el ganador
	private boolean info; //precios en pantalla o no
	private boolean mostrar_nombres; //nombres en el radar o no
	
	
	//---------------------------------------------------------------
	//para graficar:
	//---------------------------------------------------------------
	private Font fuente1, fuente2;
	private Graphics g, b;
	
	
	//---------------------------------------------------------------
	//sprites y otras cosas del juego:
	//---------------------------------------------------------------
	private Sprite naves[], tiros[], vision;
	private Sprite barritas[]; //barras de energía
	private Image barritas_img[]; //imágenes de las barras de energía para ir actualizando
	private Heroe yo; //el jugador local
	private Tiro genki; //su tiro
	private LayerManager lm;
	private TiledLayer mapa, fondo;
	private MenuCompra compras; //para realizar las compras
	
	
	//---------------------------------------------------------------
	//cadenas de información:
	//(se actualizan sólo cuando hay un cambio)
	//---------------------------------------------------------------
	private static String precio_info = "Mejoras:";
	private String precio_vel, precio_escudo, precio_pot, precio_vision;
	private String str_plata, str_puntos, str_escudo;
	private String str_ganador;
	
	
	//---------------------------------------------------------------
	//para las monedas:
	//---------------------------------------------------------------
	private Hashtable monedas;
	private Vector mnuevas, mmuertas;
	private Moneda m;
	private Enumeration enum; 
	

	//---------------------------------------------------------------
	//para calcular el framerate:
	//---------------------------------------------------------------
	private long ms1, ms2; //para medir el tiempo
	private int miFR; //mi framerate
	private int fr; //el framerate que recibo del servidor
	
	
	//---------------------------------------------------------------
	//otras variables locales que se estarían creando y destruyendo a cada rato:
	//---------------------------------------------------------------
	private int ks, ga; //para el teclado
	private int i, j; //para los for
	private int xradar, yradar; //para mostrar las posiciones de las naves en el radar
	private int aux, v2, v3; //cálculos auxiliares
	

	
	//---------------------------------------------------------------
	//----------------------------MÉTODOS:---------------------------
	//---------------------------------------------------------------
	
	
	//---------------------------------------------------------------
	//Constructor:
	//---------------------------------------------------------------
	//
	//El constructor recibe un Universo inicializado previamente, que puede
	//ser un servidor o un cliente, pero acá se abstrae de eso.
	//
	public Juego(BatallaMidlet bm, Universo u) {
		super(false);
		
		//valores iniciales:
		mDelay = 30;
		miFR = 30; fr = 30;
		
		//inicializar todo:
		midlet = bm;
		matrix = u;
		radar = false;
		ganador = false; ganador_establecido = false;
		info = true;
		mostrar_nombres = true;
		jugadores = matrix.getCantidadJugadores();
		lm = new LayerManager();
		fuente1 = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_LARGE);
		fuente2 = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
		
		//tiros:
		tiros = new Sprite[jugadores]; //máximo 1 tiro por jugador
		for (i=0; i<tiros.length; ++i){
			tiros[i] = new Sprite(ImagenVault.getGenkidama(), tirosize, tirosize);
			tiros[i].defineReferencePixel(tirorefpixel, tirorefpixel);
			tiros[i].setVisible(false);
			lm.append(tiros[i]);
		}
		
		//naves:
		naves = new Sprite[jugadores];
		for (i=0; i<naves.length; ++i){
			naves[i] = new Sprite(ImagenVault.getImgNave(i), navesize, navesize);
			naves[i].defineReferencePixel(naverefpixel, naverefpixel);
			naves[i].setVisible(false);
			lm.append(naves[i]);
		}
		
		//mapa:
		fondo = new TiledLayer(mapasize, mapasize, ImagenVault.getYuyo(), Utils.TILE_SIZE, Utils.TILE_SIZE);
		mapa = new TiledLayer(mapasize, mapasize, ImagenVault.getTileset(), Utils.TILE_SIZE, Utils.TILE_SIZE);
		int tiles[] = matrix.getMapa();
		for (i=0; i<canttiles; ++i){
			int c = i % mapasize;
			int f = (i-c) / mapasize;
			fondo.setCell(c, f, 1);
			mapa.setCell(c, f, tiles[i]);
		}
		tiles = null;
		lm.append(mapa);
		lm.append(fondo);
		
		//jugador local:
		genki = new Tiro(tiros[matrix.getMiID()]);
		yo = new Heroe(naves[matrix.getMiID()], mapa, genki);
		yo.setVisible(true);
		
		//cosas que dependen de yo:
		compras = new MenuCompra(yo, this, midlet);
		str_puntos = "Puntos: " + yo.getPuntos();
		puntos_ant = yo.getPuntos(); //inicialmente 0
		
		//barras de energía:
		barritas = new Sprite[jugadores];
		barritas_img = new Image[jugadores];
		for (i=0; i<jugadores; ++i){
			barritas_img[i] = Image.createImage(30, 3);
			barritas[i] = new Sprite(barritas_img[i]);
			barritas[i].setVisible(true);
			lm.insert(barritas[i], 0);
		}

		//campo de visión:
		vision = yo.InicializarCampoVision(getWidth(), getHeight());
		lm.insert(vision, 0);
		
	}
	
	
	//---------------------------------------------------------------
	//void IniciarJuego():
	//---------------------------------------------------------------
	//
	//Arranca el juego.
	//
	public void IniciarJuego() {
		mov = true;
		Thread t = new Thread(this);
		t.start();
	}
	

	//---------------------------------------------------------------
	//void Fin():
	//---------------------------------------------------------------
	//
	//Cierra todas las conexiones y termina el juego.
	//
	public void Fin() {
		mov = false;
		matrix.Destruir();
	}
	
	
	//---------------------------------------------------------------
	//void run():
	//---------------------------------------------------------------
	//
	//Primera función en llamarse al iniciarse el juego. Es la función del
	//hilo. Se encarga de llevar el GameLoop.
	//Es quien controla el framerate.
	//
	public void run() {
		
		//iniciar matrix:
		pp = matrix.getPosiciones();
		yo.setPosInicial(pp[matrix.getMiID()].X(), pp[matrix.getMiID()].Y());
		tt = matrix.getTiros();
		monedas = matrix.getMonedas();
		mnuevas = matrix.getMonedasNuevas();
		mmuertas = matrix.getMonedasEliminadas();
		
		//game loop:
		g = getGraphics();
		while (mov) {
			
			//dibujar todo y ver cuánto demoro para calcular mi framerate:
			ms1 = System.currentTimeMillis();
			GameLoop();
			flushGraphics();
			ms2 = System.currentTimeMillis() - ms1;
			miFR = (int)(1000/ms2);
			//------------------------------------------------------------
			

			//avisar mis nuevos datos:------------------------------------
			matrix.setMiTiro(genki.getX(), genki.getY(), genki.getPotencia());
			if (vidamenos){
				//acá simplemente perdí una vida entonces tengo que mandar los datos para que todos sepan
				matrix.setMisDatos(yo.getX(), yo.getY(), yo.getDireccion(), 0, miFR);
				//ahora sí volver a mi posición inicial
				yo.IrPosInicial();
			}
			else if (!yo.SigoVivo()) //si perdí mandar siempre E = -1
				matrix.setMisDatos(yo.getX(), yo.getY(), yo.getDireccion(), -1, miFR);
			else //todo normal
				matrix.setMisDatos(yo.getX(), yo.getY(), yo.getDireccion(), yo.getEscudo(), miFR);
			//------------------------------------------------------------
			
			
			//ver el tema del framerate:----------------------------------
			fr = matrix.getFrameRate();
			//dormirme lo necesario para cumplir el framerate que me manda el servidor:
			//(siempre como mínimo ante cualquier problema me duermo 10 ms)
			try {
				mDelay = (int)1000/fr;
				if (mDelay > ms2)
					mDelay -= ms2;
				else
					mDelay = 10;
			} catch (Exception ex) {
				mDelay = 10;
			}
			
			try { Thread.sleep(mDelay); }
			catch (InterruptedException ie) {
				System.err.println(ie.getMessage());
			}
			//------------------------------------------------------------
			
			
			//long ms3 = System.currentTimeMillis() - ms1;
			//System.out.println("FR deseado: " + fr + " FR real: " + (int)(1000/ms3));
		}
	}
	
	
	//---------------------------------------------------------------
	//void GameLoop():
	//---------------------------------------------------------------
	//
	//Función principal donde se realiza todo el dibujado y el control del
	//juego (movimiento, colisiones, actualización de posiciones, etc.).
	//
	private void GameLoop() {

		//limpiar pantalla--------------------------
		//g.setColor(0x45C21E);
		g.setColor(0);
		g.fillRect(0, 0, getWidth(), getHeight());
		//------------------------------------------
		
		
		//moverme:--------------------------------------------------------
		ks = getKeyStates();
		//diagonales:
		if ((ks & UP_PRESSED) != 0 && (ks & LEFT_PRESSED) != 0)
			yo.MoverNO();
		else if ((ks & DOWN_PRESSED) != 0 && (ks & LEFT_PRESSED) != 0)
			yo.MoverSO();
		else if ((ks & DOWN_PRESSED) != 0 && (ks & RIGHT_PRESSED) != 0)
			yo.MoverSE();
		else if ((ks & UP_PRESSED) != 0 && (ks & RIGHT_PRESSED) != 0)
			yo.MoverNE();
		//rectas:
		else if ((ks & LEFT_PRESSED) != 0)
			yo.MoverO();
		else if ((ks & RIGHT_PRESSED) != 0)
			yo.MoverE();
		else if ((ks & UP_PRESSED) != 0)
			yo.MoverN();
		else if ((ks & DOWN_PRESSED) != 0)
			yo.MoverS();
		
		if ((ks & FIRE_PRESSED) != 0){
			//si puedo disparar, disparar
			if (!genki.Existe() && yo.SigoVivo())
				yo.TirarTiro();
		}
		//----------------------------------------------------------------
		
		
		//mover mi tiro:--------------------------------------------------
		if (genki.Existe())
			genki.Mover(yo.getX(), yo.getY(), yo.getVision());
		//----------------------------------------------------------------
		
		
		//para el huevo de pascua:----------------------------------------
		if ((ks & GameCanvas.GAME_D_PRESSED) != 0){
			eegg++;
			if (eegg == 65){
				yo.HuevoDePascua();
				midlet.Vibrar();
			}
		} else eegg = 0;
		//----------------------------------------------------------------

		
		//monedas:--------------------------------------------------------
		try {
		for (i=0; i<mmuertas.size(); ++i){ //quito las agarradas
			m = (Moneda)mmuertas.elementAt(i);			
			lm.remove((Moneda)monedas.get(m.IdOb()));
			monedas.remove(m.IdOb());
		}
		mmuertas.removeAllElements();
		
		for (i=0; i<mnuevas.size(); ++i){ //agrego las nuevas
			m = (Moneda)mnuevas.elementAt(i);			
			lm.insert((Moneda)monedas.get(m.IdOb()), 1);
		}
		mnuevas.removeAllElements();
		
		//agarré una moneda?
		matrix.setMonedaAgarrada(-1);
		enum = monedas.elements();
		while (enum != null && enum.hasMoreElements()){
			m = (Moneda)enum.nextElement();
			if (yo.SigoVivo() && yo.ChocaCon(m)){
				yo.SumarPlata(m.Valor());
				str_plata = "Redios: $" + yo.getPlata();
				matrix.setMonedaAgarrada(m.Id());
				((Moneda)monedas.get(m.IdOb())).setVisible(false);
				break; //ya agarré una, cortar
			}
			m.nextFrame();
		}
		} catch (Exception ex) { //es peligrosa la parte de las monedas
			System.out.println(ex.getMessage()); 
		}
		//----------------------------------------------------------------
		
		
		//actualizar mis puntos:------------------------------------------
		yo.setPuntos(pp[matrix.getMiID()].Puntos());
		if (yo.getPuntos() != puntos_ant){
			puntos_ant = yo.getPuntos();
			str_puntos = "Puntos: " + yo.getPuntos();
		}
		//----------------------------------------------------------------
		
		
		//dibujar enemigos:-----------------------------------------------
		for (i=0; i<pp.length; ++i){
			if (i != matrix.getMiID()){ //no mirar mi posición
				naves[i].setRefPixelPosition(pp[i].X(), pp[i].Y());
				naves[i].setFrame(pp[i].Dir());
				
				if (pp[i].Vivo())
					naves[i].setVisible(true);
				else //uno menos
					naves[i].setVisible(false);
				
				//si me choqué una nave rebotar:
				if (yo.ChocaCon(naves[i]))
					yo.Rebotar(naves[i]);
			}
		}
		//----------------------------------------------------------------

		
		//tiros de los otros:---------------------------------------------
		matrix.setMiAsesino(-1); //por ahora nadie me mató
		vidamenos = false;
		//dibujar tiros:
		for (i=0; i<tt.length; ++i){
			if (i != matrix.getMiID()){ //no miro mi tiro
				if (tt[i].Existe()){
					tiros[i].setRefPixelPosition(tt[i].X(), tt[i].Y());
					tiros[i].setVisible(true);
					//me dieron? (sólo si estoy vivo)
					if (yo.SigoVivo() && yo.ChocaCon(tiros[i])){
						if (yo.MeDieron(tt[i].Potencia())){ //true si perdí una vida
							matrix.setMiAsesino(i);
							vidamenos = true;
						}
						if (!yo.SigoVivo()){ //a ver si me mataron acá
							//System.out.println("Perdí!!!!");
							midlet.Vibrar();
							//fuera del juego, quedar en modo espectador
							yo.setVisible(false);
							lm.remove(vision);
							compras.DesactivarCompra();
						}
						//me bajaron, actualizar info del escudo
						str_escudo = "" + yo.getEscudo();
					}
				}
				else
					tiros[i].setVisible(false);
			}
		}
		//----------------------------------------------------------------
		
		
//		//enemigos y tiros:-----------------------------------------------
//		matrix.setMiAsesino(-1); //por ahora nadie me mató
//		vidamenos = false;
//		
//		for (i=0; i<jugadores; ++i){
//			if (i != matrix.getMiID()){ //no mirar mis cosas
//
//				//dibujar enemigos:--------------------------------
//				naves[i].setRefPixelPosition(pp[i].X(), pp[i].Y());
//				naves[i].setFrame(pp[i].Dir());
//				
//				if (pp[i].Vivo())
//					naves[i].setVisible(true);
//				else //uno menos
//					naves[i].setVisible(false);
//				
//				//si me choqué una nave rebotar:
//				if (yo.ChocaCon(naves[i]))
//					yo.Rebotar(naves[i]);
//				//-------------------------------------------------
//
//				
//				//tiros de los otros:------------------------------
//				if (tt[i].Existe()){
//					tiros[i].setRefPixelPosition(tt[i].X(), tt[i].Y());
//					tiros[i].setVisible(true);
//					//me dieron? (sólo si estoy vivo)
//					if (yo.SigoVivo() && yo.ChocaCon(tiros[i])){
//						if (yo.MeDieron(tt[i].Potencia())){ //true si perdí una vida
//							matrix.setMiAsesino(i);
//							vidamenos = true;
//						}
//						if (!yo.SigoVivo()){ //a ver si me mataron acá
//							//System.out.println("Perdí!!!!");
//							//fuera del juego, quedar en modo espectador
//							yo.setVisible(false);
//							lm.remove(vision);
//							compras.DesactivarCompra();
//						}
//						//me bajaron, actualizar info del escudo
//						str_escudo = "" + yo.getEscudo();
//					}
//				}
//				else
//					tiros[i].setVisible(false);
//				//-------------------------------------------------
//
//			}
//		}
//		//----------------------------------------------------------------	
		
		
		//ver ganador:----------------------------------------------------
		if (matrix.getCantVivos() == 1 && !ganador_establecido){
			for (i=0; i<jugadores; ++i){
				if (naves[i].isVisible()){
					//i es el ganador
					ganador_establecido = true;
					if (i != matrix.getMiID())
						str_ganador = "" + matrix.getNombresJugadores()[i] + " ha ganado.";
					else
						ganador = true; //yo gané
					break;
				}
			}
		}
		//----------------------------------------------------------------

		
		//dibujar cosas nuevas:-------------------------------------------
		actualizar_barritas();
		//mover campo de visión:
		vision.setRefPixelPosition(yo.getX(), yo.getY());
		if (radar)
			dibujar_radar();
		else {
			lm.paint(g, getWidth()/2-yo.getX(), getHeight()/2-yo.getY());
			dibujar_informacion();
		}
		//----------------------------------------------------------------
		
	}
	

	//---------------------------------------------------------------
	//void dibujar_informacion():
	//---------------------------------------------------------------
	//
	//Dibuja las informaciones en pantalla (escudo, vidas, puntos, etc.).
	//
	private void dibujar_informacion() {
		try {
		
		//carteles de perdí/gané:-----------------------------------------
		if (!yo.SigoVivo()){ //si perdí mostrar el GAME OVER
			g.drawImage(ImagenVault.getGameOver(), getWidth()/2, 0, Graphics.TOP | Graphics.HCENTER);
			if (ganador_establecido){
				g.setColor(0xFF0000);
				g.setFont(fuente1);
				g.drawString(str_ganador, getWidth()/2, getHeight()/2, Graphics.BOTTOM | Graphics.HCENTER);
			}
			return;
		}
		else if (ganador) //si yo gané mostrar que gané
			g.drawImage(ImagenVault.getWinner(), getWidth()/2, getHeight()/2, Graphics.VCENTER | Graphics.HCENTER);
		//----------------------------------------------------------------
		
		
		//información que está siempre:-----------------------------------
		g.setColor(0xFF0000);
		g.setFont(fuente1);
		g.drawImage(ImagenVault.getShield(), 0, getHeight(), Graphics.BOTTOM | Graphics.LEFT);
		g.drawString(str_escudo, 17, getHeight(), Graphics.BOTTOM | Graphics.LEFT);
		g.drawString(str_puntos, 0, 0, Graphics.TOP | Graphics.LEFT);
		g.drawString(str_plata, getWidth(), 0, Graphics.TOP | Graphics.RIGHT);
		
		for (j=0; j<yo.getVidas(); ++j)
			g.drawImage(ImagenVault.getVida(), getWidth()-15*j, getHeight(), Graphics.BOTTOM | Graphics.RIGHT);
		//----------------------------------------------------------------
		
		
		//información sobre precios:--------------------------------------
		if (info){
			g.setColor(0xFFFFFF);
			g.setFont(fuente2);
			g.drawString(precio_info, getWidth(), fuente1.getHeight(), Graphics.TOP | Graphics.RIGHT);
			g.drawString(precio_vel, getWidth(), fuente1.getHeight()*2, Graphics.TOP | Graphics.RIGHT);
			g.drawString(precio_escudo, getWidth(), fuente1.getHeight()*3, Graphics.TOP | Graphics.RIGHT);
			g.drawString(precio_pot, getWidth(), fuente1.getHeight()*4, Graphics.TOP | Graphics.RIGHT);
			g.drawString(precio_vision, getWidth(), fuente1.getHeight()*5, Graphics.TOP | Graphics.RIGHT);
		}
		//----------------------------------------------------------------
		
		} catch (Exception ex) {
			//cualquier error acá cortar todo
			mov = false;
			matrix.Destruir();
			midlet.MostrarMensaje(Utils.MSG_ERROR_FATAL, null, 2);
		}
		
	}
	

	//---------------------------------------------------------------
	//void dibujar_radar():
	//---------------------------------------------------------------
	//
	//Dibuja el radar de naves.
	//
	private void dibujar_radar() {
		g.setColor(0x45C21E);
		g.fillRect(0, 0, getWidth(), getHeight());
		//mapeo de posiciones:
		for (j=0; j<jugadores; ++j){
			if (naves[j].isVisible()){ //sólo a los que siguen vivos
				xradar = (int) (naves[j].getRefPixelX()/Utils.MAPA_SIZE_F * getWidth());
				yradar = (int) (naves[j].getRefPixelY()/Utils.MAPA_SIZE_F * getHeight());
				if (j != matrix.getMiID())
					g.setColor(0xFF0000); //los otros son un punto rojo
				else {
					//a mí dibujarme mi radio de visión:
					v2 = (int)(yo.getVision()/Utils.MAPA_SIZE_F * getWidth());
					v3 = (int)(yo.getVision()/Utils.MAPA_SIZE_F * getHeight());
					g.setColor(0x77ae42);
					g.drawArc(xradar-v2, yradar-v3, 2*v2, 2*v3, 0, 360);
					g.setColor(0x0000FF); //yo soy un punto azul
				}
				
				//dibujar puntito:
				g.fillArc(xradar-2, yradar-2, 5, 5, 0, 360);
				
				//dibujar nombres:
				if (mostrar_nombres){
					g.setColor(0xFFFFFF);
					g.setFont(fuente2);
					g.drawString(matrix.getNombresJugadores()[j], xradar, yradar+5, Graphics.TOP | Graphics.HCENTER);
				}	
				
			}
		}
	}
	
	
	//---------------------------------------------------------------
	//void actualizar_barritas():
	//---------------------------------------------------------------
	//
	//Actualiza y dibuja las barras de energía de todas las naves.
	//
	private void actualizar_barritas() {
		for (j=0; j<jugadores; ++j){
			if (pp[j].Vivo()){ //sólo a los vivos
				
				if (pp[j].E() > pp[j].MaxE()) //si veo que tiene más que el máximo, actualizo su máximo
					pp[j].setMaxEscudo(pp[j].E());
				
				b = barritas_img[j].getGraphics();
				aux = (int)(pp[j].E() * barritas_img[j].getWidth() / (float)pp[j].MaxE());
				b.setColor(0xFF0000);
				b.fillRect(0, 0, barritas_img[j].getWidth(), barritas_img[j].getHeight());
				b.setColor(0x00FF00);
				b.fillRect(0, 0, aux, barritas_img[j].getHeight());
				
				barritas[j].setPosition(naves[j].getRefPixelX() - 15, naves[j].getRefPixelY() - 30);
				barritas[j].setVisible(true);
				
			}
			else barritas[j].setVisible(false);
		}
	}
	
	
	//---------------------------------------------------------------
	//void ActualizarInfoPrecios(String v,
	//							String pe,
	//							int e,
	//							String p,
	//							String vi,
	//							int plata):
	//---------------------------------------------------------------
	//
	//Actualiza la información sobre los precios de las mejoras. Es llamada
	//sólo cuando hubo un cambio de precio.
	//Actualiza el parámetro que no sea null.
	//
	public void ActualizarInfoPrecios(String v, String pe, int e, String p, String vi, int plata) {
		str_plata = "Redios: $" + plata;
		if (v != null)
			precio_vel = "V: $" + v;
		if (pe != null){
			precio_escudo = "E: $" + pe;
			str_escudo = "" + e;
		}
		if (p != null)
			precio_pot = "P: $" + p;
		if (vi != null)
			precio_vision = "C: $" + vi;
	}
	
	
	//---------------------------------------------------------------
	//void keyPressed(int key):
	//---------------------------------------------------------------
	//
	//Para detectar las pulsaciones de teclas que tienen que ser detectadas
	//como una simple pulsación y no continuamente.
	//Activa/desactiva las opciones de cosas que se muestran por pantalla.
	//
	protected void keyPressed(int key) {
		ga = getGameAction(key);
		if (ga == GAME_A) //A: activa/desactiva el radar
			radar = !radar;
		else if (ga == GAME_B) //B: tecla rápida para comprar escudo
			compras.ComprarEscudo();
		else if (ga == GAME_C) //C: muestra/oculta los precios
			info = !info;
		else if (ga == GAME_D) //D: muesra/oculta los nombres en el radar
			mostrar_nombres = !mostrar_nombres;
	}
	
	
}

