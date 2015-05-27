package se.mah.k3.klarappo;

import android.app.Fragment;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Handler;

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
    int width;
    int height;
    private long roundTrip = 0;
    long startTime = 0;
// detta är snyggt men vi har inte arbetat så mycket med delegates och callbacks (det är egentligen bara lyssnare) se i slutet
//    Handler timerHandler = new Handler();
//    Runnable timerRunnable = new Runnable() {
//
//        @Override
//        public void run() {
//            timerHandler.removeCallbacks(timerRunnable);
//            roundTrip = roundTrip + 1; //Assuming that we are the only one using our ID
//            lastTimeStamp = System.currentTimeMillis();  //remember when we sent the token
//            Constants.myFirebaseRef.child(Constants.userName).child("RoundTripTo").setValue(roundTrip);
//
//            timerHandler.postDelayed(timerRunnable, 0);
//        }
//    };

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Start the thread  //Nier to do this here
        MyThread myThread = new MyThread();
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
        fireBaseEntryForRoundBack.addValueEventListener(this);
        return rootView;
    }



     //Start a new time measure of roundtrip time
/*     @Override
    public void onClick(View v) {
         if (v.getId()==R.id.iv_refresh) {
             roundTrip = roundTrip + 1; //Assuming that we are the only one using our ID
             lastTimeStamp = System.currentTimeMillis();  //remember when we sent the token
             Constants.getFirebaseRef().child(Constants.userName).child("RoundTripTo").setValue(roundTrip);
         }
    }*/

    // Det vi vill göra är att uppdatera pingen till firebase automatiskt
    // istället för att det ska ske genom knapptrycket i onClick
    // här försökte vi automatisera det som händer i onclick metoden
//    public void onSearchFinished(){
//        roundTrip = roundTrip + 1; //Assuming that we are the only one using our ID
//        lastTimeStamp = System.currentTimeMillis();  //remember when we sent the token
//        Constants.myFirebaseRef.child(Constants.userName).child("RoundTripTo").setValue(roundTrip);
//    }

    //called if we move on the screen send the coordinates to fireBase
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:  // If it is the motionEvent move.
                float xRel = event.getX()/width;
                float yRel = event.getRawY()/height;//Compensate for menubar can probably be solved more beautiful test with getY to see the difference
                Constants.getFirebaseRef().child(Constants.userName).child("xRel").setValue(xRel);  //Set the x Value
                Constants.getFirebaseRef().child(Constants.userName).child("yRel").setValue(yRel);  //Set the y value
                String color = Constants.myFirebase()
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
                TextView timeLastTV = (TextView) getActivity().findViewById(R.id.timelast);
                timeLastTV.setText("" + timeLastRound);
                Constants.getFirebaseRef().child(Constants.userName).child("ping").setValue(timeLastRound);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("MainFragment","onDataChanged failed");
        }
    }

/*    //mm denna är bara tänkt att användas när man skall göra något en gång:
    private class MyAsyncTask extends AsyncTask<String,Void,Long>{
        @Override
        protected Long doInBackground(String... params) {
            // Vi gissar att det som sker i onClick istället ska ske här så vi har klistrat in det här
            // eftersom det är detta som ska ske kontinuerligt och per automatik
            roundTrip = roundTrip + 1; //Assuming that we are the only one using our ID
            lastTimeStamp = System.currentTimeMillis();  //remember when we sent the token
            Constants.myFirebaseRef.child(Constants.userName).child("RoundTripTo").setValue(roundTrip);
        return null;
        }

        // Osäkra på vad som ska ske här
        // Vi vill att det som händer i onDataChange ska ske varje gång tråden uppdateras
        // och vi tror att det ska skrivas in här i onPostExecute
        // Vi försökte köra vår metod onSearchFinished här inne men då klagar den
        @Override
        protected void onPostExecute(Long result) { onDataChange();}
    }*/

    @Override
    public void onCancelled(FirebaseError firebaseError) {

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

