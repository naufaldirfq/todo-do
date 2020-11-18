package id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Executor;

import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.data.models.User;
import id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.Const;

import static id.ac.ui.cs.mobileprogramming.naufaldi_athallah_rifqi.todo_do.utils.HelperClass.logErrorMessage;

@SuppressWarnings("ConstantConditions")
class SplashScreenRepository {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private User user = new User();
    private FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = rootRef.collection(Const.Collection.USERS);

    MutableLiveData<User> checkIfUserIsAuthenticatedInFirebase() {
        MutableLiveData<User> isUserAuthenticateInFirebaseMutableLiveData = new MutableLiveData<>();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            user.isAuthenticated = false;
            isUserAuthenticateInFirebaseMutableLiveData.setValue(user);
        } else {
            user.uid = firebaseUser.getUid();
            user.isAuthenticated = true;
            isUserAuthenticateInFirebaseMutableLiveData.setValue(user);
        }
        return isUserAuthenticateInFirebaseMutableLiveData;
    }

    MutableLiveData<User> addUserToLiveData(String uid) {
        Log.d("REPO", "ADD USER TO LIVE DATA");
        Log.d("USER REF", usersRef.toString());
        MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
        usersRef.document(uid).get().addOnCompleteListener(userTask -> {
            Log.d("USERTASK", "IF ELSE");
            if (userTask.isSuccessful()) {
                Log.d("USER TASK", "SUCCESS");
                DocumentSnapshot document = userTask.getResult();
                if (document.exists()) {
                    Log.d("USER TASK", "DOCUMENT EXIST");
                    User user = document.toObject(User.class);
                    userMutableLiveData.setValue(user);
                }
            } else {
                Log.d("USER TASK", "FAIL");
                logErrorMessage(userTask.getException().getMessage());
            }
        });
        Log.d("REPO", "WILL RETURN USERMUTABLELIVEDATA");
        Log.d("PRINT DATA > ", userMutableLiveData.toString());
        return userMutableLiveData;
    }
}