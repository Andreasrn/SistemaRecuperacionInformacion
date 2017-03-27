package sri;

import java.io.*;

/**
 * Created by Andrea on 27/03/2017.
 */
public class ClaseSerializable <T> implements Serializable{

    private T objeto;

    public ClaseSerializable(T obj){
        objeto = obj;
    }

    public T getObjeto(){
        return objeto;
    }

    public void escribirObjeto(String ruta) throws IOException {
        FileOutputStream fos = new FileOutputStream(ruta+".obj");
        ObjectOutputStream oos = new ObjectOutputStream(fos);

        oos.writeObject(objeto);

        oos.close();

    }

    public T leerObjeto(String ruta) throws IOException, ClassNotFoundException {
        FileInputStream in = new FileInputStream(ruta);
        ObjectInputStream ois = new ObjectInputStream(in);

        T output = (T) ois.readObject();
        ois.close();

        return output;
    }



}
