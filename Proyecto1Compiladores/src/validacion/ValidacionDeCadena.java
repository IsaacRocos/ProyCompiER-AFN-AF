package validacion;

public class ValidacionDeCadena {
    /* public static void main(String[] args) 
     {
     int x=7,y=8;
     char[][] tabla = new char[x][y]; //Contiene la tabla de transiciones       
     /***********************************************************************************************************************/
    /*   tabla[0][0]='A'; tabla[0][1]='I'; tabla[0][2]='a'; tabla[0][3]='B'; tabla[0][4]='b'; tabla[0][5]='C'; tabla[0][6]='c'; tabla[0][7]='D';
     tabla[1][0]='B'; tabla[1][1]='F'; tabla[1][2]='a'; tabla[1][3]='-'; tabla[1][4]='b'; tabla[1][5]='E'; tabla[1][6]='c'; tabla[1][7]='-';
     tabla[2][0]='C'; tabla[2][1]='N'; tabla[2][2]='a'; tabla[2][3]='-'; tabla[2][4]='b'; tabla[2][5]='C'; tabla[2][6]='c'; tabla[2][7]='F';
     tabla[3][0]='D'; tabla[3][1]='F'; tabla[3][2]='a'; tabla[3][3]='-'; tabla[3][4]='b'; tabla[3][5]='-'; tabla[3][6]='c'; tabla[3][7]='G';
     tabla[4][0]='E'; tabla[4][1]='F'; tabla[4][2]='a'; tabla[4][3]='-'; tabla[4][4]='b'; tabla[4][5]='-'; tabla[4][6]='c'; tabla[4][7]='-';
     tabla[5][0]='F'; tabla[5][1]='F'; tabla[5][2]='a'; tabla[5][3]='-'; tabla[5][4]='b'; tabla[5][5]='-'; tabla[5][6]='c'; tabla[5][7]='-';
     tabla[6][0]='G'; tabla[6][1]='F'; tabla[6][2]='a'; tabla[6][3]='-'; tabla[6][4]='b'; tabla[6][5]='-'; tabla[6][6]='c'; tabla[6][7]='G';
        
     /***********************************************************************************************************************/
    /*tabla[0][0]='A'; tabla[0][1]='I'; tabla[0][2]='a';  tabla[0][3]='B';  tabla[0][4]='b';  tabla[0][5]='C';
     tabla[1][0]='B'; tabla[1][1]='N'; tabla[1][2]='a';  tabla[1][3]='B';  tabla[1][4]='b';  tabla[1][5]='D';
     tabla[2][0]='C'; tabla[2][1]='N'; tabla[2][2]='a';  tabla[2][3]='B';  tabla[2][4]='b';  tabla[2][5]='C';
     tabla[3][0]='D'; tabla[3][1]='N'; tabla[3][2]='a';  tabla[3][3]='B';  tabla[3][4]='b';  tabla[3][5]='E';
     tabla[4][0]='E'; tabla[4][1]='F'; tabla[4][2]='a';  tabla[4][3]='B';  tabla[4][4]='b';  tabla[4][5]='C';*/

    /**
     * ********************************************************************************************************************
     */
    /*  String cadena2 = "aa";
     valida(tabla,cadena2);     
     }   //Fin del main 
     */

    private String valida;
    private int resultado;

    public String getValidaC() {
        return valida;

    }

    public void setValidaC(String valida) {
        this.valida = valida;
    }

    public int getResultado() {
        return resultado;
    }

    public void setResultado(int resultado) {
        this.resultado = resultado;
    }

    //---Metodo validar---
    public boolean validar() {
        char[][] tabla = new char[5][6]; //Contiene la tabla de transiciones 
        tabla[0][0] = 'A';
        tabla[0][1] = 'I';
        tabla[0][2] = 'a';
        tabla[0][3] = 'B';
        tabla[0][4] = 'b';
        tabla[0][5] = 'C';
        tabla[1][0] = 'B';
        tabla[1][1] = 'N';
        tabla[1][2] = 'a';
        tabla[1][3] = 'B';
        tabla[1][4] = 'b';
        tabla[1][5] = 'D';
        tabla[2][0] = 'C';
        tabla[2][1] = 'N';
        tabla[2][2] = 'a';
        tabla[2][3] = 'B';
        tabla[2][4] = 'b';
        tabla[2][5] = 'C';
        tabla[3][0] = 'D';
        tabla[3][1] = 'N';
        tabla[3][2] = 'a';
        tabla[3][3] = 'B';
        tabla[3][4] = 'b';
        tabla[3][5] = 'E';
        tabla[4][0] = 'E';
        tabla[4][1] = 'F';
        tabla[4][2] = 'a';
        tabla[4][3] = 'B';
        tabla[4][4] = 'b';
        tabla[4][5] = 'C';
        String cadena = getValidaC();
        int resultado = Valida(tabla, cadena);
        setResultado(resultado);
        return true;

    }

    public static int Valida(char[][] tabla, String cadena2) {

        char[] cadena = cadena2.toCharArray(); //Tiene la cadena que va a validar
        int columnas = tabla.length; //Obtiene el numero de columas de la tabla de transiciones 5
        int filas = tabla[0].length; //Obtiene el numero de filas de la tabla de transiciones 6
        int posx = 0, bandera = 0;
        char letra;
        char[][] ne = new char[columnas][2]; //Contiene la tabla de transiciones   
        for (int s = 0; s < columnas; s++) {
            ne[s][0] = tabla[s][0]; //Guarda el estado
            ne[s][1] = (char) s;   //Le asigna un numero al estado para saber su posicion en el arreglo         
        }

        for (int a = 0; a < cadena.length; a++) {
            for (int c = 2; c < filas; c += 2) {
                if (cadena[a] == tabla[posx][c]) {
                    letra = tabla[posx][c + 1];
                    if (letra == '-') {
                        c = filas;
                        a = cadena.length;
                        bandera = 1;
                    }
                    posx = numero(ne, letra, columnas);
                    break;
                }
            } //Fin del for para filas
        } //Fin del for que recorre la cadena de prueba

        if (tabla[posx][1] == 'F' && bandera != 1) {

            System.out.println("La cadena: " + cadena2 + " es aceptada");
            return 1;
        } else {
            System.out.println("La cadena: " + cadena2 + " es rechazada");
            return 0;
        }
    } //Fin de la funcion valida

    public static int numero(char[][] transicion, char estado, int colum) {
        int numsim = 0;
        for (int v = 0; v < colum; v++) {
            if (estado == transicion[v][0]) {
                numsim = (int) transicion[v][1];
                break;
            }
        }
        return numsim;
    }
}   //Fin de la clase
