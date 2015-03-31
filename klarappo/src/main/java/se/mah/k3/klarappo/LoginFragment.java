package se.mah.k3.klarappo;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.fragment_login, container, false);
        View v = returnView.findViewById(R.id.btnLogon);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Firebase myFirebaseRef = new Firebase("https://klara.firebaseio.com/");
                Constants.myFirebaseRef.child("ScreenNbr").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.getValue()!=null) {
                            long val = (long) snapshot.getValue();

                            Log.i("LoginFragment", "Value" + val);
                            EditText screenNumber = (EditText) getActivity().findViewById(R.id.screenNumber);
                            EditText name = (EditText) getActivity().findViewById(R.id.name);
                            Constants.userName = name.getText().toString();
                            //  if (String.valueOf(val).equals(screenNumber.getText())){
                            android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ft.replace(R.id.container, new MainFragment()).commit();
                            // }
                        }
                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {}
                });
            }
        });

        return returnView;
    }


}
