import models.Activite;
import services.ServiceActivite;
import utils.MyDataBase;

public class Main {

    public static void main(String[] args) {
        //MyDataBase m = MyDataBase.getInstance();
        ServiceActivite sa = new ServiceActivite();
        //sa.add(new Activite("visit museee", "Tour de 1h30 avec guide", "Détente", "Paris, Seine", 30.00));
        //afficher
        //System.out.println(sa.getAll());
        //delete

        /*Activite a = new Activite();
        a.setIdActivite(5);
        sa.delete(a);
        // UPDATE
        /*Activite aUpdate = new Activite();
        aUpdate.setIdActivite(2);
        aUpdate.setNom("Visite du Louvre");
        aUpdate.setDescription("Musée + guide 2h");
        aUpdate.setCategorie("Culture");
        aUpdate.setLieu("Paris, Musée du Louvre");
        aUpdate.setPrix(25.00);
        sa.update(aUpdate);*/
    }
}


