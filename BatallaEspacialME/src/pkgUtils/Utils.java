//Clase Utils:

/* 
 * Esta clase tiene varias funciones de utilería que pueden ser llamadas
 * desde cualquier parte. Todas las funciones son estáticas por lo que
 * se pueden llamar sin necesitar instanciar una clase de este tipo.
 */

package pkgUtils;

import javax.bluetooth.UUID;

public class Utils {

	//---------------------------------------------------------------
	//Atributos estáticos:
	//---------------------------------------------------------------
	public static final int MAPA_SIZE = 1000; //coordenadas máximas posibles dentro del mapa
	public static final float MAPA_SIZE_F = 1000f; //igual pero flotante (para radar)
	public static final int MAPA_SIZE_TILES = 20; //cantidad de tiles por lado
	public static final String MSG_ERROR_FATAL = "Ha ocurrido un error fatal.\nLa aplicación se cerrará.";
	public static final int NAVE_SIZE = 48;
	public static final int TIRO_SIZE = 32;
	public static final int MONEDA_SIZE = 32;
	public static final int TILE_SIZE = 50;
	
	
	
	//---------------------------------------------------------------
	//UUID getUUIDServicio():
	//---------------------------------------------------------------
	//
	//Devuelve el UUID del servicio Bluetooth ofrecido.
	//
	public static UUID getUUIDServicio() {
		return new UUID("112233445566778899AABBCCDDEEFF", false);
	}
	
	
	//---------------------------------------------------------------
	//String Rellenar(String s, int len):
	//---------------------------------------------------------------
	//
	//Rellena la cadena s con el caracter espacio (' ') al inicio
	//para lograr una longitud de len caracteres.
	//Sirve para armar los mensajes del protocolo, para algún campo
	//de longitud fija.
	//
	public static String Rellenar(String s, int len) {
		String nueva = "";
		for (int i=1; i<=len-s.length(); ++i)
			nueva += " ";
		nueva += s;
		return nueva;
	}
	
	
	//---------------------------------------------------------------
	//String Mapa():
	//---------------------------------------------------------------
	//
	//Devuelve la cadena correspondiente al mapa para el TiledLayer
	//(es decir los números de los tiles usados). Esta cadena está
	//armada para ser enviada en el mensaje del servidor a todos
	//los jugadores directamente, es decir que no necesita ningún
	//tratamiento especial para ser enviada.
	//
	public static String Mapa() {
		return "02030303030303030303030303030303030303040700000010" +
				"0100000000000000080000000000090700001010000000060" +
				"0000500000800000000090700000000000000000000000000" +
				"0000001010090700000008000000000000000000000000000" +
				"0090700000800000015000001111918000000000009070000" +
				"0000000020000000191919000000000009070000000000000" +
				"0000000161917060000000009070000000000000000000000" +
				"0000000000000009070000000011191918000010100000000" +
				"0001509070000000016191917000001100000000000200907" +
				"0000100000000000000000100000000000000907001010000" +
				"0000000000000001000000000000907000000000000061000" +
				"0000000000000600000907000000000000001000000000000" +
				"0000000000907000800000000000000000000000800000000" +
				"0907000000000800000011180000080800000000090700000" +
				"1000000000016170000000000000000090700000000000000" +
				"0000000000000010100000091213131313131313131313131" +
				"313131313131314";
	}
	
	
	//---------------------------------------------------------------
	//int[] MapaInt():
	//---------------------------------------------------------------
	//
	//Devuelve el mapa para el TiledLayer ya como vector de números (para
	//el servidor):
	//
	public static int[] MapaInt() {
		int m[] = {
			2,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,4,
			7,0,0,0,10,1,0,0,0,0,0,0,0,8,0,0,0,0,0,9,
			7,0,0,10,10,0,0,0,6,0,0,5,0,0,8,0,0,0,0,9,
			7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,10,10,9,
			7,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
			7,0,0,8,0,0,0,15,0,0,1,11,19,18,0,0,0,0,0,9,
			7,0,0,0,0,0,0,20,0,0,0,19,19,19,0,0,0,0,0,9,
			7,0,0,0,0,0,0,0,0,0,0,16,19,17,6,0,0,0,0,9,
			7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,
			7,0,0,0,0,11,19,19,18,0,0,10,10,0,0,0,0,0,15,9,
			7,0,0,0,0,16,19,19,17,0,0,1,10,0,0,0,0,0,20,9,
			7,0,0,10,0,0,0,0,0,0,0,0,10,0,0,0,0,0,0,9,
			7,0,10,10,0,0,0,0,0,0,0,0,0,10,0,0,0,0,0,9,
			7,0,0,0,0,0,0,6,10,0,0,0,0,0,0,0,6,0,0,9,
			7,0,0,0,0,0,0,0,10,0,0,0,0,0,0,0,0,0,0,9,
			7,0,8,0,0,0,0,0,0,0,0,0,0,0,8,0,0,0,0,9,
			7,0,0,0,0,8,0,0,0,11,18,0,0,8,8,0,0,0,0,9,
			7,0,0,1,0,0,0,0,0,16,17,0,0,0,0,0,0,0,0,9,
			7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,10,10,0,0,9,
			12,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,14
		};
		return m;
	}

}
