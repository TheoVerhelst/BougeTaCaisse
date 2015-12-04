package ParkingEscape;

import java.io.*;

public class GestionnaireES
{
    public GestionnaireES()
    {
        // constructeur trivial
    }
    
    public static Graphe creerGraphe(String chemin)
    {
        Graphe ret = new Graphe();
        String contenu = lireFichier(chemin);
        System.out.println(contenu);
        return ret;
    }

    private static String lireFichier(String chemin)
    {
        String ligne = "", contenu = "";
        try
        {
            InputStream fluxEntree = new FileInputStream(chemin);
            BufferedReader tampon = new BufferedReader(new InputStreamReader(fluxEntree));
            while((ligne = tampon.readLine()) != null)
                contenu += ligne + "\n";
            tampon.close();
        }
        catch(Exception e)
        {
            System.out.println("erreur : " + e);
        }
        return contenu;
    }
}
