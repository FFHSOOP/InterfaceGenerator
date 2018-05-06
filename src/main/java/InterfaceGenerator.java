import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**Das Programm bekommt einen vollqualifizierten Namen einer Klasse und erzeugt daraus ein Interface
 * 
 * @author Stefan Nyffenegger
 * @author Benjamin Steffen
 * @author Marco Wyssmann
 * @version 1.0
 * 
 */
public class InterfaceGenerator {

    private String klassenName;
    private Class<?> klasse;
    private Method[] methoden;
    private static final String PFAD = "lib\\generierte Interfaces\\";

    public InterfaceGenerator(String klassenName) {

	this.klassenName = klassenName;
	this.klasse = findeKlasse(klassenName);
	this.methoden = findeMethoden(klasse);
	generiereInterfaceFile(klasse, methoden);

    }

    
    /**
     * @param args Ein String Array mit den Uebergabeparametern
     */
    public static void main(String[] args) {
    
        if (args.length != 1) {
            System.out.println("Usage: Bitte einen voll-qualifizierten Klassennamen eingeben!>");
            System.exit(1);
    
        }
    
        String klassenName = args[0];
        klassenName.trim();
        InterfaceGenerator generator = new InterfaceGenerator(klassenName);
    
    }

    /**Die Methode generiert im entsprechenden Pfad ein neues Interface-File entsprechend
     *  der uebergebenen Klasse
     * 
     * 
     * @param klasse Die Klasse aufgrund derer ein Interface generiert werden soll
     * @param methoden Ein Array mit den Methoden, welche fuer diese Klasse gefunden wurden
     */
    public static void generiereInterfaceFile(Class<?> klasse, Method[] methoden) {
        String klassenName = trimmeKlassenName(klasse);
        if (!Files.exists(Paths.get(PFAD + klassenName + ".java"))) {
            try (FileWriter writer = new FileWriter(PFAD + klassenName + ".java")) {
        	writer.write(InterfaceGenerator.class.getPackageName() + "\n");
        	writer.write("public interface" + klassenName + "{" + "\n");
        	for (int i = 0; i < methoden.length; i++) {
        	    Method methode = methoden[i];
        	    writer.write(Modifier.toString(methode.getModifiers()) + " " + methode.getReturnType() + " "
        		    + methode.getName() + erstelleParameterString(methode.getParameterTypes()) + "\n");
        	}
        	writer.write("}");
        	System.out.println("Das Interface mit dem Namen" + klassenName + "wurde erzeugt und befindet sich im "
        		+ "Ordner lib\\generierte Interfaces\\ ");
            } catch (IOException e) {
        	e.printStackTrace();
            }
        } else {
            System.out.println("Das File war bereits vorhanden");
        }
    }

    /**Anhand der vollqualifizierten Namens wird die entsprechende Klasse geladen und zurueckgegeben
     * 
     * @param klassenName Der vollquealifizierte Name der Klasse
     * @return die zum Namen gehoerende Klasse
     */
    public static Class<?> findeKlasse(String klassenName) {
	Class<?> klasse = null;

	try {
	    klasse = Class.forName(klassenName);
	} catch (ClassNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return klasse;
    }

    /**Die Methoden der uebergebenen Klasse werden in einem Array zurueckgegeben
     * Es werden auch die Superklassen untersucht
     * 
     * @param klasse Die Klasse deren Methoden ermittelt werden sollen
     * @return Ein Array mit den gefundenen Methoden
     */
    public static Method[] findeMethoden(Class<?> klasse) {
	List<Method> methoden = new ArrayList<>();
	methoden.addAll(Arrays.asList(klasse.getMethods()));
	if (klasse.getSuperclass() != null) {

	    // rekursive Abfrage
	    methoden.addAll(Arrays.asList(findeMethoden(klasse.getSuperclass())));
	}

	return methoden.toArray(new Method[0]);
    }

    /**Die Methode kürzt den vollqualifizierten Namen auf den letzten Teil des 
     * Namens und fügt "If" an.
     * 
     * @param klasse Die Klasse, deren Name gekürzt werden soll
     * @return der gekürzte Klassenname
     */
    public static String trimmeKlassenName(Class<?> klasse) {
	String klassenName = klasse.getName();
	int cutPosition = klassenName.lastIndexOf(".");
	klassenName = klassenName.substring(cutPosition + 1);
	klassenName = klassenName + "If";

	return klassenName;

    }

    /**
     * @param parameterTypen Ein Array mit den Methoden-Parameter
     * @return einen String mit den Methoden-Parametern
     */
    public static String erstelleParameterString(Class<?>[] parameterTypen) {
	if (parameterTypen.length > 0) {
	    return "(" + Arrays.toString(parameterTypen) + ")";
	}
	return "()";
    }

}
