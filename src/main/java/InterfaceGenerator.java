import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 */

/**
 * @author mwyss
 *
 */
public class InterfaceGenerator {

    private String klassenName;
    private Class<?> klasse;
    private Method[] methoden;

    public InterfaceGenerator(String klassenName) {

	this.klassenName = klassenName;
	this.klasse = findeKlasse(klassenName);
	this.methoden = findeMethoden(klasse);
	generiereInterfaceFile(klasse, methoden);

    }

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

    public static Method[] findeMethoden(Class<?> klasse) {
	List<Method> methoden = new ArrayList<>();
	methoden.addAll(Arrays.asList(klasse.getMethods()));
	if (klasse.getSuperclass() != null) {

	    // rekursive Abfrage
	    methoden.addAll(Arrays.asList(findeMethoden(klasse.getSuperclass())));
	}

	return methoden.toArray(new Method[0]);
    }

    public static void generiereInterfaceFile(Class<?> klasse, Method[] methoden) {
	String klassenName = trimmeKlassenName(klasse);

	try (FileWriter writer = new FileWriter(klassenName + ".java")) {
	    writer.write(InterfaceGenerator.class.getPackageName());
	    writer.write("public interface" + klassenName + "{");
	    for (int i = 0; i < methoden.length; i++) {
		Method methode = methoden[i];
		writer.write(Modifier.toString(methode.getModifiers()) + " " + methode.getReturnType() + " "
			+ methode.getName() + erstelleParameterString(methode.getParameterTypes()));
	    }
	    writer.write("}");
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

//    public static Object generiereInterface(Class<?> klasse, Method[] methoden) {
//	String klassenName = trimmeKlassenName(klasse);
//	
//	
//    }
//
//    public interface. trimmeKlassenName(Class<?> klasse) {
//	InterfaceGenerator.class.getPackageName();
//	}
    
    
    public static String trimmeKlassenName(Class<?> klasse) {
	String klassenName = klasse.getName();
	int cutPosition = klassenName.lastIndexOf(".");
	klassenName = klassenName.substring(cutPosition + 1);
	klassenName = klassenName + "If";

	return klassenName;

    }

    public static String erstelleParameterString(Class<?>[] parameterTypen) {
	if (parameterTypen.length > 0) {
	    return "(" + Arrays.toString(parameterTypen) + ")";
	}
	return "()";
    }

    /**
     * @param args
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

}
