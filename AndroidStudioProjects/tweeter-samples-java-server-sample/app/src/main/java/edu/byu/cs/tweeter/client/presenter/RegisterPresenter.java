package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter implements UserService.RegisterObserver{


    public View view;

    public RegisterPresenter(View view) {
        this.view = view;
    }

    public interface View {
        void showInfoMessage(String message);
        void hideInfoMessage();
        void showErrorMessage(String message);
        void hideErrorMessage();
        void openMainView(User user);
    }


    public boolean validateRegistration(String firstName, String lastName, String alias, String password, Drawable image){
        if (firstName.length() == 0) {
            view.showErrorMessage("First Name cannot be empty.");
            return false;
        }
        if (lastName.length() == 0) {
            view.showErrorMessage("Last Name cannot be empty.");
            return false;
        }
        if (alias.length() == 0) {
            view.showErrorMessage("Alias cannot be empty.");
            return false;
        }
        if (alias.charAt(0) != '@') {
            view.showErrorMessage("Alias must begin with @.");
            return false;
        }
        if (alias.length() < 2) {
            view.showErrorMessage("Alias must contain 1 or more characters after the @.");
            return false;
        }
        if (password.length() == 0) {
            view.showErrorMessage("Password cannot be empty.");
            return false;
        }

        if (image == null) {
            view.showErrorMessage("Profile image must be uploaded.");
            return false;
        }
        return true;
    }

    public void register(String firstName, String lastName, String alias, String password, Drawable image){
        if(validateRegistration(firstName, lastName, alias, password, image))
        {

            // put this code into the view...
            Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] imageBytes = bos.toByteArray();

            // Intentionally, Use the java Base64 encoder so it is compatible with M4.
            String imageBytesBase64 = Base64.getEncoder().encodeToString(imageBytes);
            UserService userService = new UserService();
            userService.register(firstName, lastName, alias, password, imageBytesBase64,this);
        }

    }



    // All of the following methods are from the UserService.RegisterObserver interface
    @Override
    public void registerSucceeded(User user, AuthToken authToken) {
        view.hideInfoMessage();
        view.hideErrorMessage();
        view.showInfoMessage("Welcome to Tweeter!");
        view.openMainView(user);
    }

    @Override
    public void registerFailed(String message) {
        view.showErrorMessage(message);
    }

}
