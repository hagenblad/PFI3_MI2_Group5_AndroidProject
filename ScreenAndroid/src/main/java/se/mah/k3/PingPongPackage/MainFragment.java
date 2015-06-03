package se.mah.k3.PingPongPackage;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by K3LARA on 28/03/2015.
 */
public class MainFragment extends Fragment implements View.OnTouchListener, ValueEventListener{
    long lastTimeStamp = System.currentTimeMillis();
    long timeLastRound;
    Firebase fbColors;
    Firebase fbColorrs;

    String leUser;
    int width;
    public Long myColorIs;
    int height;
    private long roundTrip = 0;
    public int position;
    long startTime = 0;
    float xRel = 0.5f;
    float yRel = 0.5f;
    MyThread myThread = new MyThread();

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Start the thread  //Nier to do this here
        myThread = new MyThread();
        myThread.running = true;
        myThread.start();
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

        //Add listeners for the touch events onTouch will be called when screen is touched.
        rootView.setOnTouchListener(this);

        //Add listeners to initiate a measure of roundtrip time onClick will be called.
/*        View v = rootView.findViewById(R.id.iv_refresh);
        v.setOnClickListener(this);*/

        //Create listeners for response time back so know when the token returns
        String userName = Constants.userName;
        Firebase fireBaseEntryForMyID = Constants.getFirebaseRef().child(Constants.userName); //My part of the firebase
        Firebase fireBaseEntryForRoundBack =  fireBaseEntryForMyID.child("RoundTripBack"); //My roundtrip (Check firebase)


        //Listen for changes on "RoundTripBack" entry onDataChange will be called when "RoundTripBack" is changed

        Constants.getFirebaseRef().child(Constants.userName).child("xRel").setValue(xRel);  //Set the x Value
        Constants.getFirebaseRef().child(Constants.userName).child("yRel").setValue(yRel);  //Set the y Value

        Firebase fireBaseEntryForPosition = Constants.getFirebaseRef().child("position");
        fireBaseEntryForPosition.addValueEventListener(this);

        fireBaseEntryForRoundBack.addValueEventListener(this);

        /* position stuff */

        fbColorrs = new Firebase("https://pingispong.firebaseio.com/"+Constants.userName);
fbColorrs.addChildEventListener(new ChildEventListener() {
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.i("hej "+dataSnapshot.getKey().toString(), " "+Constants.userName+"position");
        if(dataSnapshot.getKey().toString().equals("position") ) {

            fbColors = new Firebase("https://pingispong.firebaseio.com/"+Constants.userName);
            fbColors.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshotz) {

                    Log.i("color pos", " " + dataSnapshotz.child("position").getValue());
                    myColorIs = (Long) dataSnapshotz.child("position").getValue();
                    if (myColorIs == 1){
                        getView().setBackgroundColor(Color.rgb(89,155,185));
                    }
                    if (myColorIs == 2){
                        getView().setBackgroundColor(Color.rgb(140,186,102));
                    }
                    if (myColorIs == 3){
                        getView().setBackgroundColor(Color.rgb(211,89,89));
                    }
                    if (myColorIs == 4){
                        getView().setBackgroundColor(Color.rgb(229,214,114));
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }
});

/* position stuff */

        return rootView;
    }

    //called if we move on the screen send the coordinates to fireBase
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:  // If it is the motionEvent move.
                float xRel = event.getX()/width;
                float yRel = event.getRawY()/height;//Compensate for menubar can probably be solved more beautiful test with getY to see the difference
                Constants.getFirebaseRef().child(Constants.userName).child("xRel").setValue(xRel/1.9);  //Set the x Value
                Constants.getFirebaseRef().child(Constants.userName).child("yRel").setValue(yRel/1.9);  //Set the y value
        }
        return true; //Ok we consumed the event and no-one can use it it is ours!
    }

    //This is called when the roundtrip is completed so show the time
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        try {
            if (roundTrip > 0 && dataSnapshot != null) {
                roundTrip = (long) dataSnapshot.getValue();
                timeLastRound = System.currentTimeMillis() - lastTimeStamp;
//                TextView timeLastTV = (TextView) getActivity().findViewById(R.id.timelast);
//                timeLastTV.setText("" + timeLastRound);
                Constants.getFirebaseRef().child(Constants.userName).child("ping").setValue(timeLastRound);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("MainFragment","onDataChanged failed");
        }
        try {

            Log.i("mainFragment", "här kommer siffran " + dataSnapshot.child(Constants.userName).child("position").getValue());
Log.i("xyz", " "+dataSnapshot);
            Log.i("xyz", " "+dataSnapshot.getKey());
            Log.i("xyz", " "+dataSnapshot.getValue());
            //Log.i("mainFragment", "här kommer siffran " + Constants.getFirebaseRef().child(Constants.userName).child("position").getValue());


            if (dataSnapshot.child(Constants.userName).child("position").getValue() != null){
               // Log.i("mainFragment", "här kommer siffran " + dataSnapshot.child(Constants.userName).child("position").getValue());

                Integer position = (int) dataSnapshot.child(Constants.userName).child("position").getValue();
                String playerPosition = String.valueOf(position);
                Log.i("MainFragment", "Player has position " + playerPosition);
            }
        } catch (Exception e){
            e.printStackTrace();
            Log.i("MainFragment", "onDataChanged failed");
        }
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    @Override
    public void onPause(){
        super.onPause();
        myThread.stopThread();
        Constants.getFirebaseRef().child(Constants.userName).removeValue();
    }

    //Fix for this user and runs as long as the program runs
    private class MyThread extends Thread{
        private boolean running = true;
        public void stopThread(){  //if you need to stop it.... can and shul not be restarted
            running = false;
        }
        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(5000); //Start delayed
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                    roundTrip = roundTrip + 1; //Assuming that we are the only one using our ID
                    lastTimeStamp = System.currentTimeMillis();  //remember when we sent the token
                    Constants.getFirebaseRef().child(Constants.userName).child("RoundTripTo").setValue(roundTrip);
            }
        }
    }

}

