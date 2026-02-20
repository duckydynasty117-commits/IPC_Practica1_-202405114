import java.util.Scanner;
import java.util.Random;

public class PacMan {
    static Scanner sc = new Scanner(System.in);
    static Random rnd = new Random();

    static char[][] tablero;
    static int filas, columnas;
    static int pacFila, pacCol;
    static String usuario;
    static int punteo, vidas, premios;

    // Historial
    static String[] hNombres = new String[100];
    static int[] hPuntos = new int[100];
    static int totalPartidas = 0;

    public static void main(String[] args) {
        int op = 0;
        while (op != 3) {
            System.out.println("\n====MENU DE INICIO====");
            System.out.println("1. Iniciar Juego");
            System.out.println("2. Historial");
            System.out.println("3. Salir");
            System.out.print("Opcion: ");
            op = sc.nextInt();

            if (op == 1) iniciarJuego();
            else if (op == 2) verHistorial();
            else if (op == 3) System.out.println("Hasta luego!");
            else System.out.println("Opcion invalida.");
        }
    }
    static void iniciarJuego() {
        System.out.print("Nombre de usuario: ");
        usuario = sc.next().toUpperCase();
        punteo = 0;
        vidas = 3;

        char tipo = ' ';
        while (tipo != 'P' && tipo != 'G') {
            System.out.print("Tablero (P=Pequeno, G=Grande): ");
            tipo = sc.next().toUpperCase().charAt(0);
        }
        filas    = (tipo == 'P') ? 5  : 10;
        columnas = (tipo == 'P') ? 6  : 10;

        int total      = filas * columnas;
        int maxPremios  = (int)(total * 0.40);
        int maxParedes  = (int)(total * 0.20);
        int maxFantasmas= (int)(total * 0.20);

        int cPremios   = pedirNumero("PREMIOS [1-" + maxPremios + "]: ", 1, maxPremios);
        int cParedes   = pedirNumero("PAREDES [1-" + maxParedes + "]: ", 1, maxParedes);
        int cFantasmas = pedirNumero("TRAMPAS [1-" + maxFantasmas + "]: ", 1, maxFantasmas);

        tablero = new char[filas][columnas];
        for (int i = 0; i < filas; i++)
            for (int j = 0; j < columnas; j++)
                tablero[i][j] = ' ';

        colocar('X', cParedes);   //que signi cada uno
        colocar('0', cPremios / 2);
        colocar('$', cPremios - cPremios / 2);
        colocar('@', cFantasmas);
        premios = cPremios;

        mostrarTablero();

        boolean ok = false;
        while (!ok) {
            System.out.print("Fila inicial (1-" + filas + "): ");
            pacFila = sc.nextInt() - 1;
            System.out.print("Columna inicial (1-" + columnas + "): ");
            pacCol = sc.nextInt() - 1;

            if (pacFila < 0 || pacFila >= filas || pacCol < 0 || pacCol >= columnas)
                System.out.println("Fuera del tablero.");
            else if (tablero[pacFila][pacCol] == 'X')
                System.out.println("Hay una pared, elige otra.");
            else {
                tablero[pacFila][pacCol] = '<';
                ok = true;
            }
        }

        jugar();
    }

    static void jugar() {
        boolean activo = true;
        while (activo) {
            mostrarPanel();
            mostrarTablero();
            System.out.print("Movimiento (8=Arriba 5=Abajo 6=Der 4=Izq F=Pausa): ");
            String tecla = sc.next().toUpperCase();

            if (tecla.equals("F")) {
                System.out.println("\n== PAUSA ==");
                System.out.println("1. Regresar");
                System.out.println("3. Terminar partida");
                System.out.print("Opcion: ");
                int op = sc.nextInt();
                if (op == 3) {
                    guardar();
                    activo = false;
                }
            } else {
                mover(tecla);
                if (vidas <= 0) {
                    mostrarPanel(); mostrarTablero();
                    System.out.println("GAME OVER! Punteo: " + punteo);
                    guardar();
                    activo = false;
                } else if (premios <= 0) {
                    mostrarPanel(); mostrarTablero();
                    System.out.println("GANASTE! Punteo: " + punteo);
                    guardar();
                    activo = false;
                }
            }
        }
    }
    static void mover(String tecla) {
        int nf = pacFila, nc = pacCol;

        if      (tecla.equals("8")) nf--;  //baja
        else if (tecla.equals("5")) nf++;  //sube
        else if (tecla.equals("6")) nc++;  //derech
        else if (tecla.equals("4")) nc--;  //izqui
        else { System.out.println("Tecla invalida."); return; }

        if (nf < 0) nf = filas - 1;
        if (nf >= filas) nf = 0;
        if (nc < 0) nc = columnas - 1;
        if (nc >= columnas) nc = 0;

        if (tablero[nf][nc] == 'X') {
            System.out.println("Hay una pared!");
            return;
        }
        if (tablero[nf][nc] == '@') {
            vidas--;
            System.out.println("Uyy un fantasma! Vidas: " + vidas);
        }
        if (tablero[nf][nc] == '0') {
            punteo += 10;
            premios--;
            System.out.println("+10 pts!");
        }
        if (tablero[nf][nc] == '$') {
            punteo += 15;
            premios--;
            System.out.println("+15 pts!");
        }

        tablero[pacFila][pacCol] = ' ';
        tablero[nf][nc] = '<';
        pacFila = nf;
        pacCol  = nc;
    }

    static void colocar(char item, int cantidad) {
        int c = 0;
        while (c < cantidad) {
            int f = rnd.nextInt(filas);
            int j = rnd.nextInt(columnas);
            if (tablero[f][j] == ' ') {
                tablero[f][j] = item;
                c++;
            }
        }
    }
    static void mostrarPanel() {
        System.out.println("------------------");
        System.out.println("USUARIO: " + usuario);
        System.out.println("PUNTEO:  " + punteo);
        System.out.println("VIDAS:   " + vidas);
    }
    static void mostrarTablero() {
        System.out.println();
        for (int i = 0; i < filas; i++) {
            System.out.print("| ");
            for (int j = 0; j < columnas; j++)
                System.out.print(tablero[i][j] + " ");
            System.out.println("|");
        }
    }
    static void guardar() {
        hNombres[totalPartidas] = usuario;
        hPuntos[totalPartidas]  = punteo;
        totalPartidas++;
    }

    static void verHistorial() {
        System.out.println("\n== HISTORIAL DE PARTIDAS ==");
        if (totalPartidas == 0) {
            System.out.println("No hay partidas aun.");
            return;
        }
        for (int i = totalPartidas - 1; i >= 0; i--)
            System.out.println((totalPartidas - i) + ". " + hNombres[i] + " - " + hPuntos[i] + " pts");
    }
    static int pedirNumero(String mensaje, int min, int max) {
        int n = -1;
        while (n < min || n > max) {
            System.out.print(mensaje);
            if (sc.hasNextInt()) n = sc.nextInt();
            else { sc.next(); System.out.println("Ingresa un numero."); }  
        }
        return n;
    }
}