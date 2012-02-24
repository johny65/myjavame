package pkgMain;


import java.util.Vector;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotOpenException;

public class NotasMidlet extends MIDlet implements CommandListener {

	private Display pantalla;
	private List lista_notas, opciones;
	private Command cmdSalir, cmdVolver, cmdOp;
	private RecordStore rs;
	private Vector notas;
	private Alert alerta;
	
	//form
	private Form agregar;
	private TextField txtTitulo, txtNota;
	private Command cmdOk;
	
	public NotasMidlet() {
		try {
			rs = RecordStore.openRecordStore("NotasJJ", true, RecordStore.AUTHMODE_ANY, true);
		} catch (Exception e) {
			System.err.println("Error al abrir el RecordStore");
			destroyApp(true);
		}
		
		cmdSalir = new Command("Salir", Command.EXIT, 1);
		cmdVolver = new Command("Volver", Command.BACK, 1);
		cmdOp = new Command("Opciones", Command.ITEM, 1);
		lista_notas = new List("Lista de Notas", List.IMPLICIT);
		opciones = new List("Opciones", List.IMPLICIT);
		notas = new Vector();
	}
	
	public void startApp() {
		
		pantalla = Display.getDisplay(this);
		InitOpciones();
		InitFormAgregar();
		CargarNotas();
		lista_notas.addCommand(cmdSalir);
		lista_notas.addCommand(cmdOp);
		lista_notas.setCommandListener(this);
		pantalla.setCurrent(lista_notas);

	}

	public void destroyApp(boolean unconditional) {
		try {
			rs.closeRecordStore();
		} catch (Exception e) {
			System.err.println("Error al cerrar RecordStore ¬¬...");
		}
		notifyDestroyed();
	}

	protected void pauseApp() {}

//	public void VerRecordStore() {
//		RecordEnumeration enum;
//		try {
//			enum = rs.enumerateRecords(null, null, false);
//		} catch (RecordStoreNotOpenException e) {
//			System.err.println("Error al enumerar records");
//			return;
//		}
//		vista.deleteAll();
//		byte[] datos;
//		String sdatos;
//		indices = new int[enum.numRecords()];
//		int i=0;
//		while (enum.hasNextElement()){
//			try {
//				indices[i] = enum.nextRecordId();
//				datos = rs.getRecord(indices[i]);
//				sdatos = new String(datos);
//			} catch (Exception e) {
//				System.err.println("Error al recuperar siguiente record");
//				sdatos = "<error>";
//			}
//			vista.append(sdatos, null);
//			i++;
//		}
//		pantalla.setCurrent(vista);
//	}
	
	
	public void ActualizarLista() {
		lista_notas.deleteAll();
		Nota n;
		for (int i=0; i<notas.size(); ++i){
			n = (Nota)notas.elementAt(i);
			lista_notas.append(n.getTitulo(), null);
		}
	}
	
	public void CargarNotas() {
		RecordEnumeration enum;
		byte[] datos;
		Nota n;
		try {
			enum = rs.enumerateRecords(null, null, false);
			while (enum.hasNextElement()){
				datos = enum.nextRecord();
				n = new Nota(datos);
				notas.addElement(n);
			}
		} catch (Exception ex) {
			System.err.println("Error al cargar notas: " + ex.getMessage());
			ex.printStackTrace();
			return;
		}
		ActualizarLista();
	}
	
	public void AgregarNota(Nota n) {
		byte b[] = n.getBytes();
		try {
			rs.addRecord(b, 0, b.length);
		} catch (RecordStoreNotOpenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreFullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecordStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public void EliminarRecords() {
//		boolean b[] = new boolean[vista.size()];
//		vista.getSelectedFlags(b);
//		for (int i=0; i<b.length; ++i){
//			if (b[i]){
//				try {
//					rs.deleteRecord(indices[i]);
//				} catch (Exception e) {
//					System.err.println("Error al borrar record");
//				}
//			}
//		}
//		pantalla.setCurrent(main);
//	}
	
	public void commandAction(Command c, Displayable d) {
		if (c == cmdSalir)
			destroyApp(true);
		else if (c == cmdVolver)
			pantalla.setCurrent(lista_notas);
		else if (c == cmdOp)
			pantalla.setCurrent(opciones);
		else if (c == cmdOk){
			Nota nueva = new Nota(txtTitulo.getString(), txtNota.getString());
			notas.addElement(nueva);
			lista_notas.append(nueva.getTitulo(), null);
			pantalla.setCurrent(lista_notas);
		}
		else if (d == opciones) {
			switch (opciones.getSelectedIndex()){
				case 0: { //agregar nota
					txtTitulo.setString("");
					txtNota.setString("");
					pantalla.setCurrent(agregar);
				}
				case 1: {
					//
				}
			}
		}
		
	}
	
	public void InitOpciones() {
		opciones = new List("Opciones", List.IMPLICIT);
		opciones.append("Agregar nueva nota", null);
		opciones.append("Editar", null);
		opciones.append("Eliminar", null);
		opciones.append("Eliminar todas las notas", null);
		opciones.addCommand(cmdVolver);
		opciones.setCommandListener(this);
		
	}
	
	public void InitFormAgregar() {
		agregar = new Form("Agregar nueva nota");
		txtTitulo = new TextField("Título:", "", 100, TextField.ANY);
		txtNota = new TextField("Contenido:", "", 1000, TextField.ANY);
		cmdOk = new Command("Agregar", Command.OK, 1);
		agregar.append(txtTitulo);
		agregar.append(txtNota);
		agregar.addCommand(cmdVolver);
		agregar.addCommand(cmdOk);
		agregar.setCommandListener(this);
	}
	
	public void MostrarMensaje(String msg) {
		if (alerta == null){
			alerta = new Alert("Información", "", null, AlertType.ERROR);
			alerta.setTimeout(Alert.FOREVER);
		}
		
	}

}
