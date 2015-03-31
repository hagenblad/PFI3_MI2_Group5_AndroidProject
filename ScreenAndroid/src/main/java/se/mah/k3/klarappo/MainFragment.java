package se.mah.k3.klarappo;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by K3LARA on 28/03/2015.
 */
public class MainFragment extends Fragment implements View.OnClickListener{
    long lastTimeStamp = System.currentTimeMillis();
    long timeLastRound;
    long averageSum=0;
    long average =0;
    int width;
    int height;
    private long roundtrip = 0;
    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        //Log.i("MainFragment","Width: "+ width+" Height: "+height);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        float xRel = event.getX()/width;
                        float yRel = event.getRawY()/height;//Compensate for menubar can probably be solved mora beautiful
                        Constants.myFirebaseRef.child(Constants.userName).child("xRel").setValue(xRel);
                        Constants.myFirebaseRef.child(Constants.userName).child("yRel").setValue(yRel);
                }

             return true;
            }

        });
            //Add listeners to resonse time
            View v = rootView.findViewById(R.id.timelast);
            v.setOnClickListener(this);
            rootView.findViewById(R.id.textView).setOnClickListener(this);

            //Create listeners for response time back
            Constants.myFirebaseRef.child(Constants.userName).child("RoundTripBack").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (roundtrip > 0 && snapshot != null) {
                        //Log.i("MainFragment", "Current value: " + snapshot.getValue());
                        roundtrip = (long) snapshot.getValue();
                        timeLastRound = System.currentTimeMillis() - lastTimeStamp;
                        TextView timeLastTV = (TextView) getActivity().findViewById(R.id.timelast);
                        timeLastTV.setText("" + timeLastRound);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
            return rootView;
        }
     //Take care of clicks on response time
     @Override
    public void onClick(View v) {
         if (v.getId()==R.id.textView || v.getId()==R.id.timelast) {
             roundtrip = roundtrip + 1;
             Log.i("MainFragment", "RoundTripTo: " + roundtrip);
             lastTimeStamp = System.currentTimeMillis();
             Constants.myFirebaseRef.child(Constants.userName).child("RoundTripTo").setValue(roundtrip);
         }
    }

}

