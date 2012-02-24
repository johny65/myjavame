package pkgUniverso;

import java.util.Hashtable;
import java.util.Vector;

import pkgBluetooth.Cliente;
import pkgJuego.Nave;
import pkgJuego.TiroPos;
import pkgUI.MenuCliente;

public class UniversoCliente extends Cliente implements Universo {

	public UniversoCliente(MenuCliente mc, String nombre) {
		super(mc, nombre);
	}

	
	public Nave[] getPosiciones() {
		return naves;
	}

	public void setMisDatos(int x, int y, int dir, int E, int FR) {
		naves[id].setX(x);
		naves[id].setY(y);
		naves[id].setDir(dir);
		naves[id].setEscudo(E);
		miFR = FR;
//		if (E == -1 && naves[id].Vivo()){ //fuera de juego
//			naves[id].setVivo(false);
//			vivos--;
//		}
		enviar();
	}

	public int getMiID() {
		return id;
	}

	public int[] getMapa() {
		int mapa[] = new int[400];
		for (int i=0; i<400; ++i)
			mapa[i] = Integer.parseInt(mapastring.substring(2*i, 2*i+2).trim());
		mapastring = null; //para liberar la memoria
		return mapa;
	}
	
	public int getCantidadJugadores() {
		return num_jugadores;
	}

	public TiroPos[] getTiros() {
		return tiros;
	}

	public void setMiTiro(int x, int y, int pot) {
		tiros[id].setX(x);
		tiros[id].setY(y);
		tiros[id].setPotencia(pot);
	}

	public int getFrameRate() {
		return framerate;
	}

	public void setMonedaAgarrada(int id) {
		id_moneda_agarrada = id;
	}

	public void setMiAsesino(int id) {
		if (id == -1)
			miasesino = "V";
		else
			miasesino = ""+id;
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


	public int getCantVivos() {
		return vivos;
	}

	public String[] getNombresJugadores() {
		return jugadores;
	}


}
