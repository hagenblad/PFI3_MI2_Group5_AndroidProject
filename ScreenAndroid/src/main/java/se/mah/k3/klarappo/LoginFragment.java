package se.mah.k3.klarappo;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements ValueEventListener
{


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View returnView = inflater.inflate(R.layout.fragment_login, container, false);
        View v = returnView.findViewById(R.id.btnLogon);

        //Imports Exo Bold & Exo Extra Light fonts
        Typeface ExoB = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ExoBold.ttf");
        Typeface ExoEL = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ExoExtraLight.ttf");


        TextView tv = (TextView)returnView.findViewById(R.id.logoText);
        tv.setTypeface(ExoB);

        TextView tv2 = (TextView)returnView.findViewById(R.id.nameTitle);
        tv2.setTypeface(ExoEL);

        EditText et = (EditText)returnView.findViewById(R.id.name);
        et.setTypeface(ExoEL);

        Button btn = (Button)returnView.findViewById(R.id.btnLogon);
        btn.setTypeface(ExoB);

        final ProgressBar prog = (ProgressBar)returnView.findViewById(R.id.progressBar);
        prog.setVisibility(View.INVISIBLE);


        v.setOnClickListener(new View.OnClickListener() {
            //Click on loginButton
            @Override
            public void onClick(View v) {

                try {
                    EditText name = (EditText) getActivity().findViewById(R.id.name);
                    Constants.userName = name.getText().toString();
                    //Are we on the right screen
                    Log.i("name",Constants.userName);
                    if (name.getText().toString().equals("")){
                        Toast.makeText(getActivity(),getResources().getString(R.string.empty_name),Toast.LENGTH_LONG).show(); //Toast message if player hasn't entered name
                    }else{
                        prog.setVisibility(View.VISIBLE);

                        //In firebase you read a value by adding a listener, then it will trigger once connected and on all changes.
                        //There is no readvalue as one could expect only listeners.
                        //Get the ScreenNbr child
                        Firebase  fireBaseEntryForScreenNbr = Constants.getFirebaseRef().child("ScreenNbr");
                        fireBaseEntryForScreenNbr.addValueEventListener(LoginFragment.this); //Ok listen the changes will sho up in the method onDataChange
                        //String screenNumber = String.valueOf(Constants.screenNbr);
                        //if (screenNbrFromFirebase.equals(screenNumber)){

                        // }else{
                        //   Toast.makeText(getActivity(),getResources().getString(R.string.toast_incorrect_screen),Toast.LENGTH_LONG).show();
  
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("LoginFragment","onDataChanged failed");
                }
            }
        });
        return returnView;
    }

    @Override
    public void onDataChange(DataSnapshot snapshot) {
        try {
            if (snapshot.getValue()!=null) {
                long val = (long) snapshot.getValue();
                String screenNbrFromFirebase = String.valueOf(val);
                Log.i("LoginFragment", "Screen nbr entered: " + val + " Value from firebase: "+screenNbrFromFirebase);
                Log.i("LoginFragment", "Logged in");



                FragmentManager fm;
                fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.container, new MainFragment());
                ft.addToBackStack(null);
                ft.commit();
                Toast.makeText(getActivity(),getResources().getString(R.string.toast_instruction),Toast.LENGTH_LONG).show(); //Toast info message when entering the game




            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("LoginFragment","onDataChanged failed");
        }


    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
}
