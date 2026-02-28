import models.depense;
import service.serviceDeppense;
import utils.mydb;

public class main {

    public static void main(String[] args) {
        /*A a = new A();
        A a1 = new A();

        P p = P.getInstance();
        P p1 = P.getInstance();

        System.out.println(p.hashCode());
        System.out.println(p1.hashCode());
        mydb m = mydb.getInstance();*/
        serviceDeppense dp= new serviceDeppense();
        // Exemple avec des valeurs fictives pour le montant, la date et l'ID catégorie
        dp.add(new depense(1, "Achat bierre", 100,new java.sql.Date(System.currentTimeMillis()), 2));
        System.out.println(dp.getAll());
    }
}
