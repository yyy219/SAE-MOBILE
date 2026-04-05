package com.openminds.app.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.openminds.app.database.entity.Formation;
import com.openminds.app.repository.StatistiquesRepository;
import com.openminds.app.database.entity.FormationTop;
import com.openminds.app.database.entity.StatThematique;
import java.util.Calendar;
import java.util.List;




public class StatistiquesViewModel extends AndroidViewModel {

    private final StatistiquesRepository repository;

    // Timestamp de début de la période sélectionnée
    // Quand il change, TOUTES les LiveData se recalculent via switchMap
    private final MutableLiveData<Long> debutPeriode = new MutableLiveData<>();


    private final MutableLiveData<String> labelPeriode = new MutableLiveData<>();


    public final LiveData<Integer> nbFormations;
    public final LiveData<Integer> nbSessions;


    public final LiveData<Integer> nbBenevolesActifs;
    public final LiveData<Float>   tauxReussite;
    public final LiveData<List<StatThematique>> participationParThematique;
    public final LiveData<List<FormationTop>>   topFormations;

    public StatistiquesViewModel(@NonNull Application application) {
        super(application);
        repository = new StatistiquesRepository(application);

        // Globaux
        nbFormations = repository.getNbFormations();
        nbSessions   = repository.getNbSessions();

        // switchMap : quand debutPeriode change → nouvelle requête Room
        nbBenevolesActifs = Transformations.switchMap(debutPeriode,
                debut -> repository.getNbBenevolesActifs(debut));

        tauxReussite = Transformations.switchMap(debutPeriode,
                debut -> repository.getTauxReussite(debut));

        participationParThematique = Transformations.switchMap(debutPeriode,
                debut -> repository.getParticipationParThematique(debut));

        topFormations = Transformations.switchMap(debutPeriode,
                debut -> repository.getTopFormations(debut, 2));


        setPeriodeMois();
    }



    public void setPeriode7Jours() {
        long debut = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
        debutPeriode.setValue(debut);
        labelPeriode.setValue("Données des 7 derniers jours");
    }

    public void setPeriodeMois() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        debutPeriode.setValue(cal.getTimeInMillis());
        labelPeriode.setValue("Données du mois en cours");
    }

    public void setPeriodeAnnee() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_YEAR, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        debutPeriode.setValue(cal.getTimeInMillis());
        labelPeriode.setValue("Données de l'année en cours");
    }

    public LiveData<String> getLabelPeriode() { return labelPeriode; }


    public LiveData<List<FormationTop>> getTopFormationsComplet() {
        Long debut = debutPeriode.getValue();
        return repository.getTopFormations(debut != null ? debut : 0L, 100);
    }


    public LiveData<List<Formation>> getFormationsTerminees(int utilisateurId) {
        return repository.getFormationsTerminees(utilisateurId);
    }
}
