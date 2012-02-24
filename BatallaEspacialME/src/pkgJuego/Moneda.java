//Clase Moneda:

/*
 * Representa una moneda en el juego. Controla toda la información
 * sobre la moneda y es a la vez el sprite mismo.
 */

package pkgJuego;

import javax.microedition.lcdui.game.Sprite;
import pkgUtils.ImagenVault;

public class Moneda extends Sprite {

	//---------------------------------------------------------------
	//para la animación:
	//---------------------------------------------------------------
	private static int sec[] = {0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 3, 3};
	
	
	//---------------------------------------------------------------
	//datos que tiene cada moneda:
	//---------------------------------------------------------------
	private int id, valor;
	
	
	//---------------------------------------------------------------
	//Constructor:
	//Moneda(int id, int x, int y, char tipo):
	//---------------------------------------------------------------
	//
	//Construye una nueva moneda.
	public Moneda(int id, int x, int y, char tipo) {
		super((tipo == 'O') ? ImagenVault.getMonedaOro() : ImagenVault.getMonedaPlata(), 32, 32);
		
		if (tipo == 'O')
			valor = 20;
		else
			valor = 5;
		
		setFrameSequence(sec);
		this.id = id;
		defineReferencePixel(16, 16);
		setRefPixelPosition(x, y);
		
	}
	
	public int Id() { return id; }
	public Integer IdOb() { return new Integer(id); }
	public int Valor() { return valor; }
	
}
