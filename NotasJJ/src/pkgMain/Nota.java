package pkgMain;

public class Nota {
	
	private String titulo, cont;
	
	public Nota(String Titulo, String Contenido) {
		titulo = Titulo;
		cont = Contenido;
	}
	
	public Nota(byte bytes[]) {
		String aux = new String(bytes);
		int sep = aux.indexOf("||");
		titulo = aux.substring(0, sep);
		cont = aux.substring(sep+2, aux.length());
	}
	
	public void setTitulo(String NuevoTitulo) {
		titulo = NuevoTitulo;
	}
	
	public void setContenido(String Contenido) {
		cont = Contenido;
	}
	
	public String getTitulo() {
		return titulo;
	}
	
	public String getContenido() {
		return cont;
	}
	
	public byte[] getBytes() {
		String aux = titulo + "||" + cont;
		return aux.getBytes();
	}

}
