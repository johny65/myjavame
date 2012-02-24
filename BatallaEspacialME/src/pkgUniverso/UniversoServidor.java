package pkgUniverso;

import java.util.Hashtable;
import java.util.Vector;

import pkgBluetooth.Servidor;
import pkgJuego.Cronometro;
import pkgJuego.Moneda;
import pkgJuego.Nave;
import pkgJuego.TiroPos;
import pkgUI.MenuServidor;
import pkgUtils.Utils;

public class UniversoServidor extends Servidor implements Universo {

	private Cronometro timer;
	
	public UniversoServidor(int cantidadJugadores, String nombre, MenuServidor m) {
		super(cantidadJugadores, nombre, m);
		timer = new Cronometro(this);
	}


	public int getMiID() {
		return getServerID(); //ID del servidor
	}

	public int getCantidadJugadores() {
		return num_jugadores;
	}

	public Nave[] getPosiciones() {
		return naves;
	}

	public void setMisDatos(int x, int y, int dir, int E, int FR) {
		naves[getMiID()].setX(x);
		naves[getMiID()].setY(y);
		naves[getMiID()].setDir(dir);
		naves[getMiID()].setEscudo(E);
		if (FR == 0) miFR = 1;
		else miFR = FR;
		if (E == 0){
			//System.out.println("moneda nueva");
			Moneda coin = new Moneda(idmonedas++, x, y, 'O');
			monedas.put(coin.IdOb(), coin);
			monedas_nuevas.addElement(coin);
		}
		else if (E == -1 && naves[getMiID()].Vivo()){ //fuera de juego
			naves[getMiID()].setVivo(false);
			vivos--;
		}
		enviar();
	}

	public int[] getMapa() {
		return Utils.MapaInt();
	}

	public TiroPos[] getTiros() {
		return tiros;
	}

	public void setMiTiro(int x, int y, int pot) {
		tiros[getMiID()].setX(x);
		tiros[getMiID()].setY(y);
		tiros[getMiID()].setPotencia(pot);
	}

	public int getFrameRate() {
		return FRmodelo;
	}

	public void setMonedaAgarrada(int id) {
		//borrar la moneda id
		if (id != -1) {
			m = (Moneda)monedas.get(new Integer(id));
			monedas_eliminadas.addElement(m);
		}
		
	}

	public void setMiAsesino(int id) {
		if (id == -1)
			naves[getMiID()].setAsesino("V");
		else {
			naves[getMiID()].setAsesino(""+id);
			naves[id].sumarPunto();
		}
	}

	public Vector getMonedasNuevas() {
		return monedas_nuevas;
	}

	public Vector getMonedasEliminadas() {
		return monedas_eliminadas;
	}

	public Hashtable getMonedas() {
		return monedas;
	}
	
	//esta función se llama cuando hay que crear una nueva moneda aleatoria
	public void agregar_moneda_aleatoria() {
		if (idmonedas < 9967){
			int x = timer.rand().nextInt(Utils.MAPA_SIZE-Utils.MONEDA_SIZE) + Utils.MONEDA_SIZE;
			int y = timer.rand().nextInt(Utils.MAPA_SIZE-Utils.MONEDA_SIZE) + Utils.MONEDA_SIZE;
			Moneda m = new Moneda(idmonedas++, x, y, 'P');
			monedas.put(m.IdOb(), m);
			monedas_nuevas.addElement(m);
		}
	}
	
	public void IniciarComunicacion() {
		super.IniciarComunicacion();
		timer.Iniciar();
	}


	public int getCantVivos() {
		return vivos;
	}


	public String[] getNombresJugadores() {
		return jugadores;
	}

}
