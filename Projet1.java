public class Projet1
{
    public static void main(String[] args)
    {
        if(args.length != 1)
            System.out.println("usage : java Projet1 fichier");
        else
            Graphe graphe = GestionnaireES.creerGraphe(args[0]);
    }
}
