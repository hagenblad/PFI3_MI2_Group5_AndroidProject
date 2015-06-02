package se.mah.k3.klarappo;

import com.firebase.client.Firebase;

/**
 * Created by K3LARA on 29/03/2015.
 */
public class Constants {
    public static String userName = "";
    public static int xPos = 101;
    public static int yPos = 100;
    public static int screenNbr = 145;
    //public static Firebase myFirebaseRef = new Firebase("https://klara.firebaseio.com/");
    //Since this is static it will be instansiated  at startup of the App
    //Use static for variables that you want to reach from anywhere in the app...

    //Make this safer by checking if it is already created.... perhaps Firebase takes cara of this....
    public static Firebase myFirebaseRef;
    public static Firebase getFirebaseRef(){
        if(myFirebaseRef==null){
            myFirebaseRef = new Firebase("https://pingispong.firebaseio.com/");
        }
        return myFirebaseRef;
    }

}
