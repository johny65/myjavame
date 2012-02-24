//Clase LWUITMultiLabel:

/*
 * Para agregar un Label multilínea con ajuste de línea automático
 * en un Form de LWUIT (simulado con un TextField).
 */

package pkgUI;

import com.sun.lwuit.TextField;

public class LWUITMultiLabel extends TextField {

	public LWUITMultiLabel(String texto) {
		super(texto);
		setSingleLineTextArea(false);
		setGrowByContent(true);
		setFlatten(true);
		setEditable(false);
		setFocusable(false);
	}
	
}
