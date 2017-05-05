package com.example.mely.seemy_v11;

/**
 * Created by MELY on 4/17/2017.
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

import butterknife.Bind;

import static android.app.Activity.RESULT_OK;

@SuppressLint("ValidFragment")
public class Profil extends Fragment implements View.OnClickListener {
    Utilisateur user;
    @SuppressLint("ValidFragment")
    public Profil(Utilisateur user) {
        this.user=user;

    }
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    @Bind(R.id.user_profile_photo)ImageButton _profileImg;
    @Bind(R.id.btn_add_distance)Button _addDistance;
    @Bind(R.id.btn_add_tags)Button _addTags;
    TextView edt_tags;
    String select;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.profil, container, false);
        TextView edt = (TextView) rootView.findViewById(R.id.user_profile_name);
        edt.setText(user.getPseudo());
        edt_tags = (TextView) rootView.findViewById(R.id.user_profile_short_bio);

        if(user.getTags()!=null) {
            String t="";
            for(String s : user.getTags())
                t=t+"#"+s+" ";
            edt_tags.setText(t);
        }


        ImageButton imB = (ImageButton)rootView.findViewById(R.id.user_profile_photo);
        imB.setOnClickListener(this);

        Button addTags = (Button)rootView.findViewById(R.id.btn_add_tags);
        addTags.setOnClickListener(this);
        Button addDistance = (Button)rootView.findViewById(R.id.btn_add_distance);
        addDistance.setOnClickListener(this);


        return rootView;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.user_profile_photo:
                loadImagefromGallery(this.getView());
                break;
            case R.id.btn_add_tags:
                //Toast.makeText(getActivity(), "liste1", Toast.LENGTH_LONG).show();// a supprimer
                this.openDialog();
                break;
            case R.id.btn_add_distance:
                this.openCheckDistance();
                break;
        }
    }

    public void loadImagefromGallery(View view) {
        Profil.this.startActivityForResult(
                new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                RESULT_LOAD_IMG);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(getActivity(),"Text2!",Toast.LENGTH_SHORT).show();
        super.onActivityResult(requestCode, resultCode, data);Toast.makeText(getActivity(),"Text3",Toast.LENGTH_SHORT).show();
        //Detects request codes
        if (requestCode == RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            // Set the Image in ImageView after decoding the String
            _profileImg.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
        }
        else{
           //Fragment fragment = new Rechercher();
            }


    }


    private void openDialog(){
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View subView = inflater.inflate(R.layout.listeview_tags, null);

        final EditText _tag1 = (EditText)subView.findViewById(R.id.EditTag);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Ajouter des tags");
        builder.setView(subView);
        builder.setPositiveButton("Ajouter", null);
        builder.setNegativeButton("Fermer", null);
        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(alertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (!_tag1.getText().toString().equals("")) {
                                edt_tags.append(" #" + _tag1.getText().toString());
                                AsyncTask AT = new SaveTagBackground(getActivity()).execute(user.getPseudo(), _tag1.getText().toString(), "Tag");
                                user.add_Tag(_tag1.getText().toString());
                                _tag1.setText("");

                            }

                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                }
            }
        );


        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
            alertDialog.show();
    }

    private  void openCheckDistance(){
        Dialog dialog;
        final CharSequence items[] = {"100","200","500","Aucun"};
        final ArrayList itemsSelected = new ArrayList();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Définisseez le périmètre de recherche (en mètre): ");

        builder.setSingleChoiceItems(items,-1,  new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int selected) {
                select= (String) items[selected];

            }
        }).setPositiveButton("Valider", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if(!select.equals("Aucun")){
                    AsyncTask AT=  new SaveTagBackground(getActivity()).execute(user.getPseudo(),select,"Distance");}
                else{
                    AsyncTask AT=  new SaveTagBackground(getActivity()).execute(user.getPseudo(),"0","Distance");
                }

            }
        }).setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}

        });

        dialog = builder.create();
        dialog.show();

    }
}
