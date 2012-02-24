package pkgJuego;

import javax.microedition.lcdui.game.Sprite;
import pkgUtils.Utils;

public class Tiro {

	private static int limites = Utils.MAPA_SIZE; //límite de la pantalla
	private Sprite sprite;
	private boolean existe;
	private int dir, potencia, vel;
	
	public Tiro(Sprite s) {
		sprite = s;
		existe = false;
		potencia = 10;
		vel = 10;
	}

	public boolean Existe() {
		return existe;
	}
	
	public int getX() {
		if (existe)
			return sprite.getRefPixelX();
		else
			return -1;
	}
	
	public int getY() {
		if (existe)
			return sprite.getRefPixelY();
		else
			return -1;
	}
	
	protected void matar() {
		existe = false;
		sprite.setVisible(false);
	}
	
	public void Lanzar(int x, int y, int d) {
		sprite.setRefPixelPosition(x, y);
		dir = d;
		existe = true;
		sprite.setVisible(true);
	}
	
	public void Mover(int x, int y, int a) {
		//acá ver si hay que eliminarlo:
		switch (dir){
		case 0: { //N
			sprite.move(0, -vel);
			break;
		}
		case 1: { //NE
			sprite.move(vel, -vel);
			break;
		}
		case 2: { //E
			sprite.move(vel, 0);
			break;
		}
		case 3: { //SE
			sprite.move(vel, vel);
			break;
		}
		case 4: { //S
			sprite.move(0, vel);
			break;
		}
		case 5: { //SO
			sprite.move(-vel, vel);
			break;
		}
		case 6: { //O
			sprite.move(-vel, 0);
			break;
		}
		case 7: { //NO
			sprite.move(-vel, -vel);
			break;
		}
		}
		
		//ver si se fue de mi visión y matarlo sino
		
		if (getX() < x - a || getX() > x + a || getY() < y - a || getY() > y + a)
			matar();

		//si se fue del mapa también
		if (getX() < 0 || getX() > limites || getY() < 0 || getY() > limites)
			matar();
		
	}

	public int getPotencia() {
		return potencia;
	}

	public void AumentarPotencia(int p) {
		potencia += p;
		if (potencia > 999)
			potencia = 999;
	}
	
	public void setVel(int v) {
		vel = v;
	}
}