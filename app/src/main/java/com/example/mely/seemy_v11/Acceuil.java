package com.example.mely.seemy_v11;

/**
 * Created by MELY on 4/17/2017.
 */
import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint("ValidFragment")
public class Acceuil extends ListFragment {

    Utilisateur user;

    @SuppressLint("ValidFragment")
    public Acceuil(Utilisateur user) {
        this.user = user;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        List<Map<String, Object>> userList = new ArrayList<Map<String, Object>>();
        AsyncTask AT = new UpdateProfilBackground(getActivity()).execute("Recherche", user.getId()); // recup les users aux alentours

        try {
            String S = (String) AT.get();
            String[] ids = S.split(" ");

            for (String id : ids) {

                AT = new UpdateProfilBackground(getActivity()).execute("RecupUsers", id); // recup les infos de chaque user
                Map<String, Object> usrs = (Map<String, Object>) AT.get();

                if (Integer.parseInt((String) usrs.get("success")) == 1) {
                    AsyncTask AT2 = new InfoProfilBackgroundActivity(getActivity()).execute(id); // recup les tags
                    String S2 = (String) AT2.get();
                    S2 = S2.replace(" ", " #"); // affiche le # car en bd le tag ne possèdent pas de #
                    usrs.put("Tags", "#"+S2);
                    userList.add(usrs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Clés utilisées dans la  Hashmap ( InfoProfilBackground)
        String[] from = {"Pseudo", "Age", "Sexe", "Tags", "Id"};

        // les items dans lequels les valeurs de hashmap seront placés
        int[] to = {R.id.user_profile_name, R.id.user_age, R.id.user_photo, R.id.user_profile_short_bio,R.id.hidden_id};

        //placer la liste dans un SimpleAdapter
        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), userList, R.layout.user_row, from, to);

        //affichage
        setListAdapter(adapter);


        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long row) {
        //intent vers MessageClasse qui va afficher tous les messages
        Intent in = new Intent(getActivity(),MessageClass.class);

        // compilation de la regex
        Pattern pt = Pattern.compile(".*Id=(\\d*)[ |,|}].*Sexe=(\\d*)[ |,|}].*Pseudo=(.*)[ |,|}]");
        // recherche dan sles infos affichés dans la liste
        Matcher ma = pt.matcher(l.getItemAtPosition(pos).toString());
        boolean b = ma.matches();

        in.putExtra("ID_RECEP",ma.group(1));
        if (ma.group(2).equals("2130837634")) // id de la photo de profil user_male
            in.putExtra("SEXE","H");
        else
            in.putExtra("SEXE","F");
        in.putExtra("LOGIN",ma.group(3));
        in.putExtra("ID_EMET",user.getId());
        startActivity(in);
    }

}
